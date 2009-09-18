package gov.nih.nci.cagrid.introduce.test.unit;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.method.MethodsType;
import gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertiesListType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

public class TemplateUtilsTest extends TestCase {
	public static String GOLD_DIRECTORY = File.separator + "test" + File.separator + "resources" + File.separator + "gold" + File.separator;

	public static String GOLD_FILE = "versions" + File.separator + "introduce_Example.xml";

	private ServiceDescription info = null;

	private ResourcePropertiesListType metadataList = null;

	private MethodsType methods = null;


	public void setUp() {
		String pathtobasedir = System.getProperty("basedir", ".");
		try {
			info = (ServiceDescription) Utils.deserializeDocument(
					pathtobasedir + GOLD_DIRECTORY + GOLD_FILE,
					ServiceDescription.class);

			metadataList = info.getServices().getService(0).getResourcePropertiesList();

			methods = info.getServices().getService(0).getMethods();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error in setup:" + e.getMessage());
		}

		assertNotNull(metadataList);
		assertNotNull(metadataList.getResourceProperty());
		assertNotNull(methods);
		assertNotNull(methods.getMethod());
	}

	public void testGetResourcePropertyVariableName() {
		// make sure the pattern is right
		String computedVarName1 = CommonTools
				.getResourcePropertyVariableName(metadataList, 0);
		assertNotNull(computedVarName1);
		assertTrue(computedVarName1.matches("([a-z])+([a-zA-Z])*"));

		// make sure the name is uniq when only the name space is different
		String computedVarName2 = CommonTools
				.getResourcePropertyVariableName(metadataList, 1);
		assertNotNull(computedVarName2);
		assertFalse(computedVarName1.equals(computedVarName2));
		assertTrue(computedVarName2.matches("([a-z])+([a-zA-Z])*[1-9]+"));

		// store all the names in a set to check for uniqueness
		Set names = new HashSet();
		for (int i = 0; i < metadataList.getResourceProperty().length; i++) {
			names.add(CommonTools.getResourcePropertyVariableName(
					metadataList, i));
		}
		// makes sure we got a unique name for all items
		assertEquals(names.size(), metadataList.getResourceProperty().length);
	}

	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(TemplateUtilsTest.class);
	}
}
