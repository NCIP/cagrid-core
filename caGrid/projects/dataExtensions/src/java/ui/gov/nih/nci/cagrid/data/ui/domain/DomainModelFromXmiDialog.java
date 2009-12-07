package gov.nih.nci.cagrid.data.ui.domain;


import gov.nih.nci.cagrid.common.portal.DocumentChangeAdapter;
import gov.nih.nci.cagrid.common.portal.validation.IconFeedbackPanel;
import gov.nih.nci.cagrid.introduce.common.FileFilters;
import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.metadata.xmi.FixXmiExecutor;
import gov.nih.nci.cagrid.metadata.xmi.XMIParser;
import gov.nih.nci.cagrid.metadata.xmi.XmiFileType;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileWriter;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.filechooser.FileFilter;

import org.cagrid.grape.GridApplication;
import org.cagrid.grape.model.Application;
import org.cagrid.grape.utils.CompositeErrorDialog;

import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.util.ValidationUtils;
import com.jgoodies.validation.view.ValidationComponentUtils;

/** 
 *  DomainModelFromXmiDialog
 *  Dialog for generating a Domain Model from an XMI file
 * 
 * @author David Ervin
 * 
 * @created Oct 23, 2007 11:05:04 AM
 * @version $Id: DomainModelFromXmiDialog.java,v 1.10 2008-04-16 14:05:43 dervin Exp $ 
 */
public class DomainModelFromXmiDialog extends JDialog {
    // keys for validation messages
    public static final String KEY_XMI_FILENAME = "XMI File Name";
    public static final String KEY_SDK_DIR = "caCORE SDK Directory";
    public static final String KEY_PROJECT_SHORT_NAME = "Project Short Name";
    public static final String KEY_PROJECT_VERSION = "Project Version";
    public static final String KEY_EXCLUDE_REGEX = "Package Excludes";

    private JLabel xmiFileLabel = null;
    private JTextField xmiFileTextField = null;
    private JButton xmiBrowseButton = null;
    private JLabel shortNameLabel = null;
    private JTextField projectShortNameTextField = null;
    private JLabel projectVersionLabel = null;
    private JTextField projectVersionTextField = null;
    private JLabel projectLongNameLabel = null;
    private JTextField projectLongNameTextField = null;
    private JLabel projectDescriptionLabel = null;
    private JTextArea projectDescriptionTextArea = null;
    private JScrollPane projectDescriptionScrollPane = null;
    private JPanel informationPanel = null;
    private JButton okButton = null;
    private JButton cancelButton = null;
    private JPanel buttonPanel = null;
    private JCheckBox fixEaModelCheckBox = null;
    private JTextField sdkDirTextField = null;
    private JButton sdkDirBrowseButton = null;
    private JComboBox xmiTypeComboBox = null;
    private JLabel xmiTypeLabel = null;
    private JPanel xmiTypePanel = null;
    private JPanel fixEaModelPanel = null;
    private JPanel xmiBrowsePanel = null;
    private JPanel mainPanel = null;
    private JLabel excludePackagesLabel = null;
    private JTextField excludePackagesTextField = null;
        
    private boolean canceled;
    private String suppliedXmiFilename = null;
    private String suppliedPackageExcludes = null;
    private ValidationResultModel validationModel = null;
    private DocumentChangeAdapter documentChangeListener = null;
    
