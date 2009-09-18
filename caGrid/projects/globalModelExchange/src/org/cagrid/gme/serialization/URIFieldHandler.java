package org.cagrid.gme.serialization;

import java.net.URI;
import java.net.URISyntaxException;

import org.exolab.castor.mapping.GeneralizedFieldHandler;


public class URIFieldHandler extends GeneralizedFieldHandler {

    @Override
    public Object convertUponGet(Object uri) {
        if (uri == null) {
            return null;
        } else {
            return uri.toString();
        }

    }


    @Override
    public Object convertUponSet(Object string) {
        URI result = null;
        try {
            result = new URI((String) string);
        } catch (URISyntaxException e) {
        }

        return result;
    }


    @Override
    public Class<URI> getFieldType() {
        return URI.class;
    }

}
