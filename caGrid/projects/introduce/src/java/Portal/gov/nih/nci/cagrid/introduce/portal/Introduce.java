package gov.nih.nci.cagrid.introduce.portal;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.portal.SplashScreen;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.common.IntroducePropertiesManager;
import gov.nih.nci.cagrid.introduce.extension.ExtensionTools;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JFrame;

import org.apache.axis.utils.ClassUtils;
import org.apache.log4j.Logger;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.model.Application;


public final class Introduce {
    
    private static final Logger logger = Logger.getLogger(Introduce.class);

    private static SplashScreen introduceSplash;


    private static void showIntroduceSplash() {
        try {
            introduceSplash = new SplashScreen("/introduceSplash.png");
            // centers in screen
            introduceSplash.setLocationRelativeTo(null);
            introduceSplash.setVisible(true);
        } catch (Exception e) {
            logger.error(e);
        }
    }


    private static void initialize() {

    }


    private static void checkForUpdatePatchBugFix() {
        File patchPropertiesFile = new File("patch.properties");
        if (patchPropertiesFile.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(patchPropertiesFile);
            } catch (FileNotFoundException e2) {
                logger.warn("Could not load the file " + patchPropertiesFile.getAbsolutePath(), e2);
                e2.printStackTrace();
            }
            Properties patchProperties = new Properties();
            try {
                patchProperties.load(fis);
            } catch (FileNotFoundException e1) {
                logger.warn("Could not load the file " + patchPropertiesFile.getAbsolutePath(), e1);
                e1.printStackTrace();
            } catch (IOException e1) {
                logger.warn("Could not load the file " + patchPropertiesFile.getAbsolutePath(), e1);
                e1.printStackTrace();
            }

            if (patchProperties.containsKey(IntroduceConstants.INTRODUCE_PATCH_VERSION_PROPERTY)) {
                // need to set the patch version in the
                // introduce.properties file
                File engineProps = new File("." + File.separator + IntroduceConstants.INTRODUCE_PROPERTIES);
                Properties props = new Properties();

                try {
                    FileInputStream enginePropsIn = new FileInputStream(engineProps);
                    props.load(enginePropsIn);
                    enginePropsIn.close();
                    props.setProperty(IntroduceConstants.INTRODUCE_PATCH_VERSION_PROPERTY, 
                        String.valueOf(patchProperties.get(IntroduceConstants.INTRODUCE_PATCH_VERSION_PROPERTY)));
                    FileOutputStream fos = new FileOutputStream(engineProps);
                    props.store(fos, "Introduce Engine Properties");
                    fos.close();
                } catch (FileNotFoundException e) {
                    logger.error("Error setting properties", e);
                    e.printStackTrace();
                } catch (IOException e) {
                    logger.error("Error setting properties", e);
                    e.printStackTrace();
                }
                File enginePropsT = new File("." + File.separator + "conf" + File.separator
                    + "introduce.properties.template");
                Properties propsT = new Properties();
                try {
                    FileInputStream enginePropsTin = new FileInputStream(enginePropsT);
                    propsT.load(enginePropsTin);
                    enginePropsTin.close();
                    propsT.setProperty(IntroduceConstants.INTRODUCE_PATCH_VERSION_PROPERTY, 
                        String.valueOf(patchProperties.get(IntroduceConstants.INTRODUCE_PATCH_VERSION_PROPERTY)));
                    FileOutputStream fos = new FileOutputStream(enginePropsT);
                    propsT.store(fos, "Introduce Engine Properties");
                    fos.close();
                } catch (FileNotFoundException e) {
                    logger.error("Error setting patch version", e);
                    e.printStackTrace();
                } catch (IOException e) {
                    logger.error("Error setting patch version", e);
                    e.printStackTrace();
                }
            }
            patchProperties.clear();
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        patchPropertiesFile.delete();
    }


    private static void showGridPortal(String confFile) {
        try {
            checkForUpdatePatchBugFix();
            initialize();
            showIntroduceSplash();

            if (confFile == null) {
                confFile = IntroducePropertiesManager.getIntroduceConfigurationFile();
            }

            Application app = Utils.deserializeDocument(confFile, Application.class);

            // launch the portal with the passed config
            GridApplication applicationInstance = GridApplication.getInstance(
                app, Introduce.class.getClassLoader());
            Dimension d = new Dimension(app.getDimensions().getWidth(), app.getDimensions().getHeight());

            try {
                applicationInstance.pack();
            } catch (Exception e) {
                applicationInstance.setIconImage(null);
                applicationInstance.pack();
            }
            applicationInstance.setSize(d);
            applicationInstance.setVisible(true);
            applicationInstance.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static final class IntroduceSplashCloser implements Runnable {
        public void run() {
            try {
                introduceSplash.dispose();
            } catch (Exception e) {
                // no error, we're just trying to close out the window
            }
        }
    }


    public static void main(String[] args) {
        if (args.length > 0) {
            showGridPortal(args[0]);
        } else {
            showGridPortal(null);
        }
        EventQueue.invokeLater(new IntroduceSplashCloser());
    }
}
