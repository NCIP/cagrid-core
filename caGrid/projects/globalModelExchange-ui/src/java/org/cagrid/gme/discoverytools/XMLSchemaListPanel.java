package org.cagrid.gme.discoverytools;

import gov.nih.nci.cagrid.common.portal.validation.IconFeedbackPanel;
import gov.nih.nci.cagrid.introduce.common.FileFilters;
import gov.nih.nci.cagrid.introduce.common.ResourceManager;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.cagrid.gme.common.FilesystemLoader;
import org.cagrid.gme.domain.XMLSchema;
import org.cagrid.gme.domain.XMLSchemaDocument;
import org.cagrid.grape.utils.CompositeErrorDialog;

import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.view.ValidationComponentUtils;
import com.jgoodies.validation.view.ValidationResultViewFactory;


public class XMLSchemaListPanel extends JPanel implements ListSelectionListener {
    private static final String NO_SCHEMAS_ERROR_MESSAGE = "Must add at least one xml schema.";

    private static final String XSD_LIST = "xsd-list";

    private final ValidationResultModel validationModel = new DefaultValidationResultModel();

    private ValidationResult validationResult = null; // @jve:decl-index=0:

    private List<XMLSchema> xmlSchemas = new ArrayList<XMLSchema>(); //@jve:decl
    private JPanel mainPanel = null;

    private JPanel xsdListPanel = null;

    private JPanel detailPanel = null;

    private JList xsdList = null;

    private JPanel controlPanel = null;

    private JButton addButton = null;

    private JButton removeButton = null;

    private JTextField xsdErrorLabel = null;

    private JScrollPane xsdScrollPane = null;

    private JScrollPane documentsScrollPane = null;

    private JList docsList = null;

    private DefaultListModel docsmodel = null;

    private final List<ValidationStatusChangeListener> validationListeners = new ArrayList<ValidationStatusChangeListener>();
    private final List<XMLSchemaDocumentSelectionListener> documentListeners = new ArrayList<XMLSchemaDocumentSelectionListener>();


    public boolean addValidationStatusChangeListener(ValidationStatusChangeListener o) {
        return this.validationListeners.add(o);
    }


    public boolean removeValidationStatusChangeListener(ValidationStatusChangeListener o) {
        return this.validationListeners.remove(o);
    }


    public boolean addXMLSchemaDocumentSelectionListener(XMLSchemaDocumentSelectionListener o) {
        return this.documentListeners.add(o);
    }


    public boolean removeXMLSchemaDocumentSelectionListener(XMLSchemaDocumentSelectionListener o) {
        return this.documentListeners.remove(o);
    }


    public XMLSchemaListPanel() {
        super();
        this.xsdErrorLabel = new JTextField(NO_SCHEMAS_ERROR_MESSAGE);
        initialize();
    }


    /**
     * This method initializes this
     */
    private void initialize() {

        this.setLayout(new GridLayout(1, 1));
        this.add(IconFeedbackPanel.getWrappedComponentTree(this.validationModel, getMainPanel()));

        this.initValidation();
        this.validateInput();

    }


    public final class TextBoxListener implements DocumentListener {

        public void changedUpdate(DocumentEvent e) {
            validateInput();
        }


        public void insertUpdate(DocumentEvent e) {
            validateInput();
        }


        public void removeUpdate(DocumentEvent e) {
            validateInput();
        }

    }


    private void initValidation() {
        ValidationComponentUtils.setMessageKey(this.xsdErrorLabel, XSD_LIST);

        validateInput();
    }


