@ECHO OFF
java.exe -Xmx256M -XX:MaxPermSize=128m -Djava.endorsed.dirs=endorsed -jar caGrid-gaards-ui-@project.version@.jar
