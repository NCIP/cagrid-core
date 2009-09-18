import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.metadata.ServiceMetadata;


public class DeserializationExample {

	public static void main(String[] args) {
		try {
			ServiceMetadata metadata = (ServiceMetadata) Utils.deserializeDocument("serviceMetadata.xml",
				ServiceMetadata.class);
			System.out.println("Success loading file for service:"
				+ metadata.getServiceDescription().getService().getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
