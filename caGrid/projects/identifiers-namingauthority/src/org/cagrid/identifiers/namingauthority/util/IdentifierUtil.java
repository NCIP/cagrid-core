package org.cagrid.identifiers.namingauthority.util;

public class IdentifierUtil {

	public static String build( String prefix, String localName ) {
		if (prefix == null || prefix.length() == 0)
			return localName;
		
		if (prefix.endsWith("/"))
			return prefix + localName;
		
		return prefix + "/" + localName;
	}
	
	public static String getLocalName( String prefix, String identifier ) {
		if (identifier.startsWith(prefix)) {
			if ( prefix.length() == identifier.length() )
				return identifier;
			
			String local = identifier.substring(prefix.length());
			if (local.startsWith("/"))
				return local.substring(1);
			return local;
		}
		
		return identifier;
	}
}
