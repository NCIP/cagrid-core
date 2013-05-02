/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.data.sdkquery44.style.wizard.mapping;

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