    private void validateInput() {
        this.validationResult = new ValidationResult();

        if (this.getXMLSchemas() == null || this.getXMLSchemas().isEmpty()) {
            this.validationResult.add(new SimpleValidationMessage(NO_SCHEMAS_ERROR_MESSAGE, Severity.ERROR, XSD_LIST));
            getXMLSchemaList().setBackground(ValidationComponentUtils.getErrorBackground());
        } else {
            getXMLSchemaList().setBackground(Color.WHITE);

            Map<URI, XMLSchema> processedSchemas = new HashMap<URI, XMLSchema>();

            for (XMLSchema xsd : getXMLSchemas()) {
                if (!processedSchemas.containsKey(xsd.getTargetNamespace())) {
                    processedSchemas.put(xsd.getTargetNamespace(), xsd);
                } else {
                    this.validationResult
                        .add(new SimpleValidationMessage(
                            "The file ("
                                + xsd.getRootDocument().getSystemID()
                                + ") has the same namespace ("
                                + xsd.getTargetNamespace()
                                + ") as an existing file ("
                                + processedSchemas.get(xsd.getTargetNamespace()).getRootDocument().getSystemID()
                                + "); schemas must have unique namespaces.  If you are making use of XML Schema includes or redefines, you should not manually add those documents, they will be automatically added.",
                            Severity.ERROR, XSD_LIST));
                    getXMLSchemaList().setBackground(ValidationComponentUtils.getErrorBackground());
                }
            }

        }

        if (this.getDetailPanel().isEnabled()) {

            // if (ValidationUtils.isBlank(getFnameTextField().getText())) {
            // this.validationResult.add(new
            // SimpleValidationMessage("First name must not be blank.",
            // Severity.ERROR,
            // "first-name"));
            // }

        }

        this.validationModel.setResult(this.validationResult);
        updateComponentTreeSeverity();
        // tell our listeners
        for (ValidationStatusChangeListener listner : this.validationListeners) {
            listner.validationStatusChanged(this.validationResult);
        }
    }


    private void updateComponentTreeSeverity() {
        ValidationComponentUtils.updateComponentTreeMandatoryAndBlankBackground(this);
        ValidationComponentUtils.updateComponentTreeSeverityBackground(this, this.validationModel.getResult());
        this.repaint();
    }


    public boolean validatePanel() {
        if (this.validationResult != null && this.validationResult.hasErrors()) {
            return false;
        } else {
            return true;
        }
    }


    private JPanel getMainPanel() {
        if (this.mainPanel == null) {
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.weightx = 1.0;
            gridBagConstraints1.weighty = 1.0;
            gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints1.gridy = 2;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridy = 1;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.weighty = 1.0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.gridx = 0;

            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridy = 0;
            gridBagConstraints2.weightx = 1.0;
            gridBagConstraints2.weighty = 1.0;
            gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints2.gridx = 0;

            this.mainPanel = new JPanel(new GridBagLayout());
            this.mainPanel.add(getValidationResultList(), gridBagConstraints2);
            this.mainPanel.add(getXSDListPanel(), gridBagConstraints);
            this.mainPanel.add(getDetailPanel(), gridBagConstraints1);
        }
        return this.mainPanel;
    }


    public List<XMLSchema> getXMLSchemas() {
        return this.xmlSchemas;
    }


    public void setXMLSchemas(List<XMLSchema> schemas) {
        this.xmlSchemas = schemas;
        updateView();
    }


    protected void updateView() {
        DefaultListModel xsdModel = new DefaultListModel();
        int xsdIndex = getXMLSchemaList().getSelectedIndex();

        if (this.xmlSchemas != null && !this.xmlSchemas.isEmpty()) {
            for (XMLSchema xsd : this.xmlSchemas) {
                xsdModel.addElement(new XMLSchemaDisplay(xsd));
            }

        } else {
            xsdModel.addElement("At least one schema is required!");
        }

        getXMLSchemaList().setModel(xsdModel);
        getXMLSchemaList().setSelectedIndex(xsdIndex);

        updateXMLSchemaView();

    }


