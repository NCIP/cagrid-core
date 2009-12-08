package gov.nih.nci.cagrid.identifiers.common;

import org.cagrid.identifiers.namingauthority.InvalidIdentifierException;
import org.cagrid.identifiers.namingauthority.InvalidIdentifierValuesException;
import org.cagrid.identifiers.namingauthority.NamingAuthorityConfigurationException;

import gov.nih.nci.cagrid.identifiers.stubs.types.InvalidIdentifierFault;
import gov.nih.nci.cagrid.identifiers.stubs.types.InvalidIdentifierValuesFault;
import gov.nih.nci.cagrid.identifiers.stubs.types.NamingAuthorityConfigurationFault;

public class MappingUtil {

	public static org.cagrid.identifiers.namingauthority.domain.IdentifierValues map(
			namingauthority.IdentifierValues identifierValues) {
		
		if (identifierValues == null)
			return null;
		
		org.cagrid.identifiers.namingauthority.domain.IdentifierValues ivs = 
			new org.cagrid.identifiers.namingauthority.domain.IdentifierValues();
		
		for( namingauthority.KeyValues kvs : identifierValues.getKeyValues() ) {
			ivs.set( kvs.getKey(), kvs.getValue() );
		}
		
		return ivs;
	}

	public static namingauthority.IdentifierValues map(
			org.cagrid.identifiers.namingauthority.domain.IdentifierValues identifierValues) {
		
		String[] keys = identifierValues.getKeys();
		namingauthority.KeyValues[] kvs = new namingauthority.KeyValues[ keys.length ];
		
		for( int i=0; i < kvs.length; i++) {
			kvs[i] = new namingauthority.KeyValues();
			kvs[i].setKey(keys[i]);
			kvs[i].setValue(identifierValues.getValues(keys[i]));
		}
				
		return new namingauthority.IdentifierValues( kvs );
	}
	
	public static InvalidIdentifierFault map(InvalidIdentifierException e) {
		InvalidIdentifierFault out = new InvalidIdentifierFault();
		out.setFaultString(e.getMessage());
		return out;
	}

	public static NamingAuthorityConfigurationFault map(NamingAuthorityConfigurationException e) {
		NamingAuthorityConfigurationFault out = new NamingAuthorityConfigurationFault();
		out.setFaultString(e.getMessage());
		return out;
	}

	public static InvalidIdentifierValuesFault map(InvalidIdentifierValuesException e) {
		InvalidIdentifierValuesFault out = new InvalidIdentifierValuesFault();
		out.setFaultString(e.getMessage());
		return out;
	}
}
