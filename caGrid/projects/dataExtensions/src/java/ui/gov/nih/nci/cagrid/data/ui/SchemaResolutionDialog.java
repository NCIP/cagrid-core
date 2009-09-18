package gov.nih.nci.cagrid.data.ui;

import gov.nih.nci.cagrid.common.portal.MultiEventProgressBar;
import gov.nih.nci.cagrid.data.ui.wizard.CacoreWizardUtils;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.configuration.NamespaceReplacementPolicy;
import gov.nih.nci.cagrid.introduce.beans.extension.DiscoveryExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;
import gov.nih.nci.cagrid.introduce.portal.extension.tools.ExtensionTools;
import gov.nih.nci.cagrid.introduce.portal.modification.discovery.NamespaceTypeDiscoveryComponent;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.cagrid.grape.GridApplication;
import org.cagrid.grape.utils.CompositeErrorDialog;


/**
 * SchemaResolutionDialog 
 * Dialog to resolve schemas from all available namespace
 * type discovery extension components
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @created Sep 27, 2006
 * @version $Id: SchemaResolutionDialog.java,v 1.9 2009-01-29 18:52:30 dervin Exp $
 */
public class SchemaResolutionDialog extends JDialog {

    public static final String SELECT_AN_ITEM = " -- SELECT AN ITEM --";

    private transient ServiceInformation serviceInfo;

    private JButton loadSchemasButton = null;
    private JButton cancelButton = null;
    private JPanel interactionPanel = null;
    private JPanel mainPanel = null;
    private JTabbedPane discoveryTabbedPane = null;
    private MultiEventProgressBar namespaceDiscoveryProgressBar = null;
    private NamespaceType[] resolvedSchemas;

    private SchemaResolutionDialog(ServiceInformation info) {
        super(GridApplication.getContext().getApplication(), 
            "Schema Resolution", true);
        this.serviceInfo = info;
        this.resolvedSchemas = null;
        initialize();
    }


    /**
     * Resolves schemas for the given cadsr package
     * 
     * @param info
     * @return null if an error occurs resolving schemas an empty array 
     *      (length == 0) if user cancels the dialog array of NamespaceType 
     *      (length != 0) if resolution was successful
     */
    public static NamespaceType[] resolveSchemas(ServiceInformation info) {
        SchemaResolutionDialog dialog = new SchemaResolutionDialog(info);
        return dialog.resolvedSchemas;
    }


