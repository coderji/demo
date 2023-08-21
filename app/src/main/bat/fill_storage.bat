@echo off
adb shell df -h /data
set /p name=name:
set /p size=size:
adb shell dd if=/dev/zero of=/sdcard/%name% bs=1048576 count=%size%