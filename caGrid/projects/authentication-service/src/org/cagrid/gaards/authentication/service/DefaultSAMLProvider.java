/**
 * $Id: DefaultSAMLProvider.java,v 1.3 2008-06-02 14:50:12 langella Exp $
 *
 */
package org.cagrid.gaards.authentication.service;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.opensaml.SAMLAssertion;
import gov.nih.nci.cagrid.opensaml.SAMLAttribute;
import gov.nih.nci.cagrid.opensaml.SAMLAttributeStatement;
import gov.nih.nci.cagrid.opensaml.SAMLAuthenticationStatement;
import gov.nih.nci.cagrid.opensaml.SAMLNameIdentifier;
import gov.nih.nci.cagrid.opensaml.SAMLSubject;
import gov.nih.nci.security.authentication.principal.EmailIdPrincipal;
import gov.nih.nci.security.authentication.principal.FirstNamePrincipal;
import gov.nih.nci.security.authentication.principal.LastNamePrincipal;
import gov.nih.nci.security.authentication.principal.LoginIdPrincipal;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.security.auth.Subject;
import javax.xml.namespace.QName;

import org.apache.xml.security.signature.XMLSignature;
import org.cagrid.gaards.authentication.common.InsufficientAttributeException;
import org.cagrid.gaards.pki.CertUtil;
import org.cagrid.gaards.pki.KeyUtil;

/**
 *
 * @version $Revision: 1.3 $
 * @author Joshua Phillips
 *
 */
