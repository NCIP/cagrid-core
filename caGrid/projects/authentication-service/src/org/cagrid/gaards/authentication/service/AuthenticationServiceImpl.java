/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.gaards.authentication.service;

import gov.nih.nci.security.authentication.BetterLockoutManager;
import gov.nih.nci.security.authentication.LockoutManager;

import java.io.File;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.cagrid.gaards.authentication.AuthenticationProfiles;
import org.cagrid.gaards.authentication.lockout.LockedUserInfo;


/**
 * TODO:I am the service side implementation class. IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.2
 */
public class AuthenticationServiceImpl extends AuthenticationServiceImplBase {

    private AuthenticationManager auth;


    public AuthenticationServiceImpl() throws RemoteException {
        super();
        try {
            String configFile = AuthenticationServiceConfiguration.getConfiguration().getAuthenticationConfiguration();
            String propertiesFile = AuthenticationServiceConfiguration.getConfiguration().getAuthenticationProperties();
            this.auth = new AuthenticationManager(new File(propertiesFile), new File(configFile));
            Set<QName> set = this.auth.getSupportedAuthenticationProfiles();
            QName[] list = new QName[set.size()];
            list = set.toArray(list);
            AuthenticationProfiles profiles = new AuthenticationProfiles();
            profiles.setProfile(list);
            getResourceHome().getAddressedResource().setAuthenticationProfiles(profiles);
            String whitelistFile = AuthenticationServiceConfiguration.getConfiguration().getLockoutWhitelistFile();
            WhitelistUpdater.monitorWhitelist(whitelistFile);
        } catch (Exception ex) {
            throw new RemoteException("Error instantiating AuthenticationProvider: " + ex.getMessage(), ex);
        }
    }


    public gov.nih.nci.cagrid.authentication.bean.SAMLAssertion authenticate(
        gov.nih.nci.cagrid.authentication.bean.Credential credential) throws RemoteException,
        gov.nih.nci.cagrid.authentication.stubs.types.InvalidCredentialFault,
        gov.nih.nci.cagrid.authentication.stubs.types.InsufficientAttributeFault,
        gov.nih.nci.cagrid.authentication.stubs.types.AuthenticationProviderFault {
        return this.auth.authenticate(credential);
    }


    public gov.nih.nci.cagrid.opensaml.SAMLAssertion authenticateUser(
        org.cagrid.gaards.authentication.Credential credential) throws RemoteException,
        org.cagrid.gaards.authentication.faults.AuthenticationProviderFault,
        org.cagrid.gaards.authentication.faults.CredentialNotSupportedFault,
        org.cagrid.gaards.authentication.faults.InsufficientAttributeFault,
        org.cagrid.gaards.authentication.faults.InvalidCredentialFault {
        return this.auth.authenticate(credential);
    }


    public org.cagrid.gaards.authentication.lockout.LockedUserInfo[] getLockedOutUsers() throws RemoteException {
        BetterLockoutManager manager = LockoutManager.getInstance().getDelegatedLockoutManager();
        Map<String, Date> lockouts = manager.getLockedOutUsers();
        LockedUserInfo[] info = new LockedUserInfo[lockouts.size()];
        int index = 0;
        for (String userId : lockouts.keySet()) {
            Date unlockTime = lockouts.get(userId);
            Calendar cal = Calendar.getInstance();
            cal.setTime(unlockTime);
            info[index] = new LockedUserInfo(cal, userId);
            index++;
        }
        return info;
    }
}
