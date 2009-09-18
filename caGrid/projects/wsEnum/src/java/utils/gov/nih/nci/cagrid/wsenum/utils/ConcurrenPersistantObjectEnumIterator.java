package gov.nih.nci.cagrid.wsenum.utils;

import gov.nih.nci.cagrid.common.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;

import org.globus.axis.utils.DurationUtils;
import org.globus.ws.enumeration.EnumIterator;
import org.globus.ws.enumeration.IterationConstraints;
import org.globus.ws.enumeration.IterationResult;
import org.globus.ws.enumeration.TimeoutException;
import org.globus.wsrf.encoding.SerializationException;

/** 
 *  ConcurrenPersistantObjectEnumIterator
 *  EnumIterator implementation which uses the Java 5 Concurrent package
 * 
 * @author David Ervin
 * 
 * @created Apr 10, 2007 10:02:57 AM
 * @version $Id: ConcurrenPersistantObjectEnumIterator.java,v 1.6 2008-11-04 15:27:15 dervin Exp $ 
 */
public class ConcurrenPersistantObjectEnumIterator extends BaseSerializedObjectIterator {
    public static final String SERIALIZATION_DIRECTORY = ConcurrenPersistantObjectEnumIterator.class.getSimpleName();
    public static final String PERSISTANCE_FILE_NAME_PREFIX = "EnumIteration";
    public static final String PERSISTANCE_FILE_EXTENSION = ".serialized";
    
    private List<SOAPElement> overflowElements;
    
    public ConcurrenPersistantObjectEnumIterator(File file, QName objectQName) 
        throws FileNotFoundException {
        super(file, objectQName);
        overflowElements = new LinkedList<SOAPElement>();
    }
    
    
    public static EnumIterator createIterator(List objects, QName objectQName) throws Exception {
        return createIterator(objects.iterator(), objectQName, null);
    }
    
    
    /**
     * Serializes a List of serializable objects to a temp file on
     * the local disk, then creates an EnumIterator which can return
     * those objects.
     * 
     * <b><i>NOTE:</b></i> The temp file is created in the current user's 
     * home directory /.cagrid/ConcurrentPersistantObjectEnumIterator directory.  
     * For security reasons, access to this location must be controlled in a 
     * production data environment. 
     * 
     * @param objects
     *      The list of data objects to be enumerated
     * @param objectQName
     *      The QName of the objects
     * @param wsddInput
     *      An input stream to the WSDD configuration file
     * @return
     *      An enum iterator instance to iterate the given objects
     * @throws Exception
     */
    public static EnumIterator createIterator(List objects, QName objectQName, InputStream wsddInput) throws Exception {
        return createIterator(objects.iterator(), objectQName, wsddInput);
    }
    
    
    /**
     * Serializes a List of serializable objects to a temp file on
     * the local disk, then creates an EnumIterator which can return
     * those objects.
     * 
     * <b><i>NOTE:</b></i> The temp file is created in the current user's 
     * home directory /.cagrid/ConcurrentPersistantObjectEnumIterator directory.
     * For security reasons, access to this location must be controlled in 
     * a production data environment. 
     * 
     * @param objectIter
     *      An iterator to a collection of data objects to be enumerated
     * @param objectQName
     *      The QName of the objects
     * @param wsddInput
     *      An input stream to the WSDD configuration file
     * @return
     *      An enum iterator instance to iterate the given objects
     * @throws Exception
     */
    public static EnumIterator createIterator(Iterator objectIter, QName objectQName, InputStream wsddInput) throws Exception {
        File tempSerializationDir = new File(Utils.getCaGridUserHome().getAbsolutePath(), SERIALIZATION_DIRECTORY);
        if (!tempSerializationDir.exists()) {
            tempSerializationDir.mkdirs();
        }
        return createIterator(objectIter, objectQName, wsddInput, 
            File.createTempFile(PERSISTANCE_FILE_NAME_PREFIX, PERSISTANCE_FILE_EXTENSION, tempSerializationDir).getAbsolutePath());
    }
    
    
    /**
     * Serializes a List of serializable objects to a specified file on
     * the local disk, then creates an EnumIterator which can return
     * those objects.
     * 
     * @param objects
     *      The list of dataobjects to be enumerated
     * @param objectQName
     *      The QName of the objects
     * @param tempFilename
     *      The name of the file to serialize objects into.
     *      <b><i>NOTE:</b></i> For security reasons, access to this location 
     *      must be controlled in a production data environment.
     * @param wsddInput
     *      An input stream of the WSDD configuration file
     * @return
     *      An enum iterator instance to iterate the given objects
     * @throws Exception
     */
    public static EnumIterator createIterator(List objects, QName objectQName, InputStream wsddInput, String tempFilename) throws Exception {
        return createIterator(objects.iterator(), objectQName, wsddInput, tempFilename);
    }
    
    
    /**
     * Serializes a List of serializable objects to a specified file on
     * the local disk, then creates an EnumIterator which can return
     * those objects.
     * 
     * @param objectIter
     *      An iterator to a collection of data objects to be enumerated
     * @param objectQName
     *      The QName of the objects
     * @param tempFilename
     *      The name of the file to serialize objects into.
     *      <b><i>NOTE:</b></i> For security reasons, access to this location 
     *      must be controlled in a production data environment.
     * @param wsddInput
     *      An input stream of the WSDD configuration file
     * @return
     *      An enum iterator instance to iterate the given objects
     * @throws Exception
     */
    public static EnumIterator createIterator(Iterator objectIter, QName objectQName, InputStream wsddInput, String tempFilename) throws Exception {
        StringBuffer wsddContents = wsddInput != null ? Utils.inputStreamToStringBuffer(wsddInput) : null;
        writeOutObjects(objectIter, objectQName, tempFilename, wsddContents);
        return new ConcurrenPersistantObjectEnumIterator(
            new File(tempFilename), objectQName);
    }
    

