package org.cagrid.data.sdkquery41.style.wizard.mapping;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

public class ComponentTableCellEditor extends AbstractCellEditor implements TableCellEditor {

    private Object editorValue = null;

    public ComponentTableCellEditor() {
        editorValue = null;
    }


    public Object getCellEditorValue() {
        return editorValue;
    }


    public Component getTableCellEditorComponent(JTable table, Object value, 
        boolean isSelected, int row, int column) {
        editorValue = value;
        return (Component) value;
    }
}
