#!/bin/sh
JAVA_OPTS="$JAVA_OPTS -Xmx256M -XX:MaxPermSize=128m " 
java $JAVA_OPTS -jar caGrid-gaards-ui-@project.version@.jar
