package org.cagrid.grape;

import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.RunnerGroup;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.cagrid.grape.model.Component;
import org.cagrid.grape.model.Dimensions;
import org.cagrid.grape.model.RenderOptions;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @created Oct 14, 2004
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class ApplicationContext {

    private GridApplication application;

    private Component component;


    public ApplicationContext(GridApplication application) {
        this(application, null);
    }


    public ApplicationContext(GridApplication application, Component component) {
        this.application = application;
        this.component = component;
    }


    public Component getComponent() {
        return component;
    }


    public GridApplication getApplication() {
        return this.application;
    }


    public void addApplicationComponent(ApplicationComponent comp, Dimensions dim, RenderOptions options) {
        this.application.addApplicationComponent(comp, dim, options);
    }


    public void addApplicationComponent(ApplicationComponent comp, Dimensions dim) {
        this.application.addApplicationComponent(comp, dim, null);
    }


    public void addApplicationComponent(ApplicationComponent comp, int width, int height) {
        this.application.addApplicationComponent(comp, new Dimensions(height, width), null);
    }
    
    public void addApplicationComponent(ApplicationComponent comp) {
        this.application.addApplicationComponent(comp);
    }


    public ConfigurationManager getConfigurationManager() {
        return this.application.getConfigurationManager();
    }


    public void executeInBackground(Runner r) throws Exception {
        this.application.getThreadManager().executeInBackground(r);
    }


    public void executeInBackground(RunnerGroup grp) throws Exception {
        this.application.getThreadManager().executeGroupInBackground(grp);
    }


    public void execute(Runner r) throws Exception {
        this.application.getThreadManager().execute(r);
    }


    public void execute(RunnerGroup grp) throws Exception {
        this.application.getThreadManager().executeGroup(grp);
    }


    public void centerComponent(JComponent comp) {
        // Determine the new location of the window
        int w = application.getSize().width;
        int h = application.getSize().height;
        int x = application.getLocationOnScreen().x;
        int y = application.getLocationOnScreen().y;
        Dimension dim = comp.getSize();
        comp.setLocation(w / 2 + x - dim.width / 2, h / 2 + y - dim.height / 2);
    }


    public void centerWindow(Window comp) {
        // Determine the new location of the window
        int w = application.getSize().width;
        int h = application.getSize().height;
        int x = application.getLocationOnScreen().x;
        int y = application.getLocationOnScreen().y;
        Dimension dim = comp.getSize();
        comp.setLocation(w / 2 + x - dim.width / 2, h / 2 + y - dim.height / 2);
    }


    public void showMessage(String msg) {
        showMessage(new String[]{msg});
    }


    public void showMessage(String[] msg) {
        showMessage("Information", msg);
    }


    public void showMessage(String title, String msg) {
        showMessage(title, new String[]{msg});
    }


    public void showMessage(String title, String[] msg) {
        JOptionPane.showMessageDialog(this.application, msg, title, JOptionPane.INFORMATION_MESSAGE);
    }


    public void showDialog(JDialog dialog) {
        centerDialog(dialog);
        dialog.setVisible(true);
    }


    public void centerDialog(JDialog dialog) {
        // Determine the new location of the window
        Frame owner = this.application;
        if (owner != null) {
            int w = owner.getSize().width;
            int h = owner.getSize().height;
            int x = owner.getLocationOnScreen().x;
            int y = owner.getLocationOnScreen().y;
            Dimension dim = dialog.getSize();
            dialog.setLocation(w / 2 + x - dim.width / 2, h / 2 + y - dim.height / 2);
        }
    }
}
