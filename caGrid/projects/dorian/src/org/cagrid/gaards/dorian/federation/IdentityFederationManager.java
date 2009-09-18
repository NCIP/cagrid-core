package org.cagrid.gaards.dorian.federation;

import gov.nih.nci.cagrid.common.FaultHelper;
import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.ThreadManager;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.gts.client.GTSAdminClient;
import gov.nih.nci.cagrid.opensaml.SAMLAssertion;
import gov.nih.nci.cagrid.opensaml.SAMLAttribute;
import gov.nih.nci.cagrid.opensaml.SAMLAttributeStatement;
import gov.nih.nci.cagrid.opensaml.SAMLAuthenticationStatement;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.security.PublicKey;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bouncycastle.asn1.x509.CRLReason;
import org.cagrid.gaards.dorian.ca.CertificateAuthority;
import org.cagrid.gaards.dorian.ca.CertificateAuthorityFault;
import org.cagrid.gaards.dorian.common.AuditConstants;
import org.cagrid.gaards.dorian.common.LoggingObject;
import org.cagrid.gaards.dorian.service.PropertyManager;
import org.cagrid.gaards.dorian.service.util.AddressValidator;
import org.cagrid.gaards.dorian.stubs.types.DorianInternalFault;
import org.cagrid.gaards.dorian.stubs.types.InvalidAssertionFault;
import org.cagrid.gaards.dorian.stubs.types.InvalidHostCertificateFault;
import org.cagrid.gaards.dorian.stubs.types.InvalidHostCertificateRequestFault;
import org.cagrid.gaards.dorian.stubs.types.InvalidTrustedIdPFault;
import org.cagrid.gaards.dorian.stubs.types.InvalidUserCertificateFault;
import org.cagrid.gaards.dorian.stubs.types.InvalidUserFault;
import org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault;
import org.cagrid.gaards.dorian.stubs.types.UserPolicyFault;
import org.cagrid.gaards.pki.CRLEntry;
import org.cagrid.gaards.pki.CertUtil;
import org.cagrid.tools.database.Database;
import org.cagrid.tools.events.Event;
import org.cagrid.tools.events.EventAuditor;
import org.cagrid.tools.events.EventManager;
import org.cagrid.tools.events.EventToHandlerMapping;
import org.cagrid.tools.groups.Group;
import org.cagrid.tools.groups.GroupException;
import org.cagrid.tools.groups.GroupManager;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class IdentityFederationManager extends LoggingObject implements Publisher {

    private final int CERTIFICATE_START_OFFSET_SECONDS = -10;

    private UserManager um;

    private TrustedIdPManager tm;

    private IdentityFederationProperties conf;

    private CertificateAuthority ca;

    private Object mutex = new Object();

    public static final String ADMINISTRATORS = "administrators";

    private Group administrators;

    private GroupManager groupManager;

    private HostCertificateManager hostManager;

    private ThreadManager threadManager;

    private boolean publishCRL = false;

    private CertificateBlacklistManager blackList;

    private UserCertificateManager userCertificateManager;

    private Database db;

    private EventManager eventManager;

    private EventAuditor federationAuditor;
    private EventAuditor gridAccountAuditor;
    private EventAuditor hostAuditor;
    private EventAuditor userCertificateAuditor;


    public IdentityFederationManager(IdentityFederationProperties conf, Database db, PropertyManager properties,
        CertificateAuthority ca, EventManager eventManager, FederationDefaults defaults) throws DorianInternalFault {
        this(conf, db, properties, ca, eventManager, defaults, false);
    }


    public IdentityFederationManager(IdentityFederationProperties conf, Database db, PropertyManager properties,
        CertificateAuthority ca, EventManager eventManager, FederationDefaults defaults, boolean ignoreCRL)
        throws DorianInternalFault {
        super();
        this.conf = conf;
        this.ca = ca;
        this.db = db;
        this.eventManager = eventManager;
        this.initializeEventManager();
        this.threadManager = new ThreadManager();
        this.blackList = new CertificateBlacklistManager(db);
        this.userCertificateManager = new UserCertificateManager(db, this, this.blackList);
        tm = new TrustedIdPManager(conf, db);
        um = new UserManager(db, conf, properties, ca, tm, this, defaults);
        um.buildDatabase();
        this.groupManager = new GroupManager(db);
        try {
            if (!this.groupManager.groupExists(ADMINISTRATORS)) {
                this.groupManager.addGroup(ADMINISTRATORS);
                this.administrators = this.groupManager.getGroup(ADMINISTRATORS);
                if (defaults.getDefaultUser() != null) {
                    this.administrators.addMember(defaults.getDefaultUser().getGridId());
                } else {
                    String mess = "COULD NOT ADD DEFAULT USER TO ADMINISTRATORS GROUP, NO DEFAULT USER WAS FOUND!!!";
                    logWarning(mess);
                    this.eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                        FederationAudit.InternalError.getValue(), mess);
                }
            } else {
                this.administrators = this.groupManager.getGroup(ADMINISTRATORS);
            }
        } catch (GroupException e) {
            logError(e.getMessage(), e);
            String mess = "An unexpected error occurred in setting up the administrators group.";
            this.eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                FederationAudit.InternalError.getValue(), mess + "\n\n" + FaultUtil.printFaultToString(e));
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString(mess);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        }
        this.hostManager = new HostCertificateManager(db, this.conf, ca, this, blackList);

        try {
            TrustedIdP idp = tm.getTrustedIdP(CertUtil.loadCertificate(defaults.getDefaultIdP().getIdPCertificate()));
            if ((idp.getAuthenticationServiceURL() == null)
                && (defaults.getDefaultIdP().getAuthenticationServiceURL() != null)) {
                idp.setAuthenticationServiceURL(defaults.getDefaultIdP().getAuthenticationServiceURL());
                tm.updateIdP(idp);
            } else if ((defaults.getDefaultIdP().getAuthenticationServiceURL() != null)
                && (!defaults.getDefaultIdP().getAuthenticationServiceURL().equals(idp.getAuthenticationServiceURL()))) {
                idp.setAuthenticationServiceURL(defaults.getDefaultIdP().getAuthenticationServiceURL());
                tm.updateIdP(idp);
            }
        } catch (Exception e) {
            logError(e.getMessage(), e);
            String mess = "An unexpected error occurred in ensuring the integrity of the Dorian IdP.";
            this.eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                FederationAudit.InternalError.getValue(), mess + "\n\n" + FaultUtil.printFaultToString(e));
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString(mess);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        }
        if (!ignoreCRL) {
            publishCRL = true;
            publishCRL();
        }
    }


    private void initializeEventManager() throws DorianInternalFault {
        try {
            if (this.eventManager.isHandlerRegistered(AuditingConstants.FEDERATION_AUDITOR)) {
                this.federationAuditor = (EventAuditor) this.eventManager
                    .getEventHandler(AuditingConstants.FEDERATION_AUDITOR);
            } else {
                this.federationAuditor = new EventAuditor(AuditingConstants.FEDERATION_AUDITOR, this.db,
                    AuditingConstants.FEDERATION_AUDITOR_DB);
                this.eventManager.registerHandler(this.federationAuditor);
            }

            if (this.eventManager.isHandlerRegistered(AuditingConstants.GRID_ACCOUNT_AUDITOR)) {
                this.gridAccountAuditor = (EventAuditor) this.eventManager
                    .getEventHandler(AuditingConstants.GRID_ACCOUNT_AUDITOR);
            } else {
                this.gridAccountAuditor = new EventAuditor(AuditingConstants.GRID_ACCOUNT_AUDITOR, this.db,
                    AuditingConstants.GRID_ACCOUNT_AUDITOR_DB);
                this.eventManager.registerHandler(this.gridAccountAuditor);
            }

            if (this.eventManager.isHandlerRegistered(AuditingConstants.HOST_AUDITOR)) {
                this.hostAuditor = (EventAuditor) this.eventManager.getEventHandler(AuditingConstants.HOST_AUDITOR);
            } else {
                this.hostAuditor = new EventAuditor(AuditingConstants.HOST_AUDITOR, this.db,
                    AuditingConstants.HOST_AUDITOR_DB);
                this.eventManager.registerHandler(this.hostAuditor);
            }

            if (this.eventManager.isHandlerRegistered(AuditingConstants.USER_CERTIFICATE_AUDITOR)) {
                this.userCertificateAuditor = (EventAuditor) this.eventManager
                    .getEventHandler(AuditingConstants.USER_CERTIFICATE_AUDITOR);
            } else {
                this.userCertificateAuditor = new EventAuditor(AuditingConstants.USER_CERTIFICATE_AUDITOR, this.db,
                    AuditingConstants.USER_CERTIFICATE_AUDITOR_DB);
                this.eventManager.registerHandler(this.userCertificateAuditor);
            }

            this.eventManager.registerEventWithHandler(new EventToHandlerMapping(FederationAudit.SystemStartup
                .getValue(), AuditingConstants.FEDERATION_AUDITOR));
            this.eventManager.registerEventWithHandler(new EventToHandlerMapping(FederationAudit.InternalError
                .getValue(), AuditingConstants.FEDERATION_AUDITOR));

            this.eventManager.registerEventWithHandler(new EventToHandlerMapping(FederationAudit.AccessDenied
                .getValue(), AuditingConstants.FEDERATION_AUDITOR));

            this.eventManager.registerEventWithHandler(new EventToHandlerMapping(FederationAudit.IdPAdded.getValue(),
                AuditingConstants.FEDERATION_AUDITOR));
            this.eventManager.registerEventWithHandler(new EventToHandlerMapping(FederationAudit.IdPUpdated.getValue(),
                AuditingConstants.FEDERATION_AUDITOR));
            this.eventManager.registerEventWithHandler(new EventToHandlerMapping(FederationAudit.IdPRemoved.getValue(),
                AuditingConstants.FEDERATION_AUDITOR));
            this.eventManager.registerEventWithHandler(new EventToHandlerMapping(FederationAudit.AdminAdded.getValue(),
                AuditingConstants.FEDERATION_AUDITOR));
            this.eventManager.registerEventWithHandler(new EventToHandlerMapping(FederationAudit.AdminRemoved
                .getValue(), AuditingConstants.FEDERATION_AUDITOR));
            this.eventManager.registerEventWithHandler(new EventToHandlerMapping(FederationAudit.CRLPublished
                .getValue(), AuditingConstants.FEDERATION_AUDITOR));
            this.eventManager.registerEventWithHandler(new EventToHandlerMapping(FederationAudit.AccountCreated
                .getValue(), AuditingConstants.GRID_ACCOUNT_AUDITOR));
            this.eventManager.registerEventWithHandler(new EventToHandlerMapping(FederationAudit.AccountUpdated
                .getValue(), AuditingConstants.GRID_ACCOUNT_AUDITOR));
            this.eventManager.registerEventWithHandler(new EventToHandlerMapping(FederationAudit.AccountRemoved
                .getValue(), AuditingConstants.GRID_ACCOUNT_AUDITOR));
            this.eventManager.registerEventWithHandler(new EventToHandlerMapping(
                FederationAudit.SuccessfulUserCertificateRequest.getValue(), AuditingConstants.GRID_ACCOUNT_AUDITOR));
            this.eventManager.registerEventWithHandler(new EventToHandlerMapping(
                FederationAudit.InvalidUserCertificateRequest.getValue(), AuditingConstants.GRID_ACCOUNT_AUDITOR));
            this.eventManager.registerEventWithHandler(new EventToHandlerMapping(
                FederationAudit.HostCertificateRequested.getValue(), AuditingConstants.HOST_AUDITOR));
            this.eventManager.registerEventWithHandler(new EventToHandlerMapping(
                FederationAudit.HostCertificateApproved.getValue(), AuditingConstants.HOST_AUDITOR));
            this.eventManager.registerEventWithHandler(new EventToHandlerMapping(FederationAudit.HostCertificateUpdated
                .getValue(), AuditingConstants.HOST_AUDITOR));
            this.eventManager.registerEventWithHandler(new EventToHandlerMapping(FederationAudit.HostCertificateRenewed
                .getValue(), AuditingConstants.HOST_AUDITOR));
            this.eventManager.registerEventWithHandler(new EventToHandlerMapping(FederationAudit.UserCertificateUpdated
                .getValue(), AuditingConstants.USER_CERTIFICATE_AUDITOR));
            this.eventManager.registerEventWithHandler(new EventToHandlerMapping(FederationAudit.UserCertificateRemoved
                .getValue(), AuditingConstants.USER_CERTIFICATE_AUDITOR));
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


    public String getIdentityAssignmentPolicy() {
        return um.getIdentityAssignmentPolicy();
    }


    public GridUserPolicy[] getUserPolicies(String callerGridIdentity) throws DorianInternalFault,
        PermissionDeniedFault {
        try {
            GridUser caller = getUser(callerGridIdentity);
            verifyActiveUser(caller);
            verifyAdminUser(caller);
            return tm.getAccountPolicies();
        } catch (DorianInternalFault e) {
            String mess = "An unexpected error occurred obtaining the supported user polcies.";
            this.eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                FederationAudit.InternalError.getValue(), mess + "\n\n" + FaultUtil.printFaultToString(e));
            throw e;
        } catch (PermissionDeniedFault e) {
            String mess = "Permission denied to obtaining supported user policies:";
            this.eventManager.logEvent(callerGridIdentity, AuditConstants.SYSTEM_ID, FederationAudit.AccessDenied
                .getValue(), mess + "\n\n" + Utils.getExceptionMessage(e));
            throw e;
        }
    }


    public String getUserIdVerifyTrustedIdP(X509Certificate idpCert, String identity) throws DorianInternalFault,
        InvalidUserFault, InvalidTrustedIdPFault, PermissionDeniedFault {
        if (identity == null) {
            PermissionDeniedFault fault = new PermissionDeniedFault();
            fault.setFaultString("No credentials specified.");
            throw fault;
        }
        TrustedIdP idp = tm.getTrustedIdPByDN(idpCert.getSubjectDN().getName());
        GridUser usr = um.getUser(identity);
        if (usr.getIdPId() != idp.getId()) {
            PermissionDeniedFault fault = new PermissionDeniedFault();
            fault.setFaultString("Not a valid user of the IdP " + idp.getName());
            throw fault;
        }
        return usr.getUID();
    }


    public TrustedIdP addTrustedIdP(String callerGridIdentity, TrustedIdP idp) throws DorianInternalFault,
        InvalidTrustedIdPFault, PermissionDeniedFault {
        try {
            GridUser caller = getUser(callerGridIdentity);
            verifyActiveUser(caller);
            verifyAdminUser(caller);
            idp = tm.addTrustedIdP(idp);
            this.eventManager.logEvent(idp.getName(), callerGridIdentity, FederationAudit.IdPAdded.getValue(),
                "The Trusted Identity Provider " + idp.getName() + " (" + idp.getId() + ") was added by "
                    + callerGridIdentity + ".");
            return idp;
        } catch (DorianInternalFault e) {
            String mess = "An unexpected error occurred in adding a trusted identity provider.";
            this.eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                FederationAudit.InternalError.getValue(), mess + "\n\n" + FaultUtil.printFaultToString(e));
            throw e;
        } catch (PermissionDeniedFault e) {
            String mess = "Caller not permitted to add a trusted identity provider:";
            this.eventManager.logEvent(callerGridIdentity, AuditConstants.SYSTEM_ID, FederationAudit.AccessDenied
                .getValue(), mess + "\n\n" + Utils.getExceptionMessage(e));
            throw e;
        }
    }


    public void updateTrustedIdP(String callerGridIdentity, TrustedIdP idp) throws DorianInternalFault,
        InvalidTrustedIdPFault, PermissionDeniedFault {
        try {
            GridUser caller = getUser(callerGridIdentity);
            verifyActiveUser(caller);
            verifyAdminUser(caller);
            TrustedIdP curr = tm.getTrustedIdPById(idp.getId());
            boolean statusChanged = false;
            if ((idp.getStatus() != null) && (!idp.getStatus().equals(curr.getStatus()))) {
                statusChanged = true;
            }
            tm.updateIdP(idp);

            if (statusChanged) {
                publishCRL();
            }
            this.eventManager.logEvent(idp.getName(), callerGridIdentity, FederationAudit.IdPUpdated.getValue(),
                ReportUtils.generateReport(curr, idp));
        } catch (DorianInternalFault e) {
            String mess = "An unexpected error occurred in updating the identity provider " + idp.getName() + " ("
                + idp.getId() + "):";
            this.eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                FederationAudit.InternalError.getValue(), mess + "\n\n" + FaultUtil.printFaultToString(e));
            throw e;
        } catch (PermissionDeniedFault e) {
            String mess = "Caller not permitted to update a trusted identity provider:";
            this.eventManager.logEvent(callerGridIdentity, AuditConstants.SYSTEM_ID, FederationAudit.AccessDenied
                .getValue(), mess + "\n\n" + Utils.getExceptionMessage(e));
            throw e;
        }
    }


    public void removeTrustedIdP(String callerGridIdentity, long idpId) throws DorianInternalFault,
        InvalidTrustedIdPFault, PermissionDeniedFault {
        try {
            GridUser caller = getUser(callerGridIdentity);
            verifyActiveUser(caller);
            verifyAdminUser(caller);
            TrustedIdP idp = tm.getTrustedIdPById(idpId);
            tm.removeTrustedIdP(idpId);
            this.eventManager.logEvent(idp.getName(), callerGridIdentity, FederationAudit.IdPRemoved.getValue(),
                "The Identity Provider " + idp.getName() + " (" + idp.getId() + ") was removed by "
                    + callerGridIdentity + ".");
            GridUserFilter uf = new GridUserFilter();
            uf.setIdPId(idpId);
            GridUser[] users = um.getUsers(uf);
            for (int i = 0; i < users.length; i++) {
                try {
                    removeUser(users[i]);
                    this.eventManager.logEvent(users[i].getGridId(), callerGridIdentity, FederationAudit.AccountRemoved
                        .getValue(), users[i].getFirstName() + " " + users[i].getLastName()
                        + "'s account was removed because the IdP " + idp.getName() + " (" + idp.getId()
                        + ") was removed by " + callerGridIdentity + " was removed as a Trusted IdP.");
                } catch (Exception e) {
                    logError(e.getMessage(), e);
                    this.eventManager
                        .logEvent(
                            AuditConstants.SYSTEM_ID,
                            AuditConstants.SYSTEM_ID,
                            FederationAudit.InternalError.getValue(),
                            "In removing the Trusted IdP "
                                + idp.getName()
                                + " ("
                                + idp.getId()
                                + ") an unexpected error was encountered when trying to remove the user account "
                                + users[i].getGridId()
                                + ".  Although the Trusted IdP was removed the user account may not have been removed as it should have been because of this error.   Details of this error are provided below:\n\n"
                                + FaultUtil.printFaultToString(e));
                }
            }

        } catch (DorianInternalFault e) {
            String mess = "An unexpected error occurred in removing the identity provider " + idpId + ":";
            this.eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                FederationAudit.InternalError.getValue(), mess + "\n\n" + FaultUtil.printFaultToString(e));
            throw e;
        } catch (PermissionDeniedFault e) {
            String mess = "Caller not permitted to remove a identity provider:";
            this.eventManager.logEvent(callerGridIdentity, AuditConstants.SYSTEM_ID, FederationAudit.AccessDenied
                .getValue(), mess + "\n\n" + Utils.getExceptionMessage(e));
            throw e;
        }
    }


    public TrustedIdP[] getTrustedIdPs(String callerGridIdentity) throws DorianInternalFault, PermissionDeniedFault {
        try {
            GridUser caller = getUser(callerGridIdentity);
            verifyActiveUser(caller);
            verifyAdminUser(caller);
            return tm.getTrustedIdPs();
        } catch (DorianInternalFault e) {
            String mess = "An unexpected error occurred in listing the trusted identity providers.";
            this.eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                FederationAudit.InternalError.getValue(), mess + "\n\n" + FaultUtil.printFaultToString(e));
            throw e;
        } catch (PermissionDeniedFault e) {
            String mess = "Caller not permitted to list trusted identity providers:";
            this.eventManager.logEvent(callerGridIdentity, AuditConstants.SYSTEM_ID, FederationAudit.AccessDenied
                .getValue(), mess + "\n\n" + Utils.getExceptionMessage(e));
            throw e;
        }
    }


    public GridUser getUser(String callerGridIdentity, long idpId, String uid) throws DorianInternalFault,
        InvalidUserFault, PermissionDeniedFault {
        try {
            GridUser caller = um.getUser(callerGridIdentity);
            verifyActiveUser(caller);
            verifyAdminUser(caller);
            return um.getUser(idpId, uid);
        } catch (DorianInternalFault e) {
            String mess = "An unexpected error occurred in loading a user account.";
            this.eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                FederationAudit.InternalError.getValue(), mess + "\n\n" + FaultUtil.printFaultToString(e));
            throw e;
        } catch (PermissionDeniedFault e) {
            String mess = "Caller not permitted to load grid user accounts:";
            this.eventManager.logEvent(callerGridIdentity, AuditConstants.SYSTEM_ID, FederationAudit.AccessDenied
                .getValue(), mess + "\n\n" + Utils.getExceptionMessage(e));
            throw e;
        }
    }


    public GridUser[] findUsers(String callerGridIdentity, GridUserFilter filter) throws DorianInternalFault,
        PermissionDeniedFault {
        try {
            GridUser caller = getUser(callerGridIdentity);
            verifyActiveUser(caller);
            verifyAdminUser(caller);
            return um.getUsers(filter);

        } catch (DorianInternalFault e) {
            String mess = "An unexpected error occurred in searching for grid user accounts.";
            this.eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                FederationAudit.InternalError.getValue(), mess + "\n\n" + FaultUtil.printFaultToString(e));
            throw e;
        } catch (PermissionDeniedFault e) {
            String mess = "Caller not permitted to search for grid user accounts:";
            this.eventManager.logEvent(callerGridIdentity, AuditConstants.SYSTEM_ID, FederationAudit.AccessDenied
                .getValue(), mess + "\n\n" + Utils.getExceptionMessage(e));
            throw e;
        }
    }


    public void updateUser(String callerGridIdentity, GridUser usr) throws DorianInternalFault, InvalidUserFault,
        PermissionDeniedFault {
        try {
            GridUser caller = um.getUser(callerGridIdentity);
            verifyActiveUser(caller);
            verifyAdminUser(caller);
            GridUser curr = um.getUser(usr.getIdPId(), usr.getUID());
            um.updateUser(usr);
            this.eventManager.logEvent(curr.getGridId(), callerGridIdentity, FederationAudit.AccountUpdated.getValue(),
                ReportUtils.generateReport(curr, usr));
        } catch (DorianInternalFault e) {
            String mess = "An unexpected error occurred in updating the grid user account " + usr.getGridId() + ":";
            this.eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                FederationAudit.InternalError.getValue(), mess + "\n\n" + FaultUtil.printFaultToString(e));
            throw e;
        } catch (PermissionDeniedFault e) {
            String mess = "Caller not permitted to update grid user accounts:";
            this.eventManager.logEvent(callerGridIdentity, AuditConstants.SYSTEM_ID, FederationAudit.AccessDenied
                .getValue(), mess + "\n\n" + Utils.getExceptionMessage(e));
            throw e;
        }
    }


    public void removeUserByLocalIdIfExists(X509Certificate idpCert, String localId) throws DorianInternalFault {
        try {
            TrustedIdP idp = tm.getTrustedIdPByDN(idpCert.getSubjectDN().getName());
            GridUser usr = um.getUser(idp.getId(), localId);
            removeUser(usr);
            this.eventManager.logEvent(usr.getGridId(), AuditConstants.SYSTEM_ID, FederationAudit.AccountRemoved
                .getValue(), usr.getFirstName() + " " + usr.getLastName()
                + "'s account was removed because their local account (" + localId
                + ") with the Dorian IdP was removed.");
        } catch (InvalidUserFault e) {

        } catch (InvalidTrustedIdPFault f) {
            logError(f.getFaultString(), f);
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("An unexpected error occurred removing the grid user, the IdP "
                + idpCert.getSubjectDN().getName() + " could not be resolved!!!");
            throw fault;
        }
    }


    private void removeUser(GridUser usr) throws DorianInternalFault, InvalidUserFault {
        try {
            um.removeUser(usr);
            this.userCertificateManager.removeCertificates(usr.getGridId());

            List<HostCertificateRecord> records = this.hostManager.getHostCertificateRecords(usr.getGridId());
            boolean updateCRL = false;
            for (int i = 0; i < records.size(); i++) {
                HostCertificateRecord r = records.get(i);
                if ((r.getStatus().equals(HostCertificateStatus.Active))
                    || (r.getStatus().equals(HostCertificateStatus.Suspended))) {
                    HostCertificateUpdate update = new HostCertificateUpdate();
                    update.setId(r.getId());
                    update.setStatus(HostCertificateStatus.Compromised);
                    this.hostManager.updateHostCertificateRecord(update);
                    this.eventManager.logEvent(String.valueOf(r.getId()), AuditConstants.SYSTEM_ID,
                        FederationAudit.HostCertificateUpdated.getValue(),
                        "The status of the certificate for the host " + r.getHost() + " (" + r.getId()
                            + "), was changed from " + r.getStatus().getValue() + " to "
                            + HostCertificateStatus.Compromised.getValue() + ", because its owner's (" + r.getOwner()
                            + ") account was removed.");
                    updateCRL = true;
                } else if (r.getStatus().equals(HostCertificateStatus.Pending)) {
                    HostCertificateUpdate update = new HostCertificateUpdate();
                    update.setId(r.getId());
                    update.setStatus(HostCertificateStatus.Rejected);
                    this.hostManager.updateHostCertificateRecord(update);
                    this.eventManager.logEvent(String.valueOf(r.getId()), AuditConstants.SYSTEM_ID,
                        FederationAudit.HostCertificateUpdated.getValue(),
                        "The status of the certificate for the host " + r.getHost() + " (" + r.getId()
                            + "), was changed from " + r.getStatus().getValue() + " to "
                            + HostCertificateStatus.Rejected.getValue() + ", because its owner's (" + r.getOwner()
                            + ") account was removed.");
                }
            }

            if (this.administrators.isMember(usr.getGridId())) {
                this.administrators.removeMember(usr.getGridId());
                this.eventManager.logEvent(usr.getGridId(), AuditConstants.SYSTEM_ID, FederationAudit.AdminRemoved
                    .getValue(), "Administrative privileges were revoked for the user " + usr.getGridId()
                    + " because the account was removed.");
            }
            this.groupManager.removeUserFromAllGroups(usr.getGridId());

            if (updateCRL) {
                publishCRL();
            }
        } catch (InvalidUserFault e) {
            throw e;
        } catch (GroupException e) {
            logError(e.getMessage(), e);
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("An unexpected error occurred in removing the user from all groups.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        } catch (InvalidHostCertificateFault e) {
            logError(e.getMessage(), e);
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("An unexpected error occurred.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        }
    }


    public void removeUser(String callerGridIdentity, GridUser usr) throws DorianInternalFault, InvalidUserFault,
        PermissionDeniedFault {
        try {
            GridUser caller = um.getUser(callerGridIdentity);
            verifyActiveUser(caller);
            verifyAdminUser(caller);
            removeUser(usr);
            this.eventManager.logEvent(usr.getGridId(), callerGridIdentity, FederationAudit.AccountRemoved.getValue(),
                usr.getFirstName() + " " + usr.getLastName() + "'s account was removed by the administrator "
                    + callerGridIdentity + ".");
        } catch (DorianInternalFault e) {
            String mess = "An unexpected error occurred in removing the grid user account " + usr.getGridId() + ": ";
            this.eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                FederationAudit.InternalError.getValue(), mess + "\n\n" + FaultUtil.printFaultToString(e));
            throw e;
        } catch (PermissionDeniedFault e) {
            String mess = "Caller not permitted to remove grid user accounts:";
            this.eventManager.logEvent(callerGridIdentity, AuditConstants.SYSTEM_ID, FederationAudit.AccessDenied
                .getValue(), mess + "\n\n" + Utils.getExceptionMessage(e));
            throw e;
        }
    }


    public void addAdmin(String callerGridIdentity, String gridIdentity) throws RemoteException, DorianInternalFault,
        PermissionDeniedFault {
        GridUser caller = getUser(callerGridIdentity);
        try {
            verifyActiveUser(caller);
            verifyAdminUser(caller);
            try {
                if (!this.administrators.isMember(gridIdentity)) {
                    GridUser admin = getUser(gridIdentity);
                    verifyActiveUser(admin);
                    this.administrators.addMember(gridIdentity);
                    this.eventManager.logEvent(gridIdentity, callerGridIdentity, FederationAudit.AdminAdded.getValue(),
                        "The user " + gridIdentity + " was granted administrator privileges by " + callerGridIdentity
                            + ".");
                }
            } catch (GroupException e) {
                logError(e.getMessage(), e);
                DorianInternalFault fault = new DorianInternalFault();
                fault.setFaultString("An unexpected error occurred in adding the user to the administrators group.");
                FaultHelper helper = new FaultHelper(fault);
                helper.addFaultCause(e);
                fault = (DorianInternalFault) helper.getFault();
                throw fault;
            }

        } catch (DorianInternalFault e) {
            String mess = "An unexpected error occurred in granting administrative privileges to " + gridIdentity + ":";
            this.eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                FederationAudit.InternalError.getValue(), mess + "\n\n" + FaultUtil.printFaultToString(e));
            throw e;
        } catch (PermissionDeniedFault e) {
            String mess = "Caller not permitted to grant administrative privileges:";
            this.eventManager.logEvent(callerGridIdentity, AuditConstants.SYSTEM_ID, FederationAudit.AccessDenied
                .getValue(), mess + "\n\n" + Utils.getExceptionMessage(e));
            throw e;
        }
    }


    public void removeAdmin(String callerGridIdentity, String gridIdentity) throws RemoteException,
        DorianInternalFault, PermissionDeniedFault {
        try {
            GridUser caller = getUser(callerGridIdentity);
            verifyActiveUser(caller);
            verifyAdminUser(caller);
            try {
                this.administrators.removeMember(gridIdentity);
                this.eventManager.logEvent(gridIdentity, callerGridIdentity, FederationAudit.AdminRemoved.getValue(),
                    "The administrative privileges for the user, " + gridIdentity + " were revoked by "
                        + callerGridIdentity + ".");
            } catch (GroupException e) {
                logError(e.getMessage(), e);
                DorianInternalFault fault = new DorianInternalFault();
                fault
                    .setFaultString("An unexpected error occurred in removing the user from the administrators group.");
                FaultHelper helper = new FaultHelper(fault);
                helper.addFaultCause(e);
                fault = (DorianInternalFault) helper.getFault();
                throw fault;
            }

        } catch (DorianInternalFault e) {
            String mess = "An unexpected error occurred in revoking administrative privileges from " + gridIdentity
                + ":";
            this.eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                FederationAudit.InternalError.getValue(), mess + "\n\n" + FaultUtil.printFaultToString(e));
            throw e;
        } catch (PermissionDeniedFault e) {
            String mess = "Caller not permitted to revoke administrative privileges:";
            this.eventManager.logEvent(callerGridIdentity, AuditConstants.SYSTEM_ID, FederationAudit.AccessDenied
                .getValue(), mess + "\n\n" + Utils.getExceptionMessage(e));
            throw e;
        }
    }


    public String[] getAdmins(String callerGridIdentity) throws RemoteException, DorianInternalFault,
        PermissionDeniedFault {
        try {
            GridUser caller = getUser(callerGridIdentity);
            verifyActiveUser(caller);
            verifyAdminUser(caller);
            try {
                List<String> members = this.administrators.getMembers();
                String[] admins = new String[members.size()];
                for (int i = 0; i < members.size(); i++) {
                    admins[i] = (String) members.get(i);
                }
                return admins;
            } catch (GroupException e) {
                logError(e.getMessage(), e);
                DorianInternalFault fault = new DorianInternalFault();
                fault
                    .setFaultString("An unexpected error occurred determining the members of the administrators group.");
                FaultHelper helper = new FaultHelper(fault);
                helper.addFaultCause(e);
                fault = (DorianInternalFault) helper.getFault();
                throw fault;
            }
        } catch (DorianInternalFault e) {
            String mess = "An unexpected error occurred in listing users with administrative privileges:";
            this.eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                FederationAudit.InternalError.getValue(), mess + "\n\n" + FaultUtil.printFaultToString(e));
            throw e;
        } catch (PermissionDeniedFault e) {
            String mess = "Caller not permitted to list users with administrative privileges:";
            this.eventManager.logEvent(callerGridIdentity, AuditConstants.SYSTEM_ID, FederationAudit.AccessDenied
                .getValue(), mess + "\n\n" + Utils.getExceptionMessage(e));
            throw e;
        }
    }


    public X509Certificate requestUserCertificate(SAMLAssertion saml, PublicKey publicKey, CertificateLifetime lifetime)
        throws DorianInternalFault, InvalidAssertionFault, UserPolicyFault, PermissionDeniedFault {
        TrustedIdP idp = null;
        try {
            idp = tm.getTrustedIdP(saml);
        } catch (InvalidAssertionFault e) {
            this.eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                FederationAudit.InvalidUserCertificateRequest.getValue(), Utils.getExceptionMessage(e));
            throw e;
        }

        String uid = null;
        try {
            uid = this.getAttribute(saml, idp.getUserIdAttributeDescriptor().getNamespaceURI(), idp
                .getUserIdAttributeDescriptor().getName());
        } catch (InvalidAssertionFault e) {
            this.eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                FederationAudit.InvalidUserCertificateRequest.getValue(), Utils.getExceptionMessage(e));
            throw e;
        }

        String gid = null;

        try {
            gid = UserManager.subjectToIdentity(UserManager.getUserSubject(this.conf.getIdentityAssignmentPolicy(), ca
                .getCACertificate().getSubjectDN().getName(), idp, uid));
        } catch (Exception e) {
            String msg = "An unexpected error occurred in determining the grid identity for the user.";
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

        // Determine whether or not the assertion is expired
        Calendar cal = new GregorianCalendar();
        Date now = cal.getTime();
        if ((now.before(saml.getNotBefore())) || (now.after(saml.getNotOnOrAfter()))) {
            String msg = "The Assertion is not valid at " + now + ", the assertion is valid from "
                + saml.getNotBefore() + " to " + saml.getNotOnOrAfter() + ".";
            this.eventManager.logEvent(gid, AuditConstants.SYSTEM_ID, FederationAudit.InvalidUserCertificateRequest
                .getValue(), msg);
            InvalidAssertionFault fault = new InvalidAssertionFault();
            fault.setFaultString(msg);
            throw fault;
        }

        // Make sure the assertion is trusted

        SAMLAuthenticationStatement auth = getAuthenticationStatement(saml);

        // We need to verify the authentication method now
        boolean allowed = false;
        for (int i = 0; i < idp.getAuthenticationMethod().length; i++) {
            if (idp.getAuthenticationMethod(i).getValue().equals(auth.getAuthMethod())) {
                allowed = true;
            }
        }
        if (!allowed) {
            String msg = "The authentication method " + auth.getAuthMethod() + " is not acceptable for the IdP "
                + idp.getName() + ".";
            this.eventManager.logEvent(gid, AuditConstants.SYSTEM_ID, FederationAudit.InvalidUserCertificateRequest
                .getValue(), msg);
            InvalidAssertionFault fault = new InvalidAssertionFault();
            fault.setFaultString(msg);
            throw fault;
        }

        String email = null;
        String firstName = null;
        String lastName = null;

        try {
            email = this.getAttribute(saml, idp.getEmailAttributeDescriptor().getNamespaceURI(), idp
                .getEmailAttributeDescriptor().getName());
            firstName = this.getAttribute(saml, idp.getFirstNameAttributeDescriptor().getNamespaceURI(), idp
                .getFirstNameAttributeDescriptor().getName());
            lastName = this.getAttribute(saml, idp.getLastNameAttributeDescriptor().getNamespaceURI(), idp
                .getLastNameAttributeDescriptor().getName());
            AddressValidator.validateEmail(email);
        } catch (InvalidAssertionFault e) {
            this.eventManager.logEvent(gid, AuditConstants.SYSTEM_ID, FederationAudit.InvalidUserCertificateRequest
                .getValue(), Utils.getExceptionMessage(e));
            throw e;
        }

        // If the user does not exist, add them
        GridUser usr = null;
        if (!um.determineIfUserExists(idp.getId(), uid)) {
            try {
                usr = new GridUser();
                usr.setIdPId(idp.getId());
                usr.setUID(uid);
                usr.setFirstName(firstName);
                usr.setLastName(lastName);
                usr.setEmail(email);
                usr.setUserStatus(GridUserStatus.Pending);
                usr = um.addUser(idp, usr);
                this.eventManager.logEvent(gid, AuditConstants.SYSTEM_ID, FederationAudit.AccountCreated.getValue(),
                    "User Account Created!!!");
            } catch (Exception e) {
                logError(e.getMessage(), e);
                String msg = "An unexpected error occurred in adding the user " + usr.getUID() + " from the IdP "
                    + idp.getName() + ".";
                this.eventManager.logEvent(gid, AuditConstants.SYSTEM_ID, FederationAudit.InvalidUserCertificateRequest
                    .getValue(), msg + "\n" + FaultUtil.printFaultToString(e));
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
        } else {
            try {
                usr = um.getUser(idp.getId(), uid);
                boolean performUpdate = false;

                if ((usr.getFirstName() == null) || (!usr.getFirstName().equals(firstName))) {
                    usr.setFirstName(firstName);
                    performUpdate = true;
                }
                if ((usr.getLastName() == null) || (!usr.getLastName().equals(lastName))) {
                    usr.setLastName(lastName);
                    performUpdate = true;
                }
                if ((usr.getEmail() == null) || (!usr.getEmail().equals(email))) {
                    usr.setEmail(email);
                    performUpdate = true;
                }
                if (performUpdate) {
                    GridUser orig = um.getUser(idp.getId(), uid);
                    um.updateUser(usr);
                    this.eventManager.logEvent(usr.getGridId(), AuditConstants.SYSTEM_ID,
                        FederationAudit.AccountUpdated.getValue(), ReportUtils.generateReport(orig, usr));
                }

            } catch (Exception e) {
                logError(e.getMessage(), e);
                String msg = "An unexpected error occurred in obtaining/updating the user " + usr.getUID()
                    + " from the IdP " + idp.getName() + ".";
                this.eventManager.logEvent(gid, AuditConstants.SYSTEM_ID, FederationAudit.InvalidUserCertificateRequest
                    .getValue(), msg + "\n" + FaultUtil.printFaultToString(e));
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

        // Validate that the certificate is of valid length

        if (FederationUtils.getProxyValid(lifetime).after(FederationUtils.getMaxProxyLifetime(conf))) {
            String msg = "The requested certificate lifetime exceeds the maximum certificate lifetime (hrs="
                + conf.getUserCertificateLifetime().getHours() + ", mins="
                + conf.getUserCertificateLifetime().getMinutes() + ", sec="
                + conf.getUserCertificateLifetime().getSeconds() + ")";
            this.eventManager.logEvent(usr.getGridId(), AuditConstants.SYSTEM_ID,
                FederationAudit.InvalidUserCertificateRequest.getValue(), msg);
            UserPolicyFault fault = new UserPolicyFault();
            fault.setFaultString(msg);
            throw fault;
        }

        // Run the policy
        AccountPolicy policy = null;
        try {
            Class c = Class.forName(idp.getUserPolicyClass());
            policy = (AccountPolicy) c.newInstance();
            policy.configure(conf, um);

        } catch (Exception e) {
            String msg = "An unexpected error occurred in creating an instance of the user policy "
                + idp.getUserPolicyClass() + ".";
            this.eventManager.logEvent(usr.getGridId(), AuditConstants.SYSTEM_ID,
                FederationAudit.InvalidUserCertificateRequest.getValue(), msg + "\n" + FaultUtil.printFaultToString(e));
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
        policy.applyPolicy(idp, usr);

        // Check to see if authorized
        try {
            this.verifyActiveUser(usr);
        } catch (PermissionDeniedFault e) {
            this.eventManager.logEvent(usr.getGridId(), AuditConstants.SYSTEM_ID,
                FederationAudit.InvalidUserCertificateRequest.getValue(), Utils.getExceptionMessage(e));
            throw e;
        }

        // create user certificate
        try {
            String caSubject = ca.getCACertificate().getSubjectDN().getName();
            String sub = um.getUserSubject(caSubject, idp, usr.getUID());
            Calendar c1 = new GregorianCalendar();
            c1.add(Calendar.SECOND, CERTIFICATE_START_OFFSET_SECONDS);
            Date start = c1.getTime();
            Calendar c2 = new GregorianCalendar();
            c2.add(Calendar.HOUR, lifetime.getHours());
            c2.add(Calendar.MINUTE, lifetime.getMinutes());
            c2.add(Calendar.SECOND, lifetime.getSeconds());
            Date end = c2.getTime();
            X509Certificate userCert = ca.signCertificate(sub, publicKey, start, end);
            userCertificateManager.addUserCertifcate(usr.getGridId(), userCert);
            this.eventManager.logEvent(usr.getGridId(), AuditConstants.SYSTEM_ID,
                FederationAudit.SuccessfulUserCertificateRequest.getValue(), "User certificate ("
                    + userCert.getSerialNumber() + ") successfully issued for " + usr.getGridId() + ".");
            return userCert;
        } catch (Exception e) {
            String msg = "An unexpected error occurred in creating a certificate for the user " + usr.getGridId() + ".";
            this.eventManager.logEvent(usr.getGridId(), AuditConstants.SYSTEM_ID,
                FederationAudit.InvalidUserCertificateRequest.getValue(), msg + "\n" + FaultUtil.printFaultToString(e));
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


    // ///////////////////////////////
    /* HOST CERTIFICATE OPERATIONS */
    // ///////////////////////////////
    public HostCertificateRecord requestHostCertificate(String callerGridId, HostCertificateRequest req)
        throws DorianInternalFault, InvalidHostCertificateRequestFault, InvalidHostCertificateFault,
        PermissionDeniedFault {
        try {
            GridUser caller = getUser(callerGridId);
            verifyActiveUser(caller);
            long id = hostManager.requestHostCertifcate(callerGridId, req);
            this.eventManager.logEvent(String.valueOf(id), callerGridId, FederationAudit.HostCertificateRequested
                .getValue(), "Host certificate requested for " + req.getHostname() + ".");
            HostCertificateRecord record = null;
            if (this.conf.autoHostCertificateApproval()) {
                record = hostManager.approveHostCertifcate(id);
                this.eventManager.logEvent(String.valueOf(id), AuditConstants.SYSTEM_ID,
                    FederationAudit.HostCertificateApproved.getValue(), "The host certificate for the host "
                        + req.getHostname() + " was automatically approved.");
            } else {
                record = hostManager.getHostCertificateRecord(id);
            }

            return record;
        } catch (DorianInternalFault e) {
            String mess = "An unexpected error occurred in requesting a host certificate for the host "
                + req.getHostname() + ":";
            this.eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                FederationAudit.InternalError.getValue(), mess + "\n\n" + FaultUtil.printFaultToString(e));
            throw e;
        } catch (PermissionDeniedFault e) {
            String mess = "Caller not permitted to request a certificate:";
            this.eventManager.logEvent(callerGridId, AuditConstants.SYSTEM_ID, FederationAudit.AccessDenied.getValue(),
                mess + "\n\n" + Utils.getExceptionMessage(e));
            throw e;
        }
    }


    public HostCertificateRecord[] getHostCertificatesForCaller(String callerGridId) throws DorianInternalFault,
        PermissionDeniedFault {
        try {
            GridUser caller = getUser(callerGridId);
            verifyActiveUser(caller);
            List<HostCertificateRecord> list = hostManager.getHostCertificateRecords(callerGridId);
            HostCertificateRecord[] records = new HostCertificateRecord[list.size()];
            for (int i = 0; i < list.size(); i++) {
                records[i] = list.get(i);
            }

            return records;

        } catch (DorianInternalFault e) {
            String mess = "An unexpected error occurred in obtaining the host certificates owned by " + callerGridId
                + ":";
            this.eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                FederationAudit.InternalError.getValue(), mess + "\n\n" + FaultUtil.printFaultToString(e));
            throw e;
        } catch (PermissionDeniedFault e) {
            String mess = "Caller not permitted to obtain a list of host certificates:";
            this.eventManager.logEvent(callerGridId, AuditConstants.SYSTEM_ID, FederationAudit.AccessDenied.getValue(),
                mess + "\n\n" + Utils.getExceptionMessage(e));
            throw e;
        }
    }


    public HostCertificateRecord approveHostCertificate(String callerGridId, long recordId) throws DorianInternalFault,
        InvalidHostCertificateFault, PermissionDeniedFault {
        try {
            GridUser caller = getUser(callerGridId);
            verifyActiveUser(caller);
            verifyAdminUser(caller);
            HostCertificateRecord record = hostManager.approveHostCertifcate(recordId);
            this.eventManager.logEvent(String.valueOf(recordId), callerGridId, FederationAudit.HostCertificateApproved
                .getValue(), "The host certificate for the host " + record.getHost() + " was approved by "
                + callerGridId + ".");
            return record;
        } catch (DorianInternalFault e) {
            String mess = "An unexpected error occurred in approving the host certificate " + recordId + ":";
            this.eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                FederationAudit.InternalError.getValue(), mess + "\n\n" + FaultUtil.printFaultToString(e));
            throw e;
        } catch (PermissionDeniedFault e) {
            String mess = "Caller not permitted to approve host certificates:";
            this.eventManager.logEvent(callerGridId, AuditConstants.SYSTEM_ID, FederationAudit.AccessDenied.getValue(),
                mess + "\n\n" + Utils.getExceptionMessage(e));
            throw e;
        }
    }


    public HostCertificateRecord[] findHostCertificates(String callerGridId, HostCertificateFilter f)
        throws DorianInternalFault, PermissionDeniedFault {
        try {
            GridUser caller = getUser(callerGridId);
            verifyActiveUser(caller);
            verifyAdminUser(caller);
            List<HostCertificateRecord> list = hostManager.findHostCertificates(f);
            HostCertificateRecord[] records = new HostCertificateRecord[list.size()];
            for (int i = 0; i < list.size(); i++) {
                records[i] = list.get(i);
            }
            return records;
        } catch (DorianInternalFault e) {
            String mess = "An unexpected error occurred in searching for host certificates:";
            this.eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                FederationAudit.InternalError.getValue(), mess + "\n\n" + FaultUtil.printFaultToString(e));
            throw e;
        } catch (PermissionDeniedFault e) {
            String mess = "Caller not permitted to search for host certificates:";
            this.eventManager.logEvent(callerGridId, AuditConstants.SYSTEM_ID, FederationAudit.AccessDenied.getValue(),
                mess + "\n\n" + Utils.getExceptionMessage(e));
            throw e;
        }
    }


    public void updateHostCertificateRecord(String callerGridId, HostCertificateUpdate update)
        throws DorianInternalFault, InvalidHostCertificateFault, PermissionDeniedFault {
        try {
            GridUser caller = getUser(callerGridId);
            verifyActiveUser(caller);
            verifyAdminUser(caller);
            // We need to make sure that if the owner changed, that the owner is
            // an
            // active user.
            HostCertificateRecord record = hostManager.getHostCertificateRecord(update.getId());
            if (update.getOwner() != null) {
                if (!record.getOwner().equals(update.getOwner())) {
                    try {
                        verifyActiveUser(getUser(update.getOwner()));
                    } catch (PermissionDeniedFault f) {
                        InvalidHostCertificateFault fault = new InvalidHostCertificateFault();
                        fault.setFaultString("The owner specified does not exist or is not an active user.");
                        throw fault;
                    }
                }
            }
            hostManager.updateHostCertificateRecord(update);
            HostCertificateRecord updated = hostManager.getHostCertificateRecord(update.getId());
            this.eventManager.logEvent(String.valueOf(record.getId()), callerGridId,
                FederationAudit.HostCertificateUpdated.getValue(), ReportUtils.generateReport(record, updated));
        } catch (DorianInternalFault e) {
            String mess = "An unexpected error occurred in updating a host certificate:";
            this.eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                FederationAudit.InternalError.getValue(), mess + "\n\n" + FaultUtil.printFaultToString(e));
            throw e;
        } catch (PermissionDeniedFault e) {
            String mess = "Caller not permitted to update host certificates:";
            this.eventManager.logEvent(callerGridId, AuditConstants.SYSTEM_ID, FederationAudit.AccessDenied.getValue(),
                mess + "\n\n" + Utils.getExceptionMessage(e));
            throw e;
        }
    }


    public HostCertificateRecord renewHostCertificate(String callerGridId, long recordId) throws DorianInternalFault,
        InvalidHostCertificateFault, PermissionDeniedFault {
        try {
            GridUser caller = getUser(callerGridId);
            verifyActiveUser(caller);
            verifyAdminUser(caller);
            HostCertificateRecord record = hostManager.renewHostCertificate(recordId);
            this.eventManager.logEvent(String.valueOf(recordId), callerGridId, FederationAudit.HostCertificateRenewed
                .getValue(), "The host certificate for the host " + record.getHost() + " was renewed by "
                + callerGridId + ".");
            return record;
        } catch (DorianInternalFault e) {
            String mess = "An unexpected error occurred in renewing the host certificate " + recordId + ":";
            this.eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                FederationAudit.InternalError.getValue(), mess + "\n\n" + FaultUtil.printFaultToString(e));
            throw e;
        } catch (PermissionDeniedFault e) {
            String mess = "Caller not permitted to renew the host certificates " + recordId + ":";
            this.eventManager.logEvent(callerGridId, AuditConstants.SYSTEM_ID, FederationAudit.AccessDenied.getValue(),
                mess + "\n\n" + Utils.getExceptionMessage(e));
            throw e;
        }
    }


    public void publishCRL() {
        if (publishCRL) {
            if ((conf.getCRLPublishingList() != null) && (conf.getCRLPublishingList().size() > 0)) {
                Runner runner = new Runner() {
                    public void execute() {
                        synchronized (mutex) {
                            List<String> services = conf.getCRLPublishingList();
                            try {
                                X509CRL crl = getCRL();
                                gov.nih.nci.cagrid.gts.bean.X509CRL x509 = new gov.nih.nci.cagrid.gts.bean.X509CRL();
                                x509.setCrlEncodedString(CertUtil.writeCRL(crl));
                                String authName = ca.getCACertificate().getSubjectDN().getName();
                                for (int i = 0; i < services.size(); i++) {
                                    String uri = services.get(i);
                                    try {
                                        debug("Publishing CRL to the GTS " + uri);
                                        GTSAdminClient client = new GTSAdminClient(uri, null);
                                        client.updateCRL(authName, x509);
                                        debug("Published CRL to the GTS " + uri);
                                        eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                                            FederationAudit.CRLPublished.getValue(), "Published CRL to the GTS " + uri
                                                + ".");
                                    } catch (Exception ex) {
                                        String msg = "Error publishing the CRL to the GTS " + uri + "!!!";
                                        getLog().error(msg, ex);
                                        eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                                            FederationAudit.InternalError.getValue(), msg + "\n"
                                                + FaultUtil.printFaultToString(ex) + "\n\n"
                                                + FaultUtil.printFaultToString(ex));
                                    }

                                }

                            } catch (Exception e) {
                                String msg = "Unexpected Error publishing the CRL!!!";
                                getLog().error(msg, e);
                                eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                                    FederationAudit.InternalError.getValue(), msg + "\n"
                                        + FaultUtil.printFaultToString(e) + "\n\n" + FaultUtil.printFaultToString(e));
                            }
                        }
                    }
                };
                try {
                    threadManager.executeInBackground(runner);
                } catch (Exception t) {
                    t.getMessage();
                }
            }
        }
    }


    public X509CRL getCRL() throws DorianInternalFault {
        Map<Long, CRLEntry> list = new HashMap<Long, CRLEntry>();

        Set<String> users = this.um.getDisabledUsers();
        Iterator<String> itr = users.iterator();
        while (itr.hasNext()) {
            String gid = itr.next();
            List<BigInteger> userCerts = this.userCertificateManager.getActiveCertificates(gid);
            for (int i = 0; i < userCerts.size(); i++) {
                Long sn = userCerts.get(i).longValue();
                if (!list.containsKey(sn)) {
                    list.put(sn, new CRLEntry(userCerts.get(i), CRLReason.PRIVILEGE_WITHDRAWN));
                }
            }

            List<Long> hostCerts = this.hostManager.getHostCertificateRecordsSerialNumbers(gid);
            for (int i = 0; i < hostCerts.size(); i++) {
                if (!list.containsKey(hostCerts.get(i))) {
                    CRLEntry entry = new CRLEntry(BigInteger.valueOf(hostCerts.get(i).longValue()),
                        CRLReason.PRIVILEGE_WITHDRAWN);
                    list.put(hostCerts.get(i), entry);
                }
            }
        }

        List<BigInteger> compromisedUserCerts = this.userCertificateManager.getCompromisedCertificates();
        for (int i = 0; i < compromisedUserCerts.size(); i++) {
            Long sn = compromisedUserCerts.get(i).longValue();
            if (!list.containsKey(sn)) {
                list.put(sn, new CRLEntry(compromisedUserCerts.get(i), CRLReason.PRIVILEGE_WITHDRAWN));
            }
        }

        List<Long> hosts = this.hostManager.getDisabledHostCertificatesSerialNumbers();
        for (int i = 0; i < hosts.size(); i++) {
            if (!list.containsKey(hosts.get(i))) {
                CRLEntry entry = new CRLEntry(BigInteger.valueOf(hosts.get(i).longValue()),
                    CRLReason.PRIVILEGE_WITHDRAWN);
                list.put(hosts.get(i), entry);
            }
        }

        List<Long> blist = this.blackList.getBlackList();

        for (int i = 0; i < blist.size(); i++) {
            if (!list.containsKey(blist.get(i))) {
                CRLEntry entry = new CRLEntry(BigInteger.valueOf(blist.get(i).longValue()),
                    CRLReason.PRIVILEGE_WITHDRAWN);
                list.put(blist.get(i), entry);
            }
        }

        CRLEntry[] entries = new CRLEntry[list.size()];
        Iterator<CRLEntry> itr2 = list.values().iterator();
        int count = 0;
        while (itr2.hasNext()) {
            entries[count] = itr2.next();
            count++;
        }
        try {
            X509CRL crl = ca.getCRL(entries);
            return crl;

        } catch (Exception e) {
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("Unexpected error obtaining the CRL.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addDescription(Utils.getExceptionMessage(e));
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        }

    }


    private GridUser getUser(String gridId) throws DorianInternalFault, PermissionDeniedFault {
        try {
            return um.getUser(gridId);
        } catch (InvalidUserFault f) {
            PermissionDeniedFault fault = new PermissionDeniedFault();
            fault.setFaultString("You are not a valid user!!!");
            throw fault;
        }
    }


    private void verifyAdminUser(GridUser usr) throws DorianInternalFault, PermissionDeniedFault {
        try {
            if (administrators.isMember(usr.getGridId())) {
                return;
            } else {
                PermissionDeniedFault fault = new PermissionDeniedFault();
                fault.setFaultString("You are NOT an Administrator!!!");
                throw fault;
            }

        } catch (GroupException e) {
            logError(e.getMessage(), e);
            DorianInternalFault fault = new DorianInternalFault();
            fault
                .setFaultString("An unexpected error occurred in determining if the user is a member of the administrators group.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        }
    }


    private void verifyActiveUser(GridUser usr) throws DorianInternalFault, PermissionDeniedFault {

        try {
            TrustedIdP idp = this.tm.getTrustedIdPById(usr.getIdPId());

            if (!idp.getStatus().equals(TrustedIdPStatus.Active)) {
                PermissionDeniedFault fault = new PermissionDeniedFault();
                fault.setFaultString("Access for your Identity Provider has been suspended!!!");
                throw fault;
            }
        } catch (InvalidTrustedIdPFault f) {
            PermissionDeniedFault fault = new PermissionDeniedFault();
            fault.setFaultString("Unexpected error in determining your Identity Provider has been suspended!!!");
            throw fault;
        }

        if (!usr.getUserStatus().equals(GridUserStatus.Active)) {
            if (usr.getUserStatus().equals(GridUserStatus.Suspended)) {
                PermissionDeniedFault fault = new PermissionDeniedFault();
                fault.setFaultString("The account has been suspended.");
                throw fault;

            } else if (usr.getUserStatus().equals(GridUserStatus.Rejected)) {
                PermissionDeniedFault fault = new PermissionDeniedFault();
                fault.setFaultString("The request for an account was rejected.");
                throw fault;

            } else if (usr.getUserStatus().equals(GridUserStatus.Pending)) {
                PermissionDeniedFault fault = new PermissionDeniedFault();
                fault.setFaultString("The request for an account has not been reviewed.");
                throw fault;
            } else {
                PermissionDeniedFault fault = new PermissionDeniedFault();
                fault.setFaultString("Unknown Reason");
                throw fault;
            }
        }

    }


    protected UserManager getUserManager() {
        return um;
    }


    private String getAttribute(SAMLAssertion saml, String namespace, String name) throws InvalidAssertionFault {
        Iterator itr = saml.getStatements();
        while (itr.hasNext()) {
            Object o = itr.next();
            if (o instanceof SAMLAttributeStatement) {
                SAMLAttributeStatement att = (SAMLAttributeStatement) o;
                Iterator attItr = att.getAttributes();
                while (attItr.hasNext()) {
                    SAMLAttribute a = (SAMLAttribute) attItr.next();
                    if ((a.getNamespace().equals(namespace)) && (a.getName().equals(name))) {
                        Iterator vals = a.getValues();
                        while (vals.hasNext()) {

                            String val = Utils.clean((String) vals.next());
                            if (val != null) {
                                return val;
                            }
                        }
                    }
                }
            }
        }
        InvalidAssertionFault fault = new InvalidAssertionFault();
        fault.setFaultString("The assertion does not contain the required attribute, " + namespace + ":" + name);
        throw fault;
    }


    private SAMLAuthenticationStatement getAuthenticationStatement(SAMLAssertion saml) throws InvalidAssertionFault {
        Iterator itr = saml.getStatements();
        SAMLAuthenticationStatement auth = null;
        while (itr.hasNext()) {
            Object o = itr.next();
            if (o instanceof SAMLAuthenticationStatement) {
                if (auth != null) {
                    InvalidAssertionFault fault = new InvalidAssertionFault();
                    fault.setFaultString("The assertion specified contained more that one authentication statement.");
                    throw fault;
                }
                auth = (SAMLAuthenticationStatement) o;
            }
        }
        if (auth == null) {
            InvalidAssertionFault fault = new InvalidAssertionFault();
            fault.setFaultString("No authentication statement specified in the assertion provided.");
            throw fault;
        }
        return auth;
    }


    public void clearDatabase() throws DorianInternalFault {
        this.um.clearDatabase();
        this.tm.clearDatabase();
        try {
            this.groupManager.clearDatabase();
        } catch (GroupException e) {
            logError(e.getMessage(), e);
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("An unexpected error occurred in deleting the groups database.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        }
        this.userCertificateManager.clearDatabase();
        this.hostManager.clearDatabase();
        this.blackList.clearDatabase();
        try {
            ca.clearCertificateAuthority();
        } catch (CertificateAuthorityFault e) {
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString(e.getFaultString());
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        }
        try {
            this.gridAccountAuditor.clear();
            this.federationAuditor.clear();
            this.hostAuditor.clear();
            this.userCertificateAuditor.clear();
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


    public TrustedIdentityProviders getTrustedIdentityProviders() throws DorianInternalFault {
        try {
            TrustedIdentityProviders idps = new TrustedIdentityProviders();
            TrustedIdP[] list1 = this.tm.getTrustedIdPs();
            if (list1 != null) {
                List<TrustedIdentityProvider> list2 = new ArrayList<TrustedIdentityProvider>();
                for (int i = 0; i < list1.length; i++) {
                    if (list1[i].getStatus().equals(TrustedIdPStatus.Active)) {
                        TrustedIdentityProvider idp = new TrustedIdentityProvider();
                        idp.setName(list1[i].getName());
                        idp.setDisplayName(list1[i].getDisplayName());
                        idp.setAuthenticationServiceURL(list1[i].getAuthenticationServiceURL());
                        idp.setAuthenticationServiceIdentity(list1[i].getAuthenticationServiceIdentity());
                        list2.add(idp);
                    }
                }

                TrustedIdentityProvider[] list3 = new TrustedIdentityProvider[list2.size()];
                for (int i = 0; i < list2.size(); i++) {
                    list3[i] = list2.get(i);
                }
                idps.setTrustedIdentityProvider(list3);
            }
            return idps;

        } catch (DorianInternalFault e) {
            String mess = "An unexpected error occurred in obtaining a list of trusted identity providers :";
            this.eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                FederationAudit.InternalError.getValue(), mess + "\n\n" + FaultUtil.printFaultToString(e));
            throw e;
        }
    }


    public List<UserCertificateRecord> findUserCertificateRecords(String callerIdentity, UserCertificateFilter f)
        throws DorianInternalFault, InvalidUserCertificateFault, PermissionDeniedFault {
        try {
            GridUser caller = getUser(callerIdentity);
            verifyActiveUser(caller);
            verifyAdminUser(caller);
            return this.userCertificateManager.findUserCertificateRecords(f);
        } catch (DorianInternalFault e) {
            String mess = "An unexpected error occurred in searching for user certificates:";

            this.eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                FederationAudit.InternalError.getValue(), mess + "\n\n" + FaultUtil.printFaultToString(e));
            throw e;
        } catch (PermissionDeniedFault e) {
            String mess = "Caller not permitted to search for user certificates:";
            this.eventManager.logEvent(callerIdentity, AuditConstants.SYSTEM_ID, FederationAudit.AccessDenied
                .getValue(), mess + "\n\n" + Utils.getExceptionMessage(e));
            throw e;
        }
    }


    public void updateUserCertificateRecord(String callerIdentity, UserCertificateUpdate update)
        throws DorianInternalFault, InvalidUserCertificateFault, PermissionDeniedFault {
        try {
            GridUser caller = getUser(callerIdentity);
            verifyActiveUser(caller);
            verifyAdminUser(caller);
            UserCertificateRecord original = this.userCertificateManager.getUserCertificateRecord(update
                .getSerialNumber());
            this.userCertificateManager.updateUserCertificateRecord(update);
            UserCertificateRecord updated = this.userCertificateManager.getUserCertificateRecord(update
                .getSerialNumber());
            this.eventManager.logEvent(String.valueOf(original.getSerialNumber()), callerIdentity,
                FederationAudit.UserCertificateUpdated.getValue(), ReportUtils.generateReport(original, updated));
        } catch (DorianInternalFault e) {
            String mess = "An unexpected error occurred in updating a user certificate:";
            this.eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                FederationAudit.InternalError.getValue(), mess + "\n\n" + FaultUtil.printFaultToString(e));
            throw e;
        } catch (PermissionDeniedFault e) {
            String mess = "Caller not permitted to update user certificates:";
            this.eventManager.logEvent(callerIdentity, AuditConstants.SYSTEM_ID, FederationAudit.AccessDenied
                .getValue(), mess + "\n\n" + Utils.getExceptionMessage(e));
            throw e;
        }
    }


    public void removeUserCertificate(String callerIdentity, long serialNumber) throws DorianInternalFault,
        InvalidUserCertificateFault, PermissionDeniedFault {
        try {
            GridUser caller = getUser(callerIdentity);
            verifyActiveUser(caller);
            verifyAdminUser(caller);
            this.userCertificateManager.removeCertificate(serialNumber);
            this.eventManager.logEvent(String.valueOf(serialNumber), callerIdentity,
                FederationAudit.UserCertificateRemoved.getValue(), "User certificate (" + serialNumber
                    + ") removed by " + callerIdentity + ".");
        } catch (DorianInternalFault e) {
            String mess = "An unexpected error occurred in removing the host certificate " + serialNumber + ":";
            this.eventManager.logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                FederationAudit.InternalError.getValue(), mess + "\n\n" + FaultUtil.printFaultToString(e));
            throw e;
        } catch (PermissionDeniedFault e) {
            String mess = "Caller not permitted to remove host certificates:";
            this.eventManager.logEvent(callerIdentity, AuditConstants.SYSTEM_ID, FederationAudit.AccessDenied
                .getValue(), mess + "\n\n" + Utils.getExceptionMessage(e));
            throw e;
        }
    }


    public List<FederationAuditRecord> performAudit(String callerIdentity, FederationAuditFilter f)
        throws DorianInternalFault, PermissionDeniedFault {
        try {
            GridUser caller = getUser(callerIdentity);
            verifyActiveUser(caller);
            verifyAdminUser(caller);
            List<EventAuditor> handlers = new ArrayList<EventAuditor>();
            handlers.add(this.federationAuditor);
            handlers.add(this.hostAuditor);
            handlers.add(this.gridAccountAuditor);
            handlers.add(this.userCertificateAuditor);
            List<FederationAuditRecord> list = new ArrayList<FederationAuditRecord>();
            for (int i = 0; i < handlers.size(); i++) {
                EventAuditor eh = handlers.get(i);
                if (f == null) {
                    f = new FederationAuditFilter();
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
                        FederationAuditRecord r = new FederationAuditRecord();
                        r.setTargetId(e.getTargetId());
                        r.setReportingPartyId(e.getReportingPartyId());
                        r.setAuditType(FederationAudit.fromValue(e.getEventType()));
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
            this.eventManager.logEvent(callerIdentity, AuditConstants.SYSTEM_ID, FederationAudit.AccessDenied
                .getValue(), mess + "\n\n" + Utils.getExceptionMessage(e));
            throw e;
        }
    }
}
