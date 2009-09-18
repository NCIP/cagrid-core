package org.cagrid.gridftp.authorization.plugin.gridgrouper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

class ClasspathEntityResolver implements EntityResolver {

	public InputSource resolveEntity(String publicID, String systemId) throws SAXException, IOException {
		InputSource source = null;
		if (systemId.contains("gridgrouper-config.xsd")) {
			InputStream stream = this.getClass().getClassLoader().getResourceAsStream(
				GridGrouperAuthCallout.SCHEMA_LOCATION);
			InputStreamReader iReader = new InputStreamReader(stream);
			BufferedReader reader
			   = new BufferedReader(iReader);
			int ch;
			String encoding = iReader.getEncoding();
			System.out.println(encoding);
			OutputStreamWriter writer = new OutputStreamWriter(System.out, encoding);
			boolean done = false;
			while (!done) {
				ch = reader.read();
				if (ch != -1) {
					writer.write(ch);
					writer.flush();
				} else {
					done = true;
				}
				
			}
			stream.reset();
			source = new InputSource(stream);
			source.setSystemId(systemId);
			source.setPublicId(publicID);
		} else if (systemId.contains("gridgrouper.xsd")) {
			InputStream stream = this.getClass().getClassLoader().getResourceAsStream(
				GridGrouperAuthCallout.PARENT_SCHEMA);
			source = new InputSource(stream);
			source.setSystemId(systemId);
			source.setPublicId(publicID);
		}
		return source;
	}

}
