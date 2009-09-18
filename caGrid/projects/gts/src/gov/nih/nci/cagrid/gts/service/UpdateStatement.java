package gov.nih.nci.cagrid.gts.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;


public class UpdateStatement {
	private List fields;

	private List values;

	private List whereFields;

	private List whereValues;

	private List whereOperators;

	private String table;


	public UpdateStatement(String table) {
		this.table = table;
		this.fields = new ArrayList();
		this.values = new ArrayList();
		this.whereFields = new ArrayList();
		this.whereValues = new ArrayList();
		this.whereOperators = new ArrayList();
	}


	public void addField(String field, Object value) {
		this.fields.add(field);

		this.values.add(value);
	}


	public void addWhereField(String field, String operator, Object value) {
		this.whereFields.add(field);
		this.whereOperators.add(operator);
		this.whereValues.add(value);
	}


	public PreparedStatement prepareUpdateStatement(Connection c) throws Exception {
		StringBuffer sql = new StringBuffer("UPDATE " + table + " SET ");
		boolean first = true;
		for (int i = 0; i < fields.size(); i++) {
			if (!first) {
				sql.append(",");
			} else {
				first = false;
			}
			sql.append((String) fields.get(i) + " = ?");
		}
		if (whereFields.size() > 0) {
			sql.append(" WHERE ");
		}

		first = true;
		for (int i = 0; i < whereFields.size(); i++) {
			if (!first) {
				sql.append(" AND ");
			} else {
				first = false;
			}
			sql.append((String) whereFields.get(i) + " " + (String) whereOperators.get(i) + " ?");
		}

		PreparedStatement s = c.prepareStatement(sql.toString());
		for (int i = 0; i < values.size(); i++) {
			Object o = values.get(i);
			if (o instanceof String) {
				String str = (String) o;
				s.setString((i + 1), str);
			} else if (o instanceof Long) {
				s.setLong((i + 1), ((Long) o).longValue());
			} else if (o instanceof Integer) {
				s.setInt((i + 1), ((Integer) o).intValue());
			} else {
				throw new Exception("Unsupported type " + o.getClass().getName());
			}
		}

		for (int i = 0; i < whereValues.size(); i++) {
			Object o = whereValues.get(i);
			int index = values.size() + i + 1;
			if (o instanceof String) {
				s.setString((index), (String) o);
			} else if (o instanceof Long) {
				s.setLong((index), ((Long) o).longValue());
			} else if (o instanceof Integer) {
				s.setInt((index), ((Integer) o).intValue());
			} else {
				throw new Exception("Unsupported type " + o.getClass().getName());
			}
		}
		return s;
	}
}
