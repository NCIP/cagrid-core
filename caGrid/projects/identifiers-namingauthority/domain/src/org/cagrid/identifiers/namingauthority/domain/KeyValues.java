/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.identifiers.namingauthority.domain;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KeyValues implements java.io.Serializable {

    private List<String> values;

    public KeyValues(){}
    
    public KeyValues( List<String> values) {
    	init(values);
    }
    
    public KeyValues( String[] values) {
    	if (values != null) {
    		init(Arrays.asList(values));
    	} else {
    		init(null);
    	}
    }
    
    protected void init(List<String> values) {
    	this.values = values;
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
   
    protected boolean compareValues(KeyValues other) {
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
    
        KeyValues other = (KeyValues) obj;
        
        return compareValues(other);
    }
}
