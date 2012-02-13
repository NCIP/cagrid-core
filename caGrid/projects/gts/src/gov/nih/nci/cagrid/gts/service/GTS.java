package gov.nih.nci.cagrid.gts.service;

import gov.nih.nci.cagrid.gts.bean.AuthorityGTS;
import gov.nih.nci.cagrid.gts.bean.AuthorityPriorityUpdate;
import gov.nih.nci.cagrid.gts.bean.Lifetime;
import gov.nih.nci.cagrid.gts.bean.Permission;
import gov.nih.nci.cagrid.gts.bean.PermissionFilter;
import gov.nih.nci.cagrid.gts.bean.Status;
import gov.nih.nci.cagrid.gts.bean.TrustLevel;
import gov.nih.nci.cagrid.gts.bean.TrustLevels;
import gov.nih.nci.cagrid.gts.bean.TrustedAuthority;
import gov.nih.nci.cagrid.gts.bean.TrustedAuthorityFilter;
import gov.nih.nci.cagrid.gts.bean.X509CRL;
import gov.nih.nci.cagrid.gts.bean.X509Certificate;
import gov.nih.nci.cagrid.gts.client.GTSClient;
import gov.nih.nci.cagrid.gts.common.Database;
import gov.nih.nci.cagrid.gts.common.MySQLDatabase;
import gov.nih.nci.cagrid.gts.service.db.DBManager;
import gov.nih.nci.cagrid.gts.service.db.mysql.MySQLManager;
import gov.nih.nci.cagrid.gts.stubs.types.CertificateValidationFault;
import gov.nih.nci.cagrid.gts.stubs.types.GTSInternalFault;
import gov.nih.nci.cagrid.gts.stubs.types.IllegalAuthorityFault;
import gov.nih.nci.cagrid.gts.stubs.types.IllegalPermissionFault;
import gov.nih.nci.cagrid.gts.stubs.types.IllegalTrustLevelFault;
import gov.nih.nci.cagrid.gts.stubs.types.IllegalTrustedAuthorityFault;
import gov.nih.nci.cagrid.gts.stubs.types.InvalidAuthorityFault;
import gov.nih.nci.cagrid.gts.stubs.types.InvalidPermissionFault;
import gov.nih.nci.cagrid.gts.stubs.types.InvalidTrustLevelFault;
import gov.nih.nci.cagrid.gts.stubs.types.InvalidTrustedAuthorityFault;
import gov.nih.nci.cagrid.gts.stubs.types.PermissionDeniedFault;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gaards.pki.CertUtil;
import org.globus.wsrf.impl.security.authorization.IdentityAuthorization;
import org.projectmobius.common.MobiusRunnable;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: TrustedAuthorityManager.java,v 1.1 2006/03/08 19:48:46 langella
 *          Exp $
 */
public class GTS implements TrustedAuthorityLevelRemover, TrustLevelLookup {

	public static boolean SYNC_WITH_AUTHORITIES = true;

	private Configuration conf;

	private String gtsURI;

	private TrustedAuthorityManager trust;

	private PermissionManager permissions;

	private TrustLevelManager trustLevelManager;

	private GTSAuthorityManager authority;

	private Log log;

	private Database db;


	public GTS(Configuration conf, String gtsURI) {
		this.conf = conf;
		this.gtsURI = gtsURI;
		log = LogFactory.getLog(this.getClass().getName());

		DBManager dbManager = new MySQLManager(new MySQLDatabase(this.conf.getConnectionManager(), this.conf
			.getGTSInternalId()));
		this.db = dbManager.getDatabase();
		trust = new TrustedAuthorityManager(this.gtsURI, this, dbManager);
		trustLevelManager = new TrustLevelManager(this.gtsURI, this, dbManager);
		permissions = new PermissionManager(dbManager);
		authority = new GTSAuthorityManager(gtsURI, conf.getAuthoritySyncTime(), dbManager);
		if (SYNC_WITH_AUTHORITIES) {
			MobiusRunnable runner = new MobiusRunnable() {
				public void execute() {
					synchronizeWithAuthorities();
				}
			};

			try {
				Thread t = new Thread(runner);
				t.setDaemon(true);
				t.start();
			} catch (Exception e) {
				log.error(e);
			}
		}
	}


