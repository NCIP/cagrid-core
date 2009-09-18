/*
 * Created on Sep 21, 2006
 */
package gov.nci.nih.cagrid.tests.core.steps;

import gov.nih.nci.cagrid.common.security.ProxyUtil;
import gov.nih.nci.cagrid.gridgrouper.bean.StemDescriptor;
import gov.nih.nci.cagrid.gridgrouper.bean.StemIdentifier;
import gov.nih.nci.cagrid.gridgrouper.client.GridGrouperClient;
import gov.nih.nci.cagrid.gridgrouper.testutils.Utils;
import gov.nih.nci.cagrid.testing.system.haste.Step;


public class GrouperCreateStemStep extends Step {
    private String endpoint;
    private String path;
    private boolean shouldFail;


    public GrouperCreateStemStep(String path, String endpoint) {
        this(path, false, endpoint);
    }


    public GrouperCreateStemStep(String path, boolean shouldFail, String endpoint) {
        super();

        this.endpoint = endpoint;
        this.path = path;
        this.shouldFail = shouldFail;
    }


    @Override
    public void runStep() throws Exception {

        GridGrouperClient grouper = new GridGrouperClient(this.endpoint);
        grouper.setAnonymousPrefered(false);
        StemIdentifier stem = Utils.getRootStemIdentifier();
        for (String name : this.path.split(":")) {
            StemIdentifier nextStem = new StemIdentifier(null, (stem.getStemName() == "" ? "" : stem.getStemName()
                + ":")
                + name);

            boolean foundChild = false;
            StemDescriptor[] childStems = grouper.getChildStems(stem);
            if (childStems != null) {
                for (StemDescriptor childStem : childStems) {
                    if (childStem.getName().equals(nextStem.getStemName())) {
                        foundChild = true;
                        break;
                    }
                }
            }
            if (!foundChild) {
                try {
                    grouper.addChildStem(stem, name, name);
                    if (this.shouldFail) {
                        fail("addChildStem should fail");
                    }
                } catch (Exception e) {
                    if (!this.shouldFail) {
                        throw e;
                    }
                }
            }
            stem = nextStem;
        }
    }
}
