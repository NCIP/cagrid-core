package gov.nih.nci.cagrid.introduce.extensions.sdk.discovery;

import gov.nih.nci.cagrid.common.ZipUtilities;
import gov.nih.nci.cagrid.common.portal.MultiEventProgressBar;
import gov.nih.nci.cagrid.common.portal.validation.IconFeedbackPanel;
import gov.nih.nci.cagrid.introduce.beans.configuration.NamespaceReplacementPolicy;
import gov.nih.nci.cagrid.introduce.beans.extension.DiscoveryExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespacesType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.FileFilters;
import gov.nih.nci.cagrid.introduce.common.ResourceManager;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;
import gov.nih.nci.cagrid.introduce.portal.modification.discovery.NamespaceTypeDiscoveryComponent;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationMessage;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.util.ValidationUtils;
import com.jgoodies.validation.view.ValidationComponentUtils;
import com.jgoodies.validation.view.ValidationResultViewFactory;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.Perl5Compiler;


public class SDKTypeSelectionComponent extends NamespaceTypeDiscoveryComponent {

    private static final String CACORESDK_DIR_NAME = "cacoresdk";
    private static final String DEFAULT_PROJECT = "YourProject";
    private static final String DEFAULT_NAMEPACE = "gme://YourProject/1.0/";
    private static final String PROJECT_NAME = "Project Name";
    private static final String NAMESPACE_PREFIX = "Namespace Prefix";
    private static final String MODEL_XMI_FILE = "Model XMI File";
    private static final String PACKAGE_INCLUDES = "Package Includes";
    private static final String PACKAGE_EXCLUDES = "Package Excludes";

    private static final String CACORE_SDK_ZIPFILE_NAME = "caCORE_SDK_411-src.zip";

    private JTextField modelTextField = null;
    private JButton modelBrowseButton = null;
    private JTextField packageIncludeTextField = null;
    private JTextField packageExcludeTextField = null;
    private JTextField namespaceTextField = null;
    private JPanel modelInfoPanel = null;
    private JPanel projectPanel = null;
    private JLabel modelFileLabel = null;
    private JLabel packIncludeLabel = null;
    private JLabel packExcludeLabel = null;
    private JTextField projNameTextField = null;
    private JLabel namespaceLabel = null;
    private JLabel projectNameLabel = null;

    private File extensionDir = null;

    private ValidationResultModel validationModel = new DefaultValidationResultModel();

    private SDKGenerationInformation genInfo = null;

    protected static Log LOG = LogFactory.getLog(SDKTypeSelectionComponent.class.getName());

    private PatternCompiler compiler = new Perl5Compiler();

    
    public SDKTypeSelectionComponent(DiscoveryExtensionDescriptionType desc, NamespacesType currentNamespaces) {
        super(desc, currentNamespaces);
        extensionDir = new File(ExtensionsLoader.EXTENSIONS_DIRECTORY + File.separator + desc.getName());
        initialize();
    }


    /**
     * This method initializes this
     */
    private void initialize() {

        GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
        gridBagConstraints21.gridx = 0;
        gridBagConstraints21.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints21.weightx = 1.0D;
        gridBagConstraints21.weighty = 1.0D;
        gridBagConstraints21.gridy = 1;
        GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
        gridBagConstraints22.gridx = 0;
        gridBagConstraints22.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints22.weightx = 1.0D;
        gridBagConstraints22.weighty = 1.0D;
        gridBagConstraints22.gridy = 2;
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.weightx = 1.0D;
        gridBagConstraints1.weighty = 1.0D;
        gridBagConstraints1.gridy = 0;
        setLayout(new GridBagLayout());
        setBorder(javax.swing.BorderFactory.createTitledBorder(null,
            "Create XML Schemas from an XMI Model File (caCORE SDK 4.1.1 Compliant)",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
            null, null));

        add(new IconFeedbackPanel(validationModel, getModelInfoPanel()), gridBagConstraints1);
        add(new IconFeedbackPanel(validationModel, getProjectPanel()), gridBagConstraints21);

        initValidation();

        JComponent validationReportList = ValidationResultViewFactory.createReportList(validationModel);
        add(validationReportList, gridBagConstraints22);

    }


