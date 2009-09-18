package gov.nih.nci.cagrid.syncgts.tools;

import gov.nih.nci.cagrid.syncgts.bean.SyncDescription;
import gov.nih.nci.cagrid.syncgts.core.SyncGTS;
import gov.nih.nci.cagrid.syncgts.core.SyncGTSDefault;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;


public class SyncGTSCommand {

	public static final String DESCRIPTION_OPT = "d";
	public static final String DESCRIPTION_OPT_FULL = "description";
	public static final String ONCE_OPT = "o";
	public static final String ONCE_OPT_FULL = "once";
	public static final String HELP_OPT = "h";
	public static final String HELP_OPT_FULL = "help";


	public static void main(String[] args) {
		Options options = new Options();
		Option help = new Option(HELP_OPT, HELP_OPT_FULL, false, "Prints this message.");
		Option desc = new Option(DESCRIPTION_OPT, DESCRIPTION_OPT_FULL, true,
			"The path to the synchronization description file.");
		desc.setRequired(false);
		Option once = new Option(ONCE_OPT, ONCE_OPT_FULL, false,
			"Specifiy this option if you wish to sync with the GTS only once.");
		once.setRequired(false);

		options.addOption(help);
		options.addOption(desc);
		options.addOption(once);

		try {
			CommandLineParser parser = new PosixParser();
			CommandLine line = parser.parse(options, args);
	

			if (line.hasOption(HELP_OPT)) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp(SyncGTSCommand.class.getName(), options);
				System.exit(0);
			} else {
				if (line.hasOption(DESCRIPTION_OPT)) {
					SyncGTSDefault.setServiceSyncDescriptionLocation(line.getOptionValue(DESCRIPTION_OPT));
				}
				SyncDescription description = SyncGTSDefault.getSyncDescription();
				if (line.hasOption(ONCE_OPT)) {
					try {
						SyncGTS sync = SyncGTS.getInstance();
						sync.syncOnce(description);
						System.exit(0);
					} catch (Exception e) {
						e.printStackTrace();
						System.exit(1);
					}

				} else {
					try {
						SyncGTS sync = SyncGTS.getInstance();
						sync.syncAndResync(description, false);
					} catch (Exception e) {
						e.printStackTrace();
						System.exit(1);
					}
				}
			}

		} catch (ParseException exp) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(SyncGTSCommand.class.getName(), options, false);
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

}
