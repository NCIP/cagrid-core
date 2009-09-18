package org.cagrid.gaards.dorian.federation;

import gov.nih.nci.cagrid.common.FaultHelper;

import org.cagrid.gaards.dorian.stubs.types.DorianInternalFault;
import org.cagrid.gaards.dorian.stubs.types.UserPolicyFault;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */

public class AutoApprovalPolicy extends AccountPolicy {
    public void applyPolicy(TrustedIdP idp, GridUser user) throws DorianInternalFault, UserPolicyFault {
        UserManager um = getUserManager();
        // First we approve if the user has not been approved.
        if (user.getUserStatus().equals(GridUserStatus.Pending)) {
            user.setUserStatus(GridUserStatus.Active);
            try {
                um.updateUser(user);
            } catch (Exception e) {
                DorianInternalFault fault = new DorianInternalFault();
                fault.setFaultString("Error updating the status of the user " + user.getGridId());
                FaultHelper helper = new FaultHelper(fault);
                helper.addFaultCause(e);
                fault = (DorianInternalFault) helper.getFault();
                throw fault;
            }
        }
    }


    public String getDisplayName() {
        return "Auto Approval";
    }
}
