package org.cagrid.identifiers.namingauthority.dao;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.NonUniqueResultException;

import org.cagrid.identifiers.namingauthority.InvalidIdentifierException;
import org.cagrid.identifiers.namingauthority.domain.IdentifierValues;
import org.cagrid.identifiers.namingauthority.domain.KeyData;
import org.cagrid.identifiers.namingauthority.hibernate.IdentifierMetadata;
import org.cagrid.identifiers.namingauthority.hibernate.IdentifierValueKey;

public class IdentifierMetadataDao extends AbstractDao<IdentifierMetadata> {

    @Override
    public Class<IdentifierMetadata> domainClass() {
        return IdentifierMetadata.class;
    }
        
    protected IdentifierMetadata loadIdentifier( final URI localIdentifier ) {
    	List<IdentifierMetadata> results = getHibernateTemplate().find(
                "SELECT md FROM " + domainClass().getName() + " md WHERE md.localIdentifier = ?",
                new Object[]{localIdentifier});
    	
    	IdentifierMetadata result = null;
    	
    	if (results.size() > 1) {
            throw new NonUniqueResultException("Found " + results.size() + " " + domainClass().getName()
                + " objects.");
        } else if (results.size() == 1) {
            result = results.get(0);
        }
        return result;
    }
    
    public IdentifierValues getIdentifierValues( java.net.URI localIdentifier ) throws InvalidIdentifierException {
    	
    	IdentifierMetadata md = loadIdentifier( localIdentifier );   
    	if (md == null) {
    		throw new InvalidIdentifierException("Local identifier (" + localIdentifier + ") does not exist");
    	}
    	
    	IdentifierValues result = null;
    	
    	if (md.getValues() != null && md.getValues().size() > 0) {
    		result = new IdentifierValues();
    		Map<String, KeyData> values = new HashMap<String, KeyData>();
    		result.setValues(values);

    		for (IdentifierValueKey vk : md.getValues()) {
    			KeyData kd = new KeyData();
    			kd.setReadWriteIdentifier(vk.getReadWriteIdentifier());
    			kd.setValues(vk.getValues());
    			values.put(vk.getKey(), kd);
    		}
    	}
    	
    	return result;
    }

	public void saveIdentifierValues(URI identifier, IdentifierValues ivalues) {
        IdentifierMetadata md = new IdentifierMetadata();
        md.setLocalIdentifier(identifier);
        List<IdentifierValueKey> values = new ArrayList<IdentifierValueKey>();
        md.setValues(values);

        if (ivalues != null) {
            String[] keys = ivalues.getKeys();
            for (String key : keys) {
                IdentifierValueKey vk = new IdentifierValueKey();
                
                KeyData kd = ivalues.getValues(key);
                vk.setKey(key);
                vk.setReadWriteIdentifier(kd.getReadWriteIdentifier());
                vk.setValues(kd.getValues());
                values.add(vk);
            }
        }

        save(md);
	}
}
