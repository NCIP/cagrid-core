package org.cagrid.gaards.ui.dorian.idp;

import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.common.Runner;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gaards.dorian.client.LocalUserClient;
import org.cagrid.gaards.dorian.idp.Application;
import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.gaards.ui.dorian.DorianHandle;
import org.cagrid.gaards.ui.dorian.DorianLookAndFeel;
import org.cagrid.gaards.ui.dorian.DorianServiceListComboBox;
import org.cagrid.grape.ApplicationComponent;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.LookAndFeel;
import org.cagrid.grape.utils.ErrorDialog;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 */
public class ApplicationWindow extends ApplicationComponent {
	private static Log log = LogFactory.getLog(ApplicationWindow.class);
	
    private static final long serialVersionUID = 1L;

    private JPanel jContentPane = null;

    private JPanel mainPanel = null;

    private JPanel authPanel = null;

    private JPanel infoPanel = null;

    private JPanel buttonPanel = null;

    private JLabel serviceLabel = null;

    private JComboBox service = null;

    private JLabel usernameLabel = null;

    private JTextField username = null;

    private JLabel passwordLabel = null;

    private JPasswordField password = null;

    private JLabel verifyLabel = null;

    private JPasswordField verify = null;

    private JLabel firstNameLabel = null;

    private JTextField firstName = null;

    private JLabel lastNameLabel = null;

    private JTextField lastName = null;

    private JLabel organizationLabel = null;

    private JTextField organization = null;

    private JLabel addressLabel = null;

    private JTextField address = null;

    private JLabel address2Label = null;

    private JTextField address2 = null;

    private JLabel cityLabel = null;

    private JTextField city = null;

    private JLabel stateLabel = null;

    private JComboBox state = null;

    private JLabel zipcodeLabel = null;

    private JTextField zipcode = null;

    private JLabel phoneNumberLabel = null;

    private JTextField phoneNumber = null;

    private JLabel emailLabel = null;

    private JTextField email = null;

    private JButton applyButton = null;

    private JLabel countryLabel = null;

    private JComboBox country = null;

    private JPanel titlePanel = null;

    private ProgressPanel progressPanel = null;


