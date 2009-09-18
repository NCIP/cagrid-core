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