package gov.nih.nci.cagrid.introduce.portal.deployment;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.extension.DeploymentExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ResourcePropertyEditorExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertyType;
import gov.nih.nci.cagrid.introduce.common.AntTools;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ResourceManager;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;
import gov.nih.nci.cagrid.introduce.portal.common.IntroduceLookAndFeel;
import gov.nih.nci.cagrid.introduce.portal.extension.DeploymentUIPanel;
import gov.nih.nci.cagrid.introduce.portal.extension.ResourcePropertyEditorPanel;
import gov.nih.nci.cagrid.introduce.portal.extension.ServiceDeploymentUIPanel;
import gov.nih.nci.cagrid.introduce.portal.modification.services.resourceproperties.editor.XMLEditorViewer;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.TitledBorder;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.cagrid.grape.ApplicationComponent;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.utils.BusyDialogRunnable;
import org.cagrid.grape.utils.CompositeErrorDialog;
import org.cagrid.grape.utils.ErrorDialog;


/**
 * DeploymentViewer
 * 
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin </A>
 * @created Jun 22, 2005
 * @version $Id: mobiusEclipseCodeTemplates.xml,v 1.2 2005/04/19 14:58:02 oster
 *          Exp $
 */
public class DeploymentViewer extends ApplicationComponent {

    private static final Logger logger = Logger.getLogger(DeploymentViewer.class);

    public static final String GLOBUS = "Globus";

    public static final String TOMCAT = "Tomcat";

    public static final String JBOSS = "JBoss"; // @jve:decl-index=0:

    private ServiceInformation info = null; // @jve:decl-index=0:

    private JTabbedPane mainPanel = null;

    private JPanel buttonPanel = null;

    private JButton deployButton = null;

    private File serviceDirectory; // @jve:decl-index=0:

    private JPanel deploymentTypePanel = null;

    private JComboBox deploymentTypeSelector = null;

    private JPanel defaultPanel = null;

    private JPanel holderPanel = null;

    private DeploymentPropertiesPanel advancedDeploymentPanel = null;

    private ServicePropertiesPanel servicePropertiesPanel = null;

    private JPanel deploymentInformationPanel = null;

    private JLabel serviceDeploymentNameLabel = null;

    private JLabel serviceDeploymentNameTextField = null;

    private JLabel containerLocationLabel = null;

    private JLabel containerLocationTextField = null;

    private JLabel serviceNamespaceLabel = null;

    private JLabel serviceNamespaceTextField = null;

    private JLabel serviceLocationLabel = null;

    private JLabel serviceLocationTextField = null;


    /**
     * This method initializes
     */
    public DeploymentViewer(File serviceDirectory) {
        super();
        this.serviceDirectory = serviceDirectory;
        try {
            initialize();
        } catch (Exception e) {
            logger.error(e);
            this.dispose();
        }
    }


