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

import java.util.HashMap;
import java.util.Map;

public class IdentifierValues implements java.io.Serializable {
    private Map<String, KeyValues> values = new HashMap<String, KeyValues>();


    public Map<String, KeyValues> getValues() {
        return this.values;
    }


    public KeyValues getValues(String key) {
        return values.get(key);
    }


    public void setValues(Map<String, KeyValues> values) {
        this.values = values;
    }


    public String[] getKeys() {
        return values.keySet().toArray(new String[values.keySet().size()]);
    }


    public void put(String keyName, KeyValues data) {
    	this.values.put(keyName, data);
    }
    
    public boolean equals(Object obj) { 
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        IdentifierValues other = (IdentifierValues) obj;
        
        if (!this.values.keySet().equals(other.getValues().keySet())) {
            return false;
        }

        // keys (types) are the same, compare values now

        for (String type : this.getKeys()) {
            if (!this.getValues(type).equals(
            		other.getValues(type))) {
            	return false;
            }
        }
        return true;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();

        for (String key : getKeys()) {
        	KeyValues data = getValues(key);
            sb.append("=====================================================================\n");
            sb.append("KEY [" + key + "]\n");
            for (String value : data.getValues()) {
                sb.append("      VALUE [" + value + "]\n");
            }
        }
        sb.append("\n");

        return sb.toString();
    }
    
}
