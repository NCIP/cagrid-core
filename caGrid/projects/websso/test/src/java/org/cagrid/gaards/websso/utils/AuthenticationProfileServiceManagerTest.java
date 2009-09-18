package org.cagrid.gaards.websso.utils;

import java.util.List;

import org.cagrid.gaards.websso.authentication.AuthenticationProfileServiceManager;
import org.cagrid.gaards.websso.beans.AuthenticationServiceInformation;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

public class AuthenticationProfileServiceManagerTest extends
		AbstractDependencyInjectionSpringContextTests {

	protected String[] getConfigLocations() {
		return new String[] { "classpath:websso-beans.xml" };
	}

	private AuthenticationProfileServiceManager servicesManager;

	public void setServicesManager(AuthenticationProfileServiceManager servicesManager) {
		this.servicesManager = servicesManager;
	}
	
	public void testSyncAuthenticationServices(){
		servicesManager.syncServices();
		List<DorianServiceHandle> dorians  =servicesManager.getDorianServices();
		for (DorianServiceHandle dorianHandle : dorians) {
			List<AuthenticationServiceHandle> authenticationServices=dorianHandle.getAuthenticationServices();
			for (AuthenticationServiceHandle authenticationServiceHandle : authenticationServices) {
				AuthenticationServiceInformation asi = authenticationServiceHandle.getAuthenticationServiceInformation();
				assertNotNull(asi.getAuthenticationServiceURL());
				assertNotNull(asi.getAuthenticationServiceName());
				assertNotNull(asi.getAuthenticationServiceProfiles());
			}
		}
	}
}
