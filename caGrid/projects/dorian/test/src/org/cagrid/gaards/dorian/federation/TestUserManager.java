package org.cagrid.gaards.dorian.federation;

import gov.nih.nci.cagrid.common.FaultUtil;

import java.security.cert.X509Certificate;

import junit.framework.TestCase;

import org.cagrid.gaards.dorian.ca.CertificateAuthority;
import org.cagrid.gaards.dorian.service.PropertyManager;
import org.cagrid.gaards.dorian.stubs.types.InvalidUserFault;
import org.cagrid.gaards.dorian.test.CA;
import org.cagrid.gaards.dorian.test.Utils;
import org.cagrid.gaards.pki.CertUtil;
import org.cagrid.gaards.pki.Credential;
import org.cagrid.gaards.saml.encoding.SAMLConstants;
import org.cagrid.tools.database.Database;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class TestUserManager extends TestCase implements Publisher {
    private static final int INIT_USER = 1;
    private static final String DEFAULT_IDP_NAME = "Dorian IdP";

    private Database db;

    private CertificateAuthority ca;
    private CA memoryCA;

    private PropertyManager props;


    public void testSingleUserIdPNameBasedIdentitfiers() {
        try {
            checkSingleUser(getUserManagerNameBasedIdentities());
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        }

    }


    public void testSingleUserIdPIdBasedIdentitfiers() {
        try {
            checkSingleUser(getUserManagerIdBasedIdentities());
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        }

    }


    public void testMultipleUsersIdPNameBasedIdentitfiers() {
        try {
            checkMultipleUsers(getUserManagerNameBasedIdentities());
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        }

    }


    public void testMultipleUsersIdPIdBasedIdentitfiers() {
        try {
            checkMultipleUsers(getUserManagerIdBasedIdentities());
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        }

    }


    public void checkSingleUser(UserManager um) {
        try {
            // Test adding user
            GridUser user = new GridUser();
            user.setIdPId(INIT_USER + 1);
            user.setUID("user");
            user.setFirstName("John");
            user.setLastName("Doe");
            user.setEmail("user@user.com");
            user = um.addUser(getIdp(user), user);
            String expectedGridIdentity = UserManager.subjectToIdentity(UserManager.getUserSubject(um
                .getIdentityAssignmentPolicy(), ca.getCACertificate().getSubjectDN().getName(), getIdp(user), user
                .getUID()));
            assertNotNull(user.getGridId());
            assertNotNull(user.getUserStatus());
            assertEquals(GridUserStatus.Pending, user.getUserStatus());
            assertEquals(expectedGridIdentity, user.getGridId());
            assertEquals(user, um.getUser(user.getIdPId(), user.getUID()));
            assertEquals(user, um.getUser(user.getGridId()));

            // Test Querying for users
            GridUserFilter f1 = new GridUserFilter();
            GridUser[] l1 = um.getUsers(f1);
            assertEquals(1 + INIT_USER, l1.length);

            // Test querying by uid
            GridUserFilter f2 = new GridUserFilter();
            f2.setUID("nobody");
            GridUser[] l2 = um.getUsers(f2);
            assertEquals(0, l2.length);
            f2.setUID("use");
            l2 = um.getUsers(f2);
            assertEquals(1, l2.length);
            assertEquals(user, l2[0]);

            // Test querying by IdP_Id
            GridUserFilter f3 = new GridUserFilter();
            f3.setIdPId(Long.MAX_VALUE);
            GridUser[] l3 = um.getUsers(f3);
            assertEquals(0, l3.length);
            f3.setIdPId(user.getIdPId());
            l3 = um.getUsers(f3);
            assertEquals(1, l3.length);
            assertEquals(user, l3[0]);

            // Test querying by GID
            GridUserFilter f4 = new GridUserFilter();
            f4.setGridId("nobody");
            GridUser[] l4 = um.getUsers(f4);
            assertEquals(0, l4.length);
            f4.setGridId(user.getGridId());
            l4 = um.getUsers(f4);
            assertEquals(1, l4.length);
            assertEquals(user, l4[0]);

            // Test querying by Email
            GridUserFilter f5 = new GridUserFilter();
            f5.setEmail("nobody");
            GridUser[] l5 = um.getUsers(f5);
            assertEquals(0, l5.length);
            f5.setEmail(user.getEmail());
            l5 = um.getUsers(f5);
            assertEquals(1, l5.length);
            assertEquals(user, l5[0]);

            // Test querying by Status
            GridUserFilter f7 = new GridUserFilter();
            f7.setUserStatus(GridUserStatus.Suspended);
            GridUser[] l7 = um.getUsers(f7);
            assertEquals(0, l7.length);
            f7.setUserStatus(user.getUserStatus());
            l7 = um.getUsers(f7);
            assertEquals(1, l7.length);
            assertEquals(user, l7[0]);

            // Test querying by First Name
            GridUserFilter f8 = new GridUserFilter();
            f8.setFirstName("nobody");
            GridUser[] l8 = um.getUsers(f8);
            assertEquals(0, l8.length);
            f8.setFirstName(user.getFirstName());
            l8 = um.getUsers(f8);
            assertEquals(1, l8.length);
            assertEquals(user, l8[0]);

            // Test querying by Last Name
            GridUserFilter f9 = new GridUserFilter();
            f9.setLastName("nobody");
            GridUser[] l9 = um.getUsers(f9);
            assertEquals(0, l9.length);
            f9.setLastName(user.getLastName());
            l9 = um.getUsers(f9);
            assertEquals(1, l9.length);
            assertEquals(user, l9[0]);

            // Test All
            GridUserFilter all = new GridUserFilter();
            all.setIdPId(user.getIdPId());
            all.setUID(user.getUID());
            all.setGridId(user.getGridId());
            all.setFirstName(user.getFirstName());
            all.setLastName(user.getLastName());
            all.setEmail(user.getEmail());
            all.setUserStatus(user.getUserStatus());
            GridUser[] allList = um.getUsers(all);
            assertEquals(1, allList.length);

            // Test Update
            GridUser u1 = um.getUser(user.getGridId());
            u1.setFirstName("newfirst");
            u1.setLastName("newlast");
            u1.setEmail("newemail@example.com");
            um.updateUser(u1);
            assertEquals(u1, um.getUser(u1.getGridId()));

            GridUser u3 = um.getUser(user.getGridId());
            u3.setUserStatus(GridUserStatus.Active);
            um.updateUser(u3);
            assertEquals(u3, um.getUser(u3.getGridId()));

            GridUser u4 = um.getUser(user.getGridId());
            u4.setUserStatus(GridUserStatus.Suspended);
            u4.setEmail("newemail2@example.com");
            um.updateUser(u4);
            assertEquals(u4, um.getUser(u4.getGridId()));

            GridUser u5 = um.getUser(user.getGridId());
            u5.setGridId("changed grid id");
            try {
                um.updateUser(u5);
                fail("Should not be able to change a user's grid identity.");
            } catch (InvalidUserFault e) {
                if (!gov.nih.nci.cagrid.common.Utils.getExceptionMessage(e).equals(
                    UserManager.CANNOT_UPDATE_GRID_IDENTITY_ERROR)) {
                    fail("Should not be able to change a user's grid identity.");
                }
            };
            um.removeUser(u5);
            assertEquals(INIT_USER, um.getUsers(new GridUserFilter()).length);
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        } finally {
            try {
                um.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void checkMultipleUsers(UserManager um) {
        try {

            String prefix = "user";
            String firstNamePrefix = "John";
            String lastNamePrefix = "Doe";

            int userCount = 9;

            for (int i = 0; i < userCount; i++) {
                // Test adding user
                long idpId = (i % 3) + 1 + INIT_USER;
                long idpCount = (i / 3) + 1;

                String uname = prefix + i;
                String firstName = firstNamePrefix + i;
                String lastName = lastNamePrefix + i;

                GridUser user = new GridUser();

                user.setIdPId(idpId);
                user.setUID(uname);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setEmail(uname + "@user.com");
                user = um.addUser(getIdp(user), user);
                String expectedGridIdentity = UserManager.subjectToIdentity(UserManager.getUserSubject(um
                    .getIdentityAssignmentPolicy(), ca.getCACertificate().getSubjectDN().getName(), getIdp(user), user
                    .getUID()));
                assertNotNull(user.getGridId());
                assertNotNull(user.getUserStatus());
                assertEquals(expectedGridIdentity, user.getGridId());
                assertEquals(user, um.getUser(user.getIdPId(), user.getUID()));
                assertEquals(user, um.getUser(user.getGridId()));

                // Test Querying for users
                GridUserFilter f1 = new GridUserFilter();
                GridUser[] l1 = um.getUsers(f1);
                assertEquals((i + 1 + INIT_USER), l1.length);

                // Test querying by uid
                GridUserFilter f2 = new GridUserFilter();
                f2.setUID("nobody");
                GridUser[] l2 = um.getUsers(f2);
                assertEquals(0, l2.length);
                f2.setUID("use");
                l2 = um.getUsers(f2);
                assertEquals((i + 1), l2.length);
                f2.setUID(uname);
                l2 = um.getUsers(f2);
                assertEquals(1, l2.length);
                assertEquals(user, l2[0]);

                // Test querying by IdP_Id
                GridUserFilter f3 = new GridUserFilter();
                f3.setIdPId(Long.MAX_VALUE);
                GridUser[] l3 = um.getUsers(f3);
                assertEquals(0, l3.length);
                f3.setIdPId(user.getIdPId());
                l3 = um.getUsers(f3);
                assertEquals(idpCount, l3.length);

                // Test querying by GID
                GridUserFilter f4 = new GridUserFilter();
                f4.setGridId("nobody");
                GridUser[] l4 = um.getUsers(f4);
                assertEquals(0, l4.length);

                String temp = user.getGridId();
                int index = temp.lastIndexOf("/");
                temp = temp.substring(0, index);
                f4.setGridId(temp);
                l4 = um.getUsers(f4);
                assertEquals(idpCount, l4.length);
                f4.setGridId(user.getGridId());
                l4 = um.getUsers(f4);
                assertEquals(1, l4.length);
                assertEquals(user, l4[0]);

                // Test querying by Email
                GridUserFilter f5 = new GridUserFilter();
                f5.setEmail("nobody");
                GridUser[] l5 = um.getUsers(f5);
                assertEquals(0, l5.length);
                f5.setEmail(user.getEmail());
                l5 = um.getUsers(f5);
                assertEquals(1, l5.length);
                assertEquals(user, l5[0]);

                // Test querying by Status
                GridUserFilter f7 = new GridUserFilter();
                f7.setUserStatus(GridUserStatus.Suspended);
                GridUser[] l7 = um.getUsers(f7);
                assertEquals(i, l7.length);
                f7.setUserStatus(user.getUserStatus());
                l7 = um.getUsers(f7);
                assertEquals(1, l7.length);
                assertEquals(user, l7[0]);

                // Test querying by First Name
                GridUserFilter f8 = new GridUserFilter();
                f8.setFirstName("nobody");
                GridUser[] l8 = um.getUsers(f8);
                assertEquals(0, l8.length);
                f8.setFirstName(firstNamePrefix);
                l8 = um.getUsers(f8);
                assertEquals((i + 1), l8.length);
                f8.setFirstName(user.getFirstName());
                l8 = um.getUsers(f8);
                assertEquals(1, l8.length);
                assertEquals(user, l8[0]);

                // Test querying by Last Name
                GridUserFilter f9 = new GridUserFilter();
                f9.setLastName("nobody");
                GridUser[] l9 = um.getUsers(f9);
                assertEquals(0, l9.length);
                f9.setLastName(lastNamePrefix);
                l9 = um.getUsers(f9);
                assertEquals((i + 1), l9.length);
                f9.setLastName(user.getLastName());
                l9 = um.getUsers(f9);
                assertEquals(1, l9.length);
                assertEquals(user, l9[0]);

                // Test All
                GridUserFilter all = new GridUserFilter();
                all.setIdPId(user.getIdPId());
                all.setUID(user.getUID());
                all.setGridId(user.getGridId());
                all.setFirstName(user.getFirstName());
                all.setLastName(user.getLastName());
                all.setEmail(user.getEmail());
                all.setUserStatus(user.getUserStatus());
                GridUser[] lall = um.getUsers(all);
                assertEquals(1, lall.length);

                // Test Update
                GridUser u1 = um.getUser(user.getGridId());
                u1.setEmail("newemail@example.com");
                um.updateUser(u1);
                assertEquals(u1, um.getUser(u1.getGridId()));

                GridUser u3 = um.getUser(user.getGridId());
                u3.setUserStatus(GridUserStatus.Active);
                um.updateUser(u3);
                assertEquals(u3, um.getUser(u3.getGridId()));

                u3.setUserStatus(GridUserStatus.Suspended);
                um.updateUser(u3);
                assertEquals(u3, um.getUser(u3.getGridId()));

                GridUser u4 = um.getUser(user.getGridId());
                u4.setUserStatus(GridUserStatus.Suspended);
                u4.setEmail("newemail2@example.com");
                um.updateUser(u4);
                assertEquals(u4, um.getUser(u4.getGridId()));

                GridUser u5 = um.getUser(user.getGridId());
                u5.setGridId("changed grid id" + i);
                try {
                    um.updateUser(u5);
                    fail("Should not be able to change a user's grid identity.");
                } catch (InvalidUserFault e) {
                    if (!gov.nih.nci.cagrid.common.Utils.getExceptionMessage(e).equals(
                        UserManager.CANNOT_UPDATE_GRID_IDENTITY_ERROR)) {
                        fail("Should not be able to change a user's grid identity.");
                    }
                }
            }

            // um.removeUser(u5);
            GridUser[] list = um.getUsers(new GridUserFilter());
            assertEquals(userCount + INIT_USER, list.length);
            int count = userCount;
            for (int i = 0; i < list.length; i++) {
                count = count - 1;
                um.removeUser(list[i]);
                assertEquals(count + INIT_USER, um.getUsers(new GridUserFilter()).length);
            }
            assertEquals(0, um.getUsers(new GridUserFilter()).length);
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        } finally {
            try {
                um.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testUpdateRejectedUserInvalidStatus() {
        UserManager um = null;
        try {
            um = getUserManagerNameBasedIdentities();
            GridUser user = new GridUser();
            user.setIdPId(INIT_USER + 1);
            user.setUID("user");
            user.setFirstName("John");
            user.setLastName("Doe");
            user.setEmail("user@user.com");
            user = um.addUser(getIdp(user), user);
            String expectedGridIdentity = UserManager.subjectToIdentity(UserManager.getUserSubject(um
                .getIdentityAssignmentPolicy(), ca.getCACertificate().getSubjectDN().getName(), getIdp(user), user
                .getUID()));
            assertNotNull(user.getGridId());
            assertNotNull(user.getUserStatus());
            assertEquals(GridUserStatus.Pending, user.getUserStatus());
            assertEquals(expectedGridIdentity, user.getGridId());
            assertEquals(user, um.getUser(user.getIdPId(), user.getUID()));
            assertEquals(user, um.getUser(user.getGridId()));
            user.setUserStatus(GridUserStatus.Rejected);
            um.updateUser(user);
            GridUser u1 = um.getUser(user.getGridId());
            assertEquals(user.getUserStatus(), u1.getUserStatus());

            user.setUserStatus(GridUserStatus.Active);

            try {
                um.updateUser(user);
                fail("Should not be able to change the status of a user to an invalid status.");
            } catch (InvalidUserFault e) {

            }

            user.setUserStatus(GridUserStatus.Suspended);
            try {
                um.updateUser(user);
                fail("Should not be able to change the status of a user to an invalid status.");
            } catch (InvalidUserFault e) {

            }

        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        } finally {
            if (um != null) {
                try {
                    um.clearDatabase();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }


    public void testUpdateActiveUserInvalidStatus() {
        UserManager um = null;
        try {
            um = getUserManagerNameBasedIdentities();
            GridUser user = new GridUser();
            user.setIdPId(INIT_USER + 1);
            user.setUID("user");
            user.setFirstName("John");
            user.setLastName("Doe");
            user.setEmail("user@user.com");
            user = um.addUser(getIdp(user), user);
            String expectedGridIdentity = UserManager.subjectToIdentity(UserManager.getUserSubject(um
                .getIdentityAssignmentPolicy(), ca.getCACertificate().getSubjectDN().getName(), getIdp(user), user
                .getUID()));
            assertNotNull(user.getGridId());
            assertNotNull(user.getUserStatus());
            assertEquals(GridUserStatus.Pending, user.getUserStatus());
            assertEquals(expectedGridIdentity, user.getGridId());
            assertEquals(user, um.getUser(user.getIdPId(), user.getUID()));
            assertEquals(user, um.getUser(user.getGridId()));
            user.setUserStatus(GridUserStatus.Active);
            um.updateUser(user);
            GridUser u1 = um.getUser(user.getGridId());
            assertEquals(user.getUserStatus(), u1.getUserStatus());

            user.setUserStatus(GridUserStatus.Rejected);

            try {
                um.updateUser(user);
                fail("Should not be able to change the status of a user to an invalid status.");
            } catch (InvalidUserFault e) {

            }

            user.setUserStatus(GridUserStatus.Pending);

            try {
                um.updateUser(user);
                fail("Should not be able to change the status of a user to an invalid status.");
            } catch (InvalidUserFault e) {

            }

        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        } finally {
            if (um != null) {
                try {
                    um.clearDatabase();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }


    private IdentityFederationProperties getConf(String policy) throws Exception {
        IdentityFederationProperties conf = Utils.getIdentityFederationProperties();
        conf.setIdentityAssignmentPolicy(policy);
        return conf;
    }


    private FederationDefaults getDefaults() throws Exception {
        TrustedIdP idp = new TrustedIdP();
        idp.setName("Initial IdP");
        idp.setDisplayName(idp.getName());

        SAMLAttributeDescriptor uid = new SAMLAttributeDescriptor();
        uid.setNamespaceURI(SAMLConstants.UID_ATTRIBUTE_NAMESPACE);
        uid.setName(SAMLConstants.UID_ATTRIBUTE);
        idp.setUserIdAttributeDescriptor(uid);

        SAMLAttributeDescriptor firstName = new SAMLAttributeDescriptor();
        firstName.setNamespaceURI(SAMLConstants.FIRST_NAME_ATTRIBUTE_NAMESPACE);
        firstName.setName(SAMLConstants.FIRST_NAME_ATTRIBUTE);
        idp.setFirstNameAttributeDescriptor(firstName);

        SAMLAttributeDescriptor lastName = new SAMLAttributeDescriptor();
        lastName.setNamespaceURI(SAMLConstants.LAST_NAME_ATTRIBUTE_NAMESPACE);
        lastName.setName(SAMLConstants.LAST_NAME_ATTRIBUTE);
        idp.setLastNameAttributeDescriptor(lastName);

        SAMLAttributeDescriptor email = new SAMLAttributeDescriptor();
        email.setNamespaceURI(SAMLConstants.EMAIL_ATTRIBUTE_NAMESPACE);
        email.setName(SAMLConstants.EMAIL_ATTRIBUTE);
        idp.setEmailAttributeDescriptor(email);

        SAMLAuthenticationMethod[] methods = new SAMLAuthenticationMethod[1];
        methods[0] = SAMLAuthenticationMethod.fromString("urn:oasis:names:tc:SAML:1.0:am:password");
        idp.setAuthenticationMethod(methods);
        idp.setUserPolicyClass(AutoApprovalPolicy.class.getName());

        String subject = Utils.CA_SUBJECT_PREFIX + ",CN=" + idp.getName();
        Credential cred = memoryCA.createIdentityCertificate(idp.getName());
        X509Certificate cert = cred.getCertificate();
        assertNotNull(cert);
        assertEquals(cert.getSubjectDN().getName(), subject);
        idp.setIdPCertificate(CertUtil.writeCertificate(cert));
        idp.setStatus(TrustedIdPStatus.Active);
        GridUser usr = new GridUser();
        usr.setUID("inital_admin");
        usr.setFirstName("Mr");
        usr.setLastName("Admin");
        usr.setEmail("inital_admin@test.com");
        usr.setUserStatus(GridUserStatus.Active);
        return new FederationDefaults(idp, usr);
    }


    protected void setUp() throws Exception {
        super.setUp();
        try {
            db = Utils.getDB();
            assertEquals(0, db.getUsedConnectionCount());
            ca = Utils.getCA();
            memoryCA = new CA(Utils.getCASubject());
            props = new PropertyManager(db);
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        }
    }


    public UserManager getUserManagerNameBasedIdentities() throws Exception {
        IdentityFederationProperties conf = getConf(IdentityAssignmentPolicy.NAME);
        TrustedIdPManager tm = new TrustedIdPManager(conf, db);
        UserManager um = new UserManager(db, conf, props, ca, tm, this, getDefaults());
        um.clearDatabase();
        return um;
    }


    public UserManager getUserManagerIdBasedIdentities() throws Exception {
        IdentityFederationProperties conf = getConf(IdentityAssignmentPolicy.ID);
        TrustedIdPManager tm = new TrustedIdPManager(conf, db);
        UserManager um = new UserManager(db, conf, props, ca, tm, this, getDefaults());
        um.clearDatabase();
        return um;
    }


    protected void tearDown() throws Exception {
        super.setUp();
        try {
            assertEquals(0, db.getUsedConnectionCount());
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        }
    }


    public void publishCRL() {

    }


    private TrustedIdP getIdp(GridUser usr) {
        TrustedIdP idp = new TrustedIdP();
        idp.setId(usr.getIdPId());
        idp.setName(DEFAULT_IDP_NAME + usr.getIdPId());
        return idp;
    }

}
