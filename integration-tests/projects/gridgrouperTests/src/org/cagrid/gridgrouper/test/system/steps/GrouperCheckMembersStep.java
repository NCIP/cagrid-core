package org.cagrid.gridgrouper.test.system.steps;

import gov.nih.nci.cagrid.gridgrouper.bean.GroupIdentifier;
import gov.nih.nci.cagrid.gridgrouper.bean.MemberDescriptor;
import gov.nih.nci.cagrid.gridgrouper.bean.MemberFilter;
import gov.nih.nci.cagrid.gridgrouper.client.GridGrouperClient;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.GridGrouperRuntimeFault;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.StemNotFoundFault;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.rmi.RemoteException;
import java.util.HashSet;

import org.apache.axis.types.URI.MalformedURIException;

public class GrouperCheckMembersStep extends Step {
	private String endpoint;
	private String path;
	private String filter;
	private String[] members;

	public GrouperCheckMembersStep(String path, String filter, String[] members, String endpoint) {
		super();

		this.endpoint = endpoint;
		this.path = path;
		this.filter = filter;
		this.members = members;
	}

	@Override
	public void runStep() throws GridGrouperRuntimeFault, StemNotFoundFault, RemoteException, MalformedURIException {
		GridGrouperClient grouper = new GridGrouperClient(this.endpoint);

		// get child stems
		MemberDescriptor[] members = grouper.getMembers(new GroupIdentifier(null, this.path), MemberFilter.fromString(this.filter));
		if (members == null && this.members.length == 0) {
			return;
		}
		assertEquals(this.members.length, members.length);
		HashSet<String> memberSet = new HashSet<String>(members.length);
		for (MemberDescriptor member : members) {
			memberSet.add(member.getSubjectId());
		}

		// check members
		for (String member : this.members) {
			assertTrue(memberSet.contains(member));
		}
	}
}
