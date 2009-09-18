
package gov.nih.nci.cagrid.workflow.handlers;


import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.globus.axis.gsi.GSIConstants;

import org.globus.axis.util.Util;

/**
 * @author madduri
 *
 */
public class SimpleGlobusAuthorizationHandler extends BasicHandler {
	static {
		Util.registerTransport();
	}
	
	public void invoke(MessageContext msgContext) throws AxisFault {
		 msgContext.setProperty(
                 GSIConstants.GSI_AUTHORIZATION,
                 org.globus.gsi.gssapi.auth.NoAuthorization.getInstance());

		/* String pathToDesc = System.getProperty("java.io.tmpdir") + 
		 File.separator + "secDescriptor.xml";
		 System.out.println("setting desc" + pathToDesc);
		 msgContext.setProperty(Constants.CLIENT_DESCRIPTOR_FILE, pathToDesc);
         */
		 msgContext.setProperty(GSIConstants.GSI_TRANSPORT,
                 GSIConstants.ENCRYPTION);
/*		 msgContext.setProperty(org.globus.wsrf.security.Constants.GSI_SEC_CONV,
					org.globus.wsrf.security.Constants.ENCRYPTION);*/
	}
	// Query service security metadata and set the message context propterties 
	// for secure conversation and secure messaging
}
