package org.cagrid.gaards.dorian.federation;

import gov.nih.nci.cagrid.common.FaultHelper;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gaards.dorian.stubs.types.DorianInternalFault;
import org.cagrid.gaards.pki.CertUtil;
import org.cagrid.tools.database.Database;


public class CertificateBlacklistManager {

    public static final String CERTIFICATE_RENEWED = "CERTIFICATE RENEWED";
    public static final String ACCOUNT_DELETED = "ACCOUNT DELETED";
    public static final String COMPROMISED = "COMPROMISED";
    public static final String TABLE = "certificate_blacklist";
    public static final String SERIAL = "SERIAL_NUMBER";
    public static final String SUBJECT = "SUBJECT";
    public static final String REASON = "REASON";
    public static final String CERTIFICATE = "CERTIFICATE";

    private boolean dbBuilt = false;
    private Database db;
    private Log log;


    public CertificateBlacklistManager(Database db) {
        this.db = db;
        log = LogFactory.getLog(this.getClass().getName());
    }


    public synchronized void addCertificateToBlackList(org.cagrid.gaards.dorian.X509Certificate cert, String reason)
        throws DorianInternalFault {
        try {
            addCertificateToBlackList(CertUtil.loadCertificate(cert.getCertificateAsString()), reason);
        } catch (GeneralSecurityException e) {
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("Unexpected Error");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        } catch (IOException e) {
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("Unexpected Error");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        }

    }


    public synchronized void addCertificateToBlackList(X509Certificate cert, String reason) throws DorianInternalFault {
        buildDatabase();
        if (!memberOfBlackList(cert.getSerialNumber().longValue())) {
            Connection c = null;
            try {
                c = db.getConnection();
                PreparedStatement s = c.prepareStatement("INSERT INTO " + TABLE + " SET " + SERIAL + "= ?," + SUBJECT
                    + "= ?," + REASON + "= ?," + CERTIFICATE + "= ?");
                s.setLong(1, cert.getSerialNumber().longValue());
                s.setString(2, cert.getSubjectDN().getName());
                s.setString(3, reason);
                s.setString(4, CertUtil.writeCertificate(cert));
                s.executeUpdate();
                s.close();
            } catch (Exception e) {
                DorianInternalFault fault = new DorianInternalFault();
                fault.setFaultString("Unexpected Error");
                FaultHelper helper = new FaultHelper(fault);
                helper.addFaultCause(e);
                fault = (DorianInternalFault) helper.getFault();
                throw fault;
            } finally {
                db.releaseConnection(c);
            }
        }
    }


    public void removeCertificateFromBlackList(long serialNumber) throws DorianInternalFault {
        buildDatabase();
        Connection c = null;
        try {
            c = db.getConnection();
            PreparedStatement s = c.prepareStatement("delete from " + TABLE + " where " + SERIAL + "= ?");
            s.setLong(1, serialNumber);
            s.executeUpdate();
            s.close();
        } catch (Exception e) {
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("Unexpected Error");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        } finally {
            db.releaseConnection(c);
        }

    }


    public List<Long> getBlackList() throws DorianInternalFault {
        buildDatabase();
        List<Long> list = new ArrayList<Long>();
        Connection c = null;
        try {
            c = db.getConnection();
            PreparedStatement s = c.prepareStatement("select " + SERIAL + " from " + TABLE);
            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                list.add(Long.valueOf(rs.getLong(SERIAL)));
            }
            rs.close();
            s.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("Unexpected error encountered.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        } finally {
            db.releaseConnection(c);
        }
        return list;
    }


    public boolean memberOfBlackList(long id) throws DorianInternalFault {
        buildDatabase();
        Connection c = null;
        boolean exists = false;
        try {
            c = db.getConnection();
            PreparedStatement s = c.prepareStatement("select count(*) from " + TABLE + " WHERE " + SERIAL + "= ?");
            s.setLong(1, id);
            ResultSet rs = s.executeQuery();
            if (rs.next()) {
                if (rs.getInt(1) > 0) {
                    exists = true;
                }
            }
            rs.close();
            s.close();
        } catch (Exception e) {
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("Unexpected Database Error");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        } finally {
            db.releaseConnection(c);
        }
        return exists;
    }


    public void buildDatabase() throws DorianInternalFault {
        if (!dbBuilt) {
            try {
                if (!this.db.tableExists(TABLE)) {

                    String certificates = "CREATE TABLE " + TABLE + " (" + SERIAL + " BIGINT PRIMARY KEY," + SUBJECT
                        + " TEXT NOT NULL," + REASON + " VARCHAR(255) NOT NULL," + CERTIFICATE + " TEXT,"
                        + "INDEX document_index (" + SERIAL + "));";
                    db.update(certificates);

                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                DorianInternalFault fault = new DorianInternalFault();
                fault.setFaultString("An unexpected database error occurred.");
                FaultHelper helper = new FaultHelper(fault);
                helper.addFaultCause(e);
                fault = (DorianInternalFault) helper.getFault();
                throw fault;
            }
            this.dbBuilt = true;
        }
    }


    public void clearDatabase() throws DorianInternalFault {
        buildDatabase();
        try {
            db.update("delete from " + TABLE);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("An unexpected database error occurred.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        }
    }

}
