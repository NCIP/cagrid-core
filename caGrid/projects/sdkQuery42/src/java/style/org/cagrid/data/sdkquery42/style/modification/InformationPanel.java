package org.cagrid.data.sdkquery42.style.modification;

import gov.nih.nci.cagrid.common.portal.NonEditableCheckBox;
import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.common.ExtensionDataManager;
import gov.nih.nci.cagrid.data.ui.DataServiceModificationSubPanel;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.cagrid.data.sdkquery42.processor.SDK42QueryProcessor;
import java.awt.Dimension;

/** 
 *  InformationPanel
 *  Panel to display the caCORE version expected / used by this service style
 *  as well as the status of various SDK query processor features
 * 
 * @author David Ervin
 * 
 * @created Jul 27, 2007 10:38:51 AM
 * @version $Id: SDK4InformationPanel.java,v 1.3 2009-01-29 20:14:18 dervin Exp $ 
 */
public class InformationPanel extends DataServiceModificationSubPanel {
    
    private JLabel sdkLogoLabel = null;
    private JCheckBox localApiCheckBox = null;
    private JCheckBox gridIdentLoginCheckBox = null;
    private JPanel statusPanel = null;
    private JLabel versionLabel = null;
    private JLabel applicationNameLabel = null;
    private JTextField applicationNameTextField = null;
    private JPanel checkBoxPanel = null;
    private JLabel applicationHostnameLabel = null;
    private JTextField applicationHostnameTextField = null;
    private JPanel infoPanel = null;
    private JLabel applicationPortNumberLabel = null;
    private JTextField applicationPortNumberTextField = null;
    private JCheckBox useHttpsCheckBox = null;

    public InformationPanel(ServiceInformation serviceInfo, ExtensionDataManager extensionDataManager) {
        super(serviceInfo, extensionDataManager);
        initialize();
    }


    private void initialize() {
        GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
        gridBagConstraints12.gridx = 1;
        gridBagConstraints12.fill = GridBagConstraints.BOTH;
        gridBagConstraints12.weightx = 1.0D;
        gridBagConstraints12.weighty = 1.0D;
        gridBagConstraints12.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints12.gridy = 0;
        GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
        gridBagConstraints4.gridx = 0;
        gridBagConstraints4.gridy = 1;
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints2.gridy = 0;
        this.setLayout(new GridBagLayout());
        this.setSize(new Dimension(598, 190));
        this.add(getSdkLogoLabel(), gridBagConstraints2);
        this.add(getVersionLabel(), gridBagConstraints4);
        this.add(getStatusPanel(), gridBagConstraints12);
    }
    
    
    public void updateDisplayedConfiguration() throws Exception {
        String applicationName = getQueryProcessorProperty(
            SDK42QueryProcessor.PROPERTY_APPLICATION_NAME);
        getApplicationNameTextField().setText(applicationName != null ? applicationName : "");
        
        boolean useLocal = false;
        String useLocalValue = getQueryProcessorProperty(
            SDK42QueryProcessor.PROPERTY_USE_LOCAL_API);
        if (useLocalValue != null) {
            useLocal = Boolean.parseBoolean(useLocalValue);
        }
        getLocalApiCheckBox().setSelected(useLocal);
        
        getApplicationHostnameLabel().setEnabled(!useLocal);
        getApplicationHostnameTextField().setEnabled(!useLocal);
        getUseHttpsCheckBox().setEnabled(!useLocal);
        getGridIdentLoginCheckBox().setEnabled(useLocal);
        
        if (!useLocal) {
            String hostname = getQueryProcessorProperty(SDK42QueryProcessor.PROPERTY_HOST_NAME);
            String port = getQueryProcessorProperty(SDK42QueryProcessor.PROPERTY_HOST_PORT);
            String useHttpsValue = getQueryProcessorProperty(SDK42QueryProcessor.PROPERTY_HOST_HTTPS);
            getApplicationHostnameTextField().setText(hostname);
            getApplicationPortNumberTextField().setText(port);
            getUseHttpsCheckBox().setSelected(Boolean.parseBoolean(useHttpsValue));
        }
        
        boolean useGridIdent = false;
        String useGridIdentValue = getQueryProcessorProperty(SDK42QueryProcessor.PROPERTY_USE_GRID_IDENTITY_LOGIN);
        if (useGridIdentValue != null) {
            useGridIdent = Boolean.parseBoolean(useGridIdentValue);
        }
        getGridIdentLoginCheckBox().setSelected(useGridIdent);
    }
    
    
    private String getQueryProcessorProperty(String key) throws Exception {
        String paddedKey = DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + key;
        ServiceDescription desc = getServiceInfo().getServiceDescriptor();
        if (CommonTools.servicePropertyExists(desc, paddedKey)) {
            return CommonTools.getServicePropertyValue(desc, paddedKey);
        }
        return null;
    }
    
    
    private JLabel getSdkLogoLabel() {
        if (sdkLogoLabel == null) {
            sdkLogoLabel = new JLabel();
            sdkLogoLabel.setIcon(new ImageIcon(
                InformationPanel.class.getResource("sdk3.gif")));
        }
        return sdkLogoLabel;
    }


