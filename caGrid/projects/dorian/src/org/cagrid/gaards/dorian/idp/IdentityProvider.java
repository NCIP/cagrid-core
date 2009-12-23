package org.cagrid.gaards.dorian.idp;

import gov.nih.nci.cagrid.common.FaultHelper;
import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.opensaml.SAMLAssertion;

import java.rmi.RemoteException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.cagrid.gaards.authentication.BasicAuthentication;
import org.cagrid.gaards.authentication.Credential;
import org.cagrid.gaards.authentication.faults.AuthenticationProviderFault;
import org.cagrid.gaards.authentication.faults.CredentialNotSupportedFault;
import org.cagrid.gaards.authentication.faults.InvalidCredentialFault;
import org.cagrid.gaards.dorian.ca.CertificateAuthority;
import org.cagrid.gaards.dorian.common.AuditConstants;
import org.cagrid.gaards.dorian.common.LoggingObject;
import org.cagrid.gaards.dorian.federation.FederationAudit;
import org.cagrid.gaards.dorian.policy.AccountInformationModificationPolicy;
import org.cagrid.gaards.dorian.policy.IdentityProviderPolicy;
import org.cagrid.gaards.dorian.policy.PasswordLockoutPolicy;
import org.cagrid.gaards.dorian.policy.PasswordPolicy;
import org.cagrid.gaards.dorian.policy.UserIdPolicy;
import org.cagrid.gaards.dorian.stubs.types.DorianInternalFault;
import org.cagrid.gaards.dorian.stubs.types.InvalidUserPropertyFault;
import org.cagrid.gaards.dorian.stubs.types.NoSuchUserFault;
import org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault;
import org.cagrid.tools.database.Database;
import org.cagrid.tools.events.Event;
import org.cagrid.tools.events.EventAuditor;
import org.cagrid.tools.events.EventManager;
import org.cagrid.tools.events.EventToHandlerMapping;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */

public class IdentityProvider extends LoggingObject {

    private UserManager userManager;

    private AssertionCredentialsManager assertionManager;

    private IdPRegistrationPolicy registrationPolicy;

    private EventManager eventManager;

    private EventAuditor identityProviderAuditor;

    private Database db;

    private IdentityProviderProperties conf;


