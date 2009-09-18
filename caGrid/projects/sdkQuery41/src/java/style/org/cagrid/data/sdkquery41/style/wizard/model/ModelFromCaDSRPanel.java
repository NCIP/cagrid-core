package org.cagrid.data.sdkquery41.style.wizard.model;

import gov.nih.nci.cadsr.umlproject.domain.Project;
import gov.nih.nci.cadsr.umlproject.domain.UMLPackageMetadata;
import gov.nih.nci.cagrid.common.portal.DocumentChangeAdapter;
import gov.nih.nci.cagrid.common.portal.validation.IconFeedbackPanel;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.introduce.common.ConfigurationUtil;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;

import org.cagrid.cadsr.portal.CaDSRBrowserPanel;
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
import com.jgoodies.validation.view.ValidationComponentUtils;

public class ModelFromCaDSRPanel extends DomainModelSourcePanel {
    
    private static final String KEY_PACKAGES_LIST = "Selected packages list";
    
    private ValidationResultModel validationModel = null;
    private IconFeedbackPanel validationOverlayPanel = null;
    
    private JPanel mainPanel = null;
    private InternalCaDSRBrowserPanel cadsrBrowser = null;
    private JButton addProjectButton = null;
    private JButton addPackageButton = null;
    private JButton removePackageButton = null;
    private JPanel buttonPanel = null;
    private JList packagesList = null;
    private JScrollPane packagesScrollPane = null;
    
    private Project selectedProject = null;

    public ModelFromCaDSRPanel(
        DomainModelSourceValidityListener validityListener, 
        DomainModelConfigurationStep configuration) {
        super(validityListener, configuration);
        validationModel = new DefaultValidationResultModel();
        initialize();
    }
    
    
    public DomainModelConfigurationSource getSourceType() {
        return DomainModelConfigurationSource.CADSR;
    }


