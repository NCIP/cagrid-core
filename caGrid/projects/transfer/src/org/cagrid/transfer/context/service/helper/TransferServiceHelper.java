package org.cagrid.transfer.context.service.helper;

import gov.nih.nci.cagrid.common.Utils;

import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.rmi.RemoteException;

import org.apache.axis.message.addressing.ReferencePropertiesType;
import org.cagrid.transfer.context.service.globus.resource.TransferServiceContextResource;
import org.cagrid.transfer.context.service.globus.resource.TransferServiceContextResourceHome;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.cagrid.transfer.descriptor.DataDescriptor;
import org.globus.wsrf.impl.SimpleResourceKey;
import org.globus.wsrf.utils.AddressingUtils;


/**
 * This class is the access mechanism for creating transfer resources which are
 * used to represent a data that is inbound or outbound. This class contains a
 * series of helper methods that will enable the user to stage data to be
 * transfered or create a resource where data can be uploaded.
 * 
 * @author hastings
 */
public class TransferServiceHelper {

    /**
     * This method is called to create a transfer resource that will be loaded
     * from the client
     * 
     * @param dd
     *            optional metadata about that data which is being staged
     * @return Reference to the resource which was created to represent your
     *         transfer data
     * @throws RemoteException
     */
    public static org.cagrid.transfer.context.stubs.types.TransferServiceContextReference createTransferContext(
        DataDescriptor dd, DataStagedCallback callback) throws RemoteException {
        org.apache.axis.message.addressing.EndpointReferenceType epr = new org.apache.axis.message.addressing.EndpointReferenceType();
        org.cagrid.transfer.context.service.globus.resource.TransferServiceContextResourceHome home = null;
        org.globus.wsrf.ResourceKey resourceKey = null;
        org.apache.axis.MessageContext ctx = org.apache.axis.MessageContext.getCurrentContext();

        String servicePath = ctx.getTargetService();
        String homeName = "java:comp/env/services/cagrid/TransferServiceContext/home";

        try {
            javax.naming.Context initialContext = new javax.naming.InitialContext();
            home = (org.cagrid.transfer.context.service.globus.resource.TransferServiceContextResourceHome) initialContext
                .lookup(homeName);
            resourceKey = home.createResource();

            // Grab the newly created resource
            org.cagrid.transfer.context.service.globus.resource.TransferServiceContextResource thisResource = (org.cagrid.transfer.context.service.globus.resource.TransferServiceContextResource) home
                .find(resourceKey);

            thisResource.setSecurityDescriptor(gov.nih.nci.cagrid.introduce.servicetools.security.SecurityUtils
                .createCreatorOnlyResourceSecurityDescriptor());
            thisResource.stage(dd, callback);

            String transportURL = (String) ctx.getProperty(org.apache.axis.MessageContext.TRANS_URL);
            transportURL = transportURL.substring(0, transportURL.lastIndexOf('/') + 1);
            transportURL += "TransferServiceContext";
            epr = org.globus.wsrf.utils.AddressingUtils.createEndpointReference(transportURL, resourceKey);
        } catch (Exception e) {
            throw new RemoteException("Error looking up TransferServiceContext home:" + e.getMessage(), e);
        }

        // return the typed EPR
        org.cagrid.transfer.context.stubs.types.TransferServiceContextReference ref = new org.cagrid.transfer.context.stubs.types.TransferServiceContextReference();
        ref.setEndpointReference(epr);

        return ref;
    }


