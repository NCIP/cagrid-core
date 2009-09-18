package gov.nih.nci.cagrid.introduce.security.client;

import gov.nih.nci.cagrid.introduce.security.common.ServiceSecurityI;
import gov.nih.nci.cagrid.introduce.security.stubs.ServiceSecurityPortType;
import gov.nih.nci.cagrid.introduce.security.stubs.service.ServiceSecurityServiceAddressingLocator;
import gov.nih.nci.cagrid.metadata.security.CommunicationMechanism;
import gov.nih.nci.cagrid.metadata.security.Operation;
import gov.nih.nci.cagrid.metadata.security.ProtectionLevelType;
import gov.nih.nci.cagrid.metadata.security.ServiceSecurityMetadata;
import gov.nih.nci.cagrid.metadata.security.ServiceSecurityMetadataOperations;

import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.AxisClient;
import org.apache.axis.client.Stub;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.axis.utils.ClassUtils;
import org.globus.gsi.GlobusCredential;
import org.globus.wsrf.impl.security.authorization.Authorization;
import org.globus.wsrf.impl.security.authorization.NoAuthorization;


/**
 * This class is autogenerated, DO NOT EDIT.
 * 
 * @created by Introduce Toolkit version 1.0
 */
public class ServiceSecurityClient implements ServiceSecurityI {
	protected GlobusCredential proxy;
	protected EndpointReferenceType epr;
	protected ServiceSecurityMetadata securityMetadata;
	protected Map operations;
	protected Authorization authorization;
	protected String delegationMode;
	protected boolean anonymousPrefered = true;

	
	/**
	 * Does this client prefer to contact services anonymously
	 * where allowed.
	 * 
	 * @return true if client will prefer to connect anonymously
	 */
	public boolean isAnonymousPrefered() {
        return anonymousPrefered;
    }

	/**
	 * If you want your client to be forced to authenticate then 
	 * you can set this to false.  The default is true.  If you want
	 * to switch back to using anonymous if allowed then set it back
	 * to true
	 */
    public void setAnonymousPrefered(boolean anonymousPrefered) {
        this.anonymousPrefered = anonymousPrefered;
    }


    static {
		org.globus.axis.util.Util.registerTransport();
	}


	public ServiceSecurityClient(String url) throws MalformedURIException, RemoteException {
		this(url, null);
	}


	public ServiceSecurityClient(String url, GlobusCredential proxy) throws MalformedURIException, RemoteException {
		this.proxy = proxy;
		epr = new EndpointReferenceType();
		epr.setAddress(new Address(url));
	}


	public ServiceSecurityClient(EndpointReferenceType epr) throws MalformedURIException, RemoteException {
		this(epr, null);
	}


	public ServiceSecurityClient(EndpointReferenceType epr, GlobusCredential proxy) throws MalformedURIException,
		RemoteException {
		this.proxy = proxy;
		this.epr = epr;
	}


	public Authorization getAuthorization() {
		return authorization;
	}


	public void setAuthorization(Authorization authorization) {
		this.authorization = authorization;
	}


	public String getDelegationMode() {
		return delegationMode;
	}


	public void setDelegationMode(String delegationMode) {
		this.delegationMode = delegationMode;
	}


	public EndpointReferenceType getEndpointReference() {
		return epr;
	}


	public GlobusCredential getProxy() {
		return proxy;
	}


	public void setProxy(GlobusCredential proxy) {
		this.proxy = proxy;
	}


	protected ServiceSecurityPortType getPortType() throws RemoteException {

		ServiceSecurityServiceAddressingLocator locator = new ServiceSecurityServiceAddressingLocator();
		// attempt to load our context sensitive wsdd file
		InputStream resourceAsStream = ClassUtils.getResourceAsStream(getClass(), "client-config.wsdd");
		if (resourceAsStream != null) {
			// we found it, so tell axis to configure an engine to use it
			EngineConfiguration engineConfig = new FileProvider(resourceAsStream);
			// set the engine of the locator
			locator.setEngine(new AxisClient(engineConfig));
		}
		ServiceSecurityPortType port = null;
		try {
			port = locator.getServiceSecurityPortTypePort(epr);
		} catch (Exception e) {
			throw new RemoteException("Unable to configured porttype:" + e.getMessage(), e);
		}

		return port;
	}


