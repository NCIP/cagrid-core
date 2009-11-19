package gov.nih.nci.cagrid.identifiers.common;

import gov.nih.nci.cagrid.identifiers.KeyValues;
import gov.nih.nci.cagrid.identifiers.KeyValuesMap;
import gov.nih.nci.cagrid.identifiers.Values;

import org.cagrid.identifiers.namingauthority.IdentifierValues;

public class MappingUtil {

	public static IdentifierValues toIdentifierValues(KeyValuesMap keyValues) {
		if (keyValues == null)
			return null;
		
		IdentifierValues ivs = new IdentifierValues();
		
		for( KeyValues tv : keyValues.getKeyValues() ) {
			if (tv.getValues() != null) {
				for( String value : tv.getValues().getValue() ) {
					ivs.add( tv.getKey(), value);
				}
			}
		}
		
		return ivs;
	}

	public static KeyValuesMap toKeyValuesMap(IdentifierValues values) {
		String[] keys = values.getKeys();
		KeyValues[] tvs = new KeyValues[ keys.length ];
		
		for( int i=0; i < tvs.length; i++) {
			tvs[i] = new KeyValues();
			tvs[i].setKey(keys[i]);
			Values newValues = new Values();
			newValues.setValue(values.getValues(keys[i]));
			tvs[i].setValues(newValues);
		}
				
		KeyValuesMap tvm = new KeyValuesMap();
		tvm.setKeyValues(tvs);
		
		return tvm;
	}
}