    private DomainModelFromXmiDialog(JFrame parent, String xmiFilename, String packageExcludes) {
        super(parent, "Generate Domain Model", true);
        canceled = true;
        suppliedXmiFilename = xmiFilename;
        suppliedPackageExcludes = packageExcludes;
        validationModel = new DefaultValidationResultModel();
        documentChangeListener = new DocumentChangeAdapter() {
            public void documentEdited(DocumentEvent e) {
                validateInput();
            }
        };
        initialize();
    }
    
    
    private void initialize() {
        setContentPane(new IconFeedbackPanel(validationModel, getMainPanel()));
        configureValidation();
        pack();
        setSize(500, getPreferredSize().height);
        setVisible(true);
    }
    
    
    public static DomainModel createDomainModel(JFrame parent) {
        return createDomainModel(parent, null, null);
    }
    
    
    public static DomainModel createDomainModel(JFrame parent, String xmiFilename, String packageExcludes) {
        DomainModelFromXmiDialog dialog = new DomainModelFromXmiDialog(parent, xmiFilename, packageExcludes);
        if (!dialog.canceled) {
            File xmiFile = null;
            if (dialog.getFixEaModelCheckBox().isSelected()) {
                File sdkDir = new File(dialog.getSdkDirTextField().getText());
                File rawXmiFile = new File(dialog.getXmiFileTextField().getText());
                try {
                    xmiFile = FixXmiExecutor.fixEaXmiModel(rawXmiFile, sdkDir);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    CompositeErrorDialog.showErrorDialog(
                        "Error executing fix-xmi command", ex.getMessage(), ex);
                    return null;
                }
            } else {
                xmiFile = new File(dialog.getXmiFileTextField().getText()); 
            }

            String shortName = dialog.getProjectShortNameTextField().getText();
            String version = dialog.getProjectVersionTextField().getText();
            
            XMIParser parser = new XMIParser(shortName, version);
            
            String longName = dialog.getProjectLongNameTextField().getText();
            if (longName != null && longName.length() != 0) {
                parser.setProjectLongName(longName);
            }
            String description = dialog.getProjectDescriptionTextArea().getText();
            if (description != null && description.length() != 0) {
                parser.setProjectDescription(description);
            }
            String excludeRegex = dialog.getExcludePackagesTextField().getText();
            if (excludeRegex != null && excludeRegex.length() != 0) {
                parser.setPackageExcludeRegex(excludeRegex);
            }
            
            DomainModel model = null;
            try {
                model = parser.parse(xmiFile, 
                    (XmiFileType) dialog.getXmiTypeComboBox().getSelectedItem());
            } catch (Exception ex) {
                ex.printStackTrace();
                CompositeErrorDialog.showErrorDialog(
                    "Error parsing XMI to domain model", ex.getMessage(), ex);
            }
            
            return model;
        }
        return null;
    }
    
    
    private void configureValidation() {
        ValidationComponentUtils.setMessageKey(getXmiFileTextField(), KEY_XMI_FILENAME);
        ValidationComponentUtils.setMessageKey(getSdkDirTextField(), KEY_SDK_DIR);
        ValidationComponentUtils.setMessageKey(getProjectShortNameTextField(), KEY_PROJECT_SHORT_NAME);
        ValidationComponentUtils.setMessageKey(getProjectVersionTextField(), KEY_PROJECT_VERSION);
        ValidationComponentUtils.setMessageKey(getExcludePackagesTextField(), KEY_EXCLUDE_REGEX);
        
        validateInput();
        updateComponentTreeSeverity();
    }
    
    
    private void validateInput() {
        ValidationResult result = new ValidationResult();
        
        if (ValidationUtils.isBlank(getXmiFileTextField().getText())) {
            result.add(new SimpleValidationMessage(
                KEY_XMI_FILENAME + " must not be blank", Severity.ERROR, KEY_XMI_FILENAME));
        }
        
        if (ValidationUtils.isBlank(getProjectShortNameTextField().getText())) {
            result.add(new SimpleValidationMessage(
                KEY_PROJECT_SHORT_NAME + " must not be blank", Severity.ERROR, KEY_PROJECT_SHORT_NAME));
        }
        
        if (ValidationUtils.isBlank(getProjectVersionTextField().getText())) {
            result.add(new SimpleValidationMessage(
                KEY_PROJECT_VERSION + " must not be blank", Severity.ERROR, KEY_PROJECT_VERSION));
        }
        
        if (getFixEaModelCheckBox().isSelected()) {
            if (ValidationUtils.isBlank(getSdkDirTextField().getText())) {
                result.add(new SimpleValidationMessage(
                    KEY_SDK_DIR + " must not be blank.\nPlease select the caCORE SDK directory", 
                    Severity.ERROR, KEY_SDK_DIR));
            }
        }
        
        if (!ValidationUtils.isBlank(getExcludePackagesTextField().getText())) {
            try {
                Pattern.compile(getExcludePackagesTextField().getText());
            } catch (PatternSyntaxException ex) {
                result.add(new SimpleValidationMessage(
                    KEY_EXCLUDE_REGEX + " is not a valid regular expression", 
                    Severity.ERROR, KEY_EXCLUDE_REGEX));
            }
        }
        
        validationModel.setResult(result);
        
        updateComponentTreeSeverity();
        updateOkButton();
    }
    
    
    private void updateComponentTreeSeverity() {
        ValidationComponentUtils.updateComponentTreeMandatoryAndBlankBackground(this);
        ValidationComponentUtils.updateComponentTreeSeverityBackground(this, validationModel.getResult());
    }
    
    
    private void updateOkButton() {
        getOkButton().setEnabled(!validationModel.hasErrors());
    }


