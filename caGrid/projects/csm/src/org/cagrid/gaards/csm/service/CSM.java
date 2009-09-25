package org.cagrid.gaards.csm.service;

import gov.nih.nci.cagrid.common.FaultHelper;
import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.ProtectionGroup;
import gov.nih.nci.security.dao.GroupSearchCriteria;
import gov.nih.nci.security.dao.ProtectionGroupSearchCriteria;
import gov.nih.nci.security.exceptions.CSException;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gaards.csm.bean.Application;
import org.cagrid.gaards.csm.bean.ApplicationSearchCriteria;
import org.cagrid.gaards.csm.bean.ProtectionElement;
import org.cagrid.gaards.csm.stubs.types.AccessDeniedFault;
import org.cagrid.gaards.csm.stubs.types.CSMInternalFault;
import org.cagrid.gaards.csm.stubs.types.CSMTransactionFault;


public class CSM {

    private CSMProperties conf;
    private AuthorizationManager auth;
    private gov.nih.nci.security.authorization.domainobjects.Application webService;
    private Log log;
    private Map<String, AuthorizationManager> managers;


    public CSM(CSMProperties conf) throws Exception {
        this.conf = conf;
        this.log = LogFactory.getLog(getClass().getName());
        this.auth = CSMInitializer.getAuthorizationManager(this.conf.getDatabaseProperties());
        this.webService = auth.getApplication(Constants.CSM_WEB_SERVICE_CONTEXT);
        this.managers = new HashMap<String, AuthorizationManager>();
    }


    public void addWebServiceAdmin(String gridIdentity) throws CSMInternalFault {
        CSMInitializer.addWebServiceAdmin(auth, gridIdentity);
    }


    public List<Application> getApplications(ApplicationSearchCriteria applicationSearchCriteria)
        throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault {
        List<gov.nih.nci.security.authorization.domainobjects.Application> apps = this.auth.getObjects(CSMUtils
            .convert(applicationSearchCriteria));
        return CSMUtils.convert(apps);
    }


