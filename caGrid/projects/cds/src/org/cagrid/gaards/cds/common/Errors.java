package org.cagrid.gaards.cds.common;

import gov.nih.nci.cagrid.common.FaultHelper;

import org.cagrid.gaards.cds.stubs.types.CDSInternalFault;
import org.cagrid.gaards.cds.stubs.types.DelegationFault;
import org.cagrid.gaards.cds.stubs.types.PermissionDeniedFault;

public class Errors {

	public static String UNEXPECTED_DATABASE_ERROR = "An unexpected database error occurred.";
	public static String UNEXPECTED_ERROR_LOADING_CERTIFICATE_CHAIN = "An unexpected error occurred in loading the certificate chain.";
	public static String UNEXPECTED_ERROR_EXTRACTING_IDENTITY_FROM_CERTIFICATE_CHAIN = "An unexpected error occurred in extracting the grid identity from the certificate chain.";
	public static String UNEXPECTED_ERROR_LOADING_DELEGATION_POLICY = "An unexpected error occurred in loading the delegation policy.";
	public static String DELEGATION_RECORD_DOES_NOT_EXIST = "The delegation record does not exist.";
	public static String KEY_MANAGER_CHANGED = "The key manager cannot be changed.";
	public static String DELEGATION_POLICY_NOT_SUPPORTED = "The delegation policy specified is not supported.";
	public static String INVALID_KEY_LENGTH_SPECIFIED = "Invalid key length specified.";
	public static String INVALID_DELEGATION_PATH_LENGTH_SPECIFIED = "The delegation path length specified was either less than 0 or has exceeded the maximum delegation path length allowed by the service.";
	public static String MULTIPLE_HANDLERS_FOUND_FOR_POLICY = "Multiple handlers found for handling the policy, ";
	public static String INITIATOR_DOES_NOT_MATCH_APPROVER = "The approver must be the same entity as the initiator.";
	public static String CERTIFICATE_CHAIN_NOT_SPECIFIED = "No certificate chain specified.";
	public static String INSUFFICIENT_CERTIFICATE_CHAIN_SPECIFIED = "Insufficient certificate chain specified.";
	public static String IDENTITY_DOES_NOT_MATCH_INITIATOR = "The identity of the delegated credentials does not match the identity of the initiator.";
	public static String PUBLIC_KEY_DOES_NOT_MATCH = "The public key generated for the delegation does not match the public key supplied in the certificate.";
	public static String INVALID_CERTIFICATE_CHAIN = "The certificate chain provided is not valid or is not trusted.";
	public static String UNEXPECTED_ERROR_DETERMINING_DELEGATION_PATH_LENGTH = "An unexpected error occurred determining the delegation path length.";
	public static String CERTIFICATE_CHAIN_DOES_NOT_CONTAIN_PROXY = "The certificate chain provided does not begin with a proxy ceritficate.";
	public static String INSUFFICIENT_DELEGATION_PATH_LENGTH = "The delegation path allowed in the delegated credential is not sufficient.";
	public static String DELEGATION_APPROVAL_BUFFER_EXPIRED = "The time buffer allowed to approve the delegation has expired, the delegation can no longer be approved.";
	public static String POLICY_HANDLER_NOT_FOUND = "Policy handler could not be found.";
	public static String SIGNING_CREDENTIAL_EXPIRED = "Cannot obtain a delegated credential, the signing credential has expired.";
	public static String PERMISSION_DENIED_TO_DELEGATED_CREDENTIAL = "You do not have permission to obtain a delegated credential.";
	public static String UNEXPECTED_ERROR_CREATING_PROXY = "An unexpected error occurred creating the proxy.";
	public static String PROXY_LIFETIME_NOT_SPECIFIED = "No lifetime specified for delegated proxies.";
	public static String SIGNING_CREDENTIAL_ABOUT_EXPIRE = "Cannot issue delegated credential, the lifetime of the signing credential is about to expire.";
	public static String CANNOT_APPROVE_INVALID_STATUS = "Cannot approve delegation, only delegated credentials with a \"Pending\" status may be approved.";
	public static String CANNOT_GET_INVALID_STATUS = "Cannot get delegated credential, the signing credential has not been approved or has been suspended.";
	public static String CANNOT_CHANGE_STATUS_TO_PENDING = "Cannot change the status of the signing credential to pending.";
	public static String AUTHENTICATION_REQUIRED = "Authentication required to perform the requested operation.";
	public static String ADMIN_REQUIRED = "You must be an administrator to perform the requested operation.";
	public static String PERMISSION_DENIED = "You do not have permission to perform the requested operation.";
	public static String PERMISSION_DENIED_NO_DELEGATED_CREDENTIAL_SPECIFIED = "You must specify a delegated credential to audit.";
	public static String PERMISSION_DENIED_TO_AUDIT = "You do not have permission to audit the specified delegated credential.";

	public static CDSInternalFault getDatabaseFault(Exception e) {
		return getInternalFault(UNEXPECTED_DATABASE_ERROR, e);
	}

	public static CDSInternalFault getInternalFault(String error) {
		CDSInternalFault f = new CDSInternalFault();
		f.setFaultString(error);
		return f;
	}

	public static CDSInternalFault getInternalFault(String error, Exception e) {
		CDSInternalFault f = new CDSInternalFault();
		f.setFaultString(error);
		FaultHelper helper = new FaultHelper(f);
		helper.addFaultCause(e);
		f = (CDSInternalFault) helper.getFault();
		return f;
	}

	public static DelegationFault getDelegationFault(String error) {
		DelegationFault f = new DelegationFault();
		f.setFaultString(error);
		return f;
	}

	public static DelegationFault getDelegationFault(String error, Exception e) {
		DelegationFault f = new DelegationFault();
		f.setFaultString(error);
		FaultHelper helper = new FaultHelper(f);
		helper.addFaultCause(e);
		f = (DelegationFault) helper.getFault();
		return f;
	}

	public static PermissionDeniedFault getPermissionDeniedFault() {
		return getPermissionDeniedFault(PERMISSION_DENIED);
	}

	public static PermissionDeniedFault getPermissionDeniedFault(String error) {
		PermissionDeniedFault f = new PermissionDeniedFault();
		f.setFaultString(error);
		return f;
	}

}
