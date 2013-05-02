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
package org.cagrid.gaards.dorian.ca;

public class KeySizeValidator {

    public static boolean isKeySizeValid(int keySize) {
        if ((keySize == 512) || (keySize == 1024) || (keySize == 2048)) {
            return true;
        } else {
            return false;
        }
    }

}
