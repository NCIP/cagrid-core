package gov.nih.nci.cagrid.introduce.portal.modification;

import gov.nih.nci.cagrid.common.portal.MultiEventProgressBar;
import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;
import gov.nih.nci.cagrid.common.portal.PromptButtonDialog;
import gov.nih.nci.cagrid.common.portal.validation.IconFeedbackPanel;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.extension.DiscoveryExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionDescription;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionsType;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespacesType;
import gov.nih.nci.cagrid.introduce.beans.namespace.SchemaElementType;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.codegen.SyncTools;
import gov.nih.nci.cagrid.introduce.common.AntTools;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ConfigurationUtil;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.ExtensionTools;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;
import gov.nih.nci.cagrid.introduce.extension.ServiceExtensionRemover;
import gov.nih.nci.cagrid.introduce.portal.common.IntroduceLookAndFeel;
import gov.nih.nci.cagrid.introduce.portal.extension.ServiceModificationUIPanel;
import gov.nih.nci.cagrid.introduce.portal.modification.discovery.NamespaceTypeDiscoveryComponent;
import gov.nih.nci.cagrid.introduce.portal.modification.extensions.ExtensionsManagerPanel;
import gov.nih.nci.cagrid.introduce.portal.modification.properties.ServicePropertiesTable;
import gov.nih.nci.cagrid.introduce.portal.modification.services.servicetree.MethodButtonPanel;
import gov.nih.nci.cagrid.introduce.portal.modification.services.servicetree.MethodsButtonPanel;
import gov.nih.nci.cagrid.introduce.portal.modification.services.servicetree.ResourcePropertyButtonPanel;
import gov.nih.nci.cagrid.introduce.portal.modification.services.servicetree.ResourcesButtonPanel;
import gov.nih.nci.cagrid.introduce.portal.modification.services.servicetree.ServiceButtonPanel;
import gov.nih.nci.cagrid.introduce.portal.modification.services.servicetree.ServicesButtonPanel;
import gov.nih.nci.cagrid.introduce.portal.modification.services.servicetree.ServicesJTree;
import gov.nih.nci.cagrid.introduce.portal.modification.types.NamespaceTypeConfigurePanel;
import gov.nih.nci.cagrid.introduce.portal.modification.types.NamespaceTypeTreeNode;
import gov.nih.nci.cagrid.introduce.portal.modification.types.NamespacesJTree;
import gov.nih.nci.cagrid.introduce.portal.modification.types.SchemaElementTypeConfigurePanel;
import gov.nih.nci.cagrid.introduce.portal.modification.types.SchemaElementTypeTreeNode;
import gov.nih.nci.cagrid.introduce.portal.modification.types.SchemaElementTypeValidator;
import gov.nih.nci.cagrid.introduce.portal.modification.upgrade.UpgradeStatusView;
import gov.nih.nci.cagrid.introduce.upgrade.UpgradeManager;
import gov.nih.nci.cagrid.introduce.upgrade.common.UpgradeStatus;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.Logger;
import org.cagrid.grape.ApplicationComponent;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.model.RenderOptions;
import org.cagrid.grape.utils.BusyDialog;
import org.cagrid.grape.utils.BusyDialogRunnable;
import org.cagrid.grape.utils.CompositeErrorDialog;
import org.cagrid.grape.utils.ErrorDialog;

import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationMessage;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.util.ValidationUtils;
import com.jgoodies.validation.view.ValidationComponentUtils;


/**
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 */
public class ModificationViewer extends ApplicationComponent {
    private static final Logger logger = Logger.getLogger(ModificationViewer.class);

    private JPanel mainPanel = null;

    private JPanel buttonPanel = null;

    private JPanel selectPanel = null;

    private File methodsDirectory = null;

    private JButton saveButton = null;

    private JButton undoButton = null;

    private JButton reloadButton = null;

    private boolean dirty = false;

    private JTabbedPane contentTabbedPane = null;

    private JLabel serviceNameLabel = null;

    private JLabel serviceName = null;

    private JLabel namespaceLable = null;

    private JLabel namespace = null;

    private JLabel lastSavedLabel = null;

    private JLabel lastSaved = null;

    private JLabel saveLocationLabel = null;

    private JLabel saveLocation = null;

    private JPanel discoveryPanel = null;

    private JPanel discoveryButtonPanel = null;

    private JButton namespaceAddButton = null;

    private JButton namespaceRemoveButton = null;

    private JScrollPane namespaceTableScrollPane = null;

    private NamespacesJTree namespaceJTree = null;

    private JPanel namespaceTypePropertiesPanel = null;

    private NamespaceTypeConfigurePanel namespaceTypeConfigurationPanel = null;

    private SchemaElementTypeConfigurePanel schemaElementTypeConfigurationPanel = null;

    private ServiceInformation info = null;

    private JTabbedPane discoveryTabbedPane = null;

    private JPanel namespaceConfPanel = null;

    private JPanel servicePropertiesPanel = null;

    private JPanel servicePropertiesTableContainerPanel = null;

    private JScrollPane servicePropertiesTableScrollPane = null;

    private ServicePropertiesTable servicePropertiesTable = null;

    private JPanel servicePropertiesControlPanel = null;

    private JButton addServiceProperyButton = null;

    private JButton removeServicePropertyButton = null;

    private JTextField servicePropertyKeyTextField = null;

    private JTextField servicePropertyValueTextField = null;

    private JLabel servicePropertiesKeyLabel = null;

    private JLabel servicePropertiesValueLabel = null;

    private JPanel servicePropertiesButtonPanel = null;

    private JPanel resourceesTabbedPanel = null;

    private JPanel resourcesPanel = null;

    private JScrollPane resourcesScrollPane = null;

    private ServicesJTree resourcesJTree = null;

    private JSplitPane typesSplitPane = null;

    private List extensionPanels = null;

    private List discoveryPanels = null;

    private JCheckBox propertyIsFromETCCheckBox = null;

    private JPanel resourcesOptionsPanel = null;

    private JLabel descriptionLabel = null;

    private JTextField servicePropertyDescriptionTextField = null;

    private boolean beenDisposed = false;

    private ValidationResultModel servicePropertiesValidation = new DefaultValidationResultModel();

    private static final String SERVICE_PROPERTY_KEY = "Service property key";

    private static final String SERVICE_PROPERTY_VALUE = "Service property default value";

    private static final String SERVICE_PROPERTY_DESCRIPTION = "Service property description";

    private ExtensionsManagerPanel extensionsPanel = null;

    private JTabbedPane namespaceManageTabbedPane = null;


    public ModificationViewer(File methodsDirectory, BusyDialogRunnable br) throws Exception {
        super();
        this.extensionPanels = new ArrayList();
        this.discoveryPanels = new ArrayList();
        this.methodsDirectory = methodsDirectory;
        initialize(br);
        if (beenDisposed) {
            throw new Exception("Unable to modify service at " + this.methodsDirectory.getAbsolutePath());
        }
    }


    public ModificationViewer(File methodsDirectory) throws Exception {
        super();
        this.extensionPanels = new ArrayList();
        this.discoveryPanels = new ArrayList();
        this.methodsDirectory = methodsDirectory;
        try {

            BusyDialogRunnable br = new BusyDialogRunnable((JFrame) GridApplication.getContext().getApplication(),
                "Modification Viewer Initializing") {

                @Override
                public void process() {
                    try {
                        initialize(this);
                    } catch (Exception e) {
                        logger.error(e);
                    }
                }

            };
            Thread th = new Thread(br);
            th.start();
            th.join();

        } catch (Exception e) {
            logger.error(e);
        }
        if (beenDisposed) {
            throw new Exception("Unable to modify service or service modification exited on service at "
                + this.methodsDirectory.getAbsolutePath());
        }
    }


    public void reInitialize(File serviceDir) throws Exception {
        this.methodsDirectory = serviceDir;
        this.initialize(null);
        this.reInitializeGUI();
    }


    private void reInitializeGUI() throws Exception {
        setLastSaved(this.info.getIntroduceServiceProperties().getProperty(
            IntroduceConstants.INTRODUCE_SKELETON_TIMESTAMP));
        getNamespaceJTree().setNamespaces(this.info.getNamespaces());
        getResourcesJTree().setServices(info);
        getExtensionsPanel().reInitialize(this.info);
        getServicePropertiesTable().setServiceInformation(this.info);
        for (int i = 0; i < this.extensionPanels.size(); i++) {
            ServiceModificationUIPanel panel = (ServiceModificationUIPanel) this.extensionPanels.get(i);
            panel.setServiceInfo(this.info);
        }
        for (int i = 0; i < this.discoveryPanels.size(); i++) {
            NamespaceTypeDiscoveryComponent comp = (NamespaceTypeDiscoveryComponent) this.discoveryPanels.get(i);
            comp.setCurrentNamespaces(this.info.getNamespaces());
        }

        // repaint the component that was selected before the save
        this.repaint();
    }


