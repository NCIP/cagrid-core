package org.cagrid.gaards.dorian.client;

import gov.nih.nci.cagrid.common.FaultHelper;
import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.common.Utils;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.security.cert.X509Certificate;
import java.util.List;

import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.gaards.dorian.common.DorianFault;
import org.cagrid.gaards.dorian.federation.FederationAuditFilter;
import org.cagrid.gaards.dorian.federation.FederationAuditRecord;
import org.cagrid.gaards.dorian.federation.GridUser;
import org.cagrid.gaards.dorian.federation.GridUserFilter;
import org.cagrid.gaards.dorian.federation.GridUserPolicy;
import org.cagrid.gaards.dorian.federation.HostCertificateFilter;
import org.cagrid.gaards.dorian.federation.HostCertificateRecord;
import org.cagrid.gaards.dorian.federation.HostCertificateUpdate;
import org.cagrid.gaards.dorian.federation.TrustedIdP;
import org.cagrid.gaards.dorian.federation.UserCertificateFilter;
import org.cagrid.gaards.dorian.federation.UserCertificateRecord;
import org.cagrid.gaards.dorian.federation.UserCertificateStatus;
import org.cagrid.gaards.dorian.federation.UserCertificateUpdate;
import org.cagrid.gaards.dorian.stubs.types.DorianInternalFault;
import org.cagrid.gaards.dorian.stubs.types.InvalidHostCertificateFault;
import org.cagrid.gaards.dorian.stubs.types.InvalidTrustedIdPFault;
import org.cagrid.gaards.dorian.stubs.types.InvalidUserCertificateFault;
import org.cagrid.gaards.dorian.stubs.types.InvalidUserFault;
import org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault;
import org.cagrid.gaards.pki.CertUtil;
import org.globus.gsi.GlobusCredential;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class GridAdministrationClient extends DorianBaseClient {

    public GridAdministrationClient(String serviceURI) throws MalformedURIException, RemoteException {
        super(serviceURI);
    }


    public GridAdministrationClient(String serviceURI, GlobusCredential credential) throws MalformedURIException,
        RemoteException {
        super(serviceURI, credential);
    }


    /**
     * This method adds an identity provider to Dorian as a trusted identity
     * provider.
     * 
     * @param idp
     *            The identity provider to add as a trusted identity provider.
     * @return The identity provider that was added to Dorian as a trusted
     *         identity provider.
     * @throws DorianFault
     * @throws PermissionDeniedFault
     * @throws InvalidTrustedIdPFault
     * @throws DorianInternalFault
     */
    public TrustedIdP addTrustedIdP(TrustedIdP idp) throws DorianFault, PermissionDeniedFault, InvalidTrustedIdPFault,
        DorianInternalFault {
        try {
            return getClient().addTrustedIdP(idp);
        } catch (DorianInternalFault gie) {
            throw gie;
        } catch (PermissionDeniedFault f) {
            throw f;
        } catch (InvalidTrustedIdPFault f) {
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
     * This method removes an identity provider from Dorian, this identity
     * provider is no longer a trusted identity provider. All accounts from this
     * identity provider will be removed.
     * 
     * @param idp
     *            The identity provider to remove.
     * @throws DorianFault
     * @throws PermissionDeniedFault
     * @throws InvalidTrustedIdPFault
     * @throws DorianInternalFault
     */
    public void removeTrustedIdP(TrustedIdP idp) throws DorianFault, PermissionDeniedFault, InvalidTrustedIdPFault,
        DorianInternalFault {
        try {
            getClient().removeTrustedIdP(idp);
        } catch (DorianInternalFault gie) {
            throw gie;
        } catch (PermissionDeniedFault f) {
            throw f;
        } catch (InvalidTrustedIdPFault f) {
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
     * This method allows a client to update a trusted identity provider.
     * 
     * @param idp
     *            The update trusted identity provider.
     * @throws DorianFault
     * @throws PermissionDeniedFault
     * @throws InvalidTrustedIdPFault
     * @throws DorianInternalFault
     */
    public void updateTrustedIdP(TrustedIdP idp) throws DorianFault, PermissionDeniedFault, InvalidTrustedIdPFault,
        DorianInternalFault {
        try {
            getClient().updateTrustedIdP(idp);
        } catch (DorianInternalFault gie) {
            throw gie;
        } catch (PermissionDeniedFault f) {
            throw f;
        } catch (InvalidTrustedIdPFault f) {
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
     * This method returns the list of IdP user policies supported by Dorian.
     * 
     * @return The list of IdP user policies supported by Dorian.
     * @throws DorianFault
     * @throws PermissionDeniedFault
     * @throws DorianInternalFault
     */
    public List<GridUserPolicy> getUserPolicies() throws DorianFault, PermissionDeniedFault, DorianInternalFault {
        try {
            List<GridUserPolicy> list = Utils.asList(getClient().getGridUserPolicies());
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
     * This method returns the list of identity providers trusted by Dorian.
     * 
     * @return The list of identity providers trusted by Dorian.
     * @throws DorianFault
     * @throws PermissionDeniedFault
     * @throws InvalidUserFault
     * @throws DorianInternalFault
     */
    public List<TrustedIdP> getTrustedIdPs() throws DorianFault, PermissionDeniedFault, InvalidUserFault,
        DorianInternalFault {

        try {
            List<TrustedIdP> list = Utils.asList(getClient().getTrustedIdPs());
            return list;
        } catch (DorianInternalFault gie) {
            throw gie;
        } catch (PermissionDeniedFault f) {
            throw f;
        } catch (InvalidUserFault f) {
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
     * This method returns a list of Grid users with accounts on Dorian that
     * meet a specified search criteria.
     * 
     * @param filter
     *            The search criteria
     * @return The list of users that meet the search criteria.
     * @throws DorianFault
     * @throws PermissionDeniedFault
     * @throws DorianInternalFault
     */
    public List<GridUser> findUsers(GridUserFilter filter) throws DorianFault, PermissionDeniedFault,
        DorianInternalFault {

        try {
            List<GridUser> list = Utils.asList(getClient().findGridUsers(filter));
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
     * This method removes a grid user account from Dorian.
     * 
     * @param usr
     *            The grid user account to remove.
     * @throws DorianFault
     * @throws PermissionDeniedFault
     * @throws InvalidUserFault
     * @throws DorianInternalFault
     */
    public void removeUser(GridUser usr) throws DorianFault, PermissionDeniedFault, InvalidUserFault,
        DorianInternalFault {

        try {
            getClient().removeGridUser(usr);
        } catch (DorianInternalFault gie) {
            throw gie;
        } catch (PermissionDeniedFault f) {
            throw f;
        } catch (InvalidUserFault f) {
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
     * This method allows a client to update a grid user account.
     * 
     * @param usr
     *            The update grid user.
     * @throws DorianFault
     * @throws PermissionDeniedFault
     * @throws InvalidUserFault
     * @throws DorianInternalFault
     */
    public void updateUser(GridUser usr) throws DorianFault, PermissionDeniedFault, InvalidUserFault,
        DorianInternalFault {
        try {
            getClient().updateGridUser(usr);
        } catch (DorianInternalFault gie) {
            throw gie;
        } catch (PermissionDeniedFault f) {
            throw f;
        } catch (InvalidUserFault f) {
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
     * This method grants a user privileges to Dorian to administrate grid
     * accounts.
     * 
     * @param gridIdentity
     *            The Grid identity of the user to add as an administrator.
     * @throws DorianFault
     * @throws DorianInternalFault
     * @throws PermissionDeniedFault
     */
    public void addAdmin(java.lang.String gridIdentity) throws DorianFault, DorianInternalFault, PermissionDeniedFault {
        try {
            getClient().addAdmin(gridIdentity);
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
     * This method revokes a user's privilege to administrate grid accounts.
     * 
     * @param gridIdentity
     *            The Grid identity of the user to revoke privileges.
     * @throws DorianFault
     * @throws DorianInternalFault
     * @throws PermissionDeniedFault
     */
    public void removeAdmin(java.lang.String gridIdentity) throws DorianFault, DorianInternalFault,
        PermissionDeniedFault {
        try {
            getClient().removeAdmin(gridIdentity);
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
     * This method obtains a list of all the users with privileges to
     * administrate Grid accounts.
     * 
     * @return A list containing the grid identities of users whom have the
     *         privilege to administrate Grid accounts.
     * @throws DorianFault
     * @throws DorianInternalFault
     * @throws PermissionDeniedFault
     */

    public List<String> getAdmins() throws DorianFault, DorianInternalFault, PermissionDeniedFault {
        try {
            List<String> list = Utils.asList(getClient().getAdmins());
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
     * Returns the list of host certificates meeting the specified search
     * criteria.
     * 
     * @param filter
     *            The search criteria.
     * @return The list of host certificates meeting the specified search
     *         criteria.
     * @throws DorianFault
     * @throws DorianInternalFault
     * @throws PermissionDeniedFault
     */
    public List<HostCertificateRecord> findHostCertificates(HostCertificateFilter filter) throws DorianFault,
        DorianInternalFault, PermissionDeniedFault {
        try {
            List<HostCertificateRecord> list = Utils.asList(getClient().findHostCertificates(filter));
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
     * This method allows a client to approve a host ceritifcate request.
     * 
     * @param recordId
     *            The id of the host certificate.
     * @return The approved host certificate record.
     * @throws DorianFault
     * @throws DorianInternalFault
     * @throws InvalidHostCertificateFault
     * @throws PermissionDeniedFault
     */
    public HostCertificateRecord approveHostCertificate(long recordId) throws DorianFault, DorianInternalFault,
        InvalidHostCertificateFault, PermissionDeniedFault {
        try {
            return getClient().approveHostCertificate(BigInteger.valueOf(recordId));
        } catch (DorianInternalFault gie) {
            throw gie;
        } catch (InvalidHostCertificateFault gie) {
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
     * This method allow a client to update a host certificate record.
     * 
     * @param update
     *            The updated host certificate record.
     * @throws DorianFault
     * @throws DorianInternalFault
     * @throws InvalidHostCertificateFault
     * @throws PermissionDeniedFault
     */

    public void updateHostCertificateRecord(HostCertificateUpdate update) throws DorianFault, DorianInternalFault,
        InvalidHostCertificateFault, PermissionDeniedFault {
        try {
            getClient().updateHostCertificateRecord(update);
        } catch (DorianInternalFault gie) {
            throw gie;
        } catch (InvalidHostCertificateFault gie) {
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
     * This method allow a client to renew a host certificate.
     * 
     * @param recordId
     *            The record id of the host certificate to renew.
     * @return The renewed host certificate record.
     * @throws DorianFault
     * @throws DorianInternalFault
     * @throws InvalidHostCertificateFault
     * @throws PermissionDeniedFault
     */
    public HostCertificateRecord renewHostCertificate(long recordId) throws DorianFault, DorianInternalFault,
        InvalidHostCertificateFault, PermissionDeniedFault {
        try {
            return getClient().renewHostCertificate(BigInteger.valueOf(recordId));
        } catch (DorianInternalFault gie) {
            throw gie;
        } catch (InvalidHostCertificateFault gie) {
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
     * This method obtains Dorian's CA certificate.
     * 
     * @return This method obtains Dorian's CA certificate.
     * @throws DorianFault
     * @throws DorianInternalFault
     */

    public X509Certificate getCACertificate() throws DorianFault, DorianInternalFault {
        try {
            return CertUtil.loadCertificate(getClient().getCACertificate().getCertificateAsString());
        } catch (DorianInternalFault gie) {
            throw gie;
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
     * This method allows a Dorian administrator to search for user certificates
     * issued by the Dorian CA.
     * 
     * @param f
     *            The search criteria.
     * @return The user certificates issued by the Dorian CA, that meet the
     *         search criteria.
     * @throws DorianFault
     * @throws DorianInternalFault
     * @throws InvalidUserCertificateFault
     * @throws PermissionDeniedFault
     */

    public List<UserCertificateRecord> findUserCertificateRecords(UserCertificateFilter f) throws DorianFault,
        DorianInternalFault, InvalidUserCertificateFault, PermissionDeniedFault {
        try {
            List<UserCertificateRecord> list = Utils.asList(getClient().findUserCertificates(f));
            return list;
        } catch (DorianInternalFault gie) {
            throw gie;
        } catch (InvalidUserCertificateFault fault) {
            throw fault;
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


    /**
     * This method allows and administrator to update the status or the notes
     * associated with a user certificate.
     * 
     * @param serialNumber
     *            The serial number of the user certificate to update.
     * @param status
     *            The new status of the user certificate or null if the status
     *            should remain the same.
     * @param notes
     *            The updated notes associated with the user certificate or null
     *            if the status should remain the same.
     * @throws DorianFault
     * @throws DorianInternalFault
     * @throws InvalidUserCertificateFault
     * @throws PermissionDeniedFault
     */

    public void updateUserCertificateRecord(long serialNumber, UserCertificateStatus status, String notes)
        throws DorianFault, DorianInternalFault, InvalidUserCertificateFault, PermissionDeniedFault {
        try {
            UserCertificateUpdate update = new UserCertificateUpdate();
            update.setSerialNumber(serialNumber);
            update.setStatus(status);
            update.setNotes(notes);
            getClient().updateUserCertificate(update);
        } catch (DorianInternalFault gie) {
            throw gie;
        } catch (InvalidUserCertificateFault fault) {
            throw fault;
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


    /**
     * This method allows an administrator to update the status of a user
     * certificate.
     * 
     * @param serialNumber
     *            The serial number of the user certificate to update.
     * @param status
     *            The new status of the user certificate.
     * @throws DorianFault
     * @throws DorianInternalFault
     * @throws InvalidUserCertificateFault
     * @throws PermissionDeniedFault
     */

    public void updateUserCertificateRecordStatus(long serialNumber, UserCertificateStatus status) throws DorianFault,
        DorianInternalFault, InvalidUserCertificateFault, PermissionDeniedFault {
        this.updateUserCertificateRecord(serialNumber, status, null);
    }


    /**
     * This method allows and administrator to update the notes associated with
     * a user certificate.
     * 
     * @param serialNumber
     *            The serial number of the user certificate to update.
     * @param notes
     *            The updated notes.
     * @throws DorianFault
     * @throws DorianInternalFault
     * @throws InvalidUserCertificateFault
     * @throws PermissionDeniedFault
     */

    public void updateUserCertificateRecordNotes(long serialNumber, String notes) throws DorianFault,
        DorianInternalFault, InvalidUserCertificateFault, PermissionDeniedFault {
        this.updateUserCertificateRecord(serialNumber, null, notes);
    }


    /**
     * This method allows an administrator to remove a user certificate.
     * 
     * @param serialNumber
     *            The serial number of the user certificate to remove.
     * @throws RemoteException
     * @throws DorianInternalFault
     * @throws InvalidUserCertificateFault
     * @throws PermissionDeniedFault
     */

    public void removeUserCertificate(long serialNumber) throws RemoteException, DorianInternalFault,
        InvalidUserCertificateFault, PermissionDeniedFault {
        try {
            getClient().removeUserCertificate(String.valueOf(serialNumber));
        } catch (DorianInternalFault gie) {
            throw gie;
        } catch (InvalidUserCertificateFault fault) {
            throw fault;
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


    /**
     * This method allows an administrator to perform an audit on
     * Federation/Grid transactions with Dorian.
     * 
     * @param f
     *            The audit search criteria
     * @return The list of audit records that meet the search criteria
     *         specified.
     * @throws DorianFault
     * @throws DorianInternalFault
     * @throws PermissionDeniedFault
     */

    public List<FederationAuditRecord> performAudit(FederationAuditFilter f) throws DorianFault, DorianInternalFault,
        PermissionDeniedFault {
        try {
            List<FederationAuditRecord> list = Utils.asList(getClient().performFederationAudit(f));
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
