package gov.nih.nci.cagrid.data.style.sdkstyle.wizard;

import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.common.ModelInformationUtil;
import gov.nih.nci.cagrid.data.extension.ModelClass;
import gov.nih.nci.cagrid.data.extension.ModelPackage;
import gov.nih.nci.cagrid.data.ui.SchemaResolutionDialog;
import gov.nih.nci.cagrid.data.ui.wizard.CacoreWizardUtils;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.SchemaElementType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

import org.cagrid.grape.utils.CompositeErrorDialog;

/** 
 *  PackageSchemasTable
 *  Table for showing cadsr packages and schema types mapped to them
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Sep 26, 2006 
 * @version $Id: PackageSchemasTable.java,v 1.8 2009-01-30 17:32:54 dervin Exp $ 
 */
public class PackageSchemasTable extends JTable {

    /**
     * Status messages for the package namespace resolution
     */
    public static final String STATUS_SCHEMA_FOUND = "Found";
    public static final String STATUS_MAPPING_ERROR = "Mapping Error";
    public static final String STATUS_GME_DOMAIN_NOT_FOUND = "No Domain";
    public static final String STATUS_GME_NAMESPACE_NOT_FOUND = "No Namespace";
    public static final String STATUS_NEVER_TRIED = "Unknown";
    
    private ModelInformationUtil modelInfoUtil = null;
    private Map wizardProperties = null;

    public PackageSchemasTable(ModelInformationUtil modelInfoUtil, Map wizardProperties) {
        setModel(new PackageSchemasTableModel());
        setDefaultRenderer(Object.class, new PackageSchemasTableRenderer());
        setDefaultEditor(Object.class, new PackageSchemasTableEditor());
        this.modelInfoUtil = modelInfoUtil;
        this.wizardProperties = wizardProperties;
    }


    public boolean isPackageInTable(ModelPackage pack) {
        String packageName = pack.getPackageName();
        String namespace = getMappedNamespace(packageName);
        for (int i = 0; i < getRowCount(); i++) {
            if (packageName.equals(getValueAt(i, 0)) 
                && namespace.equals(getValueAt(i, 1))) {
                return true;
            }
        }
        return false;
    }


    public void addNewCadsrPackage(ServiceInformation serviceInfo, ModelPackage pack) {
        String packageName = pack.getPackageName();
        String namespace = getMappedNamespace(packageName);
        Vector<Object> row = new Vector<Object>(4);
        row.add(packageName);
        row.add(namespace);
        row.add(STATUS_NEVER_TRIED);
        row.add(getResolveButton(serviceInfo, pack));

        ((DefaultTableModel) getModel()).addRow(row);
    }


    public void removeCadsrPackage(String packName) {
        for (int i = 0; i < getRowCount(); i++) {
            if (getValueAt(i, 0).equals(packName)) {
                ((DefaultTableModel) getModel()).removeRow(i);
                break;
            }
        }
    }
    
    
    private String getMappedNamespace(String packageName) {
        String namespace = null;
        NamespaceType nsType = modelInfoUtil.getMappedNamespace(packageName);
        if (nsType != null) {
            namespace = nsType.getNamespace();
        }
        return namespace;
    }


