package gov.nih.nci.cagrid.gridgrouper.client;

import edu.internet2.middleware.grouper.Privilege;
import edu.internet2.middleware.subject.Subject;
import gov.nih.nci.cagrid.gridgrouper.grouper.NamingPrivilegeI;

import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class NamingPrivilege extends GridGrouperObject implements NamingPrivilegeI {

	public static final Privilege CREATE = Privilege.getInstance("create");

	public static final Privilege STEM = Privilege.getInstance("stem");

	// Private Instance Variables
	private boolean isRevokable;

	private String className;

	private String name;

	private String stemName;

	private Subject owner;

	private Subject subj;


	// Constructors
	public NamingPrivilege(String stemName, Subject subj, Subject owner, Privilege priv, String klass,
		boolean isRevokable) {
		this.isRevokable = isRevokable;
		this.className = klass;
		this.name = priv.toString();
		this.owner = owner;
		this.stemName = stemName;
		this.subj = subj;
	}


	public String getImplementationName() {
		return this.className;
	} // public String getImplementationName()


	public boolean isRevokable() {
		return this.isRevokable;
	} // public boolean isRevokable()


	public String getName() {
		return this.name;
	} // public String getName()


	public Subject getOwner() {
		return this.owner;
	} // public Subject getOwner()


	public String getStemName() {
		return this.stemName;
	} // public Object getStem()


	public Subject getSubject() {
		return this.subj;
	} // public Subject getSubject()


	public String toString() {
		return new ToStringBuilder(this).append("name", this.getName()).append("implementation",
			this.getImplementationName()).append("revokable", this.isRevokable()).append("stem", this.getStemName())
			.append("subject", this.getSubject()).append("owner", this.getOwner()).toString();
	} // public String toString()
}
