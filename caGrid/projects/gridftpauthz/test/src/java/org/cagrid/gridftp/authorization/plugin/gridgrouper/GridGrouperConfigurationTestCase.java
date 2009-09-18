package org.cagrid.gridftp.authorization.plugin.gridgrouper;

import gov.nih.nci.cagrid.gridgrouper.bean.GroupIdentifier;
import gov.nih.nci.cagrid.gridgrouper.bean.LogicalOperator;
import gov.nih.nci.cagrid.gridgrouper.bean.MembershipExpression;
import gov.nih.nci.cagrid.gridgrouper.bean.MembershipQuery;
import gov.nih.nci.cagrid.gridgrouper.bean.MembershipStatus;

import java.net.MalformedURLException;

import junit.framework.TestCase;

import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.gridftp.authorization.plugin.GridFTPOperation;
import org.cagrid.www._1.gridftpauthz.GridFTPGrouperConfig;
import org.cagrid.www._1.gridftpauthz.GrouperConfigRule;
import org.cagrid.www._1.gridftpauthz.GrouperConfigRuleAction;


/**
 * This test case tests various configuration files against various GridFTP
 * requests, checking that the correct GridGrouper expression is used in each
 * scenario.
 * 
 * @author <A HREF="MAILTO:jpermar at bmi.osu.edu">Justin Permar</A>
 * @created Mar 30, 2007
 * @version $Id: GridGrouperConfigurationTestCase.java,v 1.1 2007/03/30 20:00:46
 *          jpermar Exp $
 */
public class GridGrouperConfigurationTestCase extends TestCase {

	/**
	 * this tests the case where the GridFTP request doesn't match any
	 * configured rule
	 * 
	 * @throws MalformedURIException
	 * @throws GridGrouperAuthorizationConfigurationException
	 * @throws MalformedURLException
	 */
	public void testNoMatchFound() throws MalformedURIException, GridGrouperAuthorizationConfigurationException,
		MalformedURLException {
		GrouperConfigRule[] entries = new GrouperConfigRule[1];
		GrouperConfigRuleAction action = GrouperConfigRuleAction.fromString("*");
		GroupIdentifier groupIdentifier = new GroupIdentifier(
			"https://training.cagrid.org:8443/wsrf/services/cagrid/GridGrouper", "demo:groupz");
		MembershipQuery query = new MembershipQuery(groupIdentifier, MembershipStatus.MEMBER_OF);
		MembershipQuery[] queries = new MembershipQuery[]{query};
		MembershipExpression expression = new MembershipExpression(LogicalOperator.AND, new MembershipExpression[0],
			queries);
		// URI uri = new URI("/my/test/dir");
		String path = "/my/test/dir";
		entries[0] = new GrouperConfigRule(action, expression, path);
		GridFTPGrouperConfig config = new GridFTPGrouperConfig(entries);

		GridGrouperConfigurationManager manager = new GridGrouperConfigurationManager(config);
		GridFTPOperation requestAction = GridFTPOperation.read;
		String requestURL = "/usr";
		// GridFTPTuple tuple = new GridFTPTuple(null, requestAction,
		// requestURL);
		MembershipExpression result = manager.getMostSpecificMembershipQuery(requestAction, requestURL);
		assertNull(result);
	}


	public void testNoMatchFound2() throws MalformedURIException, GridGrouperAuthorizationConfigurationException,
		MalformedURLException {
		GrouperConfigRule[] entries = new GrouperConfigRule[1];
		GrouperConfigRuleAction action = GrouperConfigRuleAction.fromString("*");
		GroupIdentifier groupIdentifier = new GroupIdentifier(
			"https://training.cagrid.org:8443/wsrf/services/cagrid/GridGrouper", "demo:groupz");
		MembershipQuery query = new MembershipQuery(groupIdentifier, MembershipStatus.MEMBER_OF);
		MembershipQuery[] queries = new MembershipQuery[]{query};
		MembershipExpression expression = new MembershipExpression(LogicalOperator.AND, new MembershipExpression[0],
			queries);
		// URI uri = new URI("/my/test/dir");
		String path = "/my/test/dir/*";
		entries[0] = new GrouperConfigRule(action, expression, path);
		GridFTPGrouperConfig config = new GridFTPGrouperConfig(entries);

		GridGrouperConfigurationManager manager = new GridGrouperConfigurationManager(config);
		GridFTPOperation requestAction = GridFTPOperation.read;
		String requestURL = "/m";
		// GridFTPTuple tuple = new GridFTPTuple(null, requestAction,
		// requestURL);
		MembershipExpression result = manager.getMostSpecificMembershipQuery(requestAction, requestURL);
		assertNull(result);
	}


