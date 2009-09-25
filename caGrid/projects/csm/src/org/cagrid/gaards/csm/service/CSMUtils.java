package org.cagrid.gaards.csm.service;

import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.SecurityServiceProvider;
import gov.nih.nci.security.authorization.domainobjects.Application;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElement;
import gov.nih.nci.security.dao.ApplicationSearchCriteria;
import gov.nih.nci.security.dao.GroupSearchCriteria;
import gov.nih.nci.security.dao.ProtectionElementSearchCriteria;
import gov.nih.nci.security.exceptions.CSException;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gaards.csm.stubs.types.CSMInternalFault;


public class CSMUtils {

    private static Log log = LogFactory.getLog(CSMUtils.class.getName());


    public static AuthorizationManager getAuthorizationManager(AuthorizationManager auth, DatabaseProperties db,
        long applicationId) throws CSMInternalFault {
        try {

            gov.nih.nci.security.authorization.domainobjects.Application app = auth.getApplicationById(String
                .valueOf(applicationId));
            return getAuthorizationManager(db, app);

        } catch (CSException e) {
            logError(e.getMessage(), e);
            CSMInternalFault fault = new CSMInternalFault();
            fault.setFaultString("Unexpected error obtaining the authorization manager for the application "
                + applicationId + ".");
            throw fault;
        }
    }


    public static AuthorizationManager getAuthorizationManager(DatabaseProperties db, Application app)
        throws CSMInternalFault {

        HashMap<String, String> connectionProperties = new HashMap<String, String>();
        if (gov.nih.nci.cagrid.common.Utils.clean(app.getDatabaseURL()) != null) {
            connectionProperties.put("hibernate.connection.url", app.getDatabaseURL());
        } else {
            connectionProperties.put("hibernate.connection.url", db.getConnectionURL());
        }

        if (gov.nih.nci.cagrid.common.Utils.clean(app.getDatabaseUserName()) != null) {
            connectionProperties.put("hibernate.connection.username", app.getDatabaseUserName());
        } else {
            connectionProperties.put("hibernate.connection.username", db.getUserId());
        }

        if (gov.nih.nci.cagrid.common.Utils.clean(app.getDatabasePassword()) != null) {
            connectionProperties.put("hibernate.connection.password", app.getDatabasePassword());
        } else {
            connectionProperties.put("hibernate.connection.password", db.getPassword());
        }

        if (gov.nih.nci.cagrid.common.Utils.clean(app.getDatabaseDialect()) != null) {
            connectionProperties.put("hibernate.dialect", app.getDatabaseDialect());
        } else {
            connectionProperties.put("hibernate.dialect", db.getHibernateDialect());
        }
        if (gov.nih.nci.cagrid.common.Utils.clean(app.getDatabaseDriver()) != null) {
            connectionProperties.put("hibernate.connection.driver_class", app.getDatabaseDriver());
        } else {
            connectionProperties.put("hibernate.connection.driver_class", db.getDriver());
        }

        try {
            AuthorizationManager auth = (AuthorizationManager) SecurityServiceProvider.getUserProvisioningManager(app
                .getApplicationName(), connectionProperties);

            return auth;
        } catch (CSException e) {
            logError(e.getMessage(), e);
            CSMInternalFault fault = new CSMInternalFault();
            fault.setFaultString("Unexpected error obtaining the authorization manager for the application "
                + app.getApplicationName() + ".");
            throw fault;
        }
    }


    public static ProtectionElement convert(org.cagrid.gaards.csm.bean.ProtectionElement pe) {
        ProtectionElement elem = new ProtectionElement();
        if (pe.getId() != null) {
            elem.setProtectionElementId(pe.getId().longValue());
        }
        elem.setProtectionElementName(pe.getName());
        elem.setProtectionElementDescription(pe.getDescription());
        elem.setObjectId(pe.getObjectId());
        elem.setProtectionElementType(pe.getType());
        elem.setAttribute(pe.getAttribute());
        elem.setValue(pe.getAttributeValue());
        if (pe.getLastUpdated() != null) {
            elem.setUpdateDate(pe.getLastUpdated().getTime());
        }
        return elem;
    }


