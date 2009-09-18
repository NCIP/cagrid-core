package gov.nih.nci.cagrid.authorization.filter;

import gov.nih.nci.cagrid.authorization.GridAuthorizationManager;
import gov.nih.nci.cagrid.authorization.pdp.PENode;
import gov.nih.nci.cagrid.authorization.pdp.PENodeHandler;
import gov.nih.nci.cagrid.authorization.pdp.PENodeSelector;
import gov.nih.nci.cagrid.authorization.pdp.PENodeSelectorSelector;

import javax.security.auth.Subject;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.SOAPBody;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.wsrf.security.SecurityManager;
import org.w3c.dom.Document;

public class CSMNodeFilter extends BasicHandler {

	private static Log logger = LogFactory.getLog(CSMNodeFilter.class
			.getName());

	private GridAuthorizationManager authorizationManager;

	private PENodeSelectorSelector selectorSelector;

	private PENodeHandler nodeHandler;

	public PENodeHandler getNodeHandler() {
		return nodeHandler;
	}

	public void setNodeHandler(PENodeHandler nodeHandler) {
		this.nodeHandler = nodeHandler;
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

	public void invoke(MessageContext context) throws AxisFault {

		SOAPBody body = null;
		Document doc = null;
		try {
			body = (SOAPBody) context.getCurrentMessage()
					.getSOAPBody();
			MessageElement mel = (MessageElement) body.getChildElements().next();
			doc = mel.getAsDocument();
			body.removeChild(mel);
		} catch (Exception ex) {
			throw new RuntimeException("Error getting body document: "
					+ ex.getMessage(), ex);
		}

		// Pull out the subject
		Subject subject = null;
		try {
			subject = (Subject) context
					.getProperty(org.globus.wsrf.impl.security.authentication.Constants.PEER_SUBJECT);
		} catch (Exception ex) {
			throw new AxisFault("Error getting subject from context: "
					+ ex.getMessage(), ex);
		}
		if (subject == null) {
			throw new AxisFault("No subject found in context.");
		}

		PENodeSelector selector = getSelectorSelector().select(context);
		if (selector != null) {
			PENode[] peNodes = selector.selectPENodes(doc);
			
			logger.debug("Found " + peNodes.length + " nodes.");
			String identity = SecurityManager.getManager().getCaller();
			GridAuthorizationManager authzMgr = getAuthorizationManager();
			for (int i = 0; i < peNodes.length; i++) {
				boolean isAuthorized = authzMgr.isAuthorized(identity, peNodes[i].getObjectId(), selector.getPrivilege());
				if (!isAuthorized) {
					getNodeHandler().handleNode(peNodes[i]);
				}
			}
		}
		try{
			body.addDocument(doc);
		}catch(Exception ex){
			throw new AxisFault("Error adding body document: " + ex.getMessage(), ex);
		}
	}

}
