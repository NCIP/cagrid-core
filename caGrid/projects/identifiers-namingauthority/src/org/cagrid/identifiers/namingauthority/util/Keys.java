package org.cagrid.identifiers.namingauthority.util;

import java.util.ArrayList;
import java.util.List;

//
// Key names reserved by the naming authority
//

public class Keys {

	public static String NS = "org.cagrid.identifiers.namingauthority.util";
	
	//
	// Security Keys
	//
	public static String IDENTIFIER_CREATION_USERS = NS + ".IDENTIFIER_CREATION_USERS";
	public static String ADMIN_USERS = NS + ".ADMIN_USERS";
	public static String PUBLIC_CREATION = NS + ".PUBLIC_CREATION";
	public static String ADMIN_IDENTIFIERS = NS + ".ADMIN_IDENTIFIERS";
	public static String READ_USERS = NS + ".READ_USERS";
	public static String WRITE_USERS = NS + ".WRITE_USERS";
	public static String READWRITE_IDENTIFIERS = NS + ".READWRITE_IDENTIFIERS";
	
	public static List<String> ALL_ADMIN_KEYS;
	
	static {
		ALL_ADMIN_KEYS = new ArrayList<String>();

		ALL_ADMIN_KEYS.add(IDENTIFIER_CREATION_USERS);
		ALL_ADMIN_KEYS.add(ADMIN_USERS);
		ALL_ADMIN_KEYS.add(PUBLIC_CREATION);
		ALL_ADMIN_KEYS.add(ADMIN_IDENTIFIERS);
		ALL_ADMIN_KEYS.add(READ_USERS);
		ALL_ADMIN_KEYS.add(WRITE_USERS);
		ALL_ADMIN_KEYS.add(READWRITE_IDENTIFIERS);
	};
	
	public static boolean isAdminKey(String keyName) {
		return ALL_ADMIN_KEYS.contains(keyName);
	}
}

