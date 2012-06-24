#!/bin/bash
WORKING_DIR=`pwd`

function badebug() {
    echo "[config-ndk] $*"
}

if [ ! -e "android-ndk-r8-linux-x86.tar.bz2" ]
then
    badebug "Downloading Android NDK r8 from Google..."
    wget http://dl.google.com/android/ndk/android-ndk-r8-linux-x86.tar.bz2
fi

if [ ! -e "android-ndk-r8" ]
then
    badebug "Unpacking the downloaded package..."
    tar xjf android-ndk-r8-linux-x86.tar.bz2
fi

if [ ! -e "my-ndk-r8" ]
then
    badebug "Creating standalone toolchain..."
    cd android-ndk-r8
    ./build/tools/make-standalone-toolchain.sh --arch=android-9 --install-dir="$WORKING_DIR/my-ndk-r8" --toolchain=arm-linux-androideabi-4.4.3 --arch=arm
    cd -
fi

badebug "NDK configuration completed!"

