package org.cagrid.gme.common.exceptions;

import java.io.IOException;


@SuppressWarnings("serial")
public class SchemaParsingException extends IOException {

    public SchemaParsingException() {
        super();
    }


    public SchemaParsingException(String s) {
        super(s);
    }

}
