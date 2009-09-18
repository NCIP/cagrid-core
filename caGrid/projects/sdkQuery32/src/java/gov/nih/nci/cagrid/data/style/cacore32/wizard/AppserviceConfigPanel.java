package gov.nih.nci.cagrid.data.style.cacore32.wizard;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.portal.DocumentChangeAdapter;
import gov.nih.nci.cagrid.common.portal.PortalUtils;
import gov.nih.nci.cagrid.common.portal.validation.IconFeedbackPanel;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.style.sdkstyle.wizard.AppserviceConfigCompletionListener;
import gov.nih.nci.cagrid.data.ui.wizard.AbstractWizardPanel;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ResourceManager;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
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
 * AppserviceConfigPanel 
 * Panel for configuring the application service used by
 * the data service
 * 
 * @author David Ervin
 * 
 * @created Mar 23, 2007 3:35:47 PM
 * @version $Id: AppserviceConfigPanel.java,v 1.9 2007/04/10 14:30:28 hastings
 *          Exp $
 */
public class AppserviceConfigPanel extends AbstractWizardPanel {
    public static final String KEY_APPSERVICE_URL = "Application service url";
    public static final String KEY_CSM_CONTEXT = "CSM context name";
    public static final String KEY_CSM_CONFIG = "CSM configuration file name";

    public static final String APPLICATION_SERVICE_URL = "appserviceUrl";
    public static final String USE_CSM_FLAG = "useCsmSecurity";
    public static final String CASE_INSENSITIVE_QUERYING = "queryCaseInsensitive";
    public static final String CSM_CONFIGURATION_FILENAME = "csmConfigurationFilename";
    public static final String CSM_CONTEXT_NAME = "csmContextName";
    public static final String USE_LOCAL_APPSERVICE = "useLocalAppservice";

    private JCheckBox caseInsensitiveCheckBox = null;
    private JCheckBox useCsmCheckBox = null;
    private JPanel checkBoxPanel = null;
    private JLabel urlLabel = null;
    private JTextField urlTextField = null;
    private JLabel csmContextLabel = null;
    private JTextField csmContextTextField = null;
    private JButton copyUrlButton = null;
    private JLabel csmConfigLabel = null;
    private JTextField csmConfigTextField = null;
    private JButton browseButton = null;
    private JPanel inputPanel = null;
    private IconFeedbackPanel validationPanel = null;

    private List<AppserviceConfigCompletionListener> completionListeners;
    private ValidationResultModel validationModel;
    private DocumentChangeAdapter documentChangeListener;

    public AppserviceConfigPanel(ServiceExtensionDescriptionType extensionDescription, ServiceInformation info) {
        super(extensionDescription, info);
        this.validationModel = new DefaultValidationResultModel();
        this.documentChangeListener = new DocumentChangeAdapter() {
            public void documentEdited(DocumentEvent e) {
                validateInput();
            }
        };
        initialize();
    }


    private void initialize() {
        initializeValues();
        this.setLayout(new GridLayout());
        this.add(getValidationPanel());
        configureValidation();
    }
    
    
    private IconFeedbackPanel getValidationPanel() {
        if (validationPanel == null) {
            JPanel holder = new JPanel();
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.gridx = 0;
            gridBagConstraints9.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints9.weightx = 1.0D;
            gridBagConstraints9.gridy = 1;
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.gridx = 0;
            gridBagConstraints8.gridy = 0;
            holder.setLayout(new GridBagLayout());
            holder.add(getCheckBoxPanel(), gridBagConstraints8);
            holder.add(getInputPanel(), gridBagConstraints9);
            validationPanel = new IconFeedbackPanel(validationModel, holder);
        }
        return validationPanel;
    }


