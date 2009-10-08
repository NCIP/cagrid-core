package org.cagrid.data.sdkquery42.style.wizard;

import gov.nih.nci.cagrid.common.portal.DocumentChangeAdapter;
import gov.nih.nci.cagrid.common.portal.validation.IconFeedbackPanel;
import gov.nih.nci.cagrid.data.ui.wizard.AbstractWizardPanel;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.cagrid.data.sdkquery42.style.wizard.config.SecurityConfigurationStep;

import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.util.ValidationUtils;
import com.jgoodies.validation.view.ValidationComponentUtils;

public class SecurityConfigurationPanel extends AbstractWizardPanel {
    
    private static final String KEY_USERNAME = "Username";
    private static final String KEY_PASS = "Password";
    private static final String KEY_PASS2 = "Password Again";
    
    private SecurityConfigurationStep configuration = null;
    private ValidationResultModel validationModel = null;
    
    private DocumentListener textBoxListener = null;
    
    private IconFeedbackPanel validationPanel = null;
    private JPanel mainPanel = null;
    private JPanel localApiPanel = null;
    private JCheckBox gridIdentCheckBox = null;
    private JPanel staticLoginPanel = null;
    private JCheckBox useStaticLoginCheckBox = null;
    private JLabel usernameLabel = null;
    private JLabel passwordLabel = null;
    private JLabel password2Label = null;
    private JTextField usernameTextField = null;
    private JPasswordField passwordField = null;
    private JPasswordField password2Field = null;

    public SecurityConfigurationPanel(ServiceExtensionDescriptionType extensionDescription, ServiceInformation info) {
        super(extensionDescription, info);
        configuration = new SecurityConfigurationStep(info);
        validationModel = new DefaultValidationResultModel();
        textBoxListener = new DocumentChangeAdapter() {
            public void documentEdited(DocumentEvent e) {
                validateInput();
            }
        };
        initialize();
    }


    public String getPanelShortName() {
        return "Security";
    }


    public String getPanelTitle() {
        return "SDK Security Configuration";
    }


