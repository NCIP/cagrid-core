package org.cagrid.gaards.dorian.service;

import gov.nih.nci.cagrid.authentication.stubs.types.InvalidCredentialFault;
import gov.nih.nci.cagrid.common.FaultHelper;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.opensaml.SAMLAssertion;

import java.rmi.RemoteException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.naming.InitialContext;
import javax.xml.namespace.QName;

import org.apache.axis.MessageContext;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.cagrid.gaards.authentication.AuthenticationProfiles;
import org.cagrid.gaards.authentication.BasicAuthentication;
import org.cagrid.gaards.authentication.common.AuthenticationProfile;
import org.cagrid.gaards.authentication.faults.AuthenticationProviderFault;
import org.cagrid.gaards.authentication.faults.CredentialNotSupportedFault;
import org.cagrid.gaards.dorian.common.AuditConstants;
import org.cagrid.gaards.dorian.federation.CertificateLifetime;
import org.cagrid.gaards.dorian.federation.FederationAudit;
import org.cagrid.gaards.dorian.federation.FederationAuditRecord;
import org.cagrid.gaards.dorian.federation.UserCertificateRecord;
import org.cagrid.gaards.dorian.idp.IdentityProviderAuditRecord;
import org.cagrid.gaards.dorian.stubs.types.DorianInternalFault;
import org.cagrid.gaards.dorian.stubs.types.InvalidAssertionFault;
import org.cagrid.gaards.dorian.stubs.types.InvalidProxyFault;
import org.cagrid.gaards.dorian.stubs.types.InvalidUserCertificateFault;
import org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault;
import org.cagrid.gaards.dorian.stubs.types.UserPolicyFault;
import org.cagrid.gaards.pki.CertUtil;
import org.cagrid.gaards.pki.KeyUtil;
import org.cagrid.gaards.saml.encoding.SAMLUtils;
import org.globus.wsrf.Constants;
import org.globus.wsrf.security.SecurityManager;
import org.globus.wsrf.utils.AddressingUtils;
import org.springframework.core.io.FileSystemResource;

/**
 * gov.nih.nci.cagrid.dorianI TODO:DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.0
 */
public class DorianImpl extends DorianImplBase {

    private DorianConfiguration configuration;

    private Dorian dorian;

    public DorianImpl() throws RemoteException {
        try {
            EndpointReferenceType type = AddressingUtils.createEndpointReference(null);
            String configFile = DorianConfiguration.getConfiguration().getDorianConfiguration();
            String propertiesFile = DorianConfiguration.getConfiguration().getDorianProperties();
            BeanUtils utils = new BeanUtils(new FileSystemResource(configFile), new FileSystemResource(propertiesFile));
            DorianProperties conf = utils.getDorianProperties();
            this.dorian = new Dorian(conf, type.getAddress().toString());
            getResourceHome().getAddressedResource().setDorian(this.dorian);
            QName[] list = new QName[1];
            list[0] = AuthenticationProfile.BASIC_AUTHENTICATION;
            AuthenticationProfiles profiles = new AuthenticationProfiles();
            profiles.setProfile(list);
            getResourceHome().getAddressedResource().setAuthenticationProfiles(profiles);
            

            utils.getEventManager().logEvent(AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                FederationAudit.SystemStartup.getValue(), "System successfully started!!!");
        } catch (Exception e) {
            FaultHelper.printStackTrace(e);
            throw new RemoteException(Utils.getExceptionMessage(e));
        }
    }

    public DorianConfiguration getConfiguration() throws Exception {
        if (this.configuration != null) {
            return this.configuration;
        }
        MessageContext ctx = MessageContext.getCurrentContext();

        String servicePath = ctx.getTargetService();

        String jndiName = Constants.JNDI_SERVICES_BASE_NAME + servicePath + "/serviceconfiguration";
        try {
            javax.naming.Context initialContext = new InitialContext();
            this.configuration = (DorianConfiguration) initialContext.lookup(jndiName);
        } catch (Exception e) {
            throw new Exception("Unable to instantiate service configuration.", e);
        }

        return this.configuration;
    }

    private String getCallerIdentity() throws PermissionDeniedFault {
        String caller = SecurityManager.getManager().getCaller();
        // System.out.println("Caller: " + caller);
        if ((caller == null) || (caller.equals("<anonymous>"))) {
            PermissionDeniedFault fault = new PermissionDeniedFault();
            fault.setFaultString("No Grid Credentials Provided.");
            throw fault;
        }
        return caller;
    }

