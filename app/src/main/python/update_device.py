#!/usr/bin/env python3
#
# Copyright (C) 2017 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

"""Send an A/B update to an Android device over adb."""

from __future__ import print_function
from __future__ import absolute_import

import argparse
import binascii
import hashlib
import logging
import os
import socket
import subprocess
import sys
import struct
import tempfile
import time
import threading
import xml.etree.ElementTree
import zipfile

# The path used to store the OTA package when applying the package from a file.
OTA_PACKAGE_PATH = '/data/ota_package'

# The path to the payload public key on the device.
PAYLOAD_KEY_PATH = '/etc/update_engine/update-payload-key.pub.pem'

# The port on the device that update_engine should connect to.
DEVICE_PORT = 1234


def CopyFileObjLength(fsrc, fdst, buffer_size=128 * 1024, copy_length=None):
  """Copy from a file object to another.

  This function is similar to shutil.copyfileobj except that it allows to copy
  less than the full source file.

  Args:
    fsrc: source file object where to read from.
    fdst: destination file object where to write to.
    buffer_size: size of the copy buffer in memory.
    copy_length: maximum number of bytes to copy, or None to copy everything.

  Returns:
    the number of bytes copied.
  """
  copied = 0
  while True:
    chunk_size = buffer_size
    if copy_length is not None:
      chunk_size = min(chunk_size, copy_length - copied)
      if not chunk_size:
        break
    buf = fsrc.read(chunk_size)
    if not buf:
      break
    fdst.write(buf)
    copied += len(buf)
  return copied


class AndroidOTAPackage(object):
  """Android update payload using the .zip format.

  Android OTA packages traditionally used a .zip file to store the payload. When
  applying A/B updates over the network, a payload binary is stored RAW inside
  this .zip file which is used by update_engine to apply the payload. To do
  this, an offset and size inside the .zip file are provided.
  """

  # Android OTA package file paths.
  OTA_PAYLOAD_BIN = 'payload.bin'
  OTA_PAYLOAD_PROPERTIES_TXT = 'payload_properties.txt'
  SECONDARY_OTA_PAYLOAD_BIN = 'secondary/payload.bin'
  SECONDARY_OTA_PAYLOAD_PROPERTIES_TXT = 'secondary/payload_properties.txt'
  PAYLOAD_MAGIC_HEADER = b'CrAU'

  def __init__(self, otafilename, secondary_payload=False):
    self.otafilename = otafilename

    otazip = zipfile.ZipFile(otafilename, 'r')
    payload_entry = (self.SECONDARY_OTA_PAYLOAD_BIN if secondary_payload else
                     self.OTA_PAYLOAD_BIN)
    payload_info = otazip.getinfo(payload_entry)

    if payload_info.compress_type != 0:
      logging.error(
          "Expected payload to be uncompressed, got compression method %d",
          payload_info.compress_type)
    # Don't use len(payload_info.extra). Because that returns size of extra
    # fields in central directory. We need to look at local file directory,
    # as these two might have different sizes.
    with open(otafilename, "rb") as fp:
      fp.seek(payload_info.header_offset)
      data = fp.read(zipfile.sizeFileHeader)
      fheader = struct.unpack(zipfile.structFileHeader, data)
      # Last two fields of local file header are filename length and
      # extra length
      filename_len = fheader[-2]
      extra_len = fheader[-1]
      self.offset = payload_info.header_offset
      self.offset += zipfile.sizeFileHeader
      self.offset += filename_len + extra_len
      self.size = payload_info.file_size
      fp.seek(self.offset)
      payload_header = fp.read(4)
      if payload_header != self.PAYLOAD_MAGIC_HEADER:
        logging.warning(
            "Invalid header, expected %s, got %s."
            "Either the offset is not correct, or payload is corrupted",
            binascii.hexlify(self.PAYLOAD_MAGIC_HEADER),
            binascii.hexlify(payload_header))

    property_entry = (self.SECONDARY_OTA_PAYLOAD_PROPERTIES_TXT if
                      secondary_payload else self.OTA_PAYLOAD_PROPERTIES_TXT)
    self.properties = otazip.read(property_entry)


