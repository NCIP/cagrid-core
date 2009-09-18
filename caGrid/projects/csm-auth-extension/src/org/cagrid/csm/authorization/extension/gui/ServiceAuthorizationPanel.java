package org.cagrid.csm.authorization.extension.gui;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.cagrid.introduce.beans.extension.AuthorizationExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionTypeExtensionData;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.common.SpecificServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;
import gov.nih.nci.cagrid.introduce.extension.utils.AxisJdomUtils;
import gov.nih.nci.cagrid.introduce.portal.extension.AbstractServiceAuthorizationPanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.axis.message.MessageElement;
import org.apache.log4j.Logger;
import org.cagrid.csm.authorization.extension.beans.CSMAuthorizationDescription;
import org.jdom.Document;
import org.jdom.Element;


public class ServiceAuthorizationPanel extends AbstractServiceAuthorizationPanel {
    private static final Logger logger = Logger.getLogger(ServiceAuthorizationPanel.class);

    private CSMAuthorizationPanel csmAuthorizationPanel = null;
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
        gridBagConstraints.weightx = 1.0D;
        gridBagConstraints.weighty = 1.0D;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.gridy = 0;
        this.setLayout(new GridBagLayout());
        this.add(getCsmAuthorizationPanel(), gridBagConstraints);
    		
    }

    @Override
    public ExtensionType getAuthorizationExtensionData() throws Exception {
        CSMAuthorizationDescription desc = getCsmAuthorizationPanel().getAuthorization();
        
        ExtensionType extension = new ExtensionType();
        extension.setExtensionType(ExtensionsLoader.AUTHORIZATION_EXTENSION);
        extension.setName(getAuthorizationExtensionDescriptionType().getName());  
         
        StringWriter sw = new StringWriter();
        Utils.serializeObject(desc, desc.getTypeDesc().getXmlType(), sw);
        Document doc = XMLUtilities.stringToDocument(sw.toString());
        Element el = (Element)doc.getRootElement().detach();
        MessageElement elem = AxisJdomUtils.fromElement(el);
        
        ExtensionTypeExtensionData data = new ExtensionTypeExtensionData();
        data.set_any(new MessageElement[] {elem});
        extension.setExtensionData(data);
        return extension;
    }

    /**
     * This method initializes csmAuthorizationPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private CSMAuthorizationPanel getCsmAuthorizationPanel() {
        if (csmAuthorizationPanel == null) {
            CSMAuthorizationDescription authDescription = null;
            if(getService().getExtensions()!=null && getService().getExtensions().getExtension()!=null){
                for(int i = 0; i < getService().getExtensions().getExtension().length; i++){
                    ExtensionType ext = getService().getExtensions().getExtension(i);
                    if(ext.getName().equals(org.cagrid.csm.authorization.extension.common.Constants.CSM_EXTENSION_NAME)){
                        try {
                            StringReader reader = new StringReader(ext.getExtensionData().get_any()[0].getAsString());
                            authDescription = (CSMAuthorizationDescription)Utils.deserializeObject(reader, CSMAuthorizationDescription.class);       
                        } catch (Exception e) {
                            logger.error(e.getMessage(),e);
                        }
                    }
                }
            }
            csmAuthorizationPanel = new CSMAuthorizationPanel(new SpecificServiceInformation(getServiceInformation(),getService()),authDescription);
        }
        return csmAuthorizationPanel;
    }

  

}  
