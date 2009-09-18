package gov.nih.nci.cagrid.introduce.portal.creation;

import gov.nih.nci.cagrid.common.portal.DocumentChangeAdapter;
import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;
import gov.nih.nci.cagrid.common.portal.validation.IconFeedbackPanel;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ConfigurationUtil;
import gov.nih.nci.cagrid.introduce.common.ResourceManager;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;
import gov.nih.nci.cagrid.introduce.extensions.metadata.constants.MetadataConstants;
import gov.nih.nci.cagrid.introduce.portal.common.IntroduceLookAndFeel;
import gov.nih.nci.cagrid.introduce.portal.modification.extensions.ExtensionsTable;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.util.ValidationUtils;
import com.jgoodies.validation.view.ValidationComponentUtils;


/**
 * CaBIGCreationViewer Creation dialog for caBIG service
 * 
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @created Jun 22, 2005
 * @version $Id: mobiusEclipseCodeTemplates.xml,v 1.2 2005/04/19 14:58:02 oster
 *          Exp $
 */
public class CaBIGCreationViewer extends CreationViewerBaseComponent {

    // keys for validation of input components
    public static final String SERVICE_DIR = "Service directory";

    public static final String SERVICE_NAME = "Service name";

    public static final String SERVICE_PACKAGE = "Service package name";

    public static final String SERVICE_NAMESPACE = "Service namespace";

    // extension names
    private static final String DATA_EXTSION_NAME = "data";

    private static final String METADATA_EXTSION_NAME = MetadataConstants.EXTENSION_NAME;

    private JPanel inputPanel = null;

    private JPanel mainPanel = null;

    private JPanel buttonPanel = null;

    private JButton createButton = null;

    private JLabel serviceLabel = null;

    private JTextField service = null;

    private JLabel destinationLabel = null;

    private JTextField dir = null;

    private JButton dirButton = null;

    private JLabel packageLabel = null;

    private JTextField servicePackage = null;

    private JLabel namespaceLabel = null;

    private JTextField namespaceDomain = null;

    private JButton closeButton = null;

    private JComboBox serviceStyleSeletor = null;

    private JPanel extensionsPanel = null;

    private JButton addExtensionButton = null;

    private JButton removeExtensionButton = null;

    private JScrollPane extensionsScrollPane = null;

    private ExtensionsTable extensionsTable = null;

    private JPanel extensionsTablePanel = null;

    private JLabel upExtensionLabel = null;

    private JLabel downExtensionLabel = null;

    private JPanel extSelectionPanel = null;

    private JPanel serviceStylePanel = null;

    private JTabbedPane jTabbedPane = null;

    private JPanel serviceTyeSelectionPanel = null;

    private JRadioButton dataRadioButton = null;

    private JRadioButton analyticalRadioButton = null;

    private ButtonGroup serviceStyleButtonGroup = null;

    private ValidationResultModel validationModel = new DefaultValidationResultModel();


    public CaBIGCreationViewer() {
        super();
        initialize();
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        this.setContentPane(new IconFeedbackPanel(validationModel, getMainPanel()));
        this.setFrameIcon(IntroduceLookAndFeel.getCreateServiceIcon());
        this.setTitle("Create a caBIG Grid Service");
        getServiceStyleButtonGroup().add(getDataRadioButton());
        getServiceStyleButtonGroup().add(getAnalyticalRadioButton());
        getServiceStyleButtonGroup().setSelected(getAnalyticalRadioButton().getModel(), true);
        addAnalyticalExtensions();

        initValidation();
    }


    private void initValidation() {
        ValidationComponentUtils.setMessageKey(getService(), SERVICE_NAME);
        ValidationComponentUtils.setMessageKey(getServicePackage(), SERVICE_PACKAGE);
        ValidationComponentUtils.setMessageKey(getNamespaceDomain(), SERVICE_NAMESPACE);
        ValidationComponentUtils.setMessageKey(getDir(), SERVICE_DIR);

        // updateModel();
        validateInput();
        updateComponentTreeSeverity();
    }


