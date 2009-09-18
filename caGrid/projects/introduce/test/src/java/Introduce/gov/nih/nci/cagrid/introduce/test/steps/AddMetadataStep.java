package gov.nih.nci.cagrid.introduce.test.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespacesType;
import gov.nih.nci.cagrid.introduce.beans.namespace.SchemaElementType;
import gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertiesListType;
import gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertyType;
import gov.nih.nci.cagrid.introduce.codegen.SyncTools;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfo;

import java.io.File;

import javax.xml.namespace.QName;


public class AddMetadataStep extends BaseStep {
	private TestCaseInfo tci;


	public AddMetadataStep(TestCaseInfo tci, boolean build) throws Exception {
		super(tci.getDir(),build);
		this.tci = tci;
	}


	public void runStep() throws Throwable {
		System.out.println("Adding metadata.");

		ServiceDescription introService = (ServiceDescription) Utils.deserializeDocument(getBaseDir() + File.separator
			+ tci.getDir() + File.separator + "introduce.xml", ServiceDescription.class);
		
		//		 copy over the bookstore schema to be used with the test
		File schemaFile = new File(Utils.decodeUrl(this.getClass().getResource("/schema/caDSRMetadata.xsd")));
		Utils.copyFile(schemaFile, new File(getBaseDir() + File.separator + tci.getDir() + File.separator + "schema"
			+ File.separator + tci.getName() + File.separator + "caDSRMetadata.xsd"));

		int currentLength = 0;
		NamespacesType namespaces = introService.getNamespaces();
		if (namespaces.getNamespace() != null) {
			currentLength = namespaces.getNamespace().length;
		}
		NamespaceType[] newNamespaceTypes = new NamespaceType[currentLength + 1];
		if (currentLength > 0) {
			System.arraycopy(namespaces.getNamespace(), 0, newNamespaceTypes, 0, currentLength);
		}
		NamespaceType type = new NamespaceType();
		type.setLocation("./" + "caDSRMetadata.xsd");
		type.setNamespace("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata.cadsr");
		type.setPackageName("gov.nih.nci.cagrid.metadata.cadsr");
		SchemaElementType etype = new SchemaElementType();
		etype.setType("caDSRMetadata");
		SchemaElementType[] etypeArr = new SchemaElementType[1];
		etypeArr[0] = etype;
		type.setSchemaElement(etypeArr);
		newNamespaceTypes[currentLength] = type;
		namespaces.setNamespace(newNamespaceTypes);
		
		
		ResourcePropertiesListType metadatasType = CommonTools.getService(introService.getServices(),tci.getName()).getResourcePropertiesList();
		ResourcePropertyType metadata = new ResourcePropertyType();
		metadata.setPopulateFromFile(false);
		metadata.setRegister(true);
		metadata.setQName(new QName("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata.cadsr", "caDSRMetadata"));

		// add new metadata to array in bean
		// this seems to be a wierd way be adding things....
		ResourcePropertyType[] newMetadatas;
		int newLength = 0;
		if (metadatasType !=null && metadatasType.getResourceProperty() != null) {
			newLength = metadatasType.getResourceProperty().length + 1;
			newMetadatas = new ResourcePropertyType[newLength];
			System.arraycopy(metadatasType.getResourceProperty(), 0, newMetadatas, 0, metadatasType.getResourceProperty().length);
		} else {
			newLength = 1;
			newMetadatas = new ResourcePropertyType[newLength];
		}
		ResourcePropertiesListType rplist = new ResourcePropertiesListType();
		newMetadatas[newLength - 1] = metadata;
		rplist.setResourceProperty(newMetadatas);
		introService.getServices().getService(0).setResourcePropertiesList(rplist);

		Utils.serializeDocument(getBaseDir() + File.separator + tci.getDir() + File.separator + "introduce.xml",
			introService, IntroduceConstants.INTRODUCE_SKELETON_QNAME);

		try {
			SyncTools sync = new SyncTools(new File(getBaseDir() + File.separator + tci.getDir()));
			sync.sync();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		// look at the interface to make sure method from file does not
		// exists.......
		String serviceInterface = getBaseDir() + File.separator + tci.getDir() + File.separator + "src" + File.separator
			+ tci.getPackageDir() + File.separator  + "service" + File.separator + "globus" + File.separator + "resource"
			+ File.separator + tci.getName() + "ResourceBase.java";
		assertFalse("Checking that BaseResource contains the load method", StepTools.methodExists(serviceInterface,
			"loadAnalyticalServiceMetadataFromFile"));

		buildStep();
	}

}
