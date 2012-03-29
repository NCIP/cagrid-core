package org.cagrid.data.sdkquery44.test;

import gov.nih.nci.cacoresdk.domain.onetomany.bidirectional.Computer;
import gov.nih.nci.cacoresdk.domain.onetomany.bidirectional.HardDrive;
import gov.nih.nci.cacoresdk.domain.onetoone.multipleassociation.Child;
import gov.nih.nci.cacoresdk.domain.onetoone.multipleassociation.Parent;
import gov.nih.nci.iso21090.Ad;
import gov.nih.nci.iso21090.AddressPartType;
import gov.nih.nci.iso21090.Adxp;
import gov.nih.nci.iso21090.Any;
import gov.nih.nci.iso21090.NullFlavor;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.cagrid.data.sdkquery44.translator.TypesInformationException;
import org.cagrid.data.sdkquery44.translator.cql2.ClassAssociation;
import org.cagrid.data.sdkquery44.translator.cql2.Cql2TypesInformationResolver;
import org.cagrid.data.sdkquery44.translator.cql2.HibernateConfigCql2TypesInformationResolver;
import org.hibernate.cfg.Configuration;

public class Cql2TypesInformationResolverTestCase extends TestCase {
    
    private Cql2TypesInformationResolver resolver = null;
    
    public Cql2TypesInformationResolverTestCase(String name) {
        super(name);
    }
    
    
    public void setUp() {
        InputStream is = getClass().getResourceAsStream("/hibernate.cfg.xml");
        Configuration config = new Configuration();
        config.addInputStream(is);
        config.buildMappings();
        config.configure();
        this.resolver = new HibernateConfigCql2TypesInformationResolver(config, true);
        try {
            is.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("Error closing hibernate configuration input stream: " + ex.getMessage());
        }
    }
    
    
    private void checkType(String className, String fieldName, Class<?> expected) {
        Class<?> type = null;
        try {
            type = resolver.getJavaDataType(className, fieldName);
        } catch (TypesInformationException e) {
            e.printStackTrace();
            fail("Error resolving types information: " + e.getMessage());
        }
        if (expected != null) {
            assertNotNull("No data type found", type);
        } else {
            assertNull("Datatype found when none was expected", type);
        }
        assertEquals("Unexpected datatype found", expected, type);
    }
    
    
    public void testAnyNullFlavorAttribute() {
        checkType(Any.class.getName(), "nullFlavor", NullFlavor.class);
    }
    
    
    public void testAdNullFlavorAttribute() {
        checkType(Ad.class.getName(), "nullFlavor", NullFlavor.class);
    }
    
    
    public void testAdxpAddressPartTypeAttribute() {
        checkType(Adxp.class.getName(), "type", AddressPartType.class);
    }
    
    
    public void testGetAssociationsToCollections() {
        String classname = Computer.class.getName();
        String associationClassname = HardDrive.class.getName();
        String endName = "hardDriveCollection";
        checkForAssociation(classname, associationClassname, endName);
    }
    
    
    public void testGetAssociationsToSingle() {
        String classname = Child.class.getName();
        String associationClassname = Parent.class.getName();
        checkForAssociation(classname, associationClassname, "mother");
        checkForAssociation(classname, associationClassname, "father");
    }
    
    
    private void checkForAssociation(String classname, String associationClassname, String endName) {
        try {
            List<ClassAssociation> associations = this.resolver.getAssociationsFromClass(classname);
            assertNotNull("No associations from " + classname + " were found", associations);
            assertTrue("No associations from " + classname + " were found", associations.size() != 0);
            boolean found = false;
            for (ClassAssociation ca : associations) {
                if (ca.getEndName().equals(endName) && ca.getClassName().equals(associationClassname)) {
                    found = true;
                }
            }
            assertTrue("Association from " + classname + " to " + associationClassname + " with end name " + endName + " not found", found);
        } catch (TypesInformationException e) {
            e.printStackTrace();
            fail("Error retrieving associations from class " + classname + ": " + e.getMessage());
        }
    }
    

    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(Cql2TypesInformationResolverTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}