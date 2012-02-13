package gov.nih.nci.cagrid.introduce.portal.modification.security;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;
import gov.nih.nci.cagrid.introduce.portal.common.IntroduceLookAndFeel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.cagrid.grape.utils.CompositeErrorDialog;
import org.globus.util.QuotedStringTokenizer;


public class GridMapPanel extends JPanel {

    private static final String COMMENT_CHARS = "#";

    private JPanel gridMapFilePanel = null;

    private JLabel jLabel = null;

    private JTextField gridmapLocation = null;

    private JButton browseButton = null;

    private JLabel jLabel1 = null;

    private JPanel tablePanel = null;

    private JScrollPane jScrollPane = null;

    private JTable gridmapTable = null;

    private JPanel controlPanel = null;

    private JPanel userAddEditPanel = null;

    private JLabel jLabel2 = null;

    private JTextField gridIdentity = null;

    private JPanel userManagerButtonPanel = null;

    private JButton addButton = null;

    private JButton removeButton = null;

    private JLabel jLabel3 = null;

    private JTextField localUser = null;


    /**
     * This is the default constructor
     */
    public GridMapPanel() {
        super();
        initialize();
    }


    public void setGridMapFile(String gridmap) throws Exception {
        try {
            loadGridMapFile(gridmap, true);
            this.gridmapLocation.setText(gridmap);
        } catch (Exception e) {
            this.gridmapLocation.setText("");
            ((GridMapTable) this.gridmapTable).clearTable();
            throw e;
        }
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
        gridBagConstraints21.gridx = 0;
        gridBagConstraints21.weightx = 1.0D;
        gridBagConstraints21.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints21.gridy = 1;
        GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
        gridBagConstraints11.gridx = 0;
        gridBagConstraints11.insets = new java.awt.Insets(2, 2, 2, 2);
        gridBagConstraints11.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints11.weightx = 1.0D;
        gridBagConstraints11.weighty = 1.0D;
        gridBagConstraints11.gridy = 2;
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0D;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        gridBagConstraints.gridy = 0;
        this.setLayout(new GridBagLayout());
        setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Manage Grid Map File",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
            null, PortalLookAndFeel.getPanelLabelColor()));
        this.add(getGridMapFilePanel(), gridBagConstraints);
        this.add(getTablePanel(), gridBagConstraints11);
        this.add(getControlPanel(), gridBagConstraints21);
    }


    /**
     * This method initializes gridMapFilePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getGridMapFilePanel() {
        if (gridMapFilePanel == null) {
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.gridwidth = 3;
            gridBagConstraints3.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints3.gridy = 1;
            jLabel1 = new JLabel();
            jLabel1.setText("(File will be created if it does not exist)");
            jLabel1.setForeground(PortalLookAndFeel.getPanelLabelColor());

            jLabel1.setFont(new java.awt.Font("Dialog", java.awt.Font.ITALIC, 12));
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.gridx = 1;
            gridBagConstraints2.gridy = 0;
            gridBagConstraints2.weightx = 1.0;
            gridBagConstraints2.insets = new java.awt.Insets(2, 2, 2, 2);
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints1.gridx = 0;
            jLabel = new JLabel();
            jLabel.setText("Grid Map File Location");
            gridMapFilePanel = new JPanel();
            gridMapFilePanel.setLayout(new GridBagLayout());
            gridMapFilePanel.add(jLabel, gridBagConstraints1);
            gridMapFilePanel.add(jLabel1, gridBagConstraints3);
            gridMapFilePanel.add(getGridmapLocation(), gridBagConstraints2);
            gridMapFilePanel.add(getBrowseButton(), new GridBagConstraints());
        }
        return gridMapFilePanel;
    }


    /**
     * This method initializes gridmapLocation
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getGridmapLocation() {
        if (gridmapLocation == null) {
            gridmapLocation = new JTextField();
        }
        return gridmapLocation;
    }


    /**
     * This method initializes browseButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getBrowseButton() {
        if (browseButton == null) {
            browseButton = new JButton();
            browseButton.setText("Browse");
            browseButton.setIcon(IntroduceLookAndFeel.getBrowseIcon());
            browseButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    browse();
                }
            });
        }
        return browseButton;
    }


    private void browse() {
        try {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.setMultiSelectionEnabled(false);
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                String gridmap = fc.getSelectedFile().getAbsolutePath();
                loadGridMapFile(gridmap, false);
                this.gridmapLocation.setText(gridmap);
            }

        } catch (Exception e) {
            this.gridmapLocation.setText("");
            ((GridMapTable) this.gridmapTable).clearTable();
            // PortalUtils.showErrorDialog(e);
            CompositeErrorDialog.showErrorDialog(e);
        }
    }


    /**
     * This method initializes tablePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTablePanel() {
        if (tablePanel == null) {
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints4.weighty = 1.0;
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.gridy = 0;
            gridBagConstraints4.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints4.weightx = 1.0;
            tablePanel = new JPanel();
            tablePanel.setLayout(new GridBagLayout());
            tablePanel.add(getJScrollPane(), gridBagConstraints4);
        }
        return tablePanel;
    }


    /**
     * This method initializes jScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane() {
        if (jScrollPane == null) {
            jScrollPane = new JScrollPane();
            jScrollPane.setViewportView(getGridmapTable());
        }
        return jScrollPane;
    }


    /**
     * This method initializes gridmapTable
     * 
     * @return javax.swing.JTable
     */
    private JTable getGridmapTable() {
        if (gridmapTable == null) {
            gridmapTable = new GridMapTable();
        }
        return gridmapTable;
    }


    /**
     * This method initializes controlPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getControlPanel() {
        if (controlPanel == null) {
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.gridx = 0;
            gridBagConstraints8.gridy = 1;
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.insets = new java.awt.Insets(5, 5, 5, 5);
            gridBagConstraints7.gridy = 0;
            gridBagConstraints7.weightx = 1.0D;
            gridBagConstraints7.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints7.gridx = 0;
            controlPanel = new JPanel();
            controlPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Add/Remove User(s)",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, PortalLookAndFeel.getPanelLabelColor()));
            controlPanel.setLayout(new GridBagLayout());
            controlPanel.add(getUserAddEditPanel(), gridBagConstraints7);
            controlPanel.add(getUserManagerButtonPanel(), gridBagConstraints8);
        }
        return controlPanel;
    }


    /**
     * This method initializes userAddEditPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getUserAddEditPanel() {
        if (userAddEditPanel == null) {
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints10.gridy = 1;
            gridBagConstraints10.weightx = 1.0;
            gridBagConstraints10.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints10.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints10.gridx = 1;
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.gridx = 0;
            gridBagConstraints9.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints9.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints9.gridy = 1;
            jLabel3 = new JLabel();
            jLabel3.setText("Local User");
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints6.gridx = 1;
            gridBagConstraints6.gridy = 0;
            gridBagConstraints6.weightx = 1.0;
            gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints6.insets = new java.awt.Insets(5, 5, 5, 5);
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints5.gridy = 0;
            gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints5.gridx = 0;
            jLabel2 = new JLabel();
            jLabel2.setText("Grid Identity");
            userAddEditPanel = new JPanel();
            userAddEditPanel.setLayout(new GridBagLayout());
            userAddEditPanel.add(jLabel2, gridBagConstraints5);
            userAddEditPanel.add(getGridIdentity(), gridBagConstraints6);
            userAddEditPanel.add(jLabel3, gridBagConstraints9);
            userAddEditPanel.add(getLocalUser(), gridBagConstraints10);
        }
        return userAddEditPanel;
    }


    /**
     * This method initializes gridIdentity
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getGridIdentity() {
        if (gridIdentity == null) {
            gridIdentity = new JTextField();
        }
        return gridIdentity;
    }


    /**
     * This method initializes userManagerButtonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getUserManagerButtonPanel() {
        if (userManagerButtonPanel == null) {
            userManagerButtonPanel = new JPanel();
            userManagerButtonPanel.add(getAddButton(), null);
            userManagerButtonPanel.add(getRemoveButton(), null);
        }
        return userManagerButtonPanel;
    }


    /**
     * This method initializes addButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddButton() {
        if (addButton == null) {
            addButton = new JButton();
            addButton.setText("Add User");
            addButton.setIcon(PortalLookAndFeel.getAddIcon());
            addButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    addUser();
                }
            });
        }
        return addButton;
    }


    private void addUser() {
        String gridId = this.gridIdentity.getText();
        if ((gridId == null) || (gridId.trim().length() == 0)) {
            CompositeErrorDialog.showErrorDialog("Please enter the grid identity of a user to add!!!");
            return;
        }
        String local = Utils.clean(this.localUser.getText());
        if (local == null) {
            CompositeErrorDialog.showErrorDialog("Please enter a local user id of a user to add!!!");
            return;
        }
        ((GridMapTable) this.gridmapTable).addUser(new GridMap(gridId, local));
        this.gridIdentity.setText("");
        this.localUser.setText("");
    }


    private void removeUser() {
        try {
            ((GridMapTable) this.gridmapTable).removeSelectedUser();
        } catch (Exception e) {
            // PortalUtils.showErrorDialog(e);
            CompositeErrorDialog.showErrorDialog(e);
        }
    }


    /**
     * This method initializes removeButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getRemoveButton() {
        if (removeButton == null) {
            removeButton = new JButton();
            removeButton.setText("Remove User");
            removeButton.setIcon(PortalLookAndFeel.getRemoveIcon());
            removeButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    removeUser();
                }
            });
        }
        return removeButton;
    }


    private void loadGridMapFile(String gridmapFile, boolean errorIfMissing) throws Exception {
        ((GridMapTable) this.gridmapTable).clearTable();
        File f = new File(gridmapFile);
        if (f.exists()) {
            BufferedReader in = new BufferedReader(new FileReader(f));
            String line = null;
            QuotedStringTokenizer tokenizer = null;
            StringTokenizer idTokenizer = null;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if ((line.length() == 0) || (COMMENT_CHARS.indexOf(line.charAt(0)) != -1)) {
                    continue;
                }

                tokenizer = new QuotedStringTokenizer(line);

                String globusID = null;

                if (tokenizer.hasMoreTokens()) {
                    globusID = tokenizer.nextToken();
                } else {
                    throw new Exception("Error loading the Gridmap file " + gridmapFile
                        + ", the Grid Identity is not defined in the line:\n" + line);
                }

                String userIDs = null;
                StringBuffer ids = new StringBuffer();
                if (tokenizer.hasMoreTokens()) {
                    userIDs = tokenizer.nextToken();

                    idTokenizer = new StringTokenizer(userIDs, ",");

                    boolean first = true;
                    while (idTokenizer.hasMoreTokens()) {
                        if (!first) {
                            ids.append(",");
                        }
                        ids.append(idTokenizer.nextToken());
                        first = false;
                    }
                } else {
                    throw new Exception("Error loading the Gridmap file " + gridmapFile
                        + ", the local user ids missing in the line:\n" + line);
                }
                GridMap map = new GridMap(globusID, ids.toString());
                ((GridMapTable) this.gridmapTable).addUser(map);
            }

        } else {
            if (errorIfMissing) {
                throw new Exception("The grid map file " + gridmapFile + " does not exist!!!");
            }
        }
    }


    public String saveGridMapAndGetLocation() throws Exception {
        String location = this.gridmapLocation.getText();
        if ((location == null) || (location.trim().length() == 0)) {
            throw new Exception("No Grid Map File Specified!!!");
        } else {
            File f = new File(location);
            PrintWriter out = new PrintWriter(new FileOutputStream(f, false));
            int count = ((GridMapTable) this.gridmapTable).getUserCount();
            for (int i = 0; i < count; i++) {
                GridMap map = ((GridMapTable) this.gridmapTable).getUserAt(i);
                StringBuffer line = new StringBuffer();
                line.append("\"" + map.getGridIdentity() + "\"");
                String users = Utils.clean(map.getLocalUser());
                if (users != null) {
                    line.append(" " + users);
                }
                out.println(line.toString());
            }
            out.close();
            return location;
        }
    }


    /**
     * This method initializes localUser
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getLocalUser() {
        if (localUser == null) {
            localUser = new JTextField();
        }
        return localUser;
    }
}
