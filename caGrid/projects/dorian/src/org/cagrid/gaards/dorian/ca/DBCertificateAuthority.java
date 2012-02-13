package org.cagrid.gaards.dorian.ca;

import gov.nih.nci.cagrid.common.FaultHelper;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

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

    public static final String SIGNATURE_ALGORITHM = "SHA1WithRSAEncryption";

    public static final String CA_ALIAS = "dorianca";

    private CredentialsManager manager;


    public DBCertificateAuthority(Database db, CertificateAuthorityProperties properties) {
        super(properties);
        SecurityUtil.init();
        this.manager = new CredentialsManager(db);
    }


    public String getCACredentialsProvider() {
        return getProvider();
    }


    public String getUserCredentialsProvider() {
        return getProvider();
    }


    public String getProvider() {
        return "BC";
    }


    public String getSignatureAlgorithm() {
        return SIGNATURE_ALGORITHM;
    }


    public void deleteCACredentials() throws CertificateAuthorityFault {
        try {
            manager.deleteCredentials(CA_ALIAS);
        } catch (Exception e) {
            logError(e.getMessage(), e);
            CertificateAuthorityFault fault = new CertificateAuthorityFault();
            fault.setFaultString("An unexpected error occurred, could not delete the CA credentials.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CertificateAuthorityFault) helper.getFault();
            throw fault;
        }

    }


    public X509Certificate getCertificate() throws CertificateAuthorityFault {
        try {
            if (!hasCACredentials()) {
                CertificateAuthorityFault fault = new CertificateAuthorityFault();
                fault.setFaultString("The CA certificate does not exist.");
                throw fault;
            } else {
                return manager.getCertificate(CA_ALIAS);
            }
        } catch (CertificateAuthorityFault f) {
            throw f;
        } catch (Exception e) {
            logError(e.getMessage(), e);
            CertificateAuthorityFault fault = new CertificateAuthorityFault();
            fault.setFaultString("Unexpected Error, could not obtain the certificate.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CertificateAuthorityFault) helper.getFault();
            throw fault;
        }

    }


    public PrivateKey getPrivateKey(String password) throws CertificateAuthorityFault {
        try {
            if (!hasCACredentials()) {
                CertificateAuthorityFault fault = new CertificateAuthorityFault();
                fault.setFaultString("The CA private key does not exist.");
                throw fault;
            } else {
                return manager.getPrivateKey(CA_ALIAS, password);
            }
        } catch (CertificateAuthorityFault f) {
            throw f;
        } catch (Exception e) {
            logError(e.getMessage(), e);
            CertificateAuthorityFault fault = new CertificateAuthorityFault();
            fault.setFaultString("Unexpected Error, could not obtain the private key.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CertificateAuthorityFault) helper.getFault();
            throw fault;
        }
    }


    public boolean hasCACredentials() throws CertificateAuthorityFault {
        try {
            return this.manager.hasCredentials(CA_ALIAS);
        } catch (Exception e) {
            logError(e.getMessage(), e);
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
            manager.addCredentials(CA_ALIAS, password, cert, key);
        } catch (Exception e) {
            logError(e.getMessage(), e);
            CertificateAuthorityFault fault = new CertificateAuthorityFault();
            fault.setFaultString("An unexpected error occurred, could not add CA credentials.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CertificateAuthorityFault) helper.getFault();
            throw fault;
        }
    }

}