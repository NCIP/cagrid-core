/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
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
