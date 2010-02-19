package org.cagrid.identifiers.namingauthority.dao;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.persistence.NonUniqueResultException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.identifiers.namingauthority.InvalidIdentifierException;
import org.cagrid.identifiers.namingauthority.InvalidIdentifierValuesException;
import org.cagrid.identifiers.namingauthority.NamingAuthorityConfigurationException;
import org.cagrid.identifiers.namingauthority.NamingAuthoritySecurityException;
import org.cagrid.identifiers.namingauthority.SecurityInfo;
import org.cagrid.identifiers.namingauthority.domain.IdentifierValues;
import org.cagrid.identifiers.namingauthority.domain.KeyData;
import org.cagrid.identifiers.namingauthority.hibernate.IdentifierMetadata;
import org.cagrid.identifiers.namingauthority.hibernate.IdentifierValueKey;
import org.cagrid.identifiers.namingauthority.impl.SecurityInfoImpl;
import org.cagrid.identifiers.namingauthority.util.IdentifierUtil;
import org.cagrid.identifiers.namingauthority.util.Keys;
import org.cagrid.identifiers.namingauthority.util.SecurityUtil;
import org.cagrid.identifiers.namingauthority.util.SecurityUtil.Access;

public class IdentifierMetadataDao extends AbstractDao<IdentifierMetadata> {
	
	protected static Log LOG = LogFactory.getLog(IdentifierMetadataDao.class.getName());
	private IdentifierMetadata systemValues = null;
	private URI prefix = null;
	
    @Override
    public Class<IdentifierMetadata> domainClass() {
        return IdentifierMetadata.class;
    }
    
    public synchronized void initialize( URI prefix ) throws NamingAuthorityConfigurationException {
    	this.prefix = prefix;
    	
   		try {
   			systemValues = loadLocalIdentifier(SecurityUtil.LOCAL_SYSTEM_IDENTIFIER);
   		} catch(InvalidIdentifierException e) {
   			LOG.debug("No system identifier defined");
   			createSystemIdentifier();
   		}
    }
    
    public IdentifierMetadata loadLocalIdentifier( final URI localIdentifier ) 
    	throws InvalidIdentifierException, NamingAuthorityConfigurationException {
    	
     	List<IdentifierMetadata> results = getHibernateTemplate().find(
                "SELECT md FROM " + domainClass().getName() + " md WHERE md.localIdentifier = ?",
                new Object[]{localIdentifier});
    	
    	IdentifierMetadata result = null;
    	
    	if (results.size() > 1) {
            throw new NonUniqueResultException("Found " + results.size() + " " + domainClass().getName()
                + " objects.");
        } else if (results.size() == 1) {
            result = results.get(0);
        }
    	
    	if (result == null) {
    		throw new InvalidIdentifierException("Local identifier (" + localIdentifier + ") does not exist");
    	}
    	
        return result;
    }
    
    public IdentifierMetadata loadIdentifier( URI identifier ) 
		throws InvalidIdentifierException, NamingAuthorityConfigurationException {
	
    	return loadLocalIdentifier(IdentifierUtil.getLocalName(prefix, identifier));
    }
	
    public IdentifierValues resolveIdentifier( SecurityInfo secInfo, java.net.URI identifier ) 
    	throws InvalidIdentifierException, NamingAuthoritySecurityException, NamingAuthorityConfigurationException {
    	
    	secInfo = validateSecurityInfo(secInfo);
    	
    	IdentifierMetadata resolvedValues = loadIdentifier( identifier );  
    			
		IdentifierValues outValues = null;
		try {
			outValues = resolveIdentifierSecurityChecks(secInfo, resolvedValues);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new NamingAuthoritySecurityException(e.getMessage() 
					+ " " + IdentifierUtil.getStackTrace(e));
		}
  	
		return outValues;
    }

    public String[] getKeys( SecurityInfo secInfo, java.net.URI identifier ) 
		throws 
			InvalidIdentifierException, 
			NamingAuthoritySecurityException, 
			NamingAuthorityConfigurationException {
    	
    	IdentifierValues values = resolveIdentifier( secInfo, identifier );
    	if (values != null) {
    		return values.getKeys();
    	}
    	
    	return null;
    }
    
