package org.cagrid.gaards.pki;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CRLException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.RSAPrivateKey;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERInteger;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.DSAParameter;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.util.encoders.Base64;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class PEMWriter extends BufferedWriter {

	/**
	 * Base constructor.
	 * 
	 * @param out
	 *            output stream to use.
	 */
	public PEMWriter(Writer out) {
		super(out);
	}


	private void writeEncoded(byte[] bytes) throws IOException {
		char[] buf = new char[64];

		bytes = Base64.encode(bytes);

		for (int i = 0; i < bytes.length; i += buf.length) {
			int index = 0;

			while (index != buf.length) {
				if ((i + index) >= bytes.length) {
					break;
				}
				buf[index] = (char) bytes[i + index];
				index++;
			}
			this.write(buf, 0, index);
			this.newLine();
		}
	}


	public void writeObject(Object o) throws IOException {
		String type;
		byte[] encoding;

		if (o instanceof X509Certificate) {
			type = "CERTIFICATE";
			try {
				encoding = ((X509Certificate) o).getEncoded();
			} catch (CertificateEncodingException e) {
				throw new IOException("Cannot encode object: " + e.toString());
			}
		} else if (o instanceof X509CRL) {
			type = "X509 CRL";
			try {
				encoding = ((X509CRL) o).getEncoded();
			} catch (CRLException e) {
				throw new IOException("Cannot encode object: " + e.toString());
			}
		} else if (o instanceof PrivateKey) {
			ByteArrayInputStream bIn = new ByteArrayInputStream(((Key) o).getEncoded());
			ASN1InputStream aIn = new ASN1InputStream(bIn);

			PrivateKeyInfo info = new PrivateKeyInfo((ASN1Sequence) aIn.readObject());
			ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			ASN1OutputStream aOut = new ASN1OutputStream(bOut);

			if (o instanceof RSAPrivateKey) {
				type = "RSA PRIVATE KEY";

				aOut.writeObject(info.getPrivateKey());
			} else if (o instanceof DSAPrivateKey) {
				type = "DSA PRIVATE KEY";

				DSAParameter p = DSAParameter.getInstance(info.getAlgorithmId().getParameters());
				ASN1EncodableVector v = new ASN1EncodableVector();

				v.add(new DERInteger(0));
				v.add(new DERInteger(p.getP()));
				v.add(new DERInteger(p.getQ()));
				v.add(new DERInteger(p.getG()));

				BigInteger x = ((DSAPrivateKey) o).getX();
				BigInteger y = p.getG().modPow(x, p.getP());

				v.add(new DERInteger(y));
				v.add(new DERInteger(x));

				aOut.writeObject(new DERSequence(v));
			} else {
				throw new IOException("Cannot identify private key");
			}

			encoding = bOut.toByteArray();
		} else if (o instanceof PublicKey) {
			type = "PUBLIC KEY";

			encoding = ((PublicKey) o).getEncoded();
		} else if (o instanceof PKCS10CertificationRequest) {
			type = "CERTIFICATE REQUEST";
			encoding = ((PKCS10CertificationRequest) o).getEncoded();
		} else {
			throw new IOException("unknown object passed - can't encode.");
		}

		this.write("-----BEGIN " + type + "-----");
		this.newLine();

		writeEncoded(encoding);

		this.write("-----END " + type + "-----");
		this.newLine();
	}

}
