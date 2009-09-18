/**
 * 
 */
package org.cagrid.installer.steps;

import java.io.File;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pietschy.wizard.InvalidStateException;


/**
 * @author <a href="joshua.phillips@semanticbits.com">Joshua Phillips</a>
 */
public class SpecifyPortsStep extends PropertyConfigurationStep {

    private static final Log logger = LogFactory.getLog(SpecifyPortsStep.class);


    /**
	 * 
	 */
    public SpecifyPortsStep() {

    }


    /**
     * @param name
     * @param description
     */
    public SpecifyPortsStep(String name, String description) {
        super(name, description);
    }


    /**
     * @param name
     * @param description
     * @param icon
     */
    public SpecifyPortsStep(String name, String description, Icon icon) {
        super(name, description, icon);
    }


    public void prepare() {
        JTextField httpsPortField = (JTextField) getOption(Constants.HTTPS_PORT);
        JLabel httpsPortLabel = getLabel(Constants.HTTPS_PORT);

        JTextField httpPortField = (JTextField) getOption(Constants.HTTP_PORT);
        JLabel httpPortLabel = getLabel(Constants.HTTP_PORT);
        if (!this.model.isTrue(Constants.USE_SECURE_CONTAINER)) {
            httpPortField.setVisible(true);
            httpPortLabel.setVisible(true);
            httpsPortField.setVisible(false);
            httpsPortLabel.setVisible(false);
        } else {
            httpPortField.setVisible(false);
            httpPortLabel.setVisible(false);
            httpsPortField.setVisible(true);
            httpsPortLabel.setVisible(true);
        }

        httpPortField.setText(model.getProperty(Constants.HTTP_PORT));
        httpsPortField.setText(model.getProperty(Constants.HTTPS_PORT));

        JTextField shutdownPortField = (JTextField) getOption(Constants.SHUTDOWN_PORT);
        JLabel shutdownPortLabel = getLabel(Constants.SHUTDOWN_PORT);
        shutdownPortField.setText(model.getProperty(Constants.SHUTDOWN_PORT));
        if (!model.isTomcatContainer()) {
            shutdownPortField.setVisible(false);
            shutdownPortLabel.setVisible(false);
        }

    }


    public void applyState() throws InvalidStateException {
        super.applyState();

        // set the new location for the server certificate and key
        File _basePath = new File(System.getProperty("user.home") + "/" + Constants.CAGRID_BASE_DIR_NAME);
        String serverCert = _basePath.getAbsolutePath() + File.separator + "certificates" + File.separator
            + model.getProperty(Constants.SERVICE_HOSTNAME) + "-cert.pem";
        model.setProperty(Constants.SERVICE_CERT_PATH, serverCert);
        String serverKey = _basePath.getAbsolutePath() + File.separator + "certificates" + File.separator
            + model.getProperty(Constants.SERVICE_HOSTNAME) + "-key.pem";
        model.setProperty(Constants.SERVICE_KEY_PATH, serverKey);

    }


    public static void main(String[] args) throws Exception {

    }

}