    public String[] getKeyValues( SecurityInfo secInfo, URI identifier, String key) 
    	throws 
    		InvalidIdentifierException, 
    		NamingAuthoritySecurityException, 
    		NamingAuthorityConfigurationException {
    	
    	IdentifierValues values = resolveIdentifier(secInfo, identifier);
    	if (values != null && values.getValues(key) != null) {
    		List<String> valList = values.getValues(key).getValues();
    		if (valList != null) {
    			return (String[]) valList.toArray(new String[valList.size()]);
    		}
    	}
    	return null;
    }
    
	public void createIdentifier(SecurityInfo secInfo, URI localIdentifier, IdentifierValues ivalues) 
		throws InvalidIdentifierException, NamingAuthorityConfigurationException, NamingAuthoritySecurityException {
		
		secInfo = validateSecurityInfo(secInfo);
		
    	createIdentifierSecurityChecks(secInfo);
        
    	save(IdentifierUtil.convert(localIdentifier, ivalues));
	}
	
	public void createKeys(SecurityInfo secInfo, URI identifier, IdentifierValues values) 
		throws 
			InvalidIdentifierException, 
			NamingAuthoritySecurityException, 
			InvalidIdentifierValuesException, 
			NamingAuthorityConfigurationException {

		if (values == null || values.getKeys() == null || values.getKeys().length == 0) {
			throw new InvalidIdentifierValuesException("No keys were provided");
		}
		
		secInfo = validateSecurityInfo(secInfo);
		
		URI localIdentifier = IdentifierUtil.getLocalName(prefix, identifier);
		IdentifierMetadata resolvedValues = loadLocalIdentifier(localIdentifier);
		
		writeKeysSecurityChecks(secInfo, "to create keys", resolvedValues);
		
		String[] newKeys = values.getKeys();
		Collection<IdentifierValueKey> valueKeys = resolvedValues.getValues();
		for( String key : newKeys ) {
			IdentifierValueKey ivk = IdentifierUtil.convert(key, values);
			if (valueKeys.contains(ivk)) {
				throw new InvalidIdentifierValuesException("Key [" + key 
						+ "] already exists for identifier [" 
						+ identifier.normalize().toString() 
						+ "]");
			}
			valueKeys.add(ivk);
		}

		save(resolvedValues);
		
		if (SecurityUtil.isSystemIdentifier(localIdentifier)) {
			replaceSystemValues(secInfo, resolvedValues);
		}
	}

	public void deleteKeys(SecurityInfo secInfo, URI identifier, String[] keyList) 
		throws 
			InvalidIdentifierException, 
			NamingAuthoritySecurityException, 
			InvalidIdentifierValuesException, 
			NamingAuthorityConfigurationException {

		if (keyList == null || keyList.length == 0) {
			throw new InvalidIdentifierValuesException("No keys were provided");
		}
		
		secInfo = validateSecurityInfo(secInfo);
		
		URI localIdentifier = IdentifierUtil.getLocalName(prefix, identifier);
		IdentifierMetadata resolvedValues = loadLocalIdentifier(localIdentifier);
	
		if (resolvedValues.getValues() == null || resolvedValues.getValues().size() == 0) {
			throw new InvalidIdentifierValuesException("Identifier [" 
					+ identifier + "] has no keys");
		}

		writeKeysSecurityChecks(secInfo, "to delete keys", resolvedValues);

		LOG.warn("User [" + secInfo.getUser() + "] deleting some keys for identifier [" 
				+ identifier.toString() + "]");

		List<IdentifierValueKey> keysToDelete = new ArrayList<IdentifierValueKey>();	
		ArrayList<String> keyNames = new ArrayList<String>(Arrays.asList(keyList));
		
		for(IdentifierValueKey ivk : resolvedValues.getValues()) {
			
			if (keyNames.contains(ivk.getKey())) {
				LOG.debug("Removing key [" + ivk.getKey() + "]");
				keysToDelete.add(ivk);
			}
		}

		if (keysToDelete.size() != keyNames.size()) {
			// Unrecognized key. Should we actually fail this?
			String missingKeys = "";
			for(String key : keyNames) {
				if (!keysToDelete.contains(key)) {
					if (missingKeys.equals(""))
						missingKeys += key;
					else
						missingKeys += ", " +  key;
				}
			}
			throw new InvalidIdentifierValuesException("Unexpected keys found in the request [" 
					+ missingKeys + "]");
		}
		
		if (keysToDelete.size() > 0) {
			getHibernateTemplate().deleteAll(keysToDelete);
			resolvedValues.getValues().removeAll(keysToDelete);
		}
		
		save(resolvedValues);
		
		if (SecurityUtil.isSystemIdentifier(localIdentifier)) {
			replaceSystemValues(secInfo, resolvedValues);
		}
	}
	
