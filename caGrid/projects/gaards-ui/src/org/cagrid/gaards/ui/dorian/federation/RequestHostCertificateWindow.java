package org.cagrid.gaards.ui.dorian.federation;

import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.Utils;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.net.InetAddress;
import java.security.KeyPair;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.cagrid.gaards.dorian.client.GridUserClient;
import org.cagrid.gaards.dorian.federation.HostCertificateRecord;
import org.cagrid.gaards.pki.KeyUtil;
import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.gaards.ui.dorian.DorianLookAndFeel;
import org.cagrid.gaards.ui.dorian.SessionPanel;
import org.cagrid.grape.ApplicationComponent;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.LookAndFeel;
import org.cagrid.grape.utils.ErrorDialog;


public class RequestHostCertificateWindow extends ApplicationComponent {

    private static final long serialVersionUID = 1L;

    private JPanel jContentPane = null;

    private JPanel mainPanel = null;

    private SessionPanel sessionPanel = null;

    private JPanel requestPanel = null;

    private JLabel jLabel = null;

    private JTextField host = null;

    private JLabel jLabel1 = null;

    private JComboBox strength = null;

    private ProgressPanel progressPanel = null;

    private JPanel buttonPanel = null;

    private JButton request = null;

    private JPanel locationPanel = null;

    private JLabel jLabel2 = null;

    private JTextField directory = null;

    private JButton browse = null;

	private JPanel titlePanel = null;


