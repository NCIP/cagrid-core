package gov.nih.nci.cagrid.identifiers.common;

import java.rmi.RemoteException;

/** 
 * This class is autogenerated, DO NOT EDIT.
 * 
 * This interface represents the API which is accessable on the grid service from the client. 
 * 
 * @created by Introduce Toolkit version 1.3
 * 
 */
public interface IdentifiersNAServiceI {

  public org.oasis.wsrf.properties.GetMultipleResourcePropertiesResponse getMultipleResourceProperties(org.oasis.wsrf.properties.GetMultipleResourceProperties_Element params) throws RemoteException ;

  public org.oasis.wsrf.properties.GetResourcePropertyResponse getResourceProperty(javax.xml.namespace.QName params) throws RemoteException ;

  public org.oasis.wsrf.properties.QueryResourcePropertiesResponse queryResourceProperties(org.oasis.wsrf.properties.QueryResourceProperties_Element params) throws RemoteException ;

  /**
   * Creates a new identifier
   *
   * @param typeValues
   */
  public java.lang.String createIdentifier(gov.nih.nci.cagrid.identifiers.TypeValuesMap typeValues) throws RemoteException ;

  /**
   * Returns type/values stored with the identifier
   *
   * @param identifier
   */
  public gov.nih.nci.cagrid.identifiers.TypeValuesMap getTypeValues(java.lang.String identifier) throws RemoteException ;

}