    /**
     * This method initializes this
     */
    private void initialize() throws Exception {
        this.info = new ServiceInformation(serviceDirectory);

        this.setFrameIcon(IntroduceLookAndFeel.getDeployIcon());
        this.setContentPane(getHolderPanel());
        this.setTitle("Deploy Grid Service");
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JTabbedPane getMainPanel() {
        if (mainPanel == null) {
            mainPanel = new JTabbedPane();
            mainPanel.addTab("General Deployment", getDefaultPanel());
            mainPanel.addTab("Advanced Deployment", getAdvancedDeploymentPanel());
            mainPanel.addTab("Service Properties", getServicePropertiesPanel());

            // add any panels for metadata of the 1st service that is load from
            // file
            if (info != null && info.getServices().getService(0).getResourcePropertiesList() != null
                && info.getServices().getService(0).getResourcePropertiesList().getResourceProperty() != null) {
                ResourcePropertyType[] types = info.getServices().getService(0).getResourcePropertiesList()
                    .getResourceProperty();
                for (int typei = 0; typei < types.length; typei++) {
                    try {
                        ResourcePropertyType type = types[typei];

                        File schemaDir = new File(info.getBaseDirectory() + File.separator + "schema" + File.separator
                            + info.getServices().getService(0).getName());
                        File etcDir = new File(info.getBaseDirectory() + File.separator + "etc");
                        if (type.isPopulateFromFile()) {
                            String rpData = null;
                            File resourcePropertyFile = null;

                            if (type.getFileLocation() != null) {
                                resourcePropertyFile = new File(etcDir.getAbsolutePath() + File.separator
                                    + type.getFileLocation());
                            }
                            if (resourcePropertyFile != null && resourcePropertyFile.exists()) {
                                // file has already been created
                                logger.debug("Loading resource properties file : " + resourcePropertyFile);
                                rpData = Utils.fileToStringBuffer(new File(resourcePropertyFile.getAbsolutePath()))
                                    .toString();
                            } else {

                                logger.debug("Creating a new resource properties file");
                                boolean created = resourcePropertyFile.createNewFile();
                                if (!created) {
                                    throw new Exception("Could not create file"
                                        + resourcePropertyFile.getAbsolutePath());
                                }
                                rpData = Utils.fileToStringBuffer(new File(resourcePropertyFile.getAbsolutePath()))
                                    .toString();
                            }

                            QName qname = type.getQName();
                            NamespaceType nsType = CommonTools.getNamespaceType(info.getNamespaces(), qname
                                .getNamespaceURI());

                            ResourcePropertyEditorExtensionDescriptionType mde = gov.nih.nci.cagrid.introduce.portal.extension.tools.ExtensionTools
                                .getResourcePropertyEditorExtensionDescriptor(qname);
                            ResourcePropertyEditorPanel mdec = null;

                            if (mde != null) {
                                mdec = gov.nih.nci.cagrid.introduce.portal.extension.tools.ExtensionTools
                                    .getMetadataEditorComponent(mde.getName(), type, rpData, new File(schemaDir
                                        .getAbsolutePath()
                                        + File.separator + nsType.getLocation()), schemaDir);
                            } else {
                                // use the default editor....
                                mdec = new XMLEditorViewer(type, rpData, new File(schemaDir.getAbsolutePath()
                                    + File.separator + nsType.getLocation()), schemaDir);
                            }
                            mainPanel.addTab(mde.getDisplayName() + " (" + type.getQName().getLocalPart() + ")", mdec);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }

            // run any service extensions deployment UI's that need to be ran
            if ((info != null && info.getServiceDescriptor().getExtensions() != null)
                && (info.getServiceDescriptor().getExtensions().getExtension() != null)) {
                ExtensionType[] extensions = info.getServiceDescriptor().getExtensions().getExtension();
                for (ExtensionType element : extensions) {
                    ServiceExtensionDescriptionType edesc = ExtensionsLoader.getInstance().getServiceExtension(
                        element.getName());
                    if (edesc != null) {
                        try {
                            ServiceDeploymentUIPanel depPanel = gov.nih.nci.cagrid.introduce.portal.extension.tools.ExtensionTools
                                .getServiceDeploymentUIPanel(element.getName(), info);
                            if (depPanel != null) {
                                mainPanel.addTab(edesc.getDisplayName(), depPanel);
                            }
                        } catch (Exception ex) {
                            CompositeErrorDialog.showErrorDialog("Error loading deployment UI for extension "
                                + element.getName(), ex.getMessage(), ex);
                        }
                    }
                }
            }

            // run an deployment extensions UI's that need to be ran
            List<DeploymentExtensionDescriptionType> deploymentExtensions = ExtensionsLoader.getInstance()
                .getDeploymentExtensions();
            for (DeploymentExtensionDescriptionType type : deploymentExtensions) {
                try {
                    DeploymentUIPanel depPanel = gov.nih.nci.cagrid.introduce.portal.extension.tools.ExtensionTools
                        .getDeploymentUIPanel(type.getName(), info);
                    if (depPanel != null) {
                        mainPanel.addTab(type.getDisplayName(), depPanel);
                    }
                } catch (Exception ex) {
                    CompositeErrorDialog.showErrorDialog("Error loading deployment UI for extension " + type.getName(),
                        ex.getMessage(), ex);
                }
            }

            // add a change listner that will be invoked on a tab change and
            // will
            // call to refresh the gui components if they choose to implement
            mainPanel.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent e) {
                    resetGUI();
                    for (int i = 0; i < getMainPanel().getTabCount(); i++) {
                        Component tab = getMainPanel().getComponentAt(i);
                        if (tab instanceof ServiceDeploymentUIPanel) {
                            ((ServiceDeploymentUIPanel) tab).resetGUI();
                        }
                        if (tab instanceof DeploymentUIPanel) {
                            ((DeploymentUIPanel) tab).resetGUI();
                        }
                    }
                }
            });
        }
        return mainPanel;
    }


    private void resetGUI() {
        getDeployButton().setEnabled(getAdvancedDeploymentPanel().validateInput());
        serviceDeploymentNameTextField.setText(getAdvancedDeploymentPanel().getDeploymentProperties().getProperty(
            IntroduceConstants.INTRODUCE_DEPLOYMENT_PREFIX_PROPERTY)
            + "/" + info.getServices().getService(0).getName());
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new JPanel();
            buttonPanel.add(getDeployButton(), null);
        }
        return buttonPanel;
    }


    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getDeployButton() {
        if (deployButton == null) {
            deployButton = new JButton();
            deployButton.setText("Deploy");
            deployButton.setIcon(IntroduceLookAndFeel.getDeployIcon());
            deployButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    BusyDialogRunnable r = new BusyDialogRunnable(GridApplication.getContext().getApplication(),
                        "Deployment") {

                        public void process() {

                            setProgressText("calling pre deployment callbacks...");

                            try {
                                for (int i = 0; i < getMainPanel().getTabCount(); i++) {
                                    Component tab = getMainPanel().getComponentAt(i);
                                    if (tab instanceof ServiceDeploymentUIPanel) {
                                        ((ServiceDeploymentUIPanel) tab).preDeploy();
                                    }
                                    if (tab instanceof DeploymentUIPanel) {
                                        ((DeploymentUIPanel) tab).preDeploy();
                                    }
                                    if (tab instanceof ResourcePropertyEditorPanel) {
                                        ((ResourcePropertyEditorPanel) tab).validateResourceProperty();
                                        // need to save the changes
                                        // write the xml file back out for this
                                        // properties
                                        FileWriter fw;
                                        try {
                                            fw = new FileWriter(info.getBaseDirectory()
                                                + File.separator
                                                + "etc"
                                                + File.separator
                                                + ((ResourcePropertyEditorPanel) tab).getResourcePropertyType()
                                                    .getFileLocation());
                                            // TODO: validate here?
                                            fw.write(XMLUtilities.formatXML(((ResourcePropertyEditorPanel) tab)
                                                .getResultRPString()));
                                            fw.close();
                                        } catch (Exception e1) {
                                            e1.printStackTrace();
                                            ErrorDialog.showError("ERROR: Invalid XML Document", e1);
                                            return;
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                setErrorMessage("Error: " + e.getMessage());
                                return;
                            }

                            setProgressText("setting introduce resource properties...");

                            try {
                                ResourceManager.setStateProperty(ResourceManager.LAST_DEPLOYMENT,
                                    (String) deploymentTypeSelector.getSelectedItem());
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }

                            setProgressText("writing deployment property file...");

                            info.setDeplymentProperties(getAdvancedDeploymentPanel().getDeploymentProperties());

                            try {
                                info.getDeploymentProperties().store(
                                    new FileOutputStream(new File(serviceDirectory.getAbsolutePath() + File.separator
                                        + "deploy.properties")), "introduce service deployment properties");
                            } catch (FileNotFoundException ex) {
                                ex.printStackTrace();
                                setErrorMessage("Error: " + ex.getMessage());
                            } catch (IOException ex) {
                                ex.printStackTrace();
                                setErrorMessage("Error: " + ex.getMessage());
                            }

                            Properties serviceProps = getServicePropertiesPanel().getServiceProperties();

                            try {
                                serviceProps.store(new FileOutputStream(new File(serviceDirectory.getAbsolutePath()
                                    + File.separator + IntroduceConstants.INTRODUCE_SERVICE_PROPERTIES)),
                                    "service deployment properties");
                            } catch (FileNotFoundException ex) {
                                ex.printStackTrace();
                                setErrorMessage("Error: " + ex.getMessage());
                            } catch (IOException ex) {
                                ex.printStackTrace();
                                setErrorMessage("Error: " + ex.getMessage());
                            }

                            setProgressText("deploying");

                            try {
                                String cmd = "";
                                if (((String) getDeploymentTypeSelector().getSelectedItem()).equals(GLOBUS)) {
                                    cmd = AntTools.getAntDeployGlobusCommand(serviceDirectory.getAbsolutePath());
                                } else if (((String) getDeploymentTypeSelector().getSelectedItem()).equals(TOMCAT)) {
                                    cmd = AntTools.getAntDeployTomcatCommand(serviceDirectory.getAbsolutePath());
                                } else {
                                    cmd = AntTools.getAntDeployJBossCommand(serviceDirectory.getAbsolutePath());
                                }
                                Process p = CommonTools.createAndOutputProcess(cmd);
                                p.waitFor();
                                if (p.exitValue() != 0) {
                                    setErrorMessage("Error deploying service!");
                                }
                            } catch (Exception ex) {
                                setErrorMessage("Error deploying service! " + ex.getMessage());
                                ex.printStackTrace();
                            }
                            dispose();
                        }
                    };
                    Thread th = new Thread(r);
                    th.start();
                }
            });
        }

        return deployButton;
    }


