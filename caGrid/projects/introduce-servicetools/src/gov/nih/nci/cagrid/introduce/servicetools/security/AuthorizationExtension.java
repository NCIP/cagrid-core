package gov.nih.nci.cagrid.introduce.servicetools.security;

import java.rmi.RemoteException;

import javax.security.auth.Subject;
import javax.xml.namespace.QName;
import javax.xml.rpc.handler.MessageContext;

import org.globus.wsrf.impl.security.authorization.exceptions.AuthorizationException;
import org.globus.wsrf.impl.security.authorization.exceptions.InitializeException;

/**
 * Any Introduce Authorization Extension must implement this class.
 * 
 * @author hastings
 *
 */
public interface AuthorizationExtension {

    /**
     * AuthorizeOperation method is intended to be called for a method level
     * authorization callout. if the authorization fails a remote exception
     * detailing the failure should be throws. If the authorization for the
     * operation is configured and succeeds the method should simply return.
     * 
     * @param peerSubject
     * @param context
     * @param operation
     * @throws RemoteException
     */
    public void authorizeOperation(Subject peerSubject, MessageContext context, QName operation)
        throws AuthorizationException;


    /**
     * AuthorizaService method is intended to be called for a service level
     * authorization callout. If the authorization fails or no service level
     * authorization has been configured then this operation should throw a
     * AuthorizationException stating why. If the authorization is configured
     * and succeeds this method should simply return.
     * 
     * @param peerSubject
     * @param context
     * @throws RemoteException
     */
    public void authorizeService(Subject peerSubject, MessageContext context, QName operation) throws AuthorizationException;

    
    /**
     * 
     * The service context is the name of the deployed service
     * cagrid/Myservice for example
     * 
     * @param serviceContext
     */
    public void initialize(String serviceName, String serviceEtcDir) throws InitializeException;
}
