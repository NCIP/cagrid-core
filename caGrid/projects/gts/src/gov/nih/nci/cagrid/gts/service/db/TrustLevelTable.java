package gov.nih.nci.cagrid.gts.service.db;

/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public abstract class TrustLevelTable {
	public static final String TABLE_NAME = "trust_levels";

	public static final String NAME = "NAME";

	public static final String DESCRIPTION = "DESCRIPTION";

	public static final String IS_AUTHORITY = "IS_AUTHORITY";

	public static final String AUTHORITY_GTS = "AUTHORITY_GTS";

	public static final String SOURCE_GTS = "SOURCE_GTS";

	public static final String LAST_UPDATED = "LAST_UPDATED";


	public abstract String getCreateTableSQL();
}
