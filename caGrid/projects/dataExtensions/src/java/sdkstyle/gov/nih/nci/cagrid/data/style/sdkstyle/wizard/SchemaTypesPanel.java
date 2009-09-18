package gov.nih.nci.cagrid.data.style.sdkstyle.wizard;

import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.ExtensionDataUtils;
import gov.nih.nci.cagrid.data.common.ModelInformationUtil;
import gov.nih.nci.cagrid.data.extension.Data;
import gov.nih.nci.cagrid.data.extension.ModelInformation;
import gov.nih.nci.cagrid.data.extension.ModelPackage;
import gov.nih.nci.cagrid.data.extension.ModelSourceType;
import gov.nih.nci.cagrid.data.ui.wizard.AbstractWizardPanel;
import gov.nih.nci.cagrid.data.ui.wizard.CacoreWizardUtils;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.SchemaElementType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ConfigurationUtil;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.gme.client.GlobalModelExchangeClient;
import org.cagrid.gme.discoverytools.NamespaceTools;
import org.cagrid.gme.domain.XMLSchemaNamespace;
import org.cagrid.gme.stubs.types.NoSuchNamespaceExistsFault;
import org.cagrid.grape.utils.CompositeErrorDialog;


/**
 * SchemaTypesPanel Panel to match up schema types with exposed packages from a
 * domain model
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @created Sep 26, 2006
 * @version $Id: SchemaTypesPanel.java,v 1.11 2009-01-29 19:23:56 dervin Exp $
 */
public class SchemaTypesPanel extends AbstractWizardPanel {

    public static final String TYPE_SERIALIZER_CLASS_PROPERTY = "serializerClassName";
    public static final String TYPE_DESERIALIZER_CLASS_PROPERTY = "deserializerClassName";

    private JLabel gmeUrlLabel = null;
    private JTextField gmeUrlTextField = null;
    private JButton gmeSchemasButton = null;
    private JPanel gmePanel = null;
    private JScrollPane packageNamespaceScrollPane = null;
    private PackageSchemasTable packageNamespaceTable = null;
    
    private ModelInformationUtil modelInfoUtil = null;


    public SchemaTypesPanel(ServiceExtensionDescriptionType extensionDescription, ServiceInformation info) {
        super(extensionDescription, info);
        this.modelInfoUtil = new ModelInformationUtil(info.getServiceDescriptor());
        initialize();
    }


