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
public class Group {

	private Database db;
	private long groupId;
	private String name;
	private Log log;

	protected Group(Database db, long id) {
		log = LogFactory.getLog(this.getClass().getName());
		this.db = db;
		this.groupId = id;
	}

	private void load() throws GroupException {
		Connection c = null;
		try {
			c = db.getConnection();
			PreparedStatement s = c.prepareStatement("select * from "
					+ GroupManager.GROUPS_TABLE + " where "
					+ GroupManager.GROUP_ID_FIELD + "= ?");
			s.setLong(1, getGroupId());
			ResultSet rs = s.executeQuery();
			if (rs.next()) {
				setName(rs.getString(GroupManager.GROUP_NAME_FIELD));
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

	}

	public void addMember(String member) throws GroupException {
		if ((member == null) || (member.trim().length() <= 0)) {
			GroupException fault = new GroupException(
					"Could not add member, invalid member specified!!!");
			throw fault;
		}
		member = member.trim();
		if (isMember(member)) {
			GroupException fault = new GroupException(
					"Could not add the member "
							+ member
							+ " to the group "
							+ getGroupId()
							+ ", the member specified is already a member of the group!!!");
			throw fault;
		}
		Connection c = null;
		try {
			c = db.getConnection();
			PreparedStatement s = c.prepareStatement("INSERT INTO "
					+ GroupManager.MEMBERS_TABLE + " SET "
					+ GroupManager.MEMBERS_GROUP_FIELD + "= ?, "
					+ GroupManager.MEMBERS_ID_FIELD + "= ?");
			s.setLong(1, getGroupId());
			s.setString(2, member);
			s.execute();
			s.close();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			GroupException fault = new GroupException(
					"Could not add the member " + member + " to the group "
							+ getGroupId() + ", an unexpected error occured!!!",
					e);
			throw fault;
		} finally {
			if (c != null) {
				db.releaseConnection(c);
			}
		}
	}

	public boolean isMember(String member) throws GroupException {
		Connection c = null;
		boolean exists = false;
		try {
			c = db.getConnection();
			PreparedStatement s = c.prepareStatement("select count(*) from "
					+ GroupManager.MEMBERS_TABLE + " WHERE "
					+ GroupManager.MEMBERS_GROUP_FIELD + "= ? AND "
					+ GroupManager.MEMBERS_ID_FIELD + "= ?");
			s.setLong(1, getGroupId());
			s.setString(2, member);
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
					"An unexpected error occurred in determing group membership.",
					e);
			throw fault;
		} finally {
			db.releaseConnection(c);
		}
		return exists;
	}

	public List getMembers() throws GroupException {
		List members = new ArrayList();
		Connection c = null;
		try {
			c = db.getConnection();
			PreparedStatement s = c.prepareStatement("select "
					+ GroupManager.MEMBERS_ID_FIELD + " from "
					+ GroupManager.MEMBERS_TABLE + " WHERE "
					+ GroupManager.MEMBERS_GROUP_FIELD + "= ?");
			s.setLong(1, getGroupId());
			ResultSet rs = s.executeQuery();
			while (rs.next()) {
				members.add(rs.getString(1));
			}
			rs.close();
			s.close();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			GroupException fault = new GroupException(
					"An unexpected error occurred in determining the members of the group "
							+ getGroupId() + ".", e);
			throw fault;
		} finally {
			db.releaseConnection(c);
		}
		return members;
	}

	public void removeMember(String member) throws GroupException {
		Connection c = null;
		try {
			c = db.getConnection();
			PreparedStatement s = c.prepareStatement("delete from "
					+ GroupManager.MEMBERS_TABLE + " WHERE "
					+ GroupManager.MEMBERS_GROUP_FIELD + "= ? AND "
					+ GroupManager.MEMBERS_ID_FIELD + "= ?");
			s.setLong(1, getGroupId());
			s.setString(2, member);
			s.execute();
			s.close();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			GroupException fault = new GroupException(
					"An unexpected error in trying to remove the member "
							+ member + " from the group " + getGroupId(), e);
			throw fault;
		} finally {
			db.releaseConnection(c);
		}
	}

	public void removeAllMembers() throws GroupException {
		Connection c = null;
		try {
			c = db.getConnection();
			PreparedStatement s = c.prepareStatement("delete from "
					+ GroupManager.MEMBERS_TABLE + " WHERE "
					+ GroupManager.MEMBERS_GROUP_FIELD + "= ?");
			s.setLong(1, getGroupId());
			s.execute();
			s.close();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			GroupException fault = new GroupException(
					"An unexpected error in trying to remove all the members from the group "
							+ getGroupId(), e);
			throw fault;
		} finally {
			db.releaseConnection(c);
		}
	}

	public long getGroupId() {
		return groupId;
	}

	public String getName() throws GroupException {
		if (name == null) {
			load();
		}
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
