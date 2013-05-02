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
package org.cagrid.gaards.ui.dorian.federation;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;

import org.cagrid.gaards.authentication.client.AuthenticationServiceClient;
import org.cagrid.gaards.authentication.lockout.LockedUserInfo;


/**
 * IdPLockoutsPanel A panel to show the administrator which accounts are
 * currently locked out, and when the lockout will be released.
 * 
 * @author ervin
 */
public class IdPLockoutsPanel extends JPanel {
    private JScrollPane scrollPane = null;
    private JTable lockedUsersTable = null;
    private JLabel lockoutsInServicelabel = null;
    private JTextField serviceUrlTextField = null;

    public IdPLockoutsPanel() {
        super();
        initialize();
    }


    public synchronized void loadFromIdP(AuthenticationServiceClient client) throws RemoteException {
        // set the authn service URL in the UI
        String serviceUrl = client.getEndpointReference().getAddress().toString();
        getServiceUrlTextField().setText(serviceUrl);
        
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
        setLayout(gridBagLayout);
        
        GridBagConstraints gbc_lockoutsInServicelabel = new GridBagConstraints();
        gbc_lockoutsInServicelabel.anchor = GridBagConstraints.WEST;
        gbc_lockoutsInServicelabel.insets = new Insets(2, 2, 2, 2);
        gbc_lockoutsInServicelabel.gridx = 0;
        gbc_lockoutsInServicelabel.gridy = 0;
        add(getLockoutsInServicelabel(), gbc_lockoutsInServicelabel);
        
        GridBagConstraints gbc_serviceUrlTextField = new GridBagConstraints();
        gbc_serviceUrlTextField.weightx = 1.0;
        gbc_serviceUrlTextField.insets = new Insets(2, 2, 2, 2);
        gbc_serviceUrlTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_serviceUrlTextField.gridx = 1;
        gbc_serviceUrlTextField.gridy = 0;
        add(getServiceUrlTextField(), gbc_serviceUrlTextField);

        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.gridwidth = 2;
        gbc_scrollPane.insets = new Insets(2, 2, 2, 2);
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridx = 0;
        gbc_scrollPane.gridy = 1;
        gbc_scrollPane.weightx = 1.0d;
        gbc_scrollPane.weighty = 1.0d;
        add(getScrollPane(), gbc_scrollPane);
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


    private JLabel getLockoutsInServicelabel() {
        if (lockoutsInServicelabel == null) {
            lockoutsInServicelabel = new JLabel("Lockouts In Service:");
        }
        return lockoutsInServicelabel;
    }


    private JTextField getServiceUrlTextField() {
        if (serviceUrlTextField == null) {
            serviceUrlTextField = new JTextField();
            serviceUrlTextField.setEditable(false);
            serviceUrlTextField.setColumns(10);
        }
        return serviceUrlTextField;
    }
}
