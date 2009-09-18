package org.cagrid.identifiers.namingauthority.impl;

import java.util.ArrayList;
import java.util.HashMap;

import org.cagrid.identifiers.namingauthority.IdentifierValues;

public class IdentifierValuesImpl implements IdentifierValues, java.io.Serializable {
	private HashMap<String, ArrayList<String>> values =
		new HashMap<String, ArrayList<String>>();
	
	public HashMap<String, ArrayList<String>> getValues() {
		return this.values;
	}
	
	public String[] getValues( String type ) {
		return values.get(type).toArray( new String[ values.get(type).size() ]);
	}
	
	public void setValues( HashMap<String, ArrayList<String>> values ) {
		this.values = values;
	}
	
	public String[] getTypes() {
		return values.keySet().toArray(new String[ values.keySet().size() ]);
	}

	public void add(String type, String data) {
		ArrayList<String> currValues = values.get(type);
		if (currValues == null) {
			currValues = new ArrayList<String>();
			values.put(type, currValues);
		}
		currValues.add(data);
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		for( String type : getTypes()) {
			sb.append("=====================================================================\n");
			sb.append("TYPE [" + type + "]\n");
			for( String value : getValues(type) ) {
				sb.append("      VALUE [" + value + "]\n");
			}
		}
		sb.append("\n");
		
		return sb.toString();
	}
}
