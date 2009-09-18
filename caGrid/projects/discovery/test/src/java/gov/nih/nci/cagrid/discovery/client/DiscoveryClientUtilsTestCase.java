package gov.nih.nci.cagrid.discovery.client;

import gov.nih.nci.cagrid.metadata.common.PointOfContact;
import junit.framework.TestCase;


public class DiscoveryClientUtilsTestCase extends TestCase {

    private PointOfContact nullPOC;
    private PointOfContact emptyPOC;
    private PointOfContact voidPOC;
    private PointOfContact emailPOC;
    private PointOfContact fullPOC;


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        nullPOC = null;
        emptyPOC = new PointOfContact();
        voidPOC = new PointOfContact("", "", "", "", "", "");
        emailPOC = new PointOfContact("", "emailV", "", "", "", "");
        fullPOC = new PointOfContact("affiliationV", "emailV", "firstNameV", "lastNameV", "phoneNumberV", "roleV");
    }


    public void testPredicateUtilNulls() {
        assertEquals("", DiscoveryClient.addNonNullPredicate("foo", "", false));
        assertEquals("", DiscoveryClient.addNonNullPredicate("foo", "  ", false));
        assertEquals("", DiscoveryClient.addNonNullPredicate("foo", "", true));
        assertEquals("", DiscoveryClient.addNonNullPredicate("foo", " ", true));
    }


    public void testPredicateUtil() {
        assertEquals(" and foo/text()='bar'", DiscoveryClient.addNonNullPredicate("foo", "bar", false));
        assertEquals(" and @foo='bar'", DiscoveryClient.addNonNullPredicate("foo", "bar", true));
    }


    public void testBuildPOCPredicateNulls() {
        assertEquals("true()", DiscoveryClient.buildPOCPredicate(nullPOC));
        assertEquals("true()", DiscoveryClient.buildPOCPredicate(emptyPOC));
        assertEquals("true()", DiscoveryClient.buildPOCPredicate(voidPOC));
    }


    public void testBuildPOCPredicate() {
        assertTrue(DiscoveryClient.buildPOCPredicate(emailPOC).contains("@email='emailV'"));
        assertTrue(DiscoveryClient.buildPOCPredicate(fullPOC).contains("@affiliation='affiliationV'"));
        assertTrue(DiscoveryClient.buildPOCPredicate(fullPOC).contains("@email='emailV'"));
        assertTrue(DiscoveryClient.buildPOCPredicate(fullPOC).contains("@firstName='firstNameV'"));
        assertTrue(DiscoveryClient.buildPOCPredicate(fullPOC).contains("@lastName='lastNameV'"));
        assertTrue(DiscoveryClient.buildPOCPredicate(fullPOC).contains("@phoneNumber='phoneNumberV'"));
        assertTrue(DiscoveryClient.buildPOCPredicate(fullPOC).contains("@role='roleV'"));
    }


    public static void main(String[] args) {
        junit.textui.TestRunner.run(DiscoveryClientUtilsTestCase.class);
    }
}
