package gov.nih.nci.cagrid.validator.builder;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;
import gov.nih.nci.cagrid.introduce.portal.common.IntroduceLookAndFeel;
import gov.nih.nci.cagrid.tests.core.beans.validation.ServiceTestStep;
import gov.nih.nci.cagrid.tests.core.beans.validation.ServiceTestStepConfigurationProperty;
import gov.nih.nci.cagrid.tests.core.beans.validation.ServiceType;
import gov.nih.nci.cagrid.validator.steps.AbstractBaseServiceTestStep;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.cagrid.grape.LookAndFeel;

/** 
 *  ServiceTypePanel
 *  Panel to display / manage service types and their tests
 * 
 * @author David Ervin
 * 
 * @created Sep 5, 2007 12:25:19 PM
 * @version $Id: ServiceTypePanel.java,v 1.2 2008-03-25 18:55:07 dervin Exp $ 
 */
public class ServiceTypePanel extends JPanel {
    public static final String JAVA_CLASS_PATH = "java.class.path";
    public static final String SUN_BOOT_CLASS_PATH = "sun.boot.class.path";
    public static final String JAR_EXTENSION = ".jar";
    public static final String CLASS_EXTSNSION = ".class";
    public static final String CLASS_FIELDS_PREFIX = "class$";

    private JList typesList = null;
    private JList stepsList = null;
    private JScrollPane typesScrollPane = null;
    private JScrollPane stepsScrollPane = null;
    private JButton addTypeButton = null;
    private JTextField typeNameTextField = null;
    private JLabel typeNameLabel = null;
    private JButton removeTypeButton = null;
    private JPanel typeInputPanel = null;
    private JPanel typeButtonsPanel = null;
    private JLabel stepClassnameLabel = null;
    private JComboBox stepClassnameComboBox = null;
    private JButton discoverStepsButton = null;
    private JButton addStepButton = null;
    private JButton removeStepButton = null;
    private JButton moveStepUpButton = null;
    private JButton moveStepDownButton = null;
    private JPanel stepInputPanel = null;
    private JPanel stepButtonsPanel = null;
    private JPanel stepOrderButtonPanel = null;
    private JPanel stepsPanel = null;
    private JPanel listContainerPanel = null;
    private JPanel inputContainerPanel = null;
    private JButton configureStepButton = null;


    public ServiceTypePanel() {
        super();
        initialize();
    }
    
    
    private void initialize() {
        GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
        gridBagConstraints9.gridx = 0;
        gridBagConstraints9.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints9.gridy = 1;
        GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
        gridBagConstraints8.gridx = 0;
        gridBagConstraints8.fill = GridBagConstraints.BOTH;
        gridBagConstraints8.weightx = 1.0D;
        gridBagConstraints8.weighty = 1.0D;
        gridBagConstraints8.gridy = 0;
        this.setLayout(new GridBagLayout());
        this.setSize(new Dimension(695, 265));
        this.add(getListContainerPanel(), gridBagConstraints8);
        this.add(getInputContainerPanel(), gridBagConstraints9);
        
    }
    
    
    public ServiceType[] getServiceTypes() {
        ServiceType[] types = new ServiceType[getTypesList().getModel().getSize()];
        for (int i = 0; i < getTypesList().getModel().getSize(); i++) {
            types[i] = (ServiceType) getTypesList().getModel().getElementAt(i);
        }
        return types;
    }
    
    
    public void setServiceTypes(ServiceType[] types) {
        DefaultListModel model = (DefaultListModel) getTypesList().getModel();
        model.removeAllElements();
        for (ServiceType st : types) {
            model.addElement(st);
        }
        ((DefaultListModel) getStepsList().getModel()).removeAllElements();
    }


