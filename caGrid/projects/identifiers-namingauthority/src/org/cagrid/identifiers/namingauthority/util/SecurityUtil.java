package org.cagrid.identifiers.namingauthority.util;

import org.springframework.core.io.FileSystemResource;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.cagrid.identifiers.namingauthority.InvalidIdentifierException;
import org.cagrid.identifiers.namingauthority.MaintainerNamingAuthority;
import org.cagrid.identifiers.namingauthority.NamingAuthorityConfigurationException;
import org.cagrid.identifiers.namingauthority.NamingAuthoritySecurityException;
import org.cagrid.identifiers.namingauthority.SecurityInfo;
import org.cagrid.identifiers.namingauthority.dao.IdentifierMetadataDao;
import org.cagrid.identifiers.namingauthority.domain.IdentifierValues;
import org.cagrid.identifiers.namingauthority.domain.KeyData;
import org.cagrid.identifiers.namingauthority.hibernate.IdentifierMetadata;
import org.cagrid.identifiers.namingauthority.impl.NamingAuthorityImpl;

public class SecurityUtil {

	//public static String ANONYMOUS_USER = "<anonymous>";
	public static URI SYSTEM_IDENTIFIER;
	
	public enum Access { GRANTED, DENIED, NOSECURITY };
	
	static {
		try {
			SYSTEM_IDENTIFIER = new URI("0");
		} catch(Exception e){};
	}
	
	public static String securityError(SecurityInfo secInfo, String opErr) {
		return "User [" 
			+ secInfo.getUser() 
			+ "] is not authorized ["
			+ opErr
			+ "]";
	}
	
	public static List<String> getReadUsers(IdentifierMetadata values) {
		return IdentifierUtil.getKeyValues(values, Keys.READ_USERS);
	}
	
	public static List<String> getAdminUsers(IdentifierMetadata values) {
		return IdentifierUtil.getKeyValues(values, Keys.ADMIN_USERS);
	}
	
	public static List<String> getWriteUsers(IdentifierMetadata values) {
		return IdentifierUtil.getKeyValues(values, Keys.WRITE_USERS);
	}
	
	public static List<String> getReadWriteIdentifiers(IdentifierMetadata values) {
		return IdentifierUtil.getKeyValues(values, Keys.READWRITE_IDENTIFIERS);
	}
	
	public static List<String> getAdminIdentifiers(IdentifierMetadata values) {
		return IdentifierUtil.getKeyValues(values, Keys.ADMIN_IDENTIFIERS);
	}
	
	public static List<String> getIdentifierCreationUsers(IdentifierMetadata values) {
		return IdentifierUtil.getKeyValues(values, Keys.IDENTIFIER_CREATION_USERS);
	}
	
	public static List<String> getPublicCreation(IdentifierMetadata values) {
		return IdentifierUtil.getKeyValues(values, Keys.PUBLIC_CREATION);
	}
	
	public static void createSystemIdentifier(String naConfigurationFile, 
			String naProperties, String[] adminUsers, String[] creationUsers,
			String creationFlag) 
	
		throws 
			InvalidIdentifierException, 
			URISyntaxException, 
			NamingAuthorityConfigurationException, 
			NamingAuthoritySecurityException {

        FileSystemResource naConfResource = new FileSystemResource(naConfigurationFile);
        FileSystemResource naPropertiesResource = new FileSystemResource(naProperties);

        XmlBeanFactory factory = new XmlBeanFactory(naConfResource);
        PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
        cfg.setLocation(naPropertiesResource);
        cfg.postProcessBeanFactory(factory);
             
        String dbFlag = "N";
        if (creationFlag.equalsIgnoreCase("y") || creationFlag.equalsIgnoreCase("true")) {
        	dbFlag = "Y";
        }
                
        IdentifierValues values = new IdentifierValues();
  
        if (adminUsers != null) {
        	values.put(Keys.ADMIN_USERS, new KeyData(null, adminUsers));
        }
        
        if (creationUsers != null) {
        	values.put(Keys.IDENTIFIER_CREATION_USERS, new KeyData(null, creationUsers));
        }
        
        values.put(Keys.PUBLIC_CREATION, new KeyData(null, new String[]{dbFlag}));
        
        NamingAuthorityImpl na = (NamingAuthorityImpl) factory.getBean("NamingAuthority", MaintainerNamingAuthority.class);
        na.getIdentifierDao().createIdentifier( null, SYSTEM_IDENTIFIER, values );
	}
	
	private static void usage() {
		System.err.println(SecurityUtil.class.getName() + " Usage:");
		System.err.println();
		System.err.println("java " + SecurityUtil.class.getName() 
			+ " <NA Config File> <NA Properties File> <ADMIN_USERS> <IDENTIFIER_CREATION_USERS> <PUBLIC_CREATION>");
	}
	
	public static void main(String[] args) {
		int index = 0;
		int NA_CONFIG = index++;
		int NA_PROPS = index++;
		int ADMIN_USERS = index++;
		int IDENTIFIER_CREATION_USERS = index++;
		int PUBLIC_CREATION = index++;
		
		/*
		 * arg0: na configuration file (e.g. "WebContent/WEB-INF/applicationContext-na.xml")
		 * arg1: na properties file (e.g. "WebContent/WEB-INF/na.properties")
		 * arg2: ADMIN_USERS (comma separated list of grid identifiers)
		 * arg3: IDENTIFIER_CREATION_USERS (comma separated list of grid identities)
		 * arg4: PUBLIC_CREATION (y/n)
		 */
				
		System.err.println("args[NA_CONFIG]=["+args[NA_CONFIG]+"]");
		System.err.println("args[NA_PROPS]=["+args[NA_PROPS]+"]");
		System.err.println("args[ADMIN_USERS]=["+args[ADMIN_USERS]+"]");
		System.err.println("args[IDENTIFIER_CREATION_USERS]=["+args[IDENTIFIER_CREATION_USERS]+"]");
		System.err.println("args[PUBLIC_CREATION]=["+args[PUBLIC_CREATION]+"]");
		
		if (args.length != index) {
			usage();
			System.exit(1);
		}
		
		String[] adminUsers = null;
		if (!args[ADMIN_USERS].equals("-") && args[ADMIN_USERS].length() > 0) {
			adminUsers = args[ADMIN_USERS].split(",");
		}
		
		String[] creationUsers = null;
		if (!args[IDENTIFIER_CREATION_USERS].equals("-") && args[IDENTIFIER_CREATION_USERS].length() > 0) {
			creationUsers = args[IDENTIFIER_CREATION_USERS].split(",");
		}
		
		try {
			createSystemIdentifier(args[NA_CONFIG], args[NA_PROPS], adminUsers, creationUsers, args[PUBLIC_CREATION]);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}

