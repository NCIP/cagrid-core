package gov.nih.nci.cagrid.introduce.extensions.wsenum.ui;

import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.portal.extension.ServiceModificationUIPanel;
import gov.nih.nci.cagrid.wsenum.common.WsEnumConstants;
import gov.nih.nci.cagrid.wsenum.utils.IterImplType;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;

import org.cagrid.grape.utils.CompositeErrorDialog;

/** 
 *  EnumIteratorSelectionPanel
 *  Panel for selection of an enumeration iterator implementation
 * 
 * @author David Ervin
 * 
 * @created Apr 30, 2007 1:59:16 PM
 * @version $Id: EnumIteratorSelectionPanel.java,v 1.1 2008-09-09 19:49:07 dervin Exp $ 
 */
public class EnumIteratorSelectionPanel extends ServiceModificationUIPanel {
    
    private JLabel iterTypeLabel = null;
    private JComboBox iterTypeComboBox = null;
    private JTextField descriptionTextField = null;
    private JLabel descriptionLabel = null;
    private JTextArea notesTextArea = null;
    private JScrollPane notesScrollPane = null;

    public EnumIteratorSelectionPanel(ServiceExtensionDescriptionType desc, ServiceInformation info) {
        super(desc, info);
        this.initialize();
    }
    
    
    public void resetGUI() {
        if (CommonTools.servicePropertyExists(getServiceInfo().getServiceDescriptor(),
            WsEnumConstants.ITER_IMPL_TYPE_PROPERTY)) {
            try {
                String value = CommonTools.getServicePropertyValue(getServiceInfo().getServiceDescriptor(),
                    WsEnumConstants.ITER_IMPL_TYPE_PROPERTY);
                IterImplType impl = IterImplType.valueOf(value);
                getIterTypeComboBox().setSelectedItem(impl);
            } catch (Exception ex) {
                CompositeErrorDialog.showErrorDialog(
                    "Error loading service property", 
                    "Could not load value of service property " + 
                        WsEnumConstants.ITER_IMPL_TYPE_PROPERTY + ": " + ex.getMessage(), 
                    ex);
            }
        }
    }
    
    
    private void initialize() {
        GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
        gridBagConstraints4.fill = GridBagConstraints.BOTH;
        gridBagConstraints4.gridy = 2;
        gridBagConstraints4.weightx = 1.0;
        gridBagConstraints4.weighty = 1.0;
        gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints4.gridwidth = 2;
        gridBagConstraints4.gridx = 0;
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints3.gridy = 1;
        gridBagConstraints3.weightx = 1.0;
        gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints3.gridx = 1;
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.anchor = GridBagConstraints.WEST;
        gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
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
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.gridy = 0;
        this.setLayout(new GridBagLayout());
        this.add(getIterTypeLabel(), gridBagConstraints);
        this.add(getIterTypeComboBox(), gridBagConstraints1);
        this.add(getDescriptionLabel(), gridBagConstraints2);
        this.add(getDescriptionTextField(), gridBagConstraints3);
        this.add(getNotesScrollPane(), gridBagConstraints4);        
    }


    /**
     * This method initializes iterTypeLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getIterTypeLabel() {
        if (iterTypeLabel == null) {
            iterTypeLabel = new JLabel();
            iterTypeLabel.setText("Iterator Type:");
        }
        return iterTypeLabel;
    }


    /**
     * This method initializes iterTypeComboBox	
     * 	
     * @return javax.swing.JComboBox	
     */
    private JComboBox getIterTypeComboBox() {
        if (iterTypeComboBox == null) {
            iterTypeComboBox = new JComboBox();
            // populate the combo box
            for (IterImplType impl : IterImplType.values()) {
                iterTypeComboBox.addItem(impl);
            }
            iterTypeComboBox.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    IterImplType selectedImpl = 
                        (IterImplType) iterTypeComboBox.getSelectedItem();
                    // set the information in the UI
                    getDescriptionTextField().setText(selectedImpl.getShortDescription());
                    getNotesTextArea().setText(selectedImpl.getNotes());
                    // set the service property
                    CommonTools.setServiceProperty(getServiceInfo().getServiceDescriptor(), 
                        WsEnumConstants.ITER_IMPL_TYPE_PROPERTY, selectedImpl.toString(), false);
                }
            });
            iterTypeComboBox.setSelectedItem(IterImplType.CAGRID_CONCURRENT_COMPLETE);
        }
        return iterTypeComboBox;
    }


    /**
     * This method initializes descriptionTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getDescriptionTextField() {
        if (descriptionTextField == null) {
            descriptionTextField = new JTextField();
            descriptionTextField.setEditable(false);
        }
        return descriptionTextField;
    }


    /**
     * This method initializes descriptionLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getDescriptionLabel() {
        if (descriptionLabel == null) {
            descriptionLabel = new JLabel();
            descriptionLabel.setText("Description:");
        }
        return descriptionLabel;
    }


    /**
     * This method initializes notesTextArea	
     * 	
     * @return javax.swing.JTextArea	
     */
    private JTextArea getNotesTextArea() {
        if (notesTextArea == null) {
            notesTextArea = new JTextArea();
            notesTextArea.setEditable(false);
            notesTextArea.setWrapStyleWord(true);
            notesTextArea.setLineWrap(true);
        }
        return notesTextArea;
    }


    /**
     * This method initializes notesScrollPane	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getNotesScrollPane() {
        if (notesScrollPane == null) {
            notesScrollPane = new JScrollPane();
            notesScrollPane.setBorder(BorderFactory.createTitledBorder(
                null, "Iterator Notes", TitledBorder.DEFAULT_JUSTIFICATION, 
                TitledBorder.DEFAULT_POSITION, null, PortalLookAndFeel.getPanelLabelColor()));
            notesScrollPane.setViewportView(getNotesTextArea());
            notesScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        }
        return notesScrollPane;
    }
}
