package org.cagrid.identifiers.namingauthority.hibernate;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;


/**
 * This type makes sure Hibernate stores URIs as strings in the database (when
 * its used).
 * 
 * @author oster
 */
public class URIUserType implements UserType {

    public static final int[] SQL_TYPES = {Types.VARCHAR};


    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }


    public Object deepCopy(Object value) throws HibernateException {
        if (value != null) {
            URI uri = (URI) value;
            try {
                return new URI(uri.toString());
            } catch (URISyntaxException e) {
                throw new HibernateException("Invalid URI", e);
            }
        } else {
            return null;
        }

    }


    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }


    public boolean equals(Object o1, Object o2) throws HibernateException {
        if (o1 == null) {
            return o2 == null;
        }
        return o1.equals(o2);
    }


    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }


    public boolean isMutable() {
        return false;
    }


    public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
        if (rs.wasNull()) {
            return null;
        } else {
            try {
            	String val = rs.getString(names[0]);
            	if (val == null)
            		return null;
                return new URI(val);
            } catch (URISyntaxException e) {
                throw new HibernateException("Invalid URI", e);
            }
        }
    }


    public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.VARCHAR);
        } else {
            URI uri = (URI) value;
            st.setString(index, uri.toString());
        }
    }


    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }


    public Class returnedClass() {
        return URI.class;
    }


    public int[] sqlTypes() {
        return SQL_TYPES;
    }
}
