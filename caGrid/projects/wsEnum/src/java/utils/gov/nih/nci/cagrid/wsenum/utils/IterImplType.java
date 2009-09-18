package gov.nih.nci.cagrid.wsenum.utils;

/**
  *  IterImplType
  *  An enum of ws-enumeration EnumIterator implementation
  *  identifiers, complete with descriptions and notes
  * 
  * @author David Ervin
  * 
  * @created Apr 30, 2007 3:00:10 PM
  * @version $Id: IterImplType.java,v 1.1 2007-05-16 15:00:57 dervin Exp $
 */
public enum IterImplType {
    GLOBUS_SIMPLE, GLOBUS_INDEXED_FILE, 
    CAGRID_SIMPLE, CAGRID_THREADED_COMPLETE, CAGRID_CONCURRENT_COMPLETE;
    
    public String getShortDescription() {
        switch (this) {
            case GLOBUS_SIMPLE:
                return "The Globus-provided simple enum iterator";
            case GLOBUS_INDEXED_FILE:
                return "The Globus-provided indexed file enum iterator";
            case CAGRID_SIMPLE:
                return "A simple iterator which persists objects to disk";
            case CAGRID_THREADED_COMPLETE:
                return "An implementation using threads to respect maxTime constraints";
            case CAGRID_CONCURRENT_COMPLETE:
                return "A complete implementation using Java 5's concurrent package";
        }
        throw new AssertionError("Unknown IterImpl: " + this);
    }
    
    
    public String getNotes() {
        switch (this) {
            case GLOBUS_SIMPLE:
                return "A concrete implementation " +
                    "of the EnumIterator interface. It is a very simple implementation " +
                    "that can enumerate over in-memory data passed either as an array " +
                    "of objects or a list (java.util.List). The enumeration contents can " +
                    "be of javax.xml.soap.SOAPElement type, simple types such as " +
                    "java.lang.Integer, etc. or Axis generated Java beans. The SimpleEnumIterator " +
                    "can only be used with transient type of enumerations.";
            case GLOBUS_INDEXED_FILE:
                return "A memory efficient implementation that can enumerate over " +
                    "data stored in an indexed file created by IndexedObjectFileWriter. " +
                    "The indexed file format is optimized for retrieving objects in a " +
                    "sequential and random manner. The IndexedObjectFileEnumIterator " +
                    "uses the IndexedObjectFileReader to read the indexed file and " +
                    "quickly locate and retrieve the next set of objects of the enumeration." +
                    "The IndexedObjectFileEnumIterator can be used with transient and " +
                    "persistent types of enumerations.";
            case CAGRID_SIMPLE:
                return "This iterator makes no attempt to respect any values of " +
                    "IterationConstraints except for maxElements.";
            case CAGRID_THREADED_COMPLETE:
                return "This iterator uses threads to respect maxTime constraints, " +
                    "as well as respecting maxCharacters.  Elements overflowing either " +
                    "of these constraints, however, are lost, and waits for threads " +
                    "are not optimized.";
            case CAGRID_CONCURRENT_COMPLETE:
                return "This iterator uses the Java 5 java.util.concurrent package to " +
                    "fully support the WS-Enumeration specification for an EnumIterator " +
                    "implementation.  All iteration constraints are respected, and " +
                    "elements which cause maxCharacters to be exceded are queued for " +
                    "later retrieval.";
        }
        throw new AssertionError("Unknown IterImpl: " + this);
    }
}