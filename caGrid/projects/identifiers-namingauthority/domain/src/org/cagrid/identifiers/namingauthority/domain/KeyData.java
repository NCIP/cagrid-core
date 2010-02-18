package org.cagrid.identifiers.namingauthority.domain;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KeyData implements java.io.Serializable {
	private URI readWriteIdentifier;
    private List<String> values;

    public KeyData(){}
    
    public KeyData( URI identifier, List<String> values) {
    	init(identifier, values);
    }
    
    public KeyData( URI identifier, String[] values) {
    	if (values != null) {
    		init(identifier, Arrays.asList(values));
    	} else {
    		init(identifier, null);
    	}
    }
    
    private void init(URI identifier, List<String> values) {
    	this.readWriteIdentifier = identifier;
    	this.values = values;
    }
    
    public URI getReadWriteIdentifier() {
    	return this.readWriteIdentifier;
    }
    
    public void setReadWriteIdentifier(URI identifier) {
    	this.readWriteIdentifier = identifier;
    }
    
    public List<String> getValues() {
        return this.values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public void addValue(String value) {
        if (this.values == null) {
            this.values = new ArrayList<String>();
        }
        this.values.add(value);
    }
    
    public String[] getValuesAsArray() {
    	return this.values.toArray(new String[this.values.size()]);
    }
   
    private boolean compareRWIdentifier(KeyData other) {
    	if (this.readWriteIdentifier == null) {
        	if (other.readWriteIdentifier != null) {
        		return false;
        	}
        	// both null - proceed
        	return true;
        } else {
        	if (other.readWriteIdentifier == null) {
        		return false;
        	}
        	//both not null
        	return this.readWriteIdentifier.normalize().toString().equals(
        			other.readWriteIdentifier.normalize().toString());
        }
    }
    
    private boolean compareValues(KeyData other) {
    	if (this.values == null) {
        	if (other != null) {
        		return false;
        	}
        	// both null - proceed
        	return true;
        } else {
        	if (other == null) {
        		return false;
        	}
        }

    	String[] thisValues = this.getValuesAsArray();
        String[] otherValues = other.getValuesAsArray();

        Arrays.sort(thisValues);
        Arrays.sort(otherValues);

        return Arrays.equals(thisValues, otherValues);
    }
    
    public boolean equals(Object obj) { 
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        
        KeyData other = (KeyData) obj;
        
        if (!compareRWIdentifier(other)) {
        	return false;
        }
        
        return compareValues(other);
    }
}
