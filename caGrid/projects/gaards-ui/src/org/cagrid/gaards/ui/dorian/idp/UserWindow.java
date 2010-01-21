package org.cagrid.gaards.ui.dorian.idp;

import gov.nih.nci.cagrid.common.Runner;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gaards.dorian.client.LocalAdministrationClient;
import org.cagrid.gaards.dorian.idp.LocalUser;
import org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault;
import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.gaards.ui.dorian.DorianLookAndFeel;
import org.cagrid.gaards.ui.dorian.DorianSessionProvider;
import org.cagrid.grape.ApplicationComponent;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.utils.ErrorDialog;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Langella </A>
 */
public class UserWindow extends ApplicationComponent {
	private static Log log = LogFactory.getLog(UserWindow.class);
	
    private static final long serialVersionUID = 1L;

    private final static String ACCOUNT_PANEL = "Account Information";

    private final static String INFO_PANEL = "User Information";

    private final static String PASSWORD_PANEL = "Change Password";

    private final static String PASSWORD_SECURITY = "Password Security";  
    
    private final static String AUDTING = "Audting";

    private javax.swing.JPanel jContentPane = null;

    private JPanel mainPanel = null;

    private ProgressPanel progressPanel = null;

    private JButton update1 = null;

    private JTabbedPane jTabbedPane = null;

    private JPanel jPanel1 = null;

    private JLabel jLabel = null;

    private JTextField firstName = null;

    private JLabel jLabel1 = null;

    private JTextField lastName = null;

    private JLabel jLabel2 = null;

    private JTextField organization = null;

    private JLabel jLabel3 = null;

    private JTextField address = null;

    private JLabel jLabel4 = null;

    private JTextField address2 = null;

    private JLabel jLabel5 = null;

    private JTextField city = null;

    private JLabel jLabel6 = null;

    private StateListComboBox state = null;

    private JLabel jLabel7 = null;

    private JTextField zipcode = null;

    private JLabel jLabel8 = null;

    private JTextField phoneNumber = null;

    private JLabel jLabel9 = null;

    private JTextField email = null;

    private JLabel jLabel10 = null;

    private CountryListComboBox country = null;

    private JLabel jLabel11 = null;

    private JTextField username = null;

    private JPanel titlePanel = null;

    private LocalUser user;

    private JPanel infoPanel = null;

    private JPanel accountPanel = null;

    private JLabel statusLabel = null;

    private JComboBox userStatus = null;

    private JLabel roleLabel = null;

    private JComboBox userRole = null;

    private JPanel passwordPanel = null;

    private JLabel passwordLabel = null;

    private JLabel verifyLabel = null;

    private JPasswordField password = null;

    private JPasswordField verifyPassword = null;

    private JPanel passwordSecurityPanel = null;

    private JLabel jLabel12 = null;

    private JTextField passwordStatus = null;

    private JLabel jLabel13 = null;

    private JTextField consecutiveInvalidLogins = null;

    private JLabel jLabel15 = null;

    private JTextField totalInvalidLogins = null;

    private JLabel jLabel16 = null;

    private JTextField lockoutExpiration = null;

    private DorianSessionProvider session;

    private JLabel jLabel14 = null;

    private JTextField digestAlgorithm = null;

    private JPanel infoButtonPanel = null;

    private JPanel statusPanel = null;

    private JPanel accountButtonPanel = null;

    private JButton update2 = null;

    private JPanel changePasswordPanel = null;

    private JPanel changePasswordButtonPanel = null;

    private JButton update3 = null;

    private IdentityProviderAuditPanel auditingPanel = null;


