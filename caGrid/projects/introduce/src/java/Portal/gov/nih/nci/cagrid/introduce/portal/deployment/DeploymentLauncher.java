package gov.nih.nci.cagrid.introduce.portal.deployment;

import gov.nih.nci.cagrid.introduce.common.ResourceManager;

import java.io.File;

import org.cagrid.grape.GridApplication;
import org.cagrid.grape.model.Dimensions;
import org.cagrid.grape.model.RenderOptions;
import org.cagrid.grape.utils.CompositeErrorDialog;


public class DeploymentLauncher {

    File serviceDirectory = null;
    boolean error = false;


    public DeploymentLauncher() {
        promptAndInitialize();
        if (serviceDirectory != null && !error) {
            DeploymentViewer viewer = new DeploymentViewer(serviceDirectory);
            Dimensions dim = new Dimensions(500, 700);
            RenderOptions ro = new RenderOptions();
            ro.setCentered(true);
            GridApplication.getContext().addApplicationComponent(viewer, dim, ro);
        }
    }


    private void promptAndInitialize() {

        try {
            String dir = ResourceManager.promptDir(null);
            if (dir != null) {
                if (new File(dir).exists() && new File(dir).canRead()) {
                    serviceDirectory = new File(dir);
                } else {
                    CompositeErrorDialog.showErrorDialog("Error opening directory for deployment", "Directory "
                        + serviceDirectory.getAbsolutePath()
                        + " does not exist or does not seem to be an introduce service");
                    error = true;
                }
            }
        } catch (Exception e) {
            CompositeErrorDialog.showErrorDialog("Error opening directory for deployment", "Directory "
                + serviceDirectory + " does not exist or does not seem to be an introduce service");
            error = true;
        }
    }

}
