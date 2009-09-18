package gov.nih.nci.cagrid.gridgrouper.grouper;

import edu.internet2.middleware.grouper.GroupNotFoundException;
import edu.internet2.middleware.grouper.StemNotFoundException;
import edu.internet2.middleware.subject.Subject;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public interface GrouperI {
	/**
	 * Returns a Stem object corresponding to the Grid Grouper root stem.
	 * 
	 * @return Stem object corresponding to the Grid Grouper root stem.
	 * @throws StemNotFoundException
	 *             Thrown if the root stem could not be found.
	 */
	public StemI getRootStem() throws StemNotFoundException;


	/**
	 * Obtains the Stem object for a specified Stem.
	 * 
	 * @param name
	 *            The name of the stem
	 * @return The Stem Object or the requested stem.
	 * @throws StemNotFoundException
	 *             Thrown if the request stem could not be found.
	 */
	public StemI findStem(String name) throws StemNotFoundException;


	/**
	 * Obtains the name of the Grid Grouper, generally the Grid Grouper service
	 * URI.
	 * 
	 * @return The name of the Grid Grouper service.
	 */
	public String getName();


	/**
	 * Obtains the Group object for a specified Group.
	 * 
	 * @param name
	 *            The name of the group.
	 * @return The Group Object or the requested stem.
	 * @throws GroupNotFoundException
	 *             Thrown if the request group could not be found.
	 */
	public GroupI findGroup(String name) throws GroupNotFoundException;


	/**
	 * Determines whether or not a subject is a member of a group.
	 * 
	 * @param subjectId
	 *            The id of the subject.
	 * @param groupName
	 *            The name of the group.
	 * @return
     *      True if member
	 * @throws GroupNotFoundException
	 *             Thrown if the request group could not be found.
	 */
	public boolean isMemberOf(String subjectId, String groupName) throws GroupNotFoundException;


	/**
	 * Determines whether or not a subject is a member of a group.
	 * 
	 * @param subject
	 *            The subject.
	 * @param groupName
	 *            The name of the group.
	 * @return Returns true if the subject is a member of the group, or false if
	 *         the user is not a member of the group.
	 * @throws GroupNotFoundException
	 *             Thrown if the request group could not be found.
	 */
	public boolean isMemberOf(Subject subject, String groupName) throws GroupNotFoundException;

}
