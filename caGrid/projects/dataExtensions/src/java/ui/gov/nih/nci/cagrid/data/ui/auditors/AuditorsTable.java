package gov.nih.nci.cagrid.data.ui.auditors;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.cagrid.grape.table.GrapeTableCellEditor;

/** 
 *  AuditorsTable
 *  Table of auditors used by the data service
 * 
 * @author David Ervin
 * 
 * @created May 21, 2007 10:41:23 AM
 * @version $Id: AuditorsTable.java,v 1.4 2007-12-18 19:11:40 dervin Exp $ 
 */
public class AuditorsTable extends JTable {
    
    private DefaultTableModel model = null;
    private List<AuditorChangeListener> auditorChangeListeners = null;
    private ActionListener configureActionListener = null;
    private ActionListener removeActionListener = null;

    public AuditorsTable() {
        super();
        auditorChangeListeners = new LinkedList<AuditorChangeListener>();
        setModel(getAuditorsTableModel());
        // renderers and editors to handle components on the table
        setDefaultRenderer(Object.class, new AuditorsTableCellRenderer());
        setDefaultEditor(Component.class, new GrapeTableCellEditor() {
            public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected,
                int row, int column) {
                if (!isSelected) {
                    table.getSelectionModel().setSelectionInterval(row, row);
                }
                return super.getTableCellEditorComponent(table, value, isSelected, row, column);
            }
        });
        // setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // set button column widths to the minimum required
        Component configureComponent = getCellRenderer(0, 2).getTableCellRendererComponent(
            this, new JButton("Configure"), false, false, 0, 2);
        Component removeComponent = getCellRenderer(0, 3).getTableCellRendererComponent(
            this, new JButton("Remove"), false, false, 0, 3);
        int configureWidth = (int) configureComponent.getPreferredSize().getWidth() + (2 * getIntercellSpacing().width);
        int removeWidth = (int) removeComponent.getPreferredSize().getWidth() + (2 * getIntercellSpacing().width);
        // set the width on button columns smaller
        TableColumn configureColumn = getColumn("Configure");
        configureColumn.setPreferredWidth(configureWidth);
        configureColumn.setWidth(configureWidth);
        configureColumn.setMaxWidth(configureWidth);
        TableColumn removeColumn = getColumn("Remove");
        removeColumn.setPreferredWidth(removeWidth);
        removeColumn.setWidth(removeWidth);
        removeColumn.setMaxWidth(removeWidth);
    }
    
    
    public void addAuditorChangeListener(AuditorChangeListener listener) {
        auditorChangeListeners.add(listener);
    }
    
    
    public boolean removeAuditorChangeListener(AuditorChangeListener listener) {
        return auditorChangeListeners.remove(listener);
    }
    
    
    public AuditorChangeListener[] getAuditorChangeListeners() {
        AuditorChangeListener[] listeners = new AuditorChangeListener[auditorChangeListeners.size()];
        auditorChangeListeners.toArray(listeners);
        return listeners;
    }
    
    
    public void addAuditor(String className, String instanceName) {
        Vector<Object> row = new Vector<Object>(4);
        row.add(className);
        row.add(instanceName);
        JButton configureButton = new JButton("Configure");
        configureButton.addActionListener(getConfigureActionListener());
        row.add(configureButton);
        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(getRemoveActionListener());
        row.add(removeButton);
        getAuditorsTableModel().addRow(row);
    }
    
    
    public void removeAuditor(String className, String instanceName) {
        for (int i = 0; i < getRowCount(); i++) {
            if (getValueAt(i, 0).equals(className) 
                && getValueAt(i, 1).equals(instanceName)) {
                System.out.println("Removing auditor at row " + i);
                getAuditorsTableModel().removeRow(i);
                return;
            }
        }
    }
    
    
    public boolean isAuditorDisplayed(String className, String instanceName) {
        for (int i = 0; i < getRowCount(); i++) {
            if (getValueAt(i, 0).equals(className) 
                && getValueAt(i, 1).equals(instanceName)) {
                return true;
            }
        }
        return false;
    }
    
    
    public String getSelectedClassName() {
        if (getSelectedRow() == -1) {
            return null;
        }
        return getValueAt(getSelectedRow(), 0).toString();
    }
    
    
    public String getSelectedInstanceName() {
        if (getSelectedRow() == -1) {
            return null;
        }
        return getValueAt(getSelectedRow(), 1).toString();
    }
    
    
    public Class<?> getColumnClass(int col) {
        return col < 2 ? String.class : Component.class;
    }
    
    
    private ActionListener getConfigureActionListener() {
        if (configureActionListener == null) {
            configureActionListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireAuditorConfigureClicked();
                }
            };
        }
        return configureActionListener;
    }
    
    
    private ActionListener getRemoveActionListener() {
        if (removeActionListener == null) {
            removeActionListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireAuditorRemoveClicked();
                }
            };
        }
        return removeActionListener;
    }
    
    
    private DefaultTableModel getAuditorsTableModel() {
        if (model == null) {
            model = new DefaultTableModel() {
                public boolean isCellEditable(int row, int col) {
                    return col >= 2;
                }
            };
            model.addColumn("Auditor Class");
            model.addColumn("Instance Name");
            model.addColumn("Configure");
            model.addColumn("Remove");
        }
        return model;
    }
    
    
    protected void fireAuditorConfigureClicked() {
        String className = getSelectedClassName();
        String instanceName = getSelectedInstanceName();
        for (AuditorChangeListener listener : auditorChangeListeners) {
            listener.auditorConfigureButtonClicked(className, instanceName);
        }
    }
    
    
    protected void fireAuditorRemoveClicked() {
        String className = getSelectedClassName();
        String instanceName = getSelectedInstanceName();
        for (AuditorChangeListener listener : auditorChangeListeners) {
            listener.auditorRemoveButtonClicked(className, instanceName);
        }
    }
    
    
    private static class AuditorsTableCellRenderer extends DefaultTableCellRenderer {
        
        public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof Component) {
                return (Component) value;
            }
            return this;
        }
    }
}
