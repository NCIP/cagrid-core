package gov.nih.nci.cagrid.identifiers.common;

import java.util.Set;

import org.cagrid.identifiers.namingauthority.impl.IdentifierValuesImpl;

import gov.nih.nci.cagrid.identifiers.*;

public class MappingUtil {

	public static IdentifierValuesImpl toIdentifierValues(TypeValuesMap typeValues) {
		if (typeValues == null)
			return null;
		
		IdentifierValuesImpl ivs = new IdentifierValuesImpl();
		
		for( TypeValues tv : typeValues.getTypeValues() ) {
			if (tv.getValues() != null) {
				for( String value : tv.getValues().getValue() ) {
					ivs.add( tv.getType(), value);
				}
			}
		}
		
		return ivs;
	}

	public static TypeValuesMap toTypeValuesMap(IdentifierValuesImpl values) {
		String[] types = values.getTypes();
		TypeValues[] tvs = new TypeValues[ types.length ];
		
		for( int i=0; i < tvs.length; i++) {
			tvs[i] = new TypeValues();
			tvs[i].setType(types[i]);
			Values newValues = new Values();
			newValues.setValue(values.getValues(types[i]));
			tvs[i].setValues(newValues);
		}
				
		TypeValuesMap tvm = new TypeValuesMap();
		tvm.setTypeValues(tvs);
		
		return tvm;
	}
}
