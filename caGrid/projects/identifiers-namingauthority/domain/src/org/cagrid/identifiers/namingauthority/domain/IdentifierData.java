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

public class IdentifierData implements java.io.Serializable {
    
	private Map<String, KeyData> data = new HashMap<String, KeyData>();

    public Map<String, KeyData> getData() {
        return this.data;
    }

    public KeyData getValues(String key) {
        return data.get(key);
    }

    public void setValues(Map<String, KeyData> data) {
        this.data = data;
    }

    public String[] getKeys() {
    	return data.keySet().toArray(new String[data.keySet().size()]);
    }

    public void put(String keyName, KeyData data) {
    	this.data.put(keyName, data);
    }
    
    public boolean equals(Object obj) { 
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        IdentifierData other = (IdentifierData) obj;
        
        if (!this.data.keySet().equals(other.getData().keySet())) {
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
        	KeyData data = getValues(key);
            sb.append("=====================================================================\n");
            sb.append("KEY [" + key + "]\n");
            sb.append("      POLICY IDENTIFIER [" + data.getPolicyIdentifier() + "]\n");
            if (data.getValues() != null) {
            	for (String value : data.getValues()) {
            		sb.append("      VALUE [" + value + "]\n");
            	}
            }
        }
        sb.append("\n");

        return sb.toString();
    }
    
}
