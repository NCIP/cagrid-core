package org.cagrid.identifiers.namingauthority;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class IdentifierValues implements java.io.Serializable {
    private Map<String, List<String>> values = new HashMap<String, List<String>>();


    public Map<String, List<String>> getValues() {
        return this.values;
    }


    public String[] getValues(String type) {
        return values.get(type).toArray(new String[values.get(type).size()]);
    }


    public void setValues(Map<String, List<String>> values) {
        this.values = values;
    }


    public String[] getTypes() {
        return values.keySet().toArray(new String[values.keySet().size()]);
    }


    public void add(String type, String data) {
        List<String> currValues = values.get(type);
        if (currValues == null) {
            currValues = new ArrayList<String>();
            values.put(type, currValues);
        }
        currValues.add(data);
    }


    public String toString() {
        StringBuffer sb = new StringBuffer();

        for (String type : getTypes()) {
            sb.append("=====================================================================\n");
            sb.append("TYPE [" + type + "]\n");
            for (String value : getValues(type)) {
                sb.append("      VALUE [" + value + "]\n");
            }
        }
        sb.append("\n");

        return sb.toString();
    }
}
