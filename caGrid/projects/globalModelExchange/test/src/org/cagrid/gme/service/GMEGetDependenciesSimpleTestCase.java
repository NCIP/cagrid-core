package org.cagrid.gme.service;

import java.net.URI;

import org.cagrid.gme.domain.XMLSchema;
import org.cagrid.gme.domain.XMLSchemaBundle;
import org.cagrid.gme.domain.XMLSchemaImportInformation;
import org.cagrid.gme.domain.XMLSchemaNamespace;
import org.cagrid.gme.stubs.types.NoSuchNamespaceExistsFault;
import org.springframework.test.annotation.ExpectedException;


public class GMEGetDependenciesSimpleTestCase extends GMETestCaseWithSimpleModel {

    public void testAddAll() throws Exception {
        publishAllSchemas();

        assertSchemaImportsSchema(this.testSchemaSimpleA, this.testSchemaSimpleB);
        assertNotImported(this.testSchemaSimpleA);
        assertSchemaImportsSchema(this.testSchemaSimpleB, this.testSchemaSimpleC);
        assertNoImports(this.testSchemaSimpleC);
        assertSchemaImportsSchema(this.testSchemaSimpleD, this.testSchemaSimpleB);
        assertNotImported(this.testSchemaSimpleD);
        assertSchemaImportsSchema(this.testSchemaSimpleD, this.testSchemaSimpleE);
        assertNoImports(this.testSchemaSimpleE);
        assertNoImports(this.testSchemaSimpleF);
        assertNotImported(this.testSchemaSimpleF);

    }


    @ExpectedException(NoSuchNamespaceExistsFault.class)
    public void testInvalidSchemaImports() throws Exception {
        publishAllSchemas();

        this.gme.getImportedNamespaces(new URI("http://invalid"));
    }


    @ExpectedException(NoSuchNamespaceExistsFault.class)
    public void testInvalidSchemaImported() throws Exception {
        publishAllSchemas();

        this.gme.getImportingNamespaces(new URI("http://invalid"));
    }


    @ExpectedException(NoSuchNamespaceExistsFault.class)
    public void testNullSchemaImports() throws Exception {
        publishAllSchemas();

        this.gme.getImportedNamespaces(null);
    }


    @ExpectedException(NoSuchNamespaceExistsFault.class)
    public void testNullSchemaImported() throws Exception {
        publishAllSchemas();

        this.gme.getImportingNamespaces(null);
    }


    @ExpectedException(NoSuchNamespaceExistsFault.class)
    public void testNullBundle() throws Exception {
        publishAllSchemas();

        this.gme.getSchemBundle(null);
    }


    @ExpectedException(NoSuchNamespaceExistsFault.class)
    public void testInvalidBundle() throws Exception {
        publishAllSchemas();

        this.gme.getSchemBundle(new URI("http://invalid"));
    }


