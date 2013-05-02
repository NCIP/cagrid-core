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
package gov.nih.nci.cagrid.introduce.upgrade.common;

import gov.nih.nci.cagrid.introduce.codegen.SyncTools;

import org.apache.log4j.Logger;



public abstract class ModelUpgraderBase implements ModelUpgraderI {
    
    private static final Logger logger = Logger.getLogger(ModelUpgraderBase.class);

	String fromVersion;
	String toVersion;
	String servicePath;
	IntroduceUpgradeStatus status;

	public ModelUpgraderBase(IntroduceUpgradeStatus status, String servicePath,
			String fromVersion, String toVersion) {
	    this.status = status;
		this.fromVersion = fromVersion;
		this.toVersion = toVersion;
		this.servicePath = servicePath;
	}

	public String getFromVersion() {
		return fromVersion;
	}

	public void setFromVersion(String fromVersion) {
		this.fromVersion = fromVersion;
	}
	
	public IntroduceUpgradeStatus getStatus(){
	    return this.status;
	}

	public String getToVersion() {
		return toVersion;
	}

	public void setToVersion(String toVersion) {
		this.toVersion = toVersion;
	}

	protected abstract void upgrade() throws Exception;

	public void execute() throws Exception {
	    logger.info("Upgrading introduce model"
            + " from Version " + this.getFromVersion()
            + " to Version " + this.getToVersion());
		this.upgrade();
	}

	public String getServicePath() {
		return servicePath;
	}

	public void setServicePath(String servicePath) {
		this.servicePath = servicePath;
	}
}
