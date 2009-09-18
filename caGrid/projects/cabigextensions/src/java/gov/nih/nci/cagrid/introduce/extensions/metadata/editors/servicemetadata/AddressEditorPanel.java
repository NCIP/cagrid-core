package gov.nih.nci.cagrid.introduce.extensions.metadata.editors.servicemetadata;

import gov.nih.nci.cagrid.common.portal.validation.IconFeedbackPanel;
import gov.nih.nci.cagrid.metadata.common.Address;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.util.ValidationUtils;
import com.jgoodies.validation.view.ValidationComponentUtils;


/**
 * @author oster
 */
public class AddressEditorPanel extends JPanel {
    private ValidationResultModel validationModel = new DefaultValidationResultModel();
    private ValidationResult validationResult = null;

    private JPanel mainPanel = null;
    private Address address;
    private JLabel street1Label = null;
    private JTextField street1TextField = null;
    private JLabel street2Label = null;
    private JTextField street2TextField = null;
    private JLabel localityLabel = null;
    private JTextField localityTextField = null;
    private JLabel stateLabel = null;
    private JTextField stateTextField = null;
    private JLabel zipLabel = null;
    private JTextField zipTextField = null;
    private JLabel countryLabel = null;
    private JTextField countryTextField = null;


    public AddressEditorPanel() {
        super();
        initialize();
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        
        GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
        gridBagConstraints20.gridx = 0;
        gridBagConstraints20.weightx = 1.0;
        gridBagConstraints20.weighty = 1.0;
        gridBagConstraints20.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints20.gridy = 1;

        setLayout(new GridBagLayout());
        this.add(new IconFeedbackPanel(this.validationModel, getMainPanel()), gridBagConstraints20);

        this.initValidation();
        this.validateInput();
    }