  public java.lang.String registerWithIdP(org.cagrid.gaards.dorian.idp.Application application) throws RemoteException, org.cagrid.gaards.dorian.stubs.types.DorianInternalFault, org.cagrid.gaards.dorian.stubs.types.InvalidUserPropertyFault {
        return dorian.registerLocalUser(application);
    }

  public org.cagrid.gaards.dorian.X509Certificate[] createProxy(org.cagrid.gaards.dorian.SAMLAssertion saml,org.cagrid.gaards.dorian.federation.PublicKey publicKey,org.cagrid.gaards.dorian.federation.ProxyLifetime lifetime,org.cagrid.gaards.dorian.federation.DelegationPathLength delegation) throws RemoteException, org.cagrid.gaards.dorian.stubs.types.DorianInternalFault, org.cagrid.gaards.dorian.stubs.types.InvalidAssertionFault, org.cagrid.gaards.dorian.stubs.types.InvalidProxyFault, org.cagrid.gaards.dorian.stubs.types.UserPolicyFault, org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault {
        try {
            PublicKey key = KeyUtil.loadPublicKey(publicKey.getKeyAsString());
            SAMLAssertion s = SAMLUtils.stringToSAMLAssertion(saml.getXml());
            CertificateLifetime l = new CertificateLifetime();
            l.setHours(lifetime.getHours());
            l.setMinutes(lifetime.getMinutes());
            l.setSeconds(lifetime.getSeconds());
            X509Certificate cert = dorian.requestUserCertificate(s, key, l);

            org.cagrid.gaards.dorian.X509Certificate[] certList = new org.cagrid.gaards.dorian.X509Certificate[1];
            certList[0] = new org.cagrid.gaards.dorian.X509Certificate();
            certList[0].setCertificateAsString(CertUtil.writeCertificate(cert));
            return certList;
        } catch (PermissionDeniedFault f) {
            throw f;
        } catch (UserPolicyFault f) {
            throw f;
        } catch (InvalidProxyFault f) {
            throw f;
        } catch (InvalidAssertionFault f) {
            throw f;
        } catch (DorianInternalFault f) {
            throw f;
        } catch (Exception e) {

            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString(Utils.getExceptionMessage(e));
            gov.nih.nci.cagrid.common.FaultHelper helper = new gov.nih.nci.cagrid.common.FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        }
    }

  public org.cagrid.gaards.dorian.X509Certificate getCACertificate() throws RemoteException, org.cagrid.gaards.dorian.stubs.types.DorianInternalFault {
        X509Certificate cert = dorian.getCACertificate();
        try {
            String certStr = CertUtil.writeCertificate(cert);
            return new org.cagrid.gaards.dorian.X509Certificate(certStr);
        } catch (Exception e) {
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("An error while trying to write the CA certificate.");
            throw fault;
        }
    }

  public org.cagrid.gaards.dorian.federation.TrustedIdP[] getTrustedIdPs() throws RemoteException, org.cagrid.gaards.dorian.stubs.types.DorianInternalFault, org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault {
        return dorian.getTrustedIdPs(getCallerIdentity());
    }

  public org.cagrid.gaards.dorian.federation.TrustedIdP addTrustedIdP(org.cagrid.gaards.dorian.federation.TrustedIdP idp) throws RemoteException, org.cagrid.gaards.dorian.stubs.types.DorianInternalFault, org.cagrid.gaards.dorian.stubs.types.InvalidTrustedIdPFault, org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault {
        return dorian.addTrustedIdP(getCallerIdentity(), idp);
    }

  public void updateTrustedIdP(org.cagrid.gaards.dorian.federation.TrustedIdP trustedIdP) throws RemoteException, org.cagrid.gaards.dorian.stubs.types.DorianInternalFault, org.cagrid.gaards.dorian.stubs.types.InvalidTrustedIdPFault, org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault {
        dorian.updateTrustedIdP(getCallerIdentity(), trustedIdP);
    }

  public void removeTrustedIdP(org.cagrid.gaards.dorian.federation.TrustedIdP trustedIdP) throws RemoteException, org.cagrid.gaards.dorian.stubs.types.DorianInternalFault, org.cagrid.gaards.dorian.stubs.types.InvalidTrustedIdPFault, org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault {
        dorian.removeTrustedIdP(getCallerIdentity(), trustedIdP);
    }