	public void testExactMatchFoundWildcardPathHasTrailingSlash() throws MalformedURIException,
		GridGrouperAuthorizationConfigurationException, MalformedURLException {
		GrouperConfigRule[] entries = new GrouperConfigRule[1];
		GrouperConfigRuleAction action = GrouperConfigRuleAction.fromString("*");
		GroupIdentifier groupIdentifier = new GroupIdentifier(
			"https://training.cagrid.org:8443/wsrf/services/cagrid/GridGrouper", "demo:groupz");
		MembershipQuery query = new MembershipQuery(groupIdentifier, MembershipStatus.MEMBER_OF);
		MembershipQuery[] queries = new MembershipQuery[]{query};
		MembershipExpression expression = new MembershipExpression(LogicalOperator.AND, new MembershipExpression[0],
			queries);
		// URI uri = new URI("/my/test/dir");
		String path = "/my/foo/*";
		entries[0] = new GrouperConfigRule(action, expression, path);
		GridFTPGrouperConfig config = new GridFTPGrouperConfig(entries);

		GridGrouperConfigurationManager manager = new GridGrouperConfigurationManager(config);
		GridFTPOperation requestAction = GridFTPOperation.read;
		String requestURL = "/my/foo/efeefewf/";
		// GridFTPTuple tuple = new GridFTPTuple(null, requestAction,
		// requestURL);
		MembershipExpression result = manager.getMostSpecificMembershipQuery(requestAction, requestURL);
		assertEquals(expression, result);
	}


	/**
	 * This tests the case where a GridFTP request for a file matches a rule
	 * specifically for that file e.g., request for read /tmp/foo matches read
	 * /tmp/foo
	 * 
	 * @throws MalformedURIException
	 * @throws GridGrouperAuthorizationConfigurationException
	 * @throws MalformedURLException
	 */
	public void testMatchRuleForSpecificFile() throws MalformedURIException,
		GridGrouperAuthorizationConfigurationException, MalformedURLException {
		GrouperConfigRule[] entries = new GrouperConfigRule[1];
		GrouperConfigRuleAction action = GrouperConfigRuleAction.fromString("*");
		GroupIdentifier groupIdentifier = new GroupIdentifier(
			"https://training.cagrid.org:8443/wsrf/services/cagrid/GridGrouper", "demo:groupz");
		MembershipQuery query = new MembershipQuery(groupIdentifier, MembershipStatus.MEMBER_OF);
		MembershipQuery[] queries = new MembershipQuery[]{query};
		MembershipExpression expression = new MembershipExpression(LogicalOperator.AND, new MembershipExpression[0],
			queries);
		// URI uri = new URI("/my/test/file");
		String path = "/my/test/file";
		entries[0] = new GrouperConfigRule(action, expression, path);
		GridFTPGrouperConfig config = new GridFTPGrouperConfig(entries);

		GridGrouperConfigurationManager manager = new GridGrouperConfigurationManager(config);
		GridFTPOperation requestAction = GridFTPOperation.read;
		String requestURL = "/my/test/file";
		// GridFTPTuple tuple = new GridFTPTuple(null, requestAction,
		// requestURL);
		MembershipExpression result = manager.getMostSpecificMembershipQuery(requestAction, requestURL);
		assertEquals(expression, result);
	}


	/**
	 * This tests the case where a GridFTP request for a directory matches a
	 * rule specifically for that directory e.g., request for lookup /tmp
	 * matches lookup /tmp/*
	 */
	public void testMatchRuleForSpecificDirectory() throws MalformedURIException,
		GridGrouperAuthorizationConfigurationException, MalformedURLException {
		GrouperConfigRule[] entries = new GrouperConfigRule[1];
		GrouperConfigRuleAction action = GrouperConfigRuleAction.fromString("*");
		GroupIdentifier groupIdentifier = new GroupIdentifier(
			"https://training.cagrid.org:8443/wsrf/services/cagrid/GridGrouper", "demo:groupz");
		MembershipQuery query = new MembershipQuery(groupIdentifier, MembershipStatus.MEMBER_OF);
		MembershipQuery[] queries = new MembershipQuery[]{query};
		MembershipExpression expression = new MembershipExpression(LogicalOperator.AND, new MembershipExpression[0],
			queries);
		// URI uri = new URI("/usr/local/*");
		String path = "/usr/local/*";
		entries[0] = new GrouperConfigRule(action, expression, path);
		GridFTPGrouperConfig config = new GridFTPGrouperConfig(entries);

		GridGrouperConfigurationManager manager = new GridGrouperConfigurationManager(config);
		GridFTPOperation requestAction = GridFTPOperation.read;
		String requestURL = "/usr/local/myfile";
		// GridFTPTuple tuple = new GridFTPTuple(null, requestAction,
		// requestURL);
		MembershipExpression result = manager.getMostSpecificMembershipQuery(requestAction, requestURL);
		assertEquals(expression, result);
	}


