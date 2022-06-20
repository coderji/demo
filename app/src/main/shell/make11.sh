#!/bin/bash
LOCAL_PATH=`pwd`
export JAVA_HOME=$LOCAL_PATH/prebuilts/jdk/jdk11/linux-x86
export PATH=$JAVA_HOME/bin:$PATH

SOURCE_FULL=P861AE_PVT_11.0_WIKO_CFC_034-002_2022-02-21-22_target_20220222012816.zip
SOURCE_SHORT=CFC34.2-022122
TARGET_FULL=P861AE_MP_11.0_WIKO_CFC_047_2022-06-11-22_target_20220612035143.zip
TARGET_SHORT=CFC47-061122

PACKAGE=$SOURCE_SHORT-$TARGET_SHORT
if [ -d $PACKAGE ]; then
echo "$PACKAGE already exist"
exit 1
fi
mkdir $PACKAGE

echo "make11" > $PACKAGE/info.txt
echo `date "+%Y%m%d-%H%M%S"` >> $PACKAGE/info.txt
echo "SOURCE_FULL=$SOURCE_FULL" >> $PACKAGE/info.txt
echo "SOURCE_SHORT=$SOURCE_SHORT" >> $PACKAGE/info.txt
echo "TARGET_FULL=$TARGET_FULL" >> $PACKAGE/info.txt
echo "TARGET_SHORT=$TARGET_SHORT" >> $PACKAGE/info.txt

./out/host/linux-x86/bin/ota_from_target_files -k vendor/tinno/product/common_req/tinno/security/releasekey -i $SOURCE_FULL $TARGET_FULL $PACKAGE/$PACKAGE.zip
#./out/host/linux-x86/bin/ota_from_target_files -k vendor/tinno/product/common_req/tinno/security/releasekey --carrier EEA -i $SOURCE_FULL $TARGET_FULL $PACKAGE/EEA-$PACKAGE.zip
#./out/host/linux-x86/bin/ota_from_target_files -k vendor/tinno/product/common_req/tinno/security/releasekey --carrier RU  -i $SOURCE_FULL $TARGET_FULL $PACKAGE/RU-$PACKAGE.zip

# FULL-OTA
#./out/host/linux-x86/bin/ota_from_target_files -k vendor/tinno/product/common_req/tinno/security/releasekey $SOURCE_FULL $PACKAGE/$PACKAGE.zip

echo `date "+%Y%m%d-%H%M%S"` >> $PACKAGE/info.txt
cat $PACKAGE/info.txt
