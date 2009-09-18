package gov.nih.nci.cagrid.gts.service.db.mysql;

import gov.nih.nci.cagrid.gts.service.db.TrustedAuthorityTable;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class MySQLTrustedAuthorityTable extends TrustedAuthorityTable {

	public String getCreateTableSQL() {
		String sql = "CREATE TABLE " + TrustedAuthorityTable.TABLE_NAME + " (" + "" + TrustedAuthorityTable.NAME
			+ " VARCHAR(255) NOT NULL PRIMARY KEY," + TrustedAuthorityTable.CERTIFICATE_DN + " VARCHAR(255) NOT NULL,"
			+ TrustedAuthorityTable.STATUS + " VARCHAR(50) NOT NULL," + TrustedAuthorityTable.IS_AUTHORITY
			+ " VARCHAR(5) NOT NULL," + TrustedAuthorityTable.AUTHORITY_GTS + " VARCHAR(255) NOT NULL,"
			+ TrustedAuthorityTable.SOURCE_GTS + " VARCHAR(255) NOT NULL," + TrustedAuthorityTable.EXPIRES
			+ " BIGINT NOT NULL," + TrustedAuthorityTable.LAST_UPDATED + " BIGINT NOT NULL,"
			+ TrustedAuthorityTable.CERTIFICATE + " TEXT NOT NULL," + TrustedAuthorityTable.CRL
			+ " TEXT, INDEX document_index (" + TrustedAuthorityTable.NAME + ")) ENGINE=InnoDB;";
		return sql;
	}
}
