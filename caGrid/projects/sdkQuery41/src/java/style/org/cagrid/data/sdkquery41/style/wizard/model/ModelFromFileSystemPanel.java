package org.cagrid.data.sdkquery41.style.wizard.model;

import gov.nih.nci.cagrid.common.portal.validation.IconFeedbackPanel;
import gov.nih.nci.cagrid.data.ui.wizard.OneTimeInfoDialogUtil;
import gov.nih.nci.cagrid.introduce.common.FileFilters;
import gov.nih.nci.cagrid.introduce.common.ResourceManager;
import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.metadata.dataservice.UMLClass;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.cagrid.data.sdkquery41.style.wizard.DomainModelSourcePanel;
import org.cagrid.data.sdkquery41.style.wizard.DomainModelSourceValidityListener;
import org.cagrid.data.sdkquery41.style.wizard.config.DomainModelConfigurationStep;
import org.cagrid.data.sdkquery41.style.wizard.config.DomainModelConfigurationStep.DomainModelConfigurationSource;
import org.cagrid.grape.utils.CompositeErrorDialog;

import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.util.ValidationUtils;
import com.jgoodies.validation.view.ValidationComponentUtils;

public class ModelFromFileSystemPanel extends DomainModelSourcePanel {
    
    private static final String KEY_MODEL_FILE = "Domain Model file";
    
    private ValidationResultModel validationModel = null;
    private IconFeedbackPanel validationOverlayPanel = null;
    
    private JPanel mainPanel = null;
    private JLabel modelFilenameLabel = null;
    private JTextField modelFilenameTextField = null;
    private JButton modelBrowseButton = null;
    private JList packagesList = null;
    private JScrollPane packagesScrollPane = null;
    private JLabel shortNameLabel = null;
    private JLabel versionLabel = null;
    private JLabel longNameLabel = null;
    private JLabel descriptionLabel = null;
    private JTextField shortNameTextField = null;
    private JTextField versionTextField = null;
    private JTextField longNameTextField = null;
    private JTextArea descriptionTextArea = null;
    private JScrollPane descriptionScrollPane = null;

    public ModelFromFileSystemPanel(
        DomainModelSourceValidityListener validityListener, 
        DomainModelConfigurationStep configuration) {
        super(validityListener, configuration);
        validationModel = new DefaultValidationResultModel();
        initialize();
    }
    
    
    public DomainModelConfigurationSource getSourceType() {
        return DomainModelConfigurationSource.FILE_SYSTEM;
    }


    public String getName() {
        return "Pre-Generated";
    }


