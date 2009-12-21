/*
 * Created on Jul 14, 2006
 */
package org.cagrid.cds.test.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.util.List;

import org.apache.axis.message.MessageElement;
import org.cagrid.cds.test.util.DelegatedCredential;
import org.cagrid.cds.test.util.DelegationIdentifierReference;
import org.cagrid.gaards.cds.client.DelegationUserClient;
import org.cagrid.gaards.cds.common.AllowedParties;
import org.cagrid.gaards.cds.common.DelegationIdentifier;
import org.cagrid.gaards.cds.common.IdentityDelegationPolicy;
import org.cagrid.gaards.cds.common.ProxyLifetime;
import org.cagrid.gaards.cds.delegated.stubs.types.DelegatedCredentialReference;
import org.cagrid.gaards.dorian.test.system.steps.GridCredentialRequestStep;

public class DelegateCredentialStep extends Step implements
		DelegatedCredential, DelegationIdentifierReference {

	private String serviceURL;
	private GridCredentialRequestStep delegator;
	private List<GridCredentialRequestStep> allowedParties;
	private ProxyLifetime delegationLifetime;
	private ProxyLifetime delegatedCredentialsLifetime;
	private DelegatedCredentialReference delegatedCredentialReference;

	public DelegateCredentialStep(String serviceURL,
			GridCredentialRequestStep delegator, List<GridCredentialRequestStep> allowedParties,
			ProxyLifetime delegatedCredentialsLifetime) {
		this(serviceURL, delegator, allowedParties, null,
				delegatedCredentialsLifetime);
	}

	public DelegateCredentialStep(String serviceURL,
			GridCredentialRequestStep delegator, List<GridCredentialRequestStep> allowedParties,
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
		assertNotNull(this.delegator.getGridCredential());
		assertNotNull(this.delegatedCredentialsLifetime);

		IdentityDelegationPolicy policy = new IdentityDelegationPolicy();
		AllowedParties ap = new AllowedParties();
		String[] id = new String[allowedParties.size()];
		for (int i = 0; i < allowedParties.size(); i++) {
			assertNotNull(allowedParties.get(i).getGridCredential());
			id[i] = allowedParties.get(i).getGridCredential().getIdentity();
		}
		ap.setGridIdentity(id);
		policy.setAllowedParties(ap);
		DelegationUserClient client = new DelegationUserClient(this.serviceURL,
				this.delegator.getGridCredential());
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