	public void replaceKeys(SecurityInfo secInfo, URI identifier, IdentifierValues values) 
		throws 
			InvalidIdentifierException, 
			NamingAuthoritySecurityException, 
			InvalidIdentifierValuesException, 
			NamingAuthorityConfigurationException {

		if (values == null || values.getKeys() == null || values.getKeys().length == 0) {
			throw new InvalidIdentifierValuesException("No keys were provided");
		}
		
		secInfo = validateSecurityInfo(secInfo);

		URI localIdentifier = IdentifierUtil.getLocalName(prefix, identifier);
		IdentifierMetadata resolvedValues = loadLocalIdentifier(localIdentifier);
		
		if (resolvedValues.getValues() == null || resolvedValues.getValues().size() == 0) {
			throw new InvalidIdentifierValuesException("Identifier [" 
					+ identifier + "] has no keys");
		}

		try {
			replaceKeysSecurityChecks(secInfo, identifier, resolvedValues, values);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new NamingAuthoritySecurityException(e.getMessage() 
					+ " " + IdentifierUtil.getStackTrace(e));
		}
		
		if (SecurityUtil.isSystemIdentifier(localIdentifier)) {
			replaceSystemValues(secInfo, resolvedValues);
		}
	}
	
    public synchronized void createInitialAdministrator(String identity) 
    	throws NamingAuthorityConfigurationException {
    	
    	if (systemValues == null || systemValues.getValues() == null) {
    		throw new NamingAuthorityConfigurationException("No system values. Please initialize DAO first");
    	}
    	
    	IdentifierValueKey adminKey = null;
    	Collection<IdentifierValueKey> valCol = systemValues.getValues();
    	for(IdentifierValueKey ivk : valCol) {
    		if (ivk.getKey().equals(Keys.ADMIN_USERS)) {
    			adminKey = ivk;
    			break;
    		}
    	}

    	if (adminKey == null) {
    		adminKey = new IdentifierValueKey();
    		valCol.add(adminKey);
    	}
    	
    	if (adminKey.getValues() != null && adminKey.getValues().size() > 0) {
    		throw new NamingAuthorityConfigurationException("An administrator already exists");
    	}

    	ArrayList<String> values = new ArrayList<String>();
    	values.add(identity);
    	adminKey.setValues(values);
    	save(systemValues);
    }
	
	///////////////////////////////////////////////////////////////////
	/////////////////// SECURITY CHECKS ///////////////////////////////
	///////////////////////////////////////////////////////////////////

