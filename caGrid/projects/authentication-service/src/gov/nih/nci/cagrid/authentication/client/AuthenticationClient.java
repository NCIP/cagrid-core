/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package gov.nih.nci.cagrid.authentication.client;

import gov.nih.nci.cagrid.authentication.bean.Credential;
import gov.nih.nci.cagrid.authentication.stubs.types.AuthenticationProviderFault;
import gov.nih.nci.cagrid.authentication.stubs.types.InsufficientAttributeFault;
import gov.nih.nci.cagrid.authentication.stubs.types.InvalidCredentialFault;
import gov.nih.nci.cagrid.opensaml.SAMLAssertion;

import java.rmi.RemoteException;

import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.gaards.authentication.client.AuthenticationServiceClient;
import org.cagrid.gaards.saml.encoding.SAMLUtils;
import org.globus.wsrf.impl.security.authorization.Authorization;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 *          
 * @deprecated As of release 1.3, replaced by {@link org.cagrid.gaards.authentication.client.AuthenticationClient}
 */
@Deprecated
public class AuthenticationClient {

    private Credential cred;

    private AuthenticationServiceClient client;


    public AuthenticationClient(String serviceURI, Credential cred) throws MalformedURIException, RemoteException {
        client = new AuthenticationServiceClient(serviceURI);
        this.cred = cred;
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


    public SAMLAssertion authenticate() throws RemoteException, InvalidCredentialFault, InsufficientAttributeFault,
        AuthenticationProviderFault {
        try {
            String xml = client.authenticate(cred).getXml();
            // System.out.println(XMLUtilities.formatXML(xml));
            return SAMLUtils.stringToSAMLAssertion(xml);
        } catch (InvalidCredentialFault gie) {
            throw gie;
        } catch (InsufficientAttributeFault ilf) {
            throw ilf;
        } catch (AuthenticationProviderFault ilf) {
            throw ilf;
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
    }
}