    public void testBundle() throws Exception {
        publishAllSchemas();

        // A: contains(A,B,C) imports(A->B->C)
        {
            XMLSchemaBundle aSchemaBundle = this.gme.getSchemBundle(this.testSchemaSimpleA.getTargetNamespace());
            assertEquals(2, aSchemaBundle.getImportInformation().size());
            assertEquals(3, aSchemaBundle.getXMLSchemas().size());

            XMLSchemaNamespace aTargetNamespace = new XMLSchemaNamespace(this.testSchemaSimpleA.getTargetNamespace());
            XMLSchemaNamespace bTargetNamespace = new XMLSchemaNamespace(this.testSchemaSimpleB.getTargetNamespace());
            XMLSchemaNamespace cTargetNamespace = new XMLSchemaNamespace(this.testSchemaSimpleC.getTargetNamespace());

            XMLSchema aFromBundle = aSchemaBundle.getXMLSchemaForTargetNamespace(aTargetNamespace);
            assertEquals(this.testSchemaSimpleA, aFromBundle);
            XMLSchema bFromBundle = aSchemaBundle.getXMLSchemaForTargetNamespace(bTargetNamespace);
            assertEquals(this.testSchemaSimpleB, bFromBundle);
            XMLSchema cFromBundle = aSchemaBundle.getXMLSchemaForTargetNamespace(cTargetNamespace);
            assertEquals(this.testSchemaSimpleC, cFromBundle);

            XMLSchemaImportInformation aII = aSchemaBundle.getImportInformationForTargetNamespace(aTargetNamespace);
            aII.getTargetNamespace().equals(aTargetNamespace);
            assertEquals(1, aII.getImports().size());
            assertTrue(aII.getImports().contains(bTargetNamespace));

            XMLSchemaImportInformation bII = aSchemaBundle.getImportInformationForTargetNamespace(bTargetNamespace);
            bII.getTargetNamespace().equals(bTargetNamespace);
            assertEquals(1, bII.getImports().size());
            assertTrue(bII.getImports().contains(cTargetNamespace));

            XMLSchemaImportInformation cII = aSchemaBundle.getImportInformationForTargetNamespace(cTargetNamespace);
            assertNull(cII);
        }

        // B: contains(B,C) imports(B->C)
        {
            XMLSchemaBundle bSchemaBundle = this.gme.getSchemBundle(this.testSchemaSimpleB.getTargetNamespace());
            assertEquals(1, bSchemaBundle.getImportInformation().size());
            assertEquals(2, bSchemaBundle.getXMLSchemas().size());

            XMLSchemaNamespace bTargetNamespace = new XMLSchemaNamespace(this.testSchemaSimpleB.getTargetNamespace());
            XMLSchemaNamespace cTargetNamespace = new XMLSchemaNamespace(this.testSchemaSimpleC.getTargetNamespace());

            XMLSchema bFromBundle = bSchemaBundle.getXMLSchemaForTargetNamespace(bTargetNamespace);
            assertEquals(this.testSchemaSimpleB, bFromBundle);
            XMLSchema cFromBundle = bSchemaBundle.getXMLSchemaForTargetNamespace(cTargetNamespace);
            assertEquals(this.testSchemaSimpleC, cFromBundle);

            XMLSchemaImportInformation bII = bSchemaBundle.getImportInformationForTargetNamespace(bTargetNamespace);
            bII.getTargetNamespace().equals(bTargetNamespace);
            assertEquals(1, bII.getImports().size());
            assertTrue(bII.getImports().contains(cTargetNamespace));

            XMLSchemaImportInformation cII = bSchemaBundle.getImportInformationForTargetNamespace(cTargetNamespace);
            assertNull(cII);
        }

        // C: contains(C) imports()
        {
            XMLSchemaBundle cSchemaBundle = this.gme.getSchemBundle(this.testSchemaSimpleC.getTargetNamespace());
            assertEquals(0, cSchemaBundle.getImportInformation().size());
            assertEquals(1, cSchemaBundle.getXMLSchemas().size());

            XMLSchemaNamespace cTargetNamespace = new XMLSchemaNamespace(this.testSchemaSimpleC.getTargetNamespace());

            XMLSchema cFromBundle = cSchemaBundle.getXMLSchemaForTargetNamespace(cTargetNamespace);
            assertEquals(this.testSchemaSimpleC, cFromBundle);

            XMLSchemaImportInformation cII = cSchemaBundle.getImportInformationForTargetNamespace(cTargetNamespace);
            assertNull(cII);
        }

        // D: contains(B,C,D,E) imports(D->((B->C),E))
        {
            XMLSchemaBundle dSchemaBundle = this.gme.getSchemBundle(this.testSchemaSimpleD.getTargetNamespace());
            assertEquals(2, dSchemaBundle.getImportInformation().size());
            assertEquals(4, dSchemaBundle.getXMLSchemas().size());

            XMLSchemaNamespace bTargetNamespace = new XMLSchemaNamespace(this.testSchemaSimpleB.getTargetNamespace());
            XMLSchemaNamespace cTargetNamespace = new XMLSchemaNamespace(this.testSchemaSimpleC.getTargetNamespace());
            XMLSchemaNamespace dTargetNamespace = new XMLSchemaNamespace(this.testSchemaSimpleD.getTargetNamespace());
            XMLSchemaNamespace eTargetNamespace = new XMLSchemaNamespace(this.testSchemaSimpleE.getTargetNamespace());

            XMLSchema bFromBundle = dSchemaBundle.getXMLSchemaForTargetNamespace(bTargetNamespace);
            assertEquals(this.testSchemaSimpleB, bFromBundle);
            XMLSchema cFromBundle = dSchemaBundle.getXMLSchemaForTargetNamespace(cTargetNamespace);
            assertEquals(this.testSchemaSimpleC, cFromBundle);
            XMLSchema dFromBundle = dSchemaBundle.getXMLSchemaForTargetNamespace(dTargetNamespace);
            assertEquals(this.testSchemaSimpleD, dFromBundle);
            XMLSchema eFromBundle = dSchemaBundle.getXMLSchemaForTargetNamespace(eTargetNamespace);
            assertEquals(this.testSchemaSimpleE, eFromBundle);

            XMLSchemaImportInformation dII = dSchemaBundle.getImportInformationForTargetNamespace(dTargetNamespace);
            dII.getTargetNamespace().equals(dTargetNamespace);
            assertEquals(2, dII.getImports().size());
            assertTrue(dII.getImports().contains(bTargetNamespace));
            assertTrue(dII.getImports().contains(eTargetNamespace));

            XMLSchemaImportInformation bII = dSchemaBundle.getImportInformationForTargetNamespace(bTargetNamespace);
            bII.getTargetNamespace().equals(bTargetNamespace);
            assertEquals(1, bII.getImports().size());
            assertTrue(bII.getImports().contains(cTargetNamespace));

            XMLSchemaImportInformation cII = dSchemaBundle.getImportInformationForTargetNamespace(cTargetNamespace);
            assertNull(cII);

            XMLSchemaImportInformation eII = dSchemaBundle.getImportInformationForTargetNamespace(eTargetNamespace);
            assertNull(eII);
        }

        // E: contains(E) imports()
        {
            XMLSchemaBundle eSchemaBundle = this.gme.getSchemBundle(this.testSchemaSimpleE.getTargetNamespace());
            assertEquals(0, eSchemaBundle.getImportInformation().size());
            assertEquals(1, eSchemaBundle.getXMLSchemas().size());

            XMLSchemaNamespace eTargetNamespace = new XMLSchemaNamespace(this.testSchemaSimpleE.getTargetNamespace());

            XMLSchema eFromBundle = eSchemaBundle.getXMLSchemaForTargetNamespace(eTargetNamespace);
            assertEquals(this.testSchemaSimpleE, eFromBundle);

            XMLSchemaImportInformation eII = eSchemaBundle.getImportInformationForTargetNamespace(eTargetNamespace);
            assertNull(eII);
        }
    }
}