	//
	// A user can replace the value of a key if any one of the below 
	// conditions are met:
	//
	//    (a) User is identifier's administrator
	//       - User is listed by ADMIN_USERS key
	//       - User is listed by ADMIN_IDENTIFIERS's ADMIN_USERS key
	//       - User is listed by root identifier's ADMIN_USERS key
	//
	//    (b) User is listed by the key's READWRITE_IDENTIFIER's WRITE_USERS list
	//    (c) Key has no READWRITE_IDENTIFIER and user is listed by identifier's WRITE_USERS
	//    (d) Key has no READWRITE_IDENTIFIER and user is listed by identifier's READWRITE_IDENTIFIERS's WRITE_USERS
	//    (e) No WRITE_USERS keys at any level (key & identifier) (??). Not sure here, for now, this is
    //			allowed, unless it is an admin key.
	//
	public void replaceKeysSecurityChecks(
			SecurityInfo secInfo, 
			URI identifier, 
			IdentifierMetadata resolvedValues, 
			IdentifierValues newValues) 
		throws 
			InvalidIdentifierException, 
			NamingAuthoritySecurityException, 
			InvalidIdentifierValuesException, 
			URISyntaxException, NamingAuthorityConfigurationException {

		Access identifierAdminAccess = null;
		Access identifierWriteAccess = null;

		// User access as per Identifier's ADMIN_USERS settings
		identifierAdminAccess = getIdentifierAdminUserAccess(secInfo, resolvedValues);
		
		ArrayList<String> keysToReplace = new ArrayList<String>(Arrays.asList(newValues.getKeys()));
		for(IdentifierValueKey ivk : resolvedValues.getValues()) {
			
			if (!keysToReplace.contains(ivk.getKey())) {
				continue;
			}
			
			boolean okToUpdate = false;

			if (identifierAdminAccess == Access.GRANTED) {
				// User is a USER_ADMIN
				okToUpdate = true;
				
				LOG.debug("SECURITY: User [" + secInfo.getUser() + "] is authorized to write key [" + ivk.getKey() + "] by ADMIN_USERS");
				
			} else {
				// Look at key level security
				
				Access keyAccess = getKeyWriteAccess(secInfo, ivk.getReadWriteIdentifier());
				if (keyAccess == Access.GRANTED) {
					// User is a WRITE_USER at the key level
					okToUpdate = true;
					
					LOG.debug("SECURITY: User [" + secInfo.getUser() + "] is authorized to write key [" + ivk.getKey() + "] by key's WRITE_USERS");
					
				} else if (keyAccess == Access.DENIED) {
					LOG.debug("SECURITY: User [" + secInfo.getUser() + "] is NOT authorized to write key [" + ivk.getKey() + "] by key's WRITE_USERS");
				
				} else if (keyAccess == Access.NOSECURITY) {
					//
					// Fall back to identifier level security
					//
					if (identifierWriteAccess == null) {
						List<String> identifierWriteUsers = getAllWriteUsers( resolvedValues );
						identifierWriteAccess = userAccess(secInfo.getUser(), identifierWriteUsers);
					}
					
					if (identifierWriteAccess == Access.GRANTED) { 
						LOG.debug("SECURITY: User [" + secInfo.getUser() + "] is authorized to write [" + ivk.getKey() + "] by identifier");
						okToUpdate = true;
					}
					else if (identifierWriteAccess == Access.DENIED) {
						LOG.debug("SECURITY: User [" + secInfo.getUser() + "] is NOT authorized to write key [" + ivk.getKey() + "] by identifier");
					
					} 
					else if (identifierWriteAccess == Access.NOSECURITY) { 
						if (Keys.isAdminKey(ivk.getKey()) && identifierAdminAccess == Access.DENIED) {
							LOG.debug("SECURITY: User [" + secInfo.getUser() + "] is NOT authorized to write key [" + ivk.getKey() + "]. Neither writer nor admin");
						
						} else {
							LOG.debug("SECURITY: User [" + secInfo.getUser() + "] is authorized to write [" + ivk.getKey() + "] by identifier");
							okToUpdate = true;
						}
					}
				} 
			}

			if (okToUpdate) {
				keysToReplace.remove(ivk.getKey());
				KeyData kd = newValues.getValues(ivk.getKey());
				ivk.setReadWriteIdentifier(kd.getReadWriteIdentifier());
				ivk.setValues(kd.getValues());
			
			} else {
				throw new NamingAuthoritySecurityException(
						SecurityUtil.securityError(secInfo, "replace key [" + ivk.getKey() + "]"));
			}
		}

		if (keysToReplace.size() > 0) {
			throw new InvalidIdentifierValuesException("Key [" + keysToReplace.get(0) 
					+ "] does not exist for identifier [" + identifier.normalize().toString() 
					+ "]");
		}

		save(resolvedValues);
	}
	
