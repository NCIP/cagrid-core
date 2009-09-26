package org.cagrid.gaards.csm.service;

import gov.nih.nci.cagrid.common.FaultUtil;

import java.math.BigInteger;
import java.util.List;

import junit.framework.TestCase;

import org.cagrid.gaards.csm.bean.Application;
import org.cagrid.gaards.csm.bean.ApplicationSearchCriteria;
import org.cagrid.gaards.csm.bean.ProtectionElement;
import org.cagrid.gaards.csm.bean.ProtectionElementSearchCriteria;
import org.cagrid.gaards.csm.stubs.types.AccessDeniedFault;
import org.cagrid.gaards.csm.stubs.types.CSMTransactionFault;


public class TestCSM extends TestCase {

    private static String SUPER_ADMIN = "/C=US/O=abc/OU=xyz/OU=caGrid/CN=superadmin";
    private static String GENERAL_USER = "/C=US/O=abc/OU=xyz/OU=caGrid/CN=jdoe";


    public void testCSMInitialization() {

        try {
            CSM csm = new CSM(Utils.getCSMProperties());
            findApplications(csm, null, null, 1);
            List<Application> apps = findApplications(csm, null, Constants.CSM_WEB_SERVICE_CONTEXT, 1);
            assertEquals(1, apps.size());
            assertEquals(Constants.CSM_WEB_SERVICE_CONTEXT, apps.get(0).getName());
            assertEquals(Constants.CSM_WEB_SERVICE_DESCRIPTION, apps.get(0).getDescription());
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(gov.nih.nci.cagrid.common.Utils.getExceptionMessage(e));
        } finally {
            try {

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testCreateAndDeleteApplication() {

        try {
            CSM csm = new CSM(Utils.getCSMProperties());
            findApplications(csm, null, null, 1);
            List<Application> apps = findApplications(csm, null, Constants.CSM_WEB_SERVICE_CONTEXT, 1);
            assertEquals(1, apps.size());
            assertEquals(Constants.CSM_WEB_SERVICE_CONTEXT, apps.get(0).getName());
            assertEquals(Constants.CSM_WEB_SERVICE_DESCRIPTION, apps.get(0).getDescription());
            CSMInitializer.addWebServiceAdmin(csm.getAuthorizationManager(), SUPER_ADMIN);

            Application a = new Application();
            a.setName("myapp");
            a.setDescription("This is my application!!!");

            // Test that a non admin cannot add an application
            try {
                csm.createApplication(GENERAL_USER, a);
                fail("Only CSM Web Service admins should be able to add applications");
            } catch (AccessDeniedFault e) {

            }

            Application result = csm.createApplication(SUPER_ADMIN, a);
            assertNotNull(result.getId());
            assertEquals(a.getName(), result.getName());
            assertEquals(a.getDescription(), result.getDescription());
            findApplications(csm, null, null, 2);
            List<Application> myapps = findApplications(csm, null, a.getName(), 1);
            assertEquals(result, myapps.get(0));

            // Test that a non admin cannot remove an application
            try {
                csm.removeApplication(GENERAL_USER, result.getId().longValue());
                fail("Only CSM Web Service admins should be able to remove applications");
            } catch (AccessDeniedFault e) {

            }

            csm.removeApplication(SUPER_ADMIN, result.getId().longValue());
            findApplications(csm, null, null, 1);
            findApplications(csm, null, a.getName(), 0);
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(gov.nih.nci.cagrid.common.Utils.getExceptionMessage(e));
        } finally {
            try {

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testModifyProtectionElement() {

        try {
            CSM csm = new CSM(Utils.getCSMProperties());
            CSMInitializer.addWebServiceAdmin(csm.getAuthorizationManager(), SUPER_ADMIN);
            String appName1 = "myapp1";
            String user1 = GENERAL_USER + "1";
            Application a1 = createApplication(csm, appName1);
            CSMInitializer.addApplicationAdmin(csm.getAuthorizationManager(), a1.getName(), user1);

            String protectionElementName = "my protection element";
            ProtectionElement e1 = getProtectionElement(a1.getId(), protectionElementName);
            ProtectionElement pe1 = csm.createProtectionElement(user1, e1);
            compareProtectionElements(e1, pe1);

            ProtectionElementSearchCriteria s1 = getProtectionElementSearchCriteria(a1.getId(), protectionElementName);
            List<ProtectionElement> r1 = csm.getProtectionElements(user1, s1);
            assertEquals(1, r1.size());
            assertEquals(pe1, r1.get(0));

            // Test modify a protection element without an id
            try {
                csm.modifyProtectionElement(user1, e1);
                fail("Should not be able to modify a protection element without specifying an id");
            } catch (CSMTransactionFault e) {
                // TODO: handle exception
            }

            // Test modify a protection element with the wrong application id
            BigInteger id = pe1.getApplicationId();
            pe1.setApplicationId(new BigInteger("5"));

            try {
                csm.modifyProtectionElement(user1, pe1);
                fail("Should not be able to modify the application a protection element is associated with.");
            } catch (CSMTransactionFault e) {
                // TODO: handle exception
            }
            pe1.setApplicationId(id);

            try {
                csm.modifyProtectionElement(GENERAL_USER, pe1);
                fail("Only admins should be able to modify protection elements");
            } catch (AccessDeniedFault e) {

            }
            
            pe1.setAttribute("Updated Attribute");
            pe1.setAttributeValue("Updated Attribute Value");
            pe1.setDescription("Updated Description");
            pe1.setName("Updated Name");
            pe1.setObjectId("Updated Object Id");
            pe1.setType("Updated Type");
            
            ProtectionElement updated = csm.modifyProtectionElement(user1, pe1);
            compareProtectionElements(pe1, updated);
          
            csm.removeProtectionElement(user1, a1.getId().longValue(), pe1.getId().longValue());
            r1 = csm.getProtectionElements(user1, s1);
            assertEquals(0, r1.size());

        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(gov.nih.nci.cagrid.common.Utils.getExceptionMessage(e));
        } finally {
            try {

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testCreateAndDeleteProtectionElement() {

        try {
            CSM csm = new CSM(Utils.getCSMProperties());
            CSMInitializer.addWebServiceAdmin(csm.getAuthorizationManager(), SUPER_ADMIN);
            String appName1 = "myapp1";
            String user1 = GENERAL_USER + "1";
            Application a1 = createApplication(csm, appName1);
            CSMInitializer.addApplicationAdmin(csm.getAuthorizationManager(), a1.getName(), user1);

            String appName2 = "myapp2";
            String user2 = GENERAL_USER + "2";
            Application a2 = createApplication(csm, appName2);
            CSMInitializer.addApplicationAdmin(csm.getAuthorizationManager(), a2.getName(), user2);

            String protectionElementName = "my protection element";
            ProtectionElement e1 = getProtectionElement(a1.getId(), protectionElementName);
            // Test that a non admin cannot create protection element
            try {
                csm.createProtectionElement(user2, e1);
                fail("Only admins should be able to create protection elements");
            } catch (AccessDeniedFault e) {

            }

            ProtectionElement pe1 = csm.createProtectionElement(user1, e1);
            compareProtectionElements(e1, pe1);

            ProtectionElement e2 = getProtectionElement(a2.getId(), protectionElementName);
            // Test that a non admin cannot create protection element
            try {
                csm.createProtectionElement(user1, e2);
                fail("Only admins should be able to create protection elements");
            } catch (AccessDeniedFault e) {

            }

            ProtectionElement pe2 = csm.createProtectionElement(user2, e2);
            compareProtectionElements(e2, pe2);

            ProtectionElementSearchCriteria s1 = getProtectionElementSearchCriteria(a1.getId(), protectionElementName);
            // Test that a non admin cannot search for protection elements
            try {
                csm.getProtectionElements(user2, s1);
                fail("Only admins should be able to search for protection elements");
            } catch (AccessDeniedFault e) {

            }

            List<ProtectionElement> r1 = csm.getProtectionElements(user1, s1);
            assertEquals(1, r1.size());
            assertEquals(pe1, r1.get(0));

            ProtectionElementSearchCriteria s2 = getProtectionElementSearchCriteria(a2.getId(), protectionElementName);
            // Test that a non admin cannot search for protection elements
            try {
                csm.getProtectionElements(user1, s2);
                fail("Only admins should be able to search for protection elements");
            } catch (AccessDeniedFault e) {

            }

            List<ProtectionElement> r2 = csm.getProtectionElements(user2, s2);
            assertEquals(1, r2.size());
            assertEquals(pe2, r2.get(0));

            try {
                csm.removeProtectionElement(user2, a1.getId().longValue(), pe1.getId().longValue());
                fail("Only admins should be able to remove protection elements");
            } catch (AccessDeniedFault e) {

            }

            try {
                csm.removeProtectionElement(user1, a2.getId().longValue(), pe1.getId().longValue());
                fail("Only admins should be able to remove protection elements");
            } catch (AccessDeniedFault e) {

            }
            csm.removeProtectionElement(user1, a1.getId().longValue(), pe1.getId().longValue());
            r1 = csm.getProtectionElements(user1, s1);
            assertEquals(0, r1.size());

            try {
                csm.removeProtectionElement(user1, a2.getId().longValue(), pe2.getId().longValue());
                fail("Only admins should be able to remove protection elements");
            } catch (AccessDeniedFault e) {

            }

            try {
                csm.removeProtectionElement(user2, a1.getId().longValue(), pe2.getId().longValue());
                fail("Only admins should be able to remove protection elements");
            } catch (AccessDeniedFault e) {

            }
            csm.removeProtectionElement(user2, a2.getId().longValue(), pe2.getId().longValue());
            r2 = csm.getProtectionElements(user2, s2);
            assertEquals(0, r2.size());

            // Test that a super admin, can create search for, and remove a
            // protection element.
            ProtectionElement ea = getProtectionElement(a1.getId(), "admin protection element");
            ProtectionElement pea = csm.createProtectionElement(SUPER_ADMIN, ea);
            compareProtectionElements(ea, pea);
            ProtectionElementSearchCriteria sa = getProtectionElementSearchCriteria(a1.getId(), ea.getName());
            List<ProtectionElement> ra = csm.getProtectionElements(SUPER_ADMIN, sa);
            assertEquals(1, ra.size());
            assertEquals(pea, ra.get(0));

            try {
                csm.removeProtectionElement(SUPER_ADMIN, a2.getId().longValue(), pea.getId().longValue());
                fail("Only admins should be able to remove protection elements");
            } catch (AccessDeniedFault e) {

            }
       
            csm.removeProtectionElement(SUPER_ADMIN, a1.getId().longValue(), pea.getId().longValue());
            ra = csm.getProtectionElements(SUPER_ADMIN, sa);
            assertEquals(0, ra.size());

        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(gov.nih.nci.cagrid.common.Utils.getExceptionMessage(e));
        } finally {
            try {

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void compareProtectionElements(ProtectionElement e1, ProtectionElement e2) {
        assertEquals(gov.nih.nci.cagrid.common.Utils.clean(e1.getName()), gov.nih.nci.cagrid.common.Utils.clean(e2
            .getName()));
        assertEquals(e1.getApplicationId(), e2.getApplicationId());
        assertEquals(gov.nih.nci.cagrid.common.Utils.clean(e1.getAttribute()), gov.nih.nci.cagrid.common.Utils.clean(e2
            .getAttribute()));
        assertEquals(gov.nih.nci.cagrid.common.Utils.clean(e1.getAttributeValue()), gov.nih.nci.cagrid.common.Utils
            .clean(e2.getAttributeValue()));
        assertEquals(gov.nih.nci.cagrid.common.Utils.clean(e1.getDescription()), gov.nih.nci.cagrid.common.Utils
            .clean(e2.getDescription()));
        assertEquals(gov.nih.nci.cagrid.common.Utils.clean(e1.getObjectId()), gov.nih.nci.cagrid.common.Utils.clean(e2
            .getObjectId()));
        assertEquals(gov.nih.nci.cagrid.common.Utils.clean(e1.getType()), gov.nih.nci.cagrid.common.Utils.clean(e2
            .getType()));
    }


    private ProtectionElementSearchCriteria getProtectionElementSearchCriteria(BigInteger applicationId, String name) {
        ProtectionElementSearchCriteria pe = new ProtectionElementSearchCriteria();
        pe.setApplicationId(applicationId);
        pe.setName(name);
        return pe;
    }


    private ProtectionElement getProtectionElement(BigInteger applicationId, String name) {
        ProtectionElement pe = new ProtectionElement();
        pe.setApplicationId(applicationId);
        pe.setName(name);
        pe.setObjectId(name);
        pe.setDescription("Protection Element " + name + ".");
        return pe;
    }


    private Application createApplication(CSM csm, String name) throws Exception {
        Application a = new Application();
        a.setName(name);
        a.setDescription("Application " + name + ".");
        Application result = csm.createApplication(SUPER_ADMIN, a);
        assertNotNull(result.getId());
        assertEquals(a.getName(), result.getName());
        assertEquals(a.getDescription(), result.getDescription());
        return result;
    }


    private List<Application> findApplications(CSM csm, BigInteger id, String name, int expectedResult)
        throws Exception {
        ApplicationSearchCriteria search = new ApplicationSearchCriteria();
        search.setId(id);
        search.setName(name);
        List<Application> apps = csm.getApplications(search);
        assertEquals(expectedResult, apps.size());
        return apps;
    }


    protected void setUp() throws Exception {
        super.setUp();
        Utils.initializeDatabase();
    }
}
