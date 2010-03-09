package org.cagrid.identifiers.namingauthority.domain;

import java.net.URI;
import java.util.List;


public class KeyData extends KeyValues implements java.io.Serializable {

	private URI policyIdentifier;

    public KeyData(){}
    
    public KeyData(URI identifier, String[] values) {
    	super(values);
    	this.policyIdentifier = identifier;
    }
    
    public KeyData( URI identifier, List<String> values) {
    	super(values);
    	this.policyIdentifier = identifier;
    }
    
    public URI getPolicyIdentifier() {
    	return this.policyIdentifier;
    }
    
    public void setPolicyIdentifier(URI identifier) {
    	this.policyIdentifier = identifier;
    }
    
    private boolean comparePolicyReference(KeyData other) {
    	if (this.policyIdentifier == null) {
        	if (other.policyIdentifier != null) {
        		return false;
        	}
        	// both null - proceed
        	return true;
        } else {
        	if (other.policyIdentifier == null) {
        		return false;
        	}
        	//both not null
        	return this.policyIdentifier.normalize().toString().equals(
        			other.policyIdentifier.normalize().toString());
        }
    }
    
    public boolean equals(Object obj) { 
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        
        KeyData other = (KeyData) obj;
        
        if (!comparePolicyReference(other)) {
        	return false;
        }
        
        return super.equals(obj);
    }
}
