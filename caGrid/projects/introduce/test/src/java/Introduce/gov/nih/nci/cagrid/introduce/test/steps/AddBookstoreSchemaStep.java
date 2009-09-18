package gov.nih.nci.cagrid.introduce.test.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.SchemaElementType;
import gov.nih.nci.cagrid.introduce.codegen.SyncTools;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfo;

import java.io.File;


public class AddBookstoreSchemaStep extends BaseStep {
	private TestCaseInfo tci;


	public AddBookstoreSchemaStep(TestCaseInfo tci, boolean build) throws Exception {
		super(tci.getDir(), build);
		this.tci = tci;
	}


	public void runStep() throws Throwable {
		System.out.println("Adding a bookstore schema");

		// copy over the bookstore schema to be used with the test
		File schemaFile = new File(Utils.decodeUrl(this.getClass().getResource("/schema/bookstore.xsd")));
		Utils.copyFile(schemaFile, new File(getBaseDir() + File.separator + tci.getDir() + File.separator + "schema"
			+ File.separator + tci.getName() + File.separator + "bookstore.xsd"));

		ServiceDescription introService = (ServiceDescription) Utils.deserializeDocument(getBaseDir() + File.separator
			+ tci.getDir() + File.separator + "introduce.xml", ServiceDescription.class);
		

		NamespaceType type = new NamespaceType();
		type.setLocation("." + File.separator + "bookstore.xsd");
		type.setNamespace("gme://projectmobius.org/1/BookStore");
		type.setPackageName("projectmobius.org");
		SchemaElementType etype = new SchemaElementType();
		etype.setType("Book");
		SchemaElementType[] etypeArr = new SchemaElementType[1];
		etypeArr[0] = etype;
		type.setSchemaElement(etypeArr);
		
		CommonTools.addNamespace(introService, type);
		

		Utils.serializeDocument(getBaseDir() + File.separator + tci.getDir() + File.separator + "introduce.xml",
			introService, IntroduceConstants.INTRODUCE_SKELETON_QNAME);
		
		
		try {
			SyncTools sync = new SyncTools(new File(getBaseDir() + File.separator + tci.getDir()));
			sync.sync();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		buildStep();

	}
}
