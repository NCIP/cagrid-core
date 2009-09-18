package org.cagrid.identifiers.retriever;

import org.cagrid.identifiers.namingauthority.impl.IdentifierValuesImpl;

public abstract class Retriever {
	private String[] requiredTypes;
	
	public abstract Object retrieve( IdentifierValuesImpl ivs ) throws Exception;
	
	public String[] getRequiredTypes() {
		return this.requiredTypes;
	}
	
	public void setRequiredTypes( String[] types ) {
		this.requiredTypes = types;
	}
	
	protected void validateTypes( IdentifierValuesImpl ivs ) throws Exception {
		for( String type : requiredTypes ) {
			String[] values = ivs.getValues( type );
			if (values == null || values.length == 0)
				throw new Exception("No type ["+ type +"] found in indetifier values");
		}
	}
}