    /**
     * This method initializes deploymetnTypePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getDeploymentTypePanel() {
        if (deploymentTypePanel == null) {
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.fill = GridBagConstraints.BOTH;
            gridBagConstraints7.gridy = 1;
            gridBagConstraints7.weightx = 1.0;
            gridBagConstraints7.insets = new Insets(2, 20, 2, 2);
            gridBagConstraints7.gridx = 1;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.gridx = 0;
            gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints6.gridy = 1;
            containerLocationLabel = new JLabel();
            containerLocationLabel.setText("Container Location");
            containerLocationLabel.setFont(containerLocationLabel.getFont().deriveFont(Font.BOLD));
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.gridy = 0;
            gridBagConstraints2.gridwidth = 2;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.weightx = 1.0;
            deploymentTypePanel = new JPanel();
            deploymentTypePanel.setLayout(new GridBagLayout());
            deploymentTypePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Deployment Location",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), PortalLookAndFeel
                    .getPanelLabelColor()));
            deploymentTypePanel.add(getDeploymentTypeSelector(), gridBagConstraints2);
            deploymentTypePanel.add(containerLocationLabel, gridBagConstraints6);
            deploymentTypePanel.add(getContainerLocationTextField(), gridBagConstraints7);
        }
        return deploymentTypePanel;
    }


    /**
     * This method initializes deploymentTypeSelector
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getDeploymentTypeSelector() {
        if (deploymentTypeSelector == null) {
            deploymentTypeSelector = new JComboBox();
            deploymentTypeSelector.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (((String) deploymentTypeSelector.getSelectedItem()).equals(TOMCAT)) {
                        getContainerLocationTextField().setText(System.getenv(IntroduceConstants.TOMCAT));
                    } else if (((String) deploymentTypeSelector.getSelectedItem()).equals(GLOBUS)) {
                        getContainerLocationTextField().setText(System.getenv(IntroduceConstants.GLOBUS));
                    } else if (((String) deploymentTypeSelector.getSelectedItem()).equals(JBOSS)) {
                        getContainerLocationTextField().setText(System.getenv(IntroduceConstants.JBOSS));
                    }
                }
            });

            if (System.getenv(IntroduceConstants.TOMCAT) != null) {
                deploymentTypeSelector.addItem(TOMCAT);
            }
            if (System.getenv(IntroduceConstants.GLOBUS) != null) {
                deploymentTypeSelector.addItem(GLOBUS);
            }
            if (System.getenv(IntroduceConstants.JBOSS) != null) {
                deploymentTypeSelector.addItem(JBOSS);
            }
            try {
                if (ResourceManager.getStateProperty(ResourceManager.LAST_DEPLOYMENT) != null) {
                    boolean found = false;
                    for (int i = 0; i < deploymentTypeSelector.getItemCount(); i++) {
                        if (((String) deploymentTypeSelector.getItemAt(i)).equals(ResourceManager
                            .getStateProperty(ResourceManager.LAST_DEPLOYMENT))) {
                            found = true;
                        }
                    }
                    if (found) {
                        deploymentTypeSelector.setSelectedItem(ResourceManager
                            .getStateProperty(ResourceManager.LAST_DEPLOYMENT));
                    }
                }
            } catch (Exception e) {
                logger.error(e);
            }
        }
        return deploymentTypeSelector;
    }


    /**
     * This method initializes defaultPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getDefaultPanel() {
        if (defaultPanel == null) {
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.weightx = 1.0D;
            gridBagConstraints.weighty = 1.0D;
            gridBagConstraints.gridy = 2;
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridx = 0;
            gridBagConstraints11.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints11.gridy = 0;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.gridy = 3;
            gridBagConstraints1.anchor = java.awt.GridBagConstraints.SOUTH;
            gridBagConstraints1.weighty = 0.0D;
            gridBagConstraints1.weightx = 1.0D;
            gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.gridheight = 1;
            defaultPanel = new JPanel();
            defaultPanel.setLayout(new GridBagLayout());
            defaultPanel.add(getButtonPanel(), gridBagConstraints1);
            defaultPanel.add(getDeploymentTypePanel(), gridBagConstraints11);
            defaultPanel.add(getDeploymentInformationPanel(), gridBagConstraints);
        }
        return defaultPanel;
    }


    /**
     * This method initializes holderPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getHolderPanel() {
        if (holderPanel == null) {
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = GridBagConstraints.BOTH;
            gridBagConstraints3.weighty = 1.0;
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.gridy = 0;
            gridBagConstraints3.weightx = 1.0;
            holderPanel = new JPanel();
            holderPanel.setLayout(new GridBagLayout());
            holderPanel.add(getMainPanel(), gridBagConstraints3);
        }
        return holderPanel;
    }


    /**
     * This method initializes advancedDeploymentPanel
     * 
     * @return javax.swing.JPanel
     */
    private DeploymentPropertiesPanel getAdvancedDeploymentPanel() {
        if (advancedDeploymentPanel == null) {
            advancedDeploymentPanel = new DeploymentPropertiesPanel(info.getDeploymentProperties());
        }
        return advancedDeploymentPanel;
    }


