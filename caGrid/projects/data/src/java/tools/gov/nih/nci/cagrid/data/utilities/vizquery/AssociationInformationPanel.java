package gov.nih.nci.cagrid.data.utilities.vizquery;

import gov.nih.nci.cagrid.data.utilities.DomainModelUtils;
import gov.nih.nci.cagrid.metadata.common.UMLClass;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.metadata.dataservice.UMLAssociation;
import gov.nih.nci.cagrid.metadata.dataservice.UMLAssociationEdge;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


/**
 * AssociationInformationPanel 
 * Panel to maintain and display some information on a UML association
 * 
 * @author David Ervin
 * 
 * @created Apr 2, 2007 10:37:00 AM
 * @version $Id: AssociationInformationPanel.java,v 1.2 2007-04-06 14:50:14 dervin Exp $
 */
public class AssociationInformationPanel extends JPanel {

    private JLabel sourceClassLabel = null;
    private JLabel sourceRoleNameLabel = null;
    private JLabel targetClassLabel = null;
    private JLabel targetRoleNameLabel = null;
    private JTextField sourceClassTextField = null;
    private JTextField sourceRoleNameTextField = null;
    private JTextField targetClassTextField = null;
    private JTextField targetRoleNameTextField = null;
    private UMLAssociation currentAssociation = null;


    public AssociationInformationPanel() {
        initialize();
    }


    /**
     * this.setLayout(new GridBagLayout()); 
     * This method initializes this
     * 
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
        gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints7.gridy = 3;
        gridBagConstraints7.weightx = 1.0;
        gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints7.gridx = 1;
        GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
        gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints6.gridy = 2;
        gridBagConstraints6.weightx = 1.0;
        gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints6.gridx = 1;
        GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
        gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints5.gridy = 1;
        gridBagConstraints5.weightx = 1.0;
        gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints5.gridx = 1;
        GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
        gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints4.gridy = 0;
        gridBagConstraints4.weightx = 1.0;
        gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints4.gridx = 1;
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.gridx = 0;
        gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints3.gridy = 3;
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints2.gridy = 2;
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints1.gridy = 1;
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints.gridy = 0;
        this.setLayout(new GridBagLayout());
        this.setSize(new Dimension(309, 106));
        this.add(getSourceClassLabel(), gridBagConstraints);
        this.add(getSourceRoleNameLabel(), gridBagConstraints1);
        this.add(getTargetClassLabel(), gridBagConstraints2);
        this.add(getTargetRoleNameLabel(), gridBagConstraints3);
        this.add(getSourceClassTextField(), gridBagConstraints4);
        this.add(getSourceRoleNameTextField(), gridBagConstraints5);
        this.add(getTargetClassTextField(), gridBagConstraints6);
        this.add(getTargetRoleNameTextField(), gridBagConstraints7);
    }


    /**
     * Sets the association to be displayed.  If the association is <code>null</code>,
     * the text fields will be cleared out.
     * 
     * @param association
     *      The association to be displayed
     * @param model
     *      The domain model this association pertains to
     */
    public void setAssociation(UMLAssociation association, DomainModel model) {
        if (association != null) {
            UMLAssociationEdge sourceEdge = 
                association.getSourceUMLAssociationEdge().getUMLAssociationEdge();
            UMLAssociationEdge targetEdge = 
                association.getTargetUMLAssociationEdge().getUMLAssociationEdge();
            UMLClass sourceClass = DomainModelUtils.getReferencedUMLClass(
                model, sourceEdge.getUMLClassReference());
            UMLClass targetClass = DomainModelUtils.getReferencedUMLClass(
                model, targetEdge.getUMLClassReference());
            getSourceClassTextField().setText(sourceClass.getPackageName() + "." 
                + sourceClass.getClassName());
            getSourceRoleNameTextField().setText(sourceEdge.getRoleName());
            getTargetClassTextField().setText(targetClass.getPackageName() + "."
                + targetClass.getClassName());
            getTargetRoleNameTextField().setText(targetEdge.getRoleName());
        } else {
            // clear the UI
            getSourceClassTextField().setText("");
            getSourceRoleNameTextField().setText("");
            getTargetClassTextField().setText("");
            getTargetRoleNameTextField().setText("");
        }
        currentAssociation = association;
    }
    
    
    public UMLAssociation getCurrentAssociation() {
        return currentAssociation;
    }
    
    
    public String getSourceClassName() {
        return getSourceClassTextField().getText();
    }
    
    
    public String getSourceRoleName() {
        return getSourceRoleNameTextField().getText();
    }
    
    
    public String getTargetClassName() {
        return getTargetClassTextField().getText();
    }
    
    
    public String getTargetRoleName() {
        return getTargetRoleNameTextField().getText();
    }


    /**
     * This method initializes sourceClassLabel
     * 
     * @return javax.swing.JLabel
     */
    private JLabel getSourceClassLabel() {
        if (sourceClassLabel == null) {
            sourceClassLabel = new JLabel();
            sourceClassLabel.setText("Source Class:");
        }
        return sourceClassLabel;
    }


    /**
     * This method initializes sourceRoleNameLabel
     * 
     * @return javax.swing.JLabel
     */
    private JLabel getSourceRoleNameLabel() {
        if (sourceRoleNameLabel == null) {
            sourceRoleNameLabel = new JLabel();

            sourceRoleNameLabel.setText("Source Role Name:");
        }
        return sourceRoleNameLabel;
    }


    /**
     * This method initializes targetClassLabel
     * 
     * @return javax.swing.JLabel
     */
    private JLabel getTargetClassLabel() {
        if (targetClassLabel == null) {
            targetClassLabel = new JLabel();
            targetClassLabel.setText("Target Class:");
        }
        return targetClassLabel;
    }


    /**
     * This method initializes targetRoleNameLabel
     * 
     * @return javax.swing.JLabel
     */
    private JLabel getTargetRoleNameLabel() {
        if (targetRoleNameLabel == null) {
            targetRoleNameLabel = new JLabel();
            targetRoleNameLabel.setText("Target Role Name:");
        }
        return targetRoleNameLabel;
    }


    /**
     * This method initializes sourceClassTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getSourceClassTextField() {
        if (sourceClassTextField == null) {
            sourceClassTextField = new JTextField();
            sourceClassTextField.setEditable(false);
        }
        return sourceClassTextField;
    }


    /**
     * This method initializes sourceRoleNameTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getSourceRoleNameTextField() {
        if (sourceRoleNameTextField == null) {
            sourceRoleNameTextField = new JTextField();
            sourceRoleNameTextField.setEditable(false);
        }
        return sourceRoleNameTextField;
    }


    /**
     * This method initializes targetClassTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getTargetClassTextField() {
        if (targetClassTextField == null) {
            targetClassTextField = new JTextField();
            targetClassTextField.setEditable(false);
        }
        return targetClassTextField;
    }


    /**
     * This method initializes targetRoleNameTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getTargetRoleNameTextField() {
        if (targetRoleNameTextField == null) {
            targetRoleNameTextField = new JTextField();
            targetRoleNameTextField.setEditable(false);
        }
        return targetRoleNameTextField;
    }
}
