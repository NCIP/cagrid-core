package gov.nih.nci.cagrid.introduce.portal.modification.services.resourceproperties;

import gov.nih.nci.cagrid.common.portal.PortalBaseTable;
import gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertiesListType;
import gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertyType;

import java.util.Vector;

import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.xml.namespace.QName;


/**
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 */
public class ResourcePropertyTable extends PortalBaseTable {

	public static String NAMESPACE = "Namespace";
	public static String TYPE = "Type";
	public static String POPULATE_FROM_FILE = "Populate From File";
	public static String REGISTER = "Register";
	public static String DESC = "Description";
	public static String DATA = "Data";

	private ResourcePropertiesListType metadatas;
	
	private boolean allowPopulateFromFile;


	public ResourcePropertyTable(ResourcePropertiesListType metadatas, boolean allowPopulateFromFile) {
		super(createTableModel());
		this.allowPopulateFromFile = allowPopulateFromFile;
		this.metadatas = metadatas;
		initialize();
	}


	public boolean isCellEditable(int row, int column) {
		if ((column == 0) || (column == 1)) {
			return false;
		}
		return true;
	}


	public ResourcePropertyType getRowData(int row) throws Exception {
		if ((row < 0) || (row >= getRowCount())) {
			throw new Exception("invalid row");
		}
		String namespace = (String) getValueAt(row, 0);
		String type = (String) getValueAt(row, 1);
		Boolean populateFromFile = (Boolean) getValueAt(row, 2);
		Boolean register = (Boolean) getValueAt(row, 3);
		String description = (String) getValueAt(row, 4);

		ResourcePropertyType metadata = (ResourcePropertyType) getValueAt(row, 5);

		if ((namespace != null) && !namespace.equals("") && (type != null) && !type.equals("")) {
			metadata.setQName(new QName(namespace, type));
		}
		if ((populateFromFile != null) && !populateFromFile.equals("")) {
			metadata.setPopulateFromFile(populateFromFile.booleanValue());
		}
		if ((register != null) && !register.equals("")) {
			metadata.setRegister(register.booleanValue());
		}
		if (description != null) {
			metadata.setDescription(description);
		}
		return metadata;
	}


	public void addRow(ResourcePropertyType metadata) {
		final Vector v = new Vector(5);
		v.add(metadata.getQName().getNamespaceURI());
		v.add(metadata.getQName().getLocalPart());
		v.add(new Boolean(metadata.isPopulateFromFile()));
		v.add(new Boolean(metadata.isRegister()));
		if (metadata.getDescription() == null) {
			metadata.setDescription("");
		}
		v.add(metadata.getDescription());
		v.add(metadata);

		((DefaultTableModel) this.getModel()).addRow(v);
		
		this.setRowSelectionInterval(this.getModel().getRowCount() - 1, this.getModel().getRowCount() - 1);
		paint(getGraphics());
	}


	public void removeSelectedRow() throws Exception {
		int row = getSelectedRow();
		if ((row < 0) || (row >= getRowCount())) {
			return;
		}
		int oldSelectedRow = getSelectedRow();
		((DefaultTableModel) getModel()).removeRow(oldSelectedRow);
		if (oldSelectedRow == 0) {
			oldSelectedRow++;
		}
		if (getRowCount() > 0) {
			setRowSelectionInterval(oldSelectedRow - 1, oldSelectedRow - 1);
		}
	}


	private void initialize() {
		this.getTableHeader().setReorderingAllowed(false);
		this.setColumnSelectionAllowed(false);
		this.setRowSelectionAllowed(true);
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.getColumn(DATA).setMaxWidth(0);
		this.getColumn(DATA).setMinWidth(0);
		this.getColumn(DATA).setPreferredWidth(0);
		if(!this.allowPopulateFromFile){
			this.getColumn(POPULATE_FROM_FILE).setMaxWidth(0);
			this.getColumn(POPULATE_FROM_FILE).setMinWidth(0);
			this.getColumn(POPULATE_FROM_FILE).setPreferredWidth(0);
		}
		while (getRowCount() != 0) {
			removeRow(0);
		}

		if ((metadatas != null) && (metadatas.getResourceProperty() != null)) {
			for (int i = 0; i < metadatas.getResourceProperty().length; i++) {
				this.addRow(metadatas.getResourceProperty(i));
			}
		}
	}


	public void setResourceProperties(ResourcePropertiesListType properties) {
		metadatas = properties;
		initialize();
	}


	public static MyDefaultTableModel createTableModel() {
		MyDefaultTableModel model = new MyDefaultTableModel();

		return model;
	}


	public void singleClick() throws Exception {
		// TODO Auto-generated method stub

	}


	public void doubleClick() throws Exception {
		// TODO Auto-generated method stub

	}


	public static class MyDefaultTableModel extends DefaultTableModel {

		public MyDefaultTableModel() {
			super();
			addColumn(NAMESPACE);
			addColumn(TYPE);
			addColumn(POPULATE_FROM_FILE);
			addColumn(REGISTER);
			addColumn(DESC);
			addColumn(DATA);
		}


		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}
	}
}