    public IterationResult next(IterationConstraints constraints) throws TimeoutException, NoSuchElementException {
        if (enumerationIsReleased()) {
            throw new NoSuchElementException("Enumeration has been released");
        }
        Callable<IterationResult> resultsCreator = getResultsCreator(constraints);
        FutureTask<IterationResult> createResultTask = new FutureTask<IterationResult>(resultsCreator);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(createResultTask);
        IterationResult result = null;
        if (constraints.getMaxTime() != null) {
            // user has specified how long to wait
            long mills = DurationUtils.toMilliseconds(constraints.getMaxTime());
            try {
                result = createResultTask.get(mills, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ex) {
                NoSuchElementException nse = new NoSuchElementException(
                    "Interrupted while creating results: " + ex.getMessage());
                nse.setStackTrace(ex.getStackTrace());
                throw nse;
            } catch (ExecutionException ex) {
                NoSuchElementException nse = new NoSuchElementException(
                    "Error executing iteration request: " + ex.getMessage());
                nse.setStackTrace(ex.getStackTrace());
                throw nse;
            } catch (java.util.concurrent.TimeoutException  ex) {
                throw new TimeoutException("Timeout exceded: " + ex.getMessage(), ex);
            }
        } else {
            // no timeout required
            try {
                result = createResultTask.get();
            } catch (InterruptedException ex) {
                NoSuchElementException nse = new NoSuchElementException(
                    "Interrupted while creating results: " + ex.getMessage());
                nse.setStackTrace(ex.getStackTrace());
                throw nse;
            } catch (ExecutionException ex) {
                NoSuchElementException nse = new NoSuchElementException(
                    "Error executing iteration request: " + ex.getMessage());
                nse.setStackTrace(ex.getStackTrace());
                throw nse;
            } catch (IllegalArgumentException ex) {
                NoSuchElementException nse = new NoSuchElementException(
                    "An illegal condition was reached while attempting the iteration: " + ex.getMessage());
                nse.setStackTrace(ex.getStackTrace());
                throw nse;
            }
        }
        return result;
    }
    
    
    protected Callable<IterationResult> getResultsCreator(final IterationConstraints constraints) {
        Callable<IterationResult> resultsCreator = new Callable<IterationResult>() {
            public IterationResult call() throws NoSuchElementException {
                // create the list of elements to be returned
                List<SOAPElement> soapElements = new LinkedList<SOAPElement>();
                // first, try to use up any overflow elements
                if (overflowElements.size() != 0) {
                    Iterator<SOAPElement> overflowIter = overflowElements.iterator();
                    while (overflowIter.hasNext() && soapElements.size() < constraints.getMaxElements()) {
                        SOAPElement element = overflowIter.next();
                        int elemLength = element.toString().length();
                        int currentLength = countSoapElementChars(soapElements);
                        if (elemLength + currentLength >= constraints.getMaxCharacters()) {
                            // save it for later
                            continue;
                        } else {
                            soapElements.add(element);
                            overflowIter.remove();
                        }
                    }
                }
                // now move on to new elements
                String xml = null;
                boolean hasMoreXml = false;
                try {
                    while (soapElements.size() < constraints.getMaxElements() 
                        && (xml = getNextXmlChunk()) != null) {
                        try {
                            SOAPElement element = createSOAPElement(xml, getObjectQName());
                            if (constraints.getMaxCharacters() != -1) {
                                // can the new element fit under the max characters limit?
                                int elemLength = element.toString().length();
                                int currentLength = countSoapElementChars(soapElements);
                                if (elemLength + currentLength >= constraints.getMaxCharacters()) {
                                    // store the too-big element for later and return
                                    overflowElements.add(element);
                                    break;
                                } else {
                                    soapElements.add(element);
                                }
                            } else {
                                // simply append the element to the list
                                soapElements.add(element);
                            }
                        } catch (SerializationException ex) {
                            release();
                            NoSuchElementException nse = new NoSuchElementException(
                                "Error serializing element: " + ex.getMessage());
                            nse.setStackTrace(ex.getStackTrace());
                            throw nse;
                        }
                        hasMoreXml = hasMoreXmlChunks();
                    }
                } catch (IOException ex) {
                    release();
                    NoSuchElementException nse = new NoSuchElementException(
                        "Error reading persistance file: " + ex.getMessage());
                    nse.setStackTrace(ex.getStackTrace());
                    throw nse;
                }
                
                // wrap up SOAP elements and an end of sequence flag 
                SOAPElement[] elements = new SOAPElement[soapElements.size()];
                soapElements.toArray(elements);
                // iteration has ended iff no overflow elements exist, and
                // no new XML has been returned from persistance
                boolean endOfSequence = !hasMoreXml && overflowElements.size() == 0;
                return new IterationResult(elements, endOfSequence);
            }
        };
        return resultsCreator;
    }
    
    
    /**
     * Counts all the characters in a list of soap elements
     * 
     * @param soapElements
     * @return
     *      The total character count
     */
    private int countSoapElementChars(List<SOAPElement> soapElements) {
        int count = 0;
        for (SOAPElement elem : soapElements) {
            count += elem.toString().length();
        }
        return count;
    }
}