package org.cagrid.gaards.dorian.federation;

public class IdentityAssignmentPolicy {
    public static final java.lang.String ID = "id";
    public static final java.lang.String NAME = "name";


    public static boolean isValidPolicy(String s) {
        if (s.equals(ID)) {
            return true;
        } else if (s.equals(NAME)) {
            return true;
        } else {
            return false;
        }
    }

}
