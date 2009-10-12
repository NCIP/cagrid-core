package org.cagrid.data.sdkquery42.style.wizard;

import gov.nih.nci.cagrid.common.portal.DocumentChangeAdapter;
import gov.nih.nci.cagrid.common.portal.validation.IconFeedbackPanel;
import gov.nih.nci.cagrid.data.ui.GroupSelectionListener;
import gov.nih.nci.cagrid.data.ui.NotifyingButtonGroup;
import gov.nih.nci.cagrid.data.ui.wizard.AbstractWizardPanel;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.ResourceManager;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.cagrid.data.sdkquery42.style.wizard.config.ProjectSelectionConfigurationStep;
import org.cagrid.grape.utils.CompositeErrorDialog;

import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.util.ValidationUtils;
import com.jgoodies.validation.view.ValidationComponentUtils;

public class ProjectSelectionPanel extends AbstractWizardPanel {
    
    // keys for validation
    private static final String KEY_APPLICATION_NAME = "Application Name";
    private static final String KEY_REMOTE_CLIENT_DIR = "Remote Client directory";
    private static final String KEY_LOCAL_CLIENT_DIR = "Local Client directory";
    private static final String KEY_HOSTNAME = "Application Hostname";
    private static final String KEY_PORT = "Application Port";
    
    private ValidationResultModel validationModel = null;
    private DocumentListener textFieldChangeListener = null;
    private ProjectSelectionConfigurationStep configuration = null;
    
    private IconFeedbackPanel validationPanel = null;
    private JPanel mainPanel = null;
    private JLabel applicationNameLabel = null;
    private JTextField applicationNameTextField = null;
    private JLabel remoteClientDirLabel = null;
    private JTextField remoteClientDirTextField = null;
    private JButton remoteClientDirBrowseButton = null;
    private JLabel localClientDirLabel = null;
    private JTextField localClientDirTextField = null;
    private JButton localClientDirBrowseButton = null;
    private JRadioButton localApiRadioButton = null;
    private JRadioButton remoteApiRadioButton = null;
    private JPanel localApiPanel = null;
    private JPanel remoteApiPanel = null;
    private JPanel applicationNamePanel = null;
    private JLabel hostnameLabel = null;
    private JTextField hostnameTextField = null;
    private JLabel portLabel = null;
    private JTextField portTextField = null;
    private JCheckBox useHttpsCheckBox = null;
    
    public ProjectSelectionPanel(ServiceExtensionDescriptionType extensionDescription, ServiceInformation info) {
        super(extensionDescription, info);
        this.validationModel = new DefaultValidationResultModel();
        this.configuration = new ProjectSelectionConfigurationStep(info);
        this.textFieldChangeListener = new DocumentChangeAdapter() {
            public void documentEdited(DocumentEvent e) {
                validateInput();                
            }
        };
        initialize();
    }


    public String getPanelShortName() {
        return "SDK Output";
    }


    public String getPanelTitle() {
        return "SDK Project Build Artifacts";
    }


