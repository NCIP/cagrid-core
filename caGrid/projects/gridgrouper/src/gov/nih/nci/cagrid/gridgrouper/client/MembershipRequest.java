/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package gov.nih.nci.cagrid.gridgrouper.client;

import edu.internet2.middleware.grouper.GrouperRuntimeException;
import edu.internet2.middleware.grouper.InsufficientPrivilegeException;
import edu.internet2.middleware.grouper.SchemaException;
import edu.internet2.middleware.subject.SubjectNotFoundException;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.gridgrouper.bean.GroupDescriptor;
import gov.nih.nci.cagrid.gridgrouper.bean.GroupIdentifier;
import gov.nih.nci.cagrid.gridgrouper.bean.MembershipRequestDescriptor;
import gov.nih.nci.cagrid.gridgrouper.bean.MembershipRequestHistoryDescriptor;
import gov.nih.nci.cagrid.gridgrouper.bean.MembershipRequestStatus;
import gov.nih.nci.cagrid.gridgrouper.bean.MembershipRequestUpdate;
import gov.nih.nci.cagrid.gridgrouper.grouper.MemberI;
import gov.nih.nci.cagrid.gridgrouper.grouper.MembershipRequestHistoryI;
import gov.nih.nci.cagrid.gridgrouper.grouper.MembershipRequestI;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.GridGrouperRuntimeFault;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.InsufficientPrivilegeFault;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.SchemaFault;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class MembershipRequest extends GridGrouperObject implements MembershipRequestI {
    private MembershipRequestDescriptor des;

    private GridGrouper gridGrouper;
    
    private MemberI reviewer;
    
    public MembershipRequest(GridGrouper gridGrouper, MembershipRequestDescriptor des) throws SubjectNotFoundException {
        this.gridGrouper = gridGrouper;
        this.des = des;
        if (des.getReviewer() != null) {
        	this.reviewer = new Member(gridGrouper, des.getReviewer());
        }
        
    }
    
    public String getGroupName() {
    	return des.getGroup().getDisplayName();
    }

    public String getPublicNote() {
    	return des.getPublicNote();
    }

    public String getAdminNote() {
    	return des.getAdminNote();
    }

    public long getReviewTime() {
    	return des.getReviewTime();
    }

    public String getRequestorId() {
    	return des.getRequestorId();
    }

	public MemberI getReviewer() {
		return reviewer;
	}

	public MembershipRequestStatus getStatus() {
		return des.getStatus();
	}
	
	public Set<MembershipRequestHistoryI> getHistory() {
		try {
			Set<MembershipRequestHistoryI> history = new LinkedHashSet<MembershipRequestHistoryI>();
			for (MembershipRequestHistoryDescriptor histDes : des.getHistory()) {
				history.add(new MembershipRequestHistory(gridGrouper, histDes));
			}
			return history;
		} catch (Exception e) {
			getLog().error(e.getMessage(), e);
			throw new GrouperRuntimeException(Utils.getExceptionMessage(e));
		}

	}
		
	public void approve(String publicNote, String adminNote) throws InsufficientPrivilegeException, SchemaException {
		updateMembershipRequest(publicNote, adminNote, MembershipRequestStatus.Approved);
	}
	
	public void reject(String publicNote, String adminNote) throws InsufficientPrivilegeException, SchemaException {
		updateMembershipRequest(publicNote, adminNote, MembershipRequestStatus.Rejected);
	}
	
	private void updateMembershipRequest(String publicNote, String adminNote, MembershipRequestStatus status) throws InsufficientPrivilegeException, SchemaException {
		MembershipRequestUpdate update = new MembershipRequestUpdate();
		update.setStatus(status);
		update.setPublicNote(publicNote);
		update.setAdminNote(adminNote);
		try {
			gridGrouper.getClient().updateMembershipRequest(getGroupIdentifier(des.getGroup()), des.getRequestorId(), update);
        } catch (InsufficientPrivilegeFault f) {
            throw new InsufficientPrivilegeException(f.getFaultString());
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
	
	private GroupIdentifier getGroupIdentifier(GroupDescriptor des) {
		GroupIdentifier id = new GroupIdentifier();
		id.setGroupName(des.getName());
		return id;
	}


}