    /**
     * Creates a new JButton to handle schema resolution
     * @param pack
     * @return
     * 		A JButton to resolve schemas
     */
    private JButton getResolveButton(
        final ServiceInformation serviceInfo, final ModelPackage pack) {
        JButton resolveButton = new JButton("Resolve");
        resolveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // find the table row for this button / package
                int row = 0;
                while (getValueAt(row, 3) != e.getSource()) {
                    row++;
                } 

                // figure out what the current status is
                String status = (String) getValueAt(row, 2);
                if (status.equals(STATUS_SCHEMA_FOUND)) {
                    String[] message = {
                        "This package already has a schema associated with it.",
                        "Replace the schema with a different one?"
                    };
                    int choice = JOptionPane.showConfirmDialog(
                        (JButton) e.getSource(), message, "Replace?", JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.YES_OPTION) {
                        // remove associated schema and namespace types
                        removeAssociatedSchema(serviceInfo, pack);
                    } else {
                        return;
                    }
                }
                resolveSchema(serviceInfo, pack, row);
            }
        });
        return resolveButton;
    }


    private void resolveSchema(ServiceInformation info, ModelPackage pack, int dataRow) {
        // resolve the schemas manually
        NamespaceType[] resolved = SchemaResolutionDialog.resolveSchemas(info);
        if (resolved != null) {
            if (resolved.length != 0 && packageResolvedByNamespace(pack, resolved[0])) {
                // don't generate stubs, use the ones from the SDK
                resolved[0].setGenerateStubs(Boolean.FALSE);
                // add all the resolved namespace types to the service
                for (int i = 0; i < resolved.length; i++) {
                    // see if we should actually add the package
                    if (CommonTools.getNamespaceType(
                        info.getNamespaces(), resolved[0].getNamespace()) == null) {
                        CommonTools.addNamespace(info.getServiceDescriptor(), resolved[i]);
                    }
                } 
                try {
                    // set the mapped namespace for the package
                    modelInfoUtil.setMappedNamespace(pack.getPackageName(), resolved[0].getNamespace());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    CompositeErrorDialog.showErrorDialog(
                        "Error setting namespace for package " + pack.getPackageName(), ex.getMessage(), ex);
                }
                // determine the serializer and deserialzier to use for the beans
                String serializerClass = null;
                if (wizardProperties.containsKey(SchemaTypesPanel.TYPE_SERIALIZER_CLASS_PROPERTY)) {
                    serializerClass = (String) wizardProperties.get(SchemaTypesPanel.TYPE_SERIALIZER_CLASS_PROPERTY);
                } else {
                    serializerClass = DataServiceConstants.SDK_SERIALIZER;
                }
                String deserializerClass = null;
                if (wizardProperties.containsKey(SchemaTypesPanel.TYPE_DESERIALIZER_CLASS_PROPERTY)) {
                    deserializerClass = (String) wizardProperties.get(SchemaTypesPanel.TYPE_DESERIALIZER_CLASS_PROPERTY);
                } else {
                    deserializerClass = DataServiceConstants.SDK_DESERIALIZER;
                }
                // set the serializers / deserializers for the FIRST namespace type's schema elements
                SchemaElementType[] types = resolved[0].getSchemaElement();
                try {
                    if (types != null) {
                        for (SchemaElementType element : types) {
                            modelInfoUtil.setMappedElementName(pack.getPackageName(), element.getType(), element.getType());
                            element.setSerializer(serializerClass);
                            element.setDeserializer(deserializerClass);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    CompositeErrorDialog.showErrorDialog(
                        "Error mapping elements to classes in package " + pack.getPackageName(), ex.getMessage(), ex);
                }
                // set the resolution status on the table
                setValueAt(resolved[0].getNamespace(), dataRow, 1);
                setValueAt(STATUS_SCHEMA_FOUND, dataRow, 2);
            } else {
                setValueAt(STATUS_MAPPING_ERROR, dataRow, 2);
            }
        } else {
            CompositeErrorDialog.showErrorDialog("Error retrieving schemas!");
        }
    }
    
    
    private boolean packageResolvedByNamespace(ModelPackage pkg, NamespaceType namespace) {
        Set<String> classNames = new HashSet<String>();
        for (ModelClass clazz : pkg.getModelClass()) {
            classNames.add(clazz.getShortClassName());
        }
        Set<String> elementNames = new HashSet<String>();
        for (SchemaElementType element : namespace.getSchemaElement()) {
            elementNames.add(element.getType());
        }
        if (elementNames.containsAll(classNames)) {
            return true;
        }
        
        // sort out the resolution errors
        Set<String> nonResolvedClasses = new HashSet<String>();
        nonResolvedClasses.addAll(classNames);
        nonResolvedClasses.removeAll(elementNames);
        
        // display the errors
        new PackageSchemaMappingErrorDialog(nonResolvedClasses);
        
        // return error condition
        return false;
    }


    private void removeAssociatedSchema(ServiceInformation info, ModelPackage pack) {
        // get the schema directory for the service
        String serviceName = info.getIntroduceServiceProperties().getProperty(IntroduceConstants.INTRODUCE_SKELETON_SERVICE_NAME);
        File schemaDir = new File(CacoreWizardUtils.getServiceBaseDir(info), "schema" + File.separator + serviceName);
        // get the namespace type from the service information
        NamespaceType mappedNamespace = modelInfoUtil.getMappedNamespace(pack.getPackageName());
        if (mappedNamespace != null) {
            File schemaFile = new File(schemaDir, mappedNamespace.getLocation());
            schemaFile.delete();
            CommonTools.removeNamespace(info.getServiceDescriptor(), mappedNamespace.getNamespace());
        }
    }


    private static class PackageSchemasTableModel extends DefaultTableModel {

        public PackageSchemasTableModel() {
            addColumn("Package Name");
            addColumn("Namespace");
            addColumn("Status");
            addColumn("Manual Resolution");
        }


        public boolean isCellEditable(int row, int column) {
            return column == 3;
        }
    }


    private static class PackageSchemasTableRenderer extends DefaultTableCellRenderer {

        public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof Component) {
                return (Component) value;
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }


    private static class PackageSchemasTableEditor extends AbstractCellEditor implements TableCellEditor {

        private Object editorValue = null;

        public PackageSchemasTableEditor() {
            editorValue = null;
        }


        public Object getCellEditorValue() {
            return editorValue;
        }


        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            editorValue = value;
            return (Component) value;
        }
    }
}
