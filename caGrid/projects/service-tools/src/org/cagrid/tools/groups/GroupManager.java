package org.cagrid.tools.groups;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.tools.database.Database;

/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class GroupManager {

	protected final static String GROUPS_TABLE = "groups";
	protected final static String GROUP_ID_FIELD = "id";
	protected final static String GROUP_NAME_FIELD = "name";
	protected final static String MEMBERS_TABLE = "members";
	protected final static String MEMBERS_GROUP_FIELD = "groupid";
	protected final static String MEMBERS_ID_FIELD = "member";

	private Database db;
	private boolean dbBuilt = false;
	private Log log;

	public GroupManager(Database db) {
		log = LogFactory.getLog(this.getClass().getName());
		this.db = db;
	}

	public void addGroup(String name) throws GroupException {
		buildDatabase();
		if ((name == null) || (name.trim().length() <= 0)) {
			GroupException fault = new GroupException(
					"Could not add group, no name specified!!!");
			throw fault;
		}
		name = name.trim();
		if (groupExists(name)) {
			GroupException fault = new GroupException(
					"Could not add the group " + name
							+ ", it already exists!!!");
			throw fault;
		}
		Connection c = null;
		try {
			c = db.getConnection();
			PreparedStatement s = c.prepareStatement("INSERT INTO "
					+ GROUPS_TABLE + " SET " + GROUP_NAME_FIELD + "= ?");
			s.setString(1, name);
			s.execute();
			s.close();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			GroupException fault = new GroupException("Error adding the group "
					+ name + ", an unexpected database error occurred.", e);
			throw fault;
		} finally {
			if (c != null) {
				db.releaseConnection(c);
			}
		}
	}

	public Group getGroup(long groupId) throws GroupException {
		buildDatabase();
		return new Group(db, groupId);
	}

	public void removeUserFromAllGroups(String member) throws GroupException {
		List<Group> groups = getGroups();
		for (int i = 0; i < groups.size(); i++) {
			Group g = groups.get(i);
			g.removeMember(member);
		}

	}

	public List<Group> getGroups() throws GroupException {
		buildDatabase();
		List<Group> groups = new ArrayList<Group>();
		Connection c = null;
		try {
			c = db.getConnection();
			PreparedStatement s = c.prepareStatement("select " + GROUP_ID_FIELD
					+ "," + GROUP_NAME_FIELD + " from " + GROUPS_TABLE);
			ResultSet rs = s.executeQuery();
			while (rs.next()) {
				Group grp = new Group(db, rs.getLong(GROUP_ID_FIELD));
				grp.setName(rs.getString(GROUP_NAME_FIELD));
				groups.add(grp);
			}
			rs.close();
			s.close();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			GroupException fault = new GroupException(
					"Unexpected Database Error", e);
			throw fault;
		} finally {
			db.releaseConnection(c);
		}
		return groups;
	}

	public Group getGroup(String name) throws GroupException {
		buildDatabase();
		Connection c = null;
		try {
			c = db.getConnection();
			PreparedStatement s = c.prepareStatement("select " + GROUP_ID_FIELD
					+ " from " + GROUPS_TABLE + " where " + GROUP_NAME_FIELD
					+ "= ?");
			s.setString(1, name);
			ResultSet rs = s.executeQuery();
			Group grp = null;
			if (rs.next()) {
				grp = new Group(db, rs.getLong(GROUP_ID_FIELD));
				grp.setName(name);
			}
			rs.close();
			s.close();
			return grp;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			GroupException fault = new GroupException(
					"Unexpected Database Error", e);
			throw fault;
		} finally {
			db.releaseConnection(c);
		}
	}

	public boolean groupExists(String name) throws GroupException {
		buildDatabase();
		Connection c = null;
		boolean exists = false;
		try {
			c = db.getConnection();
			PreparedStatement s = c.prepareStatement("select count(*) from "
					+ GROUPS_TABLE + " where " + GROUP_NAME_FIELD + "= ?");
			s.setString(1, name);
			ResultSet rs = s.executeQuery();
			if (rs.next()) {
				int count = rs.getInt(1);
				if (count > 0) {
					exists = true;
				}
			}
			rs.close();
			s.close();

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			GroupException fault = new GroupException(
					"Unexpected Database Error", e);
			throw fault;
		} finally {
			db.releaseConnection(c);
		}
		return exists;
	}

	public boolean groupExists(long groupId) throws GroupException {
		buildDatabase();
		Connection c = null;
		boolean exists = false;
		try {
			c = db.getConnection();
			PreparedStatement s = c.prepareStatement("select count(*) from "
					+ GROUPS_TABLE + " where " + GROUP_ID_FIELD + "= ?");
			s.setLong(1, groupId);
			ResultSet rs = s.executeQuery();
			if (rs.next()) {
				int count = rs.getInt(1);
				if (count > 0) {
					exists = true;
				}
			}
			rs.close();
			s.close();

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			GroupException fault = new GroupException(
					"Unexpected Database Error", e);
			throw fault;
		} finally {
			db.releaseConnection(c);
		}
		return exists;
	}

	public synchronized void removeGroup(Group grp) throws GroupException {
		buildDatabase();
		Connection c = null;
		try {
			c = db.getConnection();
			PreparedStatement s = c.prepareStatement("delete from "
					+ GROUPS_TABLE + " WHERE " + GROUP_ID_FIELD + "= ?");
			s.setLong(1, grp.getGroupId());
			s.execute();
			s.close();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			GroupException fault = new GroupException(
					"An unexpected error in trying to remove the group "
							+ grp.getGroupId(), e);
			throw fault;
		} finally {
			db.releaseConnection(c);
		}
		grp.removeAllMembers();
	}

	public void clearDatabase() throws GroupException {
		buildDatabase();
		try {
			db.update("DROP TABLE IF EXISTS " + GROUPS_TABLE);
			db.update("DROP TABLE IF EXISTS " + MEMBERS_TABLE);
			dbBuilt = false;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			GroupException fault = new GroupException(
					"Unexpected Database Error", e);
			throw fault;
		}
	}

	private void buildDatabase() throws GroupException {
		if (!dbBuilt) {
			try {
				if (!this.db.tableExists(GROUPS_TABLE)) {
					String groups = "CREATE TABLE " + GROUPS_TABLE + " ("
							+ GROUP_ID_FIELD
							+ " INT NOT NULL AUTO_INCREMENT PRIMARY KEY,"
							+ GROUP_NAME_FIELD + " VARCHAR(255) NOT NULL"
							+ ");";
					db.update(groups);
				}
				if (!this.db.tableExists(MEMBERS_TABLE)) {
					String members = "CREATE TABLE " + MEMBERS_TABLE + " ("
							+ MEMBERS_GROUP_FIELD + " INT NOT NULL,"
							+ MEMBERS_ID_FIELD + " VARCHAR(255) NOT NULL"
							+ ");";
					db.update(members);
				}
				dbBuilt = true;
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				GroupException fault = new GroupException(
						"Unexpected Database Error", e);
				throw fault;
			}
		}
	}
}
