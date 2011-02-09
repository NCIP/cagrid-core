package org.cagrid.gaards.dorian.service.util;

import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;

import org.cagrid.gaards.authentication.BasicAuthentication;
import org.cagrid.gaards.dorian.common.Lifetime;
import org.cagrid.gaards.dorian.idp.BasicAuthCredential;


public class Utils {

    public static Date getExpiredDate(Lifetime lifetime) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.add(Calendar.YEAR, lifetime.getYears());
        cal.add(Calendar.MONTH, lifetime.getMonths());
        cal.add(Calendar.DAY_OF_MONTH, lifetime.getDays());
        cal.add(Calendar.HOUR_OF_DAY, lifetime.getHours());
        cal.add(Calendar.MINUTE, lifetime.getMinutes());
        cal.add(Calendar.SECOND, lifetime.getSeconds());
        return cal.getTime();
    }


    private static LdapName getHostCertificateSubjectPrefix(X509Certificate cacert) {
        LdapName caPrefix = null;
		try {
			caPrefix = new LdapName(cacert.getSubjectX500Principal().getName());
	        caPrefix.remove(caPrefix.size() - 1);
	        caPrefix.add("OU=Services");
		} catch (InvalidNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return caPrefix;
    }


    public static String getHostCertificateSubject(X509Certificate cacert, String host) {
    	LdapName hostSubjectDN = getHostCertificateSubjectPrefix(cacert);
    	try {
			hostSubjectDN.add("CN=" + host);
		} catch (InvalidNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return hostSubjectDN.toString();
    }


    public static BasicAuthentication fromLegacyCredential(BasicAuthCredential auth) {
        BasicAuthentication cred = new BasicAuthentication();
        cred.setUserId(auth.getUserId());
        cred.setPassword(auth.getPassword());
        return cred;
    }

}