    /**
     * This method initializes xmiFileLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getXmiFileLabel() {
        if (xmiFileLabel == null) {
            xmiFileLabel = new JLabel();
            xmiFileLabel.setText("XMI File:");
        }
        return xmiFileLabel;
    }


    /**
     * This method initializes xmiFileTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getXmiFileTextField() {
        if (xmiFileTextField == null) {
            xmiFileTextField = new JTextField();
            xmiFileTextField.setEditable(false);
            xmiFileTextField.addFocusListener(new FocusChangeHandler());
            if (suppliedXmiFilename != null) {
                xmiFileTextField.setText(suppliedXmiFilename);
                getXmiBrowseButton().setEnabled(false);
            }
        }
        return xmiFileTextField;
    }


    /**
     * This method initializes xmiBrowseButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getXmiBrowseButton() {
        if (xmiBrowseButton == null) {
            xmiBrowseButton = new JButton();
            xmiBrowseButton.setText("Browse");
            xmiBrowseButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setFileFilter(new FileFilter() {
                        public boolean accept(File path) {
                            String name = path.getName().toLowerCase();
                            return (path.isDirectory() ||
                                name.endsWith(".xmi") || name.endsWith(".uml"));
                        }
                        
                        
                        public String getDescription() {
                            return "XMI / UML Files (*.xmi | *.uml)";
                        }
                    });
                    int choice = chooser.showOpenDialog(DomainModelFromXmiDialog.this);
                    if (choice == JFileChooser.APPROVE_OPTION) {
                        String selectedFile = chooser.getSelectedFile().getAbsolutePath();
                        getXmiFileTextField().setText(selectedFile);
                    } else {
                        getXmiFileTextField().setText(null);
                    }
                    
                    validateInput();
                }
            });
        }
        return xmiBrowseButton;
    }


    /**
     * This method initializes shortNameLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getShortNameLabel() {
        if (shortNameLabel == null) {
            shortNameLabel = new JLabel();
            shortNameLabel.setText("Project Short Name:");
        }
        return shortNameLabel;
    }


    /**
     * This method initializes shortNameTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getProjectShortNameTextField() {
        if (projectShortNameTextField == null) {
            projectShortNameTextField = new JTextField();
            projectShortNameTextField.addFocusListener(new FocusChangeHandler());
            projectShortNameTextField.getDocument().addDocumentListener(documentChangeListener);
        }
        return projectShortNameTextField;
    }


    /**
     * This method initializes projectVersionLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getProjectVersionLabel() {
        if (projectVersionLabel == null) {
            projectVersionLabel = new JLabel();
            projectVersionLabel.setText("Project Version:");
        }
        return projectVersionLabel;
    }


    /**
     * This method initializes projectVersionTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getProjectVersionTextField() {
        if (projectVersionTextField == null) {
            projectVersionTextField = new JTextField();
            projectVersionTextField.addFocusListener(new FocusChangeHandler());
            projectVersionTextField.getDocument().addDocumentListener(documentChangeListener);
        }
        return projectVersionTextField;
    }


    /**
     * This method initializes projectLongNameLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getProjectLongNameLabel() {
        if (projectLongNameLabel == null) {
            projectLongNameLabel = new JLabel();
            projectLongNameLabel.setText("Project Long Name:");
        }
        return projectLongNameLabel;
    }


    /**
     * This method initializes projectLongNameTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getProjectLongNameTextField() {
        if (projectLongNameTextField == null) {
            projectLongNameTextField = new JTextField();
        }
        return projectLongNameTextField;
    }


    /**
     * This method initializes projectDescriptionLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getProjectDescriptionLabel() {
        if (projectDescriptionLabel == null) {
            projectDescriptionLabel = new JLabel();
            projectDescriptionLabel.setText("Project Description:");
        }
        return projectDescriptionLabel;
    }


    /**
     * This method initializes projectDescriptionTextArea	
     * 	
     * @return javax.swing.JTextArea	
     */
    private JTextArea getProjectDescriptionTextArea() {
        if (projectDescriptionTextArea == null) {
            projectDescriptionTextArea = new JTextArea();
            projectDescriptionTextArea.setLineWrap(true);
            projectDescriptionTextArea.setWrapStyleWord(true);
        }
        return projectDescriptionTextArea;
    }


