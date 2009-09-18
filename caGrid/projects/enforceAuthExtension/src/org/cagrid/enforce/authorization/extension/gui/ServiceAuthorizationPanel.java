package org.cagrid.enforce.authorization.extension.gui;

import gov.nih.nci.cagrid.introduce.beans.extension.AuthorizationExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;
import gov.nih.nci.cagrid.introduce.portal.extension.AbstractServiceAuthorizationPanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import org.apache.log4j.Logger;


public class ServiceAuthorizationPanel extends AbstractServiceAuthorizationPanel {
    
    private static final Logger logger = Logger.getLogger(ServiceAuthorizationPanel.class);

    public ServiceAuthorizationPanel(AuthorizationExtensionDescriptionType authDesc, ServiceInformation serviceInfo,
        ServiceType service) {
        super(authDesc, serviceInfo, service);
		initialize();
        // TODO Auto-generated constructor stub
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0D;
        gridBagConstraints.weighty = 1.0D;
        gridBagConstraints.gridy = 0;
        this.setLayout(new GridBagLayout());
     		
    }

    public ExtensionType getAuthorizationExtensionData() throws Exception {
        ExtensionType extension = new ExtensionType();
        extension.setExtensionType(ExtensionsLoader.AUTHORIZATION_EXTENSION);
        extension.setName(getAuthorizationExtensionDescriptionType().getName());  

        return extension;
    }


}  //  @jve:decl-index=0:visual-constraint="10,10"