    private void validateInput() {
        ValidationResult result = new ValidationResult();

        if (!ValidationUtils.isNotBlank(this.getDir().getText())) {
            result.add(new SimpleValidationMessage(SERVICE_DIR + " must not be blank.", Severity.ERROR, SERVICE_DIR));
        } else {
            File file = new File(this.getDir().getText());
            if (file.exists()) {
                result.add(new SimpleValidationMessage(SERVICE_DIR
                    + " already exists and will be removed when new skeleton is created.", Severity.WARNING,
                    SERVICE_DIR));
            }
        }

        if (!ValidationUtils.isNotBlank(this.getService().getText())) {
            result.add(new SimpleValidationMessage(SERVICE_NAME + " must not be blank.", Severity.ERROR, SERVICE_NAME));
        } else if (!CommonTools.isValidServiceName(this.getService().getText())) {
            result.add(new SimpleValidationMessage(SERVICE_NAME
                + " is not valid.  Service name must be a java compatible class name. ("
                + CommonTools.ALLOWED_JAVA_CLASS_REGEX + ")", Severity.ERROR, SERVICE_NAME));
        }

        if (!ValidationUtils.isNotBlank(this.getServicePackage().getText())) {
            result.add(new SimpleValidationMessage(SERVICE_PACKAGE + " must not be blank.", Severity.ERROR,
                SERVICE_PACKAGE));
        } else if (!CommonTools.isValidPackageName(this.getServicePackage().getText())) {
            result.add(new SimpleValidationMessage(SERVICE_PACKAGE
                + " is not valid.  Service package must be in valid java package format. ("
                + CommonTools.ALLOWED_JAVA_PACKAGE_REGEX + ")", Severity.ERROR, SERVICE_PACKAGE));
        }

        if (!ValidationUtils.isNotBlank(this.getNamespaceDomain().getText())) {
            result.add(new SimpleValidationMessage(SERVICE_NAMESPACE + " must not be blank.", Severity.ERROR,
                SERVICE_NAMESPACE));
        } else {
            try {
                new URI(this.getNamespaceDomain().getText());
            } catch (Exception e) {
                result.add(new SimpleValidationMessage(SERVICE_NAMESPACE + " is not a well formed namespace",
                    Severity.ERROR, SERVICE_NAMESPACE));
            }
        }

        this.validationModel.setResult(result);

        updateComponentTreeSeverity();
        updateCreationButton();
    }


    private void updateComponentTreeSeverity() {
        ValidationComponentUtils.updateComponentTreeMandatoryAndBlankBackground(this);
        ValidationComponentUtils.updateComponentTreeSeverityBackground(this, this.validationModel.getResult());
    }


    private void updateCreationButton() {
        if (this.validationModel.hasErrors()) {
            this.getCreateButton().setEnabled(false);
        } else {
            this.getCreateButton().setEnabled(true);
        }
    }


    private void addAnalyticalExtensions() {
        ServiceExtensionDescriptionType serviceExtension = ExtensionsLoader.getInstance().getServiceExtension(
            METADATA_EXTSION_NAME);
        String displayName = serviceExtension.getDisplayName();
        getExtensionsTable().addRow(displayName);
    }


    private void addDataExtensions() {
        ServiceExtensionDescriptionType serviceExtension = ExtensionsLoader.getInstance().getServiceExtension(
            DATA_EXTSION_NAME);
        String displayName = serviceExtension.getDisplayName();
        getExtensionsTable().addRow(displayName);
    }


    private void removeDataExtensions() {
        ServiceExtensionDescriptionType serviceExtension = ExtensionsLoader.getInstance().getServiceExtension(
            DATA_EXTSION_NAME);
        String displayName = serviceExtension.getDisplayName();
        getExtensionsTable().removeRow(displayName);
    }


