package gov.nih.nci.cagrid.gts.client;

import gov.nih.nci.cagrid.gts.bean.AuthorityGTS;
import gov.nih.nci.cagrid.gts.bean.AuthorityPriorityUpdate;
import gov.nih.nci.cagrid.gts.bean.Permission;
import gov.nih.nci.cagrid.gts.bean.PermissionFilter;
import gov.nih.nci.cagrid.gts.bean.TrustLevel;
import gov.nih.nci.cagrid.gts.bean.TrustedAuthority;
import gov.nih.nci.cagrid.gts.bean.X509CRL;
import gov.nih.nci.cagrid.gts.stubs.types.GTSInternalFault;
import gov.nih.nci.cagrid.gts.stubs.types.IllegalAuthorityFault;
import gov.nih.nci.cagrid.gts.stubs.types.IllegalPermissionFault;
import gov.nih.nci.cagrid.gts.stubs.types.IllegalTrustLevelFault;
import gov.nih.nci.cagrid.gts.stubs.types.IllegalTrustedAuthorityFault;
import gov.nih.nci.cagrid.gts.stubs.types.InvalidAuthorityFault;
import gov.nih.nci.cagrid.gts.stubs.types.InvalidPermissionFault;
import gov.nih.nci.cagrid.gts.stubs.types.InvalidTrustLevelFault;
import gov.nih.nci.cagrid.gts.stubs.types.InvalidTrustedAuthorityFault;
import gov.nih.nci.cagrid.gts.stubs.types.PermissionDeniedFault;

import java.rmi.RemoteException;

import org.apache.axis.types.URI.MalformedURIException;
import org.globus.gsi.GlobusCredential;
import org.globus.wsrf.impl.security.authorization.Authorization;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */

public class GTSAdminClient {

	private GTSClient client;


	public GTSAdminClient(String url, GlobusCredential proxy) throws MalformedURIException, RemoteException {
		this.client = new GTSClient(url, proxy);
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

	public TrustedAuthority addTrustedAuthority(TrustedAuthority ta) throws RemoteException, GTSInternalFault,
		IllegalTrustedAuthorityFault, PermissionDeniedFault {
		return client.addTrustedAuthority(ta);
	}


	public void updateTrustedAuthority(TrustedAuthority ta) throws RemoteException, GTSInternalFault,
		IllegalTrustedAuthorityFault, InvalidTrustedAuthorityFault, PermissionDeniedFault {
		client.updateTrustedAuthority(ta);
	}


	public void removeTrustedAuthority(java.lang.String trustedAuthorityName) throws RemoteException, GTSInternalFault,
		InvalidTrustedAuthorityFault, PermissionDeniedFault {
		client.removeTrustedAuthority(trustedAuthorityName);
	}


	public Permission[] findPermissions(PermissionFilter f) throws RemoteException, GTSInternalFault,
		PermissionDeniedFault {
		return client.findPermissions(f);
	}


	public void addPermission(Permission permission) throws RemoteException, GTSInternalFault, IllegalPermissionFault,
		PermissionDeniedFault {
		client.addPermission(permission);
	}


	public void revokePermission(Permission permission) throws RemoteException, GTSInternalFault,
		InvalidPermissionFault, PermissionDeniedFault {
		client.revokePermission(permission);
	}


	public void addTrustLevel(TrustLevel trustLevel) throws RemoteException, GTSInternalFault, IllegalTrustLevelFault,
		PermissionDeniedFault {
		client.addTrustLevel(trustLevel);
	}


	public void updateTrustLevel(TrustLevel trustLevel) throws RemoteException, GTSInternalFault,
		InvalidTrustLevelFault, PermissionDeniedFault {
		client.updateTrustLevel(trustLevel);
	}


	public void removeTrustLevel(String trustLevelName) throws RemoteException, GTSInternalFault,
		InvalidTrustLevelFault, IllegalTrustLevelFault, PermissionDeniedFault {
		client.removeTrustLevel(trustLevelName);
	}


	public void updateAuthorityPriorities(AuthorityPriorityUpdate update) throws RemoteException, GTSInternalFault,
		IllegalAuthorityFault, PermissionDeniedFault {
		client.updateAuthorityPriorities(update);
	}


	public void removeAuthority(String serviceURI) throws RemoteException, GTSInternalFault, InvalidAuthorityFault,
		PermissionDeniedFault {
		client.removeAuthority(serviceURI);
	}


	public void addAuthority(AuthorityGTS gts) throws RemoteException, GTSInternalFault, IllegalAuthorityFault,
		PermissionDeniedFault {
		client.addAuthority(gts);
	}


	public void updateAuthority(AuthorityGTS gts) throws RemoteException, GTSInternalFault, IllegalAuthorityFault,
		InvalidAuthorityFault, PermissionDeniedFault {
		client.updateAuthority(gts);
	}


	public void updateCRL(String trustedAuthorityName, X509CRL crl) throws RemoteException, GTSInternalFault,
		IllegalTrustedAuthorityFault, InvalidTrustedAuthorityFault, PermissionDeniedFault {
		client.updateCRL(trustedAuthorityName, crl);
	}

}