    private void initializeValues() {
        ServiceDescription desc = getServiceInformation().getServiceDescriptor();
        try {
            if (CommonTools.servicePropertyExists(desc, 
                    DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX 
                        + APPLICATION_SERVICE_URL)) {
                String serviceUrl = CommonTools.getServicePropertyValue(desc,
                    DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + APPLICATION_SERVICE_URL);
                getUrlTextField().setText(serviceUrl);
            }
            if (CommonTools.servicePropertyExists(desc, 
                    DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX
                        + CASE_INSENSITIVE_QUERYING)) {
                String caseInsensitiveValue = CommonTools.getServicePropertyValue(desc,
                    DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + CASE_INSENSITIVE_QUERYING);
                boolean caseInsensitive = Boolean.valueOf(caseInsensitiveValue).booleanValue();
                getCaseInsensitiveCheckBox().setSelected(caseInsensitive);
            }
            if (CommonTools.servicePropertyExists(desc, 
                    DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX
                        + CSM_CONFIGURATION_FILENAME)) {
                String csmConfigFilename = CommonTools.getServicePropertyValue(desc,
                    DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + CSM_CONFIGURATION_FILENAME);
                getCsmConfigTextField().setText(csmConfigFilename);
            }
            if (CommonTools.servicePropertyExists(desc, 
                    DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX
                        + CSM_CONTEXT_NAME)) {
                String csmContextName = CommonTools.getServicePropertyValue(desc,
                    DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + CSM_CONTEXT_NAME);
                getCsmContextTextField().setText(csmContextName);
            }
            if (CommonTools.servicePropertyExists(desc, 
                    DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX
                        + USE_CSM_FLAG)) {
                String useCsmValue = CommonTools.getServicePropertyValue(desc,
                    DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + USE_CSM_FLAG);
                boolean useCsm = Boolean.valueOf(useCsmValue).booleanValue();
                getUseCsmCheckBox().setSelected(useCsm);
            }
            if (CommonTools.servicePropertyExists(desc, 
                    DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX
                        + USE_LOCAL_APPSERVICE)) {
                String useLocalValue = CommonTools.getServicePropertyValue(desc,
                    DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + USE_LOCAL_APPSERVICE);
                boolean useLocal = Boolean.valueOf(useLocalValue).booleanValue();
                getUrlLabel().setEnabled(!useLocal);
                getUrlTextField().setEnabled(!useLocal);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            CompositeErrorDialog.showErrorDialog("Error loading configuration values: " + ex.getMessage(), ex);
        }
        enableRelaventComponents();
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


    /**
     * This method initializes caseInsensitiveCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getCaseInsensitiveCheckBox() {
        if (caseInsensitiveCheckBox == null) {
            caseInsensitiveCheckBox = new JCheckBox();
            caseInsensitiveCheckBox.setText("Case Insensitive Queries");
            caseInsensitiveCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
            caseInsensitiveCheckBox.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    CommonTools.setServiceProperty(getServiceInformation().getServiceDescriptor(),
                        DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + CASE_INSENSITIVE_QUERYING, 
                        String.valueOf(getCaseInsensitiveCheckBox().isSelected()), false);
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
            useCsmCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
            useCsmCheckBox.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    CommonTools.setServiceProperty(getServiceInformation().getServiceDescriptor(),
                        DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + USE_CSM_FLAG, 
                        String.valueOf(getUseCsmCheckBox().isSelected()), false);
                    enableRelaventComponents();
                    validateInput();
                }
            });
        }
        return useCsmCheckBox;
    }


    /**
     * This method initializes checkBoxPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCheckBoxPanel() {
        if (checkBoxPanel == null) {
            GridLayout gridLayout = new GridLayout();
            gridLayout.setRows(1);
            gridLayout.setHgap(2);
            gridLayout.setColumns(3);
            checkBoxPanel = new JPanel();
            checkBoxPanel.setLayout(gridLayout);
            checkBoxPanel.add(getCaseInsensitiveCheckBox(), null);
            checkBoxPanel.add(getUseCsmCheckBox(), null);
        }
        return checkBoxPanel;
    }


    /**
     * This method initializes urlLabel
     * 
     * @return javax.swing.JLabel
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
                    CommonTools.setServiceProperty(getServiceInformation().getServiceDescriptor(),
                        DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + APPLICATION_SERVICE_URL, 
                        getUrlTextField().getText(), false);
                }
            });
            urlTextField.getDocument().addDocumentListener(documentChangeListener);
        }
        return urlTextField;
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
                    CommonTools.setServiceProperty(getServiceInformation().getServiceDescriptor(),
                        DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + CSM_CONTEXT_NAME, 
                        getCsmContextTextField().getText(), false);
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
                }
            });
        }
        return copyUrlButton;
    }


    /**
     * This method initializes csmConfigLabel
     * 
     * @return javax.swing.JLabel
     */
    private JLabel getCsmConfigLabel() {
        if (csmConfigLabel == null) {
            csmConfigLabel = new JLabel();
            csmConfigLabel.setText("CSM Configuration File:");
        }
        return csmConfigLabel;
    }


    /**
     * This method initializes csmConfigTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getCsmConfigTextField() {
        if (csmConfigTextField == null) {
            csmConfigTextField = new JTextField();
            csmConfigTextField.setEditable(false);
            csmConfigTextField.getDocument().addDocumentListener(documentChangeListener);
        }
        return csmConfigTextField;
    }


    /**
     * This method initializes browseButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getBrowseButton() {
        if (browseButton == null) {
            browseButton = new JButton();
            browseButton.setText("Browse");
            browseButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    String etcDir = getServiceInformation().getBaseDirectory().getAbsolutePath() 
                        + File.separator + "etc";
                    if (getCsmConfigTextField().getText().length() != 0) {
                        // delete any old config file
                        File oldConfig = 
                            new File(etcDir + File.separator + getCsmConfigTextField().getText());
                        if (oldConfig.exists()) {
                            oldConfig.delete();
                        }
                    }
                    String originalFilename = null;
                    try {
                        originalFilename = ResourceManager.promptFile(null, null);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        CompositeErrorDialog.showErrorDialog("Error in file selection: " + ex.getMessage(), ex);
                    }
                    if (originalFilename != null) {
                        File originalFile = new File(originalFilename);
                        File outputFile = new File(getServiceInformation().getBaseDirectory().getAbsolutePath()
                            + File.separator + "etc" + File.separator + originalFile.getName());
                        try {
                            Utils.copyFile(originalFile, outputFile);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            CompositeErrorDialog.showErrorDialog("Error copying selected file to service directory",
                                ex.getMessage(), ex);
                        }
                        getCsmConfigTextField().setText(outputFile.getName());
                        CommonTools.setServiceProperty(getServiceInformation().getServiceDescriptor(),
                            DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + CSM_CONFIGURATION_FILENAME, 
                            outputFile.getName(), true);
                    }
                }
            });
        }
        return browseButton;
    }


    /**
     * This method initializes inputPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getInputPanel() {
        if (inputPanel == null) {
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.gridx = 2;
            gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints7.gridy = 2;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.gridx = 2;
            gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints6.gridy = 1;
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
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridy = 0;
            gridBagConstraints3.weightx = 1.0;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.gridwidth = 2;
            gridBagConstraints3.gridx = 1;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.gridy = 2;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.gridy = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.gridy = 0;
            inputPanel = new JPanel();
            inputPanel.setLayout(new GridBagLayout());
            inputPanel.add(getUrlLabel(), gridBagConstraints);
            inputPanel.add(getCsmContextLabel(), gridBagConstraints1);
            inputPanel.add(getCsmConfigLabel(), gridBagConstraints2);
            inputPanel.add(getUrlTextField(), gridBagConstraints3);
            inputPanel.add(getCsmContextTextField(), gridBagConstraints4);
            inputPanel.add(getCsmConfigTextField(), gridBagConstraints5);
            inputPanel.add(getCopyUrlButton(), gridBagConstraints6);
            inputPanel.add(getBrowseButton(), gridBagConstraints7);
        }
        return inputPanel;
    }
    
    
    // -------
    // helpers
    // -------
    

    private void enableRelaventComponents() {
        boolean usingLocalApi = !getUrlTextField().isEnabled();
        boolean csmChecked = getUseCsmCheckBox().isSelected();

        PortalUtils.setContainerEnabled(getInputPanel(), true);

        getUseCsmCheckBox().setEnabled(true);

        getCsmContextLabel().setEnabled(csmChecked);
        getCsmContextTextField().setEnabled(csmChecked);
        getCsmContextTextField().setText("");
        getCopyUrlButton().setEnabled(csmChecked);

        getCsmConfigLabel().setEnabled(csmChecked);
        getCsmConfigTextField().setEnabled(csmChecked);
        getCsmConfigTextField().setText("");
        getBrowseButton().setEnabled(csmChecked);

        if (usingLocalApi) {
            PortalUtils.setContainerEnabled(getInputPanel(), false);
            getUseCsmCheckBox().setSelected(false);
            getUseCsmCheckBox().setEnabled(false);
            getUrlTextField().setText("");
        }
    }


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
    
    
    // ----------
    // validation
    // ----------
    
    
    private void configureValidation() {
        ValidationComponentUtils.setMessageKey(getUrlTextField(), KEY_APPSERVICE_URL);
        ValidationComponentUtils.setMessageKey(getCsmContextTextField(), KEY_CSM_CONTEXT);
        ValidationComponentUtils.setMessageKey(getCsmConfigTextField(), KEY_CSM_CONFIG);
        
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
        
        if (getUseCsmCheckBox().isSelected()) { 
            if (ValidationUtils.isBlank(getCsmContextTextField().getText())) {
                result.add(new SimpleValidationMessage(
                    KEY_CSM_CONTEXT + " must not be blank", Severity.ERROR, KEY_CSM_CONTEXT));
            }
            if (ValidationUtils.isBlank(getCsmConfigTextField().getText())) {
                result.add(new SimpleValidationMessage(
                    KEY_CSM_CONFIG + " must not be blank", Severity.ERROR, KEY_CSM_CONFIG));
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
