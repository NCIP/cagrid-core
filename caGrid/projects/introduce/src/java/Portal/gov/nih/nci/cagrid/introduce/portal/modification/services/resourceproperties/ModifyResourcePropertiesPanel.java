package gov.nih.nci.cagrid.introduce.portal.modification.services.resourceproperties;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;
import gov.nih.nci.cagrid.introduce.beans.extension.ResourcePropertyEditorExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespacesType;
import gov.nih.nci.cagrid.introduce.beans.namespace.SchemaElementType;
import gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertiesListType;
import gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertyType;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.codegen.SyncTools;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.SpecificServiceInformation;
import gov.nih.nci.cagrid.introduce.portal.common.IntroduceLookAndFeel;
import gov.nih.nci.cagrid.introduce.portal.extension.ResourcePropertyEditorPanel;
import gov.nih.nci.cagrid.introduce.portal.extension.tools.ExtensionTools;
import gov.nih.nci.cagrid.introduce.portal.modification.services.resourceproperties.editor.XMLEditorViewer;
import gov.nih.nci.cagrid.introduce.portal.modification.types.NamespaceTypeTreeNode;
import gov.nih.nci.cagrid.introduce.portal.modification.types.NamespacesJTree;
import gov.nih.nci.cagrid.introduce.portal.modification.types.SchemaElementTypeTreeNode;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.utils.ErrorDialog;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import java.awt.Font;
import java.awt.Color;


public class ModifyResourcePropertiesPanel extends JPanel {
    
    private static final Logger logger = Logger.getLogger(ModifyResourcePropertiesPanel.class);

    private JPanel resourcePropertiesPanel = null;

    private JScrollPane namespacesScrollPane = null;

    private JScrollPane resourcePropertiesScrollPane = null;

    private NamespacesJTree namespacesJTree = null;

    private ResourcePropertyTable resourcePropertiesTable = null;

    private JPanel buttonsPanel = null;

    private JButton addResourcePropertyButton = null;

    private JButton removeResourcePropertyButton = null;

    private JSplitPane mainSplitPane = null;

    private boolean showW3Cnamespaces;

    private JButton editInstanceButton = null;

    private boolean isMainMetadataPanel = true;
    
    private SpecificServiceInformation info;


    public ModifyResourcePropertiesPanel(SpecificServiceInformation info, boolean showW3Cnamespaces, boolean isMainMetadataPanel) {
        this.info = info;
        this.showW3Cnamespaces = showW3Cnamespaces;
        this.isMainMetadataPanel = isMainMetadataPanel;
        initialize();
    }


    private void initialize() {
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints2.gridy = 1;
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.weighty = 1.0D;
        gridBagConstraints1.gridx = 0;
        this.setLayout(new GridBagLayout());
        this.setSize(new java.awt.Dimension(314, 211));
        this.add(getMainSplitPane(), gridBagConstraints1);
        this.add(getButtonsPanel(), gridBagConstraints2);
    }


    public void reInitialize(SpecificServiceInformation info) {
        this.info = info;
        this.namespacesJTree.setNamespaces(info.getNamespaces());
        this.resourcePropertiesTable.setResourceProperties(info.getService().getResourcePropertiesList());
    }


    public ResourcePropertyType[] getConfiguredResourceProperties() throws Exception {
        int propertyCount = getResourcePropertiesTable().getRowCount();
        ResourcePropertyType[] configuredProperties = new ResourcePropertyType[propertyCount];
        for (int i = 0; i < propertyCount; i++) {
            configuredProperties[i] = getResourcePropertiesTable().getRowData(i);
        }
        return configuredProperties;
    }


