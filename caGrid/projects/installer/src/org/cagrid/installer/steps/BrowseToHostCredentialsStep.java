package org.cagrid.installer.steps;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.cagrid.installer.model.CaGridInstallerModel;
import org.cagrid.installer.util.InstallerUtils;
import org.pietschy.wizard.InvalidStateException;
import org.pietschy.wizard.PanelWizardStep;
import org.pietschy.wizard.WizardModel;


public class BrowseToHostCredentialsStep extends PanelWizardStep {
    private CaGridInstallerModel model = null; // @jve:decl-index=0:
    private JLabel certLabel = null;
    private JLabel keyLabel = null;
    private JTextField certTextField = null;
    private JTextField keyTextField = null;
    private JButton certBrowseButton = null;
    private JButton keyBrowseButton = null;


    /**
     * This method initializes
     */
    public BrowseToHostCredentialsStep() {
        super("", "");
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
        gridBagConstraints6.gridx = 2;
        gridBagConstraints6.insets = new Insets(10, 0, 0, 0);
        gridBagConstraints6.anchor = GridBagConstraints.EAST;
        gridBagConstraints6.gridy = 3;
        GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
        gridBagConstraints5.gridx = 2;
        gridBagConstraints5.anchor = GridBagConstraints.EAST;
        gridBagConstraints5.gridy = 1;
        GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
        gridBagConstraints4.fill = GridBagConstraints.BOTH;
        gridBagConstraints4.gridy = 3;
        gridBagConstraints4.weightx = 1.0;
        gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints4.gridwidth = 2;
        gridBagConstraints4.gridx = 0;
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.fill = GridBagConstraints.BOTH;
        gridBagConstraints3.gridy = 1;
        gridBagConstraints3.weightx = 1.0;
        gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints3.weighty = 0.0D;
        gridBagConstraints3.gridwidth = 2;
        gridBagConstraints3.gridx = 0;
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.fill = GridBagConstraints.BOTH;
        gridBagConstraints2.insets = new Insets(10, 2, 2, 2);
        gridBagConstraints2.gridy = 2;
        keyLabel = new JLabel();
        keyLabel.setText("Key");
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.fill = GridBagConstraints.BOTH;
        gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints1.gridy = 0;
        certLabel = new JLabel();
        certLabel.setText("Certificate");
        this.setLayout(new GridBagLayout());
        this.setSize(new Dimension(442, 204));

        this.add(certLabel, gridBagConstraints1);
        this.add(keyLabel, gridBagConstraints2);
        this.add(getCertTextField(), gridBagConstraints3);
        this.add(getKeyTextField(), gridBagConstraints4);
        this.add(getCertBrowseButton(), gridBagConstraints5);
        this.add(getKeyBrowseButton(), gridBagConstraints6);
    }


    public void init(WizardModel m) {
        model = (CaGridInstallerModel) m;
        super.init(m);
        setName(model.getMessage("browse.host.creds.title"));
        setSummary(model.getMessage("browse.host.creds.desc"));
        initialize();

    }


    @Override
    public void applyState() throws InvalidStateException {
        try {
            InstallerUtils.copyFile(getCertTextField().getText(), model.getProperty(Constants.SERVICE_CERT_PATH));
            InstallerUtils.copyFile(getKeyTextField().getText(), model.getProperty(Constants.SERVICE_KEY_PATH));
        } catch (IOException e) {
            e.printStackTrace();
            throw new InvalidStateException(model.getMessage("browse.host.error.copy"), e);
        }
        super.applyState();
    }


    /**
     * This method initializes certTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getCertTextField() {
        if (certTextField == null) {
            certTextField = new JTextField();
            certTextField.setEditable(false);
        }
        return certTextField;
    }


    /**
     * This method initializes keyTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getKeyTextField() {
        if (keyTextField == null) {
            keyTextField = new JTextField();
            keyTextField.setEditable(false);
        }
        return keyTextField;
    }


    private void checkComplete() {
        if (certTextField.getText() != null && certTextField.getText().length() > 0 && keyTextField.getText() != null
            && keyTextField.getText().length() > 0) {
            setComplete(true);
        }
    }


    /**
     * This method initializes certBrowseButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getCertBrowseButton() {
        if (certBrowseButton == null) {
            certBrowseButton = new JButton();
            certBrowseButton.setText(model.getMessage("browse"));
            certBrowseButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setDialogTitle(model.getMessage("browse.host.creds.cert"));
                    int returnVal = chooser.showDialog(BrowseToHostCredentialsStep.this, model.getMessage("select"));
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        certTextField.setText(chooser.getSelectedFile().getAbsolutePath());
                        checkComplete();
                    }
                }
            });
        }
        return certBrowseButton;
    }


    /**
     * This method initializes keyBrowseButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getKeyBrowseButton() {
        if (keyBrowseButton == null) {
            keyBrowseButton = new JButton();
            keyBrowseButton.setText(model.getMessage("browse"));
            keyBrowseButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setDialogTitle(model.getMessage("browse.host.creds.key"));
                    int returnVal = chooser.showDialog(BrowseToHostCredentialsStep.this, "Select");
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        keyTextField.setText(chooser.getSelectedFile().getAbsolutePath());
                        checkComplete();
                    }
                }
            });
        }
        return keyBrowseButton;
    }

} // @jve:decl-index=0:visual-constraint="14,29"
