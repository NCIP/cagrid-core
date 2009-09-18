package gov.nih.nci.cagrid.data.ui.auditors;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.auditing.DataServiceAuditors;
import gov.nih.nci.cagrid.data.common.ExtensionDataManager;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionTypeExtensionData;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.CodegenExtensionException;
import gov.nih.nci.cagrid.introduce.portal.extension.ServiceDeploymentUIPanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.FileWriter;

import org.cagrid.grape.utils.CompositeErrorDialog;

/** 
 *  AuditorDeploymentConfigPanel
 *  Panel for deployment tiem configuration of Data Service auditors
 * 
 * @author David Ervin
 * 
 * @created May 24, 2007 1:34:32 PM
 * @version $Id: AuditorDeploymentConfigPanel.java,v 1.4 2008-02-07 14:43:56 hastings Exp $ 
 */
public class AuditorDeploymentConfigPanel extends ServiceDeploymentUIPanel {
    
    private AuditorsConfigurationPanel auditorsConfigPanel = null;
    private ExtensionDataManager customExtensionDataManager = null;
    
    public AuditorDeploymentConfigPanel(ServiceExtensionDescriptionType desc, ServiceInformation info) {
        super(desc, info);
        ExtensionTypeExtensionData extensionData = getExtensionTypeExtensionData();
        // custom extension data manager writes to filesystem every time a save is made
        customExtensionDataManager = new ExtensionDataManager(extensionData) {
            public void storeAuditorsConfiguration(DataServiceAuditors auditors) throws Exception {
                super.storeAuditorsConfiguration(auditors);
                writeAuditorsConfiguration(auditors);
            }
        };
        initialize();
    }
    
    
    private void initialize() {
        setLayout(new GridBagLayout());
        GridBagConstraints cons = new GridBagConstraints();
        cons.gridx = 0;
        cons.gridy = 0;
        cons.fill = GridBagConstraints.BOTH;
        cons.weightx = 1.0D;
        cons.weighty = 1.0D;
        add(getAuditorsConfigPanel(), cons);
    }
    

    public void resetGUI() {
        try {
            getAuditorsConfigPanel().updateDisplayedConfiguration();
        } catch (Exception ex) {
            ex.printStackTrace();
            CompositeErrorDialog.showErrorDialog("Error displaying auditor configuration", 
                ex.getMessage(), ex);
        }
    }
    
    
    private AuditorsConfigurationPanel getAuditorsConfigPanel() {
        if (auditorsConfigPanel == null) {
            auditorsConfigPanel = new AuditorsConfigurationPanel(
                getServiceInfo(), customExtensionDataManager);
        }
        return auditorsConfigPanel;
    }
    
    
    private void writeAuditorsConfiguration(DataServiceAuditors auditors) throws Exception {
        if (CommonTools.servicePropertyExists(
            getServiceInfo().getServiceDescriptor(), 
            DataServiceConstants.DATA_SERVICE_AUDITORS_CONFIG_FILE_PROPERTY)) {
            try {
                // get the name of the auditor config file
                String filename = CommonTools.getServicePropertyValue(
                    getServiceInfo().getServiceDescriptor(), 
                    DataServiceConstants.DATA_SERVICE_AUDITORS_CONFIG_FILE_PROPERTY);
                File outFile = new File(getServiceInfo().getBaseDirectory().getAbsolutePath() 
                    + File.separator + "etc" + File.separator + filename);
                FileWriter writer = new FileWriter(outFile);
                Utils.serializeObject(auditors, 
                    DataServiceConstants.DATA_SERVICE_AUDITORS_QNAME, writer);
                writer.flush();
                writer.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new CodegenExtensionException(
                    "Error writing auditor configuration: " + ex.getMessage(), ex);
            }
        }
    }


    @Override
    public void preDeploy() {
        // TODO Auto-generated method stub
        
    }
}
