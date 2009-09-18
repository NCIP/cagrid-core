#!/bin/sh

cd /tmp

# GET THE NEW VERSION
rm -rf hudson.war
wget http://hudson.gotdns.com/latest/hudson.war

#BACKUP THE OLD
rm -rf hudson.war.backup
cp $CATALINA_HOME/webapps/ROOT.war /tmp/hudson.war.backup

#SHUDOWN TOMCAT
$CATALINA_HOME/bin/shutdown.sh
sleep 10

# BACKUP THE CUSTOMIZATIONS 
cp $CATALINA_HOME/webapps/ROOT/images/24x24/caGrid.gif /tmp
cp $CATALINA_HOME/webapps/ROOT/caGrid-1.0/Dashboard/index.html /tmp

# INSTALL THE NEW VERSION
rm -rf $CATALINA_HOME/webapps/ROOT
cp hudson.war $CATALINA_HOME/webapps/ROOT.war
$CATALINA_HOME/bin/startup.sh
sleep 10

# RESTORE THE CUSTOMIZATIONS
mkdir $CATALINA_HOME/webapps/ROOT/caGrid-1.0
mkdir $CATALINA_HOME/webapps/ROOT/caGrid-1.0/Dashboard
cp /tmp/index.html $CATALINA_HOME/webapps/ROOT/caGrid-1.0/Dashboard
cp /tmp/caGrid.gif $CATALINA_HOME/webapps/ROOT/images/24x24

