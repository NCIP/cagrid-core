package org.cagrid.identifiers.namingauthority.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.cagrid.identifiers.namingauthority.InvalidIdentifierException;
import org.cagrid.identifiers.namingauthority.NamingAuthorityConfigurationException;
import org.cagrid.identifiers.namingauthority.NamingAuthoritySecurityException;
import org.cagrid.identifiers.namingauthority.SecurityInfo;
import org.cagrid.identifiers.namingauthority.domain.IdentifierValues;
import org.cagrid.identifiers.namingauthority.domain.KeyData;
import org.cagrid.identifiers.namingauthority.util.SecurityUtil;
import org.cagrid.identifiers.namingauthority.util.SecurityUtil.Access;

public class SecurityHelper {
	private NamingAuthorityImpl namingAuthority;
	private volatile IdentifierValues systemValues = null;
	
	public SecurityHelper(NamingAuthorityImpl na) {
		this.namingAuthority = na;
	}

	public void checkCreateIdentifierSecurity(SecurityInfo secInfo) throws InvalidIdentifierException, NamingAuthorityConfigurationException, NamingAuthoritySecurityException {
		IdentifierValues sysValues = getSystemValues();
		if (sysValues == null) {
			// no security
			return;
		}
		
		List<String> values = SecurityUtil.getPublicCreation(sysValues);
		if (values == null || values.size() == 0) {
			// no security
			return;
		}
		
		if (values.size() != 1) {
			throw new NamingAuthorityConfigurationException("Bad PUBLIC_CREATION setting detected");
		}
		
		if (values.get(0).equalsIgnoreCase("Y")) {
			// everyone can create identifiers
			return;
		}
		
		List<String> authorizedUsers = SecurityUtil.getIdentifierCreationUsers(sysValues);
		if (authorizedUsers == null || !authorizedUsers.contains(secInfo.getUser())) {
			throw new NamingAuthoritySecurityException("User [" + secInfo.getUser() + "] is not authorized to create identifiers");
		}	
	}
	
	public IdentifierValues checkSecurity(SecurityInfo secInfo, 
			IdentifierValues tmpValues) throws InvalidIdentifierException, URISyntaxException {
			
		if (tmpValues == null) {
			return null;
		}
		
		String[] keys = tmpValues.getKeys();
		if (keys == null) {
			return tmpValues;
		}
		
		if (isAdmin(secInfo, tmpValues)) {
			return tmpValues;
		}
		
		IdentifierValues newValues = new IdentifierValues();
		List<String> identifierReadUsers = null;
		boolean identifierReadUsersLoaded = false;
		
		for(String key : keys) {
			
			KeyData kd = tmpValues.getValues(key);
			
			Access keyAccess = getKeyAccess(secInfo, kd);
			switch(keyAccess) {
			case GRANTED:
				//TODO:
				System.out.println("SECURITY: User [" + secInfo.getUser() + "] can access key [" + key + "]");
				newValues.put(key, kd);
				break;

			case DENIED:
				//TODO:
				System.out.println("SECURITY: User [" + secInfo.getUser() + "] can't access key [" + key + "]");
				break;

			case NOSECURITY:
				//TODO:
				System.out.println("SECURITY: No key security for ["+key+"]. Checking identifier security...");
				
				// Apply identifier level security
				if (!identifierReadUsersLoaded) {
					identifierReadUsers = getAllReadUsers( tmpValues );
					identifierReadUsersLoaded = true;
				}

				if (identifierReadUsers == null) {
					// no security at identifier level - grant access
					//TODO:
					System.out.println("SECURITY: No identifier security - grant access");
					newValues.put(key, kd);
				} else {
					// access controlled by identifier's readers list
					if (identifierReadUsers.contains(secInfo.getUser())) {
						//TODO:
						System.out.println("SECURITY: User [" + secInfo.getUser() + "] is authorized to read [" + key + "] by identifier");
						newValues.put(key, kd);
					} else {
						//TODO:
						System.out.println("SECURITY: User [" + secInfo.getUser() + "] is NOT authorized to read key [" + key + "] by identifier");
					}
				}
				break;
			};
		}
		
		return newValues;
	}
	
	//
	// Looks at security settings at the key level.
	// Key security is determined by looking at the READWRITE_IDENTIFIER
	// attached to the key.
	// 
	private SecurityUtil.Access getKeyAccess(SecurityInfo secInfo, 
			KeyData kd) throws InvalidIdentifierException {

		if (kd == null) {
			// no values to protect
			return SecurityUtil.Access.NOSECURITY;
		}

		URI rwIdentifier = kd.getReadWriteIdentifier();
		if (rwIdentifier != null && rwIdentifier.normalize().toString().length() > 0) {

			List<String> readers = SecurityUtil.getReadUsers(
					namingAuthority.resolveLocalIdentifier(rwIdentifier));

			if (readers != null) {
				// access controlled by key's reader list 
				if (readers.contains(secInfo.getUser())) {
					return SecurityUtil.Access.GRANTED;
				}
			
				return SecurityUtil.Access.DENIED;
			}
		}

		//
		// Either the READWRITE_IDENTIFER was missing
		// or the READ_USERS list was missing....
		// So we fall back to identifier level security
		//
		
		return SecurityUtil.Access.NOSECURITY;
	}
	
