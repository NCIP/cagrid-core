package gov.nih.nci.cagrid.dorian.client;

import gov.nih.nci.cagrid.authentication.bean.Credential;
import gov.nih.nci.cagrid.authentication.stubs.types.AuthenticationProviderFault;
import gov.nih.nci.cagrid.authentication.stubs.types.InsufficientAttributeFault;
import gov.nih.nci.cagrid.authentication.stubs.types.InvalidCredentialFault;
import gov.nih.nci.cagrid.common.FaultHelper;
import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.dorian.common.DorianFault;
import gov.nih.nci.cagrid.dorian.idp.bean.BasicAuthCredential;
import gov.nih.nci.cagrid.dorian.stubs.types.DorianInternalFault;
import gov.nih.nci.cagrid.dorian.stubs.types.InvalidUserPropertyFault;
import gov.nih.nci.cagrid.dorian.stubs.types.PermissionDeniedFault;
import gov.nih.nci.cagrid.opensaml.SAMLAssertion;

import java.rmi.RemoteException;

import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.gaards.saml.encoding.SAMLUtils;
import org.globus.wsrf.impl.security.authorization.Authorization;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class IdPUserClient {

    private DorianClient client;


    public IdPUserClient(String serviceURI) throws MalformedURIException, RemoteException {
        client = new DorianClient(serviceURI);
    }


    public SAMLAssertion authenticate(Credential cred) throws DorianFault, InvalidCredentialFault,
        InsufficientAttributeFault, AuthenticationProviderFault {

        try {
            String xml = client.authenticate(cred).getXml();
            return SAMLUtils.stringToSAMLAssertion(xml);
        } catch (InvalidCredentialFault f) {
            throw f;
        } catch (InsufficientAttributeFault f) {
            throw f;
        } catch (AuthenticationProviderFault f) {
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


    public SAMLAssertion authenticate(BasicAuthCredential cred) throws DorianFault, DorianInternalFault,
        PermissionDeniedFault {
        try {
            String xml = client.authenticateWithIdP(cred).getXml();
            return SAMLUtils.stringToSAMLAssertion(xml);
        } catch (DorianInternalFault f) {
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


    public void changePassword(BasicAuthCredential cred, String newPassword) throws DorianFault, DorianInternalFault,
        PermissionDeniedFault, InvalidUserPropertyFault {
        try {
            client.changeIdPUserPassword(cred, newPassword);
        } catch (DorianInternalFault f) {
            throw f;
        } catch (PermissionDeniedFault f) {
            throw f;
        } catch (InvalidUserPropertyFault f) {
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
