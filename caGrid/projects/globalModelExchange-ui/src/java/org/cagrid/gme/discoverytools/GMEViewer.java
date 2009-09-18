package org.cagrid.gme.discoverytools;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;
import gov.nih.nci.cagrid.introduce.beans.extension.DiscoveryExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionDescription;
import gov.nih.nci.cagrid.introduce.common.ConfigurationUtil;
import gov.nih.nci.cagrid.introduce.common.ResourceManager;
import gov.nih.nci.cagrid.introduce.portal.common.jedit.JEditTextArea;
import gov.nih.nci.cagrid.introduce.portal.common.jedit.XMLTokenMarker;
import gov.nih.nci.cagrid.introduce.portal.discoverytools.NamespaceTypeToolsComponent;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gme.client.GlobalModelExchangeClient;
import org.cagrid.gme.domain.XMLSchema;
import org.cagrid.gme.domain.XMLSchemaDocument;
import org.cagrid.gme.domain.XMLSchemaNamespace;
import org.cagrid.grape.utils.CompositeErrorDialog;

import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationResult;


/**
 * CreationViewer
 * 
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @created Jun 22, 2005
 * @version $Id: mobiusEclipseCodeTemplates.xml,v 1.2 2005/04/19 14:58:02 oster
 *          Exp $
 */
