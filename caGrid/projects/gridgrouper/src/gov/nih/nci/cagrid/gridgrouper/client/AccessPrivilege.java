package gov.nih.nci.cagrid.gridgrouper.client;

import edu.internet2.middleware.grouper.GrouperRuntimeException;
import edu.internet2.middleware.grouper.Privilege;
import edu.internet2.middleware.subject.Subject;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.gridgrouper.bean.GroupPrivilege;
import gov.nih.nci.cagrid.gridgrouper.common.SubjectUtils;
import gov.nih.nci.cagrid.gridgrouper.grouper.AccessPrivilegeI;
import gov.nih.nci.cagrid.gridgrouper.grouper.GroupI;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class AccessPrivilege extends GridGrouperObject implements AccessPrivilegeI {

	public static final Privilege ADMIN = Privilege.getInstance("admin");

	public static final Privilege OPTIN = Privilege.getInstance("optin");

	public static final Privilege OPTOUT = Privilege.getInstance("optout");

	public static final Privilege READ = Privilege.getInstance("read");

	public static final Privilege UPDATE = Privilege.getInstance("update");

	public static final Privilege VIEW = Privilege.getInstance("view");

	private GroupPrivilege priv;

	private GridGrouper grouper;

	private GroupI group;


	public AccessPrivilege(GridGrouper grouper, GroupPrivilege priv) {
		this.grouper = grouper;
		this.priv = priv;

	}


	public GroupI getGroup() {
		if (group == null) {
			try {
				group = grouper.findGroup(this.priv.getGroupName());
			} catch (Exception e) {
				this.getLog().error(e.getMessage(), e);
				throw new GrouperRuntimeException(Utils.getExceptionMessage(e));
			}
		}
		return group;
	}


	public String getImplementationName() {
		return priv.getImplementationClass();
	}


	public String getName() {
		return priv.getPrivilegeType().getValue();
	}


	public Subject getOwner() {
		try {
			return SubjectUtils.getSubject(priv.getOwner());
		} catch (Exception e) {
			this.getLog().error(e.getMessage(), e);
			throw new GrouperRuntimeException(Utils.getExceptionMessage(e));
		}
	}


	public Subject getSubject() {
		try {
			return SubjectUtils.getSubject(priv.getSubject());
		} catch (Exception e) {
			this.getLog().error(e.getMessage(), e);
			throw new GrouperRuntimeException(Utils.getExceptionMessage(e));
		}
	}


	public boolean isRevokable() {
		return this.priv.isIsRevokable();
	}

}
