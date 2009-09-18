package org.cagrid.gaards.dorian.service.upgrader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.cagrid.gaards.dorian.service.BeanUtils;
import org.cagrid.gaards.dorian.service.PropertyManager;
import org.cagrid.tools.database.Database;
import org.springframework.core.io.FileSystemResource;


public class Upgrader {

    public static final String HELP_OPT = "h";

    public static final String HELP_OPT_FULL = "help";

    public static final String DORIAN_CONFIG_FILE_OPT = "c";

    public static final String DORIAN_CONFIG_FILE_FULL = "conf";

    public static final String DORIAN_PROPERTIES_FILE_OPT = "p";

    public static final String DORIAN_PROPERTIES_FILE_FULL = "properties";

    public static final String UPGRADER_CONFIG_FILE_OPT = "u";

    public static final String UPGRADER_CONFIG_FILE_FULL = "uconf";

    public static final String TRIAL_OPT = "t";

    public static final String TRIAL_OPT_FULL = "trial";

    private Map<String, Upgrade> upgradeSet;
    private BeanUtils beanUtils;
    private Database db;


    public Upgrader(BeanUtils beanUtils, List<Upgrade> upgrades) throws Exception {
        this.beanUtils = beanUtils;
        Database db = beanUtils.getDatabase();
        db.createDatabaseIfNeeded();
        this.db = db;

        buildUpgraders(upgrades);
    }


    private void buildUpgraders(List<Upgrade> upgrades) {
        upgradeSet = new HashMap<String, Upgrade>();
        for (int i = 0; i < upgrades.size(); i++) {
            Upgrade u = upgrades.get(i);
            u.setBeanUtils(this.beanUtils);
            upgradeSet.put(u.getStartingVersion(), u);
        }

    }


    public void upgrade(boolean trialRun) {
        try {
            PropertyManager properties = new PropertyManager(db);
            List<Upgrade> upgrades = determineUpgrades(properties.getVersion(), new ArrayList<Upgrade>());
            if (upgrades.size() == 0) {
                System.out.println("No upgrades required, Dorian is already upgraded to the latest version ("
                    + PropertyManager.CURRENT_VERSION + ").");
            } else {
                System.out.println("Attempting to upgrade Dorian from version " + properties.getVersion()
                    + " to version " + PropertyManager.CURRENT_VERSION + ".");
                for (int i = 0; i < upgrades.size(); i++) {
                    Upgrade u = upgrades.get(i);
                    if (!properties.getVersion().equals(u.getStartingVersion())) {
                        if (!trialRun) {
                            throw new Exception("Cannot run the upgrader " + u.getClass().getName()
                                + ", this upgrader starts with " + u.getStartingVersion()
                                + ", your system is using version " + properties.getVersion() + ".");
                        }
                    }

                    System.out.println("Attempting to run upgrader " + u.getClass().getName()
                        + " which upgrades from Dorian " + u.getStartingVersion() + " to Dorian "
                        + u.getUpgradedVersion() + ".");
                    u.upgrade(trialRun);

                    if (!trialRun) {
                        System.out.println("Dorian upgraded from version " + u.getStartingVersion() + " to version "
                            + u.getUpgradedVersion() + ".");
                    }
                    properties = new PropertyManager(db);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }

    }


    private List<Upgrade> determineUpgrades(String version, List<Upgrade> upgrades) throws Exception {
        if (version.equals(PropertyManager.CURRENT_VERSION)) {
            return upgrades;
        } else {
            Upgrade u = upgradeSet.get(version);
            if (u == null) {
                throw new Exception("No upgrade to version " + version + " could be determined.");
            } else {
                upgrades.add(u);
                return determineUpgrades(u.getUpgradedVersion(), upgrades);
            }
        }
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        Options options = new Options();
        Option conf = new Option(DORIAN_CONFIG_FILE_OPT, DORIAN_CONFIG_FILE_FULL, true,
            "The config file for the Dorian CA.");
        options.addOption(conf);
        conf.setRequired(true);

        Option help = new Option(HELP_OPT, HELP_OPT_FULL, false, "Prints this message.");
        options.addOption(help);

        Option trial = new Option(TRIAL_OPT, TRIAL_OPT_FULL, false, "Trial run of the upgrader.");
        options.addOption(trial);

        Option props = new Option(DORIAN_PROPERTIES_FILE_OPT, DORIAN_PROPERTIES_FILE_FULL, true,
            "The properties file for the Dorian CA.");
        props.setRequired(true);
        options.addOption(props);

        Option uconf = new Option(UPGRADER_CONFIG_FILE_OPT, UPGRADER_CONFIG_FILE_FULL, true,
            "The config file for the Dorian Upgrader.");
        uconf.setRequired(true);
        options.addOption(uconf);

        try {
            CommandLineParser parser = new PosixParser();
            CommandLine line = parser.parse(options, args);

            if (line.getOptionValue(HELP_OPT) != null) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp(Upgrader.class.getName(), options);
                System.exit(0);
            } else {
                String dorianConfigFile = line.getOptionValue(DORIAN_CONFIG_FILE_OPT);
                String dorianPropertiesFile = line.getOptionValue(DORIAN_PROPERTIES_FILE_OPT);
                String upgraderConfigFile = line.getOptionValue(UPGRADER_CONFIG_FILE_OPT);

                BeanUtils beanUtils = new BeanUtils(new FileSystemResource(dorianConfigFile), new FileSystemResource(
                    dorianPropertiesFile));
                UpgradeBeanUtils upgradeUtils = new UpgradeBeanUtils(new FileSystemResource(upgraderConfigFile));

                boolean trialRun = false;
                if (line.hasOption(TRIAL_OPT)) {
                    trialRun = true;
                }
                Upgrader u = new Upgrader(beanUtils, upgradeUtils.getUpgradeList());
                u.upgrade(trialRun);
            }
        } catch (ParseException exp) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(Upgrader.class.getName(), options, false);
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

}
