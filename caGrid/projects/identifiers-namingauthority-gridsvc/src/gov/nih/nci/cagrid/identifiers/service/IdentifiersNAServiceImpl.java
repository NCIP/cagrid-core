package gov.nih.nci.cagrid.identifiers.service;

import org.cagrid.identifiers.namingauthority.NamingAuthority;
import org.cagrid.identifiers.namingauthority.NamingAuthorityLoader;
import org.cagrid.identifiers.namingauthority.impl.IdentifierValuesImpl;
import org.cagrid.identifiers.namingauthority.impl.NamingAuthorityImpl;

import gov.nih.nci.cagrid.identifiers.common.MappingUtil;

import java.rmi.RemoteException;

/** 
 * TODO:I am the service side implementation class.  IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.3
 * 
 */
public class IdentifiersNAServiceImpl extends IdentifiersNAServiceImplBase {

	private NamingAuthority namingAuthority;
	
	public IdentifiersNAServiceImpl() throws RemoteException {
		super();
		
		namingAuthority = new NamingAuthorityLoader().getNamingAuthority();
		
		System.out.println("Initializing naming authority with prefix [" +
				namingAuthority.getConfiguration().getPrefix() + 
				"]");
		
		namingAuthority.initialize();
	}
	

  public java.lang.String createIdentifier(gov.nih.nci.cagrid.identifiers.TypeValuesMap typeValues) throws RemoteException {
	  try {
		return (String)namingAuthority.createIdentifier(MappingUtil.toIdentifierValues(typeValues));
	} catch (Exception e) {
		e.printStackTrace();
		throw new RemoteException(e.toString());
	}
  }

  public gov.nih.nci.cagrid.identifiers.TypeValuesMap getTypeValues(java.lang.String identifier) throws RemoteException {
	  return MappingUtil.toTypeValuesMap((IdentifierValuesImpl)namingAuthority.resolveIdentifier(identifier));
  }

}

