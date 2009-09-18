package org.cagrid.csm.authorization.extension.service;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.servicetools.security.AuthorizationExtension;
import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.SecurityServiceProvider;
import gov.nih.nci.security.exceptions.CSException;

import java.io.File;

import javax.security.auth.Subject;
import javax.xml.namespace.QName;
import javax.xml.rpc.handler.MessageContext;

import org.cagrid.csm.authorization.extension.beans.CSMAuthorizationDescription;
import org.cagrid.csm.authorization.extension.beans.CSMServiceAuthorization;
import org.cagrid.csm.authorization.extension.beans.ProtectionMethod;
import org.globus.wsrf.impl.security.authorization.exceptions.AuthorizationException;
import org.globus.wsrf.impl.security.authorization.exceptions.InitializeException;


public class CSMAuthorization implements AuthorizationExtension {

    private CSMServiceAuthorization authorizationDescription = null;


    public static String getCallerIdentity() {
        String caller = org.globus.wsrf.security.SecurityManager.getManager().getCaller();
        if ((caller == null) || (caller.equals("<anonymous>"))) {
            return null;
        } else {
            return caller;
        }
    }


    public void authorizeOperation(Subject subject, MessageContext context, QName qname) throws AuthorizationException {
        String methodName = qname.getLocalPart();
        boolean authorized = false;
        if (authorizationDescription.getCSMMethodAuthorization() != null) {
            for (int i = 0; i < authorizationDescription.getCSMMethodAuthorization().length; i++) {
                if (authorizationDescription.getCSMMethodAuthorization(i).getMethodName().equals(methodName)) {
                    CSMAuthorizationDescription desc = authorizationDescription.getCSMMethodAuthorization(i)
                        .getCSMAuthorizationDescription();
                    try {

                        String object = "";
                        if (desc.getProtectionMethod().equals(ProtectionMethod.ServiceURI)) {
                            org.apache.axis.message.addressing.EndpointReferenceType type = org.globus.wsrf.utils.AddressingUtils
                                .createEndpointReference(null);
                            object = type.getAddress().toString() + ":" + methodName;
                        } else if (desc.getPrivilege().equals(ProtectionMethod.ServiceType)) {
                            object = authorizationDescription.getServiceName() + ":" + methodName;
                        } else {
                            object = desc.getCustomProtectionMethod();
                        }

                        try {
                            AuthorizationManager authManager = SecurityServiceProvider
                                .getUserProvisioningManager(desc.getApplicationContext());
                            authorized = authManager.checkPermission(getCallerIdentity(), object, desc
                                .getPrivilege());

                            if (!authorized) {
                                throw new AuthorizationException("User " + getCallerIdentity() + " not authorized for "
                                    + desc.getPrivilege() + " on " + object);
                            }
                        } catch (CSException cse) {
                            cse.printStackTrace();
                            throw new InitializeException(cse.getMessage(), cse);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new AuthorizationException(e.getMessage(), e);
                    }
                }
            }
        }

        if (!authorized) {
            throw new AuthorizationException("User not authorizied.");
        }

    }


    public void authorizeService(Subject subject, MessageContext context, QName operation)
        throws AuthorizationException {
        if (authorizationDescription.getCSMAuthorizationDescription() != null) {
            boolean authorized = false;
            try {

                String object = "";
                if (authorizationDescription.getCSMAuthorizationDescription().getProtectionMethod().equals(
                    ProtectionMethod.ServiceURI)) {
                    org.apache.axis.message.addressing.EndpointReferenceType type = org.globus.wsrf.utils.AddressingUtils
                        .createEndpointReference(null);
                    object = type.getAddress().toString();
                } else if (authorizationDescription.getCSMAuthorizationDescription().getPrivilege().equals(
                    ProtectionMethod.ServiceType)) {
                    object = authorizationDescription.getServiceName();
                } else {
                    object = authorizationDescription.getCSMAuthorizationDescription().getCustomProtectionMethod();
                }

                try {
                    AuthorizationManager authManager = SecurityServiceProvider
                        .getUserProvisioningManager(authorizationDescription.getCSMAuthorizationDescription()
                            .getApplicationContext());

                    authorized = authManager.checkPermission(getCallerIdentity(), object,
                        authorizationDescription.getCSMAuthorizationDescription().getPrivilege());

                    if (!authorized) {
                        throw new AuthorizationException("User " + getCallerIdentity() + " not authorized for "
                            + this.authorizationDescription.getCSMAuthorizationDescription().getPrivilege() + " on "
                            + object);
                    }

                } catch (CSException cse) {
                    cse.printStackTrace();
                    throw new InitializeException(cse.getMessage(), cse);
                }

            } catch (Exception e) {
                e.printStackTrace();
                throw new AuthorizationException(e.getMessage(), e);
            }
            if (!authorized) {
                throw new AuthorizationException("User not authorizied.");
            }
        }
    }


    public void initialize(String serviceName, String serviceEtcDir) throws InitializeException {

        String authFile = serviceEtcDir + File.separator + serviceName
            + org.cagrid.csm.authorization.extension.common.Constants.CSM_AUTH_FILE_SUFFIX;
        try {
            authorizationDescription = (CSMServiceAuthorization) Utils.deserializeDocument(authFile,
                CSMServiceAuthorization.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InitializeException(e.getMessage(), e);
        }

    }

}