	//
	// A user can read a key from an identifier if any one of the below 
	// conditions are met:
	//
	//    (a) User is identifier's administrator
	//       - User is listed by ADMIN_USERS key
	//       - User is listed by ADMIN_IDENTIFIERS's ADMIN_USERS key
	//       - User is listed by root identifier's ADMIN_USERS key
	//
	//    (b) User is listed by the key's READWRITE_IDENTIFIER's READ_USERS list
	//    (c) Key has no READWRITE_IDENTIFIER and user is listed by identifier's READ_USERS
	//    (d) Key has no READWRITE_IDENTIFIER and user is listed by identifier's READWRITE_IDENTIFIERS's READ_USERS
	//    (e) No READ_USERS keys at any level (key & identifier)
	//
	// A security exception is thrown if the identifier has keys and none are returned due to
	// permission checks.
	//
	private IdentifierValues resolveIdentifierSecurityChecks(SecurityInfo secInfo, IdentifierMetadata tmpValues) 
		throws 
			InvalidIdentifierException, 
			URISyntaxException, 
			NamingAuthoritySecurityException, 
			NamingAuthorityConfigurationException {
		
		if (tmpValues == null) {
			return null;
		}
		
		Collection<IdentifierValueKey> valueCol = tmpValues.getValues();
		if (valueCol == null || valueCol.size() == 0) {
			return null;
		}
		
		if (getIdentifierAdminUserAccess(secInfo, tmpValues) == Access.GRANTED) {
			//
			// User is ADMIN_USER
			//
			return IdentifierUtil.convert(tmpValues.getValues());
		}
		
		Access identifierReadAccess = null;
		IdentifierValues newValues = new IdentifierValues();
		
		for(IdentifierValueKey ivk : valueCol) {
						
			Access keyAccess = getKeyReadAccess(secInfo, ivk.getReadWriteIdentifier());
			if (keyAccess == Access.GRANTED) {
				LOG.debug("SECURITY: User [" + secInfo.getUser() + "] can access key [" + ivk.getKey() + "]");
				newValues.put(ivk.getKey(), new KeyData(ivk.getReadWriteIdentifier(), ivk.getValues()));
				
			} else if (keyAccess == Access.DENIED) {
				LOG.debug("SECURITY: User [" + secInfo.getUser() + "] can't access key [" + ivk.getKey() + "]");
				
			} else if (keyAccess == Access.NOSECURITY) {
				LOG.debug("SECURITY: No key security for ["+ ivk.getKey() + "]. Checking identifier security...");
				
				// Apply identifier level security

				if (identifierReadAccess == null) {
					List<String> identifierReadUsers = getAllReadUsers( tmpValues );
					identifierReadAccess = userAccess(secInfo.getUser(), identifierReadUsers);
				}
				
				if (identifierReadAccess == Access.DENIED) {
					LOG.debug("SECURITY: User [" + secInfo.getUser() + "] is NOT authorized to read key [" + ivk.getKey() + "] by identifier");
				
				} else {
					LOG.debug("SECURITY: User [" + secInfo.getUser() + "] is authorized to read [" + ivk.getKey() + "] by identifier");
					newValues.put(ivk.getKey(), new KeyData(ivk.getReadWriteIdentifier(), ivk.getValues()));
				}

			};
		}
		
		// Is this the only case when we bark?
		if (newValues.getKeys() == null || newValues.getKeys().length == 0) {
			throw new NamingAuthoritySecurityException(SecurityUtil.securityError(secInfo, 
					"to resolve identifier"));
		}
		
		return newValues;
	}
	
	//
	// A user can create identifiers if any one of the below 
	// conditions are met:
	//
	//    (a) PUBLIC_CREATTION key set to "Y" in root identifier
	//    (b) User is listed by IDENTIFIER_CREATION_USERS key in root identifier
	//    (c) No security settings are specified
	//       - No root identifier
	//       - No PUBLIC_CREATION key in root identifier
	//
	private synchronized void createIdentifierSecurityChecks(SecurityInfo secInfo) 
		throws InvalidIdentifierException, NamingAuthorityConfigurationException, NamingAuthoritySecurityException {
		
		List<String> values = SecurityUtil.getPublicCreation(systemValues);
		if (values == null || values.size() == 0) {
			// no security
			LOG.debug("SECURITY: No PUBLIC_CREATION");
			return;
		}
		
		if (values.size() != 1) {
			throw new NamingAuthorityConfigurationException("Bad PUBLIC_CREATION setting detected");
		}
		
		if (values.get(0).equalsIgnoreCase(SecurityUtil.PUBLIC_CREATION_YES)) {
			// everyone can create identifiers
			return;
		}
		
		List<String> authorizedUsers = SecurityUtil.getIdentifierCreationUsers(systemValues);
		if (authorizedUsers == null || !authorizedUsers.contains(secInfo.getUser())) {
			throw new NamingAuthoritySecurityException(SecurityUtil.securityError(secInfo, "to create identifiers"));
		}	
	}
	
