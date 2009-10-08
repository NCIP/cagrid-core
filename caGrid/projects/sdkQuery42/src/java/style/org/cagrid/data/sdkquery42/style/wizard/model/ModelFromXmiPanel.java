package org.cagrid.data.sdkquery42.style.wizard.model;

import gov.nih.nci.cagrid.common.portal.DocumentChangeAdapter;
import gov.nih.nci.cagrid.common.portal.validation.IconFeedbackPanel;
import gov.nih.nci.cagrid.data.ui.GroupSelectionListener;
import gov.nih.nci.cagrid.data.ui.NotifyingButtonGroup;
import gov.nih.nci.cagrid.introduce.common.ResourceManager;
import gov.nih.nci.cagrid.metadata.xmi.XmiFileType;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.ButtonModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.filechooser.FileFilter;

import org.cagrid.data.sdkquery42.style.wizard.DomainModelSourcePanel;
import org.cagrid.data.sdkquery42.style.wizard.DomainModelSourceValidityListener;
import org.cagrid.data.sdkquery42.style.wizard.config.DomainModelConfigurationStep;
import org.cagrid.data.sdkquery42.style.wizard.config.DomainModelConfigurationStep.DomainModelConfigurationSource;
import org.cagrid.grape.utils.CompositeErrorDialog;

import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.util.ValidationUtils;
import com.jgoodies.validation.view.ValidationComponentUtils;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JRadioButton;

/**
 * ModelFromXmiPanel
 * Creates domain models from the XMI file the selected 
 * caCORE SDK installation has been configured to use
 * 
 * @author David
 */
public class ModelFromXmiPanel extends DomainModelSourcePanel {
    
    private static final String KEY_XMI_FILENAME = "XMI filename";
    private static final String KEY_PROJECT_NAME = "Project name";
    private static final String KEY_PROJECT_VERSION = "Project version";
    
    private ValidationResultModel validationModel = null;
    private IconFeedbackPanel validationOverlayPanel = null;

    private JPanel mainPanel = null;
    private JLabel xmiFileLabel = null;
    private JTextField xmiFileTextField = null;
    private JLabel xmiTypeLabel = null;
    private JLabel projectNameLabel = null;
    private JLabel projectVersionLabel = null;
    private JTextField projectNameTextField = null;
    private JTextField projectVersionTextField = null;
    private JButton browseButton = null;
    private JPanel xmiTypePanel = null;
    private JRadioButton eaXmiTypeRadioButton = null;
    private JRadioButton argoXmiTypeRadioButton = null;
    
