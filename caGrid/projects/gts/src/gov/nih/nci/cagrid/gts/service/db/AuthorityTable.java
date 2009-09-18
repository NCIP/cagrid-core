package gov.nih.nci.cagrid.gts.service.db;

/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public abstract class AuthorityTable {

	public static final String TABLE_NAME = "gts_authorities";

	public static final String GTS_URI = "GTS_URI";

	public static final String SYNC_TRUST_LEVELS = "SYNC_TRUST_LEVELS";

	public static final String PRIORITY = "PRIORITY";

	public static final String TTL_HOURS = "TTL_HOURS";

	public static final String TTL_MINUTES = "TTL_MINUTES";

	public static final String TTL_SECONDS = "TTL_SECONDS";

	public static final String PERFORM_AUTH = "PERFORM_AUTH";

	public static final String GTS_IDENTITY = "GTS_IDENTITY";


	public abstract String getCreateTableSQL();
}
