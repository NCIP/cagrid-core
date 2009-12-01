package org.cagrid.identifiers.namingauthority.domain;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.ValidationException;

public class Serializer {
	private Marshaller marshaller;
	
	public void setMarshaller( Marshaller aMarshaller ) {
		this.marshaller = aMarshaller;
	}
	
	public String serialize( Object ivs ) throws IOException, MarshalException, ValidationException {
		Writer out = new StringWriter();
        marshaller.setWriter(out);
        marshaller.marshal(ivs);
        
        return out.toString();
	}
	
}
