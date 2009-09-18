package org.cagrid.data.sdkquery41.style.wizard;

import gov.nih.nci.cagrid.common.portal.DocumentChangeAdapter;
import gov.nih.nci.cagrid.common.portal.validation.IconFeedbackPanel;
import gov.nih.nci.cagrid.data.ui.GroupSelectionListener;
import gov.nih.nci.cagrid.data.ui.NotifyingButtonGroup;
import gov.nih.nci.cagrid.data.ui.wizard.AbstractWizardPanel;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;

import org.cagrid.data.sdkquery41.style.wizard.config.APITypeConfigurationStep;
import org.cagrid.data.sdkquery41.style.wizard.config.APITypeConfigurationStep.ApiType;
import org.cagrid.grape.utils.CompositeErrorDialog;

import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.util.ValidationUtils;
import com.jgoodies.validation.view.ValidationComponentUtils;

/**
 * APITypePanel
 * Wizard panel which allows the service developer to select the 
 * API type (local or remote) which will be used to connect to
 * the caCORE SDK system
 * 
 * @author David
 */
public class APITypePanel extends AbstractWizardPanel {
    
    private static final int MIN_PORT_NUMBER = 0;
    private static final int MAX_PORT_NUMBER = 65535;
    
    // keys for validation
    public static final String KEY_HOSTNAME = "Hostname";
    public static final String KEY_PORT = "Port number";

    private ValidationResultModel validationModel = null;
    private IconFeedbackPanel validationOverlayPanel = null;
    
    private JPanel mainPanel = null;
    private JRadioButton localApiRadioButton = null;
    private JRadioButton remoteApiRadioButton = null;
    private JLabel hostnameLabel = null;
    private JLabel portLabel = null;
    private JCheckBox useHttpsCheckBox = null;
    private JTextField hostnameTextField = null;
    private JTextField portNumberTextField = null;
    private JPanel apiTypePanel = null;
    private JPanel remoteInfoPanel = null;
    
    private APITypeConfigurationStep configuration = null;
    
    public APITypePanel(ServiceExtensionDescriptionType extensionDescription, ServiceInformation info) {
        super(extensionDescription, info);
        this.validationModel = new DefaultValidationResultModel();
        this.configuration = new APITypeConfigurationStep(info);
        initialize();
    }


    public String getPanelShortName() {
        return "API Type";
    }


    public String getPanelTitle() {
        return "Local or Remote API selection";
    }


