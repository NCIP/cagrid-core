package gov.nih.nci.cagrid.gridgrouper.client;

import edu.internet2.middleware.grouper.GrantPrivilegeException;
import edu.internet2.middleware.grouper.GroupAddException;
import edu.internet2.middleware.grouper.GrouperRuntimeException;
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
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.gridgrouper.bean.GroupDescriptor;
import gov.nih.nci.cagrid.gridgrouper.bean.StemDescriptor;
import gov.nih.nci.cagrid.gridgrouper.bean.StemIdentifier;
import gov.nih.nci.cagrid.gridgrouper.bean.StemPrivilege;
import gov.nih.nci.cagrid.gridgrouper.bean.StemPrivilegeType;
import gov.nih.nci.cagrid.gridgrouper.bean.StemUpdate;
import gov.nih.nci.cagrid.gridgrouper.common.SubjectUtils;
import gov.nih.nci.cagrid.gridgrouper.grouper.GroupI;
import gov.nih.nci.cagrid.gridgrouper.grouper.NamingPrivilegeI;
import gov.nih.nci.cagrid.gridgrouper.grouper.StemI;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.GrantPrivilegeFault;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.GridGrouperRuntimeFault;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.GroupAddFault;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.InsufficientPrivilegeFault;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.SchemaFault;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.StemAddFault;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.StemDeleteFault;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.StemModifyFault;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.StemNotFoundFault;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class Stem extends GridGrouperObject implements StemI {

    private StemDescriptor des;

    private GridGrouper gridGrouper;


    protected Stem(GridGrouper gridGrouper, StemDescriptor des) {
        this.gridGrouper = gridGrouper;
        this.des = des;
    }


    public String getCreateSource() {
        return des.getCreateSource();
    }


    public Subject getCreateSubject() throws SubjectNotFoundException {
        return SubjectUtils.getSubject(des.getCreateSubject(), true);
    }


    public Date getCreateTime() {
        return new Date(des.getCreateTime());
    }


    /**
     * Gets the description of the stem.
     * 
     * @return The description of the stem.
     */
    public String getDescription() {
        return des.getDescription();
    }


    /**
     * Gets the local display name of the stem.
     * 
     * @return The local display name of the stem.
     */
    public String getDisplayExtension() {
        return des.getDisplayExtension();
    }


    /**
     * Gets the full display name of the stem.
     * 
     * @return The full display name of the stem.
     */
    public String getDisplayName() {
        return des.getDisplayName();
    }


    /**
     * Gets the local name of the stem.
     * 
     * @return The local name of the stem.
     */
    public String getExtension() {
        return des.getExtension();
    }


    public String getModifySource() {
        return des.getModifySource();
    }


    public Subject getModifySubject() throws SubjectNotFoundException {
        return SubjectUtils.getSubject(des.getModifySubject(), true);
    }


    public Date getModifyTime() {
        if (des.getModifyTime() == 0) {
            return getCreateTime();
        } else {
            return new Date(des.getModifyTime());
        }
    }


    public StemIdentifier getStemIdentifier() {
        return gridGrouper.getStemIdentifier(getName());
    }


    /**
     * Gets the full name of the stem.
     * 
     * @return The full name of the stem.
     */
    public String getName() {
        return des.getName();
    }


    /**
     * Gets the UUID for the stem.
     * 
     * @return The UUID for the stem.
     */
    public String getUuid() {
        return des.getUUID();
    }


    public Set<StemI> getChildStems() {
        return gridGrouper.getChildStems(getName());
    }


    public StemI getParentStem() throws StemNotFoundException {
        return gridGrouper.getParentStem(getName());
    }


    public String toString() {
        return new ToStringBuilder(this).append("displayName", getDisplayName()).append("name", getName()).append(
            "uuid", getUuid()).append("created", getCreateTime()).append("modified", getModifyTime()).toString();
    }


    public void setDescription(String value) throws InsufficientPrivilegeException, StemModifyException {
        try {
            StemUpdate update = new StemUpdate();
            update.setDescription(value);
            this.des = gridGrouper.getClient().updateStem(getStemIdentifier(), update);
        } catch (InsufficientPrivilegeFault f) {
            throw new InsufficientPrivilegeException(f.getFaultString());
        } catch (StemModifyFault f) {
            throw new StemModifyException(f.getFaultString());
        } catch (GridGrouperRuntimeFault e) {
            getLog().error(e.getMessage(), e);
            throw new GrouperRuntimeException(e.getFaultString());
        } catch (Exception e) {
            getLog().error(e.getMessage(), e);
            throw new GrouperRuntimeException(Utils.getExceptionMessage(e));
        }

    }


    public Set<Subject> getCreators() {
        try {
            return getSubjectsWithPrivilege(Privilege.getInstance(StemPrivilegeType.create.getValue()));
        } catch (Exception e) {
            getLog().error(e.getMessage(), e);
            throw new GrouperRuntimeException(e.getMessage());
        }
    }


    public Set<NamingPrivilegeI> getPrivs(Subject subj) {
        try {
            return getPrivileges(subj);
        } catch (Exception e) {
            getLog().error(e.getMessage(), e);
            throw new GrouperRuntimeException(e.getMessage());
        }
    }


    public Set<Subject> getStemmers() {
        try {
            return getSubjectsWithPrivilege(Privilege.getInstance(StemPrivilegeType.stem.getValue()));
        } catch (Exception e) {
            getLog().error(e.getMessage(), e);
            throw new GrouperRuntimeException(e.getMessage());
        }
    }


    public void setDisplayExtension(String value) throws InsufficientPrivilegeException, StemModifyException {
        try {
            StemUpdate update = new StemUpdate();
            update.setDisplayExtension(value);
            this.des = gridGrouper.getClient().updateStem(getStemIdentifier(), update);
        } catch (InsufficientPrivilegeFault f) {
            throw new InsufficientPrivilegeException(f.getFaultString());
        } catch (StemModifyFault f) {
            throw new StemModifyException(f.getFaultString());
        } catch (GridGrouperRuntimeFault e) {
            getLog().error(e.getMessage(), e);
            throw new GrouperRuntimeException(e.getFaultString());
        } catch (Exception e) {
            getLog().error(e.getMessage(), e);
            throw new GrouperRuntimeException(Utils.getExceptionMessage(e));
        }

    }


    public boolean hasCreate(Subject subj) {
        try {
            return hasPrivilege(subj, NamingPrivilege.CREATE);
        } catch (Exception e) {
            getLog().error(e.getMessage(), e);
            throw new GrouperRuntimeException(e.getMessage());
        }
    }


    public boolean hasStem(Subject subj) {
        try {
            return hasPrivilege(subj, NamingPrivilege.STEM);
        } catch (Exception e) {
            getLog().error(e.getMessage(), e);
            throw new GrouperRuntimeException(e.getMessage());
        }
    }


    public void grantPriv(Subject subj, Privilege priv) throws GrantPrivilegeException, InsufficientPrivilegeException,
        SchemaException {
        try {
            StemPrivilegeType type = StemPrivilegeType.fromValue(priv.getName());
            gridGrouper.getClient().grantStemPrivilege(getStemIdentifier(), subj.getId(), type);
        } catch (InsufficientPrivilegeFault f) {
            throw new InsufficientPrivilegeException(f.getFaultString());
        } catch (GrantPrivilegeFault f) {
            throw new GrantPrivilegeException(f.getFaultString());
        } catch (SchemaFault f) {
            throw new SchemaException(f.getFaultString());
        } catch (GridGrouperRuntimeFault e) {
            getLog().error(e.getMessage(), e);
            throw new GrouperRuntimeException(e.getFaultString());
        } catch (Exception e) {
            getLog().error(e.getMessage(), e);
            throw new GrouperRuntimeException(Utils.getExceptionMessage(e));
        }

    }


    public void revokePriv(Subject subj, Privilege priv) throws InsufficientPrivilegeException,
        RevokePrivilegeException, SchemaException {
        try {
            StemPrivilegeType type = StemPrivilegeType.fromValue(priv.getName());
            gridGrouper.getClient().revokeStemPrivilege(getStemIdentifier(), subj.getId(), type);
        } catch (InsufficientPrivilegeFault f) {
            throw new InsufficientPrivilegeException(f.getFaultString());
        } catch (GrantPrivilegeFault f) {
            throw new RevokePrivilegeException(f.getFaultString());
        } catch (SchemaFault f) {
            throw new SchemaException(f.getFaultString());
        } catch (GridGrouperRuntimeFault e) {
            getLog().error(e.getMessage(), e);
            throw new GrouperRuntimeException(e.getFaultString());
        } catch (Exception e) {
            getLog().error(e.getMessage(), e);
            throw new GrouperRuntimeException(Utils.getExceptionMessage(e));
        }
    }


    public StemI addChildStem(String extension, String displayExtension) throws InsufficientPrivilegeException,
        StemAddException {
        try {
            StemDescriptor stem = gridGrouper.getClient().addChildStem(this.getStemIdentifier(), extension,
                displayExtension);
            return new Stem(this.gridGrouper, stem);
        } catch (InsufficientPrivilegeFault f) {
            throw new InsufficientPrivilegeException(f.getFaultString());
        } catch (StemAddFault f) {
            throw new StemAddException(f.getFaultString());
        } catch (GridGrouperRuntimeFault e) {
            getLog().error(e.getMessage(), e);
            throw new GrouperRuntimeException(e.getFaultString());
        } catch (Exception e) {
            getLog().error(e.getMessage(), e);
            throw new GrouperRuntimeException(Utils.getExceptionMessage(e));
        }
    }


    public void delete() throws InsufficientPrivilegeException, StemDeleteException {
        try {
            gridGrouper.getClient().deleteStem(this.getStemIdentifier());
        } catch (InsufficientPrivilegeFault f) {
            throw new InsufficientPrivilegeException(f.getFaultString());
        } catch (StemDeleteFault f) {
            throw new StemDeleteException(f.getFaultString());
        } catch (GridGrouperRuntimeFault e) {
            getLog().error(e.getMessage(), e);
            throw new GrouperRuntimeException(e.getFaultString());
        } catch (Exception e) {
            getLog().error(e.getMessage(), e);
            throw new GrouperRuntimeException(Utils.getExceptionMessage(e));
        }
    }


    public GroupI addChildGroup(String extension, String displayExtension) throws GroupAddException,
        InsufficientPrivilegeException {
        try {
            GroupDescriptor grp = gridGrouper.getClient().addChildGroup(this.getStemIdentifier(), extension,
                displayExtension);
            return new Group(this.gridGrouper, grp);
        } catch (InsufficientPrivilegeFault f) {
            throw new InsufficientPrivilegeException(f.getFaultString());
        } catch (GroupAddFault f) {
            throw new GroupAddException(f.getFaultString());
        } catch (GridGrouperRuntimeFault e) {
            getLog().error(e.getMessage(), e);
            throw new GrouperRuntimeException(e.getFaultString());
        } catch (Exception e) {
            getLog().error(e.getMessage(), e);
            throw new GrouperRuntimeException(Utils.getExceptionMessage(e));
        }
    }


    public Set<GroupI> getChildGroups() {
        try {
            GroupDescriptor[] children = gridGrouper.getClient().getChildGroups(getStemIdentifier());
            Set<GroupI> set = new HashSet<GroupI>();
            if (children != null) {
                for (int i = 0; i < children.length; i++) {
                    set.add(new Group(gridGrouper, children[i]));
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


    public GridGrouper getGridGrouper() {
        return gridGrouper;
    }


    private Set<NamingPrivilegeI> getPrivileges(Subject subject) throws StemNotFoundException {
        try {
            StemPrivilege[] privs = gridGrouper.getClient().getStemPrivileges(getStemIdentifier(), subject.getId());
            Set<NamingPrivilegeI> set = new HashSet<NamingPrivilegeI>();
            if (privs != null) {
                for (int i = 0; i < privs.length; i++) {
                    NamingPrivilegeI priv = new NamingPrivilege(privs[i].getStemName(), SubjectUtils
                        .getSubject(privs[i].getSubject()), SubjectUtils.getSubject(privs[i].getOwner()), Privilege
                        .getInstance(privs[i].getPrivilegeType().getValue()), privs[i].getImplementationClass(),
                        privs[i].isIsRevokable());
                    set.add(priv);
                }
            }
            return set;
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


    private Set<Subject> getSubjectsWithPrivilege(Privilege privilege) throws StemNotFoundException {
        try {
            String[] subs = gridGrouper.getClient().getSubjectsWithStemPrivilege(getStemIdentifier(),
                StemPrivilegeType.fromValue(privilege.getName()));
            Set<Subject> set = new HashSet<Subject>();
            if (subs != null) {
                for (int i = 0; i < subs.length; i++) {
                    set.add(SubjectUtils.getSubject(subs[i], true));
                }
            }
            return set;
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


    private boolean hasPrivilege(Subject subject, Privilege privilege) throws StemNotFoundException {
        try {
            StemPrivilegeType type = StemPrivilegeType.fromValue(privilege.getName());
            return gridGrouper.getClient().hasStemPrivilege(getStemIdentifier(), subject.getId(), type);
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

}
