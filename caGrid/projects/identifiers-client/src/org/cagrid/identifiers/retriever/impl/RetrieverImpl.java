package org.cagrid.identifiers.retriever.impl;

import org.cagrid.identifiers.namingauthority.domain.IdentifierData;
import org.cagrid.identifiers.namingauthority.domain.KeyData;
import org.cagrid.identifiers.namingauthority.domain.KeyValues;
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
    	if (requiredKeys == null) {
    		return new String[]{};
    	}
    	return this.requiredKeys;
    }

    protected void validateKeys(IdentifierData ivs) throws Exception {
        for (String key : requiredKeys) {
        	KeyData kd = ivs.getValues(key);
        	if (kd == null) {
        		throw new Exception("No key [" + key + "] found in indetifier values");
        	}
        	
        	if (kd.getValues() == null ||
        			kd.getValues().size() == 0) {
                throw new Exception("No key [" + key + "] found in identifier values");
        	}
        }
    }
    
    //
    // Interfaces
    //
    public abstract Object retrieve(IdentifierData ivs) throws Exception;
}
