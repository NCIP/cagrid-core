/*
 * Created on Jul 14, 2006
 */
package gov.nci.nih.cagrid.tests.core.steps.cds;

import gov.nci.nih.cagrid.tests.core.DelegatedCredential;
import gov.nci.nih.cagrid.tests.core.DelegationIdentifierReference;
import gov.nci.nih.cagrid.tests.core.GridCredential;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.util.List;

import org.apache.axis.message.MessageElement;
import org.cagrid.gaards.cds.client.DelegationUserClient;
import org.cagrid.gaards.cds.common.AllowedParties;
import org.cagrid.gaards.cds.common.DelegationIdentifier;
import org.cagrid.gaards.cds.common.IdentityDelegationPolicy;
import org.cagrid.gaards.cds.common.ProxyLifetime;
import org.cagrid.gaards.cds.delegated.stubs.types.DelegatedCredentialReference;

public class DelegateCredentialStep extends Step implements
		DelegatedCredential, DelegationIdentifierReference {

	private String serviceURL;
	private GridCredential delegator;
	private List<GridCredential> allowedParties;
	private ProxyLifetime delegationLifetime;
	private ProxyLifetime delegatedCredentialsLifetime;
	private DelegatedCredentialReference delegatedCredentialReference;

	public DelegateCredentialStep(String serviceURL,
			GridCredential delegator, List<GridCredential> allowedParties,
			ProxyLifetime delegatedCredentialsLifetime) {
		this(serviceURL, delegator, allowedParties, null,
				delegatedCredentialsLifetime);
	}

	public DelegateCredentialStep(String serviceURL,
			GridCredential delegator, List<GridCredential> allowedParties,
			ProxyLifetime delegationLifetime,
			ProxyLifetime delegatedCredentialsLifetime) {
		this.serviceURL = serviceURL;
		this.delegator = delegator;
		this.allowedParties = allowedParties;
		this.delegationLifetime = delegationLifetime;
		this.delegatedCredentialsLifetime = delegatedCredentialsLifetime;
	}

	@Override
	public void runStep() throws Throwable {
		assertNotNull(this.serviceURL);
		assertNotNull(this.allowedParties);
		assertNotNull(this.delegator);
		assertNotNull(this.delegator.getCredential());
		assertNotNull(this.delegatedCredentialsLifetime);

		IdentityDelegationPolicy policy = new IdentityDelegationPolicy();
		AllowedParties ap = new AllowedParties();
		String[] id = new String[allowedParties.size()];
		for (int i = 0; i < allowedParties.size(); i++) {
			assertNotNull(allowedParties.get(i).getCredential());
			id[i] = allowedParties.get(i).getCredential().getIdentity();
		}
		ap.setGridIdentity(id);
		policy.setAllowedParties(ap);
		DelegationUserClient client = new DelegationUserClient(this.serviceURL,
				this.delegator.getCredential());
		this.delegatedCredentialReference = client.delegateCredential(
				this.delegationLifetime, policy,
				this.delegatedCredentialsLifetime);
		getDelegationIdentifier();

	}
	
	public DelegationIdentifier getDelegationIdentifier(){
		MessageElement e =  (MessageElement)this.delegatedCredentialReference.getEndpointReference().getProperties().get(0);
		MessageElement c = (MessageElement)e.getChildElements().next();
		String s = c.getValue();
		DelegationIdentifier id = new DelegationIdentifier();
		id.setDelegationId(Long.valueOf(s).longValue());
		return id;
	}

	public DelegatedCredentialReference getDelegatedCredentialReference() {
		return this.delegatedCredentialReference;
	}
}
