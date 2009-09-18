package gov.nih.nci.cagrid.data.ui.wizard;

import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.SchemaElementType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

/** 
 *  CacoreWizardUtils
 *  Utils for manipulating stuff within caCORE wizard panels
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Sep 25, 2006 
 * @version $Id: CacoreWizardUtils.java,v 1.2 2007-07-27 01:56:26 dervin Exp $ 
 */
public class CacoreWizardUtils {

	public static final String LAST_DIRECTORY_KEY = "LastDirectory";
    
    private CacoreWizardUtils() {
        // prevent instantiation of class w/ all static methods
    }
		
	
	public static String getServiceBaseDir(ServiceInformation info) {
		/* You'd think this would be right, but no...
		String serviceDir = info.getIntroduceServiceProperties()
			.getProperty(IntroduceConstants.INTRODUCE_SKELETON_DESTINATION_DIR);
		*/
		String serviceDir = info.getBaseDirectory().getAbsolutePath();
		return serviceDir;
	}
	
	
	public static void setSdkSerialization(NamespaceType nsType) {
		if (nsType.getSchemaElement() != null) {
			for (int i = 0; i < nsType.getSchemaElement().length; i++) {
				SchemaElementType elem = nsType.getSchemaElement(i);
				elem.setClassName(elem.getType());
				elem.setSerializer(DataServiceConstants.SDK_SERIALIZER);
				elem.setDeserializer(DataServiceConstants.SDK_DESERIALIZER);
			}
		}
	}
}
