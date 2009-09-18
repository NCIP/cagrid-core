package org.cagrid.gridftp.authorization.plugin.gridgrouper;

import gov.nih.nci.cagrid.common.SchemaValidationException;
import gov.nih.nci.cagrid.common.SchemaValidator;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.gridgrouper.bean.MembershipExpression;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.cagrid.gridftp.authorization.plugin.GridFTPOperation;
import org.cagrid.www._1.gridftpauthz.GridFTPGrouperConfig;
import org.cagrid.www._1.gridftpauthz.GrouperConfigRule;
import org.cagrid.www._1.gridftpauthz.GrouperConfigRuleAction;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.globus.wsrf.encoding.SerializationException;


/*
 * Not public on purpose. Only meant for internal use.
 */
class GridGrouperConfigurationManager {

	private int DEFAULT = -1;
	private static String WILDCARD = "*";

	private List<GrouperConfig> _config;


	public GridGrouperConfigurationManager(GridFTPGrouperConfig xmlConfig)
		throws GridGrouperAuthorizationConfigurationException {
		try {
			validateConfig(xmlConfig);
		} catch (Exception e) {
			String msg = "Could not load grid grouper authorization configuration file due to: " + e.getMessage();
			throw new GridGrouperAuthorizationConfigurationException(msg, e);
		}

	}


	public GridGrouperConfigurationManager(String configFilePath) throws GridGrouperAuthorizationConfigurationException {
		try {
			GridFTPGrouperConfig xmlConfig = (GridFTPGrouperConfig) Utils.deserializeDocument(configFilePath,
				GridFTPGrouperConfig.class);


			validateConfig(xmlConfig);
		} catch (Exception e) {
			String msg = "Could not load grid grouper authorization configuration file due to: " + e.getMessage();
			throw new GridGrouperAuthorizationConfigurationException(msg, e);
		}
	}


	private void validateConfig(GridFTPGrouperConfig xmlConfig) throws Exception {

		// serialize config and check it
		// TODO enable validation
		String configAsString = configToString(xmlConfig);

		File temp = File.createTempFile("foo", "");
		temp.delete();
		boolean created = temp.mkdir();

		if (created) {
			try {
				validateConfigAgainstSchema(configAsString, temp);
			} finally {
				deleteDirectory(temp);
			}
		} else {
			throw new GridGrouperAuthorizationConfigurationException("Could not create directory "
				+ temp.getAbsolutePath() + " in order to validate schemas");
		}

		GrouperConfigRule[] entries = xmlConfig.getRule();

		_config = new ArrayList<GrouperConfig>();

		// Set<String> pathEntries = new HashSet<String>();

		Set<Rule> rules = new HashSet<Rule>();

		// validate all entries
		for (GrouperConfigRule entry : entries) {
			// TODO validate the xml... specifically checking that the path is
			// valid
			// entry.entry.getPath()
			// entry.getMembershipExpression().
			// the action is valid since it must be valid according to the
			// schema,
			// which has an enum for the actions
			GrouperConfigRuleAction action = entry.getAction();
			// action is essentially validated by the schema's enum
			// specification

			String path = entry.getPath();

			// TODO Need to document that URIs use "/" only
			// don't care what it ends with. Just take off "/" if
			// it is at the end
			// of the path

			String pathSeparator = "/";
			if (path.endsWith(pathSeparator)) {
				path = path.substring(0, path.length() - 1);
				// throw new Exception("Invalid path: " + path + ". URI cannot
				// end with " + pathSeparator);
			}

			Rule rule = new Rule(entry.getAction().getValue(), path);
			if (!rules.contains(rule)) {
				rules.add(rule);
			} else {
				// rule is already found...duplicate
				String msg = "Found duplicate rule in the config file for rule: " + rule
					+ ". Note that a trailing / in a rule doesn't" + "differentiate it from one withou a trailing /";
				throw new GridGrouperAuthorizationConfigurationException(msg);
			}
			// String path = uri.getPath();
			// make sure no * in the path except possibly at the very end
			int index = path.indexOf(WILDCARD);
			if ((index != -1) && (index < path.length() - 1)) {
				// wildcard was found at a spot other than at the end
				// complain
				// TODO modify exception reason when the config type changes
				// (URI to path)
				throw new Exception("Invalid path: " + path + ". " + WILDCARD + " can only appear at the end of a URI");
			}

			// check for // anywhere in the path
			// unfortunately we can't check for this in the schema
			// http://www.w3.org/TR/2001/REC-xmlschema-2-20010502/#nt-XmlChar
			// The [, ], and \ characters are not valid character ranges;
			String duplicateSlash = "//";
			if (path.contains(duplicateSlash)) {
				throw new Exception("Invalid path: " + path + ". Path cannot contain " + duplicateSlash);
			}

			// parse the grouper expression to be sure the XML is ok
			MembershipExpression exp = entry.getMembershipExpression();
			
			GrouperConfig config = new GrouperConfig(action.getValue(), path, exp);

			System.out.println(config);
			_config.add(config);

		}

	}


	private void validateConfigAgainstSchema(String configAsString, File dir) throws SchemaValidationException,
		IOException {

		// need to package schemas in the jar and copy to tmp file
		InputStream configSchema = this.getClass().getClassLoader().getResourceAsStream(
			GridGrouperAuthCallout.SCHEMA_LOCATION);

		StringBuffer buffer = Utils.inputStreamToStringBuffer(configSchema);

		File schemaFile = new File(dir, "gridgrouper-config.xsd");

		Utils.stringBufferToFile(buffer, schemaFile.getAbsolutePath());

		// NOTE: it is critical that the schema specified on the classpath
		// has an import statement with a schemaLocation="xsd/gridgrouper.xsd"
		// since we're putting the parent schema in the xsd directory
		InputStream parentSchema = this.getClass().getClassLoader().getResourceAsStream(
			GridGrouperAuthCallout.PARENT_SCHEMA);

		buffer = Utils.inputStreamToStringBuffer(parentSchema);

		// make directory temp/xsd
		File xsdDir = new File(dir, "xsd");
		xsdDir.mkdir();

		File parentSchemaFile = new File(xsdDir, "gridgrouper.xsd");

		Utils.stringBufferToFile(buffer, parentSchemaFile.getAbsolutePath());

		SchemaValidator.validate(schemaFile.getAbsolutePath(), configAsString);

	}