	/**
	 * This tests the case where a GridFTP request for a file matches a rule for
	 * a directory up from the file e.g., request for /tmp/foo matches rule /*
	 */
	public void testMatchRuleForGeneralDirectory() throws MalformedURIException,
		GridGrouperAuthorizationConfigurationException, MalformedURLException {
		GrouperConfigRule[] entries = new GrouperConfigRule[1];
		GrouperConfigRuleAction action = GrouperConfigRuleAction.fromString("*");
		GroupIdentifier groupIdentifier = new GroupIdentifier(
			"https://training.cagrid.org:8443/wsrf/services/cagrid/GridGrouper", "demo:groupz");
		MembershipQuery query = new MembershipQuery(groupIdentifier, MembershipStatus.MEMBER_OF);
		MembershipQuery[] queries = new MembershipQuery[]{query};
		MembershipExpression expression = new MembershipExpression(LogicalOperator.AND, new MembershipExpression[0],
			queries);
		// URI uri = new URI("/usr/*");
		String path = "/usr/*";
		entries[0] = new GrouperConfigRule(action, expression, path);
		GridFTPGrouperConfig config = new GridFTPGrouperConfig(entries);

		GridGrouperConfigurationManager manager = new GridGrouperConfigurationManager(config);
		GridFTPOperation requestAction = GridFTPOperation.read;
		String requestURL = "/usr/local/myfile";
		// GridFTPTuple tuple = new GridFTPTuple(null, requestAction,
		// requestURL);
		MembershipExpression result = manager.getMostSpecificMembershipQuery(requestAction, requestURL);
		assertEquals(expression, result);

	}


	/**
	 * This tests the case where a GridFTP request for a file doesn't match a
	 * rule for a file that is up from the requested file e.g., request for
	 * /tmp/foo doesn't match rule /tmp
	 */
	public void testNoMatchRuleForGeneralFile() throws MalformedURIException,
		GridGrouperAuthorizationConfigurationException, MalformedURLException {
		GrouperConfigRule[] entries = new GrouperConfigRule[1];
		GrouperConfigRuleAction action = GrouperConfigRuleAction.fromString("*");
		GroupIdentifier groupIdentifier = new GroupIdentifier(
			"https://training.cagrid.org:8443/wsrf/services/cagrid/GridGrouper", "demo:groupz");
		MembershipQuery query = new MembershipQuery(groupIdentifier, MembershipStatus.MEMBER_OF);
		MembershipQuery[] queries = new MembershipQuery[]{query};
		MembershipExpression expression = new MembershipExpression(LogicalOperator.AND, new MembershipExpression[0],
			queries);
		// URI uri = new URI("/foo");
		String path = "/foo";
		entries[0] = new GrouperConfigRule(action, expression, path);
		GridFTPGrouperConfig config = new GridFTPGrouperConfig(entries);

		GridGrouperConfigurationManager manager = new GridGrouperConfigurationManager(config);
		GridFTPOperation requestAction = GridFTPOperation.read;
		String requestURL = "/foo/bar";
		// GridFTPTuple tuple = new GridFTPTuple(null, requestAction,
		// requestURL);
		MembershipExpression result = manager.getMostSpecificMembershipQuery(requestAction, requestURL);
		assertNull(result);

	}


	/**
	 * This tests the cae where a specific file is requested that is conained
	 * within a directory that is up from the file e.g., request for read
	 * /usr/local/foo matches read /usr/*
	 */
	public void testMatchRuleForSpecificFileContainedWithinDirectory() throws MalformedURIException,
		GridGrouperAuthorizationConfigurationException, MalformedURLException {
		GrouperConfigRule[] entries = new GrouperConfigRule[1];
		GrouperConfigRuleAction action = GrouperConfigRuleAction.fromString("*");
		GroupIdentifier groupIdentifier = new GroupIdentifier(
			"https://training.cagrid.org:8443/wsrf/services/cagrid/GridGrouper", "demo:groupz");
		MembershipQuery query = new MembershipQuery(groupIdentifier, MembershipStatus.MEMBER_OF);
		MembershipQuery[] queries = new MembershipQuery[]{query};
		MembershipExpression expression = new MembershipExpression(LogicalOperator.AND, new MembershipExpression[0],
			queries);
		// URI uri = new URI("/usr/*");
		String path = "/usr/*";
		entries[0] = new GrouperConfigRule(action, expression, path);
		GridFTPGrouperConfig config = new GridFTPGrouperConfig(entries);

		GridGrouperConfigurationManager manager = new GridGrouperConfigurationManager(config);
		GridFTPOperation requestAction = GridFTPOperation.read;
		String requestURL = "/usr/local/foo";
		// GridFTPTuple tuple = new GridFTPTuple(null, requestAction,
		// requestURL);
		MembershipExpression result = manager.getMostSpecificMembershipQuery(requestAction, requestURL);
		assertEquals(expression, result);

	}


	public void testNoMatchForDifferingActionType() throws MalformedURIException,
		GridGrouperAuthorizationConfigurationException, MalformedURLException {
		GrouperConfigRule[] entries = new GrouperConfigRule[1];
		GrouperConfigRuleAction action = GrouperConfigRuleAction.fromString("write");
		GroupIdentifier groupIdentifier = new GroupIdentifier(
			"https://training.cagrid.org:8443/wsrf/services/cagrid/GridGrouper", "demo:groupz");
		MembershipQuery query = new MembershipQuery(groupIdentifier, MembershipStatus.MEMBER_OF);
		MembershipQuery[] queries = new MembershipQuery[]{query};
		MembershipExpression expression = new MembershipExpression(LogicalOperator.AND, new MembershipExpression[0],
			queries);
		// URI uri = new URI("/foo/*");
		String path = "/foo/*";
		entries[0] = new GrouperConfigRule(action, expression, path);
		GridFTPGrouperConfig config = new GridFTPGrouperConfig(entries);

		GridGrouperConfigurationManager manager = new GridGrouperConfigurationManager(config);
		GridFTPOperation requestAction = GridFTPOperation.read;
		String requestURL = "/foo/bar";
		// GridFTPTuple tuple = new GridFTPTuple(null, requestAction,
		// requestURL);
		MembershipExpression result = manager.getMostSpecificMembershipQuery(requestAction, requestURL);
		assertNull(result);

	}


