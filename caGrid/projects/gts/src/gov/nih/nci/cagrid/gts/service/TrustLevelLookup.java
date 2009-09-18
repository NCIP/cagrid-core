package gov.nih.nci.cagrid.gts.service;

import gov.nih.nci.cagrid.gts.stubs.types.GTSInternalFault;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public interface TrustLevelLookup {
	public boolean doesTrustLevelExist(String name) throws GTSInternalFault;
}
