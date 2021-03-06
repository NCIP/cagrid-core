/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package gov.nih.nci.cagrid.gts.client;

import gov.nih.nci.cagrid.gts.common.GTSI;

import java.rmi.RemoteException;

import org.apache.axis.client.Stub;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.globus.gsi.GlobusCredential;

/**
 * This class is autogenerated, DO NOT EDIT GENERATED GRID SERVICE ACCESS METHODS.
 *
 * This client is generated automatically by Introduce to provide a clean unwrapped API to the
 * service.
 *
 * On construction the class instance will contact the remote service and retrieve it's security
 * metadata description which it will use to configure the Stub specifically for each method call.
 * 
 * @created by Introduce Toolkit version 1.3
 */
public class GTSClient extends GTSClientBase implements GTSI {	

	public GTSClient(String url) throws MalformedURIException, RemoteException {
		this(url,null);	
	}

	public GTSClient(String url, GlobusCredential proxy) throws MalformedURIException, RemoteException {
	   	super(url,proxy);
	}
	
	public GTSClient(EndpointReferenceType epr) throws MalformedURIException, RemoteException {
	   	this(epr,null);
	}
	
	public GTSClient(EndpointReferenceType epr, GlobusCredential proxy) throws MalformedURIException, RemoteException {
	   	super(epr,proxy);
	}

	public static void usage(){
		System.out.println(GTSClient.class.getName() + " -url <service url>");
	}
	
	public static void main(String [] args){
	    System.out.println("Running the Grid Service Client");
		try{
		if(!(args.length < 2)){
			if(args[0].equals("-url")){
			  GTSClient client = new GTSClient(args[1]);
			  // place client calls here if you want to use this main as a
			  // test....
			} else {
				usage();
				System.exit(1);
			}
		} else {
			usage();
			System.exit(1);
		}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

  public gov.nih.nci.cagrid.gts.bean.TrustedAuthority addTrustedAuthority(gov.nih.nci.cagrid.gts.bean.TrustedAuthority ta) throws RemoteException, gov.nih.nci.cagrid.gts.stubs.types.GTSInternalFault, gov.nih.nci.cagrid.gts.stubs.types.IllegalTrustedAuthorityFault, gov.nih.nci.cagrid.gts.stubs.types.PermissionDeniedFault {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"addTrustedAuthority");
    gov.nih.nci.cagrid.gts.stubs.AddTrustedAuthorityRequest params = new gov.nih.nci.cagrid.gts.stubs.AddTrustedAuthorityRequest();
    gov.nih.nci.cagrid.gts.stubs.AddTrustedAuthorityRequestTa taContainer = new gov.nih.nci.cagrid.gts.stubs.AddTrustedAuthorityRequestTa();
    taContainer.setTrustedAuthority(ta);
    params.setTa(taContainer);
    gov.nih.nci.cagrid.gts.stubs.AddTrustedAuthorityResponse boxedResult = portType.addTrustedAuthority(params);
    return boxedResult.getTrustedAuthority();
    }
  }

