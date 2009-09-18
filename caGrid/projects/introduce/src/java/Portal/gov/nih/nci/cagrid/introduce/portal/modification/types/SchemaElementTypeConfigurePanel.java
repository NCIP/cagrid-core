package gov.nih.nci.cagrid.introduce.portal.modification.types;

import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;
import gov.nih.nci.cagrid.common.portal.validation.IconFeedbackPanel;
import gov.nih.nci.cagrid.introduce.beans.namespace.SchemaElementType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.util.ValidationUtils;
import com.jgoodies.validation.view.ValidationComponentUtils;


public class SchemaElementTypeConfigurePanel extends JPanel {

    private JTextField typeText = null;

    private JTextField classNameText = null;

    private boolean hide = false;

    private SchemaElementType type;

    private JPanel beanPanel = null;

    private JPanel customBeanPanel = null;

    private JLabel typeLabel = null;

    private JLabel classNameLabell = null;

    private JLabel serializerLabel = null;

    private JTextField serializerText = null;

    private JLabel deserializerLabel = null;

    private JTextField deserializerText = null;

    private JTextArea helpArea = null;

    private JPanel customBeanWrapperPanel = null;

    private JLabel customizeLabel = null;

    private ValidationResultModel validationModel = new DefaultValidationResultModel();

    private static final String CLASSNAME = "Classname"; // @jve:decl-index=0:

    private static final String SERIALIZER = "Serializer"; // @jve:decl-index=0:

    private static final String DESERIALIZER = "Deserializer"; //@jve:decl-index
                                                               // =0:


    /**
     * This method initializes
     */
    public SchemaElementTypeConfigurePanel() {
        super();
        initialize();
        this.setHide(true);
    }


    public void setSchemaElementType(SchemaElementType type, boolean classEditable) {
        this.type = type;

        getTypeText().setText(type.getType());
        getClassNameText().setText(type.getClassName());
        getDeserializerText().setText(type.getDeserializer());
        getSerializerText().setText(type.getSerializer());
        getClassNameText().setEditable(classEditable);
        getDeserializerText().setEditable(classEditable);
        getSerializerText().setEditable(classEditable);
        validateInput();
    }


    public SchemaElementType getSchemaElementType() {
        return type;
    }


