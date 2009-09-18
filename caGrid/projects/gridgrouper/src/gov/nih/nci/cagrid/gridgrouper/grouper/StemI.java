package gov.nih.nci.cagrid.gridgrouper.grouper;

import java.util.Date;
import java.util.Set;

import edu.internet2.middleware.grouper.GrantPrivilegeException;
import edu.internet2.middleware.grouper.GroupAddException;
import edu.internet2.middleware.grouper.InsufficientPrivilegeException;
import edu.internet2.middleware.grouper.Privilege;
import edu.internet2.middleware.grouper.RevokePrivilegeException;
import edu.internet2.middleware.grouper.SchemaException;
import edu.internet2.middleware.grouper.StemAddException;
import edu.internet2.middleware.grouper.StemDeleteException;
import edu.internet2.middleware.grouper.StemModifyException;
import edu.internet2.middleware.grouper.StemNotFoundException;
import edu.internet2.middleware.subject.Subject;
import edu.internet2.middleware.subject.SubjectNotFoundException;
import gov.nih.nci.cagrid.gridgrouper.bean.StemIdentifier;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public interface StemI {

	// Stem Information

	public String getCreateSource();


	public Subject getCreateSubject() throws SubjectNotFoundException;


	public Date getCreateTime();


	/**
	 * Gets the description of the stem.
	 * 
	 * @return The description of the stem.
	 */
	public String getDescription();


	/**
	 * Gets the local display name of the stem.
	 * 
	 * @return The local display name of the stem.
	 */
	public String getDisplayExtension();


	/**
	 * Gets the full display name of the stem.
	 * 
	 * @return The full display name of the stem.
	 */
	public String getDisplayName();


	/**
	 * Gets the local name of the stem.
	 * 
	 * @return The local name of the stem.
	 */
	public String getExtension();


	public String getModifySource();


	public Subject getModifySubject() throws SubjectNotFoundException;


	public Date getModifyTime();


	/**
	 * Gets the full name of the stem.
	 * 
	 * @return The full name of the stem.
	 */
	public String getName();


	/**
	 * Gets the UUID for the stem.
	 * 
	 * @return The UUID for the stem.
	 */
	public String getUuid();


	public StemIdentifier getStemIdentifier();


	// Stem Actions
	public Set getChildStems();


	public StemI getParentStem() throws StemNotFoundException;


	public void setDescription(String value) throws InsufficientPrivilegeException, StemModifyException;


	public void setDisplayExtension(String value) throws InsufficientPrivilegeException, StemModifyException;


	public Set getCreators();


	public Set getPrivs(Subject subj);


	public Set getStemmers();


	public boolean hasCreate(Subject subj);


	public boolean hasStem(Subject subj);


	public void grantPriv(Subject subj, Privilege priv) throws GrantPrivilegeException, InsufficientPrivilegeException,
		SchemaException;


	public void revokePriv(Subject subj, Privilege priv) throws InsufficientPrivilegeException,
		RevokePrivilegeException, SchemaException;


	public StemI addChildStem(String extension, String displayExtension) throws InsufficientPrivilegeException,
		StemAddException;


	public void delete() throws InsufficientPrivilegeException, StemDeleteException;


	public GroupI addChildGroup(String extension, String displayExtension) throws GroupAddException,
		InsufficientPrivilegeException;


	public Set getChildGroups();
}
