package org.cagrid.gaards.authentication.service;

import gov.nih.nci.cagrid.opensaml.SAMLAssertion;

import java.rmi.RemoteException;
import java.util.Set;

import javax.xml.namespace.QName;

import org.cagrid.gaards.authentication.Credential;
import org.cagrid.gaards.authentication.common.AuthenticationProviderException;
import org.cagrid.gaards.authentication.common.InsufficientAttributeException;
import org.cagrid.gaards.authentication.common.InvalidCredentialException;


public interface AuthenticationProvider {
	
    SAMLAssertion authenticate(Credential credential)
	throws RemoteException, InvalidCredentialException,
	InsufficientAttributeException, AuthenticationProviderException;
    
    public Set<QName> getSupportedAuthenticationProfiles();
    
}
