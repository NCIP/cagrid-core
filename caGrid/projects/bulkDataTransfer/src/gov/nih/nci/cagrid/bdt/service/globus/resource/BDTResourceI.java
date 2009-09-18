package gov.nih.nci.cagrid.bdt.service.globus.resource;

import org.globus.transfer.AnyXmlType;
import org.globus.ws.enumeration.EnumIterator;
import org.globus.wsrf.ResourceException;

public interface BDTResourceI {
	
	public EnumIterator createEnumeration() throws BDTException;
	
	public AnyXmlType get() throws BDTException;
	
	public org.apache.axis.types.URI[] getGridFTPURLs() throws BDTException ;
	
	public void remove() throws ResourceException ;
}
