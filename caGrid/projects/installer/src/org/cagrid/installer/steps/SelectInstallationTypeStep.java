/**
 * 
 */
package org.cagrid.installer.steps;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

import org.pietschy.wizard.InvalidStateException;


/**
 * @author <a href="mailto:joshua.phillips@semanticbits.com">Joshua Phillips</a>
 */
public class SelectInstallationTypeStep extends PropertyConfigurationStep {

    /**
	 * 
	 */
    public SelectInstallationTypeStep() {

    }


    /**
     * @param name
     * @param description
     */
    public SelectInstallationTypeStep(String name, String description) {
        super(name, description);

    }


    /**
     * @param name
     * @param description
     * @param icon
     */
    public SelectInstallationTypeStep(String name, String description, Icon icon) {
        super(name, description, icon);
    }


    protected void checkComplete() {
        if (getOption(Constants.INSTALL_CONFIGURE_CAGRID) != null
            && getOption(Constants.INSTALL_CONFIGURE_CONTAINER) != null) {
            if (model.isCaGridInstalled() || ((JCheckBox) getOption(Constants.INSTALL_CONFIGURE_CAGRID)).isSelected()) {
                setComplete(true);
            } else {
                setComplete(false);
            }
        } else {
            setComplete(false);
        }
    }


    public void applyState() throws InvalidStateException {
        super.applyState();

    }


    public void prepare() {
        super.prepare();
        checkComplete();
    }

}
