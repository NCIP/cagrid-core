package org.cagrid.gaards.dorian.client;

import gov.nih.nci.cagrid.common.FaultHelper;
import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.metadata.ResourcePropertyHelper;
import gov.nih.nci.cagrid.metadata.exceptions.InvalidResourcePropertyException;
import gov.nih.nci.cagrid.metadata.exceptions.ResourcePropertyRetrievalException;
import gov.nih.nci.cagrid.opensaml.SAMLAssertion;

import java.io.InputStream;
import java.io.StringReader;
import java.rmi.RemoteException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.gaards.dorian.common.DorianFault;
import org.cagrid.gaards.dorian.federation.CertificateLifetime;
import org.cagrid.gaards.dorian.federation.DelegationPathLength;
import org.cagrid.gaards.dorian.federation.GridUserRecord;
import org.cagrid.gaards.dorian.federation.GridUserSearchCriteria;
import org.cagrid.gaards.dorian.federation.HostCertificateRecord;
import org.cagrid.gaards.dorian.federation.HostCertificateRequest;
import org.cagrid.gaards.dorian.federation.HostRecord;
import org.cagrid.gaards.dorian.federation.HostSearchCriteria;
import org.cagrid.gaards.dorian.federation.ProxyLifetime;
import org.cagrid.gaards.dorian.federation.TrustedIdentityProvider;
import org.cagrid.gaards.dorian.federation.TrustedIdentityProviders;
import org.cagrid.gaards.dorian.stubs.types.DorianInternalFault;
import org.cagrid.gaards.dorian.stubs.types.InvalidAssertionFault;
import org.cagrid.gaards.dorian.stubs.types.InvalidHostCertificateFault;
import org.cagrid.gaards.dorian.stubs.types.InvalidHostCertificateRequestFault;
import org.cagrid.gaards.dorian.stubs.types.InvalidProxyFault;
import org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault;
import org.cagrid.gaards.dorian.stubs.types.UserPolicyFault;
import org.cagrid.gaards.pki.CertUtil;
import org.cagrid.gaards.pki.KeyUtil;
import org.cagrid.gaards.saml.encoding.SAMLUtils;
import org.globus.gsi.GlobusCredential;
import org.globus.wsrf.utils.XmlUtils;
import org.w3c.dom.Element;


public class GridUserClient extends DorianBaseClient {

    public static final QName TRUSTED_IDPS_METADATA = new QName("http://cagrid.nci.nih.gov/1/dorian-ifs",
        "TrustedIdentityProviders");


    public GridUserClient(String serviceURI) throws MalformedURIException, RemoteException {
        super(serviceURI,null, true);
    }


    public GridUserClient(String serviceURI, GlobusCredential cred) throws MalformedURIException, RemoteException {
        super(serviceURI, cred,true);
    }


    public GridUserClient(String serviceURI, GlobusCredential cred, boolean anonymousPreferred)
        throws MalformedURIException, RemoteException {
        super(serviceURI, cred, anonymousPreferred);
    }


