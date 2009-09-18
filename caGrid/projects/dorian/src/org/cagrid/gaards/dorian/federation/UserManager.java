package org.cagrid.gaards.dorian.federation;

import gov.nih.nci.cagrid.common.FaultHelper;
import gov.nih.nci.cagrid.common.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cagrid.gaards.dorian.ca.CertificateAuthority;
import org.cagrid.gaards.dorian.common.LoggingObject;
import org.cagrid.gaards.dorian.service.PropertyManager;
import org.cagrid.gaards.dorian.service.util.AddressValidator;
import org.cagrid.gaards.dorian.stubs.types.DorianInternalFault;
import org.cagrid.gaards.dorian.stubs.types.InvalidUserFault;
import org.cagrid.tools.database.Database;
import org.cagrid.tools.database.DatabaseException;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class UserManager extends LoggingObject {

    public static final String CANNOT_UPDATE_GRID_IDENTITY_ERROR = "Cannot update/change a user's Grid Identity.";

    public static final String USERS_TABLE = "ifs_users";

    public static final String GID_FIELD = "GID";

    public static final String IDP_FIELD = "IDP_ID";

    public static final String UID_FIELD = "UID";

    public static final String STATUS_FIELD = "STATUS";

    public static final String FIRST_NAME_FIELD = "FIRST_NAME";

    public static final String LAST_NAME_FIELD = "LAST_NAME";

    public static final String EMAIL_FIELD = "EMAIL";

    private Database db;

    private boolean dbBuilt = false;

    private IdentityFederationProperties conf;

    private TrustedIdPManager tm;

    private FederationDefaults defaults;

    private PropertyManager properties;

    private Publisher publisher;

    private CertificateAuthority ca;


    public UserManager(Database db, IdentityFederationProperties conf, PropertyManager properties,
        CertificateAuthority ca, TrustedIdPManager tm, Publisher publisher, FederationDefaults defaults) {
        super();
        this.db = db;
        this.tm = tm;
        this.defaults = defaults;
        this.conf = conf;
        this.properties = properties;
        this.publisher = publisher;
        this.ca = ca;
    }


    public String getIdentityAssignmentPolicy() {
        return conf.getIdentityAssignmentPolicy();
    }


    public synchronized boolean determineIfUserExists(long idpId, String uid) throws DorianInternalFault {
        buildDatabase();
        Connection c = null;
        boolean exists = false;
        try {
            c = db.getConnection();
            PreparedStatement s = c.prepareStatement("select count(*) from " + USERS_TABLE + " WHERE " + IDP_FIELD
                + "= ? AND " + UID_FIELD + "= ?");
            s.setLong(1, idpId);
            s.setString(2, uid);

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


    public String getCredentialsManagerUID(long idpId, String uid) {
        return "IdPId=" + idpId + ":UID=" + uid;
    }


    public String getUserSubject(String caSubject, TrustedIdP idp, String uid) {
        return getUserSubject(this.conf.getIdentityAssignmentPolicy(), caSubject, idp, uid);
    }


    public static String getUserSubject(String policy, String caSubject, TrustedIdP idp, String uid) {
        int caindex = caSubject.lastIndexOf(",");
        String caPreSub = caSubject.substring(0, caindex);
        if (policy.equals(IdentityAssignmentPolicy.ID)) {
            return caPreSub + ",OU=IdP [" + idp.getId() + "],CN=" + uid;
        } else {
            return caPreSub + ",OU=" + idp.getName() + ",CN=" + uid;
        }
    }


    public synchronized GridUser getUser(long idpId, String uid) throws DorianInternalFault, InvalidUserFault {
        this.buildDatabase();
        GridUser user = new GridUser();
        Connection c = null;
        try {
            c = db.getConnection();
            PreparedStatement s = c.prepareStatement("select * from " + USERS_TABLE + " WHERE " + IDP_FIELD
                + "= ? AND " + UID_FIELD + "= ?");
            s.setLong(1, idpId);
            s.setString(2, uid);
            ResultSet rs = s.executeQuery();
            if (rs.next()) {
                user.setIdPId(rs.getLong(IDP_FIELD));
                user.setUID(rs.getString(UID_FIELD));
                user.setGridId(rs.getString(GID_FIELD));
                String firstName = rs.getString(FIRST_NAME_FIELD);
                if ((firstName != null) && (!firstName.equalsIgnoreCase("null"))) {
                    user.setFirstName(firstName);
                }

                String lastName = rs.getString(LAST_NAME_FIELD);
                if ((lastName != null) && (!lastName.equalsIgnoreCase("null"))) {
                    user.setLastName(lastName);
                }
                String email = rs.getString(EMAIL_FIELD);
                if ((email != null) && (!email.equals("null"))) {
                    user.setEmail(email);
                }
                user.setUserStatus(GridUserStatus.fromValue(rs.getString(STATUS_FIELD)));
            } else {
                InvalidUserFault fault = new InvalidUserFault();
                fault.setFaultString("No such user " + getCredentialsManagerUID(user.getIdPId(), user.getUID()));
                throw fault;

            }
            rs.close();
            s.close();
        } catch (InvalidUserFault iuf) {
            throw iuf;
        } catch (Exception e) {
            logError(e.getMessage(), e);
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("Unexpected Error, could not obtain the user "
                + getCredentialsManagerUID(user.getIdPId(), user.getUID()));
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        } finally {
            db.releaseConnection(c);
        }
        return user;
    }


    public synchronized GridUser getUser(String gridId) throws DorianInternalFault, InvalidUserFault {
        this.buildDatabase();
        GridUser user = new GridUser();
        Connection c = null;
        try {
            c = db.getConnection();
            PreparedStatement s = c.prepareStatement("select * from " + USERS_TABLE + " WHERE " + GID_FIELD + "= ?");
            s.setString(1, gridId);
            ResultSet rs = s.executeQuery();
            if (rs.next()) {
                user.setIdPId(rs.getLong(IDP_FIELD));
                user.setUID(rs.getString(UID_FIELD));
                user.setGridId(rs.getString(GID_FIELD));
                String firstName = rs.getString(FIRST_NAME_FIELD);
                if ((firstName != null) && (!firstName.equalsIgnoreCase("null"))) {
                    user.setFirstName(firstName);
                }

                String lastName = rs.getString(LAST_NAME_FIELD);
                if ((lastName != null) && (!lastName.equalsIgnoreCase("null"))) {
                    user.setLastName(lastName);
                }
                String email = rs.getString(EMAIL_FIELD);
                if ((email != null) && (!email.equalsIgnoreCase("null"))) {
                    user.setEmail(email);
                }
                user.setUserStatus(GridUserStatus.fromValue(rs.getString(STATUS_FIELD)));
            } else {
                InvalidUserFault fault = new InvalidUserFault();
                fault.setFaultString("No such user " + gridId);
                throw fault;

            }
            rs.close();
            s.close();
        } catch (InvalidUserFault iuf) {
            throw iuf;
        } catch (Exception e) {
            logError(e.getMessage(), e);
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("Unexpected Error, could not obtain the user " + gridId);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        } finally {
            db.releaseConnection(c);
        }
        return user;
    }


    public synchronized GridUser[] getUsers(GridUserFilter filter) throws DorianInternalFault {

        this.buildDatabase();
        Connection c = null;
        List<GridUser> users = new ArrayList<GridUser>();
        try {
            c = db.getConnection();
            PreparedStatement s = null;
            if (filter != null) {
                s = c.prepareStatement("select * from  " + USERS_TABLE + " WHERE " + IDP_FIELD + ">= ? AND "
                    + IDP_FIELD + "<= ? AND " + UID_FIELD + " LIKE ? AND " + GID_FIELD + " LIKE ? AND " + STATUS_FIELD
                    + " LIKE ? AND " + FIRST_NAME_FIELD + " LIKE ? AND " + LAST_NAME_FIELD + " LIKE ? AND "
                    + EMAIL_FIELD + " LIKE ?");

                if (filter.getIdPId() > 0) {
                    s.setLong(1, filter.getIdPId());
                    s.setLong(2, filter.getIdPId());
                } else {
                    s.setLong(1, 0);
                    s.setLong(2, Long.MAX_VALUE);
                }

                if (filter.getUID() != null) {
                    s.setString(3, "%" + filter.getUID() + "%");
                } else {
                    s.setString(3, "%");
                }

                if (filter.getGridId() != null) {
                    s.setString(4, "%" + filter.getGridId() + "%");
                } else {
                    s.setString(4, "%");
                }

                if (filter.getUserStatus() != null) {
                    s.setString(5, filter.getUserStatus().getValue());
                } else {
                    s.setString(5, "%");
                }

                if (filter.getFirstName() != null) {
                    s.setString(6, "%" + filter.getFirstName() + "%");
                } else {
                    s.setString(6, "%");
                }

                if (filter.getLastName() != null) {
                    s.setString(7, "%" + filter.getLastName() + "%");
                } else {
                    s.setString(7, "%");
                }

                if (filter.getEmail() != null) {
                    s.setString(8, "%" + filter.getEmail() + "%");
                } else {
                    s.setString(8, "%");
                }
            } else {
                s = c.prepareStatement("select * from  " + USERS_TABLE);
            }

            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                GridUser user = new GridUser();
                user.setIdPId(rs.getLong(IDP_FIELD));
                user.setUID(rs.getString(UID_FIELD));
                user.setGridId(rs.getString(GID_FIELD));
                String firstName = rs.getString(FIRST_NAME_FIELD);
                if ((firstName != null) && (!firstName.equalsIgnoreCase("null"))) {
                    user.setFirstName(firstName);
                }

                String lastName = rs.getString(LAST_NAME_FIELD);
                if ((lastName != null) && (!lastName.equalsIgnoreCase("null"))) {
                    user.setLastName(lastName);
                }
                String email = rs.getString(EMAIL_FIELD);
                if ((email != null) && (!email.equals("null"))) {
                    user.setEmail(email);
                }
                user.setUserStatus(GridUserStatus.fromValue(rs.getString(STATUS_FIELD)));
                users.add(user);
            }
            rs.close();
            s.close();

            GridUser[] list = new GridUser[users.size()];
            for (int i = 0; i < list.length; i++) {
                list[i] = (GridUser) users.get(i);
            }
            return list;

        } catch (Exception e) {
            logError(e.getMessage(), e);
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("Unexpected Error, could not obtain a list of users");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        } finally {
            db.releaseConnection(c);
        }
    }


    public synchronized GridUser addUser(TrustedIdP idp, GridUser user) throws DorianInternalFault, InvalidUserFault {
        this.buildDatabase();
        if (!determineIfUserExists(user.getIdPId(), user.getUID())) {

            Connection c = null;
            try {

                String caSubject = ca.getCACertificate().getSubjectDN().getName();
                user.setGridId(subjectToIdentity(getUserSubject(caSubject, idp, user.getUID())));
                user.setUserStatus(GridUserStatus.Pending);
                try {
                    AddressValidator.validateEmail(user.getEmail());
                } catch (IllegalArgumentException e) {
                    InvalidUserFault fault = new InvalidUserFault();
                    fault.setFaultString(e.getMessage());
                    throw fault;
                }
                validateSpecifiedField("UID", user.getUID());
                validateSpecifiedField("Grid Id", user.getGridId(), false);
                validateSpecifiedField("First Name", user.getFirstName());
                validateSpecifiedField("Last Name", user.getLastName());
                c = db.getConnection();
                PreparedStatement s = c.prepareStatement("INSERT INTO " + USERS_TABLE + " SET " + IDP_FIELD + "= ?,"
                    + UID_FIELD + "= ?," + GID_FIELD + "= ?, " + STATUS_FIELD + "=?, " + FIRST_NAME_FIELD + "=?, "
                    + LAST_NAME_FIELD + "= ?, EMAIL=?");
                s.setLong(1, user.getIdPId());
                s.setString(2, user.getUID());
                s.setString(3, user.getGridId());
                s.setString(4, user.getUserStatus().toString());
                s.setString(5, user.getFirstName());
                s.setString(6, user.getLastName());
                s.setString(7, user.getEmail());
                s.execute();
                if (!user.getUserStatus().equals(GridUserStatus.Active)) {
                    publisher.publishCRL();
                }
            } catch (InvalidUserFault iuf) {
                throw iuf;
            } catch (Exception e) {
                try {
                    this.removeUser(user.getIdPId(), user.getUID());
                } catch (Exception ex) {

                }
                logError(e.getMessage(), e);
                DorianInternalFault fault = new DorianInternalFault();
                fault.setFaultString("Error adding the user "
                    + getCredentialsManagerUID(user.getIdPId(), user.getUID())
                    + " to the IFS, an unexpected database error occurred.");
                FaultHelper helper = new FaultHelper(fault);
                helper.addFaultCause(e);
                fault = (DorianInternalFault) helper.getFault();
                throw fault;
            } finally {
                db.releaseConnection(c);
            }

        } else {
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("Error adding the user, " + getCredentialsManagerUID(user.getIdPId(), user.getUID())
                + ", the user already exists!!!");
            throw fault;

        }

        return user;
    }


    public synchronized void updateUser(GridUser u) throws DorianInternalFault, InvalidUserFault {
        this.buildDatabase();
        String credId = getCredentialsManagerUID(u.getIdPId(), u.getUID());
        boolean publishCRL = false;
        if (determineIfUserExists(u.getIdPId(), u.getUID())) {

            Connection c = null;
            try {

                GridUser curr = this.getUser(u.getIdPId(), u.getUID());

                if ((u.getFirstName() != null) && (!u.getFirstName().equals(curr.getFirstName()))) {
                    validateSpecifiedField("First Name", u.getFirstName());
                    curr.setFirstName(u.getFirstName());
                }

                if ((u.getLastName() != null) && (!u.getLastName().equals(curr.getLastName()))) {
                    validateSpecifiedField("Last Name", u.getLastName());
                    curr.setLastName(u.getLastName());
                }

                if ((u.getEmail() != null) && (!u.getEmail().equals(curr.getEmail()))) {
                    try {
                        AddressValidator.validateEmail(u.getEmail());
                    } catch (IllegalArgumentException e) {
                        InvalidUserFault fault = new InvalidUserFault();
                        fault.setFaultString(e.getMessage());
                        throw fault;
                    }
                    curr.setEmail(u.getEmail());
                }

                if ((u.getGridId() != null) && (!u.getGridId().equals(curr.getGridId()))) {
                    InvalidUserFault fault = new InvalidUserFault();
                    fault.setFaultString(CANNOT_UPDATE_GRID_IDENTITY_ERROR);
                    throw fault;
                }

                if ((u.getUserStatus() != null) && (!u.getUserStatus().equals(curr.getUserStatus()))) {
                    if (accountCreated(curr.getUserStatus()) && !accountCreated(u.getUserStatus())) {
                        InvalidUserFault fault = new InvalidUserFault();
                        fault.setFaultString("Error, cannot change " + credId
                            + "'s status from a post-created account status (" + curr.getUserStatus()
                            + ") to a pre-created account status (" + u.getUserStatus() + ").");
                        throw fault;
                    } else if (curr.getUserStatus().equals(GridUserStatus.Rejected)) {
                        InvalidUserFault fault = new InvalidUserFault();
                        fault.setFaultString("Cannot change the status of account that has been rejected.");
                        throw fault;
                    }
                    if (curr.getUserStatus().equals(GridUserStatus.Active)) {
                        publishCRL = true;
                    } else if (u.getUserStatus().equals(GridUserStatus.Active)) {
                        publishCRL = true;
                    }
                    curr.setUserStatus(u.getUserStatus());
                }

                c = db.getConnection();
                PreparedStatement s = c.prepareStatement("UPDATE " + USERS_TABLE + " SET " + STATUS_FIELD + "=?, "
                    + FIRST_NAME_FIELD + "=?, " + LAST_NAME_FIELD + "= ?, " + EMAIL_FIELD + "=? where " + IDP_FIELD
                    + "= ? AND " + UID_FIELD + "= ?");
                s.setString(1, curr.getUserStatus().getValue());
                s.setString(2, curr.getFirstName());
                s.setString(3, curr.getLastName());
                s.setString(4, curr.getEmail());
                s.setLong(5, curr.getIdPId());
                s.setString(6, curr.getUID());
                s.execute();
                if (publishCRL) {
                    publisher.publishCRL();
                }
            } catch (InvalidUserFault e) {
                throw e;
            } catch (Exception e) {
                logError(e.getMessage(), e);
                DorianInternalFault fault = new DorianInternalFault();
                fault.setFaultString("Error updating the Grid user "
                    + getCredentialsManagerUID(u.getIdPId(), u.getUID()) + ", an unexpected database error occurred.");
                FaultHelper helper = new FaultHelper(fault);
                helper.addFaultCause(e);
                fault = (DorianInternalFault) helper.getFault();
                throw fault;
            } finally {
                db.releaseConnection(c);
            }

        } else {
            InvalidUserFault fault = new InvalidUserFault();
            fault.setFaultString("Could not update user, the user " + credId + " does not exist.");
            throw fault;
        }
    }


    private boolean accountCreated(GridUserStatus status) {
        if (status.equals(GridUserStatus.Pending)) {
            return false;
        } else if (status.equals(GridUserStatus.Rejected)) {
            return false;
        } else {
            return true;
        }
    }


    public synchronized void removeUser(GridUser user) throws DorianInternalFault, InvalidUserFault {
        this.buildDatabase();
        if (determineIfUserExists(user.getIdPId(), user.getUID())) {
            this.removeUser(user.getIdPId(), user.getUID());
        } else {
            InvalidUserFault fault = new InvalidUserFault();
            fault.setFaultString("Could not remove user, the specified user does not exist.");
            throw fault;
        }
    }


    public synchronized void removeUser(long idpId, String uid) throws DorianInternalFault {
        this.buildDatabase();
        Connection c = null;
        try {
            c = db.getConnection();
            PreparedStatement s = c.prepareStatement("delete from " + USERS_TABLE + " WHERE " + IDP_FIELD + "= ? AND "
                + UID_FIELD + "= ?");
            s.setLong(1, idpId);
            s.setString(2, uid);
            s.execute();
            s.close();
        } catch (Exception e) {
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("Unexpected Database Error - Could not remove user!!!");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        } finally {
            db.releaseConnection(c);
        }
    }


    private void validateSpecifiedField(String type, String name, boolean validateLength) throws InvalidUserFault {
        name = Utils.clean(name);
        if (name == null) {
            throw new IllegalArgumentException("No " + type + " specified.");
        }
        if (validateLength) {
            if (name.length() > 255) {
                throw new IllegalArgumentException("The " + type
                    + " specified is too long, it must be less than 255 characters.");
            }
        }
    }


    private void validateSpecifiedField(String type, String name) throws InvalidUserFault {
        validateSpecifiedField(type, name, true);
    }


    public void clearDatabase() throws DorianInternalFault {
        try {
            db.update("DROP TABLE IF EXISTS " + USERS_TABLE);
        } catch (Exception e) {
            logError(e.getMessage(), e);
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("An unexpected database error occurred.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        }
        this.tm.clearDatabase();
        this.dbBuilt = false;
    }


    public void buildDatabase() throws DorianInternalFault {
        if (!dbBuilt) {
            try {
                if (!this.db.tableExists(USERS_TABLE)) {
                    String users = "CREATE TABLE " + USERS_TABLE + " (" + IDP_FIELD + " INT NOT NULL," + UID_FIELD
                        + " VARCHAR(255) NOT NULL," + FIRST_NAME_FIELD + " VARCHAR(255) NOT NULL," + LAST_NAME_FIELD
                        + " VARCHAR(255) NOT NULL," + GID_FIELD + " TEXT NOT NULL," + STATUS_FIELD
                        + " VARCHAR(50) NOT NULL," + EMAIL_FIELD + " VARCHAR(255) NOT NULL, "
                        + "INDEX document_index (" + UID_FIELD + "));";
                    db.update(users);
                    properties.setCurrentVersion();
                    try {

                        if (defaults.getDefaultIdP() != null) {
                            TrustedIdP idp = tm.addTrustedIdP(defaults.getDefaultIdP());
                            GridUser usr = defaults.getDefaultUser();
                            if (usr != null) {
                                usr.setIdPId(idp.getId());
                                this.addUser(idp, usr);
                                usr.setUserStatus(GridUserStatus.Active);
                                this.updateUser(usr);
                            } else {
                                DorianInternalFault fault = new DorianInternalFault();
                                fault
                                    .setFaultString("Unexpected error initializing the User Manager, No initial IFS user specified.");
                                throw fault;
                            }
                        } else {
                            DorianInternalFault fault = new DorianInternalFault();
                            fault
                                .setFaultString("Unexpected error initializing the User Manager, No initial trusted IdP specified.");
                            throw fault;
                        }

                    } catch (DorianInternalFault e) {
                        throw e;

                    } catch (Exception e) {
                        DorianInternalFault fault = new DorianInternalFault();
                        fault.setFaultString("Unexpected error initializing the User Manager.");
                        FaultHelper helper = new FaultHelper(fault);
                        helper.addDescription(Utils.getExceptionMessage(e));
                        helper.addFaultCause(e);
                        fault = (DorianInternalFault) helper.getFault();
                        throw fault;

                    }

                }
            } catch (DatabaseException e) {
                logError(e.getMessage(), e);
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


    public Set<String> getDisabledUsers() throws DorianInternalFault {
        Set<String> users = new HashSet<String>();
        this.buildDatabase();
        Connection c = null;
        try {
            // First get all the users who's accounts are disabled.
            c = db.getConnection();
            Statement s = c.createStatement();

            StringBuffer sql = new StringBuffer();
            sql.append("select " + GID_FIELD + " from " + USERS_TABLE + " WHERE " + STATUS_FIELD + "='"
                + GridUserStatus.Suspended + "' OR " + STATUS_FIELD + "='" + GridUserStatus.Pending + "' OR "
                + STATUS_FIELD + "='" + GridUserStatus.Rejected + "'");
            ResultSet rs = s.executeQuery(sql.toString());
            while (rs.next()) {
                String gid = (rs.getString(GID_FIELD));
                if (!users.contains(gid)) {
                    users.add(gid);
                }
            }
            rs.close();
            s.close();

            // Now get all the IdPs who are suspended.
            TrustedIdP[] idp = this.tm.getSuspendedTrustedIdPs();
            if (idp != null) {
                for (int i = 0; i < idp.length; i++) {
                    Statement stmt = c.createStatement();
                    StringBuffer sb = new StringBuffer();
                    sb.append("select " + GID_FIELD + " from " + USERS_TABLE + " WHERE " + IDP_FIELD + "="
                        + idp[i].getId());
                    ResultSet result = stmt.executeQuery(sb.toString());
                    while (result.next()) {
                        String gid = result.getString(GID_FIELD);
                        if (!users.contains(gid)) {
                            users.add(gid);
                        }
                    }
                    stmt.close();
                    result.close();
                }
            }
            return users;

        } catch (Exception e) {
            logError(e.getMessage(), e);
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("Unexpected Error, could not obtain a list of users");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        } finally {
            db.releaseConnection(c);
        }
    }


    public static String identityToSubject(String identity) {
        String s = identity.substring(1);
        return s.replace('/', ',');
    }


    public static String subjectToIdentity(String subject) {
        return "/" + subject.replace(',', '/');
    }

}