package gov.nih.nci.cagrid.data.ui.domain;

import gov.nih.nci.cagrid.data.utilities.dmviz.DomainModelVisualizationPanel;
import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.FileReader;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.cagrid.grape.GridApplication;


/**
 * DomainModelVisualizationDialog Dialog to visually display a Data Service's
 * domain model
 * 
 * @author David Ervin
 * @created Apr 2, 2007 2:02:12 PM
 * @version $Id: DomainModelVisualizationDialog.java,v 1.2 2007/04/23 17:26:43
 *          dervin Exp $
 */
public class DomainModelVisualizationDialog extends JDialog {

    private DomainModelVisualizationPanel dmVizPanel = null;
    private JButton okButton = null;
    private JPanel mainPanel = null;


    public DomainModelVisualizationDialog(Frame owner, DomainModel model) {
        super(owner, "Domain Model", false);
        getDmVizPanel().setDomainModel(model);
        initialize();
    }
    
    
    private DomainModelVisualizationDialog(DomainModel model) {
        super();
        setTitle("Domain Model");
        setModal(true);
        getDmVizPanel().setDomainModel(model);
        initialize();
    }


    private void initialize() {
        setContentPane(getMainPanel());
        pack();
        setSize(500, 500);
        if (GridApplication.getContext() != null) {
            GridApplication.getContext().centerDialog(this);
        }
        setVisible(true);
    }


    private DomainModelVisualizationPanel getDmVizPanel() {
        if (this.dmVizPanel == null) {
            this.dmVizPanel = new DomainModelVisualizationPanel();
        }
        return this.dmVizPanel;
    }


    /**
     * This method initializes okButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getOkButton() {
        if (this.okButton == null) {
            this.okButton = new JButton();
            this.okButton.setText("OK");
            this.okButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    setVisible(false);
                    dispose();
                }
            });
        }
        return this.okButton;
    }


    /**
     * This method initializes mainPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainPanel() {
        if (this.mainPanel == null) {
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.insets = new Insets(0, 2, 2, 2);
            gridBagConstraints1.gridy = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.insets = new Insets(6, 6, 6, 6);
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.weightx = 1.0D;
            gridBagConstraints.weighty = 1.0D;
            gridBagConstraints.gridy = 0;
            this.mainPanel = new JPanel();
            this.mainPanel.setLayout(new GridBagLayout());
            this.mainPanel.add(getDmVizPanel(), gridBagConstraints);
            this.mainPanel.add(getOkButton(), gridBagConstraints1);
        }
        return this.mainPanel;
    }
    
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println("Error setting system look and feel: " + ex.getMessage());
        }
        if (args.length != 1) {
            throw new IllegalArgumentException(
                "USAGE: " + DomainModelVisualizationDialog.class.getName() + " <domainModel>");
        }
        try {
            FileReader reader = new FileReader(args[0]);
            DomainModel model = MetadataUtils.deserializeDomainModel(reader);
            DomainModelVisualizationDialog dialog = new DomainModelVisualizationDialog(model);
            dialog.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
}
