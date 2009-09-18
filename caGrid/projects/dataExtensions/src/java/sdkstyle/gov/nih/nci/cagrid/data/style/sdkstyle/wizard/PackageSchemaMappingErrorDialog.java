package gov.nih.nci.cagrid.data.style.sdkstyle.wizard;

import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;

import org.cagrid.grape.GridApplication;

/** 
 *  PackageSchemaMappingErrorDialog
 *  Dialog for showing errors in schema resolution / mapping
 * 
 * @author David Ervin
 * 
 * @created Aug 21, 2007 2:52:51 PM
 * @version $Id: PackageSchemaMappingErrorDialog.java,v 1.3 2007-11-06 15:53:40 hastings Exp $ 
 */
public class PackageSchemaMappingErrorDialog extends JDialog {
    public static final String ERROR_MESSAGE = 
        "The selected schema does not completly resolve the package.  " + 
        "Please review the classes which could not be mapped to elements " +
        "from the selected schema.";

    private Set<String> unresolvedClasses = null;
    
    private JList classList = null;
    private JScrollPane classScrollPane = null;
    private JTextArea messageTextArea = null;
    private JScrollPane messageScrollPane = null;
    private JButton doneButton = null;
    private JPanel textPanel = null;
    private JPanel mainPanel = null;
    
    public PackageSchemaMappingErrorDialog(Set<String> unresolvedClasses) {
        super(GridApplication.getContext().getApplication(), 
            "Schema Resolution Error", true);
        this.unresolvedClasses = unresolvedClasses;
        initialize();
    }
    
    
    private void initialize() {
        this.setSize(new Dimension(300, 320));
        this.setContentPane(getMainPanel());
        GridApplication.getContext().centerDialog(this);
        setVisible(true);
    }


    /**
     * This method initializes classList	
     * 	
     * @return javax.swing.JList	
     */
    private JList getClassList() {
        if (classList == null) {
            classList = new JList();
            String[] data = new String[unresolvedClasses.size()];
            unresolvedClasses.toArray(data);
            classList.setListData(data);
        }
        return classList;
    }


    /**
     * This method initializes classScrollPane	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getClassScrollPane() {
        if (classScrollPane == null) {
            classScrollPane = new JScrollPane();
            classScrollPane.setVerticalScrollBarPolicy(
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            classScrollPane.setBorder(BorderFactory.createTitledBorder(
                null, "Unresolved Classes", TitledBorder.DEFAULT_JUSTIFICATION, 
                TitledBorder.DEFAULT_POSITION, null, PortalLookAndFeel.getPanelLabelColor()));
            classScrollPane.setViewportView(getClassList());
        }
        return classScrollPane;
    }


    /**
     * This method initializes messageTextArea	
     * 	
     * @return javax.swing.JTextArea	
     */
    private JTextArea getMessageTextArea() {
        if (messageTextArea == null) {
            messageTextArea = new JTextArea();
            messageTextArea.setWrapStyleWord(true);
            messageTextArea.setLineWrap(true);
            messageTextArea.setText(ERROR_MESSAGE);
            messageTextArea.setCaretPosition(0); // scroll to top
        }
        return messageTextArea;
    }


    /**
     * This method initializes messageScrollPane	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getMessageScrollPane() {
        if (messageScrollPane == null) {
            messageScrollPane = new JScrollPane();
            messageScrollPane.setBorder(BorderFactory.createTitledBorder(
                null, "Error", TitledBorder.DEFAULT_JUSTIFICATION, 
                TitledBorder.DEFAULT_POSITION, null, PortalLookAndFeel.getPanelLabelColor()));
            messageScrollPane.setViewportView(getMessageTextArea());
        }
        return messageScrollPane;
    }


    /**
     * This method initializes doneButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getDoneButton() {
        if (doneButton == null) {
            doneButton = new JButton();
            doneButton.setText("Done");
            doneButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    dispose();
                }
            });
        }
        return doneButton;
    }


    /**
     * This method initializes textPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getTextPanel() {
        if (textPanel == null) {
            GridLayout gridLayout = new GridLayout();
            gridLayout.setRows(2);
            gridLayout.setHgap(4);
            gridLayout.setVgap(4);
            textPanel = new JPanel();
            textPanel.setLayout(gridLayout);
            textPanel.add(getMessageScrollPane(), null);
            textPanel.add(getClassScrollPane(), null);
        }
        return textPanel;
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
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.anchor = GridBagConstraints.EAST;
            gridBagConstraints1.gridy = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.weightx = 1.0D;
            gridBagConstraints.weighty = 1.0D;
            gridBagConstraints.gridy = 0;
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.add(getTextPanel(), gridBagConstraints);
            mainPanel.add(getDoneButton(), gridBagConstraints1);
        }
        return mainPanel;
    }
}