	/*
	 * can only delete directories that are empty, so need this method to really
	 * delete
	 */
	private void deleteDirectory(File dir) {
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					deleteDirectory(file);
				} else {
					file.delete();
				}
			}
		}
		dir.delete();
	}


	/**
	 * Given a path, this method finds the grid grouper membership expression
	 * that best matches. That is, it finds the most specific rule from the
	 * config file that matches and returns the membership expression for it
	 * 
	 * @param action
	 *            the action specified in the GridFTP request
	 * @param path
	 *            the path specified in the GridFTP request
	 * @return the grid grouper MembershipExpression that matched, or null if no
	 *         match was found
	 */
	public MembershipExpression getMostSpecificMembershipQuery(GridFTPOperation action, String path) {
		// TODO add in action to this
		MembershipExpression expression = null;
		// the priority is an arbitrary value for how specific a match
		// the most current rule is
		// int priority = -1;

		int maxLength = DEFAULT;
		// know that URL is not same as URI but here it doesn't matter since
		// every URL is a URI
		// String uri = tuple.getURL();
		for (GrouperConfig config : _config) {
			// check that action matches this one
			// int currentPriority = -1;
			boolean matched = false;
			int length = DEFAULT;
			// if (operationMatches(tuple.getOperation(), config)) {
			if (operationMatches(action, config)) {

				// compare path to each of the paths from the configuration
				// note that for configured paths that end in WILDCARD, use the
				// parent path as the path to match against
				// 1. check if configured path ends in WILDCARD
				String configPath = config.get_path();

				String finalConfigPath = null;
				boolean wildcard = false;
				if (configPath.endsWith(WILDCARD)) {
					// 2. if so, use the path minus the WILDCARD
					int index = configPath.indexOf(WILDCARD);
					finalConfigPath = configPath.substring(0, index);
					wildcard = true;
				} else {
					// 3. if not, use the whole path
					finalConfigPath = configPath;
				}

				// TODO document that any URI like /my/test/dir is a file
				// TODO document that any URI like /my/test/dir/* is referring
				// to any file in /my/test/dir directory
				System.out.println("comparing " + path + " against " + finalConfigPath);

				// check if the given path matches the configured path
				// this includes:
				// check that the configured path is a file or a directory
				// if directory then the directory of the given path must match
				// e.g., if configured path is /tmp/* and the given path is
				// /tmp/foo then the /tmp parts must match
				// e.g., if configured path is /tmp/* and the given path is
				// /usr/tmp/foo then no match
				// e.g., if configured path is /tmp/* and the given path is
				// /usr/foo then no match
				// test for match is that the configured path minus * must
				// match the given path entirely
				// that is, remove the * and then check that the paths match
				// (checking from the beginning)
				// if file then the entire given path must match the configured
				// path
				// e.g., if configured path is /tmp/foo then the only match is
				// for given path /tmp/foo (must match exactly)

				// 4. check if the configured path is contained within the given
				// path

				if (!wildcard) {
					// must match completely
					if (path.equals(finalConfigPath)) {
						matched = true;
						// currentPriority += path.length();
						length = path.length();
					}
				} else {
					// find the length of the match
					length = finalConfigPath.length();
					if (path.length() < length) {
						// can't possibly match
						continue;
					}
					// int length = finalConfigPath.length();
					if (path.startsWith(finalConfigPath)) {
						// 5. if it is, then that means this grid grouper
						// expression applies for the given path. add it to the
						// list
						matched = true;
						System.out.println("matched");

						// we set the length correctly above
					}
				}
			}

			// 6. if it is not, then move on to next configured path
			// if new priority if higher, set priority and expression
			if (matched) {
				if ((length > maxLength) || (length >= maxLength && isOperationExactMatch(action, config))) {

					System.out.println("using new expression");
					maxLength = length;
					expression = config.get_expression();
				}
			}
		}

		return expression;
	}


	private boolean operationMatches(GridFTPOperation action, GrouperConfig config) {
		// check that this action matches the action in the config
		// either action matches exactly or the config has WILDCARD as its
		// action, in which case it matches
		if (config.get_operation().equals(WILDCARD)) {
			return true;
		} else {
			return isOperationExactMatch(action, config);
		}
	}


	private boolean isOperationExactMatch(GridFTPOperation action, GrouperConfig config) {
		return action.name().equals(config.get_operation());
	}


	public static String membershipExpressionToString(MembershipExpression exp) throws SerializationException {
		StringWriter writer = new StringWriter();
		// TODO can get this name MembershipExpression from the schema somehow?
		ObjectSerializer.serialize(writer, exp, new QName("http://cagrid.nci.nih.gov/1/GridGrouper",
			"MembershipExpression"));
		return writer.getBuffer().toString();
	}


	String configToString(GridFTPGrouperConfig objectConfig) throws SerializationException {
		StringWriter writer = new StringWriter();
		try {
			Utils.serializeObject(objectConfig, new QName("http://www.cagrid.org/1/gridftpauthz",
				"GridFTPGrouperConfig"), writer);
		} catch (Exception e) {
			throw new SerializationException("Could not serialize grouper config object");
		}
		return writer.getBuffer().toString();

	}

}
