/*
 * Created on Sep 21, 2006
 */
package gov.nci.nih.cagrid.tests.core.steps;

import gov.nih.nci.cagrid.common.security.ProxyUtil;
import gov.nih.nci.cagrid.gridgrouper.bean.GroupIdentifier;
import gov.nih.nci.cagrid.gridgrouper.client.GridGrouperClient;
import gov.nih.nci.cagrid.testing.system.haste.Step;


public class GrouperAddMemberStep extends Step {
    private String endpoint;
    private String group;
    private String subject;
    private boolean shouldFail = false;


    public GrouperAddMemberStep(String group, String subject, String endpoint) {
        this(group, subject, false, endpoint);
    }


    public GrouperAddMemberStep(String group, String subject, boolean shouldFail, String endpoint) {
        super();

        this.endpoint = endpoint;
        this.group = group;
        this.subject = subject;
        this.shouldFail = shouldFail;
    }


    @Override
    public void runStep() throws Exception {
        GridGrouperClient grouper = new GridGrouperClient(this.endpoint);
        grouper.setAnonymousPrefered(false);
        // add member
        try {
            grouper.addMember(new GroupIdentifier(null, this.group), this.subject);
            if (this.shouldFail) {
                fail("addMember should fail");
            }
        } catch (Exception e) {
            if (!this.shouldFail) {
                throw e;
            }
        }
    }

}
