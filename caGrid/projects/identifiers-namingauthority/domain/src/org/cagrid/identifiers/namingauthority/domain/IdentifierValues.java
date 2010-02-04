package org.cagrid.identifiers.namingauthority.domain;

import java.util.HashMap;
import java.util.Map;

public class IdentifierValues implements java.io.Serializable {
    private Map<String, KeyData> values = new HashMap<String, KeyData>();


    public Map<String, KeyData> getValues() {
        return this.values;
    }


    public KeyData getValues(String key) {
        return values.get(key);
    }


    public void setValues(Map<String, KeyData> values) {
        this.values = values;
    }


    public String[] getKeys() {
        return values.keySet().toArray(new String[values.keySet().size()]);
    }


    public void put(String key, KeyData data) {
    	this.values.put(key, data);
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
        	KeyData data = getValues(key);
            sb.append("=====================================================================\n");
            sb.append("KEY [" + key + "]\n");
            sb.append("      RWIDENTIFIER [" + data.getReadWriteIdentifier() + "]\n");
            for (String value : data.getValues()) {
                sb.append("      VALUE [" + value + "]\n");
            }
        }
        sb.append("\n");

        return sb.toString();
    }
    
}
