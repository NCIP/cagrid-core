package gov.nih.nci.cagrid.introduce.portal.modification.services.resourceproperties.editor;

import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;
import gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertyType;
import gov.nih.nci.cagrid.introduce.codegen.SyncTools;
import gov.nih.nci.cagrid.introduce.portal.common.jedit.JEditTextArea;
import gov.nih.nci.cagrid.introduce.portal.common.jedit.XMLTokenMarker;
import gov.nih.nci.cagrid.introduce.portal.extension.ResourcePropertyEditorPanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.cagrid.grape.utils.ErrorDialog;


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
public class XMLEditorViewer extends ResourcePropertyEditorPanel {
    
    private static final Logger logger = Logger.getLogger(XMLEditorViewer.class);

    private JPanel xmlViewer = null;
    private String xml = null;
    private JEditTextArea xmlTextPane = null;


    /**
     * This method initializes
     */
    public XMLEditorViewer(ResourcePropertyType prop, String data, File schemaFile, File schemaDir) {
        super(prop, data, schemaFile, schemaDir);
        this.xml = getRPString();
        initialize();
        logger.debug("SCHEMA FILE: " + schemaFile.getAbsolutePath());
        logger.debug("SCHEMA DIR: " + schemaDir.getAbsolutePath());

    }


    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.gridx = -1;
        gridBagConstraints.gridy = -1;
        gridBagConstraints.weightx = 1.0D;
        gridBagConstraints.weighty = 1.0D;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        this.setSize(new java.awt.Dimension(615, 426));
        this.setPreferredSize(new java.awt.Dimension(615, 426));
        this.setLayout(new GridBagLayout());
        this.add(getXmlViewer(), gridBagConstraints);
    }


    /**
     * This method initializes schemaViewer
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getXmlViewer() {
        if (xmlViewer == null) {
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints3.gridy = 0;
            gridBagConstraints3.weightx = 1.0;
            gridBagConstraints3.weighty = 1.0;
            gridBagConstraints3.gridheight = 1;
            gridBagConstraints3.gridx = 0;
            xmlViewer = new JPanel();
            xmlViewer.setLayout(new GridBagLayout());
            xmlViewer.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Resource Property XML",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, PortalLookAndFeel.getPanelLabelColor()));
            xmlViewer.add(getXMLTextPane(), gridBagConstraints3);
        }
        return xmlViewer;
    }


    /**
     * This method initializes schemaTextPane
     * 
     * @return javax.swing.JTextArea
     */
    private JEditTextArea getXMLTextPane() {
        if (xmlTextPane == null) {
            xmlTextPane = new JEditTextArea(XMLEditorTextAreaDefaults.createDefaults());
            xmlTextPane.setTokenMarker(new XMLTokenMarker());
            xmlTextPane.setText(xml);
            xmlTextPane.setCaretPosition(0);
        }
        return xmlTextPane;
    }


    /**
     * This method initializes doneButton
     * 
     * @return javax.swing.JButton
     */
    public void validateResourceProperty() throws Exception {
        xml = getXMLTextPane().getText();
        try {
            XMLUtilities.stringToDocument(xml);
        } catch (Exception e) {
            xml = null;
            logger.error(e);
        }
        if (xml == null) {
            throw new Exception("ERROR: Invalid XML Document");
            
        }
    }


    /**
     * @see gov.nih.nci.cagrid.introduce.portal.extension.ResourcePropertyEditorPanel#getResultRPString()
     */
    public String getResultRPString() {
        if (xml == null) {
            return null;
        }
        return xml;
    }

} // @jve:decl-index=0:visual-constraint="10,4"
