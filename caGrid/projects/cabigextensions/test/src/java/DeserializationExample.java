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