    private void initValidation() {
        ValidationComponentUtils.setMessageKey(getModelTextField(), MODEL_XMI_FILE);
        ValidationComponentUtils.setMessageKey(getPackageExcludeTextField(), PACKAGE_EXCLUDES);
        ValidationComponentUtils.setMessageKey(getPackageIncludeTextField(), PACKAGE_INCLUDES);
        ValidationComponentUtils.setMessageKey(getProjNameTextField(), PROJECT_NAME);
        ValidationComponentUtils.setMessageKey(getNamespaceTextField(), NAMESPACE_PREFIX);

        updateModel();
        validateInput();
        updateComponentTreeSeverity();
    }


    private final class FocusChangeHandler implements FocusListener {

        public void focusGained(FocusEvent e) {
            update();

        }


        public void focusLost(FocusEvent e) {
            update();
        }


        private void update() {
            updateModel();
            validateInput();
        }
    }


    private void updateComponentTreeSeverity() {
        ValidationComponentUtils.updateComponentTreeMandatoryAndBlankBackground(this);
        ValidationComponentUtils.updateComponentTreeSeverityBackground(this, validationModel.getResult());
    }


    private void validateInput() {

        ValidationResult result = new ValidationResult();

        if (!ValidationUtils.isNotBlank(genInfo.getXmiFile())) {
            result.add(new SimpleValidationMessage(MODEL_XMI_FILE + " must not be blank.", Severity.ERROR,
                MODEL_XMI_FILE));
        } else {
            File f = new File(genInfo.getXmiFile());
            if (f.isFile() && f.canRead()) {
                genInfo.setXmiFile(f.getAbsolutePath());
            } else {
                result.add(new SimpleValidationMessage(MODEL_XMI_FILE + " must be a valid readible file.",
                    Severity.ERROR, MODEL_XMI_FILE));
            }
        }

        if (!ValidationUtils.isNotBlank(genInfo.getProjectName())) {
            result.add(new SimpleValidationMessage(PROJECT_NAME + " must not be blank.", Severity.ERROR, PROJECT_NAME));
        } else if (genInfo.getProjectName().equals(DEFAULT_PROJECT)) {
            result.add(new SimpleValidationMessage(PROJECT_NAME + " should be changed to your project's name.",
                Severity.WARNING, PROJECT_NAME));
        }

        if (!ValidationUtils.isNotBlank(genInfo.getNamespacePrefix())) {
            result.add(new SimpleValidationMessage(NAMESPACE_PREFIX + " must not be blank.", Severity.ERROR,
                NAMESPACE_PREFIX));
        } else if (genInfo.getNamespacePrefix().equals(DEFAULT_NAMEPACE)) {
            result.add(new SimpleValidationMessage(NAMESPACE_PREFIX
                + " should be changed to a reasonable namespace prefix for your project", Severity.WARNING,
                NAMESPACE_PREFIX));
        } else if (!genInfo.getNamespacePrefix().endsWith("/")) {
            result.add(new SimpleValidationMessage(NAMESPACE_PREFIX
                + " generally should end in a / as package names will be appended to it to create each full namespace",
                Severity.WARNING, NAMESPACE_PREFIX));
        }

        
        if (ValidationUtils.isBlank(this.genInfo.getPackageIncludes())) {
            result.add(new SimpleValidationMessage(PACKAGE_INCLUDES
                + " should not be blank, as nothing will be included.", Severity.WARNING, PACKAGE_INCLUDES));
        } else {
            try {
                this.compiler.compile(this.genInfo.getPackageIncludes());
            } catch (MalformedPatternException e) {
                result.add(new SimpleValidationMessage(PACKAGE_INCLUDES + " must be a valid regex:" + e.getMessage(),
                    Severity.ERROR, PACKAGE_INCLUDES));
            }
        }

        if (!ValidationUtils.isBlank(this.genInfo.getPackageExcludes())) {
            try {
                this.compiler.compile(this.genInfo.getPackageExcludes());
            } catch (MalformedPatternException e) {
                result.add(new SimpleValidationMessage(PACKAGE_EXCLUDES + " must be empty, or a valid regex:"
                    + e.getMessage(), Severity.ERROR, PACKAGE_EXCLUDES));
            }
        }

        validationModel.setResult(result);
        updateComponentTreeSeverity();

    }


