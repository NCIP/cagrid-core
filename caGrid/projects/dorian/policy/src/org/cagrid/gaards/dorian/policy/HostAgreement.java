package org.cagrid.gaards.dorian.policy;

import gov.nih.nci.cagrid.opensaml.XML;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.content.X509Data;
import org.apache.xml.security.signature.Reference;
import org.apache.xml.security.signature.SignedInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.transforms.params.InclusiveNamespaces;
import org.cagrid.gaards.pki.CertUtil;
import org.cagrid.gaards.pki.KeyUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import sun.security.rsa.SunRsaSign;


public class HostAgreement {
    private Document doc;
    private Element root;
    private XMLSignature sig;
    private boolean sigFromParse = false;


    public HostAgreement() {
        initializeDOM();
    }


    public HostAgreement(Element e) throws Exception {

        if (e != null) {
            fromDOM(e);
        } else {
            initializeDOM();
        }
    }


    public HostAgreement(InputStream in) throws Exception {
        this(PolicyUtils.streamToElement(in));
    }


    public void fromDOM(Element e) throws Exception {
        // Locate the Signature beneath the root.
        Element n = XML.getFirstChildElement(e, PolicyConstants.XMLSIG_NS, "Signature");
        if (n != null) {
            sig = new XMLSignature((Element) n, "");
            sigFromParse = true;
        }
    }


    private void initializeDOM() {
        doc = PolicyUtils.getParserPool().newDocument();
        root = doc.createElementNS(PolicyConstants.HOST_AGREEMENT_NS, PolicyConstants.HOST_AGREEMENT_ELEMENT);
        root.setAttributeNS(PolicyConstants.XMLNS_NS, "xmlns", PolicyConstants.HOST_AGREEMENT_NS);
        root.setAttributeNS(PolicyConstants.XMLNS_NS, "xmlns:xsd", PolicyConstants.XSD_NS);
        root.setAttributeNS(PolicyConstants.XMLNS_NS, "xmlns:policy", PolicyConstants.HOST_AGREEMENT_NS);
        root.setAttributeNS(PolicyConstants.XMLNS_NS, "xmlns:xsi", PolicyConstants.XSI_NS);
        root.getOwnerDocument().appendChild(root);
    }


    public void setName(String name) {
        Element e = PolicyUtils.getFirstChildElement(root, PolicyConstants.HOST_AGREEMENT_NS,
            PolicyConstants.NAME_ELEMENT);
        if (e == null) {
            e = doc.createElementNS(PolicyConstants.HOST_AGREEMENT_NS, PolicyConstants.NAME_ELEMENT);
            root.appendChild(e);
        }
        e.setTextContent(name);
    }


    public String getName() {
        Element e = PolicyUtils.getFirstChildElement(root, PolicyConstants.HOST_AGREEMENT_NS,
            PolicyConstants.NAME_ELEMENT);
        if (e == null) {
            return null;
        } else {
            return e.getTextContent();
        }
    }


    public Element toDOM() {
        return root;
    }


    public void sign(List<X509Certificate> certs, PrivateKey key) throws Exception {
        sign(certs, key, XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1, MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1);
    }