    public void update() {
        getGridIdentCheckBox().setSelected(configuration.isUseCsmGridIdent());
        getUseStaticLoginCheckBox().setSelected(configuration.isUseStaticLogin());
        getUsernameTextField().setText(configuration.getStaticLoginUser());
        getPasswordField().setText(configuration.getStaticLoginPass());
        getPassword2Field().setText(configuration.getStaticLoginPass());
        enableDisableComponents();
    }
    
    
    private void initialize() {
        setLayout(new GridLayout(1, 1));
        add(getValidationPanel());
        configureValidation();
    }
    
    
    private void enableDisableComponents() {
        getGridIdentCheckBox().setSelected(
            configuration.isUsingLocalApi() && configuration.isUseCsmGridIdent());
        boolean useGridIdent = getGridIdentCheckBox().isSelected(); 
        getUseStaticLoginCheckBox().setEnabled(!useGridIdent);
        getUsernameLabel().setEnabled(!useGridIdent);
        getUsernameTextField().setEnabled(!useGridIdent);
        getPasswordLabel().setEnabled(!useGridIdent);
        getPasswordField().setEnabled(!useGridIdent);
        getPassword2Label().setEnabled(!useGridIdent);
        getPassword2Field().setEnabled(!useGridIdent);
        boolean useStatic = getUseStaticLoginCheckBox().isSelected();
        getGridIdentCheckBox().setEnabled(configuration.isUsingLocalApi() && !useStatic);
    }
    
    
    private void configureValidation() {
        ValidationComponentUtils.setMessageKey(getUsernameTextField(), KEY_USERNAME);
        ValidationComponentUtils.setMessageKey(getPasswordField(), KEY_PASS);
        ValidationComponentUtils.setMessageKey(getPassword2Field(), KEY_PASS2);
        
        validateInput();
        updateComponentTreeSeverity();
    }
    
    
    private void validateInput() {
        ValidationResult result = new ValidationResult();
        
        if (getUseStaticLoginCheckBox().isSelected()) {
            if (ValidationUtils.isBlank(getUsernameTextField().getText())) {
                result.add(new SimpleValidationMessage(
                    "The username cannot be blank", Severity.ERROR, KEY_USERNAME));
            }
            if (ValidationUtils.isBlank(new String(getPasswordField().getPassword()))) {
                result.add(new SimpleValidationMessage(
                    "Password cannot be blank", Severity.ERROR, KEY_PASS));
            } else {
                char[] pass1 = getPasswordField().getPassword();
                char[] pass2 = getPassword2Field().getPassword();
                if (!Arrays.equals(pass1, pass2)) {
                    result.add(new SimpleValidationMessage("Passwords do not match", Severity.ERROR, KEY_PASS));
                    result.add(new SimpleValidationMessage("Passwords do not match", Severity.ERROR, KEY_PASS2));
                }
            }
            if (ValidationUtils.isBlank(new String(getPassword2Field().getPassword()))) {
                result.add(new SimpleValidationMessage(
                    "Password cannot be blank", Severity.ERROR, KEY_PASS2));
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
    
    
    private IconFeedbackPanel getValidationPanel() {
        if (validationPanel == null) {
            validationPanel = new IconFeedbackPanel(validationModel, getMainPanel());
        }
        return validationPanel;
    }
    
    
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.gridx = 0;
            gridBagConstraints9.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints9.weightx = 1.0D;
            gridBagConstraints9.gridy = 1;
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.gridx = 0;
            gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints8.weightx = 1.0D;
            gridBagConstraints8.gridy = 0;
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.setSize(new Dimension(395, 205));
            mainPanel.add(getLocalApiPanel(), gridBagConstraints8);
            mainPanel.add(getStaticLoginPanel(), gridBagConstraints9);
        }
        return mainPanel;
    }


    /**
     * This method initializes localApiPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getLocalApiPanel() {
        if (localApiPanel == null) {
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.weightx = 1.0D;
            gridBagConstraints.gridy = 0;
            localApiPanel = new JPanel();
            localApiPanel.setLayout(new GridBagLayout());
            localApiPanel.setBorder(BorderFactory.createTitledBorder(
                null, "Local API Only", TitledBorder.DEFAULT_JUSTIFICATION, 
                TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
            localApiPanel.add(getGridIdentCheckBox(), gridBagConstraints);
        }
        return localApiPanel;
    }


    /**
     * This method initializes gridIdentCheckBox	
     * 	
     * @return javax.swing.JCheckBox	
     */
    private JCheckBox getGridIdentCheckBox() {
        if (gridIdentCheckBox == null) {
            gridIdentCheckBox = new JCheckBox();
            gridIdentCheckBox.setText("Use Grid Identity with CSM");
            gridIdentCheckBox.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    validateInput();
                    configuration.setUseCsmGridIdent(getGridIdentCheckBox().isSelected());
                    enableDisableComponents();
                }
            });
        }
        return gridIdentCheckBox;
    }


    /**
     * This method initializes staticLoginPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getStaticLoginPanel() {
        if (staticLoginPanel == null) {
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints7.gridy = 3;
            gridBagConstraints7.weightx = 1.0;
            gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints7.gridx = 1;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints6.gridy = 2;
            gridBagConstraints6.weightx = 1.0;
            gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints6.gridx = 1;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.gridy = 1;
            gridBagConstraints5.weightx = 1.0;
            gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints5.gridx = 1;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints4.gridy = 3;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridy = 2;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.gridy = 1;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.gridwidth = 2;
            gridBagConstraints1.gridy = 0;
            staticLoginPanel = new JPanel();
            staticLoginPanel.setLayout(new GridBagLayout());
            staticLoginPanel.setBorder(BorderFactory.createTitledBorder(
                null, "Static Login", TitledBorder.DEFAULT_JUSTIFICATION, 
                TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
            staticLoginPanel.add(getUseStaticLoginCheckBox(), gridBagConstraints1);
            staticLoginPanel.add(getUsernameLabel(), gridBagConstraints2);
            staticLoginPanel.add(getPasswordLabel(), gridBagConstraints3);
            staticLoginPanel.add(getPassword2Label(), gridBagConstraints4);
            staticLoginPanel.add(getUsernameTextField(), gridBagConstraints5);
            staticLoginPanel.add(getPasswordField(), gridBagConstraints6);
            staticLoginPanel.add(getPassword2Field(), gridBagConstraints7);
        }
        return staticLoginPanel;
    }


    /**
     * This method initializes useStaticLoginCheckBox	
     * 	
     * @return javax.swing.JCheckBox	
     */
    private JCheckBox getUseStaticLoginCheckBox() {
        if (useStaticLoginCheckBox == null) {
            useStaticLoginCheckBox = new JCheckBox();
            useStaticLoginCheckBox.setText("Use Static Login");
            useStaticLoginCheckBox.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    validateInput();
                    configuration.setUseStaticLogin(getUseStaticLoginCheckBox().isSelected());
                    enableDisableComponents();
                }
            });
        }
        return useStaticLoginCheckBox;
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
     * This method initializes password2Label	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getPassword2Label() {
        if (password2Label == null) {
            password2Label = new JLabel();
            password2Label.setText("Password Again:");
        }
        return password2Label;
    }


    /**
     * This method initializes usernameTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getUsernameTextField() {
        if (usernameTextField == null) {
            usernameTextField = new JTextField();
            usernameTextField.getDocument().addDocumentListener(textBoxListener);
            usernameTextField.getDocument().addDocumentListener(new DocumentChangeAdapter() {
                public void documentEdited(DocumentEvent e) {
                    configuration.setStaticLoginUser(getUsernameTextField().getText());
                }
            });
        }
        return usernameTextField;
    }


    /**
     * This method initializes passwordField	
     * 	
     * @return javax.swing.JPasswordField	
     */
    private JPasswordField getPasswordField() {
        if (passwordField == null) {
            passwordField = new JPasswordField();
            passwordField.getDocument().addDocumentListener(textBoxListener);
            passwordField.getDocument().addDocumentListener(new DocumentChangeAdapter() {
                public void documentEdited(DocumentEvent e) {
                    configuration.setStaticLoginPass(new String(getPasswordField().getPassword()));   
                }
            });
        }
        return passwordField;
    }


    /**
     * This method initializes password2Field	
     * 	
     * @return javax.swing.JPasswordField	
     */
    private JPasswordField getPassword2Field() {
        if (password2Field == null) {
            password2Field = new JPasswordField();
            password2Field.getDocument().addDocumentListener(textBoxListener);
        }
        return password2Field;
    }
}
