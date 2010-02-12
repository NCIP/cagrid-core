package org.cagrid.data.sdkquery42.processor2;

import javax.xml.namespace.QName;

/** 
 *  QNameResolver
 *  A Qname resolver provides a machanism for XML QNames to be determined
 *  for corresponding Java classes.
 * 
 * @author David Ervin
 * 
 * @created Apr 3, 2008 2:14:42 PM
 * @version $Id: QNameResolver.java,v 1.1 2008/04/03 18:19:03 dervin Exp $ 
 */
public interface QNameResolver {

    /**
     * Determines the QName which corresponds to the Java class name
     * 
     * @param javaClassName
     * @return
     *      The QName, or <code>null</code> if none is associated with the class
     */
    public QName getQName(String javaClassName);
}
