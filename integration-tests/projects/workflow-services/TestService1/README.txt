Analytical Service Skeleton:
======================================
To use this skeleton you should have had it generated from the core
build file by running "ant createAnalyticalSkeleton" from the top level
of the caGrid core.  This should have created you this skeleton.  The instructions
below will help you in configuring, building, and deploying this analytical
service.

All that is needed for this service at this point is to populate the analytical
service provider class in the src/ directory.

Prerequisits:
=======================================
Globus 4.0 installed and GLOBUS_LOCATION env defined
Tomcat > 4.0 installed and "CATALINA_HOME" env defined

To Build:
=======================================
"ant all" will build 
"ant deployTomcat" will deploy to "CATALINA_HOME"
