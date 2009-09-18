package gov.nih.nci.cagrid.data.utilities.query;

import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

/** 
 *  AttributeSelectionDialog
 *  Dialog to select attributes of a data type for returning from a query
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Nov 1, 2006 
 * @version $Id$ 
 */
public class AttributeSelectionDialog extends JDialog {

	private AttributeType[] availableAttributes;
	private boolean singleSelection;
	private AttributeType[] selectedAttributes;
	private JList attributesList = null;
	private JScrollPane attributesScrollPane = null;
	private JButton okButton = null;
	private JButton cancelButton = null;
	private JPanel buttonPanel = null;
	private JPanel mainPanel = null;

	private AttributeSelectionDialog(JFrame owner, AttributeType[] attributes, boolean single) {
		super(owner);
		setModal(true);
		this.singleSelection = single;
		this.availableAttributes = attributes;
		if (single) {
			setTitle("Select Attribute");
		} else {
			setTitle("Select Attributes");
		}
		initialize();
	}
	
	
	private void initialize() {
        this.setContentPane(getMainPanel());
		setSize(300, 350);
		// center the dialog
		int w = getParent().getSize().width;
		int h = getParent().getSize().height;
		int x = getParent().getLocationOnScreen().x;
		int y = getParent().getLocationOnScreen().y;
		Dimension dim = getSize();
		setLocation(w / 2 + x - dim.width / 2, h / 2 + y - dim.height / 2);		
        this.setVisible(true);
	}
	
	
	public static AttributeType[] selectAttributes(QueryBuilder builder, 
		AttributeType[] availableTypes, boolean single) {
		AttributeSelectionDialog dialog = new AttributeSelectionDialog(builder, availableTypes, single);
		return dialog.selectedAttributes;
	}


	/**
	 * This method initializes jList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getAttributesList() {
		if (attributesList == null) {
			attributesList = new JList();
			if (singleSelection) {
				attributesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			} else {
				attributesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			}
			attributesList.setListData(availableAttributes);
		}
		return attributesList;
	}


	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getAttributesScrollPane() {
		if (attributesScrollPane == null) {
			attributesScrollPane = new JScrollPane();
			attributesScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(
				null, "Available Attributes", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION, null, PortalLookAndFeel.getPanelLabelColor()));
			attributesScrollPane.setViewportView(getAttributesList());
			attributesScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		}
		return attributesScrollPane;
	}


	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setText("OK");
			okButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					int[] selectedIndices = getAttributesList().getSelectedIndices();
					selectedAttributes = new AttributeType[selectedIndices.length];
					for (int i = 0; i < selectedIndices.length; i++) {
						selectedAttributes[i] = (AttributeType) getAttributesList()
							.getModel().getElementAt(selectedIndices[i]);
					}
					dispose();
				}
			});
		}
		return okButton;
	}


	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText("Cancel");
			cancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					selectedAttributes = null;
					dispose();
				}
			});
		}
		return cancelButton;
	}


	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints1.gridy = 0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints.gridy = 0;
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new GridBagLayout());
			buttonPanel.add(getOkButton(), gridBagConstraints);
			buttonPanel.add(getCancelButton(), gridBagConstraints1);
		}
		return buttonPanel;
	}


	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getMainPanel() {
		if (mainPanel == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.gridy = 1;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints2.gridy = 0;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.weighty = 1.0D;
			gridBagConstraints2.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints2.gridx = 0;
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			mainPanel.add(getAttributesScrollPane(), gridBagConstraints2);
			mainPanel.add(getButtonPanel(), gridBagConstraints3);
		}
		return mainPanel;
	}
}
