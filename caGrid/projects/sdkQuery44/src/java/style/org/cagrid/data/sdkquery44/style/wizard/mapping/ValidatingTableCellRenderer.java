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

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.jgoodies.validation.view.ValidationComponentUtils;

public abstract class ValidatingTableCellRenderer extends DefaultTableCellRenderer {

    private Color defaultBackground = null;
    
    public ValidatingTableCellRenderer() {
        super();
        defaultBackground = getBackground();
    }

    
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
        // return swing components as they are
        if (value instanceof Component) {
            return (Component) value;
        }
        
        // restore the default background
        setBackground(defaultBackground);

        // let the concrete implementation validate the cell
        validateCell(table, value, isSelected, hasFocus, row, column);
        
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
    
    
    protected abstract void validateCell(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column);
    
    
    protected void setErrorBackground() {
        setBackground(ValidationComponentUtils.getErrorBackground());
    }
    
    
    protected void setWarningBackground() {
        setBackground(ValidationComponentUtils.getWarningBackground());
    }
}