    @Override
    public void update() {
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
                            getPackageNamespaceTable().addNewCadsrPackage(getServiceInformation(), packs[i]);
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


    @Override
    public String getPanelTitle() {
        return "Schema Type Selection";
    }


    @Override
    public String getPanelShortName() {
        return "Schemas";
    }


    private void initialize() {
        GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
        gridBagConstraints4.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints4.gridy = 1;
        gridBagConstraints4.weightx = 1.0;
        gridBagConstraints4.weighty = 1.0;
        gridBagConstraints4.gridx = 0;
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.gridx = 0;
        gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints3.gridy = 0;
        this.setLayout(new GridBagLayout());
        this.add(getGmePanel(), gridBagConstraints3);
        this.add(getPackageNamespaceScrollPane(), gridBagConstraints4);
    }


    /**
     * This method initializes jLabel
     * 
     * @return javax.swing.JLabel
     */
    private JLabel getGmeUrlLabel() {
        if (this.gmeUrlLabel == null) {
            this.gmeUrlLabel = new JLabel();
            this.gmeUrlLabel.setText("GME URL:");
        }
        return this.gmeUrlLabel;
    }


    /**
     * This method initializes jTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getGmeUrlTextField() {
        if (this.gmeUrlTextField == null) {
            this.gmeUrlTextField = new JTextField();
            String url;
            try {
                url = ConfigurationUtil.getGlobalExtensionProperty(DataServiceConstants.GME_SERVICE_URL).getValue();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            this.gmeUrlTextField.setText(url);
        }
        return this.gmeUrlTextField;
    }


    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getGmeSchemasButton() {
        if (this.gmeSchemasButton == null) {
            this.gmeSchemasButton = new JButton();
            this.gmeSchemasButton.setText("Find Schemas Using GME");
            this.gmeSchemasButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    findSchemas();
                }
            });
        }
        return this.gmeSchemasButton;
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getGmePanel() {
        if (this.gmePanel == null) {
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.gridwidth = 2;
            gridBagConstraints2.gridy = 1;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.weightx = 1.0;
            gridBagConstraints1.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints1.gridx = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints.gridy = 0;
            this.gmePanel = new JPanel();
            this.gmePanel.setLayout(new GridBagLayout());
            this.gmePanel.add(getGmeUrlLabel(), gridBagConstraints);
            this.gmePanel.add(getGmeUrlTextField(), gridBagConstraints1);
            this.gmePanel.add(getGmeSchemasButton(), gridBagConstraints2);
        }
        return this.gmePanel;
    }


    /**
     * This method initializes jScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getPackageNamespaceScrollPane() {
        if (this.packageNamespaceScrollPane == null) {
            this.packageNamespaceScrollPane = new JScrollPane();
            this.packageNamespaceScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            this.packageNamespaceScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
                "Schema Mappings", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, PortalLookAndFeel.getPanelLabelColor()));
            this.packageNamespaceScrollPane.setViewportView(getPackageNamespaceTable());
        }
        return this.packageNamespaceScrollPane;
    }


    /**
     * This method initializes jTable
     * 
     * @return javax.swing.JTable
     */
    private PackageSchemasTable getPackageNamespaceTable() {
        if (this.packageNamespaceTable == null) {
            this.packageNamespaceTable = new PackageSchemasTable(modelInfoUtil, getBitBucket());
            this.packageNamespaceTable.getModel().addTableModelListener(new TableModelListener() {
                public void tableChanged(TableModelEvent e) {
                    if (e.getType() == TableModelEvent.UPDATE) {
                        setWizardComplete(allSchemasResolved());
                        try {
                            storePackageMappings();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            CompositeErrorDialog.showErrorDialog("Error storing namespace mappings", ex);
                        }
                    }
                }
            });
        }
        return this.packageNamespaceTable;
    }


    private void findSchemas() {
        // get the GME handle
        try {
            GlobalModelExchangeClient gmeHandle = getGmeHandle();

            // get the selected packages
            Data data = ExtensionDataUtils.getExtensionData(getExtensionData());
            ModelInformation info = data.getModelInformation();
            if (info != null && info.getModelPackage() != null) {
                ModelPackage[] packs = info.getModelPackage();
                for (int i = 0; i < packs.length; i++) {
                    NamespaceType nsType = modelInfoUtil.getMappedNamespace(packs[i].getPackageName());
                    if (nsType != null) {
                        XMLSchemaNamespace ns = new XMLSchemaNamespace(nsType.getNamespace());
                        try {
                            gmeHandle.getXMLSchema(ns);
                        } catch (NoSuchNamespaceExistsFault e) {
                            continue;
                        }

                        // found the namespace as well download the schema locally
                        pullSchemas(ns, gmeHandle);
                        // change the package namespace table to reflect the found
                        // schema
                        for (int row = 0; row < getPackageNamespaceTable().getRowCount(); row++) {
                            if (getPackageNamespaceTable().getValueAt(row, 0).equals(packs[i].getPackageName())) {
                                getPackageNamespaceTable().setValueAt(PackageSchemasTable.STATUS_SCHEMA_FOUND, row, 2);
                                break;
                            }
                        }
                    } else {
                        // package isn't yet mapped to any namespace
                        CompositeErrorDialog.showErrorDialog("Package " 
                            + packs[i].getPackageName() 
                            + " is not yet mapped to a namespace which can be retrieved from the GME");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(SchemaTypesPanel.this, "No packages to find schemas for");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            CompositeErrorDialog.showErrorDialog("Error retrieving schemas from the GME", ex);
        }
    }


    private GlobalModelExchangeClient getGmeHandle() throws MalformedURIException, RemoteException {
        return new GlobalModelExchangeClient(getGmeUrlTextField().getText());
    }


    private void pullSchemas(XMLSchemaNamespace ns, GlobalModelExchangeClient gme) throws Exception {
        // get the service's schema dir
        File schemaDir = new File(CacoreWizardUtils.getServiceBaseDir(getServiceInformation())
            + File.separator
            + "schema"
            + File.separator
            + getServiceInformation().getIntroduceServiceProperties().getProperty(
                IntroduceConstants.INTRODUCE_SKELETON_SERVICE_NAME));
        // have the GME cache the schema and its imports locally
        Map<XMLSchemaNamespace, File> cachedNamespaces = gme.cacheSchemas(ns, schemaDir);

        // determine the serializer and deserialzier to use for the beans
        String serializerClass = null;
        if (getBitBucket().containsKey(TYPE_SERIALIZER_CLASS_PROPERTY)) {
            serializerClass = (String) getBitBucket().get(TYPE_SERIALIZER_CLASS_PROPERTY);
        } else {
            serializerClass = DataServiceConstants.SDK_SERIALIZER;
        }
        String deserializerClass = null;
        if (getBitBucket().containsKey(TYPE_DESERIALIZER_CLASS_PROPERTY)) {
            deserializerClass = (String) getBitBucket().get(TYPE_DESERIALIZER_CLASS_PROPERTY);
        } else {
            deserializerClass = DataServiceConstants.SDK_DESERIALIZER;
        }
        // create namespace types and add them to the service
        Iterator nsIter = cachedNamespaces.keySet().iterator();
        while (nsIter.hasNext()) {
            XMLSchemaNamespace storedNs = (XMLSchemaNamespace) nsIter.next();
            //NamespaceType nsType = CommonTools.createNamespaceType(cachedNamespaces.get(storedNs).getAbsolutePath(),
            //    schemaDir);
            
            NamespaceType nsType = NamespaceTools.createNamespaceTypeForFile(cachedNamespaces.get(storedNs).getCanonicalPath(),
                schemaDir);
            // if the namespace already exists in the service, ignore it
            if (CommonTools.getNamespaceType(getServiceInformation().getNamespaces(), nsType.getNamespace()) == null) {
                // set the package name
                // TODO: this should come from what is provided from the list of
                // packages, or look it up in the caDSR
                String packName = CommonTools.getPackageName(storedNs.getURI().toString());
                nsType.setPackageName(packName);
                // fix the serialization / deserialization on the namespace
                // types
                for (int i = 0; nsType.getSchemaElement() != null && i < nsType.getSchemaElement().length; i++) {
                    SchemaElementType type = nsType.getSchemaElement(i);
                    type.setSerializer(serializerClass);
                    type.setDeserializer(deserializerClass);
                    type.setClassName(type.getType());
                }
                nsType.setGenerateStubs(Boolean.FALSE);

                CommonTools.addNamespace(getServiceInformation().getServiceDescriptor(), nsType);
                // add the namespace to the introduce namespace excludes list so
                // that beans will not be built for these data types
                String excludes = getServiceInformation().getIntroduceServiceProperties().getProperty(
                    IntroduceConstants.INTRODUCE_NS_EXCLUDES);
                excludes += " -x " + nsType.getNamespace();
                getServiceInformation().getIntroduceServiceProperties().setProperty(
                    IntroduceConstants.INTRODUCE_NS_EXCLUDES, excludes);
            }
        }
    }


    private boolean allSchemasResolved() {
        for (int i = 0; i < getPackageNamespaceTable().getRowCount(); i++) {
            String status = (String) getPackageNamespaceTable().getValueAt(i, 2);
            if (!status.equals(PackageSchemasTable.STATUS_SCHEMA_FOUND)) {
                return false;
            }
        }
        return true;
    }


    private void storePackageMappings() throws Exception {
        Data data = ExtensionDataUtils.getExtensionData(getExtensionData());
        ModelInformation info = data.getModelInformation();
        if (info == null) {
            info = new ModelInformation();
            info.setSource(ModelSourceType.mms);
            data.setModelInformation(info);
        }
        for (int i = 0; info.getModelPackage() != null && i < info.getModelPackage().length; i++) {
            ModelPackage currentPackage = info.getModelPackage(i);
            // find the package's row in the table
            for (int row = 0; row < getPackageNamespaceTable().getRowCount(); row++) {
                if (currentPackage.getPackageName().equals(getPackageNamespaceTable().getValueAt(row, 0))) {
                    // set the mapped namespace
                    String namespace = (String) getPackageNamespaceTable().getValueAt(row, 1);
                    if (namespace != null && namespace.length() != 0) {
                        modelInfoUtil.setMappedNamespace(currentPackage.getPackageName(), namespace);
                    } else {
                        modelInfoUtil.unsetMappedNamespace(currentPackage.getPackageName());
                    }
                    break;
                }
            }
        }
        ExtensionDataUtils.storeExtensionData(getExtensionData(), data);
    }
}
