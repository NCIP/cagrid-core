package org.cagrid.gaards.ui.dorian.federation;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.cagrid.gaards.dorian.federation.GridUserRecord;
import org.cagrid.gaards.ui.dorian.DorianSessionProvider;
import org.cagrid.grape.table.GrapeBaseTable;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: UsersTable.java,v 1.4 2008-11-20 15:29:42 langella Exp $
 */
public class GridUserRecordsTable extends GrapeBaseTable {

    private static final long serialVersionUID = 1L;

    public final static String USER = "user";

    public final static String IDENTITY = "Identity";

    public final static String EMAIL = "Email";

    public final static String FIRST_NAME = "First Name";

    public final static String LAST_NAME = "Last Name";

    DorianSessionProvider session;


    public GridUserRecordsTable() {
        this(null);
    }


    public GridUserRecordsTable(DorianSessionProvider session) {
        super(createTableModel());
        this.session = session;
        TableColumn c = this.getColumn(USER);
        c.setMaxWidth(0);
        c.setMinWidth(0);
        c.setPreferredWidth(0);
        c.setResizable(false);

        c = this.getColumn(IDENTITY);
        c.setMinWidth(350);
        c.setPreferredWidth(0);

        this.clearTable();

    }


    public static DefaultTableModel createTableModel() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn(USER);
        model.addColumn(IDENTITY);
        model.addColumn(FIRST_NAME);
        model.addColumn(LAST_NAME);
        model.addColumn(EMAIL);
        return model;

    }


    public void addUser(final GridUserRecord u) {
        Vector v = new Vector();
        v.add(u);
        v.add(u.getIdentity());
        v.add(u.getFirstName());
        v.add(u.getLastName());
        v.add(u.getEmail());
        addRow(v);
    }


    public synchronized GridUserRecord getSelectedUser() throws Exception {
        int row = getSelectedRow();
        if ((row >= 0) && (row < getRowCount())) {
            return (GridUserRecord) getValueAt(row, 0);
        } else {
            throw new Exception("Please select a user!!!");
        }
    }


    public synchronized void removeSelectedUser() throws Exception {
        int row = getSelectedRow();
        if ((row >= 0) && (row < getRowCount())) {
            removeRow(row);
        } else {
            throw new Exception("Please select a user!!!");
        }
    }


    public void doubleClick() {

    }


    public void singleClick() throws Exception {
        // TODO Auto-generated method stub

    }

}