package gov.nih.nci.cagrid.data.enumeration.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.xml.soap.SOAPElement;

import org.globus.ws.enumeration.EnumIterator;
import org.globus.ws.enumeration.IterationConstraints;
import org.globus.ws.enumeration.IterationResult;
import org.globus.ws.enumeration.TimeoutException;
import org.globus.wsrf.utils.AnyHelper;

/** 
 *  LazyQueryResultEnumIterator
 *  EnumIterator implementation to take advantage of LazyCQLQueryProcessor results
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Aug 7, 2006 
 * @version $Id: LazyQueryResultEnumIterator.java,v 1.3 2008-08-21 15:05:49 dervin Exp $ 
 */
public class LazyQueryResultEnumIterator implements EnumIterator {
	private boolean hasBeenReleased = false;
	private Iterator lazyResults = null;
	
	public LazyQueryResultEnumIterator(Iterator lazyResults) {
		this.lazyResults = lazyResults;
	}
	

	public IterationResult next(IterationConstraints constraints) throws TimeoutException, NoSuchElementException {
		if (hasBeenReleased) {
			throw new NoSuchElementException("The enumeration has been released!");
		}
		// list to hold soap elements
		List<SOAPElement> soapElements = new ArrayList<SOAPElement>();
		// timer variable
		long startTime = System.currentTimeMillis();
		// compute current state
		int currentSize = soapElements.size();
		int currentChars = computeCharCount(soapElements);
		long currentTime = System.currentTimeMillis() - startTime;
		// iterate the lazy collection
		while (lazyResults.hasNext() && shouldAddElement(constraints, currentSize, currentChars, currentTime)) {
			SOAPElement element = AnyHelper.toAny(lazyResults.next());
			// TODO: what to do if this new element overflows the maxChars?
			soapElements.add(element);
		}
		// Build SOAPElement array and return IterationResult
		SOAPElement[] elements = new SOAPElement[soapElements.size()];
		soapElements.toArray(elements);
		IterationResult result = new IterationResult(elements, !lazyResults.hasNext());
		return result;
	}
	
	
	private int computeCharCount(List soapElements) {
		int count = 0;
		for (int i = 0; i < soapElements.size(); i++) {
            // TODO: do I need to use toString() on the SOAPElement, instead of getValue()?
			count += ((SOAPElement) soapElements.get(i)).getValue().length();
		}
		return count;
	}
	
	
	private boolean shouldAddElement(IterationConstraints constraints, int currentSize, int currentChars, long currentTime) {
		boolean sizeOk = true;
		boolean charsOk = true;
		boolean timeOk = true;
		// check size
		sizeOk = currentSize < constraints.getMaxElements();
		if (constraints.getMaxCharacters() > 0) {
			// chars
			charsOk = currentChars < constraints.getMaxCharacters();
		}
		if (constraints.getMaxTime() != null && !constraints.getMaxTime().isNegative()) {
			// time
			timeOk = currentTime < constraints.getMaxTime().getAsCalendar().getTimeInMillis();
		}
		
		return sizeOk && charsOk && timeOk;
	}


	public void release() {
		// throw away the iterator
		lazyResults = null;
		hasBeenReleased = true;
	}
}
