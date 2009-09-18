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
