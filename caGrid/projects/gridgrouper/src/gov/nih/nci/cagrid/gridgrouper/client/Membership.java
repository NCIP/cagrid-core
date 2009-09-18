package gov.nih.nci.cagrid.gridgrouper.client;

import edu.internet2.middleware.grouper.GroupNotFoundException;
import edu.internet2.middleware.grouper.MemberNotFoundException;
import edu.internet2.middleware.subject.SubjectNotFoundException;
import gov.nih.nci.cagrid.gridgrouper.bean.MembershipDescriptor;
import gov.nih.nci.cagrid.gridgrouper.grouper.GroupI;
import gov.nih.nci.cagrid.gridgrouper.grouper.MemberI;
import gov.nih.nci.cagrid.gridgrouper.grouper.MembershipI;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class Membership extends GridGrouperObject implements MembershipI {

	private MembershipDescriptor des;

	private GroupI group;

	private GroupI viaGroup;

	private Member member;

	private GridGrouper gridGrouper;


	protected Membership(GridGrouper gridGrouper, MembershipDescriptor des) throws SubjectNotFoundException {
		this.gridGrouper = gridGrouper;
		this.des = des;
		this.member = new Member(gridGrouper, des.getMember());
		this.group = new Group(this.gridGrouper, des.getGroup());
		if (this.des.getViaGroup() != null) {
			this.viaGroup = new Group(this.gridGrouper, des.getViaGroup());
		}

	}


	public int getDepth() {
		return des.getDepth();
	}


	public GroupI getGroup() throws GroupNotFoundException {
		return group;
	}


	public MemberI getMember() throws MemberNotFoundException {
		return member;
	}


	public GroupI getViaGroup() throws GroupNotFoundException {
		if (viaGroup != null) {
			return viaGroup;
		} else {
			throw new GroupNotFoundException("No via group found for this membership!!!");
		}
	}

}
