package org.cagrid.gaards.pki;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;

import org.bouncycastle.util.encoders.Base64;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class CRLReader extends BufferedReader {
	private String provider;


	public CRLReader(Reader reader) {
		this(reader, "BC");
	}


	public CRLReader(Reader reader, String provider) {
		super(reader);
		this.provider = provider;
	}


	public X509CRL readCRL() throws IOException {
		String line;

		while ((line = readLine()) != null) {
			if (line.indexOf("-----BEGIN X509 CRL") != -1) {
				return readCRL("-----END X509 CRL");
			}

		}
		return null;
	}


	private X509CRL readCRL(String endMarker) throws IOException {
		String line;
		StringBuffer buf = new StringBuffer();

		while ((line = readLine()) != null) {
			if (line.indexOf(endMarker) != -1) {
				break;
			}
			buf.append(line.trim());
		}

		if (line == null) {
			throw new IOException(endMarker + " not found");
		}

		ByteArrayInputStream bIn = new ByteArrayInputStream(Base64.decode(buf.toString()));

		try {
			CertificateFactory fact = CertificateFactory.getInstance("X.509", provider);
			return (X509CRL) fact.generateCRL(bIn);
		} catch (Exception e) {
			throw new IOException("problem parsing cert: " + e.toString());
		}
	}

}