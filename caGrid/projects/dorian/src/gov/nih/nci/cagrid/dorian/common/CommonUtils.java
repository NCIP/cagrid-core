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
