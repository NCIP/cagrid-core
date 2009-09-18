package org.cagrid.gaards.ui.gts;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.cagrid.gaards.ui.common.CredentialComboBox;
import org.globus.gsi.GlobusCredential;


public class SessionPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private JLabel jLabel = null;

    private GTSServiceListComboBox service = null;

    private JLabel jLabel1 = null;

    private CredentialComboBox cred = null;

    private ServiceSelectionListener listener;


    /**
     * This is the default constructor
     */
    public SessionPanel() {
        this(null);
    }


    public SessionPanel(ServiceSelectionListener listener) {
        super();
        this.listener = listener;
        initialize();
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints3.gridy = 1;
        gridBagConstraints3.weightx = 1.0;
        gridBagConstraints3.anchor = GridBagConstraints.WEST;
        gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints3.gridx = 1;
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints2.anchor = GridBagConstraints.WEST;
        gridBagConstraints2.gridy = 1;
        jLabel1 = new JLabel();
        jLabel1.setText("Credential");
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.anchor = GridBagConstraints.WEST;
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        jLabel = new JLabel();
        jLabel.setText("Service");
        this.setSize(300, 200);
        this.setLayout(new GridBagLayout());
        this.add(jLabel, gridBagConstraints1);
        this.add(getService(), gridBagConstraints);
        this.add(jLabel1, gridBagConstraints2);
        this.add(getCred(), gridBagConstraints3);
    }


    /**
     * This method initializes service
     * 
     * @return javax.swing.JComboBox
     */
    private GTSServiceListComboBox getService() {
        if (service == null) {
            service = new GTSServiceListComboBox(this.listener);
        }
        return service;
    }


    /**
     * This method initializes cred
     * 
     * @return javax.swing.JComboBox
     */
    private CredentialComboBox getCred() {
        if (cred == null) {
            cred = new CredentialComboBox();
        }
        return cred;
    }


    public GTSSession getSession() throws Exception {
        if (getService().getSelectedService() != null) {
            return new GTSSession(getService().getSelectedService(), getCred().getSelectedCredential());
        } else {
            return null;
        }
    }


    public String getServiceURI() {
        if (getService().getSelectedService() != null) {
            return getService().getSelectedService().getServiceURL();
        } else {
            return null;
        }
    }


    public GlobusCredential getCredential() throws Exception {
        return getCred().getSelectedCredential();
    }

}