    public void update() {
        getApplicationNameTextField().setText(configuration.getApplicationName());
        getLocalClientDirTextField().setText(configuration.getLocalClientDir());
        getRemoteClientDirTextField().setText(configuration.getRemoteClientDir());
        getHostnameTextField().setText(configuration.getApplicationHostname());
        getPortTextField().setText(configuration.getApplicationPort() != null ? 
            configuration.getApplicationPort().toString() : null);
        getUseHttpsCheckBox().setSelected(configuration.isUseHttps());
        getLocalApiRadioButton().setSelected(configuration.isLocalApi());
        getRemoteApiRadioButton().setSelected(!configuration.isLocalApi());
    }
    
    
    public void movingNext() {
        try {
            configuration.applyConfiguration();
        } catch (Exception e) {
            CompositeErrorDialog.showErrorDialog("Error configuring: " + e.getMessage(), e);
        }
    }
    
    
    private void initialize() {
        initLocalRemoteGroup();
        setLayout(new GridLayout(1, 1));
        add(getValidationPanel());
        configureValidation();
    }
    
    
    private void configureValidation() {
        ValidationComponentUtils.setMessageKey(getApplicationNameTextField(), KEY_APPLICATION_NAME);
        ValidationComponentUtils.setMessageKey(getRemoteClientDirTextField(), KEY_REMOTE_CLIENT_DIR);
        ValidationComponentUtils.setMessageKey(getLocalClientDirTextField(), KEY_LOCAL_CLIENT_DIR);
        ValidationComponentUtils.setMessageKey(getHostnameTextField(), KEY_HOSTNAME);
        ValidationComponentUtils.setMessageKey(getPortTextField(), KEY_PORT);
        
        validateInput();
        updateComponentTreeSeverity();
    }
    
    
    private void validateInput() {
        ValidationResult result = new ValidationResult();
        
        if (ValidationUtils.isBlank(getApplicationNameTextField().getText())) {
            result.add(new SimpleValidationMessage(
                "An application name must be specified", Severity.ERROR, KEY_APPLICATION_NAME));
        }
        if (getLocalApiRadioButton().isSelected()) {
            String localClientDir = getLocalClientDirTextField().getText();
            if (ValidationUtils.isBlank(localClientDir)) {
                result.add(new SimpleValidationMessage(
                    "The local-client directory must be specified", Severity.ERROR, KEY_LOCAL_CLIENT_DIR));
            } else if (!configuration.isLocalClientDirValid()) {
                result.add(new SimpleValidationMessage(
                    "The specified local-client directory does not appear to be valid", Severity.ERROR, KEY_LOCAL_CLIENT_DIR));
            }
        } else {
            String remoteClientDir = getRemoteClientDirTextField().getText();
            if (ValidationUtils.isBlank(remoteClientDir)) {
                result.add(new SimpleValidationMessage(
                    "The remote-client directory must be specified", Severity.ERROR, KEY_REMOTE_CLIENT_DIR));
            } else if (!configuration.isRemoteClientDirValid()) {
                result.add(new SimpleValidationMessage(
                    "The specified remote-client directory does not appear to be valid", Severity.ERROR, KEY_REMOTE_CLIENT_DIR));
            }
            if (ValidationUtils.isBlank(getHostnameTextField().getText())) {
                result.add(new SimpleValidationMessage(
                    "The host name of the remote application service must be specified", Severity.ERROR, KEY_HOSTNAME));
            }
            if (ValidationUtils.isBlank(getPortTextField().getText())) {
                result.add(new SimpleValidationMessage(
                    "The port number of the remote application service must be specified", Severity.ERROR, KEY_PORT));
            } else {
                SimpleValidationMessage portError = new SimpleValidationMessage(
                    "The port number must be a valid integer between 0 and 65535", Severity.ERROR, KEY_PORT);
                try {
                    int port = Integer.valueOf(getPortTextField().getText()).intValue();
                    if (!(port >= 0 && port <= 65535)) {
                        result.add(portError);
                    }
                } catch (Exception ex) {
                    result.add(portError);
                }
            }
        }
        
        validationModel.setResult(result);
        
        updateComponentTreeSeverity();
        // update next button enabled
        setNextEnabled(!validationModel.hasErrors());
    }
    
    
    private void updateComponentTreeSeverity() {
        ValidationComponentUtils.updateComponentTreeMandatoryAndBlankBackground(this);
        ValidationComponentUtils.updateComponentTreeSeverityBackground(this, validationModel.getResult());
    }
    
    
    private void initLocalRemoteGroup() {
        NotifyingButtonGroup group = new NotifyingButtonGroup();
        group.addGroupSelectionListener(new GroupSelectionListener() {
            public void selectionChanged(ButtonModel previousSelection, ButtonModel currentSelection) {
                configuration.setLocalApi(getLocalApiRadioButton().isSelected());
                setLocalRemoteComponentsEnabled();
                validateInput();
            }
        });
        group.add(getLocalApiRadioButton());
        group.add(getRemoteApiRadioButton());
        group.setSelected(getLocalApiRadioButton().getModel(), true);
    }
    
    
    private void setLocalRemoteComponentsEnabled() {
        boolean isLocal = getLocalApiRadioButton().isSelected();
        getLocalClientDirLabel().setEnabled(isLocal);
        getLocalClientDirTextField().setEnabled(isLocal);
        getLocalClientDirBrowseButton().setEnabled(isLocal);
        
        getRemoteClientDirLabel().setEnabled(!isLocal);
        getRemoteClientDirTextField().setEnabled(!isLocal);
        getRemoteClientDirBrowseButton().setEnabled(!isLocal);
        getHostnameLabel().setEnabled(!isLocal);
        getHostnameTextField().setEnabled(!isLocal);
        getPortLabel().setEnabled(!isLocal);
        getPortTextField().setEnabled(!isLocal);
        getUseHttpsCheckBox().setEnabled(!isLocal);
    }
    
    
    private IconFeedbackPanel getValidationPanel() {
        if (validationPanel == null) {
            validationPanel = new IconFeedbackPanel(validationModel, getMainPanel());
        }
        return validationPanel;
    }
    
    
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.gridy = 2;
            GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
            gridBagConstraints17.gridx = 0;
            gridBagConstraints17.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints17.weightx = 1.0D;
            gridBagConstraints17.gridy = 1;
            GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
            gridBagConstraints16.gridx = 0;
            gridBagConstraints16.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints16.weightx = 1.0D;
            gridBagConstraints16.gridy = 0;
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.setSize(new Dimension(450, 309));
            mainPanel.add(getApplicationNamePanel(), gridBagConstraints16);
            mainPanel.add(getLocalApiPanel(), gridBagConstraints17);
            mainPanel.add(getRemoteApiPanel(), gridBagConstraints5);
        }
        return mainPanel;
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
            applicationNameTextField.getDocument().addDocumentListener(textFieldChangeListener);
            applicationNameTextField.getDocument().addDocumentListener(new DocumentChangeAdapter() {
                public void documentEdited(DocumentEvent e) {
                    configuration.setApplicationName(getApplicationNameTextField().getText());                    
                }
            });
        }
        return applicationNameTextField;
    }


    /**
     * This method initializes remoteClientLibDirLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getRemoteClientDirLabel() {
        if (remoteClientDirLabel == null) {
            remoteClientDirLabel = new JLabel();
            remoteClientDirLabel.setText("Remote Client Dir:");
        }
        return remoteClientDirLabel;
    }


    /**
     * This method initializes remoteClientDirTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getRemoteClientDirTextField() {
        if (remoteClientDirTextField == null) {
            remoteClientDirTextField = new JTextField();
            remoteClientDirTextField.setEditable(false);
            remoteClientDirTextField.getDocument().addDocumentListener(textFieldChangeListener);
            remoteClientDirTextField.getDocument().addDocumentListener(new DocumentChangeAdapter() {
                public void documentEdited(DocumentEvent e) {
                    configuration.setRemoteClientDir(getRemoteClientDirTextField().getText());                    
                }
            });
        }
        return remoteClientDirTextField;
    }


    /**
     * This method initializes remoteClientDirBrowseButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getRemoteClientDirBrowseButton() {
        if (remoteClientDirBrowseButton == null) {
            remoteClientDirBrowseButton = new JButton();
            remoteClientDirBrowseButton.setText("Browse");
            remoteClientDirBrowseButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    String dir = null;
                    try {
                        dir = ResourceManager.promptDir(ProjectSelectionPanel.this, configuration.getRemoteClientDir());
                    } catch (Exception ex) {
                        CompositeErrorDialog.showErrorDialog("Error selecting directory: " + ex.getMessage(), ex);
                    }
                    getRemoteClientDirTextField().setText(dir);
                    validateInput();
                }
            });
        }
        return remoteClientDirBrowseButton;
    }


    /**
     * This method initializes localClientDirLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getLocalClientDirLabel() {
        if (localClientDirLabel == null) {
            localClientDirLabel = new JLabel();
            localClientDirLabel.setText("Local Client Dir:");
        }
        return localClientDirLabel;
    }


    /**
     * This method initializes localClientDirTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getLocalClientDirTextField() {
        if (localClientDirTextField == null) {
            localClientDirTextField = new JTextField();
            localClientDirTextField.setEditable(false);
            localClientDirTextField.getDocument().addDocumentListener(textFieldChangeListener);
            localClientDirTextField.getDocument().addDocumentListener(new DocumentChangeAdapter() {
                public void documentEdited(DocumentEvent e) {
                    configuration.setLocalClientDir(getLocalClientDirTextField().getText());
                }
            });
        }
        return localClientDirTextField;
    }


    /**
     * This method initializes localClientDirBrowseButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getLocalClientDirBrowseButton() {
        if (localClientDirBrowseButton == null) {
            localClientDirBrowseButton = new JButton();
            localClientDirBrowseButton.setText("Browse");
            localClientDirBrowseButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    String dir = null;
                    try {
                        dir = ResourceManager.promptDir(ProjectSelectionPanel.this, configuration.getLocalClientDir());
                    } catch (Exception ex) {
                        CompositeErrorDialog.showErrorDialog("Error selecting directory: " + ex.getMessage(), ex);
                    }
                    getLocalClientDirTextField().setText(dir);
                    validateInput();
                }
            });
        }
        return localClientDirBrowseButton;
    }


    /**
     * This method initializes localApiRadioButton	
     * 	
     * @return javax.swing.JRadioButton	
     */
    private JRadioButton getLocalApiRadioButton() {
        if (localApiRadioButton == null) {
            localApiRadioButton = new JRadioButton();
            localApiRadioButton.setText("Local API");
        }
        return localApiRadioButton;
    }


    /**
     * This method initializes remoteApiRadioButton	
     * 	
     * @return javax.swing.JRadioButton	
     */
    private JRadioButton getRemoteApiRadioButton() {
        if (remoteApiRadioButton == null) {
            remoteApiRadioButton = new JRadioButton();
            remoteApiRadioButton.setText("Remote API");
        }
        return remoteApiRadioButton;
    }


    /**
     * This method initializes localApiPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getLocalApiPanel() {
        if (localApiPanel == null) {
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.gridx = 2;
            gridBagConstraints9.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints9.gridy = 1;
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints8.gridy = 1;
            gridBagConstraints8.weightx = 1.0;
            gridBagConstraints8.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints8.gridx = 1;
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.gridx = 0;
            gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints7.gridy = 1;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.gridx = 0;
            gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints6.gridwidth = 3;
            gridBagConstraints6.gridy = 0;
            localApiPanel = new JPanel();
            localApiPanel.setLayout(new GridBagLayout());
            localApiPanel.setBorder(BorderFactory.createTitledBorder(
                null, "Local API", TitledBorder.DEFAULT_JUSTIFICATION, 
                TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
            localApiPanel.add(getLocalApiRadioButton(), gridBagConstraints6);
            localApiPanel.add(getLocalClientDirLabel(), gridBagConstraints7);
            localApiPanel.add(getLocalClientDirTextField(), gridBagConstraints8);
            localApiPanel.add(getLocalClientDirBrowseButton(), gridBagConstraints9);
        }
        return localApiPanel;
    }


    /**
     * This method initializes remoteApiPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getRemoteApiPanel() {
        if (remoteApiPanel == null) {
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.gridwidth = 3;
            gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints4.gridy = 4;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridy = 3;
            gridBagConstraints3.weightx = 1.0;
            gridBagConstraints3.gridwidth = 2;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.gridx = 1;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.gridy = 3;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.gridy = 2;
            gridBagConstraints1.weightx = 1.0;
            gridBagConstraints1.gridwidth = 2;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.gridx = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.gridy = 2;
            GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
            gridBagConstraints13.gridx = 2;
            gridBagConstraints13.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints13.gridy = 1;
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints12.gridy = 1;
            gridBagConstraints12.weightx = 1.0;
            gridBagConstraints12.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints12.gridx = 1;
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridx = 0;
            gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints11.gridy = 1;
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.gridx = 0;
            gridBagConstraints10.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints10.gridwidth = 3;
            gridBagConstraints10.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints10.gridy = 0;
            remoteApiPanel = new JPanel();
            remoteApiPanel.setLayout(new GridBagLayout());
            remoteApiPanel.setBorder(BorderFactory.createTitledBorder(
                null, "Remote API", TitledBorder.DEFAULT_JUSTIFICATION, 
                TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
            remoteApiPanel.add(getRemoteApiRadioButton(), gridBagConstraints10);
            remoteApiPanel.add(getRemoteClientDirLabel(), gridBagConstraints11);
            remoteApiPanel.add(getRemoteClientDirTextField(), gridBagConstraints12);
            remoteApiPanel.add(getRemoteClientDirBrowseButton(), gridBagConstraints13);
            remoteApiPanel.add(getHostnameLabel(), gridBagConstraints);
            remoteApiPanel.add(getHostnameTextField(), gridBagConstraints1);
            remoteApiPanel.add(getPortLabel(), gridBagConstraints2);
            remoteApiPanel.add(getPortTextField(), gridBagConstraints3);
            remoteApiPanel.add(getUseHttpsCheckBox(), gridBagConstraints4);
        }
        return remoteApiPanel;
    }


    /**
     * This method initializes applicationNamePanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getApplicationNamePanel() {
        if (applicationNamePanel == null) {
            GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
            gridBagConstraints15.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints15.gridy = 0;
            gridBagConstraints15.weightx = 1.0;
            gridBagConstraints15.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints15.gridx = 1;
            GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
            gridBagConstraints14.gridx = 0;
            gridBagConstraints14.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints14.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints14.gridy = 0;
            applicationNamePanel = new JPanel();
            applicationNamePanel.setLayout(new GridBagLayout());
            applicationNamePanel.add(getApplicationNameLabel(), gridBagConstraints14);
            applicationNamePanel.add(getApplicationNameTextField(), gridBagConstraints15);
        }
        return applicationNamePanel;
    }


    /**
     * This method initializes hostnameLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getHostnameLabel() {
        if (hostnameLabel == null) {
            hostnameLabel = new JLabel();
            hostnameLabel.setText("Application Host Name:");
        }
        return hostnameLabel;
    }


    /**
     * This method initializes hostnameTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getHostnameTextField() {
        if (hostnameTextField == null) {
            hostnameTextField = new JTextField();
            hostnameTextField.getDocument().addDocumentListener(textFieldChangeListener);
            hostnameTextField.getDocument().addDocumentListener(new DocumentChangeAdapter() {
                public void documentEdited(DocumentEvent e) {
                    configuration.setApplicationHostname(getHostnameTextField().getText());
                }
            });
        }
        return hostnameTextField;
    }


    /**
     * This method initializes portlLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getPortLabel() {
        if (portLabel == null) {
            portLabel = new JLabel();
            portLabel.setText("Application Port Number:");
        }
        return portLabel;
    }


    /**
     * This method initializes portTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getPortTextField() {
        if (portTextField == null) {
            portTextField = new JTextField();
            portTextField.getDocument().addDocumentListener(textFieldChangeListener);
            portTextField.getDocument().addDocumentListener(new DocumentChangeAdapter() {
                public void documentEdited(DocumentEvent e) {
                    Integer port = null;
                    try {
                        port = Integer.valueOf(getPortTextField().getText());
                    } catch (Exception ex) {
                        // silent, since users could enter random stuff here, but validation will catch it
                    }
                    configuration.setApplicationPort(port);
                }
            });
        }
        return portTextField;
    }


    /**
     * This method initializes useHttpsCheckBox	
     * 	
     * @return javax.swing.JCheckBox	
     */
    private JCheckBox getUseHttpsCheckBox() {
        if (useHttpsCheckBox == null) {
            useHttpsCheckBox = new JCheckBox();
            useHttpsCheckBox.setText("Use HTTPS For Connections");
            useHttpsCheckBox.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    configuration.setUseHttps(getUseHttpsCheckBox().isSelected());
                }
            });
        }
        return useHttpsCheckBox;
    }
}
