package gov.nih.nci.cagrid.data.style.cacore31.wizard;

import gov.nih.nci.cagrid.common.portal.DocumentChangeAdapter;
import gov.nih.nci.cagrid.common.portal.validation.IconFeedbackPanel;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.style.sdkstyle.wizard.AppserviceConfigCompletionListener;
import gov.nih.nci.cagrid.data.ui.wizard.AbstractWizardPanel;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
 *  AppserviceConfigPanel
 *  Panel for configuring the application service used by the
 *  data service
 * 
 * @author David Ervin
 * 
 * @created Mar 23, 2007 3:35:47 PM
 * @version $Id: AppserviceConfigPanel.java,v 1.2 2009-01-29 20:14:17 dervin Exp $ 
 */
public class AppserviceConfigPanel extends AbstractWizardPanel {
    // keys for validation components
    public static final String KEY_APPSERVICE_URL = "Application service URL";
    public static final String KEY_CSM_CONTEXT = "CSM context name";

    public static final String APPLICATION_SERVICE_URL = "appserviceUrl";
    public static final String CASE_INSENSITIVE_QUERYING = "queryCaseInsensitive";
    public static final String USE_CSM_FLAG = "useCsmSecurity";
    public static final String CSM_CONTEXT_NAME = "csmContextName";

    private JLabel urlLabel = null;
    private JTextField urlTextField = null;
    private JCheckBox caseInsensitiveCheckBox = null;
    private JCheckBox useCsmCheckBox = null;
    private JLabel csmContextLabel = null;
    private JTextField csmContextTextField = null;
    private JButton copyUrlButton = null;
    private JPanel optionsPanel = null;
    private IconFeedbackPanel validationPanel = null;
    
    private List<AppserviceConfigCompletionListener> completionListeners;
    private ValidationResultModel validationModel;
    private DocumentChangeAdapter documentChangeListener;
    
    public AppserviceConfigPanel(ServiceExtensionDescriptionType extensionDescription, ServiceInformation info) {
        super(extensionDescription, info);
        this.completionListeners = new ArrayList<AppserviceConfigCompletionListener>();
        this.validationModel = new DefaultValidationResultModel();
        this.documentChangeListener = new DocumentChangeAdapter() {
            public void documentEdited(DocumentEvent e) {
                validateInput();
            }
        };
        initialize();
    }
    
    
    private void initialize() {
        // initialize values for each of the fields
        initializeValues();
        this.setLayout(new GridLayout());
        this.add(getValidationPanel());
        // set up for validation
        configureValidation();
    }
    
    
    private IconFeedbackPanel getValidationPanel() {
        if (validationPanel == null) {
            JPanel holder = new JPanel();
            // set up the interface layout
            GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
            gridBagConstraints21.gridx = 0;
            gridBagConstraints21.gridwidth = 3;
            gridBagConstraints21.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints21.gridy = 0;
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridx = 2;
            gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints11.gridy = 2;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridy = 2;
            gridBagConstraints3.weightx = 1.0;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.gridx = 1;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.gridy = 2;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.gridy = 1;
            gridBagConstraints1.weightx = 1.0;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.gridwidth = 2;
            gridBagConstraints1.gridx = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.gridy = 1;
            holder.setLayout(new GridBagLayout());
            holder.add(getUrlLabel(), gridBagConstraints);
            holder.add(getUrlTextField(), gridBagConstraints1);
            holder.add(getCsmContextLabel(), gridBagConstraints2);
            holder.add(getCsmContextTextField(), gridBagConstraints3);
            holder.add(getCopyUrlButton(), gridBagConstraints11);
            holder.add(getOptionsPanel(), gridBagConstraints21);
            validationPanel = new IconFeedbackPanel(validationModel, holder);
        }
        return validationPanel;
    }


    public String getPanelShortName() {
        return "Configuration";
    }


    public String getPanelTitle() {
        return "caCORE Application Service Configuration";
    }


