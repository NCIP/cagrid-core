package gov.nih.nci.cagrid.sdkquery4.style.wizard;

import gov.nih.nci.cagrid.common.JarUtilities;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.ExtensionDataUtils;
import gov.nih.nci.cagrid.data.common.ModelInformationUtil;
import gov.nih.nci.cagrid.data.extension.Data;
import gov.nih.nci.cagrid.data.extension.ModelClass;
import gov.nih.nci.cagrid.data.extension.ModelInformation;
import gov.nih.nci.cagrid.data.extension.ModelPackage;
import gov.nih.nci.cagrid.data.style.sdkstyle.wizard.PackageSchemaMappingErrorDialog;
import gov.nih.nci.cagrid.data.ui.SchemaResolutionDialog;
import gov.nih.nci.cagrid.data.ui.wizard.AbstractWizardPanel;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.SchemaElementType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ConfigurationUtil;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.sdkquery4.processor.SDK4QueryProcessor;
import gov.nih.nci.cagrid.sdkquery4.style.wizard.config.SchemaMappingConfigurationStep;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.gme.client.GlobalModelExchangeClient;
import org.cagrid.gme.domain.XMLSchemaNamespace;
import org.cagrid.grape.LookAndFeel;
import org.cagrid.grape.utils.CompositeErrorDialog;


/**
 * SchemaMappingPanel Panel to configure mapping of packages to schemas
 * 
 * @author David Ervin
 * @created Jan 9, 2008 11:09:22 AM
 * @version $Id: SchemaMappingPanel.java,v 1.9 2009-01-29 20:14:18 dervin Exp $
 */
public class SchemaMappingPanel extends AbstractWizardPanel {

    private PackageToNamespaceTable packageNamespaceTable = null;
    private JScrollPane packageNamespaceScrollPane = null;
    private JLabel gmeUrlLabel = null;
    private JTextField gmeUrlTextField = null;
    private JLabel configDirLabel = null;
    private JTextField configDirTextField = null;
    private JButton gmeMapButton = null;
    private JButton configMapButton = null;
    private JPanel automapPanel = null;
    private JPanel mappingPanel = null;

    private SchemaMappingConfigurationStep configuration = null;
    private ModelInformationUtil modelInfoUtil = null;

    public SchemaMappingPanel(ServiceExtensionDescriptionType extensionDescription, ServiceInformation info) {
        super(extensionDescription, info);
        configuration = new SchemaMappingConfigurationStep(info);
        modelInfoUtil = new ModelInformationUtil(info.getServiceDescriptor());
        initialize();
    }


    public String getPanelShortName() {
        return "Schemas";
    }


    public String getPanelTitle() {
        return "Package to Schema Mapping";
    }


    public void update() {
        // populate the package to namespace table from the extension data
        try {
            Data data = ExtensionDataUtils.getExtensionData(getExtensionData());
            ModelInformation info = data.getModelInformation();
            Set<String> currentPackageNames = new HashSet<String>();
            for (int i = 0; i < getPackageNamespaceTable().getRowCount(); i++) {
                currentPackageNames.add((String) getPackageNamespaceTable().getValueAt(i, 0));
            }
            if (info != null && info.getModelPackage() != null) {
                ModelPackage[] packs = info.getModelPackage();
                if (packs != null && packs.length != 0) {
                    // add any new packages to the table
                    for (int i = 0; i < packs.length; i++) {
                        if (!getPackageNamespaceTable().isPackageInTable(packs[i])) {
                            getPackageNamespaceTable().addNewModelPackage(getServiceInformation(), packs[i]);
                        }
                        currentPackageNames.remove(packs[i].getPackageName());
                    }
                }
            }
            Iterator invalidPackageNameIter = currentPackageNames.iterator();
            while (invalidPackageNameIter.hasNext()) {
                String invalidName = (String) invalidPackageNameIter.next();
                getPackageNamespaceTable().removeCadsrPackage(invalidName);
            }
            setWizardComplete(allSchemasResolved());
        } catch (Exception ex) {
            ex.printStackTrace();
            CompositeErrorDialog.showErrorDialog("Error populating the packages table", ex);
        }
    }


    public void movingNext() {
        try {
            configuration.applyConfiguration();
        } catch (Exception ex) {
            ex.printStackTrace();
            CompositeErrorDialog.showErrorDialog("Error applying configuration", ex.getMessage(), ex);
        }
    }


