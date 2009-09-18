package gov.nih.nci.cagrid.introduce.portal.undeployment;

import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.codegen.SyncTools;
import gov.nih.nci.cagrid.introduce.common.ResourceManager;
import gov.nih.nci.cagrid.introduce.portal.common.IntroduceLookAndFeel;
import gov.nih.nci.cagrid.introduce.portal.deployment.DeploymentViewer;
import gov.nih.nci.cagrid.introduce.portal.undeployment.service.DeployedServicesTable;
import gov.nih.nci.cagrid.introduce.servicetasks.beans.deployment.Deployment;
import gov.nih.nci.cagrid.introduce.servicetasks.undeployment.UndeployServiceHelper;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.util.Iterator;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;
import org.cagrid.grape.ApplicationComponent;
import org.cagrid.grape.utils.CompositeErrorDialog;
import org.cagrid.grape.utils.ErrorDialog;

import javax.swing.JScrollPane;


public class UndeployServiceViewer extends ApplicationComponent {
    
    private static final Logger logger = Logger.getLogger(UndeployServiceViewer.class);

    private JPanel mainPanel = null;

    private JPanel servicePanel = null;

    private JPanel containrePanel = null;

    private JLabel containerLabel = null;

    private JComboBox containerSelectorComboBox = null;

    private JPanel buttonPanel = null;

    private JButton undeployButton = null;

    private DeployedServicesTable servicesTable = null;

    private JScrollPane servicesScrollPane = null;


    /**
     * This method initializes
     */
    public UndeployServiceViewer() {
        super();
        initialize();
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        this.setSize(new Dimension(496, 303));
        this.setContentPane(getMainPanel());
        this.setTitle("Undeploy Service");

    }


