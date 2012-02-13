package org.cagrid.gaards.dorian.idp;

import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.opensaml.SAMLAssertion;
import gov.nih.nci.cagrid.opensaml.SAMLAttributeStatement;
import gov.nih.nci.cagrid.opensaml.SAMLAuthenticationStatement;
import gov.nih.nci.cagrid.opensaml.SAMLStatement;

import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.cagrid.gaards.authentication.BasicAuthentication;
import org.cagrid.gaards.authentication.OneTimePassword;
import org.cagrid.gaards.authentication.faults.CredentialNotSupportedFault;
import org.cagrid.gaards.authentication.faults.InvalidCredentialFault;
import org.cagrid.gaards.dorian.ca.CertificateAuthority;
import org.cagrid.gaards.dorian.common.AuditConstants;
import org.cagrid.gaards.dorian.common.SAMLConstants;
import org.cagrid.gaards.dorian.policy.AccountInformationModificationPolicy;
import org.cagrid.gaards.dorian.stubs.types.InvalidUserPropertyFault;
import org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault;
import org.cagrid.gaards.dorian.test.Utils;
import org.cagrid.tools.database.Database;
import org.cagrid.tools.events.EventManager;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class TestIdentityProvider extends TestCase {

    private Database db;

    private CertificateAuthority ca;

    private int count = 0;

    private EventManager eventManager;


    public void testAutomaticRegistration() {
        IdentityProvider idp = null;
        try {
            IdentityProviderProperties props = Utils.getIdentityProviderProperties();
            props.setRegistrationPolicy(new AutomaticRegistrationPolicy());
            idp = new IdentityProvider(props, db, ca, eventManager);
            assertEquals(AutomaticRegistrationPolicy.class, props.getRegistrationPolicy().getClass());
            BasicAuthCredential cred = getAdminCreds();
            Application a = createApplication();
            assertFalse(idp.doesUserExist(a.getUserId()));
            idp.register(a);
            performAndValidateSingleAudit(idp, cred.getUserId(), a.getUserId(), AuditConstants.SYSTEM_ID,
                IdentityProviderAudit.Registration);

            assertTrue(idp.doesUserExist(a.getUserId()));
            LocalUserFilter uf = new LocalUserFilter();
            uf.setUserId(a.getUserId());
            LocalUser[] users = idp.findUsers(cred.getUserId(), uf);
            assertEquals(1, users.length);
            assertEquals(LocalUserStatus.Active, users[0].getStatus());
            assertEquals(LocalUserRole.Non_Administrator, users[0].getRole());
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        } finally {
            try {
                idp.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testAuthenticate() {
        IdentityProvider idp = null;
        try {
            IdentityProviderProperties props = Utils.getIdentityProviderProperties();
            props.setRegistrationPolicy(new AutomaticRegistrationPolicy());
            idp = new IdentityProvider(props, db, ca, eventManager);
            Application a = createApplication();
            idp.register(a);
            BasicAuthCredential cred = getAdminCreds();
            performAndValidateSingleAudit(idp, cred.getUserId(), a.getUserId(), AuditConstants.SYSTEM_ID,
                IdentityProviderAudit.Registration);
            LocalUserFilter uf = new LocalUserFilter();
            uf.setUserId(a.getUserId());
            LocalUser[] users = idp.findUsers(cred.getUserId(), uf);
            assertEquals(1, users.length);
            assertEquals(LocalUserStatus.Active, users[0].getStatus());
            assertEquals(LocalUserRole.Non_Administrator, users[0].getRole());
            verifyAuthentication(cred.getUserId(), idp, a);

        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        } finally {
            try {
                idp.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void compareUserToProfile(LocalUser u, AccountProfile p) {
        assertEquals(u.getAddress(), p.getAddress());
        assertEquals(u.getAddress2(), p.getAddress2());
        assertEquals(u.getCity(), p.getCity());
        assertEquals(u.getEmail(), p.getEmail());
        assertEquals(u.getFirstName(), p.getFirstName());
        assertEquals(u.getLastName(), p.getLastName());
        assertEquals(u.getOrganization(), p.getOrganization());
        assertEquals(u.getPhoneNumber(), p.getPhoneNumber());
        assertEquals(u.getUserId(), p.getUserId());
        assertEquals(u.getZipcode(), p.getZipcode());
        assertEquals(u.getCountry(), p.getCountry());
        assertEquals(u.getState(), p.getState());
    }


    public void testGetModifyAccountProfile() {
        IdentityProvider idp = null;
        try {
            IdentityProviderProperties props = Utils.getIdentityProviderProperties();
            props.setRegistrationPolicy(new AutomaticRegistrationPolicy());
            props.setAccountInformationModificationPolicy(AccountInformationModificationPolicy.User.getValue());
            idp = new IdentityProvider(props, db, ca, eventManager);
            Application a = createApplication();
            idp.register(a);
            BasicAuthCredential cred = getAdminCreds();
            performAndValidateSingleAudit(idp, cred.getUserId(), a.getUserId(), AuditConstants.SYSTEM_ID,
                IdentityProviderAudit.Registration);
            LocalUserFilter uf = new LocalUserFilter();
            uf.setUserId(a.getUserId());
            LocalUser[] users = idp.findUsers(cred.getUserId(), uf);
            assertEquals(1, users.length);
            LocalUser u = users[0];
            assertEquals(LocalUserStatus.Active, u.getStatus());
            assertEquals(LocalUserRole.Non_Administrator, u.getRole());
            AccountProfile p = idp.getAccountProfile(u.getUserId());
            compareUserToProfile(u, p);

            p.setAddress("new address");
            p.setAddress2("new address2");
            p.setCity("new city");
            p.setCountry(CountryCode.AD);
            p.setEmail("new@cagrid.org");
            p.setFirstName("new name");
            p.setLastName("new last name");
            p.setOrganization("new organization");
            p.setPhoneNumber("111-111-1111");
            p.setState(StateCode.SC);
            p.setZipcode("44444");

            try {
                idp.updateAccountProfile(cred.getUserId(), p);
                fail("Only the user should be able to update their profile.");
            } catch (PermissionDeniedFault e) {

            }
            idp.updateAccountProfile(u.getUserId(), p);
            assertEquals(p, idp.getAccountProfile(u.getUserId()));
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        } finally {
            try {
                idp.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testGetModifyAccountProfileAdminOnly() {
        IdentityProvider idp = null;
        try {
            IdentityProviderProperties props = Utils.getIdentityProviderProperties();
            props.setRegistrationPolicy(new AutomaticRegistrationPolicy());
            idp = new IdentityProvider(props, db, ca, eventManager);
            Application a = createApplication();
            idp.register(a);
            BasicAuthCredential cred = getAdminCreds();
            performAndValidateSingleAudit(idp, cred.getUserId(), a.getUserId(), AuditConstants.SYSTEM_ID,
                IdentityProviderAudit.Registration);
            LocalUserFilter uf = new LocalUserFilter();
            uf.setUserId(a.getUserId());
            LocalUser[] users = idp.findUsers(cred.getUserId(), uf);
            assertEquals(1, users.length);
            LocalUser u = users[0];
            assertEquals(LocalUserStatus.Active, u.getStatus());
            assertEquals(LocalUserRole.Non_Administrator, u.getRole());
            AccountProfile p = idp.getAccountProfile(u.getUserId());
            compareUserToProfile(u, p);

            try {
                idp.updateAccountProfile(cred.getUserId(), p);
                fail("Users should not be able to update profiles.");
            } catch (PermissionDeniedFault e) {

            }
            try {
                idp.updateAccountProfile(u.getUserId(), p);
                fail("Users should not be able to update profiles.");
            } catch (PermissionDeniedFault e) {

            }
            assertEquals(p, idp.getAccountProfile(u.getUserId()));
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        } finally {
            try {
                idp.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testAuthenticateBadPermutationsOfPassword() {
        IdentityProvider idp = null;
        try {
            IdentityProviderProperties props = Utils.getIdentityProviderProperties();
            props.setRegistrationPolicy(new AutomaticRegistrationPolicy());
            PasswordSecurityPolicy policy = props.getPasswordSecurityPolicy();
            policy.setConsecutiveInvalidLogins(500);
            policy.setTotalInvalidLogins(500);
            policy.getLockout().setHours(0);
            policy.getLockout().setMinutes(0);
            policy.getLockout().setSeconds(3);
            props.setRegistrationPolicy(new AutomaticRegistrationPolicy());
            idp = new IdentityProvider(props, db, ca, eventManager);
            Application a = createApplication();
            idp.register(a);
            BasicAuthCredential cred = getAdminCreds();
            performAndValidateSingleAudit(idp, cred.getUserId(), a.getUserId(), AuditConstants.SYSTEM_ID,
                IdentityProviderAudit.Registration);
            LocalUserFilter uf = new LocalUserFilter();
            uf.setUserId(a.getUserId());
            LocalUser[] users = idp.findUsers(cred.getUserId(), uf);
            assertEquals(1, users.length);
            assertEquals(LocalUserStatus.Active, users[0].getStatus());
            assertEquals(LocalUserRole.Non_Administrator, users[0].getRole());
            verifyAuthentication(cred.getUserId(), idp, a);

            for (int i = 0; i < a.getPassword().length(); i++) {
                StringBuffer permutation = new StringBuffer();
                permutation.append(a.getPassword().substring(0, i));
                permutation.append("s");
                permutation.append(a.getPassword().substring(i + 1));

                BasicAuthentication credential = new BasicAuthentication();
                credential.setUserId(a.getUserId());
                credential.setPassword(permutation.toString());
                try {
                    idp.authenticate(credential);
                    fail("Should not be able to authenticate!!!");
                } catch (InvalidCredentialFault e) {

                }
                performAndValidateMultipleAudit(idp, cred.getUserId(), a.getUserId(), AuditConstants.SYSTEM_ID,
                    IdentityProviderAudit.InvalidLogin, (i + 1));
            }

        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        } finally {
            try {
                idp.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testResetLockedPassword() {
        IdentityProvider idp = null;
        try {
            IdentityProviderProperties props = Utils.getIdentityProviderProperties();
            props.setRegistrationPolicy(new AutomaticRegistrationPolicy());
            PasswordSecurityPolicy policy = props.getPasswordSecurityPolicy();
            policy.setConsecutiveInvalidLogins(3);
            policy.setTotalInvalidLogins(4);
            policy.getLockout().setHours(0);
            policy.getLockout().setMinutes(0);
            policy.getLockout().setSeconds(3);
            props.setRegistrationPolicy(new AutomaticRegistrationPolicy());
            idp = new IdentityProvider(props, db, ca, eventManager);
            BasicAuthCredential cred = getAdminCreds();
            Application a = createApplication();

            idp.register(a);
            performAndValidateSingleAudit(idp, cred.getUserId(), a.getUserId(), AuditConstants.SYSTEM_ID,
                IdentityProviderAudit.Registration);

            LocalUserFilter uf = new LocalUserFilter();
            uf.setUserId(a.getUserId());
            LocalUser[] users = idp.findUsers(cred.getUserId(), uf);
            assertEquals(1, users.length);
            assertEquals(LocalUserStatus.Active, users[0].getStatus());
            assertEquals(LocalUserRole.Non_Administrator, users[0].getRole());

            BasicAuthentication bad = new BasicAuthentication();
            bad.setUserId(a.getUserId());
            bad.setPassword("foobar");

            int localCount = 0;
            int lockCount = 0;
            int successfulLogins = 0;
            int invalidLogins = 0;
            boolean totalLock = false;
            for (int i = 1; i <= (policy.getTotalInvalidLogins() + 2); i++) {
                if (i > policy.getTotalInvalidLogins()) {
                    try {
                        idp.authenticate(getCredential(a));
                        fail("Should NOT be able to authenticate!!!");
                    } catch (InvalidCredentialFault e) {
                    }
                    invalidLogins = invalidLogins + 1;
                    performAndValidateMultipleAudit(idp, cred.getUserId(), a.getUserId(), AuditConstants.SYSTEM_ID,
                        IdentityProviderAudit.InvalidLogin, invalidLogins);

                    if (!totalLock) {
                        lockCount = lockCount + 1;
                        totalLock = true;
                    }
                    performAndValidateMultipleAudit(idp, cred.getUserId(), a.getUserId(), AuditConstants.SYSTEM_ID,
                        IdentityProviderAudit.LocalAccountLocked, lockCount);

                } else if (localCount != policy.getConsecutiveInvalidLogins()) {
                    try {
                        idp.authenticate(bad);
                        fail("Should NOT be able to authenticate!!!");
                    } catch (InvalidCredentialFault e) {

                    }
                    invalidLogins = invalidLogins + 1;
                    performAndValidateMultipleAudit(idp, cred.getUserId(), a.getUserId(), AuditConstants.SYSTEM_ID,
                        IdentityProviderAudit.InvalidLogin, invalidLogins);
                } else {
                    localCount = 0;
                    try {
                        idp.authenticate(getCredential(a));
                        fail("Should NOT be able to authenticate!!!");
                    } catch (InvalidCredentialFault e) {

                    }
                    invalidLogins = invalidLogins + 1;
                    performAndValidateMultipleAudit(idp, cred.getUserId(), a.getUserId(), AuditConstants.SYSTEM_ID,
                        IdentityProviderAudit.InvalidLogin, invalidLogins);

                    lockCount = lockCount + 1;
                    performAndValidateMultipleAudit(idp, cred.getUserId(), a.getUserId(), AuditConstants.SYSTEM_ID,
                        IdentityProviderAudit.LocalAccountLocked, lockCount);

                    Thread.sleep((policy.getLockout().getSeconds() * 1000) + 100);
                    successfulLogins = successfulLogins + 1;
                    verifyAuthentication(cred.getUserId(), idp, a, successfulLogins);
                    try {
                        idp.authenticate(bad);
                        fail("Should NOT be able to authenticate!!!");
                    } catch (InvalidCredentialFault e) {

                    }
                    invalidLogins = invalidLogins + 1;
                    performAndValidateMultipleAudit(idp, cred.getUserId(), a.getUserId(), AuditConstants.SYSTEM_ID,
                        IdentityProviderAudit.InvalidLogin, invalidLogins);

                }
                localCount = localCount + 1;
            }
            // Now we have an admin reset the password
            users[0].setPassword("$W0rdD0ct0R$2");
            a.setPassword(users[0].getPassword());
            idp.updateUser(cred.getUserId(), users[0]);

            performAndValidateSingleAudit(idp, cred.getUserId(), a.getUserId(), cred.getUserId(),
                IdentityProviderAudit.LocalAccountUpdated);
            successfulLogins = successfulLogins + 1;
            verifyAuthentication(cred.getUserId(), idp, a, successfulLogins);
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        } finally {
            try {
                idp.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testResetPasswordSecurityOnSuccessfulLogin() {
        IdentityProvider idp = null;
        try {
            IdentityProviderProperties props = Utils.getIdentityProviderProperties();
            PasswordSecurityPolicy policy = props.getPasswordSecurityPolicy();
            policy.setConsecutiveInvalidLogins(3);
            policy.setTotalInvalidLogins(10);
            policy.getLockout().setHours(0);
            policy.getLockout().setMinutes(0);
            policy.getLockout().setSeconds(3);
            props.setRegistrationPolicy(new AutomaticRegistrationPolicy());
            idp = new IdentityProvider(props, db, ca, eventManager);
            BasicAuthCredential cred = getAdminCreds();
            Application a = createApplication();
            idp.register(a);
            performAndValidateSingleAudit(idp, cred.getUserId(), a.getUserId(), AuditConstants.SYSTEM_ID,
                IdentityProviderAudit.Registration);

            LocalUserFilter uf = new LocalUserFilter();
            uf.setUserId(a.getUserId());
            LocalUser[] users = idp.findUsers(cred.getUserId(), uf);
            assertEquals(1, users.length);
            assertEquals(LocalUserStatus.Active, users[0].getStatus());
            assertEquals(LocalUserRole.Non_Administrator, users[0].getRole());

            BasicAuthentication bad = new BasicAuthentication();
            bad.setUserId(a.getUserId());
            bad.setPassword("foobar");

            int localCount = 0;
            int invalidLogins = 0;
            int successfulLogins = 0;
            for (int i = 1; i <= (policy.getTotalInvalidLogins()); i++) {
                if (localCount == (policy.getConsecutiveInvalidLogins() - 1)) {
                    successfulLogins = successfulLogins + 1;
                    verifyAuthentication(cred.getUserId(), idp, a, successfulLogins);
                    localCount = 0;
                } else {
                    try {
                        idp.authenticate(bad);
                        fail("Should NOT be able to authenticate!!!");
                    } catch (InvalidCredentialFault e) {

                    }
                    invalidLogins = invalidLogins + 1;
                    performAndValidateMultipleAudit(idp, cred.getUserId(), a.getUserId(), AuditConstants.SYSTEM_ID,
                        IdentityProviderAudit.InvalidLogin, invalidLogins);
                    localCount = localCount + 1;
                }

            }
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        } finally {
            try {
                idp.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testPasswordSecurity() {
        IdentityProvider idp = null;
        try {
            IdentityProviderProperties props = Utils.getIdentityProviderProperties();
            PasswordSecurityPolicy policy = props.getPasswordSecurityPolicy();
            policy.setConsecutiveInvalidLogins(3);
            policy.setTotalInvalidLogins(7);
            policy.getLockout().setHours(0);
            policy.getLockout().setMinutes(0);
            policy.getLockout().setSeconds(3);
            props.setRegistrationPolicy(new AutomaticRegistrationPolicy());
            idp = new IdentityProvider(props, db, ca, eventManager);
            for (int j = 0; j < 2; j++) {
                Application a = createApplication();

                idp.register(a);
                BasicAuthCredential cred = getAdminCreds();
                performAndValidateSingleAudit(idp, cred.getUserId(), a.getUserId(), AuditConstants.SYSTEM_ID,
                    IdentityProviderAudit.Registration);
                LocalUserFilter uf = new LocalUserFilter();
                uf.setUserId(a.getUserId());
                LocalUser[] users = idp.findUsers(cred.getUserId(), uf);
                assertEquals(1, users.length);
                assertEquals(LocalUserStatus.Active, users[0].getStatus());
                assertEquals(LocalUserRole.Non_Administrator, users[0].getRole());

                BasicAuthentication bad = new BasicAuthentication();
                bad.setUserId(a.getUserId());
                bad.setPassword("foobar");

                int localCount = 0;
                int invalidLogins = 0;
                int successfulLogins = 0;
                for (int i = 1; i <= (policy.getTotalInvalidLogins() + 1); i++) {
                    if (i > policy.getTotalInvalidLogins()) {
                        try {
                            idp.authenticate(getCredential(a));
                            fail("Should NOT be able to authenticate!!!");
                        } catch (InvalidCredentialFault e) {

                        }
                        invalidLogins = invalidLogins + 1;
                        performAndValidateMultipleAudit(idp, cred.getUserId(), a.getUserId(), AuditConstants.SYSTEM_ID,
                            IdentityProviderAudit.InvalidLogin, invalidLogins);
                    } else if (localCount != policy.getConsecutiveInvalidLogins()) {
                        try {
                            idp.authenticate(bad);
                            fail("Should NOT be able to authenticate!!!");
                        } catch (InvalidCredentialFault e) {

                        }
                        invalidLogins = invalidLogins + 1;
                        performAndValidateMultipleAudit(idp, cred.getUserId(), a.getUserId(), AuditConstants.SYSTEM_ID,
                            IdentityProviderAudit.InvalidLogin, invalidLogins);
                    } else {
                        localCount = 0;
                        try {
                            idp.authenticate(getCredential(a));
                            fail("Should NOT be able to authenticate!!!");
                        } catch (InvalidCredentialFault e) {

                        }
                        invalidLogins = invalidLogins + 1;
                        performAndValidateMultipleAudit(idp, cred.getUserId(), a.getUserId(), AuditConstants.SYSTEM_ID,
                            IdentityProviderAudit.InvalidLogin, invalidLogins);
                        Thread.sleep((policy.getLockout().getSeconds() * 1000) + 100);
                        successfulLogins = successfulLogins + 1;
                        verifyAuthentication(cred.getUserId(), idp, a, successfulLogins);
                        try {
                            idp.authenticate(bad);
                            fail("Should NOT be able to authenticate!!!");
                        } catch (InvalidCredentialFault e) {

                        }
                        invalidLogins = invalidLogins + 1;
                        performAndValidateMultipleAudit(idp, cred.getUserId(), a.getUserId(), AuditConstants.SYSTEM_ID,
                            IdentityProviderAudit.InvalidLogin, invalidLogins);
                    }
                    localCount = localCount + 1;

                }
            }
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        } finally {
            try {
                idp.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testAuthenticateBadPassword() {
        IdentityProvider idp = null;
        try {
            IdentityProviderProperties props = Utils.getIdentityProviderProperties();
            props.setRegistrationPolicy(new AutomaticRegistrationPolicy());
            idp = new IdentityProvider(props, db, ca, eventManager);
            Application a = createApplication();
            idp.register(a);
            BasicAuthCredential cred = getAdminCreds();
            performAndValidateSingleAudit(idp, cred.getUserId(), a.getUserId(), AuditConstants.SYSTEM_ID,
                IdentityProviderAudit.Registration);
            LocalUserFilter uf = new LocalUserFilter();
            uf.setUserId(a.getUserId());
            LocalUser[] users = idp.findUsers(cred.getUserId(), uf);
            assertEquals(1, users.length);
            assertEquals(LocalUserStatus.Active, users[0].getStatus());
            assertEquals(LocalUserRole.Non_Administrator, users[0].getRole());
            BasicAuthentication c = getCredential(a);
            c.setPassword("bad password");
            try {
                idp.authenticate(c);
                fail("Should not be able to authenticate with a bad password!!!");
            } catch (InvalidCredentialFault f) {

            }
            performAndValidateSingleAudit(idp, cred.getUserId(), a.getUserId(), AuditConstants.SYSTEM_ID,
                IdentityProviderAudit.InvalidLogin);
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        } finally {
            try {
                idp.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testAuthenticateInvalidCredential() {
        IdentityProvider idp = null;
        try {
            IdentityProviderProperties props = Utils.getIdentityProviderProperties();
            props.setRegistrationPolicy(new AutomaticRegistrationPolicy());
            idp = new IdentityProvider(props, db, ca, eventManager);
            Application a = createApplication();
            idp.register(a);
            BasicAuthCredential cred = getAdminCreds();
            performAndValidateSingleAudit(idp, cred.getUserId(), a.getUserId(), AuditConstants.SYSTEM_ID,
                IdentityProviderAudit.Registration);
            LocalUserFilter uf = new LocalUserFilter();
            uf.setUserId(a.getUserId());
            LocalUser[] users = idp.findUsers(cred.getUserId(), uf);
            assertEquals(1, users.length);
            assertEquals(LocalUserStatus.Active, users[0].getStatus());
            assertEquals(LocalUserRole.Non_Administrator, users[0].getRole());
            OneTimePassword c = new OneTimePassword();
            c.setUserId(a.getUserId());
            c.setOneTimePassword("onetimepassword");
            try {
                idp.authenticate(c);
                fail("Should not be able to authenticate with an credential that is not supported!!!");
            } catch (CredentialNotSupportedFault f) {

            }
            performAndValidateSingleAudit(idp, cred.getUserId(), "UNKNOWN", AuditConstants.SYSTEM_ID,
                IdentityProviderAudit.InvalidLogin);
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        } finally {
            try {
                idp.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testChangePassword() {
        IdentityProvider idp = null;
        try {
            IdentityProviderProperties props = Utils.getIdentityProviderProperties();
            props.setRegistrationPolicy(new AutomaticRegistrationPolicy());
            idp = new IdentityProvider(props, db, ca, eventManager);
            Application a = createApplication();
            idp.register(a);
            BasicAuthCredential cred = getAdminCreds();
            performAndValidateSingleAudit(idp, cred.getUserId(), a.getUserId(), AuditConstants.SYSTEM_ID,
                IdentityProviderAudit.Registration);
            LocalUserFilter uf = new LocalUserFilter();
            uf.setUserId(a.getUserId());
            LocalUser[] users = idp.findUsers(cred.getUserId(), uf);
            assertEquals(1, users.length);
            assertEquals(LocalUserStatus.Active, users[0].getStatus());
            assertEquals(LocalUserRole.Non_Administrator, users[0].getRole());
            verifyAuthentication(cred.getUserId(), idp, a);
            BasicAuthentication c = getCredential(a);
            String newPassword = "$W0rdD0ct0R$2";
            idp.changePassword(getCredential(a), newPassword);
            performAndValidateSingleAudit(idp, cred.getUserId(), a.getUserId(), AuditConstants.SYSTEM_ID,
                IdentityProviderAudit.PasswordChanged);
            try {
                idp.authenticate(c);
                fail("Should not be able to authenticate with the old password!!!");
            } catch (InvalidCredentialFault f) {

            }
            performAndValidateSingleAudit(idp, cred.getUserId(), a.getUserId(), AuditConstants.SYSTEM_ID,
                IdentityProviderAudit.InvalidLogin);
            a.setPassword(newPassword);
            verifyAuthentication(cred.getUserId(), idp, a, 2);
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        } finally {
            try {
                idp.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testChangePasswordToBadPassword() {
        IdentityProvider idp = null;
        try {
            IdentityProviderProperties props = Utils.getIdentityProviderProperties();
            props.setRegistrationPolicy(new AutomaticRegistrationPolicy());
            idp = new IdentityProvider(props, db, ca, eventManager);
            Application a = createApplication();
            idp.register(a);
            BasicAuthCredential cred = getAdminCreds();
            performAndValidateSingleAudit(idp, cred.getUserId(), a.getUserId(), AuditConstants.SYSTEM_ID,
                IdentityProviderAudit.Registration);
            LocalUserFilter uf = new LocalUserFilter();
            uf.setUserId(a.getUserId());
            LocalUser[] users = idp.findUsers(cred.getUserId(), uf);
            assertEquals(1, users.length);
            assertEquals(LocalUserStatus.Active, users[0].getStatus());
            assertEquals(LocalUserRole.Non_Administrator, users[0].getRole());
            verifyAuthentication(cred.getUserId(), idp, a);
            try {
                idp.changePassword(getCredential(a), "short");
                fail("Should not be able to change the password to something to short");
            } catch (InvalidUserPropertyFault f) {
            }

            try {
                idp.changePassword(getCredential(a), "$W0rdD0ct0R$$$$$$$$$$$$$$$$$$$$$$$W0rdD0ct0R$");
                fail("Should not be able to change the password to something to long");
            } catch (InvalidUserPropertyFault f) {
            }
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        } finally {
            try {
                idp.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testChangePasswordToSamePassword() {
        IdentityProvider idp = null;
        try {
            IdentityProviderProperties props = Utils.getIdentityProviderProperties();
            props.setRegistrationPolicy(new AutomaticRegistrationPolicy());
            idp = new IdentityProvider(props, db, ca, eventManager);
            Application a = createApplication();
            idp.register(a);
            BasicAuthCredential cred = getAdminCreds();
            performAndValidateSingleAudit(idp, cred.getUserId(), a.getUserId(), AuditConstants.SYSTEM_ID,
                IdentityProviderAudit.Registration);
            LocalUserFilter uf = new LocalUserFilter();
            uf.setUserId(a.getUserId());
            LocalUser[] users = idp.findUsers(cred.getUserId(), uf);
            assertEquals(1, users.length);
            assertEquals(LocalUserStatus.Active, users[0].getStatus());
            assertEquals(LocalUserRole.Non_Administrator, users[0].getRole());
            verifyAuthentication(cred.getUserId(), idp, a);
            try {
                idp.changePassword(getCredential(a), a.getPassword());
                fail("Should not be able to change the password to the existing password.");
            } catch (InvalidUserPropertyFault f) {
            }

        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        } finally {
            try {
                idp.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testRegistrationNoAddress2() {
        IdentityProvider idp = null;
        try {
            IdentityProviderProperties props = Utils.getIdentityProviderProperties();
            props.setRegistrationPolicy(new AutomaticRegistrationPolicy());
            idp = new IdentityProvider(props, db, ca, eventManager);
            Application a = createApplication();
            a.setAddress2(null);
            idp.register(a);
            BasicAuthCredential cred = getAdminCreds();
            performAndValidateSingleAudit(idp, cred.getUserId(), a.getUserId(), AuditConstants.SYSTEM_ID,
                IdentityProviderAudit.Registration);
            LocalUserFilter uf = new LocalUserFilter();
            uf.setUserId(a.getUserId());
            LocalUser[] users = idp.findUsers(cred.getUserId(), uf);
            assertEquals(1, users.length);
            assertEquals(LocalUserStatus.Active, users[0].getStatus());
            assertEquals(LocalUserRole.Non_Administrator, users[0].getRole());
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        } finally {
            try {
                idp.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testManualRegistration() {
        IdentityProvider idp = null;
        try {
            assertEquals(ManualRegistrationPolicy.class, Utils.getIdentityProviderProperties().getRegistrationPolicy()
                .getClass());
            idp = Utils.getIdentityProvider();

            Application a = createApplication();
            assertFalse(idp.doesUserExist(a.getUserId()));
            idp.register(a);
            assertTrue(idp.doesUserExist(a.getUserId()));
            BasicAuthCredential cred = getAdminCreds();
            performAndValidateSingleAudit(idp, cred.getUserId(), a.getUserId(), AuditConstants.SYSTEM_ID,
                IdentityProviderAudit.Registration);
            LocalUserFilter uf = new LocalUserFilter();
            uf.setUserId(a.getUserId());
            LocalUser[] users = idp.findUsers(cred.getUserId(), uf);
            assertEquals(1, users.length);
            assertEquals(LocalUserStatus.Pending, users[0].getStatus());
            assertEquals(LocalUserRole.Non_Administrator, users[0].getRole());
            users[0].setStatus(LocalUserStatus.Active);
            idp.updateUser(cred.getUserId(), users[0]);
            performAndValidateSingleAudit(idp, cred.getUserId(), a.getUserId(), cred.getUserId(),
                IdentityProviderAudit.LocalAccountUpdated);
            users = idp.findUsers(cred.getUserId(), uf);
            assertEquals(1, users.length);
            assertEquals(LocalUserStatus.Active, users[0].getStatus());
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        } finally {
            try {
                idp.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testBadRegisterWithIdP() {
        IdentityProvider idp = null;
        try {
            IdentityProviderProperties props = Utils.getIdentityProviderProperties();
            props.setRegistrationPolicy(new AutomaticRegistrationPolicy());
            idp = new IdentityProvider(props, db, ca, eventManager);
            // test the password length too long
            try {
                Application a = createTooLongPasswordApplication();
                idp.register(a);
                fail("Should not be able to register with a password of this length.");
            } catch (InvalidUserPropertyFault iupf) {
            }

            // test the password length is too short
            try {
                Application b = createTooShortPasswordApplication();
                idp.register(b);
                fail("Should not be able to register with a password of this length.");
            } catch (InvalidUserPropertyFault iupf) {
            }

            // test the UserId length is too long
            try {
                Application c = createTooLongUserIdApplication();
                idp.register(c);
                fail("Should not be able to register with a UserId of this length.");
            } catch (InvalidUserPropertyFault iupf) {
            }

            // test the UserId length is too short
            try {
                Application d = createTooShortUserIdApplication();
                idp.register(d);
                fail("Should not be able to register with a UserId of this length.");
            } catch (InvalidUserPropertyFault iupf) {
            }
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        } finally {
            try {
                idp.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testPasswordConstraints() {
        IdentityProvider idp = null;
        try {
            IdentityProviderProperties props = Utils.getIdentityProviderProperties();
            props.setRegistrationPolicy(new AutomaticRegistrationPolicy());
            idp = new IdentityProvider(props, db, ca, eventManager);
            assertTrue(isValidPassword(idp, UserManager.ADMIN_PASSWORD));
            assertFalse(isValidPassword(idp, "$$$$User44"));
            assertFalse(isValidPassword(idp, "12345Dorian6789"));
            assertFalse(isValidPassword(idp, "12345dorian6789$"));
            assertFalse(isValidPassword(idp, "12345DORIAN6789$"));
            assertFalse(isValidPassword(idp, "$$$$Dorian$$$$"));
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        } finally {
            try {
                idp.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testBadRemoveIdPUserNoSuchUser() {
        IdentityProvider idp = null;
        try {
            IdentityProviderProperties props = Utils.getIdentityProviderProperties();
            props.setRegistrationPolicy(new AutomaticRegistrationPolicy());
            idp = new IdentityProvider(props, db, ca, eventManager);
            Application a = createApplication();
            idp.register(a);
            BasicAuthCredential cred = getAdminCreds();
            performAndValidateSingleAudit(idp, cred.getUserId(), a.getUserId(), AuditConstants.SYSTEM_ID,
                IdentityProviderAudit.Registration);
            LocalUserFilter uf = new LocalUserFilter();
            LocalUser[] us = idp.findUsers(cred.getUserId(), uf);
            assertEquals(2, us.length);

            // create a userId that does not exist
            String userId = "No_SUCH_USER";
            idp.removeUser(cred.getUserId(), userId);
            LocalUserFilter f = new LocalUserFilter();
            LocalUser[] users = idp.findUsers(cred.getUserId(), f);
            assertEquals(2, users.length);
        } catch (PermissionDeniedFault pdf) {
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        } finally {
            try {
                idp.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testBadRegisterWithIdPTwoIdenticalUsers() {
        IdentityProvider idp = null;
        try {
            IdentityProviderProperties props = Utils.getIdentityProviderProperties();
            props.setRegistrationPolicy(new AutomaticRegistrationPolicy());
            idp = new IdentityProvider(props, db, ca, eventManager);
            Application a = createApplication();
            BasicAuthCredential cred = getAdminCreds();
            idp.register(a);
            performAndValidateSingleAudit(idp, cred.getUserId(), a.getUserId(), AuditConstants.SYSTEM_ID,
                IdentityProviderAudit.Registration);
            Application b = a;
            idp.register(b);
            fail("Should not be able to register two identical users.");
        } catch (InvalidUserPropertyFault iupf) {
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        } finally {
            try {
                idp.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testMultipleUsers() {
        IdentityProvider idp = null;
        try {
            idp = Utils.getIdentityProvider();

            BasicAuthCredential cred = getAdminCreds();
            int times = 3;
            for (int i = 0; i < times; i++) {
                Application a = createApplication();
                idp.register(a);
                performAndValidateSingleAudit(idp, cred.getUserId(), a.getUserId(), AuditConstants.SYSTEM_ID,
                    IdentityProviderAudit.Registration);
                LocalUserFilter uf = new LocalUserFilter();
                uf.setUserId(a.getUserId());
                LocalUser[] users = idp.findUsers(cred.getUserId(), uf);
                assertEquals(1, users.length);
                assertEquals(LocalUserStatus.Pending, users[0].getStatus());
                assertEquals(LocalUserRole.Non_Administrator, users[0].getRole());
                users[0].setStatus(LocalUserStatus.Active);
                idp.updateUser(cred.getUserId(), users[0]);
                performAndValidateSingleAudit(idp, cred.getUserId(), a.getUserId(), cred.getUserId(),
                    IdentityProviderAudit.LocalAccountUpdated);
                users = idp.findUsers(cred.getUserId(), uf);
                assertEquals(1, users.length);
                assertEquals(LocalUserStatus.Active, users[0].getStatus());
                uf.setUserId("user");
                users = idp.findUsers(cred.getUserId(), uf);
                assertEquals(i + 1, users.length);
                BasicAuthentication auth = new BasicAuthentication();
                auth.setUserId(a.getUserId());
                auth.setPassword(a.getPassword());
                gov.nih.nci.cagrid.opensaml.SAMLAssertion saml = idp.authenticate(auth);
                performAndValidateSingleAudit(idp, cred.getUserId(), a.getUserId(), AuditConstants.SYSTEM_ID,
                    IdentityProviderAudit.SuccessfulLogin);
                assertNotNull(saml);
                this.verifySAMLAssertion(saml, idp, a);
            }

            LocalUserFilter uf = new LocalUserFilter();
            LocalUser[] users = idp.findUsers(cred.getUserId(), uf);
            assertEquals(times + 1, users.length);
            for (int i = 0; i < users.length; i++) {
                LocalUserFilter f = new LocalUserFilter();
                f.setUserId(users[i].getUserId());
                LocalUser[] us = idp.findUsers(cred.getUserId(), f);
                assertEquals(1, us.length);
                us[0].setFirstName("NEW NAME");
                idp.updateUser(cred.getUserId(), us[0]);
                int updateCount = 2;
                if (us[0].getUserId().equals(cred.getUserId())) {
                    updateCount = 1;
                }
                performAndValidateMultipleAudit(idp, cred.getUserId(), us[0].getUserId(), cred.getUserId(),
                    IdentityProviderAudit.LocalAccountUpdated, updateCount);
                LocalUser[] us2 = idp.findUsers(cred.getUserId(), f);
                assertEquals(1, us2.length);
                assertEquals(us[0], us2[0]);
                if (!users[i].getUserId().equals(cred.getUserId())) {
                    idp.removeUser(cred.getUserId(), users[i].getUserId());
                    performAndValidateSingleAudit(idp, cred.getUserId(), users[i].getUserId(), cred.getUserId(),
                        IdentityProviderAudit.LocalAccountRemoved);
                    us = idp.findUsers(cred.getUserId(), f);
                    assertEquals(0, us.length);
                }
            }
            users = idp.findUsers(cred.getUserId(), uf);
            assertEquals(1, users.length);
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        } finally {
            try {
                idp.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private boolean isValidPassword(IdentityProvider idp, String password) throws Exception {
        Application app = this.createApplication(password);
        try {
            idp.register(app);
        } catch (InvalidUserPropertyFault f) {
            if (f.getFaultString().equals(UserManager.INVALID_PASSWORD_MESSAGE)) {
                return false;
            }
        }

        return true;

    }


    private void verifyAuthentication(String adminId, IdentityProvider idp, Application a) throws Exception {
        SAMLAssertion saml = idp.authenticate(getCredential(a));
        verifySAMLAssertion(saml, idp, a);
        performAndValidateSingleAudit(idp, adminId, a.getUserId(), AuditConstants.SYSTEM_ID,
            IdentityProviderAudit.SuccessfulLogin);
    }


    private void verifyAuthentication(String adminId, IdentityProvider idp, Application a, int totalLogins)
        throws Exception {
        SAMLAssertion saml = idp.authenticate(getCredential(a));
        verifySAMLAssertion(saml, idp, a);
        performAndValidateMultipleAudit(idp, adminId, a.getUserId(), AuditConstants.SYSTEM_ID,
            IdentityProviderAudit.SuccessfulLogin, totalLogins);
    }


    public void verifySAMLAssertion(SAMLAssertion saml, IdentityProvider idp, Application app) throws Exception {
        assertNotNull(saml);
        saml.verify(idp.getIdPCertificate());

        assertEquals(idp.getIdPCertificate().getSubjectDN().toString(), saml.getIssuer());
        Iterator itr = saml.getStatements();
        int statementCount = 0;
        boolean authFound = false;
        while (itr.hasNext()) {
            statementCount = statementCount + 1;
            SAMLStatement stmt = (SAMLStatement) itr.next();
            if (stmt instanceof SAMLAuthenticationStatement) {
                if (authFound) {
                    assertTrue(false);
                } else {
                    authFound = true;
                }
                SAMLAuthenticationStatement auth = (SAMLAuthenticationStatement) stmt;
                assertEquals(app.getUserId(), auth.getSubject().getNameIdentifier().getName());
                assertEquals("urn:oasis:names:tc:SAML:1.0:am:password", auth.getAuthMethod());
            }

            if (stmt instanceof SAMLAttributeStatement) {
                String uid = Utils.getAttribute(saml, SAMLConstants.UID_ATTRIBUTE_NAMESPACE,
                    SAMLConstants.UID_ATTRIBUTE);
                assertNotNull(uid);
                String email = Utils.getAttribute(saml, SAMLConstants.EMAIL_ATTRIBUTE_NAMESPACE,
                    SAMLConstants.EMAIL_ATTRIBUTE);
                assertNotNull(email);
                String firstName = Utils.getAttribute(saml, SAMLConstants.FIRST_NAME_ATTRIBUTE_NAMESPACE,
                    SAMLConstants.FIRST_NAME_ATTRIBUTE);
                assertNotNull(firstName);
                String lastName = Utils.getAttribute(saml, SAMLConstants.LAST_NAME_ATTRIBUTE_NAMESPACE,
                    SAMLConstants.LAST_NAME_ATTRIBUTE);
                assertNotNull(lastName);

                assertEquals(app.getUserId(), uid);
                assertEquals(app.getFirstName(), firstName);
                assertEquals(app.getLastName(), lastName);
                assertEquals(app.getEmail(), email);
            }

        }

        assertEquals(2, statementCount);
        assertTrue(authFound);
    }


    private BasicAuthentication getCredential(Application app) {
        BasicAuthentication cred = new BasicAuthentication();
        cred.setUserId(app.getUserId());
        cred.setPassword(app.getPassword());
        return cred;
    }


    private BasicAuthCredential getAdminCreds() {
        BasicAuthCredential cred = new BasicAuthCredential();
        cred.setUserId(UserManager.ADMIN_USER_ID);
        cred.setPassword(UserManager.ADMIN_PASSWORD);
        return cred;
    }


    private Application createApplication() {
        return createApplication(count + "$W0rdD0ct0R$");
    }


    private Application createApplication(String password) {
        Application u = new Application();
        u.setUserId(count + "user");
        u.setEmail(count + "user@mail.com");
        u.setPassword(password);
        u.setFirstName(count + "first");
        u.setLastName(count + "last");
        u.setAddress(count + "address");
        u.setAddress2(count + "address2");
        u.setCity("Columbus");
        u.setState(StateCode.OH);
        u.setCountry(CountryCode.US);
        u.setZipcode("43210");
        u.setPhoneNumber("614-555-5555");
        u.setOrganization(count + "organization");
        count = count + 1;
        return u;
    }


    private Application createTooLongPasswordApplication() {
        Application u = new Application();
        u.setUserId(count + "user");
        u.setEmail(count + "user@mail.com");
        u.setPassword(count + "$W0rdD0ct0R$$$$$$$$$$$$$$$$$W0rdD0ct0R$");
        u.setFirstName(count + "first");
        u.setLastName(count + "last");
        u.setAddress(count + "address");
        u.setAddress2(count + "address2");
        u.setCity("Columbus");
        u.setState(StateCode.OH);
        u.setCountry(CountryCode.US);
        u.setZipcode("43210");
        u.setPhoneNumber("614-555-5555");
        u.setOrganization(count + "organization");
        count = count + 1;
        return u;
    }


    private Application createTooShortPasswordApplication() {
        Application u = new Application();
        u.setUserId(count + "user");
        u.setEmail(count + "user@mail.com");
        u.setPassword(count + "p");
        u.setFirstName(count + "first");
        u.setLastName(count + "last");
        u.setAddress(count + "address");
        u.setAddress2(count + "address2");
        u.setCity("Columbus");
        u.setState(StateCode.OH);
        u.setCountry(CountryCode.US);
        u.setZipcode("43210");
        u.setPhoneNumber("614-555-5555");
        u.setOrganization(count + "organization");
        count = count + 1;
        return u;
    }


    private Application createTooLongUserIdApplication() {
        Application u = new Application();
        u.setUserId(count + "thisuseridiswaytoolong");
        u.setEmail(count + "user@mail.com");
        u.setPassword(count + "password");
        u.setFirstName(count + "first");
        u.setLastName(count + "last");
        u.setAddress(count + "address");
        u.setAddress2(count + "address2");
        u.setCity("Columbus");
        u.setState(StateCode.OH);
        u.setCountry(CountryCode.US);
        u.setZipcode("43210");
        u.setPhoneNumber("614-555-5555");
        u.setOrganization(count + "organization");
        count = count + 1;
        return u;
    }


    private Application createTooShortUserIdApplication() {
        Application u = new Application();
        u.setUserId(count + "u");
        u.setEmail(count + "user@mail.com");
        u.setPassword(count + "password");
        u.setFirstName(count + "first");
        u.setLastName(count + "last");
        u.setAddress(count + "address");
        u.setAddress2(count + "address2");
        u.setCity("Columbus");
        u.setState(StateCode.OH);
        u.setCountry(CountryCode.US);
        u.setZipcode("43210");
        u.setPhoneNumber("614-555-5555");
        u.setOrganization(count + "organization");
        count = count + 1;
        return u;
    }


    protected void setUp() throws Exception {
        super.setUp();
        try {
            count = 0;
            db = Utils.getDB();
            assertEquals(0, db.getUsedConnectionCount());
            ca = Utils.getCA();
            eventManager = Utils.getEventManager();
            eventManager.clearHandlers();
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        }
    }


    protected void tearDown() throws Exception {
        super.setUp();
        try {
            eventManager.clearHandlers();
            assertEquals(0, db.getUsedConnectionCount());
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        }
    }


    private void performAndValidateMultipleAudit(IdentityProvider idp, String adminId, String target,
        String reportingParty, IdentityProviderAudit type, int count) throws Exception {
        IdentityProviderAuditFilter f = new IdentityProviderAuditFilter();
        f.setTargetId(target);
        f.setReportingPartyId(reportingParty);
        f.setAuditType(type);
        List<IdentityProviderAuditRecord> results = idp.performAudit(adminId, f);
        assertEquals(count, results.size());
    }


    private void performAndValidateSingleAudit(IdentityProvider idp, String adminId, String target,
        String reportingParty, IdentityProviderAudit type) throws Exception {
        IdentityProviderAuditFilter f = new IdentityProviderAuditFilter();
        f.setTargetId(target);
        f.setReportingPartyId(reportingParty);
        f.setAuditType(type);
        List<IdentityProviderAuditRecord> results = idp.performAudit(adminId, f);
        assertEquals(1, results.size());
        validateAuditingResult(results.get(0), target, reportingParty, type);
    }


    private void validateAuditingResult(IdentityProviderAuditRecord a, String target, String reportingParty,
        IdentityProviderAudit type) {
        assertEquals(target, a.getTargetId());
        assertEquals(reportingParty, a.getReportingPartyId());
        assertEquals(type, a.getAuditType());
    }

}
