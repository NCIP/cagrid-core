package gov.nih.nci.cagrid.gts.service;

import gov.nih.nci.cagrid.gts.bean.Permission;
import gov.nih.nci.cagrid.gts.bean.PermissionFilter;
import gov.nih.nci.cagrid.gts.bean.Role;
import gov.nih.nci.cagrid.gts.common.Constants;
import gov.nih.nci.cagrid.gts.common.Database;
import gov.nih.nci.cagrid.gts.service.db.DBManager;
import gov.nih.nci.cagrid.gts.service.db.PermissionsTable;
import gov.nih.nci.cagrid.gts.stubs.types.GTSInternalFault;
import gov.nih.nci.cagrid.gts.stubs.types.IllegalPermissionFault;
import gov.nih.nci.cagrid.gts.stubs.types.InvalidPermissionFault;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: TrustedAuthorityManager.java,v 1.1 2006/03/08 19:48:46 langella
 *          Exp $
 */
public class PermissionManager {

    private Log log;

    private boolean dbBuilt = false;

    private DBManager dbManager;

    private Database db;


    public PermissionManager(DBManager dbManager) {
        log = LogFactory.getLog(this.getClass().getName());
        this.dbManager = dbManager;
        this.db = dbManager.getDatabase();
    }


    public synchronized void addPermission(Permission p) throws GTSInternalFault, IllegalPermissionFault {

        // This method assumes that any Trusted Authorites associated with a
        // permission is valid
        this.buildDatabase();
        if (p.getTrustedAuthorityName() == null) {
            p.setTrustedAuthorityName(Constants.ALL_TRUST_AUTHORITIES);
        }

        if (p.getGridIdentity() == null) {
            IllegalPermissionFault fault = new IllegalPermissionFault();
            fault.setFaultString("The permission " + formatPermission(p) + " no grid identity specified.");
            throw fault;
        }

        if (p.getRole() == null) {
            IllegalPermissionFault fault = new IllegalPermissionFault();
            fault.setFaultString("The permission " + formatPermission(p) + " no role specified.");
            throw fault;
        }

        if ((p.getTrustedAuthorityName().equals(Constants.ALL_TRUST_AUTHORITIES))
            && (!p.getRole().equals(Role.TrustServiceAdmin))) {
            IllegalPermissionFault fault = new IllegalPermissionFault();
            fault.setFaultString("The permission " + formatPermission(p) + " must specify a specific Trust Authority.");
            throw fault;
        }
        if ((!p.getTrustedAuthorityName().equals(Constants.ALL_TRUST_AUTHORITIES))
            && (p.getRole().equals(Role.TrustServiceAdmin))) {
            IllegalPermissionFault fault = new IllegalPermissionFault();
            fault.setFaultString("The permission " + formatPermission(p)
                + " cannot specify a specific Trust Authority.");
            throw fault;
        }

        if (this.doesPermissionExist(p)) {
            IllegalPermissionFault fault = new IllegalPermissionFault();
            fault.setFaultString("The permission " + formatPermission(p) + " cannot be added, it already exists.");
            throw fault;
        }

        StringBuffer insert = new StringBuffer();
        Connection c = null;
        try {
            insert.append("INSERT INTO " + PermissionsTable.TABLE_NAME + " SET " + PermissionsTable.GRID_IDENTITY
                + "= ?," + PermissionsTable.ROLE + "= ?," + PermissionsTable.TRUSTED_AUTHORITY + "= ?");
            c = db.getConnection();
            PreparedStatement s = c.prepareStatement(insert.toString());
            s.setString(1, p.getGridIdentity());
            s.setString(2, p.getRole().getValue());
            s.setString(3, p.getTrustedAuthorityName());
            s.execute();
            s.close();
        } catch (Exception e) {
            this.log.error("Unexpected database error incurred in adding the permission " + formatPermission(p)
                + ", the following statement generated the error: \n" + insert.toString() + "\n", e);
            GTSInternalFault fault = new GTSInternalFault();
            fault.setFaultString("Unexpected error adding the permission " + formatPermission(p) + "!!!");
            throw fault;
        } finally {
            db.releaseConnection(c);
        }
    }