    public void update() {
        initializeValues();
    }
    
    
    private void initializeValues() {
        ServiceDescription desc = getServiceInformation().getServiceDescriptor();
        try {
            if (CommonTools.servicePropertyExists(desc, 
                DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + APPLICATION_SERVICE_URL)) {
                String appUrl = CommonTools.getServicePropertyValue(
                    getServiceInformation().getServiceDescriptor(), 
                    DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + APPLICATION_SERVICE_URL);
                getUrlTextField().setText(appUrl);
            }
            if (CommonTools.servicePropertyExists(desc,
                DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + CASE_INSENSITIVE_QUERYING)) {
                String caseInsensitiveValue = CommonTools.getServicePropertyValue(
                    getServiceInformation().getServiceDescriptor(), 
                    DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + CASE_INSENSITIVE_QUERYING);
                boolean caseInsensitive = Boolean.valueOf(caseInsensitiveValue).booleanValue();
                getCaseInsensitiveCheckBox().setSelected(caseInsensitive);
            }
            boolean useCsm = false;
            if (CommonTools.servicePropertyExists(desc,
                DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + USE_CSM_FLAG)) {
                String useCsmValue = CommonTools.getServicePropertyValue(desc,
                    DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + USE_CSM_FLAG);
                useCsm = Boolean.valueOf(useCsmValue).booleanValue();
                getUseCsmCheckBox().setSelected(useCsm);
            }
            if (useCsm && CommonTools.servicePropertyExists(desc,
                DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + CSM_CONTEXT_NAME)) {
                String csmContextValue = CommonTools.getServicePropertyValue(desc,
                    DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + CSM_CONTEXT_NAME);
                getCsmContextTextField().setText(csmContextValue);
            }
            setCsmConfigEnabled(useCsm);
        } catch (Exception ex) {
            ex.printStackTrace();
            CompositeErrorDialog.showErrorDialog("Error loading configuration values: " + ex.getMessage(), ex);
        }
    }


    /**
     * This method initializes urlLabel
     */
    private JLabel getUrlLabel() {
        if (urlLabel == null) {
            urlLabel = new JLabel();
            urlLabel.setText("Remote Service URL:");
        }
        return urlLabel;
    }


    /**
     * This method initializes urlTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getUrlTextField() {
        if (urlTextField == null) {
            urlTextField = new JTextField();
            urlTextField.getDocument().addDocumentListener(new DocumentChangeAdapter() {
                public void documentEdited(DocumentEvent e) {
                    try {
                        CommonTools.setServiceProperty(getServiceInformation().getServiceDescriptor(),
                            DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + APPLICATION_SERVICE_URL,
                            getUrlTextField().getText(), false);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        CompositeErrorDialog.showErrorDialog("Error setting the application service URL: " 
                            + ex.getMessage(), ex);
                    }
                }
            });
            urlTextField.getDocument().addDocumentListener(documentChangeListener);
        }
        return urlTextField;
    }


    /**
     * This method initializes caseInsensitiveCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getCaseInsensitiveCheckBox() {
        if (caseInsensitiveCheckBox == null) {
            caseInsensitiveCheckBox = new JCheckBox();
            caseInsensitiveCheckBox.setText("Case Insensitive Queries");
            caseInsensitiveCheckBox.addItemListener(new ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    try {
                        CommonTools.setServiceProperty(getServiceInformation().getServiceDescriptor(),
                            DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + CASE_INSENSITIVE_QUERYING, 
                            String.valueOf(caseInsensitiveCheckBox.isSelected()), false);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        CompositeErrorDialog.showErrorDialog("Error setting the case insensitive flag: "
                            + ex.getMessage(), ex);
                    }
                }
            });
        }
        return caseInsensitiveCheckBox;
    }


    /**
     * This method initializes useCsmCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getUseCsmCheckBox() {
        if (useCsmCheckBox == null) {
            useCsmCheckBox = new JCheckBox();
            useCsmCheckBox.setText("Use CSM Security");
            useCsmCheckBox.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    setCsmConfigEnabled(getUseCsmCheckBox().isSelected());
                    // set the use CSM property in the service properties
                    try {
                        CommonTools.setServiceProperty(getServiceInformation().getServiceDescriptor(),
                            DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + USE_CSM_FLAG, 
                            String.valueOf(useCsmCheckBox.isSelected()), false);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        CompositeErrorDialog.showErrorDialog("Error storing use CSM property: " 
                            + ex.getMessage(), ex);
                    }
                    validateInput();
                }
            });
        }
        return useCsmCheckBox;
    }


    /**
     * This method initializes csmContextLabel
     * 
     * @return javax.swing.JLabel
     */
    private JLabel getCsmContextLabel() {
        if (csmContextLabel == null) {
            csmContextLabel = new JLabel();
            csmContextLabel.setText("CSM Context Name:");
        }
        return csmContextLabel;
    }


