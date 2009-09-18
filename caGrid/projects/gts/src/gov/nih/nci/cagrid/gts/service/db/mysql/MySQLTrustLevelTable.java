package gov.nih.nci.cagrid.gts.service.db.mysql;

import gov.nih.nci.cagrid.gts.service.db.TrustLevelTable;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class MySQLTrustLevelTable extends TrustLevelTable {

	public String getCreateTableSQL() {
		String sql = "CREATE TABLE " + TABLE_NAME + " (" + TrustLevelTable.NAME + " VARCHAR(255) NOT NULL PRIMARY KEY,"
			+ TrustLevelTable.DESCRIPTION + " TEXT, " + TrustLevelTable.IS_AUTHORITY + " VARCHAR(5) NOT NULL,"
			+ TrustLevelTable.AUTHORITY_GTS + " VARCHAR(255) NOT NULL," + TrustLevelTable.SOURCE_GTS
			+ " VARCHAR(255) NOT NULL, " + TrustLevelTable.LAST_UPDATED + " BIGINT NOT NULL,"
			+ "INDEX document_index (" + TrustLevelTable.NAME + ")) ENGINE=InnoDB;";
		return sql;
	}
}