def AndroidUpdateCommand(ota_filename, secondary, payload_url, extra_headers):
  """Return the command to run to start the update in the Android device."""
  ota = AndroidOTAPackage(ota_filename, secondary)
  headers = ota.properties
  headers += b'USER_AGENT=Dalvik (something, something)\n'
  headers += b'NETWORK_ID=0\n'
  headers += extra_headers.encode()

  return ['update_engine_client', '--update', '--follow',
          '--payload=%s' % payload_url, '--offset=%d' % ota.offset,
          '--size=%d' % ota.size, '--headers="%s"' % headers.decode()]


def OmahaUpdateCommand(omaha_url):
  """Return the command to run to start the update in a device using Omaha."""
  return ['update_engine_client', '--update', '--follow',
          '--omaha_url=%s' % omaha_url]


class AdbHost(object):
  """Represents a device connected via ADB."""

  def __init__(self, device_serial=None):
    """Construct an instance.

    Args:
        device_serial: options string serial number of attached device.
    """
    self._device_serial = device_serial
    self._command_prefix = ['adb']
    if self._device_serial:
      self._command_prefix += ['-s', self._device_serial]

  def adb(self, command):
    """Run an ADB command like "adb push".

    Args:
      command: list of strings containing command and arguments to run

    Returns:
      the program's return code.

    Raises:
      subprocess.CalledProcessError on command exit != 0.
    """
    command = self._command_prefix + command
    logging.info('Running: %s', ' '.join(str(x) for x in command))
    p = subprocess.Popen(command, universal_newlines=True)
    p.wait()
    return p.returncode

  def adb_output(self, command):
    """Run an ADB command like "adb push" and return the output.

    Args:
      command: list of strings containing command and arguments to run

    Returns:
      the program's output as a string.

    Raises:
      subprocess.CalledProcessError on command exit != 0.
    """
    command = self._command_prefix + command
    logging.info('Running: %s', ' '.join(str(x) for x in command))
    return subprocess.check_output(command, universal_newlines=True)