    /**
     * This method initializes projectDescriptionScrollPane	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getProjectDescriptionScrollPane() {
        if (projectDescriptionScrollPane == null) {
            projectDescriptionScrollPane = new JScrollPane();
            projectDescriptionScrollPane.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            projectDescriptionScrollPane.setViewportView(getProjectDescriptionTextArea());
        }
        return projectDescriptionScrollPane;
    }
    
    
    /**
     * This method initializes excludePackagesLabel 
     *  
     * @return javax.swing.JLabel   
     */
    private JLabel getExcludePackagesLabel() {
        if (excludePackagesLabel == null) {
            excludePackagesLabel = new JLabel();
            excludePackagesLabel.setText("Exclude Packages:");
        }
        return excludePackagesLabel;
    }


    /**
     * This method initializes excludePackagesTextField 
     *  
     * @return javax.swing.JTextField   
     */
    private JTextField getExcludePackagesTextField() {
        if (excludePackagesTextField == null) {
            excludePackagesTextField = new JTextField();
            String excludeValue = suppliedPackageExcludes != null ? 
                suppliedPackageExcludes : XMIParser.DEFAULT_PACKAGE_EXCLUDE_REGEX;
            excludePackagesTextField.setText(excludeValue);
            excludePackagesTextField.setToolTipText("A regular expression indicating what packages to ignore in the XMI");
            excludePackagesTextField.addFocusListener(new FocusChangeHandler());
            excludePackagesTextField.getDocument().addDocumentListener(documentChangeListener);
        }
        return excludePackagesTextField;
    }


