package gov.nih.nci.cagrid.data.utilities.query;

import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/** 
 *  TypeDisplayPanel
 *  Panel for displaying types and information about them
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Sep 11, 2006 
 * @version $Id$ 
 */
public class TypeDisplayPanel extends JPanel {

	private TypeTraverser traverser;
	
	private JList typesList = null;
	private JScrollPane typesScrollPane = null;
	private JScrollPane attributesScrollPane = null;
	private JTable attributesTable = null;
	private JTable associationsTable = null;
	private JScrollPane associationsScrollPane = null;
	
	public TypeDisplayPanel() {
		initialize();
	}
	
	
	public void setTypeTraverser(TypeTraverser typeTraverser) {
		this.traverser = typeTraverser;
		((DefaultListModel) getTypesList().getModel()).removeAllElements();
		BaseType[] allTypes = traverser.getBaseTypes();
		Arrays.sort(allTypes, new Comparator<BaseType>() {
			public int compare(BaseType o1, BaseType o2) {
				return o1.toString().compareTo(o2.toString());
			}
		});
		getTypesList().setListData(allTypes);
		DefaultTableModel attribModel = (DefaultTableModel) getAttributesTable().getModel();
		while (attribModel.getRowCount() != 0) {
			attribModel.removeRow(0);
		}
		DefaultTableModel assocModel = (DefaultTableModel) getAssociationsTable().getModel();
		while (assocModel.getRowCount() != 0) {
			assocModel.removeRow(0);
		}
	}
	
	
	public TypeTraverser getTypeTraverser() {
		return this.traverser;
	}
	
	
	public void setSelectedType(BaseType type) {
		getTypesList().setSelectedValue(type, true);
	}
	
	
	public BaseType getSelectedType() {
		return (BaseType) getTypesList().getSelectedValue();
	}
	
	
	public AttributeType getSelectedAttribute() {
		int row = getAttributesTable().getSelectedRow();
		if (row != -1) {
			String name = (String) getAttributesTable().getValueAt(row, 0);
			String value = (String) getAttributesTable().getValueAt(row, 1);
			AttributeType att = new AttributeType(name, value);
			return att;
		}
		return null;
	}
	
	
	public AssociatedType getSelectedAssociation() {
		int row = getAssociationsTable().getSelectedRow();
		if (row != -1) {
			String role = (String) getAssociationsTable().getValueAt(row, 0);
			String type = (String) getAssociationsTable().getValueAt(row, 1);
			AssociatedType assoc = new AssociatedType(type, role);
			return assoc;
		}
		return null;
	}
	
	
	/**
	 * This method initializes this 
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints2.gridy = 1;
		gridBagConstraints2.weightx = 1.0;
		gridBagConstraints2.weighty = 1.0;
		gridBagConstraints2.insets = new java.awt.Insets(2,2,2,2);
		gridBagConstraints2.gridx = 1;
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.weightx = 1.0;
		gridBagConstraints1.weighty = 1.0D;
		gridBagConstraints1.insets = new java.awt.Insets(2,2,2,2);
		gridBagConstraints1.gridx = 1;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1.0D;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.insets = new java.awt.Insets(2,2,2,2);
		gridBagConstraints.gridx = 0;
		this.setLayout(new GridBagLayout());
		this.setSize(new java.awt.Dimension(409,311));
		this.add(getTypesScrollPane(), gridBagConstraints);
		this.add(getAttributesScrollPane(), gridBagConstraints1);
		this.add(getAssociationsScrollPane(), gridBagConstraints2);
	}
	

	/**
	 * This method initializes jList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getTypesList() {
		if (typesList == null) {
			typesList = new JList();
			typesList.setModel(new DefaultListModel());
			typesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			typesList.addListSelectionListener(new ListSelectionListener() {				
				public void valueChanged(ListSelectionEvent e) {
					BaseType type = (BaseType) typesList.getModel().getElementAt(typesList.getSelectedIndex());
					loadTypeData(type);
				}
			});
		}
		return typesList;
	}

	
	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getTypesScrollPane() {
		if (typesScrollPane == null) {
			typesScrollPane = new JScrollPane();
			typesScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(
				null, "Types", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
				javax.swing.border.TitledBorder.DEFAULT_POSITION, null, PortalLookAndFeel.getPanelLabelColor()));
			typesScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			typesScrollPane.setViewportView(getTypesList());
		}
		return typesScrollPane;
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
				null, "Attributes", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
				javax.swing.border.TitledBorder.DEFAULT_POSITION, null, PortalLookAndFeel.getPanelLabelColor()));
			attributesScrollPane.setViewportView(getAttributesTable());
		}
		return attributesScrollPane;
	}
	

	/**
	 * This method initializes jTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getAttributesTable() {
		if (attributesTable == null) {
			attributesTable = new JTable();
			DefaultTableModel attributeModel = new DefaultTableModel();
			attributeModel.addColumn("Name");
			attributeModel.addColumn("Data Type");
			attributesTable.setModel(attributeModel);
		}
		return attributesTable;
	}


	/**
	 * This method initializes jTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getAssociationsTable() {
		if (associationsTable == null) {
			associationsTable = new JTable();
			DefaultTableModel associationsModel = new DefaultTableModel();
			associationsModel.addColumn("Role Name");
			associationsModel.addColumn("Type");
			associationsTable.setModel(associationsModel);
		}
		return associationsTable;
	}


	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getAssociationsScrollPane() {
		if (associationsScrollPane == null) {
			associationsScrollPane = new JScrollPane();
			associationsScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(
				null, "Associations", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
				javax.swing.border.TitledBorder.DEFAULT_POSITION, null, PortalLookAndFeel.getPanelLabelColor()));
			associationsScrollPane.setViewportView(getAssociationsTable());
		}
		return associationsScrollPane;
	}
	
	
	private void loadTypeData(BaseType type) {
		// load the attributes
		AttributeType[] attribs = traverser.getAttributes(type);
		DefaultTableModel attribModel = (DefaultTableModel) getAttributesTable().getModel();
		while (attribModel.getRowCount() != 0) {
			attribModel.removeRow(0);
		}
		for (int i = 0; attribs != null && i < attribs.length; i++) {
			attribModel.addRow(new Object[] {attribs[i].getName(), attribs[i].getDataType()});
		}
		// load associations
		AssociatedType[] associations = traverser.getAssociatedTypes(type);
		DefaultTableModel assocModel = (DefaultTableModel) getAssociationsTable().getModel();
		while (assocModel.getRowCount() != 0) {
			assocModel.removeRow(0);
		}
		for (int i = 0; associations != null && i < associations.length; i++) {
			assocModel.addRow(new Object[] {associations[i].getRoleName(), associations[i].getTypeName()});
		}
	}
}