    public String getName() {
        return "caDSR";
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
            validationOverlayPanel = 
                new IconFeedbackPanel(validationModel, getMainPanel());
        }
        return validationOverlayPanel;
    }
    
    
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.fill = GridBagConstraints.BOTH;
            gridBagConstraints2.gridy = 1;
            gridBagConstraints2.weightx = 1.0;
            gridBagConstraints2.weighty = 1.0;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.gridx = 1;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 1;
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.weightx = 1.0D;
            gridBagConstraints1.gridy = 0;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridheight = 2;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.gridy = 0;
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.add(getCadsrBrowser(), gridBagConstraints);
            mainPanel.add(getButtonPanel(), gridBagConstraints1);
            mainPanel.add(getPackagesScrollPane(), gridBagConstraints2);
        }
        return mainPanel;
    }
    
    
    private InternalCaDSRBrowserPanel getCadsrBrowser() {
        if (cadsrBrowser == null) {
            cadsrBrowser = new InternalCaDSRBrowserPanel(true, false);
            // get the configured URL from the cagrid global configuration
            String url = null;
            try {
                url = ConfigurationUtil.getGlobalExtensionProperty(
                    DataServiceConstants.CADSR_SERVICE_URL).getValue();
            } catch (Exception ex) {
                ex.printStackTrace();
                CompositeErrorDialog.showErrorDialog(
                    "Error determining caDSR service URL from configuration", 
                    ex.getMessage(), ex);
            }
            cadsrBrowser.setDefaultCaDSRURL(url);
            cadsrBrowser.getCadsr().setText(url);
            getConfiguration().setCadsrUrl(url);
            cadsrBrowser.getCadsr().getDocument().addDocumentListener(new DocumentChangeAdapter() {
                public void documentEdited(DocumentEvent e) {
                    getConfiguration().setCadsrUrl(getCadsrBrowser().getCadsr().getText());
                }
            });
        }
        return cadsrBrowser;
    }


    /**
     * This method initializes addProjectButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getAddProjectButton() {
        if (addProjectButton == null) {
            addProjectButton = new JButton();
            addProjectButton.setText("Add Project");
            addProjectButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    addProjectToModel();
                    validateInput();
                }
            });
        }
        return addProjectButton;
    }


    /**
     * This method initializes addPackageButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getAddPackageButton() {
        if (addPackageButton == null) {
            addPackageButton = new JButton();
            addPackageButton.setText("Add Package");
            addPackageButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    UMLPackageMetadata selectedPackage = getCadsrBrowser().getSelectedPackage();
                    if (selectedPackage != null) {
                        addPackageToModel(selectedPackage);
                    }
                    validateInput();
                }
            });
        }
        return addPackageButton;
    }


    /**
     * This method initializes removePackageButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getRemovePackageButton() {
        if (removePackageButton == null) {
            removePackageButton = new JButton();
            removePackageButton.setText("Remove Selected");
            removePackageButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    removeSelectedPackages();
                    validateInput();
                }
            });
        }
        return removePackageButton;
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
            gridLayout.setColumns(3);
            gridLayout.setHgap(4);
            buttonPanel = new JPanel();
            buttonPanel.setLayout(gridLayout);
            buttonPanel.add(getAddProjectButton(), null);
            buttonPanel.add(getAddPackageButton(), null);
            buttonPanel.add(getRemovePackageButton(), null);
        }
        return buttonPanel;
    }


    /**
     * This method initializes packagesList	
     * 	
     * @return javax.swing.JList	
     */
    private JList getPackagesList() {
        if (packagesList == null) {
            packagesList = new JList(new DefaultListModel());
            packagesList.setSelectionMode(
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
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
                null, "Selected Packages", TitledBorder.DEFAULT_JUSTIFICATION, 
                TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
        }
        return packagesScrollPane;
    }
    
    
    // ----------
    // helpers
    // ----------
    
    
    private void addProjectToModel() {
        // clear the packages list
        DefaultListModel model = (DefaultListModel) getPackagesList().getModel();
        while (model.getSize() != 0) {
            Object element = model.getElementAt(0);
            if (element instanceof UMLPackageDisplay) {
                UMLPackageDisplay packageDisplay = (UMLPackageDisplay) element;
                getConfiguration().removeCadsrPackage(packageDisplay.getPackage().getName());
            }
            model.removeElementAt(0);
        }
                
        // get the selected project
        Project project = getCadsrBrowser().getSelectedProject();
        // set it as the current project
        this.selectedProject = project;
        getConfiguration().setCadsrProject(this.selectedProject);
        if (project != null) {
            UMLPackageMetadata[] packages = getCadsrBrowser().getAvailablePackages();
            if (packages != null) {
                for (UMLPackageMetadata pack : packages) {
                    addPackageToModel(pack);
                }
            }
        }
    }
    
    
    private void addPackageToModel(UMLPackageMetadata pack) {
        Project project = getCadsrBrowser().getSelectedProject();
        DefaultListModel model = (DefaultListModel) getPackagesList().getModel();
        if (!projectsEqual(project, this.selectedProject) && 
            this.selectedProject != null && model.getSize() != 0) {
            // projects don't match, and there's already packages in the list
            String[] error = {
                "Selected project " + project.getShortName(),
                "does not match the project of the package",
                "selected for addition to the model.",
                "Either clear all packages, or add a",
                "whole new project."
            };
            Component root = SwingUtilities.getRoot(this);
            JOptionPane.showMessageDialog(root, error, "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            // all is well... add the package to the list
            this.selectedProject = project;
            model.addElement(new UMLPackageDisplay(pack));
            getConfiguration().setCadsrProject(this.selectedProject);
            getConfiguration().addCadsrPackage(pack);
        }
    }
    
    
    private void removeSelectedPackages() {
        Object[] selection = getPackagesList().getSelectedValues();
        DefaultListModel model = (DefaultListModel) getPackagesList().getModel();
        for (Object removeme : selection) {
            model.removeElement(removeme);
            if (removeme instanceof UMLPackageDisplay) {
                UMLPackageDisplay display = (UMLPackageDisplay) removeme;
                getConfiguration().removeCadsrPackage(display.getPackage().getName());
            }
        }
    }
    
    
    private boolean projectsEqual(Project p1, Project p2) {
        if (p1 == p2) {
            return true;
        }
        if (p1 != null && p2 == null ||
            p1 == null && p2 != null) {
            return false;
        }
        return p1.getShortName().equals(p2.getShortName()) &&
            p1.getVersion().equals(p2.getVersion());
    }
    
    
    // ----------
    // validation
    // ----------
    
    
    private void configureValidation() {
        ValidationComponentUtils.setMessageKey(getPackagesList(), KEY_PACKAGES_LIST);
        
        validateInput();
        updateComponentTreeSeverity();
    }
    
    
    private void validateInput() {
        ValidationResult result = new ValidationResult();
        
        if (getPackagesList().getModel().getSize() == 0) {
            result.add(new SimpleValidationMessage(
                KEY_PACKAGES_LIST + " cannot be empty", Severity.ERROR, KEY_PACKAGES_LIST));
        }
        
        validationModel.setResult(result);
        
        setModelValidity(!result.hasErrors());
        
        updateComponentTreeSeverity();
    }
    
    
    private void updateComponentTreeSeverity() {
        ValidationComponentUtils.updateComponentTreeMandatoryAndBlankBackground(this);
        ValidationComponentUtils.updateComponentTreeSeverityBackground(this, validationModel.getResult());
    }
    
    
    /**
     * InternalCaDSRBrowserPanel
     * Extends the caDSR Browser Panel component to get packages
     * available in a given project
     * 
     * @author David
     */
    private static class InternalCaDSRBrowserPanel extends CaDSRBrowserPanel {
        
        public InternalCaDSRBrowserPanel() {
            super();
        }
        
        
        public InternalCaDSRBrowserPanel(boolean showQueryPanel, boolean showClassSelection) {
            super(showQueryPanel, showClassSelection);
        }
        
        
        public UMLPackageMetadata[] getAvailablePackages() {
            int count = getPackageComboBox().getItemCount();
            List<UMLPackageMetadata> packs = new ArrayList<UMLPackageMetadata>();
            for (int i = 0; i < count; i++) {
                Object o = getPackageComboBox().getItemAt(i);
                if (o instanceof PackageDisplay) {
                    packs.add(((PackageDisplay) o).getPackage());
                }
            }
            UMLPackageMetadata[] packArray = new UMLPackageMetadata[packs.size()];
            packs.toArray(packArray);
            return packArray;
        }
    }
    
    
    /**
     * UMLPackageDisplay
     * Wraps up a UMLPackageMetadata instance for
     * easy display in a JList
     * 
     * @author David
     */
    private static class UMLPackageDisplay {
        private UMLPackageMetadata pack = null;
        
        
        public UMLPackageDisplay(UMLPackageMetadata pack) {
            this.pack = pack;
        }
        
        
        public UMLPackageMetadata getPackage() {
            return pack;
        }
        
        
        public String toString() {
            return pack.getName();
        }
    }
}
