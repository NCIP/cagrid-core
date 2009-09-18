/**
 * 
 */
package gov.nih.nci.cagrid.authorization;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.security.auth.Subject;

import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.authorization.domainobjects.Application;
import gov.nih.nci.security.authorization.domainobjects.ApplicationContext;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElement;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.authorization.jaas.AccessPermission;
import gov.nih.nci.security.exceptions.CSException;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;
import gov.nih.nci.security.exceptions.CSTransactionException;

/**
 * @author <a href="mailto:joshua.phillips@semanticbits.com">Joshua Phillips</a>
 *
 */
public class MockAuthorizationManagerAdapter implements AuthorizationManager {

	/* (non-Javadoc)
	 * @see gov.nih.nci.security.AuthorizationManager#assignProtectionElement(java.lang.String, java.lang.String)
	 */
	public void assignProtectionElement(String arg0, String arg1)
			throws CSTransactionException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.security.AuthorizationManager#assignProtectionElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void assignProtectionElement(String arg0, String arg1, String arg2)
			throws CSTransactionException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.security.AuthorizationManager#checkOwnership(java.lang.String, java.lang.String)
	 */
	public boolean checkOwnership(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.security.AuthorizationManager#checkPermission(gov.nih.nci.security.authorization.jaas.AccessPermission, javax.security.auth.Subject)
	 */
	public boolean checkPermission(AccessPermission arg0, Subject arg1)
			throws CSException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.security.AuthorizationManager#checkPermission(gov.nih.nci.security.authorization.jaas.AccessPermission, java.lang.String)
	 */
	public boolean checkPermission(AccessPermission arg0, String arg1)
			throws CSException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.security.AuthorizationManager#checkPermission(java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean checkPermission(String arg0, String arg1, String arg2)
			throws CSException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.security.AuthorizationManager#checkPermission(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean checkPermission(String arg0, String arg1, String arg2,
			String arg3) throws CSException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.security.AuthorizationManager#checkPermissionForGroup(java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean checkPermissionForGroup(String arg0, String arg1, String arg2)
			throws CSException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.security.AuthorizationManager#checkPermissionForGroup(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean checkPermissionForGroup(String arg0, String arg1,
			String arg2, String arg3) throws CSException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.security.AuthorizationManager#createProtectionElement(gov.nih.nci.security.authorization.domainobjects.ProtectionElement)
	 */
	public void createProtectionElement(ProtectionElement arg0)
			throws CSTransactionException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.security.AuthorizationManager#deAssignProtectionElements(java.lang.String, java.lang.String)
	 */
	public void deAssignProtectionElements(String arg0, String arg1)
			throws CSTransactionException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.security.AuthorizationManager#getAccessibleGroups(java.lang.String, java.lang.String)
	 */
	public List getAccessibleGroups(String arg0, String arg1)
			throws CSException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.security.AuthorizationManager#getAccessibleGroups(java.lang.String, java.lang.String, java.lang.String)
	 */
	public List getAccessibleGroups(String arg0, String arg1, String arg2)
			throws CSException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.security.AuthorizationManager#getApplication(java.lang.String)
	 */
	public Application getApplication(String arg0)
			throws CSObjectNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.security.AuthorizationManager#getApplicationContext()
	 */
	public ApplicationContext getApplicationContext() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.security.AuthorizationManager#getPrincipals(java.lang.String)
	 */
	public Principal[] getPrincipals(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.security.AuthorizationManager#getPrivilegeMap(java.lang.String, java.util.Collection)
	 */
	public Collection getPrivilegeMap(String arg0, Collection arg1)
			throws CSException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.security.AuthorizationManager#getProtectionElement(java.lang.String)
	 */
	public ProtectionElement getProtectionElement(String arg0)
			throws CSObjectNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.security.AuthorizationManager#getProtectionElement(java.lang.String, java.lang.String)
	 */
	public ProtectionElement getProtectionElement(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.security.AuthorizationManager#getProtectionElementById(java.lang.String)
	 */
	public ProtectionElement getProtectionElementById(String arg0)
			throws CSObjectNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.security.AuthorizationManager#getProtectionGroups()
	 */
	public List getProtectionGroups() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.security.AuthorizationManager#getProtectionGroups(java.lang.String)
	 */
	public Set getProtectionGroups(String arg0)
			throws CSObjectNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.security.AuthorizationManager#getUser(java.lang.String)
	 */
	public User getUser(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.security.AuthorizationManager#initialize(java.lang.String)
	 */
	public void initialize(String arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.security.AuthorizationManager#removeOwnerForProtectionElement(java.lang.String, java.lang.String[])
	 */
	public void removeOwnerForProtectionElement(String arg0, String[] arg1)
			throws CSTransactionException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.security.AuthorizationManager#removeOwnerForProtectionElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void removeOwnerForProtectionElement(String arg0, String arg1,
			String arg2) throws CSTransactionException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.security.AuthorizationManager#secureCollection(java.lang.String, java.util.Collection)
	 */
	public Collection secureCollection(String arg0, Collection arg1)
			throws CSException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.security.AuthorizationManager#secureObject(java.lang.String, java.lang.Object)
	 */
	public Object secureObject(String arg0, Object arg1) throws CSException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.security.AuthorizationManager#secureUpdate(java.lang.String, java.lang.Object, java.lang.Object)
	 */
	public Object secureUpdate(String arg0, Object arg1, Object arg2)
			throws CSException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.security.AuthorizationManager#setAuditUserInfo(java.lang.String, java.lang.String)
	 */
	public void setAuditUserInfo(String arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.security.AuthorizationManager#setEncryptionEnabled(boolean)
	 */
	public void setEncryptionEnabled(boolean arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.security.AuthorizationManager#setOwnerForProtectionElement(java.lang.String, java.lang.String[])
	 */
	public void setOwnerForProtectionElement(String arg0, String[] arg1)
			throws CSTransactionException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.security.AuthorizationManager#setOwnerForProtectionElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void setOwnerForProtectionElement(String arg0, String arg1,
			String arg2) throws CSTransactionException {
		// TODO Auto-generated method stub

	}

}
