package org.cagrid.identifiers.namingauthority;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cagrid.identifiers.namingauthority.hibernate.IdentifierValueKey;


public class IdentifierValues implements java.io.Serializable {
    private Map<String, List<String>> values = new HashMap<String, List<String>>();


    public Map<String, List<String>> getValues() {
        return this.values;
    }


    public String[] getValues(String key) {
        return values.get(key).toArray(new String[values.get(key).size()]);
    }


    public void setValues(Map<String, List<String>> values) {
        this.values = values;
    }


    public String[] getKeys() {
        return values.keySet().toArray(new String[values.keySet().size()]);
    }


    public void add(String key, String data) {
        List<String> currValues = values.get(key);
        if (currValues == null) {
            currValues = new ArrayList<String>();
            values.put(key, currValues);
        }
        currValues.add(data);
    }
    
    public boolean equals(Object obj) { 
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        IdentifierValues other = (IdentifierValues) obj;
        if (values == null) {
            if (other.values != null)
                return false;
        } 
        
        if (!this.values.keySet().equals(other.getValues().keySet())) {
            return false;
        }

        // keys (types) are the same, compare values now

        for (String type : this.getKeys()) {
            String[] thisValues = this.getValues(type);
            String[] otherValues = other.getValues(type);

            Arrays.sort(thisValues);
            Arrays.sort(otherValues);

            if (!Arrays.equals(thisValues, otherValues)) {
                return false;
            }
        }
        return true;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();

        for (String key : getKeys()) {
            sb.append("=====================================================================\n");
            sb.append("KEY [" + key + "]\n");
            for (String value : getValues(key)) {
                sb.append("      VALUE [" + value + "]\n");
            }
        }
        sb.append("\n");

        return sb.toString();
    }
    
}
