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
