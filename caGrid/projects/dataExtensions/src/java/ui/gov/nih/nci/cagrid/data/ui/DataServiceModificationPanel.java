package gov.nih.nci.cagrid.data.ui;

import gov.nih.nci.cagrid.data.common.ExtensionDataManager;
import gov.nih.nci.cagrid.data.style.ServiceStyleContainer;
import gov.nih.nci.cagrid.data.style.ServiceStyleLoader;
import gov.nih.nci.cagrid.data.ui.auditors.AuditorsConfigurationPanel;
import gov.nih.nci.cagrid.data.ui.domain.DomainModelConfigPanel;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.portal.extension.ServiceModificationUIPanel;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.grape.utils.CompositeErrorDialog;

/**
 * DataServiceModificationPanel 
 * Panel for configuring a caGrid data service from
 * within the Introduce Toolkit
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @created Oct 10, 2006
 * @version $Id: DataServiceModificationPanel.java,v 1.10 2009-01-29 18:52:47 dervin Exp $
 */
public class DataServiceModificationPanel extends ServiceModificationUIPanel {
    
    private static final Log LOG = LogFactory.getLog(DataServiceModificationPanel.class.getName()); 

    private DomainModelConfigPanel domainConfigPanel = null;
	private JTabbedPane mainTabbedPane = null;
	private QueryProcessorConfigPanel processorConfigPanel = null;
	private DetailsConfigurationPanel detailConfigPanel = null;
    private AuditorsConfigurationPanel auditorConfigPanel = null;

    private transient ExtensionDataManager dataManager = null;

	public DataServiceModificationPanel(ServiceExtensionDescriptionType desc, ServiceInformation info) {
		super(desc, info);
        dataManager = new ExtensionDataManager(getExtensionTypeExtensionData());
		initialize();
	}


	private void initialize() {
		setLayout(new GridBagLayout());
		GridBagConstraints cons = new GridBagConstraints();
		cons.gridx = 0;
		cons.gridy = 0;
		cons.weightx = 1.0D;
		cons.weighty = 1.0D;
		cons.fill = GridBagConstraints.BOTH;
		add(getMainTabbedPane(), cons);
	}


	protected void resetGUI() {
        // only need to update the currently displayed tab
        Component visibleTab = getMainTabbedPane().getSelectedComponent();
        if (visibleTab instanceof UpdatablePanel) {
            try {
                String tabName = getMainTabbedPane().getTitleAt(getMainTabbedPane().getSelectedIndex());
                long tabStart = System.currentTimeMillis();
                ((UpdatablePanel) visibleTab).updateDisplayedConfiguration();
                LOG.debug("Tab " + tabName + " updated in " + (System.currentTimeMillis() - tabStart) + " ms");
            } catch (Exception ex) {
                ex.printStackTrace();
                CompositeErrorDialog.showErrorDialog("Error updating information on " 
                    + getMainTabbedPane().getTitleAt(
                        getMainTabbedPane().getSelectedIndex()), ex.getMessage(), ex);
            }
        }
	}
    

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private DomainModelConfigPanel getDomainConfigPanel() {
		if (domainConfigPanel == null) {
            domainConfigPanel = new DomainModelConfigPanel(getServiceInfo(), dataManager);
            domainConfigPanel.addClassSelectionListener(new gov.nih.nci.cagrid.data.ui.domain.DomainModelClassSelectionListener() {
                public void classSelected(String packName, String className, NamespaceType packageNamespace) {
                    getDetailConfigPanel().getClassConfigTable().addClass(packName, className, packageNamespace);
                }
                
                
                public void classDeselected(String packageName, String className) {
                    getDetailConfigPanel().getClassConfigTable().removeRow(packageName, className);
                    try {
                        dataManager.setClassSelectedInModel(packageName, className, false);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        CompositeErrorDialog.showErrorDialog("Error setting class selection state in model",
                            ex.getMessage(), ex);
                    }
                }
                
                
                public void classesCleared() {
                    getDetailConfigPanel().getClassConfigTable().clearTable();
                }
            });
            domainConfigPanel.updateDisplayedConfiguration();
		}
		return domainConfigPanel;
	}


	/**
	 * This method initializes jTabbedPane
	 * 
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getMainTabbedPane() {
		if (mainTabbedPane == null) {
			mainTabbedPane = new JTabbedPane();
            // when the tab changes, update values on all the tabs
            mainTabbedPane.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    resetGUI();
                }
            });
            
            // load tabs into the tabbed pane
			mainTabbedPane.addTab("Domain Model", null, getDomainConfigPanel(), 
                "Selection of packages and classes in domain model");
			mainTabbedPane.addTab("Query Processor", null, getProcessorConfigPanel(), 
                "Selection and configuration of the CQL query processor");
			mainTabbedPane.addTab("Details", null, getDetailConfigPanel(),
				"Class to element mapping, serialization, validation");
            mainTabbedPane.addTab("Auditing", null, getAuditorConfigPanel(),
                "Optional selection and configuration of auditors");
            
            // tab for the service style
            try {
                String styleName = dataManager.getServiceStyle();
                if (styleName != null) {
                    ServiceStyleContainer styleContainer = ServiceStyleLoader.getStyle(styleName);
                    if (styleContainer == null) {
                        String[] message = {
                            "This service specifies the " + styleName + " service style,",
                            "but the style could not be loaded.  Please check that it is ",
                            "installed, properly configured, and compatible with this ",
                            "version of the Introduce toolkit."
                        };
                        CompositeErrorDialog.showErrorDialog("Service style " + styleName + " not found", message);
                    } else {
                        DataServiceModificationSubPanel panel = StyleUiLoader.loadModificationUiPanel(
                            styleContainer, getServiceInfo(), dataManager);
                        if (panel != null) {
                            mainTabbedPane.addTab(styleName, null, panel, 
                                "Configuration for the " + styleName + " data service style");
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                CompositeErrorDialog.showErrorDialog(
                    "Error loading service style configuration tab", ex.getMessage(), ex);
            }
            
            resetGUI();
		}
		return mainTabbedPane;
	}


	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private QueryProcessorConfigPanel getProcessorConfigPanel() {
		if (processorConfigPanel == null) {
			processorConfigPanel = new QueryProcessorConfigPanel(
                getServiceInfo(), dataManager);
		}
		return processorConfigPanel;
	}


	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private DetailsConfigurationPanel getDetailConfigPanel() {
		if (detailConfigPanel == null) {
			detailConfigPanel = new DetailsConfigurationPanel(getServiceInfo(), dataManager);
		}
		return detailConfigPanel;
	}
    
    
    private AuditorsConfigurationPanel getAuditorConfigPanel() {
        if (auditorConfigPanel == null) {
            auditorConfigPanel = new AuditorsConfigurationPanel(getServiceInfo(), dataManager);
        }
        return auditorConfigPanel;
    }
}