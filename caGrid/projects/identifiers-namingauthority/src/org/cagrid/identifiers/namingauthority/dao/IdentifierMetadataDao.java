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
import org.cagrid.identifiers.namingauthority.domain.IdentifierData;
import org.cagrid.identifiers.namingauthority.domain.IdentifierValues;
import org.cagrid.identifiers.namingauthority.domain.KeyData;
import org.cagrid.identifiers.namingauthority.domain.KeyValues;
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
	
    /*
     * Returns keys associated with the given identifier
     */
    public String[] getKeyNames( SecurityInfo secInfo, java.net.URI identifier ) 
		throws 
			InvalidIdentifierException, 
			NamingAuthoritySecurityException, 
			NamingAuthorityConfigurationException {
    	
    	IdentifierData values = resolveIdentifier( secInfo, identifier );
    	if (values != null) {
    		return values.getKeys();
    	}
    	
    	return null;
    }
    
    /*
     * Returns values associated with a key in the given identifier
     */
    public KeyData getKeyData( SecurityInfo secInfo, URI identifier, String key) 
    	throws 
    		InvalidIdentifierException, 
    		NamingAuthoritySecurityException, 
    		NamingAuthorityConfigurationException {
    	
    	IdentifierData values = resolveIdentifier(secInfo, identifier);
    	if (values != null && values.getValues(key) != null) {
    		return values.getValues(key);
    	}
    	return null;
    }
    
	/*
	 * A user can read a key from an identifier if any one of the below 
	 * conditions are met:
	 *
	 *    (a) User is identifier's administrator
	 *       - User is listed by ADMIN_USERS key, or
	 *       - User is listed by ADMIN_IDENTIFIERS's ADMIN_USERS key, or
	 *       - User is listed by root identifier's ADMIN_USERS key
	 *
	 *    (b) User is listed by the key's READWRITE_IDENTIFIER's READ_USERS list
	 *    (c) Key has no READWRITE_IDENTIFIER.READ_USERS and user is listed by identifier's READ_USERS
	 *    (d) Key has no READWRITE_IDENTIFIER.READ_USERS and user is listed by identifier's READWRITE_IDENTIFIERS.READ_USERS
	 *    (e) No READ_USERS keys at any level (key & identifier)
	 *
	 * A security exception is thrown if the identifier has keys and none are returned due to
	 * permission checks.
	 */
    public IdentifierData resolveIdentifier( SecurityInfo secInfo, java.net.URI identifier ) 
    	throws 
    		InvalidIdentifierException, 
    		NamingAuthoritySecurityException, 
    		NamingAuthorityConfigurationException {

    	secInfo = validateSecurityInfo(secInfo);

    	IdentifierMetadata tmpValues = loadIdentifier( identifier );  

    	if (tmpValues == null) {
			return null;
		}
		
		Collection<IdentifierValueKey> valueCol = tmpValues.getValues();
		if (valueCol == null || valueCol.size() == 0) {
			return null;
		}
		
		if (hasIdentifierAdminUserAccess(secInfo, tmpValues)) {
			//
			// User is ADMIN_USER
			//
			return IdentifierUtil.convert(tmpValues.getValues());
		}
		
		Access identifierReadAccess = null;
		IdentifierData newValues = new IdentifierData();
		
		for(IdentifierValueKey ivk : valueCol) {
						
			Access keyAccess = getKeyReadAccess(secInfo, ivk.getPolicyIdentifier());
			if (keyAccess == Access.GRANTED) {
				LOG.debug("SECURITY: User [" + secInfo.getUser() + "] can access key [" + ivk.getKey() + "]");
				newValues.put(ivk.getKey(), IdentifierUtil.convert(ivk));
				
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
					newValues.put(ivk.getKey(), IdentifierUtil.convert(ivk));
				}

			};
		}
		
		// Is this the only case when we bark?
		if (newValues.getKeys() == null || newValues.getKeys().length == 0) {
			throw new NamingAuthoritySecurityException(SecurityUtil.securityError(secInfo, 
					"resolve identifier"));
		}
		
		return newValues;
    }
    
    /*
     * Persists the provided identifier with the given values
     */
	public void createIdentifier(SecurityInfo secInfo, URI localIdentifier, IdentifierData ivalues) 
		throws InvalidIdentifierException, NamingAuthorityConfigurationException, NamingAuthoritySecurityException {
		
		secInfo = validateSecurityInfo(secInfo);
		
    	createIdentifierSecurityChecks(secInfo);
        
    	save(IdentifierUtil.convert(localIdentifier, ivalues));
	}
	
	/* 
	 * Adds the provided keys to the given identifier.
	 * 
	 * An exception is thrown if any of the provided keys already exists.
	 * 
	 * Permissions:
	 * 
	 * Case 1) Creating security-type keys as defined by Keys.isAdminKey()
	 * 		A user can create security-type keys if s/he is explicitly
	 * 		listed as an ADMIN_USER by either the identifier, or the system
	 * 		(root) identifier.
	 * 
	 * Case 2) Creating other keys
	 * 		A user can create other keys if s/he is listed as a WRITE_USER
	 * 		by the identifier, or if a WRITE_USERS key is not configured
	 * 		by the identifier.
	 *
	 *		Identifier's ADMIN_USERS can create keys of any type. It is
	 *		unnecessary to list them as WRITE_USERS.
	 */
	public void createKeys(SecurityInfo secInfo, URI identifier, IdentifierData values) 
		throws 
			InvalidIdentifierException, 
			NamingAuthoritySecurityException, 
			InvalidIdentifierValuesException, 
			NamingAuthorityConfigurationException {

		Boolean writerAccess = null;
		Boolean adminAccess = null;
		
		if (values == null || values.getKeys() == null || values.getKeys().length == 0) {
			throw new InvalidIdentifierValuesException("No keys were provided");
		}
		
		secInfo = validateSecurityInfo(secInfo);
		
		URI localIdentifier = IdentifierUtil.getLocalName(prefix, identifier);
		IdentifierMetadata resolvedValues = loadLocalIdentifier(localIdentifier);

		Collection<IdentifierValueKey> valueKeys = resolvedValues.getValues();
		for(String key : values.getKeys()) {
			

			// Start of security checks
			if (Keys.isAdminKey(key)) {
				
				if (adminAccess == null) {
					adminAccess = hasIdentifierAdminUserAccess(secInfo, resolvedValues);
				}
				
				if (!adminAccess) {
					throw new NamingAuthoritySecurityException(SecurityUtil.securityError(secInfo, 
							"create key [" + key + "]. Not an ADMIN_USER"));
				}
			
			} else {
				if (writerAccess == null) {
					writerAccess = hasWriteUserAccess(secInfo, resolvedValues);
				}
				
				if (!writerAccess) {
					//
					// Check if user is administrator
					//
					if (adminAccess == null) {
						adminAccess = hasIdentifierAdminUserAccess(secInfo, resolvedValues);
					}
					
					if (!adminAccess) {
						throw new NamingAuthoritySecurityException(SecurityUtil.securityError(secInfo, 
								"create keys. Neither WRITE_USER nor ADMIN_USER"));
					}
				}
			}
			// End of security checks
			
			IdentifierValueKey ivk = IdentifierUtil.convert(key, values.getValues(key));
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

	/* 
	 * Deletes the provided keys from the given identifier.
	 * 
	 * An exception is thrown if any of the provided keys does not exist.
	 * 
	 * Permissions:
	 * 
	 * Case 1) Deleting security-type keys as defined by Keys.isAdminKey()
	 * 		A user can delete security-type keys if s/he is explicitly
	 * 		listed as an ADMIN_USER by either the identifier, or the system
	 * 		(root) identifier.
	 * 
	 * Case 2) Deleting other keys
	 * 		A user can delete other keys if s/he is listed as a WRITE_USER
	 * 		by the identifier, or if a WRITE_USERS key is not configured
	 * 		by the identifier.
	 *
	 *		Identifier's ADMIN_USERS can delete keys of any type. It is
	 *		unnecessary to list them as WRITE_USERS.
	 */
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
		
		Boolean writerAccess = null;
		Boolean adminAccess = null;
		URI localIdentifier = IdentifierUtil.getLocalName(prefix, identifier);
		IdentifierMetadata resolvedValues = loadLocalIdentifier(localIdentifier);
	
		if (resolvedValues.getValues() == null || resolvedValues.getValues().size() == 0) {
			throw new InvalidIdentifierValuesException("Identifier [" 
					+ identifier + "] has no keys");
		}

		LOG.warn("User [" + secInfo.getUser() + "] deleting some keys for identifier [" 
				+ identifier.toString() + "]");

		List<IdentifierValueKey> keysToDelete = new ArrayList<IdentifierValueKey>();	
		ArrayList<String> keyNames = new ArrayList<String>(Arrays.asList(keyList));
		
		for(IdentifierValueKey ivk : resolvedValues.getValues()) {
			if (!keyNames.contains(ivk.getKey())) {
				continue;
			}
			
			// Start of security checks
			if (Keys.isAdminKey(ivk.getKey())) {
				
				if (adminAccess == null) {
					adminAccess = hasIdentifierAdminUserAccess(secInfo, resolvedValues);
				}
				
				if (!adminAccess) {
					throw new NamingAuthoritySecurityException(SecurityUtil.securityError(secInfo, 
							"delete key [" + ivk.getKey() + "]. Not an ADMIN_USER"));
				}
				
			} else {
				if (writerAccess == null) {
					writerAccess = hasWriteUserAccess(secInfo, resolvedValues);
				}
				
				if (!writerAccess) {
					//
					// Check if user is administrator
					//
					if (adminAccess == null) {
						adminAccess = hasIdentifierAdminUserAccess(secInfo, resolvedValues);
					}
					
					if (!adminAccess) {
						throw new NamingAuthoritySecurityException(SecurityUtil.securityError(secInfo, 
								"delete keys. Neither WRITE_USER nor ADMIN_USER"));
					}
				}
			}
			// End of security checks
			
			LOG.debug("Removing key [" + ivk.getKey() + "]");
			keysToDelete.add(ivk);
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
	
	/* 
	 * Replaces the values associated with existing keys with the new
	 * provided values.
	 * 
	 * An exception is thrown if any of the provided keys does not exist
	 * 
	 * Permissions:
	 * 
	 * Identifier's ADMIN_USERS can replace any key values
	 * 
	 * Security-type key values can only be replaced by ADMIN_USERS
	 * 
	 * Regular users can replace the values for a key if s/he is listed as
	 * a WRITE_USER for that key.
	 * 
	 * When no WRITE_USERS key is defined at the key level, the user
	 * must be listed as a WRITE_USER for the identifier.
	 * 
	 * When no WRITE_USERS key is defined for either the key or 
	 * the identifier, any user can replace the values. 
	 *
	 */
	public void replaceKeyValues(SecurityInfo secInfo, URI identifier, IdentifierValues newValues) 
		throws 
			InvalidIdentifierException, 
			NamingAuthoritySecurityException, 
			InvalidIdentifierValuesException, 
			NamingAuthorityConfigurationException {

		if (newValues == null || newValues.getKeys() == null || newValues.getKeys().length == 0) {
			throw new InvalidIdentifierValuesException("No KeyValues were provided");
		}

		secInfo = validateSecurityInfo(secInfo);

		URI localIdentifier = IdentifierUtil.getLocalName(prefix, identifier);
		IdentifierMetadata resolvedValues = loadLocalIdentifier(localIdentifier);

		if (resolvedValues.getValues() == null || resolvedValues.getValues().size() == 0) {
			throw new InvalidIdentifierValuesException("Identifier [" 
					+ identifier + "] has no keys");
		}

		Boolean identifierAdminAccess = hasIdentifierAdminUserAccess(secInfo, resolvedValues);
		Boolean identifierWriteAccess = null;
		
		ArrayList<String> keysToReplace = new ArrayList<String>(Arrays.asList(newValues.getKeys()));
		for(IdentifierValueKey ivk : resolvedValues.getValues()) {

			if (!keysToReplace.contains(ivk.getKey())) {
				continue;
			}

			if (!identifierAdminAccess) {
			
				if (Keys.isAdminKey(ivk.getKey())) {
					String error = SecurityUtil.securityError(secInfo, 
							"replace key [" + ivk.getKey() + "]. Not an ADMIN_USER");
					LOG.error(error);
					throw new NamingAuthoritySecurityException(error);
				}

				//
				// Check key level security using key's read-write identifier
				//
				Access keyAccess = getKeyWriteAccess(secInfo, ivk.getPolicyIdentifier());
				if (keyAccess == Access.DENIED) {
					//
					// WRITE_USERS defined for key and this user is not listed
					//
					String error = SecurityUtil.securityError(secInfo, 
							"replace key [" + ivk.getKey() + "]. Not a WRITE_USER");
					LOG.error(error);
					throw new NamingAuthoritySecurityException(error);
				}

				if (keyAccess == Access.NOSECURITY) {
					//
					// Fall back to identifier level security
					//
					if (identifierWriteAccess == null) {
						identifierWriteAccess = hasWriteUserAccess(secInfo, resolvedValues);
					}

					if (!identifierWriteAccess) {
						//
						// WRITE_USERS defined identifier and this user is not listed
						//
						String error = SecurityUtil.securityError(secInfo, 
								"replace key [" + ivk.getKey() + "]. Not a WRITE_USER");
						LOG.error(error);
						throw new NamingAuthoritySecurityException(error);
					} 
				}
			}
			keysToReplace.remove(ivk.getKey());
			KeyValues kvs = newValues.getValues(ivk.getKey());
			ivk.setValues(kvs.getValues());
		}

		if (keysToReplace.size() > 0) {
			throw new InvalidIdentifierValuesException("Key [" + keysToReplace.get(0) 
					+ "] does not exist for identifier [" + identifier.normalize().toString() 
					+ "]");
		}

		save(resolvedValues);

		// Save system identifier if necessary
		if (SecurityUtil.isSystemIdentifier(localIdentifier)) {
			replaceSystemValues(secInfo, resolvedValues);
		}
	}
	
	/*
	 * Adds administrator identity to system identifier
	 */
	public synchronized void createInitialAdministrator(String identity) 
		throws 
			NamingAuthorityConfigurationException {

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
	
	/*
	 * Private Stuff
	 */
		    
	/*
	 * A user can create identifiers if any one of the below 
	 * conditions are met:
	 *
	 *    (a) PUBLIC_CREATTION key set to "Y" in root identifier
	 *    (b) User is listed by IDENTIFIER_CREATION_USERS key in root identifier
	 *    (c) No security settings are specified
	 *       - No root identifier
	 *       - No PUBLIC_CREATION key in root identifier
	 */
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
			throw new NamingAuthoritySecurityException(SecurityUtil.securityError(secInfo, "create identifiers"));
		}	
	}
	
	/*
	 * Returns any directly specified READ_USERS plus any
	 * included by READWRITE_IDENTIFIERS
	 */
	private List<String> getAllReadUsers( IdentifierMetadata values ) 
		throws 
			InvalidIdentifierException,  
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
	
	private boolean hasIdentifierAdminUserAccess(SecurityInfo secInfo, IdentifierMetadata values) 
		throws 
			InvalidIdentifierException,  
			NamingAuthorityConfigurationException {

		if ( hasAdminUserAccess(secInfo, values) ||
				hasSystemAdminUserAccess(secInfo)) {
			return true;
		}
		
		return false;
	}
	
	// Checks if user is in ADMIN_USERS in system identifier
	private synchronized boolean hasSystemAdminUserAccess(SecurityInfo secInfo) {
		if (userAccess(secInfo.getUser(), SecurityUtil.getAdminUsers(systemValues))
				== Access.GRANTED) {
			return true;
		}
		return false;
	}
	
	//
	// Checks whether user is an ADMIN_USER
	//		
	private boolean hasAdminUserAccess(SecurityInfo secInfo, IdentifierMetadata values) 
		throws 
			InvalidIdentifierException, 
			NamingAuthorityConfigurationException {
		
		// Check locally defined ADMIN_USERS list
		Access access = userAccess(secInfo.getUser(), SecurityUtil.getAdminUsers(values));
		if (access == Access.GRANTED) {
			// No further checks needed
			return true;
		}
		
		// Check ADMIN_USERS defined by ADMIN_IDENTIFIERS
		Access rwAccess = userAccess(secInfo.getUser(), getAdminUsersFromAdminIdentifiers(values));
		if (rwAccess == Access.GRANTED) {
			return true;
		}
		
		return false;
	}
	
	/*
	 * Checks whether user has WRITE_USER permission.
	 * Default is "true" if there are no security settings
	 * (no WRITE_USERS key defined)
	 */		
	private boolean hasWriteUserAccess(SecurityInfo secInfo, IdentifierMetadata values) 
		throws 
			InvalidIdentifierException, 
			NamingAuthorityConfigurationException {
		
		// Check locally defined WRITE_USERS list
		Access access = userAccess(secInfo.getUser(), SecurityUtil.getWriteUsers(values));
		if (access == Access.GRANTED) {
			return true;
		}
		
		// Check WRITE_USERS defined by READWRITE_IDENTIFIERS
		Access rwAccess = userAccess(secInfo.getUser(), getWriteUsersFromReadWriteIdentifiers(values));
		if (rwAccess != Access.NOSECURITY) {
			access = rwAccess;
		}
		
		if (access == Access.DENIED) {
			return false;
		}
		
		// Access is granted in the absence of a WRITE_USERS key
		return true;
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
			NamingAuthorityConfigurationException {
		
		List<String> writers = null;
		
		List<String> rwIdentifiers = SecurityUtil.getReadWriteIdentifiers(values);
		if (rwIdentifiers != null) {
			for( String identifier : rwIdentifiers) {
				List<String> writeUsers = SecurityUtil.getWriteUsers(
						loadSecurityIdentifier(identifier));
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
			NamingAuthorityConfigurationException {
		
		List<String> readers = null;
		
		List<String> rwIdentifiers = SecurityUtil.getReadWriteIdentifiers(values);
		if (rwIdentifiers != null) {
			for( String identifier : rwIdentifiers) {
				List<String> readUsers = SecurityUtil.getReadUsers(
						loadSecurityIdentifier(identifier));
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
		throws 
			InvalidIdentifierException, 
			NamingAuthorityConfigurationException {
		
		List<String> allAdmins = null;
		
		List<String> adminIdentifiers = SecurityUtil.getAdminIdentifiers(values);
		if (adminIdentifiers != null) {
			for( String identifier : adminIdentifiers) {
				List<String> admins = SecurityUtil.getAdminUsers(
						loadSecurityIdentifier(identifier));
	
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
	private Access getKeyWriteAccess(SecurityInfo secInfo, URI rwIdentifier) 
		throws 
			InvalidIdentifierException, 
			NamingAuthorityConfigurationException {
		
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
	
	private IdentifierMetadata loadSecurityIdentifier( String identifier ) 
		throws 
			InvalidIdentifierException, 
			NamingAuthorityConfigurationException {
		
		try {
			return loadIdentifier(new URI(identifier));
		} catch (URISyntaxException e) {
			LOG.error(IdentifierUtil.getStackTrace(e));
			throw new NamingAuthorityConfigurationException("Referred security identifier is bad ["
					+ identifier + "]");
		}
	}
}
