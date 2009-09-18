package org.cagrid.metrics.service.hibernate;

import gov.nih.nci.cagrid.common.Utils;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.cagrid.metrics.common.UsageEvent;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

public class UsageEventType implements UserType {

	public static final int[] SQL_TYPES = { Types.VARCHAR };

	public Object assemble(Serializable cached, Object owner)
			throws HibernateException {
		return cached;
	}

	public Object deepCopy(Object value) throws HibernateException {
		if(value!=null){
			UsageEvent e = (UsageEvent) value;
			return UsageEvent.fromValue(e.getValue());
		}else{
			return null;
		}
		
	}

	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable) value;
	}

	public boolean equals(Object x, Object y) throws HibernateException {
		return Utils.equals(x, y);
	}

	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	public boolean isMutable() {
		return false;
	}

	public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
			throws HibernateException, SQLException {
		if (rs.wasNull()) {
			return null;
		} else {
			return UsageEvent.fromValue(rs.getString(names[0]));
		}
	}

	public void nullSafeSet(PreparedStatement st, Object value, int index)
			throws HibernateException, SQLException {
		if (value == null) {
			st.setNull(index, Types.VARCHAR);
		} else {
			UsageEvent e = (UsageEvent) value;
			st.setString(index, e.getValue());
		}
	}

	public Object replace(Object original, Object target, Object owner)
			throws HibernateException {
		return original;
	}

	public Class returnedClass() {
		return UsageEvent.class;
	}

	public int[] sqlTypes() {
		return SQL_TYPES;
	}

}
