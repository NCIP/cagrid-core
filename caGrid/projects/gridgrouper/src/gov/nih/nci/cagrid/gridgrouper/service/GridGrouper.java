package gov.nih.nci.cagrid.gridgrouper.service;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.internet2.middleware.grouper.AccessPrivilege;
import edu.internet2.middleware.grouper.CompositeType;
import edu.internet2.middleware.grouper.GrantPrivilegeException;
import edu.internet2.middleware.grouper.Group;
import edu.internet2.middleware.grouper.GroupDeleteException;
import edu.internet2.middleware.grouper.GroupFinder;
import edu.internet2.middleware.grouper.GroupModifyException;
import edu.internet2.middleware.grouper.GroupNotFoundException;
import edu.internet2.middleware.grouper.GrouperSession;
import edu.internet2.middleware.grouper.GrouperSourceAdapter;
import edu.internet2.middleware.grouper.InsufficientPrivilegeException;
import edu.internet2.middleware.grouper.Member;
import edu.internet2.middleware.grouper.MemberAddException;
import edu.internet2.middleware.grouper.MemberDeleteException;
import edu.internet2.middleware.grouper.MemberFinder;
import edu.internet2.middleware.grouper.Membership;
import edu.internet2.middleware.grouper.MembershipRequests;
import edu.internet2.middleware.grouper.MembershipRequestsFinder;
import edu.internet2.middleware.grouper.NamingPrivilege;
import edu.internet2.middleware.grouper.Privilege;
import edu.internet2.middleware.grouper.RevokePrivilegeException;
import edu.internet2.middleware.grouper.SchemaException;
import edu.internet2.middleware.grouper.Stem;
import edu.internet2.middleware.grouper.StemAddException;
import edu.internet2.middleware.grouper.StemDeleteException;
import edu.internet2.middleware.grouper.StemFinder;
import edu.internet2.middleware.grouper.StemModifyException;
import edu.internet2.middleware.grouper.StemNotFoundException;
import edu.internet2.middleware.grouper.SubjectFinder;
import edu.internet2.middleware.subject.Subject;
import edu.internet2.middleware.subject.SubjectNotFoundException;
import edu.internet2.middleware.subject.provider.SubjectTypeEnum;
import gov.nih.nci.cagrid.common.FaultHelper;
import gov.nih.nci.cagrid.gridgrouper.bean.GroupCompositeType;
import gov.nih.nci.cagrid.gridgrouper.bean.GroupDescriptor;
import gov.nih.nci.cagrid.gridgrouper.bean.GroupIdentifier;
import gov.nih.nci.cagrid.gridgrouper.bean.GroupPrivilege;
import gov.nih.nci.cagrid.gridgrouper.bean.GroupPrivilegeType;
import gov.nih.nci.cagrid.gridgrouper.bean.GroupUpdate;
import gov.nih.nci.cagrid.gridgrouper.bean.LogicalOperator;
import gov.nih.nci.cagrid.gridgrouper.bean.MemberDescriptor;
import gov.nih.nci.cagrid.gridgrouper.bean.MemberFilter;
import gov.nih.nci.cagrid.gridgrouper.bean.MemberType;
import gov.nih.nci.cagrid.gridgrouper.bean.MembershipDescriptor;
import gov.nih.nci.cagrid.gridgrouper.bean.MembershipExpression;
import gov.nih.nci.cagrid.gridgrouper.bean.MembershipQuery;
import gov.nih.nci.cagrid.gridgrouper.bean.MembershipRequestDescriptor;
import gov.nih.nci.cagrid.gridgrouper.bean.MembershipRequestStatus;
import gov.nih.nci.cagrid.gridgrouper.bean.MembershipRequestUpdate;
import gov.nih.nci.cagrid.gridgrouper.bean.MembershipStatus;
import gov.nih.nci.cagrid.gridgrouper.bean.MembershipType;
import gov.nih.nci.cagrid.gridgrouper.bean.StemDescriptor;
import gov.nih.nci.cagrid.gridgrouper.bean.StemIdentifier;
import gov.nih.nci.cagrid.gridgrouper.bean.StemPrivilege;
import gov.nih.nci.cagrid.gridgrouper.bean.StemPrivilegeType;
import gov.nih.nci.cagrid.gridgrouper.bean.StemUpdate;
import gov.nih.nci.cagrid.gridgrouper.common.SubjectUtils;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.GrantPrivilegeFault;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.GridGrouperRuntimeFault;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.GroupAddFault;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.GroupDeleteFault;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.GroupModifyFault;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.GroupNotFoundFault;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.InsufficientPrivilegeFault;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.MemberAddFault;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.MemberDeleteFault;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.RevokePrivilegeFault;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.SchemaFault;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.StemAddFault;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.StemDeleteFault;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.StemModifyFault;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.StemNotFoundFault;
import gov.nih.nci.cagrid.gridgrouper.subject.GridSourceAdapter;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 */
public class GridGrouper {

	public static final String GROUPER_SUPER_USER = "GrouperSystem";

	public static final String GROUPER_ADMIN_STEM_NAME = "grouperadministration";

	public static final String GROUPER_ADMIN_STEM_DISPLAY_NAME = "Grouper Administration";

	public static final String GROUPER_ADMIN_GROUP_NAME_EXTENTION = "gridgrouperadministrators";

	public static final String GROUPER_ADMIN_GROUP_DISPLAY_NAME_EXTENTION = "Grid Grouper Administrators";

	public static final String GROUPER_ADMIN_GROUP_NAME = "grouperadministration:gridgrouperadministrators";

	public static final String UNKNOWN_SUBJECT = "Unknown";

	private Group adminGroup;

	private Log log;


	public GridGrouper() throws GridGrouperRuntimeFault {
		try {
			this.log = LogFactory.getLog(this.getClass().getName());
			GrouperSession session = GrouperSession.start(SubjectFinder.findById(GROUPER_SUPER_USER));
			Stem adminStem = null;
			try {
				adminStem = StemFinder.findByName(session, GROUPER_ADMIN_STEM_NAME);
			} catch (StemNotFoundException e) {
				Stem root = StemFinder.findRootStem(session);
				adminStem = root.addChildStem(GROUPER_ADMIN_STEM_NAME, GROUPER_ADMIN_STEM_DISPLAY_NAME);
			}
			try {
				this.adminGroup = GroupFinder.findByName(session, GROUPER_ADMIN_GROUP_NAME);
			} catch (GroupNotFoundException gne) {
				this.adminGroup = adminStem.addChildGroup(GROUPER_ADMIN_GROUP_NAME_EXTENTION,
					GROUPER_ADMIN_GROUP_DISPLAY_NAME_EXTENTION);
			}
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("Error occurred initializing Grid Grouper: " + e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GridGrouperRuntimeFault) helper.getFault();
			throw fault;
		}
	}


	public StemDescriptor getStem(String gridIdentity, StemIdentifier stemId) throws GridGrouperRuntimeFault,
		StemNotFoundFault {
		GrouperSession session = null;
		try {
			Subject subject = SubjectFinder.findById(gridIdentity);
			session = GrouperSession.start(subject);
			StemDescriptor des = null;
			Stem stem = StemFinder.findByName(session, stemId.getStemName());
			des = stemtoStemDescriptor(stem);

			return des;
		} catch (StemNotFoundException e) {
			StemNotFoundFault fault = new StemNotFoundFault();
			fault.setFaultString("The stem, " + stemId.getStemName() + "was not found.");
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (StemNotFoundFault) helper.getFault();
			throw fault;
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("Error occurred getting the stem " + stemId.getStemName() + ": " + e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GridGrouperRuntimeFault) helper.getFault();
			throw fault;
		} finally {
			if (session != null) {
				try {
					session.stop();
				} catch (Exception e) {
					this.log.error(e.getMessage(), e);
				}
			}
		}
	}


	public StemDescriptor[] getChildStems(String gridIdentity, StemIdentifier parentStemId) throws RemoteException,
		GridGrouperRuntimeFault, StemNotFoundFault {
		GrouperSession session = null;
		try {
			Subject subject = SubjectFinder.findById(gridIdentity);
			session = GrouperSession.start(subject);
			StemDescriptor[] children = null;
			Stem parent = StemFinder.findByName(session, parentStemId.getStemName());
			Set set = parent.getChildStems();
			children = new StemDescriptor[set.size()];
			Iterator itr = set.iterator();
			int count = 0;
			while (itr.hasNext()) {
				children[count] = stemtoStemDescriptor((Stem) itr.next());
				count++;
			}

			return children;
		} catch (StemNotFoundException e) {
			StemNotFoundFault fault = new StemNotFoundFault();
			fault.setFaultString("The parent stem, " + parentStemId.getStemName() + "was not found.");
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (StemNotFoundFault) helper.getFault();
			throw fault;
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("Error occurred getting the child stems for the parent stem, "
				+ parentStemId.getStemName() + ": " + e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GridGrouperRuntimeFault) helper.getFault();
			throw fault;
		} finally {
			if (session != null) {
				try {
					session.stop();
				} catch (Exception e) {
					this.log.error(e.getMessage(), e);
				}
			}
		}
	}


