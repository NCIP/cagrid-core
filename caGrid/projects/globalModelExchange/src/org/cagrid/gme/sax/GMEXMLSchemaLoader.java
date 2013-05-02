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
package org.cagrid.gme.sax;

import java.util.List;

import org.apache.xerces.impl.xs.XMLSchemaLoader;
import org.cagrid.gme.domain.XMLSchema;
import org.cagrid.gme.service.dao.XMLSchemaInformationDao;


/**
 * @author oster
 */
public class GMEXMLSchemaLoader extends XMLSchemaLoader {

    public GMEXMLSchemaLoader(List<XMLSchema> submissionSchemas, XMLSchemaInformationDao dao) {
        setEntityResolver(new GMEEntityResolver(submissionSchemas, dao));
        setErrorHandler(new GMEErrorHandler());
    }


    @Override
    public GMEErrorHandler getErrorHandler() {
        return (GMEErrorHandler) super.getErrorHandler();
    }
}
