#!/bin/bash
function badebug() {
	echo "[build-ba] $*"
}
WORKING_DIR=`pwd`

if [ -z $ANDROID_NDK ]
then
	badebug "Variable ANDROID_NDK not set, configure local NDK..."
	./config-ndk.sh
	export ANDROID_NDK="android-ndk-r8"
	export PATH=$PATH:"$WORKING_DIR/my-ndk-r8/bin"
fi

badebug "Configuring prefix path..."
export CLICK_PREFIX="/data/click"
sudo mkdir -p "$CLICK_PREFIX"
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

badebug "Click and Blackadder source compiled!"
