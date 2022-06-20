#!/bin/sh
adb shell ps | grep -w system_server | awk '{print $2}' | xargs adb shell kill


#!/bin/sh
function _input() {
    # 健康系统
    input tap 750 620
    sleep 0.5
    # 点击继续
    input tap 550 750
    sleep 0.5
    # 再次挑战
    input tap 915 990
    sleep 0.5
    # 继续
    input tap 815 715
    sleep 95.5
}
_count=1
while [ $_count -le 10000 ]
do
    _date=`date +%T`
    echo "$_date [$_count]"
    let _count++
    _input
done