    /**
     * This method initializes localApiCheckBox	
     * 	
     * @return javax.swing.JCheckBox	
     */
    private JCheckBox getLocalApiCheckBox() {
        if (localApiCheckBox == null) {
            localApiCheckBox = new NonEditableCheckBox();
            localApiCheckBox.setText("Using Local API");
        }
        return localApiCheckBox;
    }
    
    
    /**
     * This method initializes caseInsensitiveCheckBox  
     *  
     * @return javax.swing.JCheckBox    
     */
    private JCheckBox getGridIdentLoginCheckBox() {
        if (gridIdentLoginCheckBox == null) {
            gridIdentLoginCheckBox = new NonEditableCheckBox();
            gridIdentLoginCheckBox.setText("Using Grid Identity for CSM Login");
        }
        return gridIdentLoginCheckBox;
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
            gridBagConstraints11.weightx = 1.0D;
            gridBagConstraints11.gridy = 1;
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.gridx = 0;
            gridBagConstraints10.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints10.weightx = 1.0D;
            gridBagConstraints10.gridy = 0;
            statusPanel = new JPanel();
            statusPanel.setLayout(new GridBagLayout());
            statusPanel.setBorder(BorderFactory.createTitledBorder(
                null, "Configuration Status", TitledBorder.DEFAULT_JUSTIFICATION, 
                TitledBorder.DEFAULT_POSITION, null, PortalLookAndFeel.getPanelLabelColor()));
            statusPanel.add(getInfoPanel(), gridBagConstraints10);
            statusPanel.add(getCheckBoxPanel(), gridBagConstraints11);
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
            versionLabel.setText("Version 4.2");
        }
        return versionLabel;
    }


    /**
     * This method initializes applicationNameLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getApplicationNameLabel() {
        if (applicationNameLabel == null) {
            applicationNameLabel = new JLabel();
            applicationNameLabel.setText("Application Name:");
        }
        return applicationNameLabel;
    }


    /**
     * This method initializes applicationNameTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getApplicationNameTextField() {
        if (applicationNameTextField == null) {
            applicationNameTextField = new JTextField();
            applicationNameTextField.setEditable(false);
        }
        return applicationNameTextField;
    }


    /**
     * This method initializes checkBoxPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getCheckBoxPanel() {
        if (checkBoxPanel == null) {
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints5.gridy = 0;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.gridy = 2;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.gridy = 1;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            checkBoxPanel = new JPanel();
            checkBoxPanel.setLayout(new GridBagLayout());
            checkBoxPanel.add(getLocalApiCheckBox(), gridBagConstraints2);
            checkBoxPanel.add(getGridIdentLoginCheckBox(), gridBagConstraints1);
            checkBoxPanel.add(getUseHttpsCheckBox(), gridBagConstraints5);
        }
        return checkBoxPanel;
    }


    /**
     * This method initializes applicationUrlLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getApplicationHostnameLabel() {
        if (applicationHostnameLabel == null) {
            applicationHostnameLabel = new JLabel();
            applicationHostnameLabel.setText("Application Hostname:");
        }
        return applicationHostnameLabel;
    }


    /**
     * This method initializes applicationUrlTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getApplicationHostnameTextField() {
        if (applicationHostnameTextField == null) {
            applicationHostnameTextField = new JTextField();
            applicationHostnameTextField.setEditable(false);
        }
        return applicationHostnameTextField;
    }


    /**
     * This method initializes jPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getInfoPanel() {
        if (infoPanel == null) {
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridy = 2;
            gridBagConstraints3.weightx = 1.0;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.gridx = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.gridy = 2;
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints9.gridy = 1;
            gridBagConstraints9.weightx = 1.0;
            gridBagConstraints9.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints9.gridx = 1;
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.gridx = 0;
            gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints8.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints8.gridy = 1;
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints7.gridy = 0;
            gridBagConstraints7.weightx = 1.0;
            gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints7.gridx = 1;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.gridx = 0;
            gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints6.gridy = 0;
            infoPanel = new JPanel();
            infoPanel.setLayout(new GridBagLayout());
            infoPanel.add(getApplicationNameLabel(), gridBagConstraints6);
            infoPanel.add(getApplicationNameTextField(), gridBagConstraints7);
            infoPanel.add(getApplicationHostnameLabel(), gridBagConstraints8);
            infoPanel.add(getApplicationHostnameTextField(), gridBagConstraints9);
            infoPanel.add(getApplicationPortNumberLabel(), gridBagConstraints);
            infoPanel.add(getApplicationPortNumberTextField(), gridBagConstraints3);
        }
        return infoPanel;
    }


    /**
     * This method initializes applicationPortNumberLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getApplicationPortNumberLabel() {
        if (applicationPortNumberLabel == null) {
            applicationPortNumberLabel = new JLabel();
            applicationPortNumberLabel.setText("Application Port:");
        }
        return applicationPortNumberLabel;
    }


    /**
     * This method initializes applicationPortNumberTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getApplicationPortNumberTextField() {
        if (applicationPortNumberTextField == null) {
            applicationPortNumberTextField = new JTextField();
            applicationPortNumberTextField.setEditable(false);
        }
        return applicationPortNumberTextField;
    }


    /**
     * This method initializes useHttpsCheckBox	
     * 	
     * @return javax.swing.JCheckBox	
     */
    private JCheckBox getUseHttpsCheckBox() {
        if (useHttpsCheckBox == null) {
            useHttpsCheckBox = new NonEditableCheckBox();
            useHttpsCheckBox.setText("Use HTTPS");
        }
        return useHttpsCheckBox;
    }
}  //  @jve:decl-index=0:visual-constraint="10,10"
