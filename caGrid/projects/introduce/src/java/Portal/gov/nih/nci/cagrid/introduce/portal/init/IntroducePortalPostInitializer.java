package gov.nih.nci.cagrid.introduce.portal.init;

import gov.nih.nci.cagrid.introduce.common.ConfigurationUtil;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;
import gov.nih.nci.cagrid.introduce.portal.common.IntroduceLookAndFeel;
import gov.nih.nci.cagrid.introduce.portal.updater.IntroduceUpdateWizard;
import gov.nih.nci.cagrid.introduce.portal.updater.UptodateChecker;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gaards.ui.dorian.ServicesManager;
import org.cagrid.grape.ApplicationInitializer;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.model.Application;


public class IntroducePortalPostInitializer implements ApplicationInitializer {

    Log logger = LogFactory.getLog(ApplicationInitializer.class.getName());


    public void intialize(Application app) {
        Runnable r = new Runnable() {
        
            public void run() {
                try {
                    if (ConfigurationUtil.getIntroducePortalConfiguration().isCheckForUpdatesOnStartup()) {
                        if (!UptodateChecker.introduceUptodate()) {
                            int option = JOptionPane.showOptionDialog(GridApplication.getContext().getApplication(),
                                "Updates are available.\nWould you like to view them now?", "Introduce Updates Avaiable",
                                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, IntroduceLookAndFeel.getUpdateIcon(),
                                null, null);
                            if (option == JOptionPane.YES_OPTION) {
                                IntroduceUpdateWizard.showUpdateWizard(true);
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("Unable to check for updates:, " + e.getMessage(), e);
                }
                
                
                if(ExtensionsLoader.getInstance().getExtension("cagrid_gaards_ui")!=null){
                    logger.info("Loading GAARDS UI Initializer");
                    ServicesManager.getInstance();
                }
        
            }
        };
        
        Thread th = new Thread(r);
        th.start();
        
    }

}