    /**
     * This method is to be called if a file exists that contains the data that
     * is desired to be transfered.
     * 
     * @param file
     *            file containing the data to be transfered
     * @param dd
     *            optional metadata about that data which is being staged
     * @param deleteFileOnDestroy
     *            if set to true will remove the data file after the resource is
     *            destroyed, otherwise the file will remain
     * @return Reference to the resource which was created to represent your
     *         transfer data
     * @throws RemoteException
     */
    public static org.cagrid.transfer.context.stubs.types.TransferServiceContextReference createTransferContext(
        File file, DataDescriptor dd, boolean deleteFileOnDestroy) throws RemoteException {
        org.apache.axis.message.addressing.EndpointReferenceType epr = new org.apache.axis.message.addressing.EndpointReferenceType();
        org.cagrid.transfer.context.service.globus.resource.TransferServiceContextResourceHome home = null;
        org.globus.wsrf.ResourceKey resourceKey = null;
        org.apache.axis.MessageContext ctx = org.apache.axis.MessageContext.getCurrentContext();

        String servicePath = ctx.getTargetService();
        String homeName = "java:comp/env/services/cagrid/TransferServiceContext/home";

        try {
            javax.naming.Context initialContext = new javax.naming.InitialContext();
            home = (org.cagrid.transfer.context.service.globus.resource.TransferServiceContextResourceHome) initialContext
                .lookup(homeName);
            resourceKey = home.createResource();

            // Grab the newly created resource
            org.cagrid.transfer.context.service.globus.resource.TransferServiceContextResource thisResource = (org.cagrid.transfer.context.service.globus.resource.TransferServiceContextResource) home
                .find(resourceKey);

            thisResource.setSecurityDescriptor(gov.nih.nci.cagrid.introduce.servicetools.security.SecurityUtils
                .createCreatorOnlyResourceSecurityDescriptor());
            thisResource.stage(file, dd, deleteFileOnDestroy);

            String transportURL = (String) ctx.getProperty(org.apache.axis.MessageContext.TRANS_URL);
            transportURL = transportURL.substring(0, transportURL.lastIndexOf('/') + 1);
            transportURL += "TransferServiceContext";
            epr = org.globus.wsrf.utils.AddressingUtils.createEndpointReference(transportURL, resourceKey);
        } catch (Exception e) {
            throw new RemoteException("Error looking up TransferServiceContext home:" + e.getMessage(), e);
        }

        // return the typed EPR
        org.cagrid.transfer.context.stubs.types.TransferServiceContextReference ref = new org.cagrid.transfer.context.stubs.types.TransferServiceContextReference();
        ref.setEndpointReference(epr);

        return ref;
    }


    /**
     * This method will datet the data in the byteArray and stage it for
     * transfer.
     * 
     * @param data
     *            byte array container the data to be staged
     * @param dd
     *            optional metadata about that data which is being staged
     * @return Reference to the resource which was created to represent your
     *         transfer data
     * @throws RemoteException
     */
    public static org.cagrid.transfer.context.stubs.types.TransferServiceContextReference createTransferContext(
        byte[] data, DataDescriptor dd) throws RemoteException {
        org.apache.axis.message.addressing.EndpointReferenceType epr = new org.apache.axis.message.addressing.EndpointReferenceType();
        org.cagrid.transfer.context.service.globus.resource.TransferServiceContextResourceHome home = null;
        org.globus.wsrf.ResourceKey resourceKey = null;
        org.apache.axis.MessageContext ctx = org.apache.axis.MessageContext.getCurrentContext();
        String servicePath = ctx.getTargetService();
        String homeName = "java:comp/env/services/cagrid/TransferServiceContext/home";

        try {
            javax.naming.Context initialContext = new javax.naming.InitialContext();
            home = (org.cagrid.transfer.context.service.globus.resource.TransferServiceContextResourceHome) initialContext
                .lookup(homeName);
            resourceKey = home.createResource();

            // Grab the newly created resource
            org.cagrid.transfer.context.service.globus.resource.TransferServiceContextResource thisResource = (org.cagrid.transfer.context.service.globus.resource.TransferServiceContextResource) home
                .find(resourceKey);

            thisResource.setSecurityDescriptor(gov.nih.nci.cagrid.introduce.servicetools.security.SecurityUtils
                .createCreatorOnlyResourceSecurityDescriptor());
            thisResource.stage(data, dd);

            String transportURL = (String) ctx.getProperty(org.apache.axis.MessageContext.TRANS_URL);
            transportURL = transportURL.substring(0, transportURL.lastIndexOf('/') + 1);
            transportURL += "TransferServiceContext";
            epr = org.globus.wsrf.utils.AddressingUtils.createEndpointReference(transportURL, resourceKey);
        } catch (Exception e) {
            throw new RemoteException("Error looking up TransferServiceContext home:" + e.getMessage(), e);
        }

        // return the typed EPR
        org.cagrid.transfer.context.stubs.types.TransferServiceContextReference ref = new org.cagrid.transfer.context.stubs.types.TransferServiceContextReference();
        ref.setEndpointReference(epr);

        return ref;
    }


