package org.cagrid.gaards.pki;

import java.io.ByteArrayInputStream;
import java.security.cert.X509Certificate;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.globus.gsi.CertUtil;
import org.globus.gsi.bc.BouncyCastleUtil;
import org.globus.gsi.proxy.ext.ProxyCertInfo;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: CertificateExtensionsUtil.java,v 1.1 2006/03/01 20:54:22
 *          langella Exp $
 */

public class CertificateExtensionsUtil {

	public static final String subjectKeyIdentifierOID = "2.5.29.14";
	public static final String keyUsageOID = "2.5.29.15";
	public static final String privateKeyUsageOID = "2.5.29.16";
	public static final String subjectAlternativeNameOID = "2.5.29.17";
	public static final String issuerAlternativeNameOID = "2.5.29.18";
	public static final String basicConstraintsOID = "2.5.29.19";
	public static final String nameConstraintsOID = "2.5.29.30";
	public static final String policyMappingsOID = "2.5.29.33";
	public static final String authorityKeyIdentifierOID = "2.5.29.35";
	public static final String policyConstraintsOID = "2.5.29.36";
	public static final String certificatePolicies = "2.5.29.32";


	public static String getExtentionName(String oid) {
		if (oid.equals(subjectKeyIdentifierOID)) {
			return "SubjectKeyIdentifier";
		} else if (oid.equals(keyUsageOID)) {
			return "KeyUsage";
		}else if (oid.equals(certificatePolicies)) {
			return "CertificatePolicies";
		} else if (oid.equals(privateKeyUsageOID)) {
			return "PrivateKeyUsage";
		} else if (oid.equals(subjectAlternativeNameOID)) {
			return "SubjectAlternativeName";
		} else if (oid.equals(issuerAlternativeNameOID)) {
			return "IssuerAlternativeName";
		} else if (oid.equals(basicConstraintsOID)) {
			return "BasicConstraints";
		} else if (oid.equals(nameConstraintsOID)) {
			return "NameConstraints";
		} else if (oid.equals(policyMappingsOID)) {
			return "PolicyMappings";
		} else if (oid.equals(authorityKeyIdentifierOID)) {
			return "AuthorityKeyIdentifier";
		} else if (oid.equals(policyConstraintsOID)) {
			return "PolicyConstraints";
		} else if (oid.equals(ProxyCertInfo.OID.getId())) {
			return "ProxyCertInfo";
		} else {
			return "*** UNKNOWN ***";
		}
	}


	private static DERObject getDERObject(byte[] ext) throws Exception {
		ASN1InputStream aIn = new ASN1InputStream(new ByteArrayInputStream(ext));
		ASN1OctetString extnValue = (ASN1OctetString) aIn.readObject();
		aIn = new ASN1InputStream(new ByteArrayInputStream(extnValue.getOctets()));
		DERObject extensionType = aIn.readObject();
		return extensionType;
	}


	public static String getExtensionDisplayValue(String oid, X509Certificate cert) {
		try {
			if (oid.equals(subjectKeyIdentifierOID)) {
				return "*** DISPLAY NOT SUPPORTED ***";
			} else if (oid.equals(keyUsageOID)) {
				boolean[] usage = cert.getKeyUsage();
				StringBuffer sb = new StringBuffer();
				boolean first = true;
				if (usage[0]) {
					if (first) {
						first = false;
					} else {
						sb.append(", ");
					}
					sb.append("digitalSignature");
				}

				if (usage[1]) {
					if (first) {
						first = false;
					} else {
						sb.append(", ");
					}
					sb.append("nonRepudiation");
				}

				if (usage[2]) {
					if (first) {
						first = false;
					} else {
						sb.append(", ");
					}
					sb.append("keyEncipherment");
				}

				if (usage[3]) {
					if (first) {
						first = false;
					} else {
						sb.append(", ");
					}
					sb.append("dataEncipherment");
				}

				if (usage[4]) {
					if (first) {
						first = false;
					} else {
						sb.append(", ");
					}
					sb.append("keyAgreement");
				}

				if (usage[5]) {
					if (first) {
						first = false;
					} else {
						sb.append(", ");
					}
					sb.append("keyCertSign");
				}

				if (usage[6]) {
					if (first) {
						first = false;
					} else {
						sb.append(", ");
					}
					sb.append("crlSign");
				}
				if (usage[7]) {
					if (first) {
						first = false;
					} else {
						sb.append(", ");
					}
					sb.append("encipherOnly");
				}
				if (usage[8]) {
					if (first) {
						first = false;
					} else {
						sb.append(", ");
					}
					sb.append("decipherOnlys");
				}

				return sb.toString();
			}else if (oid.equals(certificatePolicies)) {
				return "*** DISPLAY NOT SUPPORTED ***";
			} else if (oid.equals(privateKeyUsageOID)) {
				return "*** DISPLAY NOT SUPPORTED ***";
			} else if (oid.equals(subjectAlternativeNameOID)) {
				return "*** DISPLAY NOT SUPPORTED ***";
			} else if (oid.equals(issuerAlternativeNameOID)) {
				return "*** DISPLAY NOT SUPPORTED ***";
			} else if (oid.equals(basicConstraintsOID)) {
				BasicConstraints bc = new BasicConstraints((ASN1Sequence) getDERObject(cert.getExtensionValue(oid)));
				String len = "0";
				if (bc.getPathLenConstraint() != null) {
					len = bc.getPathLenConstraint().toString();
				}
				return "CA=" + bc.isCA() + ", Path Length=" + len;
			} else if (oid.equals(nameConstraintsOID)) {
				return "*** DISPLAY NOT SUPPORTED ***";
			} else if (oid.equals(policyMappingsOID)) {
				return "*** DISPLAY NOT SUPPORTED ***";
			} else if (oid.equals(authorityKeyIdentifierOID)) {
				return "*** DISPLAY NOT SUPPORTED ***";
			} else if (oid.equals(policyConstraintsOID)) {
				return "*** DISPLAY NOT SUPPORTED ***";
			} else if (oid.equals(ProxyCertInfo.OID.getId())) {
				StringBuffer sb = new StringBuffer();
				int type = BouncyCastleUtil.getCertificateType(cert);
				String typeStr = (type == -1) ? "Unknown Proxy Type" : CertUtil.getProxyTypeAsString(type);
				sb.append(typeStr);
				sb.append(", Delegation Path Length: ");
				try {
					sb.append(getDelegationPathLength(cert));
				} catch (Exception ex) {
					ex.printStackTrace();
					sb.append("UNKNOWN");
				}
				return sb.toString();
			} else {
				return "*** UNKNOWN ***";
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "*** ERROR DETERMINING VALUE ***";
		}

	}


	public static int getDelegationPathLength(X509Certificate cert) throws Exception {
		X509Extensions exts = BouncyCastleUtil.getTBSCertificateStructure(cert).getExtensions();
		ProxyCertInfo info = BouncyCastleUtil.getProxyCertInfo(exts.getExtension(ProxyCertInfo.OID));
		return info.getPathLenConstraint();
	}

}
