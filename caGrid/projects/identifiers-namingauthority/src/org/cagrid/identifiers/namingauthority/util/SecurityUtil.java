package org.cagrid.identifiers.namingauthority.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.cagrid.identifiers.namingauthority.InvalidIdentifierException;
import org.cagrid.identifiers.namingauthority.InvalidIdentifierValuesException;
import org.cagrid.identifiers.namingauthority.MaintainerNamingAuthority;
import org.cagrid.identifiers.namingauthority.NamingAuthorityConfigurationException;
import org.cagrid.identifiers.namingauthority.NamingAuthoritySecurityException;
import org.cagrid.identifiers.namingauthority.SecurityInfo;
import org.cagrid.identifiers.namingauthority.domain.IdentifierValues;
import org.cagrid.identifiers.namingauthority.domain.KeyData;
import org.cagrid.identifiers.namingauthority.hibernate.IdentifierMetadata;
import org.cagrid.identifiers.namingauthority.impl.NamingAuthorityImpl;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;

public class SecurityUtil {

	public static String ANONYMOUS_USER = "<anonymous>";
	public static String PUBLIC_CREATION_YES = "Y";
	public static String PUBLIC_CREATION_NO = "N";
	public static URI LOCAL_SYSTEM_IDENTIFIER;
	
	public enum Access { GRANTED, DENIED, NOSECURITY };
	
	static {
		try {
			LOCAL_SYSTEM_IDENTIFIER = new URI("0");
		} catch(Exception e){};
	}
	
	public static String securityError(SecurityInfo secInfo, String opErr) {
		return "User [" 
			+ secInfo.getUser() 
			+ "] is not authorized to " + opErr + ".";
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
	
	public static void addAdmin(String naConfigurationFile, 
			String naProperties, String adminUser) 
	
		throws 
			InvalidIdentifierException, 
			URISyntaxException, 
			NamingAuthorityConfigurationException, 
			NamingAuthoritySecurityException, InvalidIdentifierValuesException {

        FileSystemResource naConfResource = new FileSystemResource(naConfigurationFile);
        FileSystemResource naPropertiesResource = new FileSystemResource(naProperties);

        XmlBeanFactory factory = new XmlBeanFactory(naConfResource);
        PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
        cfg.setLocation(naPropertiesResource);
        cfg.postProcessBeanFactory(factory);
        
        NamingAuthorityImpl na = (NamingAuthorityImpl) factory.getBean("NamingAuthority", MaintainerNamingAuthority.class);
        na.getIdentifierDao().createInitialAdministrator(adminUser);
        
//        KeyData kd = na.getKeyData(null, na.getSystemIdentifier(), Keys.ADMIN_USERS);
//        if (kd == null) {
//        	System.err.println("KD IS NULL");
//        	kd = new KeyData();
//        }
//        
//        List<String> values = kd.getValues();
//        if (values == null) {
//        	System.err.println("VALUES IS NULL");
//        	values = new ArrayList<String>();
//        }
//        
//        if (values.contains(adminUser)) {
//        	throw new NamingAuthorityConfigurationException("Provided identity [" + adminUser + "] is already an administrator");
//        }
//        
//        values.add(adminUser);
//        
//        IdentifierValues ivalues = new IdentifierValues();
//        ivalues.put(Keys.ADMIN_USERS, kd);
//        na.replaceKeys(null, na.getSystemIdentifier(), ivalues);
	}
	
	private static void usage() {
		System.err.println(SecurityUtil.class.getName() + " Usage:");
		System.err.println();
		System.err.println("java " + SecurityUtil.class.getName() 
			+ " <NA Config File> <NA Properties File> <OPERATION> <OPERATION PARAMS>\n\n"
			+ " addAdmin <grid identity>");
	}
	
	public static void main(String[] args) {
		int index = 0;
		int NA_CONFIG = index++;
		int NA_PROPS = index++;
		int OPERATION = index++;
		int ADMIN_USER = index++;
		
		// OPERATIONS
		String ADD_ADMIN = "addAdmin";
		
		/*
		 * arg0: na configuration file (e.g. "WebContent/WEB-INF/applicationContext-na.xml")
		 * arg1: na properties file (e.g. "WebContent/WEB-INF/na.properties")
		 * arg2: operation (e.g., addAdmin)
		 * arg2: ADMIN_USER
		 */
				
		System.err.println("args[NA_CONFIG]=["+args[NA_CONFIG]+"]");
		System.err.println("args[NA_PROPS]=["+args[NA_PROPS]+"]");
		System.err.println("args[OPERATION]=["+args[OPERATION]+"]");
		System.err.println("args[ADMIN_USER]=["+args[ADMIN_USER]+"]");
		
		if (args.length != index) {
			usage();
			System.exit(1);
		}
		
		try {
			if (args[OPERATION].equals(ADD_ADMIN)) {
				addAdmin(args[NA_CONFIG], args[NA_PROPS], args[ADMIN_USER]);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static boolean isSystemIdentifier(URI localIdentifier) {
		return localIdentifier.normalize().toString().equals(LOCAL_SYSTEM_IDENTIFIER.normalize().toString());
	}
}

