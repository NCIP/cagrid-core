package org.cagrid.gaards.websso.utils;

import java.util.ArrayList;
import java.util.List;

import org.cagrid.gaards.websso.authentication.AuthenticationProfileServiceManager;
import org.cagrid.gaards.websso.beans.AuthenticationServiceInformation;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

public class AuthenticationProfileServiceManagerTest extends
		AbstractDependencyInjectionSpringContextTests {

	protected String[] getConfigLocations() {
		return new String[] { "classpath:test-websso-beans.xml","WEB-INF/websso-beans.xml" };
	}

	private AuthenticationProfileServiceManager servicesManager;

	public void setServicesManager(AuthenticationProfileServiceManager servicesManager) {
		this.servicesManager = servicesManager;
	}
	
	public void testSyncAuthenticationServices(){
		servicesManager.syncServices();
		List<DorianServiceHandle> dorians =servicesManager.getDorianServices();
		for (DorianServiceHandle dorianHandle : dorians) {
			List<AuthenticationServiceHandle> authenticationServices=dorianHandle.getAuthenticationServices();
			for (AuthenticationServiceHandle authenticationServiceHandle : authenticationServices) {
				AuthenticationServiceInformation asi = authenticationServiceHandle.getAuthenticationServiceInformation();
				assertNotNull(asi.getAuthenticationServiceName());
			}
		}
	}
	
	public void testLoadAuthenticationServices(){
		
		List<DorianServiceHandle> dorians =servicesManager.getDorianServices();
		for (DorianServiceHandle dorianHandle : dorians) {
			try {
				servicesManager.loadAuthenticationServices(dorianHandle);
				List <AuthenticationServiceHandle> authenticationServiceHandles =dorianHandle.getAuthenticationServices();
				for (AuthenticationServiceHandle authenticationServiceHandle : authenticationServiceHandles) {
					assertNotNull(authenticationServiceHandle.getAuthenticationServiceInformation().getAuthenticationServiceName());
				}
			} catch (Exception e) {
				fail("loading Authentication Services from Dorian failure");
			}
		}
	}
	
	public void testGetAuthenticationProfileList(){
		List<DorianServiceHandle> dorians =servicesManager.getDorianServices();
		
		List<AuthenticationServiceInformation> serviceInformations=new ArrayList<AuthenticationServiceInformation>();
		for (DorianServiceHandle dorianHandle : dorians) {
			List<AuthenticationServiceHandle> authenticationServices=dorianHandle.getAuthenticationServices();
			
			for (AuthenticationServiceHandle authenticationServiceHandle : authenticationServices) {
				serviceInformations.add(authenticationServiceHandle.getAuthenticationServiceInformation());
			}			
		}
		
		for (AuthenticationServiceInformation authenticationServiceInformation : serviceInformations) {
			servicesManager.getAuthenticationProfilesList(serviceInformations, authenticationServiceInformation.getAuthenticationServiceURL());
		}		
	}
}