    /**
     * Allow a user to request a short term Grid credential from Dorian, which
     * they may user to authenticate to Grid service.
     * 
     * @param saml
     *            A signed SAML assertion from an identity provider trusted by
     *            Dorian.
     * @param lifetime
     *            The lifetime of the Grid credential.
     * @return The short term Grid credential.
     * @throws DorianFault
     * @throws DorianInternalFault
     * @throws InvalidAssertionFault
     * @throws UserPolicyFault
     * @throws PermissionDeniedFault
     */
    public GlobusCredential requestUserCertificate(SAMLAssertion saml, CertificateLifetime lifetime)
        throws DorianFault, DorianInternalFault, InvalidAssertionFault, UserPolicyFault, PermissionDeniedFault {
        try {
            String version = getServiceVersion();
            KeyPair pair = KeyUtil.generateRSAKeyPair1024();
            org.cagrid.gaards.dorian.federation.PublicKey key = new org.cagrid.gaards.dorian.federation.PublicKey(
                KeyUtil.writePublicKey(pair.getPublic()));

            if (version.equals(VERSION_1_0) || version.equals(VERSION_1_1) || version.equals(VERSION_1_2)
                || version.equals(VERSION_UNKNOWN)) {
                try {
                    org.cagrid.gaards.dorian.SAMLAssertion assertion = new org.cagrid.gaards.dorian.SAMLAssertion();
                    assertion.setXml(SAMLUtils.samlAssertionToString(saml));
                    DelegationPathLength length = new DelegationPathLength();
                    length.setLength(0);
                    ProxyLifetime l = new ProxyLifetime();
                    l.setHours(lifetime.getHours());
                    l.setMinutes(lifetime.getMinutes());
                    l.setSeconds(lifetime.getSeconds());
                    org.cagrid.gaards.dorian.X509Certificate[] list = getClient()
                        .createProxy(assertion, key, l, length);
                    X509Certificate[] certs = new X509Certificate[list.length];
                    for (int i = 0; i < list.length; i++) {
                        certs[i] = CertUtil.loadCertificate(list[i].getCertificateAsString());
                    }
                    return new GlobusCredential(pair.getPrivate(), certs);
                } catch (InvalidProxyFault e) {
                    UserPolicyFault f = new UserPolicyFault();
                    f.setFaultString(e.getFaultString());
                    FaultHelper helper = new FaultHelper(f);
                    helper.addFaultCause(e);
                    f = (UserPolicyFault) helper.getFault();
                    throw f;
                }
            } else {

                org.cagrid.gaards.dorian.X509Certificate cert = getClient().requestUserCertificate(saml, key, lifetime);
                X509Certificate[] certs = new X509Certificate[1];
                certs[0] = CertUtil.loadCertificate(cert.getCertificateAsString());
                return new GlobusCredential(pair.getPrivate(), certs);
            }
        } catch (DorianInternalFault gie) {
            throw gie;
        } catch (InvalidAssertionFault f) {
            throw f;
        } catch (UserPolicyFault f) {
            throw f;
        } catch (PermissionDeniedFault f) {
            throw f;
        } catch (Exception e) {
            FaultUtil.printFault(e);
            DorianFault fault = new DorianFault();
            fault.setFaultString(Utils.getExceptionMessage(e));
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianFault) helper.getFault();
            throw fault;
        }
    }


    /**
     * This method allow a user to request a host certificate.
     * 
     * @param hostname
     *            The host name of the host.
     * @param publicKey
     *            The public key to use for the host ceriticate.
     * @return The host certificate record for the host certificate, if the host
     *         certificate was immediately approved, the signed host certificate
     *         will be contained in the record. Otherwise you will have to wait
     *         for the host certificate to be approved, once approved the
     *         getOwnedHostCertificates method can be used to obtain the signed
     *         host certificate.
     * @throws DorianFault
     * @throws DorianInternalFault
     * @throws InvalidHostCertificateRequestFault
     * @throws InvalidHostCertificateFault
     * @throws PermissionDeniedFault
     */
    public HostCertificateRecord requestHostCertificate(String hostname, PublicKey publicKey) throws DorianFault,
        DorianInternalFault, InvalidHostCertificateRequestFault, InvalidHostCertificateFault, PermissionDeniedFault {
        try {
            HostCertificateRequest req = new HostCertificateRequest();
            req.setHostname(hostname);
            org.cagrid.gaards.dorian.federation.PublicKey key = new org.cagrid.gaards.dorian.federation.PublicKey();
            key.setKeyAsString(KeyUtil.writePublicKey(publicKey));
            req.setPublicKey(key);
            return getClient().requestHostCertificate(req);
        } catch (DorianInternalFault gie) {
            throw gie;
        } catch (InvalidHostCertificateRequestFault f) {
            throw f;
        } catch (InvalidHostCertificateFault f) {
            throw f;
        } catch (PermissionDeniedFault f) {
            throw f;
        } catch (Exception e) {
            FaultUtil.printFault(e);
            DorianFault fault = new DorianFault();
            fault.setFaultString(Utils.getExceptionMessage(e));
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianFault) helper.getFault();
            throw fault;
        }
    }


    /**
     * This method returns a list for all the host certificates owned by the
     * user.
     * 
     * @return The list of host certificates owned by the user.
     * @throws DorianFault
     * @throws DorianInternalFault
     * @throws PermissionDeniedFault
     */
    public List<HostCertificateRecord> getOwnedHostCertificates() throws DorianFault, DorianInternalFault,
        PermissionDeniedFault {
        try {
            List<HostCertificateRecord> list = Utils.asList(getClient().getOwnedHostCertificates());
            return list;
        } catch (DorianInternalFault gie) {
            throw gie;
        } catch (PermissionDeniedFault f) {
            throw f;
        } catch (Exception e) {
            FaultUtil.printFault(e);
            DorianFault fault = new DorianFault();
            fault.setFaultString(Utils.getExceptionMessage(e));
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianFault) helper.getFault();
            throw fault;
        }

    }


    /**
     * This method obtains Dorian's CA certificate.
     * 
     * @return This method obtains Dorian's CA certificate.
     * @throws DorianFault
     * @throws DorianInternalFault
     */

    public X509Certificate getCACertificate() throws DorianFault, DorianInternalFault {
        try {
            return CertUtil.loadCertificate(getClient().getCACertificate().getCertificateAsString());
        } catch (DorianInternalFault gie) {
            throw gie;
        } catch (Exception e) {
            FaultUtil.printFault(e);
            DorianFault fault = new DorianFault();
            fault.setFaultString(Utils.getExceptionMessage(e));
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianFault) helper.getFault();
            throw fault;
        }
    }


    /**
     * This method obtains a list of the identity providers trusted by Dorian.
     * Client side authorization is not enforced when calling this method.
     * 
     * @return The list of identity providers trusted by Dorian.
     * @throws ResourcePropertyRetrievalException
     */

    public List<TrustedIdentityProvider> getTrustedIdentityProviders() throws ResourcePropertyRetrievalException {
        Element resourceProperty = null;
        try {
            InputStream wsdd = getClass().getResourceAsStream("client-config.wsdd");
            resourceProperty = ResourcePropertyHelper.getResourceProperty(getClient().getEndpointReference(),
                TRUSTED_IDPS_METADATA, wsdd);
        } catch (InvalidResourcePropertyException e) {
            return null;
        }
        try {
            TrustedIdentityProviders result = (TrustedIdentityProviders) Utils.deserializeObject(new StringReader(
                XmlUtils.toString(resourceProperty)), TrustedIdentityProviders.class);
            List<TrustedIdentityProvider> idps = new ArrayList<TrustedIdentityProvider>();
            if (result != null) {
                TrustedIdentityProvider[] list = result.getTrustedIdentityProvider();
                if (list != null) {
                    for (int i = 0; i < list.length; i++) {
                        idps.add(list[i]);
                    }
                }
            }
            return idps;

        } catch (Exception e) {
            throw new ResourcePropertyRetrievalException("Unable to deserailize: " + e.getMessage(), e);
        }
    }


    /**
     * This method allows one to search for users managed by Dorian.
     * 
     * @param criteria
     *            The search criteria
     * @return The list of users meeting the search criteria.
     * @throws DorianFault
     * @throws DorianInternalFault
     * @throws PermissionDeniedFault
     */

    public List<GridUserRecord> userSearch(GridUserSearchCriteria criteria) throws DorianFault, DorianInternalFault,
        PermissionDeniedFault {
        String version = VERSION_UNKNOWN;
        try {
            version = getServiceVersion();
        } catch (Exception e) {
            FaultUtil.printFault(e);
            DorianFault fault = new DorianFault();
            fault
                .setFaultString("Could not perform user search, an unexpected error occurred determining the version of the Dorian "
                    + getServiceURL() + ":\n" + Utils.getExceptionMessage(e));
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianFault) helper.getFault();
            throw fault;
        }
        if (version.equals(VERSION_1_0) || version.equals(VERSION_1_1) || version.equals(VERSION_1_2)
            || version.equals(VERSION_1_3) || version.equals(VERSION_UNKNOWN)) {
            DorianFault fault = new DorianFault();
            fault.setFaultString("The Dorian " + getServiceURL() + ", is operating the version " + version
                + ", which does not support user searching.");
            throw fault;

        } else {
            try {
                List<GridUserRecord> list = Utils.asList(getClient().userSearch(criteria));
                return list;
            } catch (DorianInternalFault gie) {
                throw gie;
            } catch (PermissionDeniedFault f) {
                throw f;
            } catch (Exception e) {
                FaultUtil.printFault(e);
                DorianFault fault = new DorianFault();
                fault.setFaultString(Utils.getExceptionMessage(e));
                FaultHelper helper = new FaultHelper(fault);
                helper.addFaultCause(e);
                fault = (DorianFault) helper.getFault();
                throw fault;
            }
        }
    }


    /**
     * This method allows one to search for hosts managed by Dorian.
     * 
     * @param criteria
     *            The search criteria
     * @return The list of hosts meeting the search criteria.
     * @throws DorianFault
     * @throws DorianInternalFault
     * @throws PermissionDeniedFault
     */

    public List<HostRecord> hostSearch(HostSearchCriteria criteria) throws DorianFault, DorianInternalFault,
        PermissionDeniedFault {
        String version = VERSION_UNKNOWN;
        try {
            version = getServiceVersion();
        } catch (Exception e) {
            FaultUtil.printFault(e);
            DorianFault fault = new DorianFault();
            fault
                .setFaultString("Could not perform user search, an unexpected error occurred determining the version of the Dorian "
                    + getServiceURL() + ":\n" + Utils.getExceptionMessage(e));
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianFault) helper.getFault();
            throw fault;
        }
        if (version.equals(VERSION_1_0) || version.equals(VERSION_1_1) || version.equals(VERSION_1_2)
            || version.equals(VERSION_1_3) || version.equals(VERSION_UNKNOWN)) {
            DorianFault fault = new DorianFault();
            fault.setFaultString("The Dorian " + getServiceURL() + ", is operating the version " + version
                + ", which does not support user searching.");
            throw fault;

        } else {
            try {
                List<HostRecord> list = Utils.asList(getClient().hostSearch(criteria));
                return list;
            } catch (DorianInternalFault gie) {
                throw gie;
            } catch (PermissionDeniedFault f) {
                throw f;
            } catch (Exception e) {
                FaultUtil.printFault(e);
                DorianFault fault = new DorianFault();
                fault.setFaultString(Utils.getExceptionMessage(e));
                FaultHelper helper = new FaultHelper(fault);
                helper.addFaultCause(e);
                fault = (DorianFault) helper.getFault();
                throw fault;
            }
        }

    }
}
