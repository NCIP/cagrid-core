package gov.nih.nci.cagrid.syncgts.core;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.security.OrderInsensitiveDN;
import gov.nih.nci.cagrid.gts.bean.TrustedAuthority;
import gov.nih.nci.cagrid.gts.bean.TrustedAuthorityFilter;
import gov.nih.nci.cagrid.gts.client.GTSClient;
import gov.nih.nci.cagrid.syncgts.bean.AddedTrustedCAs;
import gov.nih.nci.cagrid.syncgts.bean.ExcludedCAs;
import gov.nih.nci.cagrid.syncgts.bean.Message;
import gov.nih.nci.cagrid.syncgts.bean.MessageType;
import gov.nih.nci.cagrid.syncgts.bean.Messages;
import gov.nih.nci.cagrid.syncgts.bean.RemovedTrustedCAs;
import gov.nih.nci.cagrid.syncgts.bean.SyncDescription;
import gov.nih.nci.cagrid.syncgts.bean.SyncDescriptor;
import gov.nih.nci.cagrid.syncgts.bean.SyncReport;
import gov.nih.nci.cagrid.syncgts.bean.TrustedCA;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.security.GeneralSecurityException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.InvalidNameException;
import javax.xml.namespace.QName;

import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gaards.pki.CertUtil;
import org.globus.common.CoGProperties;
import org.globus.gsi.TrustedCertificatesLock;
import org.globus.wsrf.impl.security.authorization.IdentityAuthorization;
import org.projectmobius.common.MobiusDate;
import org.projectmobius.common.MobiusRunnable;