def main():
  parser = argparse.ArgumentParser(description='Android A/B OTA helper.')
  parser.add_argument('otafile', metavar='PAYLOAD', type=str,
                      help='the OTA package file (a .zip file) or raw payload \
                      if device uses Omaha.')
  parser.add_argument('--file', action='store_true',
                      help='Push the file to the device before updating.')
  parser.add_argument('--no-push', action='store_true',
                      help='Skip the "push" command when using --file')
  parser.add_argument('-s', type=str, default='', metavar='DEVICE',
                      help='The specific device to use.')
  parser.add_argument('--no-verbose', action='store_true',
                      help='Less verbose output')
  parser.add_argument('--public-key', type=str, default='',
                      help='Override the public key used to verify payload.')
  parser.add_argument('--extra-headers', type=str, default='',
                      help='Extra headers to pass to the device.')
  parser.add_argument('--secondary', action='store_true',
                      help='Update with the secondary payload in the package.')
  parser.add_argument('--no-slot-switch', action='store_true',
                      help='Do not perform slot switch after the update.')
  parser.add_argument('--no-postinstall', action='store_true',
                      help='Do not execute postinstall scripts after the update.')
  parser.add_argument('--allocate-only', action='store_true',
                      help='Allocate space for this OTA, instead of actually \
                        applying the OTA.')
  parser.add_argument('--verify-only', action='store_true',
                      help='Verify metadata then exit, instead of applying the OTA.')
  parser.add_argument('--no-care-map', action='store_true',
                      help='Do not push care_map.pb to device.')
  parser.add_argument('--perform-slot-switch', action='store_true',
                      help='Perform slot switch for this OTA package')
  parser.add_argument('--perform-reset-slot-switch', action='store_true',
                      help='Perform reset slot switch for this OTA package')
  args = parser.parse_args()
  logging.basicConfig(
      level=logging.WARNING if args.no_verbose else logging.INFO)

  dut = AdbHost(args.s)

  server_thread = None
  # List of commands to execute on exit.
  finalize_cmds = []
  # Commands to execute when canceling an update.
  cancel_cmd = ['shell', 'update_engine_client', '--cancel']
  # List of commands to perform the update.
  cmds = []

  help_cmd = ['shell', 'update_engine_client', '--help']
  use_omaha = 'omaha' in dut.adb_output(help_cmd)

  metadata_path = "/data/ota_package/metadata"
  if args.allocate_only:
    if PushMetadata(dut, args.otafile, metadata_path):
      dut.adb([
          "shell", "update_engine_client", "--allocate",
          "--metadata={}".format(metadata_path)])
    # Return 0, as we are executing ADB commands here, no work needed after
    # this point
    return 0
  if args.verify_only:
    if PushMetadata(dut, args.otafile, metadata_path):
      dut.adb([
          "shell", "update_engine_client", "--verify",
          "--metadata={}".format(metadata_path)])
    # Return 0, as we are executing ADB commands here, no work needed after
    # this point
    return 0
  if args.perform_slot_switch:
    assert PushMetadata(dut, args.otafile, metadata_path)
    dut.adb(["shell", "update_engine_client",
            "--switch_slot=true", "--metadata={}".format(metadata_path), "--follow"])
    return 0
  if args.perform_reset_slot_switch:
    assert PushMetadata(dut, args.otafile, metadata_path)
    dut.adb(["shell", "update_engine_client",
            "--switch_slot=false", "--metadata={}".format(metadata_path)])
    return 0

  if args.no_slot_switch:
    args.extra_headers += "\nSWITCH_SLOT_ON_REBOOT=0"
  if args.no_postinstall:
    args.extra_headers += "\nRUN_POST_INSTALL=0"

  with zipfile.ZipFile(args.otafile) as zfp:
    CARE_MAP_ENTRY_NAME = "care_map.pb"
    if CARE_MAP_ENTRY_NAME in zfp.namelist() and not args.no_care_map:
      # Need root permission to push to /data
      dut.adb(["root"])
      with tempfile.NamedTemporaryFile() as care_map_fp:
        care_map_fp.write(zfp.read(CARE_MAP_ENTRY_NAME))
        care_map_fp.flush()
        dut.adb(["push", care_map_fp.name,
                "/data/ota_package/" + CARE_MAP_ENTRY_NAME])

  if args.file:
    # Update via pushing a file to /data.
    device_ota_file = os.path.join(OTA_PACKAGE_PATH, 'debug.zip')
    payload_url = 'file://' + device_ota_file
    if not args.no_push:
      data_local_tmp_file = '/data/local/tmp/debug.zip'
      cmds.append(['push', args.otafile, data_local_tmp_file])
      cmds.append(['shell', 'mv', data_local_tmp_file,
                   device_ota_file])
      cmds.append(['shell', 'chcon',
                   'u:object_r:ota_package_file:s0', device_ota_file])
    cmds.append(['shell', 'chown', 'system:cache', device_ota_file])
    cmds.append(['shell', 'chmod', '0660', device_ota_file])

  if args.public_key:
    payload_key_dir = os.path.dirname(PAYLOAD_KEY_PATH)
    cmds.append(
        ['shell', 'mount', '-t', 'tmpfs', 'tmpfs', payload_key_dir])
    # Allow adb push to payload_key_dir
    cmds.append(['shell', 'chcon', 'u:object_r:shell_data_file:s0',
                 payload_key_dir])
    cmds.append(['push', args.public_key, PAYLOAD_KEY_PATH])
    # Allow update_engine to read it.
    cmds.append(['shell', 'chcon', '-R', 'u:object_r:system_file:s0',
                 payload_key_dir])
    finalize_cmds.append(['shell', 'umount', payload_key_dir])

  try:
    # The main update command using the configured payload_url.
    if use_omaha:
      update_cmd = \
          OmahaUpdateCommand('http://127.0.0.1:%d/update' % DEVICE_PORT)
    else:
      update_cmd = AndroidUpdateCommand(args.otafile, args.secondary,
                                        payload_url, args.extra_headers)
    cmds.append(['shell'] + update_cmd)

    for cmd in cmds:
      dut.adb(cmd)
  except KeyboardInterrupt:
    dut.adb(cancel_cmd)
  finally:
    if server_thread:
      server_thread.StopServer()
    for cmd in finalize_cmds:
      dut.adb(cmd, 5)

  return 0


if __name__ == '__main__':
  sys.exit(main())
