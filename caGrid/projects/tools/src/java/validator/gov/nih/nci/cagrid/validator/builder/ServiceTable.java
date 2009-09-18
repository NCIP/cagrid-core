package gov.nih.nci.cagrid.validator.builder;

import gov.nih.nci.cagrid.tests.core.beans.validation.ServiceDescription;

import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;

/** 
 *  ServiceTable
 *  Table to display / edit services for a deployment validation configuration
 * 
 * @author David Ervin
 * 
 * @created Aug 28, 2007 12:43:19 PM
 * @version $Id: ServiceTable.java,v 1.1 2008-03-25 14:20:30 dervin Exp $ 
 */
public class ServiceTable extends JTable {
    private DefaultTableModel model;

    public ServiceTable() {
        super();
        model = new DefaultTableModel();
        model.addColumn("Service Name");
        model.addColumn("Type");
        model.addColumn("URL");
        setModel(model);
        getColumnModel().getColumn(0).setPreferredWidth(40);
        getColumnModel().getColumn(1).setPreferredWidth(20);
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }
    
    
    public void clearTable() {
        while (getRowCount() != 0) {
            model.removeRow(0);
        }
    }
    
    
    public boolean isCellEditable(int row, int col) {
        return col == 2;
    }
    
    
    public void addService(final ServiceDescription service) {
        Vector row = new Vector(3);
        row.add(service.getServiceName());
        row.add(service.getServiceType().toString());
        row.add(service.getServiceUrl().toString());
        model.addRow(row);
    }
    
    
    public void removeSelectedRows() {
        int[] selected = getSelectedRows();
        for (int i = 0; i < selected.length; i++) {
            int rmRow = selected[i];
            model.removeRow(rmRow);
            for (int j = 0; j < selected.length; j++) {
                selected[j]--;
            }
        }
        
    }
    
    
    public ServiceDescription[] getServiceDescriptions() throws MalformedURIException {
        ServiceDescription[] descriptions = new ServiceDescription[getRowCount()];
        for (int i = 0; i < getRowCount(); i++) {
            String name = getValueAt(i, 0).toString();
            String type = getValueAt(i, 1).toString();
            String url = getValueAt(i, 2).toString();
            
            ServiceDescription desc = new ServiceDescription();
            desc.setServiceName(name);
            desc.setServiceType(type);
            desc.setServiceUrl(new URI(url));
            descriptions[i] = desc;
        }
        return descriptions;
    }
}