	//
	// Returns any directly specified READ_USERS plus any
	// included by READWRITE_IDENTIFIERS
	//
	private List<String> getAllReadUsers( IdentifierValues values ) throws InvalidIdentifierException, URISyntaxException {
		List<String> readUsers = SecurityUtil.getReadUsers(values);
		List<String> otherReadUsers = getReadUsersFromReadWriteIdentifiers(values);
		
		if (readUsers == null) {
			return otherReadUsers;
		}
		
		if (otherReadUsers != null) {
			readUsers.addAll(otherReadUsers);
		}
		
		return readUsers;
	}
	

	//
	// Returns READ_USERS listed by any READWRITE_IDENTIFIERS
	//
	private List<String> getReadUsersFromReadWriteIdentifiers( IdentifierValues values ) 
		throws InvalidIdentifierException, URISyntaxException {
		
		List<String> readers = null;
		
		List<String> rwIdentifiers = SecurityUtil.getReadWriteIdentifiers(values);
		if (rwIdentifiers != null) {
			for( String identifier : rwIdentifiers) {
				List<String> readUsers = SecurityUtil.getReadUsers(
						namingAuthority.resolveLocalIdentifier(new URI(identifier)));
				if (readUsers != null) {
					if (readers == null) {
						readers = new ArrayList<String>();
					}
					readers.addAll(readUsers);
				}
			}
		}
		
		return readers;
	}

	private boolean isAdmin(SecurityInfo secInfo, IdentifierValues values) throws InvalidIdentifierException, URISyntaxException {
		
		List<String> sysAdmins = SecurityUtil.getAdminUsers(getSystemValues());
		if (sysAdmins != null && sysAdmins.contains(secInfo.getUser())) {
			//TODO:
			System.out.println("SECURITY: User [" + secInfo.getUser() + "] is a system administrator");
			return true;
		}
		
		List<String> identifierAdmins = getAllAdminUsers(values);
		if (identifierAdmins != null && identifierAdmins.contains(secInfo.getUser())) {
			//TODO:
			System.out.println("SECURITY: User [" + secInfo.getUser() + "] is an identifier administrator");
			return true;
		}
	
		//TODO:
		System.out.println("SECURITY: User [" + secInfo.getUser() + "] is not an administrator");
		return false;
	}
	
	//
	// Returns any directly specified ADMIN_USERS plus any
	// included by ADMIN_IDENTIFIERS
	//
	private List<String> getAllAdminUsers( IdentifierValues values ) throws InvalidIdentifierException, URISyntaxException {
		List<String> adminUsers = SecurityUtil.getAdminUsers(values);
		List<String> otherAdminUsers = getAdminUsersFromAdminIdentifiers(values);
		
		if (adminUsers == null) {
			return otherAdminUsers;
		}
		
		if (otherAdminUsers != null) {
			adminUsers.addAll(otherAdminUsers);
		}
		
		return adminUsers;
	}
	
	//
	// Returns ADMIN_USERS listed by any ADMIN_IDENTIFIERS
	//
	private List<String> getAdminUsersFromAdminIdentifiers( IdentifierValues values ) 
		throws InvalidIdentifierException, URISyntaxException {
		
		List<String> allAdmins = null;
		
		List<String> adminIdentifiers = SecurityUtil.getAdminIdentifiers(values);
		if (adminIdentifiers != null) {
			for( String identifier : adminIdentifiers) {
				List<String> admins = SecurityUtil.getAdminUsers(
						namingAuthority.resolveLocalIdentifier(new URI(identifier)));
				if (admins != null) {
					if (allAdmins == null) {
						allAdmins = new ArrayList<String>();
					}
					allAdmins.addAll(admins);
				}
			}
		}
		
		return allAdmins;
	}
	

	
	private IdentifierValues getSystemValues() {
		
		if (systemValues == null) {
			synchronized(this) {
				if (systemValues == null) {
					try {
						systemValues = namingAuthority.resolveLocalIdentifier(SecurityUtil.SYSTEM_IDENTIFIER);
					} catch(InvalidIdentifierException e) {
						System.out.println("No system identifier defined");
						systemValues = new IdentifierValues();
					}
				}
			}
		}
		
		return systemValues;
	}
}