	protected Database getDatabase() {
		return db;
	}


	public TrustedAuthority addTrustedAuthority(TrustedAuthority ta, String callerGridIdentity)
		throws GTSInternalFault, IllegalTrustedAuthorityFault, PermissionDeniedFault {
		checkServiceAdministrator(callerGridIdentity);
		return trust.addTrustedAuthority(ta);
	}


	public TrustedAuthority[] findTrustAuthorities(TrustedAuthorityFilter filter) throws GTSInternalFault {
		return trust.findTrustAuthorities(filter);
	}


	public boolean validate(X509Certificate cert, TrustedAuthorityFilter filter) throws GTSInternalFault,
		CertificateValidationFault {
		X509Certificate[] chain = new X509Certificate[1];
		chain[0] = cert;
		return this.validate(chain, filter);
	}


	public boolean validate(X509Certificate[] chain, TrustedAuthorityFilter filter) throws GTSInternalFault,
		CertificateValidationFault {
		boolean isValidated = false;
		TrustedAuthority[] list = trust.findTrustAuthorities(filter);
		ProxyPathValidator validator = new ProxyPathValidator();
		if ((list == null) || (list.length == 0)) {
			CertificateValidationFault fault = new CertificateValidationFault();
			fault.setFaultString("Could not validate chain, no trusted roots found!!!");
			throw fault;
		} else {
			java.security.cert.X509Certificate[] trustedCerts = new java.security.cert.X509Certificate[list.length];
			List<java.security.cert.X509CRL> crlList = new ArrayList<java.security.cert.X509CRL>();
			for (int i = 0; i < list.length; i++) {
				try {
					trustedCerts[i] = CertUtil.loadCertificate(list[i].getCertificate().getCertificateEncodedString());
				} catch (Exception e) {
					log.error(e.getMessage(), e);
					GTSInternalFault fault = new GTSInternalFault();
					fault.setFaultString("Unexpected Error loading the certificate for the trusted authority "
						+ list[i].getName() + "!!!");
					throw fault;
				}

				try {
					if (list[i].getCRL() != null) {
						crlList.add(CertUtil.loadCRL(list[i].getCRL().getCrlEncodedString()));
					}
				} catch (Exception e) {
					log.error(e.getMessage(), e);
					GTSInternalFault fault = new GTSInternalFault();
					fault.setFaultString("Unexpected Error loading the CRL for the trusted authority "
						+ list[i].getName() + "!!!");
					throw fault;
				}

			}
			java.security.cert.X509CRL[] crls = new java.security.cert.X509CRL[crlList.size()];
			for (int i = 0; i < crlList.size(); i++) {
				crls[i] = (java.security.cert.X509CRL) crlList.get(i);
			}

			java.security.cert.X509Certificate[] certChain = new java.security.cert.X509Certificate[chain.length];
			for (int i = 0; i < crlList.size(); i++) {
				try {
					certChain[i] = CertUtil.loadCertificate(chain[i].getCertificateEncodedString());
				} catch (Exception e) {
					log.error(e.getMessage(), e);
					GTSInternalFault fault = new GTSInternalFault();
					fault.setFaultString("Unexpected Error formatting the certificate chain.!!!");
					throw fault;
				}
			}
			try {
				validator.validate(certChain, trustedCerts, CertificateRevocationLists
					.getCertificateRevocationLists(crls));
				isValidated = true;
			} catch (Exception e) {
				CertificateValidationFault fault = new CertificateValidationFault();
				fault.setFaultString("Could not validate chain " + e.getMessage());
				throw fault;
			}
		}
		return isValidated;
	}


