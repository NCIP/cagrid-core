package gov.nih.nci.cagrid.introduce.portal.updater;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.beans.software.IntroduceRevType;
import gov.nih.nci.cagrid.introduce.beans.software.IntroduceType;
import gov.nih.nci.cagrid.introduce.beans.software.SoftwareType;
import gov.nih.nci.cagrid.introduce.common.ConfigurationUtil;
import gov.nih.nci.cagrid.introduce.common.IntroducePropertiesManager;
import gov.nih.nci.cagrid.introduce.portal.updater.common.SoftwareUpdateTools;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.axis.utils.XMLUtils;
import org.globus.wsrf.encoding.ObjectDeserializer;


public class UptodateChecker {

    public static boolean introduceUptodate() throws Exception {

        URL url = null;
        url = new URL(ConfigurationUtil.getIntroducePortalConfiguration().getUpdateSiteURL() + "/software.xml");
        URLConnection connection = url.openConnection();
        InputStream stream = connection.getInputStream();
        org.w3c.dom.Document doc = XMLUtils.newDocument(stream);
        SoftwareType software = (SoftwareType) ObjectDeserializer
            .toObject(doc.getDocumentElement(), SoftwareType.class);

        // check introduce is uptodate
        if (software.getIntroduce() != null) {
            for (int i = 0; i < software.getIntroduce().length; i++) {

                IntroduceType type = software.getIntroduce(i);
                if (!SoftwareUpdateTools.isOlderVersion(IntroducePropertiesManager.getIntroduceVersion(), type
                    .getVersion())) {
                    if (IntroducePropertiesManager.getIntroduceVersion().equals(type.getVersion())) {
                        if (type.getIntroduceRev() != null && type.getIntroduceRev().length > 0) {
                            IntroduceRevType latestRev = type.getIntroduceRev(0);
                            for (int revi = 0; i < type.getIntroduceRev().length; revi++) {
                                IntroduceRevType tryRev = type.getIntroduceRev(revi);

                                if (tryRev.getPatchVersion() >= latestRev.getPatchVersion()) {
                                    latestRev = tryRev;
                                }
                            }

                            int currentRev = Integer.parseInt(IntroducePropertiesManager.getIntroducePatchVersion());

                            if (currentRev < latestRev.getPatchVersion()) {
                                // an introduce patch exists
                                return false;
                            }
                        }
                    } else {
                        // a new version of introduce exists
                        return false;
                    }
                }
            }
        }
        // nothing new that pertains to introduce
        return true;
    }
    
    public static void main(String [] args){
        try {
            introduceUptodate();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
