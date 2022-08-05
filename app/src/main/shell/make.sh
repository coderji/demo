#!/bin/bash
LOCAL_PATH=`pwd`
export JAVA_HOME=$LOCAL_PATH/prebuilts/jdk/jdk11/linux-x86
export PATH=$JAVA_HOME/bin:$PATH

echo "Usage:"
echo "  incremental OTA"
echo "    ./make.sh v1-target.zip v2-target.zip"
echo "    ./make.sh v1-target.zip v2-target.zip -end 999"
echo "    ./make.sh v1-target.zip v2-target.zip -carrier eea"
echo ""
echo "  full OTA"
echo "    ./make.sh v1-target.zip"
echo ""

if [ $# -eq 0 ];then
  echo "See usage, need target file"
  exit 0
fi

SOURCE_FULL=$1
SOURCE_SHORT=`echo $SOURCE_FULL | awk -F '_WIKO_' '{print $2}' | awk -F '_target_' '{print $1}'`
TARGET_FULL=$2
TARGET_SHORT=`echo $TARGET_FULL | awk -F '_WIKO_' '{print $2}' | awk -F '_target_' '{print $1}'`

while [ $# -gt 0 ]; do
  case $1 in
    -end)
      END=$2
    shift
    ;;
    -carrier)
      if [[ `echo $2 | grep -i "eea"` != "" ]]; then
        CARRIER_EEA=true
      fi
      if [[ `echo $2 | grep -i "ru"` != "" ]]; then
        CARRIER_RU=true
      fi
    shift
    ;;
    *)
    shift
    ;;
  esac
done

# incremental OTA
if [[ $TARGET_SHORT != "" ]]; then
  if [[ $END != "" ]]; then
    PACKAGE=$SOURCE_SHORT-$TARGET_SHORT-$END
  else
    PACKAGE=$SOURCE_SHORT-$TARGET_SHORT
  fi
  echo "$PACKAGE"
  if [ -d $PACKAGE ]; then
    echo "$PACKAGE already exist, stop"
    exit 1
  fi
  mkdir $PACKAGE

  echo `date "+%Y%m%d-%H%M%S"` > $PACKAGE/info.txt
  echo "SOURCE_FULL=$SOURCE_FULL" >> $PACKAGE/info.txt
  echo "SOURCE_SHORT=$SOURCE_SHORT" >> $PACKAGE/info.txt
  echo "TARGET_FULL=$TARGET_FULL" >> $PACKAGE/info.txt
  echo "TARGET_SHORT=$TARGET_SHORT" >> $PACKAGE/info.txt

  if [[ `echo $SOURCE_FULL | grep -E "V770AN_|V673_|P861AE_"` != "" ]]; then
    echo "V770AN/P861AE/V673 incremental"
    echo "V770AN/P861AE/V673 incremental" >> $PACKAGE/info.txt
    ./out/host/linux-x86/bin/ota_from_target_files -k vendor/tinno/product/common_req/tinno/security/releasekey -i $SOURCE_FULL $TARGET_FULL $PACKAGE/$PACKAGE.zip
    if [[ $CARRIER_EEA != "" ]]; then
      ./out/host/linux-x86/bin/ota_from_target_files -k vendor/tinno/product/common_req/tinno/security/releasekey --carrier EEA -i $SOURCE_FULL $TARGET_FULL $PACKAGE/EEA-$PACKAGE.zip
    fi
    if [[ $CARRIER_RU != "" ]]; then
      ./out/host/linux-x86/bin/ota_from_target_files -k vendor/tinno/product/common_req/tinno/security/releasekey --carrier RU  -i $SOURCE_FULL $TARGET_FULL $PACKAGE/RU-$PACKAGE.zip
    fi
  elif [[ `echo $SOURCE_FULL | grep -E "V820AE_|Romanee_"` != "" ]]; then
    echo "V820AE/Romanee incremental"
    echo "V820AE/Romanee incremental" >> $PACKAGE/info.txt
    ./out_sys/host/linux-x86/bin/ota_from_target_files -k vendor/wiko/product/common_req/wiko/security/releasekey -i $SOURCE_FULL $TARGET_FULL $PACKAGE/$PACKAGE.zip
    if [[ $CARRIER_EEA != "" ]]; then
      ./out_sys/host/linux-x86/bin/ota_from_target_files -k vendor/wiko/product/common_req/wiko/security/releasekey --carrier EEA -i $SOURCE_FULL $TARGET_FULL $PACKAGE/EEA-$PACKAGE.zip
    fi
    if [[ $CARRIER_RU != "" ]]; then
      ./out_sys/host/linux-x86/bin/ota_from_target_files -k vendor/wiko/product/common_req/wiko/security/releasekey --carrier RU  -i $SOURCE_FULL $TARGET_FULL $PACKAGE/RU-$PACKAGE.zip
    fi
  fi

  echo `date "+%Y%m%d-%H%M%S"` >> $PACKAGE/info.txt
# full OTA
else
  PACKAGE=$SOURCE_SHORT-FULL-OTA
  echo "$PACKAGE"
  if [ -d $PACKAGE ]; then
    echo "$PACKAGE already exist, stop"
    exit 1
  fi
  mkdir $PACKAGE

  echo `date "+%Y%m%d-%H%M%S"` > $PACKAGE/info.txt
  echo "SOURCE_FULL=$SOURCE_FULL" >> $PACKAGE/info.txt
  echo "SOURCE_SHORT=$SOURCE_SHORT" >> $PACKAGE/info.txt

  if [[ `echo $SOURCE_FULL | grep -E "V770AN_|V673_|P861AE_"` != "" ]]; then
    echo "V770AN/V673/P861AE full ota"
    echo "V770AN/V673/P861AE full ota" >> $PACKAGE/info.txt
    ./out/host/linux-x86/bin/ota_from_target_files -k vendor/tinno/product/common_req/tinno/security/releasekey $SOURCE_FULL $PACKAGE/$PACKAGE.zip
  elif [[ `echo $SOURCE_FULL | grep -E "V820AE_|Romanee_"` != "" ]]; then
    echo "V820AE/Romanee full ota"
    echo "V820AE/Romanee full ota" >> $PACKAGE/info.txt
    ./out_sys/host/linux-x86/bin/ota_from_target_files -k vendor/wiko/product/common_req/wiko/security/releasekey $SOURCE_FULL $PACKAGE/$PACKAGE.zip
  fi

  echo `date "+%Y%m%d-%H%M%S"` >> $PACKAGE/info.txt
fi
