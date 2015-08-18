#!/bin/sh


sed -i \
	-e '/编译日期/s/$/'"`date "+%F %R %A"`"'/' \
	-e '/当前版本/s/$/'"`sed -n '/versionName=/s/.*versionName="\(.*\)".*$/\1/p' AndroidManifest.xml`"'/' \
	res/values/about.xml 

ant release

git checkout res/values/about.xml
