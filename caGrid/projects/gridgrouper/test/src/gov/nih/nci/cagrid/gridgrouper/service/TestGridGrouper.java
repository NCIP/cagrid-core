package gov.nih.nci.cagrid.gridgrouper.service;

import junit.framework.TestCase;
import edu.internet2.middleware.grouper.GroupFinder;
import edu.internet2.middleware.grouper.GroupNotFoundException;
import edu.internet2.middleware.grouper.GrouperSession;
import edu.internet2.middleware.grouper.RegistryReset;
import edu.internet2.middleware.grouper.StemFinder;
import edu.internet2.middleware.grouper.StemNotFoundException;
import edu.internet2.middleware.grouper.SubjectFinder;
import gov.nih.nci.cagrid.common.FaultUtil;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */

public class TestGridGrouper extends TestCase {

	public void testCreateDestroy() {
		try {
			RegistryReset.reset();
			GrouperSession session = GrouperSession.start(SubjectFinder.findById(GridGrouper.GROUPER_SUPER_USER));
			try {
				StemFinder.findByName(session, GridGrouper.GROUPER_ADMIN_STEM_NAME);
				fail(GridGrouper.GROUPER_ADMIN_STEM_NAME + " stem should not exist!!!");
			} catch (StemNotFoundException e) {

			}
			try {
				GroupFinder.findByName(session, GridGrouper.GROUPER_ADMIN_GROUP_NAME);
				fail(GridGrouper.GROUPER_ADMIN_GROUP_NAME + " group should not exist!!!");
			} catch (GroupNotFoundException gne) {

			}

			new GridGrouper();
			try {
				StemFinder.findByName(session, GridGrouper.GROUPER_ADMIN_STEM_NAME);

			} catch (StemNotFoundException e) {
				fail(GridGrouper.GROUPER_ADMIN_STEM_NAME + " stem should exist!!!");
			}
			try {
				GroupFinder.findByName(session, GridGrouper.GROUPER_ADMIN_GROUP_NAME);

			} catch (GroupNotFoundException gne) {
				fail(GridGrouper.GROUPER_ADMIN_GROUP_NAME + " group should exist!!!");
			}

			RegistryReset.reset();
			try {
				StemFinder.findByName(session, GridGrouper.GROUPER_ADMIN_STEM_NAME);
				fail(GridGrouper.GROUPER_ADMIN_STEM_NAME + " stem should not exist!!!");
			} catch (StemNotFoundException e) {

			}
			try {
				GroupFinder.findByName(session, GridGrouper.GROUPER_ADMIN_GROUP_NAME);
				fail(GridGrouper.GROUPER_ADMIN_GROUP_NAME + " group should not exist!!!");
			} catch (GroupNotFoundException gne) {

			}
		} catch (Exception e) {
			FaultUtil.printFault(e);
			assertTrue(false);
		}

	}

}