    /**
     * This method initializes this viewer component
     */
    private void initialize(BusyDialogRunnable dialog) throws Exception {
        if (this.methodsDirectory != null) {
            try {

                this.info = new ServiceInformation(this.methodsDirectory);
                Properties introduceServiceProperties = this.info.getIntroduceServiceProperties();

                String extensionsProp = introduceServiceProperties
                    .getProperty(IntroduceConstants.INTRODUCE_SKELETON_EXTENSIONS);
                StringTokenizer strtok = new StringTokenizer(extensionsProp, ",", false);

                while (strtok.hasMoreElements()) {
                    String extensionName = strtok.nextToken();
                    extensionName = extensionName.trim();
                    ServiceExtensionDescriptionType extDtype = ExtensionsLoader.getInstance().getServiceExtension(
                        extensionName);
                    if (extDtype == null) {
                        JOptionPane.showMessageDialog(GridApplication.getContext().getApplication(),
                            "ERROR: This service requires the " + extensionName + " extension to be installed.");
                        ModificationViewer.this.dispose();
                        this.beenDisposed = true;
                    }
                }

            } catch (Exception e1) {
                e1.printStackTrace();
                ModificationViewer.this.dispose();
                this.beenDisposed = true;
            }

            if (!beenDisposed) {

                // check the service for upgrades
                if (dialog != null) {
                    dialog.setProgressText("Checking introduce version of service");
                }

                UpgradeManager upgrader = new UpgradeManager(this.methodsDirectory.getAbsolutePath());
                if (upgrader.introduceNeedsUpgraded() && !upgrader.canIntroduceBeUpgraded()) {

                    JOptionPane.showMessageDialog(GridApplication.getContext().getApplication(),
                        "Service was built with another version of Introduce and no upgrader currently exists");
                    ModificationViewer.this.dispose();
                    this.beenDisposed = true;

                } else if (upgrader.canIntroduceBeUpgraded() || upgrader.extensionsNeedUpgraded()) {
                    PromptButtonDialog diag = new PromptButtonDialog(
                        GridApplication.getContext().getApplication(),
                        "Upgrade?",
                        new String[]{
                                "",
                                "This service is from an older of version of Introduce or uses an older version of an extension.",
                                "Would you like to try to upgrade this service to work with the current version of Introduce and installed extensions?\n",
                                "",
                                "Upgrade: Yes I would like to upgrade my service to be able to work with the currently installed tools.",
                                "Open: Introduce will attempt to open and work with this service.  This is very dangerous.",
                                "Close: Do nothing and close the modification viewer.", ""}, new String[]{"Upgrade",
                                "Open", "Close"}, "Close");
                    GridApplication.getContext().showDialog(diag);
                    String result = diag.getSelection();

                    if (result != null && result.equals("Upgrade")) {
                        try {
                            if (dialog != null) {
                                dialog.setProgressText("Upgrading service");
                            }

                            // check extensions for deprecation or removal.
                            String extensionsProp = info.getIntroduceServiceProperties().getProperty(
                                IntroduceConstants.INTRODUCE_SKELETON_EXTENSIONS);
                            StringTokenizer strtok = new StringTokenizer(extensionsProp, ",", false);
                            strtok = new StringTokenizer(extensionsProp, ",", false);
                            String newExtension = "";
                            while (strtok.hasMoreElements()) {
                                String extensionName = strtok.nextToken();
                                extensionName = extensionName.trim();
                                ServiceExtensionDescriptionType extDtype = ExtensionsLoader.getInstance()
                                    .getServiceExtension(extensionName);

                                if (extDtype.getShouldBeRemoved()!=null && extDtype.getShouldBeRemoved().booleanValue()) {
                                    if (extDtype.getServiceExtensionRemover() != null) {
                                        PromptButtonDialog diag2 = new PromptButtonDialog(
                                            GridApplication.getContext().getApplication(),
                                            "Ignore?",
                                            new String[]{
                                                    "",
                                                    "WARNING: This service uses the "
                                                        + extensionName
                                                        + "  and this extension is no longer supported and is scheduled to be removed.",
                                                    "Ignore: Ignore this extension in the future but do not remove it.",
                                                    "Remove: Let this extension remove itself from the service if it can.",
                                                    ""}, new String[]{"Ignore", "Remove"}, "Remove");
                                        GridApplication.getContext().showDialog(diag2);
                                        String result2 = diag2.getSelection();
                                        if (result2.equals("Ignore")) {
                                            // need to remove this extension
                                            // from
                                            // the extensions list
                                            ExtensionType[] modifiedExtensionsArray = new ExtensionType[info
                                                .getExtensions().getExtension().length - 1];
                                            int kept = 0;
                                            for (int i = 0; i < info.getExtensions().getExtension().length; i++) {
                                                ExtensionType extType = info.getExtensions().getExtension(i);
                                                if (!extType.getName().equals(extensionName)) {
                                                    modifiedExtensionsArray[kept++] = extType;
                                                }
                                            }
                                        } else if (result2.equals("Remove")) {
                                            // need to call the remover and
                                            // remove
                                            // the extension
                                            ServiceExtensionRemover remover = ExtensionTools
                                                .getServiceExtensionRemover(extensionName);
                                            if (remover != null) {
                                                remover.remove(ExtensionsLoader.getInstance().getServiceExtension(
                                                    extensionName), info);
                                            }
                                            ExtensionType[] modifiedExtensionsArray = new ExtensionType[info
                                                .getExtensions().getExtension().length - 1];
                                            int kept = 0;
                                            for (int i = 0; i < info.getExtensions().getExtension().length; i++) {
                                                ExtensionType extType = info.getExtensions().getExtension(i);
                                                if (!extType.getName().equals(extensionName)) {
                                                    modifiedExtensionsArray[kept++] = extType;
                                                }
                                            }
                                        }
                                    } else {
                                        JOptionPane
                                            .showMessageDialog(
                                                GridApplication.getContext().getApplication(),
                                                "WARNING: This service uses the "
                                                    + extensionName
                                                    + "  and this extension is no longer supported and will no longer be processed");

                                        ExtensionType[] modifiedExtensionsArray = new ExtensionType[info
                                            .getExtensions().getExtension().length - 1];
                                        int kept = 0;
                                        for (int i = 0; i < info.getExtensions().getExtension().length; i++) {
                                            ExtensionType extType = info.getExtensions().getExtension(i);
                                            if (!extType.getName().equals(extensionName)) {
                                                modifiedExtensionsArray[kept++] = extType;
                                            }
                                        }
                                    }

                                    if (newExtension.length() > 0) {
                                        newExtension += ",";
                                    }
                                    newExtension += extensionName;

                                } else if (extDtype.getIsDeprecated().booleanValue()) {
                                    JOptionPane
                                        .showMessageDialog(
                                            GridApplication.getContext().getApplication(),
                                            "WARNING: This service uses the "
                                                + extensionName
                                                + "  and this extension is deprecated and may not be supported by future versions of Introduce.");
                                }

                            }

                            info.getIntroduceServiceProperties().setProperty(
                                IntroduceConstants.INTRODUCE_SKELETON_EXTENSIONS, newExtension);
                            info.persistInformation();
                            info = null;

                            UpgradeStatus status = upgrader.upgrade();
                            logger.info("SERVICE UPGRADE STATUS:\n" + status);
                            int answer = UpgradeStatusView.showUpgradeStatusView(status);
                            if (answer == UpgradeStatusView.PROCEED) {

                            } else if (answer == UpgradeStatusView.ROLL_BACK) {
                                upgrader.recover();
                                ModificationViewer.this.dispose();
                                this.beenDisposed = true;
                            } else if (answer == UpgradeStatusView.CANCEL) {
                                ModificationViewer.this.dispose();
                                this.beenDisposed = true;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            int answer = JOptionPane.showConfirmDialog(GridApplication.getContext().getApplication(),
                                "The service had the following fatal error during the upgrade process:\n"
                                    + e.getMessage()
                                    + "\nIf you select OK, Introduce will roll your service back to its previous\n"
                                    + "state before the upgrade attempt", "Error upgrading service",
                                JOptionPane.OK_CANCEL_OPTION);
                            if (answer == JOptionPane.OK_OPTION) {
                                try {
                                    if (dialog != null) {
                                        dialog.setProgressText("Rolling back upgrade changes");
                                    }
                                    upgrader.recover();
                                } catch (Exception ex) {
                                    ErrorDialog.showError(e);
                                }
                                ModificationViewer.this.dispose();
                                this.beenDisposed = true;
                            } else {
                                ModificationViewer.this.dispose();
                                this.beenDisposed = true;
                            }
                        }
                    } else if (result == null || result.equals("Close")) {
                        ModificationViewer.this.dispose();
                        this.beenDisposed = true;
                    }
                }

                if (!beenDisposed) {
                    // reload the info incase it has changed during
                    // upgrading.....
                    try {
                        if (dialog != null) {
                            dialog.setProgressText("loading service description");
                        }
                        this.info = new ServiceInformation(this.methodsDirectory);
                    } catch (Exception e) {
                        CompositeErrorDialog.showErrorDialog(e);
                        ModificationViewer.this.dispose();
                        this.beenDisposed = true;
                    }

                    setContentPane(getMainPanel());
                    setTitle("Modify Service Interface");
                    setFrameIcon(IntroduceLookAndFeel.getModifyServiceIcon());

                    initServicePropertyValidation();

                }

            }

        }
    }


    private void initServicePropertyValidation() {
        ValidationComponentUtils.setMessageKey(getServicePropertyKeyTextField(), SERVICE_PROPERTY_KEY);
        ValidationComponentUtils.setMessageKey(getServicePropertyValueTextField(), SERVICE_PROPERTY_VALUE);
        ValidationComponentUtils.setMessageKey(getServicePropertyDescriptionTextField(), SERVICE_PROPERTY_DESCRIPTION);

        validateServicePropertiesInput();
        updateServicePropertyComponentTreeSeverity();
    }


    private void updateServicePropertyComponentTreeSeverity() {
        ValidationComponentUtils.updateComponentTreeMandatoryAndBlankBackground(this);
        ValidationComponentUtils.updateComponentTreeSeverityBackground(this, this.servicePropertiesValidation
            .getResult());
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainPanel() {
        if (this.mainPanel == null) {
            GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
            gridBagConstraints13.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints13.weighty = 1.0;
            gridBagConstraints13.gridx = 0;
            gridBagConstraints13.gridy = 1;
            gridBagConstraints13.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints13.weightx = 1.0;
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.anchor = java.awt.GridBagConstraints.SOUTH;
            gridBagConstraints11.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints11.gridheight = 0;
            gridBagConstraints11.gridwidth = 0;
            gridBagConstraints11.gridx = 0;
            gridBagConstraints11.gridy = 3;
            gridBagConstraints11.weighty = 1.0D;
            gridBagConstraints11.fill = java.awt.GridBagConstraints.HORIZONTAL;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            this.mainPanel = new JPanel();
            this.mainPanel.setLayout(new GridBagLayout());
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.gridy = 2;
            gridBagConstraints2.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints2.anchor = java.awt.GridBagConstraints.SOUTH;
            gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.gridy = 0;
            gridBagConstraints3.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints3.anchor = java.awt.GridBagConstraints.NORTH;
            gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
            this.mainPanel.add(getButtonPanel(), gridBagConstraints2);
            this.mainPanel.add(getSelectPanel(), gridBagConstraints3);
            mainPanel.add(getContentTabbedPane(), gridBagConstraints13);
        }
        return this.mainPanel;
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel() {
        if (this.buttonPanel == null) {
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.insets = new java.awt.Insets(5, 5, 5, 5);
            gridBagConstraints9.gridy = 0;
            gridBagConstraints9.gridx = 2;
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.insets = new java.awt.Insets(5, 5, 5, 5);
            gridBagConstraints8.gridy = 0;
            gridBagConstraints8.gridx = 1;
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.insets = new java.awt.Insets(5, 5, 5, 5);
            gridBagConstraints11.gridy = 0;
            gridBagConstraints11.gridx = 0;
            this.buttonPanel = new JPanel();
            this.buttonPanel.setLayout(new GridBagLayout());
            this.buttonPanel.add(getSaveButton(), gridBagConstraints9);
            this.buttonPanel.add(getReloadButton(), gridBagConstraints8);
            this.buttonPanel.add(getUndoButton(), gridBagConstraints11);
        }
        return this.buttonPanel;
    }


    private JButton getReloadButton() {
        if (this.reloadButton == null) {
            this.reloadButton = new JButton(IntroduceLookAndFeel.getResyncIcon());
            this.reloadButton.setText("Reload");
            this.reloadButton.setToolTipText("reload the service and throw away the current modifications");
            this.reloadButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    int decision = JOptionPane.showConfirmDialog(ModificationViewer.this,
                        "Are you sure you wish to reload?\n" + "All current modifactions will be lost!\n"
                            + "This will simply reload the modification viewer with the\n"
                            + "service without saving the current changes since the last save.", "Are you sure?",
                        JOptionPane.YES_NO_OPTION);
                    if (decision == JOptionPane.OK_OPTION) {
                        BusyDialogRunnable r = new BusyDialogRunnable(GridApplication.getContext().getApplication(),
                            "Reload") {
                            @Override
                            public void process() {
                                logger.info("Reloading service");
                                setProgressText("Reloading service");
                                dispose();
                                RenderOptions ro = new RenderOptions();
                                ro.setMaximized(true);
                                try {
                                    GridApplication.getContext().getApplication().addApplicationComponent(
                                        new ModificationViewer(ModificationViewer.this.methodsDirectory), null, ro);
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    beenDisposed = true;
                                    e.printStackTrace();
                                }
                            }
                        };
                        Thread th = new Thread(r);
                        th.start();
                    }
                }
            });
        }
        return this.reloadButton;
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getSelectPanel() {
        if (this.selectPanel == null) {
            GridBagConstraints gridBagConstraints24 = new GridBagConstraints();
            gridBagConstraints24.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints24.gridy = 1;
            gridBagConstraints24.weightx = 1.0;
            gridBagConstraints24.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints24.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints24.gridx = 3;
            GridBagConstraints gridBagConstraints23 = new GridBagConstraints();
            gridBagConstraints23.gridx = 2;
            gridBagConstraints23.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints23.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints23.gridy = 1;
            this.saveLocationLabel = new JLabel();
            this.saveLocationLabel.setText("Location: ");
            this.saveLocationLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 12));
            GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
            gridBagConstraints22.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints22.gridy = 1;
            gridBagConstraints22.weightx = 1.0;
            gridBagConstraints22.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints22.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints22.gridx = 1;
            GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
            gridBagConstraints21.gridx = 0;
            gridBagConstraints21.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints21.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints21.gridy = 1;
            this.lastSavedLabel = new JLabel();
            this.lastSavedLabel.setText("Last Saved: ");
            this.lastSavedLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 12));
            GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
            gridBagConstraints20.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints20.gridy = 0;
            gridBagConstraints20.weightx = 1.0;
            gridBagConstraints20.gridx = 3;
            gridBagConstraints20.insets = new java.awt.Insets(2, 2, 2, 2);
            GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
            gridBagConstraints19.gridx = 2;
            gridBagConstraints19.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints19.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints19.gridy = 0;
            this.namespaceLable = new JLabel();
            this.namespaceLable.setText("Namespace: ");
            this.namespaceLable.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 12));
            GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
            gridBagConstraints18.gridx = 0;
            gridBagConstraints18.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints18.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints18.gridy = 0;
            GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
            gridBagConstraints17.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints17.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints17.gridx = 1;
            gridBagConstraints17.gridy = 0;
            gridBagConstraints17.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints17.weightx = 1.0;
            this.serviceNameLabel = new JLabel();
            this.serviceNameLabel.setText("Service Name: ");
            this.serviceNameLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 12));
            this.selectPanel = new JPanel();
            this.selectPanel.setLayout(new GridBagLayout());
            this.selectPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Properties",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, PortalLookAndFeel.getPanelLabelColor()));
            this.selectPanel.add(this.serviceNameLabel, gridBagConstraints18);
            this.selectPanel.add(getServiceName(), gridBagConstraints17);
            this.selectPanel.add(this.namespaceLable, gridBagConstraints19);
            this.selectPanel.add(getNamespace(), gridBagConstraints20);
            this.selectPanel.add(this.lastSavedLabel, gridBagConstraints21);
            this.selectPanel.add(getLastSaved(), gridBagConstraints22);
            this.selectPanel.add(this.saveLocationLabel, gridBagConstraints23);
            this.selectPanel.add(getSaveLocation(), gridBagConstraints24);
        }
        return this.selectPanel;
    }


    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getSaveButton() {
        if (this.saveButton == null) {
            this.saveButton = new JButton(PortalLookAndFeel.getSaveIcon());
            this.saveButton.setText("Save");
            this.saveButton.setToolTipText("modify and rebuild service");
            this.saveButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    saveModifications();
                }
            });
        }
        return this.saveButton;
    }


    /**
     * This method initializes undoButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getUndoButton() {
        if (this.undoButton == null) {
            this.undoButton = new JButton(IntroduceLookAndFeel.getUndoIcon());
            this.undoButton.setText("Restore");
            this.undoButton.setToolTipText("restore back to last save state");
            this.undoButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    RestoreDialog dialog = new RestoreDialog(info.getServices().getService(0).getName(), info
                        .getBaseDirectory().getAbsolutePath());
                    dialog.setVisible(true);
                    if (!dialog.wasCanceled()) {
                        BusyDialogRunnable r = new BusyDialogRunnable(GridApplication.getContext().getApplication(),
                            "Reloading") {
                            @Override
                            public void process() {

                                try {
                                    setProgressText("re-initializing modification viewer");
                                    dispose();
                                    GridApplication.getContext().getApplication().addApplicationComponent(
                                        new ModificationViewer(ModificationViewer.this.methodsDirectory));
                                } catch (Exception e1) {
                                    // e1.printStackTrace();
                                    ErrorDialog
                                        .showError("Unable to roll back, there may be no older versions available");
                                    return;
                                }
                            }
                        };
                        Thread th = new Thread(r);
                        th.start();
                    }
                }
            });

        }
        return this.undoButton;
    }


    /**
     * This method initializes contentTabbedPane
     * 
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getContentTabbedPane() {
        if (this.contentTabbedPane == null) {
            this.contentTabbedPane = new JTabbedPane();
            // this.contentTabbedPane.setTabPlacement(SwingConstants.LEFT);
            this.contentTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
            this.contentTabbedPane.addTab("Types", IntroduceLookAndFeel.getDiscoveryToolsIcon(), getTypesSplitPane(),
                null);
            this.contentTabbedPane.addTab("Services", IntroduceLookAndFeel.getServiceIcon(),
                getResourceesTabbedPanel(), null);
            this.contentTabbedPane.addTab("Service Properties", IntroduceLookAndFeel.getServicePropertiesIcon(),
                getServicePropertiesPanel(), null);
            this.contentTabbedPane.addTab("Extensions", IntroduceLookAndFeel.getExtensionIcon(), getExtensionsPanel(),
                null);
            // add a tab for each extension...
            ExtensionsType exts = this.info.getExtensions();
            if ((exts != null) && (exts.getExtension() != null)) {
                ExtensionType[] extsTypes = exts.getExtension();
                for (ExtensionType element : extsTypes) {
                    ServiceExtensionDescriptionType extDtype = ExtensionsLoader.getInstance().getServiceExtension(
                        element.getName());
                    try {
                        if ((extDtype.getServiceModificationUIPanel() != null)
                            && !extDtype.getServiceModificationUIPanel().equals("")) {
                            ServiceModificationUIPanel extPanel = gov.nih.nci.cagrid.introduce.portal.extension.tools.ExtensionTools
                                .getServiceModificationUIPanel(extDtype.getName(), this.info);
                            this.extensionPanels.add(extPanel);
                            this.contentTabbedPane
                                .addTab(extDtype.getDisplayName(), extPanel.getIcon(), extPanel, null);
                        }
                    } catch (Exception e) {
                        CompositeErrorDialog.showErrorDialog("Cannot load extension: " + extDtype.getDisplayName(), e);
                    }
                }
            }

            this.contentTabbedPane.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {

                    try {

                        switch (contentTabbedPane.getSelectedIndex()) {
                            case 0 :
                                getNamespaceJTree().setNamespaces(info.getNamespaces());
                                break;
                            case 1 :
                                getResourcesJTree().setServices(info);
                                break;
                            case 2 :
                                getServicePropertiesTable().setServiceInformation(info);
                                break;
                            case 3 :
                                break;
                            default :
                                for (int i = 0; i < extensionPanels.size(); i++) {
                                    ServiceModificationUIPanel panel = (ServiceModificationUIPanel) extensionPanels
                                        .get(i);
                                    panel.setServiceInfo(info);
                                }
                                break;
                        }

                        // reInitializeGUI();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }
        return this.contentTabbedPane;
    }


    /**
     * This method initializes serviceName
     * 
     * @return javax.swing.JTextField
     */
    private JLabel getServiceName() {
        if (this.serviceName == null) {
            this.serviceName = new JLabel();
            // this.serviceName.setFont(new java.awt.Font("Dialog",
            // java.awt.Font.ITALIC, 12));
            this.serviceName.setForeground(IntroduceLookAndFeel.getPanelLabelColor());
            this.serviceName.setText(this.info.getIntroduceServiceProperties().getProperty(
                IntroduceConstants.INTRODUCE_SKELETON_SERVICE_NAME));
        }
        return this.serviceName;
    }


    /**
     * This method initializes packageName
     * 
     * @return javax.swing.JTextField
     */
    private JLabel getNamespace() {
        if (this.namespace == null) {
            this.namespace = new JLabel();
            this.namespace.setText(this.info.getIntroduceServiceProperties().getProperty(
                IntroduceConstants.INTRODUCE_SKELETON_NAMESPACE_DOMAIN));
            // this.namespace.setFont(new java.awt.Font("Dialog",
            // java.awt.Font.ITALIC, 12));
            this.namespace.setForeground(IntroduceLookAndFeel.getPanelLabelColor());
        }
        return this.namespace;
    }


    /**
     * This method initializes lastSaved
     * 
     * @return javax.swing.JTextField
     */
    private JLabel getLastSaved() {
        if (this.lastSaved == null) {
            this.lastSaved = new JLabel();
            // this.lastSaved.setFont(new java.awt.Font("Dialog",
            // java.awt.Font.ITALIC, 12));
            this.lastSaved.setForeground(IntroduceLookAndFeel.getPanelLabelColor());
            setLastSaved(this.info.getIntroduceServiceProperties().getProperty(
                IntroduceConstants.INTRODUCE_SKELETON_TIMESTAMP));
        }
        return this.lastSaved;
    }


    private void setLastSaved(String savedDate) {
        Date date;
        if (savedDate.equals("0")) {
            date = new Date();
        } else {
            date = new Date(Long.parseLong(savedDate));
        }
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        this.lastSaved.setText(formatter.format(date));
    }


    /**
     * This method initializes location
     * 
     * @return javax.swing.JTextField
     */
    private JLabel getSaveLocation() {
        if (this.saveLocation == null) {
            this.saveLocation = new JLabel();
            this.saveLocation.setText(this.methodsDirectory.getAbsolutePath());
            this.saveLocation.setForeground(IntroduceLookAndFeel.getPanelLabelColor());
            // this.saveLocation.setFont(new java.awt.Font("Dialog",
            // java.awt.Font.ITALIC, 12));
        }
        return this.saveLocation;
    }


    /**
     * This method initializes discoveryPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getDiscoveryPanel() {
        if (this.discoveryPanel == null) {
            GridBagConstraints gridBagConstraints27 = new GridBagConstraints();
            gridBagConstraints27.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints27.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints27.gridx = 0;
            gridBagConstraints27.gridy = 1;
            GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
            gridBagConstraints16.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints16.weighty = 1.0;
            gridBagConstraints16.weightx = 1.0;
            gridBagConstraints16.gridx = 0;
            gridBagConstraints16.gridy = 0;
            gridBagConstraints16.insets = new java.awt.Insets(2, 2, 2, 2);
            this.discoveryPanel = new JPanel();
            this.discoveryPanel.setLayout(new GridBagLayout());
            this.discoveryPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Import Data Types",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
                PortalLookAndFeel.getPanelLabelColor()));
            this.discoveryPanel.add(getDiscoveryTabbedPane(), gridBagConstraints16);
            this.discoveryPanel.add(getDiscoveryButtonPanel(), gridBagConstraints27);

        }
        return this.discoveryPanel;
    }


    /**
     * This method initializes discoveryButtonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getDiscoveryButtonPanel() {
        if (this.discoveryButtonPanel == null) {
            GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
            gridBagConstraints31.gridx = 1;
            gridBagConstraints31.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints31.gridy = 0;
            GridBagConstraints gridBagConstraints30 = new GridBagConstraints();
            gridBagConstraints30.gridx = 0;
            gridBagConstraints30.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints30.gridy = 0;
            this.discoveryButtonPanel = new JPanel();
            this.discoveryButtonPanel.setLayout(new GridBagLayout());
            this.discoveryButtonPanel.add(getNamespaceAddButton(), gridBagConstraints30);
            this.discoveryButtonPanel.add(getNamespaceRemoveButton(), gridBagConstraints31);
        }
        return this.discoveryButtonPanel;
    }


    /**
     * This method initializes namespaceAddButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getNamespaceAddButton() {
        if (this.namespaceAddButton == null) {
            this.namespaceAddButton = new JButton();
            this.namespaceAddButton.setText("Add");
            this.namespaceAddButton.setIcon(IntroduceLookAndFeel.getPlusIcon());
            this.namespaceAddButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {

                    ModificationViewer.this.namespaceAddButton.setEnabled(false);

                    final MultiEventProgressBar progBar = new MultiEventProgressBar(false);
                    BusyDialog dialog = new BusyDialog((JFrame) SwingUtilities.getRoot(ModificationViewer.this),
                        "Adding types to service", progBar);
                    BusyDialogRunnable runner = new BusyDialogRunnable(dialog) {

                        @Override
                        public void process() {
                            try {
                                addNamespace(progBar);
                            } finally {
                                ModificationViewer.this.namespaceAddButton.setEnabled(true);
                            }
                        }
                    };
                    Thread t = new Thread(runner);
                    t.start();
                }
            });
        }
        return this.namespaceAddButton;
    }


    /**
     * This method initializes namespaceRemoveButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getNamespaceRemoveButton() {
        if (this.namespaceRemoveButton == null) {
            this.namespaceRemoveButton = new JButton();
            this.namespaceRemoveButton.setText("Remove");
            this.namespaceRemoveButton.setIcon(IntroduceLookAndFeel.getSubtractIcon());
            this.namespaceRemoveButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    removeNamespace();
                }
            });
        }
        return this.namespaceRemoveButton;
    }


    /**
     * This method initializes namespaceTableScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getNamespaceTableScrollPane() {
        if (this.namespaceTableScrollPane == null) {
            this.namespaceTableScrollPane = new JScrollPane();
            namespaceTableScrollPane.setBorder(BorderFactory.createTitledBorder(null, "Imported Data Types",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12),
                IntroduceLookAndFeel.getPanelLabelColor()));
            this.namespaceTableScrollPane.setViewportView(getNamespaceJTree());
        }
        return this.namespaceTableScrollPane;
    }


    /**
     * This method initializes namespaceJTree
     * 
     * @return javax.swing.JTree
     */
    private NamespacesJTree getNamespaceJTree() {
        if (this.namespaceJTree == null) {
            this.namespaceJTree = new NamespacesJTree(this.info.getNamespaces(), true);
            this.namespaceJTree.setVisibleRowCount(10);
            this.namespaceJTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
                public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
                    DefaultMutableTreeNode node = getNamespaceJTree().getCurrentNode();
                    if (node instanceof NamespaceTypeTreeNode) {
                        getNamespaceTypeConfigurationPanel().setNamespaceType((NamespaceType) node.getUserObject());
                        getSchemaElementTypeConfigurationPanel().clear();
                        getSchemaElementTypeConfigurationPanel().setHide(true);
                    } else if (node instanceof SchemaElementTypeTreeNode) {
                        NamespaceTypeTreeNode parentNode = (NamespaceTypeTreeNode) node.getParent();
                        NamespaceType nsType = (NamespaceType) parentNode.getUserObject();
                        if (nsType.getNamespace().equals(IntroduceConstants.W3CNAMESPACE)) {
                            getSchemaElementTypeConfigurationPanel().setHide(true);
                            getSchemaElementTypeConfigurationPanel().setSchemaElementType(
                                (SchemaElementType) node.getUserObject(), false);
                        } else {
                            getSchemaElementTypeConfigurationPanel().setHide(
                                (((NamespaceType) ((NamespaceTypeTreeNode) node.getParent()).getUserObject())
                                    .getGenerateStubs() == null)
                                    || (((NamespaceType) ((NamespaceTypeTreeNode) node.getParent()).getUserObject())
                                        .getGenerateStubs().booleanValue()));
                            getSchemaElementTypeConfigurationPanel().setSchemaElementType(
                                (SchemaElementType) node.getUserObject(), true);
                        }
                        getNamespaceTypeConfigurationPanel().setNamespaceType(nsType);
                    } else {
                        getNamespaceTypeConfigurationPanel().clear();
                        getSchemaElementTypeConfigurationPanel().clear();
                    }
                }
            });
        }
        return this.namespaceJTree;
    }


    /**
     * This method initializes namespaceTypePropertiesPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getNamespaceTypePropertiesPanel() {
        if (this.namespaceTypePropertiesPanel == null) {
            GridBagConstraints gridBagConstraints33 = new GridBagConstraints();
            gridBagConstraints33.gridx = 0;
            gridBagConstraints33.weightx = 1.0D;
            gridBagConstraints33.gridy = 0;
            gridBagConstraints33.fill = GridBagConstraints.BOTH;
            GridBagConstraints gridBagConstraints34 = new GridBagConstraints();
            gridBagConstraints34.gridx = 0;
            gridBagConstraints34.weightx = 1.0D;
            gridBagConstraints34.gridy = 1;
            gridBagConstraints34.fill = GridBagConstraints.BOTH;
            this.namespaceTypePropertiesPanel = new JPanel();
            this.namespaceTypePropertiesPanel.setLayout(new GridBagLayout());
            this.namespaceTypePropertiesPanel.add(getNamespaceTypeConfigurationPanel(), gridBagConstraints33);
            this.namespaceTypePropertiesPanel.add(getSchemaElementTypeConfigurationPanel(), gridBagConstraints34);
        }
        return this.namespaceTypePropertiesPanel;
    }


    /**
     * This method initializes namespaceTypeCconfigurationPanel
     * 
     * @return javax.swing.JPanel
     */
    private NamespaceTypeConfigurePanel getNamespaceTypeConfigurationPanel() {
        if (this.namespaceTypeConfigurationPanel == null) {
            this.namespaceTypeConfigurationPanel = new NamespaceTypeConfigurePanel(
                getSchemaElementTypeConfigurationPanel());
            this.namespaceTypeConfigurationPanel.setName("namespaceTypeConfigurationPanel");
        }
        return this.namespaceTypeConfigurationPanel;
    }


    /**
     * This method initializes schemaElementTypeConfigurationPanel
     * 
     * @return javax.swing.JPanel
     */
    private SchemaElementTypeConfigurePanel getSchemaElementTypeConfigurationPanel() {
        if (this.schemaElementTypeConfigurationPanel == null) {
            this.schemaElementTypeConfigurationPanel = new SchemaElementTypeConfigurePanel();
        }
        return this.schemaElementTypeConfigurationPanel;
    }


    /**
     * This method initializes discoveryTabbedPane
     * 
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getDiscoveryTabbedPane() {
        if (this.discoveryTabbedPane == null) {
            this.discoveryTabbedPane = new JTabbedPane();
            List discoveryTypes = ExtensionsLoader.getInstance().getDiscoveryExtensions();
            if (discoveryTypes != null) {
                for (int i = 0; i < discoveryTypes.size(); i++) {
                    DiscoveryExtensionDescriptionType dd = (DiscoveryExtensionDescriptionType) discoveryTypes.get(i);
                    try {
                        NamespaceTypeDiscoveryComponent comp = gov.nih.nci.cagrid.introduce.portal.extension.tools.ExtensionTools
                            .getNamespaceTypeDiscoveryComponent(dd.getName(), this.info.getNamespaces());
                        if (comp != null) {
                            this.discoveryTabbedPane.addTab(dd.getDisplayName(), comp);
                            this.discoveryPanels.add(comp);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        CompositeErrorDialog.showErrorDialog("Error loading discovery type: " + dd.getDisplayName(), e);
                    }
                }
            }
        }
        return this.discoveryTabbedPane;
    }


    private void saveModifications() {
        int confirmed = JOptionPane.showConfirmDialog(ModificationViewer.this, "Are you sure you want to save?",
            "Confirm Save", JOptionPane.YES_NO_OPTION);
        if (confirmed == JOptionPane.OK_OPTION) {
            // verify no needed namespace types have been removed or modified
            if (!CommonTools.usedTypesAvailable(this.info.getServiceDescriptor())) {
                Set unavailable = CommonTools.getUnavailableUsedTypes(this.info.getServiceDescriptor());
                String[] message = {"The following schema element types used in the service",
                        "are not available in the specified namespace types!", "Please add schemas as appropriate.",
                        "\n"};
                String[] err = new String[unavailable.size() + message.length];
                System.arraycopy(message, 0, err, 0, message.length);
                int index = message.length;
                Iterator unavailableIter = unavailable.iterator();
                while (unavailableIter.hasNext()) {
                    err[index] = unavailableIter.next().toString();
                    index++;
                }
                JOptionPane.showMessageDialog(ModificationViewer.this, err, "Unavailable types found",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            BusyDialogRunnable r = new BusyDialogRunnable(GridApplication.getContext().getApplication(), "Save") {
                @Override
                public void process() {
                    try {

                        // walk the namespaces and make sure they are valid
                        setProgressText("validating namespaces");
                        NamespacesType namespaces = ModificationViewer.this.info.getNamespaces();
                        if ((namespaces != null) && (namespaces.getNamespace() != null)) {
                            for (int i = 0; i < namespaces.getNamespace().length; i++) {
                                NamespaceType currentNs = namespaces.getNamespace(i);
                                if (currentNs.getPackageName() != null) {
                                    if ((currentNs.getGenerateStubs() == null)
                                        || (!currentNs.getGenerateStubs().booleanValue())) {
                                        if (!CommonTools.isValidNoStubPackageName(currentNs.getPackageName())) {
                                            setErrorMessage("Error: Invalid package name for namespace "
                                                + currentNs.getNamespace() + " : " + currentNs.getPackageName());
                                            return;
                                        } else if (!CommonTools.isValidPackageName(currentNs.getPackageName())) {
                                            setErrorMessage("Error: Invalid package name for namespace "
                                                + currentNs.getNamespace() + " : " + currentNs.getPackageName());
                                            return;
                                        }
                                    }
                                }

                                boolean errors = false;
                                String em = "The following data type configurations in the " + currentNs.getNamespace()
                                    + " namespacece have the following errors: \n";

                                // walk through all the types and make sure they
                                // have valid serialization configurations
                                if (currentNs.getGenerateStubs() != null
                                    && !currentNs.getGenerateStubs().booleanValue()
                                    && currentNs.getSchemaElement() != null) {
                                    for (int schemaElementI = 0; schemaElementI < currentNs.getSchemaElement().length; schemaElementI++) {
                                        SchemaElementType type = currentNs.getSchemaElement(schemaElementI);
                                        ValidationResult result = SchemaElementTypeValidator.validateSchemaElementType(
                                            type.getClassName(), type.getSerializer(), type.getDeserializer());
                                        if (result.getErrors() != null && !result.getErrors().isEmpty()) {
                                            errors = true;
                                            Iterator it = result.getErrors().iterator();
                                            while (it.hasNext()) {
                                                em += type.getType() + " : "
                                                    + ((ValidationMessage) it.next()).formattedText() + "\n";
                                            }
                                        }
                                    }
                                }

                                if (errors) {
                                    setErrorMessage(em);
                                    return;
                                }
                            }
                        }

                        // check the methods to make sure they are valid.......
                        if ((ModificationViewer.this.info.getServices() != null)
                            && (ModificationViewer.this.info.getServices().getService() != null)) {
                            for (int serviceI = 0; serviceI < ModificationViewer.this.info.getServices().getService().length; serviceI++) {
                                ServiceType service = ModificationViewer.this.info.getServices().getService(serviceI);
                                if ((service.getMethods() != null) && (service.getMethods().getMethod() != null)) {
                                    List methodNames = new ArrayList();
                                    if ((service.getMethods() != null) && (service.getMethods().getMethod() != null)) {
                                        for (int methodI = 0; methodI < service.getMethods().getMethod().length; methodI++) {
                                            MethodType method = service.getMethods().getMethod(methodI);
                                            if (method.getName().length() == 0) {
                                                setErrorMessage("The service " + service.getName()
                                                    + " has a method with no name");
                                                return;
                                            } else if (!(methodNames.contains(method.getName()))) {
                                                methodNames.add(method.getName());
                                            } else {
                                                setErrorMessage("The service " + service.getName()
                                                    + " has duplicate methods " + method.getName());
                                                return;
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // add any new extensions and run there creators
                        // for each extension in the properties make sure to add
                        // the xml to the
                        // introduce model for them to use.....
                        Properties oldProps = new Properties();
                        try {
                            oldProps.load(new FileInputStream(new File(info.getBaseDirectory().getAbsolutePath()
                                + File.separator + IntroduceConstants.INTRODUCE_PROPERTIES_FILE + ".prev")
                                .getAbsolutePath()));
                        } catch (Exception e) {
                            // do nothing this might be right after creation,
                            // therefore no prev file exists
                        }
                        List newExtsNames = new ArrayList<String>();
                        if (oldProps.size() >= 0
                            && oldProps.getProperty(IntroduceConstants.INTRODUCE_SKELETON_EXTENSIONS) != null) {
                            String oldExtensions = oldProps
                                .getProperty(IntroduceConstants.INTRODUCE_SKELETON_EXTENSIONS);
                            String currentExtensions = info.getIntroduceServiceProperties().getProperty(
                                IntroduceConstants.INTRODUCE_SKELETON_EXTENSIONS);
                            StringTokenizer strtok = new StringTokenizer(oldExtensions, ",", false);
                            List oldExts = new ArrayList<String>();
                            while (strtok.hasMoreElements()) {
                                String next = strtok.nextToken();
                                oldExts.add(next);
                            }

                            // process the new ones and compare them to the old
                            // ones
                            List newExtsTypes = new ArrayList<ExtensionDescription>();
                            strtok = new StringTokenizer(currentExtensions, ",", false);
                            while (strtok.hasMoreElements()) {
                                String next = strtok.nextToken();
                                if (!oldExts.contains(next)) {
                                    logger.info("A new extension is being added: " + next);
                                    ExtensionDescription desc = ExtensionsLoader.getInstance().getExtension(next);
                                    ExtensionType type = new ExtensionType();
                                    type.setName(next);
                                    type.setVersion(desc.getVersion());
                                    newExtsTypes.add(type);
                                    newExtsNames.add(next);
                                }
                            }
                            ExtensionType[] newExtensionTypeArray = null;
                            if (info.getExtensions() != null && info.getExtensions().getExtension() != null
                                && info.getExtensions().getExtension().length > 0) {
                                newExtensionTypeArray = new ExtensionType[info.getExtensions().getExtension().length
                                    + newExtsTypes.size()];
                                System.arraycopy(info.getExtensions().getExtension(), 0, newExtensionTypeArray, 0, info
                                    .getExtensions().getExtension().length);
                                System.arraycopy(newExtsTypes.toArray(), 0, newExtensionTypeArray, info.getExtensions()
                                    .getExtension().length, newExtsTypes.size());
                            } else {
                                newExtensionTypeArray = new ExtensionType[newExtsTypes.size()];
                                System.arraycopy(newExtsTypes.toArray(), 0, newExtensionTypeArray, 0, newExtsTypes
                                    .size());
                            }
                            ExtensionsType extType = new ExtensionsType(newExtensionTypeArray);
                            info.setExtensions(extType);

                            setProgressText("Invoking extension viewers...");
                            for (int i = 0; i < newExtsNames.size(); i++) {
                                ServiceExtensionDescriptionType edt = ExtensionsLoader.getInstance()
                                    .getServiceExtension((String) newExtsNames.get(i));
                                JDialog extDialog = gov.nih.nci.cagrid.introduce.portal.extension.tools.ExtensionTools
                                    .getCreationUIDialog(GridApplication.getContext().getApplication(), edt.getName(),
                                        info);
                                if (extDialog != null) {
                                    GridApplication.getContext().centerDialog(extDialog);
                                    extDialog.setVisible(true);
                                }
                            }

                            // process the old ones and compare to the new ones
                            Iterator it = oldExts.iterator();
                            while (it.hasNext()) {
                                String next = (String) it.next();
                                if (!newExtsNames.contains(next)) {
                                    logger.info("An extension is being removed: " + next);
                                }
                            }

                        }

                        // save the metadata and methods and then call the
                        // resync and build
                        setProgressText("writting service document");
                        ModificationViewer.this.info.persistInformation();

                        try {
                            // call the sync tools
                            setProgressText("synchronizing skeleton");
                            SyncTools sync = new SyncTools(ModificationViewer.this.methodsDirectory);
                            sync.sync();
                        } catch (Exception e) {
                            throw new Exception("FATAL ERROR: Service was unable to be re-synced: \n" + e.getMessage()
                                + "\nPlease either roll back to previous save state or re-create the service.", e);
                        }

                        // build the synchronized service
                        setProgressText("rebuilding skeleton");
                        List<String> cmd = AntTools.getAntCommand("clean all", ModificationViewer.this.methodsDirectory
                            .getAbsolutePath());
                        Process p = CommonTools.createAndOutputProcess(cmd);
                        p.waitFor();

                        if (p.exitValue() != 0) {
                            setErrorMessage("Error: Unable to rebuild the skeleton");
                        } else {
                            setProgressText("creating service archive");

                            info.createArchive();
                        }
                        ModificationViewer.this.dirty = false;
                        this.setProgressText("");

                        for (int i = 0; i < newExtsNames.size(); i++) {
                            ServiceExtensionDescriptionType edt = ExtensionsLoader.getInstance().getServiceExtension(
                                (String) newExtsNames.get(i));
                            ServiceModificationUIPanel extPanel = gov.nih.nci.cagrid.introduce.portal.extension.tools.ExtensionTools
                                .getServiceModificationUIPanel((String) newExtsNames.get(i), info);
                            if (extPanel != null) {
                                extensionPanels.add(extPanel);
                                contentTabbedPane.addTab(edt.getDisplayName(), null, extPanel, null);
                            }
                        }

                    } catch (Exception e1) {
                        e1.printStackTrace();
                        setErrorMessage("Error: " + e1.getMessage());
                        return;
                    }
                    // reinitialize the GUI with changes from saved model
                    try {
                        reInitialize(ModificationViewer.this.methodsDirectory);
                    } catch (Exception e) {
                        logger.error(e);
                    }

                }
            };

            Thread th = new Thread(r);
            th.start();
        }
    }


    /**
     * This method initializes namespaceConfPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getNamespaceConfPanel() {
        if (this.namespaceConfPanel == null) {
            this.namespaceConfPanel = new JPanel();
            this.namespaceConfPanel.setLayout(new BoxLayout(this.namespaceConfPanel, BoxLayout.Y_AXIS));
            namespaceConfPanel.add(getNamespaceManageTabbedPane(), null);
        }
        return this.namespaceConfPanel;
    }


    /**
     * This method initializes servicePropertiesPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getServicePropertiesPanel() {
        if (this.servicePropertiesPanel == null) {
            GridBagConstraints gridBagConstraints26 = new GridBagConstraints();
            gridBagConstraints26.gridx = 0;
            gridBagConstraints26.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints26.weightx = 1.0D;
            gridBagConstraints26.weighty = 1.0D;
            gridBagConstraints26.gridy = 0;
            GridBagConstraints gridBagConstraints25 = new GridBagConstraints();
            gridBagConstraints25.gridx = 0;
            gridBagConstraints25.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints25.gridy = 1;
            this.servicePropertiesPanel = new JPanel();
            this.servicePropertiesPanel.setLayout(new GridBagLayout());
            this.servicePropertiesPanel.add(getServicePropertiesTableContainerPanel(), gridBagConstraints26);
            this.servicePropertiesPanel.add(new IconFeedbackPanel(this.servicePropertiesValidation,
                getServicePropertiesControlPanel()), gridBagConstraints25);
        }
        return this.servicePropertiesPanel;
    }


    /**
     * This method initializes servicePropertiesTableContainerPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getServicePropertiesTableContainerPanel() {
        if (this.servicePropertiesTableContainerPanel == null) {
            GridBagConstraints gridBagConstraints28 = new GridBagConstraints();
            gridBagConstraints28.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints28.gridx = 0;
            gridBagConstraints28.gridy = 0;
            gridBagConstraints28.weightx = 1.0;
            gridBagConstraints28.weighty = 1.0;
            gridBagConstraints28.insets = new Insets(0, 0, 0, 0);
            this.servicePropertiesTableContainerPanel = new JPanel();
            this.servicePropertiesTableContainerPanel.setLayout(new GridBagLayout());
            servicePropertiesTableContainerPanel.setBorder(BorderFactory.createTitledBorder(null, "Service Properties",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12),
                IntroduceLookAndFeel.getPanelLabelColor()));
            servicePropertiesTableContainerPanel.add(getServicePropertiesTableScrollPane(), gridBagConstraints28);
        }
        return this.servicePropertiesTableContainerPanel;
    }


    /**
     * This method initializes servicePropertiesTableScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getServicePropertiesTableScrollPane() {
        if (this.servicePropertiesTableScrollPane == null) {
            this.servicePropertiesTableScrollPane = new JScrollPane();
            this.servicePropertiesTableScrollPane.setViewportView(getServicePropertiesTable());

        }
        return this.servicePropertiesTableScrollPane;
    }


    /**
     * This method initializes servicePropertiesTable
     * 
     * @return javax.swing.JTable
     */
    private ServicePropertiesTable getServicePropertiesTable() {
        if (this.servicePropertiesTable == null) {
            this.servicePropertiesTable = new ServicePropertiesTable(this.info);
            this.servicePropertiesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    int row = servicePropertiesTable.getSelectedRow();
                    if ((row < 0) || (row >= servicePropertiesTable.getRowCount())) {
                        getRemoveServicePropertyButton().setEnabled(false);
                    } else {
                        getRemoveServicePropertyButton().setEnabled(true);
                    }
                }
            });
            getRemoveServicePropertyButton().setEnabled(false);
        }
        return this.servicePropertiesTable;
    }


    /**
     * This method initializes servicePropertiesControlPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getServicePropertiesControlPanel() {
        if (this.servicePropertiesControlPanel == null) {
            GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
            gridBagConstraints15.fill = GridBagConstraints.BOTH;
            gridBagConstraints15.gridy = 5;
            gridBagConstraints15.weightx = 1.0;
            gridBagConstraints15.insets = new Insets(2, 2, 10, 10);
            gridBagConstraints15.gridx = 0;
            GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
            gridBagConstraints14.gridx = 0;
            gridBagConstraints14.anchor = GridBagConstraints.SOUTHWEST;
            gridBagConstraints14.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints14.gridy = 4;
            this.descriptionLabel = new JLabel();
            this.descriptionLabel.setText("Description:");
            GridBagConstraints gridBagConstraints42 = new GridBagConstraints();
            gridBagConstraints42.gridx = 1;
            gridBagConstraints42.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints42.gridwidth = 1;
            gridBagConstraints42.gridheight = 4;
            gridBagConstraints42.gridy = 0;
            GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
            gridBagConstraints41.gridx = 0;
            gridBagConstraints41.anchor = java.awt.GridBagConstraints.SOUTHWEST;
            gridBagConstraints41.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints41.gridy = 2;
            this.servicePropertiesValueLabel = new JLabel();
            this.servicePropertiesValueLabel.setText("Default Value:");
            GridBagConstraints gridBagConstraints40 = new GridBagConstraints();
            gridBagConstraints40.gridx = 0;
            gridBagConstraints40.anchor = java.awt.GridBagConstraints.SOUTHWEST;
            gridBagConstraints40.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints40.gridy = 0;
            this.servicePropertiesKeyLabel = new JLabel();
            this.servicePropertiesKeyLabel.setText("Key:");
            GridBagConstraints gridBagConstraints39 = new GridBagConstraints();
            gridBagConstraints39.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints39.gridy = 3;
            gridBagConstraints39.weightx = 1.0;
            gridBagConstraints39.insets = new java.awt.Insets(2, 2, 10, 10);
            gridBagConstraints39.gridx = 0;
            GridBagConstraints gridBagConstraints38 = new GridBagConstraints();
            gridBagConstraints38.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints38.gridy = 1;
            gridBagConstraints38.weightx = 1.0;
            gridBagConstraints38.insets = new java.awt.Insets(2, 2, 10, 10);
            gridBagConstraints38.gridx = 0;
            GridBagConstraints gridBagConstraints43 = new GridBagConstraints();
            gridBagConstraints43.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints43.gridy = 6;
            gridBagConstraints43.weightx = 1.0;
            gridBagConstraints43.insets = new java.awt.Insets(2, 2, 10, 10);
            gridBagConstraints43.gridx = 0;
            this.servicePropertiesControlPanel = new JPanel();
            this.servicePropertiesControlPanel.setLayout(new GridBagLayout());
            servicePropertiesControlPanel.setBorder(BorderFactory.createTitledBorder(null, "Add New Service Property",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12),
                IntroduceLookAndFeel.getPanelLabelColor()));
            this.servicePropertiesControlPanel.add(getServicePropertyKeyTextField(), gridBagConstraints38);
            this.servicePropertiesControlPanel.add(getServicePropertyValueTextField(), gridBagConstraints39);
            this.servicePropertiesControlPanel.add(this.servicePropertiesKeyLabel, gridBagConstraints40);
            this.servicePropertiesControlPanel.add(this.servicePropertiesValueLabel, gridBagConstraints41);
            this.servicePropertiesControlPanel.add(getServicePropertiesButtonPanel(), gridBagConstraints42);
            this.servicePropertiesControlPanel.add(getServicePropertiesIsFromETCCheckBox(), gridBagConstraints43);
            this.servicePropertiesControlPanel.add(this.descriptionLabel, gridBagConstraints14);
            this.servicePropertiesControlPanel.add(getServicePropertyDescriptionTextField(), gridBagConstraints15);
        }
        return this.servicePropertiesControlPanel;
    }


    private void validateServicePropertiesInput() {

        ValidationResult result = new ValidationResult();

        if (ValidationUtils.isNotBlank(this.getServicePropertyKeyTextField().getText())) {
            if (!CommonTools.isValidJavaField(getServicePropertyKeyTextField().getText())) {
                result.add(new SimpleValidationMessage(SERVICE_PROPERTY_KEY + " must be a valid java field. ( "
                    + CommonTools.ALLOWED_JAVA_FIELD_REGEX + " )", Severity.ERROR, SERVICE_PROPERTY_KEY));
            }
            if (ValidationUtils.isBlank(this.getServicePropertyValueTextField().getText())) {
                result.add(new SimpleValidationMessage(SERVICE_PROPERTY_VALUE + " is not provided.", Severity.WARNING,
                    SERVICE_PROPERTY_VALUE));
            }
            if (ValidationUtils.isBlank(this.getServicePropertyDescriptionTextField().getText())) {
                result.add(new SimpleValidationMessage(SERVICE_PROPERTY_DESCRIPTION + " is not provided.",
                    Severity.WARNING, SERVICE_PROPERTY_DESCRIPTION));
            }
        }

        this.servicePropertiesValidation.setResult(result);
        updateAddServicePropertiesButton();
        updateServicePropertyComponentTreeSeverity();
    }


    private void updateAddServicePropertiesButton() {
        if (this.servicePropertiesValidation.hasErrors()
            || this.getServicePropertyKeyTextField().getText().trim().length() <= 0) {
            getAddServiceProperyButton().setEnabled(false);
        } else {
            getAddServiceProperyButton().setEnabled(true);
        }
    }


    /**
     * This method initializes addServiceProperyButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddServiceProperyButton() {
        if (this.addServiceProperyButton == null) {
            this.addServiceProperyButton = new JButton();
            this.addServiceProperyButton.setText("Add");
            this.addServiceProperyButton.setIcon(IntroduceLookAndFeel.getAddServicePropertyIcon());
            this.addServiceProperyButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if ((getServicePropertyKeyTextField().getText().length() != 0)) {
                        String key = getServicePropertyKeyTextField().getText();
                        String value = getServicePropertyValueTextField().getText();
                        boolean isFromETC = getServicePropertiesIsFromETCCheckBox().isSelected();
                        String desc = getServicePropertyDescriptionTextField().getText();
                        getServicePropertiesTable().addRow(key, value, isFromETC, desc);
                        getServicePropertiesIsFromETCCheckBox().setSelected(false);

                        getServicePropertyKeyTextField().setText("");
                        getServicePropertyValueTextField().setText("");
                        getServicePropertyDescriptionTextField().setText("");
                    }
                }
            });
        }
        return this.addServiceProperyButton;
    }


    /**
     * This method initializes removeServicePropertyButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getRemoveServicePropertyButton() {
        if (this.removeServicePropertyButton == null) {
            this.removeServicePropertyButton = new JButton();
            this.removeServicePropertyButton.setText("Remove");
            this.removeServicePropertyButton.setIcon(IntroduceLookAndFeel.getRemveServicePropertyIcon());
            this.removeServicePropertyButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        getServicePropertiesTable().removeSelectedRow();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        ErrorDialog.showError(e1);
                    }
                }
            });
        }
        return this.removeServicePropertyButton;
    }


    /**
     * This method initializes servicePropertyKeyTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getServicePropertyKeyTextField() {
        if (this.servicePropertyKeyTextField == null) {
            this.servicePropertyKeyTextField = new JTextField();
            this.servicePropertyKeyTextField.getDocument().addDocumentListener(new DocumentListener() {

                public void removeUpdate(DocumentEvent e) {
                    validateServicePropertiesInput();
                }


                public void insertUpdate(DocumentEvent e) {
                    validateServicePropertiesInput();
                }


                public void changedUpdate(DocumentEvent e) {
                    validateServicePropertiesInput();
                }

            });
        }
        return this.servicePropertyKeyTextField;
    }


    /**
     * This method initializes servicePropertyValueTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getServicePropertyValueTextField() {
        if (this.servicePropertyValueTextField == null) {
            this.servicePropertyValueTextField = new JTextField();
            this.servicePropertyValueTextField.getDocument().addDocumentListener(new DocumentListener() {

                public void removeUpdate(DocumentEvent e) {
                    validateServicePropertiesInput();
                }


                public void insertUpdate(DocumentEvent e) {
                    validateServicePropertiesInput();
                }


                public void changedUpdate(DocumentEvent e) {
                    validateServicePropertiesInput();
                }

            });
        }
        return this.servicePropertyValueTextField;
    }


    /**
     * This method initializes servicePropertiesButtonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getServicePropertiesButtonPanel() {
        if (this.servicePropertiesButtonPanel == null) {
            GridBagConstraints gridBagConstraints37 = new GridBagConstraints();
            gridBagConstraints37.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints37.gridy = 0;
            gridBagConstraints37.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints37.gridx = 0;
            GridBagConstraints gridBagConstraints32 = new GridBagConstraints();
            gridBagConstraints32.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints32.gridy = 1;
            gridBagConstraints32.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints32.gridx = 0;
            this.servicePropertiesButtonPanel = new JPanel();
            this.servicePropertiesButtonPanel.setLayout(new GridBagLayout());
            this.servicePropertiesButtonPanel.add(getRemoveServicePropertyButton(), gridBagConstraints32);
            this.servicePropertiesButtonPanel.add(getAddServiceProperyButton(), gridBagConstraints37);
        }
        return this.servicePropertiesButtonPanel;
    }


    /**
     * This method initializes servicesTabbedPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getResourceesTabbedPanel() {
        if (this.resourceesTabbedPanel == null) {
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.weightx = 0.0D;
            gridBagConstraints1.weighty = 0.0D;
            gridBagConstraints1.gridx = 1;
            GridBagConstraints gridBagConstraints45 = new GridBagConstraints();
            gridBagConstraints45.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints45.gridy = 0;
            gridBagConstraints45.weightx = 1.0D;
            gridBagConstraints45.weighty = 1.0D;
            gridBagConstraints45.gridx = 0;
            this.resourceesTabbedPanel = new JPanel();
            this.resourceesTabbedPanel.setLayout(new GridBagLayout());
            this.resourceesTabbedPanel.add(getResourcesPanel(), gridBagConstraints45);
            this.resourceesTabbedPanel.add(getResourcesOptionsPanel(), gridBagConstraints1);
        }
        return this.resourceesTabbedPanel;
    }


    /**
     * This method initializes resourcesPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getResourcesPanel() {
        if (this.resourcesPanel == null) {
            GridBagConstraints gridBagConstraints46 = new GridBagConstraints();
            gridBagConstraints46.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints46.gridx = 0;
            gridBagConstraints46.gridy = 0;
            gridBagConstraints46.weightx = 1.0;
            gridBagConstraints46.weighty = 1.0;
            gridBagConstraints46.insets = new java.awt.Insets(2, 2, 2, 2);
            this.resourcesPanel = new JPanel();
            this.resourcesPanel.setLayout(new GridBagLayout());
            resourcesPanel.setBorder(BorderFactory.createTitledBorder(null, "Services",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12),
                IntroduceLookAndFeel.getPanelLabelColor()));
            this.resourcesPanel.add(getResourcesScrollPane(), gridBagConstraints46);
        }
        return this.resourcesPanel;
    }


    /**
     * This method initializes resourcesScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getResourcesScrollPane() {
        if (this.resourcesScrollPane == null) {
            this.resourcesScrollPane = new JScrollPane();
            this.resourcesScrollPane.setViewportView(getResourcesJTree());
        }
        return this.resourcesScrollPane;
    }


    /**
     * This method initializes resourcesJTree
     * 
     * @return javax.swing.JTree
     */
    private ServicesJTree getResourcesJTree() {
        if (this.resourcesJTree == null) {
            this.resourcesJTree = new ServicesJTree(this.info, getResourcesOptionsPanel());

            // initialize the option cards for this tree
            this.resourcesOptionsPanel.add(new ServicesButtonPanel(this.resourcesJTree), "services");
            this.resourcesOptionsPanel.add(new ServiceButtonPanel(this.resourcesJTree), "service");
            this.resourcesOptionsPanel.add(new MethodsButtonPanel(this.resourcesJTree), "methods");
            this.resourcesOptionsPanel.add(new MethodButtonPanel(this.resourcesJTree), "method");
            this.resourcesOptionsPanel.add(new ResourcePropertyButtonPanel(this.resourcesJTree), "resourceProperty");
            this.resourcesOptionsPanel.add(new ResourcesButtonPanel(this.resourcesJTree), "resources");
            this.resourcesOptionsPanel.add(new JPanel(), "blank");
        }
        return this.resourcesJTree;
    }


    /**
     * This method initializes jSplitPane
     * 
     * @return javax.swing.JSplitPane
     */
    private JSplitPane getTypesSplitPane() {
        if (this.typesSplitPane == null) {
            this.typesSplitPane = new JSplitPane();
            this.typesSplitPane.setOneTouchExpandable(true);
            this.typesSplitPane.setLeftComponent(getNamespaceTableScrollPane());
            getNamespaceTableScrollPane().setMinimumSize(new Dimension(200, 400));
            this.typesSplitPane.setRightComponent(getNamespaceConfPanel());
            getNamespaceConfPanel().setMinimumSize(new Dimension(200, 400));
        }
        return this.typesSplitPane;
    }


    /**
     * This method initializes propertyIsFromETCCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getServicePropertiesIsFromETCCheckBox() {
        if (this.propertyIsFromETCCheckBox == null) {
            this.propertyIsFromETCCheckBox = new JCheckBox();
            this.propertyIsFromETCCheckBox
                .setToolTipText("Checking this box will let introduce that the value of this property will be a "
                    + "file location which is meant to be relative from the service's etc location in "
                    + "the service container.  The value that is set at deploy time will be replaced "
                    + "with the absolute path to the etc directory pluss the value of the variable.");
            this.propertyIsFromETCCheckBox.setText("Value is a relative file path from the service's ETC location.");
        }
        return this.propertyIsFromETCCheckBox;
    }


    private NamespaceType[] mergeNamespaceArrays(NamespaceType[] current, NamespaceType[] additional) {
        Set additionalNamespaces = new HashSet();
        for (NamespaceType element : additional) {
            additionalNamespaces.add(element.getNamespace());
        }
        List merged = new ArrayList();
        Collections.addAll(merged, (Object[]) additional);
        if (current.length != 0) {
            for (NamespaceType element : current) {
                String currentNamespace = element.getNamespace();
                if (!additionalNamespaces.contains(currentNamespace)) {
                    merged.add(element);
                }
            }
        }
        NamespaceType[] mergedArray = new NamespaceType[merged.size()];
        merged.toArray(mergedArray);
        return mergedArray;
    }


    /**
     * This method initializes resourcesOptionsPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getResourcesOptionsPanel() {
        if (this.resourcesOptionsPanel == null) {
            this.resourcesOptionsPanel = new JPanel(new CardLayout());
            this.resourcesOptionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
                "Information and Options", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), PortalLookAndFeel
                    .getPanelLabelColor()));
        }
        return this.resourcesOptionsPanel;
    }


    /**
     * This method initializes servicePropertyDescriptionTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getServicePropertyDescriptionTextField() {
        if (this.servicePropertyDescriptionTextField == null) {
            this.servicePropertyDescriptionTextField = new JTextField();
            this.servicePropertyDescriptionTextField.getDocument().addDocumentListener(new DocumentListener() {

                public void removeUpdate(DocumentEvent e) {
                    validateServicePropertiesInput();
                }


                public void insertUpdate(DocumentEvent e) {
                    validateServicePropertiesInput();
                }


                public void changedUpdate(DocumentEvent e) {
                    validateServicePropertiesInput();
                }

            });
        }
        return this.servicePropertyDescriptionTextField;
    }


    /**
     * @param progBar
     */
    private void addNamespace(MultiEventProgressBar progBar) {
        File schemaDir = new File(ModificationViewer.this.methodsDirectory
            + File.separator
            + "schema"
            + File.separator
            + ModificationViewer.this.info.getIntroduceServiceProperties().getProperty(
                IntroduceConstants.INTRODUCE_SKELETON_SERVICE_NAME));
        NamespaceTypeDiscoveryComponent discoveryComponent = (NamespaceTypeDiscoveryComponent) getDiscoveryTabbedPane()
            .getSelectedComponent();

        NamespaceType[] types = discoveryComponent.createNamespaceType(schemaDir, ConfigurationUtil
            .getIntroducePortalConfiguration().getNamespaceReplacementPolicy(), progBar);

        List<String> messages = new ArrayList<String>();
        if (types != null) {
            for (NamespaceType currentType : types) {
                if (CommonTools.getNamespaceType(ModificationViewer.this.info.getNamespaces(), currentType
                    .getNamespace()) != null) {
                    // namespace type already exists in
                    // service
                    messages.add("The namespace " + currentType.getNamespace() + " already exists, it was reloaded");
                }
            }

            NamespacesType namespaces = ModificationViewer.this.info.getNamespaces();
            if (namespaces == null) {
                namespaces = new NamespacesType();
            }
            NamespaceType[] currentNamespaces = namespaces.getNamespace();
            if (currentNamespaces == null) {
                currentNamespaces = types;
            } else {
                // merge discovered namespaces into existing
                // namespaces
                currentNamespaces = mergeNamespaceArrays(currentNamespaces, types);
            }
            namespaces.setNamespace(currentNamespaces);
            ModificationViewer.this.info.setNamespaces(namespaces);

            // reload the types tree
            getNamespaceJTree().setNamespaces(ModificationViewer.this.info.getNamespaces());
        } else {
            String[] errorMessages = discoveryComponent.getErrorMessage();
            if (errorMessages != null && errorMessages.length > 0) {
                CompositeErrorDialog.showErrorDialog("Problem adding types, see details for more information.",
                    errorMessages, discoveryComponent.getErrorCauseThrowable());
            } else {
                CompositeErrorDialog.showErrorDialog("Unspecified problem adding types.", discoveryComponent
                    .getErrorCauseThrowable());
            }
        }
        if (messages.size() != 0) {
            String[] msg = new String[messages.size()];
            messages.toArray(msg);
            JOptionPane.showMessageDialog(ModificationViewer.this, msg);
        }
    }


    /**
     * 
     */
    private void removeNamespace() {
        try {
            if (getNamespaceJTree().getCurrentNode() instanceof NamespaceTypeTreeNode) {
                NamespaceType type = (NamespaceType) getNamespaceJTree().getCurrentNode().getUserObject();
                if (!type.getNamespace().equals(IntroduceConstants.W3CNAMESPACE)) {
                    if (CommonTools.isNamespaceTypeInUse(type, ModificationViewer.this.info.getServiceDescriptor())) {
                        String[] message = {"The namespace " + type.getNamespace(),
                                "contains types in use by this service."};
                        JOptionPane.showMessageDialog(ModificationViewer.this, message);
                    } else {
                        getNamespaceJTree().removeSelectedNode();
                    }
                } else {
                    ErrorDialog.showError("Cannot remove " + IntroduceConstants.W3CNAMESPACE);
                }
            }
        } catch (Exception ex) {
            // TODO: there has to be a better check for
            // namespace
            // selection than this!!
            JOptionPane.showMessageDialog(ModificationViewer.this, "Please select namespace to Remove");
        }
    }


    /**
     * This method initializes ExtensionsPanel
     * 
     * @return javax.swing.JPanel
     */
    private ExtensionsManagerPanel getExtensionsPanel() {
        if (extensionsPanel == null) {
            extensionsPanel = new ExtensionsManagerPanel(info);
            // extensionsPanel.setLayout(new GridBagLayout());
        }
        return extensionsPanel;
    }


    /**
     * This method initializes namespaceManageTabbedPane
     * 
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getNamespaceManageTabbedPane() {
        if (namespaceManageTabbedPane == null) {
            namespaceManageTabbedPane = new JTabbedPane();
            namespaceManageTabbedPane.addTab("Add/Remove", null, getDiscoveryPanel(), null);
            namespaceManageTabbedPane.addTab("Configure Types", null, getNamespaceTypePropertiesPanel(), null);
        }
        return namespaceManageTabbedPane;
    }

}
