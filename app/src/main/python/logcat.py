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

def main():
	time = datetime.datetime.now().strftime("%Y%m%d-%H%M%S")
	print("Date ", time)
	cmd = 'logcat -b kernel > device-{0}.txt'.format( time)
	adb(cmd)

if __name__ == '__main__':
	while True:
		main()