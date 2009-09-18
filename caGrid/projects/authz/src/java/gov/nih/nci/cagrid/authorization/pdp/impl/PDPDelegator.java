package gov.nih.nci.cagrid.authorization.pdp.impl;

import javax.security.auth.Subject;
import javax.xml.namespace.QName;
import javax.xml.rpc.handler.MessageContext;

import org.globus.wsrf.impl.security.authorization.exceptions.AuthorizationException;
import org.globus.wsrf.impl.security.authorization.exceptions.CloseException;
import org.globus.wsrf.impl.security.authorization.exceptions.InitializeException;
import org.globus.wsrf.impl.security.authorization.exceptions.InvalidPolicyException;
import org.globus.wsrf.security.authorization.PDP;
import org.globus.wsrf.security.authorization.PDPConfig;
import org.w3c.dom.Node;

public class PDPDelegator implements PDP {
	
	
	private PDP pdp;

	public Node getPolicy(Node n) throws InvalidPolicyException {
		return this.pdp.getPolicy(n);
	}

	public String[] getPolicyNames() {
		return this.pdp.getPolicyNames();
	}

	public boolean isPermitted(Subject subject, MessageContext context, QName operation)
			throws AuthorizationException {
		return this.pdp.isPermitted(subject, context, operation);
	}

	public Node setPolicy(Node n) throws InvalidPolicyException {
		return this.pdp.setPolicy(n);
	}

	public void close() throws CloseException {
		this.pdp.close();
	}

	public void initialize(PDPConfig config, String pdpName, String id)
			throws InitializeException {
		this.pdp = (PDP)AuthzUtils.getBean(config, pdpName, id);
	}

}
