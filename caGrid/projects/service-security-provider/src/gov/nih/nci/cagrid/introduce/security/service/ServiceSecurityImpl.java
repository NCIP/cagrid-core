package gov.nih.nci.cagrid.introduce.security.service;

import gov.nih.nci.cagrid.common.FaultHelper;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.metadata.security.ServiceSecurityMetadata;

import java.io.File;
import java.rmi.RemoteException;

import javax.naming.InitialContext;
import javax.xml.namespace.QName;

import org.apache.axis.MessageContext;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.globus.wsrf.Constants;
import org.globus.wsrf.config.ContainerConfig;
import org.globus.wsrf.utils.AddressingUtils;


/**
 * gov.nih.nci.cagrid.introduce.securityI TODO:DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.0
 */
public class ServiceSecurityImpl {
	private ServiceSecurityMetadata metadata;


	public ServiceSecurityImpl() throws RemoteException {
		try {
			EndpointReferenceType type = AddressingUtils.createEndpointReference(null);
			String configFileEnd = (String) MessageContext.getCurrentContext().getProperty("securityMetadata");
			String configFile = ContainerConfig.getBaseDirectory() + File.separator + configFileEnd;
			File f = new File(configFile);
			if (!f.exists()) {
				throw new RemoteException("The security metadata file (" + configFile + ") could not be found!!!");
			}
			metadata = (ServiceSecurityMetadata) Utils.deserializeDocument(configFile, ServiceSecurityMetadata.class);

		} catch (Exception e) {
			FaultHelper.printStackTrace(e);
			throw new RemoteException(Utils.getExceptionMessage(e));
		}
	}


	public gov.nih.nci.cagrid.metadata.security.ServiceSecurityMetadata getServiceSecurityMetadata()
		throws RemoteException {
		return metadata;
	}

}
