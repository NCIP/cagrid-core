package org.cagrid.enforce.authorization.extension.service;

import gov.nih.nci.cagrid.introduce.servicetools.security.AuthorizationExtension;

import javax.security.auth.Subject;
import javax.xml.namespace.QName;
import javax.xml.rpc.handler.MessageContext;

import org.apache.log4j.Logger;
import org.globus.wsrf.impl.security.authorization.exceptions.AuthorizationException;
import org.globus.wsrf.impl.security.authorization.exceptions.InitializeException;


public class EnforceAuthorization implements AuthorizationExtension {

    private static final Logger logger = Logger.getLogger(EnforceAuthorization.class);


    public static String getCallerIdentity() {
        String caller = org.globus.wsrf.security.SecurityManager.getManager().getCaller();
        if ((caller == null) || (caller.equals("<anonymous>"))) {
            return null;
        } else {
            return caller;
        }
    }


    public void authorizeOperation(Subject subject, MessageContext context, QName operation)
        throws AuthorizationException {
        if (getCallerIdentity() == null) {
            throw new AuthorizationException("Anonymous access to operation " + operation
                + " is not permitted. Please connect using credentials.");
        }
    }


    public void authorizeService(Subject subject, MessageContext context, QName operation)
        throws AuthorizationException {
        if (getCallerIdentity() == null) {
            throw new AuthorizationException(
                "Anonymous access to this service is not permitted. Please connect usign credentials");
        }
    }


    public void initialize(String serviceName, String serviceEtcDir) throws InitializeException {
    }

}