    /**
     * This method initializes servicePropertiesPanel
     * 
     * @return javax.swing.JPanel
     */
    private ServicePropertiesPanel getServicePropertiesPanel() {
        if (servicePropertiesPanel == null) {
            servicePropertiesPanel = new ServicePropertiesPanel(info);
        }
        return servicePropertiesPanel;
    }


    /**
     * This method initializes deploymentInformationPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getDeploymentInformationPanel() {
        if (deploymentInformationPanel == null) {
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.fill = GridBagConstraints.BOTH;
            gridBagConstraints12.gridy = 0;
            gridBagConstraints12.weightx = 1.0;
            gridBagConstraints12.insets = new Insets(2, 20, 2, 2);
            gridBagConstraints12.gridx = 1;
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.gridx = 0;
            gridBagConstraints10.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints10.fill = GridBagConstraints.BOTH;
            gridBagConstraints10.gridy = 0;
            serviceLocationLabel = new JLabel();
            serviceLocationLabel.setText("Service Location");
            serviceLocationLabel.setFont(serviceLocationLabel.getFont().deriveFont(Font.BOLD));
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.fill = GridBagConstraints.BOTH;
            gridBagConstraints9.gridy = 2;
            gridBagConstraints9.weightx = 1.0;
            gridBagConstraints9.insets = new Insets(2, 20, 2, 2);
            gridBagConstraints9.gridx = 1;
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.gridx = 0;
            gridBagConstraints8.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints8.fill = GridBagConstraints.BOTH;
            gridBagConstraints8.gridy = 2;
            serviceNamespaceLabel = new JLabel();
            serviceNamespaceLabel.setText("Service Namespace");
            serviceNamespaceLabel.setFont(serviceNamespaceLabel.getFont().deriveFont(Font.BOLD));
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints5.gridy = 1;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.fill = GridBagConstraints.BOTH;
            gridBagConstraints4.gridy = 1;
            gridBagConstraints4.weightx = 1.0;
            gridBagConstraints4.insets = new Insets(2, 20, 2, 2);
            gridBagConstraints4.gridx = 1;
            serviceDeploymentNameLabel = new JLabel();
            serviceDeploymentNameLabel.setText("Service Deployment Name");
            serviceDeploymentNameLabel.setFont(serviceDeploymentNameLabel.getFont().deriveFont(Font.BOLD));
            deploymentInformationPanel = new JPanel();
            deploymentInformationPanel.setLayout(new GridBagLayout());
            deploymentInformationPanel.setBorder(BorderFactory.createTitledBorder(null, "Deployment Information",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12),
                IntroduceLookAndFeel.getPanelLabelColor()));
            deploymentInformationPanel.add(serviceDeploymentNameLabel, gridBagConstraints5);
            deploymentInformationPanel.add(getServiceDeploymentNameTextField(), gridBagConstraints4);
            deploymentInformationPanel.add(serviceNamespaceLabel, gridBagConstraints8);
            deploymentInformationPanel.add(getServiceNamespaceTextField(), gridBagConstraints9);
            deploymentInformationPanel.add(serviceLocationLabel, gridBagConstraints10);
            deploymentInformationPanel.add(getServiceLocationTextField(), gridBagConstraints12);
        }
        return deploymentInformationPanel;
    }


    /**
     * This method initializes serviceDeploymentNameTextField
     * 
     * @return javax.swing.JTextField
     */
    private JLabel getServiceDeploymentNameTextField() {
        if (serviceDeploymentNameTextField == null) {
            serviceDeploymentNameTextField = new JLabel();
            serviceDeploymentNameTextField.setText(getAdvancedDeploymentPanel().getDeploymentProperties().getProperty(
                IntroduceConstants.INTRODUCE_DEPLOYMENT_PREFIX_PROPERTY)
                + "/" + info.getServices().getService(0).getName());
            serviceDeploymentNameTextField.setForeground(IntroduceLookAndFeel.getPanelLabelColor());
        }
        return serviceDeploymentNameTextField;
    }