	public void updateTrustedAuthority(TrustedAuthority ta, String callerGridIdentity) throws GTSInternalFault,
		IllegalTrustedAuthorityFault, InvalidTrustedAuthorityFault, PermissionDeniedFault {
		checkServiceAdministrator(callerGridIdentity);
		trust.updateTrustedAuthority(ta);
	}


	public void updateCRL(String trustedAuthorityName, X509CRL crl, String callerGridIdentity) throws GTSInternalFault,
		IllegalTrustedAuthorityFault, InvalidTrustedAuthorityFault, PermissionDeniedFault {
		this.checkAdministratorOfTrustedAuthority(trustedAuthorityName, callerGridIdentity);
		TrustedAuthority ta = trust.getTrustedAuthority(trustedAuthorityName);
		ta.setCRL(crl);
		trust.updateTrustedAuthority(ta);
	}


	public void removeTrustedAuthority(String name, String callerGridIdentity) throws GTSInternalFault,
		InvalidTrustedAuthorityFault, PermissionDeniedFault {
		checkServiceAdministrator(callerGridIdentity);
		trust.removeTrustedAuthority(name);
		permissions.revokePermissions(name);
	}


	public void addTrustLevel(TrustLevel level, String callerGridIdentity) throws GTSInternalFault,
		IllegalTrustLevelFault, PermissionDeniedFault {
		checkServiceAdministrator(callerGridIdentity);
		trustLevelManager.addTrustLevel(level);
	}


	public void removeTrustLevel(String name, String callerGridIdentity) throws GTSInternalFault,
		InvalidTrustLevelFault, IllegalTrustLevelFault, PermissionDeniedFault {
		checkServiceAdministrator(callerGridIdentity);
		trustLevelManager.removeTrustLevel(name);
	}


	public void updateTrustLevel(TrustLevel level, String callerGridIdentity) throws GTSInternalFault,
		InvalidTrustLevelFault, IllegalTrustLevelFault, PermissionDeniedFault {
		checkServiceAdministrator(callerGridIdentity);
		trustLevelManager.updateTrustLevel(level);
	}


	public TrustLevel[] getTrustLevels() throws GTSInternalFault {
		return trustLevelManager.getTrustLevels();
	}


	public TrustLevel[] getTrustLevels(String gtsSourceURI) throws GTSInternalFault {
		return trustLevelManager.getTrustLevels(gtsSourceURI);
	}


	public TrustLevel getTrustLevel(String name) throws GTSInternalFault, InvalidTrustLevelFault {
		return trustLevelManager.getTrustLevel(name);
	}


	public void addPermission(Permission p, String callerGridIdentity) throws GTSInternalFault, IllegalPermissionFault,
		PermissionDeniedFault {
		checkServiceAdministrator(callerGridIdentity);
		if ((p.getTrustedAuthorityName() != null)
			&& (!p.getTrustedAuthorityName().equals(gov.nih.nci.cagrid.gts.common.Constants.ALL_TRUST_AUTHORITIES))) {
			if (!trust.doesTrustedAuthorityExist(p.getTrustedAuthorityName())) {
				IllegalPermissionFault fault = new IllegalPermissionFault();
				fault.setFaultString("Cannot add permission, the Trusted Authority (" + p.getTrustedAuthorityName()
					+ ") specified does not exist.");
				throw fault;
			}
		}
		permissions.addPermission(p);
	}


	public Permission[] findPermissions(PermissionFilter filter, String callerGridIdentity) throws GTSInternalFault,
		PermissionDeniedFault {
		checkServiceAdministrator(callerGridIdentity);
		return permissions.findPermissions(filter);
	}


	public void revokePermission(Permission p, String callerGridIdentity) throws GTSInternalFault,
		InvalidPermissionFault, PermissionDeniedFault {
		checkServiceAdministrator(callerGridIdentity);
		permissions.revokePermission(p);
	}


	public void addAuthority(AuthorityGTS gts, String callerGridIdentity) throws GTSInternalFault,
		IllegalAuthorityFault, PermissionDeniedFault {
		checkServiceAdministrator(callerGridIdentity);
		authority.addAuthority(gts);
	}