	public void testInvalidRuleActionInvalid() throws MalformedURIException,
		GridGrouperAuthorizationConfigurationException, MalformedURLException {
		try {
			GrouperConfigRule[] entries = new GrouperConfigRule[1];
			String actionName = "yack";
			GrouperConfigRuleAction action = GrouperConfigRuleAction.fromString(actionName);
			GroupIdentifier groupIdentifier = new GroupIdentifier(
				"https://training.cagrid.org:8443/wsrf/services/cagrid/GridGrouper", "demo:groupz");
			MembershipQuery query = new MembershipQuery(groupIdentifier, MembershipStatus.MEMBER_OF);
			MembershipQuery[] queries = new MembershipQuery[]{query};
			MembershipExpression expression = new MembershipExpression(LogicalOperator.AND,
				new MembershipExpression[0], queries);
			// URI uri = new URI("/foo/*");
			String path = "/foo/*";
			entries[0] = new GrouperConfigRule(action, expression, path);
			GridFTPGrouperConfig config = new GridFTPGrouperConfig(entries);

			GridGrouperConfigurationManager manager = new GridGrouperConfigurationManager(config);
			GridFTPOperation requestAction = GridFTPOperation.read;
			String requestURL = "/foo/bar";
			// GridFTPTuple tuple = new GridFTPTuple(null, requestAction,
			// requestURL);
			MembershipExpression result = manager.getMostSpecificMembershipQuery(requestAction, requestURL);
			fail("Expected to fail due to invalid action " + actionName + " specified in config");
		} catch (Exception e) {
			assertTrue(true);
		}

	}


	public void testInvalidRuleInvalidURIBadFormatWildcardNotAtEnd() throws MalformedURIException,
		GridGrouperAuthorizationConfigurationException, MalformedURLException {
			GrouperConfigRule[] entries = new GrouperConfigRule[1];
			String actionName = "read";
			GrouperConfigRuleAction action = GrouperConfigRuleAction.fromString(actionName);
			GroupIdentifier groupIdentifier = new GroupIdentifier(
				"https://training.cagrid.org:8443/wsrf/services/cagrid/GridGrouper", "demo:groupz");
			MembershipQuery query = new MembershipQuery(groupIdentifier, MembershipStatus.MEMBER_OF);
			MembershipQuery[] queries = new MembershipQuery[]{query};
			MembershipExpression expression = new MembershipExpression(LogicalOperator.AND,
				new MembershipExpression[0], queries);
			// URI uri = new URI("/*foo/*");
			String path = "/*foo/*";
			entries[0] = new GrouperConfigRule(action, expression, path);
			GridFTPGrouperConfig config = new GridFTPGrouperConfig(entries);
		try {
			GridGrouperConfigurationManager manager = new GridGrouperConfigurationManager(config);
			GridFTPOperation requestAction = GridFTPOperation.read;
			String requestURL = "/foo/bar";
			// GridFTPTuple tuple = new GridFTPTuple(null, requestAction,
			// requestURL);
			MembershipExpression result = manager.getMostSpecificMembershipQuery(requestAction, requestURL);
			fail("Expected to fail schema validation due to wildcard found in the middle of the path: " + path);
		} catch (GridGrouperAuthorizationConfigurationException e) {
			//expected
		}
	}


	public void testInvalidRuleInvalidURIBadFormatMultipleWildcardsAtEnd() throws MalformedURIException,
		GridGrouperAuthorizationConfigurationException, MalformedURLException {
			GrouperConfigRule[] entries = new GrouperConfigRule[1];
			String actionName = "read";
			GrouperConfigRuleAction action = GrouperConfigRuleAction.fromString(actionName);
			GroupIdentifier groupIdentifier = new GroupIdentifier(
				"https://training.cagrid.org:8443/wsrf/services/cagrid/GridGrouper", "demo:groupz");
			MembershipQuery query = new MembershipQuery(groupIdentifier, MembershipStatus.MEMBER_OF);
			MembershipQuery[] queries = new MembershipQuery[]{query};
			MembershipExpression expression = new MembershipExpression(LogicalOperator.AND,
				new MembershipExpression[0], queries);
			// URI uri = new URI("/foo/**");
			String path = "/foo/**";
			entries[0] = new GrouperConfigRule(action, expression, path);
			GridFTPGrouperConfig config = new GridFTPGrouperConfig(entries);

			try {
			GridGrouperConfigurationManager manager = new GridGrouperConfigurationManager(config);
			GridFTPOperation requestAction = GridFTPOperation.read;
			String requestURL = "/foo/bar";
			// GridFTPTuple tuple = new GridFTPTuple(null, requestAction,
			// requestURL);
			MembershipExpression result = manager.getMostSpecificMembershipQuery(requestAction, requestURL);
			fail("Expected to fail schema validation due to * at place other than end of the configured URI");
		} catch (GridGrouperAuthorizationConfigurationException e) {
			assertTrue(true);
		}
	}


