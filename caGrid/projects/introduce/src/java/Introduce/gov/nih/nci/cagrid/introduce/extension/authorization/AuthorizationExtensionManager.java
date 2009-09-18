package gov.nih.nci.cagrid.introduce.extension.authorization;

import gov.nih.nci.cagrid.introduce.beans.extension.AuthorizationExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.common.SpecificServiceInformation;

/**
 * 
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @created 
 */
public interface AuthorizationExtensionManager {
	
    /**
     * Should create or configure the class that should be used for this particular auth extensions callback.
     * 
     * @param desc
     * @param info
     * @return
     * @throws AuthorizationExtensionException
     */
	public String generateAuthorizationExtension(AuthorizationExtensionDescriptionType desc, SpecificServiceInformation info) throws AuthorizationExtensionException;
	
	public void removeAuthorizationExtension(AuthorizationExtensionDescriptionType desc, SpecificServiceInformation info) throws AuthorizationExtensionException;
}
