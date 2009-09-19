/*
 * Created on Sep 21, 2006
 */
package gov.nci.nih.cagrid.tests.core.steps;

import gov.nih.nci.cagrid.gridgrouper.bean.GroupDescriptor;
import gov.nih.nci.cagrid.gridgrouper.bean.StemIdentifier;
import gov.nih.nci.cagrid.gridgrouper.client.GridGrouperClient;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.GridGrouperRuntimeFault;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.StemNotFoundFault;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.rmi.RemoteException;
import java.util.HashSet;

import org.apache.axis.types.URI.MalformedURIException;


public class GrouperCheckGroupsStep extends Step {
    private String endpoint;
    private String path;
    private String[] children;


    public GrouperCheckGroupsStep(String path, String[] children, String endpoint) {
        super();

        this.endpoint = endpoint;
        this.path = path;
        this.children = children;
    }


    @Override
    public void runStep() throws GridGrouperRuntimeFault, StemNotFoundFault, RemoteException, MalformedURIException {
        GridGrouperClient grouper = new GridGrouperClient(this.endpoint);

        // get child stems
        GroupDescriptor[] groups = grouper.getChildGroups(new StemIdentifier(null, this.path));
        if (groups == null && this.children.length == 0) {
            return;
        }
        assertEquals(this.children.length, groups.length);
        HashSet<String> groupSet = new HashSet<String>(groups.length);
        for (GroupDescriptor group : groups) {
            groupSet.add(group.getName());
        }

        // check child groups
        String path = this.path;
        if (!path.equals("")) {
            path = path + ":";
        }
        for (String child : this.children) {
            assertTrue(groupSet.contains(path + child));
        }
    }

}
