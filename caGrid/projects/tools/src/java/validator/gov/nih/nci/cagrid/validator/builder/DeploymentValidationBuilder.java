package gov.nih.nci.cagrid.validator.builder;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;
import gov.nih.nci.cagrid.introduce.common.FileFilters;
import gov.nih.nci.cagrid.testing.system.haste.Story;
import gov.nih.nci.cagrid.tests.core.beans.validation.Schedule;
import gov.nih.nci.cagrid.tests.core.beans.validation.ServiceDescription;
import gov.nih.nci.cagrid.tests.core.beans.validation.ServiceType;
import gov.nih.nci.cagrid.tests.core.beans.validation.ValidationDescription;
import gov.nih.nci.cagrid.validator.GridDeploymentValidationLoader;
import gov.nih.nci.cagrid.validator.ValidationPackage;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import junit.framework.TestResult;
import junit.textui.TestRunner;

import org.apache.axis.types.URI.MalformedURIException;

/** 
 *  DeploymentValidationBuilder
 *  Utility to build a grid deployment validation test
 * 
 * @author David Ervin
 * 
 * @created Aug 28, 2007 12:14:58 PM
 * @version $Id: DeploymentValidationBuilder.java,v 1.2 2008-11-12 23:36:16 jpermar Exp $ 
 */
public class DeploymentValidationBuilder extends JFrame {
    // -XX:MaxPermSize=256m
    
    private JMenuBar mainMenuBar = null;
    private JMenu fileMenu = null;
    private JMenu testMenu = null;
    private JMenuItem fileLoadMenuItem = null;
    private JMenuItem fileSaveMenuItem = null;
    private JMenuItem fileSaveAsMenuItem = null;
    private JMenuItem fileExitMenuItem = null;
    private JMenuItem testNowMenuItem = null;
    private ServiceTable serviceTable = null;
    private JScrollPane serviceTableScrollPane = null;
    private JPanel buttonPanel = null;
    private JButton addServicesButton = null;
    private JButton removeServicesButton = null;
    private JPanel mainPanel = null;
    private SchedulePanel schedulePanel = null;
    private ServiceTypePanel serviceTypePanel = null;
    
    private File currentDeploymentDescriptionFile = null;

    public DeploymentValidationBuilder() {
        super();
        setTitle("Deployment Validation Builder");
        initialize();
    }
    
    
    private void initialize() {
        this.setContentPane(getMainPanel());
        this.setJMenuBar(getMainMenuBar());
        this.pack();
        this.setSize(new Dimension(625, 450));
        setVisible(true);
    }
    

    /**
     * This method initializes mainMenuBar	
     * 	
     * @return javax.swing.JMenuBar	
     */
    private JMenuBar getMainMenuBar() {
        if (mainMenuBar == null) {
            mainMenuBar = new JMenuBar();
            mainMenuBar.add(getFileMenu());
            mainMenuBar.add(getTestMenu());
        }
        return mainMenuBar;
    }


    /**
     * This method initializes fileMenu	
     * 	
     * @return javax.swing.JMenu	
     */
    private JMenu getFileMenu() {
        if (fileMenu == null) {
            fileMenu = new JMenu();
            fileMenu.setText("File");
            fileMenu.add(getFileLoadMenuItem());
            fileMenu.add(getFileSaveMenuItem());
            fileMenu.add(getFileSaveAsMenuItem());
            fileMenu.addSeparator();
            fileMenu.add(getFileExitMenuItem());
        }
        return fileMenu;
    }


