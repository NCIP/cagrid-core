/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package gov.nih.nci.cagrid.gts.service.db.mysql;

import gov.nih.nci.cagrid.gts.service.db.TrustedAuthorityTrustLevelsTable;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class MySQLTrustedAuthorityTrustLevelsTable extends TrustedAuthorityTrustLevelsTable {

	public String getCreateTableSQL() {
		String sql = "CREATE TABLE " + TrustedAuthorityTrustLevelsTable.TABLE_NAME + " (" + ""
			+ TrustedAuthorityTrustLevelsTable.NAME + " VARCHAR(255) NOT NULL,"
			+ TrustedAuthorityTrustLevelsTable.TRUST_LEVEL + " VARCHAR(255) NOT NULL, INDEX document_index ("
			+ TrustedAuthorityTrustLevelsTable.NAME + ")) ENGINE=InnoDB;";
		return sql;
	}
}