    /**
     * This method initializes mainPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridy = 2;
            gridBagConstraints11.fill = GridBagConstraints.BOTH;
            gridBagConstraints11.weightx = 0.0D;
            gridBagConstraints11.weighty = 0.0D;
            gridBagConstraints11.gridx = 0;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.fill = GridBagConstraints.BOTH;
            gridBagConstraints1.weightx = 0.0D;
            gridBagConstraints1.weighty = 0.0D;
            gridBagConstraints1.gridy = 0;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.weightx = 1.0D;
            gridBagConstraints.weighty = 1.0D;
            gridBagConstraints.gridy = 1;
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.add(getServicePanel(), gridBagConstraints);
            mainPanel.add(getContainrePanel(), gridBagConstraints1);
            mainPanel.add(getButtonPanel(), gridBagConstraints11);
        }
        return mainPanel;
    }


    /**
     * This method initializes servicePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getServicePanel() {
        if (servicePanel == null) {
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.fill = GridBagConstraints.BOTH;
            gridBagConstraints5.weighty = 1.0;
            gridBagConstraints5.weightx = 1.0;
            servicePanel = new JPanel();
            servicePanel.setLayout(new GridBagLayout());
            servicePanel.setBorder(BorderFactory.createTitledBorder(null, "Deployed Services",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12),
                IntroduceLookAndFeel.getPanelLabelColor()));
            servicePanel.add(getServicesScrollPane(), gridBagConstraints5);
        }
        return servicePanel;
    }


    /**
     * This method initializes containrePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getContainrePanel() {
        if (containrePanel == null) {
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridy = 0;
            gridBagConstraints3.weightx = 1.0;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.gridx = 1;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.gridy = 0;
            containerLabel = new JLabel();
            containerLabel.setText("Container");
            containrePanel = new JPanel();
            containrePanel.setLayout(new GridBagLayout());
            containrePanel.add(containerLabel, gridBagConstraints2);
            containrePanel.add(getContainerSelectorComboBox(), gridBagConstraints3);
        }
        return containrePanel;
    }


    /**
     * This method initializes containerSelectorComboBox
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getContainerSelectorComboBox() {
        if (containerSelectorComboBox == null) {
            containerSelectorComboBox = new JComboBox();
            if (System.getenv(IntroduceConstants.TOMCAT) != null) {
                containerSelectorComboBox.addItem(DeploymentViewer.TOMCAT);
            }
            if (System.getenv(IntroduceConstants.GLOBUS) != null) {
                containerSelectorComboBox.addItem(DeploymentViewer.GLOBUS);
            }
            if (System.getenv(IntroduceConstants.JBOSS) != null) {
                containerSelectorComboBox.addItem(DeploymentViewer.JBOSS);
            }

            try {
                if (ResourceManager.getStateProperty(ResourceManager.LAST_DEPLOYMENT) != null) {
                    boolean found = false;
                    for (int i = 0; i < containerSelectorComboBox.getItemCount(); i++) {
                        if (((String) containerSelectorComboBox.getItemAt(i)).equals(ResourceManager
                            .getStateProperty(ResourceManager.LAST_DEPLOYMENT))) {
                            found = true;
                        }
                    }
                    if (found) {
                        containerSelectorComboBox.setSelectedItem(ResourceManager
                            .getStateProperty(ResourceManager.LAST_DEPLOYMENT));
                    }
                }
            } catch (Exception e) {
                logger.error(e);
            }

            processContainerSelection();

            containerSelectorComboBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    processContainerSelection();
                }
            });
        }
        return containerSelectorComboBox;
    }


    private void processContainerSelection() {
        String containerType = (String) containerSelectorComboBox.getSelectedItem();
        try {
            getServicesTable().removeAllRows();
        } catch (Exception e2) {
            e2.printStackTrace();
            return;
        }
        String webAppDir = null;
        String webAppEtcDir = null;
        if (containerType.equals(DeploymentViewer.GLOBUS)) {
            webAppDir = System.getenv("GLOBUS_LOCATION");
            webAppEtcDir = webAppDir + File.separator + "etc";
        } else if (containerType.equals(DeploymentViewer.TOMCAT)) {
            webAppDir = System.getenv("CATALINA_HOME") + File.separator + "webapps" + File.separator + "wsrf";
            webAppEtcDir = webAppDir + File.separator + "WEB-INF" + File.separator + "etc";
        } else if (containerType.equals(DeploymentViewer.JBOSS)) {
            webAppDir = System.getenv("JBOSS_HOME") + File.separator + "server" + File.separator + "default"
                + File.separator + "deploy" + File.separator + "wsrf.war";
            webAppEtcDir = webAppDir + File.separator + "WEB-INF" + File.separator + "etc";
        }
        Map<String, Deployment> map = null;
        try {
            map = UndeployServiceHelper.loadIntroduceServices(webAppEtcDir);
        } catch (Exception e1) {
            e1.printStackTrace();
            return;
        }
        Iterator<Deployment> it = map.values().iterator();
        while (it.hasNext()) {
            getServicesTable().addRow(it.next());
        }
    }


    /**
     * This method initializes buttonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints4.gridy = 0;
            buttonPanel = new JPanel();
            buttonPanel.setLayout(new GridBagLayout());
            buttonPanel.add(getUndeployButton(), gridBagConstraints4);
        }
        return buttonPanel;
    }


    /**
     * This method initializes undeployButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getUndeployButton() {
        if (undeployButton == null) {
            undeployButton = new JButton();
            undeployButton.setText("Undeploy");
            undeployButton.setEnabled(false);
            undeployButton.setIcon(IntroduceLookAndFeel.getUndeployIcon());
            undeployButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        if (getServicesTable().getSelectedRow() >= 0) {
                            Deployment dep = getServicesTable().getSelectedRowData();
                            if (dep != null) {

                                // build up properties that the undeploy
                                // helper will need to undeploy the service
                                String containerType = (String) containerSelectorComboBox.getSelectedItem();
                                getServicesTable().removeAll();
                                String webAppDir = null;
                                String webAppEtcDir = null;
                                String webAppLibDir = null;
                                String webAppSchemaDir = null;
                                if (containerType.equals(DeploymentViewer.GLOBUS)) {
                                    webAppDir = System.getenv("GLOBUS_LOCATION");
                                    webAppEtcDir = webAppDir + File.separator + "etc";
                                    webAppLibDir = webAppDir + File.separator + "lib";
                                    webAppSchemaDir = webAppDir + File.separator + "share" + File.separator + "schema";
                                } else if (containerType.equals(DeploymentViewer.TOMCAT)) {
                                    webAppDir = System.getenv("CATALINA_HOME") + File.separator + "webapps"
                                        + File.separator + "wsrf";
                                    webAppEtcDir = webAppDir + File.separator + "WEB-INF" + File.separator + "etc";
                                    webAppLibDir = webAppDir + File.separator + "WEB-INF" + File.separator + "lib";
                                    webAppSchemaDir = webAppDir + File.separator + "share" + File.separator + "schema";
                                } else if (containerType.equals(DeploymentViewer.JBOSS)) {
                                    webAppDir = System.getenv("JBOSS_HOME") + File.separator + "server"
                                        + File.separator + "default" + File.separator + "deploy" + File.separator
                                        + "wsrf.war";
                                    webAppEtcDir = webAppDir + File.separator + "WEB-INF" + File.separator + "etc";
                                    webAppLibDir = webAppDir + File.separator + "WEB-INF" + File.separator + "lib";
                                    webAppSchemaDir = webAppDir + File.separator + "share" + File.separator + "schema";
                                }

                                // use the undeploy helper to remove the
                                // service
                                UndeployServiceHelper helper = new UndeployServiceHelper(webAppDir, webAppLibDir,
                                    webAppSchemaDir, webAppEtcDir, dep.getServiceDeploymentDirName(), dep
                                        .getDeploymentPrefix(), dep.getServiceName());
                                helper.execute();

                                // force refresh on the services table
                                getContainerSelectorComboBox().setSelectedItem(
                                    getContainerSelectorComboBox().getSelectedItem());
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        ErrorDialog.showError("Undeployment Error" ,ex);
                    }
                }
            });

        }
        return undeployButton;
    }


    /**
     * This method initializes servicesTable
     * 
     * @return javax.swing.JTable
     */
    private DeployedServicesTable getServicesTable() {
        if (servicesTable == null) {
            servicesTable = new DeployedServicesTable();
            servicesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
				public void valueChanged(ListSelectionEvent e) {
				    int row = servicesTable.getSelectedRow();
				    if(row < 0 || row >= servicesTable.getRowCount()){
				        getUndeployButton().setEnabled(false);
				    } else {
				        getUndeployButton().setEnabled(true);
				    }
				} 
			
			});
        }
        return servicesTable;
    }


    /**
     * This method initializes servicesScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getServicesScrollPane() {
        if (servicesScrollPane == null) {
            servicesScrollPane = new JScrollPane();
            servicesScrollPane.setViewportView(getServicesTable());
        }
        return servicesScrollPane;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
