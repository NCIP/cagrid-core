package gov.nih.nci.cagrid.identifiers.common;

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
}
