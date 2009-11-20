package org.cagrid.identifiers.namingauthority.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.NonUniqueResultException;

import org.cagrid.identifiers.namingauthority.IdentifierValues;
import org.cagrid.identifiers.namingauthority.hibernate.IdentifierMetadata;
import org.cagrid.identifiers.namingauthority.hibernate.IdentifierValueKey;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

public class IdentifierMetadataDao extends AbstractDao<IdentifierMetadata> {

    @Override
    public Class<IdentifierMetadata> domainClass() {
        return IdentifierMetadata.class;
    }
    
    public IdentifierValues getIdentifierValues( java.net.URI localIdentifier ) {
//TODO: remove this junk    	
//    	IdentifierMetadata md = null;
//
//        List<IdentifierMetadata> identifiers = getHibernateTemplate().find(
//            "SELECT s FROM " + domainClass().getName() + " s WHERE s.localIdentifier = ?",
//            new Object[]{localIdentifier});
//
//        if (identifiers.size() > 1) {
//            throw new NonUniqueResultException("Found " + identifiers.size() + " " + domainClass().getSimpleName()
//                + " objects for identifier '" + localIdentifier + "'");
//        } else if (identifiers.size() == 1) {
//            md = identifiers.get(0);
//        }

  	
    	IdentifierMetadata template = new IdentifierMetadata();
    	template.setLocalIdentifier(localIdentifier);

    	IdentifierMetadata md = getByExample(template);
    	if (md == null) {
    		return null;
    	}
    	
    	materializeIdentifierMetadata(md);
    	
    	IdentifierValues result = new IdentifierValues();
    	
    	if (md.getValues() != null && md.getValues().size() > 0) {
    		Map<String, List<String>> values = new HashMap<String, List<String>>();
    		result.setValues(values);

    		for (IdentifierValueKey vk : md.getValues()) {
    			values.put(vk.getKey(), vk.getValues());
    		}
    	}
    	
    	return result;
    }
    
    public void materializeIdentifierMetadata(final IdentifierMetadata md) {
        getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                session.lock(md, LockMode.READ);
                Hibernate.initialize(md.getValues());
                for(IdentifierValueKey vk : md.getValues()) {
                	Hibernate.initialize(vk.getValues());
      			}
                return null;
            }
        });
    }
    
    
//TODO: junk available for later removal
    // public IdentifierValuesImpl getValues( String identifier ) {
    //        
    // Session session = dbFactory.getCurrentSession();
    // session.beginTransaction();
    // List<IdentifierValue> values = session.
    // createQuery("from IdentifierValue as iv where iv.name = :name").
    // setParameter("name", identifier).list();
    // session.getTransaction().commit();
    //        
    // if (values.size() == 0)
    // return null;
    //        
    // IdentifierValuesImpl ivs = new IdentifierValuesImpl();
    // for( int i=0; i < values.size(); i++ ) {
    // ivs.add(values.get(i).getType(), values.get(i).getData());
    // }
    //        
    // return ivs;
    // }

}
