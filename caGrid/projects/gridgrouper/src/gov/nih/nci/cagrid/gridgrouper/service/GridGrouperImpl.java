package gov.nih.nci.cagrid.gridgrouper.service;

import gov.nih.nci.cagrid.gridgrouper.subject.AnonymousGridUserSubject;

import java.rmi.RemoteException;

import org.globus.wsrf.security.SecurityManager;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class GridGrouperImpl {
    private GridGrouper gridGrouper;


    public GridGrouperImpl() throws RemoteException {
        this.gridGrouper = new GridGrouper();
    }


    private String getCallerIdentity() {
        String caller = SecurityManager.getManager().getCaller();
        System.out.println("Grid Grouper Caller: " + caller);
        if ((caller == null) || (caller.equals("<anonymous>"))) {
            return AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID;
        } else {
            return caller;
        }
    }


    public gov.nih.nci.cagrid.gridgrouper.bean.StemDescriptor getStem(
        gov.nih.nci.cagrid.gridgrouper.bean.StemIdentifier stem) throws RemoteException,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GridGrouperRuntimeFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.StemNotFoundFault {
        return gridGrouper.getStem(getCallerIdentity(), stem);
    }


    public gov.nih.nci.cagrid.gridgrouper.bean.StemDescriptor[] getChildStems(
        gov.nih.nci.cagrid.gridgrouper.bean.StemIdentifier parentStem) throws RemoteException,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GridGrouperRuntimeFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.StemNotFoundFault {
        return gridGrouper.getChildStems(getCallerIdentity(), parentStem);
    }


    public gov.nih.nci.cagrid.gridgrouper.bean.StemDescriptor getParentStem(
        gov.nih.nci.cagrid.gridgrouper.bean.StemIdentifier childStem) throws RemoteException,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GridGrouperRuntimeFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.StemNotFoundFault {
        return gridGrouper.getParentStem(getCallerIdentity(), childStem);
    }


    public java.lang.String[] getSubjectsWithStemPrivilege(gov.nih.nci.cagrid.gridgrouper.bean.StemIdentifier stem,
        gov.nih.nci.cagrid.gridgrouper.bean.StemPrivilegeType privilege) throws RemoteException,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GridGrouperRuntimeFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.StemNotFoundFault {
        return gridGrouper.getSubjectsWithStemPrivilege(getCallerIdentity(), stem, privilege);
    }


    public gov.nih.nci.cagrid.gridgrouper.bean.StemPrivilege[] getStemPrivileges(
        gov.nih.nci.cagrid.gridgrouper.bean.StemIdentifier stem, java.lang.String subject) throws RemoteException,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GridGrouperRuntimeFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.StemNotFoundFault {
        return gridGrouper.getStemPrivileges(getCallerIdentity(), stem, subject);
    }


    public boolean hasStemPrivilege(gov.nih.nci.cagrid.gridgrouper.bean.StemIdentifier stem, java.lang.String subject,
        gov.nih.nci.cagrid.gridgrouper.bean.StemPrivilegeType privilege) throws RemoteException,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GridGrouperRuntimeFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.StemNotFoundFault {
        return gridGrouper.hasStemPrivilege(getCallerIdentity(), stem, subject, privilege);
    }


    public void grantStemPrivilege(gov.nih.nci.cagrid.gridgrouper.bean.StemIdentifier stem, java.lang.String subject,
        gov.nih.nci.cagrid.gridgrouper.bean.StemPrivilegeType privilege) throws RemoteException,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GridGrouperRuntimeFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.StemNotFoundFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GrantPrivilegeFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.InsufficientPrivilegeFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.SchemaFault {
        gridGrouper.grantStemPrivilege(getCallerIdentity(), stem, subject, privilege);
    }


    public void revokeStemPrivilege(gov.nih.nci.cagrid.gridgrouper.bean.StemIdentifier stem, java.lang.String subject,
        gov.nih.nci.cagrid.gridgrouper.bean.StemPrivilegeType privilege) throws RemoteException,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.StemNotFoundFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.InsufficientPrivilegeFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.RevokePrivilegeFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.SchemaFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GridGrouperRuntimeFault {
        gridGrouper.revokeStemPrivilege(getCallerIdentity(), stem, subject, privilege);
    }


    public gov.nih.nci.cagrid.gridgrouper.bean.StemDescriptor addChildStem(
        gov.nih.nci.cagrid.gridgrouper.bean.StemIdentifier stem, java.lang.String extension,
        java.lang.String displayExtension) throws RemoteException,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GridGrouperRuntimeFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.InsufficientPrivilegeFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.StemAddFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.StemNotFoundFault {
        return gridGrouper.addChildStem(getCallerIdentity(), stem, extension, displayExtension);
    }


    public void deleteStem(gov.nih.nci.cagrid.gridgrouper.bean.StemIdentifier stem) throws RemoteException,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GridGrouperRuntimeFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.InsufficientPrivilegeFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.StemDeleteFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.StemNotFoundFault {
        gridGrouper.deleteStem(getCallerIdentity(), stem);
    }


    public gov.nih.nci.cagrid.gridgrouper.bean.GroupDescriptor getGroup(
        gov.nih.nci.cagrid.gridgrouper.bean.GroupIdentifier group) throws RemoteException,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GridGrouperRuntimeFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GroupNotFoundFault {
        return gridGrouper.getGroup(getCallerIdentity(), group);
    }


    public gov.nih.nci.cagrid.gridgrouper.bean.GroupDescriptor[] getChildGroups(
        gov.nih.nci.cagrid.gridgrouper.bean.StemIdentifier stem) throws RemoteException,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GridGrouperRuntimeFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.StemNotFoundFault {
        return gridGrouper.getChildGroups(getCallerIdentity(), stem);
    }


    public gov.nih.nci.cagrid.gridgrouper.bean.GroupDescriptor addChildGroup(
        gov.nih.nci.cagrid.gridgrouper.bean.StemIdentifier stem, java.lang.String extension,
        java.lang.String displayExtension) throws RemoteException,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GridGrouperRuntimeFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GroupAddFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.InsufficientPrivilegeFault {
        return gridGrouper.addChildGroup(getCallerIdentity(), stem, extension, displayExtension);
    }


    public void deleteGroup(gov.nih.nci.cagrid.gridgrouper.bean.GroupIdentifier group) throws RemoteException,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GridGrouperRuntimeFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GroupNotFoundFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GroupDeleteFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.InsufficientPrivilegeFault {
        gridGrouper.deleteGroup(getCallerIdentity(), group);
    }


    public gov.nih.nci.cagrid.gridgrouper.bean.StemDescriptor updateStem(
        gov.nih.nci.cagrid.gridgrouper.bean.StemIdentifier stem, gov.nih.nci.cagrid.gridgrouper.bean.StemUpdate update)
        throws RemoteException, gov.nih.nci.cagrid.gridgrouper.stubs.types.GridGrouperRuntimeFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.InsufficientPrivilegeFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.StemModifyFault {
        return gridGrouper.updateStem(getCallerIdentity(), stem, update);
    }


    public gov.nih.nci.cagrid.gridgrouper.bean.GroupDescriptor updateGroup(
        gov.nih.nci.cagrid.gridgrouper.bean.GroupIdentifier group,
        gov.nih.nci.cagrid.gridgrouper.bean.GroupUpdate update) throws RemoteException,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GridGrouperRuntimeFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GroupNotFoundFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GroupModifyFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.InsufficientPrivilegeFault {
        return gridGrouper.updateGroup(getCallerIdentity(), group, update);
    }


    public void addMember(gov.nih.nci.cagrid.gridgrouper.bean.GroupIdentifier group, java.lang.String subject)
        throws RemoteException, gov.nih.nci.cagrid.gridgrouper.stubs.types.GridGrouperRuntimeFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GroupNotFoundFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.InsufficientPrivilegeFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.MemberAddFault {
        gridGrouper.addMember(getCallerIdentity(), group, subject);
    }


    public gov.nih.nci.cagrid.gridgrouper.bean.MemberDescriptor[] getMembers(
        gov.nih.nci.cagrid.gridgrouper.bean.GroupIdentifier group,
        gov.nih.nci.cagrid.gridgrouper.bean.MemberFilter filter) throws RemoteException,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GridGrouperRuntimeFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GroupNotFoundFault {
        return gridGrouper.getMembers(getCallerIdentity(), group, filter);
    }


    public boolean isMemberOf(gov.nih.nci.cagrid.gridgrouper.bean.GroupIdentifier group, java.lang.String member,
        gov.nih.nci.cagrid.gridgrouper.bean.MemberFilter filter) throws RemoteException,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GridGrouperRuntimeFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GroupNotFoundFault {
        return gridGrouper.isMemberOf(getCallerIdentity(), group, member, filter);
    }


    public gov.nih.nci.cagrid.gridgrouper.bean.MembershipDescriptor[] getMemberships(
        gov.nih.nci.cagrid.gridgrouper.bean.GroupIdentifier group,
        gov.nih.nci.cagrid.gridgrouper.bean.MemberFilter filter) throws RemoteException,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GridGrouperRuntimeFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GroupNotFoundFault {
        return this.gridGrouper.getMemberships(getCallerIdentity(), group, filter);

    }


    public void deleteMember(gov.nih.nci.cagrid.gridgrouper.bean.GroupIdentifier group, java.lang.String member)
        throws RemoteException, gov.nih.nci.cagrid.gridgrouper.stubs.types.GridGrouperRuntimeFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.InsufficientPrivilegeFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GroupNotFoundFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.MemberDeleteFault {
        gridGrouper.deleteMember(getCallerIdentity(), group, member);
    }


    public gov.nih.nci.cagrid.gridgrouper.bean.GroupDescriptor addCompositeMember(
        gov.nih.nci.cagrid.gridgrouper.bean.GroupCompositeType type,
        gov.nih.nci.cagrid.gridgrouper.bean.GroupIdentifier composite,
        gov.nih.nci.cagrid.gridgrouper.bean.GroupIdentifier left,
        gov.nih.nci.cagrid.gridgrouper.bean.GroupIdentifier right) throws RemoteException,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GridGrouperRuntimeFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GroupNotFoundFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.MemberAddFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.InsufficientPrivilegeFault {
        return gridGrouper.addCompositeMember(getCallerIdentity(), type, composite, left, right);
    }


    public gov.nih.nci.cagrid.gridgrouper.bean.GroupDescriptor deleteCompositeMember(
        gov.nih.nci.cagrid.gridgrouper.bean.GroupIdentifier group) throws RemoteException,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GridGrouperRuntimeFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GroupNotFoundFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.InsufficientPrivilegeFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.MemberDeleteFault {
        return gridGrouper.deleteCompositeMember(getCallerIdentity(), group);
    }


    public void grantGroupPrivilege(gov.nih.nci.cagrid.gridgrouper.bean.GroupIdentifier group,
        java.lang.String subject, gov.nih.nci.cagrid.gridgrouper.bean.GroupPrivilegeType privilege)
        throws RemoteException, gov.nih.nci.cagrid.gridgrouper.stubs.types.GridGrouperRuntimeFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GroupNotFoundFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GrantPrivilegeFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.InsufficientPrivilegeFault {
        gridGrouper.grantGroupPrivilege(getCallerIdentity(), group, subject, privilege);
    }


    public void revokeGroupPrivilege(gov.nih.nci.cagrid.gridgrouper.bean.GroupIdentifier group,
        java.lang.String subject, gov.nih.nci.cagrid.gridgrouper.bean.GroupPrivilegeType privilege)
        throws RemoteException, gov.nih.nci.cagrid.gridgrouper.stubs.types.GridGrouperRuntimeFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GroupNotFoundFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.RevokePrivilegeFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.InsufficientPrivilegeFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.SchemaFault {
        gridGrouper.revokeGroupPrivilege(getCallerIdentity(), group, subject, privilege);
    }


    public java.lang.String[] getSubjectsWithGroupPrivilege(gov.nih.nci.cagrid.gridgrouper.bean.GroupIdentifier group,
        gov.nih.nci.cagrid.gridgrouper.bean.GroupPrivilegeType privilege) throws RemoteException,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GridGrouperRuntimeFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GroupNotFoundFault {
        return gridGrouper.getSubjectsWithGroupPrivilege(getCallerIdentity(), group, privilege);
    }


    public gov.nih.nci.cagrid.gridgrouper.bean.GroupPrivilege[] getGroupPrivileges(
        gov.nih.nci.cagrid.gridgrouper.bean.GroupIdentifier group, java.lang.String subject) throws RemoteException,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GridGrouperRuntimeFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GroupNotFoundFault {
        return gridGrouper.getGroupPrivileges(getCallerIdentity(), group, subject);
    }


    public boolean hasGroupPrivilege(gov.nih.nci.cagrid.gridgrouper.bean.GroupIdentifier group,
        java.lang.String subject, gov.nih.nci.cagrid.gridgrouper.bean.GroupPrivilegeType privilege)
        throws RemoteException, gov.nih.nci.cagrid.gridgrouper.stubs.types.GridGrouperRuntimeFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GroupNotFoundFault {
        return gridGrouper.hasGroupPrivilege(getCallerIdentity(), group, subject, privilege);
    }


    public boolean isMember(java.lang.String member, gov.nih.nci.cagrid.gridgrouper.bean.MembershipExpression expression)
        throws RemoteException, gov.nih.nci.cagrid.gridgrouper.stubs.types.GridGrouperRuntimeFault {
        return gridGrouper.isMember(getCallerIdentity(), member, expression);
    }


    public gov.nih.nci.cagrid.gridgrouper.bean.MemberDescriptor getMember(java.lang.String member)
        throws RemoteException, gov.nih.nci.cagrid.gridgrouper.stubs.types.GridGrouperRuntimeFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.InsufficientPrivilegeFault {
        return gridGrouper.getMember(getCallerIdentity(), member);
    }


    public gov.nih.nci.cagrid.gridgrouper.bean.GroupDescriptor[] getMembersGroups(java.lang.String member,
        gov.nih.nci.cagrid.gridgrouper.bean.MembershipType type) throws RemoteException,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.GridGrouperRuntimeFault,
        gov.nih.nci.cagrid.gridgrouper.stubs.types.InsufficientPrivilegeFault {
        return gridGrouper.getMembersGroups(getCallerIdentity(), member, type);
    }

}
