package org.cagrid.gaards.cds.testutils;

import org.cagrid.tools.database.Database;

public class Nuker {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Database db = Utils.getDatabase();
			System.out.println("Destroying database........ "
					+ db.getDatabaseName());
			db.destroyDatabase();
			System.out.println("The database " + db.getDatabaseName()
					+ " was successfully destroyed!!!");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
