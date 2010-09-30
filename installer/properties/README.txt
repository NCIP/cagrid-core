This location is to maintain and version the cagrid.installer.properties files used by the caGrid installer.

The general process for creating an installer for a specific version of caGrid is detailed in the release process document, which specifies placing the finalized cagrid.installer.properties file in this location and referencing it when calling the installer's dist-installer ant target.

In this way, the files downloaded by the installer (released on GForge) can be changed at a future time without the need to regenerate the installer.