  public gov.nih.nci.cagrid.authentication.bean.SAMLAssertion authenticate(gov.nih.nci.cagrid.authentication.bean.Credential credential) throws RemoteException, gov.nih.nci.cagrid.authentication.stubs.types.InvalidCredentialFault, gov.nih.nci.cagrid.authentication.stubs.types.InsufficientAttributeFault, gov.nih.nci.cagrid.authentication.stubs.types.AuthenticationProviderFault {

        if (credential.getBasicAuthenticationCredential() == null) {
            InvalidCredentialFault fault = new InvalidCredentialFault();
            fault.setFaultString("A username and password is required to authenticate in this manner!!!");
            throw fault;
        } else {
            BasicAuthentication cred = new BasicAuthentication();
            cred.setUserId(credential.getBasicAuthenticationCredential().getUserId());
            cred.setPassword(credential.getBasicAuthenticationCredential().getPassword());

            try {
                SAMLAssertion saml = dorian.authenticate(cred);
                String xml = SAMLUtils.samlAssertionToString(saml);
                return new gov.nih.nci.cagrid.authentication.bean.SAMLAssertion(xml);
            } catch (AuthenticationProviderFault f) {
                gov.nih.nci.cagrid.authentication.stubs.types.AuthenticationProviderFault fault = new gov.nih.nci.cagrid.authentication.stubs.types.AuthenticationProviderFault();
                fault.setFaultString(f.getFaultString());
                throw fault;
            } catch (org.cagrid.gaards.authentication.faults.InvalidCredentialFault f) {
                InvalidCredentialFault fault = new InvalidCredentialFault();
                fault.setFaultString(f.getFaultString());
                throw fault;
            } catch (CredentialNotSupportedFault f) {
                InvalidCredentialFault fault = new InvalidCredentialFault();
                fault.setFaultString(f.getFaultString());
                throw fault;
            } catch (Exception e) {
                DorianInternalFault fault = new DorianInternalFault();
                fault.setFaultString(e.getMessage());
                FaultHelper helper = new FaultHelper(fault);
                helper.setDescription(Utils.getExceptionMessage(e));
                helper.addFaultCause(e);
                fault = (DorianInternalFault) helper.getFault();
                throw fault;
            }
        }
    }

  public void addAdmin(java.lang.String gridIdentity) throws RemoteException, org.cagrid.gaards.dorian.stubs.types.DorianInternalFault, org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault {
        dorian.addAdmin(getCallerIdentity(), gridIdentity);
    }

  public void removeAdmin(java.lang.String gridIdentity) throws RemoteException, org.cagrid.gaards.dorian.stubs.types.DorianInternalFault, org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault {
        dorian.removeAdmin(getCallerIdentity(), gridIdentity);
    }

  public java.lang.String[] getAdmins() throws RemoteException, org.cagrid.gaards.dorian.stubs.types.DorianInternalFault, org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault {
        return dorian.getAdmins(getCallerIdentity());
    }

  public org.cagrid.gaards.dorian.federation.HostCertificateRecord requestHostCertificate(org.cagrid.gaards.dorian.federation.HostCertificateRequest req) throws RemoteException, org.cagrid.gaards.dorian.stubs.types.DorianInternalFault, org.cagrid.gaards.dorian.stubs.types.InvalidHostCertificateRequestFault, org.cagrid.gaards.dorian.stubs.types.InvalidHostCertificateFault, org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault {
        return dorian.requestHostCertificate(getCallerIdentity(), req);
    }

  public org.cagrid.gaards.dorian.federation.HostCertificateRecord[] getOwnedHostCertificates() throws RemoteException, org.cagrid.gaards.dorian.stubs.types.DorianInternalFault, org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault {
        return dorian.getOwnedHostCertificates(getCallerIdentity());
    }

  public org.cagrid.gaards.dorian.federation.HostCertificateRecord approveHostCertificate(java.math.BigInteger recordId) throws RemoteException, org.cagrid.gaards.dorian.stubs.types.DorianInternalFault, org.cagrid.gaards.dorian.stubs.types.InvalidHostCertificateFault, org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault {
        return dorian.approveHostCertificate(getCallerIdentity(), recordId.longValue());
    }

  public org.cagrid.gaards.dorian.federation.HostCertificateRecord[] findHostCertificates(org.cagrid.gaards.dorian.federation.HostCertificateFilter hostCertificateFilter) throws RemoteException, org.cagrid.gaards.dorian.stubs.types.DorianInternalFault, org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault {
        return dorian.findHostCertificates(getCallerIdentity(), hostCertificateFilter);
    }