    /**
     * This method initializes fileLoadMenuItem	
     * 	
     * @return javax.swing.JMenuItem	
     */
    private JMenuItem getFileLoadMenuItem() {
        if (fileLoadMenuItem == null) {
            fileLoadMenuItem = new JMenuItem();
            fileLoadMenuItem.setText("Load");
            fileLoadMenuItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    JFileChooser chooser = new JFileChooser(currentDeploymentDescriptionFile == null 
                        ? new File("./") : currentDeploymentDescriptionFile);
                    chooser.setFileFilter(new FileFilters.XMLFileFilter());
                    int choice = chooser.showOpenDialog(DeploymentValidationBuilder.this);
                    if (choice == JFileChooser.APPROVE_OPTION) {
                        currentDeploymentDescriptionFile = chooser.getSelectedFile();
                        loadCurrentFile();
                    }
                }
            });
        }
        return fileLoadMenuItem;
    }


    /**
     * This method initializes fileSaveMenuItem	
     * 	
     * @return javax.swing.JMenuItem	
     */
    private JMenuItem getFileSaveMenuItem() {
        if (fileSaveMenuItem == null) {
            fileSaveMenuItem = new JMenuItem();
            fileSaveMenuItem.setText("Save");
            fileSaveMenuItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (currentDeploymentDescriptionFile != null) {
                        saveCurrentFile();
                    } else {
                        // punt to the save as action
                        getFileSaveAsMenuItem().doClick();
                    }
                }
            });
        }
        return fileSaveMenuItem;
    }


    /**
     * This method initializes fileSaveAsMenuItem	
     * 	
     * @return javax.swing.JMenuItem	
     */
    private JMenuItem getFileSaveAsMenuItem() {
        if (fileSaveAsMenuItem == null) {
            fileSaveAsMenuItem = new JMenuItem();
            fileSaveAsMenuItem.setText("Save As...");
            fileSaveAsMenuItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    JFileChooser chooser = new JFileChooser(currentDeploymentDescriptionFile);
                    chooser.setFileFilter(new FileFilters.XMLFileFilter());
                    int choice = chooser.showSaveDialog(DeploymentValidationBuilder.this);
                    if (choice == JFileChooser.APPROVE_OPTION) {
                        currentDeploymentDescriptionFile = chooser.getSelectedFile();
                        saveCurrentFile();
                    }
                }
            });
        }
        return fileSaveAsMenuItem;
    }


    /**
     * This method initializes fileExitMenuItem	
     * 	
     * @return javax.swing.JMenuItem	
     */
    private JMenuItem getFileExitMenuItem() {
        if (fileExitMenuItem == null) {
            fileExitMenuItem = new JMenuItem();
            fileExitMenuItem.setText("Exit");
            fileExitMenuItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    dispose();
                }
            });
        }
        return fileExitMenuItem;
    }
    
    
    private JMenu getTestMenu() {
        if (testMenu == null) {
            testMenu = new JMenu();
            testMenu.setText("Test");
            testMenu.add(getTestNowMenuItem());
        }
        return testMenu;
    }
    
    
    private JMenuItem getTestNowMenuItem() {
        if (testNowMenuItem == null) {
            testNowMenuItem = new JMenuItem();
            testNowMenuItem.setText("Test Now");
            testNowMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ValidationPackage testPackage = null;
                    try {
                        ValidationDescription description = createValidationDescription();
                        testPackage = GridDeploymentValidationLoader.createValidationPackage(description);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(
                            DeploymentValidationBuilder.this, "Error preparing tests: " + ex.getMessage());
                    }
                    UsefulTestRunner runner = new UsefulTestRunner(testPackage.getValidationStories());
                    runner.go();
                }
            });
        }
        return testNowMenuItem;
    }
    
    
    private ServiceTable getServiceTable() {
        if (serviceTable == null) {
            serviceTable = new ServiceTable();
        }
        return serviceTable;
    }


    /**
     * This method initializes serviceTableScrollPane	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getServiceTableScrollPane() {
        if (serviceTableScrollPane == null) {
            serviceTableScrollPane = new JScrollPane();
            serviceTableScrollPane.setBorder(BorderFactory.createTitledBorder(
                null, "Tested Services", TitledBorder.DEFAULT_JUSTIFICATION, 
                TitledBorder.DEFAULT_POSITION, null, PortalLookAndFeel.getPanelLabelColor()));
            serviceTableScrollPane.setViewportView(getServiceTable());
        }
        return serviceTableScrollPane;
    }


    /**
     * This method initializes buttonPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 1;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.gridy = 0;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.gridy = 0;
            buttonPanel = new JPanel();
            buttonPanel.setLayout(new GridBagLayout());
            buttonPanel.add(getAddServicesButton(), gridBagConstraints);
            buttonPanel.add(getRemoveServicesButton(), gridBagConstraints1);
        }
        return buttonPanel;
    }


    /**
     * This method initializes addServicesButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getAddServicesButton() {
        if (addServicesButton == null) {
            addServicesButton = new JButton();
            addServicesButton.setText("Add Service");
            addServicesButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    addService();
                }
            });
        }
        return addServicesButton;
    }


    /**
     * This method initializes removeServicesButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getRemoveServicesButton() {
        if (removeServicesButton == null) {
            removeServicesButton = new JButton();
            removeServicesButton.setText("Remove Selected");
            removeServicesButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    removeServices();
                }
            });
        }
        return removeServicesButton;
    }


    /**
     * This method initializes mainPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints5.weightx = 1.0D;
            gridBagConstraints5.weighty = 0.5D;
            gridBagConstraints5.gridy = 3;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints4.weightx = 1.0D;
            gridBagConstraints4.gridy = 2;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.anchor = GridBagConstraints.EAST;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.gridy = 1;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.fill = GridBagConstraints.BOTH;
            gridBagConstraints2.gridy = 0;
            gridBagConstraints2.weightx = 1.0;
            gridBagConstraints2.weighty = 1.0D;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.gridx = 0;
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.add(getServiceTableScrollPane(), gridBagConstraints2);
            mainPanel.add(getButtonPanel(), gridBagConstraints3);
            mainPanel.add(getSchedulePanel(), gridBagConstraints4);
            mainPanel.add(getServiceTypePanel(), gridBagConstraints5);
        }
        return mainPanel;
    }
    
    
    private SchedulePanel getSchedulePanel() {
        if (schedulePanel == null) {
            schedulePanel = new SchedulePanel();
        }
        return schedulePanel;
    }
    
    
    private ServiceTypePanel getServiceTypePanel() {
        if (serviceTypePanel == null) {
            serviceTypePanel = new ServiceTypePanel();
        }
        return serviceTypePanel;
    }
    
    // ------------
    // Helpers
    // ------------
    
    
    private ValidationDescription createValidationDescription() throws MalformedURIException {
        ValidationDescription description = new ValidationDescription();
        
        Schedule schedule = getSchedulePanel().getSchedule();
        
        ServiceDescription[] descriptions = getServiceTable().getServiceDescriptions();
        description.setSchedule(schedule);
        if (descriptions.length != 0) {
            description.setServiceDescription(descriptions);
        } else {
            description.setServiceDescription(null);
        }
        
        ServiceType[] types = getServiceTypePanel().getServiceTypes();
        description.setServiceType(types);
        
        return description;
    }
    
    
    private void saveCurrentFile() {
        try {
            ValidationDescription description = createValidationDescription();
            
            FileWriter writer = new FileWriter(currentDeploymentDescriptionFile);
            Utils.serializeObject(description, GridDeploymentValidationLoader.VALIDATION_DESCRIPTION_QNAME, writer);
            writer.flush();
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            String[] message = {
                "An error occured saving the deployment description:",
                ex.getMessage()
            };
            JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    private void loadCurrentFile() {
        try {
            ValidationDescription description = (ValidationDescription) Utils.deserializeDocument(
                currentDeploymentDescriptionFile.getAbsolutePath(), ValidationDescription.class);
            getSchedulePanel().setSchedule(description.getSchedule());
            getServiceTable().clearTable();
            if (description.getServiceDescription() != null) {
                for (ServiceDescription des : description.getServiceDescription()) {
                    getServiceTable().addService(des);
                }
            }
            ServiceType[] types = description.getServiceType();
            if (types != null) {
                getServiceTypePanel().setServiceTypes(types);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            String[] message = {
                "An error occured loading the deployment description:",
                ex.getMessage()
            };
            JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    private void addService() {
        try {
            ServiceType[] types = getServiceTypePanel().getServiceTypes();
            String[] typeNames = null;
            if (types != null) { 
                typeNames = new String[types.length];
                for (int i = 0; i < types.length; i++) {
                    typeNames[i] = types[i].getTypeName();
                }
            }
            ServiceDescription desc = AddServiceDialog.getDescription(this, typeNames);
            if (desc != null) {
                getServiceTable().addService(desc);
            }
        } catch (MalformedURIException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "The specified URL does not appear to be valid", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    private void removeServices() {
        getServiceTable().removeSelectedRows();
    }


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println("Error setting system look and feel");
        }
        JFrame frame = new DeploymentValidationBuilder();
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    
    
    private static class UsefulTestRunner extends TestRunner {
        
        List<Story> stories = null;
        
        public UsefulTestRunner(List<Story> stories) {
            super();
            this.stories = stories;
        }
        
        
        public void go() {
            for (Story s : this.stories) {
            	TestResult result = this.doRun(s);
            }
            
        }
    }
}