    /**
     * This is the default constructor
     */
    public ApplicationWindow() {
        super();
        initialize();
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        this.setContentPane(getJContentPane());
        this.setTitle("Account Registration");
        this.setFrameIcon(DorianLookAndFeel.getApplicationIcon());
    }


    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new JPanel();
            jContentPane.setLayout(new BorderLayout());
            jContentPane.add(getMainPanel(), BorderLayout.CENTER);
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
            GridBagConstraints gridBagConstraints210 = new GridBagConstraints();
            gridBagConstraints210.gridx = 0;
            gridBagConstraints210.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints210.weightx = 1.0D;
            gridBagConstraints210.gridy = 4;
            GridBagConstraints gridBagConstraints110 = new GridBagConstraints();
            gridBagConstraints110.gridx = 0;
            gridBagConstraints110.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints110.weightx = 1.0D;
            gridBagConstraints110.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints110.gridy = 0;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints2.gridy = 3;
            gridBagConstraints2.anchor = GridBagConstraints.SOUTH;
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.weightx = 1.0D;
            gridBagConstraints2.gridx = 0;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints1.gridy = 2;
            gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints1.weightx = 1.0D;
            gridBagConstraints1.weighty = 1.0D;
            gridBagConstraints1.gridx = 0;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints.gridy = 1;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.weightx = 1.0D;
            gridBagConstraints.weighty = 1.0D;
            gridBagConstraints.gridx = 0;
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.add(getAuthPanel(), gridBagConstraints);
            mainPanel.add(getInfoPanel(), gridBagConstraints1);
            mainPanel.add(getButtonPanel(), gridBagConstraints2);
            mainPanel.add(getTitlePanel(), gridBagConstraints110);
            mainPanel.add(getProgressPanel(), gridBagConstraints210);
        }
        return mainPanel;
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getAuthPanel() {
        if (authPanel == null) {
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints10.gridy = 3;
            gridBagConstraints10.weightx = 1.0;
            gridBagConstraints10.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints10.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints10.gridx = 1;
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.gridx = 0;
            gridBagConstraints9.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints9.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints9.gridy = 3;
            verifyLabel = new JLabel();
            verifyLabel.setText("Verify Password");
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints8.gridy = 2;
            gridBagConstraints8.weightx = 1.0;
            gridBagConstraints8.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints8.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints8.gridx = 1;
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints7.gridy = 2;
            gridBagConstraints7.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints7.gridx = 0;
            passwordLabel = new JLabel();
            passwordLabel.setText("Password");
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints6.gridy = 1;
            gridBagConstraints6.weightx = 1.0;
            gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints6.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints6.gridx = 1;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints5.gridy = 1;
            usernameLabel = new JLabel();
            usernameLabel.setText("Username");
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.gridx = 1;
            gridBagConstraints4.gridy = 0;
            gridBagConstraints4.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints4.weightx = 1.0;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints3.weightx = 0.0D;
            gridBagConstraints3.gridy = 0;
            serviceLabel = new JLabel();
            serviceLabel.setText("Service");
            authPanel = new JPanel();
            authPanel.setLayout(new GridBagLayout());
            authPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Login Information",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, LookAndFeel.getPanelLabelColor()));
            authPanel.add(serviceLabel, gridBagConstraints3);
            authPanel.add(getService(), gridBagConstraints4);
            authPanel.add(usernameLabel, gridBagConstraints5);
            authPanel.add(getUsername(), gridBagConstraints6);
            authPanel.add(passwordLabel, gridBagConstraints7);
            authPanel.add(getPassword(), gridBagConstraints8);
            authPanel.add(verifyLabel, gridBagConstraints9);
            authPanel.add(getVerify(), gridBagConstraints10);
        }
        return authPanel;
    }


    /**
     * This method initializes infoPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getInfoPanel() {
        if (infoPanel == null) {
            GridBagConstraints gridBagConstraints32 = new GridBagConstraints();
            gridBagConstraints32.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints32.gridy = 8;
            gridBagConstraints32.weightx = 1.0;
            gridBagConstraints32.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints32.gridx = 1;
            GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
            gridBagConstraints31.gridx = 0;
            gridBagConstraints31.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints31.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints31.gridy = 8;
            countryLabel = new JLabel();
            countryLabel.setText("Country");
            GridBagConstraints gridBagConstraints30 = new GridBagConstraints();
            gridBagConstraints30.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints30.gridy = 10;
            gridBagConstraints30.weightx = 1.0;
            gridBagConstraints30.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints30.gridx = 1;
            GridBagConstraints gridBagConstraints29 = new GridBagConstraints();
            gridBagConstraints29.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints29.gridy = 10;
            gridBagConstraints29.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints29.gridx = 0;
            emailLabel = new JLabel();
            emailLabel.setText("Email");
            GridBagConstraints gridBagConstraints28 = new GridBagConstraints();
            gridBagConstraints28.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints28.gridy = 9;
            gridBagConstraints28.weightx = 1.0;
            gridBagConstraints28.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints28.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints28.gridx = 1;
            GridBagConstraints gridBagConstraints27 = new GridBagConstraints();
            gridBagConstraints27.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints27.gridy = 9;
            gridBagConstraints27.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints27.gridx = 0;
            phoneNumberLabel = new JLabel();
            phoneNumberLabel.setText("Phone Number");
            GridBagConstraints gridBagConstraints26 = new GridBagConstraints();
            gridBagConstraints26.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints26.gridy = 7;
            gridBagConstraints26.weightx = 1.0;
            gridBagConstraints26.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints26.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints26.gridx = 1;
            GridBagConstraints gridBagConstraints25 = new GridBagConstraints();
            gridBagConstraints25.gridx = 0;
            gridBagConstraints25.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints25.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints25.gridy = 7;
            zipcodeLabel = new JLabel();
            zipcodeLabel.setText("Zipcode");
            GridBagConstraints gridBagConstraints24 = new GridBagConstraints();
            gridBagConstraints24.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints24.gridy = 6;
            gridBagConstraints24.weightx = 1.0;
            gridBagConstraints24.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints24.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints24.gridx = 1;
            GridBagConstraints gridBagConstraints23 = new GridBagConstraints();
            gridBagConstraints23.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints23.gridy = 6;
            gridBagConstraints23.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints23.gridx = 0;
            stateLabel = new JLabel();
            stateLabel.setText("State");
            GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
            gridBagConstraints22.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints22.gridy = 5;
            gridBagConstraints22.weightx = 1.0;
            gridBagConstraints22.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints22.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints22.gridx = 1;
            GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
            gridBagConstraints21.gridx = 0;
            gridBagConstraints21.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints21.gridy = 5;
            cityLabel = new JLabel();
            cityLabel.setText("City");
            GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
            gridBagConstraints20.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints20.gridy = 4;
            gridBagConstraints20.weightx = 1.0;
            gridBagConstraints20.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints20.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints20.gridx = 1;
            GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
            gridBagConstraints19.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints19.gridx = 0;
            gridBagConstraints19.gridy = 4;
            gridBagConstraints19.anchor = java.awt.GridBagConstraints.WEST;
            address2Label = new JLabel();
            address2Label.setText("Address2");
            GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
            gridBagConstraints18.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints18.gridy = 3;
            gridBagConstraints18.weightx = 1.0;
            gridBagConstraints18.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints18.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints18.gridx = 1;
            organizationLabel = new JLabel();
            organizationLabel.setText("Organization");

            GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
            gridBagConstraints17.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints17.gridy = 3;
            gridBagConstraints17.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints17.gridx = 0;
            addressLabel = new JLabel();
            addressLabel.setText("Address");
            GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
            gridBagConstraints16.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints16.gridy = 2;
            gridBagConstraints16.weightx = 1.0;
            gridBagConstraints16.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints16.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints16.gridx = 1;
            GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
            gridBagConstraints15.gridx = 0;
            gridBagConstraints15.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints15.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints15.gridy = 2;
            GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
            gridBagConstraints14.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints14.gridy = 1;
            gridBagConstraints14.weightx = 1.0;
            gridBagConstraints14.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints14.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints14.gridx = 1;
            GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
            gridBagConstraints13.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints13.gridy = 1;
            gridBagConstraints13.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints13.gridx = 0;
            lastNameLabel = new JLabel();
            lastNameLabel.setText("Last Name");
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints12.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints12.gridx = 1;
            gridBagConstraints12.gridy = 0;
            gridBagConstraints12.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints12.weightx = 1.0;
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints11.gridy = 0;
            gridBagConstraints11.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints11.gridx = 0;
            firstNameLabel = new JLabel();
            firstNameLabel.setText("First Name");
            infoPanel = new JPanel();
            infoPanel.setLayout(new GridBagLayout());

            infoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Personal Information",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, LookAndFeel.getPanelLabelColor()));
            infoPanel.add(firstNameLabel, gridBagConstraints11);
            infoPanel.add(getFirstName(), gridBagConstraints12);
            infoPanel.add(lastNameLabel, gridBagConstraints13);
            infoPanel.add(getLastName(), gridBagConstraints14);
            infoPanel.add(organizationLabel, gridBagConstraints15);
            infoPanel.add(getOrganization(), gridBagConstraints16);
            infoPanel.add(addressLabel, gridBagConstraints17);
            infoPanel.add(getAddress(), gridBagConstraints18);
            infoPanel.add(address2Label, gridBagConstraints19);
            infoPanel.add(getAddress2(), gridBagConstraints20);
            infoPanel.add(cityLabel, gridBagConstraints21);
            infoPanel.add(getCity(), gridBagConstraints22);
            infoPanel.add(stateLabel, gridBagConstraints23);
            infoPanel.add(getState(), gridBagConstraints24);
            infoPanel.add(zipcodeLabel, gridBagConstraints25);
            infoPanel.add(getZipcode(), gridBagConstraints26);
            infoPanel.add(phoneNumberLabel, gridBagConstraints27);
            infoPanel.add(getPhoneNumber(), gridBagConstraints28);
            infoPanel.add(emailLabel, gridBagConstraints29);
            infoPanel.add(getEmail(), gridBagConstraints30);
            infoPanel.add(countryLabel, gridBagConstraints31);
            infoPanel.add(getCountry(), gridBagConstraints32);
        }
        return infoPanel;
    }


    /**
     * This method initializes buttonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new JPanel();
            buttonPanel.add(getApplyButton(), null);
        }
        return buttonPanel;
    }


    /**
     * This method initializes service
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getService() {
        if (service == null) {
            service = new DorianServiceListComboBox();
        }
        return service;
    }


    /**
     * This method initializes username
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getUsername() {
        if (username == null) {
            username = new JTextField();
        }
        return username;
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
     * This method initializes verify
     * 
     * @return javax.swing.JPasswordField
     */
    private JPasswordField getVerify() {
        if (verify == null) {
            verify = new JPasswordField();
        }
        return verify;
    }


    /**
     * This method initializes firstName
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getFirstName() {
        if (firstName == null) {
            firstName = new JTextField();
        }
        return firstName;
    }


    /**
     * This method initializes lastName
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getLastName() {
        if (lastName == null) {
            lastName = new JTextField();
        }
        return lastName;
    }


    /**
     * This method initializes organization
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getOrganization() {
        if (organization == null) {
            organization = new JTextField();
        }
        return organization;
    }


    /**
     * This method initializes address
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getAddress() {
        if (address == null) {
            address = new JTextField();
        }
        return address;
    }


    /**
     * This method initializes address2
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getAddress2() {
        if (address2 == null) {
            address2 = new JTextField();
        }
        return address2;
    }


    /**
     * This method initializes city
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getCity() {
        if (city == null) {
            city = new JTextField();
        }
        return city;
    }


    /**
     * This method initializes state
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getState() {
        if (state == null) {
            state = new StateListComboBox();
        }
        return state;
    }


    /**
     * This method initializes zipcode
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getZipcode() {
        if (zipcode == null) {
            zipcode = new JTextField();
        }
        return zipcode;
    }


    /**
     * This method initializes phoneNumber
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getPhoneNumber() {
        if (phoneNumber == null) {
            phoneNumber = new JTextField();
        }
        return phoneNumber;
    }


    /**
     * This method initializes email
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getEmail() {
        if (email == null) {
            email = new JTextField();
        }
        return email;
    }


    /**
     * This method initializes applyButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getApplyButton() {
        if (applyButton == null) {
            applyButton = new JButton();
            applyButton.setText("Apply");
            applyButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    apply();

                }
            });
        }
        return applyButton;
    }


    /**
     * This method initializes country
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getCountry() {
        if (country == null) {
            country = new CountryListComboBox();
        }
        return country;
    }


    public void apply() {
        getApplyButton().setEnabled(false);
        getProgressPanel().showProgress("Registering account...");
        String pass = new String(this.getPassword().getPassword());
        String vpass = new String(this.getVerify().getPassword());
        if (!pass.equals(vpass)) {
            ErrorDialog.showError("Registration Error", "Password don't match!!!");
            getApplyButton().setEnabled(true);
            getProgressPanel().stopProgress("Error");
            return;
        }

        final Application a = new Application();
        a.setUserId(this.getUsername().getText());
        a.setPassword(pass);
        a.setFirstName(this.getFirstName().getText());
        a.setLastName(this.getLastName().getText());
        a.setOrganization(this.getOrganization().getText());
        a.setAddress(this.getAddress().getText());
        a.setAddress2(this.getAddress2().getText());
        a.setCity(this.getCity().getText());
        a.setState(((StateListComboBox) this.getState()).getSelectedState());
        a.setZipcode(this.getZipcode().getText());
        a.setCountry(((CountryListComboBox) this.getCountry()).getSelectedCountry());
        a.setPhoneNumber(this.getPhoneNumber().getText());
        a.setEmail(this.getEmail().getText());
        final DorianHandle handle = ((DorianServiceListComboBox) this.getService()).getSelectedService();

        Runner runner = new Runner() {
            public void execute() {
                try {
                    LocalUserClient client = handle.getLocalUserClient();
                    GridApplication.getContext().showMessage(client.register(a));
                    dispose();
                } catch (Exception e) {
                    ErrorDialog.showError(e);
                    getProgressPanel().stopProgress("Error");
                    getApplyButton().setEnabled(true);
                }
            }
        };
        try {
            GridApplication.getContext().executeInBackground(runner);
        } catch (Exception t) {
            FaultUtil.logFault(log, t);
            getProgressPanel().stopProgress("Error");
            getApplyButton().setEnabled(true);
        }
    }


    /**
     * This method initializes titlePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTitlePanel() {
        if (titlePanel == null) {
            titlePanel = new TitlePanel("Account Registration",
                "Register for an account with the local Dorian identity provider.");
        }
        return titlePanel;
    }


    /**
     * This method initializes progressPanel
     * 
     * @return javax.swing.JPanel
     */
    private ProgressPanel getProgressPanel() {
        if (progressPanel == null) {
            progressPanel = new ProgressPanel();
        }
        return progressPanel;
    }
}