    public void update() {
        // load values from the config to the GUI
        ApiType apiType = configuration.getApiType();
        boolean remoteApi = ApiType.LOCAL_API.equals(apiType);
        getRemoteApiRadioButton().setSelected(remoteApi);
        getLocalApiRadioButton().setSelected(!remoteApi);
        String hostname = configuration.getHostname();
        getHostnameTextField().setText(hostname != null ? hostname : "");
        getHostnameTextField().setEnabled(remoteApi);
        Integer port = configuration.getPortNumber();
        getPortNumberTextField().setText(port != null ? port.toString() : "");
        getPortNumberTextField().setEnabled(remoteApi);
        Boolean useHttps = configuration.getUseHttps();
        getUseHttpsCheckBox().setSelected(useHttps != null && useHttps.booleanValue());
        getUseHttpsCheckBox().setEnabled(remoteApi);
        validateInput();
    }
    
    
    public void movingNext() {
        try {
            configuration.applyConfiguration();
        } catch (Exception ex) {
            ex.printStackTrace();
            CompositeErrorDialog.showErrorDialog("Error applying API type configuration", ex.getMessage(), ex);
        }
    }
    
    
    private void initialize() {
        initRadioButtonGroup();
        configureValidation();
        setLayout(new GridLayout());
        add(getValidationOverlayPanel());
    }
    
    
    private IconFeedbackPanel getValidationOverlayPanel() {
        if (validationOverlayPanel == null) {
            validationOverlayPanel = new IconFeedbackPanel(validationModel, getMainPanel());
        }
        return validationOverlayPanel;
    }
    
    
    private void initRadioButtonGroup() {
        NotifyingButtonGroup apiTypeGroup = new NotifyingButtonGroup();
        apiTypeGroup.addGroupSelectionListener(new GroupSelectionListener() {
            public void selectionChanged(final ButtonModel previousSelection, final ButtonModel currentSelection) {
                ApiType api = currentSelection == getLocalApiRadioButton().getModel() 
                    ? ApiType.LOCAL_API : ApiType.REMOTE_API;
                configuration.setApiType(api);
                boolean remote = ApiType.REMOTE_API == api;
                getHostnameTextField().setEnabled(remote);
                getHostnameTextField().setEditable(remote);
                getPortNumberTextField().setEnabled(remote);
                getPortNumberTextField().setEditable(remote);
                getUseHttpsCheckBox().setEnabled(remote);
                validateInput();
            }
        });
        apiTypeGroup.add(getLocalApiRadioButton());
        apiTypeGroup.add(getRemoteApiRadioButton());
        apiTypeGroup.setSelected(getRemoteApiRadioButton().getModel(), true);
    }
    
    
    /**
     * This method initializes mainPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.gridx = 0;
            gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints6.weightx = 1.0D;
            gridBagConstraints6.gridy = 1;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints5.weightx = 1.0D;
            gridBagConstraints5.gridy = 0;
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.add(getApiTypePanel(), gridBagConstraints5);
            mainPanel.add(getRemoteInfoPanel(), gridBagConstraints6);
        }
        return mainPanel;
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
            localApiRadioButton.setToolTipText(
                "Use the local API of the Application Service");
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
            remoteApiRadioButton.setToolTipText(
                "Use the remote HTTP API of the Application Service");
        }
        return remoteApiRadioButton;
    }


    /**
     * This method initializes hostnameLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getHostnameLabel() {
        if (hostnameLabel == null) {
            hostnameLabel = new JLabel();
            hostnameLabel.setText("Hostname:");
        }
        return hostnameLabel;
    }


    /**
     * This method initializes portLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getPortLabel() {
        if (portLabel == null) {
            portLabel = new JLabel();
            portLabel.setText("Port Number:");
        }
        return portLabel;
    }


    /**
     * This method initializes useHttpsCheckBox	
     * 	
     * @return javax.swing.JCheckBox	
     */
    private JCheckBox getUseHttpsCheckBox() {
        if (useHttpsCheckBox == null) {
            useHttpsCheckBox = new JCheckBox();
            useHttpsCheckBox.setText("Use HTTPS");
            useHttpsCheckBox.setToolTipText(
                "Check this box to use the HTTPS protocol when communicating " +
                "with the caCORE SDK service.");
            useHttpsCheckBox.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    configuration.setUseHttps(Boolean.valueOf(getUseHttpsCheckBox().isSelected()));
                }
            });
        }
        return useHttpsCheckBox;
    }


    /**
     * This method initializes hostnameTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getHostnameTextField() {
        if (hostnameTextField == null) {
            hostnameTextField = new JTextField();
            hostnameTextField.setToolTipText("Enter the host name of the " +
                    "caCORE SDK application service (eg. example.com)");
            hostnameTextField.getDocument().addDocumentListener(new DocumentChangeAdapter() {
                public void documentEdited(DocumentEvent e) {
                    validateInput();
                    configuration.setHostname(getHostnameTextField().getText());
                }
            });
        }
        return hostnameTextField;
    }


    /**
     * This method initializes portNumberTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getPortNumberTextField() {
        if (portNumberTextField == null) {
            portNumberTextField = new JTextField();
            portNumberTextField.setToolTipText("Enter the port number on which the " +
                    "caCORE SDK application service listens for connections");
            portNumberTextField.getDocument().addDocumentListener(new DocumentChangeAdapter() {
                public void documentEdited(DocumentEvent e) {
                    validateInput();
                    try {
                        Integer port = Integer.valueOf(getPortNumberTextField().getText());
                        configuration.setPortNumber(port);
                    } catch (Exception ex) {
                        // not an integer?!
                    }
                }
            });
        }
        return portNumberTextField;
    }


    /**
     * This method initializes apiTypePanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getApiTypePanel() {
        if (apiTypePanel == null) {
            GridLayout gridLayout = new GridLayout();
            gridLayout.setRows(1);
            gridLayout.setHgap(4);
            gridLayout.setColumns(2);
            apiTypePanel = new JPanel();
            apiTypePanel.setBorder(BorderFactory.createTitledBorder(
                null, "API Type", TitledBorder.DEFAULT_JUSTIFICATION, 
                TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
            apiTypePanel.setLayout(gridLayout);
            apiTypePanel.add(getLocalApiRadioButton(), null);
            apiTypePanel.add(getRemoteApiRadioButton(), null);
        }
        return apiTypePanel;
    }


    /**
     * This method initializes remoteInfoPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getRemoteInfoPanel() {
        if (remoteInfoPanel == null) {
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 1;
            gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints4.gridy = 2;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridy = 1;
            gridBagConstraints3.weightx = 1.0;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.gridx = 1;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.gridy = 1;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.weightx = 1.0;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.gridx = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.gridy = 0;
            remoteInfoPanel = new JPanel();
            remoteInfoPanel.setLayout(new GridBagLayout());
            remoteInfoPanel.setBorder(BorderFactory.createTitledBorder(
                null, "Remote Configuration", TitledBorder.DEFAULT_JUSTIFICATION, 
                TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
            remoteInfoPanel.add(getHostnameLabel(), gridBagConstraints);
            remoteInfoPanel.add(getHostnameTextField(), gridBagConstraints1);
            remoteInfoPanel.add(getPortLabel(), gridBagConstraints2);
            remoteInfoPanel.add(getPortNumberTextField(), gridBagConstraints3);
            remoteInfoPanel.add(getUseHttpsCheckBox(), gridBagConstraints4);
        }
        return remoteInfoPanel;
    }
    
    
    // ----------
    // validation
    // ----------
    
    
    private void configureValidation() {
        ValidationComponentUtils.setMessageKey(getHostnameTextField(), KEY_HOSTNAME);
        ValidationComponentUtils.setMessageKey(getPortNumberTextField(), KEY_PORT);
        
        validateInput();
        updateComponentTreeSeverity();
    }
    
    
    private void validateInput() {
        ValidationResult result = new ValidationResult();
        
        // only have work to do if using the remote API
        if (getRemoteApiRadioButton().isSelected()) {
            if (ValidationUtils.isBlank(getHostnameTextField().getText())) {
                result.add(new SimpleValidationMessage("Hostname cannot be blank!", Severity.ERROR, KEY_HOSTNAME));
            }
            if (ValidationUtils.isBlank(getPortNumberTextField().getText())) {
                result.add(new SimpleValidationMessage("Port number cannot be blank!", Severity.ERROR, KEY_PORT));
            } else {
                try {
                    int value = Integer.parseInt(getPortNumberTextField().getText());
                    if (value < MIN_PORT_NUMBER || value > MAX_PORT_NUMBER) {
                        result.add(new SimpleValidationMessage("Port number must be in the range [" 
                            + MIN_PORT_NUMBER + ", " + MAX_PORT_NUMBER + "]", Severity.ERROR, KEY_PORT));
                    }
                } catch (NumberFormatException ex) {
                    result.add(new SimpleValidationMessage("Port number must be a valid integer!", 
                        Severity.ERROR, KEY_PORT));
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
}