    /**
     * This method initializes containerLocationTextField
     * 
     * @return javax.swing.JTextField
     */
    private JLabel getContainerLocationTextField() {
        if (containerLocationTextField == null) {
            containerLocationTextField = new JLabel();
            containerLocationTextField.setFont(containerLocationTextField.getFont().deriveFont(Font.ITALIC));
        }
        return containerLocationTextField;
    }


    /**
     * This method initializes serviceNamespaceTextField
     * 
     * @return javax.swing.JTextField
     */
    private JLabel getServiceNamespaceTextField() {
        if (serviceNamespaceTextField == null) {
            serviceNamespaceTextField = new JLabel();
            serviceNamespaceTextField.setForeground(IntroduceLookAndFeel.getPanelLabelColor());
            serviceNamespaceTextField.setText(info.getServices().getService(0).getNamespace());
        }
        return serviceNamespaceTextField;
    }


    /**
     * This method initializes serviceLocationTextField
     * 
     * @return javax.swing.JTextField
     */
    private JLabel getServiceLocationTextField() {
        if (serviceLocationTextField == null) {
            serviceLocationTextField = new JLabel();
            serviceLocationTextField.setForeground(IntroduceLookAndFeel.getPanelLabelColor());
            serviceLocationTextField.setText(info.getBaseDirectory().getAbsolutePath());
        }
        return serviceLocationTextField;
    }
} // @jve:decl-index=0:visual-constraint="10,10"
