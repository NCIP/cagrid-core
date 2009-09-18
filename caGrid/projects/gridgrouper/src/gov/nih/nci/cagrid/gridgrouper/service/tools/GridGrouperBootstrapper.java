package gov.nih.nci.cagrid.gridgrouper.service.tools;

import edu.internet2.middleware.grouper.RegistryReset;
import edu.internet2.middleware.subject.Subject;
import gov.nih.nci.cagrid.common.IOUtils;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.gridgrouper.service.GridGrouper;
import gov.nih.nci.cagrid.gridgrouper.subject.GridSourceAdapter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class GridGrouperBootstrapper {

	public static final String ADD_ADMIN_OPT = "a";

	public static final String ADD_ADMIN_OPT_FULL = "addAdmin";

	public static final String USER_ID_OPT = "u";

	public static final String USER_ID_OPT_FULL = "userId";

	public static final String RESET_REGISTRY_OPT = "r";

	public static final String RESET_REGISTRY_OPT_FULL = "reset";

	public static final String HELP_OPT = "h";

	public static final String HELP_OPT_FULL = "help";


	public static void addAdminMember(String memberId) {
		try {
			if (memberId == null) {
				memberId = IOUtils.readLine("Enter User Id", true);
			}
			GridSourceAdapter guss = new GridSourceAdapter("grid", "Grid Grouper: Grid Source Adapter");
			Subject admin = guss.getSubject(memberId);
			GridGrouper gg = new GridGrouper();
			gg.getAdminGroup().addMember(admin);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void resetRegistry() {
		try {
			RegistryReset.reset();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void main(String[] args) {
		Options options = new Options();
		Option help = new Option(HELP_OPT, HELP_OPT_FULL, false, "Prints this message.");
		Option addAdmin = new Option(ADD_ADMIN_OPT, ADD_ADMIN_OPT_FULL, false,
			"Specifies to add a grid grouper administrator.");
		addAdmin.setRequired(false);
		Option userId = new Option(USER_ID_OPT, USER_ID_OPT_FULL, true,
			"The user id of the user to add as a grid grouper administrator.");
		userId.setRequired(false);
		Option resetRegisty = new Option(RESET_REGISTRY_OPT, RESET_REGISTRY_OPT_FULL, false,
			"Resets the Grid Grouper registry, this will remove all stems and groups.");
		userId.setRequired(false);
		options.addOption(help);
		options.addOption(addAdmin);
		options.addOption(userId);
		options.addOption(resetRegisty);

		try {
			CommandLineParser parser = new PosixParser();
			CommandLine line = parser.parse(options, args);

			if (line.hasOption(HELP_OPT)) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp(GridGrouperBootstrapper.class.getName(), options);
				System.exit(0);
			} else {
				boolean printMenu = true;
				if (line.hasOption(ADD_ADMIN_OPT)) {
					printMenu = false;
					addAdminMember(Utils.clean(line.getOptionValue(USER_ID_OPT)));
				}
				if (line.hasOption(RESET_REGISTRY_OPT)) {
					printMenu = false;
					resetRegistry();
				}
				if (printMenu) {
					System.out.println("*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*");
					System.out.println("*                   Grid Grouper Bootstapper                        *");
					System.out.println("*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*");
					System.out.println();
					System.out.println("1) Add a administrative user to Grid Grouper.");
					System.out.println("2) Reset the Grouper Registry.");
					System.out.println("3) Print command line options.");
					System.out.println();
					int option = IOUtils.readInteger("Enter Menu Option", true);
					if (option == 1) {
						addAdminMember(null);
					} else if (option == 2) {
						resetRegistry();
					} else {
						HelpFormatter formatter = new HelpFormatter();
						formatter.printHelp(GridGrouperBootstrapper.class.getName(), options);
					}
				}
			}

		} catch (ParseException exp) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(GridGrouperBootstrapper.class.getName(), options, false);
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

}