    public NamespaceType[] createNamespaceType(File schemaDestinationDir, NamespaceReplacementPolicy replacementPolicy, MultiEventProgressBar progress) {
        // updates genInfo
        updateModel();

        // updates validationModel
        validateInput();

        // populate the error dialog with messages
        if (validationModel.getResult().hasErrors()) {
            System.out.println("Inputs not valid, see error messages.");
            for (ValidationMessage message : validationModel.getResult().getErrors()) {
                addError(message.formattedText());
            }
            return null;
        }

        SDKExecutionResult result = null;
        try {
            try {
                int initEventID = progress.startEvent("Initializing SDK...");
                File sdkDir = new File(extensionDir, CACORESDK_DIR_NAME);
                // create working dir from zip if not extracted yet
                if (!sdkDir.exists()) {
                    // progress.getProgress().setString("Extracting SDK...");
                    File zip = new File(extensionDir, CACORE_SDK_ZIPFILE_NAME);
                    if (zip.exists() && zip.canRead() && zip.isFile()) {
                        try {
                            ZipUtilities.unzip(zip, sdkDir);
                        } catch (IOException e) {
                            addError("Problem extracting SDK from zip file: " + zip);
                            setErrorCauseThrowable(e);
                            return null;
                        }
                    } else {
                        addError("SDK directory [" + sdkDir + "] not found, and couldn't locate zip file: " + zip);
                        return null;
                    }
                }
                progress.stopEvent(initEventID, "Initialization Complete");

                int sdkEventID = progress.startEvent("Executing SDK...");
                result = SDKExecutor.runSDK(sdkDir, genInfo, progress);
                progress.stopEvent(sdkEventID, "SDK execution complete.");
            } catch (SDKExecutionException e) {
                addError("Problem executing SDK: " + e.getMessage());
                setErrorCauseThrowable(e);
                return null;
            }

            // validate any existing schemas against the namespace replacement
            // policy
            try {
                int validateEventID = progress.startEvent("Validating results...");
                validateGeneratedXSDs(result, replacementPolicy);
                progress.stopEvent(validateEventID, "Validation complete.");
            } catch (SDKExecutionException e) {
                addError("Problem validating schemas generated by SDK against replacement policy:" + e.getMessage());
                setErrorCauseThrowable(e);
                return null;
            }

            // copy the XSD(s) to service schema dir
            List<File> copiedXSDs = null;
            try {
                int copyEventID = progress.startEvent("Copying schemas to service...");
                copiedXSDs = copyGeneratedXSDs(schemaDestinationDir, result, replacementPolicy);
                progress.stopEvent(copyEventID, "Copy successful.");
            } catch (IOException e) {
                addError("Problem copying schemas generated by SDK:" + e.getMessage());
                setErrorCauseThrowable(e);
                return null;
            }

            // add schema types to introduce namespaces
            NamespaceType[] results = null;
            try {
                // progress.getProgress().setString("Adding results to
                // service...");
                //TODO: add UMLProjectIdenifier's to the namespaces
                results = createNamespaceTypes(schemaDestinationDir, copiedXSDs);
            } catch (Exception e) {
                addError("Problem processing schemas created by SDK: " + e.getMessage());
                setErrorCauseThrowable(e);
                return null;
            }

            return results;
        } finally {
            if (result != null) {
                progress.startEvent("Cleaning up working directory.");
                // clean up our working area when we are done with it
                result.destroy();
                progress.stopAll("Complete.");
            }
        }

    }


    /**
     * @param result
     * @throws Exception
     */
    private NamespaceType[] createNamespaceTypes(File schemaDestinationDir, List<File> schemas) throws Exception {
        List<NamespaceType> namespaceTypes = new ArrayList<NamespaceType>();
        for (File schema : schemas) {
            NamespaceType nsType = CommonTools.createNamespaceType(schema.getAbsolutePath(), schemaDestinationDir);
            namespaceTypes.add(nsType);
        }

        NamespaceType[] nsTypeArray = new NamespaceType[namespaceTypes.size()];
        namespaceTypes.toArray(nsTypeArray);
        return nsTypeArray;
    }


