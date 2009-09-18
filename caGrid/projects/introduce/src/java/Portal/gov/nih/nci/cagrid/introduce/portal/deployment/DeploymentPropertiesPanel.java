package gov.nih.nci.cagrid.introduce.portal.deployment;

import gov.nih.nci.cagrid.common.portal.validation.IconFeedbackPanel;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.portal.common.IntroduceLookAndFeel;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.log4j.Logger;

import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.util.ValidationUtils;
import com.jgoodies.validation.view.ValidationComponentUtils;


public class DeploymentPropertiesPanel extends JPanel {

    private static final Logger logger = Logger.getLogger(DeploymentPropertiesPanel.class);

    private final ValidationResultModel validationModel = new DefaultValidationResultModel();

    private JPanel contentPanel = null;

    private JLabel registrationLabel = null;

    private JLabel deploymentPrefixLabel = null;

    private JLabel indexServiceURLLabel = null;

    private JLabel indexRefreshLabel = null;

    private JLabel refreshRegistationLabel = null;

    private JTextField deploymentPrefixTextField = null;

    private JCheckBox performRegistrationCheckBox = null;

    private JTextField indexServiceURLTextField = null;

    private JTextField indexServiceRefreshjTextField = null;

    private JTextField registrationRefreshjTextField = null;

    private final Properties deploymentProperties;

    private JComboBox IndexIntervalSelectionComboBox = null;

    private JComboBox registrationIntervalSelectionComboBox = null;


    /**
     * This method initializes
     */
    public DeploymentPropertiesPanel(Properties deploymentProperties) {
        this.deploymentProperties = deploymentProperties;
        initialize();
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridheight = 0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0D;
        gridBagConstraints.weighty = 1.0D;
        gridBagConstraints.gridwidth = 0;
        this.setLayout(new GridBagLayout());
        this.setSize(new Dimension(452, 198));
        this.setBorder(BorderFactory.createTitledBorder(null, "Deployment Properties",
            TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12),
            IntroduceLookAndFeel.getPanelLabelColor()));
        this.add(new IconFeedbackPanel(this.validationModel, getContentPanel()), gridBagConstraints);

        // this.add(getContentPanel(), gridBagConstraints);

