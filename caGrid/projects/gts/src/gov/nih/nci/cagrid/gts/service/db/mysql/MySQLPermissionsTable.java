package gov.nih.nci.cagrid.gts.service.db.mysql;

import gov.nih.nci.cagrid.gts.service.db.PermissionsTable;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class MySQLPermissionsTable extends PermissionsTable {

	public String getCreateTableSQL() {
		String sql = "CREATE TABLE " + PermissionsTable.TABLE_NAME + " (" + PermissionsTable.GRID_IDENTITY
			+ " VARCHAR(255) NOT NULL," + PermissionsTable.ROLE + " VARCHAR(50) NOT NULL,"
			+ PermissionsTable.TRUSTED_AUTHORITY + " VARCHAR(255) NOT NULL," + "INDEX document_index ("
			+ PermissionsTable.GRID_IDENTITY + ")) ENGINE=InnoDB;";
		return sql;
	}

}