    /**
     * This method initializes csmContextTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getCsmContextTextField() {
        if (csmContextTextField == null) {
            csmContextTextField = new JTextField();
            csmContextTextField.getDocument().addDocumentListener(new DocumentChangeAdapter() {
                public void documentEdited(DocumentEvent e) {
                    try {
                        CommonTools.setServiceProperty(getServiceInformation().getServiceDescriptor(),
                            DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + CSM_CONTEXT_NAME,
                            getCsmContextTextField().getText(), false);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        CompositeErrorDialog.showErrorDialog("Error setting CSM context: " + ex.getMessage(), ex);
                    }
                }
            });
            csmContextTextField.getDocument().addDocumentListener(documentChangeListener);
        }
        return csmContextTextField;
    }


    /**
     * This method initializes copyUrlButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getCopyUrlButton() {
        if (copyUrlButton == null) {
            copyUrlButton = new JButton();
            copyUrlButton.setText("Copy App URL");
            copyUrlButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    String url = getUrlTextField().getText();
                    getCsmContextTextField().setText(url);
                    validateInput();
                }
            });
        }
        return copyUrlButton;
    }


    /**
     * This method initializes optionsPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getOptionsPanel() {
        if (optionsPanel == null) {
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 1;
            gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints5.gridy = 0;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.gridy = 0;
            optionsPanel = new JPanel();
            optionsPanel.setLayout(new GridBagLayout());
            optionsPanel.add(getCaseInsensitiveCheckBox(), gridBagConstraints4);
            optionsPanel.add(getUseCsmCheckBox(), gridBagConstraints5);
        }
        return optionsPanel;
    }


    private void setCsmConfigEnabled(boolean enable) {
        getCsmContextLabel().setEnabled(enable);
        getCsmContextTextField().setEnabled(enable);
        if (!enable) {
            getCsmContextTextField().setText("");
        }
        getCopyUrlButton().setEnabled(enable);
    }
    
    
    // --------------------
    // Completion listeners
    // --------------------
    
    
    public void addCompletionListener(AppserviceConfigCompletionListener listener) {
        completionListeners.add(listener);
    }
    
    
    public boolean removeCompletionListener(AppserviceConfigCompletionListener listener) {
        return completionListeners.remove(listener);
    }
    
    
    protected void setConfigurationComplete(boolean complete) {
        for (AppserviceConfigCompletionListener listener : completionListeners) {
            listener.completionStatusChanged(complete);
        }
    }
    
    
    // ------------------
    // Validation helpers
    // ------------------
    
    
    private void configureValidation() {
        ValidationComponentUtils.setMessageKey(getUrlTextField(), KEY_APPSERVICE_URL);
        ValidationComponentUtils.setMessageKey(getCsmContextTextField(), KEY_CSM_CONTEXT);
        
        validateInput();
        updateComponentTreeSeverity();
    }
    
    
    private void validateInput() {
        ValidationResult result = new ValidationResult();
        
        if (ValidationUtils.isBlank(getUrlTextField().getText())) {
            result.add(new SimpleValidationMessage(
                KEY_APPSERVICE_URL + " should not be blank", Severity.WARNING, KEY_APPSERVICE_URL));
        } else {
            try {
                new URL(getUrlTextField().getText());
            } catch (MalformedURLException ex) {
                result.add(new SimpleValidationMessage(
                    KEY_APPSERVICE_URL + " must contain a valid URL", Severity.ERROR, KEY_APPSERVICE_URL));
            }
        }
        if (getUseCsmCheckBox().isSelected() 
            && ValidationUtils.isBlank(getCsmContextTextField().getText())) {
            result.add(new SimpleValidationMessage(
                KEY_CSM_CONTEXT + " must not be blank", Severity.ERROR, KEY_CSM_CONTEXT));
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
