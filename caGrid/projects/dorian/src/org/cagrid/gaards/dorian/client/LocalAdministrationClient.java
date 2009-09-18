package org.cagrid.gaards.dorian.client;

import gov.nih.nci.cagrid.common.FaultHelper;
import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.common.Utils;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.gaards.dorian.common.DorianFault;
import org.cagrid.gaards.dorian.idp.IdentityProviderAuditFilter;
import org.cagrid.gaards.dorian.idp.IdentityProviderAuditRecord;
import org.cagrid.gaards.dorian.idp.LocalUser;
import org.cagrid.gaards.dorian.idp.LocalUserFilter;
import org.cagrid.gaards.dorian.stubs.types.DorianInternalFault;
import org.cagrid.gaards.dorian.stubs.types.InvalidUserPropertyFault;
import org.cagrid.gaards.dorian.stubs.types.NoSuchUserFault;
import org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault;
import org.globus.gsi.GlobusCredential;
import org.globus.wsrf.impl.security.authorization.Authorization;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class LocalAdministrationClient extends DorianBaseClient {

    public LocalAdministrationClient(String serviceURI) throws MalformedURIException, RemoteException {
        super(serviceURI);
    }


    public LocalAdministrationClient(String serviceURI, GlobusCredential credential) throws MalformedURIException,
        RemoteException {
        super(serviceURI, credential);
    }


    /**
     * This method specifies an authorization policy that the client should use
     * for authorizing the server that it connects to.
     * 
     * @param authorization
     *            The authorization policy to enforce
     */

    public void setAuthorization(Authorization authorization) {
        getClient().setAuthorization(authorization);
    }


    /**
     * This method allows a client to determine whether or not a user id is
     * already registered with the Dorian Identity Provider.
     * 
     * @param userId
     *            The user id to determine whether or not is registered.
     * @return True is returned a user with the user id is registered with the
     *         Dorian Identity Provider, otherwise False is returned.
     * @throws DorianFault
     * @throws DorianInternalFault
     */
    public boolean doesUserExist(String userId) throws DorianFault, DorianInternalFault {
        try {
            return getClient().doesLocalUserExist(userId);
        } catch (DorianInternalFault f) {
            throw f;
        } catch (Exception e) {
            FaultUtil.printFault(e);
            DorianFault fault = new DorianFault();
            fault.setFaultString(Utils.getExceptionMessage(e));
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianFault) helper.getFault();
            throw fault;
        }
    }


    /**
     * This methods returns the list of users registered with the Dorian
     * Identity Provider meeting the specified search criteria.
     * 
     * @param filter
     *            The search criteria specifying the users to find.
     * @return The list of users found meeting the search criteria.
     * @throws DorianFault
     * @throws DorianInternalFault
     * @throws PermissionDeniedFault
     */
    public List<LocalUser> findUsers(LocalUserFilter filter) throws DorianFault, DorianInternalFault,
        PermissionDeniedFault {
        try {
            List<LocalUser> list = Utils.asList(getClient().findLocalUsers(filter));
            return list;
        } catch (DorianInternalFault gie) {
            throw gie;
        } catch (PermissionDeniedFault f) {
            throw f;
        } catch (Exception e) {
            FaultUtil.printFault(e);
            DorianFault fault = new DorianFault();
            fault.setFaultString(Utils.getExceptionMessage(e));
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianFault) helper.getFault();
            throw fault;
        }
    }


    /**
     * This method deletes a user account in the Dorian Identity Provider.
     * 
     * @param userId
     *            The user id of the account to be deleted.
     * @throws DorianFault
     * @throws DorianInternalFault
     * @throws PermissionDeniedFault
     */
    public void removeUser(String userId) throws DorianFault, DorianInternalFault, PermissionDeniedFault {
        try {
            getClient().removeLocalUser(userId);
        } catch (DorianInternalFault gie) {
            throw gie;
        } catch (PermissionDeniedFault f) {
            throw f;
        } catch (Exception e) {
            FaultUtil.printFault(e);
            DorianFault fault = new DorianFault();
            fault.setFaultString(Utils.getExceptionMessage(e));
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianFault) helper.getFault();
            throw fault;
        }

    }


    /**
     * This method allows a client to make update(s) to a Dorian Identity
     * Provider user account.
     * 
     * @param u
     *            The updated user.
     * @throws DorianFault
     * @throws DorianInternalFault
     * @throws PermissionDeniedFault
     * @throws NoSuchUserFault
     * @throws InvalidUserPropertyFault
     */
    public void updateUser(LocalUser u) throws DorianFault, DorianInternalFault, PermissionDeniedFault,
        NoSuchUserFault, InvalidUserPropertyFault {

        try {
            getClient().updateLocalUser(u);
        } catch (DorianInternalFault gie) {
            throw gie;
        } catch (PermissionDeniedFault f) {
            throw f;
        } catch (NoSuchUserFault f) {
            throw f;
        } catch (InvalidUserPropertyFault f) {
            throw f;
        } catch (Exception e) {
            FaultUtil.printFault(e);
            DorianFault fault = new DorianFault();
            fault.setFaultString(Utils.getExceptionMessage(e));
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianFault) helper.getFault();
            throw fault;
        }

    }


    /**
     * This method allows an administrator to perform an audit on the Dorian
     * Identity Provider.
     * 
     * @param f
     *            The audit search criteria
     * @return The list of audit records that meet the search criteria
     *         specified.
     * @throws DorianFault
     * @throws DorianInternalFault
     * @throws PermissionDeniedFault
     */

    public List<IdentityProviderAuditRecord> performAudit(IdentityProviderAuditFilter f) throws DorianFault,
        DorianInternalFault, PermissionDeniedFault {
        try {
            List<IdentityProviderAuditRecord> list = Utils.asList(getClient().performIdentityProviderAudit(f));
            return list;
        } catch (DorianInternalFault gie) {
            throw gie;
        } catch (PermissionDeniedFault fault) {
            throw fault;
        } catch (Exception e) {
            FaultUtil.printFault(e);
            DorianFault fault = new DorianFault();
            fault.setFaultString(Utils.getExceptionMessage(e));
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianFault) helper.getFault();
            throw fault;
        }

    }
}
