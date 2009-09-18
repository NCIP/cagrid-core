package gov.nih.nci.cagrid.data.utilities;

import gov.nih.nci.cagrid.data.utilities.DomainModelUtils.CDE;
import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.common.UMLClass;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;

import java.io.FileReader;
import java.util.Map;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


public class DomainModelUtilsTestCase extends TestCase {
    public static final String DOMAIN_MODEL_FILE = "test/resources/domainModel.xml";
    public static final String PIR_DOMAIN_MODEL_FILE = "test/resources/PIR_1.2_DomainModel.xml";


    public void testGetUMLClassForConcept() {
        UMLClass[] classForConcept = DomainModelUtils.getUMLClassForConcept(getDomainModel(DOMAIN_MODEL_FILE),
            "C42614", true);
        assertNotNull(classForConcept);
        assertEquals(1, classForConcept.length);
        assertEquals("ProteinAlias", classForConcept[0].getClassName());

        classForConcept = DomainModelUtils.getUMLClassForConcept(getDomainModel(DOMAIN_MODEL_FILE), "C42614", false);
        assertNotNull(classForConcept);
        assertEquals(1, classForConcept.length);
        assertEquals("ProteinAlias", classForConcept[0].getClassName());

        classForConcept = DomainModelUtils.getUMLClassForConcept(getDomainModel(DOMAIN_MODEL_FILE), "C16612", false);
        assertNotNull(classForConcept);
        assertEquals(1, classForConcept.length);
        assertEquals("Gene", classForConcept[0].getClassName());

        classForConcept = DomainModelUtils.getUMLClassForConcept(getDomainModel(DOMAIN_MODEL_FILE), "C45377", true);
        assertNotNull(classForConcept);
        assertEquals(2, classForConcept.length);
        String[] classNames = new String[classForConcept.length];
        int index = 0;
        for (UMLClass clazz : classForConcept) {
            classNames[index] = clazz.getClassName();
            index++;
        }
        assertTrue(arrayContains(classNames, "GeneRelativeLocation"));
        assertTrue(arrayContains(classNames, "CloneRelativeLocation"));

    }


    public void testGetUMLClassForConcept_Primary() {
        UMLClass[] classForConcept;
        classForConcept = DomainModelUtils.getUMLClassForConcept(getDomainModel(DOMAIN_MODEL_FILE), "C37925", false);
        assertNotNull(classForConcept);
        assertEquals(1, classForConcept.length);
        assertEquals("CloneRelativeLocation", classForConcept[0].getClassName());

        classForConcept = DomainModelUtils.getUMLClassForConcept(getDomainModel(DOMAIN_MODEL_FILE), "C37925", true);
        assertEquals(0, classForConcept.length);
    }


    public void testMapCommonDataElements() {
        DomainModel domainModel1 = getDomainModel(DOMAIN_MODEL_FILE);
        DomainModel domainModel2 = getDomainModel(PIR_DOMAIN_MODEL_FILE);
        Map<CDE, CDE> map = DomainModelUtils.mapCommonDataElements(domainModel1, domainModel2);
        assertNotNull(map);
        assertEquals(4, map.size());

        System.out.println(domainModel1.getProjectShortName() + "==>" + domainModel2.getProjectShortName());
        System.out.println("====================");
        for (CDE cde : map.keySet()) {
            CDE cde2 = map.get(cde);
            System.out.println(cde.getUmlClass().getClassName() + "." + cde.getUmlAttribute().getName() + " ==> "
                + cde2.getUmlClass().getClassName() + "." + cde2.getUmlAttribute().getName());

            assertEquals(cde.getIdentifier(), cde2.getIdentifier());
            assertEquals(cde.getUmlAttribute().getPublicID(), cde2.getUmlAttribute().getPublicID());
            assertEquals(Float.valueOf(cde.getUmlAttribute().getVersion()),
                Float.valueOf(cde2.getUmlAttribute().getVersion()));
        }
    }


    private DomainModel getDomainModel(String filename) {
        DomainModel domainModel = null;
        try {
            FileReader domainModelReader = new FileReader(filename);
            domainModel = MetadataUtils.deserializeDomainModel(domainModelReader);
            domainModelReader.close();
            assertNotNull(domainModel);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Could not load domain model");
        }

        return domainModel;
    }
    
    
    private <T> boolean arrayContains(T[] array, T item) {
        for (T arrayItem : array) {
            if (arrayItem == item ||
                (arrayItem != null && arrayItem.equals(item))) {
                return true;
            }
        }
        return false;
    }


    public static void main(String args[]) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(DomainModelUtilsTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
