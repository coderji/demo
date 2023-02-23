import datetime
import logging
import re
import string
import subprocess
import time

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

def device():
	result, device = adb('devices')
	serial = ''
	for line in device.splitlines():
		match = re.search("^([0-9a-zA-Z]+)\s+device", line)
		if match is not None:
			if len(device.splitlines()) > 3:
				logging.error("More than one device is connected.")
				raise Exception("More than one device is connected.")
			serial = match.group(1)
			break
	return serial

def diskstats(filename):
	result, diskstats = adb('shell cat /proc/diskstats')
	lines = diskstats.split('\n')
	for index in range(len(lines)):
		# start = lines[index].find('mmcblk0 ')
		start = lines[index].find('sda ')
		if start > 0:
			mmcblkstr = lines[index].split()
			readsize = int(mmcblkstr[5])*512/1024/1024
			writesize = int(mmcblkstr[9])*512/1024/1024
			print("==============mmcblk0/sda===============")
			print("ReadSize  ", readsize, "MB")
			print("WriteSize ", writesize, "MB")
			with open(filename, 'a') as fileobj:
				fileobj.write("==============mmcblk0/sda===============\n")
				fileobj.write("ReadSize  {0}MB\n".format(readsize))
				fileobj.write("WriteSize {0}MB\n".format(writesize))
			break
	for index in range(len(lines)):
		start = lines[index].find('dm-0 ')
		if start > 0:
			mmcblkstr = lines[index].split()
			readsize = int(mmcblkstr[5])*512/1024/1024
			writesize = int(mmcblkstr[9])*512/1024/1024
			print("==============dm-0======================")
			print("ReadSize  ", readsize, "MB")
			print("WriteSize ", writesize, "MB")
			with open(filename, 'a') as fileobj:
				fileobj.write("==============dm-0======================\n")
				fileobj.write("ReadSize  {0}MB\n".format(readsize))
				fileobj.write("WriteSize {0}MB\n".format(writesize))
			break

def root():
	result, proc = adb('root')
	if result == 0:
		print("==============root success==============")
	else:
		print("==============root fail=================")

def pid_io(filename):
	result, proc = adb('shell ls /proc')
	lines = proc.split('\n')
	readsize = int(0)
	writesize = int(0)
	for index in range(len(lines)):
		if lines[index].isdecimal():
			result, io = adb('shell cat /proc/{0}/io'.format(lines[index]))
			if result == 0:
				iostr = io.split()
				if int(iostr[1]) != 0:
					read = int(iostr[9])/8/1024/1024
					readsize = readsize + read
					write = int(iostr[11])/8/1024/1024
					writesize = writesize + write
					if read > 0.5 or write > 0.5:
						with open(filename, 'a') as fileobj:
							result, process = adb('shell ps -A {0}'.format(lines[index]))
							processstr = process.split()
							fileobj.write('{0:5} {1:56} ReadSize {2:16}MB  WriteSize {3:16}MB\n'.format(lines[index], processstr[17], read, write))
	print("==============/proc/[pid]/io============")
	print("ReadSize  ", readsize, "MB")
	print("WriteSize ", writesize, "MB")
	with open(filename, 'a') as fileobj:
		fileobj.write("==============/proc/[pid]/io============\n")
		fileobj.write("ReadSize  {0}MB\n".format(readsize))
		fileobj.write("WriteSize {0}MB\n".format(writesize))

filename = ''
def main():
	serial = device()
	print("Device ", serial)
	time = datetime.datetime.now().strftime("%Y%m%d-%H%M%S")
	print("Date ", time)
	filename = 'device-{0}-{1}.txt'.format(serial, time)
	with open(filename, 'w') as fileobj:
		fileobj.write('MMC-SDA\n')
	diskstats(filename)
	pid_io(filename)

if __name__ == '__main__':
	root()
	#while True:
	if True:
		main()
		#time.sleep(60*10)