    public void sign(List<X509Certificate> certs, PrivateKey key, String sigalg, String digalg) throws Exception {

        try {
            unsign();
            // Build the empty signature.
            sig = new XMLSignature(this.doc, "", sigalg, Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
            // Have the object place it in the proper place.
            root.appendChild(sig.getElement());

            Transforms transforms = new Transforms(sig.getDocument());
            transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
            transforms.addTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);

            transforms.item(1).getElement().appendChild(
                new InclusiveNamespaces(this.doc, PolicyConstants.INCLUSIVE_NAMESPACES).getElement());

            sig.addDocument("", transforms, (digalg != null) ? digalg : MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1);
            // Add any X.509 certificates provided.
            X509Data x509 = new X509Data(root.getOwnerDocument());
            if (certs != null) {
                int count = 0;
                for (int i = 0; i < certs.size(); i++) {
                    X509Certificate cert = certs.get(i);
                    if (((i + 1) < certs.size()) && count > 0) {
                        // Last (but not only) cert in chain. Only add if
                        // it's not self-signed.
                        if (((X509Certificate) cert).getSubjectDN().equals(((X509Certificate) cert).getIssuerDN()))
                            break;
                    }
                    x509.addCertificate((X509Certificate) cert);
                    count++;
                }
            }
            if (x509.lengthCertificate() > 0) {
                KeyInfo keyinfo = new KeyInfo(root.getOwnerDocument());
                keyinfo.add(x509);
                sig.getElement().appendChild(keyinfo.getElement());
            }

            // Finally, sign the thing.
            sig.sign(key);
        } catch (XMLSecurityException e) {
            unsign();
            throw new Exception("XML security exception: " + e.getMessage(), e);
        }
    }


    public void unsign() {
        if (sig != null && sig.getElement().getParentNode() != null) {
            sig.getElement().getParentNode().removeChild(sig.getElement());
        }
        sig = null;
    }


    public boolean isSigned() {
        return (sig != null);
    }


    public void verify() throws Exception {
        verify((Key) null);
    }


    public void verify(Certificate cert) throws Exception {
        verify(cert.getPublicKey());
    }


    public void verify(Key k) throws Exception {
        if (!isSigned())
            throw new Exception("Cannot verify an unsigned document.");

        try {
            // Validate the signature content by checking for specific
            // Transforms.
            boolean valid = false;
            SignedInfo si = sig.getSignedInfo();
            if (si.getLength() == 1) {
                Reference ref = si.item(0);
                if (ref.getURI() == null || ref.getURI().equals("")) {
                    Transforms trans = ref.getTransforms();
                    for (int i = 0; i < trans.getLength(); i++) {
                        if (trans.item(i).getURI().equals(Transforms.TRANSFORM_ENVELOPED_SIGNATURE))
                            valid = true;
                        else if (!trans.item(i).getURI().equals(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS)) {
                            valid = false;
                            break;
                        }
                    }
                }
            }

            if (!valid)
                throw new Exception("Cannot verify document, an invalid signature profile was detected.");

            // If k is null, try and find a key inside the signature.
            if (k == null) {
                if (sigFromParse) {
                    k = sig.getKeyInfo().getPublicKey();
                } else {
                    // This is really, ugly, but when the signature hasn't been
                    // fully built from a DOM,
                    // none of the interesting bits of keying material are
                    // reachable via the API.
                    // We have to serialize out the KeyInfo piece, and reparse
                    // it.
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    Canonicalizer c = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS);
                    out.write(c.canonicalizeSubtree(sig.getElement().getLastChild()));
                    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
                    KeyInfo temp = new KeyInfo(PolicyUtils.getParserPool().parse(in).getDocumentElement(), "");
                    k = temp.getPublicKey();
                }
            }

            if (!sig.checkSignatureValue(k)) {
                throw new Exception("Failed to validate signature");
            }
        } catch (XMLSecurityException e) {
            throw new Exception("Error validating signature, detected an XML security exception: " + e.getMessage(), e);
        } catch (java.io.IOException e) {
            throw new Exception("Error validating signature, detected an I/O exception: " + e.getMessage(), e);
        } catch (SAXException e) {
            throw new Exception("Error validating signature, detected a XML parsing exception: " + e.getMessage(), e);
        }
    }


    public String toString() {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            toStream(os);
            return os.toString("UTF8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public void toStream(OutputStream out) throws java.io.IOException {
        try {
            Canonicalizer c = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
            out.write(c.canonicalizeSubtree(toDOM(), PolicyConstants.INCLUSIVE_NAMESPACES));
        } catch (InvalidCanonicalizerException e) {
            throw new java.io.IOException(e.getMessage());
        } catch (CanonicalizationException e) {
            throw new java.io.IOException(e.getMessage());
        }
    }
}
