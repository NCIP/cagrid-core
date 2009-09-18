package gov.nih.nci.cagrid.authorization.pdp.impl;

import gov.nih.nci.cagrid.authorization.GridAuthorizationManager;
import gov.nih.nci.cagrid.authorization.pdp.PENode;
import gov.nih.nci.cagrid.authorization.pdp.PENodeSelector;
import gov.nih.nci.cagrid.authorization.pdp.PENodeSelectorSelector;

import javax.security.auth.Subject;
import javax.xml.namespace.QName;
import javax.xml.rpc.handler.MessageContext;

import org.apache.axis.message.MessageElement;
import org.apache.axis.message.SOAPBody;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.wsrf.impl.security.authorization.exceptions.AuthorizationException;
import org.globus.wsrf.impl.security.authorization.exceptions.CloseException;
import org.globus.wsrf.impl.security.authorization.exceptions.InitializeException;
import org.globus.wsrf.impl.security.authorization.exceptions.InvalidPolicyException;
import org.globus.wsrf.security.SecurityManager;
import org.globus.wsrf.security.authorization.PDP;
import org.globus.wsrf.security.authorization.PDPConfig;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class CSMPDP implements PDP {

	private static Log logger = LogFactory.getLog(CSMPDP.class.getName());

	private GridAuthorizationManager authorizationManager;

	private PENodeSelectorSelector selectorSelector;

	public Node getPolicy(Node arg0) throws InvalidPolicyException {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getPolicyNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isPermitted(Subject subject, MessageContext context,
			QName operation) throws AuthorizationException {

		boolean permitted = false;

		if (!(context instanceof org.apache.axis.MessageContext)) {
			throw new IllegalArgumentException(
					"Expected instance of org.apache.axis.MessageContext. Got "
							+ (context == null ? "null" : context.getClass()
									.getName()));
		}
		org.apache.axis.MessageContext apacheCtx = (org.apache.axis.MessageContext) context;

		Document doc = null;
		try {
			SOAPBody body = (SOAPBody) apacheCtx.getCurrentMessage()
					.getSOAPBody();
			MessageElement mel = (MessageElement) body.getChildElements().next();
			doc = mel.getAsDocument();
		} catch (Exception ex) {
			throw new RuntimeException("Error getting body document: "
					+ ex.getMessage(), ex);
		}

		PENodeSelector selector = getSelectorSelector().select(apacheCtx);
		if (selector != null) {
			PENode[] peNodes = selector.selectPENodes(doc);
			logger.debug("Found " + peNodes.length + " nodes.");
			String identity = SecurityManager.getManager().getCaller();
			GridAuthorizationManager authzMgr = getAuthorizationManager();
			for (int i = 0; i < peNodes.length; i++) {
				permitted = authzMgr.isAuthorized(identity, peNodes[i].getObjectId(), selector.getPrivilege());
			}
		}

		return permitted;
	}

	public Node setPolicy(Node arg0) throws InvalidPolicyException {
		// TODO Auto-generated method stub
		return null;
	}

	public void close() throws CloseException {
		// TODO Auto-generated method stub

	}

	public void initialize(PDPConfig arg0, String arg1, String arg2)
			throws InitializeException {
		// TODO Auto-generated method stub

	}

	public GridAuthorizationManager getAuthorizationManager() {
		return authorizationManager;
	}

	public void setAuthorizationManager(
			GridAuthorizationManager authorizationManager) {
		this.authorizationManager = authorizationManager;
	}

	public PENodeSelectorSelector getSelectorSelector() {
		return selectorSelector;
	}

	public void setSelectorSelector(PENodeSelectorSelector selectorSelector) {
		this.selectorSelector = selectorSelector;
	}

}
