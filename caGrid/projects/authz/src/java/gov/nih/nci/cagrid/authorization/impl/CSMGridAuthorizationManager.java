/**
 * 
 */
package gov.nih.nci.cagrid.authorization.impl;

import gov.nih.nci.cagrid.authorization.GridAuthorizationManager;
import gov.nih.nci.cagrid.authorization.GridGroupName;
import gov.nih.nci.cagrid.authorization.GridGrouperClientFactory;
import gov.nih.nci.cagrid.gridgrouper.grouper.GrouperI;
import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.authorization.domainobjects.Application;
import gov.nih.nci.security.authorization.domainobjects.ApplicationContext;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElement;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.authorization.jaas.AccessPermission;
import gov.nih.nci.security.exceptions.CSConfigurationException;
import gov.nih.nci.security.exceptions.CSException;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;
import gov.nih.nci.security.exceptions.CSTransactionException;
import gov.nih.nci.security.provisioning.UserProvisioningManagerImpl;

import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

/**
 * An implementation of gov.nih.nci.security.AuthorizationManager that enables
 * integration between local CSM policy and GridGrouper groups. In most cases,
 * this class simply delegates to the provided AuthorizationManager. However,
 * for methods that check access permissions for a specified individual, the
 * following logic is used. <p/> Delegate to the supplied AuthenticationManager.
 * If access is granted, return that decision. If access is denied, retrieve all
 * accessible groups for the protection element in question. For each accessible
 * group, if the group name is recognized to be a GridGrouper group name, ask
 * the GridGrouper service if the individual is a member of the group. If the
 * individual is a member of any of the accessible groups, grant access,
 * otherwise deny access. <p/> {@link GridGroupName#isGridGroupName(String)} is
 * used to recognize GridGrouper group names. <p/> <p/> The methods that are
 * intercepted are:
 * <ul>
 * <li>{@link #checkPermission(AccessPermission, String)}</li>
 * <li>{@link #checkPermission(AccessPermission, Subject)}</li>
 * <li>{@link #checkPermission(String, String, String)}</li>
 * <li>{@link #checkPermission(String, String, String, String)}</li>
 * </ul>
 * 
 * <b>NOTE:</b> This class no longer implements the
 * gov.nih.nci.security.AuthenticationManager interface, as it did in caGrid
 * 1.0.
 * 
 * @author <a href="mailto:joshua.phillips@semanticbits.com">Joshua Phillips</a>
 * 
 */