    private void initialize() {
        this.setLayout(new GridLayout());
        GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
        gridBagConstraints6.fill = GridBagConstraints.BOTH;
        gridBagConstraints6.gridy = 1;
        gridBagConstraints6.weightx = 1.0;
        gridBagConstraints6.weighty = 1.0;
        gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints6.gridx = 0;
        GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
        gridBagConstraints5.gridx = 0;
        gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints5.fill = GridBagConstraints.BOTH;
        gridBagConstraints5.gridy = 0;
        this.setLayout(new GridBagLayout());
        this.add(getMappingPanel(), gridBagConstraints5);
        this.add(getPackageNamespaceScrollPane(), gridBagConstraints6);
    }


    private PackageToNamespaceTable getPackageNamespaceTable() {
        if (this.packageNamespaceTable == null) {
            this.packageNamespaceTable = new PackageToNamespaceTable(modelInfoUtil);
            this.packageNamespaceTable.getModel().addTableModelListener(new TableModelListener() {
                public void tableChanged(TableModelEvent e) {
                    if (e.getType() == TableModelEvent.UPDATE) {
                        setWizardComplete(allSchemasResolved());
                    }
                }
            });
            // handler for resolving individual packages
            this.packageNamespaceTable.setSchemaResolutionHandler(new SchemaResolutionHandler() {
                public SchemaResolutionStatus resolveSchemaForPackage(ServiceInformation serviceInfo, String packageName) {
                    SchemaResolutionStatus status = null;
                    try {
                        status = resolveSingleSchema(packageName);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        CompositeErrorDialog.showErrorDialog("Error resolving schema", ex.getMessage(), ex);
                        status = SchemaResolutionStatus.MAPPING_ERROR;
                    }
                    return status;
                }
            });
        }
        return this.packageNamespaceTable;
    }


    /**
     * This method initializes packageNamespaceScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getPackageNamespaceScrollPane() {
        if (packageNamespaceScrollPane == null) {
            packageNamespaceScrollPane = new JScrollPane();
            packageNamespaceScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            packageNamespaceScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            packageNamespaceScrollPane.setViewportView(getPackageNamespaceTable());
            packageNamespaceScrollPane.setBorder(BorderFactory.createTitledBorder(null, "Namespace Mappings",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, LookAndFeel
                    .getPanelLabelColor()));
        }
        return packageNamespaceScrollPane;
    }


    /**
     * This method initializes gmeUrlLabel
     * 
     * @return javax.swing.JLabel
     */
    private JLabel getGmeUrlLabel() {
        if (gmeUrlLabel == null) {
            gmeUrlLabel = new JLabel();
            gmeUrlLabel.setText("GME Url:");
        }
        return gmeUrlLabel;
    }


    /**
     * This method initializes gmeUrlTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getGmeUrlTextField() {
        if (gmeUrlTextField == null) {
            gmeUrlTextField = new JTextField();
            try {
                String url = ConfigurationUtil.getGlobalExtensionProperty(DataServiceConstants.GME_SERVICE_URL)
                    .getValue();
                gmeUrlTextField.setText(url);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        }
        return gmeUrlTextField;
    }


    /**
     * This method initializes configDirLabel
     * 
     * @return javax.swing.JLabel
     */
    private JLabel getConfigDirLabel() {
        if (configDirLabel == null) {
            configDirLabel = new JLabel();
            configDirLabel.setText("Client Config Dir:");
        }
        return configDirLabel;
    }


