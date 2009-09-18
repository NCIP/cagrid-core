WS-Enumeration support for Globus Toolkit 4.0.2

Latest available from http://www-unix.mcs.anl.gov/~gawor/ws-enum/

Applying the patch:

1. Download and unzip Globus WS-Core toolkit source 4.0.3
2. Copy patch files and apply.sh to WS-Core location
3. Execute apply.sh
4. Rebuild WS-Core (ant all)
5. Redeploy WS-Core to Tomcat:
	a. cd "%GLOBUS_LOCATION%"
	b. ant -f share\globus_wsrf_common\tomcat\tomcat.xml deployTomcat -Dtomcat.dir="%CATALINA_HOME%"
 
