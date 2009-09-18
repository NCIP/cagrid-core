package gov.nih.nci.cagrid.data.style.cacore31.modification;

import gov.nih.nci.cagrid.common.portal.NonEditableCheckBox;
import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.common.ExtensionDataManager;
import gov.nih.nci.cagrid.data.ui.DataServiceModificationSubPanel;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.awt.AWTEvent;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/** 
 *  CaCoreVersionPanel
 *  Panel to display the caCORE version expected / used by this service style
 *  as well as the status of various SDK query processor features
 * 
 * @author David Ervin
 * 
 * @created Jul 27, 2007 10:38:51 AM
 * @version $Id: CaCoreVersionPanel.java,v 1.2 2009-01-29 20:14:17 dervin Exp $ 
 */
public class CaCoreVersionPanel extends DataServiceModificationSubPanel {
    // from HQLCoreQueryProcessor...
    public static final String USE_CSM_FLAG = "useCsmSecurity";
    public static final String CASE_INSENSITIVE_QUERYING = "queryCaseInsensitive";
    
    private JLabel sdkLogoLabel = null;
    private JCheckBox usingCsmCheckBox = null;
    private JCheckBox caseInsensitiveCheckBox = null;
    private JPanel statusPanel = null;
    private JLabel versionLabel = null;

    public CaCoreVersionPanel(ServiceInformation serviceInfo, ExtensionDataManager extensionDataManager) {
        super(serviceInfo, extensionDataManager);
        initialize();
    }


    private void initialize() {
        GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
        gridBagConstraints4.gridx = 0;
        gridBagConstraints4.gridy = 1;
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.gridx = 1;
        gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints3.anchor = GridBagConstraints.NORTH;
        gridBagConstraints3.fill = GridBagConstraints.BOTH;
        gridBagConstraints3.weightx = 1.0D;
        gridBagConstraints3.gridy = 0;
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints2.gridy = 0;
        this.setLayout(new GridBagLayout());
        this.add(getSdkLogoLabel(), gridBagConstraints2);
        this.add(getStatusPanel(), gridBagConstraints3);
        this.add(getVersionLabel(), gridBagConstraints4);
    }
    
    
    public void updateDisplayedConfiguration() throws Exception {
        if (CommonTools.servicePropertyExists(getServiceInfo().getServiceDescriptor(),
                DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + USE_CSM_FLAG)) {
            boolean useCsm = Boolean.parseBoolean(CommonTools.getServicePropertyValue(
                getServiceInfo().getServiceDescriptor(),
                DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + USE_CSM_FLAG));
            getUsingCsmCheckBox().setSelected(useCsm);
        }
        if (CommonTools.servicePropertyExists(getServiceInfo().getServiceDescriptor(),
                DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + CASE_INSENSITIVE_QUERYING)) {
            boolean caseInsensitive = Boolean.parseBoolean(CommonTools.getServicePropertyValue(
                getServiceInfo().getServiceDescriptor(), 
                DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + CASE_INSENSITIVE_QUERYING));
            getCaseInsensitiveCheckBox().setSelected(caseInsensitive);
        }
    }
    
    
    private JLabel getSdkLogoLabel() {
        if (sdkLogoLabel == null) {
            sdkLogoLabel = new JLabel();
            sdkLogoLabel.setIcon(new ImageIcon(
                CaCoreVersionPanel.class.getResource("sdk3.gif")));
        }
        return sdkLogoLabel;
    }


    /**
     * This method initializes usingCsmCheckBox	
     * 	
     * @return javax.swing.JCheckBox	
     */
    private JCheckBox getUsingCsmCheckBox() {
        if (usingCsmCheckBox == null) {
            usingCsmCheckBox = new NonEditableCheckBox();
            usingCsmCheckBox.setText("Using CSM");
        }
        return usingCsmCheckBox;
    }
    
    
    /**
     * This method initializes caseInsensitiveCheckBox  
     *  
     * @return javax.swing.JCheckBox    
     */
    private JCheckBox getCaseInsensitiveCheckBox() {
        if (caseInsensitiveCheckBox == null) {
            caseInsensitiveCheckBox = new JCheckBox() {
                public void processEvent(AWTEvent e) {
                    /// nuke all events
                }
            };
            Font font = caseInsensitiveCheckBox.getFont();
            Font italFont = new Font(font.getName(), Font.ITALIC, font.getSize());
            caseInsensitiveCheckBox.setFont(italFont);
            caseInsensitiveCheckBox.setText("Case Insensitive Queries");
        }
        return caseInsensitiveCheckBox;
    }


    /**
     * This method initializes statusPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getStatusPanel() {
        if (statusPanel == null) {
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridx = 0;
            gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints11.gridy = 1;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.gridy = 0;
            statusPanel = new JPanel();
            statusPanel.setLayout(new GridBagLayout());
            statusPanel.setBorder(BorderFactory.createTitledBorder(
                null, "Configuration Status", TitledBorder.DEFAULT_JUSTIFICATION, 
                TitledBorder.DEFAULT_POSITION, null, PortalLookAndFeel.getPanelLabelColor()));
            statusPanel.add(getUsingCsmCheckBox(), gridBagConstraints1);
            statusPanel.add(getCaseInsensitiveCheckBox(), gridBagConstraints11);
        }
        return statusPanel;
    }


    /**
     * This method initializes versionLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getVersionLabel() {
        if (versionLabel == null) {
            versionLabel = new JLabel();
            versionLabel.setText("Version 3.1");
        }
        return versionLabel;
    }
}