	public static void usage() {
		System.out.println(ServiceSecurityClient.class.getName() + " -url <service url>");
	}


	public static void main(String[] args) {
		System.out.println("Running the Grid Service Client");
		try {
			if (!(args.length < 2)) {
				if (args[0].equals("-url")) {
					ServiceSecurityClient client = new ServiceSecurityClient(args[1]);
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
		}
	}

	/**
	 * Method should be and is called each time this client attempts to talk
	 * the service.  This method will configure the axis stub with the 
	 * appropriate GSI configuration parameters based on the services
	 * provided security metadata.  This prevents the client from having
	 * to brute force or guess the proper way to connect to the server.
	 * 
	 * @param stub
	 * @param method
	 * @throws RemoteException
	 */
	protected void configureStubSecurity(Stub stub, String method) throws RemoteException {

		boolean https = false;
		if (epr.getAddress().getScheme().equals("https")) {
			https = true;
		}

		if (method.equals("getServiceSecurityMetadata")) {
			if (https) {
				resetStub(stub);
				stub._setProperty(org.globus.wsrf.security.Constants.GSI_TRANSPORT,
					org.globus.wsrf.security.Constants.SIGNATURE);
				stub._setProperty(org.globus.wsrf.security.Constants.GSI_ANONYMOUS, Boolean.TRUE);
				stub._setProperty(org.globus.wsrf.security.Constants.AUTHORIZATION,
					org.globus.wsrf.impl.security.authorization.NoAuthorization.getInstance());
			}
			return;
		}

		if (securityMetadata == null) {
			operations = new HashMap();
			securityMetadata = getServiceSecurityMetadata();
			ServiceSecurityMetadataOperations ssmo = securityMetadata.getOperations();
			if (ssmo != null) {
				Operation[] ops = ssmo.getOperation();
				if (ops != null) {
					for (int i = 0; i < ops.length; i++) {
						String lowerMethodName = ops[i].getName().substring(0, 1).toLowerCase()
							+ ops[i].getName().substring(1);
						operations.put(lowerMethodName, ops[i]);
					}
				}
			}

		}
		resetStub(stub);

		CommunicationMechanism serviceDefault = securityMetadata.getDefaultCommunicationMechanism();

		CommunicationMechanism mechanism = null;
		if (operations.containsKey(method)) {
			Operation o = (Operation) operations.get(method);
			mechanism = o.getCommunicationMechanism();
		} else {
			mechanism = serviceDefault;
		}
		boolean anonymousAllowed = true;
		boolean authorizationAllowed = true;
		boolean delegationAllowed = true;
		boolean credentialsAllowed = true;

		if ((https) && (mechanism.getGSITransport() != null)) {
			ProtectionLevelType level = mechanism.getGSITransport().getProtectionLevel();
			if (level != null) {
				if ((level.equals(ProtectionLevelType.privacy)) || (level.equals(ProtectionLevelType.either))) {
					stub._setProperty(org.globus.wsrf.security.Constants.GSI_TRANSPORT,
						org.globus.wsrf.security.Constants.ENCRYPTION);
				} else {
					stub._setProperty(org.globus.wsrf.security.Constants.GSI_TRANSPORT,
						org.globus.wsrf.security.Constants.SIGNATURE);
				}

			} else {
				stub._setProperty(org.globus.wsrf.security.Constants.GSI_TRANSPORT,
					org.globus.wsrf.security.Constants.SIGNATURE);
			}
			delegationAllowed = false;

		} else if (https) {
			stub._setProperty(org.globus.wsrf.security.Constants.GSI_TRANSPORT,
				org.globus.wsrf.security.Constants.SIGNATURE);
			delegationAllowed = false;
		} else if (mechanism.getGSISecureConversation() != null) {
			ProtectionLevelType level = mechanism.getGSISecureConversation().getProtectionLevel();
			if (level != null) {
				if ((level.equals(ProtectionLevelType.privacy)) || (level.equals(ProtectionLevelType.either))) {
					stub._setProperty(org.globus.wsrf.security.Constants.GSI_SEC_CONV,
						org.globus.wsrf.security.Constants.ENCRYPTION);

				} else {
					stub._setProperty(org.globus.wsrf.security.Constants.GSI_SEC_CONV,
						org.globus.wsrf.security.Constants.SIGNATURE);
				}

			} else {
				stub._setProperty(org.globus.wsrf.security.Constants.GSI_SEC_CONV,
					org.globus.wsrf.security.Constants.ENCRYPTION);
			}

		} else if (mechanism.getGSISecureMessage() != null) {
			ProtectionLevelType level = mechanism.getGSISecureMessage().getProtectionLevel();
			if (level != null) {
				if ((level.equals(ProtectionLevelType.privacy)) || (level.equals(ProtectionLevelType.either))) {
					stub._setProperty(org.globus.wsrf.security.Constants.GSI_SEC_MSG,
						org.globus.wsrf.security.Constants.ENCRYPTION);
				} else {
					stub._setProperty(org.globus.wsrf.security.Constants.GSI_SEC_MSG,
						org.globus.wsrf.security.Constants.SIGNATURE);
				}

			} else {
				stub._setProperty(org.globus.wsrf.security.Constants.GSI_SEC_MSG,
					org.globus.wsrf.security.Constants.ENCRYPTION);
			}
			delegationAllowed = false;
			anonymousAllowed = false;
		} else {
			anonymousAllowed = false;
			authorizationAllowed = false;
			delegationAllowed = false;
			credentialsAllowed = false;
		}

		if ((anonymousAllowed) && (mechanism.isAnonymousPermitted()) && isAnonymousPrefered()) {
			stub._setProperty(org.globus.wsrf.security.Constants.GSI_ANONYMOUS, Boolean.TRUE);
		} else if ((credentialsAllowed) && (proxy != null)) {
			try {
				org.ietf.jgss.GSSCredential gss = new org.globus.gsi.gssapi.GlobusGSSCredentialImpl(proxy,
					org.ietf.jgss.GSSCredential.INITIATE_AND_ACCEPT);
				stub._setProperty(org.globus.axis.gsi.GSIConstants.GSI_CREDENTIALS, gss);
			} catch (org.ietf.jgss.GSSException ex) {
				throw new RemoteException(ex.getMessage());
			}
		}

		if (authorizationAllowed) {
			if (authorization == null) {
				stub._setProperty(org.globus.wsrf.security.Constants.AUTHORIZATION, NoAuthorization.getInstance());
			} else {
				stub._setProperty(org.globus.wsrf.security.Constants.AUTHORIZATION, getAuthorization());
			}
		}
		if (delegationAllowed) {
			if (getDelegationMode() != null) {
				stub._setProperty(org.globus.axis.gsi.GSIConstants.GSI_MODE, getDelegationMode());
			}
		}
	}


	protected void resetStub(Stub stub) {
		stub.removeProperty(org.globus.wsrf.security.Constants.GSI_TRANSPORT);
		stub.removeProperty(org.globus.wsrf.security.Constants.GSI_ANONYMOUS);
		stub.removeProperty(org.globus.wsrf.security.Constants.AUTHORIZATION);
		stub.removeProperty(org.globus.axis.gsi.GSIConstants.GSI_CREDENTIALS);
		stub.removeProperty(org.globus.wsrf.security.Constants.GSI_SEC_CONV);
		stub.removeProperty(org.globus.wsrf.security.Constants.GSI_SEC_MSG);
		stub.removeProperty(org.globus.axis.gsi.GSIConstants.GSI_MODE);

	}


	public gov.nih.nci.cagrid.metadata.security.ServiceSecurityMetadata getServiceSecurityMetadata()
		throws RemoteException {
		ServiceSecurityPortType port = this.getPortType();
		this.configureStubSecurity((Stub) port, "getServiceSecurityMetadata");
		gov.nih.nci.cagrid.introduce.security.stubs.GetServiceSecurityMetadataRequest params = new gov.nih.nci.cagrid.introduce.security.stubs.GetServiceSecurityMetadataRequest();
		gov.nih.nci.cagrid.introduce.security.stubs.GetServiceSecurityMetadataResponse boxedResult = port
			.getServiceSecurityMetadata(params);
		return boxedResult.getServiceSecurityMetadata();

	}
}
