package org.cagrid.gaards.ui.common;

import gov.nih.nci.cagrid.common.security.ProxyUtil;

import java.util.List;

import javax.swing.JComboBox;

import org.cagrid.gaards.credentials.CredentialEntryFactory;
import org.cagrid.gaards.credentials.X509CredentialEntry;
import org.globus.gsi.GlobusCredential;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class CredentialComboBox extends JComboBox {

    private static final long serialVersionUID = 1L;

    private static final String NO_CREDENTIAL = "Anonymous";

    private boolean allowAnonymous = false;


    public CredentialComboBox() {
        this(false);
    }


    public CredentialComboBox(boolean none) {
        allowAnonymous = none;
        handleDefaultCredential(true);
        addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                try {
                    X509CredentialEntry cred = getSelectedCredential();
                    if (cred != null) {
                        setToolTipText(cred.getDescription());
                    } else {
                        setToolTipText("Anonymous");
                    }
                    // handleDefaultCredential(false);
                } catch (Exception ex) {
                    setToolTipText("Anonymous");
                }
            }
        });

    }


    public CredentialComboBox(X509CredentialEntry cred) {
        this(false);
        this.setSelectedItem(cred);
    }


    public CredentialComboBox(X509CredentialEntry cred, boolean none) {
        this(none);
        this.setSelectedItem(cred);
    }


    public void populateList() {
        this.removeAllItems();
        List<X509CredentialEntry> creds = CredentialManager.getInstance().getCredentials();
        if (allowAnonymous) {
            addItem(NO_CREDENTIAL);
        }
        for (int i = 0; i < creds.size(); i++) {
            addItem(creds.get(i));
        }

    }


    public void handleDefaultCredential(boolean setSelected) {
        X509CredentialEntry defaultCredential = null;
        try {
            GlobusCredential cred = ProxyUtil.getDefaultProxy();
            defaultCredential = CredentialEntryFactory.getEntry(cred);
            defaultCredential = CredentialManager.getInstance().setDefaultCredential(defaultCredential);

        } catch (Exception ex) {

        }
        populateList();
        if ((setSelected) && (defaultCredential != null)) {
            setSelectedItem(defaultCredential);
        }
    }


    public X509CredentialEntry getSelectedCredential() throws Exception {
        Object obj = getSelectedItem();
        if ((obj == null) || (!(obj instanceof X509CredentialEntry))) {
            return null;
        } else {
            X509CredentialEntry credential = (X509CredentialEntry) obj;
            return credential;
        }
    }
}
