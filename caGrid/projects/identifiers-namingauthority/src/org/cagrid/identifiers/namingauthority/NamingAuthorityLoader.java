package org.cagrid.identifiers.namingauthority;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class NamingAuthorityLoader {
	private ApplicationContext appCtx;
	private NamingAuthority namingAuthority;
	
	public NamingAuthorityLoader() {
		init( new String[] { 
				"/resources/spring/identifiers-namingauthority-context.xml"},
			"NamingAuthority");
	}
	
	public NamingAuthorityLoader( String[] contextList, String authorityName ) {
		init( contextList, authorityName );
	}
	
	private void init( String[] contextList, String authorityName ) {
		appCtx = new ClassPathXmlApplicationContext( contextList );
		namingAuthority = (NamingAuthority) appCtx.getBean( authorityName );
	}
	
	public NamingAuthority getNamingAuthority() {
		return namingAuthority;
	}
}
