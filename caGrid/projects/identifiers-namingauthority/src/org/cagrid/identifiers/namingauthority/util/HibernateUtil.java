package org.cagrid.identifiers.namingauthority.util;

import org.hibernate.*;
import org.hibernate.cfg.*;

public class HibernateUtil {

	private static SessionFactory sessionFactory = null;
	
	private HibernateUtil(){}
	
	public static synchronized SessionFactory initFactory( String dbUrl, String dbUser, String dbPassword ) {
		if (sessionFactory != null)
			return sessionFactory;
		
		try {
			// Create the SessionFactory from hibernate.cfg.xml
			Configuration conf = new Configuration();
			conf.configure("org/cagrid/identifiers/namingauthority/hibernate/identifiers.hibernate.cfg.xml");
			
			if (dbUrl != null)
				conf.setProperty("hibernate.connection.url", dbUrl);
			
			if (dbUser != null)
				conf.setProperty("hibernate.connection.username", dbUser);
			
			if (dbPassword != null)
				conf.setProperty("hibernate.connection.password", dbPassword);
			
			sessionFactory = conf.buildSessionFactory();
		} catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
		
		return sessionFactory;
		
	}

	public static synchronized SessionFactory getSessionFactory() {
		return sessionFactory;
	}

}