    public Application createApplication(String callerIdentity, Application app) throws CSMInternalFault,
        AccessDeniedFault, CSMTransactionFault {
        checkWebServiceAdmin(callerIdentity);
        gov.nih.nci.security.authorization.domainobjects.Application a = CSMUtils.convert(app);
        try {
            this.auth.createApplication(a);
            gov.nih.nci.security.authorization.domainobjects.Application search = new gov.nih.nci.security.authorization.domainobjects.Application();
            search.setApplicationName(app.getName());
            List<gov.nih.nci.security.authorization.domainobjects.Application> apps = this.auth
                .getObjects(new gov.nih.nci.security.dao.ApplicationSearchCriteria(search));
            gov.nih.nci.security.authorization.domainobjects.Application created = apps.get(0);
            CSMInitializer.initializeApplication(auth, auth.getApplication(Constants.CSM_WEB_SERVICE_CONTEXT), created);
            return CSMUtils.convert(created);
        } catch (Exception e) {
            String error = "Error creating the application " + app.getName() + ":\n" + e.getMessage();
            log.error(error, e);
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMTransactionFault) helper.getFault();
            throw fault;
        }
    }


    public void removeApplication(String callerIdentity, long applicationId) throws CSMInternalFault,
        AccessDeniedFault, CSMTransactionFault {
        checkWebServiceAdmin(callerIdentity);
        try {
            gov.nih.nci.security.authorization.domainobjects.Application app = auth.getApplicationById(String
                .valueOf(applicationId));

            auth.removeApplication(String.valueOf(applicationId));

            // Remove protection element
            gov.nih.nci.security.authorization.domainobjects.ProtectionElement pe = new gov.nih.nci.security.authorization.domainobjects.ProtectionElement();
            pe.setApplication(webService);
            pe.setProtectionElementName(app.getApplicationName());
            List<gov.nih.nci.security.authorization.domainobjects.ProtectionElement> pes = auth
                .getObjects(new gov.nih.nci.security.dao.ProtectionElementSearchCriteria(pe));
            if (pes.size() > 0) {
                auth.removeProtectionElement(String.valueOf(pes.get(0).getProtectionElementId()));
            }
            // Remove protection group
            ProtectionGroup pg = new ProtectionGroup();
            pg.setApplication(webService);
            pg.setProtectionGroupName(app.getApplicationName());
            List<ProtectionGroup> pgs = auth.getObjects(new ProtectionGroupSearchCriteria(pg));
            if (pgs.size() > 0) {
                auth.removeProtectionGroup(String.valueOf(pgs.get(0).getProtectionGroupId()));
            }

            // Remove Group

            Group grp = new Group();
            grp.setApplication(webService);
            grp.setGroupName(app.getApplicationName() + " " + Constants.ADMIN_GROUP_SUFFIX);
            List<Group> grps = auth.getObjects(new GroupSearchCriteria(grp));
            if (grps.size() > 0) {
                auth.removeGroup(String.valueOf(grps.get(0).getGroupId()));
            }

        } catch (Exception e) {
            String error = "Error removing the application " + applicationId + ":\n" + e.getMessage();
            log.error(error, e);
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMTransactionFault) helper.getFault();
            throw fault;
        }
    }


    public ProtectionElement createProtectionElement(String callerIdentity, ProtectionElement pe)
        throws CSMInternalFault, AccessDeniedFault, CSMTransactionFault {
        if ((pe.getApplicationId() == null) || (pe.getApplicationId().longValue() <= 0)) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("No application specified to add the protection element to.!!!");
            throw fault;
        } else {
            long applicationId = pe.getApplicationId().longValue();
            AuthorizationManager am = getAuthorizationManager(applicationId);
            checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());
            try {

                am.createProtectionElement(CSMUtils.convert(pe));
                gov.nih.nci.security.authorization.domainobjects.ProtectionElement search = new gov.nih.nci.security.authorization.domainobjects.ProtectionElement();
                search.setProtectionElementName(pe.getName());
                search.setObjectId(pe.getObjectId());
                List<gov.nih.nci.security.authorization.domainobjects.ProtectionElement> result = this.auth
                    .getObjects(new gov.nih.nci.security.dao.ProtectionElementSearchCriteria(search));
                return CSMUtils.convert(result.get(0));
            } catch (Exception e) {
                String error = "Error creating the protection element " + pe.getName() + ":\n" + e.getMessage();
                log.error(error, e);
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString(error);
                FaultHelper helper = new FaultHelper(fault);
                helper.addFaultCause(e);
                fault = (CSMTransactionFault) helper.getFault();
                throw fault;
            }
        }
    }


    public List<ProtectionElement> getProtectionElements(String callerIdentity,
        org.cagrid.gaards.csm.bean.ProtectionElementSearchCriteria criteria) throws CSMInternalFault,
        AccessDeniedFault, CSMTransactionFault {
        if ((criteria.getApplicationId() == null) || (criteria.getApplicationId().longValue() <= 0)) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault
                .setFaultString("No application specified in the protection element search criteria when one is required!!!");
            throw fault;
        } else {
            long applicationId = criteria.getApplicationId().longValue();
            AuthorizationManager am = getAuthorizationManager(applicationId);
            checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());
            try {

                List<gov.nih.nci.security.authorization.domainobjects.ProtectionElement> result = this.auth
                    .getObjects(CSMUtils.convert(criteria));
                List<ProtectionElement> pes = new ArrayList<ProtectionElement>();
                for (int i = 0; i < result.size(); i++) {
                    pes.add(CSMUtils.convert(result.get(i)));
                }
                return pes;
            } catch (Exception e) {
                String error = "Error searching for protection elements:\n" + e.getMessage();
                log.error(error, e);
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString(error);
                FaultHelper helper = new FaultHelper(fault);
                helper.addFaultCause(e);
                fault = (CSMTransactionFault) helper.getFault();
                throw fault;
            }
        }
    }


    public void removeProtectionElement(String callerIdentity, long applicationId, long protectionElementId)
        throws CSMInternalFault, AccessDeniedFault, CSMTransactionFault {
        if (applicationId <= 0) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("Cannot remove protection element, invalid application id specified.");
            throw fault;
        } else {
            AuthorizationManager am = getAuthorizationManager(applicationId);
            checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());
            try {
                gov.nih.nci.security.authorization.domainobjects.ProtectionElement pe = auth
                    .getProtectionElement(String.valueOf(protectionElementId));
                if (pe.getApplication().getApplicationId().longValue() == applicationId) {
                    am.removeProtectionElement(String.valueOf(protectionElementId));
                } else {
                    AccessDeniedFault fault = new AccessDeniedFault();
                    fault.setFaultString("Cannot remove a protection element that belongs to another application.");
                    throw fault;
                }
            } catch (CSException e) {
                String error = "Error removing the protection element " + protectionElementId + ":\n" + e.getMessage();
                log.error(error, e);
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString(error);
                FaultHelper helper = new FaultHelper(fault);
                helper.addFaultCause(e);
                fault = (CSMTransactionFault) helper.getFault();
                throw fault;
            }
        }

    }


    private AuthorizationManager getAuthorizationManager(long applicationId) throws CSMInternalFault {
        String key = String.valueOf(applicationId);
        if (this.managers.containsKey(key)) {
            return this.managers.get(key);
        } else {
            AuthorizationManager am = CSMUtils.getAuthorizationManager(this.auth, this.conf.getDatabaseProperties(),
                applicationId);
            this.managers.put(key, am);
            return am;
        }
    }


    private void checkApplictionAdmin(String callerIdentity, String applicationName) throws CSMInternalFault,
        AccessDeniedFault {
        try {
            if (!auth.checkPermission(callerIdentity, applicationName, Constants.ADMIN_PRIVILEGE)) {
                AccessDeniedFault fault = new AccessDeniedFault();
                fault.setFaultString("You are not an administrator for the application " + applicationName + "!!!");
                throw fault;
            }
        } catch (CSException e) {
            log.error(e.getMessage(), e);
            CSMInternalFault fault = new CSMInternalFault();
            fault
                .setFaultString("An unexpected error occurred determining administrative access to the CSM Web Service.");
            throw fault;
        }

    }


    private void checkWebServiceAdmin(String callerIdentity) throws CSMInternalFault, AccessDeniedFault {
        try {
            if (!auth.checkPermission(callerIdentity, Constants.CSM_WEB_SERVICE_CONTEXT, Constants.ADMIN_PRIVILEGE)) {
                AccessDeniedFault fault = new AccessDeniedFault();
                fault.setFaultString("You are not a CSM Web Service administrator!!!");
                throw fault;
            }
        } catch (CSException e) {
            log.error(e.getMessage(), e);
            CSMInternalFault fault = new CSMInternalFault();
            fault
                .setFaultString("An unexpected error occurred determining administrative access to the CSM Web Service.");
            throw fault;
        }

    }


    public AuthorizationManager getAuthorizationManager() {
        return auth;
    }
}
