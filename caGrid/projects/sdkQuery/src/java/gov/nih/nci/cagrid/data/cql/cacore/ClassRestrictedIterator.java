package gov.nih.nci.cagrid.data.cql.cacore;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/** 
 *  ClassRestrictedIterator
 *  Iterator implementation that only returns objects from a collection
 *  which are instances of a given class. 
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Sep 18, 2006 
 * @version $Id$ 
 */
public class ClassRestrictedIterator implements Iterator {
	
	private Iterator collectionIter;
	private String validClassName;
	private Object nextObject;
	
	public ClassRestrictedIterator(Collection col, String validClassName) {
		this.collectionIter = col.iterator();
		this.validClassName = validClassName;
		this.nextObject = null;
	}
	

	/**
	 * Returns true if the iteration has at least one more object of the type
	 * specified by <code>validClassName</code>
	 */
	public boolean hasNext() {
		if (nextObject == null) {
			findNextTypedObject();
		}
		return nextObject != null;
	}


	/**
	 * Returns the next object of the type specified by <code>validClassName</code>
	 * @throws NoSuchElementException 
	 * 		Thrown if the iteration contains no more elements
	 */
	public Object next() {
		// if there is no next object try to find one
		if (nextObject == null) {
			findNextTypedObject();
		}
		
		// if there's still no next object, we're done iterating and hasNext() 
		// should have returned false...  Throw exception
		if (nextObject == null) {
			throw new NoSuchElementException();
		}
		
		// copy the next object's pointer
		Object returnMe = nextObject;
		// clear the next object
		nextObject = null;
		// return
		return returnMe;
	}


	/**
	 * Remove is not implemented.
	 * @throws UnsupportedOperationException
	 */
	public void remove() {
		throw new UnsupportedOperationException("remove() is not supported by " + getClass().getName());
	}
	
	
	private void findNextTypedObject() {
		// iterate the underlying list looking for an object of the right type
		while (nextObject == null && collectionIter.hasNext()) {
			Object candidate = collectionIter.next();
			if (candidate.getClass().getName().equals(validClassName)) {
				nextObject = candidate;
			}
		}
	}
}
