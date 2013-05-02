/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
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
