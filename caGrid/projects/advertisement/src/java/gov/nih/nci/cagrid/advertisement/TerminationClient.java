package gov.nih.nci.cagrid.advertisement;

import gov.nih.nci.cagrid.advertisement.exceptions.UnregistrationException;
import gov.nih.nci.cagrid.metadata.ResourcePropertyHelper;
import gov.nih.nci.cagrid.metadata.XPathUtils;
import gov.nih.nci.cagrid.metadata.exceptions.QueryInvalidException;
import gov.nih.nci.cagrid.metadata.exceptions.RemoteResourcePropertyRetrievalException;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Stub;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.wsrf.WSRFConstants;
import org.globus.wsrf.encoding.DeserializationException;
import org.globus.wsrf.encoding.ObjectDeserializer;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.globus.wsrf.encoding.SerializationException;
import org.globus.wsrf.utils.AnyHelper;
import org.oasis.wsrf.lifetime.ResourceUnknownFaultType;
import org.oasis.wsrf.lifetime.ScheduledResourceTermination;
import org.oasis.wsrf.lifetime.SetTerminationTime;
import org.oasis.wsrf.lifetime.SetTerminationTimeResponse;
import org.oasis.wsrf.lifetime.TerminationTimeChangeRejectedFaultType;
import org.oasis.wsrf.lifetime.UnableToSetTerminationTimeFaultType;
import org.oasis.wsrf.lifetime.WSResourceLifetimeServiceAddressingLocator;


/**
 * TerminationClient
 * 
 * @author oster
 * @created Mar 30, 2007 1:11:10 PM
 * @version $Id: multiscaleEclipseCodeTemplates.xml,v 1.1 2007/03/02 14:35:01
 *          dervin Exp $
 */
public class TerminationClient {
    protected EndpointReferenceType indexEPR;

    private String securityDescriptorFile;

    protected static Log LOG = LogFactory.getLog(TerminationClient.class.getName());

    protected static final String wssg = WSRFConstants.SERVICEGROUP_PREFIX;
    protected static final String add = "add";

    // Map the prefixes to there namepsaces
    protected static Map<String, String> nsMap = new HashMap<String, String>();
    static {
        nsMap.put(wssg, WSRFConstants.SERVICEGROUP_NS);
        nsMap.put(add, "http://schemas.xmlsoap.org/ws/2004/03/addressing");
    }


    /**
     * Uses the specified Index Service
     * 
     * @param indexEPR
     *            the EPR to the Index Service to use
     */
    public TerminationClient(EndpointReferenceType indexEPR) {
        this.indexEPR = indexEPR;
    }


    public int unregister(EndpointReferenceType registeredEPR) throws UnregistrationException {
        // look for entries from the service
        EndpointReferenceType[] entryEPRs = locateEntryEPR(registeredEPR);
        if (entryEPRs == null) {
            LOG.debug("No entries found.");
            return 0;
        }
        for (EndpointReferenceType entryEPR : entryEPRs) {
            try {
                Calendar terminateEntry = terminateEntry(entryEPR);
                LOG.debug("Scheduled (" + printEPR(registeredEPR) + ") to termiante at (" + terminateEntry.getTime()
                    + ")");
            } catch (UnableToSetTerminationTimeFaultType e) {
                String message = "Problem setting remote termination time.";
                LOG.error(message, e);
                throw new UnregistrationException(message, e);
            } catch (ResourceUnknownFaultType e) {
                String message = "Problem setting remote termination time, resource not valid.";
                LOG.error(message, e);
                throw new UnregistrationException(message, e);
            } catch (TerminationTimeChangeRejectedFaultType e) {
                String message = "Problem setting remote termination time, invalid termination time.";
                LOG.error(message, e);
                throw new UnregistrationException(message, e);
            } catch (RemoteException e) {
                String message = "Problem accessing remote service.";
                LOG.error(message, e);
                throw new UnregistrationException(message, e);
            } catch (ServiceException e) {
                String message = "Problem accessing remote service.";
                LOG.error(message, e);
                throw new UnregistrationException(message, e);
            }
        }
        return entryEPRs.length;
    }


