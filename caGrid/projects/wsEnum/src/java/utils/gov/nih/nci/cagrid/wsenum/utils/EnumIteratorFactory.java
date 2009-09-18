package gov.nih.nci.cagrid.wsenum.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.globus.ws.enumeration.EnumIterator;
import org.globus.ws.enumeration.IndexedObjectFileEnumIterator;
import org.globus.ws.enumeration.SimpleEnumIterator;
import org.globus.wsrf.utils.io.IndexedObjectFileUtils;

/** 
 *  EnumIteratorFactory
 *  Creates instances of EnumIterator implementations 
 * 
 * @author David Ervin
 * 
 * @created Apr 30, 2007 12:18:15 PM
 * @version $Id: EnumIteratorFactory.java,v 1.6 2008-11-04 15:27:15 dervin Exp $ 
 */
public class EnumIteratorFactory {

    public static EnumIterator createIterator(IterImplType iterType, List objects, QName objectQName, InputStream wsddInput) throws Exception {
        switch (iterType) {
            case GLOBUS_SIMPLE:
                return getGlobusSimpleIterator(objects, objectQName);
            case GLOBUS_INDEXED_FILE:
                return getGlobusIndexedFileIterator(objects, objectQName, null);
            case CAGRID_SIMPLE:
                return SimplePersistantObjectIterator.createIterator(objects, objectQName, wsddInput);
            case CAGRID_THREADED_COMPLETE:
                return PersistantObjectIterator.createIterator(objects, objectQName, wsddInput);
            case CAGRID_CONCURRENT_COMPLETE:
                return ConcurrenPersistantObjectEnumIterator.createIterator(objects, objectQName, wsddInput);
        }
        throw new EnumIteratorInitializationException("Unknown enum iter implementation: " + iterType.toString());
    }
    
    
    /**
     * Serializes a List of serialiable objects to a temp file on
     * the local disk, then creates an EnumIterator which can return
     * those objects.
     * 
     * <b><i>NOTE:</b></i> The temp file is created in the current user's 
     * home directory /.cagrid/<i>Iterator Class</i> directory.  For security
     * reasons, access to this location must be controlled in a production
     * data environment. 
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
    public static EnumIterator createIterator(IterImplType iterType, Iterator objectIter, QName objectQName, InputStream wsddInput) throws Exception {
        switch (iterType) {
            case GLOBUS_SIMPLE:
                return getGlobusSimpleIterator(iteratorToList(objectIter), objectQName);
            case GLOBUS_INDEXED_FILE:
                return getGlobusIndexedFileIterator(iteratorToList(objectIter), objectQName, null);
            case CAGRID_SIMPLE:
                return SimplePersistantObjectIterator.createIterator(iteratorToList(objectIter), objectQName, wsddInput);
            case CAGRID_THREADED_COMPLETE:
                return PersistantObjectIterator.createIterator(objectIter, objectQName, wsddInput);
            case CAGRID_CONCURRENT_COMPLETE:
                return ConcurrenPersistantObjectEnumIterator.createIterator(objectIter, objectQName, wsddInput);
        }
        throw new EnumIteratorInitializationException("Unknown enum iter implementation: " + iterType.toString());
    }
    
    
    /**
     * Serializes a List of serializable objects to a specified file on
     * the local disk, then creates an EnumIterator which can return
     * those objects.
     * 
     * @param objects
     *      The list of data objects to be enumerated
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
    public static EnumIterator createIterator(IterImplType iterType, List objects, QName objectQName, InputStream wsddInput, String tempFilename) throws Exception {
        switch (iterType) {
            case GLOBUS_SIMPLE:
                return getGlobusSimpleIterator(objects, objectQName);
            case GLOBUS_INDEXED_FILE:
                return getGlobusIndexedFileIterator(objects, objectQName, tempFilename);
            case CAGRID_SIMPLE:
                return SimplePersistantObjectIterator.createIterator(objects, objectQName, wsddInput, tempFilename);
            case CAGRID_THREADED_COMPLETE:
                return PersistantObjectIterator.createIterator(objects, objectQName, wsddInput, tempFilename);
            case CAGRID_CONCURRENT_COMPLETE:
                return ConcurrenPersistantObjectEnumIterator.createIterator(objects, objectQName, wsddInput, tempFilename);
        }
        throw new EnumIteratorInitializationException("Unknown enum iter implementation: " + iterType.toString());
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
    public static EnumIterator createIterator(IterImplType iterType, Iterator objectIter, QName objectQName, InputStream wsddInput, String tempFilename) throws Exception {
        switch (iterType) {
            case GLOBUS_SIMPLE:
                return getGlobusSimpleIterator(iteratorToList(objectIter), objectQName);
            case GLOBUS_INDEXED_FILE:
                return getGlobusIndexedFileIterator(iteratorToList(objectIter), objectQName, tempFilename);
            case CAGRID_SIMPLE:
                return SimplePersistantObjectIterator.createIterator(iteratorToList(objectIter), objectQName, wsddInput, tempFilename);
            case CAGRID_THREADED_COMPLETE:
                return PersistantObjectIterator.createIterator(objectIter, objectQName, wsddInput, tempFilename);
            case CAGRID_CONCURRENT_COMPLETE:
                return ConcurrenPersistantObjectEnumIterator.createIterator(objectIter, objectQName, wsddInput, tempFilename);
        }
        throw new EnumIteratorInitializationException("Unknown enum iter implementation: " + iterType.toString());
    }
    
    
    private static List<Object> iteratorToList(Iterator iter) {
        List<Object> l = new LinkedList<Object>();
        while (iter.hasNext()) {
            l.add(iter.next());
        }
        return l;
    }
    
    
    private static SimpleEnumIterator getGlobusSimpleIterator(List items, QName typeName) {
        return new SimpleEnumIterator(items, typeName);
    }
    
    
    private static IndexedObjectFileEnumIterator getGlobusIndexedFileIterator(List items, QName typeName, String filename) 
        throws EnumIteratorInitializationException {
        try {
            File indexedFile = null;
            if (filename == null) {
                indexedFile = IndexedObjectFileUtils.createIndexedObjectFile(items);
            } else {
                IndexedObjectFileUtils.createIndexedObjectFile(filename, items);
                indexedFile = new File(filename);
            }
            return new IndexedObjectFileEnumIterator(indexedFile, typeName);
        } catch (IOException ex) {
            throw new EnumIteratorInitializationException(ex.getMessage(), ex);
        }
    }
}
