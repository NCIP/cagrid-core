package org.cagrid.identifiers.namingauthority.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cagrid.identifiers.namingauthority.IdentifierValues;
import org.cagrid.identifiers.namingauthority.InvalidIdentifierException;
import org.cagrid.identifiers.namingauthority.hibernate.IdentifierMetadata;
import org.cagrid.identifiers.namingauthority.hibernate.IdentifierValueKey;

public class IdentifierMetadataDao extends AbstractDao<IdentifierMetadata> {

    @Override
    public Class<IdentifierMetadata> domainClass() {
        return IdentifierMetadata.class;
    }
        
    public IdentifierValues getIdentifierValues( java.net.URI localIdentifier ) throws InvalidIdentifierException {
    	
    	IdentifierMetadata template = new IdentifierMetadata();
    	template.setLocalIdentifier(localIdentifier);
    	
    	IdentifierMetadata md = getByExample(template);
   
    	if (md == null) {
    		throw new InvalidIdentifierException("The specified local identifier (" + localIdentifier + ") was not found.");
    	}
    	
    	IdentifierValues result = null;
    	
    	if (md.getValues() != null && md.getValues().size() > 0) {
    		result = new IdentifierValues();
    		Map<String, List<String>> values = new HashMap<String, List<String>>();
    		result.setValues(values);

    		for (IdentifierValueKey vk : md.getValues()) {
    			values.put(vk.getKey(), vk.getValues());
    		}
    	}
    	
    	return result;
    }
}
