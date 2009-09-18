package org.cagrid.gaards.core;

import gov.nih.nci.cagrid.common.Utils;

import java.security.cert.X509Certificate;

import org.cagrid.gaards.pki.CertUtil;

public class EncodedX509Certificate {
	private String encodedCertificate;
	private X509Certificate certificate;
	
	public String getEncodedCertificate() {
		return encodedCertificate;
	}
	
	public void setEncodedCertificate(String encodedCertificate) throws Exception{
		this.encodedCertificate = encodedCertificate;
		this.certificate = CertUtil.loadCertificate(this.encodedCertificate);
	}
	
	public X509Certificate getCertificate() {
		return certificate;
	}
	
	public void setCertificate(X509Certificate certificate) throws Exception{
		this.certificate = certificate;
		this.encodedCertificate = CertUtil.writeCertificate(this.certificate);
	}

	public boolean equals(Object obj) {
	       if((obj!=null)&&(obj instanceof EncodedX509Certificate)){
	    	   EncodedX509Certificate c = (EncodedX509Certificate)obj;
	    	   return Utils.equals(getCertificate(), c.getCertificate());
	       }else{
	    	   return false;
	       }
	}
}
