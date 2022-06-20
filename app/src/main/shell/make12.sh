#!/bin/bash
LOCAL_PATH=`pwd`
export JAVA_HOME=$LOCAL_PATH/prebuilts/jdk/jdk11/linux-x86
export PATH=$JAVA_HOME/bin:$PATH

SOURCE_FULL=V820AE_VN1_12.0_WIKO_CFC_013_2022-06-07-09_target_20220607123534.zip
SOURCE_SHORT=CFC13-060609
TARGET_FULL=V820AE_VN1_12.0_WIKO_CFC_014_2022-06-13-15_target_20220613194440.zip
TARGET_SHORT=CFC14-060315

PACKAGE=$SOURCE_SHORT-$TARGET_SHORT
if [ -d $PACKAGE ]; then
echo "$PACKAGE already exist"
exit 1
fi
mkdir $PACKAGE

echo "make12" > $PACKAGE/info.txt
echo `date "+%Y%m%d-%H%M%S"` >> $PACKAGE/info.txt
echo "SOURCE_FULL=$SOURCE_FULL" >> $PACKAGE/info.txt
echo "SOURCE_SHORT=$SOURCE_SHORT" >> $PACKAGE/info.txt
echo "TARGET_FULL=$TARGET_FULL" >> $PACKAGE/info.txt
echo "TARGET_SHORT=$TARGET_SHORT" >> $PACKAGE/info.txt

./out_sys/host/linux-x86/bin/ota_from_target_files -k vendor/wiko/product/common_req/wiko/security/releasekey -i $SOURCE_FULL $TARGET_FULL $PACKAGE/$PACKAGE.zip
#./out_sys/host/linux-x86/bin/ota_from_target_files -k vendor/wiko/product/common_req/wiko/security/releasekey --carrier EEA -i $SOURCE_FULL $TARGET_FULL $PACKAGE/EEA-$PACKAGE.zip
#./out_sys/host/linux-x86/bin/ota_from_target_files -k vendor/wiko/product/common_req/wiko/security/releasekey --carrier RU  -i $SOURCE_FULL $TARGET_FULL $PACKAGE/RU-$PACKAGE.zip

# FULL-OTA
#./out_sys/host/linux-x86/bin/ota_from_target_files -k vendor/wiko/product/common_req/wiko/security/releasekey $SOURCE_FULL $PACKAGE/$PACKAGE.zip

echo `date "+%Y%m%d-%H%M%S"` >> $PACKAGE/info.txt
cat $PACKAGE/info.txt
