#!/bin/sh

proxy=off
while [ $# -gt 0 ]
do
    case "$1" in
        -proxy)  proxy=on;;
        *) echo "USAGE: ${0##*/} (-proxy)"; exit 1;; ## Do nothing; continue with next command
    esac
    shift
done

if [ "$proxy" == on ]
then
    echo "Setting proxy values to JAVA_OPTS"

    # Java Proxy Configuration Details: http://java.sun.com/javase/6/docs/technotes/guides/net/properties.html
    
    # HTTP proxy settings. nonProxyHosts= pipe (|) delimited list of hosts that must not be proxied
    #JAVA_OPTS="-DproxySet= -Dhttp.proxyHost= -Dhttp.proxyPort=8080 -Dhttp.nonProxyHosts=\"localhost\" $JAVA_OPTS"
    
    # HTTPS proxy settings
    #JAVA_OPTS="-Dhttps.proxyHost= -Dhttps.proxyPort= -Dhttps.nonProxyHosts=\"\" $JAVA_OPTS"
    
    # Set username and password for HTTP proxies that require authentication
    #JAVA_OPTS="-Dhttp.proxyPassword= -Dhttp.proxyUser= $JAVA_OPTS"
    
    # FTP proxy settings 
    # The installer uses ftp for one file.
    #JAVA_OPTS="-Dftp.proxyHost= -Dftp.proxyPort= -Dftp.nonProxyHosts=\"\" $JAVA_OPTS"
fi

java $JAVA_OPTS -jar caGrid-installer-@CAGRID_VERSION@.jar
