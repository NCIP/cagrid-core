package gov.nih.nci.cagrid.identifiers.service;

import gov.nih.nci.cagrid.identifiers.common.IdentifiersNAUtil;
import gov.nih.nci.cagrid.identifiers.stubs.types.InvalidIdentifierFault;

import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.identifiers.namingauthority.InvalidIdentifierException;
import org.cagrid.identifiers.namingauthority.InvalidIdentifierValuesException;
import org.cagrid.identifiers.namingauthority.MaintainerNamingAuthority;
import org.cagrid.identifiers.namingauthority.NamingAuthorityConfigurationException;
import org.cagrid.identifiers.namingauthority.NamingAuthoritySecurityException;
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
    protected MaintainerNamingAuthority namingAuthority = null;

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

            this.namingAuthority = (MaintainerNamingAuthority) factory.getBean(NA_BEAN_NAME, MaintainerNamingAuthority.class);

        } catch (Exception e) {
            String message = "Problem inititializing NamingAuthority while loading configuration:" + e.getMessage();
            LOG.error(message, e);
            throw new RemoteException(message, e);
        }
    }

  public org.apache.axis.types.URI createIdentifier(namingauthority.IdentifierValues identifierValues) throws RemoteException, gov.nih.nci.cagrid.identifiers.stubs.types.NamingAuthorityConfigurationFault, gov.nih.nci.cagrid.identifiers.stubs.types.InvalidIdentifierValuesFault {
    	try {
    		java.net.URI identifier = namingAuthority.createIdentifier(null, IdentifiersNAUtil.map(identifierValues));
    		return new org.apache.axis.types.URI(identifier.toString());
    	} catch( InvalidIdentifierValuesException e) {
    		e.printStackTrace();
    		throw IdentifiersNAUtil.map(e);
    	} catch( NamingAuthorityConfigurationException e ) {
    		e.printStackTrace();
    		throw IdentifiersNAUtil.map(e);
    	} catch (Exception e) {
    		e.printStackTrace();
    		throw new RemoteException(e.toString());
    	}
    }

  public namingauthority.IdentifierValues resolveIdentifier(org.apache.axis.types.URI identifier) throws RemoteException, gov.nih.nci.cagrid.identifiers.stubs.types.NamingAuthorityConfigurationFault, gov.nih.nci.cagrid.identifiers.stubs.types.InvalidIdentifierFault {
    	try {
    		return IdentifiersNAUtil.map(namingAuthority.resolveIdentifier( null, new URI(identifier.toString() )));
    	} catch( InvalidIdentifierException e) {
    		e.printStackTrace();
    		throw IdentifiersNAUtil.map(e);
    	} catch( NamingAuthorityConfigurationException e ) {
    		e.printStackTrace();
    		throw IdentifiersNAUtil.map(e);
    	} catch(Exception e) {
    		e.printStackTrace();
    		throw new RemoteException(e.toString());
    	}
    }
  public void deleteKeys(org.apache.axis.types.URI identifier,java.lang.String[] keyList) throws RemoteException, gov.nih.nci.cagrid.identifiers.stubs.types.InvalidIdentifierFault, gov.nih.nci.cagrid.identifiers.stubs.types.NamingAuthorityConfigurationFault, gov.nih.nci.cagrid.identifiers.stubs.types.NamingAuthoritySecurityFault {
    
    try {
    	namingAuthority.deleteKeys(null, new URI(identifier.toString()), keyList);
	} catch (NamingAuthorityConfigurationException e) {
		e.printStackTrace();
		throw IdentifiersNAUtil.map(e);
	} catch (InvalidIdentifierValuesException e) {
		e.printStackTrace();
		throw IdentifiersNAUtil.map(e);
	} catch (InvalidIdentifierException e) {
		e.printStackTrace();
		throw IdentifiersNAUtil.map(e);
	} catch (NamingAuthoritySecurityException e) {
		e.printStackTrace();
		throw IdentifiersNAUtil.map(e);
	} catch (Exception e) {
		e.printStackTrace();
		throw new RemoteException(e.toString());
	}
  }

  public void createKeys(org.apache.axis.types.URI identifier,namingauthority.IdentifierValues identifierValues) throws RemoteException, gov.nih.nci.cagrid.identifiers.stubs.types.InvalidIdentifierFault, gov.nih.nci.cagrid.identifiers.stubs.types.NamingAuthorityConfigurationFault, gov.nih.nci.cagrid.identifiers.stubs.types.NamingAuthoritySecurityFault {
    try {
		namingAuthority.createKeys(null, new URI(identifier.toString()), IdentifiersNAUtil.map(identifierValues));
	} catch (NamingAuthorityConfigurationException e) {
		e.printStackTrace();
		throw IdentifiersNAUtil.map(e);
	} catch (InvalidIdentifierValuesException e) {
		e.printStackTrace();
		throw IdentifiersNAUtil.map(e);
	} catch (InvalidIdentifierException e) {
		e.printStackTrace();
		throw IdentifiersNAUtil.map(e);
	} catch (NamingAuthoritySecurityException e) {
		e.printStackTrace();
		throw IdentifiersNAUtil.map(e);
	} catch (Exception e) {
		e.printStackTrace();
		throw new RemoteException(e.toString());
	}
  }

  public void replaceKeys(org.apache.axis.types.URI identifier,namingauthority.IdentifierValues identifierValues) throws RemoteException, gov.nih.nci.cagrid.identifiers.stubs.types.InvalidIdentifierFault, gov.nih.nci.cagrid.identifiers.stubs.types.NamingAuthorityConfigurationFault, gov.nih.nci.cagrid.identifiers.stubs.types.NamingAuthoritySecurityFault {
    try {
		namingAuthority.replaceKeys(null, new URI(identifier.toString()), IdentifiersNAUtil.map(identifierValues));
    } catch (NamingAuthorityConfigurationException e) {
		e.printStackTrace();
		throw IdentifiersNAUtil.map(e);
	} catch (InvalidIdentifierValuesException e) {
		e.printStackTrace();
		throw IdentifiersNAUtil.map(e);
	} catch (InvalidIdentifierException e) {
		e.printStackTrace();
		throw IdentifiersNAUtil.map(e);
	} catch (NamingAuthoritySecurityException e) {
		e.printStackTrace();
		throw IdentifiersNAUtil.map(e);
	} catch (Exception e) {
		e.printStackTrace();
		throw new RemoteException(e.toString());
	}
  }

  public void deleteAllKeys(org.apache.axis.types.URI identifier) throws RemoteException, gov.nih.nci.cagrid.identifiers.stubs.types.InvalidIdentifierFault, gov.nih.nci.cagrid.identifiers.stubs.types.NamingAuthorityConfigurationFault, gov.nih.nci.cagrid.identifiers.stubs.types.NamingAuthoritySecurityFault {
	  try {
			namingAuthority.deleteAllKeys(null, new URI(identifier.toString()));
	    } catch (NamingAuthorityConfigurationException e) {
			e.printStackTrace();
			throw IdentifiersNAUtil.map(e);
		} catch (InvalidIdentifierValuesException e) {
			e.printStackTrace();
			throw IdentifiersNAUtil.map(e);
		} catch (InvalidIdentifierException e) {
			e.printStackTrace();
			throw IdentifiersNAUtil.map(e);
		} catch (NamingAuthoritySecurityException e) {
			e.printStackTrace();
			throw IdentifiersNAUtil.map(e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException(e.toString());
		}
  }

}