	public void updateAuthorityPriorities(AuthorityPriorityUpdate update, String callerGridIdentity)
		throws GTSInternalFault, IllegalAuthorityFault, PermissionDeniedFault {
		checkServiceAdministrator(callerGridIdentity);
		authority.updateAuthorityPriorities(update);
	}


	public void updateAuthority(AuthorityGTS gts, String callerGridIdentity) throws GTSInternalFault,
		IllegalAuthorityFault, InvalidAuthorityFault, PermissionDeniedFault {
		checkServiceAdministrator(callerGridIdentity);
		authority.updateAuthority(gts);
	}


	public AuthorityGTS[] getAuthorities() throws GTSInternalFault {
		return authority.getAuthorities();
	}


	public void removeAuthority(String serviceURI, String callerGridIdentity) throws GTSInternalFault,
		InvalidAuthorityFault, PermissionDeniedFault {
		checkServiceAdministrator(callerGridIdentity);
		try {
			authority.removeAuthority(serviceURI);
			TrustedAuthorityFilter f = new TrustedAuthorityFilter();
			f.setSourceGTS(serviceURI);
			TrustedAuthority[] ta = this.trust.findTrustAuthorities(f);
			boolean error = false;
			StringBuffer elist = null;
			for (int i = 0; i < ta.length; i++) {
				try {
					trust.removeTrustedAuthority(ta[i].getName());
				} catch (Exception ex) {
					log.error(ex);
					if (elist == null) {
						error = true;
						elist = new StringBuffer("Unable to remove the trusted authorities:\n");

					}
					elist.append(ta[i].getName() + "\n");

				}
			}
			if (error) {
				throw new Exception(elist.toString());
			}
		} catch (Exception e) {
			log.error(e);
			GTSInternalFault fault = new GTSInternalFault();
			fault.setFaultString("An following unexpected error occurred removing the authority " + serviceURI + ": "
				+ e.getMessage());
			throw fault;
		}
	}


	private void checkServiceAdministrator(String gridIdentity) throws GTSInternalFault, PermissionDeniedFault {
		if (!permissions.isUserTrustServiceAdmin(gridIdentity)) {
			PermissionDeniedFault fault = new PermissionDeniedFault();
			fault.setFaultString("You are not a trust service administrator!!!");
			throw fault;
		}
	}


	private void checkAdministratorOfTrustedAuthority(String ta, String gridIdentity) throws GTSInternalFault,
		PermissionDeniedFault {
		if (permissions.isUserTrustServiceAdmin(gridIdentity)) {
			return;
		}

		if (permissions.isUserTrustedAuthorityAdmin(ta, gridIdentity)) {
			return;
		}
		PermissionDeniedFault fault = new PermissionDeniedFault();
		fault.setFaultString("You are not an administrator for the trusted authority " + ta + "!!!");
		throw fault;
	}


	public void clearDatabase() throws GTSInternalFault {
		trust.clearDatabase();
		permissions.clearDatabase();
		trustLevelManager.clearDatabase();
		authority.clearDatabase();
	}


	public boolean isTrustLevelUsed(String name) throws GTSInternalFault {
		TrustedAuthorityFilter f = new TrustedAuthorityFilter();
		String[] level = new String[1];
		level[0] = name;
		TrustLevels levels = new TrustLevels();
		levels.setTrustLevel(level);
		f.setTrustLevels(levels);
		TrustedAuthority[] ta = this.findTrustAuthorities(f);
		if ((ta == null) || (ta.length == 0)) {
			return false;
		} else {
			return true;
		}
	}


	public boolean doesTrustLevelExist(String name) throws GTSInternalFault {
		if (trustLevelManager.doesTrustLevelExist(name)) {
			return true;
		} else {
			return false;
		}
	}


	public void removeAssociatedTrustedAuthorities(String trustLevel) throws GTSInternalFault {
		this.trust.removeLevelFromTrustedAuthorities(trustLevel);
	}