    public static org.cagrid.gaards.csm.bean.ProtectionElement convert(ProtectionElement pe) {
        org.cagrid.gaards.csm.bean.ProtectionElement elem = new org.cagrid.gaards.csm.bean.ProtectionElement();
        if (pe.getProtectionElementId() != null) {
            elem.setId(new BigInteger(String.valueOf(pe.getProtectionElementId().longValue())));
        }
        elem.setName(pe.getProtectionElementName());
        elem.setDescription(pe.getProtectionElementDescription());
        elem.setObjectId(pe.getObjectId());
        elem.setType(pe.getProtectionElementType());
        elem.setAttribute(pe.getAttribute());
        elem.setAttributeValue(pe.getValue());
        if (pe.getApplication() != null) {
            elem.setApplicationId(new BigInteger(String.valueOf(pe.getApplication().getApplicationId().longValue())));
        }
        if (pe.getUpdateDate() != null) {
            Calendar c = new GregorianCalendar();
            c.setTime(pe.getUpdateDate());
            elem.setLastUpdated(c);
        }
        return elem;
    }


    public static ApplicationSearchCriteria convert(org.cagrid.gaards.csm.bean.ApplicationSearchCriteria criteria) {
        Application app = new Application();
        if (criteria != null) {
            if (criteria.getId() != null) {
                app.setApplicationId(criteria.getId().longValue());
            }
            app.setApplicationName(criteria.getName());
            app.setApplicationDescription(criteria.getDescription());
        }
        return new ApplicationSearchCriteria(app);
    }


    public static ProtectionElementSearchCriteria convert(
        org.cagrid.gaards.csm.bean.ProtectionElementSearchCriteria criteria) {
        ProtectionElement pe = new ProtectionElement();
        if (criteria != null) {
            if (criteria.getId() != null) {
                pe.setProtectionElementId(criteria.getId().longValue());
            }
            pe.setAttribute(criteria.getAttribute());
            pe.setObjectId(criteria.getObjectId());
            pe.setProtectionElementDescription(criteria.getDescription());
            pe.setProtectionElementName(criteria.getName());
            pe.setProtectionElementType(criteria.getType());
            pe.setValue(criteria.getAttributeValue());
        }
        return new ProtectionElementSearchCriteria(pe);
    }


    public static org.cagrid.gaards.csm.bean.Application convert(Application app) {
        org.cagrid.gaards.csm.bean.Application a = new org.cagrid.gaards.csm.bean.Application();
        if (app.getApplicationId() != null) {
            a.setId(new BigInteger(String.valueOf(app.getApplicationId())));
        }
        a.setName(app.getApplicationName());
        a.setDescription(app.getApplicationDescription());
        return a;
    }


    public static Application convert(org.cagrid.gaards.csm.bean.Application app) {
        Application a = new Application();
        if (app.getId() != null) {
            a.setApplicationId(app.getId().longValue());
        }
        a.setApplicationName(app.getName());
        a.setApplicationDescription(app.getDescription());
        return a;
    }


    public static List<org.cagrid.gaards.csm.bean.Application> convert(List<Application> apps) {
        List<org.cagrid.gaards.csm.bean.Application> list = new ArrayList<org.cagrid.gaards.csm.bean.Application>();
        for (int i = 0; i < apps.size(); i++) {
            list.add(convert(apps.get(i)));
        }
        return list;
    }


    public static Group getAdminGroup(AuthorizationManager auth, String applicationName) throws CSMInternalFault {
        try {
            Application webService = auth.getApplication(Constants.CSM_WEB_SERVICE_CONTEXT);
            Group group = new Group();
            group.setApplication(webService);
            group.setGroupName(applicationName + " " + Constants.ADMIN_GROUP_SUFFIX);
            List<Group> groups = auth.getObjects(new GroupSearchCriteria(group));
            return groups.get(0);
        } catch (Exception e) {
            logError(e.getMessage(), e);
            CSMInternalFault fault = new CSMInternalFault();
            fault.setFaultString("An unexpected error occurred loading the admin group for the application "
                + applicationName + ".");
            throw fault;
        }
    }


    public static Group getWebServiceAdminGroup(AuthorizationManager auth) throws CSMInternalFault {
        return getAdminGroup(auth, Constants.CSM_WEB_SERVICE_CONTEXT);
    }


    private static void logInfo(String s) {
        System.out.println(s);
        log.info(s);
    }


    private static void logError(String s, Exception e) {
        log.error(s, e);
    }
}
