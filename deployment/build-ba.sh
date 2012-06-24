#!/bin/bash
export ANDROID_NDK="/home/jave/program/android-ndk-r7"
export CLICK_PREFIX="/data/click"

function badebug() {
	echo "[build-ba] $*"
}

badebug "Setting prefix path owner..."
sudo chown "$USER": "$CLICK_PREFIX"

badebug "Compiling click..."
cd click
./ndk-make-click.sh
make install
cd -

badebug "Compiling blackadder..."
cd blackadder/src
autoconf
./ndk-make-ba.sh
make install
cd -

badebug "Copying libgnustl_shared.so..."
cp $ANDROID_NDK/sources/cxx-stl/gnu-libstdc++/libs/armeabi-v7a/libgnustl_shared.so $CLICK_PREFIX/lib

badebug "Removing unnecessary files..."
rm -r $CLICK_PREFIX/share
rm -r $CLICK_PREFIX/include
find $CLICK_PREFIX/bin -type f | grep -v click$ | xargs rm


