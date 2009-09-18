package org.cagrid.data.sdkquery41.style.wizard;

import gov.nih.nci.cagrid.common.portal.DocumentChangeAdapter;
import gov.nih.nci.cagrid.common.portal.validation.IconFeedbackPanel;
import gov.nih.nci.cagrid.data.ui.wizard.AbstractWizardPanel;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

import org.cagrid.data.sdkquery41.style.wizard.config.LoginConfigurationStep;
import org.cagrid.grape.utils.CompositeErrorDialog;

import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.util.ValidationUtils;
import com.jgoodies.validation.view.ValidationComponentUtils;

/**
 * LoginConfigurationPanel
 * Wizard panel which allows the service developer to specify
 * an optional login to use with the caCORE SDK Application Service
 * 
 * @author David
 */
public class LoginConfigurationPanel extends AbstractWizardPanel {
    
    public static final String KEY_USERNAME = "Username";
    public static final String KEY_PASSWORD = "Password";
    public static final String KEY_REPEAT_PASSWORD = "Repeat password";
    
    private ValidationResultModel validationModel = null;
    private IconFeedbackPanel validationOverlayPanel = null;
    
    private JPanel mainPanel = null;
    private JCheckBox useLoginCheckBox = null;
    private JLabel usernameLabel = null;
    private JLabel passwordLabel = null;
    private JLabel repeatPasswordLabel = null;
    private JTextField usernameTextField = null;
    private JPasswordField mainPasswordField = null;
    private JPasswordField repeatPasswordField = null;
    
    private LoginConfigurationStep configuration = null;
    private DocumentChangeAdapter documentValidationListener = null;
    
    public LoginConfigurationPanel(ServiceExtensionDescriptionType extensionDescription, ServiceInformation info) {
        super(extensionDescription, info);
        validationModel = new DefaultValidationResultModel();
        configuration = new LoginConfigurationStep(info);
        documentValidationListener = new DocumentChangeAdapter() {
            public void documentEdited(DocumentEvent e) {
                validateInput();
            }
        };
        initialize();
    }


    public String getPanelShortName() {
        return "Login";
    }


    public String getPanelTitle() {
        return "Optional Application Service login";
    }


