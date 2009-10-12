package gov.nih.nci.cagrid.data.ui.creation;

import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;
import gov.nih.nci.cagrid.data.ExtensionDataUtils;
import gov.nih.nci.cagrid.data.extension.Data;
import gov.nih.nci.cagrid.data.extension.ServiceFeatures;
import gov.nih.nci.cagrid.data.extension.ServiceStyle;
import gov.nih.nci.cagrid.data.style.ServiceStyleContainer;
import gov.nih.nci.cagrid.data.style.ServiceStyleLoader;
import gov.nih.nci.cagrid.data.ui.StyleUiLoader;
import gov.nih.nci.cagrid.data.ui.wizard.AbstractWizardPanel;
import gov.nih.nci.cagrid.data.ui.wizard.ServiceWizard;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionTypeExtensionData;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;
import gov.nih.nci.cagrid.introduce.portal.extension.CreationExtensionUIDialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import org.cagrid.grape.GridApplication;
import org.cagrid.grape.utils.CompositeErrorDialog;
import java.awt.GridLayout;
import javax.swing.SwingConstants;


/**
 * DataServiceCreationDialog 
 * Dialog for post-creation changes to a data service
 * and configuration of data service features
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @created Aug 1, 2006
 * @version $Id: DataServiceCreationDialog.java,v 1.10 2009-04-30 16:16:25 dervin Exp $
 */
public class DataServiceCreationDialog extends CreationExtensionUIDialog {
    // default service style is "None / Custom Data Source"
    public static final String DEFAULT_SERVICE_STYLE = "Custom Data Source (Default)";
    
    // extension names needed for additional features
    public static final String WS_ENUM_EXTENSION_NAME = "cagrid_wsEnum";
    public static final String TRANSFER_EXTENSION_NAME = "caGrid_Transfer";

    private JPanel mainPanel = null;
    private JCheckBox wsEnumCheckBox = null;
    private JCheckBox gridIdentCheckBox = null;
    private JCheckBox transferCheckBox = null;
    private JButton okButton = null;
    private JPanel featuresPanel = null;
    private JPanel stylePanel = null;
    private JLabel styleNameLabel = null;
    private JComboBox styleComboBox = null;
    private JTextArea styleDescriptionTextArea = null;
    private JScrollPane styleDescriptionScrollPane = null;
    

