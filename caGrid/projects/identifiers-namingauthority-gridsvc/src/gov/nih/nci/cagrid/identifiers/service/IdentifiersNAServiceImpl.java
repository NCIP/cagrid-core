package gov.nih.nci.cagrid.identifiers.service;

import gov.nih.nci.cagrid.identifiers.common.MappingUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.identifiers.namingauthority.InvalidIdentifierException;
import org.cagrid.identifiers.namingauthority.NamingAuthority;
import org.cagrid.identifiers.namingauthority.NamingAuthorityConfigurationException;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;

/**
 * The service side implementation class of the IdentifiersNAService, managed by
 * Introduce. This mostly just delegates the operations to a Spring-loaded 
 * bean implementation of the NamingAuthority and does any necessary mapping 
 * to/from Axis types or arrays to the Naming Authority interface.
 * 
 * @created by Introduce Toolkit version 1.3
 */
public class IdentifiersNAServiceImpl extends IdentifiersNAServiceImplBase {

	protected static Log LOG = LogFactory.getLog(IdentifiersNAServiceImpl.class.getName());
	protected static final String NA_BEAN_NAME = "NamingAuthority";
    protected NamingAuthority namingAuthority = null;

    public IdentifiersNAServiceImpl() throws RemoteException {
        super();

        try {
            String naConfigurationFile = getConfiguration().getNaConfigurationFile();
            String naProperties = getConfiguration().getNaPropertiesFile();
            FileSystemResource naConfResource = new FileSystemResource(naConfigurationFile);
            FileSystemResource naPropertiesResource = new FileSystemResource(naProperties);

            XmlBeanFactory factory = new XmlBeanFactory(naConfResource);
            PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
            cfg.setLocation(naPropertiesResource);
            cfg.postProcessBeanFactory(factory);

            this.namingAuthority = (NamingAuthority) factory.getBean(NA_BEAN_NAME, NamingAuthority.class);

        } catch (Exception e) {
            String message = "Problem inititializing NamingAuthority while loading configuration:" + e.getMessage();
            LOG.error(message, e);
            throw new RemoteException(message, e);
        }
    }

    // TODO: handle all the exceptions appropriately, returning faults as
    // necessary
  public org.apache.axis.types.URI createIdentifier(namingauthority.IdentifierValues identifierValues) throws RemoteException {
	   try {
		   java.net.URI identifier = namingAuthority.createIdentifier(MappingUtil.map(identifierValues));
		   return new org.apache.axis.types.URI(identifier.toString());
	   } catch (Exception e) {
	      e.printStackTrace();
	      throw new RemoteException(e.toString());
	   }
    }

    // TODO: handle all the exceptions appropriately, returning faults as
    // necessary
  public namingauthority.IdentifierValues resolveIdentifier(org.apache.axis.types.URI identifier) throws RemoteException {
    	try {
    		return MappingUtil.map(namingAuthority.resolveIdentifier( new URI(identifier.toString() )));
    	} catch(Exception e) {
    		e.printStackTrace();
    		throw new RemoteException(e.toString());
    	}
    }

}
