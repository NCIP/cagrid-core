package gov.nih.nci.cagrid.gts.service.db;

/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public abstract class TrustedAuthorityTrustLevelsTable {
	public static final String TABLE_NAME = "trusted_authority_trust_levels";

	public static final String NAME = "TRUSTED_AUTHORITY_NAME";

	public static final String TRUST_LEVEL = "TRUST_LEVEL";


	public abstract String getCreateTableSQL();
}
