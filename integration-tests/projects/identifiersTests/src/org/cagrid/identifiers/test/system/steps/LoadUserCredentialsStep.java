package org.cagrid.identifiers.test.system.steps;

import gov.nih.nci.cagrid.common.security.ProxyUtil;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;

import org.cagrid.identifiers.test.system.IdentifiersTestInfo;

public class LoadUserCredentialsStep extends Step {
    
	private IdentifiersTestInfo testInfo;

	public LoadUserCredentialsStep(IdentifiersTestInfo info) {
		this.testInfo = info;
	}
	
	@Override
	public void runStep() throws Exception {
		
		String credsPath = testInfo.getGridCertsPath() + File.separator;
		
		// User A: /O=osu/CN=testing user
		// User B: /O=osu/CN=testing user 2
		// User C: /O=osu/CN=testing user 3
		
		testInfo.setUserA(ProxyUtil.loadProxy(credsPath + "user.proxy"));
		testInfo.setUserB(ProxyUtil.loadProxy(credsPath + "user2.proxy"));
		testInfo.setUserC(ProxyUtil.loadProxy(credsPath + "user3.proxy"));
	}
		
}