	protected void synchronizeTrustLevels(String authorityServiceURI, TrustLevel[] levels) {
		// Synchronize the Trust Level

		// We need to get a list of all the Trusted Authorities provided
		// by the source,
		// such that we can remove the ones that are not provided in the
		// new list

		Map<String,Boolean> toBeDeleted = new HashMap<String,Boolean>();
		try {
			TrustLevel[] existing = this.trustLevelManager.getTrustLevels(authorityServiceURI);
			for (int i = 0; i < existing.length; i++) {
				toBeDeleted.put(existing[i].getName(), Boolean.TRUE);
			}
		} catch (Exception e) {
			this.log.error("Error synchronizing with the authority " + authorityServiceURI
				+ "the following error occurred obtaining the existing trust level: " + e.getMessage(), e);
			return;
		}
		if (levels != null) {
			for (int j = 0; j < levels.length; j++) {
				toBeDeleted.remove(levels[j].getName());
				try {

					if (this.trustLevelManager.doesTrustLevelExist(levels[j].getName())) {
						// Perform Update
						TrustLevel l = this.trustLevelManager.getTrustLevel(levels[j].getName());
						AuthorityGTS updateAuthority = authority.getAuthority(authorityServiceURI);
						// Determine if we should peform update
						boolean performUpdate = false;
						// Check to see if this service is the authority
						if (!l.getAuthorityGTS().equals(gtsURI)) {
							AuthorityGTS currAuthority = authority.getAuthority(l.getSourceGTS());

							// Check to see if the authority GTS is the same
							if (currAuthority.getServiceURI().equals(updateAuthority.getServiceURI())) {
								performUpdate = true;
								this.log.debug("The trust level (" + levels[j].getName() + ") will be updated!!!");
							} else if (currAuthority.getPriority() > updateAuthority.getPriority()) {
								performUpdate = true;
								this.log.debug("The trust level (" + levels[j].getName()
									+ ") will be updated, the authority (" + updateAuthority.getServiceURI()
									+ ") has a greater priority then the current source authority ("
									+ currAuthority.getServiceURI() + ")!!!");

							} else {
								this.log.debug("The trust level(" + levels[j].getName()
									+ ") will NOT be updated, the current source authority ("
									+ currAuthority.getServiceURI()
									+ ") has a greater priority then the source authority ("
									+ updateAuthority.getServiceURI() + ")!!!");
								performUpdate = false;
							}
						} else {
							this.log.debug("The trust level (" + levels[j].getName()
								+ ") will NOT be updated, this GTS is its authority !!!");
							performUpdate = false;
						}
						if (performUpdate) {
							levels[j].setIsAuthority(Boolean.FALSE);
							levels[j].setSourceGTS(authorityServiceURI);

							try {
								this.trustLevelManager.updateTrustLevel(levels[j], false);
							} catch (Exception e) {
								this.log.error("Error synchronizing with the authority " + authorityServiceURI
									+ ", the following error occcurred when trying to update the authority, "
									+ levels[j].getName() + ": " + e.getMessage(), e);
								continue;
							}
						}
					} else {
						this.log.debug("The trusted authority (" + levels[j].getName()
							+ ") will be added with the authority (" + authorityServiceURI + ") as the source!!!");
						levels[j].setIsAuthority(Boolean.FALSE);
						levels[j].setSourceGTS(authorityServiceURI);

						try {
							this.trustLevelManager.addTrustLevel(levels[j], false);
						} catch (Exception e) {
							this.log.error("Error synchronizing with the authority " + authorityServiceURI
								+ ", the following error occcurred when trying to add the trust level, "
								+ levels[j].getName() + ": " + e.getMessage(), e);
							continue;
						}

					}
				} catch (Exception ex) {
					this.log.error("Error synchronizing with the authority " + authorityServiceURI + ": "
						+ ex.getMessage(), ex);
					continue;
				}
			}
		}
		Iterator<String> itr = toBeDeleted.keySet().iterator();
		while (itr.hasNext()) {
			String name =  itr.next();
			try {
				this.trustLevelManager.removeTrustLevel(name);
				this.log.debug("The trust level (" + name
					+ ") was removed because it has been removed from the authority " + authorityServiceURI + "!!!");
			} catch (Exception e) {
				this.log.error("The trust level (" + name
					+ ") should have been removed because it has been removed from the authority "
					+ authorityServiceURI + ", however the following error occurred:" + e.getMessage(), e);
			}
		}
	}