        initValidation();

    }


    private void initValidation() {
        ValidationComponentUtils.setMessageKey(getDeploymentPrefixTextField(),
            IntroduceConstants.INTRODUCE_DEPLOYMENT_PREFIX_PROPERTY);
        ValidationComponentUtils.setMessageKey(getPerformRegistrationCheckBox(),
            IntroduceConstants.INTRODUCE_DEPLOYMENT_PERFORM_REGISTRATION_PROPERTY);
        ValidationComponentUtils.setMessageKey(getIndexServiceURLTextField(),
            IntroduceConstants.INTRODUCE_DEPLOYMENT_INDEX_SERVICE_URL_PROPERTY);
        ValidationComponentUtils.setMessageKey(getIndexServiceRefreshjTextField(),
            IntroduceConstants.INTRODUCE_DEPLOYMENT_INDEX_REFRESH_PROPERTY);
        ValidationComponentUtils.setMessageKey(getRegistrationRefreshjTextField(),
            IntroduceConstants.INTRODUCE_DEPLOYMENT_REFRESH_REGISTRATION_PROPERTY);

        validateInput();
    }


    public boolean validateInput() {

        ValidationResult result = new ValidationResult();

        if (!ValidationUtils.isNotBlank(getDeploymentPrefixTextField().getText())) {
            result.add(new SimpleValidationMessage("Deployment prefix must not be blank.", Severity.ERROR,
                IntroduceConstants.INTRODUCE_DEPLOYMENT_PREFIX_PROPERTY));
        } else if (!ValidationUtils.isAlphanumeric(getDeploymentPrefixTextField().getText())) {
            result.add(new SimpleValidationMessage("Deployment prefix must be alpha numeric.", Severity.ERROR,
                IntroduceConstants.INTRODUCE_DEPLOYMENT_PREFIX_PROPERTY));
        }

        if (!ValidationUtils.isNotBlank(getIndexServiceURLTextField().getText())) {
            result.add(new SimpleValidationMessage("Index service must not be blank.", Severity.ERROR,
                IntroduceConstants.INTRODUCE_DEPLOYMENT_INDEX_SERVICE_URL_PROPERTY));
        }

        if (!ValidationUtils.isNotBlank(getIndexServiceRefreshjTextField().getText())) {
            result.add(new SimpleValidationMessage("Index service refresh must not be blank.", Severity.ERROR,
                IntroduceConstants.INTRODUCE_DEPLOYMENT_INDEX_REFRESH_PROPERTY));
        } else if (!ValidationUtils.isNumeric(getIndexServiceRefreshjTextField().getText())
            || Integer.parseInt(getIndexServiceRefreshjTextField().getText()) <= 0) {
            result.add(new SimpleValidationMessage("Index service refresh must be a positive integer", Severity.ERROR,
                IntroduceConstants.INTRODUCE_DEPLOYMENT_INDEX_REFRESH_PROPERTY));
        }

        if (!ValidationUtils.isNotBlank(getRegistrationRefreshjTextField().getText())) {
            result.add(new SimpleValidationMessage("Registration refresh must not be blank.", Severity.ERROR,
                IntroduceConstants.INTRODUCE_DEPLOYMENT_REFRESH_REGISTRATION_PROPERTY));
        } else if (!ValidationUtils.isNumeric(getRegistrationRefreshjTextField().getText())
            || Integer.parseInt(getRegistrationRefreshjTextField().getText()) <= 0) {
            result.add(new SimpleValidationMessage("Registration refresh must be a positive integer", Severity.ERROR,
                IntroduceConstants.INTRODUCE_DEPLOYMENT_REFRESH_REGISTRATION_PROPERTY));
        }

        this.validationModel.setResult(result);

        updateComponentTreeSeverity();

        if (result.getErrors() != null && result.getErrors().size() > 0) {
            return false;
        }
        return true;
    }


    private void updateComponentTreeSeverity() {
        ValidationComponentUtils.updateComponentTreeMandatoryAndBlankBackground(this.contentPanel);
        ValidationComponentUtils.updateComponentTreeSeverityBackground(this.contentPanel, this.validationModel
            .getResult());
    }


    /**
     * This method initializes contentPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getContentPanel() {
        if (this.contentPanel == null) {
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.fill = GridBagConstraints.VERTICAL;
            gridBagConstraints10.gridy = 6;
            gridBagConstraints10.weightx = 0.0D;
            gridBagConstraints10.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints10.gridx = 2;
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.fill = GridBagConstraints.VERTICAL;
            gridBagConstraints9.gridy = 4;
            gridBagConstraints9.weightx = 0.0D;
            gridBagConstraints9.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints9.gridx = 2;
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints12.gridy = 6;
            gridBagConstraints12.weightx = 1.0;
            gridBagConstraints12.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints12.gridheight = 2;
            gridBagConstraints12.gridx = 1;
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints11.gridy = 4;
            gridBagConstraints11.weightx = 1.0;
            gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints11.gridheight = 2;
            gridBagConstraints11.gridx = 1;
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.fill = GridBagConstraints.BOTH;
            gridBagConstraints8.gridy = 3;
            gridBagConstraints8.weightx = 1.0;
            gridBagConstraints8.insets = new Insets(2, 50, 2, 2);
            gridBagConstraints8.gridwidth = 3;
            gridBagConstraints8.gridx = 0;
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.gridx = 1;
            gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints7.anchor = GridBagConstraints.WEST;
            gridBagConstraints7.gridy = 1;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.fill = GridBagConstraints.BOTH;
            gridBagConstraints6.gridy = 0;
            gridBagConstraints6.weightx = 1.0;
            gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints6.gridwidth = 2;
            gridBagConstraints6.gridx = 1;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints5.gridy = 6;
            this.refreshRegistationLabel = new JLabel();
            this.refreshRegistationLabel.setText("Service Registration Refresh Rate");
            this.refreshRegistationLabel
                .setToolTipText("This controls how often the service will renew its registration with the Index Service, and is used to set the lifetime of the registration.");
            this.refreshRegistationLabel.setFont(this.refreshRegistationLabel.getFont().deriveFont(Font.BOLD));
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.gridy = 4;
            this.indexRefreshLabel = new JLabel();
            this.indexRefreshLabel.setText("Resource Property Update Rate");
            this.indexRefreshLabel
                .setToolTipText("This controls how often the Index Service will call back to the service to update the cached values of its resource properites.  Unless you are dynamically changing your resource properties, you don't want to set this to too often.");
            this.indexRefreshLabel.setFont(this.indexRefreshLabel.getFont().deriveFont(Font.BOLD));
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridy = 2;
            this.indexServiceURLLabel = new JLabel();
            this.indexServiceURLLabel.setText("Index Service URL");
            this.indexServiceURLLabel.setFont(this.indexServiceURLLabel.getFont().deriveFont(Font.BOLD));
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.gridy = 0;
            this.deploymentPrefixLabel = new JLabel();
            this.deploymentPrefixLabel.setText("Deployment Prefix");
            this.deploymentPrefixLabel.setFont(this.deploymentPrefixLabel.getFont().deriveFont(Font.BOLD));
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.gridy = 1;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.gridx = 0;
            this.registrationLabel = new JLabel();
            this.registrationLabel.setText("Perform Registration");
            this.registrationLabel.setFont(this.registrationLabel.getFont().deriveFont(Font.BOLD));
            this.contentPanel = new JPanel();
            this.contentPanel.setLayout(new GridBagLayout());
            this.contentPanel.add(this.registrationLabel, gridBagConstraints1);
            this.contentPanel.add(this.deploymentPrefixLabel, gridBagConstraints2);
            this.contentPanel.add(this.indexServiceURLLabel, gridBagConstraints3);
            this.contentPanel.add(this.indexRefreshLabel, gridBagConstraints4);
            this.contentPanel.add(this.refreshRegistationLabel, gridBagConstraints5);
            this.contentPanel.add(getDeploymentPrefixTextField(), gridBagConstraints6);
            this.contentPanel.add(getPerformRegistrationCheckBox(), gridBagConstraints7);
            this.contentPanel.add(getIndexServiceURLTextField(), gridBagConstraints8);
            this.contentPanel.add(getIndexServiceRefreshjTextField(), gridBagConstraints11);
            this.contentPanel.add(getRegistrationRefreshjTextField(), gridBagConstraints12);
            this.contentPanel.add(getIndexIntervalSelectionComboBox(), gridBagConstraints9);
            this.contentPanel.add(getRegistrationIntervalSelectionComboBox(), gridBagConstraints10);
        }
        return this.contentPanel;
    }


    /**
     * This method initializes deploymentPrefixTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getDeploymentPrefixTextField() {
        if (this.deploymentPrefixTextField == null) {
            this.deploymentPrefixTextField = new JTextField();
            this.deploymentPrefixTextField.setText(this.deploymentProperties
                .getProperty(IntroduceConstants.INTRODUCE_DEPLOYMENT_PREFIX_PROPERTY));
            this.deploymentPrefixTextField.getDocument().addDocumentListener(new DocumentListener() {

                public void removeUpdate(DocumentEvent e) {
                    validateInput();

                }


                public void insertUpdate(DocumentEvent e) {
                    validateInput();

                }


                public void changedUpdate(DocumentEvent e) {
                    validateInput();

                }

            });
        }
        return this.deploymentPrefixTextField;
    }


    /**
     * This method initializes performRegistrationCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getPerformRegistrationCheckBox() {
        if (this.performRegistrationCheckBox == null) {
            this.performRegistrationCheckBox = new JCheckBox();
            this.performRegistrationCheckBox.setSelected(new Boolean(this.deploymentProperties
                .getProperty(IntroduceConstants.INTRODUCE_DEPLOYMENT_PERFORM_REGISTRATION_PROPERTY)));
        }
        return this.performRegistrationCheckBox;
    }


    /**
     * This method initializes indexServiceURLTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getIndexServiceURLTextField() {
        if (this.indexServiceURLTextField == null) {
            this.indexServiceURLTextField = new JTextField();
            this.indexServiceURLTextField.setText(this.deploymentProperties
                .getProperty(IntroduceConstants.INTRODUCE_DEPLOYMENT_INDEX_SERVICE_URL_PROPERTY));
            this.indexServiceURLTextField.getDocument().addDocumentListener(new DocumentListener() {

                public void removeUpdate(DocumentEvent e) {
                    validateInput();

                }


                public void insertUpdate(DocumentEvent e) {
                    validateInput();

                }


                public void changedUpdate(DocumentEvent e) {
                    validateInput();

                }

            });
        }
        return this.indexServiceURLTextField;
    }


    /**
     * This method initializes indexServiceRefreshjTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getIndexServiceRefreshjTextField() {
        if (this.indexServiceRefreshjTextField == null) {
            this.indexServiceRefreshjTextField = new JTextField();

            int value = Integer.parseInt(this.deploymentProperties
                .getProperty(IntroduceConstants.INTRODUCE_DEPLOYMENT_INDEX_REFRESH_PROPERTY));

            if (value < 60000) {
                this.indexServiceRefreshjTextField.setText(String.valueOf(Integer.parseInt(this.deploymentProperties
                    .getProperty(IntroduceConstants.INTRODUCE_DEPLOYMENT_INDEX_REFRESH_PROPERTY))));
                getIndexIntervalSelectionComboBox().setSelectedItem("seconds");
            } else {
                this.indexServiceRefreshjTextField.setText(String.valueOf(Integer.parseInt(this.deploymentProperties
                    .getProperty(IntroduceConstants.INTRODUCE_DEPLOYMENT_INDEX_REFRESH_PROPERTY)) / 60000));
            }

            this.indexServiceRefreshjTextField.getDocument().addDocumentListener(new DocumentListener() {

                public void removeUpdate(DocumentEvent e) {
                    validateInput();

                }


                public void insertUpdate(DocumentEvent e) {
                    validateInput();

                }


                public void changedUpdate(DocumentEvent e) {
                    validateInput();

                }
            });
        }
        return this.indexServiceRefreshjTextField;
    }


    /**
     * This method initializes registrationRefreshjTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getRegistrationRefreshjTextField() {
        if (this.registrationRefreshjTextField == null) {
            this.registrationRefreshjTextField = new JTextField();

            this.registrationRefreshjTextField.setText(String.valueOf(Integer.parseInt(this.deploymentProperties
                .getProperty(IntroduceConstants.INTRODUCE_DEPLOYMENT_REFRESH_REGISTRATION_PROPERTY)) / 60));

            this.registrationRefreshjTextField.getDocument().addDocumentListener(new DocumentListener() {

                public void removeUpdate(DocumentEvent e) {
                    validateInput();

                }


                public void insertUpdate(DocumentEvent e) {
                    validateInput();

                }


                public void changedUpdate(DocumentEvent e) {
                    validateInput();

                }

            });
        }
        return this.registrationRefreshjTextField;
    }


    public Properties getDeploymentProperties() {
        Properties props = new Properties();
        props.put(IntroduceConstants.INTRODUCE_DEPLOYMENT_PREFIX_PROPERTY, getDeploymentPrefixTextField().getText());
        props.put(IntroduceConstants.INTRODUCE_DEPLOYMENT_PERFORM_REGISTRATION_PROPERTY, String
            .valueOf(getPerformRegistrationCheckBox().isSelected()));
        props.put(IntroduceConstants.INTRODUCE_DEPLOYMENT_INDEX_SERVICE_URL_PROPERTY, getIndexServiceURLTextField()
            .getText());
        int multiplier = 1;
        if (((String) getIndexIntervalSelectionComboBox().getSelectedItem()).equals("seconds")) {
            multiplier = 1000;
        } else if (((String) getIndexIntervalSelectionComboBox().getSelectedItem()).equals("minutes")) {
            multiplier = 1000 * 60;
        } else if (((String) getIndexIntervalSelectionComboBox().getSelectedItem()).equals("hours")) {
            multiplier = 1000 * 60 * 60;
        } else if (((String) getIndexIntervalSelectionComboBox().getSelectedItem()).equals("days")) {
            multiplier = 1000 * 60 * 60 * 24;
        }
        try {
            props.put(IntroduceConstants.INTRODUCE_DEPLOYMENT_INDEX_REFRESH_PROPERTY, String.valueOf((Integer
                .parseInt(getIndexServiceRefreshjTextField().getText()) * multiplier)));
        } catch (NumberFormatException e) {
            // bad format on the field, validator will catch this.
        }
        if (((String) getRegistrationIntervalSelectionComboBox().getSelectedItem()).equals("minutes")) {
            multiplier = 60;
        } else if (((String) getRegistrationIntervalSelectionComboBox().getSelectedItem()).equals("hours")) {
            multiplier = 60 * 60;
        } else if (((String) getRegistrationIntervalSelectionComboBox().getSelectedItem()).equals("days")) {
            multiplier = 60 * 60 * 24;
        }
        try {
            props.put(IntroduceConstants.INTRODUCE_DEPLOYMENT_REFRESH_REGISTRATION_PROPERTY, String.valueOf((Integer
                .parseInt(getRegistrationRefreshjTextField().getText()) * multiplier)));
        } catch (NumberFormatException e) {

            logger.error(e);
        }
        return props;

    }


    /**
     * This method initializes IndexIntervalSelectionComboBox
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getIndexIntervalSelectionComboBox() {
        if (this.IndexIntervalSelectionComboBox == null) {
            this.IndexIntervalSelectionComboBox = new JComboBox();
            this.IndexIntervalSelectionComboBox.addItem("days");
            this.IndexIntervalSelectionComboBox.addItem("hours");
            this.IndexIntervalSelectionComboBox.addItem("minutes");
            this.IndexIntervalSelectionComboBox.addItem("seconds");
            this.IndexIntervalSelectionComboBox.setSelectedItem("minutes");
        }
        return this.IndexIntervalSelectionComboBox;
    }


    /**
     * This method initializes registrationIntervalSelectionComboBox
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getRegistrationIntervalSelectionComboBox() {
        if (this.registrationIntervalSelectionComboBox == null) {
            this.registrationIntervalSelectionComboBox = new JComboBox();
            this.registrationIntervalSelectionComboBox.addItem("days");
            this.registrationIntervalSelectionComboBox.addItem("hours");
            this.registrationIntervalSelectionComboBox.addItem("minutes");
            this.registrationIntervalSelectionComboBox.setSelectedItem("minutes");
        }
        return this.registrationIntervalSelectionComboBox;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
