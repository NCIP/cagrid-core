package gov.nih.nci.cagrid.wsenum.utils;

import java.util.NoSuchElementException;

import javax.xml.soap.SOAPElement;

import org.globus.ws.enumeration.EnumIterator;
import org.globus.ws.enumeration.IterationConstraints;
import org.globus.ws.enumeration.IterationResult;
import org.globus.ws.enumeration.TimeoutException;

/**
 * DummyEnumIterator
 * Dummy implementation of the EnumIterator interface doesn't actually do anything.
 * Intended for use as a placeholder in things that require an EnumIterator to be
 * returned but no data is present.
 * 
 * @author ervin
 *
 */
public class DummyEnumIterator implements EnumIterator {
    boolean returnedEndOfSequence;

    public DummyEnumIterator() {
        this.returnedEndOfSequence = false;
    }


    public IterationResult next(IterationConstraints constraints) throws TimeoutException, NoSuchElementException {
        if (!returnedEndOfSequence) {
            returnedEndOfSequence = true;
            return new IterationResult(new SOAPElement[0], true);
        }
        throw new NoSuchElementException(DummyEnumIterator.class.getName() + " does not return any data!");
    }


    public void release() {
        // ok...
    }
}
