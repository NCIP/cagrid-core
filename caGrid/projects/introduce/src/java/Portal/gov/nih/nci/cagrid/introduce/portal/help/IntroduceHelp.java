package gov.nih.nci.cagrid.introduce.portal.help;

import gov.nih.nci.cagrid.introduce.codegen.SyncTools;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.net.URL;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.swing.JFrame;

import org.apache.log4j.Logger;
import org.cagrid.grape.GridApplication;


public class IntroduceHelp {
    
    private static final Logger logger = Logger.getLogger(IntroduceHelp.class);
    
    private static HelpBroker fHelp;
    private static CSH.DisplayHelpFromSource fDisplayHelp;
    private static HelpSet helpSet;
    private static boolean initialized = false;


    public IntroduceHelp() {
        if (!initialized) {
            IntroduceHelp.initialized = true;
            IntroduceHelp.initHelpSystem();
        }
        fDisplayHelp.actionPerformed(new ActionEvent(GridApplication.getContext().getApplication().getMDIDesktopPane(), 0 , "help"));
    }


    public static CSH.DisplayHelpFromSource getFDisplayHelp() {
        return fDisplayHelp;
    }


    public static void setFDisplayHelp(CSH.DisplayHelpFromSource displayHelp) {
        IntroduceHelp.fDisplayHelp = displayHelp;
    }


    public static HelpBroker getFHelp() {
        return fHelp;
    }


    public static void setFHelp(HelpBroker help) {
        fHelp = help;
    }


    public static HelpSet getHelpSet() {
        return helpSet;
    }


    public static void setHelpSet(HelpSet helpSet) {
        IntroduceHelp.helpSet = helpSet;
    }


    /**
     * Initialize the JavaHelp system.
     */
    public static void initHelpSystem() {
        // optimization to avoid repeated init
        if (fHelp != null && fDisplayHelp != null)
            return;

        // (uses the classloader mechanism)
        ClassLoader loader = IntroduceHelp.class.getClassLoader();
        URL helpSetURL = HelpSet.findHelpSet(loader, "Introduce.hs");
        try {
            helpSet = new HelpSet(null, helpSetURL);
            fHelp = helpSet.createHelpBroker();
            fDisplayHelp = new CSH.DisplayHelpFromSource(fHelp);

        } catch (HelpSetException ex) {
            logger.warn("Cannot create help system with: " + helpSetURL + " " + ex.getMessage());
        }
    }

}
