package org.cagrid.gme.persistence.hibernate.types;

import gov.nih.nci.cagrid.common.Utils;

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


    public boolean equals(Object x, Object y) throws HibernateException {
        return Utils.equals(x, y);
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
                return new URI(rs.getString(names[0]));
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
