package gov.nih.nci.cagrid.data.ui.tree.uml;

import gov.nih.nci.cadsr.umlproject.domain.UMLClassMetadata;
import gov.nih.nci.cadsr.umlproject.domain.UMLPackageMetadata;
import org.cagrid.cadsr.portal.CaDSRBrowserPanel;
import gov.nih.nci.cagrid.data.ui.tree.CheckTreeSelectionEvent;
import gov.nih.nci.cagrid.data.ui.tree.CheckTreeSelectionListener;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.cagrid.cadsr.UMLModelService;
import org.cagrid.cadsr.client.CaDSRUMLModelService;
import org.cagrid.grape.utils.CompositeErrorDialog;


/**
 * UMLTreeTest Util to test the UML Tree
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @created Oct 5, 2006
 * @version $Id: UMLTreeTest.java,v 1.4 2009-01-07 04:45:40 oster Exp $
 */
public class UMLTreeTest extends JFrame {
    private UMLProjectTree projectTree = null;
    private CaDSRBrowserPanel cadsrBrowser = null;
    private JScrollPane projectScrollPane = null;
    private JButton addButton = null;
    private JPanel jPanel = null; // @jve:decl-index=0:visual-constraint="11,69"


    public UMLTreeTest() {
        super();
        initialize();
    }


    private void initialize() {
        setContentPane(getJPanel());
        setSize(new Dimension(400, 400));
        setVisible(true);
    }


    private UMLProjectTree getProjectTree() {
        if (projectTree == null) {
            projectTree = new UMLProjectTree();
            projectTree.addCheckTreeSelectionListener(new CheckTreeSelectionListener() {
                public void nodeChecked(CheckTreeSelectionEvent e) {
                    System.out.println("Checked");
                    if (e.getNode() instanceof UMLPackageTreeNode) {
                        System.out.println("Package: " + e.getNode().getUserObject().toString());
                    } else {
                        System.out.println("Class: " + e.getNode().getUserObject().toString());
                    }
                }


                public void nodeUnchecked(CheckTreeSelectionEvent e) {
                    System.out.println("Unchecked");
                    if (e.getNode() instanceof UMLPackageTreeNode) {
                        System.out.println("Package: " + e.getNode().getUserObject().toString());
                    } else {
                        System.out.println("Class: " + e.getNode().getUserObject().toString());
                    }
                }
            });
        }
        return projectTree;
    }


    private CaDSRBrowserPanel getBrowserPanel() {
        if (cadsrBrowser == null) {
            cadsrBrowser = new CaDSRBrowserPanel();
        }
        return cadsrBrowser;
    }


    /**
     * This method initializes jScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane() {
        if (projectScrollPane == null) {
            projectScrollPane = new JScrollPane();
            projectScrollPane.setViewportView(getProjectTree());
        }
        return projectScrollPane;
    }


    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getJButton() {
        if (addButton == null) {
            addButton = new JButton();
            addButton.setText("Add Package");
            addButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    UMLPackageMetadata pack = getBrowserPanel().getSelectedPackage();
                    getProjectTree().addUmlPackage(pack.getName());
                    String cadsrUrl = getBrowserPanel().getCadsr().getText();
                    try {
                        UMLModelService cadsr = new CaDSRUMLModelService(cadsrUrl);
                        UMLClassMetadata[] classes = cadsr.findClassesInPackage(getBrowserPanel().getSelectedProject(),
                            pack.getName());
                        for (int i = 0; i < classes.length; i++) {
                            getProjectTree().addUmlClass(pack.getName(), classes[i].getName());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        CompositeErrorDialog.showErrorDialog(ex);
                    }
                }
            });
        }
        return addButton;
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel() {
        if (jPanel == null) {
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints2.gridy = 2;
            gridBagConstraints2.weightx = 1.0;
            gridBagConstraints2.weighty = 1.0;
            gridBagConstraints2.gridx = 0;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints1.gridy = 1;
            gridBagConstraints1.gridx = 0;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1.0D;
            gridBagConstraints.gridx = 0;
            jPanel = new JPanel();
            jPanel.setLayout(new GridBagLayout());
            jPanel.setSize(new java.awt.Dimension(390, 298));
            jPanel.add(getBrowserPanel(), gridBagConstraints);
            jPanel.add(getJButton(), gridBagConstraints1);
            jPanel.add(getJScrollPane(), gridBagConstraints2);
        }
        return jPanel;
    }


    public static void main(String[] args) {
        UMLTreeTest test = new UMLTreeTest();
        test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
