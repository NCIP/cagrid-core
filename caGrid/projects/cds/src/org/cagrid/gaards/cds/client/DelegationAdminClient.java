package org.cagrid.gaards.cds.client;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.cagrid.gaards.cds.common.DelegationIdentifier;
import org.cagrid.gaards.cds.common.DelegationRecord;
import org.cagrid.gaards.cds.common.DelegationRecordFilter;
import org.cagrid.gaards.cds.common.DelegationStatus;
import org.cagrid.gaards.cds.stubs.types.CDSInternalFault;
import org.cagrid.gaards.cds.stubs.types.DelegationFault;
import org.cagrid.gaards.cds.stubs.types.PermissionDeniedFault;
import org.globus.gsi.GlobusCredential;
import org.globus.wsrf.impl.security.authorization.Authorization;


/**
 * @author langella
 */
public class DelegationAdminClient {
    private CredentialDelegationServiceClient client;


    public DelegationAdminClient(String url) throws Exception {
        this(url, null);
    }


    public DelegationAdminClient(String url, GlobusCredential cred) throws Exception {
        this.client = new CredentialDelegationServiceClient(url, cred);
    }


    /**
     * This method specifies an authorization policy that the client should use
     * for authorizing the server that it connects to.
     * 
     * @param authorization
     *            The authorization policy to enforce
     */

    public void setAuthorization(Authorization authorization) {
        client.setAuthorization(authorization);
    }


    /**
     * This method allows a admin to find credentials that have been delegated.
     * 
     * @return A list of records each representing a credential delegated.
     * @throws RemoteException
     * @throws CDSInternalFault
     * @throws DelegationInternalFault
     * @throws PermissionDeniedFault
     */

    public List<DelegationRecord> findDelegatedCredentials() throws RemoteException, CDSInternalFault, DelegationFault,
        PermissionDeniedFault {
        return findDelegatedCredentials(new DelegationRecordFilter());
    }


    /**
     * This method allows a admin to find credentials that have been delegated.
     * 
     * @param filter
     *            Search criteria to use in finding delegated credentials
     * @return A list of records each representing a credential delegated.
     * @throws RemoteException
     * @throws CDSInternalFault
     * @throws DelegationInternalFault
     * @throws PermissionDeniedFault
     */

    public List<DelegationRecord> findDelegatedCredentials(DelegationRecordFilter filter) throws RemoteException,
        CDSInternalFault, DelegationFault, PermissionDeniedFault {
        if (filter == null) {
            filter = new DelegationRecordFilter();
        }

        DelegationRecord[] records = client.findDelegatedCredentials(filter);
        if (records == null) {
            return new ArrayList<DelegationRecord>();
        } else {
            List<DelegationRecord> list = Arrays.asList(records);
            return list;
        }
    }


    /**
     * This method allows and admin to update the status of a delegated
     * credential.
     * 
     * @param id
     *            The delegation identifier of the delegated credentials to
     *            suspend.
     * @param status
     *            The updated delegation status.
     * @throws RemoteException
     * @throws CDSInternalFault
     * @throws DelegationFault
     * @throws PermissionDeniedFault
     */

    public void updateDelegationStatus(DelegationIdentifier id, DelegationStatus status) throws RemoteException,
        CDSInternalFault, DelegationFault, PermissionDeniedFault {
        client.updateDelegatedCredentialStatus(id, status);
    }


    /**
     * This method allow and administrator to delete a delegated credential.
     * 
     * @param id
     *            The Id of the delegated credential to delete.
     * @throws RemoteException
     * @throws CDSInternalFault
     * @throws PermissionDeniedFault
     */

    public void deleteDelegatedCredential(DelegationIdentifier id) throws RemoteException, CDSInternalFault,
        PermissionDeniedFault {
        client.deleteDelegatedCredential(id);
    }


    /**
     * This method allows a CDS administrator to add another user as a CDS
     * administrator.
     * 
     * @param gridIdentity
     *            The grid identity of the user to add as a CDS administrator.
     * @throws RemoteException
     * @throws CDSInternalFault
     * @throws PermissionDeniedFault
     */

    public void addAdmin(String gridIdentity) throws RemoteException, CDSInternalFault, PermissionDeniedFault {
        client.addAdmin(gridIdentity);
    }


    /**
     * This method allows a CDS administrator to remove the user from the CDS
     * administrators group.
     * 
     * @param gridIdentity
     *            The Grid Identity of the user to remove from the CDS
     *            administrators group.
     * @throws RemoteException
     * @throws CDSInternalFault
     * @throws PermissionDeniedFault
     */

    public void removeAdmin(String gridIdentity) throws RemoteException, CDSInternalFault, PermissionDeniedFault {
        client.removeAdmin(gridIdentity);
    }


    /**
     * This method allows a CDS administrator to get a list of CDS
     * administrators.
     * 
     * @return The list of CDS administrators.
     * @throws RemoteException
     * @throws CDSInternalFault
     * @throws PermissionDeniedFault
     */

    public List<String> getAdmins() throws RemoteException, CDSInternalFault, PermissionDeniedFault {
        List<String> admins = new ArrayList<String>();
        String[] list = client.getAdmins();
        if (list != null) {
            for (int i = 0; i < list.length; i++) {
                admins.add(list[i]);
            }
        }
        return admins;
    }

}
