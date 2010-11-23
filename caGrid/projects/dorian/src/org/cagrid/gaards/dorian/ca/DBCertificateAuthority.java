package org.cagrid.gaards.dorian.ca;

import gov.nih.nci.cagrid.common.FaultHelper;
import gov.nih.nci.cagrid.common.security.SecurityConstants;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gaards.dorian.stubs.types.DorianInternalFault;
import org.cagrid.gaards.pki.SecurityUtil;
import org.cagrid.tools.database.Database;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class DBCertificateAuthority extends CertificateAuthority {
    public static final String CA_ALIAS = "dorianca";
    
    private static Log LOG = LogFactory.getLog(DBCertificateAuthority.class);

    private CredentialsManager manager = null;
    private Database database = null;

    public DBCertificateAuthority(Database db, CertificateAuthorityProperties properties) 
        throws CertificateAuthorityFault {
        super(properties);
        SecurityUtil.init();
        this.database = db;
    }
    
    
    protected CredentialsManager getCredentialsManager() throws CertificateAuthorityFault {
        if (manager == null) {
            try {
                manager = new CredentialsManager(database);
            } catch (DorianInternalFault e) {
                LOG.error(e.getMessage(), e);
                CertificateAuthorityFault fault = new CertificateAuthorityFault();
                fault.setFaultString("Could not create the credentials manager instance.");
                FaultHelper helper = new FaultHelper(fault);
                helper.addFaultCause(e);
                fault = (CertificateAuthorityFault) helper.getFault();
                throw fault;
            }
        }
        return manager;
    }


    public String getCACredentialsProvider() {
        return getProvider();
    }


    public String getUserCredentialsProvider() {
        return getProvider();
    }


    // Gets the SecurityConstants.CRYPTO_PROVIDER default
    public String getProvider() {
        return SecurityConstants.CRYPTO_PROVIDER;
    }


    public void deleteCACredentials() throws CertificateAuthorityFault {
        try {
            getCredentialsManager().deleteCredentials(CA_ALIAS);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            CertificateAuthorityFault fault = new CertificateAuthorityFault();
            fault.setFaultString("An unexpected error occurred, could not delete the CA credentials.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CertificateAuthorityFault) helper.getFault();
            throw fault;
        }
    }
    

    public boolean hasCACredentials() throws CertificateAuthorityFault {
        try {
            return getCredentialsManager().hasCredentials(CA_ALIAS);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            CertificateAuthorityFault fault = new CertificateAuthorityFault();
            fault.setFaultString("An unexpected error occurred, could not determine if credentials exist.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CertificateAuthorityFault) helper.getFault();
            throw fault;
        }
    }


    public void setCACredentials(X509Certificate cert, PrivateKey key, String password)
        throws CertificateAuthorityFault {
        try {
            if (hasCACredentials()) {
                CertificateAuthorityFault fault = new CertificateAuthorityFault();
                fault.setFaultString("Credentials already exist for the CA.");
                throw fault;
            }
            getCredentialsManager().addCredentials(CA_ALIAS, password, cert, key);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            CertificateAuthorityFault fault = new CertificateAuthorityFault();
            fault.setFaultString("An unexpected error occurred, could not add CA credentials.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CertificateAuthorityFault) helper.getFault();
            throw fault;
        }
    }


    protected X509Certificate internalGetCertificate() throws CertificateAuthorityFault {
        try {
            if (!hasCACredentials()) {
                CertificateAuthorityFault fault = new CertificateAuthorityFault();
                fault.setFaultString("The CA certificate does not exist.");
                throw fault;
            } else {
                return getCredentialsManager().getCertificate(CA_ALIAS);
            }
        } catch (CertificateAuthorityFault f) {
            throw f;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            CertificateAuthorityFault fault = new CertificateAuthorityFault();
            fault.setFaultString("Unexpected Error, could not obtain the certificate.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CertificateAuthorityFault) helper.getFault();
            throw fault;
        }
    }


    protected PrivateKey internalGetPrivateKey(String password) throws CertificateAuthorityFault, NoCACredentialsFault {
        try {
            if (!hasCACredentials()) {
                CertificateAuthorityFault fault = new CertificateAuthorityFault();
                fault.setFaultString("The CA private key does not exist.");
                throw fault;
            } else {
                return getCredentialsManager().getPrivateKey(CA_ALIAS, password);
            }
        } catch (CertificateAuthorityFault f) {
            throw f;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            CertificateAuthorityFault fault = new CertificateAuthorityFault();
            fault.setFaultString("Unexpected Error, could not obtain the private key.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CertificateAuthorityFault) helper.getFault();
            throw fault;
        }
    }
}