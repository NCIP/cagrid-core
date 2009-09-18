package org.cagrid.cadsr.portal.discovery;

import gov.nih.nci.cadsr.umlproject.domain.Project;
import gov.nih.nci.cadsr.umlproject.domain.UMLPackageMetadata;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.utilities.dmviz.DomainModelVisualizationPanel;
import gov.nih.nci.cagrid.introduce.beans.extension.DiscoveryExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionDescription;
import gov.nih.nci.cagrid.introduce.common.ConfigurationUtil;
import gov.nih.nci.cagrid.introduce.portal.discoverytools.NamespaceTypeToolsComponent;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.cadsr.portal.CaDSRBrowserPanel;
import org.cagrid.cadsr.portal.PackageSelectedListener;
import org.cagrid.cadsr.portal.ProjectSelectedListener;
import org.cagrid.grape.utils.CompositeErrorDialog;
import org.cagrid.mms.client.MetadataModelServiceClient;
import org.cagrid.mms.domain.UMLProjectIdentifer;
import org.cagrid.mms.stubs.types.InvalidUMLProjectIndentifier;


/**
 * @author oster
 */

public class CaDSRTypeDiscoveryComponent extends NamespaceTypeToolsComponent
    implements
        PackageSelectedListener,
        ProjectSelectedListener {
    private CaDSRBrowserPanel caDSRPanel = null;
    private JPanel graphPanel = null;
    private DomainModelVisualizationPanel umlDiagram;
    private JPanel refreshPanel = null;
    private JButton refreshButton = null;


    /**
     * @param desc
     */
    public CaDSRTypeDiscoveryComponent(DiscoveryExtensionDescriptionType desc) {
        super(desc);
        initialize();
        this.getCaDSRPanel().setDefaultCaDSRURL(getCaDSRURL());
        this.getCaDSRPanel().discoverFromCaDSR();
    }


    private String getCaDSRURL() {
        try {
            return ConfigurationUtil
                .getGlobalExtensionProperty(CaDSRDiscoveryConstants.CADSR_DATA_SERVICE_URL_PROPERTY).getValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private String getMMSURL() {
        try {
            return ConfigurationUtil.getGlobalExtensionProperty(CaDSRDiscoveryConstants.MMS_URL_PROPERTY).getValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private void initialize() {
        GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
        gridBagConstraints11.gridy = 1;
        gridBagConstraints11.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints11.gridx = 0;
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.weightx = 1.0D;
        gridBagConstraints1.weighty = 1.0D;
        gridBagConstraints1.gridy = 2;
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 0.0D;
        gridBagConstraints.weighty = 0.0D;
        this.setLayout(new GridBagLayout());
        this.add(getCaDSRPanel(), gridBagConstraints);
        this.add(getRefreshPanel(), gridBagConstraints11);
        this.add(getGraphPanel(), gridBagConstraints1);
    }


    /**
     * This method initializes cadsrPanel
     * 
     * @return javax.swing.JPanel
     */
    private CaDSRBrowserPanel getCaDSRPanel() {
        if (this.caDSRPanel == null) {
            this.caDSRPanel = new CaDSRBrowserPanel(false, false);
            this.caDSRPanel.addPackageSelectionListener(this);
            this.caDSRPanel.addProjectSelectionListener(this);
        }
        return this.caDSRPanel;
    }


    public void handleProjectSelection(Project project) {
    }


    public void handlePackageSelection(final UMLPackageMetadata pkg) {
        // update the graph for the given package
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    getRefreshButton().setEnabled(false);
                    final int progressEventID = getCaDSRPanel().getMultiEventProgressBar().startEvent(
                        "Processing Package " + pkg.getName());

                    MetadataModelServiceClient mms = null;
                    try {
                        mms = new MetadataModelServiceClient(getMMSURL());
                    } catch (MalformedURIException e) {
                        e.printStackTrace();
                        CompositeErrorDialog.showErrorDialog("Invalid MMS URL (" + getMMSURL()
                            + "); please check the MMS URL in the preferences!", e);
                        return;
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        CompositeErrorDialog.showErrorDialog("Error communicating with MMS (" + getMMSURL()
                            + "); please check the MMS URL!", e);
                        getCaDSRPanel().getMultiEventProgressBar().stopAll(
                            "Error communicating with MMS (" + getMMSURL() + "); please check the MMS URL!");
                        // getUMLDiagram().clear();
                        return;
                    }

                    UMLProjectIdentifer umlProjectIdentifer = new UMLProjectIdentifer();
                    umlProjectIdentifer.setIdentifier(getCaDSRPanel().getSelectedProject().getShortName());
                    umlProjectIdentifer.setVersion(getCaDSRPanel().getSelectedProject().getVersion());

                    String[] packageNames = new String[]{getCaDSRPanel().getSelectedPackage().getName()};

                    final int genProgressEventID = getCaDSRPanel().getMultiEventProgressBar().startEvent(
                        "Generating Model...");
                    DomainModel domainModel = mms.generateDomainModelForPackages(umlProjectIdentifer, packageNames);
                    getCaDSRPanel().getMultiEventProgressBar().stopEvent(genProgressEventID, "Done with model.");

                    final int renderProgressEventID = getCaDSRPanel().getMultiEventProgressBar().startEvent(
                        "Rendering...");
                    getUMLDiagram().setDomainModel(domainModel);
                    getCaDSRPanel().getMultiEventProgressBar().stopEvent(renderProgressEventID, "Done with Rendering.");
                    getCaDSRPanel().getMultiEventProgressBar().stopEvent(progressEventID, "Done with Package.");

                } catch (InvalidUMLProjectIndentifier e) {
                    e.printStackTrace();
                    CompositeErrorDialog.showErrorDialog("Invalid project specified:" + e.getMessage(), e);
                    getCaDSRPanel().getMultiEventProgressBar().stopAll("Error processing model!");
                } catch (RemoteException e) {
                    e.printStackTrace();
                    CompositeErrorDialog.showErrorDialog("Error processing model!", e);
                    getCaDSRPanel().getMultiEventProgressBar().stopAll("Error processing model!");
                } catch (Exception e) {
                    e.printStackTrace();
                    CompositeErrorDialog.showErrorDialog("Error processing model!", e);
                    getCaDSRPanel().getMultiEventProgressBar().stopAll("Error processing model!");
                } finally {
                    getRefreshButton().setEnabled(true);
                }
            }
        };
        t.start();
    }


    /**
     * This method initializes graphPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getGraphPanel() {
        if (this.graphPanel == null) {
            this.graphPanel = new JPanel();
            this.graphPanel.setLayout(new GridBagLayout());
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints1.weightx = 1.0D;
            gridBagConstraints1.weighty = 1.0D;
            gridBagConstraints1.gridy = 1;
            this.graphPanel.add(getUMLDiagram(), gridBagConstraints1);

        }
        return this.graphPanel;
    }


    private DomainModelVisualizationPanel getUMLDiagram() {
        if (this.umlDiagram == null) {
            this.umlDiagram = new DomainModelVisualizationPanel();

        }
        return this.umlDiagram;
    }


    /**
     * This method initializes refreshPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getRefreshPanel() {
        if (this.refreshPanel == null) {
            this.refreshPanel = new JPanel();
            this.refreshPanel.add(getRefreshButton(), null);
        }
        return this.refreshPanel;
    }


    /**
     * This method initializes refreshButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getRefreshButton() {
        if (this.refreshButton == null) {
            this.refreshButton = new JButton();
            this.refreshButton.setText("Refresh");
            this.refreshButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    getUMLDiagram().setDomainModel(null);
                    getCaDSRPanel().getCadsr().setText(getCaDSRURL());
                    getCaDSRPanel().discoverFromCaDSR();
                }
            });
        }
        return this.refreshButton;
    }


    public static void main(String[] args) {
        try {
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            ExtensionDescription ext = (ExtensionDescription) Utils.deserializeDocument("../cadsr/extension.xml",
                ExtensionDescription.class);
            final CaDSRTypeDiscoveryComponent panel = new CaDSRTypeDiscoveryComponent(ext
                .getDiscoveryExtensionDescription());
            frame.getContentPane().setLayout(new BorderLayout());
            frame.getContentPane().add(panel, BorderLayout.CENTER);

            frame.pack();
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