    public IdentityProvider(IdentityProviderProperties conf, Database db, CertificateAuthority ca,
        EventManager eventManager) throws DorianInternalFault {
        this.conf = conf;
        this.eventManager = eventManager;
        this.db = db;
        try {
            initializeEventManager();
            this.registrationPolicy = conf.getRegistrationPolicy();
            this.userManager = new UserManager(db, conf);
            this.assertionManager = new AssertionCredentialsManager(conf, ca, db);
        } catch (Exception e) {
            logError(e.getMessage(), e);
            String message = "Error initializing the Identity Manager Provider.";
            this.eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                FederationAudit.InternalError.getValue(), message + "\n\n" + FaultUtil.printFaultToString(e));
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString(message);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        }
    }


    private void initializeEventManager() throws DorianInternalFault {
        try {
            if (this.eventManager.isHandlerRegistered(AuditingConstants.IDENTITY_PROVIDER_AUDITOR)) {
                this.identityProviderAuditor = (EventAuditor) this.eventManager
                    .getEventHandler(AuditingConstants.IDENTITY_PROVIDER_AUDITOR);
            } else {
                this.identityProviderAuditor = new EventAuditor(AuditingConstants.IDENTITY_PROVIDER_AUDITOR, this.db,
                    AuditingConstants.IDENTITY_PROVIDER_AUDITOR_DB);
                this.eventManager.registerHandler(this.identityProviderAuditor);
            }

            this.eventManager.registerEventWithHandler(new EventToHandlerMapping(
                IdentityProviderAudit.LocalAccountLocked.getValue(), AuditingConstants.IDENTITY_PROVIDER_AUDITOR));

            this.eventManager.registerEventWithHandler(new EventToHandlerMapping(
                IdentityProviderAudit.LocalAccountRemoved.getValue(), AuditingConstants.IDENTITY_PROVIDER_AUDITOR));
            this.eventManager.registerEventWithHandler(new EventToHandlerMapping(
                IdentityProviderAudit.LocalAccountUpdated.getValue(), AuditingConstants.IDENTITY_PROVIDER_AUDITOR));

            this.eventManager.registerEventWithHandler(new EventToHandlerMapping(IdentityProviderAudit.InvalidLogin
                .getValue(), AuditingConstants.IDENTITY_PROVIDER_AUDITOR));
            this.eventManager.registerEventWithHandler(new EventToHandlerMapping(IdentityProviderAudit.PasswordChanged
                .getValue(), AuditingConstants.IDENTITY_PROVIDER_AUDITOR));
            this.eventManager.registerEventWithHandler(new EventToHandlerMapping(
                IdentityProviderAudit.LocalAccessDenied.getValue(), AuditingConstants.IDENTITY_PROVIDER_AUDITOR));
            this.eventManager.registerEventWithHandler(new EventToHandlerMapping(IdentityProviderAudit.Registration
                .getValue(), AuditingConstants.IDENTITY_PROVIDER_AUDITOR));
            this.eventManager.registerEventWithHandler(new EventToHandlerMapping(IdentityProviderAudit.SuccessfulLogin
                .getValue(), AuditingConstants.IDENTITY_PROVIDER_AUDITOR));
        } catch (Exception e) {
            logError(Utils.getExceptionMessage(e), e);
            String mess = "An unexpected error occurred initializing the auditing system:\n"
                + Utils.getExceptionMessage(e);
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString(mess);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        }
    }


    public SAMLAssertion authenticate(Credential credential) throws AuthenticationProviderFault,
        InvalidCredentialFault, CredentialNotSupportedFault {
        String uid = "UNKNOWN";
        if (credential.getClass().equals(BasicAuthentication.class)) {
            BasicAuthentication cred = (BasicAuthentication) credential;
            uid = cred.getUserId();
        }
        try {
            LocalUser requestor = userManager.authenticateAndVerifyUser(credential, this.eventManager);

            SAMLAssertion saml = assertionManager.getAuthenticationAssertion(requestor.getUserId(), requestor
                .getFirstName(), requestor.getLastName(), requestor.getEmail());
            this.eventManager.logEvent(requestor.getUserId(), AuditConstants.SYSTEM_ID,
                IdentityProviderAudit.SuccessfulLogin.getValue(), "Successful login using "
                    + credential.getClass().getName() + ".");
            return saml;
        } catch (InvalidCredentialFault e) {
            this.eventManager.logEvent(uid, AuditConstants.SYSTEM_ID, IdentityProviderAudit.InvalidLogin.getValue(),
                "Authentication Failed:\n\n" + FaultUtil.printFaultToString(e));
            throw e;
        } catch (CredentialNotSupportedFault e) {
            this.eventManager.logEvent(uid, AuditConstants.SYSTEM_ID, IdentityProviderAudit.InvalidLogin.getValue(),
                "Authentication Failed:\n\n" + FaultUtil.printFaultToString(e));
            throw e;
        } catch (DorianInternalFault e) {
            logError(e.getMessage(), e);
            String message = "An unexpected error occurred while trying to authenticate.";
            this.eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                FederationAudit.InternalError.getValue(), message + "\n\n" + FaultUtil.printFaultToString(e));
            AuthenticationProviderFault fault = new AuthenticationProviderFault();
            fault.setFaultString(message);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (AuthenticationProviderFault) helper.getFault();
            throw fault;
        }
    }


    public void changePassword(BasicAuthentication credential, String newPassword) throws DorianInternalFault,
        PermissionDeniedFault, InvalidUserPropertyFault {
        try {
            LocalUser requestor = userManager.authenticateAndVerifyUser(credential, this.eventManager);

            try {
                String newPasswordDigest = PasswordSecurityManager.encrypt(newPassword, requestor.getPasswordSecurity()
                    .getDigestSalt());
                if (newPasswordDigest.equals(requestor.getPassword())) {
                    InvalidUserPropertyFault fault = new InvalidUserPropertyFault();
                    fault
                        .setFaultString("Unacceptable password specified, cannot change password to be the same as the current password.");
                    throw fault;
                }
            } catch (InvalidUserPropertyFault e) {
                throw e;
            } catch (Exception e) {
                logError(e.getMessage(), e);
                String message = "Could not changed password and unexpected error occurred calculating the password digest";
                this.eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                    FederationAudit.InternalError.getValue(), message + "\n\n" + FaultUtil.printFaultToString(e));
                DorianInternalFault fault = new DorianInternalFault();
                fault.setFaultString(message);
                throw fault;
            }

            try {
                requestor.setPassword(newPassword);
                this.userManager.updateUser(requestor);
                this.eventManager.logEvent(requestor.getUserId(), AuditConstants.SYSTEM_ID,
                    IdentityProviderAudit.PasswordChanged.getValue(), "Password changed by user.");
            } catch (NoSuchUserFault e) {
                logError(e.getMessage(), e);
                String message = "An unexpected error occurred in trying to changing the password for the user "
                    + requestor.getUserId() + ":";
                this.eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                    FederationAudit.InternalError.getValue(), message + "\n\n" + FaultUtil.printFaultToString(e));
                DorianInternalFault fault = new DorianInternalFault();
                fault.setFaultString(message);
                FaultHelper helper = new FaultHelper(fault);
                helper.addFaultCause(e);
                fault = (DorianInternalFault) helper.getFault();
                throw fault;
            }
        } catch (CredentialNotSupportedFault e) {
            String message = "Permission to change the " + credential.getUserId() + " password was denied.";
            this.eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                IdentityProviderAudit.LocalAccessDenied.getValue(), message + "\n\n" + FaultUtil.printFaultToString(e));
            PermissionDeniedFault fault = new PermissionDeniedFault();
            fault.setFaultString(e.getFaultString());
            throw fault;
        } catch (InvalidCredentialFault e) {
            String message = "Permission to change the " + credential.getUserId() + " password was denied.";
            this.eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                IdentityProviderAudit.LocalAccessDenied.getValue(), message + "\n\n" + FaultUtil.printFaultToString(e));

            PermissionDeniedFault fault = new PermissionDeniedFault();
            fault.setFaultString(e.getFaultString());
            throw fault;
        }
    }


    public X509Certificate getIdPCertificate() throws DorianInternalFault {
        return assertionManager.getIdPCertificate();
    }


    public String register(Application a) throws DorianInternalFault, InvalidUserPropertyFault {
        try {
            ApplicationReview ar = this.registrationPolicy.register(a);
            LocalUserStatus status = ar.getStatus();
            LocalUserRole role = ar.getRole();
            String message = ar.getMessage();
            if (status == null) {
                status = LocalUserStatus.Pending;
            }

            if (role == null) {
                role = LocalUserRole.Non_Administrator;
            }
            if (message == null) {
                message = "None";
            }

            LocalUser u = new LocalUser();
            u.setUserId(a.getUserId());
            u.setEmail(a.getEmail());
            u.setPassword(a.getPassword());
            u.setFirstName(a.getFirstName());
            u.setLastName(a.getLastName());
            u.setOrganization(a.getOrganization());
            u.setAddress(a.getAddress());
            u.setAddress2(a.getAddress2());
            u.setCity(a.getCity());
            u.setState(a.getState());
            u.setZipcode(a.getZipcode());
            u.setCountry(a.getCountry());
            u.setPhoneNumber(a.getPhoneNumber());
            u.setRole(role);
            u.setStatus(status);
            userManager.addUser(u);
            this.eventManager.logEvent(u.getUserId(), AuditConstants.SYSTEM_ID, IdentityProviderAudit.Registration
                .getValue(), "User registered with the initial satus: " + status.getValue());
            return message;
        } catch (DorianInternalFault e) {
            String message = "An unexpected error occurred while trying to register the user " + a.getUserId() + ":";
            this.eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                FederationAudit.InternalError.getValue(), message + "\n\n" + FaultUtil.printFaultToString(e));
            throw e;
        }
    }


    public LocalUser getUser(String requestorUID, String uid) throws DorianInternalFault, PermissionDeniedFault,
        NoSuchUserFault {
        try {
            LocalUser requestor = verifyUser(requestorUID);
            verifyAdministrator(requestor);
            return this.userManager.getUser(uid);
        } catch (DorianInternalFault e) {
            String message = "An unexpected error occurred while trying to obtain the user " + uid + ":";
            this.eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                FederationAudit.InternalError.getValue(), message + "\n\n" + FaultUtil.printFaultToString(e));
            throw e;
        } catch (PermissionDeniedFault e) {
            String message = "Permission to obtain information on the  user, " + uid + " was denied:";
            this.eventManager.logEvent(requestorUID, AuditConstants.SYSTEM_ID, IdentityProviderAudit.LocalAccessDenied
                .getValue(), message + "\n\n" + Utils.getExceptionMessage(e));
            throw e;
        }
    }


    public LocalUser[] findUsers(String requestorUID, LocalUserFilter filter) throws DorianInternalFault,
        PermissionDeniedFault {
        try {
            LocalUser requestor = verifyUser(requestorUID);
            verifyAdministrator(requestor);
            return this.userManager.getUsers(filter, false);
        } catch (DorianInternalFault e) {
            String message = "An unexpected error occurred while trying to search for users.";
            this.eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                FederationAudit.InternalError.getValue(), message + "\n\n" + FaultUtil.printFaultToString(e));
            throw e;
        } catch (PermissionDeniedFault e) {
            String message = "Permission to search for users was denied.";
            this.eventManager.logEvent(requestorUID, AuditConstants.SYSTEM_ID, IdentityProviderAudit.LocalAccessDenied
                .getValue(), message + "\n\n" + Utils.getExceptionMessage(e));
            throw e;
        }
    }


    public void updateUser(String requestorUID, LocalUser u) throws DorianInternalFault, PermissionDeniedFault,
        NoSuchUserFault, InvalidUserPropertyFault {
        try {
            LocalUser requestor = verifyUser(requestorUID);
            verifyAdministrator(requestor);
            LocalUser beforeUpdate = this.userManager.getUser(u.getUserId());
            this.userManager.updateUser(u);
            if (u.getPassword() != null) {
                this.eventManager.logEvent(u.getUserId(), requestorUID, IdentityProviderAudit.PasswordChanged
                    .getValue(), "Password changed by " + u.getUserId() + ".");
            }
            this.eventManager.logEvent(u.getUserId(), requestorUID, IdentityProviderAudit.LocalAccountUpdated
                .getValue(), ReportUtils.generateReport(beforeUpdate, this.userManager.getUser(u.getUserId())));
        } catch (DorianInternalFault e) {
            String message = "An unexpected error occurred while trying to update the user " + u.getUserId() + ": ";
            this.eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                FederationAudit.InternalError.getValue(), message + "\n\n" + FaultUtil.printFaultToString(e));
            throw e;
        } catch (PermissionDeniedFault e) {
            String message = "Permission to update the user " + u.getUserId() + " was denied.";
            this.eventManager.logEvent(requestorUID, AuditConstants.SYSTEM_ID, IdentityProviderAudit.LocalAccessDenied
                .getValue(), message + "\n\n" + Utils.getExceptionMessage(e));
            throw e;
        }
    }


    private void verifyAdministrator(LocalUser u) throws PermissionDeniedFault {
        if (!u.getRole().equals(LocalUserRole.Administrator)) {
            PermissionDeniedFault fault = new PermissionDeniedFault();
            fault.setFaultString("You are NOT an administrator.");
            throw fault;
        }
    }


    private LocalUser verifyUser(String uid) throws DorianInternalFault, PermissionDeniedFault {
        try {
            LocalUser u = this.userManager.getUser(uid);
            userManager.verifyUser(u);
            return u;
        } catch (NoSuchUserFault e) {
            PermissionDeniedFault fault = new PermissionDeniedFault();
            fault.setFaultString("Invalid User!!!");
            throw fault;
        }
    }


    public void removeUser(String requestorUID, String userId) throws DorianInternalFault, PermissionDeniedFault {
        try {
            LocalUser requestor = verifyUser(requestorUID);
            verifyAdministrator(requestor);
            userManager.removeUser(userId);
            this.eventManager.logEvent(userId, requestorUID, IdentityProviderAudit.LocalAccountRemoved.getValue(),
                "The user account for " + userId + " was removed by " + requestorUID + ".");
        } catch (DorianInternalFault e) {
            String message = "An unexpected error occurred while trying to remove the user " + userId + ": ";
            this.eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                FederationAudit.InternalError.getValue(), message + "\n\n" + FaultUtil.printFaultToString(e));
            throw e;
        } catch (PermissionDeniedFault e) {
            String message = "Permission to remove the user " + userId + " was denied.";
            this.eventManager.logEvent(requestorUID, AuditConstants.SYSTEM_ID, IdentityProviderAudit.LocalAccessDenied
                .getValue(), message + "\n\n" + Utils.getExceptionMessage(e));
            throw e;
        }
    }


    public void clearDatabase() throws DorianInternalFault {
        assertionManager.clearDatabase();
        userManager.clearDatabase();
        try {
            this.identityProviderAuditor.clear();
        } catch (Exception e) {
            logError(e.getMessage(), e);
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("An unexpected error occurred in deleting the auditing logs.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        }
    }


    public boolean doesUserExist(String userId) throws DorianInternalFault {
        return this.userManager.userExists(userId);
    }


    public List<IdentityProviderAuditRecord> performAudit(String requestorUID, IdentityProviderAuditFilter f)
        throws DorianInternalFault, PermissionDeniedFault {
        try {
            LocalUser requestor = verifyUser(requestorUID);
            verifyAdministrator(requestor);
            List<EventAuditor> handlers = new ArrayList<EventAuditor>();
            handlers.add(this.identityProviderAuditor);
            List<IdentityProviderAuditRecord> list = new ArrayList<IdentityProviderAuditRecord>();
            for (int i = 0; i < handlers.size(); i++) {
                EventAuditor eh = handlers.get(i);
                if (f == null) {
                    f = new IdentityProviderAuditFilter();
                }
                String eventType = null;
                if (f.getAuditType() != null) {
                    eventType = f.getAuditType().getValue();
                }

                Date start = null;
                Date end = null;

                if (f.getStartDate() != null) {
                    start = f.getStartDate().getTime();
                }
                if (f.getEndDate() != null) {
                    end = f.getEndDate().getTime();
                }

                try {
                    List<Event> events = eh.findEvents(f.getTargetId(), f.getReportingPartyId(), eventType, start, end,
                        f.getAuditMessage());
                    for (int j = 0; j < events.size(); j++) {
                        Event e = events.get(j);
                        IdentityProviderAuditRecord r = new IdentityProviderAuditRecord();
                        r.setTargetId(e.getTargetId());
                        r.setReportingPartyId(e.getReportingPartyId());
                        r.setAuditType(IdentityProviderAudit.fromValue(e.getEventType()));
                        Calendar c = new GregorianCalendar();
                        c.setTimeInMillis(e.getOccurredAt());
                        r.setOccurredAt(c);
                        r.setAuditMessage(e.getMessage());
                        list.add(r);
                    }
                } catch (Exception e) {
                    logError(e.getMessage(), e);
                    String msg = "An unexpected error occurred in searching the auditing logs.";
                    this.eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                        FederationAudit.InternalError.getValue(), msg + "\n" + Utils.getExceptionMessage(e) + "\n\n"
                            + FaultUtil.printFaultToString(e));
                    DorianInternalFault fault = new DorianInternalFault();
                    fault.setFaultString(msg);
                    FaultHelper helper = new FaultHelper(fault);
                    helper.addFaultCause(e);
                    fault = (DorianInternalFault) helper.getFault();
                    throw fault;
                }
            }
            return list;

        } catch (DorianInternalFault e) {
            String mess = "An unexpected error occurred in performing an audit:";
            this.eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                FederationAudit.InternalError.getValue(), mess + "\n\n" + FaultUtil.printFaultToString(e));
            throw e;
        } catch (PermissionDeniedFault e) {
            String mess = "Caller not permitted to perform audits:";
            this.eventManager.logEvent(requestorUID, AuditConstants.SYSTEM_ID, IdentityProviderAudit.LocalAccessDenied
                .getValue(), mess + "\n\n" + Utils.getExceptionMessage(e));
            throw e;
        }
    }


    private AccountProfile userToAccountProfile(LocalUser u) {
        AccountProfile p = new AccountProfile();
        p.setUserId(u.getUserId());
        p.setAddress(u.getAddress());
        p.setAddress2(u.getAddress2());
        p.setCity(u.getCity());
        p.setCountry(u.getCountry());
        p.setEmail(u.getEmail());
        p.setFirstName(u.getFirstName());
        p.setLastName(u.getLastName());
        p.setOrganization(u.getOrganization());
        p.setPhoneNumber(u.getPhoneNumber());
        p.setState(u.getState());
        p.setZipcode(u.getZipcode());
        return p;
    }


    public AccountProfile getAccountProfile(String requestorUID) throws RemoteException, DorianInternalFault,
        PermissionDeniedFault {
        try {
            LocalUser requestor = verifyUser(requestorUID);
            return userToAccountProfile(requestor);
        } catch (DorianInternalFault e) {
            String message = "An unexpected error occurred while trying to get the account profile for the user "
                + requestorUID + ".";
            this.eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                FederationAudit.InternalError.getValue(), message + "\n\n" + FaultUtil.printFaultToString(e));
            throw e;
        } catch (PermissionDeniedFault e) {
            String message = "Permission to get the requested account profile was denied.";
            this.eventManager.logEvent(requestorUID, AuditConstants.SYSTEM_ID, IdentityProviderAudit.LocalAccessDenied
                .getValue(), message + "\n\n" + Utils.getExceptionMessage(e));
            throw e;
        }
    }


    public void updateAccountProfile(String requestorUID, AccountProfile profile) throws RemoteException,
        DorianInternalFault, InvalidUserPropertyFault, PermissionDeniedFault, NoSuchUserFault {
        try {
            LocalUser requestor = verifyUser(requestorUID);
            if (profile == null) {
                InvalidUserPropertyFault f = new InvalidUserPropertyFault();
                f.setFaultString("Could not update the account profile, no profile was provided.");
                throw f;
            } else if (conf.getAccountInformationModificationPolicy().equals(
                AccountInformationModificationPolicy.Admin.getValue())) {
                PermissionDeniedFault f = new PermissionDeniedFault();
                f.setFaultString("You do not have permission to update the account profile for " + profile.getUserId()
                    + ".");
                throw f;
            } else if ((Utils.clean(profile.getUserId()) == null)
                || (!requestor.getUserId().equals(profile.getUserId()))) {
                PermissionDeniedFault f = new PermissionDeniedFault();
                f.setFaultString("You do not have permission to update the account profile for " + profile.getUserId()
                    + ".");
                throw f;
            } else {
                LocalUser beforeUpdate = this.userManager.getUser(requestor.getUserId());
                requestor.setPassword(null);
                requestor.setAddress(profile.getAddress());
                requestor.setAddress2(profile.getAddress2());
                requestor.setCity(profile.getCity());
                requestor.setCountry(profile.getCountry());
                requestor.setEmail(profile.getEmail());
                requestor.setFirstName(profile.getFirstName());
                requestor.setLastName(profile.getLastName());
                requestor.setOrganization(profile.getOrganization());
                requestor.setPhoneNumber(profile.getPhoneNumber());
                requestor.setState(profile.getState());
                requestor.setZipcode(profile.getZipcode());
                this.userManager.updateUser(requestor);
                this.eventManager.logEvent(requestor.getUserId(), requestorUID,
                    IdentityProviderAudit.LocalAccountUpdated.getValue(), ReportUtils.generateReport(beforeUpdate,
                        this.userManager.getUser(requestor.getUserId())));
            }
        } catch (DorianInternalFault e) {
            String message = "An unexpected error occurred while trying to get the account profile for the user "
                + requestorUID + ".";
            this.eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                FederationAudit.InternalError.getValue(), message + "\n\n" + FaultUtil.printFaultToString(e));
            throw e;
        } catch (PermissionDeniedFault e) {
            String message = "Permission to get the requested account profile was denied.";
            this.eventManager.logEvent(requestorUID, AuditConstants.SYSTEM_ID, IdentityProviderAudit.LocalAccessDenied
                .getValue(), message + "\n\n" + Utils.getExceptionMessage(e));
            throw e;
        }
    }


    public IdentityProviderPolicy getPolicy() {
        IdentityProviderPolicy policy = new IdentityProviderPolicy();
        UserIdPolicy u = new UserIdPolicy();
        u.setMinLength(this.conf.getMinUserIdLength());
        u.setMaxLength(this.conf.getMaxUserIdLength());
        policy.setUserIdPolicy(u);
        PasswordPolicy p = new PasswordPolicy();
        p.setDictionaryWordsAllowed(false);
        p.setLowerCaseLetterRequired(true);
        p.setMaxLength(conf.getPasswordSecurityPolicy().getMaxPasswordLength());
        p.setMinLength(conf.getPasswordSecurityPolicy().getMinPasswordLength());
        p.setNumericRequired(true);
        PasswordLockoutPolicy plp = new PasswordLockoutPolicy();
        plp.setConsecutiveInvalidLogins(conf.getPasswordSecurityPolicy().getConsecutiveInvalidLogins());
        plp.setHours(conf.getPasswordSecurityPolicy().getLockout().getHours());
        plp.setMinutes(conf.getPasswordSecurityPolicy().getLockout().getMinutes());
        plp.setSeconds(conf.getPasswordSecurityPolicy().getLockout().getSeconds());
        p.setPasswordLockoutPolicy(plp);
        policy.setAccountInformationModificationPolicy(AccountInformationModificationPolicy.fromValue(this.conf
            .getAccountInformationModificationPolicy()));
        p.setSymbolRequired(true);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < PasswordUtils.SYMBOLS.length; i++) {
            sb.append(PasswordUtils.SYMBOLS[i]);
            if ((i + 1) != PasswordUtils.SYMBOLS.length) {
                sb.append(", ");
            }
        }
        p.setSymbolsSupported(sb.toString());
        p.setUpperCaseLetterRequired(true);
        p
            .setDescription("A valid password MUST contain at least 1 lower case letter, 1 upper case letter, 1 number, and 1 symbol ("
                + sb.toString()
                + ").  A valid password MUST be between "
                + conf.getPasswordSecurityPolicy().getMinPasswordLength()
                + " and "
                + conf.getPasswordSecurityPolicy().getMaxPasswordLength()
                + " characters in length.  A valid password MAY NOT contain a dictionary word greater than 3 characters, spelled forwards or backwards.");
        policy.setPasswordPolicy(p);
        return policy;
    }
}