    public void clear() {
        type = null;
        getTypeText().setText("");
        getClassNameText().setText("");
        getDeserializerText().setText("");
        getSerializerText().setText("");
        validateInput();
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
        gridBagConstraints8.gridx = 0;
        gridBagConstraints8.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints8.weightx = 1.0D;
        gridBagConstraints8.weighty = 1.0D;
        gridBagConstraints8.gridy = 1;
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 0;
        gridBagConstraints2.weighty = 1.0D;
        gridBagConstraints2.weightx = 1.0D;
        gridBagConstraints2.insets = new java.awt.Insets(0, 0, 0, 0);
        gridBagConstraints2.ipady = 2;
        gridBagConstraints2.ipadx = 2;
        gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createTitledBorder(null, "Element Type Configuration",
            TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12),
            new Color(62, 109, 181)));
        this.add(getBeanPanel(), gridBagConstraints2);
        this.add(getCustomBeanWrapperPanel(), gridBagConstraints8);
        initValidation();
    }


    private void initValidation() {
        ValidationComponentUtils.setMessageKey(getClassNameText(), CLASSNAME);
        ValidationComponentUtils.setMessageKey(getSerializerText(), SERIALIZER);
        ValidationComponentUtils.setMessageKey(getDeserializerText(), DESERIALIZER);
        validateInput();
        updateComponentTreeSeverity();
    }


    private void validateInput() {

        if (getCustomBeanPanel().isVisible()) {
            ValidationResult result = SchemaElementTypeValidator.validateSchemaElementType(getClassNameText().getText(),
                getSerializerText().getText(), getDeserializerText().getText());
            this.validationModel.setResult(result);
            updateComponentTreeSeverity();
        } else{
            this.validationModel.setResult(new ValidationResult());
            updateComponentTreeSeverity();
        }

    }


    private void updateComponentTreeSeverity() {
        ValidationComponentUtils.updateComponentTreeMandatoryAndBlankBackground(this.getCustomBeanPanel());
        ValidationComponentUtils.updateComponentTreeSeverityBackground(this.getCustomBeanPanel(), this.validationModel
            .getResult());
    }


    /**
     * This method initializes typeText
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getTypeText() {
        if (typeText == null) {
            typeText = new JTextField();
            typeText.setEditable(false);
            typeText.getDocument().addDocumentListener(new DocumentListener() {
                public void changedUpdate(DocumentEvent e) {
                    if (type != null) {
                        type.setType(getTypeText().getText());
                    }
                }


                public void removeUpdate(DocumentEvent e) {
                    if (type != null) {
                        type.setType(getTypeText().getText());
                    }
                }


                public void insertUpdate(DocumentEvent e) {
                    if (type != null) {
                        type.setType(getTypeText().getText());
                    }
                }
            });
        }
        return typeText;
    }


    /**
     * This method initializes classNameText
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getClassNameText() {
        if (classNameText == null) {
            classNameText = new JTextField();
            classNameText.getDocument().addDocumentListener(new DocumentListener() {
                public void changedUpdate(DocumentEvent e) {
                    if (type != null) {
                        type.setClassName(getClassNameText().getText());
                        validateInput();
                    }
                }


                public void removeUpdate(DocumentEvent e) {
                    if (type != null) {
                        type.setClassName(getClassNameText().getText());
                        validateInput();
                    }
                }


                public void insertUpdate(DocumentEvent e) {
                    if (type != null) {
                        type.setClassName(getClassNameText().getText());
                        validateInput();
                    }
                }
            });
        }
        return classNameText;
    }


    /**
     * This method initializes beanPanel
     * 
     * @return javax. gridBagConstraints6.gridwidth = 2;
     *         gridBagConstraints6.anchor =
     *         java.awt.GridBagConstraints.NORTHWEST;
     *         beanPanel.add(getCustomizeButton(), gridBagConstraints6);
     *         swing.JPanel
     */
    private JPanel getBeanPanel() {
        if (beanPanel == null) {
            GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
            gridBagConstraints31.gridx = 1;
            gridBagConstraints31.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints31.gridy = 0;
            typeLabel = new JLabel();
            typeLabel.setText("Type");
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.weightx = 1.0;
            gridBagConstraints1.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints1.gridx = 2;
            beanPanel = new JPanel();
            beanPanel.setLayout(new GridBagLayout());
            beanPanel.add(getTypeText(), gridBagConstraints1);
            beanPanel.add(typeLabel, gridBagConstraints31);
        }
        return beanPanel;
    }


    /**
     * This method initializes customBeanPanel
     * 
     * @return gridBagConstraints51.insets = new java.awt.Insets(2,2,2,2);
     *         customBeanPanel.add(getSerializerText(), gridBagConstraints51);
     *         customBeanPanel
     *         .setBorder(javax.swing.BorderFactory.createTitledBorder(null,
     *         "Customize Bean",
     *         javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
     *         javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
     *         javax.swing.JPanel
     */
    private JPanel getCustomBeanPanel() {
        if (customBeanPanel == null) {
            GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
            gridBagConstraints15.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints15.gridy = 3;
            gridBagConstraints15.weightx = 1.0;
            gridBagConstraints15.weighty = 1.0;
            gridBagConstraints15.gridwidth = 2;
            gridBagConstraints15.gridheight = 2;
            gridBagConstraints15.gridx = 0;
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.gridx = 1;
            gridBagConstraints12.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints12.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints12.gridy = 2;
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints11.gridy = 1;
            gridBagConstraints11.weightx = 1.0;
            gridBagConstraints11.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints11.gridx = 1;
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.gridx = 0;
            gridBagConstraints10.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints10.gridy = 2;
            deserializerLabel = new JLabel();
            deserializerLabel.setText("Deserializer*");
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.gridy = 1;
            serializerLabel = new JLabel();
            serializerLabel.setText("Serializer*");
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.gridy = 0;
            classNameLabell = new JLabel();
            classNameLabell.setText("Classname*");
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
            customBeanPanel = new JPanel();
            customBeanPanel.setLayout(new GridBagLayout());
            customBeanPanel.setVisible(false);
            customBeanPanel.add(getClassNameText(), gridBagConstraints);
            customBeanPanel.add(classNameLabell, gridBagConstraints4);
            customBeanPanel.add(serializerLabel, gridBagConstraints5);
            customBeanPanel.add(deserializerLabel, gridBagConstraints10);
            customBeanPanel.add(getDeserializerText(), gridBagConstraints12);
            customBeanPanel.add(getSerializerText(), gridBagConstraints11);
            customBeanPanel.add(getHelpArea(), gridBagConstraints15);
        }
        return customBeanPanel;
    }


    /**
     * This method initializes serializerText
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getSerializerText() {
        if (serializerText == null) {
            serializerText = new JTextField();
            serializerText.getDocument().addDocumentListener(new DocumentListener() {
                public void changedUpdate(DocumentEvent e) {
                    if (type != null) {
                        type.setSerializer(getSerializerText().getText());
                        validateInput();
                    }
                }


                public void removeUpdate(DocumentEvent e) {
                    if (type != null) {
                        type.setSerializer(getSerializerText().getText());
                        validateInput();
                    }
                }


                public void insertUpdate(DocumentEvent e) {
                    if (type != null) {
                        type.setSerializer(getSerializerText().getText());
                        validateInput();
                    }
                }
            });
        }
        return serializerText;
    }


    /**
     * This method initializes deserializerText
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getDeserializerText() {
        if (deserializerText == null) {
            deserializerText = new JTextField();
            deserializerText.getDocument().addDocumentListener(new DocumentListener() {
                public void changedUpdate(DocumentEvent e) {
                    if (type != null) {
                        type.setDeserializer(getDeserializerText().getText());
                        validateInput();
                    }
                }


                public void removeUpdate(DocumentEvent e) {
                    if (type != null) {
                        type.setDeserializer(getDeserializerText().getText());
                        validateInput();
                    }
                }


                public void insertUpdate(DocumentEvent e) {
                    if (type != null) {
                        type.setDeserializer(getDeserializerText().getText());
                        validateInput();
                    }
                }
            });
        }
        return deserializerText;
    }


    /**
     * This method initializes helpArea
     * 
     * @return javax.swing.JTextArea
     */
    private JTextArea getHelpArea() {
        if (helpArea == null) {
            helpArea = new JTextArea();
            helpArea.setEditable(false);
            helpArea.setFont(new java.awt.Font("Dialog", java.awt.Font.ITALIC, 10));
            helpArea.setLineWrap(true);
            helpArea.setBackground(new java.awt.Color(204, 204, 204));
            helpArea
                .setToolTipText("<html>For every Schema that omits these fields for all of its data types, "
                    + "default Classes, Serializers, and Deserializers will be generated and used.<br>"
                    + "If you specify a customization of any data types in a given "
                    + "Schema, no Classes, Serializers, or Deserializers will be generated for any other data types"
                    + " in that Schema.<br><b>Therefore, if you customize a data type in a Schema,"
                    + " you need to also customize all other data types in that Schema that you are using in your service.");
            helpArea
                .setText("* Optional.  You must specify all these fields if you specify any. [See tooltip, and documentation, for more details]");
        }
        return helpArea;
    }


    public void setHide(boolean hide) {
        this.hide = hide;
        if (hide) {
            getCustomBeanPanel().setVisible(false);
            customizeLabel.setIcon(PortalLookAndFeel.getAddIcon());
            customizeLabel.setEnabled(false);
            getTypeText().setEnabled(false);
            typeLabel.setEnabled(false);
        } else {
            customizeLabel.setEnabled(true);
            getTypeText().setEnabled(true);
            typeLabel.setEnabled(true);
        }
        validateInput();
    }


    public boolean getHide() {
        return hide;
    }


    /**
     * This method initializes customBeanWrapperPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCustomBeanWrapperPanel() {
        if (customBeanWrapperPanel == null) {
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.gridx = 0;
            gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints6.fill = java.awt.GridBagConstraints.NONE;
            gridBagConstraints6.weightx = 1.0D;
            gridBagConstraints6.gridy = 0;
            customizeLabel = new JLabel();
            customizeLabel.setIcon(PortalLookAndFeel.getAddIcon());
            customizeLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.ITALIC, 10));
            customizeLabel.setText("Customize Beans");
            customizeLabel.addMouseListener(new MouseAdapter() {

                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    if (getCustomBeanPanel().isVisible()) {
                        getCustomBeanPanel().setVisible(false);
                        customizeLabel.setIcon(PortalLookAndFeel.getAddIcon());
                    } else if (!getHide()) {
                        getCustomBeanPanel().setVisible(true);
                        customizeLabel.setIcon(PortalLookAndFeel.getRemoveIcon());
                    }

                    validateInput();
                }

            });
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = GridBagConstraints.BOTH;
            gridBagConstraints3.gridwidth = 2;
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.gridy = 1;
            gridBagConstraints3.weightx = 1.0D;
            gridBagConstraints3.weighty = 1.0D;
            gridBagConstraints3.gridheight = 2;
            customBeanWrapperPanel = new JPanel();
            customBeanWrapperPanel.setLayout(new GridBagLayout());
            customBeanWrapperPanel.add(new IconFeedbackPanel(this.validationModel, getCustomBeanPanel()),
                gridBagConstraints3);
            customBeanWrapperPanel.add(customizeLabel, gridBagConstraints6);
        }
        return customBeanWrapperPanel;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