/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class SyncGTS {
    private final static QName TRUSTED_CA_QN = new QName(SyncGTSDefault.SYNC_GTS_NAMESPACE, "TrustedCA");
    private final static HashMap<String, OrderInsensitiveDN> orderInsensitiveDNCache = new HashMap<String, OrderInsensitiveDN>();

    private Map<String, TrustedCAFileListing> caListings;
    private Map<Integer, TrustedCAFileListing> listingsById;
    private static final Log logger = LogFactory.getLog(SyncGTS.class.getName());
    private List<Message> messages;
    private HistoryManager history;
    private static SyncGTS instance;
    private boolean lock = false;

    private SyncGTS() {
        this.history = new HistoryManager();
    }

    public synchronized static SyncGTS getInstance() {
        if (instance == null) {
            instance = new SyncGTS();
        }
        return instance;
    }

    private synchronized void reset() {
        this.caListings = null;
        this.listingsById = null;

        messages = new ArrayList<Message>();
    }

    private synchronized boolean getLock() {
        if (!lock) {
            lock = true;
            return lock;
        } else {
            return false;
        }
    }

    private synchronized void releaseLock() {
        lock = false;
    }

    public void syncAndResync(final SyncDescription description, boolean waitFirst) throws Exception {
        if (getLock()) {
            getRunner(description, waitFirst).run();
            releaseLock();
        } else {
            throw new Exception("Cannot sync unable to get lock.");
        }
    }

    public void syncAndResyncInBackground(final SyncDescription description, boolean waitFirst) throws Exception {
        if (getLock()) {
            Thread t = new Thread(getRunner(description, waitFirst));
            t.setDaemon(true);
            t.start();
            releaseLock();
        } else {
            throw new Exception("Cannot sync unable to get lock.");
        }
    }

    public SyncReport syncOnce(final SyncDescription description) throws Exception {
        if (getLock()) {
            SyncReport r = sync(description);
            releaseLock();
            return r;
        } else {
            throw new Exception("Cannot sync unable to get lock.");
        }
    }

    private MobiusRunnable getRunner(final SyncDescription description, final boolean waitFirst) {
        MobiusRunnable runner = new MobiusRunnable() {
            public void execute() {
                if (waitFirst) {
                    try {
                        Thread.sleep(description.getNextSync().intValue() * 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                while (true) {

                    sync(description);
                    try {
                        Thread.sleep(description.getNextSync().intValue() * 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        return runner;
    }

    private synchronized SyncReport sync(SyncDescription description) {
        SyncReport report = new SyncReport();
        try {
            reset();
            Set<String> unableToSync = new HashSet<String>();
            initializeReport(report, description);
            Map<String, TrustedCAListing> master = fetchTrustedAuthoritiesFromGTSServices(description, unableToSync);

            // Create a list of exclude certificate authorities and remove
            // all excluded certificates from the master list.
            Set<String> excluded = removeExcludedAuthorities(master, description);

            synchronized (TrustedCertificatesLock.getInstance()) {

                // Write the master list out and generate signing policy

                this.readInCurrentCADirectory(description);
                Set<String> completeCASetByName = new HashSet<String>();
                Set<String> completeCASetByHash = new HashSet<String>();
                // Remove all CAs from the ca directory and COMPLETE list that
                // except the following:
                // Any CA in the exclude list.
                // Any CA in the unable to sync list that is not in the master
                // list.
                int removeCount = 0;
                List<TrustedCA> removedList = new ArrayList<TrustedCA>();
                Iterator<TrustedCAFileListing> del = caListings.values().iterator();
              whileDelHasNext:
		while (del.hasNext()) {
                    TrustedCAFileListing fl = del.next();
                    TrustedCA ca = new TrustedCA();

                    // Get the file's certificate and see if it is in the
                    // excluded list.
                    X509Certificate cert = null;
                    if (fl.getCertificate() != null) {
                        try {
                            cert = CertUtil.loadCertificate(fl.getCertificate());
                            ca.setName(cert.getSubjectDN().getName());
                            ca.setCertificateFile(fl.getCertificate().getAbsolutePath());
			    OrderInsensitiveDN thisOidn = getOrderInsensitiveDN(ca.getName());
			    Iterator<String> excludedIterator = excluded.iterator();
			    while (excludedIterator.hasNext()) {
				OrderInsensitiveDN excludedOidn = getOrderInsensitiveDN(excludedIterator.next());
				if (thisOidn.equalsIgnoringOrder(excludedOidn)) {
                                    processExcludedCA(cert, ca, fl, completeCASetByName, completeCASetByHash);
                                    continue whileDelHasNext;
				}
                            }
                        } catch (Exception exception) {
                            ca.setCertificateFile(fl.getCertificate().getAbsolutePath());
                            errorMessage("Error loading the certificate, " + fl.getCertificate().getAbsolutePath() + ": \n"
                                    + exception.getMessage());
                        }
                    }

                    if (fl.getMetadata() != null) {
                        try {
                            TrustedCA tca = Utils.deserializeDocument(fl.getMetadata().getAbsolutePath(), TrustedCA.class);
                            ca.setDiscovered(tca.getDiscovered());
                            ca.setExpiration(tca.getExpiration());
                            if ((unableToSync.contains(tca.getGts())) && (!master.containsKey(tca.getName()))) {
                                Calendar c = new GregorianCalendar();
                                if (c.getTimeInMillis() < tca.getExpiration()) {
                                    warningMessage("Unable to communicate with the GTS " + tca.getGts() + " did not remove the the CA "
                                            + tca.getName() + " because it was not expired.");
                                    completeCASetByName.add(cert.getSubjectDN().getName());
                                    completeCASetByHash.add(fl.getName());
                                    continue;
                                }
                            }
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                    removeCount = removeCount + 1;
                    removeCAFiles(fl, ca, removedList);

                }

                RemovedTrustedCAs rtc = new RemovedTrustedCAs();
                rtc.setTrustedCA(removedList.toArray(new TrustedCA[removedList.size()]));
                report.setRemovedTrustedCAs(rtc);
                infoMessage("Successfully removed " + removeCount + " Trusted Authority(s) from "
                        + CoGProperties.getDefault().getCaCertLocations());

                int taCount = 0;
                Iterator<TrustedCAListing> itr = master.values().iterator();
                List<TrustedCA> addedList = new ArrayList<TrustedCA>();

                taCount = writeNewTrustFiles(addedList, excluded, completeCASetByName, completeCASetByHash, taCount, itr);
                infoMessage("Successfully wrote out " + taCount + " Trusted Authority(s) to "
                        + CoGProperties.getDefault().getCaCertLocations());

                TrustedCA[] addedCAs = new TrustedCA[taCount];
                for (int i = 0; i < addedList.size(); i++) {
                    addedCAs[i] = addedList.get(i);
                }
                AddedTrustedCAs atc = new AddedTrustedCAs();
                atc.setTrustedCA(addedCAs);
                report.setAddedTrustedCAs(atc);
            }
        } catch (Exception e) {
            logger.fatal(e.getMessage(), e);
            Message error = new Message();
            error.setType(MessageType.Fatal);
            error.setValue(e.getMessage());
            messages.add(error);
        }

        addMessagesToReport(report);
        logReport(report, description);
        return report;
    }

    /**
     * @param addedList
     * @param excluded
     * @param completeCASetByName
     * @param completeCASetByHash
     * @param taCount
     * @param itr
     * @return
     */
    private int writeNewTrustFiles(List<TrustedCA> addedList, Set<String> excluded, Set<String> completeCASetByName,
            Set<String> completeCASetByHash, int taCount, Iterator<TrustedCAListing> itr) {
        while (itr.hasNext()) {
            taCount = taCount + 1;
            File caFile = null;
            File crlFile = null;
            File metadataFile = null;
            File signingPolicyFile = null;
            String caHash = null;
            String subject = null;
            try {
                TrustedCAListing listing = itr.next();
                TrustedAuthority ta = listing.getTrustedAuthority();
                X509Certificate cert = CertUtil.loadCertificate(ta.getCertificate().getCertificateEncodedString());
                caHash = CertUtil.getHashCode(cert);
                TrustedCA ca = new TrustedCA();
                subject = cert.getSubjectDN().getName();
                ca.setName(subject);
                ca.setGts(listing.getService());

                if ((completeCASetByName.contains(cert.getSubjectDN())) && (excluded.contains(cert.getSubjectDN()))) {
                    warningMessage("Ignoring the CA " + ca.getName() + " obtained from the GTS " + ca.getGts()
                            + " because the CA is in the excludes list.");
                } else if ((completeCASetByName.contains(cert.getSubjectDN())) && (!excluded.contains(cert.getSubjectDN()))) {
                    warningMessage("Ignoring the CA " + ca.getName() + " obtained from the GTS " + ca.getGts()
                            + " because the CA is already trusted.");
                } else if (completeCASetByHash.contains(caHash)) {
                    warningMessage("Ignoring the CA " + ca.getName() + " obtained from the GTS " + ca.getGts()
                            + " because a CA with the hash " + caHash + " already exists.");
                } else {
                    String filePrefix = CoGProperties.getDefault().getCaCertLocations() + File.separator + caHash;
                    caFile = new File(filePrefix + "." + 0);
                    crlFile = new File(filePrefix + ".r" + 0);
                    metadataFile = new File(filePrefix + ".syncgts");
                    signingPolicyFile = new File(filePrefix + ".signing_policy");

                    ca.setMetadataFile(metadataFile.getAbsolutePath());
                    CertUtil.writeCertificate(cert, caFile);
                    ca.setCertificateFile(caFile.getAbsolutePath());
                    CertUtil.writeSigningPolicy(cert, signingPolicyFile);
                    ca.setSigningPolicyFile(signingPolicyFile.getAbsolutePath());
                    logger.debug("Wrote out the certificate for the Trusted Authority " + ta.getName() + " to the file "
                            + caFile.getAbsolutePath());
                    if (ta.getCRL() != null) {
                        if (ta.getCRL().getCrlEncodedString() != null) {
                            writeCRL(ta, crlFile);
                            ca.setCRLFile(crlFile.getAbsolutePath());
                        }
                    }
                    Calendar cal = new GregorianCalendar();
                    ca.setDiscovered(cal.getTimeInMillis());
                    if (listing.getDescriptor().getExpiration() != null) {
                        cal.add(Calendar.HOUR_OF_DAY, listing.getDescriptor().getExpiration().getHours());
                        cal.add(Calendar.MINUTE, listing.getDescriptor().getExpiration().getMinutes());
                        cal.add(Calendar.SECOND, listing.getDescriptor().getExpiration().getSeconds());
                        ca.setExpiration(cal.getTimeInMillis());
                    } else {
                        ca.setExpiration(cal.getTimeInMillis());
                    }
                    Utils.serializeDocument(metadataFile.getAbsolutePath(), ca, TRUSTED_CA_QN);
                    logger.debug("Wrote out the metadata for the Trusted Authority " + ta.getName() + " to the file "
                            + metadataFile.getAbsolutePath());
                    completeCASetByName.add(subject);
                    completeCASetByHash.add(caHash);
                    addedList.add(ca);
                }
            } catch (Exception e) {
                logger.error("An unexpected error occurred writing out the Trusted Authorities!!!", e);
                if (caFile != null) {
                    caFile.delete();
                }
                if (crlFile != null) {
                    crlFile.delete();
                }
                if (metadataFile != null) {
                    metadataFile.delete();
                }
                if (signingPolicyFile != null) {
                    signingPolicyFile.delete();
                }
                if (subject != null) {
                    completeCASetByName.remove(subject);

                }
                if (caHash != null) {
                    completeCASetByHash.remove(caHash);
                }
            }
        }
        return taCount;
    }

    /**
     * @param ta
     * @param crlFile
     * @throws IOException
     * @throws GeneralSecurityException
     */
    private void writeCRL(TrustedAuthority ta, File crlFile) throws IOException, GeneralSecurityException {
        X509CRL crl = CertUtil.loadCRL(ta.getCRL().getCrlEncodedString());
        CertUtil.writeCRL(crl, crlFile);
        logger.debug("Wrote out the CRL for the Trusted Authority " + ta.getName() + " to the file " + crlFile.getAbsolutePath());
    }

    /**
     * @param report
     */
    private void addMessagesToReport(SyncReport report) {
        Message[] list = new Message[messages.size()];
        for (int i = 0; i < messages.size(); i++) {
            list[i] = messages.get(i);
        }
        Messages reportMessages = new Messages();
        reportMessages.setMessage(list);
        report.setMessages(reportMessages);
    }

    /**
     * @param report
     * @param description
     */
    private void logReport(SyncReport report, SyncDescription description) {
        try {
            history.addReport(report);
            if (description.getCacheSize() != null) {
                history.prune(description.getCacheSize());
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * @param fl
     * @param ca
     * @param removedList
     */
    private void removeCAFiles(TrustedCAFileListing fl, TrustedCA ca, List<TrustedCA> removedList) {
        if (fl.getCertificate() != null) {
            if (fl.getCertificate().delete()) {
                logger.debug("Removed the certificate (" + fl.getCertificate().getAbsolutePath() + ") for the CA " + ca.getName() + ".");
            } else {
                errorMessage("Error removing the certificate (" + fl.getCertificate().getAbsolutePath() + ") for the CA " + ca.getName()
                        + ".");
            }
        }

        if (fl.getCRL() != null) {
            ca.setCRLFile(fl.getCRL().getAbsolutePath());
            if (fl.getCRL().delete()) {
                logger.debug("Removed the CRL (" + fl.getCRL().getAbsolutePath() + ") for the CA " + ca.getName() + ".");
            } else {
                errorMessage("Error removing the CRL (" + fl.getCRL().getAbsolutePath() + ") for the CA " + ca.getName() + ".");
            }
        }

        if (fl.getSigningPolicy() != null) {
            ca.setSigningPolicyFile(fl.getSigningPolicy().getAbsolutePath());
            if (fl.getSigningPolicy().delete()) {
                logger.debug("Removed the Signing Policy (" + fl.getCertificate().getAbsolutePath() + ") for the CA " + ca.getName() + ".");
            } else {
                errorMessage("Error removing the Signing Policy (" + fl.getCRL().getAbsolutePath() + ") for the CA " + ca.getName() + ".");
            }
        }

        if (fl.getMetadata() != null) {
            ca.setMetadataFile(fl.getMetadata().getAbsolutePath());
            if (fl.getMetadata().delete()) {
                logger.debug("Removed the CA Metadata (" + fl.getMetadata().getAbsolutePath() + ") for the CA " + ca.getName() + ".");
            } else {
                errorMessage("Error removing the metadata (" + fl.getMetadata().getAbsolutePath() + ") for the CA " + ca.getName() + ".");
            }
        }

        removedList.add(ca);
    }

    /**
     * @param msg
     */
    private void infoMessage(String msg) {
        Message m = new Message();
        m.setType(MessageType.Info);
        m.setValue(msg);
        this.messages.add(m);
        logger.info(m.getValue());
    }

    /**
     * @param msg
     */
    private void warningMessage(String msg) {
        Message m = new Message();
        m.setType(MessageType.Warning);
        m.setValue(msg);
        this.messages.add(m);
        logger.warn(m.getValue());
    }

    /**
     * @param msg
     */
    private void errorMessage(String msg) {
        Message err = new Message();
        err.setType(MessageType.Error);
        err.setValue(msg);
        this.messages.add(err);
        logger.error(err.getValue());
    }

    /**
     * @param cert
     * @param ca
     * @param fl
     * @param completeCASetByName
     * @param completeCASetByHash
     */
    private void processExcludedCA(X509Certificate cert, TrustedCA ca, TrustedCAFileListing fl, Set<String> completeCASetByName,
            Set<String> completeCASetByHash) {
        infoMessage("The CA " + ca.getName() + " was not removed because it is the exclude list.");
        completeCASetByName.add(cert.getSubjectDN().getName());
        completeCASetByHash.add(fl.getName());
    }

    /**
     * Get the set of excluded CAs in the given description and remove them from
     * the given master map if present.
     * 
     * @param master
     *            A map of name-authority pairs that are trusted.
     * @param description
     *            the overall description of the syncrhonization being
     *            performed.
     * @return a set of the excluded CAs.
     */
    private Set<String> removeExcludedAuthorities(Map<String, TrustedCAListing> master, SyncDescription description) {
        Set<String> excluded = new HashSet<String>();
        ExcludedCAs ex = description.getExcludedCAs();
        if (ex != null) {
            String[] caSubjects = ex.getCASubject();
            if (caSubjects != null) {
                for (int i = 0; i < caSubjects.length; i++) {
                    excluded.add(caSubjects[i]);

                    if (master.containsKey(caSubjects[i])) {
                        master.remove(caSubjects[i]);
                        Message m = new Message();
                        m.setType(MessageType.Warning);
                        m
                                .setValue("Ignoring the CA " + caSubjects[i]
                                        + " that was obtained in the sync because it is the excludes list!!!");
                        logger.warn(m.getValue());
                        this.messages.add(m);
                    }
                }
            }
        }
        return excluded;
    }

    /**
     * Consult all of the GTS services identified in the given SyncDescription
     * and return all of the authorities trusted by the GTS services.
     * 
     * @param description
     *            The description of what sync operation is supposed to do.
     * @param unableToSync
     *            If we are unable to get consult with any of the identified GTS
     *            services, their URIs are added (as strings) to this set.
     * @return A Map that contains name-description pairs for each trusted
     *         authority.
     */
    private Map<String, TrustedCAListing> fetchTrustedAuthoritiesFromGTSServices(SyncDescription description, Set<String> unableToSync) {
        Map<String, TrustedCAListing> master = new HashMap<String, TrustedCAListing>();
        SyncDescriptor[] syncDescriptors = description.getSyncDescriptor();
        int syncDescriptorCount = (syncDescriptors != null) ? syncDescriptors.length : 0;

        for (int i = 0; i < syncDescriptorCount; i++) {
            SyncDescriptor thisSyncDescriptor = syncDescriptors[i];
            Map<String, TrustedAuthority> trustedAuthorityMap = mapTrustedAuthorityNameToTrustedAuthorities(thisSyncDescriptor,
                    unableToSync);
            // add all to the master list;
            addTrustedAuthoritiesToMap(master, trustedAuthorityMap, thisSyncDescriptor);
            this.logger.debug("Done syncing with the GTS " + thisSyncDescriptor.getGtsServiceURI() + " " + trustedAuthorityMap.size()
                    + " Trusted Authority(s) found!!!");
        }
        return master;
    }
    /**
     * Add trusted authorities to a master map from a map that contains
     * name-descrition pairs for authorities trusted by the GTS service
     * identified by the given SyncDescriptor
     * 
     * @param master
     * @param trustedAuthorityMap
     * @param thisSyncDescriptor
     */
    private void addTrustedAuthoritiesToMap(Map<String, TrustedCAListing> master, Map<String, TrustedAuthority> trustedAuthorityMap,
            SyncDescriptor thisSyncDescriptor) {
        String gtsUri = thisSyncDescriptor.getGtsServiceURI();
        Iterator<TrustedAuthority> itr = trustedAuthorityMap.values().iterator();
        while (itr.hasNext()) {
            TrustedAuthority ta = itr.next();

            if (master.containsValue(ta.getName())) {
                TrustedCAListing gta = master.get(ta.getName());
                String msg = "Conflict Detected: The Trusted Authority " + ta.getName() + " was determined to be trusted by both "
                        + gta.getService() + " and " + gtsUri + ".";
                Message mess = new Message();
                mess.setType(MessageType.Warning);
                mess.setValue(msg);
                messages.add(mess);
            } else {
                master.put(ta.getName(), new TrustedCAListing(gtsUri, ta, thisSyncDescriptor));
            }
        }
    }

    /**
     * Poll GTS service specified by the given SyncDescriptor to get the trusted
     * authorities.
     * 
     * @param thisSyncDescriptor
     *            The description of the GTS to synchronize with.
     * @param unableToSync
     *            If unable to sync with the GTS, the URI for the GTS is added
     *            to this set as a string.
     * @return a map whose key are the names of trusted authorities as strings
     *         and whose values are the trusted authority descriptions.
     */
    private Map<String, TrustedAuthority> mapTrustedAuthorityNameToTrustedAuthorities(SyncDescriptor thisSyncDescriptor,
            Set<String> unableToSync) {
        String gtsServiceUri = thisSyncDescriptor.getGtsServiceURI();
        this.logger.info("Syncing with the GTS " + gtsServiceUri);
        Map<String, TrustedAuthority> taMap = new HashMap<String, TrustedAuthority>();
        TrustedAuthorityFilter[] authorityFilters = thisSyncDescriptor.getTrustedAuthorityFilter();
        int filterCount = 0;
        if (authorityFilters != null) {
            filterCount = authorityFilters.length;
        }
        for (int j = 0; j < filterCount; j++) {
            int filter = j + 1;
            try {
                GTSClient client = makeGTSClient(thisSyncDescriptor);

                TrustedAuthority[] tas = client.findTrustedAuthorities(authorityFilters[j]);
                int length = 0;
                if (tas != null) {
                    length = tas.length;
                }
                this.logger.debug("Successfully synced with " + gtsServiceUri + " using filter " + filter + " the search found " + length
                        + " Trusted Authority(s)!!!");

                for (int x = 0; x < length; x++) {
                    taMap.put(tas[x].getName(), tas[x]);
                }

            } catch (Exception e) {
                unableToSync.add(gtsServiceUri);
                Message mess = new Message();
                mess.setType(MessageType.Error);
                mess.setValue("An error occurred syncing with " + gtsServiceUri + " using filter " + filter + "\n " + e.getMessage());
                messages.add(mess);
                logger.error(mess.getValue(), e);
            }
        }
        return taMap;
    }

    /**
     * Create and configure a GTSClient
     * 
     * @param thisSyncDescriptor
     *            A description of the synchronization operation.
     * @return the configured client
     * @throws MalformedURIException
     *             if there is a problem
     * @throws RemoteException
     *             if there is a problem
     */
    private GTSClient makeGTSClient(SyncDescriptor thisSyncDescriptor) throws MalformedURIException, RemoteException {
        EndpointReferenceType endpoint = new EndpointReferenceType();
        endpoint.setAddress(new Address(thisSyncDescriptor.getGtsServiceURI()));
        GTSClient client = new GTSClient(endpoint);

        // If a required host identity is specified for the GTS service, tell
        // the client about it.
        if (thisSyncDescriptor.isPerformAuthorization()) {
            IdentityAuthorization ia = new IdentityAuthorization(thisSyncDescriptor.getGTSIdentity());
            client.setAuthorization(ia);
        }
        return client;
    }

    /**
     * Initialize the given synchronization report with the given description
     * and the current time.
     * 
     * @param report
     *            The report to be synchronized
     * @param description
     *            the description of the current synchronization operation.
     */
    private void initializeReport(SyncReport report, SyncDescription description) {
        String dt = MobiusDate.getCurrentDateTimeAsString();
        report.setSyncDescription(description);
        report.setTimestamp(dt);
    }

    private void readInCurrentCADirectory(SyncDescription description) throws Exception {
        caListings = new HashMap<String, TrustedCAFileListing>();
        this.listingsById = new HashMap<Integer, TrustedCAFileListing>();
        File dir = Utils.getTrustedCerificatesDirectory();
        logger.info("Taking Snapshot of Trusted CA Directory (" + dir.getAbsolutePath() + ")....");
        if (dir.exists()) {
            if (!dir.isDirectory()) {
                Message mess = new Message();
                mess.setType(MessageType.Fatal);
                mess.setValue("The Trusted Certificates directory, " + dir.getAbsolutePath() + " is not a directory.");
                messages.add(mess);
                throw new Exception(mess.getValue());
            }

        } else {
            boolean create = dir.mkdirs();
            if (!create) {
                Message mess = new Message();
                mess.setType(MessageType.Fatal);
                mess.setValue("The Trusted Certificates directory, " + dir.getAbsolutePath() + " does not exist and could not be created.");
                messages.add(mess);
                throw new Exception(mess.getValue());
            }
        }
        File[] list = dir.listFiles();
        for (int i = 0; i < list.length; i++) {
            String fn = list[i].getName();
            int index = fn.lastIndexOf(".");
            if (index == -1) {
                handleUnexpectedFile(description, list[i]);
                continue;
            }
            String name = fn.substring(0, index);
            String extension = fn.substring(index + 1);

            TrustedCAFileListing ca = this.caListings.get(name);
            if (ca == null) {
                ca = new TrustedCAFileListing(name);
                caListings.put(name, ca);
            }

            if (extension.matches("[0-9]+")) {
                ca.setFileId(Integer.valueOf(extension));
                ca.setCertificate(list[i]);
            } else if (extension.matches("[r]{1}[0-9]+")) {
                ca.setCRL(list[i]);
            } else if (extension.equals("signing_policy")) {
                ca.setSigningPolicy(list[i]);
            } else if (extension.indexOf("syncgts") != -1) {
                ca.setMetadata(list[i]);
            } else {
                handleUnexpectedFile(description, list[i]);
                continue;
            }

        }
        Iterator<TrustedCAFileListing> itr = this.caListings.values().iterator();
        logger.debug("Found " + caListings.size() + " Trusted CAs found!!!");
        while (itr.hasNext()) {
            TrustedCAFileListing ca = itr.next();
            this.listingsById.put(ca.getFileId(), ca);
            logger.debug(ca.toPrintText());
        }
        infoMessage("A pre synchronization snapshot of the Trusted CA Directory found " + caListings.size() + " Trusted CAs.");
    }

    private void handleUnexpectedFile(SyncDescription description, File f) {
        if (description.isDeleteInvalidFiles()) {
            Message mess = new Message();
            mess.setType(MessageType.Warning);
            mess.setValue("The file " + f.getAbsolutePath() + " is unexpected and will be removed!!!");
            messages.add(mess);
            logger.warn(mess.getValue());
            f.delete();
        } else {
            Message mess = new Message();
            mess.setType(MessageType.Warning);
            mess.setValue("The file " + f.getAbsolutePath() + " is unexpected and will be ignored!!!");
            messages.add(mess);
            logger.warn(mess.getValue());
        }
    }

    private static OrderInsensitiveDN getOrderInsensitiveDN(String dn) {
                OrderInsensitiveDN oidn = orderInsensitiveDNCache.get(dn);
                if (oidn == null) {
		    try {
                        oidn = new OrderInsensitiveDN(dn);
		    } catch (InvalidNameException e) {
			String msg = "distinguished name is not well-formed: " + dn;
			logger.error(msg, e);
			throw new RuntimeException(msg, e);
		    }
                    orderInsensitiveDNCache.put(dn, oidn);
                }
                return oidn;
    }
}
