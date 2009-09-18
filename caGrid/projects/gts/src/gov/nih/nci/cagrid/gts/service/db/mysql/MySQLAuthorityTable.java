package gov.nih.nci.cagrid.gts.service.db.mysql;

import gov.nih.nci.cagrid.gts.service.db.AuthorityTable;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class MySQLAuthorityTable extends AuthorityTable {

	public String getCreateTableSQL() {
		String trust = "CREATE TABLE " + TABLE_NAME + " (" + GTS_URI + " VARCHAR(255) NOT NULL PRIMARY KEY," + PRIORITY
			+ " INT NOT NULL, " + SYNC_TRUST_LEVELS + " VARCHAR(5) NOT NULL, " + TTL_HOURS + " INT NOT NULL, "
			+ TTL_MINUTES + " INT NOT NULL," + TTL_SECONDS + " INT NOT NULL, " + PERFORM_AUTH
			+ " VARCHAR(5) NOT NULL, " + GTS_IDENTITY + " VARCHAR(255)," + " INDEX document_index (" + GTS_URI + ")) ENGINE=InnoDB;";
		return trust;
	}

}
