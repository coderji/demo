import subprocess
import logging
import re
import datetime
import time
import string
from datetime import * 

serial = ''
def run_command_line(command_line = 'NO_COMMAND_GIVEN'):
	logging.debug("RUN: '{0}'".format(command_line))
	P = subprocess.Popen(command_line, stdout = subprocess.PIPE, stderr = subprocess.STDOUT, shell = True)
	out = P.communicate()[0].decode()
	out = re.sub(r'\r+\n', r'\n', out)
	logging.debug("Command '{0}' output: '{1}'".format(command_line, out))
	result = P.wait()
	if result != 0:
		logging.warning("Command '{0}' failed to run, output:{1}".format(command_line, out))
	return result, out

def adb(command):
	retry_count = 0
	while True:
		result, out = run_command_line("adb {0}".format(command))
		if "error: protocol fault" in out or "error: cannot connect to daemon:" in out:
			time.sleep(5)
		elif "error: device offline" in out:
			time.sleep(5)
		else:
			break
		retry_count = retry_count + 1

	return result, out

_, res = adb('shell cat /proc/diskstats')
print("==============diskstats==============")
print(res.split('\n'))
print("============================")

result,device = adb('devices')
for line in device.splitlines():
	match = re.search("^([0-9a-zA-Z]+)\s+device", line)
	if match is not None:
		if len(device.splitlines()) > 3:
			logging.error("More than one device is connected.")
			raise Exception("More than one device is connected.")
		serial = match.group(1)
		break

lines = res.split('\n')
print("Date-time ",datetime.now().strftime("%Y-%m-%d %H:%M:%S"))
print("Device	",serial)

for index in range(len(lines)):
	# start = lines[index].find('mmcblk0 ')
	start = lines[index].find('sda ')
	if start > 0:
		mmcblkstr = lines[index].split()
		readsize = int(mmcblkstr[5])*512/1024/1024
		writesize = int(mmcblkstr[9])*512/1024/1024
		print("==============mmcblk0==============")
		print("Readsize  ",readsize,"MB")
		print("Writesize ",writesize,"MB")
		break
for index in range(len(lines)):
	start = lines[index].find('dm-0 ')
	if start > 0:
		mmcblkstr = lines[index].split()
		readsize = int(mmcblkstr[5])*512/1024/1024
		writesize = int(mmcblkstr[9])*512/1024/1024
		print("==============dm-0==============")
		print("Readsize  ",readsize,"MB")
		print("Writesize ",writesize,"MB")
		break

