package org.cagrid.gaards.ui.dorian.idp;

import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.security.ProxyUtil;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;
import org.cagrid.gaards.credentials.CredentialEntryFactory;
import org.cagrid.gaards.credentials.DorianUserCredentialEntry;
import org.cagrid.gaards.credentials.X509CredentialEntry;
import org.cagrid.gaards.dorian.client.LocalUserClient;
import org.cagrid.gaards.dorian.idp.AccountProfile;
import org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault;
import org.cagrid.gaards.ui.common.CredentialManager;
import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.gaards.ui.dorian.DorianHandle;
import org.cagrid.gaards.ui.dorian.DorianLookAndFeel;
import org.cagrid.gaards.ui.dorian.DorianSession;
import org.cagrid.gaards.ui.dorian.ServicesManager;
import org.cagrid.grape.ApplicationComponent;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.utils.ErrorDialog;
import org.globus.gsi.GlobusCredential;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Langella </A>
 */
public class AccountProfileWindow extends ApplicationComponent {
	private static Logger log = Logger.getLogger(AccountProfileWindow.class);
	
    private static final long serialVersionUID = 1L;

    private final static String INFO_PANEL = "User Information";

    private javax.swing.JPanel jContentPane = null;

    private JPanel mainPanel = null;

    private ProgressPanel progressPanel = null;

    private JButton update = null;

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

    private AccountProfile profile;

    private JPanel infoPanel = null;

    private DorianSession session;

    private JPanel infoButtonPanel = null;

    private boolean modificationAllowed = false;


    /**
     * This is the default constructor
     */
    public AccountProfileWindow() {
        super();
        try {

            GlobusCredential cred = ProxyUtil.getDefaultProxy();
            X509CredentialEntry defaultCredential = CredentialEntryFactory.getEntry(cred);
            defaultCredential = CredentialManager.getInstance().setDefaultCredential(defaultCredential);
            if (defaultCredential instanceof DorianUserCredentialEntry) {
                DorianUserCredentialEntry entry = (DorianUserCredentialEntry) defaultCredential;
                DorianHandle handle = ServicesManager.getInstance().getDorianHandle(entry.getDorianURL());
                if (handle == null) {
                    throw new Exception("Cannot determine the connection information for " + entry.getDorianURL() + ".");
                }
                this.session = new DorianSession(handle, entry.getCredential());
                this.profile = this.session.getLocalUserClient().getAccountProfile();
                this.modificationAllowed = session.getHandle().localAccountModification();
            } else {
                throw new Exception("The account manager for your credential could not be determined.");
            }
        } catch (Exception e) {
            ErrorDialog.showError(e);
            log.error(e, e);
            dispose();
        }
        initialize();
        this.setFrameIcon(DorianLookAndFeel.getUserIcon());
        setActiveComponents(true);
    }


    private void setActiveComponents(boolean enabled) {
        getUsername().setEditable(false);
        if (this.modificationAllowed) {
            getFirstName().setEditable(enabled);
            getLastName().setEditable(enabled);
            getOrganization().setEditable(enabled);
            getPhoneNumber().setEditable(enabled);
            getEmail().setEditable(enabled);
            getAddress().setEditable(enabled);
            getAddress2().setEditable(enabled);
            getCity().setEditable(enabled);
            getState().setEnabled(enabled);
            getZipcode().setEditable(enabled);
            getCountry().setEnabled(enabled);
            getUpdate().setEnabled(enabled);
        } else {
            getFirstName().setEditable(false);
            getLastName().setEditable(false);
            getOrganization().setEditable(false);
            getPhoneNumber().setEditable(false);
            getEmail().setEditable(false);
            getAddress().setEditable(false);
            getAddress2().setEditable(false);
            getCity().setEditable(false);
            getState().setEnabled(false);
            getZipcode().setEditable(false);
            getCountry().setEnabled(false);
            getUpdate().setEnabled(false);
            getUpdate().setVisible(false);
        }
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        this.setContentPane(getJContentPane());
        this.setTitle(profile.getFirstName() + " " + profile.getLastName() + " (" + profile.getUserId() + ")");
        this.setSize(600, 350);

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
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.fill = GridBagConstraints.BOTH;
            gridBagConstraints4.weightx = 1.0D;
            gridBagConstraints4.weighty = 1.0D;
            gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints4.gridy = 1;
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
            mainPanel.add(getInfoPanel(), gridBagConstraints4);
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
    private JButton getUpdate() {
        if (update == null) {
            update = new JButton();
            update.setText("Modify");
            update.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {
                        public void execute() {
                            modifyProfile();
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
        return update;
    }


    private void modifyProfile() {
        this.setActiveComponents(false);
        getProgressPanel().showProgress("Modifying user profile...");
        profile.setFirstName(getFirstName().getText());
        profile.setLastName(getLastName().getText());
        profile.setOrganization(getOrganization().getText());
        profile.setAddress(getAddress().getText());
        profile.setAddress2(getAddress2().getText());
        profile.setCity(getCity().getText());
        profile.setState(getState().getSelectedState());
        profile.setZipcode(getZipcode().getText());
        profile.setCountry(getCountry().getSelectedCountry());
        profile.setPhoneNumber(getPhoneNumber().getText());
        profile.setEmail(getEmail().getText());

        try {
            LocalUserClient client = session.getLocalUserClient();
            client.updateAccountProfile(profile);
            getProgressPanel().stopProgress("User profile successfully modified.");
        } catch (PermissionDeniedFault pdf) {
            ErrorDialog.showError(pdf);
            log.error(pdf, pdf);
            getProgressPanel().stopProgress("Error.");
        } catch (Exception e) {
            ErrorDialog.showError(e);
            log.error(e, e);
            getProgressPanel().stopProgress("Error.");
        }
        setActiveComponents(true);
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
            firstName.setText(profile.getFirstName());
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
            lastName.setText(profile.getLastName());
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
            organization.setText(profile.getOrganization());
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
            address.setText(profile.getAddress());
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
            address2.setText(profile.getAddress2());
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
            city.setText(profile.getCity());
        }
        return city;
    }


    private StateListComboBox getState() {
        if (state == null) {
            state = new StateListComboBox(false);
            state.setSelectedItem(profile.getState());
        }
        return state;
    }


    private JTextField getZipcode() {
        if (zipcode == null) {
            zipcode = new JTextField();
            zipcode.setText(profile.getZipcode());
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
            phoneNumber.setText(profile.getPhoneNumber());
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
            email.setText(profile.getEmail());
        }
        return email;
    }


    private CountryListComboBox getCountry() {
        if (country == null) {
            country = new CountryListComboBox(false);
            country.setSelectedItem(profile.getCountry());
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
            username.setText(profile.getUserId());
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
            titlePanel = new TitlePanel(this.profile.getFirstName() + " " + this.profile.getLastName(), this.profile
                .getEmail());
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
     * This method initializes infoButtonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getInfoButtonPanel() {
        if (infoButtonPanel == null) {
            infoButtonPanel = new JPanel();
            infoButtonPanel.setLayout(new FlowLayout());
            infoButtonPanel.add(getUpdate(), null);
        }
        return infoButtonPanel;
    }

}
