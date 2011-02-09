package gov.nih.nci.cagrid.dorian.common;

import org.bouncycastle.asn1.x509.X509Name;
import org.globus.gsi.bc.X509NameHelper;

public class CommonUtils {
    public static String identityToSubject(String identity) {
        if (identity != null) {
            String s = identity.substring(1);
            X509Name name = new X509Name(true,s.replace('/', ','));
            return name.toString();
        } else {
            return null;
        }
    }


    public static String subjectToIdentity(String subject) {
        if (subject != null) {
        	X509Name name = new X509Name(true, subject);
        	return X509NameHelper.toString(name);
        } else {
            return null;
        }
    }

}