    public synchronized void revokePermission(Permission p) throws GTSInternalFault, InvalidPermissionFault {
        buildDatabase();
        if (!doesPermissionExist(p)) {
            InvalidPermissionFault fault = new InvalidPermissionFault();
            fault.setFaultString("Could not revoke " + formatPermission(p) + ", the permission does not exist!!!");
            throw fault;
        }

        String sql = "delete from " + PermissionsTable.TABLE_NAME + " where " + PermissionsTable.GRID_IDENTITY
            + "= ? AND " + PermissionsTable.ROLE + "= ? AND " + PermissionsTable.TRUSTED_AUTHORITY + "= ?";
        Connection c = null;
        try {
            c = db.getConnection();
            PreparedStatement s = c.prepareStatement(sql);
            s.setString(1, p.getGridIdentity());
            s.setString(2, p.getRole().getValue());
            s.setString(3, p.getTrustedAuthorityName());
            s.execute();
            s.close();
        } catch (Exception e) {
            String perm = formatPermission(p);
            this.log.error("Unexpected database error incurred in removing the permission " + perm
                + " exists, the following statement generated the error: \n" + sql + "\n", e);
            GTSInternalFault fault = new GTSInternalFault();
            fault.setFaultString("Unexpected error in removing the permission " + perm + " exists.");
            throw fault;
        } finally {
            db.releaseConnection(c);
        }

    }


    public synchronized void revokePermissions(String trustedAuthorityName) throws GTSInternalFault {
        buildDatabase();

        String sql = "delete from " + PermissionsTable.TABLE_NAME + " where " + PermissionsTable.TRUSTED_AUTHORITY
            + "= ?";
        Connection c = null;
        try {
            c = db.getConnection();
            PreparedStatement s = c.prepareStatement(sql);
            s.setString(1, trustedAuthorityName);
            s.execute();
            s.close();
        } catch (Exception e) {
            this.log.error("Unexpected database error incurred in removing the permissions for the trusted authority "
                + trustedAuthorityName + ".", e);
            GTSInternalFault fault = new GTSInternalFault();
            fault
                .setFaultString("Unexpected database error incurred in removing the permissions for the trusted authority "
                    + trustedAuthorityName + ".");
            throw fault;
        } finally {
            db.releaseConnection(c);
        }

    }


    public synchronized boolean doesPermissionExist(Permission p) throws GTSInternalFault {
        String sql = "select count(*) from " + PermissionsTable.TABLE_NAME + " where " + PermissionsTable.GRID_IDENTITY
            + "= ? AND " + PermissionsTable.ROLE + "= ?  AND " + PermissionsTable.TRUSTED_AUTHORITY + "= ?";
        Connection c = null;
        boolean exists = false;
        try {
            c = db.getConnection();
            PreparedStatement s = c.prepareStatement(sql);
            s.setString(1, p.getGridIdentity());
            s.setString(2, p.getRole().getValue());
            s.setString(3, p.getTrustedAuthorityName());
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
            String perm = formatPermission(p);
            this.log.error("Unexpected database error incurred in determining if the permission " + perm
                + " exists, the following statement generated the error: \n" + sql + "\n", e);
            GTSInternalFault fault = new GTSInternalFault();
            fault.setFaultString("Unexpected error in determining if the permission " + perm + " exists.");
            throw fault;
        } finally {
            db.releaseConnection(c);
        }
        return exists;
    }


