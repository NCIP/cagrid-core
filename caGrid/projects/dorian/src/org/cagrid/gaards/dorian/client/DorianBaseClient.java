package org.cagrid.gaards.dorian.client;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.metadata.ResourcePropertyHelper;
import gov.nih.nci.cagrid.metadata.ServiceMetadata;
import gov.nih.nci.cagrid.metadata.exceptions.InvalidResourcePropertyException;
import gov.nih.nci.cagrid.metadata.exceptions.ResourcePropertyRetrievalException;

import java.io.InputStream;
import java.io.StringReader;
import java.rmi.RemoteException;

import javax.xml.namespace.QName;

import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.gaards.dorian.policy.DorianPolicy;
import org.globus.gsi.GlobusCredential;
import org.globus.wsrf.impl.security.authorization.Authorization;
import org.globus.wsrf.utils.XmlUtils;
import org.w3c.dom.Element;


public abstract class DorianBaseClient {

    public static final String VERSION_UNKNOWN = "UNKNOWN";
    public static final String VERSION_1_0 = "1.0";
    public static final String VERSION_1_1 = "1.1";
    public static final String VERSION_1_2 = "1.2";
    public static final String VERSION_1_3 = "1.3";
    public static final String VERSION_1_4 = "1.4";

    public static final QName SERVICE_METADATA = new QName("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata",
        "ServiceMetadata");

    public static final QName POLICY = new QName("http://cagrid.nci.nih.gov/1/dorian-policy", "DorianPolicy");

    private DorianClient client;
    private ServiceMetadata serviceMetadata;
    private String serviceURL;
    private DorianPolicy policy;


    public DorianBaseClient(String serviceURL) throws MalformedURIException, RemoteException {
        this(serviceURL, null);
    }


    public DorianBaseClient(String serviceURI, GlobusCredential cred) throws MalformedURIException, RemoteException {
        this.serviceURL = serviceURI;
        client = new DorianClient(serviceURI, cred);
    }


    public DorianBaseClient(String serviceURI, GlobusCredential cred, boolean anonymousPrefered)
        throws MalformedURIException, RemoteException {
        this.serviceURL = serviceURI;
        client = new DorianClient(serviceURI, cred);
        client.setAnonymousPrefered(anonymousPrefered);
    }


    protected DorianClient getClient() {
        return this.client;
    }


    /**
     * This method specifies an authorization policy that the client should use
     * for authorizing the server that it connects to.
     * 
     * @param authorization
     *            The authorization policy to enforce
     */

    public void setAuthorization(Authorization authorization) {
        client.setAuthorization(authorization);
    }


    /**
     * This method obtains the service metadata for the service.
     * 
     * @return The service metadata.
     * @throws ResourcePropertyRetrievalException
     */

    public ServiceMetadata getServiceMetadata() throws InvalidResourcePropertyException,
        ResourcePropertyRetrievalException {
        if (serviceMetadata == null) {
            Element resourceProperty = null;

            InputStream wsdd = getClass().getResourceAsStream("client-config.wsdd");
            resourceProperty = ResourcePropertyHelper.getResourceProperty(client.getEndpointReference(),
                SERVICE_METADATA, wsdd);

            try {
                this.serviceMetadata = (ServiceMetadata) Utils.deserializeObject(new StringReader(XmlUtils
                    .toString(resourceProperty)), ServiceMetadata.class);
            } catch (Exception e) {
                throw new ResourcePropertyRetrievalException("Unable to deserailize: " + e.getMessage(), e);
            }
        }
        return this.serviceMetadata;
    }


    /**
     * This method returns the version of the Grid Service
     * 
     * @return The version of the grid service.
     * @throws InvalidResourcePropertyException
     * @throws ResourcePropertyRetrievalException
     */

    public String getServiceVersion() throws InvalidResourcePropertyException, ResourcePropertyRetrievalException {
        ServiceMetadata sm = getServiceMetadata();
        if (sm == null) {
            return VERSION_UNKNOWN;
        } else {
            if (sm.getServiceDescription() != null) {
                if (sm.getServiceDescription().getService() != null) {
                    if (sm.getServiceDescription().getService().getVersion() != null) {
                        return sm.getServiceDescription().getService().getVersion();
                    } else {
                        return VERSION_UNKNOWN;
                    }
                } else {
                    return VERSION_UNKNOWN;
                }
            } else {
                return VERSION_UNKNOWN;
            }

        }
    }


    public DorianPolicy getPolicy() throws InvalidResourcePropertyException, ResourcePropertyRetrievalException {
        if (this.policy == null) {
            String version = getServiceVersion();
            if (version.equals(VERSION_1_0) || version.equals(VERSION_1_1) || version.equals(VERSION_1_2)
                || version.equals(VERSION_1_3) || version.equals(VERSION_UNKNOWN)) {
                return null;
            } else {
                Element resourceProperty = null;
                InputStream wsdd = getClass().getResourceAsStream("client-config.wsdd");
                resourceProperty = ResourcePropertyHelper.getResourceProperty(client.getEndpointReference(), POLICY,
                    wsdd);

                try {
                    this.policy = (DorianPolicy) Utils.deserializeObject(new StringReader(XmlUtils
                        .toString(resourceProperty)), DorianPolicy.class);
                } catch (Exception e) {
                    throw new ResourcePropertyRetrievalException("Unable to deserailize the Dorian Policy: "
                        + e.getMessage(), e);
                }

                return this.policy;
            }
        } else {
            return this.policy;
        }
    }


    public String getServiceURL() {
        return serviceURL;
    }

}