    /**
     * This method will read from the input stream and persist this data for
     * transfering.
     * 
     * @param is
     *            input stream that will be read to stage the data
     * @param dd
     *            optional metadata about that data which is being staged
     * @return Reference to the resource which was created to represent your
     *         transfer data
     * @throws RemoteException
     */
    public static org.cagrid.transfer.context.stubs.types.TransferServiceContextReference createTransferContext(
        InputStream is, DataDescriptor dd) throws RemoteException {
        org.apache.axis.message.addressing.EndpointReferenceType epr = new org.apache.axis.message.addressing.EndpointReferenceType();
        org.cagrid.transfer.context.service.globus.resource.TransferServiceContextResourceHome home = null;
        org.globus.wsrf.ResourceKey resourceKey = null;
        org.apache.axis.MessageContext ctx = org.apache.axis.MessageContext.getCurrentContext();
        String servicePath = ctx.getTargetService();
        String homeName = "java:comp/env/services/cagrid/TransferServiceContext/home";

        try {
            javax.naming.Context initialContext = new javax.naming.InitialContext();
            home = (org.cagrid.transfer.context.service.globus.resource.TransferServiceContextResourceHome) initialContext
                .lookup(homeName);
            resourceKey = home.createResource();

            // Grab the newly created resource
            org.cagrid.transfer.context.service.globus.resource.TransferServiceContextResource thisResource = (org.cagrid.transfer.context.service.globus.resource.TransferServiceContextResource) home
                .find(resourceKey);

            thisResource.setSecurityDescriptor(gov.nih.nci.cagrid.introduce.servicetools.security.SecurityUtils
                .createCreatorOnlyResourceSecurityDescriptor());
            thisResource.stage(is, dd);

            String transportURL = (String) ctx.getProperty(org.apache.axis.MessageContext.TRANS_URL);
            transportURL = transportURL.substring(0, transportURL.lastIndexOf('/') + 1);
            transportURL += "TransferServiceContext";
            epr = org.globus.wsrf.utils.AddressingUtils.createEndpointReference(transportURL, resourceKey);
        } catch (Exception e) {
            throw new RemoteException("Error looking up TransferServiceContext home:" + e.getMessage(), e);
        }

        // return the typed EPR
        org.cagrid.transfer.context.stubs.types.TransferServiceContextReference ref = new org.cagrid.transfer.context.stubs.types.TransferServiceContextReference();
        ref.setEndpointReference(epr);

        return ref;
    }


    /**
     * Takes in a transfer context resource reference and can lookup the resource and 
     * return it so that the resource object can be manipulated.
     * 
     * @param ref
     * @return
     * @throws Exception
     */
    public static TransferServiceContextResource getTransferServiceContextResource(TransferServiceContextReference ref)
        throws Exception {
        
        String homeName = "java:comp/env/services/cagrid/TransferServiceContext/home";
        javax.naming.Context initialContext = new javax.naming.InitialContext();
        TransferServiceContextResourceHome  home = (TransferServiceContextResourceHome) initialContext.lookup(homeName);
        ReferencePropertiesType props = ref.getEndpointReference().getProperties();
        String keystr = props.get_any()[0].getAsDOM().getTextContent();
        System.out.println("RESOURCE KEYS: " + keystr);
        SimpleResourceKey key = new SimpleResourceKey(home.getKeyTypeName(),keystr);
        TransferServiceContextResource resource = home.getResource(key);
        return resource;
    }

}
