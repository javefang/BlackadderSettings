#!/bin/bash
CLICK_PREFIX="/data/click"
CLICK_ANDROID="/data/click"

# uploading click/blackadder binary and libraries
adb push $CLICK_PREFIX $CLICK_ANDROID
adb push runclick $CLICK_ANDROID/

# uploading test files
adb push click/conf/test.click $CLICK_ANDROID/
adb push blackadder/src/sample.conf $CLICK_ANDROID/

# uploading environment setting script
adb push .bashrc /sdcard/
adb push runclick $CLICK_ANDROID/

# create blackadder settings directory
adb shell mkdir /sdcard/blackadder

