package org.cagrid.gaards.dorian.service;

import gov.nih.nci.cagrid.common.FaultHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.cagrid.gaards.dorian.Metadata;
import org.cagrid.gaards.dorian.common.LoggingObject;
import org.cagrid.gaards.dorian.stubs.types.DorianInternalFault;
import org.cagrid.tools.database.Database;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class MetadataManager extends LoggingObject {
    private Database db;

    private boolean dbBuilt = false;

    private String table;


    public MetadataManager(Database db, String table) {
        this.db = db;
        this.table = table;
    }


    public boolean exists(String name) throws DorianInternalFault {
        this.buildDatabase();
        Connection c = null;
        boolean exists = false;
        try {
            c = db.getConnection();
            PreparedStatement s = c.prepareStatement("select count(*) from " + table + " where name= ?");
            s.setString(1, name);
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
            fault.setFaultString("Unexpected Database Error, could not determine if the metadata " + name + " exists.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        } finally {
            db.releaseConnection(c);
        }
        return exists;
    }


    public synchronized void insert(Metadata metadata) throws DorianInternalFault {
        this.buildDatabase();
        Connection c = null;
        try {
            if (!exists(metadata.getName())) {
                c = db.getConnection();
                PreparedStatement s = c.prepareStatement("INSERT INTO " + table
                    + " SET NAME= ?, DESCRIPTION= ?, VALUE= ?");
                s.setString(1, metadata.getName());
                s.setString(2, metadata.getDescription());
                s.setString(3, metadata.getValue());
                s.execute();
            } else {
                DorianInternalFault fault = new DorianInternalFault();
                fault.setFaultString("Could not insert the metadata " + metadata.getName()
                    + " because it already exists.");
                throw fault;
            }
        } catch (Exception e) {
            logError(e.getMessage(), e);
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("Unexpected Database Error, could insert  metadata!!!");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        } finally {
            db.releaseConnection(c);
        }
    }


    public synchronized void update(Metadata metadata) throws DorianInternalFault {
        this.buildDatabase();
        Connection c = null;
        try {
            if (exists(metadata.getName())) {
                c = db.getConnection();
                PreparedStatement s = c.prepareStatement("UPDATE " + table
                    + " SET DESCRIPTION= ?, VALUE= ? WHERE NAME= ?");

                s.setString(1, metadata.getDescription());
                s.setString(2, metadata.getValue());
                s.setString(3, metadata.getName());
                s.execute();
            } else {
                insert(metadata);
            }
        } catch (Exception e) {
            logError(e.getMessage(), e);
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("Unexpected Database Error, could update metadata!!!");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        } finally {
            db.releaseConnection(c);
        }
    }


    public synchronized void remove(String name) throws DorianInternalFault {
        this.buildDatabase();
        Connection c = null;
        try {
            c = db.getConnection();
            PreparedStatement s = c.prepareStatement("DELETE FROM " + table + " WHERE NAME= ?");
            s.setString(1, name);
            s.execute();

        } catch (Exception e) {
            logError(e.getMessage(), e);
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("Unexpected Database Error, could remove metadata!!!");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        } finally {
            db.releaseConnection(c);
        }
    }


    public Metadata get(String name) throws DorianInternalFault {
        this.buildDatabase();
        Connection c = null;

        String value = null;
        String description = null;
        try {
            c = db.getConnection();
            PreparedStatement s = c.prepareStatement("select DESCRIPTION,VALUE from " + table + " where name= ?");
            s.setString(1, name);
            ResultSet rs = s.executeQuery();
            if (rs.next()) {
                value = rs.getString("VALUE");
                description = rs.getString("DESCRIPTION");
            }
            rs.close();
            s.close();

        } catch (Exception e) {
            logError(e.getMessage(), e);
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("Unexpected Database Error, obtain the metadata " + name + ".");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        } finally {
            db.releaseConnection(c);
        }

        if (value == null) {
            return null;
        } else {
            Metadata metadata = new Metadata();
            metadata.setName(name);
            metadata.setValue(value);
            metadata.setDescription(description);
            return metadata;
        }
    }


    public void clearDatabase() throws DorianInternalFault {
        this.buildDatabase();
        try {
            db.update("DELETE FROM " + table);
        } catch (Exception e) {
            logError(e.getMessage(), e);
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("Unexpected Database Error.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        }
    }


    private void buildDatabase() throws DorianInternalFault {
        try {
            if (!dbBuilt) {
                if (!this.db.tableExists(table)) {
                    String applications = "CREATE TABLE " + table + " (" + "NAME VARCHAR(255) NOT NULL PRIMARY KEY,"
                        + "DESCRIPTION TEXT," + "VALUE TEXT NOT NULL," + "INDEX document_index (NAME));";
                    db.update(applications);
                }
                this.dbBuilt = true;
            }
        } catch (Exception e) {
            logError(e.getMessage(), e);
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("Unexpected Database Error.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        }
    }
}