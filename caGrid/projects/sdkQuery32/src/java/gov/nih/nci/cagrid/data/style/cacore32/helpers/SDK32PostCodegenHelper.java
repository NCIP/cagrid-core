package gov.nih.nci.cagrid.data.style.cacore32.helpers;

import gov.nih.nci.cagrid.data.style.sdkstyle.helpers.PostCodegenHelper;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

/** 
 *  SDK32PostCodegenHelper
 *  Fixes stuff in the castor mapping
 * 
 * @author David Ervin
 * 
 * @created Aug 30, 2007 3:56:50 PM
 * @version $Id: SDK32PostCodegenHelper.java,v 1.1 2009-01-06 17:29:28 dervin Exp $ 
 */
public class SDK32PostCodegenHelper extends PostCodegenHelper {

    public void codegenPostProcessStyle(ServiceExtensionDescriptionType desc, ServiceInformation info) throws Exception {
        super.codegenPostProcessStyle(desc, info);
    }
}
