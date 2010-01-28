package org.cagrid.identifiers.retriever.impl;

import org.cagrid.identifiers.namingauthority.domain.IdentifierValues;
import org.cagrid.identifiers.namingauthority.domain.KeyData;
import org.cagrid.identifiers.retriever.Retriever;

public abstract class RetrieverImpl implements Retriever {

	private String[] requiredKeys;

	//
	// Getters/Setters
	//
    public void setRequiredKeys(String[] types) {
        this.requiredKeys = types;
    }
    
    public String[] getRequiredKeys() {
    	return this.requiredKeys;
    }

    protected void validateTypes(IdentifierValues ivs) throws Exception {
        for (String type : requiredKeys) {
        	KeyData kd = ivs.getValues(type);
        	if (kd == null) {
        		throw new Exception("No type [" + type + "] found in indetifier values");
        	}
            String[] values = kd.getValuesAsArray();
            if (values == null || values.length == 0)
                throw new Exception("No type [" + type + "] found in indetifier values");
        }
    }
    
    //
    // Interfaces
    //
    public abstract Object retrieve(IdentifierValues ivs) throws Exception;
}
