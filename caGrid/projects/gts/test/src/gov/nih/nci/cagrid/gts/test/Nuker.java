package gov.nih.nci.cagrid.gts.test;

import gov.nih.nci.cagrid.gts.common.Database;


public class Nuker {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Database db = Utils.getDBManager().getDatabase();
			System.out.println("Destroying database........ " + db.getDatabaseName());
			db.destroyDatabase();
			System.out.println("The database " + db.getDatabaseName() + " was successfully destroyed!!!");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
