package gov.nih.nci.cagrid.gridgrouper.grouper;

import java.util.Set;

import edu.internet2.middleware.grouper.GrouperRuntimeException;
import edu.internet2.middleware.grouper.InsufficientPrivilegeException;
import edu.internet2.middleware.subject.Source;
import edu.internet2.middleware.subject.Subject;
import edu.internet2.middleware.subject.SubjectNotFoundException;
import edu.internet2.middleware.subject.SubjectType;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public interface MemberI {

	// Descriptors

	public String getSubjectId();


	public Source getSubjectSource() throws GrouperRuntimeException;


	public String getSubjectSourceId();


	public SubjectType getSubjectType();


	public String getSubjectTypeId();


	public String getUuid();


	public Subject getSubject() throws SubjectNotFoundException;


	// Actions

	// public boolean canAdmin(Group g) throws IllegalArgumentException;
	//
	// public boolean canCreate(StemI ns) throws IllegalArgumentException;
	//
	// public boolean canOptin(Group g) throws IllegalArgumentException;
	//
	// public boolean canOptout(Group g) throws IllegalArgumentException;
	//
	// public boolean canRead(Group g) throws IllegalArgumentException;
	//
	// public boolean canStem(StemI ns) throws IllegalArgumentException;
	//
	// public boolean canUpdate(Group g) throws IllegalArgumentException;
	//
	// public boolean canView(Group g) throws IllegalArgumentException;
	//
	public Set getEffectiveGroups() throws GrouperRuntimeException, InsufficientPrivilegeException;


	//
	// public Set getEffectiveMemberships() throws GrouperRuntimeException;
	//
	// public Set getEffectiveMemberships(FieldI f) throws SchemaException;
	//
	public Set getGroups() throws GrouperRuntimeException, InsufficientPrivilegeException;

	public Set getImmediateGroups() throws GrouperRuntimeException, InsufficientPrivilegeException;

	//
	// public Set getImmediateMemberships() throws GrouperRuntimeException;
	//
	// public Set getImmediateMemberships(FieldI f) throws SchemaException;
	//
	// public Set getMemberships() throws GrouperRuntimeException;
	//
	// public Set getMemberships(FieldI f) throws SchemaException;
	//
	// public Set getPrivs(Group g);
	//
	// public Set getPrivs(StemI ns);
	//
	// public Set hasAdmin() throws GrouperRuntimeException;
	//
	// public boolean hasAdmin(Group g);
	//
	// public Set hasCreate() throws GrouperRuntimeException;
	//
	// public boolean hasCreate(StemI ns);
	//
	// public Set hasOptin() throws GrouperRuntimeException;
	//
	// public boolean hasOptin(Group g);
	//
	// public Set hasOptout() throws GrouperRuntimeException;
	//
	// public boolean hasOptout(Group g);
	//
	// public Set hasRead() throws GrouperRuntimeException;
	//
	// public boolean hasRead(Group g);
	//
	// public Set hasStem() throws GrouperRuntimeException;
	//
	// public boolean hasStem(StemI ns);
	//
	// public Set hasUpdate() throws GrouperRuntimeException;
	//
	// public boolean hasUpdate(Group g);
	//
	// public Set hasView() throws GrouperRuntimeException;
	//
	// public boolean hasView(Group g);
	//
	// public boolean isEffectiveMember(Group g) throws GrouperRuntimeException;
	//
	// public boolean isEffectiveMember(Group g, FieldI f) throws
	// SchemaException;
	//
	// public boolean isImmediateMember(Group g) throws GrouperRuntimeException;
	//
	// public boolean isImmediateMember(Group g, FieldI f) throws
	// SchemaException;
	//
	// public boolean isMember(Group g) throws GrouperRuntimeException;
	//
	// public boolean isMember(Group g, FieldI f) throws SchemaException;
	//
	// public void setSubjectId(String id) throws
	// InsufficientPrivilegeException;
}
