package org.cagrid.identifiers.namingauthority.util;

import java.util.List;



import org.cagrid.identifiers.namingauthority.hibernate.IdentifierValue;
import org.cagrid.identifiers.namingauthority.impl.IdentifierValuesImpl;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class Database {

	private String dbUrl;
	private String dbUser;
	private String dbPassword;
	
	private SessionFactory dbFactory = null;
	
	public void initialize() {
		dbFactory = HibernateUtil.initFactory(dbUrl, dbUser, dbPassword);
	}
			
	public String getDbUrl() {
		return dbUrl;
	}
	
	public void setDbUrl( String dbUrl ) {
		this.dbUrl = dbUrl;
	}
	
	public String getDbUser() {
		return dbUser;
	}
	
	public void setDbUser( String dbUser ) {
		this.dbUser = dbUser;
	}
	
	public String getDbPassword() {
		return dbPassword;
	}
	
	public void setDbPassword( String dbPassword ) {
		this.dbPassword = dbPassword;
	}
	
	public void save( String identifier, IdentifierValuesImpl values ) {
			
        Session session = dbFactory.getCurrentSession();
        session.beginTransaction();

        for( String type : values.getTypes()) {
        	for( String value : values.getValues(type) ) {
        		IdentifierValue iv = new IdentifierValue();
        		iv.setName( identifier );
        		iv.setData( value );
        		iv.setType(type);
         		
            	session.save(iv);
        	}
        }
      
        session.getTransaction().commit();
	}
	
	public IdentifierValuesImpl getValues( String identifier ) {
		
		Session session = dbFactory.getCurrentSession();
		session.beginTransaction();
		List<IdentifierValue> values = session.
			createQuery("from IdentifierValue as iv where iv.name = :name").
				setParameter("name", identifier).list();
		session.getTransaction().commit();
		
		if (values.size() == 0)
			return null;
		
		IdentifierValuesImpl ivs = new IdentifierValuesImpl();
		for( int i=0; i < values.size(); i++ ) {
			ivs.add(values.get(i).getType(), values.get(i).getData());
		}
		
		return ivs;
	}
}