	public void testRuleOverrideOnlyGeneralMatches() throws MalformedURIException,
		GridGrouperAuthorizationConfigurationException, MalformedURLException {
		// entry 1
		String actionName = "read";
		GrouperConfigRuleAction action = GrouperConfigRuleAction.fromString(actionName);
		GroupIdentifier groupIdentifier = new GroupIdentifier(
			"https://training.cagrid.org:8443/wsrf/services/cagrid/GridGrouper", "demo:groupz");
		MembershipQuery query = new MembershipQuery(groupIdentifier, MembershipStatus.MEMBER_OF);
		MembershipQuery[] queries = new MembershipQuery[]{query};
		MembershipExpression expression1 = new MembershipExpression(LogicalOperator.AND, new MembershipExpression[0],
			queries);
		// URI uri = new URI("/tmp/*");
		String path = "/tmp/*";
		GrouperConfigRule entry1 = new GrouperConfigRule(action, expression1, path);

		// entry 2
		actionName = "read";
		action = GrouperConfigRuleAction.fromString(actionName);
		groupIdentifier = new GroupIdentifier("https://training.cagrid.org:8443/wsrf/services/cagrid/GridGrouper",
			"demo:groupz");
		query = new MembershipQuery(groupIdentifier, MembershipStatus.MEMBER_OF);
		queries = new MembershipQuery[]{query};
		MembershipExpression expression2 = new MembershipExpression(LogicalOperator.AND, new MembershipExpression[0],
			queries);
		// uri = new URI("/tmp/my/test/file2");
		path = "/tmp/my/test/file2";
		GrouperConfigRule entry2 = new GrouperConfigRule(action, expression2, path);

		GrouperConfigRule[] entries = new GrouperConfigRule[]{entry1, entry2};

		GridFTPGrouperConfig config = new GridFTPGrouperConfig(entries);

		// request
		GridGrouperConfigurationManager manager = new GridGrouperConfigurationManager(config);
		GridFTPOperation requestAction = GridFTPOperation.read;
		String requestURL = "/tmp/my/test/file";
		// GridFTPTuple tuple = new GridFTPTuple(null, requestAction,
		// requestURL);

		// check that only entry 1 matches
		MembershipExpression result = manager.getMostSpecificMembershipQuery(requestAction, requestURL);
		assertEquals(expression1, result);
	}


	public void testRuleOverrideOnlySpecificMatches() throws MalformedURIException,
		GridGrouperAuthorizationConfigurationException, MalformedURLException {
		// entry 1
		String actionName = "read";
		GrouperConfigRuleAction action = GrouperConfigRuleAction.fromString(actionName);
		GroupIdentifier groupIdentifier = new GroupIdentifier(
			"https://training.cagrid.org:8443/wsrf/services/cagrid/GridGrouper", "demo:groupz");
		MembershipQuery query = new MembershipQuery(groupIdentifier, MembershipStatus.MEMBER_OF);
		MembershipQuery[] queries = new MembershipQuery[]{query};
		MembershipExpression expression1 = new MembershipExpression(LogicalOperator.AND, new MembershipExpression[0],
			queries);
		// URI uri = new URI("/tmp/*");
		String path = "/tmp/*";
		GrouperConfigRule entry1 = new GrouperConfigRule(action, expression1, path);

		// entry 2
		actionName = "read";
		action = GrouperConfigRuleAction.fromString(actionName);
		groupIdentifier = new GroupIdentifier("https://training.cagrid.org:8443/wsrf/services/cagrid/GridGrouper",
			"demo:groupz");
		query = new MembershipQuery(groupIdentifier, MembershipStatus.MEMBER_OF);
		queries = new MembershipQuery[]{query};
		MembershipExpression expression2 = new MembershipExpression(LogicalOperator.AND, new MembershipExpression[0],
			queries);
		// uri = new URI("/tmp/my/test/file2");
		path = "/tmp/my/test/file2";
		GrouperConfigRule entry2 = new GrouperConfigRule(action, expression2, path);

		GrouperConfigRule[] entries = new GrouperConfigRule[]{entry1, entry2};

		GridFTPGrouperConfig config = new GridFTPGrouperConfig(entries);

		// request
		GridGrouperConfigurationManager manager = new GridGrouperConfigurationManager(config);
		GridFTPOperation requestAction = GridFTPOperation.read;
		String requestURL = "/tmp/my/test/file2";
		// GridFTPTuple tuple = new GridFTPTuple(null, requestAction,
		// requestURL);

		// check that only entry 2 matches
		MembershipExpression result = manager.getMostSpecificMembershipQuery(requestAction, requestURL);
		assertEquals(expression2, result);
	}


