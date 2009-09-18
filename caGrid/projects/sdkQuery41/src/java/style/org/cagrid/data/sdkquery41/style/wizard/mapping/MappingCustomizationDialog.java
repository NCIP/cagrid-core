package org.cagrid.data.sdkquery41.style.wizard.mapping;

import gov.nih.nci.cagrid.data.extension.ModelClass;
import gov.nih.nci.cagrid.data.extension.ModelPackage;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.SchemaElementType;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import org.cagrid.data.sdkquery41.style.wizard.config.SchemaMappingConfigStep;
import org.cagrid.grape.utils.CompositeErrorDialog;

/**
 * Customizes the class to element mapping
 * 
 * @author David
 */
public class MappingCustomizationDialog extends JDialog {
    
    private static final String NO_ELEMENT_SELECTED = "-- Select Element --";

    private NamespaceType nsType = null;
    private ModelPackage modelPackage = null;
    private SchemaMappingConfigStep configuration = null;
    
    private JTable mappingTable = null;
    private JScrollPane mappingScrollPane = null;
    private JPanel mainPanel = null;
    private JPanel infoPanel = null;
    private JLabel packageNameLabel = null;
    private JTextField packageNameTextField = null;
    private JLabel namespaceLabel = null;
    private JTextField namespaceTextField = null;
    private JButton doneButton = null;

