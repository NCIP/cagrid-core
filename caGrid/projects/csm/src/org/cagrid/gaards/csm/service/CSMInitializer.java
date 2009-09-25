package org.cagrid.gaards.csm.service;

import gov.nih.nci.logging.api.logger.hibernate.HibernateSessionFactoryHelper;
import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.SecurityServiceProvider;
import gov.nih.nci.security.authorization.domainobjects.Application;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.Privilege;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElement;
import gov.nih.nci.security.authorization.domainobjects.ProtectionGroup;
import gov.nih.nci.security.authorization.domainobjects.Role;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.dao.ApplicationSearchCriteria;
import gov.nih.nci.security.dao.GroupSearchCriteria;
import gov.nih.nci.security.dao.PrivilegeSearchCriteria;
import gov.nih.nci.security.dao.ProtectionElementSearchCriteria;
import gov.nih.nci.security.dao.ProtectionGroupSearchCriteria;
import gov.nih.nci.security.dao.RoleSearchCriteria;
import gov.nih.nci.security.dao.UserSearchCriteria;
import gov.nih.nci.security.exceptions.CSException;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;
import gov.nih.nci.security.exceptions.CSTransactionException;
import gov.nih.nci.security.system.ApplicationSessionFactory;
import gov.nih.nci.security.util.ObjectUpdater;
import gov.nih.nci.security.util.StringUtilities;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gaards.csm.stubs.types.CSMInternalFault;
import org.hibernate.PropertyValueException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.GenericJDBCException;


public class CSMInitializer {

    private static Log log = LogFactory.getLog(CSMInitializer.class.getName());


    public static AuthorizationManager getAuthorizationManager(DatabaseProperties db) throws CSMInternalFault {
        HashMap<String, String> connectionProperties = new HashMap<String, String>();
        connectionProperties.put("hibernate.connection.url", db.getConnectionURL());
        connectionProperties.put("hibernate.connection.username", db.getUserId());
        connectionProperties.put("hibernate.connection.password", db.getPassword());
        connectionProperties.put("hibernate.dialect", db.getHibernateDialect());
        connectionProperties.put("hibernate.connection.driver_class", db.getDriver());
        try {
            SessionFactory sf = ApplicationSessionFactory.getSessionFactory(Constants.CSM_WEB_SERVICE_CONTEXT,
                connectionProperties);
            Application webService = getApplication(sf, Constants.CSM_WEB_SERVICE_CONTEXT);
            if (webService == null) {
                logInfo("No application for the CSM Web Service exists, attempting to create one.");
                webService = new Application();
                webService.setApplicationName(Constants.CSM_WEB_SERVICE_CONTEXT);
                webService.setApplicationDescription(Constants.CSM_WEB_SERVICE_DESCRIPTION);
                webService.setUpdateDate(new Date());
                try {
                    createObject(sf, webService);
                } catch (Exception e) {
                    logError(e.getMessage(), e);
                    CSMInternalFault fault = new CSMInternalFault();
                    fault
                        .setFaultString("Error initializing CSM Web Service, could not create the application required for administration.");
                    throw fault;
                }
                logInfo("Successfully created CSM Web Service application.");
            }

            AuthorizationManager auth = (AuthorizationManager) SecurityServiceProvider.getUserProvisioningManager(
                Constants.CSM_WEB_SERVICE_CONTEXT, connectionProperties);
            createAdminPrivilegeIfNeeded(auth);
            createAdministratorRoleIfNeeded(auth, webService);
            createAdminGroupIfNeeded(auth, webService);
            initializeApplications(auth, webService);
            return auth;
        } catch (CSException e) {
            logError(e.getMessage(), e);
            CSMInternalFault fault = new CSMInternalFault();
            fault.setFaultString("Unexpected error initializing the CSM Web Service.");
            throw fault;
        }
    }


    private static void createAdminGroupIfNeeded(AuthorizationManager auth, Application webService)
        throws CSMInternalFault {
        Group grp = new Group();
        grp.setApplication(webService);
        grp.setGroupName(webService.getApplicationName() + " " + Constants.ADMIN_GROUP_SUFFIX);
        List<ProtectionGroup> grps = auth.getObjects(new GroupSearchCriteria(grp));
        if (grps.size() == 0) {
            logInfo("No administrators group found for the CSM Web Service.");
            grp.setGroupDesc("CSM Web Service administrators group");
            try {
                auth.createGroup(grp);
            } catch (Exception e) {
                logError(e.getMessage(), e);
                CSMInternalFault fault = new CSMInternalFault();
                fault
                    .setFaultString("Error initializing the CSM Web Service could not create an administrators group.");
                throw fault;
            }
            logInfo("Successfully created an administrators group for the CSM Web Service.");
        }
    }


