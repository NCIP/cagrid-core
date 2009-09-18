package gov.nih.nci.cagrid.dorian.client;

import gov.nih.nci.cagrid.common.FaultHelper;
import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.dorian.common.DorianFault;
import gov.nih.nci.cagrid.dorian.ifs.bean.DelegationPathLength;
import gov.nih.nci.cagrid.dorian.ifs.bean.HostCertificateRecord;
import gov.nih.nci.cagrid.dorian.ifs.bean.HostCertificateRequest;
import gov.nih.nci.cagrid.dorian.ifs.bean.ProxyLifetime;
import gov.nih.nci.cagrid.dorian.stubs.types.DorianInternalFault;
import gov.nih.nci.cagrid.dorian.stubs.types.InvalidAssertionFault;
import gov.nih.nci.cagrid.dorian.stubs.types.InvalidHostCertificateFault;
import gov.nih.nci.cagrid.dorian.stubs.types.InvalidHostCertificateRequestFault;
import gov.nih.nci.cagrid.dorian.stubs.types.InvalidProxyFault;
import gov.nih.nci.cagrid.dorian.stubs.types.PermissionDeniedFault;
import gov.nih.nci.cagrid.dorian.stubs.types.UserPolicyFault;
import gov.nih.nci.cagrid.opensaml.SAMLAssertion;

import java.rmi.RemoteException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.gaards.pki.CertUtil;
import org.cagrid.gaards.pki.KeyUtil;
import org.cagrid.gaards.saml.encoding.SAMLUtils;
import org.globus.gsi.GlobusCredential;
import org.globus.wsrf.impl.security.authorization.Authorization;


public class IFSUserClient {

    private DorianClient client;


    public IFSUserClient(String serviceURI) throws MalformedURIException, RemoteException {
        client = new DorianClient(serviceURI);
    }


    public IFSUserClient(String serviceURI, GlobusCredential cred) throws MalformedURIException, RemoteException {
        client = new DorianClient(serviceURI, cred);
    }


    public GlobusCredential createProxy(SAMLAssertion saml, ProxyLifetime lifetime, int delegationPathLength)
        throws DorianFault, DorianInternalFault, InvalidAssertionFault, InvalidProxyFault, UserPolicyFault,
        PermissionDeniedFault {

        try {
            KeyPair pair = KeyUtil.generateRSAKeyPair1024();

            gov.nih.nci.cagrid.dorian.ifs.bean.PublicKey key = new gov.nih.nci.cagrid.dorian.ifs.bean.PublicKey(KeyUtil
                .writePublicKey(pair.getPublic()));
            gov.nih.nci.cagrid.dorian.bean.SAMLAssertion s = new gov.nih.nci.cagrid.dorian.bean.SAMLAssertion(SAMLUtils
                .samlAssertionToString(saml));
            gov.nih.nci.cagrid.dorian.bean.X509Certificate list[] = client.createProxy(s, key, lifetime,
                new DelegationPathLength(delegationPathLength));
            X509Certificate[] certs = new X509Certificate[list.length];
            for (int i = 0; i < list.length; i++) {
                certs[i] = CertUtil.loadCertificate(list[i].getCertificateAsString());
            }
            return new GlobusCredential(pair.getPrivate(), certs);
        } catch (DorianInternalFault gie) {
            throw gie;
        } catch (InvalidAssertionFault f) {
            throw f;
        } catch (InvalidProxyFault f) {
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


    public HostCertificateRecord requestHostCertificate(String hostname, PublicKey publicKey) throws DorianFault,
        DorianInternalFault, InvalidHostCertificateRequestFault, InvalidHostCertificateFault, PermissionDeniedFault {
        try {
            HostCertificateRequest req = new HostCertificateRequest();
            req.setHostname(hostname);
            gov.nih.nci.cagrid.dorian.ifs.bean.PublicKey key = new gov.nih.nci.cagrid.dorian.ifs.bean.PublicKey();
            key.setKeyAsString(KeyUtil.writePublicKey(publicKey));
            req.setPublicKey(key);
            return client.requestHostCertificate(req);
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


    public HostCertificateRecord[] getOwnedHostCertificates() throws DorianFault, DorianInternalFault,
        PermissionDeniedFault {
        try {
            return client.getOwnedHostCertificates();
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
     * This method specifies an authorization policy that the client should use
     * for authorizing the server that it connects to.
     * 
     * @param authorization
     *            The authorization policy to enforce
     */

    public void setAuthorization(Authorization authorization) {
        client.setAuthorization(authorization);
    }

}
