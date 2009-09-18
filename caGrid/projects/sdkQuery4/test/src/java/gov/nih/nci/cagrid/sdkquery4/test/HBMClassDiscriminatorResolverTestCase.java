package gov.nih.nci.cagrid.sdkquery4.test;

import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.sdkquery4.processor.HBMClassDiscriminatorResolver;

import java.io.FileReader;

import junit.framework.TestCase;

public class HBMClassDiscriminatorResolverTestCase extends TestCase {
    
    public static final String DOMAIN_MODEL_FILENAME = "test/resources/exampleDomainModel.xml";

    private HBMClassDiscriminatorResolver resolver = null;
    
    public HBMClassDiscriminatorResolverTestCase(String name) {
        super(name);
    }
    
    
    public void setUp() {
        try {
            FileReader reader = new FileReader(DOMAIN_MODEL_FILENAME);
            DomainModel model = MetadataUtils.deserializeDomainModel(reader);
            reader.close();
            resolver = new HBMClassDiscriminatorResolver(model);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }
    
    
    public void testTablePerClassBaseClass() {
        String className = "gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.Display";
        Object discriminator = null;
        try {
            discriminator = resolver.getClassDiscriminatorValue(className);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error determining class discriminator");
        }
        assertTrue("Discriminator value was not an instance of Integer (was " 
            + discriminator.getClass().getName() + ")", discriminator instanceof Integer);
        assertEquals("Base class discriminator value did not match expected", Integer.valueOf(0), discriminator);
    }
    
    
    public void testTablePerClassSubClass() {
        String className = "gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.LCDMonitor";
        Object discriminator = null;
        try {
            discriminator = resolver.getClassDiscriminatorValue(className);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error determining class discriminator");
        }
        assertTrue("Discriminator value was not an instance of Integer (was " 
            + discriminator.getClass().getName() + ")", discriminator instanceof Integer);
        assertEquals("Base class discriminator value did not match expected", Integer.valueOf(3), discriminator);
    }
    
    
    public void testTablePerHierarchyBaseClass() {
        String className = "gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.sametable.Goverment"; // (sic)
        Object discriminator = null;
        try {
            discriminator = resolver.getClassDiscriminatorValue(className);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error determining class discriminator");
        }
        assertTrue("Discriminator value was not an instance of String (was " 
            + discriminator.getClass().getName() + ")", discriminator instanceof String);
        assertEquals("Base class discriminator value did not match expected", "Goverment", discriminator);
    }
    
    
    public void testTablePerHierarchySubClass() {
        String className = "gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.sametable.PresidentialGovt";
        Object discriminator = null;
        try {
            discriminator = resolver.getClassDiscriminatorValue(className);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error determining class discriminator");
        }
        assertTrue("Discriminator value was not an instance of String (was " 
            + discriminator.getClass().getName() + ")", discriminator instanceof String);
        assertEquals("Base class discriminator value did not match expected", "PresidentialGovt", discriminator);
    }
}
