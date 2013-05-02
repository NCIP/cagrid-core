/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.iso21090.sdkquery.processor.cql2;

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
