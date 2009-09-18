/*
 * Created on Sep 21, 2006
 */
package gov.nci.nih.cagrid.tests.core.steps;

import gov.nih.nci.cagrid.common.security.ProxyUtil;
import gov.nih.nci.cagrid.gridgrouper.bean.StemIdentifier;
import gov.nih.nci.cagrid.gridgrouper.client.GridGrouperClient;
import gov.nih.nci.cagrid.testing.system.haste.Step;


public class GrouperRemoveStemStep extends Step {
    private String endpoint;
    private String stem;
    private boolean shouldFail;


    public GrouperRemoveStemStep(String stem, String endpoint) {
        this(stem, false, endpoint);
    }


    public GrouperRemoveStemStep(String stem, boolean shouldFail, String endpoint) {
        super();

        this.endpoint = endpoint;
        this.stem = stem;
        this.shouldFail = shouldFail;
    }


    @Override
    public void runStep() throws Exception {
        GridGrouperClient grouper = new GridGrouperClient(this.endpoint);
        grouper.setAnonymousPrefered(false);
        // remove stem
        try {
            grouper.deleteStem(new StemIdentifier(null, this.stem));
            if (this.shouldFail) {
                fail("deleteMember should fail");
            }
        } catch (Exception e) {
            if (!this.shouldFail) {
                throw e;
            }
        }
    }

}
