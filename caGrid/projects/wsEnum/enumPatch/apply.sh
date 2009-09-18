#!/bin/sh

DIR=`dirname $0`

patch -p0 -i $DIR/enum.base.patch
patch -p1 -i $DIR/enum.patch

patch -p1 -i $DIR/enum.sample.patch

patch -p0 -i $DIR/enum.test.base.patch
patch -p1 -i $DIR/enum.test.patch