    public void populateFromConfiguration() {
        // TODO Auto-generated method stub
    }
    
    
    public void revalidateModel() {
        validateInput();
    }
    
    
    private void initialize() {
        configureValidation();
        setLayout(new GridLayout());
        add(getValidationOverlayPanel());
    }
    
    
    private IconFeedbackPanel getValidationOverlayPanel() {
        if (validationOverlayPanel == null) {
            validationOverlayPanel = new IconFeedbackPanel(validationModel, getMainPanel());
        }
        return validationOverlayPanel;
    }
    
    
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.fill = GridBagConstraints.BOTH;
            gridBagConstraints11.gridy = 5;
            gridBagConstraints11.weightx = 1.0;
            gridBagConstraints11.weighty = 1.0;
            gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints11.gridwidth = 3;
            gridBagConstraints11.gridx = 0;
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints10.gridy = 4;
            gridBagConstraints10.weightx = 1.0;
            gridBagConstraints10.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints10.gridwidth = 2;
            gridBagConstraints10.gridx = 1;
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.gridx = 0;
            gridBagConstraints9.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints9.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints9.gridy = 4;
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints8.gridy = 3;
            gridBagConstraints8.weightx = 1.0;
            gridBagConstraints8.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints8.gridwidth = 2;
            gridBagConstraints8.gridx = 1;
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.gridx = 0;
            gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints7.gridy = 3;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints6.gridy = 2;
            gridBagConstraints6.weightx = 1.0;
            gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints6.gridwidth = 2;
            gridBagConstraints6.gridx = 1;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.gridy = 2;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.gridy = 1;
            gridBagConstraints4.weightx = 1.0;
            gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints4.gridwidth = 2;
            gridBagConstraints4.gridx = 1;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.gridy = 1;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 2;
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.gridy = 0;
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
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.add(getModelFilenameLabel(), gridBagConstraints);
            mainPanel.add(getModelFilenameTextField(), gridBagConstraints1);
            mainPanel.add(getModelBrowseButton(), gridBagConstraints2);
            mainPanel.add(getShortNameLabel(), gridBagConstraints3);
            mainPanel.add(getShortNameTextField(), gridBagConstraints4);
            mainPanel.add(getVersionLabel(), gridBagConstraints5);
            mainPanel.add(getVersionTextField(), gridBagConstraints6);
            mainPanel.add(getLongNameLabel(), gridBagConstraints7);
            mainPanel.add(getLongNameTextField(), gridBagConstraints8);
            mainPanel.add(getDescriptionLabel(), gridBagConstraints9);
            mainPanel.add(getDescriptionScrollPane(), gridBagConstraints10);
            mainPanel.add(getPackagesScrollPane(), gridBagConstraints11);
        }
        return mainPanel;
    }


    /**
     * This method initializes modelFilenameLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getModelFilenameLabel() {
        if (modelFilenameLabel == null) {
            modelFilenameLabel = new JLabel();
            modelFilenameLabel.setText("Model Filename:");
        }
        return modelFilenameLabel;
    }


    /**
     * This method initializes modelFilenameTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getModelFilenameTextField() {
        if (modelFilenameTextField == null) {
            modelFilenameTextField = new JTextField();
            modelFilenameTextField.setEditable(false);
        }
        return modelFilenameTextField;
    }


    /**
     * This method initializes modelBrowseButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getModelBrowseButton() {
        if (modelBrowseButton == null) {
            modelBrowseButton = new JButton();
            modelBrowseButton.setText("Browse");
            modelBrowseButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    String[] message = {
                        "Select the Domain Model XML document on your",
                        "file system which represents the data model",
                        "explosed by the caCORE SDK system."
                    };
                    OneTimeInfoDialogUtil.showInfoDialog(
                        ModelFromFileSystemPanel.class, KEY_MODEL_FILE, message);
                    String selectedFilename = null;
                    try {
                        selectedFilename = ResourceManager.promptFile(null, FileFilters.XML_FILTER);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        CompositeErrorDialog.showErrorDialog(
                            "Error selecting Domain Model", ex.getMessage(), ex);
                    }
                    if (selectedFilename != null) {
                        getModelFilenameTextField().setText(selectedFilename);
                        getConfiguration().setDomainModelLocalFile(new File(selectedFilename));
                        validateInput();
                        if (!validationModel.hasErrors()) {
                            // valid domain model, so we can populate fields
                            populateFromSelectedModel();
                        }
                    }
                }
            });
        }
        return modelBrowseButton;
    }


    /**
     * This method initializes packagesList	
     * 	
     * @return javax.swing.JList	
     */
    private JList getPackagesList() {
        if (packagesList == null) {
            packagesList = new JList(new DefaultListModel());
        }
        return packagesList;
    }


    /**
     * This method initializes packagesScrollPane	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getPackagesScrollPane() {
        if (packagesScrollPane == null) {
            packagesScrollPane = new JScrollPane();
            packagesScrollPane.setViewportView(getPackagesList());
            packagesScrollPane.setBorder(BorderFactory.createTitledBorder(
                null, "Packages", TitledBorder.DEFAULT_JUSTIFICATION, 
                TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
        }
        return packagesScrollPane;
    }


    /**
     * This method initializes shortNameLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getShortNameLabel() {
        if (shortNameLabel == null) {
            shortNameLabel = new JLabel();
            shortNameLabel.setText("Short Name:");
        }
        return shortNameLabel;
    }


    /**
     * This method initializes versionLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getVersionLabel() {
        if (versionLabel == null) {
            versionLabel = new JLabel();
            versionLabel.setText("Version:");
        }
        return versionLabel;
    }


    /**
     * This method initializes longNameLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getLongNameLabel() {
        if (longNameLabel == null) {
            longNameLabel = new JLabel();
            longNameLabel.setText("Long Name:");
        }
        return longNameLabel;
    }


    /**
     * This method initializes descriptionLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getDescriptionLabel() {
        if (descriptionLabel == null) {
            descriptionLabel = new JLabel();
            descriptionLabel.setText("Description:");
        }
        return descriptionLabel;
    }


    /**
     * This method initializes shortNameTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getShortNameTextField() {
        if (shortNameTextField == null) {
            shortNameTextField = new JTextField();
            shortNameTextField.setEditable(false);
        }
        return shortNameTextField;
    }


    /**
     * This method initializes versionTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getVersionTextField() {
        if (versionTextField == null) {
            versionTextField = new JTextField();
            versionTextField.setEditable(false);
        }
        return versionTextField;
    }


    /**
     * This method initializes longNameTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getLongNameTextField() {
        if (longNameTextField == null) {
            longNameTextField = new JTextField();
            longNameTextField.setEditable(false);
        }
        return longNameTextField;
    }


    /**
     * This method initializes descriptionTextArea	
     * 	
     * @return javax.swing.JTextArea	
     */
    private JTextArea getDescriptionTextArea() {
        if (descriptionTextArea == null) {
            descriptionTextArea = new JTextArea();
            descriptionTextArea.setEditable(false);
            descriptionTextArea.setLineWrap(true);
            descriptionTextArea.setWrapStyleWord(true);
        }
        return descriptionTextArea;
    }


    /**
     * This method initializes descriptionScrollPane	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getDescriptionScrollPane() {
        if (descriptionScrollPane == null) {
            descriptionScrollPane = new JScrollPane();
            descriptionScrollPane.setViewportView(getDescriptionTextArea());
        }
        return descriptionScrollPane;
    }
    
    
    // ----------
    // helpers
    // ----------
    
    
    private void populateFromSelectedModel() {
        // read in the domain model
        DomainModel model = null;
        FileReader reader = null;
        try {
            reader = new FileReader(getModelFilenameTextField().getText());
            model = MetadataUtils.deserializeDomainModel(reader);
        } catch (Exception ex) {
            ex.printStackTrace();
            CompositeErrorDialog.showErrorDialog("Error loading domain model", ex.getMessage(), ex);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception ex) {
                    // we tried
                }
            }
        }
        
        if (model != null) {
            // sort the package names
            SortedSet<String> packages = new TreeSet<String>();
            for (UMLClass clazz : model.getExposedUMLClassCollection().getUMLClass()) {
                packages.add(clazz.getPackageName());
            }
            // clear the package list
            DefaultListModel listModel = (DefaultListModel) getPackagesList().getModel();
            while (listModel.size() != 0) {
                listModel.removeElementAt(0);
            }
            // add packages to the list
            for (String name : packages) {
                listModel.addElement(name);
            }
            // populate other model information fields
            String shortName = model.getProjectShortName();
            String version = model.getProjectVersion();
            String longName = model.getProjectLongName();
            String description = model.getProjectDescription();
            getShortNameTextField().setText(shortName != null ? shortName : "");
            getVersionTextField().setText(version != null ? version : "");
            getLongNameTextField().setText(longName != null ? longName : "");
            getDescriptionTextArea().setText(description != null ? description : "");
        }
    }
    
    
    // ----------
    // validation
    // ----------
    
    
    private void configureValidation() {
        ValidationComponentUtils.setMessageKey(getModelFilenameTextField(), KEY_MODEL_FILE);
        
        validateInput();
        updateComponentTreeSeverity();
    }
    
    
    private void validateInput() {
        ValidationResult result = new ValidationResult();
        
        if (ValidationUtils.isBlank(getModelFilenameTextField().getText())) {
            result.add(new SimpleValidationMessage(KEY_MODEL_FILE + " cannot be blank!", Severity.ERROR, KEY_MODEL_FILE));
        } else {
            // validate the domain model
            FileReader reader = null;
            try {
                reader = new FileReader(getModelFilenameTextField().getText());
                MetadataUtils.deserializeDomainModel(reader);
            } catch (Exception ex) {
                result.add(new SimpleValidationMessage(getModelFilenameTextField().getText() 
                    + " does not appear to be a valid domain model.  See console for details", Severity.ERROR, KEY_MODEL_FILE));
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException ex) {
                        // we tried!
                    }
                }
            }
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