    private MappingCustomizationDialog(NamespaceType nsType, 
        ModelPackage modelPackage, SchemaMappingConfigStep configuration) {
        super((JFrame) null, "Element Mapping Customization", true);
        this.nsType = nsType;
        this.modelPackage = modelPackage;
        this.configuration = configuration;
        initialize();
    }
    
    
    public static void customizeElementMapping(NamespaceType nsType, 
        ModelPackage modelPackage, SchemaMappingConfigStep configuration) {
        MappingCustomizationDialog dialog = 
            new MappingCustomizationDialog(nsType, modelPackage, configuration);
        dialog.populatePackageInfo();
        dialog.populateMappingTable();
        dialog.setVisible(true);
    }
    
    
    private void initialize() {
        this.setSize(new Dimension(500, 400));
        this.setContentPane(getMainPanel());
    }
    
    
    private JTable getMappingTable() {
        if (mappingTable == null) {
            DefaultTableModel mappingTableModel = new DefaultTableModel() {
                public boolean isCellEditable(int row, int column) {
                    return column == 1;
                }
            };
            mappingTableModel.addColumn("Class Name");
            mappingTableModel.addColumn("Element Name");
            mappingTable = new JTable(mappingTableModel);
            mappingTable.setDefaultRenderer(Object.class, new ValidatingTableCellRenderer() {
                protected void validateCell(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                    if (column == 1) {
                        setToolTipText("");
                        JComboBox combo = (JComboBox) value;
                        Object selected = combo.getSelectedItem();
                        if (selected == NO_ELEMENT_SELECTED) {
                            // it's an error to map a class
                            setErrorBackground();
                            setToolTipText("An element must be selected");
                        } else {
                            // it's a warning to map multiple classes to the same element
                            SchemaElementType selectedElement = (SchemaElementType) selected;
                            if (getClassesMappedToElement(selectedElement).size() > 1) {
                                setErrorBackground();
                                setToolTipText("Multiple classes are mapped to this element");
                            }
                        }
                    }
                }
            });
            mappingTable.setDefaultEditor(Object.class, new ComponentTableCellEditor());
        }
        return mappingTable;
    }
    
    
    private JScrollPane getMappingScrollPane() {
        if (mappingScrollPane == null) {
            mappingScrollPane = new JScrollPane();
            mappingScrollPane.setViewportView(getMappingTable());
            mappingScrollPane.setBorder(BorderFactory.createTitledBorder(
                null, "Class Mappings", TitledBorder.DEFAULT_JUSTIFICATION, 
                TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
        }
        return mappingScrollPane;
    }


    /**
     * This method initializes mainPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.gridx = 0;
            gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints6.anchor = GridBagConstraints.EAST;
            gridBagConstraints6.gridy = 2;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.fill = GridBagConstraints.BOTH;
            gridBagConstraints5.gridy = 1;
            gridBagConstraints5.weightx = 1.0;
            gridBagConstraints5.weighty = 1.0D;
            gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints5.gridx = 0;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints4.gridy = 0;
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.add(getInfoPanel(), gridBagConstraints4);
            mainPanel.add(getMappingScrollPane(), gridBagConstraints5);
            mainPanel.add(getDoneButton(), gridBagConstraints6);
        }
        return mainPanel;
    }


    /**
     * This method initializes infoPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getInfoPanel() {
        if (infoPanel == null) {
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridy = 1;
            gridBagConstraints3.weightx = 1.0;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.gridx = 1;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.gridy = 0;
            gridBagConstraints2.weightx = 1.0;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.gridx = 1;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.gridy = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.gridy = 0;
            infoPanel = new JPanel();
            infoPanel.setLayout(new GridBagLayout());
            infoPanel.setBorder(BorderFactory.createTitledBorder(
                null, "Package Mapping", TitledBorder.DEFAULT_JUSTIFICATION, 
                TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
            infoPanel.add(getPackageNameLabel(), gridBagConstraints);
            infoPanel.add(getNamespaceLabel(), gridBagConstraints1);
            infoPanel.add(getPackageNameTextField(), gridBagConstraints2);
            infoPanel.add(getNamespaceTextField(), gridBagConstraints3);
        }
        return infoPanel;
    }


    /**
     * This method initializes packageNameLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getPackageNameLabel() {
        if (packageNameLabel == null) {
            packageNameLabel = new JLabel();
            packageNameLabel.setText("Package Name:");
        }
        return packageNameLabel;
    }


    /**
     * This method initializes packageNameTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getPackageNameTextField() {
        if (packageNameTextField == null) {
            packageNameTextField = new JTextField();
            packageNameTextField.setEditable(false);
        }
        return packageNameTextField;
    }


    /**
     * This method initializes namespaceLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getNamespaceLabel() {
        if (namespaceLabel == null) {
            namespaceLabel = new JLabel();
            namespaceLabel.setText("Namespace:");
        }
        return namespaceLabel;
    }


    /**
     * This method initializes namespaceTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getNamespaceTextField() {
        if (namespaceTextField == null) {
            namespaceTextField = new JTextField();
            namespaceTextField.setEditable(false);
        }
        return namespaceTextField;
    }


    /**
     * This method initializes doneButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getDoneButton() {
        if (doneButton == null) {
            doneButton = new JButton();
            doneButton.setText("Done");
            doneButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    // check for errors, show a warning, close anyway
                    Map<SchemaElementType, List<String>> classesMappedToSameElement =
                        getClassesMappedToSameElement();
                    final StringBuffer warning = new StringBuffer();
                    if (classesMappedToSameElement.size() != 0) {
                        warning.append("The following classes are mapped to the same element:");
                        warning.append("\n");
                        for (SchemaElementType element : classesMappedToSameElement.keySet()) {
                            List<String> classes = classesMappedToSameElement.get(element);
                            warning.append("\tElement name: " + element.getType()).append("\n");
                            for (String className : classes) {
                                warning.append("\t\t").append(className).append("\n");
                            }
                        }
                    }
                    final StringBuffer error = new StringBuffer();
                    List<String> nonMapped = getClassesNotMapped();
                    if (nonMapped.size() != 0) {
                        error.append("The following classes are not mapped to any element:");
                        error.append("\n");
                        for (String className : nonMapped) {
                            error.append("\t").append(className).append("\n");
                        }
                    }
                    if (warning.length() != 0) {
                        Runnable warningRunner = new Runnable() {
                            public void run() {
                                CompositeErrorDialog.showErrorDialog(
                                    "Multiple classes are mapped to the same element", warning.toString().split("\n"));
                            }
                        };
                        SwingUtilities.invokeLater(warningRunner);
                    }
                    if (error.length() != 0) {
                        Runnable errorRunner = new Runnable() {
                            public void run() {
                                CompositeErrorDialog.showErrorDialog(
                                    "Classes have not been mapped to any element", error.toString().split("\n"));
                            }
                        };
                        SwingUtilities.invokeLater(errorRunner);
                    }
                    
                    dispose();
                }
            });
        }
        return doneButton;
    }
    
    
    // ---------
    // helpers
    // ---------
    
    
    private Map<SchemaElementType, List<String>> getClassesMappedToSameElement() {
        Map<SchemaElementType, List<String>> mappings = new HashMap<SchemaElementType, List<String>>();
        DefaultTableModel tableModel = (DefaultTableModel) getMappingTable().getModel();
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            String className = (String) tableModel.getValueAt(row, 0);
            JComboBox combo = (JComboBox) tableModel.getValueAt(row, 1);
            Object selection = combo.getSelectedItem();
            if (selection instanceof SchemaElementType) {
                SchemaElementType element = (SchemaElementType) selection;
                List<String> mappedClasses = mappings.get(element);
                if (mappedClasses == null) {
                    mappedClasses = new LinkedList<String>();
                    mappings.put(element, mappedClasses);
                }
                mappedClasses.add(className);
            }
        }
        // post process... it's expected that each element will map to 1 and ONLY 1 class
        Map<SchemaElementType, List<String>> overused = new HashMap<SchemaElementType, List<String>>();
        for (SchemaElementType element : mappings.keySet()) {
            if (mappings.get(element).size() > 1) {
                overused.put(element, mappings.get(element));
            }
        }
        return overused;
    }
    
    
    private List<String> getClassesNotMapped() {
        List<String> unmapped = new LinkedList<String>();
        DefaultTableModel tableModel = (DefaultTableModel) getMappingTable().getModel();
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            String className = (String) tableModel.getValueAt(row, 0);
            JComboBox combo = (JComboBox) tableModel.getValueAt(row, 1);
            Object selection = combo.getSelectedItem();
            if (selection == NO_ELEMENT_SELECTED) {
                unmapped.add(className);
            }
        }
        return unmapped;
    }
    
    
    private void populatePackageInfo() {
        getPackageNameTextField().setText(modelPackage.getPackageName());
        getNamespaceTextField().setText(nsType.getNamespace());
    }
    
    
    private void populateMappingTable() {
        // a single combo box renderer for a little more memory efficiency
        ListCellRenderer comboBoxRenderer = new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(
                JList list, Object value, int index, 
                boolean isSelected, boolean cellHasFocus) {
                // let default impl do its thing
                super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
                // special handling for schema elements
                if (value instanceof SchemaElementType) {
                    SchemaElementType element = (SchemaElementType) value;
                    setText(element.getType());
                }
                return this;
            }
        };
        
        // item listener to set mapping information and 
        // repaint the table on combo box changes
        ItemListener comboListener = new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                int row = getMappingTable().getEditingRow();
                String className = (String) getMappingTable().getValueAt(row, 0);
                JComboBox combo = (JComboBox) getMappingTable().getValueAt(row, 1);
                Object selection = combo.getSelectedItem();
                try {
                    if (selection == NO_ELEMENT_SELECTED) {
                        configuration.unsetClassMapping(
                            modelPackage.getPackageName(), className);
                    } else if (selection instanceof SchemaElementType) {
                        configuration.setClassMapping(
                            modelPackage.getPackageName(), className, (SchemaElementType) selection);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    CompositeErrorDialog.showErrorDialog(
                        "Error setting class to element mapping", ex.getMessage(), ex);
                }
                getMappingTable().repaint();
            }
        };
        
        // sorted element names
        SchemaElementType[] elementTypes = new SchemaElementType[nsType.getSchemaElement().length];
        for (int i = 0; i < nsType.getSchemaElement().length; i++) {
            elementTypes[i] = nsType.getSchemaElement(i);
        }
        Arrays.sort(elementTypes, new Comparator<SchemaElementType>() {
            public int compare(SchemaElementType o1, SchemaElementType o2) {
                return o1.getType().compareTo(o2.getType());
            }
        });
        
        // sort class names
        List<String> classNames = new ArrayList<String>(modelPackage.getModelClass().length);
        for (ModelClass mapping : modelPackage.getModelClass()) {
            classNames.add(mapping.getShortClassName());
        }
        Collections.sort(classNames);
        
        // for each class, create a combo box and table row
        for (String className : classNames) {
            JComboBox combo = createElementSelectionCombo(comboBoxRenderer, comboListener, elementTypes);
            // add the row to the table
            ((DefaultTableModel) getMappingTable().getModel()).addRow(
                new Object[] {className, combo});
        }
    }
    
    
    private JComboBox createElementSelectionCombo(ListCellRenderer renderer, 
        ItemListener listener, SchemaElementType[] elementTypes) {
        JComboBox combo = new JComboBox();
        combo.setRenderer(renderer);
        combo.addItem(NO_ELEMENT_SELECTED);
        for (SchemaElementType element : elementTypes) {
            combo.addItem(element);
        }
        combo.addItemListener(listener);
        return combo;
    }
    
    
    private List<String> getClassesMappedToElement(SchemaElementType mappedElement) {
        List<String> classNames = new LinkedList<String>();
        for (int row = 0; row < getMappingTable().getRowCount(); row++) {
            JComboBox combo = (JComboBox) getMappingTable().getValueAt(row, 1);
            Object selection = combo.getSelectedItem();
            if (selection instanceof SchemaElementType && selection == mappedElement) {
                String name = (String) getMappingTable().getValueAt(row, 0);
                classNames.add(name);
            }
        }
        return classNames;
    }
}
