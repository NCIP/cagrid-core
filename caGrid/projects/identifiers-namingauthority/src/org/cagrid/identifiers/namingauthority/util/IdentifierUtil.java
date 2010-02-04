package org.cagrid.identifiers.namingauthority.util;

import java.net.URI;

import org.cagrid.identifiers.namingauthority.InvalidIdentifierException;
import org.cagrid.identifiers.namingauthority.NamingAuthorityConfigurationException;


public class IdentifierUtil {

    public static URI build(URI prefix, URI localName) throws NamingAuthorityConfigurationException {
    	try {
    		verifyPrefix(prefix);
    	} catch(Exception e) {
        	throw new NamingAuthorityConfigurationException(e.getMessage());
        }
    	
        if (localName == null) {
            throw new IllegalArgumentException("Localname must not be null.");
        } else if (localName.isAbsolute()) {
            throw new IllegalArgumentException("Localname must be a relative URI.");

        }

        // trim off any leading / so the URI resolving doesn't treat it as an
        // absolute path
        if (localName.getPath().startsWith("/")) {
            localName = URI.create(localName.getPath().substring(1));
        }

        return prefix.resolve(localName);
    }

    public static URI getLocalName(URI prefix, URI identifier) throws InvalidIdentifierException, NamingAuthorityConfigurationException {
        try {
        	verifyPrefix(prefix);
        } catch(Exception e) {
        	throw new NamingAuthorityConfigurationException(e.getMessage());
        }
 
        String idStr = identifier.normalize().toString();
        String prefixStr = prefix.normalize().toString();
        if (!idStr.startsWith(prefixStr) || prefixStr.length() >= idStr.length()) {
            throw new InvalidIdentifierException("Identifier (" + identifier + ") is not local to prefix (" + prefix
                + ").");
        }

        return prefix.relativize(identifier);
    }

    public static void verifyPrefix(URI prefix) throws IllegalArgumentException {
        if (prefix == null) {
            throw new IllegalArgumentException("Prefix must not be null.");
        } else if (!prefix.isAbsolute()) {
            throw new IllegalArgumentException("Prefix must be an absolute URI : " + prefix);
        } else if (prefix.getFragment() != null) {
            throw new IllegalArgumentException("Prefix must not contain a fragment: " + prefix);
        } else if (prefix.getQuery() != null) {
            throw new IllegalArgumentException("Prefix must not contain a query: " + prefix);
        } else if (!prefix.getPath().endsWith("/")) {
            throw new IllegalArgumentException("Prefix must have a trailing slash: " + prefix);
        }
    }
}
