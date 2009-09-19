package gov.nci.nih.cagrid.tests.core.util;

import gov.nci.nih.cagrid.tests.cqlprocessors.util.RandomObject;
import gov.nih.nci.cagrid.common.Utils;

import java.io.File;

import javax.xml.namespace.QName;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;


public class BeanFileUtil {
    private File serviceDir;
    private String className;


    public BeanFileUtil(File serviceDir, String className) {
        super();

        this.serviceDir = serviceDir;
        this.className = className;
    }


    public void writeBean(int depth, String qname, File outFile) throws Exception {
        Class cl = IntroduceServiceInfo.loadClass(serviceDir, className);;

        RandomObject rand = new RandomObject();
        Object obj = rand.next(cl, depth);
        Utils.serializeDocument(outFile.toString(), obj, new QName(qname));
    }


    public Object readBean(File inFile) throws Exception {
        Class cl = IntroduceServiceInfo.loadClass(serviceDir, className);;
        return Utils.deserializeDocument(inFile.toString(), cl);
    }


    /**
     * Get the command-line options for
     */
    public static Options getOptions() {
        // create options

        Option dir = OptionBuilder.withArgName("dir").hasArg().isRequired(true).withDescription(
            "the service dir to look for beans").create("dir");

        Option out = OptionBuilder.withArgName("out").hasArg().isRequired(true).withDescription("the output xml file")
            .create("out");

        Option cl = OptionBuilder.withArgName("class").hasArg().isRequired(true).withDescription(
            "the name of the root class to generate an object for").create("class");

        Option qname = OptionBuilder.withArgName("qname").hasArg().isRequired(true).withDescription(
            "the qname for the root element").create("qname");

        Option depth = OptionBuilder.withArgName("depth").hasArg().isRequired(false).withDescription(
            "depth of classes to create").create("depth");

        // add options

        Options options = new Options();

        options.addOption(dir);
        options.addOption(out);
        options.addOption(cl);
        options.addOption(qname);
        options.addOption(depth);

        return options;
    }


    public static void main(String[] args) throws Exception {
        Options options = getOptions();
        CommandLine cmd = null;
        try {
            cmd = new BasicParser().parse(options, args);
        } catch (ParseException e) {
            System.out.println("Error parsing arguments: " + e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("BeanFileUtil", options);
            System.exit(-1);
            return;
        }

        int depth = 0;
        if (cmd.hasOption("depth"))
            depth = Integer.parseInt(cmd.getOptionValue("depth"));

        BeanFileUtil bean = new BeanFileUtil(new File(cmd.getOptionValue("dir")), cmd.getOptionValue("class"));
        bean.writeBean(depth, cmd.getOptionValue("qname"), new File(cmd.getOptionValue("out")));
    }
}
