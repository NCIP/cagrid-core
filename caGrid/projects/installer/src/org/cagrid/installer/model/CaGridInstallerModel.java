/**
 * 
 */
package org.cagrid.installer.model;

import java.util.Map;

import org.pietschy.wizard.WizardModel;
import org.pietschy.wizard.WizardStep;
import org.pietschy.wizard.models.Condition;


/**
 * @author <a href="mailto:joshua.phillips@semanticbits.com">Joshua Phillips</a>
 */
public interface CaGridInstallerModel extends WizardModel {

    // Map getState();

    void add(WizardStep step);


    void add(WizardStep step, Condition condition);


    Map<String, String> getStateMap();


    void unsetProperty(String propName);


    void setProperty(String propName, String propValue);


    String getProperty(String propName);


    String getProperty(String propName, String defaultValue);


    String getMessage(String key);


    boolean isTrue(String propName);


    boolean isTomcatContainer();


    boolean isJBossContainer();


    boolean isGlobusContainer();


    boolean isSet(String propName);


    boolean isEqual(String value, String propName2);

    
    boolean isDeployGlobusRequired();


    boolean isConfigureContainerSelected();


    boolean isAntInstalled();


    boolean isTomcatInstalled();


    boolean isJBossInstalled();


    boolean isGlobusInstalled();


    boolean isCaGridInstalled();


    boolean isGlobusConfigured();


    boolean isGlobusDeployed();


    String getInstallerDir();
}
