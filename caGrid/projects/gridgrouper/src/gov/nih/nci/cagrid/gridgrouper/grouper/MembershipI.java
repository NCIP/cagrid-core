package gov.nih.nci.cagrid.gridgrouper.grouper;

import edu.internet2.middleware.grouper.GroupNotFoundException;
import edu.internet2.middleware.grouper.MemberNotFoundException;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public interface MembershipI {
	// public Set getChildMemberships();

	public GroupI getGroup() throws GroupNotFoundException;


	// public FieldI getList();

	public MemberI getMember() throws MemberNotFoundException;


	// public MembershipI getParentMembership() throws
	// MembershipNotFoundException;

	// public Owner getVia() throws OwnerNotFoundException;

	public GroupI getViaGroup() throws GroupNotFoundException;


	public int getDepth();
}