	public void testNoMatchWrongAction() throws MalformedURIException, GridGrouperAuthorizationConfigurationException,
		MalformedURLException {
		// entry 1
		String actionName = "write";
		GrouperConfigRuleAction action = GrouperConfigRuleAction.fromString(actionName);
		GroupIdentifier groupIdentifier = new GroupIdentifier(
			"https://training.cagrid.org:8443/wsrf/services/cagrid/GridGrouper", "demo:groupz");
		MembershipQuery query = new MembershipQuery(groupIdentifier, MembershipStatus.MEMBER_OF);
		MembershipQuery[] queries = new MembershipQuery[]{query};
		MembershipExpression expression1 = new MembershipExpression(LogicalOperator.AND, new MembershipExpression[0],
			queries);
		// URI uri = new URI("/tmp/*");
		String path = "/tmp/*";
		GrouperConfigRule entry1 = new GrouperConfigRule(action, expression1, path);

		// entry 2
		actionName = "write";
		action = GrouperConfigRuleAction.fromString(actionName);
		groupIdentifier = new GroupIdentifier("https://training.cagrid.org:8443/wsrf/services/cagrid/GridGrouper",
			"demo:groupz");
		query = new MembershipQuery(groupIdentifier, MembershipStatus.MEMBER_OF);
		queries = new MembershipQuery[]{query};
		MembershipExpression expression2 = new MembershipExpression(LogicalOperator.AND, new MembershipExpression[0],
			queries);
		// uri = new URI("/tmp/my/test/file2");
		path = "/tmp/my/test/file2";
		GrouperConfigRule entry2 = new GrouperConfigRule(action, expression2, path);

		GrouperConfigRule[] entries = new GrouperConfigRule[]{entry1, entry2};

		GridFTPGrouperConfig config = new GridFTPGrouperConfig(entries);

		// request
		GridGrouperConfigurationManager manager = new GridGrouperConfigurationManager(config);
		GridFTPOperation requestAction = GridFTPOperation.read;
		String requestURL = "/tmp/my/test/file2";
		// GridFTPTuple tuple = new GridFTPTuple(null, requestAction,
		// requestURL);

		// check that only entry 1 matches
		MembershipExpression result = manager.getMostSpecificMembershipQuery(requestAction, requestURL);
		assertNull(result);
	}


	public void testSpecificActionOverridesGeneralAction() throws MalformedURIException,
		GridGrouperAuthorizationConfigurationException, MalformedURLException {
		// entry 1
		String actionName = "read";
		GrouperConfigRuleAction action = GrouperConfigRuleAction.fromString(actionName);
		GroupIdentifier groupIdentifier = new GroupIdentifier(
			"https://training.cagrid.org:8443/wsrf/services/cagrid/GridGrouper", "demo:groupz");
		MembershipQuery query = new MembershipQuery(groupIdentifier, MembershipStatus.MEMBER_OF);
		MembershipQuery[] queries = new MembershipQuery[]{query};
		MembershipExpression expression1 = new MembershipExpression(LogicalOperator.AND, new MembershipExpression[0],
			queries);
		// URI uri = new URI("/tmp/my/test/file2");
		String path = "/tmp/my/test/file2";
		GrouperConfigRule entry1 = new GrouperConfigRule(action, expression1, path);

		// entry 2
		actionName = "*";
		action = GrouperConfigRuleAction.fromString(actionName);
		groupIdentifier = new GroupIdentifier("https://training.cagrid.org:8443/wsrf/services/cagrid/GridGrouper",
			"demo:groupz");
		query = new MembershipQuery(groupIdentifier, MembershipStatus.MEMBER_OF);
		queries = new MembershipQuery[]{query};
		MembershipExpression expression2 = new MembershipExpression(LogicalOperator.AND, new MembershipExpression[0],
			queries);
		// uri = new URI("/tmp/my/test/file2");
		path = "/tmp/my/test/file2";
		GrouperConfigRule entry2 = new GrouperConfigRule(action, expression2, path);

		GrouperConfigRule[] entries = new GrouperConfigRule[]{entry1, entry2};

		GridFTPGrouperConfig config = new GridFTPGrouperConfig(entries);

		// request
		GridGrouperConfigurationManager manager = new GridGrouperConfigurationManager(config);
		GridFTPOperation requestAction = GridFTPOperation.read;
		String requestURL = "/tmp/my/test/file2";
		// GridFTPTuple tuple = new GridFTPTuple(null, requestAction,
		// requestURL);

		// check that only entry 1 matches
		MembershipExpression result = manager.getMostSpecificMembershipQuery(requestAction, requestURL);
		assertEquals(expression1, result);
	}


