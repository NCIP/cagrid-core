package org.cagrid.gaards.dorian.idp;

import gov.nih.nci.cagrid.common.FaultHelper;
import gov.nih.nci.cagrid.common.Utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.cagrid.gaards.authentication.BasicAuthentication;
import org.cagrid.gaards.authentication.Credential;
import org.cagrid.gaards.authentication.faults.CredentialNotSupportedFault;
import org.cagrid.gaards.authentication.faults.InvalidCredentialFault;
import org.cagrid.gaards.dorian.common.AuditConstants;
import org.cagrid.gaards.dorian.common.LoggingObject;
import org.cagrid.gaards.dorian.service.util.AddressValidator;
import org.cagrid.gaards.dorian.service.util.Crypt;
import org.cagrid.gaards.dorian.stubs.types.DorianInternalFault;
import org.cagrid.gaards.dorian.stubs.types.InvalidUserPropertyFault;
import org.cagrid.gaards.dorian.stubs.types.NoSuchUserFault;
import org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault;
import org.cagrid.tools.database.Database;
import org.cagrid.tools.events.EventManager;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class UserManager extends LoggingObject {

    public final static String PASSWORD_ERROR_MESSAGE = "The uid or password is incorrect.";

    public final static int SYSTEM_MAX_PASSWORD_LENGTH = 30;

    public final static String SYSTEM_MAX_PASSWORD_ERROR_PREFIX = "Unacceptable password, the length of the password cannot exceed the maximum system password length of ";

    public static String INVALID_PASSWORD_MESSAGE = "Invalid password, a valid password CANNOT contain a dictionary word and MUST contain at least one upper case letter, at least one lower case letter, at least one number, and at least one symbol (~!@#$%^&*()_-+={}[]|:;<>,.?)";

    public static String ADMIN_USER_ID = "dorian";

    public static String ADMIN_PASSWORD = "DorianAdmin$1";

    private static final String IDP_USERS_TABLE = "idp_users";

    private Database db;

    private boolean dbBuilt = false;

    private IdentityProviderProperties conf;

    private PasswordSecurityManager passwordSecurityManager;


    public UserManager(Database db, IdentityProviderProperties conf) throws DorianInternalFault {
        this.db = db;
        this.conf = conf;
        this.passwordSecurityManager = new PasswordSecurityManager(db, conf.getPasswordSecurityPolicy());
    }


    public LocalUser authenticateAndVerifyUser(Credential credential, EventManager eventManager)
        throws DorianInternalFault, InvalidCredentialFault, CredentialNotSupportedFault {
        if (credential.getClass().equals(BasicAuthentication.class)) {
            BasicAuthentication cred = (BasicAuthentication) credential;
            try {
                LocalUser u = getUser(cred.getUserId());
                PasswordSecurity entry = this.passwordSecurityManager.getEntry(u.getUserId(), true);
                PasswordStatus status = entry.getPasswordStatus();
                String suppliedPassword = cred.getPassword();

                if (suppliedPassword.length() > SYSTEM_MAX_PASSWORD_LENGTH) {
                    InvalidCredentialFault fault = new InvalidCredentialFault();
                    fault.setFaultString(PASSWORD_ERROR_MESSAGE);
                    throw fault;
                }

                if (status.equals(PasswordStatus.Valid)) {
                    String digest = null;
                    boolean crypt = false;
                    if ((entry.getDigestAlgorithm() == null)
                        || (entry.getDigestAlgorithm().equals(PasswordSecurityManager.CRYPT_DIGEST_ALGORITHM))) {
                        crypt = true;
                        digest = Crypt.crypt(suppliedPassword);
                    } else if (entry.getDigestAlgorithm().equals(PasswordSecurityManager.PASSWORD_DIGEST_ALGORITHM)) {
                        try {
                            digest = PasswordSecurityManager.encrypt(suppliedPassword, entry.getDigestSalt());
                        } catch (Exception e) {
                            log.error(e);
                            DorianInternalFault fault = new DorianInternalFault();
                            fault.setFaultString("Unexpected error calculating password digest!!!");
                            throw fault;
                        }
                    } else {
                        DorianInternalFault fault = new DorianInternalFault();
                        fault.setFaultString("Could not obtain password digest, unknown digest algorithm ("
                            + entry.getDigestAlgorithm() + ")!!!");
                        throw fault;
                    }
                    if (!u.getPassword().equals(digest)) {
                        this.passwordSecurityManager.reportInvalidLoginAttempt(u.getUserId());
                        PasswordSecurity ps = this.passwordSecurityManager.getEntry(u.getUserId());
                        if (ps.getPasswordStatus().equals(PasswordStatus.Locked)) {
                            Date unlock = new Date(ps.getLockoutExpiration());
                            if (eventManager != null) {
                                eventManager.logEvent(u.getUserId(), AuditConstants.SYSTEM_ID,
                                    IdentityProviderAudit.LocalAccountLocked.getValue(),
                                    "Account locked because of to many consecutive invalid logins ("
                                        + conf.getPasswordSecurityPolicy().getConsecutiveInvalidLogins()
                                        + ").  The lock will expire on " + unlock.toString() + ".");
                            }
                        } else if (ps.getPasswordStatus().equals(PasswordStatus.LockedUntilChanged)) {
                            if (eventManager != null) {
                                eventManager
                                    .logEvent(
                                        u.getUserId(),
                                        AuditConstants.SYSTEM_ID,
                                        IdentityProviderAudit.LocalAccountLocked.getValue(),
                                        "Account locked because of to many total invalid logins ("
                                            + conf.getPasswordSecurityPolicy().getTotalInvalidLogins()
                                            + ").  The lock will not expire until the account password is reset by an administrator.");
                            }

                        }
                        InvalidCredentialFault fault = new InvalidCredentialFault();
                        fault.setFaultString(PASSWORD_ERROR_MESSAGE);
                        throw fault;
                    } else {
                        this.passwordSecurityManager.reportSuccessfulLoginAttempt(u.getUserId());
                        if (crypt) {
                            u.setPassword(suppliedPassword);
                            try {
                                updateUser(u);
                                if (eventManager != null) {
                                    eventManager.logEvent(u.getUserId(), AuditConstants.SYSTEM_ID,
                                        IdentityProviderAudit.LocalAccountUpdated.getValue(),
                                        "Password encryption algorithm updated from crypt to "
                                            + PasswordSecurityManager.PASSWORD_DIGEST_ALGORITHM + ".");
                                }
                            } catch (Exception e) {
                                log.error(e);
                                DorianInternalFault fault = new DorianInternalFault();
                                fault.setFaultString("Unexpected error upgrading password digest.");
                                throw fault;
                            }
                        }
                    }
                } else if (status.equals(PasswordStatus.LockedUntilChanged)) {
                    InvalidCredentialFault fault = new InvalidCredentialFault();
                    fault
                        .setFaultString("This account has been locked because the maximum number of invalid logins has been exceeded, please contact an administrator to have your password reset.");
                    throw fault;
                } else if (status.equals(PasswordStatus.Locked)) {
                    InvalidCredentialFault fault = new InvalidCredentialFault();
                    fault
                        .setFaultString("This account has been temporarily locked because the maximum number of consecutive invalid logins has been exceeded.");
                    throw fault;
                } else {
                    InvalidCredentialFault fault = new InvalidCredentialFault();
                    fault.setFaultString("Unexpected security status code received.");
                    throw fault;
                }
                try {
                    verifyUser(u);
                } catch (PermissionDeniedFault e) {
                    InvalidCredentialFault fault = new InvalidCredentialFault();
                    fault.setFaultString(e.getFaultString());
                    throw fault;
                }
                return u;
            } catch (NoSuchUserFault e) {
                InvalidCredentialFault fault = new InvalidCredentialFault();
                fault.setFaultString("User Id or password is incorrect");
                throw fault;
            } catch (DorianInternalFault e) {
                logError(e.getMessage(), e);
                DorianInternalFault fault = new DorianInternalFault();
                fault.setFaultString("An unexpected database error occurred.");
                FaultHelper helper = new FaultHelper(fault);
                helper.addFaultCause(e);
                fault = (DorianInternalFault) helper.getFault();
                throw fault;
            }
        } else {
            CredentialNotSupportedFault fault = new CredentialNotSupportedFault();
            fault.setFaultString("The credential provided is not supported.");
            throw fault;
        }
    }


    public void verifyUser(LocalUser u) throws DorianInternalFault, PermissionDeniedFault {

        if (!u.getStatus().equals(LocalUserStatus.Active)) {
            if (u.getStatus().equals(LocalUserStatus.Suspended)) {
                PermissionDeniedFault fault = new PermissionDeniedFault();
                fault.setFaultString("The account has been suspended.");
                throw fault;

            } else if (u.getStatus().equals(LocalUserStatus.Rejected)) {
                PermissionDeniedFault fault = new PermissionDeniedFault();
                fault.setFaultString("The application for the account was rejected.");
                throw fault;

            } else if (u.getStatus().equals(LocalUserStatus.Pending)) {
                PermissionDeniedFault fault = new PermissionDeniedFault();
                fault.setFaultString("The application for this account has not yet been reviewed.");
                throw fault;
            } else {
                PermissionDeniedFault fault = new PermissionDeniedFault();
                fault.setFaultString("Unknown Reason");
                throw fault;
            }
        }

    }


    private void validateSpecifiedField(String fieldName, String name) throws InvalidUserPropertyFault {
        try {
            AddressValidator.validateField(fieldName, name);
        } catch (Exception e) {
            InvalidUserPropertyFault fault = new InvalidUserPropertyFault();
            fault.setFaultString(e.getMessage());
            throw fault;
        }
    }


    private void validateEmail(String email) throws InvalidUserPropertyFault {
        try {
            AddressValidator.validateEmail(email);
        } catch (Exception e) {
            InvalidUserPropertyFault fault = new InvalidUserPropertyFault();
            fault.setFaultString(e.getMessage());
            throw fault;
        }
    }


    private void validatePassword(LocalUser user) throws DorianInternalFault, InvalidUserPropertyFault {
        String password = user.getPassword();
        if (password == null) {
            InvalidUserPropertyFault fault = new InvalidUserPropertyFault();
            fault.setFaultString("Unacceptable password, the length of the password must be between "
                + conf.getPasswordSecurityPolicy().getMinPasswordLength() + " and "
                + conf.getPasswordSecurityPolicy().getMaxPasswordLength() + " characters.");
            throw fault;
        } else if (password.length() > SYSTEM_MAX_PASSWORD_LENGTH) {
            InvalidUserPropertyFault fault = new InvalidUserPropertyFault();
            fault.setFaultString(SYSTEM_MAX_PASSWORD_ERROR_PREFIX + +SYSTEM_MAX_PASSWORD_LENGTH + ".");
            throw fault;
        } else if ((conf.getPasswordSecurityPolicy().getMinPasswordLength() > password.length())
            || (conf.getPasswordSecurityPolicy().getMaxPasswordLength() < password.length())) {
            InvalidUserPropertyFault fault = new InvalidUserPropertyFault();
            fault.setFaultString("Unacceptable password, the length of the password must be between "
                + conf.getPasswordSecurityPolicy().getMinPasswordLength() + " and "
                + conf.getPasswordSecurityPolicy().getMaxPasswordLength() + " characters.");
            throw fault;
        } else {
            boolean hasDictionaryWord = true;
            try {
                hasDictionaryWord = DictionaryCheck.doesStringContainDictionaryWord(password);
            } catch (IOException e) {
                logError(e.getMessage(), e);
                DorianInternalFault fault = new DorianInternalFault();
                fault
                    .setFaultString("Unexpected error validating the user's password, please contact an administrator.");
                throw fault;
            }
            boolean hasCapital = PasswordUtils.hasCapitalLetter(password);
            boolean hasLowerCase = PasswordUtils.hasLowerCaseLetter(password);
            boolean hasNumber = PasswordUtils.hasNumber(password);
            boolean hasSymbol = PasswordUtils.hasSymbol(password);
            if ((!hasCapital) || (!hasLowerCase) || (!hasNumber) || (!hasSymbol) || (hasDictionaryWord)) {
                InvalidUserPropertyFault fault = new InvalidUserPropertyFault();
                fault.setFaultString(INVALID_PASSWORD_MESSAGE);
                throw fault;
            }

        }

    }


    private void validateUserId(LocalUser user) throws InvalidUserPropertyFault {
        String uid = user.getUserId();
        if ((uid == null) || (conf.getMinUserIdLength() > uid.length()) || (conf.getMaxUserIdLength() < uid.length())) {
            InvalidUserPropertyFault fault = new InvalidUserPropertyFault();
            fault.setFaultString("Unacceptable User ID, the length of the user id must be between "
                + conf.getMinUserIdLength() + " and " + conf.getMaxUserIdLength() + " characters.");
            throw fault;
        }
    }


    private void validateUser(LocalUser user) throws DorianInternalFault, InvalidUserPropertyFault {
        validateUserId(user);
        validatePassword(user);
        validateSpecifiedField("First Name", user.getFirstName());
        validateSpecifiedField("Last Name", user.getLastName());
        validateSpecifiedField("Address", user.getAddress());
        validateSpecifiedField("City", user.getCity());
        validateSpecifiedField("Organization", user.getOrganization());
        validateSpecifiedField("Zip Code", user.getZipcode());
        validateSpecifiedField("Phone", user.getPhoneNumber());
        validateEmail(user.getEmail());
    }


    public synchronized void addUser(LocalUser user) throws DorianInternalFault, InvalidUserPropertyFault {
        this.buildDatabase();
        this.validateUser(user);
        if (userExists(user.getUserId())) {
            InvalidUserPropertyFault fault = new InvalidUserPropertyFault();
            fault.setFaultString("The user " + user.getUserId() + " already exists.");
            throw fault;
        }
        Connection c = null;
        try {
            String passwordSalt = PasswordSecurityManager.getRandomSalt();
            String passwordDigest = PasswordSecurityManager.encrypt(user.getPassword(), passwordSalt);
            c = db.getConnection();
            PreparedStatement ps = c
                .prepareStatement("INSERT INTO "
                    + IDP_USERS_TABLE
                    + " SET UID = ?, EMAIL= ?, PASSWORD= ?, FIRST_NAME= ?, LAST_NAME= ?, ORGANIZATION= ?, ADDRESS= ?, ADDRESS2= ?,CITY= ?, STATE= ?, ZIP_CODE= ?, COUNTRY= ?, PHONE_NUMBER= ?, STATUS= ?, ROLE= ?");
            ps.setString(1, user.getUserId());
            ps.setString(2, user.getEmail());
            ps.setString(3, passwordDigest);
            ps.setString(4, user.getFirstName());
            ps.setString(5, user.getLastName());
            ps.setString(6, user.getOrganization());
            ps.setString(7, user.getAddress());
            if (Utils.clean(user.getAddress2()) == null) {
                ps.setString(8, "");
            } else {
                ps.setString(8, user.getAddress2());
            }
            ps.setString(9, user.getCity());
            ps.setString(10, user.getState().getValue());
            ps.setString(11, user.getZipcode());
            ps.setString(12, user.getCountry().getValue());
            ps.setString(13, user.getPhoneNumber());
            ps.setString(14, user.getStatus().getValue());
            ps.setString(15, user.getRole().getValue());
            ps.executeUpdate();
            ps.close();
            this.passwordSecurityManager.resetEntry(user.getUserId(), passwordSalt);
            user.setPasswordSecurity(this.passwordSecurityManager.getEntry(user.getUserId(), true));
        } catch (Exception e) {

            try {
                this.removeUser(user.getUserId());
            } catch (Exception ex) {

            }
            try {
                this.passwordSecurityManager.deleteEntry(user.getUserId());
            } catch (Exception ex) {

            }
            logError(e.getMessage(), e);
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("Unexpected Error, Could not add user!!!");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        } finally {
            db.releaseConnection(c);
        }
    }


    public synchronized void removeUser(String uid) throws DorianInternalFault {
        this.buildDatabase();
        Connection c = null;
        try {
            c = db.getConnection();
            PreparedStatement ps = c.prepareStatement("DELETE FROM " + IDP_USERS_TABLE + " WHERE UID= ?");
            ps.setString(1, uid);
            ps.executeUpdate();
            ps.close();
            this.passwordSecurityManager.deleteEntry(uid);
        } catch (Exception e) {
            logError(e.getMessage(), e);
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("Unexpected Error, Could not delete user!!!");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        } finally {
            db.releaseConnection(c);
        }
    }


    public LocalUser[] getUsers(LocalUserFilter filter) throws DorianInternalFault {
        return getUsers(filter, true);
    }


    public LocalUser[] getUsers(LocalUserFilter filter, boolean includePassword) throws DorianInternalFault {

        this.buildDatabase();
        Connection c = null;
        List<LocalUser> users = new ArrayList<LocalUser>();
        try {
            c = db.getConnection();
            PreparedStatement ps = null;
            if (filter != null) {
                ps = c
                    .prepareStatement("select * from "
                        + IDP_USERS_TABLE
                        + " WHERE UID LIKE ? AND EMAIL LIKE ? AND FIRST_NAME LIKE ? AND LAST_NAME LIKE ? AND ORGANIZATION LIKE ? AND ADDRESS LIKE ? AND ADDRESS2 LIKE ? AND CITY LIKE ? AND STATE LIKE ? AND ZIP_CODE LIKE ? AND COUNTRY LIKE ? AND PHONE_NUMBER LIKE ? AND STATUS LIKE ? AND ROLE LIKE ?");

                if (filter.getUserId() != null) {
                    ps.setString(1, "%" + filter.getUserId() + "%");
                } else {
                    ps.setString(1, "%");
                }

                if (filter.getEmail() != null) {
                    ps.setString(2, "%" + filter.getEmail() + "%");
                } else {
                    ps.setString(2, "%");
                }

                if (filter.getFirstName() != null) {
                    ps.setString(3, "%" + filter.getFirstName() + "%");
                } else {
                    ps.setString(3, "%");
                }

                if (filter.getLastName() != null) {
                    ps.setString(4, "%" + filter.getLastName() + "%");
                } else {
                    ps.setString(4, "%");
                }

                if (filter.getOrganization() != null) {
                    ps.setString(5, "%" + filter.getOrganization() + "%");
                } else {
                    ps.setString(5, "%");
                }

                if (filter.getAddress() != null) {
                    ps.setString(6, "%" + filter.getAddress() + "%");
                } else {
                    ps.setString(6, "%");
                }

                if (filter.getAddress2() != null) {
                    ps.setString(7, "%" + filter.getAddress2() + "%");
                } else {
                    ps.setString(7, "%");
                }

                if (filter.getCity() != null) {
                    ps.setString(8, "%" + filter.getCity() + "%");
                } else {
                    ps.setString(8, "%");
                }

                if (filter.getState() != null) {
                    ps.setString(9, "%" + filter.getState() + "%");
                } else {
                    ps.setString(9, "%");
                }

                if (filter.getZipcode() != null) {
                    ps.setString(10, "%" + filter.getZipcode() + "%");
                } else {
                    ps.setString(10, "%");
                }

                if (filter.getCountry() != null) {
                    ps.setString(11, "%" + filter.getCountry() + "%");
                } else {
                    ps.setString(11, "%");
                }

                if (filter.getPhoneNumber() != null) {
                    ps.setString(12, "%" + filter.getPhoneNumber() + "%");
                } else {
                    ps.setString(12, "%");
                }

                if (filter.getStatus() != null) {
                    ps.setString(13, filter.getStatus().getValue());
                } else {
                    ps.setString(13, "%");
                }

                if (filter.getRole() != null) {
                    ps.setString(14, filter.getRole().getValue());
                } else {
                    ps.setString(14, "%");
                }
            } else {
                ps = c.prepareStatement("select * from " + IDP_USERS_TABLE);
            }
            // System.out.println(ps.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                LocalUser user = new LocalUser();
                user.setUserId(rs.getString("UID"));
                user.setEmail(rs.getString("EMAIL"));
                if (includePassword) {
                    user.setPassword(rs.getString("PASSWORD"));
                }
                user.setFirstName(rs.getString("FIRST_NAME"));
                user.setLastName(rs.getString("LAST_NAME"));
                user.setOrganization(rs.getString("ORGANIZATION"));
                user.setAddress(rs.getString("ADDRESS"));
                user.setAddress2(rs.getString("ADDRESS2"));
                user.setCity(rs.getString("CITY"));
                user.setState(StateCode.fromValue(rs.getString("STATE")));
                user.setZipcode(rs.getString("ZIP_CODE"));
                user.setCountry(CountryCode.fromValue(rs.getString("COUNTRY")));
                user.setPhoneNumber(rs.getString("PHONE_NUMBER"));
                user.setStatus(LocalUserStatus.fromValue(rs.getString("STATUS")));
                user.setRole(LocalUserRole.fromValue(rs.getString("ROLE")));
                if (includePassword) {
                    user.setPasswordSecurity(this.passwordSecurityManager.getEntry(user.getUserId(), true));
                } else {
                    user.setPasswordSecurity(this.passwordSecurityManager.getEntry(user.getUserId(), false));
                }
                users.add(user);
            }
            rs.close();
            ps.close();

            LocalUser[] list = new LocalUser[users.size()];
            for (int i = 0; i < list.length; i++) {
                list[i] = (LocalUser) users.get(i);
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


    public LocalUser getUser(String uid) throws DorianInternalFault, NoSuchUserFault {
        return this.getUser(uid, true);
    }


    public LocalUser getUser(String uid, boolean includePassword) throws DorianInternalFault, NoSuchUserFault {
        this.buildDatabase();
        LocalUser user = new LocalUser();
        Connection c = null;
        try {
            c = db.getConnection();
            PreparedStatement s = c.prepareStatement("select * from " + IDP_USERS_TABLE + " where UID= ?");
            s.setString(1, uid);
            ResultSet rs = s.executeQuery();
            if (rs.next()) {
                user.setUserId(uid);
                user.setEmail(rs.getString("EMAIL"));
                if (includePassword) {
                    user.setPassword(rs.getString("PASSWORD"));
                }
                user.setFirstName(rs.getString("FIRST_NAME"));
                user.setLastName(rs.getString("LAST_NAME"));
                user.setOrganization(rs.getString("ORGANIZATION"));
                user.setAddress(rs.getString("ADDRESS"));
                user.setAddress2(rs.getString("ADDRESS2"));
                user.setCity(rs.getString("CITY"));
                user.setState(StateCode.fromValue(rs.getString("STATE")));
                user.setZipcode(rs.getString("ZIP_CODE"));
                user.setCountry(CountryCode.fromValue(rs.getString("COUNTRY")));
                user.setPhoneNumber(rs.getString("PHONE_NUMBER"));
                user.setStatus(LocalUserStatus.fromValue(rs.getString("STATUS")));
                user.setRole(LocalUserRole.fromValue(rs.getString("ROLE")));
                if (includePassword) {
                    user.setPasswordSecurity(this.passwordSecurityManager.getEntry(uid, true));
                } else {
                    user.setPasswordSecurity(this.passwordSecurityManager.getEntry(uid, false));
                }
            } else {
                NoSuchUserFault fault = new NoSuchUserFault();
                fault.setFaultString("The user " + uid + " does not exist.");
                throw fault;
            }
            rs.close();
            s.close();

        } catch (NoSuchUserFault f) {
            throw f;
        } catch (Exception e) {

            logError(e.getMessage(), e);
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("Unexpected Error, could not obtain the user " + uid + ".");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        } finally {
            db.releaseConnection(c);
        }
        return user;
    }


    private void buildDatabase() throws DorianInternalFault {
        if (!dbBuilt) {
            try {
                if (!this.db.tableExists(IDP_USERS_TABLE)) {
                    String applications = "CREATE TABLE " + IDP_USERS_TABLE + " ("
                        + "UID VARCHAR(255) NOT NULL PRIMARY KEY," + "EMAIL VARCHAR(255) NOT NULL,"
                        + "PASSWORD VARCHAR(255) NOT NULL," + "FIRST_NAME VARCHAR(255) NOT NULL,"
                        + "LAST_NAME VARCHAR(255) NOT NULL," + "ORGANIZATION VARCHAR(255) NOT NULL,"
                        + "ADDRESS VARCHAR(255) NOT NULL," + "ADDRESS2 VARCHAR(255)," + "CITY VARCHAR(255) NOT NULL,"
                        + "STATE VARCHAR(20) NOT NULL," + "ZIP_CODE VARCHAR(20) NOT NULL,"
                        + "COUNTRY VARCHAR(2) NOT NULL," + "PHONE_NUMBER VARCHAR(20) NOT NULL,"
                        + "STATUS VARCHAR(20) NOT NULL," + "ROLE VARCHAR(20) NOT NULL,"
                        + "INDEX document_index (EMAIL));";
                    db.update(applications);
                    try {
                        LocalUser u = new LocalUser();
                        u.setUserId(ADMIN_USER_ID);
                        u.setPassword(ADMIN_PASSWORD);
                        u.setEmail("dorian@dorian.org");
                        u.setFirstName("Mr.");
                        u.setLastName("Administrator");
                        u.setOrganization("caBIG");
                        u.setAddress("3184 Graves Hall");
                        u.setAddress2("333 W. Tenth Avenue");
                        u.setCity("Columbus");
                        u.setState(StateCode.OH);
                        u.setZipcode("43210");
                        u.setCountry(CountryCode.US);
                        u.setPhoneNumber("555-555-5555");
                        u.setStatus(LocalUserStatus.Active);
                        u.setRole(LocalUserRole.Administrator);
                        this.addUser(u);
                    } catch (Exception e) {
                        logError(e.getMessage(), e);
                        DorianInternalFault fault = new DorianInternalFault();
                        fault.setFaultString("Unexpected Error, Could not add initial IdP user!!!");
                        FaultHelper helper = new FaultHelper(fault);
                        helper.addFaultCause(e);
                        fault = (DorianInternalFault) helper.getFault();
                        throw fault;
                    }
                }
                this.dbBuilt = true;

            } catch (DorianInternalFault e) {
                throw e;
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


    public synchronized void updateUser(LocalUser u) throws DorianInternalFault, NoSuchUserFault,
        InvalidUserPropertyFault {
        this.buildDatabase();
        if (u.getUserId() == null) {
            NoSuchUserFault fault = new NoSuchUserFault();
            fault.setFaultString("Could not update user, the user " + u.getUserId() + " does not exist.");
            throw fault;
        } else if (userExists(u.getUserId())) {
            String passwordSalt = null;
            StringBuffer sb = new StringBuffer();
            sb.append("update " + IDP_USERS_TABLE + " SET ");
            int changes = 0;
            LocalUser curr = this.getUser(u.getUserId());
            boolean passwordChanged = false;
            if (u.getPassword() != null) {
                String newPasswordDigest = null;
                String existingDigestAlgorithm = curr.getPasswordSecurity().getDigestAlgorithm();
                if ((existingDigestAlgorithm == null)
                    || (existingDigestAlgorithm.equals(PasswordSecurityManager.CRYPT_DIGEST_ALGORITHM))) {
                    newPasswordDigest = Crypt.crypt(u.getPassword());
                } else if (existingDigestAlgorithm.equals(PasswordSecurityManager.PASSWORD_DIGEST_ALGORITHM)) {
                    try {
                        newPasswordDigest = PasswordSecurityManager.encrypt(u.getPassword(), curr.getPasswordSecurity()
                            .getDigestSalt());
                    } catch (Exception e) {
                        log.error(e);
                        DorianInternalFault fault = new DorianInternalFault();
                        fault.setFaultString("Unexpected error calculating password digest!!!");
                        throw fault;
                    }
                } else {
                    DorianInternalFault fault = new DorianInternalFault();
                    fault.setFaultString("Could not obtain password digest, unknown digest algorithm ("
                        + existingDigestAlgorithm + ")!!!");
                    throw fault;
                }

                if (!newPasswordDigest.equals(curr.getPassword())) {
                    validatePassword(u);
                    String newPass = null;
                    try {
                        passwordSalt = PasswordSecurityManager.getRandomSalt();
                        newPass = PasswordSecurityManager.encrypt(u.getPassword(), passwordSalt);
                    } catch (Exception e) {
                        log.error(e);
                        DorianInternalFault fault = new DorianInternalFault();
                        fault
                            .setFaultString("Could not update user, unexpected error calculating the password digest.");
                        throw fault;
                    }
                    curr.setPassword(newPass);
                    passwordChanged = true;
                }
            }

            if ((u.getEmail() != null) && (!u.getEmail().equals(curr.getEmail()))) {
                validateEmail(u.getEmail());
                curr.setEmail(u.getEmail());
            }

            if ((u.getFirstName() != null) && (!u.getFirstName().equals(curr.getFirstName()))) {
                validateSpecifiedField("First Name", u.getFirstName());
                curr.setFirstName(u.getFirstName());
            }

            if ((u.getLastName() != null) && (!u.getLastName().equals(curr.getLastName()))) {
                validateSpecifiedField("Last Name", u.getLastName());
                curr.setLastName(u.getLastName());
            }

            if ((u.getOrganization() != null) && (!u.getOrganization().equals(curr.getOrganization()))) {
                validateSpecifiedField("Organization", u.getOrganization());
                curr.setOrganization(u.getOrganization());
            }

            if ((u.getAddress() != null) && (!u.getAddress().equals(curr.getAddress()))) {
                validateSpecifiedField("Address", u.getAddress());
                curr.setAddress(u.getAddress());
            }

            if ((u.getAddress2() != null) && (!u.getAddress2().equals(curr.getAddress2()))) {
                curr.setAddress2(u.getAddress2());
            }

            if ((u.getCity() != null) && (!u.getCity().equals(curr.getCity()))) {
                validateSpecifiedField("City", u.getCity());
                curr.setCity(u.getCity());
            }

            if ((u.getState() != null) && (!u.getState().equals(curr.getState()))) {
                curr.setState(u.getState());
            }

            if ((u.getCountry() != null) && (!u.getCountry().equals(curr.getCountry()))) {
                curr.setCountry(u.getCountry());
            }

            if ((u.getZipcode() != null) && (!u.getZipcode().equals(curr.getZipcode()))) {

                validateSpecifiedField("Zip Code", u.getZipcode());
                curr.setZipcode(u.getZipcode());
            }

            if ((u.getPhoneNumber() != null) && (!u.getPhoneNumber().equals(curr.getPhoneNumber()))) {
                validateSpecifiedField("Phone Number", u.getPhoneNumber());
                curr.setPhoneNumber(u.getPhoneNumber());
            }

            if ((u.getStatus() != null) && (!u.getStatus().equals(curr.getStatus()))) {
                if (accountCreated(curr.getStatus()) && !accountCreated(u.getStatus())) {
                    InvalidUserPropertyFault fault = new InvalidUserPropertyFault();
                    fault.setFaultString("Error, cannot change " + u.getUserId()
                        + "'s status from a post-created account status (" + curr.getStatus()
                        + ") to a pre-created account status (" + u.getStatus() + ").");
                    throw fault;
                }

                curr.setStatus(u.getStatus());
            }

            if ((u.getRole() != null) && (!u.getRole().equals(curr.getRole()))) {
                if (changes > 0) {
                    sb.append(",");
                }
                curr.setRole(u.getRole());
            }

            Connection c = null;
            try {
                c = db.getConnection();
                PreparedStatement ps = c
                    .prepareStatement("UPDATE "
                        + IDP_USERS_TABLE
                        + " SET UID = ?, EMAIL= ?, PASSWORD= ?, FIRST_NAME= ?, LAST_NAME= ?, ORGANIZATION= ?, ADDRESS= ?, ADDRESS2= ?,CITY= ?, STATE= ?, ZIP_CODE= ?, COUNTRY= ?, PHONE_NUMBER= ?, STATUS= ?, ROLE= ? WHERE UID = ?");
                ps.setString(1, curr.getUserId());
                ps.setString(2, curr.getEmail());
                ps.setString(3, curr.getPassword());
                ps.setString(4, curr.getFirstName());
                ps.setString(5, curr.getLastName());
                ps.setString(6, curr.getOrganization());
                ps.setString(7, curr.getAddress());
                ps.setString(8, curr.getAddress2());
                ps.setString(9, curr.getCity());
                ps.setString(10, curr.getState().getValue());
                ps.setString(11, curr.getZipcode());
                ps.setString(12, curr.getCountry().getValue());
                ps.setString(13, curr.getPhoneNumber());
                ps.setString(14, curr.getStatus().getValue());
                ps.setString(15, curr.getRole().getValue());
                ps.setString(16, curr.getUserId());
                ps.executeUpdate();
                ps.close();

                if (passwordChanged) {
                    this.passwordSecurityManager.resetEntry(curr.getUserId(), passwordSalt);
                    u.setPasswordSecurity(this.passwordSecurityManager.getEntry(curr.getUserId(), false));
                }
            } catch (Exception e) {
                logError(e.getMessage(), e);
                DorianInternalFault fault = new DorianInternalFault();
                fault.setFaultString("Unexpected Error, Could not update user!!!");
                FaultHelper helper = new FaultHelper(fault);
                helper.addFaultCause(e);
                fault = (DorianInternalFault) helper.getFault();
                throw fault;
            } finally {
                db.releaseConnection(c);
            }

        } else {
            NoSuchUserFault fault = new NoSuchUserFault();
            fault.setFaultString("Could not update user, the user " + u.getUserId() + " does not exist.");
            throw fault;
        }
    }


    private boolean accountCreated(LocalUserStatus status) {
        if (status.equals(LocalUserStatus.Suspended)) {
            return true;
        } else if (status.equals(LocalUserStatus.Active)) {
            return true;
        } else {
            return false;
        }
    }


    public boolean userExists(String uid) throws DorianInternalFault {
        this.buildDatabase();
        Connection c = null;
        boolean exists = false;
        try {
            c = db.getConnection();
            PreparedStatement s = c.prepareStatement("select count(*) from " + IDP_USERS_TABLE + " where UID= ?");
            s.setString(1, uid);
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
            fault.setFaultString("Unexpected Database Error, could not determine if the user " + uid + " exists.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        } finally {
            db.releaseConnection(c);
        }
        return exists;
    }


    public void clearDatabase() throws DorianInternalFault {
        this.buildDatabase();
        try {
            db.update("drop TABLE " + IDP_USERS_TABLE);
            this.passwordSecurityManager.clearDatabase();
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


    protected PasswordSecurityManager getPasswordSecurityManager() {
        return passwordSecurityManager;
    }
}