    /**
     * This method initializes configDirTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getConfigDirTextField() {
        if (configDirTextField == null) {
            configDirTextField = new JTextField();
            configDirTextField.setEditable(false);
        }
        return configDirTextField;
    }


    /**
     * This method initializes gmeMapButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getGmeMapButton() {
        if (gmeMapButton == null) {
            gmeMapButton = new JButton();
            gmeMapButton.setText("Map From GME");
            gmeMapButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    mapFromGme();
                }
            });
        }
        return gmeMapButton;
    }


    /**
     * This method initializes configMapButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getConfigMapButton() {
        if (configMapButton == null) {
            configMapButton = new JButton();
            configMapButton.setText("Map From Config");
            configMapButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    mapFromConfig();
                }
            });
        }
        return configMapButton;
    }


    /**
     * This method initializes automapPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getAutomapPanel() {
        if (automapPanel == null) {
            GridLayout gridLayout = new GridLayout();
            gridLayout.setRows(1);
            gridLayout.setHgap(4);
            gridLayout.setColumns(2);
            automapPanel = new JPanel();
            automapPanel.setLayout(gridLayout);
            automapPanel.setBorder(BorderFactory.createTitledBorder(null, "Automatic Mapping",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, LookAndFeel
                    .getPanelLabelColor()));
            automapPanel.add(getGmeMapButton(), null);
            automapPanel.add(getConfigMapButton(), null);
        }
        return automapPanel;
    }


    /**
     * This method initializes mappingPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMappingPanel() {
        if (mappingPanel == null) {
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints4.gridwidth = 2;
            gridBagConstraints4.gridy = 2;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridy = 1;
            gridBagConstraints3.weightx = 1.0;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.gridx = 1;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.gridy = 1;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.weightx = 1.0;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.gridx = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.gridy = 0;
            mappingPanel = new JPanel();
            mappingPanel.setLayout(new GridBagLayout());
            mappingPanel.add(getGmeUrlLabel(), gridBagConstraints);
            mappingPanel.add(getGmeUrlTextField(), gridBagConstraints1);
            mappingPanel.add(getConfigDirLabel(), gridBagConstraints2);
            mappingPanel.add(getConfigDirTextField(), gridBagConstraints3);
            mappingPanel.add(getAutomapPanel(), gridBagConstraints4);
        }
        return mappingPanel;
    }


    // ---------
    // helpers
    // ---------

    private boolean allSchemasResolved() {
        for (int i = 0; i < getPackageNamespaceTable().getRowCount(); i++) {
            SchemaResolutionStatus status = (SchemaResolutionStatus) getPackageNamespaceTable().getValueAt(i, 2);
            if (status != SchemaResolutionStatus.SCHEMA_FOUND) {
                return false;
            }
        }
        return true;
    }


    private void mapFromConfig() {
        File schemaDir = getServiceSchemaDirectory();

        // the config dir jar will have the xsds in it
        try {
            String applicationName = CommonTools.getServicePropertyValue(
                getServiceInformation().getServiceDescriptor(), DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX
                    + SDK4QueryProcessor.PROPERTY_APPLICATION_NAME);
            String configJarFilename = getServiceInformation().getBaseDirectory().getAbsolutePath() + File.separator
                + "lib" + File.separator + applicationName + "-config.jar";
            JarFile configJar = new JarFile(configJarFilename);
            Enumeration<JarEntry> entries = configJar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".xsd")) {
                    // found a schema, what package does it go with?
                    String schemaPackageName = new File(entry.getName()).getName();
                    schemaPackageName = schemaPackageName.substring(0, schemaPackageName.length() - 4);
                    for (int i = 0; i < getPackageNamespaceTable().getRowCount(); i++) {
                        String packageName = (String) getPackageNamespaceTable().getValueAt(i, 0);
                        if (packageName.equals(schemaPackageName)) {
                            // extract the schema contents and create the XSD
                            StringBuffer schemaText = JarUtilities.getFileContents(configJar, entry.getName());
                            File schemaFile = new File(schemaDir, new File(entry.getName()).getName());
                            Utils.stringBufferToFile(schemaText, schemaFile.getAbsolutePath());
                            
                            // add the namespace to the configuration for later
                            // incorperation in the service
                            String schemaNamespace = configuration.mapPackageToSchema(packageName, schemaFile);

                            // set the namespace in the table
                            getPackageNamespaceTable().setValueAt(schemaNamespace, i, 1);
                            // set the status to found in the table
                            getPackageNamespaceTable().setValueAt(SchemaResolutionStatus.SCHEMA_FOUND, i, 2);
                            break;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            CompositeErrorDialog.showErrorDialog("Error loading application configuration files", ex.getMessage(), ex);
        }
    }


    private void mapFromGme() {
        File schemaDir = getServiceSchemaDirectory();

        try {
            GlobalModelExchangeClient gmeHandle = getGmeHandle();
            // get the selected packages from the schema table
            for (int row = 0; row < getPackageNamespaceTable().getRowCount(); row++) {
                String packageName = (String) getPackageNamespaceTable().getValueAt(row, 0);
                String proposedNamespace = (String) getPackageNamespaceTable().getValueAt(row, 1);
                // extract the domain portion of the namespace
                XMLSchemaNamespace gmeNamespace = new XMLSchemaNamespace(proposedNamespace);

                // pull the schema (and it's imports) down for local use
                Map<XMLSchemaNamespace, File> cachedNamespaces = gmeHandle.cacheSchemas(gmeNamespace, schemaDir);
                // each namespace corresponds to a schema file stored in the
                // schema directory
                boolean namespaceFound = false;
                for (XMLSchemaNamespace ns : cachedNamespaces.keySet()) {
                    if (ns.getURI().toString().equals(proposedNamespace)) {
                        // map the package to that namespace in the configuration
                        configuration.mapPackageToSchema(packageName, cachedNamespaces.get(ns));
                        namespaceFound = true;
                        break;
                    }
                }
                if (namespaceFound) {
                    // change the status in the table to found
                    getPackageNamespaceTable().setValueAt(SchemaResolutionStatus.SCHEMA_FOUND, row, 2);
                } else {
                    // namespace not found, but domain exists
                    getPackageNamespaceTable().setValueAt(SchemaResolutionStatus.GME_NAMESPACE_NOT_FOUND, row, 2);
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            CompositeErrorDialog.showErrorDialog("Error retrieving schemas from the GME", ex);
        }
    }


    private GlobalModelExchangeClient getGmeHandle() throws MalformedURIException, RemoteException {
        return new GlobalModelExchangeClient(getGmeUrlTextField().getText());
    }


    private SchemaResolutionStatus resolveSingleSchema(String packageName) throws Exception {
        // determine the row of this package in the table
        int dataRow = 0;
        while (!packageName.equals(getPackageNamespaceTable().getValueAt(dataRow, 0))
            && dataRow <= getPackageNamespaceTable().getRowCount()) {
            dataRow++;
        }
        if (dataRow == getPackageNamespaceTable().getRowCount()) {
            CompositeErrorDialog.showErrorDialog("Error locating table row for package " + packageName);
            return SchemaResolutionStatus.MAPPING_ERROR; // bail out
        }

        SchemaResolutionStatus status = SchemaResolutionStatus.NEVER_TRIED;
        File schemaDir = getServiceSchemaDirectory();
        // use the schema resolution dialog to create namespace types
        // and copy schemas to the service's schema directory
        NamespaceType[] resolved = SchemaResolutionDialog.resolveSchemas(getServiceInformation());
        if (resolved != null) {
            ModelPackage pack = getNamedCadsrPackage(packageName);
            if (resolved.length != 0 && packageResolvedByNamespace(pack, resolved[0])) {
                File primarySchemaFile = new File(schemaDir, resolved[0].getLocation());
                configuration.mapPackageToSchema(packageName, primarySchemaFile);
                
                // set the namespace of the resolved schema on the table
                String schemaNamespace = CommonTools.getTargetNamespace(primarySchemaFile);
                getPackageNamespaceTable().setValueAt(schemaNamespace, dataRow, 1);

                status = SchemaResolutionStatus.SCHEMA_FOUND;
            } else {
                // some schema was resolved, but it does not map to the package
                status = SchemaResolutionStatus.MAPPING_ERROR;
            }
        } else {
            CompositeErrorDialog.showErrorDialog("Error retrieving schemas!");
        }
        return status;
    }


    private boolean packageResolvedByNamespace(ModelPackage pack, NamespaceType namespace) {
        Set<String> classNames = new HashSet<String>();
        for (ModelClass clazz : pack.getModelClass()) {
            classNames.add(clazz.getShortClassName());
        }
        Set<String> elementNames = new HashSet<String>();
        for (SchemaElementType element : namespace.getSchemaElement()) {
            elementNames.add(element.getType());
        }

        boolean status = elementNames.containsAll(classNames);
        if (!status) {
            // sort out the resolution errors
            Set<String> nonResolvedClasses = new HashSet<String>();
            nonResolvedClasses.addAll(classNames);
            nonResolvedClasses.removeAll(elementNames);

            // display the errors
            new PackageSchemaMappingErrorDialog(nonResolvedClasses);
        }

        return status;
    }


    private ModelPackage getNamedCadsrPackage(String packageName) throws Exception {
        Data extensionData = ExtensionDataUtils.getExtensionData(getExtensionData());
        ModelPackage[] modelPackages = extensionData.getModelInformation().getModelPackage();
        if (modelPackages != null) {
            for (ModelPackage pack : modelPackages) {
                if (pack.getPackageName().equals(packageName)) {
                    return pack;
                }
            }
        }
        return null;
    }


    private File getServiceSchemaDirectory() {
        File schemaDir = new File(getServiceInformation().getBaseDirectory(), "schema"
            + File.separator
            + getServiceInformation().getIntroduceServiceProperties().getProperty(
                IntroduceConstants.INTRODUCE_SKELETON_SERVICE_NAME));
        return schemaDir;
    }
}