    /**
     * This method initializes jPanel
     */
    private JPanel getInputPanel() {
        if (inputPanel == null) {
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints12.gridy = 4;
            gridBagConstraints12.weightx = 1.0;
            gridBagConstraints12.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints12.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints12.gridwidth = 2;
            gridBagConstraints12.weighty = 1.0D;
            gridBagConstraints12.gridx = 1;
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridy = 4;
            gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints11.gridx = 0;
            namespaceLabel = new JLabel();
            namespaceLabel.setText("STEP 4:  Enter a namespace for the generated WSDL:");
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints10.gridy = 3;
            gridBagConstraints10.weightx = 1.0;
            gridBagConstraints10.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints10.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints10.gridwidth = 2;
            gridBagConstraints10.weighty = 1.0D;
            gridBagConstraints10.gridx = 1;
            inputPanel = new JPanel();
            inputPanel.setLayout(new GridBagLayout());
            inputPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Define the service",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, PortalLookAndFeel.getPanelLabelColor()));
            packageLabel = new JLabel();
            packageLabel.setText("STEP 3:  Enter a Java package for the generated code:");
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.gridx = 0;
            gridBagConstraints9.gridy = 3;
            gridBagConstraints9.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints9.anchor = java.awt.GridBagConstraints.WEST;
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints8.gridy = 3;
            gridBagConstraints8.weightx = 1.0;
            gridBagConstraints8.gridwidth = 2;
            gridBagConstraints8.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints8.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints8.weighty = 1.0D;
            gridBagConstraints8.gridx = 1;
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.gridy = 3;
            gridBagConstraints7.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints7.gridx = 0;
            destinationLabel = new JLabel();
            destinationLabel.setText("STEP 1:  Select a directory for your service:");
            destinationLabel.setName("Destination Directory");
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints6.gridx = 2;
            gridBagConstraints6.gridy = 1;
            gridBagConstraints6.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints6.gridwidth = 1;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridy = 1;
            gridBagConstraints5.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.gridx = 0;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridy = 2;
            gridBagConstraints4.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.gridx = 0;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints3.gridx = 1;
            gridBagConstraints3.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints3.gridy = 1;
            gridBagConstraints3.weighty = 1.0D;
            gridBagConstraints3.weightx = 1.0;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints2.gridx = 1;
            gridBagConstraints2.gridy = 2;
            gridBagConstraints2.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints2.gridwidth = 2;
            gridBagConstraints2.weighty = 1.0D;
            gridBagConstraints2.weightx = 1.0;
            inputPanel.add(destinationLabel, gridBagConstraints5);
            inputPanel.add(getDirButton(), gridBagConstraints6);
            serviceLabel = new JLabel();
            serviceLabel.setText("STEP 2:  Enter a name for the service:");
            inputPanel.add(packageLabel, gridBagConstraints7);
            inputPanel.add(getService(), gridBagConstraints2);
            inputPanel.add(getDir(), gridBagConstraints3);
            inputPanel.add(getServicePackage(), gridBagConstraints8);
            inputPanel.add(getNamespaceDomain(), gridBagConstraints12);
            inputPanel.add(serviceLabel, gridBagConstraints4);
            inputPanel.add(namespaceLabel, gridBagConstraints11);
        }
        return inputPanel;
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            GridBagConstraints gridBagConstraints110 = new GridBagConstraints();
            gridBagConstraints110.gridx = 0;
            gridBagConstraints110.weightx = 1.0;
            gridBagConstraints110.weighty = 1.0;
            gridBagConstraints110.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints110.gridy = 1;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.insets = new java.awt.Insets(5, 5, 5, 5);
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.gridy = 2;
            gridBagConstraints1.anchor = java.awt.GridBagConstraints.SOUTH;
            gridBagConstraints1.gridheight = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridheight = 1;
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
            gridBagConstraints.weightx = 1.0D;
            gridBagConstraints.weighty = 0.0D;
            gridBagConstraints.gridwidth = 1;
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.add(getInputPanel(), gridBagConstraints);
            mainPanel.add(getButtonPanel(), gridBagConstraints1);
            mainPanel.add(getServiceStylePanel(), gridBagConstraints110);
        }
        return mainPanel;
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new JPanel();
            buttonPanel.add(getCreateButton(), null);
            buttonPanel.add(getCloseButton(), null);
        }
        return buttonPanel;
    }


    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getCreateButton() {
        if (createButton == null) {
            createButton = new JButton();
            createButton.setText("Create");
            createButton.setIcon(IntroduceLookAndFeel.getCreateServiceIcon());
            createButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    List<String> extensions = new ArrayList<String>();
                    for (int i = 0; i < getExtensionsTable().getRowCount(); i++) {
                        ServiceExtensionDescriptionType edt = null;
                        try {
                            edt = ExtensionsLoader.getInstance().getServiceExtensionByDisplayName(
                                getExtensionsTable().getRowData(i));
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        extensions.add(edt.getName());
                    }
                    createService(getDir().getText(), getService().getText(), getServicePackage().getText(),
                        getNamespaceDomain().getText(), Arrays.asList(new String[]{
                                IntroduceConstants.INTRODUCE_MAIN_RESOURCE,
                                IntroduceConstants.INTRODUCE_SINGLETON_RESOURCE,
                                IntroduceConstants.INTRODUCE_IDENTIFIABLE_RESOURCE,
                                IntroduceConstants.INTRODUCE_RESOURCEPROPETIES_RESOURCE}), extensions);

                    try {
                        ResourceManager.setStateProperty(ResourceManager.LAST_DIRECTORY, getDir().getText());
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            });
        }

        return createButton;
    }


    /**
     * This method initializes serviceStyleSeletor
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getServiceStyleSeletor() {
        if (serviceStyleSeletor == null) {
            serviceStyleSeletor = new JComboBox();
            serviceStyleSeletor.addItem("NONE");

            List extensionDescriptors = ExtensionsLoader.getInstance().getServiceExtensions();
            for (int i = 0; i < extensionDescriptors.size(); i++) {
                ServiceExtensionDescriptionType ex = (ServiceExtensionDescriptionType) extensionDescriptors.get(i);
                serviceStyleSeletor.addItem(ex.getDisplayName());
            }
        }
        return serviceStyleSeletor;
    }


    /**
     * This method initializes service
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getService() {
        if (service == null) {
            service = new JTextField();
            service.setText(ConfigurationUtil.getIntroduceServiceDefaults().getServiceName());
            service.getDocument().addDocumentListener(new DocumentChangeAdapter() {
                public void documentEdited(DocumentEvent e) {
                    updateSuggestedNamespace();
                    validateInput();
                }
            });
        }
        return service;
    }


    protected void updateSuggestedNamespace() {
        String servName = getService().getText();
        String pack = getServicePackage().getText();

        if (servName.equals("") || pack.equals("")) {
            return;
        }

        String ns = "";
        StringTokenizer strtok = new StringTokenizer(pack, ".", false);
        while (strtok.hasMoreElements()) {
            String packageItem = strtok.nextToken();
            if (!ns.equals("")) {
                ns = "." + ns;
            }
            ns = packageItem + ns;
        }

        String new_ns = "http://" + ns + "/" + getService().getText();
        getNamespaceDomain().setText(new_ns);
    }


    /**
     * This method initializes jTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getDir() {
        if (dir == null) {
            dir = new JTextField();
            String home = System.getProperty("user.home");
            dir.setText(home + File.separator + ConfigurationUtil.getIntroduceServiceDefaults().getServiceName());
            dir.getDocument().addDocumentListener(new DocumentChangeAdapter() {
                public void documentEdited(DocumentEvent e) {
                    validateInput();
                }
            });
        }
        return dir;
    }


    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getDirButton() {
        if (dirButton == null) {
            dirButton = new JButton();
            dirButton.setText("Browse");
            dirButton.setIcon(IntroduceLookAndFeel.getBrowseIcon());
            dirButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        String previous = getDir().getText();
                        String location = ResourceManager.promptDir(previous);
                        if (location != null && location.length() > 0) {
                            getDir().setText(location);
                        } else {
                            getDir().setText(previous);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }
        return dirButton;
    }


    /**
     * This method initializes servicePackage
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getServicePackage() {
        if (servicePackage == null) {
            servicePackage = new JTextField();
            servicePackage.setText((ConfigurationUtil.getIntroduceServiceDefaults().getServicePackage()).toLowerCase());
            servicePackage.getDocument().addDocumentListener(new DocumentChangeAdapter() {
                public void documentEdited(DocumentEvent e) {
                    updateSuggestedNamespace();
                    validateInput();
                }
            });
        }
        return servicePackage;
    }


    /**
     * This method initializes namespaceDomain
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getNamespaceDomain() {
        if (namespaceDomain == null) {
            namespaceDomain = new JTextField();
            namespaceDomain.setText(ConfigurationUtil.getIntroduceServiceDefaults().getServiceNamespace());
            namespaceDomain.getDocument().addDocumentListener(new DocumentChangeAdapter() {
                public void documentEdited(DocumentEvent e) {
                    validateInput();
                }
            });
        }
        return namespaceDomain;
    }


    /**
     * This method initializes closeButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getCloseButton() {
        if (closeButton == null) {
            closeButton = new JButton();
            closeButton.setIcon(PortalLookAndFeel.getCloseIcon());
            closeButton.setText("Cancel");
            closeButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    dispose();
                }
            });
        }
        return closeButton;
    }


    /**
     * This method initializes extensionsPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getExtensionsPanel() {
        if (extensionsPanel == null) {
            GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
            gridBagConstraints13.gridx = 0;
            gridBagConstraints13.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints13.weightx = 1.0D;
            gridBagConstraints13.weighty = 0.0D;
            gridBagConstraints13.gridy = 0;
            GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
            gridBagConstraints20.gridx = 0;
            gridBagConstraints20.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints20.gridwidth = 3;
            gridBagConstraints20.weightx = 1.0D;
            gridBagConstraints20.weighty = 1.0D;
            gridBagConstraints20.insets = new java.awt.Insets(5, 2, 5, 2);
            gridBagConstraints20.gridy = 1;
            GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
            gridBagConstraints19.gridx = 0;
            gridBagConstraints19.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints19.gridheight = 2;
            gridBagConstraints19.weightx = 1.0D;
            gridBagConstraints19.weighty = 1.0D;
            gridBagConstraints19.gridy = 2;
            extensionsPanel = new JPanel();
            extensionsPanel.setLayout(new GridBagLayout());
            extensionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Service Extensions",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, PortalLookAndFeel.getPanelLabelColor()));
            extensionsPanel.add(getExtSelectionPanel(), gridBagConstraints13);
            extensionsPanel.add(getExtensionsTable(), gridBagConstraints19);
            extensionsPanel.add(getExtensionsTableionsTablePanel(), gridBagConstraints20);
        }
        return extensionsPanel;
    }


    /**
     * This method initializes addExtensionButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddExtensionButton() {
        if (addExtensionButton == null) {
            addExtensionButton = new JButton();
            addExtensionButton.setText("Add");
            addExtensionButton.setIcon(PortalLookAndFeel.getAddIcon());
            addExtensionButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (!((String) getServiceStyleSeletor().getSelectedItem()).equals("NONE")) {
                        getExtensionsTable().addRow((String) getServiceStyleSeletor().getSelectedItem());
                    }
                }
            });
        }
        return addExtensionButton;
    }


    /**
     * This method initializes removeExtensionButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getRemoveExtensionButton() {
        if (removeExtensionButton == null) {
            removeExtensionButton = new JButton();
            removeExtensionButton.setText("Remove");
            removeExtensionButton.setIcon(PortalLookAndFeel.getRemoveIcon());
            removeExtensionButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        getExtensionsTable().removeSelectedRow();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }

            });
        }
        return removeExtensionButton;
    }


    /**
     * This method initializes extensionsScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getExtensionsScrollPane() {
        if (extensionsScrollPane == null) {
            extensionsScrollPane = new JScrollPane();
            extensionsScrollPane.setViewportView(getExtensionsTable());
        }
        return extensionsScrollPane;
    }


    /**
     * This method initializes extensionsTable
     * 
     * @return javax.swing.JTable
     */
    private ExtensionsTable getExtensionsTable() {
        if (extensionsTable == null) {
            extensionsTable = new ExtensionsTable();
            extensionsTable.setMinimumSize(new java.awt.Dimension(100, 150));
        }
        return extensionsTable;
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getExtensionsTableionsTablePanel() {
        if (extensionsTablePanel == null) {
            GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
            gridBagConstraints21.gridx = 1;
            gridBagConstraints21.anchor = java.awt.GridBagConstraints.SOUTHWEST;
            gridBagConstraints21.fill = java.awt.GridBagConstraints.NONE;
            gridBagConstraints21.gridy = 0;
            GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
            gridBagConstraints14.gridx = 1;
            gridBagConstraints14.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints14.gridy = 1;
            downExtensionLabel = new JLabel();
            downExtensionLabel.setToolTipText("Moves the selected extension "
                + "down in the list so that it will be executed after the preceding extensions");
            downExtensionLabel.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    try {
                        getExtensionsTable().moveSelectedRowDown();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            });
            downExtensionLabel.setIcon(IntroduceLookAndFeel.getDownIcon());
            upExtensionLabel = new JLabel();
            upExtensionLabel.setToolTipText("moves the selected extension "
                + "higher in the list so that it will be executed before the following extensions");
            upExtensionLabel.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    try {
                        getExtensionsTable().moveSelectedRowUp();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            });
            upExtensionLabel.setIcon(IntroduceLookAndFeel.getUpIcon());
            GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
            gridBagConstraints18.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints18.gridy = 0;
            gridBagConstraints18.weightx = 1.0;
            gridBagConstraints18.weighty = 1.0;
            gridBagConstraints18.gridheight = 2;
            gridBagConstraints18.gridx = 0;
            extensionsTablePanel = new JPanel();
            extensionsTablePanel.setLayout(new GridBagLayout());
            extensionsTablePanel.add(getExtensionsScrollPane(), gridBagConstraints18);
            extensionsTablePanel.add(upExtensionLabel, gridBagConstraints21);
            extensionsTablePanel.add(downExtensionLabel, gridBagConstraints14);
        }
        return extensionsTablePanel;
    }


    /**
     * This method initializes extSelectionPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getExtSelectionPanel() {
        if (extSelectionPanel == null) {
            GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
            gridBagConstraints22.gridy = 0;
            gridBagConstraints22.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints22.gridx = 1;
            GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
            gridBagConstraints16.gridy = 0;
            gridBagConstraints16.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints16.gridx = 2;
            GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
            gridBagConstraints15.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints15.gridx = 0;
            gridBagConstraints15.gridy = 0;
            gridBagConstraints15.weightx = 1.0;
            extSelectionPanel = new JPanel();
            extSelectionPanel.setLayout(new GridBagLayout());
            extSelectionPanel.add(getServiceStyleSeletor(), gridBagConstraints15);
            extSelectionPanel.add(getRemoveExtensionButton(), gridBagConstraints16);
            extSelectionPanel.add(getAddExtensionButton(), gridBagConstraints22);
        }
        return extSelectionPanel;
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getServiceStylePanel() {
        if (serviceStylePanel == null) {
            GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
            gridBagConstraints17.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints17.gridx = 0;
            gridBagConstraints17.gridy = 0;
            gridBagConstraints17.weightx = 1.0;
            gridBagConstraints17.weighty = 1.0;
            serviceStylePanel = new JPanel();
            serviceStylePanel.setLayout(new GridBagLayout());
            serviceStylePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Customize the service",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, PortalLookAndFeel.getPanelLabelColor()));
            serviceStylePanel.add(getJTabbedPane(), gridBagConstraints17);
        }
        return serviceStylePanel;
    }


    /**
     * This method initializes jTabbedPane
     * 
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getJTabbedPane() {
        if (jTabbedPane == null) {
            jTabbedPane = new JTabbedPane();
            jTabbedPane.addTab("Standard", null, getServiceTyeSelectionPanel(), "Standard configuration options.");
            jTabbedPane.addTab("Advanced", null, getExtensionsPanel(),
                "Provides access to configuration of specific Introduce extensions.");
        }
        return jTabbedPane;
    }


    /**
     * This method initializes serviceTyeSelectionPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getServiceTyeSelectionPanel() {
        if (serviceTyeSelectionPanel == null) {
            GridBagConstraints gridBagConstraints24 = new GridBagConstraints();
            gridBagConstraints24.gridy = 0;
            gridBagConstraints24.ipady = 1;
            gridBagConstraints24.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints24.gridx = 0;
            GridBagConstraints gridBagConstraints23 = new GridBagConstraints();
            gridBagConstraints23.gridy = 1;
            gridBagConstraints23.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints23.gridx = 0;
            serviceTyeSelectionPanel = new JPanel();
            serviceTyeSelectionPanel.setLayout(new GridBagLayout());
            serviceTyeSelectionPanel.add(getDataRadioButton(), gridBagConstraints23);
            serviceTyeSelectionPanel.add(getAnalyticalRadioButton(), gridBagConstraints24);
        }
        return serviceTyeSelectionPanel;
    }


    private ButtonGroup getServiceStyleButtonGroup() {
        if (serviceStyleButtonGroup == null) {
            serviceStyleButtonGroup = new ButtonGroup();
        }
        return serviceStyleButtonGroup;
    }


    /**
     * This method initializes dataRadioButton
     * 
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getDataRadioButton() {
        if (dataRadioButton == null) {
            dataRadioButton = new JRadioButton();
            dataRadioButton.setText("Data Service");
            dataRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    addDataExtensions();
                    addAnalyticalExtensions();
                }
            });
        }
        return dataRadioButton;
    }


    /**
     * This method initializes analyticalRadioButton
     * 
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getAnalyticalRadioButton() {
        if (analyticalRadioButton == null) {
            analyticalRadioButton = new JRadioButton();
            analyticalRadioButton.setText("Analytical Service");
            analyticalRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    removeDataExtensions();
                    addAnalyticalExtensions();
                }
            });
        }
        return analyticalRadioButton;
    }
}