    public JPanel getMainPanel() {
        if (this.mainPanel == null) {

            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints11.gridy = 3;
            gridBagConstraints11.weightx = 1.0;
            gridBagConstraints11.insets = new java.awt.Insets(5, 5, 5, 5);
            gridBagConstraints11.weighty = 0.0;
            gridBagConstraints11.gridx = 3;
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.gridx = 2;
            gridBagConstraints10.insets = new java.awt.Insets(5, 5, 5, 5);
            gridBagConstraints10.anchor = java.awt.GridBagConstraints.EAST;
            gridBagConstraints10.weightx = 0.5;
            gridBagConstraints10.weighty = 0.0;
            gridBagConstraints10.fill = java.awt.GridBagConstraints.NONE;
            gridBagConstraints10.gridy = 3;
            this.countryLabel = new JLabel();
            this.countryLabel.setText("Country");
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints9.gridy = 3;
            gridBagConstraints9.weightx = 1.0;
            gridBagConstraints9.insets = new java.awt.Insets(5, 5, 5, 5);
            gridBagConstraints9.weighty = 0.0;
            gridBagConstraints9.gridx = 1;
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.gridx = 0;
            gridBagConstraints8.anchor = java.awt.GridBagConstraints.EAST;
            gridBagConstraints8.weightx = 0.5;
            gridBagConstraints8.weighty = 0.0;
            gridBagConstraints8.fill = java.awt.GridBagConstraints.NONE;
            gridBagConstraints8.insets = new java.awt.Insets(0, 0, 0, 5);
            gridBagConstraints8.gridy = 3;
            this.zipLabel = new JLabel();
            this.zipLabel.setText("Zip Code");
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints7.gridy = 2;
            gridBagConstraints7.weightx = 1.0;
            gridBagConstraints7.insets = new java.awt.Insets(5, 5, 5, 5);
            gridBagConstraints7.weighty = 0.0;
            gridBagConstraints7.gridx = 3;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.gridx = 2;
            gridBagConstraints6.insets = new java.awt.Insets(5, 5, 5, 5);
            gridBagConstraints6.anchor = java.awt.GridBagConstraints.EAST;
            gridBagConstraints6.weightx = 0.5;
            gridBagConstraints6.weighty = 0.0;
            gridBagConstraints6.fill = java.awt.GridBagConstraints.NONE;
            gridBagConstraints6.gridy = 2;
            this.stateLabel = new JLabel();
            this.stateLabel.setText("State");
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.gridy = 2;
            gridBagConstraints5.weightx = 1.0;
            gridBagConstraints5.insets = new java.awt.Insets(5, 5, 5, 5);
            gridBagConstraints5.weighty = 0.0;
            gridBagConstraints5.gridx = 1;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.anchor = java.awt.GridBagConstraints.EAST;
            gridBagConstraints4.insets = new java.awt.Insets(5, 5, 5, 5);
            gridBagConstraints4.weightx = 0.5;
            gridBagConstraints4.weighty = 0.0;
            gridBagConstraints4.fill = java.awt.GridBagConstraints.NONE;
            gridBagConstraints4.gridy = 2;
            this.localityLabel = new JLabel();
            this.localityLabel.setText("Locality (city)");
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridy = 0;
            gridBagConstraints3.weightx = 1.0;
            gridBagConstraints3.insets = new java.awt.Insets(5, 5, 5, 5);
            gridBagConstraints3.weighty = 0.0;
            gridBagConstraints3.gridx = 3;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 2;
            gridBagConstraints2.anchor = java.awt.GridBagConstraints.EAST;
            gridBagConstraints2.insets = new java.awt.Insets(5, 5, 5, 5);
            gridBagConstraints2.weightx = 0.5;
            gridBagConstraints2.weighty = 0.0;
            gridBagConstraints2.fill = java.awt.GridBagConstraints.NONE;
            gridBagConstraints2.gridy = 0;
            this.street2Label = new JLabel();
            this.street2Label.setText("Street 2");
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.weightx = 1.0;
            gridBagConstraints1.insets = new java.awt.Insets(5, 5, 5, 5);
            gridBagConstraints1.weighty = 0.0;
            gridBagConstraints1.gridx = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
            gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
            gridBagConstraints.weightx = 0.5;
            gridBagConstraints.weighty = 0.0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.NONE;
            gridBagConstraints.gridy = 0;
            this.street1Label = new JLabel();
            this.street1Label.setText("Street 1");

            mainPanel = new JPanel(new GridBagLayout());
            mainPanel.add(this.street1Label, gridBagConstraints);
            mainPanel.add(getStreet1TextField(), gridBagConstraints1);
            mainPanel.add(this.street2Label, gridBagConstraints2);
            mainPanel.add(getStreet2TextField(), gridBagConstraints3);
            mainPanel.add(this.localityLabel, gridBagConstraints4);
            mainPanel.add(getLocalityTextField(), gridBagConstraints5);
            mainPanel.add(this.stateLabel, gridBagConstraints6);
            mainPanel.add(getStateTextField(), gridBagConstraints7);
            mainPanel.add(this.zipLabel, gridBagConstraints8);
            mainPanel.add(getZipTextField(), gridBagConstraints9);
            mainPanel.add(this.countryLabel, gridBagConstraints10);
            mainPanel.add(getCountryTextField(), gridBagConstraints11);
        }
        return this.mainPanel;
    }


    public final class TextBoxListener implements DocumentListener {

        public void changedUpdate(DocumentEvent e) {
            validateInput();
        }


        public void insertUpdate(DocumentEvent e) {
            validateInput();
        }


        public void removeUpdate(DocumentEvent e) {
            validateInput();
        }

    }


    private void initValidation() {
        ValidationComponentUtils.setMessageKey(getZipTextField(), "zip-code");
        ValidationComponentUtils.setMessageKey(getStreet1TextField(), "street-one");
        ValidationComponentUtils.setMessageKey(getCountryTextField(), "country");
        ValidationComponentUtils.setMessageKey(getLocalityTextField(), "locality");

        validateInput();
        updateComponentTreeSeverity();
    }


