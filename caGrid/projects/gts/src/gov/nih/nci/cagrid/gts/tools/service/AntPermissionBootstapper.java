package gov.nih.nci.cagrid.gts.tools.service;

import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.gts.bean.Permission;
import gov.nih.nci.cagrid.gts.bean.Role;
import gov.nih.nci.cagrid.gts.common.MySQLDatabase;
import gov.nih.nci.cagrid.gts.service.Configuration;
import gov.nih.nci.cagrid.gts.service.PermissionManager;
import gov.nih.nci.cagrid.gts.service.SimpleResourceManager;
import gov.nih.nci.cagrid.gts.service.db.mysql.MySQLManager;
import gov.nih.nci.cagrid.gts.stubs.types.GTSInternalFault;
import gov.nih.nci.cagrid.gts.stubs.types.IllegalPermissionFault;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: TrustedAuthorityManager.java,v 1.1 2006/03/08 19:48:46 langella
 *          Exp $
 */

public class AntPermissionBootstapper {

	private PermissionManager pm;


	public AntPermissionBootstapper(Configuration conf) {
		pm = new PermissionManager(new MySQLManager(new MySQLDatabase(conf.getConnectionManager(), conf
			.getGTSInternalId())));
	}


	public void addAdminUser(String gridIdentity) throws GTSInternalFault, IllegalPermissionFault {
		Permission p = new Permission();
		p.setGridIdentity(gridIdentity);
		p.setRole(Role.TrustServiceAdmin);
		pm.addPermission(p);
	}


	public static void usage() {
		System.err.println(AntPermissionBootstapper.class.getName() + " Usage:");
		System.err.println();
		System.err.println("java " + AntPermissionBootstapper.class.getName() + " GTS_CONFIGURATION_FILE");
	}


	public static void main(String[] args) {
		if (args.length != 2) {
			usage();
			System.exit(1);
		}
		Configuration conf = null;
		try {
			SimpleResourceManager srm = new SimpleResourceManager(args[0]);
			conf = (Configuration) srm.getResource(Configuration.RESOURCE);
		} catch (Exception e) {
			System.out.println("Error loading the GTS config file, " + args[0]);
			e.printStackTrace();
			System.exit(1);
		}
		try {
			AntPermissionBootstapper util = new AntPermissionBootstapper(conf);
			String gridId = args[1];
			util.addAdminUser(gridId);
			System.out.println("The user " + gridId + " was succesfully added as an administrator of the GTS ("
				+ conf.getGTSInternalId() + ")");
		} catch (Exception e) {
			FaultUtil.printFault(e);
			System.exit(1);
		}
	}

}