    private static void createAdministratorRoleIfNeeded(AuthorizationManager auth, Application webService)
        throws CSMInternalFault {
        Privilege priv = new Privilege();
        priv.setName(Constants.ADMIN_PRIVILEGE);
        List<Privilege> privileges = auth.getObjects(new PrivilegeSearchCriteria(priv));
        priv = privileges.get(0);
        Role r = new Role();
        r.setName(Constants.ADMIN_ROLE);
        List<Role> roles = auth.getObjects(new RoleSearchCriteria(r));
        if (roles.size() == 0) {
            logInfo("Administrator role does not yet exist adding.....");
            r.setDesc(Constants.ADMIN_ROLE_DESCRIPTION);
            r.setApplication(webService);
            Set<Privilege> privs = new HashSet<Privilege>();
            privs.add(priv);
            r.setPrivileges(privs);
            try {
                auth.createRole(r);
            } catch (Exception e) {
                logError(e.getMessage(), e);
                CSMInternalFault fault = new CSMInternalFault();
                fault.setFaultString("Error initializing the CSM Web Service, could not create the "
                    + Constants.ADMIN_ROLE + " role.");
                throw fault;
            }
            logInfo("Successfully added admin role to the CSM Web Service application.");
        }
    }


    private static void createAdminPrivilegeIfNeeded(AuthorizationManager auth) throws CSMInternalFault {
        Privilege priv = new Privilege();
        priv.setName(Constants.ADMIN_PRIVILEGE);
        List<Privilege> privs = auth.getObjects(new PrivilegeSearchCriteria(priv));
        if (privs.size() == 0) {
            logInfo("Admin privilege does not yet exist adding.....");
            priv.setDesc(Constants.CSM_WEB_SERVICE_DESCRIPTION);
            try {
                auth.createPrivilege(priv);
            } catch (Exception e) {
                logError(e.getMessage(), e);
                CSMInternalFault fault = new CSMInternalFault();
                fault.setFaultString("Error initializing the CSM Web Service, could not create the "
                    + Constants.ADMIN_PRIVILEGE + " privilege.");
                throw fault;
            }
            logInfo("Successfully added admin privilege to the CSM Web Service application.");
        }
    }


    private static void initializeApplications(AuthorizationManager auth, Application webService)
        throws CSMInternalFault {
        List<Application> apps = auth.getObjects(new ApplicationSearchCriteria(new Application()));
        for (int i = 0; i < apps.size(); i++) {
            Application a = apps.get(i);
            initializeApplication(auth, webService, a);
        }
    }