    public void update() {
        boolean useLogin = configuration.getUseLogin() != null 
            && configuration.getUseLogin().booleanValue();
        getUseLoginCheckBox().setSelected(useLogin);
        getUsernameTextField().setText(
            configuration.getUsername() != null ? configuration.getUsername() : "");
        getUsernameTextField().setEnabled(useLogin);
        getMainPasswordField().setText(
            configuration.getPassword() != null ? configuration.getPassword() : "");
        getMainPasswordField().setEnabled(useLogin);
        getRepeatPasswordField().setText(
            configuration.getPassword() != null ? configuration.getPassword() : "");
        getRepeatPasswordField().setEnabled(useLogin);
    }
    
    
    public void movingNext() {
        try {
            configuration.applyConfiguration();
        } catch (Exception ex) {
            ex.printStackTrace();
            CompositeErrorDialog.showErrorDialog("Error applying login configuration", ex.getMessage(), ex);
        }
    }
    
    
    private void initialize() {
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
    
    
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints6.gridy = 3;
            gridBagConstraints6.weightx = 1.0;
            gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints6.gridx = 1;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.gridy = 2;
            gridBagConstraints5.weightx = 1.0;
            gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints5.gridx = 1;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.gridy = 1;
            gridBagConstraints4.weightx = 1.0;
            gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints4.gridx = 1;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.gridy = 3;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.gridy = 2;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.gridy = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.gridy = 0;
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.add(getUseLoginCheckBox(), gridBagConstraints);
            mainPanel.add(getUsernameLabel(), gridBagConstraints1);
            mainPanel.add(getPasswordLabel(), gridBagConstraints2);
            mainPanel.add(getRepeatPasswordLabel(), gridBagConstraints3);
            mainPanel.add(getUsernameTextField(), gridBagConstraints4);
            mainPanel.add(getMainPasswordField(), gridBagConstraints5);
            mainPanel.add(getRepeatPasswordField(), gridBagConstraints6);
        }
        return mainPanel;
    }


    /**
     * This method initializes useLoginCheckBox	
     * 	
     * @return javax.swing.JCheckBox	
     */
    private JCheckBox getUseLoginCheckBox() {
        if (useLoginCheckBox == null) {
            useLoginCheckBox = new JCheckBox();
            useLoginCheckBox.setText("Use Login");
            useLoginCheckBox.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    boolean useLogin = getUseLoginCheckBox().isSelected();
                    configuration.setUseLogin(Boolean.valueOf(useLogin));
                    getUsernameTextField().setEnabled(useLogin);
                    getMainPasswordField().setEnabled(useLogin);
                    getRepeatPasswordField().setEnabled(useLogin);
                    validateInput();
                }
            });
        }
        return useLoginCheckBox;
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
     * This method initializes repeatPasswordLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getRepeatPasswordLabel() {
        if (repeatPasswordLabel == null) {
            repeatPasswordLabel = new JLabel();
            repeatPasswordLabel.setText("Repeat Password:");
        }
        return repeatPasswordLabel;
    }


    /**
     * This method initializes usernameTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getUsernameTextField() {
        if (usernameTextField == null) {
            usernameTextField = new JTextField();
            usernameTextField.setToolTipText(
                "Enter the username to use for logging in to the caCORE Application Service");
            usernameTextField.getDocument().addDocumentListener(documentValidationListener);
            usernameTextField.getDocument().addDocumentListener(new DocumentChangeAdapter() {
                public void documentEdited(DocumentEvent e) {
                    configuration.setUsername(getUsernameTextField().getText());
                }
            });
        }
        return usernameTextField;
    }


    /**
     * This method initializes mainPasswordField	
     * 	
     * @return javax.swing.JPasswordField	
     */
    private JPasswordField getMainPasswordField() {
        if (mainPasswordField == null) {
            mainPasswordField = new JPasswordField();
            mainPasswordField.setToolTipText(
                "Enter the password to use for logging in to the caCORE Application Service");
            mainPasswordField.getDocument().addDocumentListener(documentValidationListener);
            mainPasswordField.getDocument().addDocumentListener(new DocumentChangeAdapter() {
                public void documentEdited(DocumentEvent e) {
                    configuration.setPassword(new String(getMainPasswordField().getPassword()));
                }
            });
        }
        return mainPasswordField;
    }


    /**
     * This method initializes repeatPasswordField	
     * 	
     * @return javax.swing.JPasswordField	
     */
    private JPasswordField getRepeatPasswordField() {
        if (repeatPasswordField == null) {
            repeatPasswordField = new JPasswordField();
            repeatPasswordField.setToolTipText(
                "Repeat the password to use for logging in to the caCORE Application Service");
            repeatPasswordField.getDocument().addDocumentListener(documentValidationListener);
        }
        return repeatPasswordField;
    }
    
    
    // ----------
    // validation
    // ----------
    
    
    private void configureValidation() {
        ValidationComponentUtils.setMessageKey(getUsernameTextField(), KEY_USERNAME);
        ValidationComponentUtils.setMessageKey(getMainPasswordField(), KEY_PASSWORD);
        ValidationComponentUtils.setMessageKey(getRepeatPasswordField(), KEY_REPEAT_PASSWORD);
        
        validateInput();
        updateComponentTreeSeverity();
    }
    
    
    private void validateInput() {
        ValidationResult result = new ValidationResult();
        
        // only have work to do if the developer wants login
        if (getUseLoginCheckBox().isSelected()) {
            if (ValidationUtils.isBlank(getUsernameTextField().getText())) {
                result.add(new SimpleValidationMessage(
                    KEY_USERNAME + " cannot be blank", Severity.ERROR, KEY_USERNAME));
            }
            String mainPassword = new String(getMainPasswordField().getPassword());
            String repeatPassword = new String(getRepeatPasswordField().getPassword());
            if (ValidationUtils.isBlank(mainPassword)) {
                result.add(new SimpleValidationMessage(
                    KEY_PASSWORD + " should not be blank, but is allowed", Severity.WARNING, KEY_PASSWORD));
            }
            if (ValidationUtils.isBlank(repeatPassword)) {
                result.add(new SimpleValidationMessage(
                    KEY_REPEAT_PASSWORD + " should not be blank, but is allowed", Severity.WARNING, KEY_REPEAT_PASSWORD));
            }
            if (!ValidationUtils.equals(mainPassword, repeatPassword)) {
                result.add(new SimpleValidationMessage(
                    KEY_PASSWORD + " and " + KEY_REPEAT_PASSWORD + " do not match!", Severity.ERROR, KEY_PASSWORD));
                result.add(new SimpleValidationMessage(
                    KEY_REPEAT_PASSWORD + " and " + KEY_PASSWORD + " do not match!", Severity.ERROR, KEY_REPEAT_PASSWORD));
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
