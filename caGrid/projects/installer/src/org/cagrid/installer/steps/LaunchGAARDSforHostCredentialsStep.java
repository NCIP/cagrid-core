package org.cagrid.installer.steps;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import org.cagrid.installer.model.CaGridInstallerModel;
import org.cagrid.installer.tasks.AntExecutionTask;
import org.pietschy.wizard.PanelWizardStep;
import org.pietschy.wizard.WizardModel;


public class LaunchGAARDSforHostCredentialsStep extends PanelWizardStep {
    private CaGridInstallerModel model = null; // @jve:decl-index=0:
    private JTextArea descriptionTextArea = null;
    private JButton relaunchButton = null;


    /**
     * This method initializes
     */
    public LaunchGAARDSforHostCredentialsStep() {
        super("","");
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 1;
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 0;
        this.setLayout(new GridBagLayout());
        this.setSize(new Dimension(365, 233));

        this.add(getDescriptionTextArea(), gridBagConstraints);
        this.add(getRelaunchButton(), gridBagConstraints1);
    }


    private void launchGAARDS() {
        final AntExecutionTask task = new AntExecutionTask("", "", model.getProperty(Constants.CAGRID_HOME)
            + File.separator + "build.xml", "security");
        Thread th = new Thread(new Runnable() {
            public void run() {
                try {
                    getRelaunchButton().setEnabled(false);
                    task.execute(model);
                    if (new File(model.getProperty(Constants.SERVICE_CERT_PATH)).exists()
                        && new File(model.getProperty(Constants.SERVICE_KEY_PATH)).exists()) {
                        setComplete(true);
                    } else {
                        getRelaunchButton().setEnabled(true);
                        JOptionPane.showMessageDialog(LaunchGAARDSforHostCredentialsStep.this, "You have not yet saved the certificates to the correct location.\n  Please either launch gaards again to obtain the host credentials or exit the installer.");
                    }
     
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        th.start();
    }


    @Override
    public void prepare() {
        super.prepare();
        StringBuffer sb = new StringBuffer();
        sb
            .append("Please use GAARDS to login and obtain host credentials.  If you do not have an account yet with the target grid you may need to register first.  For help on using gaards to obtain host credentials please see http://cagrid.org \n");
        sb.append("\nDo not forget that the hostname is set to " + model.getProperty(Constants.SERVICE_HOSTNAME)
            + " so you need to register for this hostname host certificates with GAARDS.\n");
        sb.append("\nThe certificate and key must be written to the following locations:\n");
        sb.append("\t" + model.getProperty(Constants.SERVICE_CERT_PATH) + "\n");
        sb.append("\t" + model.getProperty(Constants.SERVICE_KEY_PATH) + "\n");
        getDescriptionTextArea().setText(sb.toString());

        launchGAARDS();

    }


    public void init(WizardModel m) {
        model = (CaGridInstallerModel) m;
        super.init(m);
        setName(model.getMessage("gaards.host.creds.title"));
        setSummary(model.getMessage("gaards.host.creds.desc"));
        initialize();
    }


    /**
     * This method initializes descriptionTextArea
     * 
     * @return javax.swing.JTextArea
     */
    private JTextArea getDescriptionTextArea() {
        if (descriptionTextArea == null) {
            descriptionTextArea = new JTextArea();
            descriptionTextArea.setWrapStyleWord(true);
            descriptionTextArea.setLineWrap(true);
        }
        return descriptionTextArea;
    }


    /**
     * This method initializes relaunchButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getRelaunchButton() {
        if (relaunchButton == null) {
            relaunchButton = new JButton();
            relaunchButton.setText(model.getMessage("gaards.host.creds.relaunch"));
            relaunchButton.setEnabled(false);
            relaunchButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    launchGAARDS();
                }
            });
        }
        return relaunchButton;
    }

} // @jve:decl-index=0:visual-constraint="14,29"
