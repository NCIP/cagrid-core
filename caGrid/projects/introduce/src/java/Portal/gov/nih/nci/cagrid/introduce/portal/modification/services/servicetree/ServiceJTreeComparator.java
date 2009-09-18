package gov.nih.nci.cagrid.introduce.portal.modification.services.servicetree;

import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.beans.service.ServicesType;
import gov.nih.nci.cagrid.introduce.portal.modification.services.methods.MethodTypeContainer;

import java.util.Comparator;

import javax.swing.tree.DefaultMutableTreeNode;

public class ServiceJTreeComparator implements Comparator {
	  public int compare(Object o1, Object o2) {
	    if (!(o1 instanceof DefaultMutableTreeNode && o2 instanceof DefaultMutableTreeNode)) {
	      throw new IllegalArgumentException(
	          "Can only compare DefaultMutableTreeNode objects");
	    }
	    if((o1 instanceof MethodTypeTreeNode && o2 instanceof MethodTypeTreeNode)){
	    	MethodTypeContainer c1  = new MethodTypeContainer( ((MethodTypeTreeNode) o1).getMethod());
	    	MethodTypeContainer c2  = new MethodTypeContainer( ((MethodTypeTreeNode) o2).getMethod());
		    return c1.compareTo(c2);
	    }
	    
	    if((o1 instanceof ServiceTypeTreeNode && o2 instanceof ServiceTypeTreeNode)){
	    	ServiceType c1  = ((ServiceTypeTreeNode) o1).getServiceType();
	    	ServiceType c2  = ((ServiceTypeTreeNode) o2).getServiceType();
	    	ServicesType service = ((ServiceTypeTreeNode) o2).getInfo().getServices();
	    	for(int i = 0; i < service.getService().length; i++){
	    		if(c1.getName().equals(service.getService(i).getName())){
	    			return i;
	    		}
	    	}
	    }
	    String s1 = ((DefaultMutableTreeNode) o1).getUserObject().toString();
	    String s2 = ((DefaultMutableTreeNode) o2).getUserObject().toString();
	    return s1.compareToIgnoreCase(s2);
	  }
	}