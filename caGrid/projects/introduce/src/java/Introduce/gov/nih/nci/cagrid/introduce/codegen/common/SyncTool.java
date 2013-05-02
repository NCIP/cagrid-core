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
package gov.nih.nci.cagrid.introduce.codegen.common;

import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.io.File;


public abstract class SyncTool {
	private ServiceInformation info;
	private File baseDirectory;


	public SyncTool(File baseDirectory, ServiceInformation info) {
		this.info = info;
		this.baseDirectory = baseDirectory;

	}


	public File getBaseDirectory() {
		return baseDirectory;
	}


	public void setBaseDirectory(File baseDirectory) {
		this.baseDirectory = baseDirectory;
	}


	public ServiceInformation getServiceInformation() {
		return info;
	}


	public void setServiceInformation(ServiceInformation info) {
		this.info = info;
	}


	public abstract void sync() throws SynchronizationException;

}
