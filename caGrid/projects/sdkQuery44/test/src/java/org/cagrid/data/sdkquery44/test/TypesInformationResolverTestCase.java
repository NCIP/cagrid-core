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
package org.cagrid.data.sdkquery44.test;

import gov.nih.nci.iso21090.Ad;
import gov.nih.nci.iso21090.AddressPartType;
import gov.nih.nci.iso21090.Adxp;
import gov.nih.nci.iso21090.Any;
import gov.nih.nci.iso21090.NullFlavor;

import java.io.IOException;
import java.io.InputStream;

import org.cagrid.data.sdkquery44.translator.HibernateConfigTypesInformationResolver;
import org.cagrid.data.sdkquery44.translator.TypesInformationException;
import org.cagrid.data.sdkquery44.translator.TypesInformationResolver;
import org.hibernate.cfg.Configuration;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class TypesInformationResolverTestCase extends TestCase {
    
    private TypesInformationResolver resolver = null;
    
    public TypesInformationResolverTestCase(String name) {
        super(name);
    }
    
    
    public void setUp() {
        InputStream is = getClass().getResourceAsStream("/hibernate.cfg.xml");
        Configuration config = new Configuration();
        config.addInputStream(is);
        config.buildMappings();
        config.configure();
        this.resolver = new HibernateConfigTypesInformationResolver(config, true);
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
    

    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(TypesInformationResolverTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
