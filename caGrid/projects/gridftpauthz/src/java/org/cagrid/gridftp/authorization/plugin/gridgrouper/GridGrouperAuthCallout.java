package org.cagrid.gridftp.authorization.plugin.gridgrouper;

import gov.nih.nci.cagrid.gridgrouper.bean.MembershipExpression;
import gov.nih.nci.cagrid.gridgrouper.client.GridGrouperClientUtils;

import java.net.URL;
import java.util.logging.Level;

import org.cagrid.gridftp.authorization.plugin.AbstractAuthCallout;
import org.cagrid.gridftp.authorization.plugin.GridFTPTuple;
import org.globus.wsrf.encoding.SerializationException;

/**
 * 
 * This authorization plug-in implements a grid grouper check. To use the
 * plug-in, you need to create an xml configuration file and put it on
 * the classpath at RESOURCE_LOCATION. The xml file needs to use the
 * gridgrouper-config schema.
 * 
 * For complete documentation, refer to the GridFTP with Java Authorization
 * User Documentation.
 * 
 * @see #RESOURCE_LOCATION
 * 
 * @author <A HREF="MAILTO:jpermar at bmi.osu.edu">Justin Permar</A>
 * 
 * @created Mar 20, 2007
 * @version $Id: GridGrouperAuthCallout.java,v 1.1 2007/03/22 18:54:44 jpermar
 *          Exp $
 */
public class GridGrouperAuthCallout extends AbstractAuthCallout {

	/**
	 * The xml gridgrouper auth config file that must be specified on the
	 * classpath at this location.
	 */
	public static final String RESOURCE_LOCATION = "org/cagrid/gridftp/authorization/plugin/gridgrouper/gridgrouper_auth_config.xml";
	
	public static final String SCHEMA_LOCATION = "org/cagrid/gridftp/authorization/plugin/gridgrouper/gridgrouper-config.xsd";
	public static final String PARENT_SCHEMA = "org/cagrid/gridftp/authorization/plugin/gridgrouper/gridgrouper.xsd";
	
	private GridGrouperConfigurationManager _manager;

	public GridGrouperAuthCallout() throws Exception {
		// TODO test this when it throws an exception with the java_callout in
		// GridFTP
		// TODO change the config from URI type to simply string. we just want
		// user to specify paths
		super();
		URL configResource = this.getClass().getClassLoader().getResource(
				RESOURCE_LOCATION);
		_manager = new GridGrouperConfigurationManager(configResource.getPath());
		//_manager.loadConfig(configResource.getPath());
	}

	@Override
	public boolean authorizeOperation(GridFTPTuple tuple) { 
		
		boolean authorized = false;

		MembershipExpression gridGrouperExpression = _manager
				.getMostSpecificMembershipQuery(tuple.getOperation(), tuple.getURL());

		// do grid grouper check
		// String gridGrouperAuthorizeCheck =
		// _entries[0].getGrouper_expression();
		if (gridGrouperExpression != null) {
			try {
				String membershipExpression = GridGrouperConfigurationManager
						.membershipExpressionToString(gridGrouperExpression);
				_logger.fine("using grid grouper membership expression: "
						+ membershipExpression);

				if (tuple.getIdentity() != null) {
					_logger.fine("calling isMember()");
					try {
						 authorized =
						 GridGrouperClientUtils.isMember(gridGrouperExpression,
						 tuple.getIdentity());
					} catch (Exception e) {
						_logger.log(Level.WARNING,
								"Grid grouper check threw exception due to reason: "
										+ e.getMessage(), e);
					}
					_logger.fine("called isMember()");
					_logger.info("authorization check returned: " + authorized);
				}
			} catch (SerializationException e1) {
				// this is really an impossible exception to ever
				// get in this situation
				// this is because we already parsed the expression from a file
				// and since we are just writing out the same expression there
				// can't possibly be an error. essentially ignore
				e1.printStackTrace();
			}
		} else {
			_logger
					.info("No grid grouper configuration rule matched the GridFTP request: "
							+ tuple);
		}

		return authorized;
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		GridGrouperAuthCallout callout = new GridGrouperAuthCallout();
		callout
				.authorize(
						"/O=cagrid.org/OU=training/OU=caBIG User Group/OU=IdP [1]/CN=gridftp",
						"read", "ftp://irondale/my/test/dir/a");
	}

}
