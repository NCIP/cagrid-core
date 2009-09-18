/**
 * 
 */
package org.cagrid.installer.steps;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Map.Entry;

import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;

import org.cagrid.installer.model.CaGridInstallerModel;
import org.cagrid.installer.util.InstallerUtils;
import org.pietschy.wizard.PanelWizardStep;
import org.pietschy.wizard.WizardModel;


/**
 * @author <a href="mailto:joshua.phillips@semanticbits.com">Joshua Phillips</a>
 */
public class InstallationCompleteStep extends PanelWizardStep {

    private CaGridInstallerModel model;

    private JTextPane textPane;


    /**
	 * 
	 */
    public InstallationCompleteStep() {
    }


    /**
     * @param arg0
     * @param arg1
     */
    public InstallationCompleteStep(String name, String summary) {
        super(name, summary);
    }


    /**
     * @param arg0
     * @param arg1
     * @param arg2
     */
    public InstallationCompleteStep(String name, String summary, Icon icon) {
        super(name, summary, icon);
    }


    public void init(WizardModel m) {
        this.model = (CaGridInstallerModel) m;

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        this.setLayout(new GridBagLayout());
        this.setSize(new Dimension(263, 161));

        this.textPane = new JTextPane();
        InputMap inputMap = this.textPane.getInputMap();
        KeyStroke keyStroke = KeyStroke.getKeyStroke("control C");
        inputMap.put(keyStroke, DefaultEditorKit.copyAction);
        this.add(textPane, gridBagConstraints);

        setComplete(true);
    }


    public void prepare() {
        StringBuilder sb = new StringBuilder();

        sb.append("caGrid installation is complete. ");

        sb.append("Please remember to set the following environment variables:\n\n");
        if (InstallerUtils.isEmpty(System.getenv("JAVA_HOME"))) {
            sb.append("\t").append("JAVA_HOME=").append(InstallerUtils.getJavaHomePath()).append("\n");
        }
        sb.append("\t").append("ANT_HOME=").append(this.model.getProperty(Constants.ANT_HOME)).append("\n");
        sb.append("\t").append("GLOBUS_LOCATION=").append(this.model.getProperty(Constants.GLOBUS_HOME)).append("\n");
        if (this.model.getMessage("container.type.tomcat").equals(this.model.getProperty(Constants.CONTAINER_TYPE))) {
            sb.append("\t").append("CATALINA_HOME=").append(this.model.getProperty(Constants.TOMCAT_HOME)).append("\n");
        }
        if (this.model.getMessage("container.type.jboss").equals(this.model.getProperty(Constants.CONTAINER_TYPE))) {
            sb.append("\t").append("JBOSS_HOME=").append(this.model.getProperty(Constants.JBOSS_HOME)).append("\n");
        }

        if (model.isConfigureContainerSelected() && model.isTrue(Constants.USE_SECURE_CONTAINER)
            && model.getProperty(Constants.HOST_CREDS_SELECTION_TYPE) != null
            && model.getProperty(Constants.HOST_CREDS_SELECTION_TYPE).equals(Constants.HOST_CREDS_FROM_MANUAL)) {
            sb.append("\nPlease remember to copy the host certificate and key into the following locations:\n");
            sb.append("\t" + model.getProperty(Constants.SERVICE_CERT_PATH) + "\n");
            sb.append("\t" + model.getProperty(Constants.SERVICE_KEY_PATH) + "\n");
        }

        if (model.isConfigureContainerSelected() && model.isTrue(Constants.USE_SECURE_CONTAINER)
            && model.getProperty(Constants.HOST_CREDS_SELECTION_TYPE) != null
            && model.getProperty(Constants.HOST_CREDS_SELECTION_TYPE).equals(Constants.HOST_CREDS_ALREADY_INSTALLED)) {
            sb.append("\nPlease remember to validate the host certificate and key are in the following locations:\n");
            sb.append("\t" + model.getProperty(Constants.SERVICE_CERT_PATH) + "\n");
            sb.append("\t" + model.getProperty(Constants.SERVICE_KEY_PATH) + "\n");
        }
        
 
        this.textPane.setText(sb.toString());

        try {
            FileWriter fw = new FileWriter(new File("." + File.separator + "CAGRID_POST_INSTALLATION.txt"));
            fw.write(sb.toString());
            fw.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        // Save the setting for this run of the installer
        Properties props = new Properties();
        for (Iterator i = model.getStateMap().entrySet().iterator(); i.hasNext();) {
            Entry entry = (Entry) i.next();
            if (entry.getKey() instanceof String && entry.getValue() instanceof String) {
                props.setProperty((String) entry.getKey(), (String) entry.getValue());
            }
        }
        try {
            props.store(new FileOutputStream(model.getProperty(Constants.CAGRID_INSTALLER_PROPERTIES)),
                "Generated by caGrid Installer at " + System.currentTimeMillis());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

}