    /**
     * This is the default constructor
     */
    public RequestHostCertificateWindow() {
        super();
        initialize();
    }


    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(400, 325);
        this.setContentPane(getJContentPane());
        this.setTitle("Request Host Certificate");
        this.setFrameIcon(DorianLookAndFeel.getHostIcon());
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
     * This method initializes mainPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridx = 0;
            gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints11.weightx = 1.0D;
            gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints11.gridy = 0;
            GridBagConstraints gridBagConstraints51 = new GridBagConstraints();
            gridBagConstraints51.gridx = 0;
            gridBagConstraints51.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints51.gridy = 3;
            GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
            gridBagConstraints41.gridx = 0;
            gridBagConstraints41.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints41.weightx = 1.0D;
            gridBagConstraints41.gridy = 4;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = GridBagConstraints.BOTH;
            gridBagConstraints3.gridy = 2;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.weightx = 1.0D;
            gridBagConstraints3.weighty = 1.0D;
            gridBagConstraints3.gridx = 0;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.weightx = 1.0D;
            gridBagConstraints.weighty = 0.0D;
            gridBagConstraints.anchor = GridBagConstraints.NORTH;
            gridBagConstraints.gridy = 1;
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.add(getSessionPanel(), gridBagConstraints);
            mainPanel.add(getRequestPanel(), gridBagConstraints3);
            mainPanel.add(getProgressPanel(), gridBagConstraints41);
            mainPanel.add(getButtonPanel(), gridBagConstraints51);
            mainPanel.add(getTitlePanel(), gridBagConstraints11);
        }
        return mainPanel;
    }


    /**
     * This method initializes sessionPanel
     * 
     * @return javax.swing.JPanel
     */
    private SessionPanel getSessionPanel() {
        if (sessionPanel == null) {
            sessionPanel = new SessionPanel(false);
        }
        return sessionPanel;
    }


    /**
     * This method initializes requestPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getRequestPanel() {
        if (requestPanel == null) {
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.gridx = 0;
            gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints7.gridwidth = 2;
            gridBagConstraints7.weightx = 1.0D;
            gridBagConstraints7.fill = GridBagConstraints.BOTH;
            gridBagConstraints7.weighty = 1.0D;
            gridBagConstraints7.gridy = 2;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.gridy = 1;
            gridBagConstraints5.weightx = 1.0;
            gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints5.gridx = 1;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.anchor = GridBagConstraints.WEST;
            gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints4.gridy = 1;
            jLabel1 = new JLabel();
            jLabel1.setText("Strength");
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.gridx = 1;
            gridBagConstraints2.gridy = 0;
            gridBagConstraints2.anchor = GridBagConstraints.WEST;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.weightx = 1.0;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.anchor = GridBagConstraints.WEST;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.gridy = 0;
            jLabel = new JLabel();
            jLabel.setText("Host");
            requestPanel = new JPanel();
            requestPanel.setLayout(new GridBagLayout());
            requestPanel.setBorder(BorderFactory.createTitledBorder(null, "Host Certificate",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, LookAndFeel
                    .getPanelLabelColor()));
            requestPanel.add(jLabel, gridBagConstraints1);
            requestPanel.add(getHost(), gridBagConstraints2);
            requestPanel.add(jLabel1, gridBagConstraints4);
            requestPanel.add(getStrength(), gridBagConstraints5);
            requestPanel.add(getLocationPanel(), gridBagConstraints7);
        }
        return requestPanel;
    }


    /**
     * This method initializes host
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getHost() {
        if (host == null) {
            host = new JTextField();
            try {
                host.setText(InetAddress.getLocalHost().getHostName());
            } catch (Exception e) {

            }
        }
        return host;
    }


    /**
     * This method initializes strength
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getStrength() {
        if (strength == null) {
            strength = new JComboBox();
            strength.addItem("512");
            strength.addItem("1024");
            strength.addItem("2048");
            strength.setSelectedItem("1024");
        }
        return strength;
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


    /**
     * This method initializes buttonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout());
            buttonPanel.add(getRequest(), null);
        }
        return buttonPanel;
    }


    /**
     * This method initializes request
     * 
     * @return javax.swing.JButton
     */
    private JButton getRequest() {
        if (request == null) {
            request = new JButton();
            request.setText("Request Certificate");
            request.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {
                        public void execute() {
                            requestHostCertificate();
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
        return request;
    }


    private void requestHostCertificate() {
        this.getRequest().setEnabled(false);
        try {
            if (Utils.clean(getHost().getText()) == null) {
                throw new Exception("You must specify a host.");
            }
            int stren = Integer.parseInt((String) getStrength().getSelectedItem());
            getProgressPanel().showProgress("Generating key pair.....");
            KeyPair pair = KeyUtil.generateRSAKeyPair(stren);
            getProgressPanel().showProgress("Submitting certificate request to Dorian....");
            GridUserClient client = getSessionPanel().getUserClientWithCredentials();
            HostCertificateRecord record = client.requestHostCertificate(getHost().getText(), pair.getPublic());
            getProgressPanel().stopProgress("Successfully sent certificate request to Dorian.");
            GridApplication.getContext().addApplicationComponent(
                new RequestHostCertificateResponseWindow(record, pair.getPrivate(), new File(directory.getText())),
                500, 300);
            dispose();

        } catch (Exception e) {
        	getProgressPanel().stopProgress("Error");
            ErrorDialog.showError(e);
            this.getRequest().setEnabled(true);
        }

    }


    /**
     * This method initializes locationPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getLocationPanel() {
        if (locationPanel == null) {
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.gridx = 1;
            gridBagConstraints10.anchor = GridBagConstraints.WEST;
            gridBagConstraints10.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints10.gridy = 1;
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints9.gridx = 0;
            gridBagConstraints9.gridy = 1;
            gridBagConstraints9.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints9.anchor = GridBagConstraints.WEST;
            gridBagConstraints9.weightx = 1.0;
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.gridx = 0;
            gridBagConstraints8.insets = new Insets(5, 5, 5, 5);
            gridBagConstraints8.gridwidth = 2;
            gridBagConstraints8.gridy = 0;
            jLabel2 = new JLabel();
            jLabel2.setText("Specify Directory to Write Credentials");
            locationPanel = new JPanel();
            locationPanel.setLayout(new GridBagLayout());
            locationPanel.add(jLabel2, gridBagConstraints8);
            locationPanel.add(getDirectory(), gridBagConstraints9);
            locationPanel.add(getBrowse(), gridBagConstraints10);
        }
        return locationPanel;
    }


    /**
     * This method initializes directory
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getDirectory() {
        if (directory == null) {
            directory = new JTextField();
            directory.setEditable(false);
            directory.setText(Utils.getCaGridUserHome().getAbsolutePath() + File.separator + "certificates");
        }
        return directory;
    }


    /**
     * This method initializes browse
     * 
     * @return javax.swing.JButton
     */
    private JButton getBrowse() {
        if (browse == null) {
            browse = new JButton();
            browse.setText("Browse");
            final RequestHostCertificateWindow window = this;
            browse.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    JFileChooser fc = new JFileChooser();
                    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    int returnVal = fc.showSaveDialog(window);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        try {
                            directory.setText(fc.getSelectedFile().getAbsolutePath());
                        } catch (Exception ex) {
                            ErrorDialog.showError(ex);
                        }
                    }
                }
            });
        }
        return browse;
    }


	/**
	 * This method initializes titlePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getTitlePanel() {
		if (titlePanel == null) {
			titlePanel = new TitlePanel("Request Host Certificate","Request a host certificate for securing grid services.");
		}
		return titlePanel;
	}
}