public class GMEViewer extends NamespaceTypeToolsComponent
    implements
        ValidationStatusChangeListener,
        XMLSchemaDocumentSelectionListener {

    private static final Log logger = LogFactory.getLog(GMEViewer.class);

    private JPanel mainPanel = null;

    private JTabbedPane gmeToolsTabs = null;

    private JPanel schemaUploadPanel = null;

    private JPanel gmeBrowsePanel = null;

    private JPanel schemaViewer = null;

    private JPanel gmeViewerPanel = null;

    private GMESchemaLocatorPanel gmeSchemaLocatorPanel = null;

    private JButton uploadUploadButton = null;

    private JPanel uploadSchemaViewPanel = null;

    private JPanel gmeBrowseButtonPanel = null;

    private JButton gmeDownloadButton = null;

    private JEditTextArea uploadSchemaTextPane = null;

    private JEditTextArea schemaTextPane = null;

    private XMLSchemaListPanel xmlSchemaListPanel = null;


    /**
     * This method initializes
     */
    public GMEViewer(DiscoveryExtensionDescriptionType descriptor) {
        super(descriptor);
        initialize();
        getGmeSchemaLocatorPanel().discoverFromGME();
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
        gridBagConstraints11.insets = new java.awt.Insets(5, 0, 0, 0);
        gridBagConstraints11.gridy = 0;
        gridBagConstraints11.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints11.weightx = 1.0D;
        gridBagConstraints11.weighty = 1.0D;
        gridBagConstraints11.gridx = 0;
        this.setLayout(new GridBagLayout());
        this.add(getMainPanel(), gridBagConstraints11);
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
            gridBagConstraints.gridx = 0;
            this.mainPanel = new JPanel();
            this.mainPanel.setLayout(new GridBagLayout());
            this.mainPanel.add(getGmeToolsTabs(), gridBagConstraints);
        }
        return this.mainPanel;
    }


    /**
     * This method initializes gmeToolsTabs
     * 
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getGmeToolsTabs() {
        if (this.gmeToolsTabs == null) {
            this.gmeToolsTabs = new JTabbedPane();
            this.gmeToolsTabs.addTab("Browse", null, getGmeBrowsePanel(), null);
            this.gmeToolsTabs.addTab("Upload", null, getSchemaUploadPanel(), null);
        }
        return this.gmeToolsTabs;
    }


    /**
     * This method initializes schemaUploadPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getSchemaUploadPanel() {
        if (this.schemaUploadPanel == null) {

            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.gridy = 0;
            gridBagConstraints5.weightx = 1.0;
            gridBagConstraints5.weighty = 1.0;
            gridBagConstraints5.fill = GridBagConstraints.BOTH;

            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.gridx = 0;
            gridBagConstraints7.gridy = 1;
            gridBagConstraints7.weightx = 1.0;
            gridBagConstraints7.weighty = 1.0;

            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.gridx = 0;
            gridBagConstraints8.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints8.gridy = 2;
            gridBagConstraints8.weightx = .5;
            gridBagConstraints8.weighty = .5;

            this.schemaUploadPanel = new JPanel();
            this.schemaUploadPanel.setLayout(new GridBagLayout());
            this.schemaUploadPanel.add(getUploadUploadButton(), gridBagConstraints7);
            this.schemaUploadPanel.add(getUploadSchemaViewPanel(), gridBagConstraints8);
            this.schemaUploadPanel.add(getXmlSchemaListPanel(), gridBagConstraints5);
        }
        return this.schemaUploadPanel;
    }


    /**
     * This method initializes gmeBrowsePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getGmeBrowsePanel() {
        if (this.gmeBrowsePanel == null) {
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.gridx = 0;
            gridBagConstraints10.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints10.gridy = 2;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints1.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints1.weightx = 0.0D;
            gridBagConstraints1.weighty = 0.0D;
            gridBagConstraints1.gridy = 0;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints2.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints2.weightx = 1.0D;
            gridBagConstraints2.weighty = 1.0D;
            gridBagConstraints2.gridy = 1;
            this.gmeBrowsePanel = new JPanel();
            this.gmeBrowsePanel.setLayout(new GridBagLayout());
            this.gmeBrowsePanel.add(getSchemaViewer(), gridBagConstraints2);
            this.gmeBrowsePanel.add(getGmeViewerPanel(), gridBagConstraints1);
            this.gmeBrowsePanel.add(getGmeBrowseButtonPanel(), gridBagConstraints10);
        }
        return this.gmeBrowsePanel;
    }


    /**
     * This method initializes schemaViewer
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getSchemaViewer() {
        if (this.schemaViewer == null) {
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints3.gridy = 0;
            gridBagConstraints3.weightx = 1.0;
            gridBagConstraints3.weighty = 1.0;
            gridBagConstraints3.gridx = 0;
            this.schemaViewer = new JPanel();
            this.schemaViewer.setLayout(new GridBagLayout());
            this.schemaViewer.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Schema Root Document Text",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, PortalLookAndFeel.getPanelLabelColor()));
            this.schemaViewer.add(getSchemaTextPane(), gridBagConstraints3);
        }
        return this.schemaViewer;
    }


    /**
     * This method initializes gmeViewerPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getGmeViewerPanel() {
        if (this.gmeViewerPanel == null) {
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints4.weightx = 1.0D;
            gridBagConstraints4.weighty = 1.0D;
            gridBagConstraints4.gridy = 0;
            this.gmeViewerPanel = new JPanel();
            this.gmeViewerPanel.setLayout(new GridBagLayout());
            this.gmeViewerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Schema Locator",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, PortalLookAndFeel.getPanelLabelColor()));
            this.gmeViewerPanel.add(getGmeSchemaLocatorPanel(), gridBagConstraints4);
        }
        return this.gmeViewerPanel;
    }


    /**
     * This method initializes gmeSchemaLocatorPanel
     * 
     * @return javax.swing.JPanel
     */
    private GMESchemaLocatorPanel getGmeSchemaLocatorPanel() {
        if (this.gmeSchemaLocatorPanel == null) {
            this.gmeSchemaLocatorPanel = new GMESchemaLocatorPanel();
            this.gmeSchemaLocatorPanel.getSchemaComboBox().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    if (GMEViewer.this.gmeSchemaLocatorPanel.currentSchema != null) {
                        try {
                            getSchemaTextPane().setText(
                                XMLUtilities.formatXML(GMEViewer.this.gmeSchemaLocatorPanel.currentSchema
                                    .getRootDocument().getSchemaText()));
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        getSchemaTextPane().setCaretPosition(0);
                    }
                }
            });
        }
        return this.gmeSchemaLocatorPanel;
    }


    /**
     * This method initializes uploadUploadButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getUploadUploadButton() {
        if (this.uploadUploadButton == null) {
            this.uploadUploadButton = new JButton();
            this.uploadUploadButton.setEnabled(false);
            this.uploadUploadButton.setText("Publish Schemas");
            this.uploadUploadButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {

                    try {
                        GlobalModelExchangeClient gme = new GlobalModelExchangeClient(ConfigurationUtil
                            .getGlobalExtensionProperty(GMESchemaLocatorPanel.GME_URL).getValue());

                        List<XMLSchema> schemas = getXmlSchemaListPanel().getXMLSchemas();
                        XMLSchema schemaArray[] = new XMLSchema[schemas.size()];

                        gme.publishXMLSchemas(schemas.toArray(schemaArray));

                        String successMessage = "Successfully published (" + schemas.size() + ") schemas.";
                        JOptionPane.showMessageDialog(SwingUtilities.getRootPane(GMEViewer.this), successMessage);

                    } catch (Exception e1) {
                        e1.printStackTrace();
                        CompositeErrorDialog.showErrorDialog("Error publishing schemas:"+e1.getMessage(), e1);
                    }
                    GMEViewer.this.uploadSchemaTextPane.setText("");
                }
            });
        }
        return this.uploadUploadButton;
    }


    /**
     * This method initializes uploadSchemaViewPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getUploadSchemaViewPanel() {
        if (this.uploadSchemaViewPanel == null) {
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints9.weighty = 1.0;
            gridBagConstraints9.gridx = 0;
            gridBagConstraints9.gridy = 0;
            gridBagConstraints9.weightx = 1.0;
            this.uploadSchemaViewPanel = new JPanel();
            this.uploadSchemaViewPanel.setLayout(new GridBagLayout());
            this.uploadSchemaViewPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Schema Preview",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, PortalLookAndFeel.getPanelLabelColor()));
            this.uploadSchemaViewPanel.add(getUploadSchemaTextPane(), gridBagConstraints9);
        }
        return this.uploadSchemaViewPanel;
    }


    /**
     * This method initializes gmeBrowseButtonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getGmeBrowseButtonPanel() {
        if (this.gmeBrowseButtonPanel == null) {
            this.gmeBrowseButtonPanel = new JPanel();
            this.gmeBrowseButtonPanel.add(getGmeDownloadButton(), null);
        }
        return this.gmeBrowseButtonPanel;
    }


    /**
     * This method initializes Download
     * 
     * @return javax.swing.JButton
     */
    private JButton getGmeDownloadButton() {
        if (this.gmeDownloadButton == null) {
            this.gmeDownloadButton = new JButton();
            this.gmeDownloadButton.setText("Download Schema and Dependencies...");
            this.gmeDownloadButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    String location = null;
                    try {
                        location = ResourceManager.promptDir(null);
                    } catch (Exception ex) {
                        CompositeErrorDialog.showErrorDialog(ex);
                    }
                    if (location != null && location.length() > 0) {
                        try {
                            GlobalModelExchangeClient gme = new GlobalModelExchangeClient(ConfigurationUtil
                                .getGlobalExtensionProperty(GMESchemaLocatorPanel.GME_URL).getValue());
                            if (GMEViewer.this.gmeSchemaLocatorPanel.getSchemaComboBox().getSelectedItem() != null) {
                                Map<XMLSchemaNamespace, File> cacheSchemas = gme.cacheSchemas(
                                    ((XMLSchemaNamespace) GMEViewer.this.gmeSchemaLocatorPanel.getSchemaComboBox()
                                        .getSelectedItem()), new File(location));
                                String successMessage = "Successfully saved (" + cacheSchemas.size()
                                    + ") root schema files to the filesystem.";

                                JOptionPane.showMessageDialog(SwingUtilities.getRootPane(GMEViewer.this),
                                    successMessage);

                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                            CompositeErrorDialog.showErrorDialog("Error contacting GME", e1);
                        }
                    }
                }
            });
        }
        return this.gmeDownloadButton;
    }


    /**
     * This method initializes uploadSchemaTextPane
     * 
     * @return javax.swing.JTextArea
     */
    private JEditTextArea getUploadSchemaTextPane() {
        if (this.uploadSchemaTextPane == null) {
            this.uploadSchemaTextPane = new JEditTextArea(GMETextAreaDefaults.createDefaults());
            this.uploadSchemaTextPane.setTokenMarker(new XMLTokenMarker());
        }
        return this.uploadSchemaTextPane;
    }


    /**
     * This method initializes schemaTextPane
     * 
     * @return javax.swing.JTextArea
     */
    private JEditTextArea getSchemaTextPane() {
        if (this.schemaTextPane == null) {
            this.schemaTextPane = new JEditTextArea(GMETextAreaDefaults.createDefaults());
            this.schemaTextPane.setTokenMarker(new XMLTokenMarker());
            this.schemaTextPane.setEditable(false);
        }
        return this.schemaTextPane;
    }


    /**
     * This method initializes xmlSchemaListPanel
     * 
     * @return org.cagrid.gme.discoverytools.xmlSchemaListPanel
     */
    private XMLSchemaListPanel getXmlSchemaListPanel() {
        if (this.xmlSchemaListPanel == null) {
            this.xmlSchemaListPanel = new XMLSchemaListPanel();
            this.xmlSchemaListPanel.addValidationStatusChangeListener(this);
            this.xmlSchemaListPanel.addXMLSchemaDocumentSelectionListener(this);
        }
        return this.xmlSchemaListPanel;
    }


    public static void main(String[] args) {
        try {
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            ExtensionDescription ext = (ExtensionDescription) Utils.deserializeDocument("extensions" + File.separator
                + "gme-discovery" + File.separator + "extension.xml", ExtensionDescription.class);
            final GMEViewer panel = new GMEViewer(ext.getDiscoveryExtensionDescription());
            frame.getContentPane().setLayout(new BorderLayout());
            frame.getContentPane().add(panel, BorderLayout.CENTER);

            frame.pack();
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
        }
    }


    public void validationStatusChanged(ValidationResult validationResult) {
        if (validationResult.getSeverity().equals(Severity.ERROR)) {
            getUploadUploadButton().setEnabled(false);
        } else {
            getUploadUploadButton().setEnabled(true);
        }

    }


    public void documentSelected(XMLSchemaDocument document) {
        String text = "";
        if (document != null) {
            text = document.getSchemaText();
        }
        getUploadSchemaTextPane().setText(text);
    }
} // @jve:decl-index=0:visual-constraint="10,10"
