package gov.nih.nci.cagrid.gridgrouper.client;

import edu.internet2.middleware.grouper.GrouperRuntimeException;
import edu.internet2.middleware.grouper.InsufficientPrivilegeException;
import edu.internet2.middleware.subject.Source;
import edu.internet2.middleware.subject.Subject;
import edu.internet2.middleware.subject.SubjectNotFoundException;
import edu.internet2.middleware.subject.SubjectType;
import gov.nih.nci.cagrid.gridgrouper.bean.MemberDescriptor;
import gov.nih.nci.cagrid.gridgrouper.common.SubjectUtils;
import gov.nih.nci.cagrid.gridgrouper.grouper.GroupI;
import gov.nih.nci.cagrid.gridgrouper.grouper.MemberI;

import java.util.Set;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class Member implements MemberI {

    private MemberDescriptor des;

    private Subject subject;

    private GridGrouper gridGrouper;


    public Member(GridGrouper gridGrouper, MemberDescriptor des) throws SubjectNotFoundException {
        this.gridGrouper = gridGrouper;
        this.des = des;
        subject = SubjectUtils.getSubject(des);

    }


    public String getSubjectId() {
        return subject.getId();
    }


    public Source getSubjectSource() throws GrouperRuntimeException {
        return subject.getSource();
    }


    public String getSubjectSourceId() {
        if (subject.getSource() == null) {
            return null;
        } else {
            return subject.getSource().getId();
        }
    }


    public SubjectType getSubjectType() {
        return subject.getType();
    }


    public String getSubjectTypeId() {
        return subject.getType().getName();
    }


    public String getUuid() {
        return des.getUUID();
    }


    public Subject getSubject() {
        return subject;
    }


    public Set<GroupI> getEffectiveGroups() throws GrouperRuntimeException, InsufficientPrivilegeException {
        return this.gridGrouper.getMembersEffectiveGroups(getSubjectId());
    }


    public Set<GroupI> getGroups() throws GrouperRuntimeException, InsufficientPrivilegeException {
        return this.gridGrouper.getMembersGroups(getSubjectId());
    }


    public Set<GroupI> getImmediateGroups() throws GrouperRuntimeException, InsufficientPrivilegeException {
        return this.gridGrouper.getMembersImmediateGroups(getSubjectId());
    }

}