  public void updateHostCertificateRecord(org.cagrid.gaards.dorian.federation.HostCertificateUpdate hostCertificateUpdate) throws RemoteException, org.cagrid.gaards.dorian.stubs.types.DorianInternalFault, org.cagrid.gaards.dorian.stubs.types.InvalidHostCertificateFault, org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault {
        dorian.updateHostCertificateRecord(getCallerIdentity(), hostCertificateUpdate);
    }

  public org.cagrid.gaards.dorian.federation.HostCertificateRecord renewHostCertificate(java.math.BigInteger recordId) throws RemoteException, org.cagrid.gaards.dorian.stubs.types.DorianInternalFault, org.cagrid.gaards.dorian.stubs.types.InvalidHostCertificateFault, org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault {
        return dorian.renewHostCertificate(getCallerIdentity(), recordId.longValue());
    }

  public void changeIdPUserPassword(org.cagrid.gaards.dorian.idp.BasicAuthCredential credential,java.lang.String newPassword) throws RemoteException, org.cagrid.gaards.dorian.stubs.types.DorianInternalFault, org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault, org.cagrid.gaards.dorian.stubs.types.InvalidUserPropertyFault {
        dorian.changeLocalUserPassword(org.cagrid.gaards.dorian.service.util.Utils.fromLegacyCredential(credential),
            newPassword);
    }

  public gov.nih.nci.cagrid.opensaml.SAMLAssertion authenticateUser(org.cagrid.gaards.authentication.Credential credential) throws RemoteException, org.cagrid.gaards.authentication.faults.AuthenticationProviderFault, org.cagrid.gaards.authentication.faults.CredentialNotSupportedFault, org.cagrid.gaards.authentication.faults.InsufficientAttributeFault, org.cagrid.gaards.authentication.faults.InvalidCredentialFault {
        return dorian.authenticate(credential);
    }

  public void changeLocalUserPassword(org.cagrid.gaards.authentication.BasicAuthentication credential,java.lang.String newPassword) throws RemoteException, org.cagrid.gaards.dorian.stubs.types.DorianInternalFault, org.cagrid.gaards.dorian.stubs.types.InvalidUserPropertyFault, org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault {
        dorian.changeLocalUserPassword(credential, newPassword);
    }

  public boolean doesLocalUserExist(java.lang.String userId) throws RemoteException, org.cagrid.gaards.dorian.stubs.types.DorianInternalFault {
        return dorian.doesLocalUserExist(userId);
    }

  public org.cagrid.gaards.dorian.federation.GridUser[] findGridUsers(org.cagrid.gaards.dorian.federation.GridUserFilter filter) throws RemoteException, org.cagrid.gaards.dorian.stubs.types.DorianInternalFault, org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault {
        return dorian.findGridUsers(getCallerIdentity(), filter);
    }

  public void updateGridUser(org.cagrid.gaards.dorian.federation.GridUser user) throws RemoteException, org.cagrid.gaards.dorian.stubs.types.DorianInternalFault, org.cagrid.gaards.dorian.stubs.types.InvalidUserFault, org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault {
        dorian.updateGridUser(getCallerIdentity(), user);
    }

  public void removeGridUser(org.cagrid.gaards.dorian.federation.GridUser user) throws RemoteException, org.cagrid.gaards.dorian.stubs.types.DorianInternalFault, org.cagrid.gaards.dorian.stubs.types.InvalidUserFault, org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault {
        dorian.removeGridUser(getCallerIdentity(), user);
    }

  public org.cagrid.gaards.dorian.federation.GridUserPolicy[] getGridUserPolicies() throws RemoteException, org.cagrid.gaards.dorian.stubs.types.DorianInternalFault, org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault {
        return dorian.getGridUserPolicies(getCallerIdentity());
    }

  public void updateUserCertificate(org.cagrid.gaards.dorian.federation.UserCertificateUpdate update) throws RemoteException, org.cagrid.gaards.dorian.stubs.types.DorianInternalFault, org.cagrid.gaards.dorian.stubs.types.InvalidUserCertificateFault, org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault {
        dorian.updateUserCertificateRecord(getCallerIdentity(), update);
    }

