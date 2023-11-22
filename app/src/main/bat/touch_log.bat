adb root

adb shell dumpsys window -d enable DEBUG_FOCUS
adb shell dumpsys window -d enable DEBUG_INPUT

adb shell setprop mtk_d.viewroot.enable 707000
adb shell setprop sys.inputlog.enabled true

adb shell settings put system show_touches 1
adb shell settings put system pointer_location 1

adb shell stop
adb shell start

::adb shell screenrecord --bugreport /sdcard/test.mp4
