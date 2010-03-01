package gov.nih.nci.cagrid.workflow.factory.taverna;

import net.sf.taverna.t2.security.credentialmanager.MasterPasswordProviderSPI;

public class MyMasterPasswordProvider implements MasterPasswordProviderSPI {

	public int canProvidePassword() {
		// TODO Auto-generated method stub
		//Thread.dumpStack();
		return 5;
	}

	public String getPassword() {
		// TODO Auto-generated method stub
		return "somePass";
	}

}