    public static void initializeApplication(AuthorizationManager auth, Application webService, Application a)
        throws CSMInternalFault {
        logInfo("Initializing CSM Web Service to host access control policy for the application "
            + a.getApplicationName() + ".");
        boolean addMapping = false;
        ProtectionElement app = new ProtectionElement();
        app.setApplication(webService);
        app.setObjectId(a.getApplicationName());
        app.setProtectionElementName(a.getApplicationName());
        ProtectionElementSearchCriteria pesc = new ProtectionElementSearchCriteria(app);
        List<ProtectionElement> pes = auth.getObjects(pesc);
        boolean linkPEToPG = false;
        if (pes.size() == 0) {
            logInfo("No protection element found for application " + a.getApplicationName() + ".");
            app.setProtectionElementDescription("CSM Web Service protection element for the application "
                + a.getApplicationName() + ".");
            try {
                auth.createProtectionElement(app);
                linkPEToPG = true;
            } catch (Exception e) {
                logError(e.getMessage(), e);
                CSMInternalFault fault = new CSMInternalFault();
                fault.setFaultString("Error initializing the application, " + a.getApplicationName()
                    + " could not create a protection element for the application.");
                throw fault;
            }
            logInfo("Created protection element for application " + a.getApplicationName() + ".");
        }
        ProtectionGroup pg = new ProtectionGroup();
        pg.setApplication(webService);
        pg.setProtectionGroupName(a.getApplicationName());
        List<ProtectionGroup> pgs = auth.getObjects(new ProtectionGroupSearchCriteria(pg));
        if (pgs.size() == 0) {
            logInfo("No protection group found for application " + a.getApplicationName() + ".");
            addMapping = true;
            pg.setProtectionGroupDescription("CSM Web Service protection group for the application "
                + a.getApplicationName() + ".");
            try {
                auth.createProtectionGroup(pg);
                linkPEToPG = true;
            } catch (Exception e) {
                logError(e.getMessage(), e);
                CSMInternalFault fault = new CSMInternalFault();
                fault.setFaultString("Error initializing the application, " + a.getApplicationName()
                    + " could not create a protection group for the application.");
                throw fault;
            }
            logInfo("Successfully created a protection group for application " + a.getApplicationName() + ".");
        }

        if (linkPEToPG) {
            List<ProtectionElement> peList = auth.getObjects(pesc);
            List<ProtectionGroup> pgList = auth.getObjects(new ProtectionGroupSearchCriteria(pg));
            ProtectionElement pElement = peList.get(0);
            ProtectionGroup pGroup = pgList.get(0);
            String[] elements = new String[1];
            elements[0] = String.valueOf(pElement.getProtectionElementId());
            try {
                auth.addProtectionElements(String.valueOf(pGroup.getProtectionGroupId()), elements);
                logInfo("Successfully added the protection element " + pElement.getProtectionElementName()
                    + " to the protection group " + pGroup.getProtectionGroupName() + ".");
            } catch (Exception e) {
                logError(e.getMessage(), e);
                CSMInternalFault fault = new CSMInternalFault();
                fault.setFaultString("Error initializing the application, " + a.getApplicationName()
                    + " could add the protection element to the protection group.");
                throw fault;
            }
        }

        Group grp = new Group();
        grp.setApplication(webService);
        grp.setGroupName(a.getApplicationName() + " " + Constants.ADMIN_GROUP_SUFFIX);
        List<Group> grps = auth.getObjects(new GroupSearchCriteria(grp));
        if (grps.size() == 0) {
            logInfo("No administrators group found for application " + a.getApplicationName() + ".");
            addMapping = true;
            grp.setGroupDesc("CSM Web Service administrator group for the application " + a.getApplicationName() + ".");
            try {
                auth.createGroup(grp);
            } catch (Exception e) {
                logError(e.getMessage(), e);
                CSMInternalFault fault = new CSMInternalFault();
                fault.setFaultString("Error initializing the application, " + a.getApplicationName()
                    + " could not create an administrators group for the application.");
                throw fault;
            }
            logInfo("Successfully created an administrators group (" + grp.getGroupName() + ") for application "
                + a.getApplicationName() + ".");
        }

        if (addMapping) {
            logInfo("Access control rights on the application " + a.getApplicationName()
                + " have not been assigned, attempting to assign....");
            Role r = new Role();
            r.setName(Constants.ADMIN_ROLE);
            List<Role> roles = auth.getObjects(new RoleSearchCriteria(r));
            r = roles.get(0);

            String[] rls = new String[1];
            rls[0] = String.valueOf(r.getId());
            ProtectionGroup protectionGroup = new ProtectionGroup();
            protectionGroup.setApplication(webService);
            protectionGroup.setProtectionGroupName(pg.getProtectionGroupName());
            List<ProtectionGroup> protectionGroups = auth
                .getObjects(new ProtectionGroupSearchCriteria(protectionGroup));
            protectionGroup = protectionGroups.get(0);

            Group group = new Group();
            group.setApplication(webService);
            group.setGroupName(grp.getGroupName());
            List<Group> groups = auth.getObjects(new GroupSearchCriteria(group));
            group = groups.get(0);
            try {
                auth.assignGroupRoleToProtectionGroup(String.valueOf(protectionGroup.getProtectionGroupId()), String
                    .valueOf(group.getGroupId()), rls);
                logInfo("Successfully assigned the members of the group " + group.getGroupName() + ", the role "
                    + r.getName() + ", on the application " + a.getApplicationName() + ".");
            } catch (Exception e) {
                logError(e.getMessage(), e);
                CSMInternalFault fault = new CSMInternalFault();
                fault.setFaultString("Error initializing the application, " + a.getApplicationName()
                    + " could not assign members of the group " + group.getGroupName() + " the role " + r.getName()
                    + " on the application " + a.getApplicationName() + ".");
                throw fault;
            }

            if (!a.getApplicationName().equals(Constants.CSM_WEB_SERVICE_CONTEXT)) {
                Group wheel = CSMUtils.getWebServiceAdminGroup(auth);
                try {

                    auth.assignGroupRoleToProtectionGroup(String.valueOf(protectionGroup.getProtectionGroupId()),
                        String.valueOf(wheel.getGroupId()), rls);
                    logInfo("Successfully assigned the members of the group " + wheel.getGroupName() + ", the role "
                        + r.getName() + ", on the application " + a.getApplicationName() + ".");
                } catch (Exception e) {
                    logError(e.getMessage(), e);
                    CSMInternalFault fault = new CSMInternalFault();
                    fault.setFaultString("Error initializing the application, " + a.getApplicationName()
                        + " could not assign members of the group " + wheel.getGroupName() + " the role " + r.getName()
                        + " on the application " + a.getApplicationName() + ".");
                    throw fault;
                }
            }

        }
    }


