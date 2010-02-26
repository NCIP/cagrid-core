package org.cagrid.cql.utilities.encoding;

import javax.xml.namespace.QName;

import org.exolab.castor.mapping.GeneralizedFieldHandler;

public class QNameFieldHandler extends GeneralizedFieldHandler {

    public QNameFieldHandler() {
        super();
    }


    public Object convertUponGet(Object value) {
        if (value == null) return null;
        return ((QName) value).toString();
    }


    public Object convertUponSet(Object value) {
        return QName.valueOf((String) value);
    }


    public Class<?> getFieldType() {
        return QName.class;
    }

}