    /**
     * This method initializes pocListPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getXSDListPanel() {
        if (this.xsdListPanel == null) {
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.fill = GridBagConstraints.BOTH;
            gridBagConstraints6.weighty = 1.0;
            gridBagConstraints6.gridx = 0;
            gridBagConstraints6.gridy = 0;
            gridBagConstraints6.weightx = 1.0;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 1;
            gridBagConstraints3.weightx = 1.0D;
            gridBagConstraints3.weighty = 1.0D;
            gridBagConstraints3.gridheight = 1;
            gridBagConstraints3.fill = GridBagConstraints.BOTH;
            gridBagConstraints3.gridy = 0;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.gridy = 0;
            gridBagConstraints2.weightx = 1.0D;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.weighty = 1.0D;

            this.xsdListPanel = new JPanel();
            this.xsdListPanel.setLayout(new GridBagLayout());
            this.xsdListPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "XML Schemas",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            this.xsdListPanel.add(getControlPanel(), gridBagConstraints3);
            this.xsdListPanel.add(getXsdScrollPane(), gridBagConstraints6);

        }
        return this.xsdListPanel;
    }


    /**
     * This method initializes detailPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getDetailPanel() {
        if (this.detailPanel == null) {

            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.fill = GridBagConstraints.BOTH;
            gridBagConstraints7.weighty = 1.0;
            gridBagConstraints7.weightx = 1.0;
            this.detailPanel = new JPanel();
            this.detailPanel.setLayout(new GridBagLayout());
            this.detailPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
                "Documents comprising selected Schema", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            this.detailPanel.add(getDocumentsScrollPane(), gridBagConstraints7);
        }
        return this.detailPanel;
    }


    /**
     * This method initializes pocList
     * 
     * @return javax.swing.JList
     */
    private JList getXMLSchemaList() {
        if (this.xsdList == null) {
            DefaultListModel model = new DefaultListModel();
            this.xsdList = new JList(model);
            this.xsdList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
            this.xsdList.addListSelectionListener(this);
            this.xsdList.setVisibleRowCount(8);

        }
        return this.xsdList;
    }


