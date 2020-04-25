#!/bin/sh -x

PC_DIR=pcr-pc
ANDROID_DIR=pcr-android

ANDROID_TMP_DIR=/data/local/tmp
TAR="$ANDROID_TMP_DIR/busybox tar"

cd /data/project/project_dev/pcr-tools

function clean(){
rm -rf $ANDROID_DIR $PC_DIR tmp/*
}

function build_common(){
rm -rf $ANDROID_DIR $PC_DIR
mkdir $ANDROID_DIR $PC_DIR

rm -rf tmp/*
find lib/ lib/javacv/ -maxdepth 1 -name '*.jar' -exec unzip -q -o -d tmp {} ';'
rm -rf tmp/META-INF/versions
jar cf $PC_DIR/classes.jar -C tmp .
dx --dex --multi-dex --output $ANDROID_DIR $PC_DIR/classes.jar

rm -rf tmp/*
find lib/javacv/windows-x86* lib/javacv/linux-x86* -name '*.jar' -exec unzip -q -o -d tmp {} ';'
jar cf $PC_DIR/native.jar -C tmp .

find lib/javacv/android-* -name '*.jar' -exec unzip -q  -o -d $ANDROID_DIR {} ';'
rm -rf $ANDROID_DIR/META-INF

cp -rf tessdata $PC_DIR
cp -rf tessdata $ANDROID_DIR
}

function build_project(){
rm -rf tmp/*
java -Dretrolambda.inputDir=bin -Dretrolambda.classpath=bin -Dretrolambda.outputDir=tmp -jar /data/bin/retrolambda-2.5.7.jar
jar cf $PC_DIR/main.jar -C tmp .
dx --dex --output $ANDROID_DIR/main.dex $PC_DIR/main.jar
}

function package(){
tar czvf $PC_DIR.tar.gz $PC_DIR
tar czvf $ANDROID_DIR.tar.gz $ANDROID_DIR
}

function publish_android(){
adb shell rm -rf $ANDROID_TMP_DIR/$ANDROID_DIR
adb push $ANDROID_DIR.tar.gz $ANDROID_TMP_DIR
adb shell tar xf $ANDROID_TMP_DIR/$ANDROID_DIR.tar.gz -C $ANDROID_TMP_DIR
adb shell rm -f $ANDROID_TMP_DIR/$ANDROID_DIR.tar.gz
}

function update_android(){
adb push $ANDROID_DIR/main.dex $ANDROID_TMP_DIR/$ANDROID_DIR
}

function test_pc(){
#java -cp lib/rhino1_7R2.jar org.mozilla.javascript.tools.shell.Main
pushd $PC_DIR >/dev/null
local opt
#opt+=" -Xdebug -Xrunjdwp:transport=dt_socket,address=127.0.0.1:8000,suspend=y"
#opt+=" -Dorg.bytedeco.javacpp.logger.debug=true"
java -cp `find . -name '*.jar' | tr '\n' ':'` $opt com.test.main.Main "$@"
popd >/dev/null
}

function test_android_remote(){
#dalvikvm -cp rhino.dex org.mozilla.javascript.tools.shell.Main
dalvikvm -cp `find . -name '*.dex' | tr '\n' ':'` -Djava.library.path=lib/`getprop ro.product.cpu.abi` com.test.main.Main "$@"
}

function hex_string(){
hexdump -v -e '"\\\x"' -e '1/1 "%02x"'
}

function push_android(){
adb push "$@" $ANDROID_TMP_DIR/$ANDROID_DIR
}

function test_android(){
local func_name=test_android_remote
adb shell "cd $ANDROID_TMP_DIR/$ANDROID_DIR;eval \$'`type $func_name | awk 'NR>1' | hex_string`';$func_name $@"
}

"$@"