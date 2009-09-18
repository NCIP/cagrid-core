package org.cagrid.metrics.service.hibernate;

import gov.nih.nci.cagrid.common.Utils;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import javax.xml.namespace.QName;

import org.cagrid.metrics.common.UsageEvent;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

public class QNameType implements UserType {

	public static final int[] SQL_TYPES = { Types.VARCHAR };

	public Object assemble(Serializable cached, Object owner)
			throws HibernateException {
		return cached;
	}

	public Object deepCopy(Object value) throws HibernateException {
		if (value != null) {
			QName q = (QName) value;
			return new QName(q.getNamespaceURI(), q.getLocalPart(), q
					.getPrefix());
		} else {
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
			String qname = rs.getString(names[0]);
			int index = qname.indexOf("}");
			String ns = null;
			String name = null;
			if (index >= 0) {
				ns = qname.substring(0, index + 1);
				name = qname.substring(index + 1);
			} else {
				name = qname;
			}
			return new QName(ns, name);
		}
	}

	public void nullSafeSet(PreparedStatement st, Object value, int index)
			throws HibernateException, SQLException {
		if (value == null) {
			st.setNull(index, Types.VARCHAR);
		} else {
			QName q = (QName) value;
			st.setString(index, q.toString());
		}
	}

	public Object replace(Object original, Object target, Object owner)
			throws HibernateException {
		return original;
	}

	public Class returnedClass() {
		return QName.class;
	}

	public int[] sqlTypes() {
		return SQL_TYPES;
	}

}
