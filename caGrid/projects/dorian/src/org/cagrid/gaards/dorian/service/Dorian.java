package org.cagrid.gaards.dorian.service;

import gov.nih.nci.cagrid.common.FaultHelper;
import gov.nih.nci.cagrid.opensaml.SAMLAssertion;

import java.rmi.RemoteException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.List;

import org.cagrid.gaards.authentication.BasicAuthentication;
import org.cagrid.gaards.authentication.Credential;
import org.cagrid.gaards.authentication.faults.AuthenticationProviderFault;
import org.cagrid.gaards.authentication.faults.CredentialNotSupportedFault;
import org.cagrid.gaards.authentication.faults.InvalidCredentialFault;
import org.cagrid.gaards.dorian.ca.CertificateAuthority;
import org.cagrid.gaards.dorian.common.AuditConstants;
import org.cagrid.gaards.dorian.common.LoggingObject;
import org.cagrid.gaards.dorian.common.SAMLConstants;
import org.cagrid.gaards.dorian.federation.AutoApprovalPolicy;
import org.cagrid.gaards.dorian.federation.CertificateLifetime;
import org.cagrid.gaards.dorian.federation.FederationAuditFilter;
import org.cagrid.gaards.dorian.federation.FederationAuditRecord;
import org.cagrid.gaards.dorian.federation.FederationDefaults;
import org.cagrid.gaards.dorian.federation.GridUser;
import org.cagrid.gaards.dorian.federation.GridUserFilter;
import org.cagrid.gaards.dorian.federation.GridUserPolicy;
import org.cagrid.gaards.dorian.federation.GridUserRecord;
import org.cagrid.gaards.dorian.federation.GridUserSearchCriteria;
import org.cagrid.gaards.dorian.federation.GridUserStatus;
import org.cagrid.gaards.dorian.federation.HostCertificateFilter;
import org.cagrid.gaards.dorian.federation.HostCertificateRecord;
import org.cagrid.gaards.dorian.federation.HostCertificateRequest;
import org.cagrid.gaards.dorian.federation.HostCertificateUpdate;
import org.cagrid.gaards.dorian.federation.HostRecord;
import org.cagrid.gaards.dorian.federation.HostSearchCriteria;
import org.cagrid.gaards.dorian.federation.IdentityFederationManager;
import org.cagrid.gaards.dorian.federation.IdentityFederationProperties;
import org.cagrid.gaards.dorian.federation.SAMLAttributeDescriptor;
import org.cagrid.gaards.dorian.federation.SAMLAuthenticationMethod;
import org.cagrid.gaards.dorian.federation.TrustedIdP;
import org.cagrid.gaards.dorian.federation.TrustedIdPStatus;
import org.cagrid.gaards.dorian.federation.TrustedIdentityProviders;
import org.cagrid.gaards.dorian.federation.UserCertificateFilter;
import org.cagrid.gaards.dorian.federation.UserCertificateRecord;
import org.cagrid.gaards.dorian.federation.UserCertificateUpdate;
import org.cagrid.gaards.dorian.idp.Application;
import org.cagrid.gaards.dorian.idp.IdentityProvider;
import org.cagrid.gaards.dorian.idp.IdentityProviderAudit;
import org.cagrid.gaards.dorian.idp.IdentityProviderAuditFilter;
import org.cagrid.gaards.dorian.idp.IdentityProviderAuditRecord;
import org.cagrid.gaards.dorian.idp.LocalUser;
import org.cagrid.gaards.dorian.idp.LocalUserFilter;
import org.cagrid.gaards.dorian.idp.UserManager;
import org.cagrid.gaards.dorian.stubs.types.DorianInternalFault;
import org.cagrid.gaards.dorian.stubs.types.InvalidAssertionFault;
import org.cagrid.gaards.dorian.stubs.types.InvalidHostCertificateFault;
import org.cagrid.gaards.dorian.stubs.types.InvalidHostCertificateRequestFault;
import org.cagrid.gaards.dorian.stubs.types.InvalidTrustedIdPFault;
import org.cagrid.gaards.dorian.stubs.types.InvalidUserCertificateFault;
import org.cagrid.gaards.dorian.stubs.types.InvalidUserFault;
import org.cagrid.gaards.dorian.stubs.types.InvalidUserPropertyFault;
import org.cagrid.gaards.dorian.stubs.types.NoSuchUserFault;
import org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault;
import org.cagrid.gaards.dorian.stubs.types.UserPolicyFault;
import org.cagrid.gaards.pki.CertUtil;
import org.cagrid.tools.database.Database;
import org.cagrid.tools.events.EventManager;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class Dorian extends LoggingObject {

    private Database db;

    public static final String IDP_ADMIN_USER_ID = "dorian";

    public static final String IDP_ADMIN_PASSWORD = "DorianAdmin$1";

    private CertificateAuthority ca;

    private IdentityProvider identityProvider;

    private IdentityFederationManager ifm;

    private IdentityFederationProperties ifsConfiguration;

    private DorianProperties configuration;

    private PropertyManager properties;

    private EventManager eventManager;


    public Dorian(DorianProperties conf, String serviceId) throws DorianInternalFault {
        this(conf, serviceId, false);
    }


    public Dorian(DorianProperties conf, String serviceId, boolean ignoreCRL) throws DorianInternalFault {
        try {

            this.configuration = conf;
            this.eventManager = this.configuration.getEventManager();
            UserManager.ADMIN_USER_ID = IDP_ADMIN_USER_ID;
            UserManager.ADMIN_PASSWORD = IDP_ADMIN_PASSWORD;
            this.db = this.configuration.getDatabase();
            this.db.createDatabaseIfNeeded();
            this.properties = new PropertyManager(this.db);
            if (this.properties.getCertificateAuthorityType() == null) {
                this.properties.setCertificateAuthorityType(configuration.getCertificateAuthority().getClass()
                    .getName());
            } else if (!this.properties.getCertificateAuthorityType().equals(
                configuration.getCertificateAuthority().getClass().getName())) {
                DorianInternalFault fault = new DorianInternalFault();
                fault.setFaultString("Certificate Authority type conflict detected, this Dorian was created using a "
                    + configuration.getCertificateAuthority().getClass().getName()
                    + " CA but the configuration file specifies the usage of a "
                    + configuration.getCertificateAuthority().getClass().getName() + " CA.");
                throw fault;
            }
            this.ca = this.configuration.getCertificateAuthority();
            this.identityProvider = new IdentityProvider(configuration.getIdentityProviderProperties(), db, ca,
                this.eventManager);

            TrustedIdP idp = new TrustedIdP();
            idp.setName(conf.getIdentityProviderProperties().getName());
            idp.setDisplayName(conf.getIdentityProviderProperties().getName());
            SAMLAuthenticationMethod[] methods = new SAMLAuthenticationMethod[1];
            methods[0] = SAMLAuthenticationMethod.fromString("urn:oasis:names:tc:SAML:1.0:am:password");
            idp.setAuthenticationMethod(methods);
            idp.setUserPolicyClass(AutoApprovalPolicy.class.getName());
            idp.setIdPCertificate(CertUtil.writeCertificate(this.identityProvider.getIdPCertificate()));
            idp.setStatus(TrustedIdPStatus.Active);
            idp.setAuthenticationServiceURL(serviceId);
            SAMLAttributeDescriptor uid = new SAMLAttributeDescriptor();
            uid.setNamespaceURI(SAMLConstants.UID_ATTRIBUTE_NAMESPACE);
            uid.setName(SAMLConstants.UID_ATTRIBUTE);
            idp.setUserIdAttributeDescriptor(uid);

            SAMLAttributeDescriptor firstName = new SAMLAttributeDescriptor();
            firstName.setNamespaceURI(SAMLConstants.FIRST_NAME_ATTRIBUTE_NAMESPACE);
            firstName.setName(SAMLConstants.FIRST_NAME_ATTRIBUTE);
            idp.setFirstNameAttributeDescriptor(firstName);

            SAMLAttributeDescriptor lastName = new SAMLAttributeDescriptor();
            lastName.setNamespaceURI(SAMLConstants.LAST_NAME_ATTRIBUTE_NAMESPACE);
            lastName.setName(SAMLConstants.LAST_NAME_ATTRIBUTE);
            idp.setLastNameAttributeDescriptor(lastName);

            SAMLAttributeDescriptor email = new SAMLAttributeDescriptor();
            email.setNamespaceURI(SAMLConstants.EMAIL_ATTRIBUTE_NAMESPACE);
            email.setName(SAMLConstants.EMAIL_ATTRIBUTE);
            idp.setEmailAttributeDescriptor(email);

            GridUser usr = null;
            try {
                LocalUser idpUsr = identityProvider.getUser(IDP_ADMIN_USER_ID, IDP_ADMIN_USER_ID);
                usr = new GridUser();
                usr.setUID(idpUsr.getUserId());
                usr.setFirstName(idpUsr.getFirstName());
                usr.setLastName(idpUsr.getLastName());
                usr.setEmail(idpUsr.getEmail());
                usr.setUserStatus(GridUserStatus.Active);
            } catch (Exception e) {
            }

            ifsConfiguration = configuration.getIdentityFederationProperties();
            FederationDefaults defaults = new FederationDefaults(idp, usr);
            this.ifm = new IdentityFederationManager(ifsConfiguration, db, properties, ca, this.eventManager, defaults,
                ignoreCRL);

            if (!this.properties.getVersion().equals(PropertyManager.CURRENT_VERSION)) {
                DorianInternalFault fault = new DorianInternalFault();
                fault.setFaultString("Version conflict detected, your are running Dorian "
                    + PropertyManager.CURRENT_VERSION + " against a Dorian " + properties.getVersion() + " database.");
                throw fault;
            }
        } catch (Exception e) {
            logError(e.getMessage(), e);
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("An unexpected error occurred in configuring the service.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        }
    }


    public DorianProperties getConfiguration() {
        return configuration;
    }


    public Database getDatabase() {
        return this.db;
    }


    public X509Certificate getCACertificate() throws DorianInternalFault {
        try {
            return this.ca.getCACertificate();
        } catch (Exception e) {
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("An unexpected error occurred, in obtaining the CA certificate.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        }
    }


    public X509Certificate getIdPCertificate() throws DorianInternalFault {
        return identityProvider.getIdPCertificate();
    }


    public void changeLocalUserPassword(BasicAuthentication credential, String newPassword) throws DorianInternalFault,
        PermissionDeniedFault, InvalidUserPropertyFault {
        this.identityProvider.changePassword(credential, newPassword);
    }


    public LocalUser[] findLocalUsers(String gridIdentity, LocalUserFilter filter) throws DorianInternalFault,
        PermissionDeniedFault {
        String uid = null;
        try {
            uid = ifm.getUserIdVerifyTrustedIdP(identityProvider.getIdPCertificate(), gridIdentity);
        } catch (Exception e) {
            String message = "Permission to find local users was denied, caller is not a valid user.";
            this.eventManager.logEvent(gridIdentity, AuditConstants.SYSTEM_ID, IdentityProviderAudit.LocalAccessDenied
                .getValue(), message);
            PermissionDeniedFault fault = new PermissionDeniedFault();
            fault.setFaultString(message);
            throw fault;
        }
        return this.identityProvider.findUsers(uid, filter);
    }


    public void updateLocalUser(String gridIdentity, LocalUser u) throws DorianInternalFault, PermissionDeniedFault,
        NoSuchUserFault, InvalidUserPropertyFault {
        String uid = null;
        try {
            uid = ifm.getUserIdVerifyTrustedIdP(identityProvider.getIdPCertificate(), gridIdentity);
        } catch (Exception e) {
            String message = "Permission to update a user was denied, caller is not a valid user.";
            this.eventManager.logEvent(gridIdentity, AuditConstants.SYSTEM_ID, IdentityProviderAudit.LocalAccessDenied
                .getValue(), message);
            PermissionDeniedFault fault = new PermissionDeniedFault();
            fault.setFaultString(message);
            throw fault;
        }
        this.identityProvider.updateUser(uid, u);
    }


    public void removeLocalUser(String gridIdentity, String userId) throws DorianInternalFault, PermissionDeniedFault {
        String uid = null;
        try {
            uid = ifm.getUserIdVerifyTrustedIdP(identityProvider.getIdPCertificate(), gridIdentity);
        } catch (Exception e) {
            String message = "Permission to remove a user was denied, caller is not a valid user.";
            this.eventManager.logEvent(gridIdentity, AuditConstants.SYSTEM_ID, IdentityProviderAudit.LocalAccessDenied
                .getValue(), message);
            PermissionDeniedFault fault = new PermissionDeniedFault();
            fault.setFaultString(message);
            throw fault;
        }
        this.identityProvider.removeUser(uid, userId);
        this.ifm.removeUserByLocalIdIfExists(identityProvider.getIdPCertificate(), userId);
    }


    public List<IdentityProviderAuditRecord> performIdentityProviderAudit(String gridIdentity,
        IdentityProviderAuditFilter f) throws DorianInternalFault, PermissionDeniedFault {
        String uid = null;
        try {
            uid = ifm.getUserIdVerifyTrustedIdP(identityProvider.getIdPCertificate(), gridIdentity);
        } catch (Exception e) {
            String message = "Permission to perform an audit was denied, caller is not a valid user.";
            this.eventManager.logEvent(gridIdentity, AuditConstants.SYSTEM_ID, IdentityProviderAudit.LocalAccessDenied
                .getValue(), message);
            PermissionDeniedFault fault = new PermissionDeniedFault();
            fault.setFaultString(message);
            throw fault;
        }
        return this.identityProvider.performAudit(uid, f);
    }


    public SAMLAssertion authenticate(Credential credential) throws AuthenticationProviderFault,
        InvalidCredentialFault, CredentialNotSupportedFault {
        return this.identityProvider.authenticate(credential);
    }


    public String registerLocalUser(Application a) throws DorianInternalFault, InvalidUserPropertyFault {
        return this.identityProvider.register(a);
    }


    /** *************** Federation FUNCTIONS ********************** */

    public GridUserPolicy[] getGridUserPolicies(String callerGridIdentity) throws DorianInternalFault,
        PermissionDeniedFault {
        return ifm.getUserPolicies(callerGridIdentity);
    }


    public X509Certificate requestUserCertificate(SAMLAssertion saml, PublicKey publicKey, CertificateLifetime lifetime)
        throws DorianInternalFault, InvalidAssertionFault, UserPolicyFault, PermissionDeniedFault {
        return this.ifm.requestUserCertificate(saml, publicKey, lifetime);
    }


    public TrustedIdP[] getTrustedIdPs(String callerGridIdentity) throws DorianInternalFault, PermissionDeniedFault {
        return ifm.getTrustedIdPs(callerGridIdentity);
    }


    public TrustedIdP addTrustedIdP(String callerGridIdentity, TrustedIdP idp) throws DorianInternalFault,
        InvalidTrustedIdPFault, PermissionDeniedFault {
        return ifm.addTrustedIdP(callerGridIdentity, idp);
    }


    public void updateTrustedIdP(String callerGridIdentity, TrustedIdP idp) throws DorianInternalFault,
        InvalidTrustedIdPFault, PermissionDeniedFault {
        ifm.updateTrustedIdP(callerGridIdentity, idp);
    }


    public void removeTrustedIdP(String callerGridIdentity, TrustedIdP idp) throws DorianInternalFault,
        InvalidTrustedIdPFault, PermissionDeniedFault {
        ifm.removeTrustedIdP(callerGridIdentity, idp.getId());
    }


    public GridUser[] findGridUsers(String callerGridIdentity, GridUserFilter filter) throws DorianInternalFault,
        PermissionDeniedFault {
        return ifm.findUsers(callerGridIdentity, filter);
    }


    public void updateGridUser(String callerGridIdentity, GridUser usr) throws DorianInternalFault, InvalidUserFault,
        PermissionDeniedFault {
        ifm.updateUser(callerGridIdentity, usr);
    }


    public void removeGridUser(String callerGridIdentity, GridUser user) throws DorianInternalFault, InvalidUserFault,
        PermissionDeniedFault {
        ifm.removeUser(callerGridIdentity, user);
    }


    public void addAdmin(String callerGridIdentity, String gridIdentity) throws RemoteException, DorianInternalFault,
        PermissionDeniedFault {
        ifm.addAdmin(callerGridIdentity, gridIdentity);
    }


    public void removeAdmin(String callerGridIdentity, String gridIdentity) throws RemoteException,
        DorianInternalFault, PermissionDeniedFault {
        ifm.removeAdmin(callerGridIdentity, gridIdentity);
    }


    public String[] getAdmins(String callerGridIdentity) throws RemoteException, DorianInternalFault,
        PermissionDeniedFault {
        return ifm.getAdmins(callerGridIdentity);
    }


    public HostCertificateRecord requestHostCertificate(String callerGridId, HostCertificateRequest req)
        throws DorianInternalFault, InvalidHostCertificateRequestFault, InvalidHostCertificateFault,
        PermissionDeniedFault {
        return ifm.requestHostCertificate(callerGridId, req);
    }


    public HostCertificateRecord[] getOwnedHostCertificates(String callerGridId) throws DorianInternalFault,
        PermissionDeniedFault {
        return ifm.getHostCertificatesForCaller(callerGridId);

    }


    public HostCertificateRecord approveHostCertificate(String callerGridId, long recordId) throws DorianInternalFault,
        InvalidHostCertificateFault, PermissionDeniedFault {
        return ifm.approveHostCertificate(callerGridId, recordId);
    }


    public HostCertificateRecord[] findHostCertificates(String callerGridId, HostCertificateFilter hostCertificateFilter)
        throws DorianInternalFault, PermissionDeniedFault {
        return ifm.findHostCertificates(callerGridId, hostCertificateFilter);
    }


    public void updateHostCertificateRecord(String callerGridId, HostCertificateUpdate update)
        throws DorianInternalFault, InvalidHostCertificateFault, PermissionDeniedFault {
        ifm.updateHostCertificateRecord(callerGridId, update);
    }


    public HostCertificateRecord renewHostCertificate(String callerGridId, long recordId) throws DorianInternalFault,
        InvalidHostCertificateFault, PermissionDeniedFault {
        return ifm.renewHostCertificate(callerGridId, recordId);
    }


    public boolean doesLocalUserExist(String userId) throws DorianInternalFault {
        return this.identityProvider.doesUserExist(userId);
    }


    public void clearDatabase() throws DorianInternalFault {
        this.identityProvider.clearDatabase();
        this.ifm.clearDatabase();
    }


    public TrustedIdentityProviders getTrustedIdentityProviders() throws DorianInternalFault {
        return this.ifm.getTrustedIdentityProviders();
    }


    public List<UserCertificateRecord> findUserCertificateRecords(String callerIdentity, UserCertificateFilter f)
        throws DorianInternalFault, InvalidUserCertificateFault, PermissionDeniedFault {
        return this.ifm.findUserCertificateRecords(callerIdentity, f);
    }


    public void updateUserCertificateRecord(String callerIdentity, UserCertificateUpdate update)
        throws DorianInternalFault, InvalidUserCertificateFault, PermissionDeniedFault {
        this.ifm.updateUserCertificateRecord(callerIdentity, update);
    }


    public void removeUserCertificate(String callerIdentity, long serialNumber) throws DorianInternalFault,
        InvalidUserCertificateFault, PermissionDeniedFault {
        this.ifm.removeUserCertificate(callerIdentity, serialNumber);
    }


    public List<FederationAuditRecord> performFederationAudit(String callerIdentity, FederationAuditFilter f)
        throws DorianInternalFault, PermissionDeniedFault {
        return this.ifm.performAudit(callerIdentity, f);
    }


    public List<GridUserRecord> userSearch(String callerIdentity, GridUserSearchCriteria criteria)
        throws RemoteException, DorianInternalFault, PermissionDeniedFault {
        return this.ifm.userSearch(callerIdentity, criteria);
    }


    public List<HostRecord> hostSearch(String callerIdentity, HostSearchCriteria criteria) throws RemoteException,
        DorianInternalFault, PermissionDeniedFault {
        return this.ifm.hostSearch(callerIdentity, criteria);
    }

}
