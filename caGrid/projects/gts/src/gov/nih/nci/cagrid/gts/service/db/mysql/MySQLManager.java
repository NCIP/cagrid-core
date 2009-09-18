package gov.nih.nci.cagrid.gts.service.db.mysql;

import gov.nih.nci.cagrid.gts.common.Database;
import gov.nih.nci.cagrid.gts.common.MySQLDatabase;
import gov.nih.nci.cagrid.gts.service.db.AuthorityTable;
import gov.nih.nci.cagrid.gts.service.db.DBManager;
import gov.nih.nci.cagrid.gts.service.db.PermissionsTable;
import gov.nih.nci.cagrid.gts.service.db.TrustLevelTable;
import gov.nih.nci.cagrid.gts.service.db.TrustedAuthorityTable;
import gov.nih.nci.cagrid.gts.service.db.TrustedAuthorityTrustLevelsTable;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class MySQLManager implements DBManager {

	private Database db;

	private AuthorityTable authorityTable;

	private TrustedAuthorityTable trustedAuthorityTable;

	private TrustLevelTable trustLevelTable;

	private PermissionsTable permissionsTable;

	private TrustedAuthorityTrustLevelsTable trustLevels;


	public MySQLManager(MySQLDatabase db) {
		this.db = db;
		authorityTable = new MySQLAuthorityTable();
		trustedAuthorityTable = new MySQLTrustedAuthorityTable();
		trustLevelTable = new MySQLTrustLevelTable();
		permissionsTable = new MySQLPermissionsTable();
		trustLevels = new MySQLTrustedAuthorityTrustLevelsTable();
	}


	public TrustedAuthorityTable getTrustedAuthorityTable() {
		return trustedAuthorityTable;
	}


	public AuthorityTable getAuthorityTable() {
		return authorityTable;
	}


	public TrustLevelTable getTrustLevelTable() {
		return trustLevelTable;
	}


	public PermissionsTable getPermissionsTable() {
		return permissionsTable;
	}


	public TrustedAuthorityTrustLevelsTable getTrustedAuthorityTrustLevelsTable() {
		return trustLevels;
	}


	public Database getDatabase() {
		return db;
	}

}