	// TODO check for "duplicate" entries in the config...
	// same action and similar path. e.g., with or without trailing / should
	// still count as duplicate
	public void testDuplicateEntriesWhenExact() throws MalformedURIException,
		GridGrouperAuthorizationConfigurationException, MalformedURLException {
			// entry 1
			String actionName = "read";
			GrouperConfigRuleAction action = GrouperConfigRuleAction.fromString(actionName);
			GroupIdentifier groupIdentifier = new GroupIdentifier(
				"https://training.cagrid.org:8443/wsrf/services/cagrid/GridGrouper", "demo:groupz");
			MembershipQuery query = new MembershipQuery(groupIdentifier, MembershipStatus.MEMBER_OF);
			MembershipQuery[] queries = new MembershipQuery[]{query};
			MembershipExpression expression1 = new MembershipExpression(LogicalOperator.AND,
				new MembershipExpression[0], queries);
			// URI uri = new URI("/tmp/my/test/file2");
			String path = "/tmp/my/test/file2";
			GrouperConfigRule entry1 = new GrouperConfigRule(action, expression1, path);

			// entry 2
			actionName = "read";
			action = GrouperConfigRuleAction.fromString(actionName);
			groupIdentifier = new GroupIdentifier("https://training.cagrid.org:8443/wsrf/services/cagrid/GridGrouper",
				"demo:groupz");
			query = new MembershipQuery(groupIdentifier, MembershipStatus.MEMBER_OF);
			queries = new MembershipQuery[]{query};
			MembershipExpression expression2 = new MembershipExpression(LogicalOperator.AND,
				new MembershipExpression[0], queries);
			// uri = new URI("/tmp/my/test/file2");
			path = "/tmp/my/test/file2";
			GrouperConfigRule entry2 = new GrouperConfigRule(action, expression2, path);

			GrouperConfigRule[] entries = new GrouperConfigRule[]{entry1, entry2};

			GridFTPGrouperConfig config = new GridFTPGrouperConfig(entries);

			// request
			try {
			GridGrouperConfigurationManager manager = new GridGrouperConfigurationManager(config);
			fail("Did not catch expected GridGrouperAuthorizationConfigurationException for duplicate configuration entries");
		} catch (GridGrouperAuthorizationConfigurationException e) {
			//expected
		}
		/*
		 * GridFTPOperation requestAction = GridFTPOperation.read; String
		 * requestURL = "/tmp/my/test/file2"; //GridFTPTuple tuple = new
		 * GridFTPTuple(null, requestAction, requestURL); // check that only
		 * entry 1 matches MembershipExpression result =
		 * manager.getMostSpecificMembershipQuery(requestAction, requestURL);
		 * assertEquals(expression1, result);
		 */
	}


	public void testDuplicateEntriesWhenOnlyTrailingSlashDiffers() throws MalformedURIException,
		GridGrouperAuthorizationConfigurationException, MalformedURLException {
		try {
			// entry 1
			String actionName = "read";
			GrouperConfigRuleAction action = GrouperConfigRuleAction.fromString(actionName);
			GroupIdentifier groupIdentifier = new GroupIdentifier(
				"https://training.cagrid.org:8443/wsrf/services/cagrid/GridGrouper", "demo:groupz");
			MembershipQuery query = new MembershipQuery(groupIdentifier, MembershipStatus.MEMBER_OF);
			MembershipQuery[] queries = new MembershipQuery[]{query};
			MembershipExpression expression1 = new MembershipExpression(LogicalOperator.AND,
				new MembershipExpression[0], queries);
			// URI uri = new URI("/tmp/my/test/file2");
			String path = "/tmp/my/test/file2/";
			GrouperConfigRule entry1 = new GrouperConfigRule(action, expression1, path);

			// entry 2
			actionName = "read";
			action = GrouperConfigRuleAction.fromString(actionName);
			groupIdentifier = new GroupIdentifier("https://training.cagrid.org:8443/wsrf/services/cagrid/GridGrouper",
				"demo:groupz");
			query = new MembershipQuery(groupIdentifier, MembershipStatus.MEMBER_OF);
			queries = new MembershipQuery[]{query};
			MembershipExpression expression2 = new MembershipExpression(LogicalOperator.AND,
				new MembershipExpression[0], queries);
			// uri = new URI("/tmp/my/test/file2");
			path = "/tmp/my/test/file2";
			GrouperConfigRule entry2 = new GrouperConfigRule(action, expression2, path);

			GrouperConfigRule[] entries = new GrouperConfigRule[]{entry1, entry2};

			GridFTPGrouperConfig config = new GridFTPGrouperConfig(entries);

			// request
			GridGrouperConfigurationManager manager = new GridGrouperConfigurationManager(config);
			fail("Did not catch expected GridGrouperAuthorizationConfigurationException for duplicate configuration entries");
		} catch (GridGrouperAuthorizationConfigurationException e) {
			assertTrue(true);
		}
		/*
		 * GridFTPOperation requestAction = GridFTPOperation.read; String
		 * requestURL = "/tmp/my/test/file2"; //GridFTPTuple tuple = new
		 * GridFTPTuple(null, requestAction, requestURL); // check that only
		 * entry 1 matches MembershipExpression result =
		 * manager.getMostSpecificMembershipQuery(requestAction, requestURL);
		 * assertEquals(expression1, result);
		 */
	}


