package gov.nih.nci.cagrid.data.ui;

import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.ExtensionDataUtils;
import gov.nih.nci.cagrid.data.common.ExtensionDataManager;
import gov.nih.nci.cagrid.data.cql.CQLQueryProcessor;
import gov.nih.nci.cagrid.data.cql.ui.CQLQueryProcessorConfigUI;
import gov.nih.nci.cagrid.data.ui.browser.AdditionalJarsChangeListener;
import gov.nih.nci.cagrid.data.ui.browser.AdditionalJarsChangedEvent;
import gov.nih.nci.cagrid.data.ui.browser.ClassBrowserPanel;
import gov.nih.nci.cagrid.data.ui.browser.ClassSelectionEvent;
import gov.nih.nci.cagrid.data.ui.browser.ClassSelectionListener;
import gov.nih.nci.cagrid.data.ui.table.QueryProcessorParametersTable;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.property.ServicePropertiesProperty;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.cagrid.grape.GridApplication;
import org.cagrid.grape.utils.CompositeErrorDialog;

/** 
 *  QueryProcessorConfigPanel
 *  Panel to consolidate configuration of the Query Processor
 * 
 * @author David Ervin
 * 
 * @created Jun 27, 2007 8:58:22 AM
 * @version $Id: QueryProcessorConfigPanel.java,v 1.8 2008-11-03 18:19:36 dervin Exp $ 
 */
public class QueryProcessorConfigPanel extends DataServiceModificationSubPanel {
    
    private ClassBrowserPanel classBrowserPanel = null;
    private JPanel processorConfigurationPanel = null;
    private JScrollPane qpParamsScrollPane = null;
    private JButton launchProcessorConfigButton = null;
    private QueryProcessorParametersTable qpParamsTable = null;
    private JSplitPane configPartsSplitPane = null;

