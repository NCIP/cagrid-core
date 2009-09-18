package org.cagrid.gaards.dorian.idp;

import gov.nih.nci.cagrid.common.FaultUtil;
import junit.framework.TestCase;

import org.cagrid.gaards.authentication.BasicAuthentication;
import org.cagrid.gaards.dorian.stubs.types.NoSuchUserFault;
import org.cagrid.gaards.dorian.test.Utils;
import org.cagrid.tools.database.Database;

/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class TestUserManager extends TestCase {

	private Database db;

	private int count = 0;

	public void testMultipleUsers() {
		int userCount = 20;
		int activeNA = 0;
		int pendingNA = 0;
		int rejectedNA = 0;
		int suspendedNA = 0;

		int activeA = 1;
		int pendingA = 0;
		int rejectedA = 0;
		int suspendedA = 0;

		LocalUser[] users = new LocalUser[userCount];
		UserManager um = null;
		try {

			um = Utils.getIdPUserManager();

			for (int i = 0; i < users.length; i++) {
				if ((i % 8) == 0) {
					users[i] = makeUser(LocalUserRole.Non_Administrator,
							LocalUserStatus.Active);
					activeNA = activeNA + 1;
				} else if ((i % 8) == 1) {
					users[i] = makeUser(LocalUserRole.Non_Administrator,
							LocalUserStatus.Pending);
					pendingNA = pendingNA + 1;
				} else if ((i % 8) == 2) {
					users[i] = makeUser(LocalUserRole.Non_Administrator,
							LocalUserStatus.Rejected);
					rejectedNA = rejectedNA + 1;
				} else if ((i % 8) == 3) {
					users[i] = makeUser(LocalUserRole.Non_Administrator,
							LocalUserStatus.Suspended);
					suspendedNA = suspendedNA + 1;
				} else if ((i % 8) == 4) {
					users[i] = makeUser(LocalUserRole.Administrator,
							LocalUserStatus.Active);
					activeA = activeA + 1;
				} else if ((i % 8) == 5) {
					users[i] = makeUser(LocalUserRole.Administrator,
							LocalUserStatus.Pending);
					pendingA = pendingA + 1;
				} else if ((i % 8) == 6) {
					users[i] = makeUser(LocalUserRole.Administrator,
							LocalUserStatus.Rejected);
					rejectedA = rejectedA + 1;
				} else if ((i % 8) == 7) {
					users[i] = makeUser(LocalUserRole.Administrator,
							LocalUserStatus.Suspended);
					suspendedA = suspendedA + 1;
				}

				um.addUser(users[i]);
				String salt = um.getPasswordSecurityManager().getEntry(
						users[i].getUserId()).getDigestSalt();
				users[i].setPassword(PasswordSecurityManager.encrypt(users[i]
						.getPassword(), salt));
				assertTrue(um.userExists(users[i].getUserId()));
				LocalUser u = um.getUser(users[i].getUserId());
				assertEquals(users[i], u);

				LocalUser[] list = um.getUsers(null);
				assertEquals(i + 2, list.length);
				LocalUserFilter f = new LocalUserFilter();
				f.setStatus(LocalUserStatus.Active);
				f.setRole(LocalUserRole.Non_Administrator);
				assertEquals(activeNA, um.getUsers(f).length);

				f.setStatus(LocalUserStatus.Pending);
				assertEquals(pendingNA, um.getUsers(f).length);

				f.setStatus(LocalUserStatus.Rejected);
				assertEquals(rejectedNA, um.getUsers(f).length);

				f.setStatus(LocalUserStatus.Suspended);
				assertEquals(suspendedNA, um.getUsers(f).length);

				f.setStatus(LocalUserStatus.Active);
				f.setRole(LocalUserRole.Administrator);
				assertEquals(activeA, um.getUsers(f).length);

				f.setStatus(LocalUserStatus.Pending);
				assertEquals(pendingA, um.getUsers(f).length);

				f.setStatus(LocalUserStatus.Rejected);
				assertEquals(rejectedA, um.getUsers(f).length);

				f.setStatus(LocalUserStatus.Suspended);
				assertEquals(suspendedA, um.getUsers(f).length);

			}

			int numberOfUsers = users.length + 1;

			for (int i = 0; i < users.length; i++) {
				um.removeUser(users[i].getUserId());
				numberOfUsers = numberOfUsers - 1;
				if ((users[i].getStatus().equals(LocalUserStatus.Active))
						&& (users[i].getRole()
								.equals(LocalUserRole.Non_Administrator))) {
					activeNA = activeNA - 1;
				} else if ((users[i].getStatus().equals(LocalUserStatus.Pending))
						&& (users[i].getRole()
								.equals(LocalUserRole.Non_Administrator))) {
					pendingNA = pendingNA - 1;
				}
				if ((users[i].getStatus().equals(LocalUserStatus.Rejected))
						&& (users[i].getRole()
								.equals(LocalUserRole.Non_Administrator))) {
					rejectedNA = rejectedNA - 1;
				}
				if ((users[i].getStatus().equals(LocalUserStatus.Suspended))
						&& (users[i].getRole()
								.equals(LocalUserRole.Non_Administrator))) {
					users[i] = makeUser(LocalUserRole.Non_Administrator,
							LocalUserStatus.Suspended);
					suspendedNA = suspendedNA - 1;
				} else if ((users[i].getStatus().equals(LocalUserStatus.Active))
						&& (users[i].getRole()
								.equals(LocalUserRole.Administrator))) {
					activeA = activeA - 1;
				} else if ((users[i].getStatus().equals(LocalUserStatus.Pending))
						&& (users[i].getRole()
								.equals(LocalUserRole.Administrator))) {
					pendingA = pendingA - 1;
				} else if ((users[i].getStatus().equals(LocalUserStatus.Rejected))
						&& (users[i].getRole()
								.equals(LocalUserRole.Administrator))) {
					rejectedA = rejectedA - 1;
				} else if ((users[i].getStatus()
						.equals(LocalUserStatus.Suspended))
						&& (users[i].getRole()
								.equals(LocalUserRole.Administrator))) {
					suspendedA = suspendedA - 1;
				}
				assertFalse(um.userExists(users[i].getEmail()));

				LocalUser[] list = um.getUsers(null);
				assertEquals(numberOfUsers, list.length);
				LocalUserFilter f = new LocalUserFilter();
				f.setStatus(LocalUserStatus.Active);
				f.setRole(LocalUserRole.Non_Administrator);
				assertEquals(activeNA, um.getUsers(f).length);

				f.setStatus(LocalUserStatus.Pending);
				assertEquals(pendingNA, um.getUsers(f).length);

				f.setStatus(LocalUserStatus.Rejected);
				assertEquals(rejectedNA, um.getUsers(f).length);

				f.setStatus(LocalUserStatus.Suspended);
				assertEquals(suspendedNA, um.getUsers(f).length);

				f.setStatus(LocalUserStatus.Active);
				f.setRole(LocalUserRole.Administrator);
				assertEquals(activeA, um.getUsers(f).length);

				f.setStatus(LocalUserStatus.Pending);
				assertEquals(pendingA, um.getUsers(f).length);

				f.setStatus(LocalUserStatus.Rejected);
				assertEquals(rejectedA, um.getUsers(f).length);

				f.setStatus(LocalUserStatus.Suspended);
				assertEquals(suspendedA, um.getUsers(f).length);

			}

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

	public void testChangeStatus() {
		UserManager um = null;
		try {

			um = Utils.getIdPUserManager();
			LocalUser u1 = makeActiveUser();
			um.addUser(u1);
			assertTrue(um.userExists(u1.getUserId()));
			u1.setStatus(LocalUserStatus.Suspended);
			um.updateUser(u1);

			String salt = um.getPasswordSecurityManager().getEntry(
					u1.getUserId()).getDigestSalt();
			u1.setPassword(PasswordSecurityManager.encrypt(u1.getPassword(),
					salt));
			LocalUser u2 = um.getUser(u1.getUserId());

			assertEquals(u1.getPassword(), u2.getPassword());
			assertEquals(u1, u2);

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

	public void testSystemMaxPasswordLength() {
		UserManager um = null;
		try {
			um = Utils.getIdPUserManager();
			LocalUser u1 = makeActiveUser();
			String password = u1.getPassword();

			StringBuffer sb = new StringBuffer();
			sb.append(password);
			int size = (UserManager.SYSTEM_MAX_PASSWORD_LENGTH + 1)
					- sb.length();
			for (int i = 0; i < size; i++) {
				sb.append("a");
			}
			u1.setPassword(sb.toString());
			try {
				um.addUser(u1);
				fail("Should not be able to add a user that specified a password with a greater length the system max!!!");
			} catch (Exception e) {
				if (gov.nih.nci.cagrid.common.Utils.getExceptionMessage(e)
						.indexOf(UserManager.SYSTEM_MAX_PASSWORD_ERROR_PREFIX) == -1) {
					fail("Should not be able to add a user that specified a password with a greater length the system max!!!");
				}
			}
			u1.setPassword(password);
			um.addUser(u1);
			assertTrue(um.userExists(u1.getUserId()));
			BasicAuthentication ba = new BasicAuthentication();
			ba.setUserId(u1.getUserId());
			ba.setPassword(password);
			um.authenticateAndVerifyUser(ba,null);

			ba.setPassword(sb.toString());
			try {
				um.authenticateAndVerifyUser(ba,null);
				fail("Should not be able to authenticate with a password with a greater length the system max!!!");
			} catch (Exception e) {
				if (gov.nih.nci.cagrid.common.Utils.getExceptionMessage(e)
						.indexOf(UserManager.PASSWORD_ERROR_MESSAGE) == -1) {
					fail("Should not be able to authenticate with a password with a greater length the system max!!!");
				}
			}

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

	public void testChangeRole() {
		UserManager um = null;
		try {

			um = Utils.getIdPUserManager();
			LocalUser u1 = makeActiveUser();
			um.addUser(u1);
			assertTrue(um.userExists(u1.getUserId()));
			u1.setRole(LocalUserRole.Administrator);
			um.updateUser(u1);

			String salt = um.getPasswordSecurityManager().getEntry(
					u1.getUserId()).getDigestSalt();
			u1.setPassword(PasswordSecurityManager.encrypt(u1.getPassword(),
					salt));

			LocalUser u2 = um.getUser(u1.getUserId());
			assertEquals(u1, u2);
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

	public void testChangePassword() {
		UserManager um = null;
		try {

			um = Utils.getIdPUserManager();
			LocalUser u1 = makeActiveUser();
			um.addUser(u1);
			assertTrue(um.userExists(u1.getUserId()));
			u1.setPassword("$W0rdD0ct0R$2");
			um.updateUser(u1);

			LocalUser u2 = um.getUser(u1.getUserId());

			String salt = um.getPasswordSecurityManager().getEntry(
					u1.getUserId()).getDigestSalt();
			u1.setPassword(PasswordSecurityManager.encrypt(u1.getPassword(),
					salt));
			assertEquals(u1.getPassword(), u2.getPassword());

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

	public void testUpdateUser() {
		UserManager um = null;
		try {

			um = Utils.getIdPUserManager();
			LocalUser u1 = makeActiveUser();
			um.addUser(u1);
			assertTrue(um.userExists(u1.getUserId()));
			String password = u1.getPassword();
			u1.setPassword(null);
			u1.setFirstName("changedfirst");
			u1.setLastName("changedlast");
			u1.setAddress("changedaddress");
			u1.setAddress2("changedaddress2");
			u1.setCity("New York");
			u1.setState(StateCode.NY);
			u1.setCountry(CountryCode.AG);
			u1.setZipcode("11776");
			u1.setPhoneNumber("718-555-5555");
			u1.setOrganization("changedorganization");
			u1.setStatus(LocalUserStatus.Suspended);
			u1.setRole(LocalUserRole.Administrator);
			um.updateUser(u1);
			u1.getPasswordSecurity().setDigestSalt(null);
			LocalUser u2 = um.getUser(u1.getUserId(),false);
			
			assertEquals(u1, u2);

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

	public void testSingleUser() {
		UserManager um = null;
		try {

			um = Utils.getIdPUserManager();
			LocalUser u1 = makeActiveUser();
			um.addUser(u1);
			String salt = um.getPasswordSecurityManager().getEntry(
					u1.getUserId()).getDigestSalt();
			u1.setPassword(PasswordSecurityManager.encrypt(u1.getPassword(),
					salt));
			assertTrue(um.userExists(u1.getUserId()));
			LocalUser u2 = um.getUser(u1.getUserId());
			assertEquals(u1, u2);

			LocalUser[] list = um.getUsers(null);
			assertEquals(2, list.length);
			LocalUserFilter f = new LocalUserFilter();
			f.setStatus(LocalUserStatus.Active);
			f.setRole(LocalUserRole.Non_Administrator);
			assertEquals(1, um.getUsers(f).length);
			f.setStatus(LocalUserStatus.Pending);
			assertEquals(0, um.getUsers(f).length);
			f.setStatus(LocalUserStatus.Rejected);
			assertEquals(0, um.getUsers(f).length);
			f.setStatus(LocalUserStatus.Suspended);
			assertEquals(0, um.getUsers(f).length);
			f.setStatus(LocalUserStatus.Active);
			f.setRole(LocalUserRole.Administrator);
			assertEquals(1, um.getUsers(f).length);
			f.setStatus(LocalUserStatus.Pending);
			assertEquals(0, um.getUsers(f).length);
			f.setStatus(LocalUserStatus.Rejected);
			assertEquals(0, um.getUsers(f).length);
			f.setStatus(LocalUserStatus.Suspended);
			assertEquals(0, um.getUsers(f).length);
			um.removeUser(u1.getUserId());
			assertFalse(um.userExists(u1.getEmail()));

			try {
				um.getUser(u1.getEmail());
				assertTrue(false);
			} catch (NoSuchUserFault fs) {

			}

			assertEquals(1, um.getUsers(null).length);

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

	public void testFindUsers() {
		UserManager um = null;
		try {

			um = Utils.getIdPUserManager();
			int size = 10;

			for (int i = 0; i < size; i++) {
				um.addUser(makeUser(LocalUserRole.Non_Administrator,
						LocalUserStatus.Active));
			}
			assertEquals(size, um.getUsers(getActiveUserFilter()).length);

			// test email address
			LocalUserFilter fid = getActiveUserFilter();
			fid.setUserId("user");
			assertEquals(size, um.getUsers(fid).length);
			fid.setUserId("XX");
			assertEquals(0, um.getUsers(fid).length);

			// test email address
			LocalUserFilter f1 = getActiveUserFilter();
			f1.setEmail("@mail.com");
			assertEquals(size, um.getUsers(f1).length);
			f1.setEmail("@mail.");
			assertEquals(size, um.getUsers(f1).length);
			f1.setEmail("XX");
			assertEquals(0, um.getUsers(f1).length);

			// Test First Name and Last Name
			LocalUserFilter f2 = getActiveUserFilter();
			f2.setFirstName("firs");
			assertEquals(size, um.getUsers(f2).length);
			f2.setLastName("ast");
			assertEquals(size, um.getUsers(f2).length);
			f2.setFirstName("XX");
			assertEquals(0, um.getUsers(f2).length);
			f2.setFirstName(null);
			assertEquals(size, um.getUsers(f2).length);

			// Test Organization
			LocalUserFilter f0 = getActiveUserFilter();
			f0.setOrganization("org");
			assertEquals(size, um.getUsers(f0).length);
			f0.setOrganization("XX");
			assertEquals(0, um.getUsers(f0).length);

			// Test Address
			LocalUserFilter f3 = getActiveUserFilter();
			f3.setAddress("address");
			assertEquals(size, um.getUsers(f3).length);
			f3.setAddress("XX");
			assertEquals(0, um.getUsers(f3).length);

			// Test Address 2
			LocalUserFilter f4 = getActiveUserFilter();
			f4.setAddress2("address2");
			assertEquals(size, um.getUsers(f4).length);
			f4.setAddress2("XX");
			assertEquals(0, um.getUsers(f4).length);

			// Test City and State
			LocalUserFilter f5 = getActiveUserFilter();
			f5.setCity("Columbus");
			assertEquals(size, um.getUsers(f5).length);
			f5.setState(StateCode.OH);
			assertEquals(size, um.getUsers(f5).length);
			f5.setCity(null);
			assertEquals(size, um.getUsers(f5).length);
			f5.setState(null);
			assertEquals(size, um.getUsers(f5).length);

			// Test Zip Code
			LocalUserFilter f6 = getActiveUserFilter();
			f6.setZipcode("43210");
			assertEquals(size, um.getUsers(f6).length);
			f6.setZipcode("XX");
			assertEquals(0, um.getUsers(f6).length);

			// Test country
			LocalUserFilter cf = getActiveUserFilter();
			cf.setCountry(CountryCode.US);
			assertEquals(size, um.getUsers(cf).length);

			// Test Phone Number
			LocalUserFilter f7 = getActiveUserFilter();
			f7.setPhoneNumber("614-555-5555");
			assertEquals(size, um.getUsers(f7).length);
			f7.setPhoneNumber("XX");
			assertEquals(0, um.getUsers(f7).length);

			// test for each user

			for (int i = 0; i < size; i++) {

				// test email address
				LocalUserFilter all = getActiveUserFilter();

				LocalUserFilter uid = getActiveUserFilter();
				uid.setUserId(i + "user");
				all.setUserId(i + "user");
				assertEquals(1, um.getUsers(uid).length);
				assertEquals(1, um.getUsers(all).length);

				LocalUserFilter u1 = getActiveUserFilter();
				u1.setEmail(i + "user@mail.com");
				all.setEmail(i + "user@mail.com");
				assertEquals(1, um.getUsers(u1).length);
				assertEquals(1, um.getUsers(all).length);

				// Test First Name
				LocalUserFilter u2 = getActiveUserFilter();
				u2.setFirstName(i + "first");
				all.setFirstName(i + "first");
				assertEquals(1, um.getUsers(u2).length);
				assertEquals(1, um.getUsers(all).length);

				// Test Last Name
				LocalUserFilter u3 = getActiveUserFilter();
				u3.setLastName(i + "last");
				all.setLastName(i + "last");
				assertEquals(1, um.getUsers(u3).length);
				assertEquals(1, um.getUsers(all).length);

				// Test Organization
				LocalUserFilter u4 = getActiveUserFilter();
				u4.setOrganization(i + "organization");
				all.setOrganization(i + "organization");
				assertEquals(1, um.getUsers(u4).length);
				assertEquals(1, um.getUsers(all).length);

				// Test Address
				LocalUserFilter u5 = getActiveUserFilter();
				u5.setAddress(i + "address");
				all.setAddress(i + "address");
				assertEquals(1, um.getUsers(u5).length);
				assertEquals(1, um.getUsers(all).length);

				// Test Address 2
				LocalUserFilter u6 = getActiveUserFilter();
				u6.setAddress2(i + "address2");
				all.setAddress2(i + "address2");
				assertEquals(1, um.getUsers(u6).length);
				assertEquals(1, um.getUsers(all).length);

				all.setCity("Columbus");
				all.setState(StateCode.OH);
				all.setCountry(CountryCode.US);
				all.setZipcode("43210");
				all.setPhoneNumber("614-555-5555");
				assertEquals(1, um.getUsers(all).length);
			}

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

	public void testForeignUser() {
		UserManager um = null;
		try {

			um = Utils.getIdPUserManager();

			LocalUser u = new LocalUser();
			u.setUserId("user");
			u.setEmail("user@mail.com");
			u.setPassword("$W0rdD0ct0R$");
			u.setFirstName("first");
			u.setLastName("last");
			u.setAddress("address");
			u.setAddress2("address2");
			u.setCity("Somewhere");
			u.setState(StateCode.Outside_US);
			u.setZipcode("G12 8QQ");
			u.setCountry(CountryCode.AE);
			u.setPhoneNumber("+44 141 330 4119");
			u.setOrganization("organization");
			u.setStatus(LocalUserStatus.Active);
			u.setRole(LocalUserRole.Non_Administrator);
			um.addUser(u);
			assertTrue(um.userExists(u.getUserId()));

			LocalUser u2 = um.getUser(u.getUserId());
			String salt = um.getPasswordSecurityManager().getEntry(
					u.getUserId()).getDigestSalt();
			u.setPassword(PasswordSecurityManager
					.encrypt(u.getPassword(), salt));
			assertEquals(u, u2);

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

	private LocalUserFilter getActiveUserFilter() {
		LocalUserFilter filter = new LocalUserFilter();
		filter.setStatus(LocalUserStatus.Active);
		filter.setRole(LocalUserRole.Non_Administrator);
		return filter;
	}

	private LocalUser makeActiveUser() {
		return makeUser(LocalUserRole.Non_Administrator, LocalUserStatus.Active);
	}

	private LocalUser makeUser(LocalUserRole role, LocalUserStatus status) {
		LocalUser u = new LocalUser();
		u.setUserId(count + "user");
		u.setEmail(count + "user@mail.com");
		u.setPassword(count + "$W0rdD0ct0R$");
		u.setFirstName(count + "first");
		u.setLastName(count + "last");
		u.setAddress(count + "address");
		u.setAddress2(count + "address2");
		u.setCity("Columbus");
		u.setState(StateCode.OH);
		u.setZipcode("43210");
		u.setCountry(CountryCode.US);
		u.setPhoneNumber("614-555-5555");
		u.setOrganization(count + "organization");
		u.setStatus(status);
		u.setRole(role);
		count = count + 1;
		return u;
	}

	protected void setUp() throws Exception {
		super.setUp();
		try {
			count = 0;
			db = Utils.getDB();
			assertEquals(0, db.getUsedConnectionCount());
		} catch (Exception e) {
			FaultUtil.printFault(e);
			assertTrue(false);
		}
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
}
