@ECHO OFF

REM Check for "-proxy" flag to indicate to use proxy
:CheckArgs
IF "%1"=="" (
    GOTO Execute
)
IF "%1"=="-proxy" (
    GOTO SetProxy
)
IF NOT "%1"=="-proxy" (
    GOTO Usage
)
SHIFT
GOTO CheckArgs

:SetProxy
echo Setting proxy values in JAVA_OPTS
REM Java Proxy Configuration Details: http://java.sun.com/javase/6/docs/technotes/guides/net/properties.html

REM HTTP proxy settings
REM set JAVA_OPTS=%JAVA_OPTS% -Dhttp.proxyHost= -Dhttp.proxyPort= -Dhttp.nonProxyHosts=\"\"

REM Set username and password for HTTP proxies that require authentication
REM set JAVA_OPTS=%JAVA_OPTS% -Dhttp.proxyPassword= -Dhttp.proxyUser=

REM FTP proxy settings 
REM The installer uses ftp for one file.
REM set JAVA_OPTS=%JAVA_OPTS% -Dftp.proxyHost= -Dftp.proxyPort= -Dftp.nonProxyHosts=\"\"

:Execute
java.exe %JAVA_OPTS% -jar caGrid-installer-1.4-dev.jar
GOTO End

:Usage
ECHO Usage: install.bat (-proxy)

:End