    /**
     * Copies schemas into service directory, honoring
     * 
     * @param schemaDestinationDir
     * @param result
     * @param namespaceExistsPolicy
     * @return A list of the schema files in the destination directory
     * @throws IOException
     */
    private List<File> copyGeneratedXSDs(File schemaDestinationDir, SDKExecutionResult result,
        NamespaceReplacementPolicy replacementPolicy) throws IOException {
        List<File> results = new ArrayList<File>();
        for (File schema : result.getGeneratedXMLSchemas()) {
            String targetNamespace = null;
            try {
                targetNamespace = CommonTools.getTargetNamespace(schema);
            } catch (Exception e) {
                String error = "Problem determining targetNamespace of generated schema:" + schema.getAbsolutePath();
                LOG.error(error, e);
                throw new IOException(error);
            }

            // this should have been handled by the validation method and we
            // shouldn't be copying schemas
            assert (!(namespaceAlreadyExists(targetNamespace) && replacementPolicy.equals(NamespaceReplacementPolicy.ERROR)));

            if (namespaceAlreadyExists(targetNamespace) && replacementPolicy.equals(NamespaceReplacementPolicy.IGNORE)) {
                LOG.info("Ignoring schema [" + schema.getAbsolutePath() + "] with namespace [" + targetNamespace
                    + "], as it already exists");
            } else {
                File dest = new File(schemaDestinationDir, genInfo.getProjectName().replace(" ", "_") + "/"
                    + schema.getName());
                FileUtils.copyFile(schema, dest);
                LOG.info("Adding schema:" + dest);
                results.add(dest);
            }
        }
        return results;
    }


    /**
     * Checks generated schemas against ERROR policy so no schemas are copied
     * into service if they already exist
     * 
     * @param result
     * @param namespaceExistsPolicy
     * @throws SDKExecutionException
     */
    private void validateGeneratedXSDs(SDKExecutionResult result, NamespaceReplacementPolicy replacementPolicy)
        throws SDKExecutionException {
        for (File schema : result.getGeneratedXMLSchemas()) {
            String targetNamespace = null;
            try {
                targetNamespace = CommonTools.getTargetNamespace(schema);
            } catch (Exception e) {
                String error = "Problem determining targetNamespace of generated schema:" + schema.getAbsolutePath();
                LOG.error(error, e);
                throw new SDKExecutionException(error, e);
            }
            if (namespaceAlreadyExists(targetNamespace) && replacementPolicy.equals(NamespaceReplacementPolicy.ERROR)) {
                String error = "Could not add generated schema with namespace ["
                    + targetNamespace
                    + "], as it already exists and Introduce preference was to error.  To change this behavior, edit your preferences.";
                throw new SDKExecutionException(error);
            }

        }
    }


    private SDKGenerationInformation updateModel() {
        genInfo = new SDKGenerationInformation();

        genInfo.setProjectName(getProjNameTextField().getText());
        genInfo.setNamespacePrefix(getNamespaceTextField().getText());
        genInfo.setPackageExcludes(getPackageExcludeTextField().getText());
        genInfo.setPackageIncludes(getPackageIncludeTextField().getText());
        genInfo.setXmiFile(getModelTextField().getText());
        return genInfo;
    }


