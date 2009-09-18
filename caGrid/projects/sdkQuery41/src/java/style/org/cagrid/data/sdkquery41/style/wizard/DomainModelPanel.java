package org.cagrid.data.sdkquery41.style.wizard;

import gov.nih.nci.cagrid.data.ui.wizard.AbstractWizardPanel;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.cagrid.data.sdkquery41.style.wizard.config.DomainModelConfigurationStep;
import org.cagrid.data.sdkquery41.style.wizard.config.DomainModelConfigurationStep.DomainModelConfigurationSource;
import org.cagrid.data.sdkquery41.style.wizard.model.ModelFromCaDSRPanel;
import org.cagrid.data.sdkquery41.style.wizard.model.ModelFromConfigPanel;
import org.cagrid.data.sdkquery41.style.wizard.model.ModelFromFileSystemPanel;
import org.cagrid.grape.utils.CompositeErrorDialog;

/**
 * DomainModelPanel
 * Wizard panel to allow the service developer to select and view the
 * domain model which will be used by the grid data service.
 * 
 * @author David
 */
public class DomainModelPanel extends AbstractWizardPanel {
    
    private JPanel modelSelectionPanel = null;
    private JPanel mainPanel = null;
    private JLabel modelSourceLabel = null;
    private JComboBox modelSourceComboBox = null;
    
    private SortedMap<String, DomainModelSourcePanel> domainModelSources = null;
    
    private DomainModelConfigurationStep configuration = null;

    public DomainModelPanel(ServiceExtensionDescriptionType extensionDescription, ServiceInformation info) {
        super(extensionDescription, info);
        domainModelSources = new TreeMap<String, DomainModelSourcePanel>();
        configuration = new DomainModelConfigurationStep(info);
        initialize();
    }


    public String getPanelShortName() {
        return "Domain Model";
    }


    public String getPanelTitle() {
        return "Domain Model selection";
    }


    public void update() {
        // update the domain model source panels
        for (DomainModelSourcePanel panel : domainModelSources.values()) {
            panel.populateFromConfiguration();
        }
    }
    
    
    public void movingNext() {
        String selectedSourceName = (String) getModelSourceComboBox().getSelectedItem();
        DomainModelSourcePanel selectedSource = domainModelSources.get(selectedSourceName);
        DomainModelConfigurationSource sourceType = selectedSource.getSourceType();
        configuration.setModelSource(sourceType);
        try {
            configuration.applyConfiguration();
        } catch (Exception ex) {
            ex.printStackTrace();
            CompositeErrorDialog.showErrorDialog("Error obtaining domain model information", ex.getMessage(), ex);
        }
    }
    
    
    private void initialize() {
        populateModelPanels();
        setLayout(new GridLayout());
        add(getMainPanel());
    }
    
    
    private void populateModelPanels() {
        DomainModelSourceValidityListener validityListener = new DomainModelSourceValidityListener() {
            public void domainModelSourceValid(DomainModelSourcePanel source, boolean valid) {
                String selectedSourceName = (String) getModelSourceComboBox().getSelectedItem();
                DomainModelSourcePanel selectedSource = domainModelSources.get(selectedSourceName);
                if (selectedSource == source) {
                    setNextEnabled(valid);
                }
            }
        };
        
        DomainModelSourcePanel cadsrSourcePanel = new ModelFromCaDSRPanel(validityListener, configuration);
        DomainModelSourcePanel configSourcePanel = new ModelFromConfigPanel(validityListener, configuration);
        DomainModelSourcePanel fileSourcePanel = new ModelFromFileSystemPanel(validityListener, configuration);
        domainModelSources.put(cadsrSourcePanel.getName(), cadsrSourcePanel);
        domainModelSources.put(configSourcePanel.getName(), configSourcePanel);
        domainModelSources.put(fileSourcePanel.getName(), fileSourcePanel);
        
        // add the model source panels to the combo box and display panel
        for (String name : domainModelSources.keySet()) {
            getModelSourceComboBox().addItem(name);
            DomainModelSourcePanel sourcePanel = domainModelSources.get(name);
            getModelSelectionPanel().add(sourcePanel, name);
        }
    }
    
    
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.fill = GridBagConstraints.BOTH;
            gridBagConstraints2.gridwidth = 2;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.weightx = 1.0D;
            gridBagConstraints2.weighty = 1.0D;
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
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.add(getModelSourceLabel(), gridBagConstraints);
            mainPanel.add(getModelSourceComboBox(), gridBagConstraints1);
            mainPanel.add(getModelSelectionPanel(), gridBagConstraints2);
        }
        return mainPanel;
    }
    
    
    private JPanel getModelSelectionPanel() {
        if (modelSelectionPanel == null) {
            modelSelectionPanel = new JPanel();
            modelSelectionPanel.setLayout(new CardLayout());
            modelSelectionPanel.setBorder(BorderFactory.createTitledBorder(
                null, "Domain Model Source", TitledBorder.DEFAULT_JUSTIFICATION, 
                TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
        }
        return modelSelectionPanel;
    }


    /**
     * This method initializes modelSourceLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getModelSourceLabel() {
        if (modelSourceLabel == null) {
            modelSourceLabel = new JLabel();
            modelSourceLabel.setText("Domain Model Source:");
        }
        return modelSourceLabel;
    }


    /**
     * This method initializes modelSourceComboBox	
     * 	
     * @return javax.swing.JComboBox	
     */
    private JComboBox getModelSourceComboBox() {
        if (modelSourceComboBox == null) {
            modelSourceComboBox = new JComboBox();
            modelSourceComboBox.setToolTipText("Select the domain model source");
            modelSourceComboBox.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    String sourceName = getModelSourceComboBox().getSelectedItem().toString();
                    ((CardLayout) getModelSelectionPanel().getLayout()).show(
                        getModelSelectionPanel(), 
                        getModelSourceComboBox().getSelectedItem().toString());
                    domainModelSources.get(sourceName).revalidateModel();
                }
            });
        }
        return modelSourceComboBox;
    }
}
