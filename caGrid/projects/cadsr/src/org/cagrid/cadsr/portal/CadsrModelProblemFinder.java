package org.cagrid.cadsr.portal;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileWriter;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.cagrid.cadsr.util.ModelProblemsUtil;

/** 
 *  CadsrModelProblemFinder
 *  Graphical utility to locate problems in models from the caDSR
 * 
 * @author David Ervin
 * 
 * @created Feb 11, 2008 10:30:54 AM
 * @version $Id: CadsrModelProblemFinder.java,v 1.1 2009-01-07 04:45:39 oster Exp $ 
 */
public class CadsrModelProblemFinder extends JFrame {
    
    private CadsrModelProblemsPanel cadsrProblemsPanel = null;
    private JButton saveButton = null;
    private JButton exitButton = null;
    private JPanel buttonsPanel = null;
    private JPanel mainPanel = null;
    
    private String cadsrUrl = null;
    
    
    public CadsrModelProblemFinder() {
        this(ModelProblemsUtil.DEFAULT_CADSR_APPLICATION_URL);
    }
    
    
    public CadsrModelProblemFinder(String cadsrUrl) {
        super();
        setTitle("caDSR Model Problem Finder");
        this.cadsrUrl = cadsrUrl;
        this.initialize();
    }
    
    
    private void initialize() {
        this.setContentPane(getMainPanel());
        this.setSize(400,500);
    }
    
    
    private CadsrModelProblemsPanel getCadsrProblemsPanel() {
        if (cadsrProblemsPanel == null) {
            cadsrProblemsPanel = new CadsrModelProblemsPanel(cadsrUrl);
        }
        return cadsrProblemsPanel;
    }
    

    /**
     * This method initializes saveButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getSaveButton() {
        if (saveButton == null) {
            saveButton = new JButton();
            saveButton.setText("Save Results");
            saveButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    JFileChooser chooser = new JFileChooser();
                    int choice = chooser.showSaveDialog(CadsrModelProblemFinder.this);
                    if (choice == JFileChooser.APPROVE_OPTION) {
                        File selection = chooser.getSelectedFile();
                        if (selection.exists()) {
                            int approve = JOptionPane.showConfirmDialog(CadsrModelProblemFinder.this,
                                selection.getAbsolutePath() + " exists.  Overwrite it?",
                                "Confirm", JOptionPane.YES_NO_OPTION);
                            if (approve != JOptionPane.YES_OPTION) {
                                return;
                            }
                            selection.delete();
                        }
                        try {
                            FileWriter writer = new FileWriter(selection);
                            writer.write(getCadsrProblemsPanel().getModelProblems());
                            writer.flush();
                            writer.close();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });
        }
        return saveButton;
    }


    /**
     * This method initializes exitButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getExitButton() {
        if (exitButton == null) {
            exitButton = new JButton();
            exitButton.setText("Exit");
            exitButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    dispose();
                }
            });
        }
        return exitButton;
    }


    /**
     * This method initializes buttonsPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getButtonsPanel() {
        if (buttonsPanel == null) {
            GridLayout gridLayout = new GridLayout();
            gridLayout.setRows(1);
            gridLayout.setHgap(4);
            gridLayout.setColumns(2);
            buttonsPanel = new JPanel();
            buttonsPanel.setLayout(gridLayout);
            buttonsPanel.add(getSaveButton(), null);
            buttonsPanel.add(getExitButton(), null);
        }
        return buttonsPanel;
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
            gridBagConstraints1.gridy = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.weightx = 1.0D;
            gridBagConstraints.weighty = 1.0D;
            gridBagConstraints.gridy = 0;
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.add(getCadsrProblemsPanel(), gridBagConstraints);
            mainPanel.add(getButtonsPanel(), gridBagConstraints1);
        }
        return mainPanel;
    }


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println("Error setting system look and feel");
        }
        JFrame frame = null;
        if (args.length == 1) {
            frame = new CadsrModelProblemFinder(args[0]);
        } else {
            System.out.println("No URL provided, using default");
            frame = new CadsrModelProblemFinder();
        }
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