    public ModelFromXmiPanel(
        DomainModelSourceValidityListener validityListener, 
        DomainModelConfigurationStep configuration) {
        super(validityListener, configuration);
        validationModel = new DefaultValidationResultModel();
        initialize();
    }
    
    
    public DomainModelConfigurationSource getSourceType() {
        return DomainModelConfigurationSource.XMI;
    }
    
    
    public String getName() {
        return "XMI";
    }
    
    
    public void revalidateModel() {
        validateInput();
    }
    
    
    public void populateFromConfiguration() {
        validateInput();
    }
    
    
    private void initialize() {
        NotifyingButtonGroup xmiTypeGroup = new NotifyingButtonGroup();
        xmiTypeGroup.addGroupSelectionListener(new GroupSelectionListener() {
            public void selectionChanged(ButtonModel previousSelection, ButtonModel currentSelection) {
                getConfiguration().setXmiType(currentSelection == getEaXmiTypeRadioButton().getModel() ? 
                    XmiFileType.SDK_40_EA : XmiFileType.SDK_40_ARGO);
            }
        });
        xmiTypeGroup.add(getEaXmiTypeRadioButton());
        xmiTypeGroup.add(getArgoXmiTypeRadioButton());
        configureValidation();
        setLayout(new GridLayout());
        add(getValidationOverlayPanel());
        validateInput();
    }
    
    
    private IconFeedbackPanel getValidationOverlayPanel() {
        if (validationOverlayPanel == null) {
            validationOverlayPanel = new IconFeedbackPanel(validationModel, getMainPanel());
        }
        return validationOverlayPanel;
    }
    
    
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            mainPanel = new JPanel();
            GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
            gridBagConstraints22.gridx = 1;
            gridBagConstraints22.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints22.gridwidth = 2;
            gridBagConstraints22.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints22.gridy = 1;
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.gridx = 2;
            gridBagConstraints12.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints12.gridy = 0;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.gridwidth = 2;
            gridBagConstraints5.gridy = 4;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.gridy = 3;
            gridBagConstraints4.weightx = 1.0;
            gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints4.gridwidth = 2;
            gridBagConstraints4.gridx = 1;
            GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
            gridBagConstraints31.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints31.gridy = 2;
            gridBagConstraints31.weightx = 1.0;
            gridBagConstraints31.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints31.gridwidth = 2;
            gridBagConstraints31.gridx = 1;
            GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
            gridBagConstraints21.gridx = 0;
            gridBagConstraints21.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints21.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints21.gridy = 3;
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridx = 0;
            gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints11.gridy = 2;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.gridy = 1;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.weightx = 1.0;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.gridx = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.gridy = 0;
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.setSize(new Dimension(482, 115));
            mainPanel.add(getXmiFileLabel(), gridBagConstraints);
            mainPanel.add(getXmiFileTextField(), gridBagConstraints1);
            mainPanel.add(getXmiTypeLabel(), gridBagConstraints2);
            mainPanel.add(getProjectNameLabel(), gridBagConstraints11);
            mainPanel.add(getProjectVersionLabel(), gridBagConstraints21);
            mainPanel.add(getProjectNameTextField(), gridBagConstraints31);
            mainPanel.add(getProjectVersionTextField(), gridBagConstraints4);
            mainPanel.add(getBrowseButton(), gridBagConstraints12);
            mainPanel.add(getXmiTypePanel(), gridBagConstraints22);
        }
        return mainPanel;
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
            xmiFileTextField.getDocument().addDocumentListener(new DocumentChangeAdapter() {
                public void documentEdited(DocumentEvent e) {
                    validateInput();
                }
            });
        }
        return xmiFileTextField;
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
     * This method initializes projectNameLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getProjectNameLabel() {
        if (projectNameLabel == null) {
            projectNameLabel = new JLabel();
            projectNameLabel.setText("Project Short Name:");
        }
        return projectNameLabel;
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
     * This method initializes projectNameTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getProjectNameTextField() {
        if (projectNameTextField == null) {
            projectNameTextField = new JTextField();
            projectNameTextField.getDocument().addDocumentListener(new DocumentChangeAdapter() {
                public void documentEdited(DocumentEvent e) {
                    getConfiguration().setProjectShortName(getProjectNameTextField().getText());
                    validateInput();
                }
            });
        }
        return projectNameTextField;
    }


    /**
     * This method initializes projectVersionTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getProjectVersionTextField() {
        if (projectVersionTextField == null) {
            projectVersionTextField = new JTextField();
            projectVersionTextField.getDocument().addDocumentListener(new DocumentChangeAdapter() {
                public void documentEdited(DocumentEvent e) {
                    getConfiguration().setProjectVersion(getProjectVersionTextField().getText());
                    validateInput();
                }
            });
        }
        return projectVersionTextField;
    }


    /**
     * This method initializes browseButton 
     *  
     * @return javax.swing.JButton  
     */
    private JButton getBrowseButton() {
        if (browseButton == null) {
            browseButton = new JButton();
            browseButton.setText("Browse");
            browseButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String filename = null;
                    try {
                        filename = ResourceManager.promptFile(null, new FileFilter() {
                            public String getDescription() {
                                return "(*.xmi) Xml Metadata Interchange";
                            }
                            
                        
                            public boolean accept(File f) {
                                return f.getName().toLowerCase().endsWith(".xmi");
                            }
                        });
                    } catch (IOException ex) {
                        CompositeErrorDialog.showErrorDialog("Error selecting file: " + ex.getMessage(), ex);
                    }
                    getConfiguration().setXmiFile(filename != null ? new File(filename) : null);
                    validateInput();
                }
            });
        }
        return browseButton;
    }


    /**
     * This method initializes xmiTypePanel 
     *  
     * @return javax.swing.JPanel   
     */
    private JPanel getXmiTypePanel() {
        if (xmiTypePanel == null) {
            GridLayout gridLayout = new GridLayout();
            gridLayout.setRows(1);
            gridLayout.setHgap(4);
            gridLayout.setColumns(2);
            xmiTypePanel = new JPanel();
            xmiTypePanel.setLayout(gridLayout);
            xmiTypePanel.add(getEaXmiTypeRadioButton(), null);
            xmiTypePanel.add(getArgoXmiTypeRadioButton(), null);
        }
        return xmiTypePanel;
    }


    /**
     * This method initializes eaXmiTypeRadioButton 
     *  
     * @return javax.swing.JRadioButton 
     */
    private JRadioButton getEaXmiTypeRadioButton() {
        if (eaXmiTypeRadioButton == null) {
            eaXmiTypeRadioButton = new JRadioButton();
            eaXmiTypeRadioButton.setText("Enterprise Architect");
        }
        return eaXmiTypeRadioButton;
    }


    /**
     * This method initializes argoXmiTypeRadioButton   
     *  
     * @return javax.swing.JRadioButton 
     */
    private JRadioButton getArgoXmiTypeRadioButton() {
        if (argoXmiTypeRadioButton == null) {
            argoXmiTypeRadioButton = new JRadioButton();
            argoXmiTypeRadioButton.setText("Argo UML");
        }
        return argoXmiTypeRadioButton;
    }
    
    
    // ----------
    // validation
    // ----------
    
    
    private void configureValidation() {
        ValidationComponentUtils.setMessageKey(getXmiFileTextField(), KEY_XMI_FILENAME);
        ValidationComponentUtils.setMessageKey(getProjectNameTextField(), KEY_PROJECT_NAME);
        ValidationComponentUtils.setMessageKey(getProjectVersionTextField(), KEY_PROJECT_VERSION);
        
        validateInput();
        updateComponentTreeSeverity();
    }
    
    
    private void validateInput() {
        ValidationResult result = new ValidationResult();
        
        if (ValidationUtils.isBlank(getXmiFileTextField().getText())) {
            result.add(new SimpleValidationMessage("XMI Filename cannot be blank!", Severity.ERROR, KEY_XMI_FILENAME));
        }
        if (ValidationUtils.isBlank(getProjectNameTextField().getText())) {
            result.add(new SimpleValidationMessage(KEY_PROJECT_NAME + " cannot be blank!", Severity.ERROR, KEY_PROJECT_NAME));
        }
        if (ValidationUtils.isBlank(getProjectVersionTextField().getText())) {
            result.add(new SimpleValidationMessage(KEY_PROJECT_VERSION + " cannot be blank!", Severity.ERROR, KEY_PROJECT_VERSION));
        }
        
        validationModel.setResult(result);
        
        setModelValidity(!result.hasErrors());
        
        updateComponentTreeSeverity();
    }
    
    
    private void updateComponentTreeSeverity() {
        ValidationComponentUtils.updateComponentTreeMandatoryAndBlankBackground(this);
        ValidationComponentUtils.updateComponentTreeSeverityBackground(this, validationModel.getResult());
    }
}
