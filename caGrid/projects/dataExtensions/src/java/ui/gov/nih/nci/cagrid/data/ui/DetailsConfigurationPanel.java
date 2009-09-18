package gov.nih.nci.cagrid.data.ui;

import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.common.ExtensionDataManager;
import gov.nih.nci.cagrid.data.common.ModelInformationUtil;
import gov.nih.nci.cagrid.data.ui.table.ClassChangeEvent;
import gov.nih.nci.cagrid.data.ui.table.ClassElementSerializationTable;
import gov.nih.nci.cagrid.data.ui.table.ClassInformatonChangeListener;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.SchemaElementType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.cagrid.grape.utils.CompositeErrorDialog;

/** 
 *  DetailsConfigurationPanel
 *  Panel for managing the fine (and largely optional) details of data service configuration
 * 
 * @author David Ervin
 * 
 * @created Jun 27, 2007 11:05:29 AM
 * @version $Id: DetailsConfigurationPanel.java,v 1.4 2009-01-13 15:55:19 dervin Exp $ 
 */
public class DetailsConfigurationPanel extends DataServiceModificationSubPanel {
    
    private JScrollPane classConfigScrollPane = null;
    private ClassElementSerializationTable classConfigTable = null;
    private JPanel validationCheckPanel = null;
    private JCheckBox cqlSyntaxValidationCheckBox = null;
    private JCheckBox domainModelValidationCheckBox = null;
    
    private ModelInformationUtil modelInfoUtil = null;
    
    
    public DetailsConfigurationPanel(ServiceInformation serviceInfo, ExtensionDataManager dataManager) {
        super(serviceInfo, dataManager);
        modelInfoUtil = new ModelInformationUtil(serviceInfo.getServiceDescriptor());
        initialize();
    }
    
    
    private void initialize() {
        GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
        gridBagConstraints18.gridx = 0;
        gridBagConstraints18.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints18.gridy = 1;
        GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
        gridBagConstraints17.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints17.gridy = 0;
        gridBagConstraints17.weightx = 1.0;
        gridBagConstraints17.weighty = 1.0D;
        gridBagConstraints17.gridx = 0;
        setLayout(new GridBagLayout());
        add(getClassConfigScrollPane(), gridBagConstraints17);
        add(getValidationCheckPanel(), gridBagConstraints18);
    }
    

