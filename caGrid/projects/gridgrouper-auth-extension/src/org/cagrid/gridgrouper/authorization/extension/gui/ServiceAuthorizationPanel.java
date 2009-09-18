package org.cagrid.gridgrouper.authorization.extension.gui;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.cagrid.gridgrouper.bean.MembershipExpression;
import gov.nih.nci.cagrid.introduce.beans.extension.AuthorizationExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionTypeExtensionData;
import gov.nih.nci.cagrid.introduce.beans.extension.PropertiesProperty;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.codegen.services.SyncServices;
import gov.nih.nci.cagrid.introduce.common.ConfigurationUtil;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;
import gov.nih.nci.cagrid.introduce.extension.utils.AxisJdomUtils;
import gov.nih.nci.cagrid.introduce.portal.extension.AbstractServiceAuthorizationPanel;

import org.apache.axis.message.MessageElement;
import org.apache.log4j.Logger;
import org.cagrid.gaards.ui.gridgrouper.expressioneditor.GridGrouperExpressionEditor;
import org.cagrid.gridgrouper.authorization.extension.common.Constants;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.globus.wsrf.utils.AnyHelper;
import org.jdom.Document;
import org.jdom.Element;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


public class ServiceAuthorizationPanel extends AbstractServiceAuthorizationPanel {
    
    private static final Logger logger = Logger.getLogger(ServiceAuthorizationPanel.class);

    private GridGrouperExpressionEditor gridgrouperExpressionEditor = null;

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
        this.add(getGridgrouperExpressionEditor(), gridBagConstraints);
    		
    }

    public ExtensionType getAuthorizationExtensionData() throws Exception {
        ExtensionType extension = new ExtensionType();
        extension.setExtensionType(ExtensionsLoader.AUTHORIZATION_EXTENSION);
        extension.setName(getAuthorizationExtensionDescriptionType().getName());  
        MembershipExpression expression = getGridgrouperExpressionEditor().getMembershipExpression();
        
        MembershipExpressionValidator.validateMembeshipExpression(expression);
        
        StringWriter sw = new StringWriter();
        Utils.serializeObject(expression, MembershipExpression.getTypeDesc().getXmlType(), sw);
        Document doc = XMLUtilities.stringToDocument(sw.toString());
        Element el = (Element)doc.getRootElement().detach();
        MessageElement elem = AxisJdomUtils.fromElement(el);
        
        ExtensionTypeExtensionData data = new ExtensionTypeExtensionData();
        data.set_any(new MessageElement[] {elem});
        extension.setExtensionData(data);
        return extension;
    }

    /**
     * This method initializes gridgrouperExpressionEditor	
     * 	
     * @return javax.swing.JPanel	
     */
    private GridGrouperExpressionEditor getGridgrouperExpressionEditor() {
        if (gridgrouperExpressionEditor == null) {
            List grouperURLs = new ArrayList();
            try {
                PropertiesProperty prop = ConfigurationUtil.getGlobalExtensionProperty("GRID_GROUPER_URLS");
                if(prop !=null){
                    StringTokenizer strtok = new StringTokenizer(prop.getValue()," ,",false);
                    while(strtok.hasMoreElements()){
                        grouperURLs.add(strtok.nextToken());
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(),e);
            }
            
            MembershipExpression exp = null;
            if(getService().getExtensions()!=null && getService().getExtensions().getExtension()!=null){
                for (int i = 0; i < getService().getExtensions().getExtension().length; i++) {
                    ExtensionType ext = getService().getExtensions().getExtension(i);
                    if(ext.getExtensionType().equals(ExtensionsLoader.AUTHORIZATION_EXTENSION)){
                        if(ext.getName().equals(Constants.GRID_GROUPER_EXTENSION_NAME)){
                            try {
                                StringReader reader = new StringReader(ext.getExtensionData().get_any()[0].getAsString());
                                exp = (MembershipExpression)Utils.deserializeObject(reader, MembershipExpression.class);       
                            } catch (Exception e) {
                                logger.error(e.getMessage(),e);
                            }
                        }
                    }
                    
                }
            }
  
            if(exp != null){
                gridgrouperExpressionEditor = new GridGrouperExpressionEditor(grouperURLs,false,exp);
            } else {
                gridgrouperExpressionEditor = new GridGrouperExpressionEditor(grouperURLs,false);
            }
           
        }
        return gridgrouperExpressionEditor;
    }


}  //  @jve:decl-index=0:visual-constraint="10,10"