	public StemDescriptor getParentStem(String gridIdentity, StemIdentifier childStemId) throws RemoteException,
		GridGrouperRuntimeFault, StemNotFoundFault {
		GrouperSession session = null;
		try {
			Subject subject = SubjectFinder.findById(gridIdentity);
			session = GrouperSession.start(subject);
			StemDescriptor parent = null;
			Stem child = StemFinder.findByName(session, childStemId.getStemName());
			Stem s = child.getParentStem();
			parent = stemtoStemDescriptor(s);

			return parent;
		} catch (StemNotFoundException e) {
			StemNotFoundFault fault = new StemNotFoundFault();
			fault.setFaultString("The parent stem for the child " + childStemId.getStemName()
				+ " could not be found!!!");
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (StemNotFoundFault) helper.getFault();
			throw fault;
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("Error occurred getting the parent stem for the child stem, "
				+ childStemId.getStemName() + ": " + e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GridGrouperRuntimeFault) helper.getFault();
			throw fault;
		} finally {
			if (session != null) {
				try {
					session.stop();
				} catch (Exception e) {
					this.log.error(e.getMessage(), e);
				}
			}
		}
	}


	public StemDescriptor updateStem(String gridIdentity, StemIdentifier stem, StemUpdate update)
		throws GridGrouperRuntimeFault, InsufficientPrivilegeFault, StemModifyFault {
		GrouperSession session = null;
		try {
			Subject subject = SubjectFinder.findById(gridIdentity);
			session = GrouperSession.start(subject);
			StemDescriptor des = null;
			Stem target = StemFinder.findByName(session, stem.getStemName());
			if ((update.getDescription() != null) && (!update.getDescription().equals(target.getDescription()))) {
				target.setDescription(update.getDescription());
			}

			if ((update.getDisplayExtension() != null)
				&& (!update.getDisplayExtension().equals(target.getDisplayExtension()))) {
				target.setDisplayExtension(update.getDisplayExtension());
			}

			des = stemtoStemDescriptor(target);
			return des;
		} catch (InsufficientPrivilegeException e) {
			InsufficientPrivilegeFault fault = new InsufficientPrivilegeFault();
			fault.setFaultString(e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (InsufficientPrivilegeFault) helper.getFault();
			throw fault;
		} catch (StemModifyException e) {
			StemModifyFault fault = new StemModifyFault();
			fault.setFaultString(e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (StemModifyFault) helper.getFault();
			throw fault;
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("Error occurred getting the stem " + stem.getStemName() + ": " + e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GridGrouperRuntimeFault) helper.getFault();
			throw fault;
		} finally {
			if (session != null) {
				try {
					session.stop();
				} catch (Exception e) {
					this.log.error(e.getMessage(), e);
				}
			}
		}

	}


	public String[] getSubjectsWithStemPrivilege(String gridIdentity, StemIdentifier stem, StemPrivilegeType privilege)
		throws RemoteException, GridGrouperRuntimeFault, StemNotFoundFault {
		GrouperSession session = null;
		try {
			Subject subject = SubjectFinder.findById(gridIdentity);
			session = GrouperSession.start(subject);
			Stem target = StemFinder.findByName(session, stem.getStemName());
			Set subs = null;
			if (privilege.equals(StemPrivilegeType.create)) {
				subs = target.getCreators();
			} else if (privilege.equals(StemPrivilegeType.stem)) {
				subs = target.getStemmers();
			} else {
				throw new Exception(privilege.getValue() + " is not a valid stem privilege!!!");
			}
			int size = 0;
			if (subs != null) {
				size = subs.size();
			}
			String[] subjects = new String[size];
			if (subs != null) {
				Iterator itr = subs.iterator();
				int count = 0;
				while (itr.hasNext()) {
					Subject s = (Subject) itr.next();
					subjects[count] = s.getId();
					count++;
				}
			}
			return subjects;
		} catch (StemNotFoundException e) {
			StemNotFoundFault fault = new StemNotFoundFault();
			fault.setFaultString("The stem " + stem.getStemName() + " could not be found!!!");
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (StemNotFoundFault) helper.getFault();
			throw fault;
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("Error occurred getting the subjects with the privilege " + privilege.getValue()
				+ " on the stem " + stem.getStemName() + ": " + e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GridGrouperRuntimeFault) helper.getFault();
			throw fault;
		} finally {
			if (session != null) {
				try {
					session.stop();
				} catch (Exception e) {
					this.log.error(e.getMessage(), e);
				}
			}
		}
	}


	public StemPrivilege[] getStemPrivileges(String gridIdentity, StemIdentifier stem, String subject)
		throws RemoteException, GridGrouperRuntimeFault, StemNotFoundFault {
		GrouperSession session = null;
		try {

			Subject subj = SubjectFinder.findById(gridIdentity);
			session = GrouperSession.start(subj);
			Stem target = StemFinder.findByName(session, stem.getStemName());
			Set privs = target.getPrivs(SubjectFinder.findById(subject));
			int size = 0;
			if (privs != null) {
				size = privs.size();
			}
			StemPrivilege[] rights = new StemPrivilege[size];
			if (privs != null) {
				Iterator itr = privs.iterator();
				int count = 0;
				while (itr.hasNext()) {
					NamingPrivilege p = (NamingPrivilege) itr.next();
					rights[count] = new StemPrivilege();
					rights[count].setStemName(p.getStem().getName());
					rights[count].setImplementationClass(p.getImplementationName());
					rights[count].setIsRevokable(p.isRevokable());
					rights[count].setOwner(p.getOwner().getId());
					rights[count].setPrivilegeType(StemPrivilegeType.fromValue(p.getName()));
					rights[count].setSubject(p.getSubject().getId());
					count++;
				}
			}
			return rights;
		} catch (StemNotFoundException e) {
			StemNotFoundFault fault = new StemNotFoundFault();
			fault.setFaultString("The stem " + stem.getStemName() + " could not be found!!!");
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (StemNotFoundFault) helper.getFault();
			throw fault;
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("Error occurred getting the privileges for the subject " + subject + " on the stem "
				+ stem.getStemName() + ": " + e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GridGrouperRuntimeFault) helper.getFault();
			throw fault;
		} finally {
			if (session != null) {
				try {
					session.stop();
				} catch (Exception e) {
					this.log.error(e.getMessage(), e);
				}
			}
		}
	}


	public boolean hasStemPrivilege(String gridIdentity, StemIdentifier stem, String subject,
		StemPrivilegeType privilege) throws GridGrouperRuntimeFault, StemNotFoundFault {
		GrouperSession session = null;
		try {
			Subject subj = SubjectFinder.findById(gridIdentity);
			session = GrouperSession.start(subj);
			Stem target = StemFinder.findByName(session, stem.getStemName());
			if (privilege == null) {
				return false;
			} else if (privilege.equals(StemPrivilegeType.create)) {
				return target.hasCreate(SubjectFinder.findById(subject));
			} else if (privilege.equals(StemPrivilegeType.stem)) {
				return target.hasStem(SubjectFinder.findById(subject));
			} else {
				return false;
			}

		} catch (StemNotFoundException e) {
			StemNotFoundFault fault = new StemNotFoundFault();
			fault.setFaultString("The stem " + stem.getStemName() + " could not be found!!!");
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (StemNotFoundFault) helper.getFault();
			throw fault;
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("Error determing if the subject " + subject + " has the privilege "
				+ privilege.getValue() + " on the stem " + stem.getStemName() + ": " + e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GridGrouperRuntimeFault) helper.getFault();
			throw fault;
		} finally {
			if (session != null) {
				try {
					session.stop();
				} catch (Exception e) {
					this.log.error(e.getMessage(), e);
				}
			}
		}
	}


	public void grantStemPrivilege(String gridIdentity, StemIdentifier stem, String subject, StemPrivilegeType privilege)
		throws GridGrouperRuntimeFault, StemNotFoundFault, GrantPrivilegeFault, InsufficientPrivilegeFault, SchemaFault {
		GrouperSession session = null;
		try {
			Subject subj = SubjectFinder.findById(gridIdentity);
			session = GrouperSession.start(subj);
			Stem target = StemFinder.findByName(session, stem.getStemName());
			target.grantPriv(SubjectFinder.findById(subject), Privilege.getInstance(privilege.getValue()));
		} catch (GrantPrivilegeException e) {
			GrantPrivilegeFault fault = new GrantPrivilegeFault();
			fault.setFaultString(e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GrantPrivilegeFault) helper.getFault();
			throw fault;
		} catch (InsufficientPrivilegeException e) {
			InsufficientPrivilegeFault fault = new InsufficientPrivilegeFault();
			fault.setFaultString("You do not have the right to manages privileges on the stem " + stem.getStemName()
				+ ": " + e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (InsufficientPrivilegeFault) helper.getFault();
			throw fault;
		} catch (SchemaException e) {
			SchemaFault fault = new SchemaFault();
			fault.setFaultString(e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (SchemaFault) helper.getFault();
			throw fault;
		} catch (StemNotFoundException e) {
			StemNotFoundFault fault = new StemNotFoundFault();
			fault.setFaultString("The stem " + stem.getStemName() + " could not be found!!!");
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (StemNotFoundFault) helper.getFault();
			throw fault;
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("Error occurred granting a privilege for the subject " + subject + " on the stem "
				+ stem.getStemName() + ": " + e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GridGrouperRuntimeFault) helper.getFault();
			throw fault;
		} finally {
			if (session != null) {
				try {
					session.stop();
				} catch (Exception e) {
					this.log.error(e.getMessage(), e);
				}
			}
		}
	}


	public void revokeStemPrivilege(String gridIdentity, StemIdentifier stem, String subject,
		StemPrivilegeType privilege) throws GridGrouperRuntimeFault, StemNotFoundFault, InsufficientPrivilegeFault,
		RevokePrivilegeFault, SchemaFault {
		GrouperSession session = null;
		try {
			Subject subj = SubjectFinder.findById(gridIdentity);
			session = GrouperSession.start(subj);
			Stem target = StemFinder.findByName(session, stem.getStemName());
			target.revokePriv(SubjectFinder.findById(subject), Privilege.getInstance(privilege.getValue()));
		} catch (RevokePrivilegeException e) {
			RevokePrivilegeFault fault = new RevokePrivilegeFault();
			fault.setFaultString(e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (RevokePrivilegeFault) helper.getFault();
			throw fault;
		} catch (InsufficientPrivilegeException e) {
			InsufficientPrivilegeFault fault = new InsufficientPrivilegeFault();
			fault.setFaultString("You do not have the right to manages privileges on the stem " + stem.getStemName()
				+ ": " + e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (InsufficientPrivilegeFault) helper.getFault();
			throw fault;
		} catch (SchemaException e) {
			SchemaFault fault = new SchemaFault();
			fault.setFaultString(e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (SchemaFault) helper.getFault();
			throw fault;
		} catch (StemNotFoundException e) {
			StemNotFoundFault fault = new StemNotFoundFault();
			fault.setFaultString("The stem " + stem.getStemName() + " could not be found!!!");
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (StemNotFoundFault) helper.getFault();
			throw fault;
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("Error occurred getting the privileges for the subject " + subject + " on the stem "
				+ stem.getStemName() + ": " + e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GridGrouperRuntimeFault) helper.getFault();
			throw fault;
		} finally {
			if (session != null) {
				try {
					session.stop();
				} catch (Exception e) {
					this.log.error(e.getMessage(), e);
				}
			}
		}
	}


	public StemDescriptor addChildStem(String gridIdentity, StemIdentifier stem, String extension,
		String displayExtension) throws GridGrouperRuntimeFault, InsufficientPrivilegeFault, StemAddFault,
		StemNotFoundFault {
		GrouperSession session = null;
		try {
			Subject subj = SubjectFinder.findById(gridIdentity);
			session = GrouperSession.start(subj);
			Stem target = StemFinder.findByName(session, stem.getStemName());
			Stem child = target.addChildStem(extension, displayExtension);
			return stemtoStemDescriptor(child);
		} catch (StemAddException e) {
			StemAddFault fault = new StemAddFault();
			fault.setFaultString(e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (StemAddFault) helper.getFault();
			throw fault;
		} catch (InsufficientPrivilegeException e) {
			InsufficientPrivilegeFault fault = new InsufficientPrivilegeFault();
			fault.setFaultString("You do not have the right to add children to the stem " + stem.getStemName() + ": "
				+ e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (InsufficientPrivilegeFault) helper.getFault();
			throw fault;
		} catch (StemNotFoundException e) {
			StemNotFoundFault fault = new StemNotFoundFault();
			fault.setFaultString("The stem " + stem.getStemName() + " could not be found!!!");
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (StemNotFoundFault) helper.getFault();
			throw fault;
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("Error occurred adding the child " + extension + " to the stem " + stem.getStemName()
				+ ": " + e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GridGrouperRuntimeFault) helper.getFault();
			throw fault;
		} finally {
			if (session != null) {
				try {
					session.stop();
				} catch (Exception e) {
					this.log.error(e.getMessage(), e);
				}
			}
		}
	}


	public void deleteStem(String gridIdentity, StemIdentifier stem) throws GridGrouperRuntimeFault,
		InsufficientPrivilegeFault, StemDeleteFault, StemNotFoundFault {
		GrouperSession session = null;
		try {
			Subject subj = SubjectFinder.findById(gridIdentity);
			session = GrouperSession.start(subj);
			Stem target = StemFinder.findByName(session, stem.getStemName());
			target.delete();
		} catch (StemDeleteException e) {
			StemDeleteFault fault = new StemDeleteFault();
			fault.setFaultString(e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (StemDeleteFault) helper.getFault();
			throw fault;
		} catch (InsufficientPrivilegeException e) {
			InsufficientPrivilegeFault fault = new InsufficientPrivilegeFault();
			fault.setFaultString("You do not have the right to add children to the stem " + stem.getStemName() + ": "
				+ e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (InsufficientPrivilegeFault) helper.getFault();
			throw fault;
		} catch (StemNotFoundException e) {
			StemNotFoundFault fault = new StemNotFoundFault();
			fault.setFaultString("The stem " + stem.getStemName() + " could not be found!!!");
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (StemNotFoundFault) helper.getFault();
			throw fault;
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault
				.setFaultString("An error occurred in deleting the stem " + stem.getStemName() + ": " + e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GridGrouperRuntimeFault) helper.getFault();
			throw fault;
		} finally {
			if (session != null) {
				try {
					session.stop();
				} catch (Exception e) {
					this.log.error(e.getMessage(), e);
				}
			}
		}

	}


	public GroupDescriptor[] getChildGroups(String gridIdentity, StemIdentifier stem) throws GridGrouperRuntimeFault,
		StemNotFoundFault {
		GrouperSession session = null;
		try {
			Subject subj = SubjectFinder.findById(gridIdentity);
			session = GrouperSession.start(subj);
			Stem target = StemFinder.findByName(session, stem.getStemName());

			GroupDescriptor[] children = null;
			Set set = target.getChildGroups();
			children = new GroupDescriptor[set.size()];
			Iterator itr = set.iterator();
			int count = 0;
			while (itr.hasNext()) {
				children[count] = grouptoGroupDescriptor((Group) itr.next());
				count++;
			}

			return children;
		} catch (StemNotFoundException e) {
			StemNotFoundFault fault = new StemNotFoundFault();
			fault.setFaultString("The stem " + stem.getStemName() + " could not be found!!!");
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (StemNotFoundFault) helper.getFault();
			throw fault;
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("An error occurred in getting the child groups for the stem " + stem.getStemName()
				+ ": " + e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GridGrouperRuntimeFault) helper.getFault();
			throw fault;
		} finally {
			if (session != null) {
				try {
					session.stop();
				} catch (Exception e) {
					this.log.error(e.getMessage(), e);
				}
			}
		}
	}


	public gov.nih.nci.cagrid.gridgrouper.bean.GroupDescriptor addChildGroup(String gridIdentity, StemIdentifier stem,
		String extension, String displayExtension) throws RemoteException, GridGrouperRuntimeFault, GroupAddFault,
		InsufficientPrivilegeFault {
		GrouperSession session = null;
		try {
			Subject subj = SubjectFinder.findById(gridIdentity);
			session = GrouperSession.start(subj);
			Stem target = StemFinder.findByName(session, stem.getStemName());
			Group child = target.addChildGroup(extension, displayExtension);
			return grouptoGroupDescriptor(child);
		} catch (GroupAddFault e) {
			GroupAddFault fault = new GroupAddFault();
			fault.setFaultString(e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GroupAddFault) helper.getFault();
			throw fault;
		} catch (InsufficientPrivilegeException e) {
			InsufficientPrivilegeFault fault = new InsufficientPrivilegeFault();
			fault.setFaultString("You do not have the right to add groups to the stem " + stem.getStemName() + ": "
				+ e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (InsufficientPrivilegeFault) helper.getFault();
			throw fault;
		} catch (StemNotFoundException e) {
			StemNotFoundFault fault = new StemNotFoundFault();
			fault.setFaultString("The stem " + stem.getStemName() + " could not be found!!!");
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (StemNotFoundFault) helper.getFault();
			throw fault;
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("Error occurred adding the group " + extension + " to the stem " + stem.getStemName()
				+ ": " + e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GridGrouperRuntimeFault) helper.getFault();
			throw fault;
		} finally {
			if (session != null) {
				try {
					session.stop();
				} catch (Exception e) {
					this.log.error(e.getMessage(), e);
				}
			}
		}
	}


	public GroupDescriptor getGroup(String gridIdentity, GroupIdentifier group) throws GridGrouperRuntimeFault,
		GroupNotFoundFault {
		GrouperSession session = null;
		try {
			Subject subject = SubjectFinder.findById(gridIdentity);
			session = GrouperSession.start(subject);
			Group grp = GroupFinder.findByName(session, group.getGroupName());
			return grouptoGroupDescriptor(grp);
		} catch (GroupNotFoundException e) {
			GroupNotFoundFault fault = new GroupNotFoundFault();
			fault.setFaultString("The group, " + group.getGroupName() + "was not found.");
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GroupNotFoundFault) helper.getFault();
			throw fault;
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("Error occurred getting the group " + group.getGroupName() + ": " + e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GridGrouperRuntimeFault) helper.getFault();
			throw fault;
		} finally {
			if (session != null) {
				try {
					session.stop();
				} catch (Exception e) {
					this.log.error(e.getMessage(), e);
				}
			}
		}
	}


	public void deleteGroup(String gridIdentity, GroupIdentifier group) throws GridGrouperRuntimeFault,
		GroupNotFoundFault, GroupDeleteFault, InsufficientPrivilegeFault {
		GrouperSession session = null;
		try {
			Subject subject = SubjectFinder.findById(gridIdentity);
			session = GrouperSession.start(subject);
			Group grp = GroupFinder.findByName(session, group.getGroupName());
			grp.delete();
		} catch (GroupNotFoundException e) {
			GroupNotFoundFault fault = new GroupNotFoundFault();
			fault.setFaultString("The group, " + group.getGroupName() + "was not found.");
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GroupNotFoundFault) helper.getFault();
			throw fault;
		} catch (GroupDeleteException e) {
			GroupDeleteFault fault = new GroupDeleteFault();
			fault.setFaultString(e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GroupDeleteFault) helper.getFault();
			throw fault;
		} catch (InsufficientPrivilegeException e) {
			InsufficientPrivilegeFault fault = new InsufficientPrivilegeFault();
			fault.setFaultString(e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (InsufficientPrivilegeFault) helper.getFault();
			throw fault;
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("Error occurred deleting the group " + group.getGroupName() + ": " + e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GridGrouperRuntimeFault) helper.getFault();
			throw fault;
		} finally {
			if (session != null) {
				try {
					session.stop();
				} catch (Exception e) {
					this.log.error(e.getMessage(), e);
				}
			}
		}

	}


	public GroupDescriptor updateGroup(String gridIdentity, GroupIdentifier group, GroupUpdate update)
		throws GridGrouperRuntimeFault, GroupNotFoundFault, GroupModifyFault, InsufficientPrivilegeFault {
		GrouperSession session = null;
		try {
			Subject subject = SubjectFinder.findById(gridIdentity);
			session = GrouperSession.start(subject);
			Group grp = GroupFinder.findByName(session, group.getGroupName());
			if ((update.getDescription() != null) && (!update.getDescription().equals(grp.getDescription()))) {
				grp.setDescription(update.getDescription());
			}

			if ((update.getExtension() != null) && (!update.getExtension().equals(grp.getExtension()))) {
				grp.setExtension(update.getExtension());
			}

			if ((update.getDisplayExtension() != null)
				&& (!update.getDisplayExtension().equals(grp.getDisplayExtension()))) {
				grp.setDisplayExtension(update.getDisplayExtension());
			}
			return grouptoGroupDescriptor(grp);
		} catch (GroupNotFoundException e) {
			GroupNotFoundFault fault = new GroupNotFoundFault();
			fault.setFaultString("The group, " + group.getGroupName() + "was not found.");
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GroupNotFoundFault) helper.getFault();
			throw fault;
		} catch (InsufficientPrivilegeException e) {
			InsufficientPrivilegeFault fault = new InsufficientPrivilegeFault();
			fault.setFaultString(e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (InsufficientPrivilegeFault) helper.getFault();
			throw fault;
		} catch (GroupModifyException e) {
			GroupModifyFault fault = new GroupModifyFault();
			fault.setFaultString(e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GroupModifyFault) helper.getFault();
			throw fault;
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("Error occurred updating the group " + group.getGroupName() + ": " + e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GridGrouperRuntimeFault) helper.getFault();
			throw fault;
		} finally {
			if (session != null) {
				try {
					session.stop();
				} catch (Exception e) {
					this.log.error(e.getMessage(), e);
				}
			}
		}
	}


	public void addMember(String gridIdentity, GroupIdentifier group, String subject) throws GridGrouperRuntimeFault,
		GroupNotFoundFault, InsufficientPrivilegeFault, MemberAddFault {
		GrouperSession session = null;
		try {
			Subject caller = SubjectFinder.findById(gridIdentity);
			session = GrouperSession.start(caller);
			Group grp = GroupFinder.findByName(session, group.getGroupName());
			grp.addMember(SubjectFinder.findById(subject));
		} catch (GroupNotFoundException e) {
			GroupNotFoundFault fault = new GroupNotFoundFault();
			fault.setFaultString("The group, " + group.getGroupName() + "was not found.");
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GroupNotFoundFault) helper.getFault();
			throw fault;
		} catch (MemberAddException e) {
			MemberAddFault fault = new MemberAddFault();
			fault.setFaultString(e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (MemberAddFault) helper.getFault();
			throw fault;
		} catch (InsufficientPrivilegeException e) {
			InsufficientPrivilegeFault fault = new InsufficientPrivilegeFault();
			fault.setFaultString(e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (InsufficientPrivilegeFault) helper.getFault();
			throw fault;
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("Error occurred adding a member to the group " + group.getGroupName() + ": "
				+ e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GridGrouperRuntimeFault) helper.getFault();
			throw fault;
		} finally {
			if (session != null) {
				try {
					session.stop();
				} catch (Exception e) {
					this.log.error(e.getMessage(), e);
				}
			}
		}
	}
	
	
	public MemberDescriptor[] getMembers(String gridIdentity, GroupIdentifier group, MemberFilter filter)
		throws RemoteException, GridGrouperRuntimeFault, GroupNotFoundFault {
		GrouperSession session = null;
		try {
			Subject caller = SubjectFinder.findById(gridIdentity);
			session = GrouperSession.start(caller);
			Group target = GroupFinder.findByName(session, group.getGroupName());
			Set set = null;
			if (filter.equals(MemberFilter.All)) {
				set = target.getMembers();
			} else if (filter.equals(MemberFilter.EffectiveMembers)) {
				set = target.getEffectiveMembers();
			} else if (filter.equals(MemberFilter.ImmediateMembers)) {
				set = target.getImmediateMembers();
			} else if (filter.equals(MemberFilter.CompositeMembers)) {
				set = target.getCompositeMembers();
			} else {
				throw new Exception("Unsuppoted member filter type!!!");
			}

			MemberDescriptor[] members = new MemberDescriptor[set.size()];
			Iterator itr = set.iterator();
			int count = 0;
			while (itr.hasNext()) {
				Member m = (Member) itr.next();
				members[count] = memberToMemberDescriptor(m);
				count++;
			}
			return members;
		} catch (GroupNotFoundException e) {
			GroupNotFoundFault fault = new GroupNotFoundFault();
			fault.setFaultString("The group, " + group.getGroupName() + " was not found.");
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GroupNotFoundFault) helper.getFault();
			throw fault;
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("Error occurred getting the members of the group " + group.getGroupName() + ": "
				+ e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GridGrouperRuntimeFault) helper.getFault();
			throw fault;
		} finally {
			if (session != null) {
				try {
					session.stop();
				} catch (Exception e) {
					this.log.error(e.getMessage(), e);
				}
			}
		}
	}


	public boolean isMemberOf(GrouperSession session, GroupIdentifier group, String member, MemberFilter filter)
		throws GridGrouperRuntimeFault, GroupNotFoundFault {
		try {
			Group target = GroupFinder.findByName(session, group.getGroupName());
			if (filter.equals(MemberFilter.All)) {
				return target.hasMember(SubjectFinder.findById(member));
			} else if (filter.equals(MemberFilter.EffectiveMembers)) {
				return target.hasEffectiveMember(SubjectFinder.findById(member));
			} else if (filter.equals(MemberFilter.ImmediateMembers)) {
				return target.hasImmediateMember(SubjectFinder.findById(member));
			} else {
				throw new Exception("Unsuppoted member filter type!!!");
			}

		} catch (GroupNotFoundException e) {
			GroupNotFoundFault fault = new GroupNotFoundFault();
			fault.setFaultString("The group, " + group.getGroupName() + "was not found.");
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GroupNotFoundFault) helper.getFault();
			throw fault;
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("Error occurred determining if " + member + " is a member of the group "
				+ group.getGroupName() + ": " + e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GridGrouperRuntimeFault) helper.getFault();
			throw fault;
		} 
	}


	public boolean isMemberOf(String gridIdentity, GroupIdentifier group, String member, MemberFilter filter)
		throws GridGrouperRuntimeFault, GroupNotFoundFault {
		GrouperSession session = null;
		try {
			Subject caller = SubjectFinder.findById(gridIdentity);
			session = GrouperSession.start(caller);
			return isMemberOf(session, group, member, filter);
		} catch (GridGrouperRuntimeFault e) {
			throw e;
		} catch (GroupNotFoundFault e) {
			throw e;
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("Error occurred determining if " + member + " is a member of the group "
				+ group.getGroupName() + ": " + e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GridGrouperRuntimeFault) helper.getFault();
			throw fault;
		} finally {
			if (session != null) {
				try {
					session.stop();
				} catch (Exception e) {
					this.log.error(e.getMessage(), e);
				}
			}
		}

	}


	public MembershipDescriptor[] getMemberships(String gridIdentity, GroupIdentifier group, MemberFilter filter)
		throws RemoteException, GridGrouperRuntimeFault, GroupNotFoundFault {
		GrouperSession session = null;
		try {
			Subject caller = SubjectFinder.findById(gridIdentity);
			session = GrouperSession.start(caller);
			Group target = GroupFinder.findByName(session, group.getGroupName());
			Set set = null;
			if (filter.equals(MemberFilter.All)) {
				set = target.getMemberships();
			} else if (filter.equals(MemberFilter.EffectiveMembers)) {
				set = target.getEffectiveMemberships();
			} else if (filter.equals(MemberFilter.ImmediateMembers)) {
				set = target.getImmediateMemberships();
			} else if (filter.equals(MemberFilter.CompositeMembers)) {
				set = target.getCompositeMemberships();
			} else {
				throw new Exception("Unsuppoted member filter type!!!");
			}

			MembershipDescriptor[] members = new MembershipDescriptor[set.size()];
			Iterator itr = set.iterator();
			int count = 0;
			while (itr.hasNext()) {
				Membership m = (Membership) itr.next();
				members[count] = new MembershipDescriptor();
				members[count].setMember(memberToMemberDescriptor(m.getMember()));
				members[count].setGroup(grouptoGroupDescriptor(m.getGroup()));
				try {
					members[count].setViaGroup(grouptoGroupDescriptor(m.getViaGroup()));
				} catch (GroupNotFoundException gnfe) {

				}

				members[count].setDepth(m.getDepth());
				count++;
			}
			return members;
		} catch (GroupNotFoundException e) {
			GroupNotFoundFault fault = new GroupNotFoundFault();
			fault.setFaultString("The group, " + group.getGroupName() + "was not found.");
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GroupNotFoundFault) helper.getFault();
			throw fault;
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("Error occurred getting the members of the group " + group.getGroupName() + ": "
				+ e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GridGrouperRuntimeFault) helper.getFault();
			throw fault;
		} finally {
			if (session != null) {
				try {
					session.stop();
				} catch (Exception e) {
					this.log.error(e.getMessage(), e);
				}
			}
		}

	}


	public Group getAdminGroup() {
		return this.adminGroup;
	}


	private MemberDescriptor memberToMemberDescriptor(Member m) throws Exception {
		MemberDescriptor member = new MemberDescriptor();
		member.setUUID(m.getUuid());
		member.setSubjectId(m.getSubjectId());
		member.setSubjectName(m.getSubject().getName());
		if (m.getSubject().getSource().getClass().getName().equals(GridSourceAdapter.class.getName())) {
			member.setMemberType(MemberType.Grid);

		} else if ((m.getSubjectType().equals(SubjectTypeEnum.GROUP))
			&& (m.getSubject().getSource().getClass().getName().equals(GrouperSourceAdapter.class.getName()))) {
			member.setMemberType(MemberType.GrouperGroup);
		} else {
			member.setMemberType(MemberType.Other);
		}
		return member;
	}


	private GroupDescriptor grouptoGroupDescriptor(Group group) throws Exception {
		GroupDescriptor des = new GroupDescriptor();
		des.setCreateSource(group.getCreateSource());
		des.setParentStem(group.getParentStem().getName());
		des.setCreateSubject(group.getCreateSubject().getId());
		des.setCreateTime(group.getCreateTime().getTime());
		des.setDescription(group.getDescription());
		des.setDisplayExtension(group.getDisplayExtension());
		des.setDisplayName(group.getDisplayName());
		des.setExtension(group.getExtension());
		des.setModifySource(group.getModifySource());
		try {
			des.setModifySubject(group.getModifySubject().getId());
		} catch (Exception ex) {
			if (ex.getMessage().indexOf("has not been modified") != -1) {
				des.setModifySubject("");
			} else {
				throw ex;
			}
		}
		des.setModifyTime(group.getModifyTime().getTime());
		des.setName(group.getName());
		des.setUUID(group.getUuid());
		des.setHasComposite(group.hasComposite());
		des.setIsComposite(group.isComposite());
		return des;
	}

	private MembershipRequestDescriptor membershiprequesttoMembershipRequestDescriptor(MembershipRequests membershipRequest) throws Exception {
		MembershipRequestDescriptor des = new MembershipRequestDescriptor();
		des.setUUID(membershipRequest.getId());
		des.setRequestorId(membershipRequest.getRequestor());
		des.setRequestTime(membershipRequest.getRequestTime());
		des.setStatus(membershipRequest.getStatus());
		if (membershipRequest.getReviewer() != null) {
			des.setReviewerId(membershipRequest.getReviewer().getSubjectId());
		}
		des.setReviewTime(membershipRequest.getReviewTime());
		des.setReviewerNote(membershipRequest.getReviewerNote());
		return des;
	}

	private StemDescriptor stemtoStemDescriptor(Stem stem) throws Exception {
		StemDescriptor des = new StemDescriptor();
		des.setCreateSource(stem.getCreateSource());
		des.setCreateSubject(stem.getCreateSubject().getId());
		des.setCreateTime(stem.getCreateTime().getTime());
		des.setDescription(stem.getDescription());
		des.setDisplayExtension(stem.getDisplayExtension());
		des.setDisplayName(stem.getDisplayName());
		des.setExtension(stem.getExtension());
		des.setModifySource(stem.getModifySource());
		try {
			des.setModifySubject(stem.getModifySubject().getId());
		} catch (Exception ex) {
			if (ex.getMessage().indexOf("has not been modified") != -1) {
				des.setModifySubject("");
			} else {
				throw ex;
			}
		}
		des.setModifyTime(stem.getModifyTime().getTime());
		des.setName(stem.getName());
		des.setUUID(stem.getUuid());
		return des;
	}


	public void deleteMember(String gridIdentity, GroupIdentifier group, String member) throws RemoteException,
		GridGrouperRuntimeFault, InsufficientPrivilegeFault, GroupNotFoundFault, MemberDeleteFault {
		GrouperSession session = null;
		try {
			Subject caller = SubjectFinder.findById(gridIdentity);
			session = GrouperSession.start(caller);
			Group grp = GroupFinder.findByName(session, group.getGroupName());
			grp.deleteMember(SubjectFinder.findById(member));
		} catch (GroupNotFoundException e) {
			GroupNotFoundFault fault = new GroupNotFoundFault();
			fault.setFaultString("The group, " + group.getGroupName() + "was not found.");
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GroupNotFoundFault) helper.getFault();
			throw fault;
		} catch (MemberDeleteException e) {
			MemberDeleteFault fault = new MemberDeleteFault();
			fault.setFaultString(e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (MemberDeleteFault) helper.getFault();
			throw fault;
		} catch (InsufficientPrivilegeException e) {
			InsufficientPrivilegeFault fault = new InsufficientPrivilegeFault();
			fault.setFaultString(e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (InsufficientPrivilegeFault) helper.getFault();
			throw fault;
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("Error occurred deleting the member " + member + " from the group "
				+ group.getGroupName() + ": " + e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GridGrouperRuntimeFault) helper.getFault();
			throw fault;
		} finally {
			if (session != null) {
				try {
					session.stop();
				} catch (Exception e) {
					this.log.error(e.getMessage(), e);
				}
			}
		}
	}


	public GroupDescriptor addCompositeMember(String gridIdentity, GroupCompositeType type, GroupIdentifier composite,
		GroupIdentifier left, GroupIdentifier right) throws GridGrouperRuntimeFault, GroupNotFoundFault,
		MemberAddFault, InsufficientPrivilegeFault {
		GrouperSession session = null;
		try {
			Subject caller = SubjectFinder.findById(gridIdentity);
			session = GrouperSession.start(caller);
			Group grp = GroupFinder.findByName(session, composite.getGroupName());
			Group leftgrp = GroupFinder.findByName(session, left.getGroupName());
			Group rightgrp = GroupFinder.findByName(session, right.getGroupName());
			CompositeType ct = null;
			if (type.equals(GroupCompositeType.Union)) {
				ct = CompositeType.UNION;
			} else if (type.equals(GroupCompositeType.Intersection)) {
				ct = CompositeType.INTERSECTION;
			} else if (type.equals(GroupCompositeType.Complement)) {
				ct = CompositeType.COMPLEMENT;
			} else {
				throw new Exception("The composite type " + type.getValue() + " is not supported!!!");
			}
			grp.addCompositeMember(ct, leftgrp, rightgrp);
			return grouptoGroupDescriptor(grp);
		} catch (GroupNotFoundException e) {
			GroupNotFoundFault fault = new GroupNotFoundFault();
			fault.setFaultString(e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GroupNotFoundFault) helper.getFault();
			throw fault;
		} catch (MemberAddException e) {
			MemberAddFault fault = new MemberAddFault();
			fault.setFaultString(e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (MemberAddFault) helper.getFault();
			throw fault;
		} catch (InsufficientPrivilegeException e) {
			InsufficientPrivilegeFault fault = new InsufficientPrivilegeFault();
			fault.setFaultString(e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (InsufficientPrivilegeFault) helper.getFault();
			throw fault;
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("Error occurred adding a composite member to the group " + composite.getGroupName()
				+ ": " + e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GridGrouperRuntimeFault) helper.getFault();
			throw fault;
		} finally {
			if (session != null) {
				try {
					session.stop();
				} catch (Exception e) {
					this.log.error(e.getMessage(), e);
				}
			}
		}
	}


	public GroupDescriptor deleteCompositeMember(String gridIdentity, GroupIdentifier group)
		throws GridGrouperRuntimeFault, GroupNotFoundFault, InsufficientPrivilegeFault, MemberDeleteFault {
		GrouperSession session = null;
		try {
			Subject caller = SubjectFinder.findById(gridIdentity);
			session = GrouperSession.start(caller);
			Group grp = GroupFinder.findByName(session, group.getGroupName());
			grp.deleteCompositeMember();
			return grouptoGroupDescriptor(grp);
		} catch (GroupNotFoundException e) {
			GroupNotFoundFault fault = new GroupNotFoundFault();
			fault.setFaultString("The group, " + group.getGroupName() + "was not found.");
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GroupNotFoundFault) helper.getFault();
			throw fault;
		} catch (MemberDeleteException e) {
			MemberDeleteFault fault = new MemberDeleteFault();
			fault.setFaultString(e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (MemberDeleteFault) helper.getFault();
			throw fault;
		} catch (InsufficientPrivilegeException e) {
			InsufficientPrivilegeFault fault = new InsufficientPrivilegeFault();
			fault.setFaultString(e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (InsufficientPrivilegeFault) helper.getFault();
			throw fault;
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("Error occurred deleting the composite member from the group " + group.getGroupName()
				+ ": " + e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GridGrouperRuntimeFault) helper.getFault();
			throw fault;
		} finally {
			if (session != null) {
				try {
					session.stop();
				} catch (Exception e) {
					this.log.error(e.getMessage(), e);
				}
			}
		}
	}


	public void grantGroupPrivilege(String gridIdentity, GroupIdentifier group, String subject,
		GroupPrivilegeType privilege) throws GridGrouperRuntimeFault, GroupNotFoundFault, GrantPrivilegeFault,
		InsufficientPrivilegeFault {
		GrouperSession session = null;
		try {
			Subject subj = SubjectFinder.findById(gridIdentity);
			session = GrouperSession.start(subj);
			Group grp = GroupFinder.findByName(session, group.getGroupName());
			grp.grantPriv(SubjectFinder.findById(subject), Privilege.getInstance(privilege.getValue()));
		} catch (GroupNotFoundException e) {
			GroupNotFoundFault fault = new GroupNotFoundFault();
			fault.setFaultString("The group, " + group.getGroupName() + "was not found.");
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GroupNotFoundFault) helper.getFault();
			throw fault;
		} catch (GrantPrivilegeException e) {
			GrantPrivilegeFault fault = new GrantPrivilegeFault();
			fault.setFaultString(e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GrantPrivilegeFault) helper.getFault();
			throw fault;
		} catch (InsufficientPrivilegeException e) {
			InsufficientPrivilegeFault fault = new InsufficientPrivilegeFault();
			fault.setFaultString("You do not have the right to manages privileges on the group " + group.getGroupName()
				+ ": " + e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (InsufficientPrivilegeFault) helper.getFault();
			throw fault;
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("Error occurred granting a privilege for the subject " + subject + " on the group "
				+ group.getGroupName() + ": " + e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GridGrouperRuntimeFault) helper.getFault();
			throw fault;
		} finally {
			if (session != null) {
				try {
					session.stop();
				} catch (Exception e) {
					this.log.error(e.getMessage(), e);
				}
			}
		}
	}


	public void revokeGroupPrivilege(String gridIdentity, GroupIdentifier group, String subject,
		GroupPrivilegeType privilege) throws RemoteException, GridGrouperRuntimeFault, GroupNotFoundFault,
		RevokePrivilegeFault, InsufficientPrivilegeFault, SchemaFault {
		GrouperSession session = null;
		try {
			Subject subj = SubjectFinder.findById(gridIdentity);
			session = GrouperSession.start(subj);
			Group grp = GroupFinder.findByName(session, group.getGroupName());
			grp.revokePriv(SubjectFinder.findById(subject), Privilege.getInstance(privilege.getValue()));
		} catch (GroupNotFoundException e) {
			GroupNotFoundFault fault = new GroupNotFoundFault();
			fault.setFaultString("The group, " + group.getGroupName() + "was not found.");
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GroupNotFoundFault) helper.getFault();
			throw fault;
		} catch (RevokePrivilegeException e) {
			RevokePrivilegeFault fault = new RevokePrivilegeFault();
			fault.setFaultString(e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (RevokePrivilegeFault) helper.getFault();
			throw fault;
		} catch (InsufficientPrivilegeException e) {
			InsufficientPrivilegeFault fault = new InsufficientPrivilegeFault();
			fault.setFaultString("You do not have the right to manages privileges on the group " + group.getGroupName()
				+ ": " + e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (InsufficientPrivilegeFault) helper.getFault();
			throw fault;
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("Error occurred revoking a privilege for the subject " + subject + " on the group "
				+ group.getGroupName() + ": " + e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GridGrouperRuntimeFault) helper.getFault();
			throw fault;
		} finally {
			if (session != null) {
				try {
					session.stop();
				} catch (Exception e) {
					this.log.error(e.getMessage(), e);
				}
			}
		}
	}


	public String[] getSubjectsWithGroupPrivilege(String gridIdentity, GroupIdentifier group,
		GroupPrivilegeType privilege) throws RemoteException, GridGrouperRuntimeFault, GroupNotFoundFault {
		GrouperSession session = null;
		try {
			Subject subject = SubjectFinder.findById(gridIdentity);
			session = GrouperSession.start(subject);
			Group grp = GroupFinder.findByName(session, group.getGroupName());
			Set subs = null;
			if (privilege.equals(GroupPrivilegeType.admin)) {
				subs = grp.getAdmins();
			} else if (privilege.equals(GroupPrivilegeType.optin)) {
				subs = grp.getOptins();
			} else if (privilege.equals(GroupPrivilegeType.optout)) {
				subs = grp.getOptouts();
			} else if (privilege.equals(GroupPrivilegeType.read)) {
				subs = grp.getReaders();
			} else if (privilege.equals(GroupPrivilegeType.update)) {
				subs = grp.getUpdaters();
			} else if (privilege.equals(GroupPrivilegeType.view)) {
				subs = grp.getViewers();
			} else {
				throw new Exception(privilege.getValue() + " is not a valid group privilege!!!");
			}
			int size = 0;
			if (subs != null) {
				size = subs.size();
			}
			String[] subjects = new String[size];
			if (subs != null) {
				Iterator itr = subs.iterator();
				int count = 0;
				while (itr.hasNext()) {
					Subject s = (Subject) itr.next();
					subjects[count] = s.getId();
					count++;
				}
			}
			return subjects;
		} catch (GroupNotFoundException e) {
			GroupNotFoundFault fault = new GroupNotFoundFault();
			fault.setFaultString("The group, " + group.getGroupName() + "was not found.");
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GroupNotFoundFault) helper.getFault();
			throw fault;
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("Error occurred getting the subjects with the privilege " + privilege.getValue()
				+ " on the group " + group.getGroupName() + ": " + e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GridGrouperRuntimeFault) helper.getFault();
			throw fault;
		} finally {
			if (session != null) {
				try {
					session.stop();
				} catch (Exception e) {
					this.log.error(e.getMessage(), e);
				}
			}
		}
	}


	public GroupPrivilege[] getGroupPrivileges(String gridIdentity, GroupIdentifier group, String subject)
		throws GridGrouperRuntimeFault, GroupNotFoundFault {
		GrouperSession session = null;
		try {

			Subject subj = SubjectFinder.findById(gridIdentity);
			session = GrouperSession.start(subj);
			Group grp = GroupFinder.findByName(session, group.getGroupName());
			Set privs = grp.getPrivs(SubjectFinder.findById(subject));
			int size = 0;
			if (privs != null) {
				size = privs.size();
			}
			GroupPrivilege[] rights = new GroupPrivilege[size];
			if (privs != null) {
				Iterator itr = privs.iterator();
				int count = 0;
				while (itr.hasNext()) {
					AccessPrivilege p = (AccessPrivilege) itr.next();
					rights[count] = new GroupPrivilege();
					rights[count].setGroupName(p.getGroup().getName());
					rights[count].setImplementationClass(p.getImplementationName());
					rights[count].setIsRevokable(p.isRevokable());
					rights[count].setOwner(p.getOwner().getId());
					rights[count].setPrivilegeType(GroupPrivilegeType.fromValue(p.getName()));
					rights[count].setSubject(p.getSubject().getId());
					count++;
				}
			}
			return rights;
		} catch (GroupNotFoundException e) {
			GroupNotFoundFault fault = new GroupNotFoundFault();
			fault.setFaultString("The group, " + group.getGroupName() + "was not found.");
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GroupNotFoundFault) helper.getFault();
			throw fault;
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("Error occurred getting the privileges for the subject " + subject + " on the group "
				+ group.getGroupName() + ": " + e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GridGrouperRuntimeFault) helper.getFault();
			throw fault;
		} finally {
			if (session != null) {
				try {
					session.stop();
				} catch (Exception e) {
					this.log.error(e.getMessage(), e);
				}
			}
		}
	}


	public boolean hasGroupPrivilege(String gridIdentity, GroupIdentifier group, String subject,
		GroupPrivilegeType privilege) throws GridGrouperRuntimeFault, GroupNotFoundFault {
		GrouperSession session = null;
		try {
			Subject subj = SubjectFinder.findById(gridIdentity);
			session = GrouperSession.start(subj);
			Group target = GroupFinder.findByName(session, group.getGroupName());
			if (privilege == null) {
				return false;
			} else if (privilege.equals(GroupPrivilegeType.admin)) {
				return target.hasAdmin(SubjectFinder.findById(subject));
			} else if (privilege.equals(GroupPrivilegeType.optin)) {
				return target.hasOptin(SubjectFinder.findById(subject));
			} else if (privilege.equals(GroupPrivilegeType.optout)) {
				return target.hasOptout(SubjectFinder.findById(subject));
			} else if (privilege.equals(GroupPrivilegeType.read)) {
				return target.hasRead(SubjectFinder.findById(subject));
			} else if (privilege.equals(GroupPrivilegeType.update)) {
				return target.hasUpdate(SubjectFinder.findById(subject));
			} else if (privilege.equals(GroupPrivilegeType.view)) {
				return target.hasView(SubjectFinder.findById(subject));
			} else {
				return false;
			}

		} catch (GroupNotFoundException e) {
			GroupNotFoundFault fault = new GroupNotFoundFault();
			fault.setFaultString("The group, " + group.getGroupName() + "was not found.");
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GroupNotFoundFault) helper.getFault();
			throw fault;
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("Error determing if the subject " + subject + " has the privilege "
				+ privilege.getValue() + " on the group " + group.getGroupName() + ": " + e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GridGrouperRuntimeFault) helper.getFault();
			throw fault;
		} finally {
			if (session != null) {
				try {
					session.stop();
				} catch (Exception e) {
					this.log.error(e.getMessage(), e);
				}
			}
		}
	}

	private void validateMemberAccess(String caller, String member) throws SubjectNotFoundException,
		InsufficientPrivilegeFault {
		if ((caller.equals(member)) || (getAdminGroup().hasMember(SubjectUtils.getSubject(caller)))) {
			return;
		} else {
			InsufficientPrivilegeFault fault = new InsufficientPrivilegeFault();
			fault.setFaultString("You do not have access to the member " + member + ".");
			throw fault;
		}
	}

	public MemberDescriptor getMember(String gridIdentity, String memberIdentity) throws GridGrouperRuntimeFault,
		InsufficientPrivilegeFault {
		GrouperSession session = null;
		try {
			validateMemberAccess(gridIdentity, memberIdentity);
			Subject subj = SubjectFinder.findById(gridIdentity);
			session = GrouperSession.start(subj);
			Member m = MemberFinder.findBySubject(session, SubjectUtils.getSubject(memberIdentity));
			return memberToMemberDescriptor(m);
			// TODO: We may need to also throw a member not found fault
		} catch (InsufficientPrivilegeFault e) {
			throw e;
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("Error finding the member " + memberIdentity + ":\n" + e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GridGrouperRuntimeFault) helper.getFault();
			throw fault;
		} finally {
			if (session != null) {
				try {
					session.stop();
				} catch (Exception e) {
					this.log.error(e.getMessage(), e);
				}
			}
		}

	}


	public GroupDescriptor[] getMembersGroups(String gridIdentity, String memberIdentity, MembershipType type)
		throws GridGrouperRuntimeFault, InsufficientPrivilegeFault {
		GrouperSession session = null;
		try {
			Subject subj = SubjectFinder.findById(gridIdentity);
			session = GrouperSession.start(subj);
			Member m = MemberFinder.findBySubject(session, SubjectUtils.getSubject(memberIdentity));
			Set<?> set = null;
			if ((type != null) && (type.equals(MembershipType.EffectiveMembers))) {
				set = m.getEffectiveGroups();
			} else if ((type != null) && (type.equals(MembershipType.ImmediateMembers))) {
				set = m.getImmediateGroups();
			} else {
				set = m.getGroups();
			}

			Iterator<?> itr = set.iterator();
			ArrayList<GroupDescriptor> groups = new ArrayList<GroupDescriptor>();
			while (itr.hasNext()) {
				Group group = (Group) itr.next();
				if ((group.hasRead(subj) && group.hasView(subj)) || group.hasUpdate(subj) || group.hasAdmin(subj) || gridIdentity.equals(memberIdentity) || (getAdminGroup().hasMember(subj))) {					
					groups.add(grouptoGroupDescriptor(group));
				}
			}
			GroupDescriptor[] grps = groups.toArray(new GroupDescriptor[groups.size()]);
			return grps;
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("Error finding the member " + memberIdentity + ":\n" + e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GridGrouperRuntimeFault) helper.getFault();
			throw fault;
		} finally {
			if (session != null) {
				try {
					session.stop();
				} catch (Exception e) {
					this.log.error(e.getMessage(), e);
				}
			}
		}

	}


	public boolean isMember(String gridIdentity, String member, MembershipExpression exp)
		throws GridGrouperRuntimeFault {
		GrouperSession session = null;
		try {
			Subject subj = SubjectFinder.findById(gridIdentity);
			session = GrouperSession.start(subj);
			return isMember(session, member, exp);
		} catch (GridGrouperRuntimeFault f) {
			throw f;
		} catch (Exception e) {

			this.log.error(e.getMessage(), e);
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("Error determing if the subject " + member + " is a member: " + e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GridGrouperRuntimeFault) helper.getFault();
			throw fault;
		} finally {
			if (session != null) {
				try {
					session.stop();
				} catch (Exception e) {
					this.log.error(e.getMessage(), e);
				}
			}
		}
	}


	private boolean isMember(GrouperSession session, String member, MembershipExpression exp)
		throws GridGrouperRuntimeFault {
		if (exp.getLogicRelation().equals(LogicalOperator.AND)) {
			return evaluateAndExpression(session, member, exp);
		} else {
			return evaluateOrExpression(session, member, exp);
		}
	}


	private boolean evaluateAndExpression(GrouperSession session, String member, MembershipExpression exp)
		throws GridGrouperRuntimeFault {
		MembershipExpression[] exps = exp.getMembershipExpression();
		MembershipQuery[] queries = exp.getMembershipQuery();

		if ((exps == null) && (queries == null)) {
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("Invalid Expression");
			throw fault;
		}

		if (exps != null) {
			for (int i = 0; i < exps.length; i++) {
				if (!isMember(session, member, exps[i])) {
					return false;
				}
			}
		}

		if (queries != null) {
			for (int i = 0; i < queries.length; i++) {
				String grpName = queries[i].getGroupIdentifier().getGroupName();
				try {
					Group grp = GroupFinder.findByName(session, grpName);
					boolean isMember = grp.hasMember(SubjectFinder.findById(member));
					if (queries[i].getMembershipStatus().equals(MembershipStatus.NOT_MEMBER_OF)) {
						if (isMember) {
							return false;
						}
					} else {
						if (!isMember) {
							return false;
						}
					}
				} catch (Exception e) {
					this.log.error(e.getMessage(), e);
					GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
					fault.setFaultString("Error in determining if the subject " + member + " is a member of the group "
						+ grpName + ": " + e.getMessage());
					throw fault;
				}
			}
		}

		return true;
	}


	private boolean evaluateOrExpression(GrouperSession session, String member, MembershipExpression exp)
		throws GridGrouperRuntimeFault {
		MembershipExpression[] exps = exp.getMembershipExpression();
		MembershipQuery[] queries = exp.getMembershipQuery();

		if ((exps == null) && (queries == null)) {
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("Invalid Expression");
			throw fault;
		}

		if (exps != null) {
			for (int i = 0; i < exps.length; i++) {
				if (isMember(session, member, exps[i])) {
					return true;
				}
			}
		}

		if (queries != null) {
			for (int i = 0; i < queries.length; i++) {
				String grpName = queries[i].getGroupIdentifier().getGroupName();
				try {
					Group grp = GroupFinder.findByName(session, grpName);
					boolean isMember = grp.hasMember(SubjectFinder.findById(member));
					if (queries[i].getMembershipStatus().equals(MembershipStatus.NOT_MEMBER_OF)) {
						if (!isMember) {
							return true;
						}
					} else {
						if (isMember) {
							return true;
						}
					}
				} catch (Exception e) {
					this.log.error(e.getMessage(), e);
					GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
					fault.setFaultString("Error in determining if the subject " + member + " is a member of the group "
						+ grpName + ": " + e.getMessage());
					throw fault;
				}
			}
		}
		return false;
	}
	
	public void addMembershipRequest(String gridIdentity, GroupIdentifier group, String subject) throws GridGrouperRuntimeFault,
			GroupNotFoundFault, InsufficientPrivilegeFault, MemberAddFault {
		GrouperSession session = null;
		try {
			Subject caller = SubjectFinder.findById(gridIdentity);
			Subject subj = SubjectFinder.findById(subject);
			session = GrouperSession.start(caller);
			Group grp = GroupFinder.findByName(session, group.getGroupName());

			MembershipRequests.create(grp, subject);

		} catch (GroupNotFoundException e) {
			GroupNotFoundFault fault = new GroupNotFoundFault();
			fault.setFaultString("The group, " + group.getGroupName() + "was not found.");
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GroupNotFoundFault) helper.getFault();
			throw fault;
//		} catch (InsufficientPrivilegeException e) {
//			InsufficientPrivilegeFault fault = new InsufficientPrivilegeFault();
//			fault.setFaultString(e.getMessage());
//			FaultHelper helper = new FaultHelper(fault);
//			helper.addFaultCause(e);
//			fault = (InsufficientPrivilegeFault) helper.getFault();
//			throw fault;
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("Error occurred adding a member to the group " + group.getGroupName() + ": " + e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GridGrouperRuntimeFault) helper.getFault();
			throw fault;
		} finally {
			if (session != null) {
				try {
					session.stop();
				} catch (Exception e) {
					this.log.error(e.getMessage(), e);
				}
			}
		}
	}
	
	public MembershipRequestDescriptor[] getMembershipRequests(String gridIdentity, GroupIdentifier group, MembershipRequestStatus status)
			throws RemoteException, GridGrouperRuntimeFault, GroupNotFoundFault {
		GrouperSession session = null;
		try {
			Subject caller = SubjectFinder.findById(gridIdentity);
			session = GrouperSession.start(caller);
			Group grp = GroupFinder.findByName(session, group.getGroupName());

			ArrayList<MembershipRequests> requests = MembershipRequestsFinder.findRequestsByStatus(session, grp, status);
			ArrayList<MembershipRequestDescriptor> membershipRequestDescriptor = new ArrayList<MembershipRequestDescriptor>();
			for (MembershipRequests request : requests) {
				if (grp.hasAdmin(caller) || gridIdentity.equals(request.getRequestor()) || (getAdminGroup().hasMember(caller))) {					
					membershipRequestDescriptor.add(membershiprequesttoMembershipRequestDescriptor(request));
				}
			}
			
			return membershipRequestDescriptor.toArray(new MembershipRequestDescriptor[membershipRequestDescriptor.size()]);

		} catch (GroupNotFoundException e) {
			GroupNotFoundFault fault = new GroupNotFoundFault();
			fault.setFaultString("The group, " + group.getGroupName() + "was not found.");
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GroupNotFoundFault) helper.getFault();
			throw fault;
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("Error occurred adding a member to the group " + group.getGroupName() + ": " + e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GridGrouperRuntimeFault) helper.getFault();
			throw fault;
		} finally {
			if (session != null) {
				try {
					session.stop();
				} catch (Exception e) {
					this.log.error(e.getMessage(), e);
				}
			}
		}
	}
	
	public MembershipRequestDescriptor updateMembershipRequest(String gridIdentity, GroupIdentifier group, String subject, MembershipRequestUpdate update) throws GridGrouperRuntimeFault,
			GroupNotFoundFault, InsufficientPrivilegeFault, MemberAddFault {
		GrouperSession session = null;
		try {
			Subject callerSubject = SubjectFinder.findById(gridIdentity);
			session = GrouperSession.start(callerSubject);
			Group grp = GroupFinder.findByName(session, group.getGroupName());

			Member caller = MemberFinder.findBySubject(session, callerSubject);
			Subject subj = SubjectFinder.findById(subject);

			MembershipRequests membershipRequest = MembershipRequestsFinder.findRequest(session, grp, subject);
			
			if (MembershipRequestStatus.Approved.equals(update.getStatus())) {
				membershipRequest.approve(caller, update.getNote());
				grp.addMember(subj);
			} else {
				membershipRequest.reject(caller, update.getNote());
			}
			
			return membershiprequesttoMembershipRequestDescriptor(membershipRequest);

		} catch (GroupNotFoundException e) {
			GroupNotFoundFault fault = new GroupNotFoundFault();
			fault.setFaultString("The group, " + group.getGroupName() + "was not found.");
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GroupNotFoundFault) helper.getFault();
			throw fault;
		} catch (InsufficientPrivilegeException e) {
			InsufficientPrivilegeFault fault = new InsufficientPrivilegeFault();
			fault.setFaultString(e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (InsufficientPrivilegeFault) helper.getFault();
			throw fault;
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("Error occurred adding a member to the group " + group.getGroupName() + ": " + e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GridGrouperRuntimeFault) helper.getFault();
			throw fault;
		} finally {
			if (session != null) {
				try {
					session.stop();
				} catch (Exception e) {
					this.log.error(e.getMessage(), e);
				}
			}
		}
	}


}
