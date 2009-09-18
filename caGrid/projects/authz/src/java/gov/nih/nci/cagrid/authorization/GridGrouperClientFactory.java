/**
 * 
 */
package gov.nih.nci.cagrid.authorization;

import gov.nih.nci.cagrid.gridgrouper.grouper.GrouperI;

/**
 * @author <a href="mailto:joshua.phillips@semanticbits.com">Joshua Phillips</a>
 *
 */
public interface GridGrouperClientFactory {
	
	/**
	 * Returns a client to the GroupGrouper service given
	 * a URL.
	 * 
	 * @param url
	 * @return GridGrouper client
	 * @throws RuntimeException if error occurs instantiating client
	 */
	GrouperI getGridGrouperClient(String url);

}
