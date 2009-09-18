package org.cagrid.gridgrouper.authorization.extension.service;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.gridgrouper.client.GridGrouperClientUtils;
import gov.nih.nci.cagrid.introduce.servicetools.security.AuthorizationExtension;
import gov.nih.nci.cagrid.introduce.servicetools.security.SecurityUtils;

import java.io.File;

import javax.security.auth.Subject;
import javax.xml.namespace.QName;
import javax.xml.rpc.handler.MessageContext;

import org.apache.log4j.Logger;
import org.cagrid.gridgrouper.authorization.extension.beans.GridGrouperServiceAuthorization;
import org.cagrid.gridgrouper.authorization.extension.common.Constants;
import org.cagrid.gridgrouper.authorization.extension.gui.MethodAuthorizationPanel;
import org.globus.wsrf.impl.security.authorization.exceptions.AuthorizationException;
import org.globus.wsrf.impl.security.authorization.exceptions.InitializeException;


public class GridGrouperAuthorization implements AuthorizationExtension {

    private GridGrouperServiceAuthorization authorization = null;
    private static final Logger logger = Logger.getLogger(GridGrouperServiceAuthorization.class);

    public void authorizeOperation(Subject subject, MessageContext context, QName operation) throws AuthorizationException {
        String methodName = operation.getLocalPart();
        boolean authorized = false; 
        if(authorization.getGridGrouperMethodAuthorization()!=null){
            for (int i = 0; i < authorization.getGridGrouperMethodAuthorization().length; i++) {
                if(authorization.getGridGrouperMethodAuthorization(i).getMethodName().equals(methodName)){
                    try {
                        authorized = GridGrouperClientUtils.isMember(authorization.getGridGrouperMethodAuthorization(i).getMembershipExpression(), SecurityUtils.getCallerIdentity());
                        if(authorized){
                            logger.info("authorized user " + subject + " to invoke " + operation);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new AuthorizationException(e.getMessage(),e);
                    }
                }
            }
        }
        
        if(!authorized){
            throw new AuthorizationException("User not authorizied.");
        }
        
    }


    public void authorizeService(Subject subject, MessageContext context, QName operation) throws AuthorizationException {
        if (authorization.getMembershipExpression() != null) {
            boolean authorized = false;
            try {
                authorized = GridGrouperClientUtils.isMember(authorization.getMembershipExpression(), SecurityUtils
                    .getCallerIdentity());
                if(authorized){
                    logger.info("authorized user " + subject + " to invoke " + operation);
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
        String authFile = serviceEtcDir + File.separator + serviceName + Constants.GRID_GROUPER_AUTH_FILE_SUFFIX;
        try {
            authorization = (GridGrouperServiceAuthorization) Utils.deserializeDocument(authFile,
                GridGrouperServiceAuthorization.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InitializeException(e.getMessage(), e);
        }
    }

}
