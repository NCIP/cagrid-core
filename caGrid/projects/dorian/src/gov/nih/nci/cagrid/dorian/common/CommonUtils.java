package gov.nih.nci.cagrid.dorian.common;

public class CommonUtils {
    public static String identityToSubject(String identity) {
        if (identity != null) {
            String s = identity.substring(1);
            return s.replace('/', ',');
        } else {
            return null;
        }
    }


    public static String subjectToIdentity(String subject) {
        if (subject != null) {
            return "/" + subject.replace(',', '/');
        } else {
            return null;
        }
    }

}