    public QueryProcessorConfigPanel(ServiceInformation serviceInfo, ExtensionDataManager dataManager) {
        super(serviceInfo, dataManager);
        initialize();
    }
    
    
    private void initialize() {
        setLayout(new GridBagLayout());
        GridBagConstraints cons = new GridBagConstraints();
        cons.gridx = 0;
        cons.gridy = 0;
        cons.fill = GridBagConstraints.BOTH;
        cons.weightx = 1.0D;
        cons.weighty = 1.0D;
        add(getConfigPartsSplitPane(), cons);
    }
    
    
    public void updateDisplayedConfiguration() {
        // check for change to class
        getClassBrowserPanel().populateFields();
        // repopulate qp params table based on service property values
        try {
            getQpParamsTable().populateProperties();
        } catch (Throwable ex) {
            ex.printStackTrace();
            CompositeErrorDialog.showErrorDialog("Error updating displayed properties", ex.getMessage(), ex);
        }
    }
    
    
    private ClassBrowserPanel getClassBrowserPanel() {
        if (classBrowserPanel == null) {
            classBrowserPanel = new ClassBrowserPanel(getExtensionDataManager(), getServiceInfo());
            // classBrowserPanel = new ClassBrowserPanel(null, null); //uncomment this line to edit in VE
            classBrowserPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
                "Query Processor Class Selection", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                null, PortalLookAndFeel.getPanelLabelColor()));
            // listen for class selection events
            classBrowserPanel.addClassSelectionListener(new ClassSelectionListener() {
                public void classSelectionChanged(ClassSelectionEvent e) {
                    // class selection changed...
                    // a) Blow away QP service properties
                    // b) Load new service properties into service properties
                    // c) Populate QP properties table
                    try {
                        saveProcessorClassName(classBrowserPanel.getSelectedClassName());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        CompositeErrorDialog.showErrorDialog(
                            "Error setting the query processor class: " + ex.getMessage(), ex);
                    }
                }
            });
            // listen for jar addition events
            classBrowserPanel.addAdditionalJarsChangeListener(new AdditionalJarsChangeListener() {
                public void additionalJarsChanged(AdditionalJarsChangedEvent e) {
                    // remove any existing qp jars element from the service data
                    String[] additionalJars = classBrowserPanel.getAdditionalJars();
                    try {
                        getExtensionDataManager().setAdditionalJars(additionalJars);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        CompositeErrorDialog.showErrorDialog("Error storing additional libraries information: "
                            + ex.getMessage(), ex);
                    }
                }
            });
        }
        return classBrowserPanel;
    }
    
    
    /**
     * This method initializes processorConfigurationPanel  
     *  
     * @return javax.swing.JPanel   
     */
    private JPanel getProcessorConfigurationPanel() {
        if (processorConfigurationPanel == null) {
            GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
            gridBagConstraints21.gridx = 0;
            gridBagConstraints21.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints21.gridy = 0;
            GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
            gridBagConstraints20.fill = GridBagConstraints.BOTH;
            gridBagConstraints20.gridx = 0;
            gridBagConstraints20.gridy = 1;
            gridBagConstraints20.weightx = 1.0D;
            gridBagConstraints20.weighty = 1.0D;
            gridBagConstraints20.insets = new Insets(6, 6, 6, 6);
            processorConfigurationPanel = new JPanel();
            processorConfigurationPanel.setLayout(new GridBagLayout());
            processorConfigurationPanel.add(getQpParamsScrollPane(), gridBagConstraints20);
            processorConfigurationPanel.add(getLaunchProcessorConfigButton(), gridBagConstraints21);
        }
        return processorConfigurationPanel;
    }
    
    
    private JScrollPane getQpParamsScrollPane() {
        if (qpParamsScrollPane == null) {
            qpParamsScrollPane = new JScrollPane();
            qpParamsScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
                "Processor Parameter Configuration", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, PortalLookAndFeel.getPanelLabelColor()));
            qpParamsScrollPane.setViewportView(getQpParamsTable());
        }
        return qpParamsScrollPane;
    }
    
    
    /**
     * This method initializes launchProcessorConfigButton  
     *  
     * @return javax.swing.JButton  
     */
    private JButton getLaunchProcessorConfigButton() {
        if (launchProcessorConfigButton == null) {
            launchProcessorConfigButton = new JButton();
            launchProcessorConfigButton.setText("Launch Query Processor Configurator");
            launchProcessorConfigButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    launchQueryProcessorConfigUi();
                }
            });
        }
        return launchProcessorConfigButton;
    }
    
    
    private QueryProcessorParametersTable getQpParamsTable() {
        if (qpParamsTable == null) {
            // comment out the following line to edit with VE
            qpParamsTable = new QueryProcessorParametersTable(getServiceInfo());
            // uncomment the following to edit with VE
            // qpParamsTable = new QueryProcessorParametersTable(null, null);
        }
        return qpParamsTable;
    }
    
    
    private JSplitPane getConfigPartsSplitPane() {
        if (configPartsSplitPane == null) {
            configPartsSplitPane = new JSplitPane();
            configPartsSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
            configPartsSplitPane.setTopComponent(getClassBrowserPanel());
            configPartsSplitPane.setBottomComponent(getProcessorConfigurationPanel());
            configPartsSplitPane.setOneTouchExpandable(false);
            configPartsSplitPane.setDividerLocation(0.5);
        }
        return configPartsSplitPane;
    }
    
    
    // -----------------------------------------
    // helpers
    // -----------------------------------------
    
    private void saveProcessorClassName(String className) throws Exception {
        // if property does not exist or current class is not same as selected, perform update
        if (isDifferentProcessorClass(className)) {
            // store the property
            CommonTools.setServiceProperty(getServiceInfo().getServiceDescriptor(),
                DataServiceConstants.QUERY_PROCESSOR_CLASS_PROPERTY, className, false);
            // remove all query processor config properties from the service properties
            ServicePropertiesProperty[] oldProperties = getServiceInfo().getServiceProperties().getProperty();
            List<ServicePropertiesProperty> keptProperties = new ArrayList<ServicePropertiesProperty>();
            for (ServicePropertiesProperty oldProp : oldProperties) {
                if (!oldProp.getKey().startsWith(DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX)) {
                    keptProperties.add(oldProp);
                }
            }
            ServicePropertiesProperty[] properties = new ServicePropertiesProperty[keptProperties.size()];
            keptProperties.toArray(properties);
            getServiceInfo().getServiceDescriptor().getServiceProperties().setProperty(properties);
            // inform the parameters table that the class name is different
            getQpParamsTable().classChanged();
        }
    }
    
    
    private boolean isDifferentProcessorClass(String className) throws Exception {
        ServiceDescription desc = getServiceInfo().getServiceDescriptor();
        boolean propertyExists = CommonTools.servicePropertyExists(
            desc, DataServiceConstants.QUERY_PROCESSOR_CLASS_PROPERTY);
        if (propertyExists) {
            String current = CommonTools.getServicePropertyValue(
                desc, DataServiceConstants.QUERY_PROCESSOR_CLASS_PROPERTY);
            return current == null || !current.equals(className);
        }
        return true;
    }
    
    
    private void launchQueryProcessorConfigUi() {
        String stubQpClassname = ExtensionDataUtils.getQueryProcessorStubClassName(getServiceInfo());
        String qpClassname = getClassBrowserPanel().getSelectedClassName();
        if (qpClassname != null && qpClassname.length() != 0) {
            Class uiClass = null;
            try {
                uiClass = getQueryProcessorConfigUIClass();
            } catch (Exception ex) {
                if (!(ex instanceof ClassNotFoundException && qpClassname.equals(stubQpClassname))) {
                    ex.printStackTrace();
                    CompositeErrorDialog.showErrorDialog("Error loading the query processor's configuration UI", 
                        ex.getMessage(), ex);
                }
            }
            if (uiClass != null) {
                Properties postUiConfig = null;
                try {
                    CQLQueryProcessorConfigUI uiPanel = 
                        (CQLQueryProcessorConfigUI) uiClass.newInstance();
                    // get the current configuration out of the table
                    Properties currentConfig = getQpParamsTable().getNonPrefixedConfiguredProperties();
                    postUiConfig = QueryProcessorConfigurationDialog
                        .showConfigurationUi(uiPanel, getServiceInfo().getBaseDirectory(), currentConfig);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    CompositeErrorDialog.showErrorDialog("Error executing query processor configuration UI", 
                        ex.getMessage(), ex);
                }
                if (postUiConfig != null) {
                    // store the configuration that came back from the UI config dialog
                    // start by removing the old query processor properties
                    ServicePropertiesProperty[] oldProperties = 
                        getServiceInfo().getServiceProperties().getProperty();
                    List<ServicePropertiesProperty> keptProperties = new ArrayList<ServicePropertiesProperty>();
                    for (ServicePropertiesProperty prop : oldProperties) {
                        if (!prop.getKey().startsWith(DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX)) {
                            keptProperties.add(prop);
                        }
                    }
                    // add the changed properties
                    Iterator postUiPropKeys = postUiConfig.keySet().iterator();
                    while (postUiPropKeys.hasNext()) {
                        String key = (String) postUiPropKeys.next();
                        String value = postUiConfig.getProperty(key);
                        String prefixedKey = DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + key;
                        ServicePropertiesProperty configProperty = new ServicePropertiesProperty();
                        configProperty.setKey(prefixedKey);
                        configProperty.setValue(value);
                        configProperty.setIsFromETC(Boolean.FALSE);
                        keptProperties.add(configProperty);
                    }
                    // set the properties into the model
                    ServicePropertiesProperty[] properties = 
                        new ServicePropertiesProperty[keptProperties.size()];
                    keptProperties.toArray(properties);
                    getServiceInfo().getServiceDescriptor().getServiceProperties().setProperty(properties);
                    // inform the parameters table that it should update
                    getQpParamsTable().classChanged();
                }
            } else {
                GridApplication.getContext().showMessage(new String[] {
                    "The query processor " + qpClassname, "did not supply a configuration UI"});
            }
        }
    }
    
    
    private Class getQueryProcessorConfigUIClass() throws Exception {
        String qpClassname = getClassBrowserPanel().getSelectedClassName();
        // reflect-load the class
        String[] libs = getJarFilenames();
        URL[] urls = new URL[libs.length];
        for (int i = 0; i < libs.length; i++) {
            File libFile = new File(libs[i]);
            urls[i] = libFile.toURL();
        }
        ClassLoader loader = new URLClassLoader(
            urls, Thread.currentThread().getContextClassLoader());
        Class qpClass = loader.loadClass(qpClassname);
        CQLQueryProcessor processorInstance = (CQLQueryProcessor) qpClass.newInstance();
        String configUiCLassname = processorInstance.getConfigurationUiClassname();
        if (configUiCLassname != null && configUiCLassname.length() != 0) {
            Class uiClass = loader.loadClass(configUiCLassname);
            return uiClass;
        }
        return null;
    }
    
    
    private String[] getJarFilenames() {
        String libDir = getServiceInfo().getBaseDirectory().getAbsolutePath()
            + File.separator + "lib";
        String[] qpJarNames = getClassBrowserPanel().getAdditionalJars();
        if (qpJarNames != null) {
            for (int i = 0; i < qpJarNames.length; i++) {
                qpJarNames[i] = libDir + File.separator + qpJarNames[i];
            }
        }
        return qpJarNames;
    }
}
