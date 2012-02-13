package org.cagrid.gaards.dorian.ca;

import gov.nih.nci.cagrid.common.FaultHelper;

import java.io.ByteArrayInputStream;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.cagrid.gaards.dorian.common.LoggingObject;
import org.cagrid.gaards.dorian.stubs.types.DorianInternalFault;
import org.cagrid.gaards.pki.CertUtil;
import org.cagrid.gaards.pki.KeyUtil;
import org.cagrid.tools.database.Database;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class CredentialsManager extends LoggingObject {

    public static String CREDENTIALS_TABLE = "certificate_authority";

    private Database db;

    private boolean dbBuilt = false;


    public CredentialsManager(Database db) {
        this.db = db;
    }


    public boolean hasCredentials(String alias) throws DorianInternalFault {
        this.buildDatabase();
        Connection c = null;
        boolean exists = false;
        try {
            c = db.getConnection();
            PreparedStatement s = c.prepareStatement("select count(*) from " + CREDENTIALS_TABLE + " where ALIAS= ?");
            s.setString(1, alias);
            ResultSet rs = s.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                if (count > 0) {
                    exists = true;
                }
            }
            rs.close();
            s.close();
        } catch (Exception e) {
            logError(e.getMessage(), e);
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("Unexpected Database Error, Error determining if the user " + alias
                + " has credentials.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        } finally {
            db.releaseConnection(c);
        }
        return exists;
    }


    public void deleteCredentials(String alias) throws DorianInternalFault {
        this.buildDatabase();
        Connection c = null;
        try {
            c = db.getConnection();
            PreparedStatement s = c.prepareStatement("delete from " + CREDENTIALS_TABLE + " where ALIAS= ? ");
            s.setString(1, alias);
            s.execute();
            s.close();
        } catch (Exception e) {
            logError(e.getMessage(), e);
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("Unexpected Database Error, Error removing the credentials for the user " + alias
                + "!!!");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        } finally {
            db.releaseConnection(c);
        }
    }


    public void addCredentials(String alias, String password, X509Certificate cert, PrivateKey key)
        throws DorianInternalFault {
        this.buildDatabase();
        Connection c = null;
        try {
            if (!hasCredentials(alias)) {
                c = db.getConnection();
                long serial = cert.getSerialNumber().longValue();
                String keyStr = KeyUtil.writePrivateKey(key, password);
                String certStr = CertUtil.writeCertificate(cert);
                PreparedStatement s = c.prepareStatement("INSERT INTO " + CREDENTIALS_TABLE
                    + " SET ALIAS= ?, SERIAL_NUMBER= ?, CERTIFICATE= ?, PRIVATE_KEY= ?");
                s.setString(1, alias);
                s.setLong(2, serial);
                s.setString(3, certStr);
                s.setString(4, keyStr);
                s.execute();
                s.close();
            }
        } catch (Exception e) {
            logError(e.getMessage(), e);
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("Unexpected Error, could not add credentials to the credentials database.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        } finally {
            db.releaseConnection(c);
        }
    }


    public void addCertificate(String alias, X509Certificate cert) throws DorianInternalFault {
        this.buildDatabase();
        Connection c = null;
        try {
            if (!hasCredentials(alias)) {
                c = db.getConnection();
                long serial = cert.getSerialNumber().longValue();
                String keyStr = "";
                String certStr = CertUtil.writeCertificate(cert);
                PreparedStatement s = c.prepareStatement("INSERT INTO " + CREDENTIALS_TABLE
                    + " SET ALIAS= ?, SERIAL_NUMBER= ?, CERTIFICATE= ?, PRIVATE_KEY= ?");
                s.setString(1, alias);
                s.setLong(2, serial);
                s.setString(3, certStr);
                s.setString(4, keyStr);
                s.execute();
                s.close();
            }
        } catch (Exception e) {
            logError(e.getMessage(), e);
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("Unexpected Error, could not add certificate to the credentials database.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        } finally {
            db.releaseConnection(c);
        }
    }


    public PrivateKey getPrivateKey(String alias, String password) throws DorianInternalFault, InvalidPasswordFault {
        this.buildDatabase();
        Connection c = null;
        PrivateKey key = null;
        String keyStr = null;
        try {
            c = db.getConnection();
            PreparedStatement s = c
                .prepareStatement("select PRIVATE_KEY from " + CREDENTIALS_TABLE + " where ALIAS= ?");
            s.setString(1, alias);
            ResultSet rs = s.executeQuery();
            if (rs.next()) {
                keyStr = rs.getString("PRIVATE_KEY");
            }
            rs.close();
            s.close();
        } catch (Exception e) {
            logError(e.getMessage(), e);
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("Unexpected Database Error, Error obtaining the private key for the user " + alias
                + ".");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        } finally {
            db.releaseConnection(c);
        }
        if (keyStr == null || keyStr.trim().equals("")) {
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("No PrivateKey exists for the user " + alias + ".");
            throw fault;
        }
        try {
            key = KeyUtil.loadPrivateKey(new ByteArrayInputStream(keyStr.getBytes()), password);
        } catch (Exception e) {
            InvalidPasswordFault fault = new InvalidPasswordFault();
            fault.setFaultString("Invalid Password Specified.");
            throw fault;
        }
        return key;
    }


    public X509Certificate getCertificate(String alias) throws DorianInternalFault {
        this.buildDatabase();
        Connection c = null;
        X509Certificate cert = null;
        try {
            c = db.getConnection();
            PreparedStatement s = c
                .prepareStatement("select CERTIFICATE from " + CREDENTIALS_TABLE + " where ALIAS= ?");
            s.setString(1, alias);
            ResultSet rs = s.executeQuery();
            if (rs.next()) {
                String certStr = rs.getString("CERTIFICATE");
                cert = CertUtil.loadCertificate(certStr);
            }
            rs.close();
            s.close();
        } catch (Exception e) {
            logError(e.getMessage(), e);
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("Unexpected Database Error, Error obtaining the certificate for the user " + alias
                + ".");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        } finally {
            db.releaseConnection(c);
        }
        if (cert == null) {
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("No Certificate exists for the user " + alias + ".");
            throw fault;
        }
        return cert;
    }


    public long getCertificateSerialNumber(String alias) throws DorianInternalFault {
        this.buildDatabase();
        Connection c = null;
        long sn = -1;
        try {
            c = db.getConnection();
            PreparedStatement s = c.prepareStatement("select SERIAL_NUMBER from " + CREDENTIALS_TABLE
                + " where ALIAS= ?");
            s.setString(1, alias);
            ResultSet rs = s.executeQuery();
            if (rs.next()) {
                sn = rs.getLong("SERIAL_NUMBER");
            }
            rs.close();
            s.close();
        } catch (Exception e) {
            logError(e.getMessage(), e);
            DorianInternalFault fault = new DorianInternalFault();
            fault
                .setFaultString("Unexpected Database Error, Error obtaining the certificate serial number for the user "
                    + alias + ".");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        } finally {
            db.releaseConnection(c);
        }
        if (sn == -1) {
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("No Certificate exists for the user " + alias + ".");
            throw fault;
        }
        return sn;
    }


    private synchronized void buildDatabase() throws DorianInternalFault {
        try {
            if (!dbBuilt) {
                if (!this.db.tableExists(CREDENTIALS_TABLE)) {
                    String users = "CREATE TABLE " + CREDENTIALS_TABLE + " ("
                        + "ALIAS VARCHAR(255) NOT NULL PRIMARY KEY," + " SERIAL_NUMBER BIGINT NOT NULL,"
                        + "CERTIFICATE TEXT NOT NULL," + "PRIVATE_KEY TEXT NOT NULL,"
                        + "INDEX document_index (ALIAS));";
                    db.update(users);
                }
                this.dbBuilt = true;
            }
        } catch (Exception e) {
            logError(e.getMessage(), e);
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("An unexpected database error occurred.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        }
    }


    public void clearDatabase() throws DorianInternalFault {
        buildDatabase();
        try {
            db.update("delete from " + CREDENTIALS_TABLE);
        } catch (Exception e) {
            logError(e.getMessage(), e);
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("An unexpected database error occurred.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        }
    }

}