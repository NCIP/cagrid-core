/**
 * 
 */
package org.cagrid.installer.steps.options;

/**
 * @author <a href="mailto:joshua.phillips@semanticbits.com">Joshua Phillips</a>
 *
 */
public interface PropertyConfigurationOption {
	String getName();
	String getDescription();
	boolean isRequired();
}