	protected void synchronizeTrustedAuthorities(String authorityServiceURI, TrustedAuthority[] trusted) {
		// Synchronize the Trusted Authorities
		if (trusted != null) {
			// We need to get a list of all the trust levels provided
			// by the source,
			// such that we can remove the ones that are not provided in the
			// new list
			Map<String,Boolean> toBeDeleted = new HashMap<String,Boolean>();
			try {
				TrustedAuthorityFilter f = new TrustedAuthorityFilter();
				f.setSourceGTS(authorityServiceURI);
				TrustedAuthority[] existing = this.trust.findTrustAuthorities(f);
				for (int i = 0; i < existing.length; i++) {
					toBeDeleted.put(existing[i].getName(), Boolean.TRUE);
				}
			} catch (Exception e) {
				this.log.error("Error synchronizing with the authority " + authorityServiceURI
					+ "the following error occurred obtaining the existing Trusted Authorities: " + e.getMessage(), e);
				return;
			}

			for (int j = 0; j < trusted.length; j++) {
				try {
					toBeDeleted.remove(trusted[j].getName());
					if (this.trust.doesTrustedAuthorityExist(trusted[j].getName())) {
						// Perform Update
						TrustedAuthority ta = this.trust.getTrustedAuthority(trusted[j].getName());
						AuthorityGTS updateAuthority = authority.getAuthority(authorityServiceURI);
						// Determine if we should peform update
						boolean performUpdate = false;
						// Check to see if this service is the authority
						if (!ta.getAuthorityGTS().equals(gtsURI)) {
							AuthorityGTS currAuthority = authority.getAuthority(ta.getSourceGTS());

							// Check to see if the authority GTS is the same
							if (currAuthority.getServiceURI().equals(updateAuthority.getServiceURI())) {
								performUpdate = true;
								this.log.debug("The trusted authority (" + ta.getName() + ") will be updated!!!");
							} else if (currAuthority.getPriority() > updateAuthority.getPriority()) {
								performUpdate = true;
								this.log.debug("The trusted authority (" + ta.getName()
									+ ") will be updated, the authority (" + updateAuthority.getServiceURI()
									+ ") has a greater priority then the current source authority ("
									+ currAuthority.getServiceURI() + ")!!!");

							} else {
								this.log.debug("The trusted authority (" + ta.getName()
									+ ") will NOT be updated, the current source authority ("
									+ currAuthority.getServiceURI()
									+ ") has a greater priority then the source authority ("
									+ updateAuthority.getServiceURI() + ")!!!");
								performUpdate = false;
							}
						} else {
							this.log.debug("The trusted authority (" + ta.getName()
								+ ") will NOT be updated, this GTS is its authority !!!");
							performUpdate = false;
						}
						if (performUpdate) {
							trusted[j].setIsAuthority(Boolean.FALSE);
							trusted[j].setSourceGTS(authorityServiceURI);
							Calendar c = new GregorianCalendar();
							c.add(Calendar.HOUR, updateAuthority.getTimeToLive().getHours());
							c.add(Calendar.MINUTE, updateAuthority.getTimeToLive().getMinutes());
							c.add(Calendar.SECOND, updateAuthority.getTimeToLive().getSeconds());
							trusted[j].setExpires(c.getTimeInMillis());
							try {
								trust.updateTrustedAuthority(trusted[j], false);
							} catch (Exception e) {
								this.log.error("Error synchronizing with the authority " + authorityServiceURI
									+ ", the following error occcurred when trying to update the authority, "
									+ trusted[j].getName() + ": " + e.getMessage(), e);
								continue;
							}
						}
					} else {
						AuthorityGTS updateAuthority = authority.getAuthority(authorityServiceURI);
						this.log.debug("The trusted authority (" + trusted[j].getName()
							+ ") will be added with the authority (" + authorityServiceURI + ") as the source!!!");
						trusted[j].setIsAuthority(Boolean.FALSE);
						trusted[j].setSourceGTS(authorityServiceURI);
						Calendar c = new GregorianCalendar();
						c.add(Calendar.HOUR, updateAuthority.getTimeToLive().getHours());
						c.add(Calendar.MINUTE, updateAuthority.getTimeToLive().getMinutes());
						c.add(Calendar.SECOND, updateAuthority.getTimeToLive().getSeconds());
						trusted[j].setExpires(c.getTimeInMillis());
						try {
							trust.addTrustedAuthority(trusted[j], false);
						} catch (Exception e) {
							this.log.error("Error synchronizing with the authority " + authorityServiceURI
								+ ", the following error occcurred when trying to add the authority, "
								+ trusted[j].getName() + ": " + e.getMessage(), e);
							continue;
						}

					}
				} catch (Exception ex) {
					this.log.error("Error synchronizing with the authority " + authorityServiceURI + ": "
						+ ex.getMessage(), ex);
					continue;
				}
			}
			Iterator<String> itr = toBeDeleted.keySet().iterator();
			while (itr.hasNext()) {
				String name = itr.next();
				try {
					trust.removeTrustedAuthority(name);
					this.log
						.debug("The trusted authority (" + name
							+ ") was removed because it has been removed from the authority " + authorityServiceURI
							+ "!!!");
				} catch (Exception e) {
					this.log.error("The trusted authority (" + name
						+ ") should have been removed because it has been removed from the authority "
						+ authorityServiceURI + ", however the following error occurred:" + e.getMessage(), e);
				}
			}

		}
	}