  public org.cagrid.gaards.dorian.X509Certificate requestUserCertificate(gov.nih.nci.cagrid.opensaml.SAMLAssertion saml,org.cagrid.gaards.dorian.federation.PublicKey key,org.cagrid.gaards.dorian.federation.CertificateLifetime lifetime) throws RemoteException, org.cagrid.gaards.dorian.stubs.types.DorianInternalFault, org.cagrid.gaards.dorian.stubs.types.InvalidAssertionFault, org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault, org.cagrid.gaards.dorian.stubs.types.UserPolicyFault {
        try {
            PublicKey pkey = KeyUtil.loadPublicKey(key.getKeyAsString());
            X509Certificate cert = dorian.requestUserCertificate(saml, pkey, lifetime);
            org.cagrid.gaards.dorian.X509Certificate certificate = new org.cagrid.gaards.dorian.X509Certificate();
            certificate.setCertificateAsString(CertUtil.writeCertificate(cert));
            return certificate;
        } catch (PermissionDeniedFault f) {
            throw f;
        } catch (UserPolicyFault f) {
            throw f;
        } catch (InvalidAssertionFault f) {
            throw f;
        } catch (DorianInternalFault f) {
            throw f;
        } catch (Exception e) {
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString(Utils.getExceptionMessage(e));
            gov.nih.nci.cagrid.common.FaultHelper helper = new gov.nih.nci.cagrid.common.FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;
        }
    }

  public org.cagrid.gaards.dorian.federation.UserCertificateRecord[] findUserCertificates(org.cagrid.gaards.dorian.federation.UserCertificateFilter userCertificateFilter) throws RemoteException, org.cagrid.gaards.dorian.stubs.types.DorianInternalFault, org.cagrid.gaards.dorian.stubs.types.InvalidUserCertificateFault, org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault {
        List<UserCertificateRecord> list = dorian
            .findUserCertificateRecords(getCallerIdentity(), userCertificateFilter);
        UserCertificateRecord[] records = new UserCertificateRecord[list.size()];
        for (int i = 0; i < list.size(); i++) {
            records[i] = list.get(i);
        }
        return records;
    }

  public void removeUserCertificate(java.lang.String serialNumber) throws RemoteException, org.cagrid.gaards.dorian.stubs.types.DorianInternalFault, org.cagrid.gaards.dorian.stubs.types.InvalidUserCertificateFault, org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault {
        try {
            dorian.removeUserCertificate(getCallerIdentity(), Long.valueOf(serialNumber).longValue());
        } catch (NumberFormatException e) {
            InvalidUserCertificateFault fault = new InvalidUserCertificateFault();
            fault.setFaultString("Invalid user certificate specified!!!");
            throw fault;
        }
    }

  public org.cagrid.gaards.dorian.idp.LocalUser[] findLocalUsers(org.cagrid.gaards.dorian.idp.LocalUserFilter f) throws RemoteException, org.cagrid.gaards.dorian.stubs.types.DorianInternalFault, org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault {
        return dorian.findLocalUsers(getCallerIdentity(), f);
    }

  public void updateLocalUser(org.cagrid.gaards.dorian.idp.LocalUser user) throws RemoteException, org.cagrid.gaards.dorian.stubs.types.DorianInternalFault, org.cagrid.gaards.dorian.stubs.types.NoSuchUserFault, org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault {
        dorian.updateLocalUser(getCallerIdentity(), user);
    }

  public void removeLocalUser(java.lang.String userId) throws RemoteException, org.cagrid.gaards.dorian.stubs.types.DorianInternalFault, org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault {
        dorian.removeLocalUser(getCallerIdentity(), userId);
    }

  public org.cagrid.gaards.dorian.federation.FederationAuditRecord[] performFederationAudit(org.cagrid.gaards.dorian.federation.FederationAuditFilter f) throws RemoteException, org.cagrid.gaards.dorian.stubs.types.DorianInternalFault, org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault {
        List<FederationAuditRecord> records = this.dorian.performFederationAudit(getCallerIdentity(), f);
        FederationAuditRecord[] array = new FederationAuditRecord[records.size()];
        return records.toArray(array);
    }

  public org.cagrid.gaards.dorian.idp.IdentityProviderAuditRecord[] performIdentityProviderAudit(org.cagrid.gaards.dorian.idp.IdentityProviderAuditFilter f) throws RemoteException, org.cagrid.gaards.dorian.stubs.types.DorianInternalFault, org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault {
        List<IdentityProviderAuditRecord> records = this.dorian.performIdentityProviderAudit(getCallerIdentity(), f);
        IdentityProviderAuditRecord[] array = new IdentityProviderAuditRecord[records.size()];
        return records.toArray(array);
    }

  public java.lang.String registerLocalUser(org.cagrid.gaards.dorian.idp.Application a) throws RemoteException, org.cagrid.gaards.dorian.stubs.types.DorianInternalFault, org.cagrid.gaards.dorian.stubs.types.InvalidUserPropertyFault {
      return dorian.registerLocalUser(a);
  }

}