    public boolean isUserTrustServiceAdmin(String gridIdentity) throws GTSInternalFault {
        this.buildDatabase();
        Connection c = null;
        boolean isAdmin = false;
        StringBuffer sql = new StringBuffer();
        sql.append("select count(*) from " + PermissionsTable.TABLE_NAME);
        sql.append(" WHERE " + PermissionsTable.GRID_IDENTITY + " = ? AND ");
        sql.append(PermissionsTable.ROLE + "='" + Role.TrustServiceAdmin + "' AND ");
        sql.append(PermissionsTable.TRUSTED_AUTHORITY + " = '" + Constants.ALL_TRUST_AUTHORITIES + "'");
        try {
            c = db.getConnection();
            PreparedStatement s = c.prepareStatement(sql.toString());
            s.setString(1, gridIdentity);
            ResultSet rs = s.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                if (count > 0) {
                    isAdmin = true;
                }
            }
            rs.close();
            s.close();

        } catch (Exception e) {
            this.log.error("Unexpected database error incurred in determining whether or not the user " + gridIdentity
                + "  is a trust service administrator, the following statement generated the error: \n"
                + sql.toString() + "\n", e);
            GTSInternalFault fault = new GTSInternalFault();
            fault.setFaultString("Unexpected error occurred in determining whether or not the user " + gridIdentity
                + "  is a trust service administrator.");
            throw fault;
        } finally {
            db.releaseConnection(c);
        }
        return isAdmin;
    }


    public boolean isUserTrustedAuthorityAdmin(String authority, String gridIdentity) throws GTSInternalFault {
        this.buildDatabase();
        Connection c = null;
        boolean isAdmin = false;
        StringBuffer sql = new StringBuffer();
        sql.append("select count(*) from " + PermissionsTable.TABLE_NAME);
        sql.append(" WHERE " + PermissionsTable.GRID_IDENTITY + " = ?" + " AND ");
        sql.append(PermissionsTable.ROLE + "='" + Role.TrustAuthorityManager + "' AND ");
        sql.append(PermissionsTable.TRUSTED_AUTHORITY + " = '" + authority + "'");
        try {
            c = db.getConnection();
            PreparedStatement s = c.prepareStatement(sql.toString());
            s.setString(1, gridIdentity);
            ResultSet rs = s.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                if (count > 0) {
                    isAdmin = true;
                }
            }
            rs.close();
            s.close();

        } catch (Exception e) {
            this.log.error("Unexpected database error incurred in determining whether or not the user " + gridIdentity
                + "  is a trust service administrator, the following statement generated the error: \n"
                + sql.toString() + "\n", e);
            GTSInternalFault fault = new GTSInternalFault();
            fault.setFaultString("Unexpected error occurred in determining whether or not the user " + gridIdentity
                + "  is a trust service administrator.");
            throw fault;
        } finally {
            db.releaseConnection(c);
        }
        return isAdmin;
    }


    public synchronized Permission[] findPermissions(PermissionFilter filter) throws GTSInternalFault {

        this.buildDatabase();
        Connection c = null;
        List<Permission> permissions = new ArrayList<Permission>();
        StringBuffer sql = new StringBuffer();

        try {
            c = db.getConnection();
            PreparedStatement s = null;
            if (filter != null) {
                s = c.prepareStatement("select * from " + PermissionsTable.TABLE_NAME + " WHERE "
                    + PermissionsTable.GRID_IDENTITY + " LIKE ? AND " + PermissionsTable.ROLE + " LIKE ? AND "
                    + PermissionsTable.TRUSTED_AUTHORITY + " LIKE ?");

                if (filter.getGridIdentity() != null) {
                    s.setString(1, "%" + filter.getGridIdentity() + "%");
                } else {
                    s.setString(1, "%");
                }

                if (filter.getRole() != null) {
                    s.setString(2, "%" + filter.getRole().getValue() + "%");
                } else {
                    s.setString(2, "%");
                }

                if (filter.getTrustedAuthorityName() != null) {
                    s.setString(3, "%" + filter.getTrustedAuthorityName() + "%");
                } else {
                    s.setString(3, "%");
                }
            } else {
                s = c.prepareStatement("select * from " + PermissionsTable.TABLE_NAME);
            }

            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                Permission p = new Permission();
                p.setGridIdentity(rs.getString(PermissionsTable.GRID_IDENTITY));
                p.setRole(Role.fromValue(rs.getString(PermissionsTable.ROLE)));
                p.setTrustedAuthorityName(clean(rs.getString(PermissionsTable.TRUSTED_AUTHORITY)));
                permissions.add(p);
            }
            rs.close();
            s.close();

            Permission[] list = new Permission[permissions.size()];
            for (int i = 0; i < list.length; i++) {
                list[i] = (Permission) permissions.get(i);
            }
            return list;

        } catch (Exception e) {
            this.log.error(
                "Unexpected database error incurred in finding permissions, the following statement generated the error: \n"
                    + sql.toString() + "\n", e);
            GTSInternalFault fault = new GTSInternalFault();
            fault.setFaultString("Unexpected error occurred in finding permissions.");
            throw fault;
        } finally {
            db.releaseConnection(c);
        }
    }


    private String clean(String s) {
        if ((s == null) || (s.trim().length() == 0)) {
            return null;
        } else {
            return s;
        }
    }


    private String formatPermission(Permission p) {
        String role = null;
        if (p.getRole() != null) {
            role = p.getRole().getValue();
        }
        return "[" + p.getGridIdentity() + "," + role + "," + p.getTrustedAuthorityName() + "]";
    }


    public synchronized void buildDatabase() throws GTSInternalFault {
        if (!dbBuilt) {
            try {
                db.createDatabase();
                if (!this.db.tableExists(PermissionsTable.TABLE_NAME)) {
                    String sql = this.dbManager.getPermissionsTable().getCreateTableSQL();
                    db.update(sql);
                }
                dbBuilt = true;
            } catch (Exception e) {
                this.log.error("Unexpected error in creating the database.", e);
                GTSInternalFault fault = new GTSInternalFault();
                fault.setFaultString("Unexpected error in creating the database.");
                throw fault;
            }
        }
    }


    public synchronized void clearDatabase() throws GTSInternalFault {
        try {
            buildDatabase();
            db.update("delete FROM " + PermissionsTable.TABLE_NAME);
        } catch (Exception e) {
            this.log.error("Unexpected error in removing the database.", e);
            GTSInternalFault fault = new GTSInternalFault();
            fault.setFaultString("Unexpected error in removing the database.");
            throw fault;
        }
    }

}
