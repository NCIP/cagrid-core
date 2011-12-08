package org.cagrid.gaards.ui.dorian.federation;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;

import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.gaards.authentication.client.AuthenticationServiceClient;
import org.cagrid.gaards.authentication.lockout.LockedUserInfo;
import org.cagrid.grape.utils.ErrorDialog;


/**
 * IdPLockoutsPanel A panel to show the administrator which accounts are
 * currently locked out, and when the lockout will be released.
 * 
 * @author ervin
 */
public class IdPLockoutsPanel extends JPanel {
    private JScrollPane scrollPane = null;
    private JTable lockedUsersTable = null;
    private JButton reloadButton = null;
    
    private AuthenticationServiceClient authnServiceClient = null;

    public IdPLockoutsPanel() {
        super();
        initialize();
    }


    public void loadFromIdP(AuthenticationServiceClient client) throws RemoteException, MalformedURIException {
        this.authnServiceClient = client;
        DefaultTableModel tableModel = (DefaultTableModel) getLockedUsersTable().getModel();
        // clear out the rows in the table
        while (tableModel.getRowCount() > 0) {
            tableModel.removeRow(0);
        }

        // get the locked out users from the IdP
        LockedUserInfo[] lockouts = client.getLockedOutUsers();

        // sort the lockouts by user ID, then lockout end time, if necessary
        Arrays.sort(lockouts, new Comparator<LockedUserInfo>() {

            @Override
            public int compare(LockedUserInfo o1, LockedUserInfo o2) {
                String id1 = o1.getUserId();
                String id2 = o2.getUserId();
                int value = id1.compareTo(id2);
                if (value == 0) {
                    Calendar cal1 = o1.getUntil();
                    Calendar cal2 = o2.getUntil();
                    value = cal1.compareTo(cal2);
                }
                return value;
            }
        });

        // add rows to the table
        for (LockedUserInfo lockout : lockouts) {
            Object[] row = new Object[]{lockout.getUntil(), lockout.getUntil().getTime()};
            tableModel.addRow(row);
        }
    }


    private void initialize() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{0, 0};
        gridBagLayout.rowHeights = new int[]{0, 0, 0};
        gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
        setLayout(gridBagLayout);

        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.insets = new Insets(2, 2, 5, 2);
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridx = 0;
        gbc_scrollPane.gridy = 0;
        add(getScrollPane(), gbc_scrollPane);
        GridBagConstraints gbc_reloadButton = new GridBagConstraints();
        gbc_reloadButton.insets = new Insets(2, 2, 2, 2);
        gbc_reloadButton.gridx = 0;
        gbc_reloadButton.gridy = 1;
        add(getReloadButton(), gbc_reloadButton);
    }


    private JScrollPane getScrollPane() {
        if (scrollPane == null) {
            scrollPane = new JScrollPane();
            scrollPane.setViewportBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
            scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.setViewportView(getLockedUsersTable());
        }
        return scrollPane;
    }


    private JTable getLockedUsersTable() {
        if (lockedUsersTable == null) {
            lockedUsersTable = new JTable();
            lockedUsersTable.setModel(new DefaultTableModel(new Object[][]{}, 
                new String[]{"User ID", "Lockout Exipration"}) {
                Class<?>[] columnTypes = new Class[]{String.class, Date.class};

                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    return columnTypes[columnIndex];
                }
            });
        }
        return lockedUsersTable;
    }


    private JButton getReloadButton() {
        if (reloadButton == null) {
            reloadButton = new JButton("Reload");
            reloadButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (IdPLockoutsPanel.this.authnServiceClient != null) {
                        try {
                            loadFromIdP(IdPLockoutsPanel.this.authnServiceClient);
                        } catch (Exception ex) {
                            ErrorDialog.showError(ex);
                        }
                    }
                }
            });
        }
        return reloadButton;
    }
}