	private void synchronizeWithAuthorities() {
		if (conf.getAuthoritySyncTime() != null) {
			long sleep = (conf.getAuthoritySyncTime().getSeconds() * 1000)
				+ (conf.getAuthoritySyncTime().getMinutes() * 1000 * 60)
				+ (conf.getAuthoritySyncTime().getHours() * 1000 * 60 * 60);
			while (true) {
				AuthorityGTS[] auths = null;
				try {
					auths = this.getAuthorities();
				} catch (Exception ex) {
					this.log.error(
						"Error synchronizing with the authorities, could not obtain a list of authorities!!!", ex);
				}

				if (auths != null) {
					TrustedAuthorityFilter filter = new TrustedAuthorityFilter();
					filter.setStatus(Status.Trusted);
					filter.setLifetime(Lifetime.Valid);

					for (int i = 0; i < auths.length; i++) {
						TrustLevel[] levels = null;
						TrustedAuthority[] trusted = null;
						try {
							EndpointReferenceType endpoint = new EndpointReferenceType();
							endpoint.setAddress(new Address(auths[i].getServiceURI()));
							GTSClient client = new GTSClient(endpoint);

							if (auths[i].isPerformAuthorization()) {
								IdentityAuthorization ia = new IdentityAuthorization(auths[i].getServiceIdentity());
								client.setAuthorization(ia);
							}
							levels = client.getTrustLevels();
							trusted = client.findTrustedAuthorities(filter);

						} catch (Exception ex) {
							this.log.error("Error synchronizing with the authority " + auths[i].getServiceURI() + ": "
								+ ex.getMessage(), ex);
							continue;
						}

						// Synchronize the Trust Levels
						this.synchronizeTrustLevels(auths[i].getServiceURI(), levels);

						// Synchronize the Trusted Authorities
						this.synchronizeTrustedAuthorities(auths[i].getServiceURI(), trusted);
					}
				}
				try {
					Thread.sleep(sleep);
				} catch (Exception e) {
					log.error(e);
				}
			}
		}
	}
}