    /**
     * This method initializes controlPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getControlPanel() {
        if (this.controlPanel == null) {

            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.insets = new java.awt.Insets(5, 5, 5, 5);
            gridBagConstraints4.gridy = 0;
            gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.gridx = 0;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.insets = new java.awt.Insets(5, 5, 5, 5);
            gridBagConstraints5.gridy = 1;
            gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.gridx = 0;
            this.controlPanel = new JPanel();
            this.controlPanel.setLayout(new GridBagLayout());
            this.controlPanel.add(getAddButton(), gridBagConstraints4);
            this.controlPanel.add(getRemoveButton(), gridBagConstraints5);
        }
        return this.controlPanel;
    }


    private JComponent getValidationResultList() {
        return ValidationResultViewFactory.createReportList(this.validationModel);
    }


    /**
     * This method initializes addButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddButton() {
        if (this.addButton == null) {
            this.addButton = new JButton();
            this.addButton.setText("Add Schemas...");
            this.addButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        String[] files = ResourceManager.promptMultiFiles(SwingUtilities
                            .getRoot(XMLSchemaListPanel.this), null, FileFilters.XSD_FILTER);

                        if (files != null) {
                            List<File> fileList = new ArrayList<File>();
                            for (String fileName : files) {
                                fileList.add(new File(fileName));
                            }

                            FilesystemLoader loader = new FilesystemLoader(fileList);
                            List<XMLSchema> loadedSchemas = loader.loadSchemas();
                            XMLSchemaListPanel.this.addXMLSchemas(loadedSchemas);
                        } else {
                            return;
                        }

                    } catch (IOException e1) {
                        CompositeErrorDialog.showErrorDialog("Error selecting schema file", e1);
                    }
                }
            });
        }
        return this.addButton;
    }


    protected void addXMLSchemas(List<XMLSchema> xsds) {
        this.xmlSchemas.addAll(xsds);
        updateView();
        this.getXMLSchemaList().setSelectedIndex(getXMLSchemaList().getModel().getSize() - 1);

    }


    protected void addXMLSchema(XMLSchema xsd) {
        this.xmlSchemas.add(xsd);
        updateView();
        this.getXMLSchemaList().setSelectedIndex(getXMLSchemaList().getModel().getSize() - 1);

    }


    /**
     * This method initializes removeButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getRemoveButton() {
        if (this.removeButton == null) {
            this.removeButton = new JButton();
            this.removeButton.setText("Remove Selected Schema");
            this.removeButton.setEnabled(false);
            this.removeButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    removeSelectedXMLSchema();
                }
            });
        }
        return this.removeButton;
    }


    protected void removeSelectedXMLSchema() {
        int selectedIndex = getXMLSchemaList().getSelectedIndex();
        if (selectedIndex != -1) {
            this.xmlSchemas.remove(selectedIndex);
            if (getXMLSchemaList().getModel().getSize() >= 0) {
                if (selectedIndex > 0) {
                    getXMLSchemaList().setSelectedIndex(selectedIndex - 1);
                } else {
                    getXMLSchemaList().setSelectedIndex(0);
                }
            }
        }
        updateView();
    }


    protected class XMLSchemaDisplay {
        XMLSchema schema;


        public XMLSchemaDisplay(XMLSchema schema) {
            this.schema = schema;
        }


        @Override
        public String toString() {
            if (this.schema == null) {
                return "null";
            }

            // return this.schema.getRootDocument().getSystemID() + " - " +
            // this.schema.getTargetNamespace().toString();
            return this.schema.getTargetNamespace().toString();
        }


        public XMLSchema getXMLSchema() {
            return this.schema;
        }


        public void setXMLSchema(XMLSchema xsd) {
            this.schema = xsd;
        }
    }


    protected class XMLSchemaDocumentDisplay {
        XMLSchemaDocument doc;


        public XMLSchemaDocumentDisplay(XMLSchemaDocument doc) {
            this.doc = doc;
        }


        @Override
        public String toString() {
            if (this.doc == null) {
                return "null";
            }

            return this.doc.getSystemID();
        }


        public XMLSchemaDocument getXMLSchemaDocument() {
            return this.doc;
        }


        public void setXMLSchemaDocument(XMLSchemaDocument doc) {
            this.doc = doc;
        }
    }


    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {
            if (e.getSource().equals(getXMLSchemaList())) {
                updateXMLSchemaView();
            } else if (e.getSource().equals(getDocsList())) {
                XMLSchemaDocument doc = null;
                if ((XMLSchemaDocumentDisplay) getDocsList().getSelectedValue() != null) {
                    doc = ((XMLSchemaDocumentDisplay) getDocsList().getSelectedValue()).getXMLSchemaDocument();
                }
                for (XMLSchemaDocumentSelectionListener listener : this.documentListeners) {
                    listener.documentSelected(doc);
                }
            }
        }
    }


    private void setInfoFieldsEnabled(boolean enabled) {
        getDocsList().setEnabled(enabled);

        validateInput();
    }


    private void updateXMLSchemaView() {
        XMLSchema xsd = null;
        if (getXMLSchemaList().getSelectedValue() instanceof XMLSchemaDisplay
            && getXMLSchemaList().getSelectedValue() != null) {
            xsd = ((XMLSchemaDisplay) getXMLSchemaList().getSelectedValue()).getXMLSchema();
        }

        if (xsd == null) {
            getRemoveButton().setEnabled(false);
            setInfoFieldsEnabled(false);
            getDocsList().setModel(new DefaultListModel());
        } else {
            getRemoveButton().setEnabled(true);
            setInfoFieldsEnabled(true);

            DefaultListModel docsModel = new DefaultListModel();

            docsModel.addElement(new XMLSchemaDocumentDisplay(xsd.getRootDocument()));
            for (XMLSchemaDocument doc : xsd.getAdditionalSchemaDocuments()) {
                docsModel.addElement(new XMLSchemaDocumentDisplay(doc));
            }
            getDocsList().setModel(docsModel);
            getDocsList().setSelectedIndex(0);
        }

        validateInput();
    }


    /**
     * This method initializes xsdScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getXsdScrollPane() {
        if (this.xsdScrollPane == null) {
            this.xsdScrollPane = new JScrollPane();
            this.xsdScrollPane.setViewportView(getXMLSchemaList());
        }
        return this.xsdScrollPane;
    }


    /**
     * This method initializes documentsScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getDocumentsScrollPane() {
        if (this.documentsScrollPane == null) {
            this.documentsScrollPane = new JScrollPane();
            this.documentsScrollPane.setViewportView(getDocsList());
        }
        return this.documentsScrollPane;
    }


    /**
     * This method initializes docsmodel
     * 
     * @return javax.swing.DefaultListModel
     */
    private DefaultListModel getDocsmodel() {
        if (this.docsmodel == null) {
            this.docsmodel = new DefaultListModel();
        }
        return this.docsmodel;
    }


    /**
     * This method initializes docsList
     * 
     * @return javax.swing.JList
     */
    private JList getDocsList() {
        if (this.docsList == null) {
            this.docsList = new JList(getDocsmodel());
            this.docsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            this.docsList.addListSelectionListener(this);
            this.docsList.setVisibleRowCount(8);
        }
        return this.docsList;
    }
} // @jve:decl-index=0:visual-constraint="10,10"
