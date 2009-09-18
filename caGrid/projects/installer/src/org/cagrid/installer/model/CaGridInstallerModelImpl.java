/**
 * 
 */
package org.cagrid.installer.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.installer.steps.Constants;
import org.cagrid.installer.steps.InstallationCompleteStep;
import org.cagrid.installer.steps.RunTasksStep;
import org.cagrid.installer.util.InstallerUtils;
import org.pietschy.wizard.OverviewProvider;
import org.pietschy.wizard.models.DynamicModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * @author <a href="mailto:joshua.phillips@semanticbits.com">Joshua Phillips</a>
 */
public class CaGridInstallerModelImpl extends DynamicModel implements

CaGridInstallerModel, OverviewProvider {

    private static final Log logger = LogFactory.getLog(CaGridInstallerModelImpl.class);

    private PropertyChangeEventProviderMap state;

    private ResourceBundle messages;

    private Boolean tomcatInstalled = null;

    private Boolean jbossInstalled = null;

    private Boolean globusInstalled = null;

    private Boolean antInstalled = null;

    private Boolean cagridInstalled = null;

    private Boolean globusConfigured = null;

    private Boolean globusDeployed = null;


    /**
     * 
     */
    public CaGridInstallerModelImpl() {
        this(null, null);
    }


    public CaGridInstallerModelImpl(Map<String, String> state) {
        this(state, null);
    }


    public CaGridInstallerModelImpl(Map<String, String> state, ResourceBundle messages) {

        if (state == null) {
            this.state = new PropertyChangeEventProviderMap(new HashMap<String, String>());
        } else {
            this.state = new PropertyChangeEventProviderMap(state);
        }
        this.messages = messages;
        if (this.messages == null) {
            // Load messages
            try {
                // TODO: support international messages
                this.messages = ResourceBundle.getBundle(Constants.MESSAGES, Locale.US);
            } catch (Exception ex) {
                throw new RuntimeException("Error loading messages: " + ex.getMessage());
            }
        }
        checkEnvironment();
    }


    private void checkEnvironment() {
        // Look for ant
        if (isAntInstalled()) {
            setProperty(Constants.ANT_HOME, getHomeDir(Constants.ANT_HOME, "ANT_HOME"));
        }

        // Look for tomcat
        if (isTomcatInstalled()) {
            setProperty(Constants.TOMCAT_HOME, getHomeDir(Constants.TOMCAT_HOME, "CATALINA_HOME"));
        }

        // Look for jboss
        if (isJBossInstalled()) {
            setProperty(Constants.JBOSS_HOME, getHomeDir(Constants.JBOSS_HOME, "JBOSS_HOME"));
        }

        // Look for globus
        if (isGlobusInstalled()) {
            setProperty(Constants.GLOBUS_HOME, getHomeDir(Constants.GLOBUS_HOME, "GLOBUS_LOCATION"));
        }

        // Look for cagrid
        if (isCaGridInstalled()) {
            setProperty(Constants.CAGRID_HOME, getHomeDir(Constants.CAGRID_HOME, "CAGRID_HOME"));
        }

    }


    public void addPropertyChangeListener(PropertyChangeListener l) {
        super.addPropertyChangeListener(l);
        this.state.addPropertyChangeListener(l);
    }


    public String getMessage(String key) {
        String message = null;
        if (this.messages != null) {
            message = this.messages.getString(key);
        }
        return message;
    }


    private class PropertyChangeEventProviderMap extends HashMap {
        private PropertyChangeSupport pcs = new PropertyChangeSupport(CaGridInstallerModelImpl.this);


        PropertyChangeEventProviderMap(Map<String, String> map) {
            super(map);
        }


        void addPropertyChangeListener(PropertyChangeListener l) {
            this.pcs.addPropertyChangeListener(l);
        }


        public Object put(Object key, Object newValue) {

            Object oldValue = get(key);
            if (oldValue != null) {
                this.pcs.firePropertyChange((String) oldValue, oldValue, newValue);
            }
            logger.info("Setting " + key + " = " + newValue);
            super.put(key, newValue);
            return oldValue;
        }


        public void putAll(Map m) {
            for (Iterator i = m.entrySet().iterator(); i.hasNext();) {
                Map.Entry entry = (Map.Entry) i.next();
                put(entry.getKey(), entry.getValue());
            }
        }
    }


    public boolean isTrue(String propName) {
        return Constants.TRUE.equals(getProperty(propName));
    }


    public boolean isTomcatContainer() {
        return getMessage("container.type.tomcat").equals(getProperty(Constants.CONTAINER_TYPE));
    }


    public boolean isJBossContainer() {
        return getMessage("container.type.jboss").equals(getProperty(Constants.CONTAINER_TYPE));
    }


    public boolean isGlobusContainer() {
        return getMessage("container.type.globus").equals(getProperty(Constants.CONTAINER_TYPE));
    }


    public String getProperty(String propName) {
        return (String) this.state.get(propName);
    }


    public boolean isSet(String propName) {
        return !isEmpty(getProperty(propName));
    }


    public boolean isEmpty(String value) {
        return value == null || value.trim().length() == 0;
    }


    public boolean isEqual(String value, String propName) {
        return value.equals(getProperty(propName));
    }


    public void refreshModelState() {
        super.refreshModelState();
        if (getActiveStep() instanceof RunTasksStep) {
            RunTasksStep rts = (RunTasksStep) getActiveStep();
            setPreviousAvailable(!(rts.isBusy() || rts.isExecuted()));
        } else if (getActiveStep() instanceof InstallationCompleteStep) {
            setPreviousAvailable(false);
        }
    }


    public boolean isDeployGlobusRequired() {
        return (isTomcatContainer() || isJBossContainer())
            && (isTrue(Constants.REDEPLOY_GLOBUS) || !isGlobusDeployed());
    }


    public void unsetProperty(String propName) {
        this.state.remove(propName);
    }


    public void setProperty(String propName, String propValue) {
        this.state.put(propName, propValue);
    }


    public String getProperty(String propName, String defaultValue) {
        String value = (String) this.state.get(propName);
        return InstallerUtils.isEmpty(value) ? defaultValue : value;
    }


    public Map<String, String> getStateMap() {
        return new HashMap<String, String>(this.state);
    }


    public boolean isConfigureContainerSelected() {
        return isTrue(Constants.INSTALL_CONFIGURE_CONTAINER);
    }


    public boolean isAntInstalled() {
        if (antInstalled == null) {
            String homeDir = getHomeDir(Constants.ANT_HOME, "ANT_HOME");
            antInstalled = homeDir != null && InstallerUtils.checkAntVersion(homeDir);
        }
        return antInstalled;
    }


    protected String getHomeDir(String homeProp, String envName) {
        logger.debug("looking for home '" + homeProp + "'...");
        String home = getProperty(homeProp);
        if (home == null) {
            if (envName != null) {
                logger.info(homeProp + " was not found in initial properties. Checking environment variable: "
                    + envName);
                home = System.getenv(envName);
            }
        }
        if (home != null) {
            File f = new File(home);
            if (!f.exists()) {
                logger.info(home + " does not exist");
                home = null;
            }
        }
        logger.debug("...home = " + home);
        return home;
    }


    public boolean isTomcatInstalled() {
        if (tomcatInstalled == null) {
            String homeDir = getHomeDir(Constants.TOMCAT_HOME, "CATALINA_HOME");
            tomcatInstalled = homeDir != null && InstallerUtils.checkTomcatVersion(homeDir);
        }
        return tomcatInstalled;
    }


    public boolean isJBossInstalled() {
        if (jbossInstalled == null) {
            String homeDir = getHomeDir(Constants.JBOSS_HOME, "JBOSS_HOME");
            jbossInstalled = homeDir != null && InstallerUtils.checkJBossVersion(homeDir);
        }
        return jbossInstalled;
    }


    public boolean isGlobusInstalled() {
        if (globusInstalled == null) {
            String homeDir = getHomeDir(Constants.GLOBUS_HOME, "GLOBUS_LOCATION");
            globusInstalled = homeDir != null && InstallerUtils.checkGlobusVersion(homeDir);
        }
        return globusInstalled;
    }


    public boolean isCaGridInstalled() {
        if (cagridInstalled == null) {
            String homeDir = getHomeDir(Constants.CAGRID_HOME, "CAGRID_HOME");
            cagridInstalled = homeDir != null && InstallerUtils.checkCaGridIsValid(homeDir);
        }
        return cagridInstalled;
    }


    public boolean isGlobusConfigured() {

        if (globusConfigured == null) {
            globusConfigured = false;
            File secDesc = new File(getProperty(Constants.GLOBUS_HOME)
                + "/etc/globus_wsrf_core/global_security_descriptor.xml");
            if (secDesc.exists()) {
                try {
                    DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
                    fact.setValidating(false);
                    fact.setNamespaceAware(true);
                    DocumentBuilder builder = fact.newDocumentBuilder();
                    Document doc = builder.parse(secDesc);
                    XPathFactory xpFact = XPathFactory.newInstance();
                    Element keyFileEl = (Element) xpFact.newXPath().compile(
                        "/*[local-name()='securityConfig']/*[local-name()='credential']/*[local-name()='key-file']")
                        .evaluate(doc, XPathConstants.NODE);
                    Element certFileEl = (Element) xpFact.newXPath().compile(
                        "/*[local-name()='securityConfig']/*[local-name()='credential']/*[local-name()='cert-file']")
                        .evaluate(doc, XPathConstants.NODE);
                    if (keyFileEl != null && certFileEl != null) {
                        String keyFilePath = keyFileEl.getAttribute("value");
                        String certFilePath = certFileEl.getAttribute("value");
                        if (keyFilePath != null && certFilePath != null) {
                            File keyFile = new File(keyFilePath);
                            File certFile = new File(certFilePath);
                            globusConfigured = keyFile.exists() && certFile.exists();
                        }
                    }
                } catch (Exception ex) {
                    logger.error("Error checking if globus is already configured: " + ex.getMessage(), ex);
                }
            }
        }

        if (isTrue(Constants.REINSTALL_TOMCAT)) {
            globusConfigured = false;
        }
        return globusConfigured;
    }


    public boolean isGlobusDeployed() {
        globusDeployed = false;
        
        if (isTomcatContainer() && isTomcatInstalled()) {
            File wsrfDir = new File((String) getProperty(Constants.TOMCAT_HOME) + "/webapps/wsrf");
            globusDeployed = wsrfDir.exists();
        } else if (isJBossContainer() && isJBossInstalled()) {
            File wsrfDir = new File((String) getProperty(Constants.JBOSS_HOME) + "/server/default/deploy/wsrf.war/");
            globusDeployed = wsrfDir.exists();
        }
    
    	//just assume its not there if we are reinstalling
        if (isTrue(Constants.REINSTALL_TOMCAT)) {
            globusDeployed = false;
        }
        if (isTrue(Constants.REINSTALL_JBOSS)) {
            globusDeployed = false;
        }
    
        return globusDeployed;
    }


    public String getInstallerDir() {
        return InstallerUtils.buildInstallerDirPath(getProperty(Constants.CAGRID_VERSION));
    }


    public JComponent getOverviewComponent() {
        JPanel overviewPanel = new JPanel();
        ImageIcon myImage = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(
            "images/cagrid.jpeg"));
        overviewPanel.add(new JLabel(myImage));
        return overviewPanel;
    }
}
