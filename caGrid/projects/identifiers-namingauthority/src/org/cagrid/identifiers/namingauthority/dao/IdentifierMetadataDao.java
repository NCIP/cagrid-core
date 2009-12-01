package org.cagrid.identifiers.namingauthority.dao;

import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.NonUniqueResultException;

import org.cagrid.identifiers.namingauthority.InvalidIdentifierException;
import org.cagrid.identifiers.namingauthority.domain.IdentifierValues;
import org.cagrid.identifiers.namingauthority.hibernate.IdentifierMetadata;
import org.cagrid.identifiers.namingauthority.hibernate.IdentifierValueKey;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.springframework.orm.hibernate3.HibernateCallback;

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

	public void saveIdentifierValues(URI identifier, IdentifierValues ivalues) {
        IdentifierMetadata md = new IdentifierMetadata();
        md.setLocalIdentifier(identifier);
        List<IdentifierValueKey> values = new ArrayList<IdentifierValueKey>();
        md.setValues(values);

        if (ivalues != null) {
            String[] keys = ivalues.getKeys();
            for (String key : keys) {
                IdentifierValueKey vk = new IdentifierValueKey();
                vk.setKey(key);
                String[] data = ivalues.getValues(key);
                vk.setValues(Arrays.asList(data));
                values.add(vk);
            }
        }

        save(md);
	}
}