    public static void addWebServiceAdmin(AuthorizationManager auth, String adminIdentity) throws CSMInternalFault {
        Group grp = CSMUtils.getWebServiceAdminGroup(auth);
        User u = getUserCreateIfNeeded(auth, adminIdentity);
        String[] users = new String[1];
        users[0] = String.valueOf(u.getUserId());
        try {
            auth.addUsersToGroup(String.valueOf(grp.getGroupId()), users);
        } catch (Exception e) {
            logError(e.getMessage(), e);
            CSMInternalFault fault = new CSMInternalFault();
            fault.setFaultString("An unexpected error occurred in addin the user, " + adminIdentity
                + " to the CSM Web Service administrators group.");
            throw fault;
        }
    }


    public static void addApplicationAdmin(AuthorizationManager auth, String applicationName, String adminIdentity) throws CSMInternalFault {
        Group grp = CSMUtils.getAdminGroup(auth, applicationName);
        User u = getUserCreateIfNeeded(auth, adminIdentity);
        String[] users = new String[1];
        users[0] = String.valueOf(u.getUserId());
        try {
            auth.addUsersToGroup(String.valueOf(grp.getGroupId()), users);
        } catch (Exception e) {
            logError(e.getMessage(), e);
            CSMInternalFault fault = new CSMInternalFault();
            fault.setFaultString("An unexpected error occurred in addin the user, " + adminIdentity
                + " to the CSM Web Service administrators group.");
            throw fault;
        }
    }


    public static User getUserCreateIfNeeded(AuthorizationManager auth, String userIdentity) throws CSMInternalFault {
        User u = new User();
        u.setLoginName(userIdentity);
        List<User> users = auth.getObjects(new UserSearchCriteria(u));
        if (users.size() == 0) {
            try {
                auth.createUser(u);
            } catch (Exception e) {
                logError(e.getMessage(), e);
                CSMInternalFault fault = new CSMInternalFault();
                fault.setFaultString("An unexpected error registering the user, " + userIdentity + ".");
                throw fault;
            }
            users = auth.getObjects(new UserSearchCriteria(u));
            return users.get(0);
        } else {
            return users.get(0);
        }
    }


    private static void logInfo(String s) {
        System.out.println(s);
        log.info(s);
    }


    private static void logError(String s, Exception e) {
        log.error(s, e);
    }


    private static void createObject(SessionFactory sf, Object obj) throws CSTransactionException {
        Session s = null;
        Transaction t = null;
        try {

            try {
                obj = ObjectUpdater.trimObjectsStringFieldValues(obj);
            } catch (Exception e) {
                throw new CSObjectNotFoundException(e);
            }

            s = HibernateSessionFactoryHelper.getAuditSession(sf);
            t = s.beginTransaction();
            s.save(obj);
            t.commit();
            s.flush();

        } catch (PropertyValueException pve) {
            try {
                t.rollback();
            } catch (Exception ex3) {

            }

            throw new CSTransactionException("An error occured in creating the "
                + StringUtilities.getClassName(obj.getClass().getName()) + ".\n"
                + " A null value was passed for a required attribute "
                + pve.getMessage().substring(pve.getMessage().indexOf(":")), pve);
        } catch (ConstraintViolationException cve) {
            try {
                t.rollback();
            } catch (Exception ex3) {

            }

            throw new CSTransactionException("An error occured in creating the "
                + StringUtilities.getClassName(obj.getClass().getName()) + ".\n"
                + " Duplicate entry was found in the database for the entered data", cve);
        } catch (Exception ex) {

            try {
                t.rollback();
            } catch (Exception ex3) {

            }

            throw new CSTransactionException("An error occured in creating the "
                + StringUtilities.getClassName(obj.getClass().getName()) + "\n" + ex.getMessage(), ex);
        } finally {
            try {

                s.close();
            } catch (Exception ex2) {

            }
        }

    }


    private static Application getApplication(SessionFactory sf, String contextName) throws CSObjectNotFoundException {
        Session s = null;

        try {
            Application search = new Application();
            search.setApplicationName(contextName);
            s = HibernateSessionFactoryHelper.getAuditSession(sf);

            Query q = s.createQuery("from Application as app where app.applicationName='" + contextName + "'");
            List list = q.list();

            if (list.size() == 0) {
                return null;
            }
            return (Application) list.get(0);

        } catch (GenericJDBCException eex) {

            throw new CSObjectNotFoundException(
                " Invalid database login credentials in the application hibernate configuration file.", eex);

        } catch (Exception ex) {
            throw new CSObjectNotFoundException(
                "An error occured in retrieving Application for the given Context Name\n" + ex.getMessage(), ex);

        } finally {
            try {
                s.close();
            } catch (Exception ex2) {

            }
        }

    }

}
