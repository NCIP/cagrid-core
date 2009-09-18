package gov.nih.nci.cagrid.authorization.filter;

import gov.nih.nci.cagrid.authorization.pdp.impl.AuthzUtils;

import org.apache.axis.AxisFault;
import org.apache.axis.Handler;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;

public class FilterDelegator extends BasicHandler {
	
	public void invoke(MessageContext context) throws AxisFault {
		try{
			String beansFile = (String) getOption(AuthzUtils.BEANS_FILE);
			String beanName = getName();
			Handler filter = (Handler) AuthzUtils.getBean(beansFile, beanName);
			filter.invoke(context);
		}catch(Exception ex){
			throw new AxisFault("Error encountered: " + ex.getMessage(), ex);
		}
	}

}