    /**
     * This method initializes typesList	
     * 	
     * @return javax.swing.JList	
     */
    private JList getTypesList() {
        if (typesList == null) {
            typesList = new JList();
            typesList.setModel(new DefaultListModel());
            typesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            // set up a renderer to allow storing ServiceTypes directly in the JList
            typesList.setCellRenderer(new DefaultListCellRenderer() {
                public Component getListCellRendererComponent(
                    JList list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof ServiceType) {
                        setText(((ServiceType) value).getTypeName());
                    }
                    return this;
                }
            });
            // selection listener updates the steps list
            typesList.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    ServiceType selected = (ServiceType) getTypesList().getSelectedValue();
                    if (selected != null && selected.getTestStep() != null) {
                        getStepsList().setListData(selected.getTestStep());
                    } else {
                        ((DefaultListModel) getStepsList().getModel()).removeAllElements();
                    }
                }
            });
        }
        return typesList;
    }


    /**
     * This method initializes stepsList	
     * 	
     * @return javax.swing.JList	
     */
    private JList getStepsList() {
        if (stepsList == null) {
            stepsList = new JList();
            stepsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            // set up a renderer to allow storing TestSteps directly in the JList
            stepsList.setCellRenderer(new DefaultListCellRenderer() {
                public Component getListCellRendererComponent(
                    JList list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof ServiceTestStep) {
                        setText(((ServiceTestStep) value).getClassname());
                    }
                    return this;
                }
            });
            stepsList.setModel(new DefaultListModel());
        }
        return stepsList;
    }


    /**
     * This method initializes typesScrollPane	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getTypesScrollPane() {
        if (typesScrollPane == null) {
            typesScrollPane = new JScrollPane();
            typesScrollPane.setBorder(BorderFactory.createTitledBorder(
                null, "Service Types", TitledBorder.DEFAULT_JUSTIFICATION, 
                TitledBorder.DEFAULT_POSITION, null, PortalLookAndFeel.getPanelLabelColor()));
            typesScrollPane.setViewportView(getTypesList());
        }
        return typesScrollPane;
    }


    /**
     * This method initializes stepsScrollPane	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getStepsScrollPane() {
        if (stepsScrollPane == null) {
            stepsScrollPane = new JScrollPane();
            stepsScrollPane.setBorder(BorderFactory.createTitledBorder(
                null, "Test Steps", TitledBorder.DEFAULT_JUSTIFICATION, 
                TitledBorder.DEFAULT_POSITION, null, PortalLookAndFeel.getPanelLabelColor()));
            stepsScrollPane.setViewportView(getStepsList());
        }
        return stepsScrollPane;
    }


    /**
     * This method initializes addTypeButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getAddTypeButton() {
        if (addTypeButton == null) {
            addTypeButton = new JButton();
            addTypeButton.setText("Add Type");
            addTypeButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    String typeName = getTypeNameTextField().getText().trim();
                    // validate the type name
                    if (typeName.length() == 0) {
                        JOptionPane.showMessageDialog(ServiceTypePanel.this, "Please specify a type name");
                    } else {
                        boolean nameOk = true;
                        for (int i = 0; nameOk && i < getTypesList().getModel().getSize(); i++) {
                            ServiceType type = (ServiceType) getTypesList().getModel().getElementAt(i);
                            if (type.getTypeName().equals(typeName)) {
                                JOptionPane.showMessageDialog(
                                    ServiceTypePanel.this, "The type name " + typeName + " is already in use.");
                                nameOk = false;
                            }
                        }
                        if (nameOk) {
                            ServiceType newType = new ServiceType();
                            newType.setTypeName(typeName);
                            ((DefaultListModel) getTypesList().getModel()).addElement(newType);
                        }
                    }
                }
            });
        }
        return addTypeButton;
    }


    /**
     * This method initializes typeNameTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getTypeNameTextField() {
        if (typeNameTextField == null) {
            typeNameTextField = new JTextField();
        }
        return typeNameTextField;
    }


    /**
     * This method initializes typeNameLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getTypeNameLabel() {
        if (typeNameLabel == null) {
            typeNameLabel = new JLabel();
            typeNameLabel.setText("Type Name:");
        }
        return typeNameLabel;
    }


    /**
     * This method initializes removeTypeButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getRemoveTypeButton() {
        if (removeTypeButton == null) {
            removeTypeButton = new JButton();
            removeTypeButton.setText("Remove Type");
            removeTypeButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    int selection = getTypesList().getSelectedIndex();
                    if (selection == -1) {
                        JOptionPane.showMessageDialog(
                            ServiceTypePanel.this, "Please select a service type to remove");
                    } else {
                        ((DefaultListModel) getTypesList().getModel()).removeElementAt(selection);
                    }
                }
            });
        }
        return removeTypeButton;
    }


    /**
     * This method initializes typeInputPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getTypeInputPanel() {
        if (typeInputPanel == null) {
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridx = 1;
            gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints11.anchor = GridBagConstraints.EAST;
            gridBagConstraints11.gridy = 1;
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
            typeInputPanel = new JPanel();
            typeInputPanel.setLayout(new GridBagLayout());
            typeInputPanel.add(getTypeNameLabel(), gridBagConstraints);
            typeInputPanel.add(getTypeNameTextField(), gridBagConstraints1);
            typeInputPanel.add(getTypeButtonsPanel(), gridBagConstraints11);
        }
        return typeInputPanel;
    }


    /**
     * This method initializes typeButtonsPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getTypeButtonsPanel() {
        if (typeButtonsPanel == null) {
            GridLayout gridLayout = new GridLayout();
            gridLayout.setRows(1);
            gridLayout.setHgap(4);
            gridLayout.setColumns(2);
            typeButtonsPanel = new JPanel();
            typeButtonsPanel.setLayout(gridLayout);
            typeButtonsPanel.add(getAddTypeButton(), null);
            typeButtonsPanel.add(getRemoveTypeButton(), null);
        }
        return typeButtonsPanel;
    }


    /**
     * This method initializes stepClassnameLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getStepClassnameLabel() {
        if (stepClassnameLabel == null) {
            stepClassnameLabel = new JLabel();
            stepClassnameLabel.setText("Step Classname:");
        }
        return stepClassnameLabel;
    }


    /**
     * This method initializes stepClassnameComboBox	
     * 	
     * @return javax.swing.JComboBox	
     */
    private JComboBox getStepClassnameComboBox() {
        if (stepClassnameComboBox == null) {
            stepClassnameComboBox = new JComboBox();
        }
        return stepClassnameComboBox;
    }


    /**
     * This method initializes discoverStepsButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getDiscoverStepsButton() {
        if (discoverStepsButton == null) {
            discoverStepsButton = new JButton();
            discoverStepsButton.setText("Discover...");
            discoverStepsButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    discoverStepClasses();
                }
            });
        }
        return discoverStepsButton;
    }


    /**
     * This method initializes addStepButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getAddStepButton() {
        if (addStepButton == null) {
            addStepButton = new JButton();
            addStepButton.setText("Add Step");
            addStepButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (getStepClassnameComboBox().getSelectedItem() == null) {
                        JOptionPane.showMessageDialog(ServiceTypePanel.this, "Please select a step");
                    } else if (getTypesList().getSelectedValue() == null) {
                        JOptionPane.showMessageDialog(ServiceTypePanel.this, "Please select a service type to add test steps to");
                    } else {
                        String classname = String.valueOf(getStepClassnameComboBox().getSelectedItem());
                        ServiceTestStep step = new ServiceTestStep();
                        step.setClassname(classname);
                        ServiceType type = (ServiceType) getTypesList().getSelectedValue();
                        ServiceTestStep[] steps = null;
                        if (type.getTestStep() != null) {
                            steps = (ServiceTestStep[]) Utils.appendToArray(
                                type.getTestStep(), step);
                        } else {
                            steps = new ServiceTestStep[] {step};
                        }
                        type.setTestStep(steps);
                        getStepsList().setListData(steps);
                    }
                }
            });
        }
        return addStepButton;
    }


    /**
     * This method initializes removeStepButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getRemoveStepButton() {
        if (removeStepButton == null) {
            removeStepButton = new JButton();
            removeStepButton.setText("Remove Step");
            removeStepButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (getStepsList().getSelectedValue() == null) {
                        JOptionPane.showMessageDialog(ServiceTypePanel.this, "Please select a step to remove");
                    } else {
                        String stepClassname = ((ServiceTestStep) getStepsList().getSelectedValue()).getClassname();
                        ServiceType selectedType = (ServiceType) getTypesList().getSelectedValue();
                        ServiceTestStep removeStep = null;
                        for (ServiceTestStep step : selectedType.getTestStep()) {
                            if (step.getClassname().equals(stepClassname)) {
                                removeStep = step;
                                break;
                            }
                        }
                        ServiceTestStep[] cleanedSteps = (ServiceTestStep[]) 
                            Utils.removeFromArray(selectedType.getTestStep(), removeStep);
                        selectedType.setTestStep(cleanedSteps);
                        getStepsList().setListData(cleanedSteps);
                    }
                }
            });
        }
        return removeStepButton;
    }


    /**
     * This method initializes moveStepUpButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getMoveStepUpButton() {
        if (moveStepUpButton == null) {
            moveStepUpButton = new JButton();
            moveStepUpButton.setIcon(getUpIcon());
            // moveStepUpButton.setText("Up");
            moveStepUpButton.setToolTipText("Move selected step up");
            moveStepUpButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    int selected = getStepsList().getSelectedIndex();
                    if (selected > 0) { // avoids non-selection and top of list
                        // swap the selected item with the one "above" it
                        ServiceType service = (ServiceType) getTypesList().getSelectedValue();
                        ServiceTestStep previousStep = service.getTestStep(selected - 1);
                        ServiceTestStep selectedStep = service.getTestStep(selected);
                        service.setTestStep(selected - 1, selectedStep);
                        service.setTestStep(selected, previousStep);
                        // update the list UI
                        getStepsList().setListData(service.getTestStep());
                    }
                }
            });
        }
        return moveStepUpButton;
    }


    /**
     * This method initializes moveStepDownButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getMoveStepDownButton() {
        if (moveStepDownButton == null) {
            moveStepDownButton = new JButton();
            moveStepDownButton.setIcon(getDownIcon());
            // moveStepDownButton.setText("Down");
            moveStepDownButton.setToolTipText("Move selected step down");
            moveStepDownButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    int selected = getStepsList().getSelectedIndex();
                    if (selected != -1 && selected < getStepsList().getModel().getSize() - 1) {
                        // swap the selected item with the one "below" it
                        ServiceType service = (ServiceType) getTypesList().getSelectedValue();
                        ServiceTestStep nextStep = service.getTestStep(selected + 1);
                        ServiceTestStep selectedStep = service.getTestStep(selected);
                        service.setTestStep(selected + 1, selectedStep);
                        service.setTestStep(selected, nextStep);
                        // update the UI
                        getStepsList().setListData(service.getTestStep());
                    }
                }
            });
        }
        return moveStepDownButton;
    }


    /**
     * This method initializes stepInputPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getStepInputPanel() {
        if (stepInputPanel == null) {
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 1;
            gridBagConstraints5.gridwidth = 2;
            gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints5.anchor = GridBagConstraints.EAST;
            gridBagConstraints5.gridy = 1;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 2;
            gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.gridy = 0;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridy = 0;
            gridBagConstraints3.weightx = 1.0;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.gridx = 1;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.gridy = 0;
            stepInputPanel = new JPanel();
            stepInputPanel.setLayout(new GridBagLayout());
            stepInputPanel.add(getStepClassnameLabel(), gridBagConstraints2);
            stepInputPanel.add(getStepClassnameComboBox(), gridBagConstraints3);
            stepInputPanel.add(getDiscoverStepsButton(), gridBagConstraints4);
            stepInputPanel.add(getStepButtonsPanel(), gridBagConstraints5);
        }
        return stepInputPanel;
    }


    /**
     * This method initializes stepButtonsPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getStepButtonsPanel() {
        if (stepButtonsPanel == null) {
            GridLayout gridLayout1 = new GridLayout();
            gridLayout1.setRows(1);
            gridLayout1.setColumns(2);
            gridLayout1.setHgap(4);
            stepButtonsPanel = new JPanel();
            stepButtonsPanel.setLayout(gridLayout1);
            stepButtonsPanel.add(getAddStepButton(), null);
            stepButtonsPanel.add(getRemoveStepButton(), null);
        }
        return stepButtonsPanel;
    }


    /**
     * This method initializes stepOrderButtonPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getStepOrderButtonPanel() {
        if (stepOrderButtonPanel == null) {
            GridLayout gridLayout2 = new GridLayout();
            gridLayout2.setRows(3);
            gridLayout2.setVgap(4);
            gridLayout2.setColumns(1);
            stepOrderButtonPanel = new JPanel();
            stepOrderButtonPanel.setLayout(gridLayout2);
            stepOrderButtonPanel.add(getMoveStepUpButton(), null);
            stepOrderButtonPanel.add(getConfigureStepButton(), null);
            stepOrderButtonPanel.add(getMoveStepDownButton(), null);
        }
        return stepOrderButtonPanel;
    }


    /**
     * This method initializes stepsPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getStepsPanel() {
        if (stepsPanel == null) {
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.gridx = 1;
            gridBagConstraints7.fill = GridBagConstraints.VERTICAL;
            gridBagConstraints7.gridy = 0;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.fill = GridBagConstraints.BOTH;
            gridBagConstraints6.gridy = 0;
            gridBagConstraints6.weightx = 1.0D;
            gridBagConstraints6.weighty = 1.0;
            gridBagConstraints6.gridx = 0;
            stepsPanel = new JPanel();
            stepsPanel.setLayout(new GridBagLayout());
            stepsPanel.add(getStepsScrollPane(), gridBagConstraints6);
            stepsPanel.add(getStepOrderButtonPanel(), gridBagConstraints7);
        }
        return stepsPanel;
    }


    /**
     * This method initializes listContainerPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getListContainerPanel() {
        if (listContainerPanel == null) {
            GridLayout gridLayout3 = new GridLayout();
            gridLayout3.setColumns(2);
            gridLayout3.setHgap(4);
            listContainerPanel = new JPanel();
            listContainerPanel.setLayout(gridLayout3);
            listContainerPanel.add(getTypesScrollPane(), null);
            listContainerPanel.add(getStepsPanel(), null);
        }
        return listContainerPanel;
    }


    /**
     * This method initializes inputContainerPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getInputContainerPanel() {
        if (inputContainerPanel == null) {
            GridLayout gridLayout4 = new GridLayout();
            gridLayout4.setRows(1);
            gridLayout4.setHgap(4);
            gridLayout4.setColumns(2);
            inputContainerPanel = new JPanel();
            inputContainerPanel.setLayout(gridLayout4);
            inputContainerPanel.add(getTypeInputPanel(), null);
            inputContainerPanel.add(getStepInputPanel(), null);
        }
        return inputContainerPanel;
    }
    
    
    /**
     * This method initializes configureStepButton  
     *  
     * @return javax.swing.JButton  
     */
    private JButton getConfigureStepButton() {
        if (configureStepButton == null) {
            configureStepButton = new JButton();
            configureStepButton.setIcon(getEditIcon());
            configureStepButton.setToolTipText("Configure Step");
            configureStepButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    ServiceTestStep selectedStep = (ServiceTestStep) getStepsList().getSelectedValue();
                    if (selectedStep == null) {
                        JOptionPane.showMessageDialog(ServiceTypePanel.this, "Please select a step");
                    } else {
                        configureStep(selectedStep);
                    }
                }
            });
        }
        return configureStepButton;
    }
    
    
    private void discoverStepClasses() {
        Thread discoverThread = new Thread() {
            public void run() {
                getDiscoverStepsButton().setEnabled(false);
                System.out.println("Starting discovery");
                Class baseClass = AbstractBaseServiceTestStep.class;
                List<String> subs = new ArrayList();
                String classPath = System.getProperty(JAVA_CLASS_PATH);
                String[] pathElements = classPath.split(File.pathSeparator);
                URL[] pathUrls = new URL[pathElements.length];
                try {
                    for (int i = 0; i < pathElements.length; i++) {
                        pathUrls[i] = new File(pathElements[i]).toURL();
                    }
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                    System.out.println("Error converting path to URL: " + ex.getMessage());
                }
                StringTokenizer pathTokenizer 
                    = new StringTokenizer(classPath, File.pathSeparator);
                while (pathTokenizer.hasMoreTokens()) {
                    // create a class loader with the URLs
                    ClassLoader loader = new URLClassLoader(pathUrls);
                    // locate class names in the path element
                    File path = new File(pathTokenizer.nextToken());
                    System.out.println("Examining " + path.getAbsolutePath());
                    List<String> classNames = null;
                    if (!path.exists() && path.canRead()) {
                        continue;
                    }
                    if (path.getName().toLowerCase().indexOf(JAR_EXTENSION) != -1) {
                        classNames = getJarClassNames(path);
                    } else if (path.isDirectory()) {
                        classNames = getDirectoryClassNames(path);
                    } else {
                        System.err.println(path.getAbsolutePath() 
                            + " is not a directory or jar file!");
                        continue;
                    }
                    for (String className : classNames) {
                        if (!className.contains("$") 
                            && !className.startsWith("java.") && !className.startsWith("javax.")) {
                            Class candidate = null;
                            try {
                                candidate = loader.loadClass(className);
                            } catch (Throwable th) {
                                // th.printStackTrace();
                            }
                            if (candidate != null) {
                                if (!Modifier.isAbstract(candidate.getModifiers()) && baseClass.isAssignableFrom(candidate)) {
                                    System.out.println("Keeping class " + className);
                                    subs.add(candidate.getName());
                                }
                            }
                        }
                    }
                    loader = null;
                    System.runFinalization();
                    System.gc();
                }
                Collections.sort(subs);
                System.out.println("Adding classes to steps combo box");
                getStepClassnameComboBox().removeAllItems();
                for (String name : subs) {
                    getStepClassnameComboBox().addItem(name);   
                }
                getDiscoverStepsButton().setEnabled(true);
            }
        };
        SwingUtilities.invokeLater(discoverThread);
    }
    
    
    /**
     * Gets a list of class names found by browsing through a Jar file
     * for class files
     * @param jarFile
     * @return
     *      A List of all class names in the jar
     */
    private List<String> getJarClassNames(File jarFile) {
        List<String> classNames = new ArrayList();
        try {
            JarFile jar = new JarFile(jarFile);
            Enumeration entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = (JarEntry) entries.nextElement();
                String entryName = entry.getName();
                if (entryName.toLowerCase().endsWith(CLASS_EXTSNSION)) {
                    classNames.add(entryName.substring(0, entryName.length() - CLASS_EXTSNSION.length())
                        .replace('/', '.'));
                }
            }
            jar.close();
        } catch (Throwable th) {
        }
        return classNames;
    }
    
    
    /**
     * Gets class names from the given directory of .class files
     * @param directory
     * @return
     *      A List of Class names
     */
    private List<String> getDirectoryClassNames(File directory) {
        List<File> classFiles = Utils.recursiveListFiles(directory, new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.getName().toLowerCase().endsWith(CLASS_EXTSNSION);
            }
        });
        List classNames = new ArrayList(classFiles.size());
        int baseDirNameLength = directory.getAbsolutePath().length() + 1;
        for (File classFile : classFiles) {
            String rawName = classFile.getAbsolutePath();
            rawName = rawName.substring(baseDirNameLength);
            rawName = rawName.substring(0, rawName.length() - CLASS_EXTSNSION.length());
            rawName = rawName.replace(File.separatorChar, '.');
            classNames.add(rawName);
        }
        return classNames;
    }
    
    
    private void configureStep(ServiceTestStep step) {
        String stepClassname = step.getClassname();
        try {
            Class stepClass = Class.forName(stepClassname);
            AbstractBaseServiceTestStep stepInstance = (AbstractBaseServiceTestStep) stepClass.newInstance();
            Set<String> requiredParameters = stepInstance.getRequiredConfigurationProperties();
            ServiceTestStepConfigurationProperty[] currentConfigProperties = step.getConfigurationProperty();
            if (currentConfigProperties == null) {
                currentConfigProperties = new ServiceTestStepConfigurationProperty[0];
            }
            // ensure all the required parameters are represented in the configuration
            Map<String, ServiceTestStepConfigurationProperty> byKey = new HashMap();
            for (ServiceTestStepConfigurationProperty prop : currentConfigProperties) {
                if (requiredParameters.contains(prop.getKey())) {
                    byKey.put(prop.getKey(), prop);
                }
            }
            for (String key : requiredParameters) {
                if (!byKey.keySet().contains(key)) {
                    ServiceTestStepConfigurationProperty prop = new ServiceTestStepConfigurationProperty();
                    prop.setKey(key);
                    byKey.put(key, prop);
                }
            }
            // build a properties instance with the current step's configuration
            Properties currentConfiguration = new Properties();
            for (ServiceTestStepConfigurationProperty prop : byKey.values()) {
                String value = "";
                if (prop.getValue() != null) {
                    value = prop.getValue();
                }
                currentConfiguration.setProperty(prop.getKey(), value);
            }
            // fire up the dialog
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(ServiceTypePanel.this);
            Properties editedConfiguration = StepConfigurationDialog.configureStep(parentFrame, currentConfiguration);
            ServiceTestStepConfigurationProperty[] editedProperties = new ServiceTestStepConfigurationProperty[editedConfiguration.size()];
            String[] editedKeys = editedConfiguration.keySet().toArray(new String[0]);
            for (int i = 0; i < editedKeys.length; i++) {
                editedProperties[i] = new ServiceTestStepConfigurationProperty();
                editedProperties[i].setKey(editedKeys[i]);
                editedProperties[i].setValue(editedConfiguration.getProperty(editedKeys[i]));
            }
            step.setConfigurationProperty(editedProperties);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(ServiceTypePanel.this, "Error initializing step configuration: " + ex.getMessage());
        }
    }
    
    
    public Icon getUpIcon() {
        return IntroduceLookAndFeel.getUpIcon();
    }


    public Icon getDownIcon() {
        return IntroduceLookAndFeel.getDownIcon();
    }
    
    
    public Icon getEditIcon() {
        return LookAndFeel.getPreferencesIcon();
    }
}
