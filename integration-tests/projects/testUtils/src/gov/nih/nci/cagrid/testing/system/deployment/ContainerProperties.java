package gov.nih.nci.cagrid.testing.system.deployment;

import java.io.File;


/** 
 *  ContainerProperties
 *  Properties of the service container
 * 
 * @author David Ervin
 * 
 * @created Oct 12, 2007 12:10:03 PM
 * @version $Id: ContainerProperties.java,v 1.1 2008-05-14 17:17:42 hastings Exp $ 
 */
public class ContainerProperties {

	private File containerDirectory = null;
	private File containerZip = null;
	private ContainerPorts portPreference = null;
	private boolean secure;

	private Integer maxStartupWaitTime;
	private Integer maxShutdownWaitTime;
	private Integer heapSizeInMegabytes;


	public ContainerProperties() {
        
	}

	public ContainerProperties(File containerDirectory, File containerZip, ContainerPorts portPreference,
		boolean secure, Integer maxStartupWaitTime, Integer maxShutdownWaitTime,
		Integer heapSizeInMegabytes) {
		this.containerDirectory = containerDirectory;
		this.containerZip = containerZip;
		this.portPreference = portPreference;
		this.secure = secure;
		this.maxStartupWaitTime = maxStartupWaitTime;
		this.maxShutdownWaitTime = maxShutdownWaitTime;
		this.heapSizeInMegabytes = heapSizeInMegabytes;
	}


	public File getContainerDirectory() {
		return containerDirectory;
	}


	public void setContainerDirectory(File containerDirectory) {
		this.containerDirectory = containerDirectory;
	}


	public File getContainerZip() {
		return containerZip;
	}


	public void setContainerZip(File containerZip) {
		this.containerZip = containerZip;
	}


	public ContainerPorts getPortPreference() {
		return portPreference;
	}


	public void setPortPreference(ContainerPorts portPreference) {
		this.portPreference = portPreference;
	}


	public boolean isSecure() {
		return secure;
	}


	public void setSecure(boolean secure) {
		this.secure = secure;
	}


	public Integer getMaxShutdownWaitTime() {
		return maxShutdownWaitTime;
	}


	public void setMaxShutdownWaitTime(Integer maxShutdownWaitTime) {
		this.maxShutdownWaitTime = maxShutdownWaitTime;
	}


	public Integer getMaxStartupWaitTime() {
		return maxStartupWaitTime;
	}


	public void setMaxStartupWaitTime(Integer maxStartupWaitTime) {
		this.maxStartupWaitTime = maxStartupWaitTime;
	}


	public Integer getHeapSizeInMegabytes() {
		return heapSizeInMegabytes;
	}
    
    
    public void setHeapSizeInMegabytes(Integer heapSizeInMegabytes) {
        this.heapSizeInMegabytes = heapSizeInMegabytes;
    }
}
