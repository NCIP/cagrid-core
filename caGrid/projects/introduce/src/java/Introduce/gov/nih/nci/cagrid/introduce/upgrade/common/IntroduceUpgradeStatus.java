package gov.nih.nci.cagrid.introduce.upgrade.common;

import java.util.ArrayList;
import java.util.List;

public class IntroduceUpgradeStatus extends StatusBase {

    private List<ExtensionUpgradeStatus> extensionUpgradesStatus = null;

    public IntroduceUpgradeStatus() {
        extensionUpgradesStatus = new ArrayList<ExtensionUpgradeStatus>();
    }
    
    public void addExtensionUpgradeStatus(ExtensionUpgradeStatus extensionStatus){
        this.extensionUpgradesStatus.add(extensionStatus);
    }
    
    public List<ExtensionUpgradeStatus> getExtensionUgradesStatus(){
        return this.extensionUpgradesStatus;
    }
}
