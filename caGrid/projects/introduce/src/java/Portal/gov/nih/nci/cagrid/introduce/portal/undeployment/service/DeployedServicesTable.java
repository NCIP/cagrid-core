package gov.nih.nci.cagrid.introduce.portal.undeployment.service;

import gov.nih.nci.cagrid.common.portal.PortalBaseTable;
import gov.nih.nci.cagrid.introduce.servicetasks.beans.deployment.Deployment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;


/**
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 */
public class DeployedServicesTable extends PortalBaseTable {

    public static String NAME = "Service Name";
    public static String DATE = "Date Deployed";

    public static String DATA1 = "DATA1";


    public DeployedServicesTable() {
        super(createTableModel());
        setTableHeader(null);

        initialize();
    }


    public boolean isCellEditable(int row, int column) {
        return false;
    }


    public void addRow(final Deployment service) {
        final Vector v = new Vector();
        v.add(service.getDeploymentPrefix() + "/" + service.getServiceName());
        if (service.getDeployDateTime() != null) {
            Date date;
            if (service.getDeployDateTime().equals("0")) {
                date = new Date();
            } else {
                date = new Date(Long.parseLong(service.getDeployDateTime()));
            }
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            v.add(formatter.format(date));
        } else {
            v.add("");
        }
        v.add(service);
        v.add(v);

        ((DefaultTableModel) this.getModel()).addRow(v);
    }


    public Deployment getSelectedRowData() throws Exception {
        return getRowData(getSelectedRow());
    }


    public Deployment getRowData(int row) throws Exception {
        Deployment type = ((Deployment) getValueAt(row, 2));
        return type;
    }


    public void removeSelectedRow() throws Exception {
        int row = getSelectedRow();
        if ((row < 0) || (row >= getRowCount())) {
            throw new Exception("invalid row");
        }
        int oldSelectedRow = getSelectedRow();
        ((DefaultTableModel) getModel()).removeRow(oldSelectedRow);
        if (oldSelectedRow == 0) {
            oldSelectedRow++;
        }
        if (getRowCount() > 0) {
            setRowSelectionInterval(oldSelectedRow - 1, oldSelectedRow - 1);
        }
    }


    public void removeAllRows() throws Exception {
        for (int i = this.getRowCount() - 1; i >= 0; i--) {
            ((DefaultTableModel) this.getModel()).removeRow(i);
        }
    }


    private void initialize() {
        this.setColumnSelectionAllowed(false);
        this.setRowSelectionAllowed(true);
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.getColumn(DATA1).setMaxWidth(0);
        this.getColumn(DATA1).setMinWidth(0);
        this.getColumn(DATA1).setPreferredWidth(0);

    }


    public static DefaultTableModel createTableModel() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn(NAME);
        model.addColumn(DATE);
        model.addColumn(DATA1);

        return model;
    }


    public void singleClick() throws Exception {
        // TODO Auto-generated method stub

    }


    public void doubleClick() throws Exception {
        // TODO Auto-generated method stub

    }
}