package gov.nih.nci.cagrid.sdkquery4.style.wizard;

import gov.nih.nci.cagrid.common.portal.DocumentChangeAdapter;
import gov.nih.nci.cagrid.common.portal.PortalUtils;
import gov.nih.nci.cagrid.common.portal.validation.IconFeedbackPanel;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.ui.GroupSelectionListener;
import gov.nih.nci.cagrid.data.ui.NotifyingButtonGroup;
import gov.nih.nci.cagrid.data.ui.wizard.AbstractWizardPanel;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.sdkquery4.processor.SDK4QueryProcessor;
import gov.nih.nci.cagrid.sdkquery4.style.wizard.config.QueryProcessorSecurityConfigurationStep;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

import org.cagrid.grape.utils.CompositeErrorDialog;

import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.util.ValidationUtils;
import com.jgoodies.validation.view.ValidationComponentUtils;

/** 
 *  QueryProcessorSecurityPanel
 *  Panel to configure security options on the SDK 4 Query Processor
 * 
 * @author David Ervin
 * 
 * @created Dec 11, 2007 9:56:51 AM
 * @version $Id: QueryProcessorSecurityPanel.java,v 1.5 2009-01-29 20:14:18 dervin Exp $ 
 */
public class QueryProcessorSecurityPanel extends AbstractWizardPanel {
    // validation keys
    public static final String KEY_USERNAME_FIELD = "User Name";
    public static final String KEY_PASSWORD_FIELD = "Password";
    
    private JPanel mainPanel = null;
    private JCheckBox useSecurityCheckBox = null;
    private JRadioButton staticLoginRadioButton = null;
    private JRadioButton gridIdentityRadioButton = null;
    private JLabel usernameLabel = null;
    private JLabel passwordLabel = null;
    private JTextField usernameTextField = null;
    private JTextField passwordTextField = null;
    private JPanel securityOptionsPanel = null;

    private IconFeedbackPanel validationPanel = null;
    private ValidationResultModel validationModel = null;
    private DocumentChangeAdapter documentChangeListener = null;
    private NotifyingButtonGroup radioButtonGroup = null;
    private QueryProcessorSecurityConfigurationStep configuration;

    public QueryProcessorSecurityPanel(
        ServiceExtensionDescriptionType extensionDescription, ServiceInformation info) {
        super(extensionDescription, info);
        this.validationModel = new DefaultValidationResultModel();
        this.documentChangeListener = new DocumentChangeAdapter() {
            public void documentEdited(DocumentEvent e) {
                validateInput();
            }
        };
        this.configuration = new QueryProcessorSecurityConfigurationStep(info);
        getRadioButtonGroup();
        initialize();
    }


    public String getPanelShortName() {
        return "Security";
    }


    public String getPanelTitle() {
        return "Security Configuration Options";
    }


