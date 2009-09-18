package gov.nih.nci.cagrid.gts.service.db;

/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public abstract class PermissionsTable {
	public static final String TABLE_NAME = "permissions";

	public static final String GRID_IDENTITY = "GRID_IDENTITY";

	public static final String ROLE = "ROLE";

	public static final String TRUSTED_AUTHORITY = "TRUSTED_AUTHORITY";


	public abstract String getCreateTableSQL();
}
