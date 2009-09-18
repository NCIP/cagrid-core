package gov.nih.nci.cagrid.introduce.test;

import java.io.File;

public abstract class TestCaseInfo {

	public abstract String getDir();

	public abstract String getName();

	public abstract String getNamespace();

	public abstract String getPackageName();

	public abstract String getPackageDir();
	
	public abstract String getResourceFrameworkType();
	
	public String getExtensions(){
	    return "";
	}

}