    public void update() {
        try {
            String useLoginValue = getConfigurationProperty(SDK4QueryProcessor.PROPERTY_USE_LOGIN);
            boolean useLogin = useLoginValue != null && Boolean.parseBoolean(useLoginValue);
            getUseSecurityCheckBox().setSelected(useLogin);
            String useGridIdentValue = getConfigurationProperty(SDK4QueryProcessor.PROPERTY_USE_GRID_IDENTITY_LOGIN);
            boolean useGridIdent = useGridIdentValue != null && Boolean.parseBoolean(useGridIdentValue);
            getGridIdentityRadioButton().setSelected(useGridIdent);
            String loginUsername = getConfigurationProperty(SDK4QueryProcessor.PROPERTY_STATIC_LOGIN_USERNAME);
            String loginPassword = getConfigurationProperty(SDK4QueryProcessor.PROPERTY_STATIC_LOGIN_PASSWORD);
            getUsernameTextField().setText(loginUsername);
            getPasswordTextField().setText(loginPassword);
            PortalUtils.setContainerEnabled(getSecurityOptionsPanel(), useLogin);
        } catch (Exception ex) {
            ex.printStackTrace();
            CompositeErrorDialog.showErrorDialog("Error loading configuration", ex.getMessage(), ex);
        }
    }
    
    
    public void movingNext() {
        try {
            configuration.applyConfiguration();
        } catch (Exception ex) {
            ex.printStackTrace();
            CompositeErrorDialog.showErrorDialog("Error storing security configuration", ex.getMessage(), ex);
        }
    }
    
    
    private void initialize() {
        this.setLayout(new GridLayout());
        this.add(getValidationPanel());
        // set up for validation
        configureValidation();
    }
    
    
    private NotifyingButtonGroup getRadioButtonGroup() {
        if (radioButtonGroup == null) {
            radioButtonGroup = new NotifyingButtonGroup();
            radioButtonGroup.addGroupSelectionListener(new GroupSelectionListener() {
                public void selectionChanged(ButtonModel previousSelection, ButtonModel currentSelection) {
                    boolean useGridIdent = currentSelection == getGridIdentityRadioButton().getModel();
                    configuration.setUseGridIdentLogin(useGridIdent);
                    validateInput();
                }
            });
            radioButtonGroup.add(getStaticLoginRadioButton());
            radioButtonGroup.add(getGridIdentityRadioButton());
            radioButtonGroup.setSelected(getStaticLoginRadioButton().getModel(), true);
            setLoginComponentsEnabled();
        }
        return radioButtonGroup;
    }
    
    
    private IconFeedbackPanel getValidationPanel() {
        if (validationPanel == null) {
            validationPanel = new IconFeedbackPanel(validationModel, getMainPanel());
        }
        return validationPanel;
    }
    
    
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridx = 0;
            gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints11.weightx = 1.0D;
            gridBagConstraints11.gridy = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.gridy = 0;
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.add(getUseSecurityCheckBox(), gridBagConstraints);
            mainPanel.add(getSecurityOptionsPanel(), gridBagConstraints11);
        }
        return mainPanel;
    }


    /**
     * This method initializes useSecurityCheckBox	
     * 	
     * @return javax.swing.JCheckBox	
     */
    private JCheckBox getUseSecurityCheckBox() {
        if (useSecurityCheckBox == null) {
            useSecurityCheckBox = new JCheckBox();
            useSecurityCheckBox.setText("Use Security");
            useSecurityCheckBox.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    PortalUtils.setContainerEnabled(getSecurityOptionsPanel(), useSecurityCheckBox.isSelected());
                    setLoginComponentsEnabled();
                    configuration.setUseGridIdentLogin(getUseSecurityCheckBox().isSelected());
                }
            });
        }
        return useSecurityCheckBox;
    }


    /**
     * This method initializes staticLoginRadioButton	
     * 	
     * @return javax.swing.JRadioButton	
     */
    private JRadioButton getStaticLoginRadioButton() {
        if (staticLoginRadioButton == null) {
            staticLoginRadioButton = new JRadioButton();
            staticLoginRadioButton.setText("Static Login");
        }
        return staticLoginRadioButton;
    }


    /**
     * This method initializes gridIdentityRadioButton	
     * 	
     * @return javax.swing.JRadioButton	
     */
    private JRadioButton getGridIdentityRadioButton() {
        if (gridIdentityRadioButton == null) {
            gridIdentityRadioButton = new JRadioButton();
            gridIdentityRadioButton.setText("Grid Identity");
        }
        return gridIdentityRadioButton;
    }


    /**
     * This method initializes usernameLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getUsernameLabel() {
        if (usernameLabel == null) {
            usernameLabel = new JLabel();
            usernameLabel.setText("Username:");
        }
        return usernameLabel;
    }


    /**
     * This method initializes passwordLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getPasswordLabel() {
        if (passwordLabel == null) {
            passwordLabel = new JLabel();
            passwordLabel.setText("Password:");
        }
        return passwordLabel;
    }


    /**
     * This method initializes usernameTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getUsernameTextField() {
        if (usernameTextField == null) {
            usernameTextField = new JTextField();
            usernameTextField.getDocument().addDocumentListener(documentChangeListener);
            usernameTextField.getDocument().addDocumentListener(new DocumentChangeAdapter() {
                public void documentEdited(DocumentEvent e) {
                    configuration.setStaticLoginUsername(getUsernameTextField().getText());
                }
            });
        }
        return usernameTextField;
    }


    /**
     * This method initializes passwordTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getPasswordTextField() {
        if (passwordTextField == null) {
            passwordTextField = new JTextField();
            passwordTextField.getDocument().addDocumentListener(documentChangeListener);
            passwordTextField.getDocument().addDocumentListener(new DocumentChangeAdapter() {
                public void documentEdited(DocumentEvent e) {
                    configuration.setStaticLoginPassword(getPasswordTextField().getText());
                }
            });
        }
        return passwordTextField;
    }


    /**
     * This method initializes securityOptionsPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getSecurityOptionsPanel() {
        if (securityOptionsPanel == null) {
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints6.gridy = 4;
            gridBagConstraints6.weightx = 1.0;
            gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints6.gridx = 1;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.gridy = 3;
            gridBagConstraints5.weightx = 1.0;
            gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints5.gridx = 1;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.anchor = GridBagConstraints.WEST;
            gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints4.gridy = 4;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.anchor = GridBagConstraints.WEST;
            gridBagConstraints3.gridy = 3;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.anchor = GridBagConstraints.WEST;
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.gridwidth = 2;
            gridBagConstraints2.gridy = 2;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.anchor = GridBagConstraints.WEST;
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.gridwidth = 2;
            gridBagConstraints1.gridy = 1;
            securityOptionsPanel = new JPanel();
            securityOptionsPanel.setLayout(new GridBagLayout());
            securityOptionsPanel.add(getStaticLoginRadioButton(), gridBagConstraints1);
            securityOptionsPanel.add(getGridIdentityRadioButton(), gridBagConstraints2);
            securityOptionsPanel.add(getUsernameLabel(), gridBagConstraints3);
            securityOptionsPanel.add(getPasswordLabel(), gridBagConstraints4);
            securityOptionsPanel.add(getUsernameTextField(), gridBagConstraints5);
            securityOptionsPanel.add(getPasswordTextField(), gridBagConstraints6);
        }
        return securityOptionsPanel;
    }
    
    
    // -----------
    // validation
    // -----------
    
    
    private void configureValidation() {
        ValidationComponentUtils.setMessageKey(getUsernameTextField(), KEY_USERNAME_FIELD);
        ValidationComponentUtils.setMessageKey(getPasswordTextField(), KEY_PASSWORD_FIELD);
        
        validateInput();
        updateComponentTreeSeverity();
    }
    
    
    private void validateInput() {
        ValidationResult result = new ValidationResult();
        
        if (getUseSecurityCheckBox().isSelected() && getStaticLoginRadioButton().isSelected()) {
            if (ValidationUtils.isBlank(getUsernameTextField().getText())) {
                result.add(new SimpleValidationMessage(
                    KEY_USERNAME_FIELD + " must not be blank", Severity.ERROR, KEY_USERNAME_FIELD));
            }
            if (ValidationUtils.isBlank(getPasswordTextField().getText())) {
                result.add(new SimpleValidationMessage(
                    KEY_PASSWORD_FIELD + " must not be blank", Severity.ERROR, KEY_PASSWORD_FIELD));
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
    
    
    // ------------
    // helpers
    // ------------
    
    
    private String getConfigurationProperty(String rawKey) throws Exception {
        ServiceDescription desc = getServiceInformation().getServiceDescriptor();
        String paddedKey = DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + rawKey;
        if (CommonTools.servicePropertyExists(desc, paddedKey)) {
            return CommonTools.getServicePropertyValue(desc, paddedKey);
        }
        return null;
    }
    
    
    private void setLoginComponentsEnabled() {
        boolean enable = getUseSecurityCheckBox().isSelected() && getStaticLoginRadioButton().isSelected();
        getUsernameLabel().setEnabled(enable);
        getUsernameTextField().setEnabled(enable);
        getPasswordLabel().setEnabled(enable);
        getPasswordTextField().setEnabled(enable);
        if (!enable) {
            getUsernameTextField().setText("");
            getPasswordTextField().setText("");
        }
    }
}
