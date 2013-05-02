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
package gov.nih.nci.cagrid.wsenum;

import javax.xml.namespace.QName;

/** 
 *  TestingConstants
 *  TODO:DOCUMENT ME
 * 
 * @author David Ervin
 * 
 * @created Jun 6, 2007 12:09:51 PM
 * @version $Id: TestingConstants.java,v 1.1 2007-06-06 16:59:27 dervin Exp $ 
 */
public interface TestingConstants {

    public static final QName BOOKSTORE_QNAME = new QName("gme://projectmobius.org/1/BookStore", "BookStore");
    public static final QName BOOK_QNAME = new QName("gme://projectmobius.org/1/BookStore", "Book");
}