    public void updateDisplayedConfiguration() throws Exception {

    }

    
    /**
     * This method initializes jScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getClassConfigScrollPane() {
        if (classConfigScrollPane == null) {
            classConfigScrollPane = new JScrollPane();
            classConfigScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
                "Exposed Class Configuration", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                null, PortalLookAndFeel.getPanelLabelColor()));
            classConfigScrollPane.setViewportView(getClassConfigTable());
            classConfigScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        }
        return classConfigScrollPane;
    }
    
    
    public ClassElementSerializationTable getClassConfigTable() {
        if (classConfigTable == null) {
            classConfigTable = new ClassElementSerializationTable(getExtensionDataManager(), modelInfoUtil);
            classConfigTable.addClassInformatonChangeListener(new ClassInformatonChangeListener() {
                public void elementNameChanged(ClassChangeEvent e) {
                    // save the mapping info
                    try {
                        modelInfoUtil.setMappedNamespace(
                            e.getPackageName(), e.getNamespace());
                        modelInfoUtil.setMappedElementName(
                            e.getPackageName(), e.getClassName(), e.getElementName());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        CompositeErrorDialog.showErrorDialog("Error storing class mapping", ex);
                    }
                }


                public void serializationChanged(ClassChangeEvent e) {
                    // get the namespace type for the class
                    NamespaceType nsType = CommonTools.getNamespaceType(
                        getServiceInfo().getNamespaces(), e.getNamespace());
                    // find the schema element type
                    SchemaElementType schemaType = NamespaceUtils.getElementByName(
                        nsType, e.getElementName());
                    // user may have selected an element type name which is not
                    // in the namespace type.
                    // TODO: what do I do in that case? maybe prevent that in
                    // handling element name changed
                    if (schemaType != null) {
                        schemaType.setSerializer(e.getSerializer());
                        schemaType.setDeserializer(e.getDeserializer());
                    }
                }


                public void targetabilityChanged(ClassChangeEvent e) {
                    try {
                        getExtensionDataManager().setClassTargetableInModel(
                            e.getPackageName(), e.getClassName(), e.isTargetable());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        CompositeErrorDialog.showErrorDialog("Error storing change to targetability", ex);
                    }
                }
            });
        }
        return classConfigTable;
    }
    
    
    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getValidationCheckPanel() {
        if (validationCheckPanel == null) {
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 1;
            gridBagConstraints1.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints1.gridy = 0;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints.gridy = 0;
            validationCheckPanel = new JPanel();
            validationCheckPanel.setLayout(new GridBagLayout());
            validationCheckPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                null, "Query Validation", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                null, PortalLookAndFeel.getPanelLabelColor()));
            validationCheckPanel.add(getCqlSyntaxValidationCheckBox(), gridBagConstraints);
            validationCheckPanel.add(getDomainModelValidationCheckBox(), gridBagConstraints1);
        }
        return validationCheckPanel;
    }
    
    
    private JCheckBox getCqlSyntaxValidationCheckBox() {
        if (cqlSyntaxValidationCheckBox == null) {
            cqlSyntaxValidationCheckBox = new JCheckBox();
            cqlSyntaxValidationCheckBox.setText("Validate CQL Syntax");
            cqlSyntaxValidationCheckBox.setToolTipText("Causes the Data Service to "
                + "validate all CQL queries for syntactic correctness");
            cqlSyntaxValidationCheckBox.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    CommonTools.setServiceProperty(getServiceInfo().getServiceDescriptor(),
                        DataServiceConstants.VALIDATE_CQL_FLAG, String.valueOf(
                            getCqlSyntaxValidationCheckBox().isSelected()), false);
                }
            });
            // set the check box selection
            if (CommonTools.servicePropertyExists(getServiceInfo().getServiceDescriptor(),
                DataServiceConstants.VALIDATE_CQL_FLAG)) {
                try {
                    cqlSyntaxValidationCheckBox.setSelected(Boolean.parseBoolean(
                        CommonTools.getServicePropertyValue(
                            getServiceInfo().getServiceDescriptor(), 
                            DataServiceConstants.VALIDATE_CQL_FLAG)));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    CompositeErrorDialog.showErrorDialog("Error getting service property value for "
                        + DataServiceConstants.VALIDATE_CQL_FLAG, ex);
                }
            }
        }
        return cqlSyntaxValidationCheckBox;
    }


    /**
     * This method initializes jCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getDomainModelValidationCheckBox() {
        if (domainModelValidationCheckBox == null) {
            domainModelValidationCheckBox = new JCheckBox();
            domainModelValidationCheckBox.setText("Validate Domain Model");
            domainModelValidationCheckBox.setToolTipText("Causes the data service to ensure "
                + "all queries remain within the limits of the exposed domain model");
            domainModelValidationCheckBox.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    CommonTools.setServiceProperty(getServiceInfo().getServiceDescriptor(),
                        DataServiceConstants.VALIDATE_DOMAIN_MODEL_FLAG, String
                            .valueOf(getDomainModelValidationCheckBox().isSelected()), false);
                }
            });
            // set the check box selection
            if (CommonTools.servicePropertyExists(getServiceInfo().getServiceDescriptor(),
                DataServiceConstants.VALIDATE_DOMAIN_MODEL_FLAG)) {
                try {
                    domainModelValidationCheckBox.setSelected(Boolean.parseBoolean(
                        CommonTools.getServicePropertyValue(
                            getServiceInfo().getServiceDescriptor(), 
                            DataServiceConstants.VALIDATE_DOMAIN_MODEL_FLAG)));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    CompositeErrorDialog.showErrorDialog("Error getting service property value for "
                        + DataServiceConstants.VALIDATE_DOMAIN_MODEL_FLAG, ex);
                }
            }
        }
        return domainModelValidationCheckBox;
    }

}
