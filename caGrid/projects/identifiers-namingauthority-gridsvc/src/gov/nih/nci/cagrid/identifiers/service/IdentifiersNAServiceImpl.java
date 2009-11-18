package gov.nih.nci.cagrid.identifiers.service;

import gov.nih.nci.cagrid.identifiers.common.MappingUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;

import org.cagrid.identifiers.namingauthority.InvalidIdentifierException;
import org.cagrid.identifiers.namingauthority.NamingAuthority;
import org.cagrid.identifiers.namingauthority.NamingAuthorityConfigurationException;
import org.cagrid.identifiers.namingauthority.NamingAuthorityLoader;


/**
 * TODO:I am the service side implementation class. IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.3
 */
public class IdentifiersNAServiceImpl extends IdentifiersNAServiceImplBase {

    private NamingAuthority namingAuthority;


    public IdentifiersNAServiceImpl() throws RemoteException {
        super();

        try {
            //TODO: replace with service properties to load properties file and spring file (as GME does it)
            namingAuthority = new NamingAuthorityLoader().getNamingAuthority();
        } catch (NamingAuthorityConfigurationException e) {
            throw new RemoteException(e.getMessage(),e);
        }

        System.out.println("Initializing naming authority with prefix ["
            + namingAuthority.getConfiguration().getPrefix() + "]");

        namingAuthority.initialize();
    }


    // TODO: fix the method to return a URI or an "Identifier"
    // TODO: handle all the exceptions appropriately, returning faults as
    // necessary
    public java.lang.String createIdentifier(gov.nih.nci.cagrid.identifiers.TypeValuesMap typeValues)
        throws RemoteException {
        try {
            return namingAuthority.createIdentifier(MappingUtil.toIdentifierValues(typeValues)).toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException(e.toString());
        }
    }


    // TODO: fix the method to take a URI or an "Identifier"
    // TODO: handle all the exceptions appropriately, returning faults as
    // necessary
    public gov.nih.nci.cagrid.identifiers.TypeValuesMap getTypeValues(java.lang.String identifier)
        throws RemoteException {
        try {
            return MappingUtil.toTypeValuesMap(namingAuthority.resolveIdentifier(new URI(identifier)));
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new RemoteException(e.getMessage(), e);
        } catch (InvalidIdentifierException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new RemoteException(e.getMessage(), e);

        } catch (NamingAuthorityConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new RemoteException(e.getMessage(), e);

        }
    }
}
