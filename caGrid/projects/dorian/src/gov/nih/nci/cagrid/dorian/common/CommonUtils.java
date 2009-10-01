package gov.nih.nci.cagrid.dorian.common;

public class CommonUtils {
    public static String identityToSubject(String identity) {
        String s = identity.substring(1);
        return s.replace('/', ',');
    }

    public static String subjectToIdentity(String subject) {
        return "/" + subject.replace(',', '/');
    }

}