    /**
     * This method initializes modelTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getModelTextField() {
        if (modelTextField == null) {
            modelTextField = new JTextField();
            modelTextField.setColumns(20);
            modelTextField.addFocusListener(new FocusChangeHandler());
        }
        return modelTextField;
    }


    /**
     * This method initializes modelBrowseButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getModelBrowseButton() {
        if (modelBrowseButton == null) {
            modelBrowseButton = new JButton();
            modelBrowseButton.setText("Browse...");
            modelBrowseButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    launchModelFileBrowser();
                }
            });
        }
        return modelBrowseButton;
    }


    protected void launchModelFileBrowser() {
        String selectedFilename = null;
        try {
            selectedFilename = ResourceManager.promptFile(null, FileFilters.XMI_FILTER);
        } catch (IOException e) {
            addError("Problem selecting file, please try again or manually type the file path.");
            setErrorCauseThrowable(e);
            return;
        }

        if (selectedFilename != null) {
            getModelTextField().setText(new File(selectedFilename).getAbsolutePath());
            updateModel();
            validateInput();
        }
    }


    /**
     * This method initializes packageIncludeTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getPackageIncludeTextField() {
        if (packageIncludeTextField == null) {
            packageIncludeTextField = new JTextField();
            packageIncludeTextField.setText(".*?domain.*");
            packageIncludeTextField.setColumns(20);
            packageIncludeTextField.addFocusListener(new FocusChangeHandler());
        }
        return packageIncludeTextField;
    }


    /**
     * This method initializes packageExcludeTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getPackageExcludeTextField() {
        if (packageExcludeTextField == null) {
            packageExcludeTextField = new JTextField();
            packageExcludeTextField.addFocusListener(new FocusChangeHandler());
        }
        return packageExcludeTextField;
    }


    private JTextField getNamespaceTextField() {
        if (namespaceTextField == null) {
            namespaceTextField = new JTextField();
            namespaceTextField.setText(DEFAULT_NAMEPACE);
            namespaceTextField.addFocusListener(new FocusChangeHandler());
        }
        return namespaceTextField;
    }


    /**
     * This method initializes modelInfoPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getModelInfoPanel() {
        if (modelInfoPanel == null) {
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints7.anchor = java.awt.GridBagConstraints.WEST;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.gridx = 0;
            gridBagConstraints6.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints6.gridy = 2;
            packExcludeLabel = new JLabel();
            packExcludeLabel.setText(PACKAGE_EXCLUDES);
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints5.gridy = 1;
            packIncludeLabel = new JLabel();
            packIncludeLabel.setText(PACKAGE_INCLUDES);
            modelFileLabel = new JLabel();
            modelFileLabel.setText(MODEL_XMI_FILE);
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.gridx = 1;
            gridBagConstraints4.gridy = 2;
            gridBagConstraints4.weightx = 1.0;
            gridBagConstraints4.insets = new java.awt.Insets(2, 2, 2, 2);
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridx = 1;
            gridBagConstraints3.gridy = 1;
            gridBagConstraints3.weightx = 1.0;
            gridBagConstraints3.insets = new java.awt.Insets(2, 2, 2, 2);
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridy = 0;
            gridBagConstraints2.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints2.gridx = 2;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints.weightx = 1.0;
            modelInfoPanel = new JPanel();
            modelInfoPanel.setLayout(new GridBagLayout());
            modelInfoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Model Information",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            modelInfoPanel.add(modelFileLabel, gridBagConstraints7);
            modelInfoPanel.add(getModelTextField(), gridBagConstraints);
            modelInfoPanel.add(getModelBrowseButton(), gridBagConstraints2);
            modelInfoPanel.add(getPackageIncludeTextField(), gridBagConstraints3);
            modelInfoPanel.add(getPackageExcludeTextField(), gridBagConstraints4);
            modelInfoPanel.add(packIncludeLabel, gridBagConstraints5);
            modelInfoPanel.add(packExcludeLabel, gridBagConstraints6);
        }
        return modelInfoPanel;
    }


    /**
     * This method initializes projectPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getProjectPanel() {
        if (projectPanel == null) {
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridx = 0;
            gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints11.anchor = GridBagConstraints.WEST;
            gridBagConstraints11.gridy = 0;
            projectNameLabel = new JLabel();
            projectNameLabel.setText("Project Name");
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.gridx = 0;
            gridBagConstraints10.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints10.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints10.anchor = GridBagConstraints.WEST;
            gridBagConstraints10.gridy = 1;
            namespaceLabel = new JLabel();
            namespaceLabel.setText("Namespace Prefix");
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints9.gridx = 2;
            gridBagConstraints9.gridy = 0;
            gridBagConstraints9.weightx = 1.0;
            gridBagConstraints9.weighty = 0.0;
            gridBagConstraints9.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints9.insets = new java.awt.Insets(2, 2, 2, 2);

            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints8.gridx = 2;
            gridBagConstraints8.gridy = 1;
            gridBagConstraints8.weightx = 1.0;
            gridBagConstraints8.weighty = 0.0;
            gridBagConstraints8.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints8.insets = new java.awt.Insets(2, 2, 2, 2);

            projectPanel = new JPanel();
            projectPanel.setLayout(new GridBagLayout());
            projectPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Project Information",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            projectPanel.add(getNamespaceTextField(), gridBagConstraints8);
            projectPanel.add(getProjNameTextField(), gridBagConstraints9);
            projectPanel.add(namespaceLabel, gridBagConstraints10);
            projectPanel.add(projectNameLabel, gridBagConstraints11);
        }
        return projectPanel;
    }


    /**
     * This method initializes projNameTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getProjNameTextField() {
        if (projNameTextField == null) {
            projNameTextField = new JTextField();
            projNameTextField.setText(DEFAULT_PROJECT);
            projNameTextField.addFocusListener(new FocusChangeHandler());
        }
        return projNameTextField;
    }

}
