@ECHO OFF
set JAVA_OPTS=%JAVA_OPTS% -Xmx256M -XX:MaxPermSize=128m
java.exe %JAVA_OPTS% -jar caGrid-gaards-ui-@project.version@.jar