public class DefaultSAMLProvider implements
	org.cagrid.gaards.authentication.service.SAMLProvider {
    
    private String certificateFileName;
    private String privateKeyFileName;
    private X509Certificate certificate;
    private PrivateKey privateKey;
    private String password;
    
    public void loadCertificates(){
	try{
	    File certFile = new File(getCertificateFileName());
	    if(!certFile.exists()){
		throw new Exception("Certificate file not found at: " + certFile.getAbsolutePath());
	    }
	    Reader certReader = new FileReader(certFile);
	    X509Certificate cert = CertUtil.loadCertificate(certReader);
	    if(cert == null){
		throw new Exception("Failed to load certificate.");
	    }
	    setCertificate(cert);
	    File keyFile = new File(getPrivateKeyFileName());
	    if(!keyFile.exists()){
		throw new Exception("Private Key file not found at: " + keyFile.getAbsolutePath());
	    }
	    PrivateKey key = KeyUtil.loadPrivateKey(keyFile, Utils.clean(getPassword()));
	    if(key == null){
		throw new Exception("Failed to load private key.");
	    }
	    setPrivateKey(key);
	}catch(Exception ex){
	    throw new RuntimeException("Error loading certificates: " + ex.getMessage(), ex);
	}
    }
    
    public X509Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(X509Certificate certificate) {
        this.certificate = certificate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    /* (non-Javadoc)
     * @see gov.nih.nci.cagrid.authentication.common.SAMLProvider#getSAML(javax.security.auth.Subject)
     */
    public SAMLAssertion getSAML(Subject subject)
	    throws InsufficientAttributeException {

	//Get attributes
	String uid = null;
	String firstName = null;
	String lastName = null;
	String email = null;
	Set principals = subject.getPrincipals();

	Iterator it = principals.iterator();
	while (it.hasNext()) {
	    Principal p = (Principal) it.next();
	    if(p instanceof LoginIdPrincipal){
		uid = p.getName();
	    }else
	    if (p instanceof FirstNamePrincipal) {
		firstName = p.getName();
	    }else
	    if (p instanceof LastNamePrincipal) {
		lastName = p.getName();
	    }else
	    if (p instanceof EmailIdPrincipal) {
		email = p.getName();
	    }
	}
	if (firstName == null || firstName.trim().length() < 1 || lastName == null || lastName.trim().length() < 1
		|| email == null || email.trim().length() < 1) {
	    throw new InsufficientAttributeException(
		    "Missing attributes for the user");
	}
	
	SAMLAssertion saml = null;

	try {
	    org.apache.xml.security.Init.init();
	    GregorianCalendar cal = new GregorianCalendar();
	    Date start = cal.getTime();
	    cal.add(Calendar.MINUTE, 2);
	    Date end = cal.getTime();
	    String issuer = this.certificate.getSubjectDN().toString();
	    String federation = this.certificate.getSubjectDN().toString();
	    String ipAddress = null;
	    String subjectDNS = null;

	    SAMLNameIdentifier ni1 = new SAMLNameIdentifier(uid, federation,
		    "urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified");
	    SAMLSubject sub = new SAMLSubject(ni1, null, null, null);
	    sub.addConfirmationMethod(SAMLSubject.CONF_BEARER);
	    SAMLNameIdentifier ni2 = new SAMLNameIdentifier(uid, federation,
		    "urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified");
	    SAMLSubject sub2 = new SAMLSubject(ni2, null, null, null);
	    sub2.addConfirmationMethod(SAMLSubject.CONF_BEARER);
	    SAMLAuthenticationStatement auth = new SAMLAuthenticationStatement(
		    sub, "urn:oasis:names:tc:SAML:1.0:am:unspecified", new Date(),
		    ipAddress, subjectDNS, null);

	    QName quid = new QName(SAMLConstants.UID_ATTRIBUTE_NAMESPACE,
		    SAMLConstants.UID_ATTRIBUTE);
	    List vals1 = new ArrayList();
	    vals1.add(uid);
	    SAMLAttribute uidAtt = new SAMLAttribute(quid.getLocalPart(), quid
		    .getNamespaceURI(), null, 0, vals1);

	    QName qfirst = new QName(
		    SAMLConstants.FIRST_NAME_ATTRIBUTE_NAMESPACE,
		    SAMLConstants.FIRST_NAME_ATTRIBUTE);
	    List vals2 = new ArrayList();
	    vals2.add(firstName);
	    SAMLAttribute firstNameAtt = new SAMLAttribute(qfirst
		    .getLocalPart(), qfirst.getNamespaceURI(), null, 0, vals2);

	    QName qLast = new QName(
		    SAMLConstants.LAST_NAME_ATTRIBUTE_NAMESPACE,
		    SAMLConstants.LAST_NAME_ATTRIBUTE);
	    List vals3 = new ArrayList();
	    vals3.add(lastName);
	    SAMLAttribute lastNameAtt = new SAMLAttribute(qLast.getLocalPart(),
		    qLast.getNamespaceURI(), null, 0, vals3);

	    QName qemail = new QName(SAMLConstants.EMAIL_ATTRIBUTE_NAMESPACE,
		    SAMLConstants.EMAIL_ATTRIBUTE);
	    List vals4 = new ArrayList();
	    vals4.add(email);
	    SAMLAttribute emailAtt = new SAMLAttribute(qemail.getLocalPart(),
		    qemail.getNamespaceURI(), null, 0, vals4);

	    List atts = new ArrayList();
	    atts.add(uidAtt);
	    atts.add(firstNameAtt);
	    atts.add(lastNameAtt);
	    atts.add(emailAtt);

	    SAMLAttributeStatement attState = new SAMLAttributeStatement(sub2,
		    atts);

	    List l = new ArrayList();
	    l.add(auth);
	    l.add(attState);

	    saml = new SAMLAssertion(issuer, start, end, null,
		    null, l);
	    List a = new ArrayList();
	    a.add(this.certificate);
	    saml.sign(XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1,
		    this.privateKey, a);

	} catch (Exception e) {
	    throw new RuntimeException(e);

	}
	return saml;
    }

    public String getCertificateFileName() {
        return certificateFileName;
    }

    public void setCertificateFileName(String certificateFileName) {
        this.certificateFileName = certificateFileName;
    }

    public String getPrivateKeyFileName() {
        return privateKeyFileName;
    }

    public void setPrivateKeyFileName(String privateKeyFileName) {
        this.privateKeyFileName = privateKeyFileName;
    }


}
