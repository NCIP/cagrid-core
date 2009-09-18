package gov.nih.nci.cagrid.syncgts.core;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.syncgts.bean.SyncDescription;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class SyncGTSDefault {

    public static final String SYNC_GTS_NAMESPACE = "http://cagrid.nci.nih.gov/12/SyncGTS";

    private static Log logger = LogFactory.getLog(SyncGTS.class.getName());

    private static String serviceSyncDescriptionLocation = null;


    public static File getSyncGTSUserDir() {
        File dir = new File(Utils.getCaGridUserHome() + File.separator + "syncgts");
        return dir;
    }


    public static File getUserHomeSyncDescription() {
        File dir = new File(getSyncGTSUserDir() + File.separator + "sync-description.xml");
        return dir;
    }


    public static void setServiceSyncDescriptionLocation(String serviceSyncDescriptionLocation) {
        SyncGTSDefault.serviceSyncDescriptionLocation = serviceSyncDescriptionLocation;
    }


    public static SyncDescription getSyncDescription() throws Exception {
        File userHome = getUserHomeSyncDescription();
        SyncDescription description = null;

        if (serviceSyncDescriptionLocation != null) {
            File serviceLocation = new File(serviceSyncDescriptionLocation);
            if ((serviceLocation.exists())) {
                try {
                    description = (SyncDescription) Utils.deserializeDocument(serviceLocation.getAbsolutePath(),
                        SyncDescription.class);
                    logger.debug("SyncGTS using sync description: " + serviceLocation.getAbsolutePath());
                } catch (Exception e) {
                    description = null;
                    logger.error(e.getMessage(), e);
                }
            }
        }

        if ((description == null) && (userHome.exists())) {
            try {
                description = (SyncDescription) Utils.deserializeDocument(userHome.getAbsolutePath(),
                    SyncDescription.class);
                logger.debug("SyncGTS using sync description: " + userHome.getAbsolutePath());
            } catch (Exception e) {
                description = null;
                logger.error(e.getMessage(), e);
            }
        }

        if (description == null) {
            StringBuffer error = new StringBuffer();
            error
                .append("SyncGTS unable to locate a valid sync description, the following locations were searched:\n 1)"
                    + userHome.getAbsolutePath());
            if (serviceSyncDescriptionLocation != null) {
                error.append("\n 2)" + serviceSyncDescriptionLocation);
            }
            throw new Exception(error.toString());
        }
        return description;
    }

}
