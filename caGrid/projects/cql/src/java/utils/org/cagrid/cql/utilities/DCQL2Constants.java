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
package org.cagrid.cql.utilities;

import javax.xml.namespace.QName;

public class DCQL2Constants {
    
    public static final String DCQL2_NAMESPACE_URI = "http://DCQL.caBIG/2/org.cagrid.data.dcql";
    public static final QName DCQL2_QUERY_QNAME = new QName(DCQL2_NAMESPACE_URI, "DCQLQuery");
    
    public static final String DCQL2_RESULTS_NAMESPACE_URI = "http://DCQL.caBIG/2/org.cagrid.data.dcql.results";
    public static final QName DCQL2_RESULTS_QNAME = new QName(DCQL2_RESULTS_NAMESPACE_URI, "DCQLQueryResultsCollection");

    private DCQL2Constants() {
        // just constants
    }
}
