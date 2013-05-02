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
package org.cagrid.transfer.extension.upgrade;

public interface TransferCallbackUpgradeNotes {

    public static final String GFORGE_URL = "http://gforge.nci.nih.gov/tracker/?func=detail&group_id=25&aid=22321&atid=174";
    
    public static final String ISSUE = 
        "caGrid transfer prior to 1.4 was affected\n" +
        "by this bug: " + GFORGE_URL;
    public static final String RESOLUTION = 
        "When creating transfer callback listeners,\n" +
        "the service must make use of the interface\n" +
    	"org.cagrid.transfer.context.service.globus.resource.PersistentTransferCallback.\n" +
    	"Please see the linked GForge issue #22321 for details.";
}
