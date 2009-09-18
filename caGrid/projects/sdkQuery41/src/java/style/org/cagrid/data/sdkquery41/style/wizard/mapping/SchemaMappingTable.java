package org.cagrid.data.sdkquery41.style.wizard.mapping;

import gov.nih.nci.cagrid.data.common.ModelInformationUtil;
import gov.nih.nci.cagrid.data.extension.ModelClass;
import gov.nih.nci.cagrid.data.extension.ModelInformation;
import gov.nih.nci.cagrid.data.extension.ModelPackage;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.SchemaElementType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import org.cagrid.data.sdkquery41.style.wizard.SchemaMappingValidityListener;
import org.cagrid.data.sdkquery41.style.wizard.config.SchemaMappingConfigStep;

/**
 * Table which shows the mapping between domain model packages
 * and XML schemas, presenting the user with a means to see
 * what's been mapped, and a way to edit the mappings
 * 
 * @author David
 */
public class SchemaMappingTable extends JTable {
    
    private SchemaMappingTableModel tableModel = null;
    
    private ServiceInformation serviceInfo = null;
    private SchemaMappingConfigStep configuration = null;
    private SchemaMappingValidityListener validityListener = null;
    
    private ModelInformationUtil modelInfoUtil = null;

    public SchemaMappingTable(ServiceInformation serviceInfo, 
        SchemaMappingConfigStep configuration, SchemaMappingValidityListener validityListener) {
        super();
        this.serviceInfo = serviceInfo;
        this.configuration = configuration;
        this.validityListener = validityListener;
        this.modelInfoUtil = new ModelInformationUtil(serviceInfo.getServiceDescriptor());
        tableModel = new SchemaMappingTableModel();
        setModel(tableModel);
        setDefaultRenderer(Object.class, new ValidatingTableCellRenderer() {
            protected void validateCell(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
                // validate status of the 2nd cell
                if (column == 1) {
                    if (value.equals(PackageMappingStatus.MISSING_ELEMENTS) || 
                        value.equals(PackageMappingStatus.NO_SCHEMA)) {
                        setErrorBackground();
                    }
                }
            }
        });
        tableModel.addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                SchemaMappingTable.this.validityListener
                    .updateSchemaMappingValidity(!hasErrors());
            }
        });
        setDefaultEditor(Object.class, new ComponentTableCellEditor());
    }
    
    
    public boolean hasErrors() {
        boolean errors = false;
        for (int i = 0; i < tableModel.getRowCount() && !errors; i++) {
            Object status = tableModel.getValueAt(i, 1);
            if (!PackageMappingStatus.OK.equals(status)) {
                errors = true;
            }
        }
        return errors;
    }
    
    
    public void reloadCadsrInformation() throws Exception {
        // empty the table
        while (tableModel.getRowCount() != 0) {
            tableModel.removeRow(0);
        }
        
        ModelInformation modelInformation = configuration.getCurrentModelInformation();
        if (modelInformation.getModelPackage() != null) {
            // sort packages by name
            ModelPackage[] packages = 
                new ModelPackage[modelInformation.getModelPackage().length];
            for (int i = 0; i < modelInformation.getModelPackage().length; i++) {
                packages[i] = modelInformation.getModelPackage(i);
            }
            Arrays.sort(packages, new Comparator<ModelPackage>() {
                public int compare(ModelPackage p1, ModelPackage p2) {
                    return p1.getPackageName().compareTo(p2.getPackageName());
                }
            });
            
            // create rows for the packages
            for (ModelPackage pack : packages) {
                Vector<Object> row = new Vector<Object>();
                row.add(pack.getPackageName());
                PackageMappingStatus status = determineMappingStatus(pack);
                row.add(status);
                SchemaResolutionButton resolutionButton = 
                    new SchemaResolutionButton(serviceInfo, pack, configuration, this);
                row.add(resolutionButton);
                tableModel.addRow(row);
            }
        } else {
            System.out.println("Packages in caDSR information are null!!!!!!!");
        }
    }
    
    
    private PackageMappingStatus determineMappingStatus(ModelPackage pack) {
        // see if there's even a schema associated witht the package
        NamespaceType mappedNamespace = modelInfoUtil.getMappedNamespace(pack.getPackageName());
        if (mappedNamespace == null) {
            return PackageMappingStatus.NO_SCHEMA;
        }
        
        // see if the schema associated with this package exists in the service description
        NamespaceType[] serviceNamespaces = serviceInfo.getServiceDescriptor().getNamespaces().getNamespace();
        boolean exists = false;
        for (NamespaceType nsType : serviceNamespaces) {
            if (nsType.getNamespace().equals(mappedNamespace.getNamespace())) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            return PackageMappingStatus.SCHEMA_NOT_FOUND;
        }
        
        // verify each class has a mapped element which exists in the schema
        for (ModelClass clazz : pack.getModelClass()) {
            SchemaElementType mappedElement = modelInfoUtil.getMappedElement(
                pack.getPackageName(), clazz.getShortClassName());
            if (mappedElement == null) {
                return PackageMappingStatus.MISSING_ELEMENTS;
            }
        }
        
        return PackageMappingStatus.OK;
    }
    
    
    public static enum PackageMappingStatus {
        OK, NO_SCHEMA, SCHEMA_NOT_FOUND, MISSING_ELEMENTS;
        
        public String toString() {
            String value = null;
            switch (this) {
                case OK:
                    value = "OK";
                    break;
                case NO_SCHEMA:
                    value = "No Schema Assigned";
                    break;
                case SCHEMA_NOT_FOUND:
                    value = "Schema not found";
                    break;
                case MISSING_ELEMENTS:
                    value = "Missing Elements";
                    break;
                default:
                    throw new IllegalArgumentException("Unknown value " + this.name());
            }
            return value;
        }
    }
    
    
    private static class SchemaMappingTableModel extends DefaultTableModel {

        public SchemaMappingTableModel() {
            addColumn("Package Name");
            addColumn("Status");
            addColumn("Manual Resolution");
        }


        public boolean isCellEditable(int row, int column) {
            return column == 2;
        }
    }
}