    /**
     * This is the default constructor
     */
    public UserWindow(DorianSessionProvider session, LocalUser u) {
        super();
        this.session = session;
        this.user = u;
        initialize();
        this.setFrameIcon(DorianLookAndFeel.getUserIcon());
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        this.setContentPane(getJContentPane());
        this.setTitle("Manage User [" + user.getUserId() + "]");
        this.setSize(600, 400);

    }


    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private javax.swing.JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new javax.swing.JPanel();
            jContentPane.setLayout(new java.awt.BorderLayout());
            jContentPane.add(getMainPanel(), java.awt.BorderLayout.CENTER);
        }
        return jContentPane;
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.fill = GridBagConstraints.BOTH;
            gridBagConstraints4.gridy = 1;
            gridBagConstraints4.weightx = 1.0;
            gridBagConstraints4.weighty = 1.0D;
            gridBagConstraints4.gridx = 0;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.weightx = 1.0D;
            gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTH;
            gridBagConstraints1.insets = new Insets(5, 5, 5, 5);
            gridBagConstraints1.gridx = 0;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.gridy = 2;
            gridBagConstraints2.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints2.anchor = java.awt.GridBagConstraints.SOUTH;
            gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
            mainPanel.add(getProgressPanel(), gridBagConstraints2);
            mainPanel.add(getTitlePanel(), gridBagConstraints1);
            mainPanel.add(getJTabbedPane(), gridBagConstraints4);
        }
        return mainPanel;
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private ProgressPanel getProgressPanel() {
        if (progressPanel == null) {
            progressPanel = new ProgressPanel();
        }
        return progressPanel;
    }


    /**
     * This method initializes manageUser
     * 
     * @return javax.swing.JButton
     */
    private JButton getUpdate1() {
        if (update1 == null) {
            update1 = new JButton();
            update1.setText("Update");
            update1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {
                        public void execute() {
                            updateUser();
                        }
                    };
                    try {
                        GridApplication.getContext().executeInBackground(runner);
                    } catch (Exception t) {
                        t.getMessage();
                    }

                }
            });
        }
        return update1;
    }


    private String format(char[] array) {
        if (array == null) {
            return "";
        } else {
            String s = new String(array);
            if ((s == null) || (s.trim().length() == 0)) {
                return "";
            } else {
                return s.trim();
            }
        }
    }


    private void updateUser() {
        disableAllButtons();
        getProgressPanel().showProgress("Updating user...");
        String pass = format(this.getPassword().getPassword());
        String verify = format(this.getVerifyPassword().getPassword());

        if ((pass.length() > 0) && (verify.length() > 0)) {
            if (pass.equals(verify)) {
                user.setPassword(pass);
            } else {
                ErrorDialog.showError("Cannot update the user " + user.getUserId() + ", password don't match.");
                getProgressPanel().stopProgress("Error.");
                enableAllButtons();
                log.warn("Cannot update the user " + user.getUserId() + ", password don't match.");
                return;
            }
        }else if((pass.length()>0) && (verify.length()<=0)){
            ErrorDialog.showError("Cannot update the user " + user.getUserId() + ", password don't match.");
            getProgressPanel().stopProgress("Error.");
            enableAllButtons();
            log.warn("Cannot update the user " + user.getUserId() + ", password don't match.");
            return;
        }else if((pass.length()<=0) && (verify.length()>0)){
            ErrorDialog.showError("Cannot update the user " + user.getUserId() + ", password don't match.");
            getProgressPanel().stopProgress("Error.");
            enableAllButtons();
            log.warn("Cannot update the user " + user.getUserId() + ", password don't match.");
            return;
        }

        user.setRole(((UserRolesComboBox) this.getUserRole()).getSelectedUserRole());
        user.setStatus(((UserStatusComboBox) this.getUserStatus()).getSelectedUserStatus());

        user.setUserId(getUsername().getText());
        user.setFirstName(getFirstName().getText());
        user.setLastName(getLastName().getText());
        user.setOrganization(getOrganization().getText());
        user.setAddress(getAddress().getText());
        user.setAddress2(getAddress2().getText());
        user.setCity(getCity().getText());
        user.setState(getState().getSelectedState());
        user.setZipcode(getZipcode().getText());
        user.setCountry(getCountry().getSelectedCountry());
        user.setPhoneNumber(getPhoneNumber().getText());
        user.setEmail(getEmail().getText());

        try {
            LocalAdministrationClient client = session.getSession().getLocalAdminClient();
            client.updateUser(user);
            getProgressPanel().stopProgress("User successfully updated.");
        } catch (PermissionDeniedFault pdf) {
            ErrorDialog.showError(pdf);
            getProgressPanel().stopProgress("Error.");
            log.error(pdf, pdf);
        } catch (Exception e) {
            ErrorDialog.showError(e);
            getProgressPanel().stopProgress("Error.");
            log.error(e, e);
        }
        enableAllButtons();
    }


    /**
     * This method initializes jTabbedPane
     * 
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getJTabbedPane() {
        if (jTabbedPane == null) {
            jTabbedPane = new JTabbedPane();
            jTabbedPane.addTab(INFO_PANEL, null, getInfoPanel(), null);
            jTabbedPane.addTab(ACCOUNT_PANEL, null, getAccountPanel(), null);
            jTabbedPane.addTab(PASSWORD_PANEL, null, getPasswordPanel(), null);
            jTabbedPane.addTab(PASSWORD_SECURITY, null, getPasswordSecurityPanel(), null);
            jTabbedPane.addTab(AUDTING, null, getAuditingPanel(), null);
        }
        return jTabbedPane;
    }


    /**
     * This method initializes jPanel1
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel1() {
        if (jPanel1 == null) {
            GridBagConstraints gridBagConstraints26 = new GridBagConstraints();
            gridBagConstraints26.anchor = GridBagConstraints.WEST;
            gridBagConstraints26.insets = new Insets(1, 1, 1, 1);
            gridBagConstraints26.gridwidth = 1;
            gridBagConstraints26.gridx = 1;
            gridBagConstraints26.gridy = 0;
            gridBagConstraints26.weightx = 1.0D;
            gridBagConstraints26.weighty = 0.0D;
            gridBagConstraints26.fill = GridBagConstraints.HORIZONTAL;
            GridBagConstraints gridBagConstraints25 = new GridBagConstraints();
            gridBagConstraints25.anchor = GridBagConstraints.WEST;
            gridBagConstraints25.gridwidth = 1;
            gridBagConstraints25.gridx = 0;
            gridBagConstraints25.gridy = 0;
            gridBagConstraints25.insets = new Insets(1, 1, 1, 1);
            jLabel11 = new JLabel();
            jLabel11.setText("Username");
            GridBagConstraints gridBagConstraints24 = new GridBagConstraints();
            gridBagConstraints24.anchor = GridBagConstraints.WEST;
            gridBagConstraints24.insets = new Insets(1, 1, 1, 1);
            gridBagConstraints24.gridx = 3;
            gridBagConstraints24.gridy = 5;
            gridBagConstraints24.weightx = 1.0D;
            gridBagConstraints24.weighty = 0.0D;
            gridBagConstraints24.fill = GridBagConstraints.HORIZONTAL;
            GridBagConstraints gridBagConstraints23 = new GridBagConstraints();
            gridBagConstraints23.anchor = GridBagConstraints.WEST;
            gridBagConstraints23.gridx = 2;
            gridBagConstraints23.gridy = 5;
            gridBagConstraints23.insets = new Insets(1, 1, 1, 1);
            jLabel10 = new JLabel();
            jLabel10.setText("Country");
            GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
            gridBagConstraints22.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints22.gridx = 1;
            gridBagConstraints22.gridy = 5;
            gridBagConstraints22.weightx = 1.0D;
            gridBagConstraints22.weighty = 0.0D;
            gridBagConstraints22.insets = new Insets(1, 1, 1, 1);
            GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
            gridBagConstraints21.anchor = GridBagConstraints.WEST;
            gridBagConstraints21.gridx = 0;
            gridBagConstraints21.gridy = 5;
            gridBagConstraints21.insets = new Insets(1, 1, 1, 1);
            jLabel9 = new JLabel();
            jLabel9.setText("Email");
            GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
            gridBagConstraints20.anchor = GridBagConstraints.WEST;
            gridBagConstraints20.insets = new Insets(1, 1, 1, 1);
            gridBagConstraints20.gridx = 1;
            gridBagConstraints20.gridy = 4;
            gridBagConstraints20.weightx = 1.0D;
            gridBagConstraints20.weighty = 0.0D;
            gridBagConstraints20.fill = GridBagConstraints.HORIZONTAL;
            GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
            gridBagConstraints19.anchor = GridBagConstraints.WEST;
            gridBagConstraints19.gridx = 0;
            gridBagConstraints19.gridy = 4;
            gridBagConstraints19.insets = new Insets(2, 2, 2, 2);
            jLabel8 = new JLabel();
            jLabel8.setText("Phone Number");
            GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
            gridBagConstraints18.anchor = GridBagConstraints.WEST;
            gridBagConstraints18.insets = new Insets(1, 1, 1, 1);
            gridBagConstraints18.gridx = 3;
            gridBagConstraints18.gridy = 4;
            gridBagConstraints18.weightx = 1.0D;
            gridBagConstraints18.weighty = 0.0D;
            gridBagConstraints18.fill = GridBagConstraints.HORIZONTAL;
            GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
            gridBagConstraints17.anchor = GridBagConstraints.WEST;
            gridBagConstraints17.gridx = 2;
            gridBagConstraints17.gridy = 4;
            gridBagConstraints17.insets = new Insets(1, 1, 1, 1);
            jLabel7 = new JLabel();
            jLabel7.setText("Zipcode");
            GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
            gridBagConstraints16.anchor = GridBagConstraints.WEST;
            gridBagConstraints16.insets = new Insets(1, 1, 1, 1);
            gridBagConstraints16.gridx = 3;
            gridBagConstraints16.gridy = 3;
            gridBagConstraints16.weightx = 1.0D;
            gridBagConstraints16.weighty = 0.0D;
            gridBagConstraints16.fill = GridBagConstraints.HORIZONTAL;
            GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
            gridBagConstraints15.anchor = GridBagConstraints.WEST;
            gridBagConstraints15.gridx = 2;
            gridBagConstraints15.gridy = 3;
            gridBagConstraints15.insets = new Insets(1, 1, 1, 1);
            jLabel6 = new JLabel();
            jLabel6.setText("State");
            GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
            gridBagConstraints14.anchor = GridBagConstraints.WEST;
            gridBagConstraints14.insets = new Insets(1, 1, 1, 1);
            gridBagConstraints14.gridx = 3;
            gridBagConstraints14.gridy = 2;
            gridBagConstraints14.weightx = 1.0D;
            gridBagConstraints14.weighty = 0.0D;
            gridBagConstraints14.fill = GridBagConstraints.HORIZONTAL;
            GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
            gridBagConstraints13.anchor = GridBagConstraints.WEST;
            gridBagConstraints13.gridx = 2;
            gridBagConstraints13.gridy = 2;
            gridBagConstraints13.insets = new Insets(1, 1, 1, 1);
            jLabel5 = new JLabel();
            jLabel5.setText("City");
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.anchor = GridBagConstraints.WEST;
            gridBagConstraints12.insets = new Insets(1, 1, 1, 1);
            gridBagConstraints12.gridx = 3;
            gridBagConstraints12.gridy = 1;
            gridBagConstraints12.weightx = 1.0D;
            gridBagConstraints12.weighty = 0.0D;
            gridBagConstraints12.fill = GridBagConstraints.HORIZONTAL;
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.anchor = GridBagConstraints.WEST;
            gridBagConstraints11.gridx = 2;
            gridBagConstraints11.gridy = 1;
            gridBagConstraints11.insets = new Insets(1, 1, 1, 1);
            jLabel4 = new JLabel();
            jLabel4.setText("Address2");
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.anchor = GridBagConstraints.WEST;
            gridBagConstraints10.insets = new Insets(1, 1, 1, 1);
            gridBagConstraints10.gridx = 3;
            gridBagConstraints10.gridy = 0;
            gridBagConstraints10.weightx = 1.0D;
            gridBagConstraints10.weighty = 0.0D;
            gridBagConstraints10.fill = GridBagConstraints.HORIZONTAL;
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.anchor = GridBagConstraints.WEST;
            gridBagConstraints9.gridx = 2;
            gridBagConstraints9.gridy = 0;
            gridBagConstraints9.insets = new Insets(1, 1, 1, 1);
            jLabel3 = new JLabel();
            jLabel3.setText("Address");
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.anchor = GridBagConstraints.WEST;
            gridBagConstraints8.insets = new Insets(1, 1, 1, 1);
            gridBagConstraints8.gridwidth = 1;
            gridBagConstraints8.gridx = 1;
            gridBagConstraints8.gridy = 3;
            gridBagConstraints8.weightx = 1.0D;
            gridBagConstraints8.weighty = 0.0D;
            gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.anchor = GridBagConstraints.WEST;
            gridBagConstraints7.gridwidth = 1;
            gridBagConstraints7.gridx = 0;
            gridBagConstraints7.gridy = 3;
            gridBagConstraints7.insets = new Insets(1, 1, 1, 1);
            jLabel2 = new JLabel();
            jLabel2.setText("Organization");
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.anchor = GridBagConstraints.WEST;
            gridBagConstraints6.insets = new Insets(1, 1, 1, 1);
            gridBagConstraints6.gridx = 1;
            gridBagConstraints6.gridy = 2;
            gridBagConstraints6.weightx = 1.0D;
            gridBagConstraints6.weighty = 0.0D;
            gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.anchor = GridBagConstraints.WEST;
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.gridy = 2;
            gridBagConstraints5.insets = new Insets(1, 1, 1, 1);
            jLabel1 = new JLabel();
            jLabel1.setText("Last Name");
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.anchor = GridBagConstraints.WEST;
            gridBagConstraints3.insets = new Insets(1, 1, 1, 1);
            gridBagConstraints3.gridx = 1;
            gridBagConstraints3.gridy = 1;
            gridBagConstraints3.weightx = 1.0D;
            gridBagConstraints3.weighty = 0.0D;
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.insets = new Insets(1, 1, 1, 1);
            jLabel = new JLabel();
            jLabel.setText("First Name");
            jPanel1 = new JPanel();
            jPanel1.setName(INFO_PANEL);
            jPanel1.setLayout(new GridBagLayout());
            jPanel1.add(jLabel, gridBagConstraints);
            jPanel1.add(getFirstName(), gridBagConstraints3);
            jPanel1.add(jLabel1, gridBagConstraints5);
            jPanel1.add(getLastName(), gridBagConstraints6);
            jPanel1.add(jLabel2, gridBagConstraints7);
            jPanel1.add(getOrganization(), gridBagConstraints8);
            jPanel1.add(jLabel3, gridBagConstraints9);
            jPanel1.add(getAddress(), gridBagConstraints10);
            jPanel1.add(jLabel4, gridBagConstraints11);
            jPanel1.add(getAddress2(), gridBagConstraints12);
            jPanel1.add(jLabel5, gridBagConstraints13);
            jPanel1.add(getCity(), gridBagConstraints14);
            jPanel1.add(jLabel6, gridBagConstraints15);
            jPanel1.add(getState(), gridBagConstraints16);
            jPanel1.add(jLabel7, gridBagConstraints17);
            jPanel1.add(getZipcode(), gridBagConstraints18);
            jPanel1.add(jLabel8, gridBagConstraints19);
            jPanel1.add(getPhoneNumber(), gridBagConstraints20);
            jPanel1.add(jLabel9, gridBagConstraints21);
            jPanel1.add(getEmail(), gridBagConstraints22);
            jPanel1.add(jLabel10, gridBagConstraints23);
            jPanel1.add(getCountry(), gridBagConstraints24);
            jPanel1.add(jLabel11, gridBagConstraints25);
            jPanel1.add(getUsername(), gridBagConstraints26);
        }
        return jPanel1;
    }


    /**
     * This method initializes jTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getFirstName() {
        if (firstName == null) {
            firstName = new JTextField();
            firstName.setText(user.getFirstName());
        }
        return firstName;
    }


    /**
     * This method initializes jTextField1
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getLastName() {
        if (lastName == null) {
            lastName = new JTextField();
            lastName.setText(user.getLastName());
        }
        return lastName;
    }


    /**
     * This method initializes jTextField2
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getOrganization() {
        if (organization == null) {
            organization = new JTextField();
            organization.setText(user.getOrganization());
        }
        return organization;
    }


    /**
     * This method initializes jTextField3
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getAddress() {
        if (address == null) {
            address = new JTextField();
            address.setText(user.getAddress());
        }
        return address;
    }


    /**
     * This method initializes jTextField4
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getAddress2() {
        if (address2 == null) {
            address2 = new JTextField();
            address2.setText(user.getAddress2());
        }
        return address2;
    }


    /**
     * This method initializes jTextField5
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getCity() {
        if (city == null) {
            city = new JTextField();
            city.setText(user.getCity());
        }
        return city;
    }


    private StateListComboBox getState() {
        if (state == null) {
            state = new StateListComboBox(false);
            state.setSelectedItem(user.getState());
        }
        return state;
    }


    private JTextField getZipcode() {
        if (zipcode == null) {
            zipcode = new JTextField();
            zipcode.setText(user.getZipcode());
        }
        return zipcode;
    }


    /**
     * This method initializes jTextField7
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getPhoneNumber() {
        if (phoneNumber == null) {
            phoneNumber = new JTextField();
            phoneNumber.setText(user.getPhoneNumber());
        }
        return phoneNumber;
    }


    /**
     * This method initializes jTextField8
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getEmail() {
        if (email == null) {
            email = new JTextField();
            email.setText(user.getEmail());
        }
        return email;
    }


    private CountryListComboBox getCountry() {
        if (country == null) {
            country = new CountryListComboBox(false);
            country.setSelectedItem(user.getCountry());
        }
        return country;
    }


    /**
     * This method initializes jTextField9
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getUsername() {
        if (username == null) {
            username = new JTextField();
            username.setEditable(false);
            username.setText(user.getUserId());
        }
        return username;
    }


    /**
     * This method initializes titlePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTitlePanel() {
        if (titlePanel == null) {
            titlePanel = new TitlePanel(this.user.getFirstName() + " " + this.user.getLastName(), this.user.getEmail());
        }
        return titlePanel;
    }


    /**
     * This method initializes infoPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getInfoPanel() {
        if (infoPanel == null) {
            infoPanel = new JPanel();
            infoPanel.setLayout(new BorderLayout());
            infoPanel.add(getJPanel1(), java.awt.BorderLayout.NORTH);
            infoPanel.add(getInfoButtonPanel(), BorderLayout.SOUTH);
        }
        return infoPanel;
    }


    /**
     * This method initializes accountPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getAccountPanel() {
        if (accountPanel == null) {
            GridBagConstraints gridBagConstraints46 = new GridBagConstraints();
            gridBagConstraints46.gridx = 0;
            gridBagConstraints46.anchor = GridBagConstraints.SOUTH;
            gridBagConstraints46.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints46.gridy = 1;
            GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
            gridBagConstraints31.gridx = 0;
            gridBagConstraints31.weightx = 1.0D;
            gridBagConstraints31.weighty = 1.0D;
            gridBagConstraints31.fill = GridBagConstraints.BOTH;
            gridBagConstraints31.gridy = 0;

            accountPanel = new JPanel();
            accountPanel.setLayout(new GridBagLayout());
            accountPanel.add(getStatusPanel(), gridBagConstraints31);
            accountPanel.add(getAccountButtonPanel(), gridBagConstraints46);
        }
        return accountPanel;
    }


    /**
     * This method initializes userStatus
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getUserStatus() {
        if (userStatus == null) {
            userStatus = new UserStatusComboBox();
            userStatus.setSelectedItem(user.getStatus());
        }
        return userStatus;
    }


    /**
     * This method initializes userRole
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getUserRole() {
        if (userRole == null) {
            userRole = new UserRolesComboBox();
            userRole.setSelectedItem(user.getRole());
        }
        return userRole;
    }


    /**
     * This method initializes passwordPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getPasswordPanel() {
        if (passwordPanel == null) {
            GridBagConstraints gridBagConstraints49 = new GridBagConstraints();
            gridBagConstraints49.gridx = 0;
            gridBagConstraints49.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints49.gridy = 1;
            GridBagConstraints gridBagConstraints48 = new GridBagConstraints();
            gridBagConstraints48.fill = GridBagConstraints.BOTH;
            gridBagConstraints48.gridx = 0;
            gridBagConstraints48.gridy = 0;
            gridBagConstraints48.weightx = 1.0D;
            gridBagConstraints48.weighty = 1.0D;
            gridBagConstraints48.anchor = GridBagConstraints.NORTH;
            passwordPanel = new JPanel();
            passwordPanel.setLayout(new GridBagLayout());
            passwordPanel.add(getChangePasswordPanel(), gridBagConstraints48);
            passwordPanel.add(getChangePasswordButtonPanel(), gridBagConstraints49);
        }
        return passwordPanel;
    }


    /**
     * This method initializes password
     * 
     * @return javax.swing.JPasswordField
     */
    private JPasswordField getPassword() {
        if (password == null) {
            password = new JPasswordField();
        }
        return password;
    }


    /**
     * This method initializes verifyPassword
     * 
     * @return javax.swing.JPasswordField
     */
    private JPasswordField getVerifyPassword() {
        if (verifyPassword == null) {
            verifyPassword = new JPasswordField();
        }
        return verifyPassword;
    }


    /**
     * This method initializes passwordSecurityPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getPasswordSecurityPanel() {
        if (passwordSecurityPanel == null) {
            GridBagConstraints gridBagConstraints28 = new GridBagConstraints();
            gridBagConstraints28.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints28.gridy = 0;
            gridBagConstraints28.weightx = 1.0;
            gridBagConstraints28.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints28.anchor = GridBagConstraints.WEST;
            gridBagConstraints28.gridx = 1;
            GridBagConstraints gridBagConstraints27 = new GridBagConstraints();
            gridBagConstraints27.anchor = GridBagConstraints.WEST;
            gridBagConstraints27.gridx = 0;
            gridBagConstraints27.gridy = 0;
            gridBagConstraints27.insets = new Insets(2, 2, 2, 2);
            jLabel14 = new JLabel();
            jLabel14.setText("Digest Algorithm");
            GridBagConstraints gridBagConstraints45 = new GridBagConstraints();
            gridBagConstraints45.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints45.gridy = 4;
            gridBagConstraints45.weightx = 1.0;
            gridBagConstraints45.anchor = GridBagConstraints.WEST;
            gridBagConstraints45.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints45.gridx = 1;
            GridBagConstraints gridBagConstraints44 = new GridBagConstraints();
            gridBagConstraints44.gridx = 0;
            gridBagConstraints44.anchor = GridBagConstraints.WEST;
            gridBagConstraints44.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints44.gridy = 4;
            jLabel16 = new JLabel();
            jLabel16.setText("Lockout Expiration");
            GridBagConstraints gridBagConstraints43 = new GridBagConstraints();
            gridBagConstraints43.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints43.gridy = 3;
            gridBagConstraints43.weightx = 1.0;
            gridBagConstraints43.anchor = GridBagConstraints.WEST;
            gridBagConstraints43.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints43.gridx = 1;
            GridBagConstraints gridBagConstraints42 = new GridBagConstraints();
            gridBagConstraints42.gridx = 0;
            gridBagConstraints42.anchor = GridBagConstraints.WEST;
            gridBagConstraints42.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints42.gridy = 3;
            jLabel15 = new JLabel();
            jLabel15.setText("Total Invalid Logins");
            GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
            gridBagConstraints41.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints41.gridy = 2;
            gridBagConstraints41.weightx = 1.0;
            gridBagConstraints41.anchor = GridBagConstraints.WEST;
            gridBagConstraints41.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints41.gridx = 1;
            GridBagConstraints gridBagConstraints40 = new GridBagConstraints();
            gridBagConstraints40.gridx = 0;
            gridBagConstraints40.anchor = GridBagConstraints.WEST;
            gridBagConstraints40.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints40.gridy = 2;
            jLabel13 = new JLabel();
            jLabel13.setText("Consecutive Invalid Logins");
            GridBagConstraints gridBagConstraints39 = new GridBagConstraints();
            gridBagConstraints39.anchor = GridBagConstraints.WEST;
            gridBagConstraints39.gridy = 1;
            gridBagConstraints39.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints39.gridx = 0;
            GridBagConstraints gridBagConstraints38 = new GridBagConstraints();
            gridBagConstraints38.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints38.anchor = GridBagConstraints.WEST;
            gridBagConstraints38.gridx = 1;
            gridBagConstraints38.gridy = 1;
            gridBagConstraints38.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints38.weightx = 1.0;
            jLabel12 = new JLabel();
            jLabel12.setText("Password Status");
            passwordSecurityPanel = new JPanel();
            passwordSecurityPanel.setLayout(new GridBagLayout());
            passwordSecurityPanel.add(jLabel12, gridBagConstraints39);
            passwordSecurityPanel.add(getPasswordStatus(), gridBagConstraints38);
            passwordSecurityPanel.add(jLabel13, gridBagConstraints40);
            passwordSecurityPanel.add(getConsecutiveInvalidLogins(), gridBagConstraints41);
            passwordSecurityPanel.add(jLabel15, gridBagConstraints42);
            passwordSecurityPanel.add(getTotalInvalidLogins(), gridBagConstraints43);
            passwordSecurityPanel.add(jLabel16, gridBagConstraints44);
            passwordSecurityPanel.add(getLockoutExpiration(), gridBagConstraints45);
            passwordSecurityPanel.add(jLabel14, gridBagConstraints27);
            passwordSecurityPanel.add(getDigestAlgorithm(), gridBagConstraints28);
        }
        return passwordSecurityPanel;
    }


    /**
     * This method initializes passwordStatus
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getPasswordStatus() {
        if (passwordStatus == null) {
            passwordStatus = new JTextField();
            passwordStatus.setEditable(false);
            passwordStatus.setText(user.getPasswordSecurity().getPasswordStatus().getValue());
        }
        return passwordStatus;
    }


    /**
     * This method initializes consecutiveInvalidLogins
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getConsecutiveInvalidLogins() {
        if (consecutiveInvalidLogins == null) {
            consecutiveInvalidLogins = new JTextField();
            consecutiveInvalidLogins.setEditable(false);
            consecutiveInvalidLogins.setText(String.valueOf(user.getPasswordSecurity().getConsecutiveInvalidLogins()));
        }
        return consecutiveInvalidLogins;
    }


    /**
     * This method initializes totalInvalidLogins
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getTotalInvalidLogins() {
        if (totalInvalidLogins == null) {
            totalInvalidLogins = new JTextField();
            totalInvalidLogins.setEditable(false);
            totalInvalidLogins.setText(String.valueOf(user.getPasswordSecurity().getTotalInvalidLogins()));
        }
        return totalInvalidLogins;
    }


    /**
     * This method initializes lockoutExpiration
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getLockoutExpiration() {
        if (lockoutExpiration == null) {
            lockoutExpiration = new JTextField();
            lockoutExpiration.setEditable(false);
            long time = user.getPasswordSecurity().getLockoutExpiration();
            if (time == 0) {
                lockoutExpiration.setText("Password has never been locked.");
            } else {
                Date d = new Date(time);
                lockoutExpiration.setText(d.toString());
            }
        }
        return lockoutExpiration;
    }


    /**
     * This method initializes digestAlgorithm
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getDigestAlgorithm() {
        if (digestAlgorithm == null) {
            digestAlgorithm = new JTextField();
            digestAlgorithm.setEditable(false);
            digestAlgorithm.setText(user.getPasswordSecurity().getDigestAlgorithm());
        }
        return digestAlgorithm;
    }


    /**
     * This method initializes infoButtonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getInfoButtonPanel() {
        if (infoButtonPanel == null) {
            infoButtonPanel = new JPanel();
            infoButtonPanel.setLayout(new FlowLayout());
            infoButtonPanel.add(getUpdate1(), null);
        }
        return infoButtonPanel;
    }


    /**
     * This method initializes statusPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getStatusPanel() {
        if (statusPanel == null) {
            GridBagConstraints gridBagConstraints33 = new GridBagConstraints();
            gridBagConstraints33.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints33.gridy = 1;
            gridBagConstraints33.weightx = 1.0;
            gridBagConstraints33.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints33.insets = new java.awt.Insets(5, 5, 5, 5);
            gridBagConstraints33.gridx = 1;
            GridBagConstraints gridBagConstraints32 = new GridBagConstraints();
            gridBagConstraints32.gridx = 0;
            gridBagConstraints32.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints32.insets = new java.awt.Insets(5, 5, 5, 5);
            gridBagConstraints32.gridy = 1;
            roleLabel = new JLabel();
            roleLabel.setText("User Role");
            GridBagConstraints gridBagConstraints30 = new GridBagConstraints();
            gridBagConstraints30.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints30.gridx = 1;
            gridBagConstraints30.gridy = 0;
            gridBagConstraints30.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints30.insets = new java.awt.Insets(5, 5, 5, 5);
            gridBagConstraints30.weightx = 1.0;
            GridBagConstraints gridBagConstraints29 = new GridBagConstraints();
            gridBagConstraints29.insets = new java.awt.Insets(5, 5, 5, 5);
            gridBagConstraints29.gridy = 0;
            gridBagConstraints29.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints29.gridx = 0;
            statusLabel = new JLabel();
            statusLabel.setText("User Status");
            statusPanel = new JPanel();
            statusPanel.setLayout(new GridBagLayout());
            statusPanel.add(statusLabel, gridBagConstraints29);
            statusPanel.add(getUserStatus(), gridBagConstraints30);
            statusPanel.add(roleLabel, gridBagConstraints32);
            statusPanel.add(getUserRole(), gridBagConstraints33);
        }
        return statusPanel;
    }


    /**
     * This method initializes accountButtonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getAccountButtonPanel() {
        if (accountButtonPanel == null) {
            GridBagConstraints gridBagConstraints47 = new GridBagConstraints();
            gridBagConstraints47.gridx = -1;
            gridBagConstraints47.gridy = -1;
            accountButtonPanel = new JPanel();
            accountButtonPanel.setLayout(new FlowLayout());
            accountButtonPanel.add(getUpdate2(), null);
        }
        return accountButtonPanel;
    }


    /**
     * This method initializes update2	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getUpdate2() {
        if (update2 == null) {
            update2 = new JButton();
            update2.setText("Update");
            update2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {
                        public void execute() {
                            updateUser();
                        }
                    };
                    try {
                        GridApplication.getContext().executeInBackground(runner);
                    } catch (Exception t) {
                        t.getMessage();
                    }

                }
            });
        }
        return update2;
    }


    /**
     * This method initializes changePasswordPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getChangePasswordPanel() {
        if (changePasswordPanel == null) {
            changePasswordPanel = new JPanel();
            changePasswordPanel.setLayout(new GridBagLayout());
            GridBagConstraints gridBagConstraints37 = new GridBagConstraints();
            gridBagConstraints37.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints37.gridy = 1;
            gridBagConstraints37.weightx = 1.0;
            gridBagConstraints37.insets = new java.awt.Insets(5, 5, 5, 5);
            gridBagConstraints37.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints37.gridx = 1;
            GridBagConstraints gridBagConstraints35 = new GridBagConstraints();
            gridBagConstraints35.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints35.gridy = 0;
            gridBagConstraints35.weightx = 1.0;
            gridBagConstraints35.insets = new java.awt.Insets(5, 5, 5, 5);
            gridBagConstraints35.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints35.gridx = 1;
            GridBagConstraints gridBagConstraints36 = new GridBagConstraints();
            gridBagConstraints36.gridx = 0;
            gridBagConstraints36.insets = new java.awt.Insets(5, 5, 5, 5);
            gridBagConstraints36.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints36.gridy = 1;
            verifyLabel = new JLabel();
            verifyLabel.setText("Verify Password");
            GridBagConstraints gridBagConstraints34 = new GridBagConstraints();
            gridBagConstraints34.insets = new java.awt.Insets(5, 5, 5, 5);
            gridBagConstraints34.gridy = 0;
            gridBagConstraints34.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints34.gridx = 0;
            passwordLabel = new JLabel();
            passwordLabel.setText("Password");
            changePasswordPanel.add(passwordLabel, gridBagConstraints34);
            changePasswordPanel.add(verifyLabel, gridBagConstraints36);
            changePasswordPanel.add(getPassword(), gridBagConstraints35);
            changePasswordPanel.add(getVerifyPassword(), gridBagConstraints37);
        }
        return changePasswordPanel;
    }


    /**
     * This method initializes changePasswordButtonPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getChangePasswordButtonPanel() {
        if (changePasswordButtonPanel == null) {
            changePasswordButtonPanel = new JPanel();
            changePasswordButtonPanel.setLayout(new FlowLayout());
            changePasswordButtonPanel.add(getUpdate3(), null);
        }
        return changePasswordButtonPanel;
    }


    /**
     * This method initializes update3	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getUpdate3() {
        if (update3 == null) {
            update3 = new JButton();
            update3.setText("Update");
            update3.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {
                        public void execute() {
                            updateUser();
                        }
                    };
                    try {
                        GridApplication.getContext().executeInBackground(runner);
                    } catch (Exception t) {
                        t.getMessage();
                    }

                }
            });
        }
        return update3;
    }
    
    private void disableAllButtons(){
        getUpdate1().setEnabled(false);
        getUpdate2().setEnabled(false);
        getUpdate3().setEnabled(false);
    }
    
    private void enableAllButtons(){
        getUpdate1().setEnabled(true);
        getUpdate2().setEnabled(true);
        getUpdate3().setEnabled(true);
    }


    /**
     * This method initializes auditingPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private IdentityProviderAuditPanel getAuditingPanel() {
        if (auditingPanel == null) {
            auditingPanel = new IdentityProviderAuditPanel(this.session, this.user.getUserId());
            auditingPanel.setProgess(getProgressPanel());
        }
        return auditingPanel;
    }

}