    /**
     * This method initializes resourcePropertiesPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getResourcePropertiesPanel() {
        if (this.resourcePropertiesPanel == null) {
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.gridy = 0;
            gridBagConstraints4.weightx = 1.0;
            gridBagConstraints4.weighty = 1.0;
            gridBagConstraints4.insets = new java.awt.Insets(2, 2, 2, 2);
            this.resourcePropertiesPanel = new JPanel();
            this.resourcePropertiesPanel.setLayout(new GridBagLayout());
            this.resourcePropertiesPanel.add(getResourcePropertiesScrollPane(), gridBagConstraints4);
        }
        return this.resourcePropertiesPanel;
    }


    /**
     * This method initializes namespacesScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getNamespacesScrollPane() {
        if (this.namespacesScrollPane == null) {
            this.namespacesScrollPane = new JScrollPane();
            this.namespacesScrollPane.setPreferredSize(new java.awt.Dimension(200, 240));
            namespacesScrollPane.setBorder(BorderFactory.createTitledBorder(null, "Imported Data Types", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), IntroduceLookAndFeel.getPanelLabelColor()));
            this.namespacesScrollPane.setViewportView(getNamespacesJTree());
        }
        return this.namespacesScrollPane;
    }


    /**
     * This method initializes resourcePropertiesScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getResourcePropertiesScrollPane() {
        if (this.resourcePropertiesScrollPane == null) {
            this.resourcePropertiesScrollPane = new JScrollPane();
            this.resourcePropertiesScrollPane.setViewportView(getResourcePropertiesTable());
        }
        return this.resourcePropertiesScrollPane;
    }


    /**
     * This method initializes namespacesJTree
     * 
     * @return javax.swing.JTree
     */
    private NamespacesJTree getNamespacesJTree() {
        if (this.namespacesJTree == null) {
            this.namespacesJTree = new NamespacesJTree(info.getNamespaces(), this.showW3Cnamespaces);
            this.namespacesJTree.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    if (e.getClickCount() == 2) {
                        addResourceProperty();
                    }
                }
            });
        }
        return this.namespacesJTree;
    }


    /**
     * This method initializes resourcePropertiesTable
     * 
     * @return javax.swing.JTable
     */
    private ResourcePropertyTable getResourcePropertiesTable() {
        if (this.resourcePropertiesTable == null) {
            if (this.isMainMetadataPanel) {
                this.resourcePropertiesTable = new ResourcePropertyTable(info.getService().getResourcePropertiesList(), true);
            } else {
                this.resourcePropertiesTable = new ResourcePropertyTable(info.getService().getResourcePropertiesList(), false);
            }
            SelectionListener listener = new SelectionListener(this.resourcePropertiesTable);
            this.resourcePropertiesTable.getSelectionModel().addListSelectionListener(listener);
            this.resourcePropertiesTable.getColumnModel().getSelectionModel().addListSelectionListener(listener);
            this.resourcePropertiesTable.getModel().addTableModelListener(new TableModelListener() {

                public void tableChanged(TableModelEvent e) {

                    try {

                        if (e.getType() != TableModelEvent.DELETE
                            && ModifyResourcePropertiesPanel.this.resourcePropertiesTable.getSelectedRow() >= 0) {
                            if (ModifyResourcePropertiesPanel.this.resourcePropertiesTable.getRowData(
                                ModifyResourcePropertiesPanel.this.resourcePropertiesTable.getSelectedRow())
                                .isPopulateFromFile()) {
                                getEditInstanceButton().setEnabled(true);
                            } else {
                                getEditInstanceButton().setEnabled(false);
                            }
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }

            });
        }
        return this.resourcePropertiesTable;
    }


    public class SelectionListener implements ListSelectionListener {
        ResourcePropertyTable table;


        SelectionListener(ResourcePropertyTable table) {
            this.table = table;
        }


        public void valueChanged(ListSelectionEvent e) {
            try {
                if (this.table.getSelectedRow() >= 0) {
                    if (this.table.getRowData(this.table.getSelectedRow()).isPopulateFromFile()) {
                        getEditInstanceButton().setEnabled(true);
                    } else {
                        getEditInstanceButton().setEnabled(false);
                    }
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonsPanel() {
        if (this.buttonsPanel == null) {
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridx = 2;
            gridBagConstraints11.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints11.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints11.gridy = 0;
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints8.gridx = 1;
            gridBagConstraints8.gridy = 0;
            gridBagConstraints8.insets = new Insets(2, 2, 2, 2);
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            this.buttonsPanel = new JPanel();
            this.buttonsPanel.setLayout(new GridBagLayout());
            this.buttonsPanel.add(getAddButton(), gridBagConstraints);
            this.buttonsPanel.add(getRemoveButton(), gridBagConstraints8);
            if (this.isMainMetadataPanel) {
                this.buttonsPanel.add(getEditInstanceButton(), gridBagConstraints11);
            }
        }
        return this.buttonsPanel;
    }


    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddButton() {
        if (this.addResourcePropertyButton == null) {
            this.addResourcePropertyButton = new JButton();
            this.addResourcePropertyButton.setToolTipText("add new operation");
            this.addResourcePropertyButton.setText("Add");
            this.addResourcePropertyButton.setIcon(IntroduceLookAndFeel.getAddIcon());
            this.addResourcePropertyButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    addResourceProperty();
                }
            });
        }
        return this.addResourcePropertyButton;
    }


    private void addResourceProperty() {
        if (getNamespacesJTree().getCurrentNode() instanceof SchemaElementTypeTreeNode) {
            NamespaceType nt = ((NamespaceType) ((NamespaceTypeTreeNode) getNamespacesJTree().getCurrentNode()
                .getParent()).getUserObject());
            SchemaElementType st = ((SchemaElementType) ((SchemaElementTypeTreeNode) getNamespacesJTree()
                .getCurrentNode()).getUserObject());
            ResourcePropertyType metadata = new ResourcePropertyType();
            metadata.setQName(new QName(nt.getNamespace(), st.getType()));
            metadata.setPopulateFromFile(false);
            metadata.setRegister(false);
            
            //look to make sure it is not already in there
            if (info.getService().getResourcePropertiesList() != null && info.getService().getResourcePropertiesList().getResourceProperty() != null) {
                for (int i = 0; i < info.getService().getResourcePropertiesList().getResourceProperty().length; i++) {
                    ResourcePropertyType rp = info.getService().getResourcePropertiesList().getResourceProperty(i);
                    if (rp.getQName().equals(metadata.getQName())) {
                        return;
                    }
                }
            }

            getResourcePropertiesTable().addRow(metadata);

            // add new metadata
            CommonTools.addResourcePropety(info.getService(), metadata);

            // set the file name for the resource property instance......
            int i = 0;
            for (i = 0; i < info.getService().getResourcePropertiesList().getResourceProperty().length; i++) {
                if (metadata.equals(info.getService().getResourcePropertiesList().getResourceProperty(i))) {
                    break;
                }
            }
            metadata.setFileLocation(this.info.getService().getName() + "_"
                + CommonTools.getResourcePropertyVariableName(this.info.getService().getResourcePropertiesList(), i) + ".xml");
        }
    }


    /**
     * This method initializes jButton2
     * 
     * @return javax.swing.JButton
     */
    private JButton getRemoveButton() {
        if (this.removeResourcePropertyButton == null) {
            this.removeResourcePropertyButton = new JButton();
            this.removeResourcePropertyButton.setToolTipText("remove selected operation");
            this.removeResourcePropertyButton.setText("Remove");
            this.removeResourcePropertyButton.setIcon(IntroduceLookAndFeel.getSubtractIcon());
            this.removeResourcePropertyButton.addActionListener(new java.awt.event.ActionListener() {
                // remove from table
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    ResourcePropertyType resource = null;
                    try {
                        resource = getResourcePropertiesTable().getRowData(
                            getResourcePropertiesTable().getSelectedRow());
                    } catch (Exception e1) {
                        ErrorDialog.showError("Please select a metdata type to remove.");
                        return;
                    }
                    try {
                        getResourcePropertiesTable().removeSelectedRow();
                    } catch (Exception e1) {
                        ErrorDialog.showError("Please select a metdata type to remove.");
                        return;
                    }
                    
                    // remove from resource properties list
                    CommonTools.removeResourceProperty(info.getService(), resource.getQName());
                }
            });
        }
        return this.removeResourcePropertyButton;
    }


    /**
     * This method initializes jSplitPane
     * 
     * @return javax.swing.JSplitPane
     */
    private JSplitPane getMainSplitPane() {
        if (this.mainSplitPane == null) {
            this.mainSplitPane = new JSplitPane();
            this.mainSplitPane.setOneTouchExpandable(true);
            this.mainSplitPane.setLeftComponent(getNamespacesScrollPane());
            this.mainSplitPane.setRightComponent(getResourcePropertiesPanel());
        }
        return this.mainSplitPane;
    }


    /**
     * This method initializes editInstanceButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getEditInstanceButton() {
        if (this.editInstanceButton == null) {
            this.editInstanceButton = new JButton();
            this.editInstanceButton.setText("View/Edit Instance");
            this.editInstanceButton.setIcon(IntroduceLookAndFeel.getModifyResourcePropertyIcon());
            this.editInstanceButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    if (getResourcePropertiesTable().getSelectedRow() >= 0) {
                        try {
                            ResourcePropertyType type = getResourcePropertiesTable().getRowData(
                                getResourcePropertiesTable().getSelectedRow());
                            viewEditResourceProperty(type, info);
                            
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                }

            });

        }
        return this.editInstanceButton;
    }
    
    
    public static void viewEditResourceProperty(ResourcePropertyType type, SpecificServiceInformation info) throws Exception {
    	File schemaDir = new File(info.getBaseDirectory() + File.separator + "schema" + File.separator + info.getService().getName());
    	File etcDir = new File(info.getBaseDirectory() + File.separator + "etc");
    	if (type.isPopulateFromFile()) {
            String rpData = null;
            File resourcePropertyFile = null;

            if (type.getFileLocation() != null) {
                resourcePropertyFile = new File(etcDir
                    .getAbsolutePath()
                    + File.separator + type.getFileLocation());
            }
            if (resourcePropertyFile != null && resourcePropertyFile.exists()) {
                // file has already been created
                logger.debug("Loading resource properties file : " + resourcePropertyFile);
                rpData = Utils.fileToStringBuffer(new File(resourcePropertyFile.getAbsolutePath())).toString();
            } else {
                // file has not been created yet, we will
                // create it, set the path to file, and then
                // call the editor
                logger.debug("Creating a new resource properties file");
                boolean created = resourcePropertyFile.createNewFile();
                if (!created) {
                    throw new Exception("Could not create file"
                        + resourcePropertyFile.getAbsolutePath());
                }
                rpData = Utils.fileToStringBuffer(new File(resourcePropertyFile.getAbsolutePath())).toString();
            }

            QName qname = type.getQName();
            NamespaceType nsType = CommonTools.getNamespaceType(info.getNamespaces(), qname.getNamespaceURI());

            ResourcePropertyEditorExtensionDescriptionType mde = ExtensionTools
                .getResourcePropertyEditorExtensionDescriptor(qname);
            ResourcePropertyEditorPanel mdec = null;

            if (mde != null) {
                mdec = ExtensionTools.getMetadataEditorComponent(mde.getName(), type, rpData, new File(
                   schemaDir.getAbsolutePath() + File.separator
                        + nsType.getLocation()), schemaDir);
            } else {
                // use the default editor....
                mdec = new XMLEditorViewer(type,rpData, new File(
                    schemaDir.getAbsolutePath() + File.separator
                        + nsType.getLocation()), schemaDir);
            }
            ResourcePropertyEditorDialog diag = new ResourcePropertyEditorDialog(mdec,
                resourcePropertyFile);
            GridApplication.getContext().showDialog(diag);
        }

    }
}
