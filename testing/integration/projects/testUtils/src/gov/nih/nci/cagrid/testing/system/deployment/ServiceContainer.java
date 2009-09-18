package gov.nih.nci.cagrid.testing.system.deployment;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.ZipUtilities;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;


/**
 * ServiceContainer Performs operations on a service container
 * 
 * @author David Ervin
 * @created Oct 12, 2007 9:37:44 AM
 * @version $Id: ServiceContainer.java,v 1.3 2008-11-07 17:59:14 dervin Exp $
 */
public abstract class ServiceContainer {

    protected ContainerProperties properties = null;

    protected boolean unpacked = false;
    protected boolean started = false;


    public ServiceContainer(ContainerProperties properties) {
        this.properties = properties;
    }


    public void unpackContainer() throws ContainerException {
        try {
            ZipUtilities.unzip(this.properties.getContainerZip(), this.properties.getContainerDirectory());
        } catch (IOException ex) {
            throw new ContainerException("Error unziping container: " + ex.getMessage(), ex);
        }
        this.unpacked = true;
    }


    public void deleteContainer() throws ContainerException {
        if (this.started) {
            throw new ContainerException("Cannot delete running container");
        }
        Utils.deleteDir(this.properties.getContainerDirectory());
    }


    public void startContainer() throws ContainerException {
        if (this.started) {
            throw new ContainerException("Container is already started");
        }
        if (!this.unpacked) {
            throw new ContainerException("Container has not been unpacked");
        }
        startup();
        this.started = true;
    }


    public void stopContainer() throws ContainerException {
        if (!this.unpacked) {
            throw new ContainerException("Container has not been unpacked");
        }
        shutdown();
        this.started = false;
    }


    public void deployService(File serviceDir) throws Exception {
        deployService(serviceDir, null);
    }


    public void deployService(File serviceDir, List<String> deployArgs) throws Exception {
        if (this.started) {
            throw new ContainerException("Container has already been started");
        }
        if (!this.unpacked) {
            throw new ContainerException("Container has not been unpacked");
        }
        deploy(serviceDir, deployArgs);
    }


    public boolean isStarted() {
        return this.started;
    }


    public boolean isUnpacked() {
        return this.unpacked;
    }


    public ContainerProperties getProperties() {
        return this.properties;
    }


    public synchronized URI getContainerBaseURI() throws MalformedURIException {
        String url = "";
        if (getProperties().isSecure()) {
            url += "https://";
        } else {
            url += "http://";
        }
        url += "localhost:" + getProperties().getPortPreference().getPort() + "/wsrf/services/";
        return new URI(url);
    }


    public synchronized EndpointReferenceType getServiceEPR(String servicePath) throws MalformedURIException {
        EndpointReferenceType epr = null;

        String url = getContainerBaseURI().toString() + servicePath;
        epr = new EndpointReferenceType(new Address(url));

        return epr;
    }


    protected abstract void startup() throws ContainerException;


    protected abstract void shutdown() throws ContainerException;


    protected abstract void deploy(File serviceDir, List<String> deployArgs) throws ContainerException;
}
