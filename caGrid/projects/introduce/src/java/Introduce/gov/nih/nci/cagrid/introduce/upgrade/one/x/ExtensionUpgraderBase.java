package gov.nih.nci.cagrid.introduce.upgrade.one.x;

import org.apache.log4j.Logger;

import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.codegen.SyncTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.upgrade.common.ExtensionUpgradeStatus;
import gov.nih.nci.cagrid.introduce.upgrade.common.ExtensionUpgraderI;

/**
 * Class must be extended to provide an extension upgrader. An extension
 * upgrader should be used to provide upgrades to the service to support a newer
 * version of an extension. The extension upgrader should only touch parts of
 * the service that the extension itself controls, i.e. the extension data in
 * the xml entity in the introduce.xml document in the service's top level
 * directory. The version attribute in the extensionType will automatically be
 * updated.
 * 
 * @author hastings
 * 
 */
public abstract class ExtensionUpgraderBase implements ExtensionUpgraderI {
    
    private static final Logger logger = Logger.getLogger(ExtensionUpgraderBase.class);

	ExtensionType extensionType;
	   ServiceInformation serviceInformation;
	    String fromVersion;
	    String toVersion;
	    String servicePath;
	    ExtensionUpgradeStatus status;

	public ExtensionUpgraderBase(String upgraderName, ExtensionType extensionType,
			ServiceInformation serviceInformation, String servicePath,
			String fromVersion, String toVersion) {
	    this.serviceInformation = serviceInformation;
        this.fromVersion = fromVersion;
        this.toVersion = toVersion;
        this.servicePath = servicePath;
		this.extensionType = extensionType;
		this.status = new ExtensionUpgradeStatus(upgraderName,this.extensionType.getName(),this.fromVersion,this.toVersion);
	}

	public void execute() throws Exception {
		logger.info("Upgrading services " + extensionType.getName()
				+ " extension  from Version " + this.getFromVersion()
				+ " to Version " + this.getToVersion());
		upgrade();
		extensionType.setVersion(getToVersion());
	}
	
	public ExtensionUpgradeStatus getStatus(){
	    return this.status;
	}

	public ExtensionType getExtensionType() {
		return extensionType;
	}

	public void setExtensionType(ExtensionType extensionType) {
		this.extensionType = extensionType;
	}
	
	   public String getFromVersion() {
	        return fromVersion;
	    }

	    public void setFromVersion(String fromVersion) {
	        this.fromVersion = fromVersion;
	    }

	    public String getToVersion() {
	        return toVersion;
	    }

	    public void setToVersion(String toVersion) {
	        this.toVersion = toVersion;
	    }

	    protected abstract void upgrade() throws Exception;

	    public ServiceInformation getServiceInformation() {
	        return serviceInformation;
	    }

	    public void setServiceInformation(ServiceInformation serviceInformation) {
	        this.serviceInformation = serviceInformation;
	    }

	    public String getServicePath() {
	        return servicePath;
	    }

	    public void setServicePath(String servicePath) {
	        this.servicePath = servicePath;
	    }
}