	//
	// A user can add keys to an identifier if any one of the below 
	// conditions are met:
	//
	//    (a) User is listed by WRITE_USERS key
	//    (b) User is listed by WRITE_USERS key in READWRITE_IDENTIFIERS
	//    (c) User is listed by ADMIN_USERS key
	//    (d) User is listed by ADMIN_USERS key in ADMIN_IDENTIFIERS
	//    (e) User is listed by the system root identifier's ADMIN_USERS key
	//    (f) No security settings are specified
	//       - No WRITE_USERS key
	//       - No WRITE_USERS key in READWRITE_IDENTIFIERS
	//       - No ADMIN_USERS key
	//       - No ADMIN_USERS key in ADMIN_IDENTIFIERS
	//       - No ADMIN_USERS key in the system root identifier
	//
	private void writeKeysSecurityChecks(SecurityInfo secInfo, String op, IdentifierMetadata resolvedValues) 
		throws NamingAuthoritySecurityException {

		Access writeKeysAccess;
		
		try {
			//
			// Check if the user is a writer (WRITE_USERS)
			//
			writeKeysAccess = getWriteUserAccess(secInfo, resolvedValues);
			if (writeKeysAccess != Access.GRANTED) {
				//
				// Not listed as writer. Is it an administrator?
				//
				Access adminAccess = getIdentifierAdminUserAccess(secInfo, resolvedValues);
				if (adminAccess != Access.NOSECURITY) {
					writeKeysAccess = adminAccess;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new NamingAuthoritySecurityException(e.getMessage() 
					+ " " + IdentifierUtil.getStackTrace(e));
		} 
		
		if (writeKeysAccess == Access.DENIED) {
			throw new NamingAuthoritySecurityException(SecurityUtil.securityError(secInfo, op));
		}	
	}
	
	//
	// Returns any directly specified READ_USERS plus any
	// included by READWRITE_IDENTIFIERS
	//
	private List<String> getAllReadUsers( IdentifierMetadata values ) 
		throws 
			InvalidIdentifierException, 
			URISyntaxException, 
			NamingAuthorityConfigurationException {
		
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
	// Returns any directly specified WRITE_USERS plus any
	// included by READWRITE_IDENTIFIERS
	//
	private List<String> getAllWriteUsers( IdentifierMetadata values ) 
		throws 
			InvalidIdentifierException, 
			URISyntaxException, 
			NamingAuthorityConfigurationException {
		
		List<String> writeUsers = SecurityUtil.getWriteUsers(values);
		List<String> otherWriteUsers = getWriteUsersFromReadWriteIdentifiers(values);
		
		if (writeUsers == null) {
			return otherWriteUsers;
		}
		
		if (otherWriteUsers != null) {
			writeUsers.addAll(otherWriteUsers);
		}
		
		return writeUsers;
	}
	
	private Access getIdentifierAdminUserAccess(SecurityInfo secInfo, IdentifierMetadata values) 
		throws 
			InvalidIdentifierException, 
			URISyntaxException, 
			NamingAuthorityConfigurationException {

		// Check administrators defined by ADMIN_USERS and
		// ADMIN_IDENTIFIERS
		Access access = getAdminUserAccess(secInfo, values);
		if (access == Access.GRANTED) {
			// No further checks needed
			return Access.GRANTED;
		}
		
		// Check ADMIN_USERS defined by system (root) identifier
		if (getSystemAdminUserAccess(secInfo) == Access.GRANTED) {
			return Access.GRANTED;
		}
		
		return access;
	}
	
	// Checks if user is in ADMIN_USERS in system identifier
	private synchronized Access getSystemAdminUserAccess(SecurityInfo secInfo) {
		return userAccess(secInfo.getUser(), SecurityUtil.getAdminUsers(systemValues));
	}
	
	//
	// Returns:
	//		NOSECURITY: the identifier has no WRITE_USERS lists
	//		GRANTED: WRITE_USERS list defined and the user is listed
	//		DENIED: WRITE_USERS list defined and the user is not listed
	//		
	private Access getAdminUserAccess(SecurityInfo secInfo, IdentifierMetadata values) 
		throws 
			InvalidIdentifierException, 
			URISyntaxException, 
			NamingAuthorityConfigurationException {
		
		// Check locally defined ADMIN_USERS list
		Access access = userAccess(secInfo.getUser(), SecurityUtil.getAdminUsers(values));
		if (access == Access.GRANTED) {
			// No further checks needed
			return Access.GRANTED;
		}
		
		// Check ADMIN_USERS defined by ADMIN_IDENTIFIERS
		Access rwAccess = userAccess(secInfo.getUser(), getAdminUsersFromAdminIdentifiers(values));
		if (rwAccess == Access.NOSECURITY) {
			return access;
		}
		
		return rwAccess;
	}
	
	//
	// Returns:
	//		NOSECURITY: the identifier has no WRITE_USERS lists
	//		GRANTED: WRITE_USERS list defined and the user is listed
	//		DENIED: WRITE_USERS list defined and the user is not listed
	//		
	private Access getWriteUserAccess(SecurityInfo secInfo, IdentifierMetadata values) 
		throws 
			InvalidIdentifierException, 
			URISyntaxException, 
			NamingAuthorityConfigurationException {
		
		// Check locally defined WRITE_USERS list
		Access access = userAccess(secInfo.getUser(), SecurityUtil.getWriteUsers(values));
		if (access == Access.GRANTED) {
			// No further checks needed
			return Access.GRANTED;
		}
		
		// Check WRITE_USERS defined by READWRITE_IDENTIFIERS
		Access rwAccess = userAccess(secInfo.getUser(), getWriteUsersFromReadWriteIdentifiers(values));
		if (rwAccess == Access.NOSECURITY) {
			return access;
		}
		
		return rwAccess;
	}
	
	private Access userAccess(String requestingUser, List<String> authorizedUsers) {
		if (authorizedUsers == null) {
			return Access.NOSECURITY;
		}
		
		if (authorizedUsers.contains(requestingUser)) {
			return Access.GRANTED;
		}
		
		return Access.DENIED;
	}
	
	//
	// Returns WRITE_USERS listed by any READWRITE_IDENTIFIERS
	//
	private List<String> getWriteUsersFromReadWriteIdentifiers( IdentifierMetadata values ) 
		throws 
			InvalidIdentifierException, 
			URISyntaxException, 
			NamingAuthorityConfigurationException {
		
		List<String> writers = null;
		
		List<String> rwIdentifiers = SecurityUtil.getReadWriteIdentifiers(values);
		if (rwIdentifiers != null) {
			for( String identifier : rwIdentifiers) {
				List<String> writeUsers = SecurityUtil.getWriteUsers(
						loadIdentifier(new URI(identifier)));
				if (writeUsers != null) {
					if (writers == null) {
						writers = new ArrayList<String>();
					}
					writers.addAll(writeUsers);
				}
			}
		}
		
		return writers;
	}
	
	//
	// Returns READ_USERS listed by any READWRITE_IDENTIFIERS
	//
	private List<String> getReadUsersFromReadWriteIdentifiers( IdentifierMetadata values ) 
		throws 
			InvalidIdentifierException, 
			URISyntaxException, 
			NamingAuthorityConfigurationException {
		
		List<String> readers = null;
		
		List<String> rwIdentifiers = SecurityUtil.getReadWriteIdentifiers(values);
		if (rwIdentifiers != null) {
			for( String identifier : rwIdentifiers) {
				List<String> readUsers = SecurityUtil.getReadUsers(
						loadIdentifier(new URI(identifier)));
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
	
	//
	// Returns ADMIN_USERS listed by any ADMIN_IDENTIFIERS
	//
	private List<String> getAdminUsersFromAdminIdentifiers( IdentifierMetadata values ) 
		throws InvalidIdentifierException, URISyntaxException, NamingAuthorityConfigurationException {
		
		List<String> allAdmins = null;
		
		List<String> adminIdentifiers = SecurityUtil.getAdminIdentifiers(values);
		if (adminIdentifiers != null) {
			for( String identifier : adminIdentifiers) {
				List<String> admins = SecurityUtil.getAdminUsers(
						loadIdentifier(new URI(identifier)));
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
	
	//
	// Looks at READ security settings at the key level.
	// Key security is determined by looking at the READWRITE_IDENTIFIER
	// attached to the key.
	// 
	private Access getKeyReadAccess(SecurityInfo secInfo, 
			URI rwIdentifier) throws InvalidIdentifierException, NamingAuthorityConfigurationException {
		
		if (rwIdentifier == null || rwIdentifier.normalize().toString().length() == 0) {
			// no security at key level
			return Access.NOSECURITY;
		}

		List<String> readers = SecurityUtil.getReadUsers(
				loadIdentifier(rwIdentifier));
			
		return userAccess(secInfo.getUser(), readers);
	}
	
	//
	// Looks at WRITE security settings at the key level.
	// Key security is determined by looking at the READWRITE_IDENTIFIER
	// attached to the key.
	// 
	private Access getKeyWriteAccess(SecurityInfo secInfo, 
			URI rwIdentifier) throws InvalidIdentifierException, NamingAuthorityConfigurationException {
		
		if (rwIdentifier == null || rwIdentifier.normalize().toString().length() == 0) {
			// no security at key level
			return Access.NOSECURITY;
		}

		List<String> writers = SecurityUtil.getWriteUsers(
				loadIdentifier(rwIdentifier));
			
		return userAccess(secInfo.getUser(), writers);
	}
	
	private synchronized void replaceSystemValues(SecurityInfo secInfo, IdentifierMetadata resolvedValues) {
		systemValues = resolvedValues;
		LOG.warn("System identifier updated by [" + secInfo.getUser() + "]");
	}
	
	private synchronized void createSystemIdentifier() {
		systemValues = new IdentifierMetadata();
		Collection<IdentifierValueKey> valCol = new ArrayList<IdentifierValueKey>();
		systemValues.setLocalIdentifier(SecurityUtil.LOCAL_SYSTEM_IDENTIFIER);
		systemValues.setValues(valCol);
		
		//
		// PUBLIC_CREATION is true by default
		//
		valCol.add(new IdentifierValueKey(Keys.PUBLIC_CREATION, 
				new String[]{ SecurityUtil.PUBLIC_CREATION_YES }, null ));
		
		//
		// ADMIN_USERS is an empty list by default
		//
		valCol.add(new IdentifierValueKey(Keys.ADMIN_USERS, 
				new String[]{}, null ));
		save(systemValues);
	}
	
	private SecurityInfo validateSecurityInfo( SecurityInfo secInfo ) {
		if (secInfo == null || secInfo.getUser() == null) {
			return new SecurityInfoImpl("");
		}
		
		return secInfo;
	}
		
//	Not supported
//	public void deleteAllKeys(SecurityInfo secInfo, URI localIdentifier) 
//		throws 
//			InvalidIdentifierException, 
//			NamingAuthoritySecurityException, 
//			InvalidIdentifierValuesException {
//		
//		secInfo = validateSecurityInfo(secInfo);
//
//		IdentifierMetadata resolvedValues = loadIdentifier(localIdentifier);
//
//		Collection<IdentifierValueKey> valueCol = resolvedValues.getValues();
//		if (valueCol == null || valueCol.size() == 0) {
//			// Identifier has nothing already
//			return;
//		}
//
//		writeKeysSecurityChecks(secInfo, "deleteAllKeys", resolvedValues);
//
//		LOG.warn("User [" + secInfo.getUser() + "] deleting all keys for identifier [" + localIdentifier.toString() + "]");
//
//		List<IdentifierValueKey> keysToDelete = new ArrayList<IdentifierValueKey>();
//		
//		for( IdentifierValueKey ivk : valueCol) {
//			if (Keys.isAdminKey(ivk.getKey())) {
//				LOG.debug("Won't remove key [" + ivk.getKey() + "]");
//				// "ADMIN" keys can't be deleted using this API
//				continue;
//			}
//			keysToDelete.add(ivk);
//		}
//
//		if (keysToDelete.size() > 0) {
//			getHibernateTemplate().deleteAll(keysToDelete);
//		}
//	}
}
