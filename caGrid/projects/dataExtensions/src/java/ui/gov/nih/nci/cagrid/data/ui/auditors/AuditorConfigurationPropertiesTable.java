package gov.nih.nci.cagrid.data.ui.auditors;

import gov.nih.nci.cagrid.data.auditing.AuditorConfigurationConfigurationProperties;
import gov.nih.nci.cagrid.data.auditing.ConfigurationProperty;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

/** 
 *  AuditorConfigurationPropertiesTable
 *  Configures / displays configuration properties for data service auditors
 * 
 * @author David Ervin
 * 
 * @created May 21, 2007 10:58:45 AM
 * @version $Id: AuditorConfigurationPropertiesTable.java,v 1.2 2007-12-18 19:11:40 dervin Exp $ 
 */
public class AuditorConfigurationPropertiesTable extends JTable {

    private DefaultTableModel model;
    
    private List<AuditorPropertyChangeListener> propertyChangeListeners = null;
    
    public AuditorConfigurationPropertiesTable() {
        super();
        setModel(getConfigurationPropertiesModel());
        propertyChangeListeners = new LinkedList<AuditorPropertyChangeListener>();
    }
    
    
    public void addAuditorPropertyListener(AuditorPropertyChangeListener listener) {
        propertyChangeListeners.add(listener);
    }
    
    
    public boolean removeAuditorPropertyChangeListener(AuditorPropertyChangeListener listener) {
        return propertyChangeListeners.remove(listener);
    }
    
    
    public AuditorPropertyChangeListener[] getAuditorPropertyChangeListeners() {
        AuditorPropertyChangeListener[] listeners = 
            new AuditorPropertyChangeListener[propertyChangeListeners.size()];
        propertyChangeListeners.toArray(listeners);
        return listeners;
    }
    
    
    public void setConfigurationProperties(AuditorConfigurationConfigurationProperties props, Properties defaults) {
        while (getRowCount() != 0) {
            getConfigurationPropertiesModel().removeRow(0);
        }
        Enumeration defaultKeys = defaults.keys();
        while (defaultKeys.hasMoreElements()) {
            String key = (String) defaultKeys.nextElement();
            String defaultValue = defaults.getProperty(key);
            String currentValue = defaultValue;
            if (props.getProperty() != null) {
                for (ConfigurationProperty prop : props.getProperty()) {
                    if (prop.getKey().equals(key)) {
                        currentValue = prop.getValue();
                    }
                }
            }
            Vector<String> row = new Vector<String>(3);
            row.add(key);
            row.add(defaultValue);
            row.add(currentValue);
            getConfigurationPropertiesModel().addRow(row);
        }
    }
    
    
    public AuditorConfigurationConfigurationProperties getConfigurationProperties() {
        AuditorConfigurationConfigurationProperties props = 
            new AuditorConfigurationConfigurationProperties();
        ConfigurationProperty[] propArray = new ConfigurationProperty[getRowCount()];
        for (int i = 0; i < getRowCount(); i++) {
            propArray[i] = new ConfigurationProperty(
                getValueAt(i, 0).toString(), getValueAt(i, 2).toString());
        }
        props.setProperty(propArray);
        return props;
    }
    
    
    public void clearTable() {
        while (getRowCount() != 0) {
            getConfigurationPropertiesModel().removeRow(0);
        }
    }
    
    
    private DefaultTableModel getConfigurationPropertiesModel() {
        if (model == null) {
            model = new DefaultTableModel() {
                public boolean isCellEditable(int row, int col) {
                    return col == 2;
                }
            };
            model.addColumn("Key");
            model.addColumn("Default");
            model.addColumn("Value");
            
            model.addTableModelListener(new TableModelListener() {
                public void tableChanged(TableModelEvent e) {
                    if (e.getType() == TableModelEvent.UPDATE) {
                        int row = e.getFirstRow();
                        int col = e.getColumn();
                        if (col == 2) {
                            fireConfigurationPropertyChanged(row);
                        }
                    }
                }
            });
        }
        return model;
    }
    
    
    protected void fireConfigurationPropertyChanged(int row) {
        String key = getValueAt(row, 0).toString();
        String value = getValueAt(row, 2).toString();
        for (AuditorPropertyChangeListener listener : propertyChangeListeners) {
            listener.propertyValueEdited(key, value);
        }
    }
}