	public void testDoubleSlashInPathFails() throws MalformedURIException,
		GridGrouperAuthorizationConfigurationException, MalformedURLException {
			GrouperConfigRule[] entries = new GrouperConfigRule[1];
			GrouperConfigRuleAction action = GrouperConfigRuleAction.fromString("*");
			GroupIdentifier groupIdentifier = new GroupIdentifier(
				"https://training.cagrid.org:8443/wsrf/services/cagrid/GridGrouper", "demo:groupz");
			MembershipQuery query = new MembershipQuery(groupIdentifier, MembershipStatus.MEMBER_OF);
			MembershipQuery[] queries = new MembershipQuery[]{query};
			MembershipExpression expression = new MembershipExpression(LogicalOperator.AND,
				new MembershipExpression[0], queries);
			// URI uri = new URI("/my/test/dir");
			String path = "//my/test/dir/*";
			entries[0] = new GrouperConfigRule(action, expression, path);
			GridFTPGrouperConfig config = new GridFTPGrouperConfig(entries);

		try {
			GridGrouperConfigurationManager manager = new GridGrouperConfigurationManager(config);
			GridFTPOperation requestAction = GridFTPOperation.read;
			String requestURL = "/m";
			// GridFTPTuple tuple = new GridFTPTuple(null, requestAction,
			// requestURL);
			MembershipExpression result = manager.getMostSpecificMembershipQuery(requestAction, requestURL);
			fail("Expected to throw GridGrouperAuthorizationConfigurationException due to two // in the path: " + path);
		} catch (GridGrouperAuthorizationConfigurationException e) {
			assertTrue(true);
		}
	}


	public void testShortRequestPathLongerPathInRule() throws MalformedURIException,
		GridGrouperAuthorizationConfigurationException, MalformedURLException {
		GrouperConfigRule[] entries = new GrouperConfigRule[1];
		GrouperConfigRuleAction action = GrouperConfigRuleAction.fromString("*");
		GroupIdentifier groupIdentifier = new GroupIdentifier(
			"https://training.cagrid.org:8443/wsrf/services/cagrid/GridGrouper", "demo:groupz");
		MembershipQuery query = new MembershipQuery(groupIdentifier, MembershipStatus.MEMBER_OF);
		MembershipQuery[] queries = new MembershipQuery[]{query};
		MembershipExpression expression = new MembershipExpression(LogicalOperator.AND, new MembershipExpression[0],
			queries);
		// URI uri = new URI("/my/test/dir");
		String path = "/my/test/dir/*";
		entries[0] = new GrouperConfigRule(action, expression, path);
		GridFTPGrouperConfig config = new GridFTPGrouperConfig(entries);

		GridGrouperConfigurationManager manager = new GridGrouperConfigurationManager(config);
		GridFTPOperation requestAction = GridFTPOperation.read;
		String requestURL = "/m";
		// GridFTPTuple tuple = new GridFTPTuple(null, requestAction,
		// requestURL);
		MembershipExpression result = manager.getMostSpecificMembershipQuery(requestAction, requestURL);
		assertNull(result);
	}


	public void testWherePathDiffersByOne() throws MalformedURIException,
		GridGrouperAuthorizationConfigurationException, MalformedURLException {
		// entry 1
		String actionName = "read";
		GrouperConfigRuleAction action = GrouperConfigRuleAction.fromString(actionName);
		GroupIdentifier groupIdentifier = new GroupIdentifier(
			"https://training.cagrid.org:8443/wsrf/services/cagrid/GridGrouper", "demo:groupz");
		MembershipQuery query = new MembershipQuery(groupIdentifier, MembershipStatus.MEMBER_OF);
		MembershipQuery[] queries = new MembershipQuery[]{query};
		MembershipExpression expression1 = new MembershipExpression(LogicalOperator.AND, new MembershipExpression[0],
			queries);
		// URI uri = new URI("/tmp/my/test/file2");
		String path = "/tmp/ab";
		GrouperConfigRule entry1 = new GrouperConfigRule(action, expression1, path);

		// entry 2
		actionName = "*";
		action = GrouperConfigRuleAction.fromString(actionName);
		groupIdentifier = new GroupIdentifier("https://training.cagrid.org:8443/wsrf/services/cagrid/GridGrouper",
			"demo:groupz");
		query = new MembershipQuery(groupIdentifier, MembershipStatus.MEMBER_OF);
		queries = new MembershipQuery[]{query};
		MembershipExpression expression2 = new MembershipExpression(LogicalOperator.AND, new MembershipExpression[0],
			queries);
		// uri = new URI("/tmp/my/test/file2");
		path = "/tmp/abc";
		GrouperConfigRule entry2 = new GrouperConfigRule(action, expression2, path);

		GrouperConfigRule[] entries = new GrouperConfigRule[]{entry1, entry2};

		GridFTPGrouperConfig config = new GridFTPGrouperConfig(entries);

		GridGrouperConfigurationManager manager = new GridGrouperConfigurationManager(config);
		GridFTPOperation requestAction = GridFTPOperation.read;
		String requestURL = "/tmp/abc";
		// GridFTPTuple tuple = new GridFTPTuple(null, requestAction,
		// requestURL);
		MembershipExpression result = manager.getMostSpecificMembershipQuery(requestAction, requestURL);
		assertEquals(expression2, result);
	}

}