    /**
     * This method initializes informationPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getInformationPanel() {
        if (informationPanel == null) {
            GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
            gridBagConstraints22.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints22.gridy = 4;
            gridBagConstraints22.weightx = 1.0;
            gridBagConstraints22.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints22.gridx = 1;
            GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
            gridBagConstraints21.gridx = 0;
            gridBagConstraints21.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints21.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints21.gridy = 4;
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.fill = GridBagConstraints.BOTH;
            gridBagConstraints10.gridy = 3;
            gridBagConstraints10.weightx = 1.0;
            gridBagConstraints10.weighty = 1.0;
            gridBagConstraints10.gridwidth = 2;
            gridBagConstraints10.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints10.gridx = 1;
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.gridx = 0;
            gridBagConstraints9.anchor = GridBagConstraints.NORTHWEST;
            gridBagConstraints9.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints9.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints9.gridy = 3;
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints8.gridy = 2;
            gridBagConstraints8.weightx = 1.0;
            gridBagConstraints8.gridwidth = 1;
            gridBagConstraints8.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints8.gridx = 1;
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.gridx = 0;
            gridBagConstraints7.anchor = GridBagConstraints.WEST;
            gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints7.gridy = 2;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints6.gridy = 1;
            gridBagConstraints6.weightx = 1.0;
            gridBagConstraints6.gridwidth = 1;
            gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints6.gridx = 1;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.anchor = GridBagConstraints.WEST;
            gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints5.gridy = 1;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.gridy = 0;
            gridBagConstraints4.weightx = 1.0;
            gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints4.gridx = 1;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.anchor = GridBagConstraints.WEST;
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.gridy = 0;
            informationPanel = new JPanel();
            informationPanel.setLayout(new GridBagLayout());
            informationPanel.add(getShortNameLabel(), gridBagConstraints3);
            informationPanel.add(getProjectShortNameTextField(), gridBagConstraints4);
            informationPanel.add(getProjectVersionLabel(), gridBagConstraints5);
            informationPanel.add(getProjectVersionTextField(), gridBagConstraints6);
            informationPanel.add(getProjectLongNameLabel(), gridBagConstraints7);
            informationPanel.add(getProjectLongNameTextField(), gridBagConstraints8);
            informationPanel.add(getProjectDescriptionLabel(), gridBagConstraints9);
            informationPanel.add(getProjectDescriptionScrollPane(), gridBagConstraints10);
            informationPanel.add(getExcludePackagesLabel(), gridBagConstraints21);
            informationPanel.add(getExcludePackagesTextField(), gridBagConstraints22);
        }
        return informationPanel;
    }


    /**
     * This method initializes okButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getOkButton() {
        if (okButton == null) {
            okButton = new JButton();
            okButton.setText("OK");
            okButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    canceled = false;
                    dispose();
                }
            });
        }
        return okButton;
    }


    /**
     * This method initializes cancelButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getCancelButton() {
        if (cancelButton == null) {
            cancelButton = new JButton();
            cancelButton.setText("Cancel");
            cancelButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    canceled = true;
                    dispose();
                }
            });
        }
        return cancelButton;
    }


    /**
     * This method initializes buttonPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            GridLayout gridLayout = new GridLayout();
            gridLayout.setRows(1);
            gridLayout.setHgap(4);
            buttonPanel = new JPanel();
            buttonPanel.setLayout(gridLayout);
            buttonPanel.add(getCancelButton(), null);
            buttonPanel.add(getOkButton(), null);
        }
        return buttonPanel;
    }
    
    
    /**
     * This method initializes fixEaModelCheckBox   
     *  
     * @return javax.swing.JCheckBox    
     */
    private JCheckBox getFixEaModelCheckBox() {
        if (fixEaModelCheckBox == null) {
            fixEaModelCheckBox = new JCheckBox();
            fixEaModelCheckBox.setText("Fix EA Model");
            fixEaModelCheckBox.setToolTipText("Requires caCORE SDK 3.2");
            fixEaModelCheckBox.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    boolean enable = fixEaModelCheckBox.isSelected();
                    getSdkDirTextField().setEnabled(enable);
                    getSdkDirBrowseButton().setEnabled(enable);
                    validateInput();
                }
            });
        }
        return fixEaModelCheckBox;
    }


    /**
     * This method initializes sdkDirTextField  
     *  
     * @return javax.swing.JTextField   
     */
    private JTextField getSdkDirTextField() {
        if (sdkDirTextField == null) {
            sdkDirTextField = new JTextField();
            sdkDirTextField.setToolTipText("Select the caCORE SDK 3.2 Directory");
            sdkDirTextField.setEditable(false);
            sdkDirTextField.setEnabled(getFixEaModelCheckBox().isSelected());
            sdkDirTextField.getDocument().addDocumentListener(documentChangeListener);
        }
        return sdkDirTextField;
    }


    /**
     * This method initializes sdkDirBrowseButton   
     *  
     * @return javax.swing.JButton  
     */
    private JButton getSdkDirBrowseButton() {
        if (sdkDirBrowseButton == null) {
            sdkDirBrowseButton = new JButton();
            sdkDirBrowseButton.setText("Browse");
            sdkDirBrowseButton.setEnabled(getFixEaModelCheckBox().isSelected());
            sdkDirBrowseButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    chooser.setApproveButtonText("Select");
                    int choice = chooser.showOpenDialog(DomainModelFromXmiDialog.this);
                    if (choice == JFileChooser.APPROVE_OPTION) {
                        getSdkDirTextField().setText(chooser.getSelectedFile().getAbsolutePath());
                    } else {
                        getSdkDirTextField().setText("");
                    }
                    validateInput();
                }
            });
        }
        return sdkDirBrowseButton;
    }
    
    
    /**
     * This method initializes xmiTypeComboBox  
     *  
     * @return javax.swing.JComboBox    
     */
    private JComboBox getXmiTypeComboBox() {
        if (xmiTypeComboBox == null) {
            xmiTypeComboBox = new JComboBox();
            for (XmiFileType type : XmiFileType.values()) {
                xmiTypeComboBox.addItem(type);
            }
            xmiTypeComboBox.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    XmiFileType selectedType = (XmiFileType) getXmiTypeComboBox().getSelectedItem();
                    boolean fixXmiAllowed = (selectedType == XmiFileType.SDK_32_EA);
                    getFixEaModelCheckBox().setEnabled(fixXmiAllowed);
                    getSdkDirTextField().setEnabled(fixXmiAllowed);
                    getSdkDirBrowseButton().setEnabled(fixXmiAllowed);
                }
            });
        }
        return xmiTypeComboBox;
    }


    /**
     * This method initializes xmiTypeLabel 
     *  
     * @return javax.swing.JLabel   
     */
    private JLabel getXmiTypeLabel() {
        if (xmiTypeLabel == null) {
            xmiTypeLabel = new JLabel();
            xmiTypeLabel.setText("XMI Type:");
        }
        return xmiTypeLabel;
    }


    /**
     * This method initializes xmiTypePanel 
     *  
     * @return javax.swing.JPanel   
     */
    private JPanel getXmiTypePanel() {
        if (xmiTypePanel == null) {
            GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
            gridBagConstraints14.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints14.gridy = 0;
            gridBagConstraints14.weightx = 1.0;
            gridBagConstraints14.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints14.gridx = 1;
            GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
            gridBagConstraints13.gridx = 0;
            gridBagConstraints13.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints13.gridy = 0;
            xmiTypePanel = new JPanel();
            xmiTypePanel.setLayout(new GridBagLayout());
            xmiTypePanel.add(getXmiTypeLabel(), gridBagConstraints13);
            xmiTypePanel.add(getXmiTypeComboBox(), gridBagConstraints14);
        }
        return xmiTypePanel;
    }


    /**
     * This method initializes fixEaModelPanel  
     *  
     * @return javax.swing.JPanel   
     */
    private JPanel getFixEaModelPanel() {
        if (fixEaModelPanel == null) {
            GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
            gridBagConstraints16.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints16.gridx = 2;
            gridBagConstraints16.gridy = 0;
            gridBagConstraints16.insets = new Insets(2, 2, 2, 2);
            GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
            gridBagConstraints15.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints15.gridx = 1;
            gridBagConstraints15.gridy = 0;
            gridBagConstraints15.weightx = 1.0;
            gridBagConstraints15.insets = new Insets(2, 2, 2, 2);
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.anchor = GridBagConstraints.WEST;
            gridBagConstraints12.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints12.gridx = 0;
            gridBagConstraints12.gridy = 0;
            fixEaModelPanel = new JPanel();
            fixEaModelPanel.setLayout(new GridBagLayout());
            fixEaModelPanel.add(getFixEaModelCheckBox(), gridBagConstraints12);
            fixEaModelPanel.add(getSdkDirTextField(), gridBagConstraints15);
            fixEaModelPanel.add(getSdkDirBrowseButton(), gridBagConstraints16);
        }
        return fixEaModelPanel;
    }


    /**
     * This method initializes xmiBrowsePanel   
     *  
     * @return javax.swing.JPanel   
     */
    private JPanel getXmiBrowsePanel() {
        if (xmiBrowsePanel == null) {
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.gridx = 2;
            gridBagConstraints2.gridy = 0;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.gridx = 1;
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.weightx = 1.0;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            xmiBrowsePanel = new JPanel();
            xmiBrowsePanel.setLayout(new GridBagLayout());
            xmiBrowsePanel.add(getXmiFileLabel(), gridBagConstraints);
            xmiBrowsePanel.add(getXmiFileTextField(), gridBagConstraints1);
            xmiBrowsePanel.add(getXmiBrowseButton(), gridBagConstraints2);
        }
        return xmiBrowsePanel;
    }


    /**
     * This method initializes mainPanel    
     *  
     * @return javax.swing.JPanel   
     */
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
            gridBagConstraints20.gridx = 0;
            gridBagConstraints20.anchor = GridBagConstraints.EAST;
            gridBagConstraints20.gridy = 4;
            GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
            gridBagConstraints19.gridx = 0;
            gridBagConstraints19.weightx = 1.0D;
            gridBagConstraints19.fill = GridBagConstraints.BOTH;
            gridBagConstraints19.weighty = 1.0D;
            gridBagConstraints19.gridy = 3;
            GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
            gridBagConstraints18.gridx = 0;
            gridBagConstraints18.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints18.weightx = 1.0D;
            gridBagConstraints18.gridy = 2;
            GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
            gridBagConstraints17.gridx = 0;
            gridBagConstraints17.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints17.weightx = 1.0D;
            gridBagConstraints17.gridy = 1;
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridx = 0;
            gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints11.weightx = 1.0D;
            gridBagConstraints11.gridy = 0;
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.setSize(new Dimension(356, 270));
            mainPanel.add(getXmiBrowsePanel(), gridBagConstraints11);
            mainPanel.add(getXmiTypePanel(), gridBagConstraints17);
            mainPanel.add(getFixEaModelPanel(), gridBagConstraints18);
            mainPanel.add(getInformationPanel(), gridBagConstraints19);
            mainPanel.add(getButtonPanel(), gridBagConstraints20);
        }
        return mainPanel;
    }
    
    
    private final class FocusChangeHandler implements FocusListener {

        public void focusGained(FocusEvent e) {
            update();
        }


        public void focusLost(FocusEvent e) {
            update();
        }


        private void update() {
            validateInput();
        }
    }


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println("Error selecting system look and feel");
        }
        // set up the Grid Portal application instance
        Application app = new Application();
        app.setName("Domain Model from XMI");
        try {
            GridApplication.getInstance(app);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Error creating grid application instance");
            System.exit(1);
        }
        
        DomainModel model = createDomainModel(null);
        if (model != null) {
            JFileChooser saveChooser = new JFileChooser();
            saveChooser.setFileFilter(FileFilters.XML_FILTER);
            int choice = saveChooser.showSaveDialog(null);
            if (choice == JFileChooser.APPROVE_OPTION) {
                try {
                    FileWriter writer = new FileWriter(saveChooser.getSelectedFile());
                    MetadataUtils.serializeDomainModel(model, writer);
                    writer.flush();
                    writer.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.exit(1);
                }
            }
        }
    }
}