    public DataServiceCreationDialog(Frame f, ServiceExtensionDescriptionType desc, ServiceInformation info) {
        super(f, desc, info);
        // keep users from closing the window unexpectedly
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setFeatureStatus();
            }
        });
        initialize();
    }


    private void initialize() {
        this.setTitle("Data Service Configuration");
        this.setContentPane(getMainPanel());
        pack();
        this.setSize(new Dimension(500, 300));
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainPanel() {
        if (this.mainPanel == null) {
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.gridy = 1;
            gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.fill = GridBagConstraints.BOTH;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.weightx = 1.0D;
            gridBagConstraints1.weighty = 1.0D;
            gridBagConstraints1.gridy = 0;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.insets = new java.awt.Insets(4, 4, 4, 4);
            gridBagConstraints5.anchor = java.awt.GridBagConstraints.EAST;
            gridBagConstraints5.gridwidth = 2;
            gridBagConstraints5.gridy = 2;
            this.mainPanel = new JPanel();
            this.mainPanel.setLayout(new GridBagLayout());
            mainPanel.add(getOkButton(), gridBagConstraints5);
            mainPanel.add(getStylePanel(), gridBagConstraints1);
            mainPanel.add(getFeaturesPanel(), gridBagConstraints4);
        }
        return this.mainPanel;
    }


    /**
     * This method initializes jCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getWsEnumCheckBox() {
        if (this.wsEnumCheckBox == null) {
            this.wsEnumCheckBox = new JCheckBox();
            this.wsEnumCheckBox.setText("WS-Enumeration");
            // can only use ws-enumeration if the extension has been installed
            boolean wsEnumInstalled = wsEnumExtensionInstalled();
            this.wsEnumCheckBox.setEnabled(wsEnumInstalled);
            wsEnumCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
            if (!wsEnumInstalled) {
                wsEnumCheckBox.setToolTipText(
                    "The caGrid WS-Enumeration service extension is not installed");
            }
        }
        return this.wsEnumCheckBox;
    }
    
    
    /**
     * This method initializes transferCheckBox 
     *  
     * @return javax.swing.JCheckBox    
     */
    private JCheckBox getTransferCheckBox() {
        if (transferCheckBox == null) {
            transferCheckBox = new JCheckBox();
            transferCheckBox.setText("caGrid Transfer");
            transferCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
            // can only enable if Transfer has been installed
            boolean transferInstalled = transferExtensionInstalled();
            transferCheckBox.setEnabled(transferInstalled);
            if (!transferInstalled) {
                transferCheckBox.setToolTipText("The Transfer service extension is not installed");
            }
        }
        return transferCheckBox;
    }


    /**
     * This method initializes jCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getGridIdentCheckBox() {
        if (this.gridIdentCheckBox == null) {
            this.gridIdentCheckBox = new JCheckBox();
            this.gridIdentCheckBox.setEnabled(false);
            this.gridIdentCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
            this.gridIdentCheckBox.setText("Grid Identifier");
        }
        return this.gridIdentCheckBox;
    }


    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getOkButton() {
        if (this.okButton == null) {
            this.okButton = new JButton();
            this.okButton.setText("OK");
            this.okButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    // enable / disable features the developer has selected
                    setFeatureStatus();
                    // see what (if any) style has been selected
                    if (getStyleComboBox().getSelectedItem() instanceof ServiceStyleContainer) {
                        // stop the user from hitting OK again
                        getOkButton().setEnabled(false);
                        ServiceStyleContainer container = (ServiceStyleContainer) 
                            getStyleComboBox().getSelectedItem();
                        List<AbstractWizardPanel> stylePanels = null;
                        try {
                            stylePanels = StyleUiLoader.loadWizardPanels(
                                container, getExtensionDescription(), getServiceInfo());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            CompositeErrorDialog.showErrorDialog("Error loading wizard panels for service style " 
                                + container.getServiceStyle().getName(), ex.getMessage(), ex);
                        }
                        
                        if (stylePanels != null && stylePanels.size() != 0) {
                            System.out.println("Found " + stylePanels.size() + " panels");
                            // create the style's wizard
                            ServiceWizard wiz = new ServiceWizard(
                                GridApplication.getContext().getApplication(),
                                container.getServiceStyle().getName() + " Style");
                            for (AbstractWizardPanel panel : stylePanels) {
                                System.out.println("Adding panel " + panel.getPanelShortName());
                                wiz.addWizardPanel(panel);
                            }
                            wiz.setVisible(true);
                        }
                    }
                    dispose();
                }
            });
        }
        return this.okButton;
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getFeaturesPanel() {
        if (this.featuresPanel == null) {
            GridLayout gridLayout = new GridLayout();
            gridLayout.setRows(1);
            gridLayout.setColumns(3);
            this.featuresPanel = new JPanel();
            featuresPanel.setLayout(gridLayout);
            this.featuresPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                null, "Optional Features",javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, PortalLookAndFeel.getPanelLabelColor()));
            featuresPanel.add(getWsEnumCheckBox());
            featuresPanel.add(getTransferCheckBox());
            featuresPanel.add(getGridIdentCheckBox());
        }
        return this.featuresPanel;
    }


    private void setFeatureStatus() {
        // set the selected service features
        ExtensionTypeExtensionData data = getExtensionTypeExtensionData();
        ServiceFeatures features = new ServiceFeatures();
        try {
            features.setUseGridIdentifiers(getGridIdentCheckBox().isSelected());
            features.setUseWsEnumeration(getWsEnumCheckBox().isSelected());
            features.setUseTransfer(getTransferCheckBox().isSelected());
            
            // service style
            if (getStyleComboBox().getSelectedItem() != DEFAULT_SERVICE_STYLE) {
                ServiceStyleContainer styleContainer = (ServiceStyleContainer) getStyleComboBox().getSelectedItem();
                ServiceStyle selectedStyle = new ServiceStyle();
                selectedStyle.setName(styleContainer.getServiceStyle().getName());
                selectedStyle.setVersion(styleContainer.getServiceStyle().getVersion());
                features.setServiceStyle(selectedStyle);
            } else if (features.getServiceStyle() != null) {
                features.setServiceStyle(null);
            }

            Data extData = ExtensionDataUtils.getExtensionData(data);
            extData.setServiceFeatures(features);
            ExtensionDataUtils.storeExtensionData(data, extData);
        } catch (Exception ex) {
            ex.printStackTrace();
            CompositeErrorDialog.showErrorDialog("Error storing configuration: " + ex.getMessage(), ex);
        }
    }


    private boolean wsEnumExtensionInstalled() {
        List<?> extensionDescriptors = ExtensionsLoader.getInstance().getServiceExtensions();
        for (int i = 0; i < extensionDescriptors.size(); i++) {
            ServiceExtensionDescriptionType ex = (ServiceExtensionDescriptionType) extensionDescriptors.get(i);
            if (ex.getName().equals(WS_ENUM_EXTENSION_NAME)) {
                return true;
            }
        }
        return false;
    }
    
    
    private boolean transferExtensionInstalled() {
        List<?> extensionDescriptors = ExtensionsLoader.getInstance().getServiceExtensions();
        for (int i = 0; i < extensionDescriptors.size(); i++) {
            ServiceExtensionDescriptionType desc = (ServiceExtensionDescriptionType) extensionDescriptors.get(i);
            if (desc.getName().equals(TRANSFER_EXTENSION_NAME)) {
                return true;
            }
        }
        return false;
    }


    /**
     * This method initializes stylePanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getStylePanel() {
        if (stylePanel == null) {
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.fill = GridBagConstraints.BOTH;
            gridBagConstraints8.gridy = 1;
            gridBagConstraints8.weightx = 1.0;
            gridBagConstraints8.weighty = 1.0;
            gridBagConstraints8.gridwidth = 2;
            gridBagConstraints8.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints8.gridx = 0;
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints7.gridy = 0;
            gridBagConstraints7.weightx = 1.0;
            gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints7.gridx = 1;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.gridx = 0;
            gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints6.gridy = 0;
            stylePanel = new JPanel();
            stylePanel.setLayout(new GridBagLayout());
            stylePanel.setBorder(BorderFactory.createTitledBorder(
                null, "Service Style", TitledBorder.DEFAULT_JUSTIFICATION, 
                TitledBorder.DEFAULT_POSITION, null, PortalLookAndFeel.getPanelLabelColor()));
            stylePanel.add(getStyleNameLabel(), gridBagConstraints6);
            stylePanel.add(getStyleComboBox(), gridBagConstraints7);
            stylePanel.add(getStyleDescriptionScrollPane(), gridBagConstraints8);
        }
        return stylePanel;
    }


    /**
     * This method initializes styleNameLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getStyleNameLabel() {
        if (styleNameLabel == null) {
            styleNameLabel = new JLabel();
            styleNameLabel.setText("Style:");
        }
        return styleNameLabel;
    }


    /**
     * This method initializes styleComboBox	
     * 	
     * @return javax.swing.JComboBox	
     */
    private JComboBox getStyleComboBox() {
        if (styleComboBox == null) {
            styleComboBox = new JComboBox();
            // extend the default renderer to show the style names
            styleComboBox.setRenderer(new DefaultListCellRenderer() {
                public Component getListCellRendererComponent(
                    JList list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof ServiceStyleContainer) {
                        setText(((ServiceStyleContainer) value).getServiceStyle().getName());
                    }
                    return this;
                }
            });
            // populate the style combo box
            try {
                styleComboBox.addItem(DEFAULT_SERVICE_STYLE);
                List<ServiceStyleContainer> styles = ServiceStyleLoader.getAvailableStyles();
                // sort styles by name
                Collections.sort(styles, new Comparator<ServiceStyleContainer>() {
                    public int compare(ServiceStyleContainer o1, ServiceStyleContainer o2) {
                        String name1 = o1.getServiceStyle().getName().toLowerCase();
                        String name2 = o2.getServiceStyle().getName().toLowerCase();
                        return name1.compareTo(name2);
                    }
                });
                for (ServiceStyleContainer container : styles) {
                    styleComboBox.addItem(container);
                }
                styleComboBox.setSelectedItem(DEFAULT_SERVICE_STYLE);
            } catch (Exception ex) {
                ex.printStackTrace();
                CompositeErrorDialog.showErrorDialog(
                    "Error loading data service styles", ex.getMessage(), ex);
            }
            // add change listener to know when a new item is selected and update
            // the description text field as required
            styleComboBox.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        String description = null;
                        if (e.getItem() instanceof ServiceStyleContainer) {
                            ServiceStyleContainer container = (ServiceStyleContainer) e.getItem();
                            description = container.getServiceStyle().getStyleDescription();
                        }
                        getStyleDescriptionTextArea().setText(description);
                        getStyleDescriptionTextArea().setCaretPosition(0);
                    }
                }
            });
        }
        return styleComboBox;
    }


    /**
     * This method initializes styleDescriptionTextArea	
     * 	
     * @return javax.swing.JTextArea	
     */
    private JTextArea getStyleDescriptionTextArea() {
        if (styleDescriptionTextArea == null) {
            styleDescriptionTextArea = new JTextArea();
            styleDescriptionTextArea.setLineWrap(true);
            styleDescriptionTextArea.setWrapStyleWord(true);
            styleDescriptionTextArea.setEditable(false);
        }
        return styleDescriptionTextArea;
    }


    /**
     * This method initializes styleDescriptionScrollPane	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getStyleDescriptionScrollPane() {
        if (styleDescriptionScrollPane == null) {
            styleDescriptionScrollPane = new JScrollPane();
            styleDescriptionScrollPane.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            styleDescriptionScrollPane.setBorder(BorderFactory.createTitledBorder(
                null, "Description", TitledBorder.DEFAULT_JUSTIFICATION, 
                TitledBorder.DEFAULT_POSITION, null, PortalLookAndFeel.getPanelLabelColor()));
            styleDescriptionScrollPane.setViewportView(getStyleDescriptionTextArea());
        }
        return styleDescriptionScrollPane;
    }
}
