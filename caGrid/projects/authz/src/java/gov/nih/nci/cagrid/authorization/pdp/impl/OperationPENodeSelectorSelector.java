package gov.nih.nci.cagrid.authorization.pdp.impl;

import gov.nih.nci.cagrid.authorization.pdp.PENodeSelector;
import gov.nih.nci.cagrid.authorization.pdp.PENodeSelectorSelector;

import java.util.Iterator;
import java.util.List;

import org.apache.axis.MessageContext;

public class OperationPENodeSelectorSelector implements PENodeSelectorSelector {
	
	private List mappings;

	public PENodeSelector select(MessageContext context) {
		PENodeSelector selector = null;
		for(Iterator i = getMappings().iterator(); i.hasNext();){
			RegExPENodeSelectorMapping m = (RegExPENodeSelectorMapping)i.next();
			if(m.matches(context.getOperation().getElementQName())){
				selector = m.getSelector();
				break;
			}
		}
		return selector;
	}

	public List getMappings() {
		return mappings;
	}

	public void setMappings(List mappings) {
		this.mappings = mappings;
	}

}