    private void initialize() {
        setModal(true);
        this.setSize(new java.awt.Dimension(600, 500));
        this.setContentPane(getMainPanel());
        GridApplication.getContext().centerDialog(this);
        this.setVisible(true);
    }


    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getLoadSchemasButton() {
        if (this.loadSchemasButton == null) {
            this.loadSchemasButton = new JButton();
            this.loadSchemasButton.setText("Load Schemas");
            this.loadSchemasButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    SchemaResolutionDialog.this.resolvedSchemas = loadSchemas();
                    dispose();
                }
            });
        }
        return this.loadSchemasButton;
    }


    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getCancelButton() {
        if (this.cancelButton == null) {
            this.cancelButton = new JButton();
            this.cancelButton.setText("Cancel");
            this.cancelButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    SchemaResolutionDialog.this.resolvedSchemas = new NamespaceType[0];
                    dispose();
                }
            });
        }
        return this.cancelButton;
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getInteractionPanel() {
        if (this.interactionPanel == null) {
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.weightx = 1.0D;
            gridBagConstraints1.insets = new Insets(4, 4, 4, 4);
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.gridy = 0;
            GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
            gridBagConstraints15.gridx = 2;
            gridBagConstraints15.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints15.gridy = 0;
            GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
            gridBagConstraints14.gridx = 1;
            gridBagConstraints14.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints14.gridy = 0;
            this.interactionPanel = new JPanel();
            this.interactionPanel.setLayout(new GridBagLayout());
            interactionPanel.add(getLoadSchemasButton(), gridBagConstraints14);
            interactionPanel.add(getCancelButton(), gridBagConstraints15);
            interactionPanel.add(getNamespaceDiscoveryProgressBar(), gridBagConstraints1);
        }
        return this.interactionPanel;
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainPanel() {
        if (this.mainPanel == null) {
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.weighty = 1.0;
            gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints.gridx = 0;
            GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
            gridBagConstraints19.gridx = 0;
            gridBagConstraints19.anchor = java.awt.GridBagConstraints.EAST;
            gridBagConstraints19.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints19.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints19.gridy = 1;
            this.mainPanel = new JPanel();
            this.mainPanel.setLayout(new GridBagLayout());
            mainPanel.add(getInteractionPanel(), gridBagConstraints19);
            this.mainPanel.add(getDiscoveryTabbedPane(), gridBagConstraints);
        }
        return this.mainPanel;
    }


    private JTabbedPane getDiscoveryTabbedPane() {
        if (this.discoveryTabbedPane == null) {
            this.discoveryTabbedPane = new JTabbedPane();
            // get the discovery extensions from Introduce
            List discoveryTypes = ExtensionsLoader.getInstance().getDiscoveryExtensions();
            if (discoveryTypes != null) {
                Iterator discoIter = discoveryTypes.iterator();
                while (discoIter.hasNext()) {
                    final DiscoveryExtensionDescriptionType dd = (DiscoveryExtensionDescriptionType) discoIter.next();
                    Thread componentLoader = new Thread() {
                        public void run() {
                            int myId = getNamespaceDiscoveryProgressBar()
                                .startEvent("Loading " + dd.getDisplayName());
                            try {
                                NamespaceTypeDiscoveryComponent comp = ExtensionTools.getNamespaceTypeDiscoveryComponent(
                                    dd.getName(), serviceInfo.getNamespaces());
                                if (comp != null) {
                                    insertAndSortTab(comp, dd.getDisplayName());
                                    getNamespaceDiscoveryProgressBar().stopEvent(myId, dd.getDisplayName() + " complete");
                                }
                            } catch (Exception ex) {
                                getNamespaceDiscoveryProgressBar().stopEvent(myId, ex.getMessage());
                                ex.printStackTrace();
                                CompositeErrorDialog.showErrorDialog("Error adding type discovery component to dialog", ex);
                            }
                        }
                    };
                    componentLoader.start();
                }
            }
        }
        return this.discoveryTabbedPane;
    }
    
    
    private synchronized void insertAndSortTab(final NamespaceTypeDiscoveryComponent component, final String displayName) {
        List<String> titles = new ArrayList<String>();
        for (int i = 0; i < getDiscoveryTabbedPane().getTabCount(); i++) {
            titles.add(getDiscoveryTabbedPane().getTitleAt(i).toLowerCase());
        }
        titles.add(displayName.toLowerCase());
        Collections.sort(titles);
        final int insertIndex = titles.indexOf(displayName.toLowerCase());
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                getDiscoveryTabbedPane().insertTab(displayName, null, component, null, insertIndex);
            }
        });
    }
    
    
    private MultiEventProgressBar getNamespaceDiscoveryProgressBar() {
        if (namespaceDiscoveryProgressBar == null) {
            namespaceDiscoveryProgressBar = new MultiEventProgressBar(false);
        }
        return namespaceDiscoveryProgressBar;
    }


    private NamespaceType[] loadSchemas() {
        // get the discovery type component
        NamespaceTypeDiscoveryComponent discComponent = (NamespaceTypeDiscoveryComponent) getDiscoveryTabbedPane()
            .getSelectedComponent();
        // get the service's schema directory
        File schemaDir = new File(CacoreWizardUtils.getServiceBaseDir(this.serviceInfo)
            + File.separator + "schema" + File.separator
            + this.serviceInfo.getIntroduceServiceProperties().getProperty(
                IntroduceConstants.INTRODUCE_SKELETON_SERVICE_NAME));

        NamespaceType[] namespaces = discComponent.createNamespaceType(schemaDir, 
            NamespaceReplacementPolicy.REPLACE,
            getNamespaceDiscoveryProgressBar());
        if (namespaces == null) {
            CompositeErrorDialog.showErrorDialog("Error getting types from discovery component");
        }
        return namespaces;
    }
}