    private void validateInput() {
        this.validationResult = new ValidationResult();

        if (ValidationUtils.isBlank(getZipTextField().getText())) {
            validationResult.add(new SimpleValidationMessage("Zip Code should not be blank", Severity.WARNING, "zip-code"));
        }
        if (!ValidationUtils.isNumeric(getZipTextField().getText())) {
            validationResult.add(new SimpleValidationMessage("Zip Code should be numeric", Severity.WARNING, "zip-code"));
        }
        if (!ValidationUtils.hasMinimumLength(getZipTextField().getText(), 5)) {
            validationResult.add(new SimpleValidationMessage("Zip Code should be 5 digits", Severity.WARNING, "zip-code"));
        }
        if (!ValidationUtils.hasMaximumLength(getZipTextField().getText(), 5)) {
            validationResult.add(new SimpleValidationMessage("Zip Code should be 5 digits", Severity.WARNING, "zip-code"));
        }
        
        if (ValidationUtils.isBlank(getStreet1TextField().getText())) {
            validationResult.add(new SimpleValidationMessage("Street must be populated", Severity.ERROR, "street-one"));
        }
        
        if (ValidationUtils.isBlank(getCountryTextField().getText())) {
            validationResult.add(new SimpleValidationMessage("Country must be populated", Severity.ERROR, "country"));
        }
        
        if (ValidationUtils.isBlank(getLocalityTextField().getText())) {
            validationResult.add(new SimpleValidationMessage("Locality (city) must be populated", Severity.ERROR, "locality"));
        }
        
        this.validationModel.setResult(validationResult);
        updateComponentTreeSeverity();
    }


    private void updateComponentTreeSeverity() {
        ValidationComponentUtils.updateComponentTreeMandatoryAndBlankBackground(this);
        ValidationComponentUtils.updateComponentTreeSeverityBackground(this, this.validationModel.getResult());
    }


    protected boolean validatePanel() {
        if (validationResult != null && validationResult.hasErrors()) {
            return false;
        } else {
            return true;
        }
    }


    public Address getAddress() {
        this.address = new Address();
        this.address.setCountry(getCountryTextField().getText());
        this.address.setLocality(getLocalityTextField().getText());
        this.address.setPostalCode(getZipTextField().getText());
        this.address.setStateProvince(getStateTextField().getText());
        this.address.setStreet1(getStreet1TextField().getText());
        this.address.setStreet2(getStreet2TextField().getText());

        return this.address;
    }


    public void setAddress(Address address) {
        this.address = address;
        updateView();
    }


    private void updateView() {
        String country = null;
        String locality = null;
        String postalCode = null;
        String stateProvince = null;
        String street1 = null;
        String street2 = null;

        if (this.address != null) {
            country = this.address.getCountry();
            locality = this.address.getLocality();
            postalCode = this.address.getPostalCode();
            stateProvince = this.address.getStateProvince();
            street1 = this.address.getStreet1();
            street2 = this.address.getStreet2();
        }

        getCountryTextField().setText(country);
        getLocalityTextField().setText(locality);
        getZipTextField().setText(postalCode);
        getStateTextField().setText(stateProvince);
        getStreet1TextField().setText(street1);
        getStreet2TextField().setText(street2);
        validateInput();
    }


    /**
     * This method initializes street1TextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getStreet1TextField() {
        if (this.street1TextField == null) {
            this.street1TextField = new JTextField();
            this.street1TextField.setColumns(10);
            this.street1TextField.getDocument().addDocumentListener(new TextBoxListener());
        }
        return this.street1TextField;
    }


    /**
     * This method initializes street2TextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getStreet2TextField() {
        if (this.street2TextField == null) {
            this.street2TextField = new JTextField();
            this.street2TextField.setColumns(10);
        }
        return this.street2TextField;
    }


    /**
     * This method initializes localityTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getLocalityTextField() {
        if (this.localityTextField == null) {
            this.localityTextField = new JTextField();
            this.localityTextField.setColumns(10);
            this.localityTextField.getDocument().addDocumentListener(new TextBoxListener());
        }
        return this.localityTextField;
    }


    /**
     * This method initializes stateTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getStateTextField() {
        if (this.stateTextField == null) {
            this.stateTextField = new JTextField();
            this.stateTextField.setColumns(10);
        }
        return this.stateTextField;
    }


    /**
     * This method initializes zipTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getZipTextField() {
        if (this.zipTextField == null) {
            this.zipTextField = new JTextField();
            this.zipTextField.setColumns(10);
            this.zipTextField.getDocument().addDocumentListener(new TextBoxListener());
        }
        return this.zipTextField;
    }


    /**
     * This method initializes countryTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getCountryTextField() {
        if (this.countryTextField == null) {
            this.countryTextField = new JTextField();
            this.countryTextField.setColumns(10);
            this.countryTextField.getDocument().addDocumentListener(new TextBoxListener());
        }
        return this.countryTextField;
    }
} // @jve:decl-index=0:visual-constraint="10,10"
