package org.cagrid.gme.service;

import java.net.URI;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gme.domain.XMLSchema;
import org.cagrid.gme.domain.XMLSchemaNamespace;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;

/**
 * The service side implementation class of the GlobalModelExchange,managed by
 * Introduce. This mostly just delegates the operations to a Spring-loaded GME
 * implementation of the GME and does any necessary mapping to/from Axis types
 * or arrays to the GME interface.
 * 
 * @created by Introduce Toolkit version 1.2
 */
public class GlobalModelExchangeImpl extends GlobalModelExchangeImplBase {

    protected static Log LOG = LogFactory.getLog(GlobalModelExchangeImpl.class.getName());

    protected static final String GME_BEAN_NAME = "gme";
    protected GME gme = null;

    public GlobalModelExchangeImpl() throws RemoteException {
        super();
        try {
            String gmeConfigurationFile = getConfiguration().getGmeConfigurationFile();
            String gmeProperties = getConfiguration().getGmePropertiesFile();
            FileSystemResource gmeConfResource = new FileSystemResource(gmeConfigurationFile);
            FileSystemResource gmePropertiesResource = new FileSystemResource(gmeProperties);

            XmlBeanFactory factory = new XmlBeanFactory(gmeConfResource);
            PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
            cfg.setLocation(gmePropertiesResource);
            cfg.postProcessBeanFactory(factory);

            this.gme = (GME) factory.getBean(GME_BEAN_NAME, GME.class);

        } catch (Exception e) {
            String message = "Problem inititializing GME while loading configuration:" + e.getMessage();
            LOG.error(message, e);
            throw new RemoteException(message, e);
        }
    }

    public GlobalModelExchangeImpl(GME gme) throws RemoteException {
        this.gme = gme;
    }

  public void publishXMLSchemas(org.cagrid.gme.domain.XMLSchema[] schemas) throws RemoteException, org.cagrid.gme.stubs.types.InvalidSchemaSubmissionFault {
        List<XMLSchema> list = null;
        if (schemas != null) {
            list = Arrays.asList(schemas);
        }
        this.gme.publishSchemas(list);
    }

  public org.cagrid.gme.domain.XMLSchema getXMLSchema(org.cagrid.gme.domain.XMLSchemaNamespace targetNamespace) throws RemoteException, org.cagrid.gme.stubs.types.NoSuchNamespaceExistsFault {
        URI uri = null;
        if (targetNamespace != null) {
            uri = targetNamespace.getURI();
        }
        return this.gme.getSchema(uri);
    }

  public org.cagrid.gme.domain.XMLSchemaNamespace[] getXMLSchemaNamespaces() throws RemoteException {
        Collection<URI> namespaces = this.gme.getNamespaces();
        XMLSchemaNamespace[] result = new XMLSchemaNamespace[namespaces.size()];
        int i = 0;
        for (URI namespace : namespaces) {
            result[i++] = new XMLSchemaNamespace(namespace);
        }
        return result;
    }

  public void deleteXMLSchemas(org.cagrid.gme.domain.XMLSchemaNamespace[] targetNamespaces) throws RemoteException, org.cagrid.gme.stubs.types.NoSuchNamespaceExistsFault, org.cagrid.gme.stubs.types.UnableToDeleteSchemaFault {
        List<URI> schemaNamespaces = new ArrayList<URI>();
        if (targetNamespaces != null) {
            for (XMLSchemaNamespace ns : targetNamespaces) {
                schemaNamespaces.add(ns.getURI());
            }
        }
        this.gme.deleteSchemas(schemaNamespaces);
    }

  public org.cagrid.gme.domain.XMLSchemaBundle getXMLSchemaAndDependencies(org.cagrid.gme.domain.XMLSchemaNamespace targetNamespace) throws RemoteException, org.cagrid.gme.stubs.types.NoSuchNamespaceExistsFault {

        URI uri = null;
        if (targetNamespace != null) {
            uri = targetNamespace.getURI();
        }
        return this.gme.getSchemBundle(uri);
    }

  public org.cagrid.gme.domain.XMLSchemaNamespace[] getImportedXMLSchemaNamespaces(org.cagrid.gme.domain.XMLSchemaNamespace targetNamespace) throws RemoteException, org.cagrid.gme.stubs.types.NoSuchNamespaceExistsFault {
        URI uri = null;
        if (targetNamespace != null) {
            uri = targetNamespace.getURI();
        }

        XMLSchemaNamespace[] result = null;
        Collection<URI> importedNamespaces = this.gme.getImportedNamespaces(uri);

        if (importedNamespaces != null) {
            result = new XMLSchemaNamespace[importedNamespaces.size()];
            Iterator<URI> iterator = importedNamespaces.iterator();
            for (int i = 0; i < importedNamespaces.size(); i++) {
                result[i] = new XMLSchemaNamespace(iterator.next());
            }
        }

        return result;
    }

  public org.cagrid.gme.domain.XMLSchemaNamespace[] getImportingXMLSchemaNamespaces(org.cagrid.gme.domain.XMLSchemaNamespace targetNamespace) throws RemoteException, org.cagrid.gme.stubs.types.NoSuchNamespaceExistsFault {

        URI uri = null;
        if (targetNamespace != null) {
            uri = targetNamespace.getURI();
        }

        XMLSchemaNamespace[] result = null;
        Collection<URI> importingNamespaces = this.gme.getImportingNamespaces(uri);

        if (importingNamespaces != null) {
            result = new XMLSchemaNamespace[importingNamespaces.size()];
            Iterator<URI> iterator = importingNamespaces.iterator();
            for (int i = 0; i < importingNamespaces.size(); i++) {
                result[i] = new XMLSchemaNamespace(iterator.next());
            }
        }

        return result;
    }

}
