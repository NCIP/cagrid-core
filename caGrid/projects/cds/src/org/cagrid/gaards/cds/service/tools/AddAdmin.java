package org.cagrid.gaards.cds.service.tools;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.cagrid.gaards.cds.service.ConfigurationConstants;
import org.cagrid.gaards.cds.service.DelegationManager;
import org.cagrid.gaards.pki.tools.GenerateTrustReport;
import org.cagrid.tools.database.Database;
import org.cagrid.tools.groups.Group;
import org.cagrid.tools.groups.GroupManager;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;

public class AddAdmin {

	public static final String ADMIN_OPT = "a";
	public static final String ADMIN_OPT_FULL = "admin";
	public static final String CONF_OPT = "c";
	public static final String CONF_OPT_FULL = "conf";
	public static final String PROPERTIES_OPT = "p";
	public static final String PROPERTIES_OPT_FULL = "properties";
	public static final String HELP_OPT = "h";
	public static final String HELP_OPT_FULL = "help";

	public static void main(String[] args) {
		Options options = new Options();
		Option help = new Option(HELP_OPT, HELP_OPT_FULL, false,
				"Prints this message.");
		Option admin = new Option(ADMIN_OPT, ADMIN_OPT_FULL, true,
				"The grid identity of the user to add as a CDS administrator.");
		admin.setRequired(true);

		Option props = new Option(PROPERTIES_OPT, PROPERTIES_OPT_FULL, true,
				"The location of the CDS properties file.");
		props.setRequired(true);

		Option conf = new Option(CONF_OPT, CONF_OPT_FULL, true,
				"The location of the CDS configuration file.");
		conf.setRequired(true);

		options.addOption(help);
		options.addOption(props);
		options.addOption(conf);
		options.addOption(admin);
		try {
			CommandLineParser parser = new PosixParser();
			CommandLine line = parser.parse(options, args);

			if (line.hasOption(HELP_OPT)) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp(GenerateTrustReport.class.getName(),
						options);
				System.exit(0);
			} else {
				String gridIdentity = admin.getValue();
				FileSystemResource fsr = new FileSystemResource(conf.getValue());
				XmlBeanFactory factory = new XmlBeanFactory(fsr);
				PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
				cfg.setLocation(new FileSystemResource(props.getValue()));
				cfg.postProcessBeanFactory(factory);
				Database db = (Database) factory
				.getBean(ConfigurationConstants.DATABASE_CONFIGURATION_BEAN);
				db.createDatabaseIfNeeded();
				GroupManager gm = (GroupManager) factory
						.getBean(ConfigurationConstants.GROUP_MANAGER_BEAN);
				Group admins = null;
				if (!gm.groupExists(DelegationManager.ADMINISTRATORS)) {
					gm.addGroup(DelegationManager.ADMINISTRATORS);
					admins = gm.getGroup(DelegationManager.ADMINISTRATORS);
				} else {
					admins = gm.getGroup(DelegationManager.ADMINISTRATORS);
				}
				admins.addMember(gridIdentity);
				System.out.println("Succesfully added " + gridIdentity
						+ " as a CDS administrator.");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