public class CSMGridAuthorizationManager implements AuthorizationManager,
		GridAuthorizationManager {

	private static final Log logger = LogFactory
			.getLog(CSMGridAuthorizationManager.class);

	private AuthorizationManager authorizationManager;

	private Map<String, GrouperI> gridGrouperMap = new HashMap<String, GrouperI>();

	private GridGrouperClientFactory gridGrouperClientFactory;

	/*
	 * Amount of time, in milliseconds, until a call to a GridGrouper instance
	 * will timeout.
	 */
	private long gridGrouperThreadTimeout = 30000;

	public CSMGridAuthorizationManager() {

	}

	public CSMGridAuthorizationManager(String ctxName) {
		initialize(ctxName);
	}

	public boolean isAuthorized(String identity, String objectId,
			String privilege) {
		return isAuthorized(identity, objectId, null, privilege);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.nih.nci.cagrid.authorization.GridAuthorizationManager#isAuthorized(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public boolean isAuthorized(String identity, String objectId,
			String attributeId, String privilege) {
		
		
		Assert.notNull(this.authorizationManager, "An AuthorizationManager is required");
		
		boolean isAuthorized = false;

		
		try {
			if (attributeId != null) {
				isAuthorized = this.authorizationManager.checkPermission(
						identity, objectId, attributeId, privilege);
			} else {
				isAuthorized = this.authorizationManager.checkPermission(
						identity, objectId, privilege);
			}
			if (!isAuthorized) { // then, check grid grouper groups

				List groups = null;
				if (attributeId != null) {
					groups = getAccessibleGroups(objectId, attributeId,
							privilege);
				} else {
					groups = getAccessibleGroups(objectId, privilege);
				}
				Assert.notNull(groups, "Groups must not be empty");
				for (Iterator i = groups.iterator(); i.hasNext();) {
					Group group = (Group) i.next();
					String name = group.getGroupName();

					GridGroupName gName = new GridGroupName(name);
					GrouperI client = getGridGrouper(gName.getUrl());
					Assert.notNull(client, "GridGrouper client must not be null");
					GridGrouperThread t = new GridGrouperThread(client,
							identity, gName.getName());
					t.start();
					try {
						t.join(getGridGrouperThreadTimeout());
					} catch (InterruptedException ex) {
						logger.warn("GridGrouper search thread interrupted: "
								+ ex.getMessage(), ex);
					}
					if (t.ex != null) {
						throw new RuntimeException(
								"GridGrouper search failed: "
										+ t.ex.getMessage(), t.ex);
					}
					if (!t.finished) {
						throw new RuntimeException(
								"GridGrouper search did not finish");
					}

					if (t.isMember) {
						isAuthorized = true;
						break;
					}

				}
			}
		} catch (Exception ex) {
			throw new RuntimeException("Error checking authorization: "
					+ ex.getMessage(), ex);
		}

		return isAuthorized;
	}

	protected GrouperI getGridGrouper(String url) {
		GrouperI grouper = null;
		grouper = this.gridGrouperMap.get(url);
		if (grouper == null) {
			grouper = getGridGrouperClientFactory().getGridGrouperClient(url);
			this.gridGrouperMap.put(url, grouper);
		}
		return grouper;
	}

	// ========= Accessors/Mutators ===========//
	public AuthorizationManager getAuthorizationManager() {
		return authorizationManager;
	}

	public void setAuthorizationManager(
			AuthorizationManager authorizationManager) {
		this.authorizationManager = authorizationManager;
	}

	// ========= DELEGATED METHODS ============//

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws CSTransactionException
	 * @see gov.nih.nci.security.AuthorizationManager#assignProtectionElement(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public void assignProtectionElement(String arg0, String arg1, String arg2)
			throws CSTransactionException {
		authorizationManager.assignProtectionElement(arg0, arg1, arg2);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws CSTransactionException
	 * @see gov.nih.nci.security.AuthorizationManager#assignProtectionElement(java.lang.String,
	 *      java.lang.String)
	 */
	public void assignProtectionElement(String arg0, String arg1)
			throws CSTransactionException {
		authorizationManager.assignProtectionElement(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @see gov.nih.nci.security.AuthorizationManager#checkOwnership(java.lang.String,
	 *      java.lang.String)
	 */
	public boolean checkOwnership(String arg0, String arg1) {
		return authorizationManager.checkOwnership(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws CSException
	 * @see gov.nih.nci.security.AuthorizationManager#checkPermission(gov.nih.nci.security.authorization.jaas.AccessPermission,
	 *      java.lang.String)
	 */
	public boolean checkPermission(AccessPermission accessPermission,
			String userName) throws CSException {
		return isAuthorized(userName, accessPermission.getName(),
				accessPermission.getActions());
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws CSException
	 * @see gov.nih.nci.security.AuthorizationManager#checkPermission(gov.nih.nci.security.authorization.jaas.AccessPermission,
	 *      javax.security.auth.Subject)
	 */
	public boolean checkPermission(AccessPermission accessPermission,
			Subject subject) throws CSException {
		boolean authorized = false;
		Set<Principal> principals = subject.getPrincipals();
		for (Principal principal : principals) {
			if (isAuthorized(principal.getName(), accessPermission.getName(),
					accessPermission.getActions())) {
				authorized = true;
				break;
			}
		}
		return authorized;
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 * @return
	 * @throws CSException
	 * @see gov.nih.nci.security.AuthorizationManager#checkPermission(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean checkPermission(String arg0, String arg1, String arg2,
			String arg3) throws CSException {
		return isAuthorized(arg0, arg1, arg2, arg3);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @return
	 * @throws CSException
	 * @see gov.nih.nci.security.AuthorizationManager#checkPermission(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public boolean checkPermission(String arg0, String arg1, String arg2)
			throws CSException {
		return isAuthorized(arg0, arg1, arg2);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 * @return
	 * @throws CSException
	 * @see gov.nih.nci.security.AuthorizationManager#checkPermissionForGroup(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean checkPermissionForGroup(String arg0, String arg1,
			String arg2, String arg3) throws CSException {
		return authorizationManager.checkPermissionForGroup(arg0, arg1, arg2,
				arg3);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @return
	 * @throws CSException
	 * @see gov.nih.nci.security.AuthorizationManager#checkPermissionForGroup(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public boolean checkPermissionForGroup(String arg0, String arg1, String arg2)
			throws CSException {
		return authorizationManager.checkPermissionForGroup(arg0, arg1, arg2);
	}

	/**
	 * @param arg0
	 * @throws CSTransactionException
	 * @see gov.nih.nci.security.AuthorizationManager#createProtectionElement(gov.nih.nci.security.authorization.domainobjects.ProtectionElement)
	 */
	public void createProtectionElement(ProtectionElement arg0)
			throws CSTransactionException {
		authorizationManager.createProtectionElement(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws CSTransactionException
	 * @see gov.nih.nci.security.AuthorizationManager#deAssignProtectionElements(java.lang.String,
	 *      java.lang.String)
	 */
	public void deAssignProtectionElements(String arg0, String arg1)
			throws CSTransactionException {
		authorizationManager.deAssignProtectionElements(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @return
	 * @throws CSException
	 * @see gov.nih.nci.security.AuthorizationManager#getAccessibleGroups(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public List getAccessibleGroups(String arg0, String arg1, String arg2)
			throws CSException {
		return authorizationManager.getAccessibleGroups(arg0, arg1, arg2);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws CSException
	 * @see gov.nih.nci.security.AuthorizationManager#getAccessibleGroups(java.lang.String,
	 *      java.lang.String)
	 */
	public List getAccessibleGroups(String arg0, String arg1)
			throws CSException {
		return authorizationManager.getAccessibleGroups(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @return
	 * @throws CSObjectNotFoundException
	 * @see gov.nih.nci.security.AuthorizationManager#getApplication(java.lang.String)
	 */
	public Application getApplication(String arg0)
			throws CSObjectNotFoundException {
		return authorizationManager.getApplication(arg0);
	}

	/**
	 * @return
	 * @see gov.nih.nci.security.AuthorizationManager#getApplicationContext()
	 */
	public ApplicationContext getApplicationContext() {
		return authorizationManager.getApplicationContext();
	}

	/**
	 * @param arg0
	 * @return
	 * @see gov.nih.nci.security.AuthorizationManager#getPrincipals(java.lang.String)
	 */
	public Principal[] getPrincipals(String arg0) {
		return authorizationManager.getPrincipals(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws CSException
	 * @see gov.nih.nci.security.AuthorizationManager#getPrivilegeMap(java.lang.String,
	 *      java.util.Collection)
	 */
	public Collection getPrivilegeMap(String arg0, Collection arg1)
			throws CSException {
		return authorizationManager.getPrivilegeMap(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @see gov.nih.nci.security.AuthorizationManager#getProtectionElement(java.lang.String,
	 *      java.lang.String)
	 */
	public ProtectionElement getProtectionElement(String arg0, String arg1) {
		return authorizationManager.getProtectionElement(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @return
	 * @throws CSObjectNotFoundException
	 * @see gov.nih.nci.security.AuthorizationManager#getProtectionElement(java.lang.String)
	 */
	public ProtectionElement getProtectionElement(String arg0)
			throws CSObjectNotFoundException {
		return authorizationManager.getProtectionElement(arg0);
	}

	/**
	 * @param arg0
	 * @return
	 * @throws CSObjectNotFoundException
	 * @see gov.nih.nci.security.AuthorizationManager#getProtectionElementById(java.lang.String)
	 */
	public ProtectionElement getProtectionElementById(String arg0)
			throws CSObjectNotFoundException {
		return authorizationManager.getProtectionElementById(arg0);
	}

	/**
	 * @return
	 * @see gov.nih.nci.security.AuthorizationManager#getProtectionGroups()
	 */
	public List getProtectionGroups() {
		return authorizationManager.getProtectionGroups();
	}

	/**
	 * @param arg0
	 * @return
	 * @throws CSObjectNotFoundException
	 * @see gov.nih.nci.security.AuthorizationManager#getProtectionGroups(java.lang.String)
	 */
	public Set getProtectionGroups(String arg0)
			throws CSObjectNotFoundException {
		return authorizationManager.getProtectionGroups(arg0);
	}

	/**
	 * @param arg0
	 * @return
	 * @see gov.nih.nci.security.AuthorizationManager#getUser(java.lang.String)
	 */
	public User getUser(String arg0) {
		return authorizationManager.getUser(arg0);
	}

	/**
	 * @param cxtName
	 * @see gov.nih.nci.security.AuthorizationManager#initialize(java.lang.String)
	 */
	public void initialize(String ctxName) {
		if (this.authorizationManager == null) {
			try {
				this.authorizationManager = new UserProvisioningManagerImpl(
						ctxName);
			} catch (CSConfigurationException ex) {
				throw new RuntimeException(
						"Error instantiating UserProvisioningManagerImpl: "
								+ ex.getMessage(), ex);
			}
		}
		this.authorizationManager.initialize(ctxName);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws CSTransactionException
	 * @see gov.nih.nci.security.AuthorizationManager#removeOwnerForProtectionElement(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public void removeOwnerForProtectionElement(String arg0, String arg1,
			String arg2) throws CSTransactionException {
		authorizationManager.removeOwnerForProtectionElement(arg0, arg1, arg2);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws CSTransactionException
	 * @see gov.nih.nci.security.AuthorizationManager#removeOwnerForProtectionElement(java.lang.String,
	 *      java.lang.String[])
	 */
	public void removeOwnerForProtectionElement(String arg0, String[] arg1)
			throws CSTransactionException {
		authorizationManager.removeOwnerForProtectionElement(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws CSException
	 * @see gov.nih.nci.security.AuthorizationManager#secureCollection(java.lang.String,
	 *      java.util.Collection)
	 */
	public Collection secureCollection(String arg0, Collection arg1)
			throws CSException {
		return authorizationManager.secureCollection(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws CSException
	 * @see gov.nih.nci.security.AuthorizationManager#secureObject(java.lang.String,
	 *      java.lang.Object)
	 */
	public Object secureObject(String arg0, Object arg1) throws CSException {
		return authorizationManager.secureObject(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @return
	 * @throws CSException
	 * @see gov.nih.nci.security.AuthorizationManager#secureUpdate(java.lang.String,
	 *      java.lang.Object, java.lang.Object)
	 */
	public Object secureUpdate(String arg0, Object arg1, Object arg2)
			throws CSException {
		return authorizationManager.secureUpdate(arg0, arg1, arg2);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @see gov.nih.nci.security.AuthorizationManager#setAuditUserInfo(java.lang.String,
	 *      java.lang.String)
	 */
	public void setAuditUserInfo(String arg0, String arg1) {
		authorizationManager.setAuditUserInfo(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @see gov.nih.nci.security.AuthorizationManager#setEncryptionEnabled(boolean)
	 */
	public void setEncryptionEnabled(boolean arg0) {
		authorizationManager.setEncryptionEnabled(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws CSTransactionException
	 * @see gov.nih.nci.security.AuthorizationManager#setOwnerForProtectionElement(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public void setOwnerForProtectionElement(String arg0, String arg1,
			String arg2) throws CSTransactionException {
		authorizationManager.setOwnerForProtectionElement(arg0, arg1, arg2);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws CSTransactionException
	 * @see gov.nih.nci.security.AuthorizationManager#setOwnerForProtectionElement(java.lang.String,
	 *      java.lang.String[])
	 */
	public void setOwnerForProtectionElement(String arg0, String[] arg1)
			throws CSTransactionException {
		authorizationManager.setOwnerForProtectionElement(arg0, arg1);
	}

	private class GridGrouperThread extends Thread {
		boolean finished;

		Exception ex;

		boolean isMember;

		GrouperI grouper;

		String identity;

		String groupName;

		GridGrouperThread(GrouperI grouper, String identity, String groupName) {
			this.grouper = grouper;
			this.identity = identity;
			this.groupName = groupName;
		}

		public void run() {
			try {
				this.isMember = this.grouper.isMemberOf(this.identity,
						this.groupName);
				this.finished = true;
			} catch (Exception ex) {
				this.ex = ex;
			}
		}
	}

	/**
	 * @return the gridGrouperThreadTimeout
	 */
	public long getGridGrouperThreadTimeout() {
		return gridGrouperThreadTimeout;
	}

	/**
	 * @param gridGrouperThreadTimeout
	 *            the gridGrouperThreadTimeout to set
	 */
	public void setGridGrouperThreadTimeout(long gridGrouperThreadTimeout) {
		this.gridGrouperThreadTimeout = gridGrouperThreadTimeout;
	}

	/**
	 * @return the gridGrouperClientFactory
	 */
	public GridGrouperClientFactory getGridGrouperClientFactory() {
		return gridGrouperClientFactory;
	}

	/**
	 * @param gridGrouperClientFactory
	 *            the gridGrouperClientFactory to set
	 */
	public void setGridGrouperClientFactory(
			GridGrouperClientFactory gridGrouperClientFactory) {
		this.gridGrouperClientFactory = gridGrouperClientFactory;
	}

}
