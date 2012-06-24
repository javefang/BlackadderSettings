#!/bin/bash
function badebug() {
	echo "[clean-ba] $*"
}

badebug "Clearing blackadder..."
cd blackadder/src
make clean
make distclean
cd -

badebug "Clearing click..."
cd click
make clean
make distclean
cd -


