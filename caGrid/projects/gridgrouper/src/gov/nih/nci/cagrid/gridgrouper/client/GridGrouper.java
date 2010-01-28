package gov.nih.nci.cagrid.gridgrouper.client;

import edu.internet2.middleware.grouper.GroupNotFoundException;
import edu.internet2.middleware.grouper.GrouperRuntimeException;
import edu.internet2.middleware.grouper.InsufficientPrivilegeException;
import edu.internet2.middleware.grouper.StemNotFoundException;
import edu.internet2.middleware.subject.Subject;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.gridgrouper.bean.GroupDescriptor;
import gov.nih.nci.cagrid.gridgrouper.bean.GroupIdentifier;
import gov.nih.nci.cagrid.gridgrouper.bean.MemberFilter;
import gov.nih.nci.cagrid.gridgrouper.bean.MembershipExpression;
import gov.nih.nci.cagrid.gridgrouper.bean.MembershipType;
import gov.nih.nci.cagrid.gridgrouper.bean.StemDescriptor;
import gov.nih.nci.cagrid.gridgrouper.bean.StemIdentifier;
import gov.nih.nci.cagrid.gridgrouper.grouper.GroupI;
import gov.nih.nci.cagrid.gridgrouper.grouper.GrouperI;
import gov.nih.nci.cagrid.gridgrouper.grouper.MemberI;
import gov.nih.nci.cagrid.gridgrouper.grouper.StemI;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.GridGrouperRuntimeFault;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.GroupNotFoundFault;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.InsufficientPrivilegeFault;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.StemNotFoundFault;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.globus.gsi.GlobusCredential;
import org.globus.wsrf.impl.security.authorization.Authorization;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class GridGrouper extends GridGrouperObject implements GrouperI {

    public static final String ROOT_STEM = "";

    private GridGrouperClient client;


    /**
     * Used to Construct a Grid Grouper object corresponding to a Grid Grouper
     * Service.
     * 
     * @param serviceURI
     *            The service URI of the Grid Grouper service.
     */
    public GridGrouper(String serviceURI) {
        this(serviceURI, null);
    }

    /**
     * Used to Construct a Grid Grouper object corresponding to a Grid Grouper
     * Service. This constructor uses a default credential search order (as
     * specified by Globus). Thus, this constructor is preferable to use
     * when creating a GridGrouper instance in a service implementation.
     * 
     * @param serviceURI
     *            The service URI of the Grid Grouper service.
     * @param preferAnonymous
     * 			  Whether or not to prefer connecting anonymously to the service.
     */
	public GridGrouper(String serviceURI, boolean preferAnonymous) {
		try {
			GridGrouperClient c = new GridGrouperClient(serviceURI);
			c.setAnonymousPrefered(preferAnonymous);
			this.setClient(c);
		} catch (Exception e) {
			getLog().error(e.getMessage(), e);
			throw new GrouperRuntimeException(Utils.getExceptionMessage(e));
		}
	}

    /**
     * Used to Construct a Grid Grouper object corresponding to a Grid Grouper
     * Service.
     * 
     * @param serviceURI
     *            The service URI of the Grid Grouper service.
     * @param cred
     *            The grid credential to use to authenticate to the Grid Grouper
     *            Service.
     */
    public GridGrouper(String serviceURI, GlobusCredential cred) {
        try {
            GridGrouperClient c = new GridGrouperClient(serviceURI,cred);
            if(cred==null){
                c.setAnonymousPrefered(true);
            }else{
                c.setAnonymousPrefered(false);
            }    
            this.setClient(c);
        } catch (Exception e) {
            getLog().error(e.getMessage(), e);
            throw new GrouperRuntimeException(Utils.getExceptionMessage(e));
        }
    }


    /**
     * This method specifies an authorization policy that the client should use
     * for authorizing the server that it connects to.
     * 
     * @param authorization
     *            The authorization policy to enforce
     */

    public void setAuthorization(Authorization authorization) {
        client.setAuthorization(authorization);
    }


    /**
     * Returns a Stem object corresponding to the Grid Grouper root stem.
     * 
     * @return Stem object corresponding to the Grid Grouper root stem.
     * @throws StemNotFoundException
     *             Thrown if the root stem could not be found.
     */
    public StemI getRootStem() throws StemNotFoundException {
        return findStem(ROOT_STEM);
    }


    /**
     * Obtains the Stem object for a specified Stem.
     * 
     * @param name
     *            The name of the stem
     * @return The Stem Object or the requested stem.
     * @throws StemNotFoundException
     *             Thrown if the request stem could not be found.
     */
    public StemI findStem(String name) throws StemNotFoundException {
        try {
            StemDescriptor des = getClient().getStem(getStemIdentifier(name));
            return new Stem(this, des);
        } catch (StemNotFoundFault f) {
            throw new StemNotFoundException(f.getFaultString());
        } catch (GridGrouperRuntimeFault e) {
            getLog().error(e.getMessage(), e);
            throw new GrouperRuntimeException(e.getFaultString());
        } catch (Exception e) {
            getLog().error(e.getMessage(), e);
            throw new GrouperRuntimeException(Utils.getExceptionMessage(e));
        }
    }


    /**
     * Obtains the Group object for a specified Group.
     * 
     * @param name
     *            The name of the group.
     * @return The Group Object or the requested stem.
     * @throws GroupNotFoundException
     *             Thrown if the request group could not be found.
     */
    public GroupI findGroup(String name) throws GroupNotFoundException {
        try {
            GroupDescriptor des = getClient().getGroup(getGroupIdentifier(name));
            return new Group(this, des);
        } catch (GroupNotFoundFault f) {
            throw new GroupNotFoundException(f.getFaultString());
        } catch (GridGrouperRuntimeFault e) {
            getLog().error(e.getMessage(), e);
            throw new GrouperRuntimeException(e.getFaultString());
        } catch (Exception e) {
            getLog().error(e.getMessage(), e);
            throw new GrouperRuntimeException(Utils.getExceptionMessage(e));
        }
    }


    protected Set<StemI> getChildStems(String stemName) {
        try {
            StemDescriptor[] children = getClient().getChildStems(getStemIdentifier(stemName));
            Set<StemI> set = new HashSet<StemI>();
            if (children != null) {
                for (int i = 0; i < children.length; i++) {
                    set.add(new Stem(this, children[i]));
                }
            }
            return set;
        } catch (GridGrouperRuntimeFault e) {
            getLog().error(e.getMessage(), e);
            throw new GrouperRuntimeException(e.getFaultString());
        } catch (Exception e) {
            getLog().error(e.getMessage(), e);
            throw new GrouperRuntimeException(Utils.getExceptionMessage(e));
        }
    }


    protected StemI getParentStem(String childStemName) throws StemNotFoundException {
        try {
            StemDescriptor des = getClient().getParentStem(getStemIdentifier(childStemName));
            return new Stem(this, des);
        } catch (StemNotFoundFault f) {
            throw new StemNotFoundException(f.getFaultString());
        } catch (GridGrouperRuntimeFault e) {
            getLog().error(e.getMessage(), e);
            throw new GrouperRuntimeException(e.getFaultString());
        } catch (Exception e) {
            getLog().error(e.getMessage(), e);
            throw new GrouperRuntimeException(Utils.getExceptionMessage(e));
        }
    }


    protected GridGrouperClient getClient() {
        return client;
    }


    protected void setClient(GridGrouperClient client) {
        this.client = client;
    }


    protected StemIdentifier getStemIdentifier(String stemName) {
        StemIdentifier id = new StemIdentifier();
        id.setGridGrouperURL(getClient().getEndpointReference().toString());
        id.setStemName(stemName);
        return id;
    }


    protected GroupIdentifier getGroupIdentifier(String groupName) {
        GroupIdentifier id = new GroupIdentifier();
        id.setGridGrouperURL(getName());
        id.setGroupName(groupName);
        return id;
    }


    /**
     * Obtains the name of the Grid Grouper, generally the Grid Grouper service
     * URI.
     * 
     * @return The name of the Grid Grouper service.
     */
    public String getName() {
        return getClient().getEndpointReference().getAddress().toString();
    }


    public String getProxyIdentity() {
        return getClient().getProxyIdentity();
    }


    /**
     * Determines whether or not a subject is a member of a group.
     * 
     * @param subjectId
     *            The id of the subject.
     * @param groupName
     *            The name of the group.
     * @return True if member
     * @throws GroupNotFoundException
     *             Thrown if the request group could not be found.
     */

    public boolean isMemberOf(String subjectId, String groupName) throws GroupNotFoundException {
        try {
            return getClient().isMemberOf(getGroupIdentifier(groupName), subjectId, MemberFilter.All);
        } catch (GridGrouperRuntimeFault e) {
            getLog().error(e.getMessage(), e);
            throw new GrouperRuntimeException(e.getFaultString());
        } catch (GroupNotFoundFault f) {
            throw new GroupNotFoundException(f.getFaultString());
        } catch (Exception e) {
            getLog().error(e.getMessage(), e);
            throw new GrouperRuntimeException(Utils.getExceptionMessage(e));
        }
    }


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

    public boolean isMemberOf(Subject subject, String groupName) throws GroupNotFoundException {
        try {
            return getClient().isMemberOf(getGroupIdentifier(groupName), subject.getId(), MemberFilter.All);
        } catch (GridGrouperRuntimeFault e) {
            getLog().error(e.getMessage(), e);
            throw new GrouperRuntimeException(e.getFaultString());
        } catch (GroupNotFoundFault f) {
            throw new GroupNotFoundException(f.getFaultString());
        } catch (Exception e) {
            getLog().error(e.getMessage(), e);
            throw new GrouperRuntimeException(Utils.getExceptionMessage(e));
        }

    }


    public boolean isMember(String member, MembershipExpression exp) {
        try {
            return getClient().isMember(member, exp);
        } catch (Exception e) {
            getLog().error(e.getMessage(), e);
            throw new GrouperRuntimeException(Utils.getExceptionMessage(e));
        }
    }


    public MemberI findMember(Subject subject) throws GrouperRuntimeException, InsufficientPrivilegeException {
        return findMember(subject.getId());
    }


    public MemberI findMember(String subject) throws GrouperRuntimeException, InsufficientPrivilegeException {
        try {
            return new Member(this, getClient().getMember(subject));
        } catch (GridGrouperRuntimeFault e) {
            getLog().error(e.getMessage(), e);
            throw new GrouperRuntimeException(e.getFaultString());
        } catch (InsufficientPrivilegeFault f) {
            throw new InsufficientPrivilegeException(f.getFaultString());
        } catch (Exception e) {
            getLog().error(e.getMessage(), e);
            throw new GrouperRuntimeException(Utils.getExceptionMessage(e));
        }
    }


    public Set<GroupI> getMembersGroups(String subject, MembershipType type) throws GrouperRuntimeException,
        InsufficientPrivilegeException {
        try {
            GroupDescriptor[] list = getClient().getMembersGroups(subject, type);
            Set<GroupI> grps = new LinkedHashSet<GroupI>();
            if (list != null) {
                for (int i = 0; i < list.length; i++) {
                    grps.add(new Group(this, list[i]));
                }
            }
            return grps;
        } catch (GridGrouperRuntimeFault e) {
            getLog().error(e.getMessage(), e);
            throw new GrouperRuntimeException(e.getFaultString());
        } catch (InsufficientPrivilegeFault f) {
            throw new InsufficientPrivilegeException(f.getFaultString());
        } catch (Exception e) {
            getLog().error(e.getMessage(), e);
            throw new GrouperRuntimeException(Utils.getExceptionMessage(e));
        }
    }


    public Set<GroupI> getMembersEffectiveGroups(String subject) throws GrouperRuntimeException,
        InsufficientPrivilegeException {
        return getMembersGroups(subject, MembershipType.EffectiveMembers);
    }


    public Set<GroupI> getMembersGroups(String subject) throws GrouperRuntimeException, InsufficientPrivilegeException {
        return getMembersGroups(subject, MembershipType.Any);
    }


    public Set<GroupI> getMembersImmediateGroups(String subject) throws GrouperRuntimeException,
        InsufficientPrivilegeException {
        return getMembersGroups(subject, MembershipType.ImmediateMembers);
    }

}
