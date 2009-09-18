/**
 * 
 */
package org.cagrid.installer.validator;

import java.util.Map;

import org.pietschy.wizard.InvalidStateException;

/**
 * @author <a href="mailto:joshua.phillips@semanticbits.com">Joshua Phillips</a>
 *
 */
public interface Validator {
	void validate(Map state) throws InvalidStateException;
}
