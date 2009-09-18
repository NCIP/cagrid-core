package gov.nih.nci.cagrid.introduce.upgrade.one.x;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import gov.nih.nci.cagrid.introduce.codegen.SyncTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.upgrade.common.IntroduceUpgradeStatus;
import gov.nih.nci.cagrid.introduce.upgrade.common.IntroduceUpgraderI;
import gov.nih.nci.cagrid.introduce.upgrade.common.StatusBase;


public abstract class IntroduceUpgraderBase implements  IntroduceUpgraderI{
    
    private static final Logger logger = Logger.getLogger(IntroduceUpgraderBase.class);
    
    ServiceInformation serviceInformation;
    IntroduceUpgradeStatus status;
    String fromVersion;
    String toVersion;
    String servicePath;


    public IntroduceUpgraderBase(IntroduceUpgradeStatus status, ServiceInformation serviceInformation, String servicePath, String fromVersion,
        String toVersion)  throws Exception {
        this.status = status;
        this.serviceInformation = serviceInformation;
        this.fromVersion = fromVersion;
        this.toVersion = toVersion;
        this.servicePath = servicePath;
        status.setFromVersion(fromVersion);
        status.setToVersion(toVersion);
        status.setType(StatusBase.UPGRADE_TYPE_INTRODUCE);
        status.setName("IntroduceUpgrader " + fromVersion + " - " + toVersion);
        
    }


    public void execute() throws Exception {
        logger.info("Upgrading Introduce Service From Version " + this.getFromVersion() + " to Version "
            + this.getToVersion());
        upgrade();
        getServiceInformation().getServiceDescriptor().setIntroduceVersion(getToVersion());
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


    public IntroduceUpgradeStatus getStatus() {
        return status;
    }


    public void setStatus(IntroduceUpgradeStatus status) {
        this.status = status;
    }

}
