package gov.nih.nci.cagrid.workflow.factory.taverna;

import java.net.URI;
import java.security.cert.X509Certificate;

import net.sf.taverna.t2.security.credentialmanager.CredentialProviderSPI;
import net.sf.taverna.t2.security.credentialmanager.TrustConfirmation;
import net.sf.taverna.t2.security.credentialmanager.UsernamePassword;
public class MyMasterPasswordProvider implements CredentialProviderSPI {


	public boolean canHandleTrustConfirmation(X509Certificate[] arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean canProvideJavaTruststorePassword() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean canProvideMasterPassword() {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean canProvideUsernamePassword(URI arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public String getJavaTruststorePassword() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getMasterPassword(boolean arg0) {
		return "somePas";
	}

	public int getProviderPriority() {
		// TODO Auto-generated method stub
		return 500;
	}

	public UsernamePassword getUsernamePassword(URI arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public TrustConfirmation shouldTrust(X509Certificate[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}
