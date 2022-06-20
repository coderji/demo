chcp 65001

@echo off

cls

:1

cls

echo 当前内存使用情况

adb shell df -h /data/media

set /p a=请输入文件名：

set /p b=请输入文件大小：

adb shell dd if=/dev/zero of=/sdcard/%a% bs=1048574 count=%b%

goto 1