    /**
     * @param registeredEPR
     * @return The EPR as a serialized string
     * @throws SerializationException
     */
    private static String printEPR(EndpointReferenceType epr) {
        if (epr == null) {
            return null;
        }
        String result = epr.getAddress().toString();
        if (epr.getProperties() != null && epr.getProperties().get_any() != null
            && epr.getProperties().get_any().length > 0) {

            try {
                result = ObjectSerializer.toString(epr, EndpointReferenceType.getTypeDesc().getXmlType());
            } catch (SerializationException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    protected EndpointReferenceType[] locateEntryEPR(EndpointReferenceType registeredEPR)
        throws UnregistrationException {
        LOG.debug("Looking for entry for:" + printEPR(registeredEPR));

        String xpath = "/*/" + wssg + ":Entry[" + wssg + ":MemberServiceEPR/" + add + ":Address/text()='"
            + registeredEPR.getAddress().toString() + "'" + "]/" + wssg + ":ServiceGroupEntryEPR";
        LOG.debug("Logical xpath: " + xpath);
        String translatedxpath = XPathUtils.translateXPath(xpath, nsMap);
        LOG.debug("Issuing actual query: " + translatedxpath);

        EndpointReferenceType[] eprs = null;

        // query the service and deser the results
        MessageElement[] elements = null;
        try {
            elements = ResourcePropertyHelper.queryResourceProperties(this.indexEPR, translatedxpath);
        } catch (RemoteResourcePropertyRetrievalException e) {
            String message = "Problem determining resource entry; cannot access Index Service.";
            LOG.error(message, e);
            throw new UnregistrationException(message, e);
        } catch (QueryInvalidException e) {
            String message = "Problem determining resource entry; query sent (" + translatedxpath + ")was invalid.";
            LOG.error(message, e);
            throw new UnregistrationException(message, e);
        }
        Object[] objects = null;
        try {
            objects = ObjectDeserializer.toObject(elements, EndpointReferenceType.class);
        } catch (DeserializationException e) {
            String message = "Problem determining resource entry; cannot deserialize results.";
            LOG.error(message, e);
            throw new UnregistrationException(message, e);

        }

        // if we got results, cast them into what we are expected to return
        if (objects != null) {
            eprs = new EndpointReferenceType[objects.length];
            System.arraycopy(objects, 0, eprs, 0, objects.length);
        }

        return eprs;
    }


    protected Calendar terminateEntry(EndpointReferenceType entryEPR) throws ServiceException,
        UnableToSetTerminationTimeFaultType, ResourceUnknownFaultType, TerminationTimeChangeRejectedFaultType,
        RemoteException {

        if (LOG.isDebugEnabled()) {
            debugPrintResource("Terminating entry for:", entryEPR);
        }

        WSResourceLifetimeServiceAddressingLocator lifetimeloc = new WSResourceLifetimeServiceAddressingLocator();
        ScheduledResourceTermination lifetimePort = lifetimeloc.getScheduledResourceTerminationPort(entryEPR);
        setSecurityProperties((Stub) lifetimePort);

        SetTerminationTime setTermTimeReq = new SetTerminationTime();
        // terminate now (in 5 seconds)
        Calendar term = Calendar.getInstance();
        term.add(Calendar.SECOND, 5);
        setTermTimeReq.setRequestedTerminationTime(term);
        SetTerminationTimeResponse response = lifetimePort.setTerminationTime(setTermTimeReq);

        LOG.debug("Set to terminate at:" + response.getNewTerminationTime().getTime());
        if (LOG.isDebugEnabled()) {
            debugPrintResource("After terminating entry:", entryEPR);
        }

        return response.getNewTerminationTime();
    }


    protected void setSecurityProperties(Stub port) {
        if (port == null) {
            return;
        }
        try {
            if (this.securityDescriptorFile != null) {
                (port)._setProperty(org.globus.wsrf.security.Constants.CLIENT_DESCRIPTOR_FILE,
                    this.securityDescriptorFile);
            } else { // default to anonymous
                port._setProperty(org.globus.wsrf.security.Constants.GSI_ANONYMOUS, Boolean.TRUE);
                port._setProperty(org.globus.wsrf.security.Constants.AUTHORIZATION,
                    org.globus.wsrf.impl.security.authorization.NoAuthorization.getInstance());
                port._setProperty(org.globus.axis.gsi.GSIConstants.GSI_AUTHORIZATION,
                    org.globus.gsi.gssapi.auth.NoAuthorization.getInstance());
            }

        } catch (Exception e) {
            LOG.warn("Exception while setting security properties on stub" + e.toString());
        }
    }


    /**
     * @param entryEPR
     */
    private void debugPrintResource(String header, EndpointReferenceType entryEPR) {
        if (LOG.isDebugEnabled()) {
            try {
                LOG.debug(header + " " + printEPR(entryEPR));
                MessageElement[] rps = ResourcePropertyHelper.queryResourceProperties(entryEPR, "/");
                StringBuffer sb = new StringBuffer("Resource Properties of entry: ");
                if (rps != null) {
                    for (MessageElement elm : rps) {
                        sb.append("\n" + AnyHelper.toString(elm));
                    }

                } else {
                    sb.append("none");
                }
                LOG.debug(sb.toString());
            } catch (Exception e) {
                LOG.debug("Problem querying RPs for debug purposes:", e);
            }
        }
    }


    /**
     * @param securityDescriptorFile
     */
    public void setSecurityDescriptorFile(String securityDescriptorFile) {
        this.securityDescriptorFile = securityDescriptorFile;
    }

}
