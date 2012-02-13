package gov.nih.nci.cagrid.common;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

public class VersionNumberTestCase {
    
    public static final String SIMPLE_VERSION = "1";
    public static final String MINOR_VERSION = "1.2";
    public static final String REVISION_VERSION = "1.2.3";
    public static final String MAINTENANCE_VERSION = "1.2.3.4";
    public static final String VERSION_WITH_EVERYTHING = "1.2.3.4-rc2";
    
    public static VersionNumber simpleVersion = null;
    public static VersionNumber minorVersion = null;
    public static VersionNumber revisionVersion = null;
    public static VersionNumber maintenanceVersion = null;
    public static VersionNumber fullVersion = null;
    
    @BeforeClass
    public static void initialize() {
        simpleVersion = new VersionNumber(SIMPLE_VERSION);
        minorVersion = new VersionNumber(MINOR_VERSION);
        revisionVersion = new VersionNumber(REVISION_VERSION);
        maintenanceVersion = new VersionNumber(MAINTENANCE_VERSION);
        fullVersion = new VersionNumber(VERSION_WITH_EVERYTHING);
    }
    

    @Test
    public void testGetMajorVersion() {
        assertEquals("1", simpleVersion.getMajorVersion());
        assertEquals("1", minorVersion.getMajorVersion());
        assertEquals("1", revisionVersion.getMajorVersion());
        assertEquals("1", maintenanceVersion.getMajorVersion());
        assertEquals("1", fullVersion.getMajorVersion());
    }


    @Test
    public void testGetMinorVersion() {
        assertEquals(null, simpleVersion.getMinorVersion());
        assertEquals("2", minorVersion.getMinorVersion());
        assertEquals("2", revisionVersion.getMinorVersion());
        assertEquals("2", maintenanceVersion.getMinorVersion());
        assertEquals("2", fullVersion.getMinorVersion());
    }


    @Test
    public void testGetRevision() {
        assertEquals(null, simpleVersion.getRevision());
        assertEquals(null, minorVersion.getRevision());
        assertEquals("3", revisionVersion.getRevision());
        assertEquals("3", maintenanceVersion.getRevision());
        assertEquals("3", fullVersion.getRevision());
    }


    @Test
    public void testGetMaintenanceVersion() {
        assertEquals(null, simpleVersion.getMaintenanceVersion());
        assertEquals(null, minorVersion.getMaintenanceVersion());
        assertEquals(null, revisionVersion.getMaintenanceVersion());
        assertEquals("4", maintenanceVersion.getMaintenanceVersion());
        assertEquals("4", fullVersion.getMaintenanceVersion());
    }


    @Test
    public void testGetSuffix() {
        assertEquals(null, simpleVersion.getSuffix());
        assertEquals(null, minorVersion.getSuffix());
        assertEquals(null, revisionVersion.getSuffix());
        assertEquals(null, maintenanceVersion.getSuffix());
        assertEquals("rc2", fullVersion.getSuffix());
    }
    
    
    @Test
    public void testGetCaGridVersion() {
        VersionNumber caGridVersion = VersionNumber.getCaGridVersion();
        String ver = caGridVersion.getOriginalVersionString();
        assertNotNull(ver);
        assertEquals("1.4.1", ver);
    }
}
