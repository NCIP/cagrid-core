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
package org.cagrid.tools;

import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.TestCase;

import org.cagrid.tools.database.Database;
import org.cagrid.tools.database.DatabaseConfiguration;


public class Utils {

	private static final String DB = "test_tools";

	private static Database db = null;


	public static Database getDB() throws Exception {
		if (db == null) {
			InputStream resource = TestCase.class.getResourceAsStream(Constants.DB_CONFIG);
			DatabaseConfiguration conf = (DatabaseConfiguration) gov.nih.nci.cagrid.common.Utils.deserializeObject(
				new InputStreamReader(resource), DatabaseConfiguration.class);
			db = new Database(conf, DB);
			db.createDatabaseIfNeeded();
		}
		return db;
	}
}
