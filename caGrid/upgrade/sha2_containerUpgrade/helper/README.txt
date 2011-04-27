This is a helper program for the container migration script.

It copies .jar files into the new container's lib directory, either from the
old container or from cagrid_home, as it thinks is appropriate.  It also 
updates the sevice's introduceDeployment.xml to reflect changes in file names. 