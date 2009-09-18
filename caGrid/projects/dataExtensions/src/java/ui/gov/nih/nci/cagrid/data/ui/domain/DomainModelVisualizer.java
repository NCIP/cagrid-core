package gov.nih.nci.cagrid.data.ui.domain;

import gov.nih.nci.cagrid.data.utilities.dmviz.DomainModelVisualizationPanel;
import gov.nih.nci.cagrid.introduce.common.FileFilters;
import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;

/** 
 *  DomainModelVisualizer
 *  GUI app for visualizing a domain model
 * 
 * @author David Ervin
 * 
 * @created Mar 19, 2008 11:13:47 AM
 * @version $Id: DomainModelVisualizer.java,v 1.3 2009-05-29 20:50:20 dervin Exp $ 
 */
public class DomainModelVisualizer extends JFrame {
    
    private DomainModelVisualizationPanel dmVizPanel = null;
    private JButton loadFileButton = null;
    private JButton saveButton = null;
    private JPanel buttonPanel = null;
    private JPanel mainPanel = null;
    private JButton loadFromServiceButton = null;
    
    private DomainModel currentModel = null;

    public DomainModelVisualizer() {
        super();
        setTitle("Domain Model Visualizer");
        initialize();
    }
    
    
    private void initialize() {
        this.setContentPane(getMainPanel());
        setSize(600,600);
    }
    
    
    private void setCurrentModel(DomainModel model) {
        getDmVizPanel().setDomainModel(model);
        this.currentModel = model;
    }
    
    
    private DomainModelVisualizationPanel getDmVizPanel() {
        if (this.dmVizPanel == null) {
            this.dmVizPanel = new DomainModelVisualizationPanel();
            dmVizPanel.setBorder(BorderFactory.createTitledBorder(
                null, "Domain Model", TitledBorder.DEFAULT_JUSTIFICATION, 
                TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
        }
        return this.dmVizPanel;
    }
    
    
    /**
     * This method initializes loadFileButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getLoadFileButton() {
        if (loadFileButton == null) {
            loadFileButton = new JButton();
            loadFileButton.setText("Load File");
            loadFileButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setFileFilter(FileFilters.XML_FILTER);
                    int choice = chooser.showOpenDialog(DomainModelVisualizer.this);
                    if (choice == JFileChooser.APPROVE_OPTION) {
                        try {
                            FileReader reader = new FileReader(chooser.getSelectedFile());
                            DomainModel model = MetadataUtils.deserializeDomainModel(reader);
                            reader.close();
                            setCurrentModel(model);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });
        }
        return loadFileButton;
    }


    /**
     * This method initializes saveButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getSaveButton() {
        if (saveButton == null) {
            saveButton = new JButton();
            saveButton.setText("Save File");
            saveButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (currentModel != null) {
                        JFileChooser chooser = new JFileChooser();
                        chooser.setFileFilter(FileFilters.XML_FILTER);
                        int choice = chooser.showSaveDialog(DomainModelVisualizer.this);
                        if (choice == JFileChooser.APPROVE_OPTION) {
                            File saveme = chooser.getSelectedFile();
                            try {
                                FileWriter writer = new FileWriter(saveme);
                                MetadataUtils.serializeDomainModel(currentModel, writer);
                                writer.flush();
                                writer.close();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            });
        }
        return saveButton;
    }
    
    
    /**
     * This method initializes loadFromServiceButton    
     *  
     * @return javax.swing.JButton  
     */
    private JButton getLoadFromServiceButton() {
        if (loadFromServiceButton == null) {
            loadFromServiceButton = new JButton();
            loadFromServiceButton.setText("Load From Service");
            loadFromServiceButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    String url = JOptionPane.showInputDialog("Enter Service URL");
                    if (url != null && url.length() != 0) {
                        try {
                            EndpointReferenceType epr = new EndpointReferenceType(new Address(url));
                            DomainModel model = MetadataUtils.getDomainModel(epr);
                            setCurrentModel(model);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });
        }
        return loadFromServiceButton;
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
            gridLayout.setColumns(3);
            buttonPanel = new JPanel();
            buttonPanel.setLayout(gridLayout);
            buttonPanel.add(getLoadFromServiceButton(), null);
            buttonPanel.add(getLoadFileButton(), null);
            buttonPanel.add(getSaveButton(), null);
        }
        return buttonPanel;
    }


    /**
     * This method initializes mainPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.anchor = GridBagConstraints.EAST;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.gridy = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.weightx = 1.0D;
            gridBagConstraints.weighty = 1.0D;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.gridy = 0;
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.add(getDmVizPanel(), gridBagConstraints);
            mainPanel.add(getButtonPanel(), gridBagConstraints1);
        }
        return mainPanel;
    }


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println("Error setting system look and feel: " + ex.getMessage());
        }
        DomainModelVisualizer viz = new DomainModelVisualizer();
        viz.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        viz.setVisible(true);
    }
}