  public gov.nih.nci.cagrid.gts.bean.TrustedAuthority[] findTrustedAuthorities(gov.nih.nci.cagrid.gts.bean.TrustedAuthorityFilter filter) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"findTrustedAuthorities");
    gov.nih.nci.cagrid.gts.stubs.FindTrustedAuthoritiesRequest params = new gov.nih.nci.cagrid.gts.stubs.FindTrustedAuthoritiesRequest();
    gov.nih.nci.cagrid.gts.stubs.FindTrustedAuthoritiesRequestFilter filterContainer = new gov.nih.nci.cagrid.gts.stubs.FindTrustedAuthoritiesRequestFilter();
    filterContainer.setTrustedAuthorityFilter(filter);
    params.setFilter(filterContainer);
    gov.nih.nci.cagrid.gts.stubs.FindTrustedAuthoritiesResponse boxedResult = portType.findTrustedAuthorities(params);
    return boxedResult.getTrustedAuthority();
    }
  }

  public void removeTrustedAuthority(java.lang.String trustedAuthorityName) throws RemoteException, gov.nih.nci.cagrid.gts.stubs.types.GTSInternalFault, gov.nih.nci.cagrid.gts.stubs.types.InvalidTrustedAuthorityFault, gov.nih.nci.cagrid.gts.stubs.types.PermissionDeniedFault {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"removeTrustedAuthority");
    gov.nih.nci.cagrid.gts.stubs.RemoveTrustedAuthorityRequest params = new gov.nih.nci.cagrid.gts.stubs.RemoveTrustedAuthorityRequest();
    params.setTrustedAuthorityName(trustedAuthorityName);
    gov.nih.nci.cagrid.gts.stubs.RemoveTrustedAuthorityResponse boxedResult = portType.removeTrustedAuthority(params);
    }
  }

  public void addPermission(gov.nih.nci.cagrid.gts.bean.Permission permission) throws RemoteException, gov.nih.nci.cagrid.gts.stubs.types.GTSInternalFault, gov.nih.nci.cagrid.gts.stubs.types.IllegalPermissionFault, gov.nih.nci.cagrid.gts.stubs.types.PermissionDeniedFault {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"addPermission");
    gov.nih.nci.cagrid.gts.stubs.AddPermissionRequest params = new gov.nih.nci.cagrid.gts.stubs.AddPermissionRequest();
    gov.nih.nci.cagrid.gts.stubs.AddPermissionRequestPermission permissionContainer = new gov.nih.nci.cagrid.gts.stubs.AddPermissionRequestPermission();
    permissionContainer.setPermission(permission);
    params.setPermission(permissionContainer);
    gov.nih.nci.cagrid.gts.stubs.AddPermissionResponse boxedResult = portType.addPermission(params);
    }
  }

  public gov.nih.nci.cagrid.gts.bean.Permission[] findPermissions(gov.nih.nci.cagrid.gts.bean.PermissionFilter filter) throws RemoteException, gov.nih.nci.cagrid.gts.stubs.types.GTSInternalFault, gov.nih.nci.cagrid.gts.stubs.types.PermissionDeniedFault {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"findPermissions");
    gov.nih.nci.cagrid.gts.stubs.FindPermissionsRequest params = new gov.nih.nci.cagrid.gts.stubs.FindPermissionsRequest();
    gov.nih.nci.cagrid.gts.stubs.FindPermissionsRequestFilter filterContainer = new gov.nih.nci.cagrid.gts.stubs.FindPermissionsRequestFilter();
    filterContainer.setPermissionFilter(filter);
    params.setFilter(filterContainer);
    gov.nih.nci.cagrid.gts.stubs.FindPermissionsResponse boxedResult = portType.findPermissions(params);
    return boxedResult.getPermission();
    }
  }

  public void revokePermission(gov.nih.nci.cagrid.gts.bean.Permission permission) throws RemoteException, gov.nih.nci.cagrid.gts.stubs.types.GTSInternalFault, gov.nih.nci.cagrid.gts.stubs.types.InvalidPermissionFault, gov.nih.nci.cagrid.gts.stubs.types.PermissionDeniedFault {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"revokePermission");
    gov.nih.nci.cagrid.gts.stubs.RevokePermissionRequest params = new gov.nih.nci.cagrid.gts.stubs.RevokePermissionRequest();
    gov.nih.nci.cagrid.gts.stubs.RevokePermissionRequestPermission permissionContainer = new gov.nih.nci.cagrid.gts.stubs.RevokePermissionRequestPermission();
    permissionContainer.setPermission(permission);
    params.setPermission(permissionContainer);
    gov.nih.nci.cagrid.gts.stubs.RevokePermissionResponse boxedResult = portType.revokePermission(params);
    }
  }

  public void updateTrustedAuthority(gov.nih.nci.cagrid.gts.bean.TrustedAuthority ta) throws RemoteException, gov.nih.nci.cagrid.gts.stubs.types.GTSInternalFault, gov.nih.nci.cagrid.gts.stubs.types.IllegalTrustedAuthorityFault, gov.nih.nci.cagrid.gts.stubs.types.InvalidTrustedAuthorityFault, gov.nih.nci.cagrid.gts.stubs.types.PermissionDeniedFault {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"updateTrustedAuthority");
    gov.nih.nci.cagrid.gts.stubs.UpdateTrustedAuthorityRequest params = new gov.nih.nci.cagrid.gts.stubs.UpdateTrustedAuthorityRequest();
    gov.nih.nci.cagrid.gts.stubs.UpdateTrustedAuthorityRequestTa taContainer = new gov.nih.nci.cagrid.gts.stubs.UpdateTrustedAuthorityRequestTa();
    taContainer.setTrustedAuthority(ta);
    params.setTa(taContainer);
    gov.nih.nci.cagrid.gts.stubs.UpdateTrustedAuthorityResponse boxedResult = portType.updateTrustedAuthority(params);
    }
  }

  public void addTrustLevel(gov.nih.nci.cagrid.gts.bean.TrustLevel trustLevel) throws RemoteException, gov.nih.nci.cagrid.gts.stubs.types.GTSInternalFault, gov.nih.nci.cagrid.gts.stubs.types.IllegalTrustLevelFault, gov.nih.nci.cagrid.gts.stubs.types.PermissionDeniedFault {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"addTrustLevel");
    gov.nih.nci.cagrid.gts.stubs.AddTrustLevelRequest params = new gov.nih.nci.cagrid.gts.stubs.AddTrustLevelRequest();
    gov.nih.nci.cagrid.gts.stubs.AddTrustLevelRequestTrustLevel trustLevelContainer = new gov.nih.nci.cagrid.gts.stubs.AddTrustLevelRequestTrustLevel();
    trustLevelContainer.setTrustLevel(trustLevel);
    params.setTrustLevel(trustLevelContainer);
    gov.nih.nci.cagrid.gts.stubs.AddTrustLevelResponse boxedResult = portType.addTrustLevel(params);
    }
  }

  public void updateTrustLevel(gov.nih.nci.cagrid.gts.bean.TrustLevel trustLevel) throws RemoteException, gov.nih.nci.cagrid.gts.stubs.types.GTSInternalFault, gov.nih.nci.cagrid.gts.stubs.types.InvalidTrustLevelFault, gov.nih.nci.cagrid.gts.stubs.types.IllegalTrustLevelFault, gov.nih.nci.cagrid.gts.stubs.types.PermissionDeniedFault {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"updateTrustLevel");
    gov.nih.nci.cagrid.gts.stubs.UpdateTrustLevelRequest params = new gov.nih.nci.cagrid.gts.stubs.UpdateTrustLevelRequest();
    gov.nih.nci.cagrid.gts.stubs.UpdateTrustLevelRequestTrustLevel trustLevelContainer = new gov.nih.nci.cagrid.gts.stubs.UpdateTrustLevelRequestTrustLevel();
    trustLevelContainer.setTrustLevel(trustLevel);
    params.setTrustLevel(trustLevelContainer);
    gov.nih.nci.cagrid.gts.stubs.UpdateTrustLevelResponse boxedResult = portType.updateTrustLevel(params);
    }
  }

  public gov.nih.nci.cagrid.gts.bean.TrustLevel[] getTrustLevels() throws RemoteException, gov.nih.nci.cagrid.gts.stubs.types.GTSInternalFault {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"getTrustLevels");
    gov.nih.nci.cagrid.gts.stubs.GetTrustLevelsRequest params = new gov.nih.nci.cagrid.gts.stubs.GetTrustLevelsRequest();
    gov.nih.nci.cagrid.gts.stubs.GetTrustLevelsResponse boxedResult = portType.getTrustLevels(params);
    return boxedResult.getTrustLevel();
    }
  }

  public void removeTrustLevel(java.lang.String trustLevelName) throws RemoteException, gov.nih.nci.cagrid.gts.stubs.types.GTSInternalFault, gov.nih.nci.cagrid.gts.stubs.types.InvalidTrustLevelFault, gov.nih.nci.cagrid.gts.stubs.types.IllegalTrustLevelFault, gov.nih.nci.cagrid.gts.stubs.types.PermissionDeniedFault {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"removeTrustLevel");
    gov.nih.nci.cagrid.gts.stubs.RemoveTrustLevelRequest params = new gov.nih.nci.cagrid.gts.stubs.RemoveTrustLevelRequest();
    params.setTrustLevelName(trustLevelName);
    gov.nih.nci.cagrid.gts.stubs.RemoveTrustLevelResponse boxedResult = portType.removeTrustLevel(params);
    }
  }

  public void addAuthority(gov.nih.nci.cagrid.gts.bean.AuthorityGTS authorityGTS) throws RemoteException, gov.nih.nci.cagrid.gts.stubs.types.GTSInternalFault, gov.nih.nci.cagrid.gts.stubs.types.IllegalAuthorityFault, gov.nih.nci.cagrid.gts.stubs.types.PermissionDeniedFault {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"addAuthority");
    gov.nih.nci.cagrid.gts.stubs.AddAuthorityRequest params = new gov.nih.nci.cagrid.gts.stubs.AddAuthorityRequest();
    gov.nih.nci.cagrid.gts.stubs.AddAuthorityRequestAuthorityGTS authorityGTSContainer = new gov.nih.nci.cagrid.gts.stubs.AddAuthorityRequestAuthorityGTS();
    authorityGTSContainer.setAuthorityGTS(authorityGTS);
    params.setAuthorityGTS(authorityGTSContainer);
    gov.nih.nci.cagrid.gts.stubs.AddAuthorityResponse boxedResult = portType.addAuthority(params);
    }
  }

  public void updateAuthority(gov.nih.nci.cagrid.gts.bean.AuthorityGTS authorityGTS) throws RemoteException, gov.nih.nci.cagrid.gts.stubs.types.GTSInternalFault, gov.nih.nci.cagrid.gts.stubs.types.IllegalAuthorityFault, gov.nih.nci.cagrid.gts.stubs.types.InvalidAuthorityFault, gov.nih.nci.cagrid.gts.stubs.types.PermissionDeniedFault {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"updateAuthority");
    gov.nih.nci.cagrid.gts.stubs.UpdateAuthorityRequest params = new gov.nih.nci.cagrid.gts.stubs.UpdateAuthorityRequest();
    gov.nih.nci.cagrid.gts.stubs.UpdateAuthorityRequestAuthorityGTS authorityGTSContainer = new gov.nih.nci.cagrid.gts.stubs.UpdateAuthorityRequestAuthorityGTS();
    authorityGTSContainer.setAuthorityGTS(authorityGTS);
    params.setAuthorityGTS(authorityGTSContainer);
    gov.nih.nci.cagrid.gts.stubs.UpdateAuthorityResponse boxedResult = portType.updateAuthority(params);
    }
  }

  public void updateAuthorityPriorities(gov.nih.nci.cagrid.gts.bean.AuthorityPriorityUpdate authorityPriorityUpdate) throws RemoteException, gov.nih.nci.cagrid.gts.stubs.types.GTSInternalFault, gov.nih.nci.cagrid.gts.stubs.types.IllegalAuthorityFault, gov.nih.nci.cagrid.gts.stubs.types.PermissionDeniedFault {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"updateAuthorityPriorities");
    gov.nih.nci.cagrid.gts.stubs.UpdateAuthorityPrioritiesRequest params = new gov.nih.nci.cagrid.gts.stubs.UpdateAuthorityPrioritiesRequest();
    gov.nih.nci.cagrid.gts.stubs.UpdateAuthorityPrioritiesRequestAuthorityPriorityUpdate authorityPriorityUpdateContainer = new gov.nih.nci.cagrid.gts.stubs.UpdateAuthorityPrioritiesRequestAuthorityPriorityUpdate();
    authorityPriorityUpdateContainer.setAuthorityPriorityUpdate(authorityPriorityUpdate);
    params.setAuthorityPriorityUpdate(authorityPriorityUpdateContainer);
    gov.nih.nci.cagrid.gts.stubs.UpdateAuthorityPrioritiesResponse boxedResult = portType.updateAuthorityPriorities(params);
    }
  }

  public gov.nih.nci.cagrid.gts.bean.AuthorityGTS[] getAuthorities() throws RemoteException, gov.nih.nci.cagrid.gts.stubs.types.GTSInternalFault {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"getAuthorities");
    gov.nih.nci.cagrid.gts.stubs.GetAuthoritiesRequest params = new gov.nih.nci.cagrid.gts.stubs.GetAuthoritiesRequest();
    gov.nih.nci.cagrid.gts.stubs.GetAuthoritiesResponse boxedResult = portType.getAuthorities(params);
    return boxedResult.getAuthorityGTS();
    }
  }

  public void removeAuthority(java.lang.String serviceURI) throws RemoteException, gov.nih.nci.cagrid.gts.stubs.types.GTSInternalFault, gov.nih.nci.cagrid.gts.stubs.types.InvalidAuthorityFault, gov.nih.nci.cagrid.gts.stubs.types.PermissionDeniedFault {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"removeAuthority");
    gov.nih.nci.cagrid.gts.stubs.RemoveAuthorityRequest params = new gov.nih.nci.cagrid.gts.stubs.RemoveAuthorityRequest();
    params.setServiceURI(serviceURI);
    gov.nih.nci.cagrid.gts.stubs.RemoveAuthorityResponse boxedResult = portType.removeAuthority(params);
    }
  }

  public void updateCRL(java.lang.String trustedAuthorityName,gov.nih.nci.cagrid.gts.bean.X509CRL crl) throws RemoteException, gov.nih.nci.cagrid.gts.stubs.types.GTSInternalFault, gov.nih.nci.cagrid.gts.stubs.types.IllegalTrustedAuthorityFault, gov.nih.nci.cagrid.gts.stubs.types.InvalidTrustedAuthorityFault, gov.nih.nci.cagrid.gts.stubs.types.PermissionDeniedFault {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"updateCRL");
    gov.nih.nci.cagrid.gts.stubs.UpdateCRLRequest params = new gov.nih.nci.cagrid.gts.stubs.UpdateCRLRequest();
    params.setTrustedAuthorityName(trustedAuthorityName);
    gov.nih.nci.cagrid.gts.stubs.UpdateCRLRequestCrl crlContainer = new gov.nih.nci.cagrid.gts.stubs.UpdateCRLRequestCrl();
    crlContainer.setX509CRL(crl);
    params.setCrl(crlContainer);
    gov.nih.nci.cagrid.gts.stubs.UpdateCRLResponse boxedResult = portType.updateCRL(params);
    }
  }

  public boolean validate(gov.nih.nci.cagrid.gts.bean.X509Certificate[] chain,gov.nih.nci.cagrid.gts.bean.TrustedAuthorityFilter filter) throws RemoteException, gov.nih.nci.cagrid.gts.stubs.types.GTSInternalFault, gov.nih.nci.cagrid.gts.stubs.types.CertificateValidationFault {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"validate");
    gov.nih.nci.cagrid.gts.stubs.ValidateRequest params = new gov.nih.nci.cagrid.gts.stubs.ValidateRequest();
    gov.nih.nci.cagrid.gts.stubs.ValidateRequestChain chainContainer = new gov.nih.nci.cagrid.gts.stubs.ValidateRequestChain();
    chainContainer.setX509Certificate(chain);
    params.setChain(chainContainer);
    gov.nih.nci.cagrid.gts.stubs.ValidateRequestFilter filterContainer = new gov.nih.nci.cagrid.gts.stubs.ValidateRequestFilter();
    filterContainer.setTrustedAuthorityFilter(filter);
    params.setFilter(filterContainer);
    gov.nih.nci.cagrid.gts.stubs.ValidateResponse boxedResult = portType.validate(params);
    return boxedResult.isResponse();
    }
  }

  public gov.nih.nci.cagrid.metadata.security.ServiceSecurityMetadata getServiceSecurityMetadata() throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"getServiceSecurityMetadata");
    gov.nih.nci.cagrid.introduce.security.stubs.GetServiceSecurityMetadataRequest params = new gov.nih.nci.cagrid.introduce.security.stubs.GetServiceSecurityMetadataRequest();
    gov.nih.nci.cagrid.introduce.security.stubs.GetServiceSecurityMetadataResponse boxedResult = portType.getServiceSecurityMetadata(params);
    return boxedResult.getServiceSecurityMetadata();
    }
  }

  public org.oasis.wsrf.properties.GetMultipleResourcePropertiesResponse getMultipleResourceProperties(org.oasis.wsrf.properties.GetMultipleResourceProperties_Element params) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"getMultipleResourceProperties");
    return portType.getMultipleResourceProperties(params);
    }
  }

  public org.oasis.wsrf.properties.GetResourcePropertyResponse getResourceProperty(javax.xml.namespace.QName params) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"getResourceProperty");
    return portType.getResourceProperty(params);
    }
  }

  public org.oasis.wsrf.properties.QueryResourcePropertiesResponse queryResourceProperties(org.oasis.wsrf.properties.QueryResourceProperties_Element params) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"queryResourceProperties");
    return portType.queryResourceProperties(params);
    }
  }

}
