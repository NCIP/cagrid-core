package org.cagrid.gaards.ui.dorian.federation;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.cagrid.gaards.dorian.federation.HostCertificateRecord;
import org.cagrid.gaards.dorian.federation.HostRecord;
import org.cagrid.grape.table.GrapeBaseTable;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: HostCertificatesTable.java,v 1.3 2008-11-20 15:29:42 langella
 *          Exp $
 */
public class HostRecordsTable extends GrapeBaseTable {

    private static final long serialVersionUID = 1L;

    public final static String HOST_RECORD = "record";

    public final static String ID = "Host Identity";

    public final static String HOST = "Hostname";

    public final static String FIRST_NAME = "First Name";

    public final static String LAST_NAME = "Last Name";

    public final static String EMAIL = "Email";

    public final static String OWNER = "Owner";

    private HostCertificateLauncher launcher;


    public HostRecordsTable() {
        super(createTableModel());
        this.launcher = launcher;
        TableColumn c = this.getColumn(HOST_RECORD);
        c.setMaxWidth(0);
        c.setMinWidth(0);
        c.setPreferredWidth(0);
        c.setResizable(false);
        this.clearTable();

    }


    public static DefaultTableModel createTableModel() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn(HOST_RECORD);
        model.addColumn(ID);
        model.addColumn(HOST);
        model.addColumn(FIRST_NAME);
        model.addColumn(LAST_NAME);
        model.addColumn(EMAIL);
        model.addColumn(OWNER);
        return model;

    }


    public void addHost(final HostRecord record) {
        Vector v = new Vector();
        v.add(record);
        v.add(record.getIdentity());
        v.add(record.getHostname());
        v.add(record.getOwnerFirstName());
        v.add(record.getOwnerLastName());
        v.add(record.getOwnerEmail());
        v.add(record.getOwner());
        addRow(v);
    }


    public synchronized HostRecord getSelectedHost() throws Exception {
        int row = getSelectedRow();
        if ((row >= 0) && (row < getRowCount())) {
            return (HostRecord) getValueAt(row, 0);
        } else {
            throw new Exception("Please select a host!!!");
        }
    }


    public synchronized void removeSelectedTrustedIdP() throws Exception {
        int row = getSelectedRow();
        if ((row >= 0) && (row < getRowCount())) {
            removeRow(row);
        } else {
            throw new Exception("Please select a host!!!");
        }
    }


    public void doubleClick() throws Exception {
    }


    public void singleClick() throws Exception {
        // TODO Auto-generated method stub

    }

}