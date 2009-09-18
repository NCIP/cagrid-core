package gov.nih.nci.cagrid.wsenum.utils;

import gov.nih.nci.cagrid.common.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;

import org.globus.ws.enumeration.EnumIterator;
import org.globus.ws.enumeration.IterationConstraints;
import org.globus.ws.enumeration.IterationResult;
import org.globus.ws.enumeration.TimeoutException;
import org.globus.wsrf.encoding.SerializationException;

/** 
 *  SimplePersistantObjectIterator
 *  Enumeration iterator which provides for persisting serializable objects to disk.
 *  <b>Like Globus' provided iterator implementations, this iterator makes no 
 *  attempt to respect any IterationConstraints except for maxElements.</b>
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Aug 17, 2006 
 * @version $Id: SimplePersistantObjectIterator.java,v 1.1 2008-11-04 15:27:15 dervin Exp $ 
 */
public class SimplePersistantObjectIterator extends BaseSerializedObjectIterator {
    public static final String SERIALIZATION_DIRECTORY = SimplePersistantObjectIterator.class.getSimpleName();
    public static final String PERSISTANCE_FILE_NAME_PREFIX = "EnumIteration";
    public static final String PERSISTANCE_FILE_EXTENSION = ".serialized";
	
	private SimplePersistantObjectIterator(File file, QName objectQName) throws FileNotFoundException {
		super(file, objectQName);
	}
	
    
    public static EnumIterator createIterator(List objects, QName objectQName) throws Exception {
        return createIterator(objects, objectQName, null);
    }
	
	/**
	 * Serializes a List of serializable objects to a temp file on
	 * the local disk, then creates an EnumIterator which can return
	 * those objects.
	 * 
	 * <b><i>NOTE:</b></i> The temp file is created in the current user's 
	 * home directory /.cagrid/SimplePersistantObjectIterator directory.  
     * For security reasons, access to this location must be controlled 
     * in a production data environment. 
	 * 
	 * @param objects
	 * 		The list of data objects to be enumerated
	 * @param objectQName
	 * 		The QName of the objects
	 * @param wsddInput
	 * 		An input stream of the WSDD configuration
	 * @return
	 * 		An enum iterator instance for the given objects
	 * @throws Exception
	 */
	public static EnumIterator createIterator(List objects, QName objectQName, InputStream wsddInput) throws Exception {
		File tempSerializationDir = new File(Utils.getCaGridUserHome().getAbsolutePath(), SERIALIZATION_DIRECTORY);
		if (!tempSerializationDir.exists()) {
			tempSerializationDir.mkdirs();
		}
		return createIterator(objects, objectQName, wsddInput, 
			File.createTempFile(PERSISTANCE_FILE_NAME_PREFIX, PERSISTANCE_FILE_EXTENSION, tempSerializationDir).getAbsolutePath());
	}
	
	
	/**
	 * Serializes a List of serializable objects to a specified file on
	 * the local disk, then creates an EnumIterator which can return
	 * those objects.
	 * 
	 * @param objects
	 * 		The list of data objects to be enumerated
	 * @param objectQName
	 * 		The QName of the objects
	 * @param filename
	 * 		The name of the file to serialize objects into.
	 * 		<b><i>NOTE:</b></i> For security reasons, access to this location 
	 * 		must be controlled in a production data environment.
	 * @param wsddInput
	 * 		An input stream of the WSDD configuration 
	 * @return
	 * 		An enum iterator instance for the given objects
	 * @throws Exception
	 */
	public static EnumIterator createIterator(List objects, QName objectQName, InputStream wsddInput, String filename) throws Exception {
		StringBuffer wsddContents = wsddInput != null ? Utils.inputStreamToStringBuffer(wsddInput) : null;
		writeOutObjects(objects.iterator(), objectQName, filename, wsddContents);
		return new SimplePersistantObjectIterator(new File(filename), objectQName);
	}
	

	/**
     * Retrieves the next set of items of the enumeration.
     * <b>Note:</b> This implementation ignores any iteration constraints except for max elements
     *
     * @param constraints the constrains for this iteration. Can be null.
     *        If null, default constraints must be assumed.
     * @return the result of this iteration that fulfils the specified
     *         constraints. It must always be non-null.
     * @throws TimeoutException if <tt>maxTime</tt> constraint was specified
     *         and the enumeration data was not collected within that time.
     *         <i>This is never thrown in this implementation</i>
     * @throws NoSuchElementException if iterator has no more elements
     */
	public IterationResult next(IterationConstraints constraints) throws TimeoutException, NoSuchElementException {
		// check for release
		if (enumerationIsReleased()) {
			throw new NoSuchElementException("Enumeration has been released");
		}
		// temporary list to hold SOAPElements
		List<SOAPElement> soapElements = new ArrayList<SOAPElement>(constraints.getMaxElements());
		
		// start building results
		String xml = null;
        boolean hasMoreXml = false;
		try {
			while (soapElements.size() < constraints.getMaxElements() && (xml = getNextXmlChunk()) != null) {
				try {
					SOAPElement element = createSOAPElement(xml, getObjectQName());
					soapElements.add(element);
				} catch (SerializationException ex) {
					release();
					NoSuchElementException nse = new NoSuchElementException("Error serializing element -- " + ex.getMessage());
					nse.setStackTrace(ex.getStackTrace());
					throw nse;
				}
                hasMoreXml = hasMoreXmlChunks();
			}
		} catch (IOException ex) {
			release();
			NoSuchElementException nse = new NoSuchElementException("Error reading XML chunk from persistance file: " + ex.getMessage());
			nse.setStackTrace(ex.getStackTrace());
			throw nse;
		}
		// if the xml text is null, we're at the end of the iteration
		return wrapUpElements(soapElements, !hasMoreXml);
	}
}
