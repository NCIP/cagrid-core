package org.cagrid.gaards.cds.service;

import java.security.KeyPair;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gaards.cds.common.CertificateChain;
import org.cagrid.gaards.cds.common.ClientDelegationFilter;
import org.cagrid.gaards.cds.common.DelegatedCredentialAuditFilter;
import org.cagrid.gaards.cds.common.DelegatedCredentialAuditRecord;
import org.cagrid.gaards.cds.common.DelegatedCredentialEvent;
import org.cagrid.gaards.cds.common.DelegationIdentifier;
import org.cagrid.gaards.cds.common.DelegationPolicy;
import org.cagrid.gaards.cds.common.DelegationRecord;
import org.cagrid.gaards.cds.common.DelegationRecordFilter;
import org.cagrid.gaards.cds.common.DelegationRequest;
import org.cagrid.gaards.cds.common.DelegationSigningRequest;
import org.cagrid.gaards.cds.common.DelegationSigningResponse;
import org.cagrid.gaards.cds.common.DelegationStatus;
import org.cagrid.gaards.cds.common.Errors;
import org.cagrid.gaards.cds.common.ExpirationStatus;
import org.cagrid.gaards.cds.common.ProxyLifetime;
import org.cagrid.gaards.cds.common.PublicKey;
import org.cagrid.gaards.cds.common.Utils;
import org.cagrid.gaards.cds.service.policy.PolicyHandler;
import org.cagrid.gaards.cds.stubs.types.CDSInternalFault;
import org.cagrid.gaards.cds.stubs.types.DelegationFault;
import org.cagrid.gaards.cds.stubs.types.InvalidPolicyFault;
import org.cagrid.gaards.cds.stubs.types.PermissionDeniedFault;
import org.cagrid.gaards.pki.CertificateExtensionsUtil;
import org.cagrid.gaards.pki.KeyUtil;
import org.cagrid.gaards.pki.ProxyCreator;
import org.cagrid.tools.database.Database;
import org.cagrid.tools.events.Event;
import org.cagrid.tools.events.EventAuditor;
import org.cagrid.tools.events.EventManager;
import org.cagrid.tools.events.InvalidHandlerException;
import org.globus.gsi.CertUtil;
import org.globus.gsi.CertificateRevocationLists;
import org.globus.gsi.TrustedCertificates;
import org.globus.gsi.bc.BouncyCastleUtil;
import org.globus.gsi.proxy.ProxyPathValidator;

public class DelegatedCredentialManager {

	public static int DELEGATION_BUFFER_SECONDS = 120;
	private final static String TABLE = "delegated_credentials";
	private final static String DELEGATION_ID = "DELEGATION_ID";
	private final static String GRID_IDENTITY = "GRID_IDENTITY";
	private final static String STATUS = "STATUS";
	private final static String POLICY_TYPE = "POLICY_TYPE";
	private final static String EXPIRATION = "EXPIRATION";
	private final static String DELEGATION_PATH_LENGTH = "DELEGATION_PATH_LENGTH";
	private final static String DATE_INITIATED = "DATE_INITIATED";
	private final static String DATE_APPROVED = "DATE_APPROVED";
	private final static String PROXY_LIFETIME_HOURS = "PROXY_LIFETIME_HOURS";
	private final static String PROXY_LIFETIME_MINUTES = "PROXY_LIFETIME_MINUTES";
	private final static String PROXY_LIFETIME_SECONDS = "PROXY_LIFETIME_SECONDS";
	private final static String DELEGATED_CREDENTIAL_AUDITOR = "delegatedCredentialAuditor";

	public static int PROXY_EXPIRATION_BUFFER_SECONDS = 5;

	private Database db;
	private boolean dbBuilt = false;
	private List<PolicyHandler> handlers;
	private Log log;
	private KeyManager keyManager;
	private ProxyPolicy proxyPolicy;
	private EventManager events;
	private EventAuditor delegationAuditor;

	public DelegatedCredentialManager(Database db, PropertyManager properties,
			KeyManager keyManager, List<PolicyHandler> policyHandlers,
			ProxyPolicy proxyPolicy, EventManager events)
			throws CDSInternalFault {
		this.db = db;
		this.log = LogFactory.getLog(this.getClass().getName());
		this.handlers = policyHandlers;
		this.proxyPolicy = proxyPolicy;
		this.events = events;
		try {
			this.delegationAuditor = (EventAuditor) events
					.getEventHandler(DELEGATED_CREDENTIAL_AUDITOR);
		} catch (InvalidHandlerException e) {
			throw Errors
					.getInternalFault(
							"Could not initialize the delegation credential manager, could not obtain the delegated credential auditor.",
							e);
		}
		String currentKeyManager = properties.getKeyManager();
		if ((currentKeyManager != null)
				&& (!currentKeyManager.equals(keyManager.getClass().getName()))) {
			throw Errors.getInternalFault(Errors.KEY_MANAGER_CHANGED);
		}
		this.keyManager = keyManager;
		if (currentKeyManager == null) {
			properties.setKeyManager(this.keyManager.getClass().getName());
		}
	}

	public PolicyHandler findHandler(String policyClassName)
			throws InvalidPolicyFault {
		PolicyHandler handler = null;
		boolean handlerFound = false;
		for (int i = 0; i < handlers.size(); i++) {
			if (handlers.get(i).isSupported(policyClassName)) {
				if (!handlerFound) {
					handler = handlers.get(i);
					handlerFound = true;
				} else {
					InvalidPolicyFault f = new InvalidPolicyFault();
					f.setFaultString(Errors.MULTIPLE_HANDLERS_FOUND_FOR_POLICY
							+ policyClassName
							+ ", cannot decide which handler to employ.");
					throw f;
				}
			}
		}
		if (!handlerFound) {
			InvalidPolicyFault f = new InvalidPolicyFault();
			f.setFaultString(Errors.DELEGATION_POLICY_NOT_SUPPORTED);
			throw f;
		}
		return handler;
	}

	public DelegatedCredentialAuditRecord[] searchAuditLog(
			DelegatedCredentialAuditFilter f) throws CDSInternalFault {
		try {
			String targetId = null;
			if (f.getDelegationIdentifier() != null) {
				targetId = String.valueOf(f.getDelegationIdentifier()
						.getDelegationId());
			}

			String eventType = null;
			if (f.getEvent() != null) {
				eventType = f.getEvent().getValue();
			}

			Date start = null;
			if (f.getStartDate() != null) {
				start = new Date(f.getStartDate().longValue());
			}
			Date end = null;

			if (f.getEndDate() != null) {
				end = new Date(f.getEndDate().longValue());
			}
			List<Event> events = this.delegationAuditor.findEvents(targetId, f
					.getSourceGridIdentity(), eventType, start, end, null);
			DelegatedCredentialAuditRecord[] records = new DelegatedCredentialAuditRecord[events
					.size()];
			for (int i = 0; i < events.size(); i++) {
				records[i] = new DelegatedCredentialAuditRecord();
				DelegationIdentifier id = new DelegationIdentifier();
				id.setDelegationId(Long.valueOf(events.get(i).getTargetId())
						.longValue());
				records[i].setDelegationIdentifier(id);
				records[i].setEvent(DelegatedCredentialEvent.fromValue(events
						.get(i).getEventType()));
				records[i].setMessage(events.get(i).getMessage());
				records[i].setOccurredAt(events.get(i).getOccurredAt());
				records[i].setSourceGridIdentity(events.get(i)
						.getReportingPartyId());
			}

			return records;
		} catch (Exception e) {
			throw Errors
					.getInternalFault(
							"An unexpected error occurred in searching the audit logs.",
							e);
		}
	}

	public DelegationRecord[] findCredentialsDelegatedToClient(
			String callerIdentity, ClientDelegationFilter filter)
			throws CDSInternalFault {
		DelegationRecordFilter f = new DelegationRecordFilter();
		f.setDelegationStatus(DelegationStatus.Approved);
		f.setExpirationStatus(ExpirationStatus.Valid);

		if (filter != null) {
			f.setGridIdentity(filter.getGridIdentity());
		}

		DelegationRecord[] records = findDelegatedCredentials(f);
		if (records != null) {
			List<DelegationRecord> list = new ArrayList<DelegationRecord>();
			for (int i = 0; i < records.length; i++) {
				try {
					PolicyHandler handler = this.findHandler(records[i]
							.getDelegationPolicy().getClass().getName());
					if (handler.isAuthorized(records[i]
							.getDelegationIdentifier(), callerIdentity)) {
						list.add(records[i]);
					}

				} catch (Exception e) {
					log.error(e);
				}
			}
			DelegationRecord[] results = new DelegationRecord[list.size()];
			return list.toArray(results);
		} else {
			return new DelegationRecord[0];
		}
	}

	public synchronized DelegationSigningRequest initiateDelegation(
			String callerGridIdentity, DelegationRequest request)
			throws CDSInternalFault, DelegationFault, InvalidPolicyFault {
		this.buildDatabase();
		DelegationPolicy policy = request.getDelegationPolicy();
		PolicyHandler handler = this.findHandler(policy.getClass().getName());
		if (!this.proxyPolicy.isKeySizeSupported(request.getKeyLength())) {
			throw Errors
					.getDelegationFault(Errors.INVALID_KEY_LENGTH_SPECIFIED);
		}

		if (request.getIssuedCredentialLifetime() == null) {
			throw Errors
					.getDelegationFault(Errors.PROXY_LIFETIME_NOT_SPECIFIED);
		}

		if ((request.getIssuedCredentialPathLength() < 0)
				|| (this.proxyPolicy.getMaxDelegationPathLength() < request
						.getIssuedCredentialPathLength())) {
			throw Errors
					.getDelegationFault(Errors.INVALID_DELEGATION_PATH_LENGTH_SPECIFIED);
		}

		Connection c = null;
		long delegationId = -1;
		try {
			c = this.db.getConnection();
			PreparedStatement s = c.prepareStatement("INSERT INTO " + TABLE
					+ " SET " + GRID_IDENTITY + "= ?, " + POLICY_TYPE + "= ?, "
					+ STATUS + "= ?," + DATE_INITIATED + "=?," + DATE_APPROVED
					+ "=?," + EXPIRATION + "=?," + DELEGATION_PATH_LENGTH
					+ "=?," + PROXY_LIFETIME_HOURS + "=?,"
					+ PROXY_LIFETIME_MINUTES + "=?," + PROXY_LIFETIME_SECONDS
					+ "=?");
			s.setString(1, callerGridIdentity);
			s.setString(2, policy.getClass().getName());
			s.setString(3, DelegationStatus.Pending.getValue());
			s.setLong(4, new Date().getTime());
			s.setLong(5, 0);
			s.setLong(6, 0);
			s.setInt(7, request.getIssuedCredentialPathLength());
			s.setInt(8, request.getIssuedCredentialLifetime().getHours());
			s.setInt(9, request.getIssuedCredentialLifetime().getMinutes());
			s.setInt(10, request.getIssuedCredentialLifetime().getSeconds());
			s.execute();
			s.close();
			delegationId = db.getLastAutoId(c);
			DelegationIdentifier id = new DelegationIdentifier();
			id.setDelegationId(delegationId);
			// Create and Store Key Pair.
			KeyPair keys = this.keyManager.createAndStoreKeyPair(String
					.valueOf(delegationId), request.getKeyLength());
			handler.storePolicy(id, policy);
			DelegationSigningRequest req = new DelegationSigningRequest();
			req.setDelegationIdentifier(id);
			PublicKey publicKey = new PublicKey();
			publicKey.setKeyAsString(KeyUtil.writePublicKey(keys.getPublic()));
			req.setPublicKey(publicKey);
			logEvent(delegationId, callerGridIdentity,
					DelegatedCredentialEvent.DelegationInitiated,
					"Delegation initiated for " + callerGridIdentity + ".");
			return req;
		} catch (CDSInternalFault e) {
			try {
				this.delete(delegationId);
			} catch (Exception ex) {
				log.error(ex.getMessage(), ex);
			}
			throw e;
		} catch (InvalidPolicyFault e) {
			try {
				this.delete(delegationId);
			} catch (Exception ex) {
				log.error(ex.getMessage(), ex);
			}
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			try {
				this.delete(delegationId);
			} catch (Exception ex) {
				log.error(ex.getMessage(), ex);
			}
			throw Errors.getDatabaseFault(e);
		} finally {
			db.releaseConnection(c);
		}
	}

	private void logEvent(long delegationId, String callerGridIdentity,
			DelegatedCredentialEvent event, String message)
			throws CDSInternalFault {
		try {
			events.logEvent(String.valueOf(delegationId), callerGridIdentity,
					event.getValue(), message);
		} catch (Exception e) {
			throw Errors
					.getInternalFault(
							"Unexpected error encountered in establishing the audit trail.",
							e);
		}
	}

	public boolean delegationExists(DelegationIdentifier id)
			throws CDSInternalFault {
		try {
			if (id == null) {
				return false;
			} else {
				return db.exists(TABLE, DELEGATION_ID, id.getDelegationId());
			}
		} catch (Exception e) {
			throw Errors.getDatabaseFault(e);
		}
	}

	public DelegationRecord getDelegationRecord(DelegationIdentifier id)
			throws CDSInternalFault, DelegationFault {

		if (delegationExists(id)) {
			DelegationRecord r = new DelegationRecord();
			r.setDelegationIdentifier(id);
			Connection c = null;
			try {
				c = this.db.getConnection();
				PreparedStatement s = c.prepareStatement("select * from "
						+ TABLE + " WHERE " + DELEGATION_ID + "= ?");
				s.setLong(1, id.getDelegationId());
				ResultSet rs = s.executeQuery();
				if (rs.next()) {
					r.setDateApproved(rs.getLong(DATE_APPROVED));
					r.setDateInitiated(rs.getLong(DATE_INITIATED));
					r.setDelegationStatus(DelegationStatus.fromValue(rs
							.getString(STATUS)));
					r.setExpiration(rs.getLong(EXPIRATION));
					r.setGridIdentity(rs.getString(GRID_IDENTITY));
					r.setIssuedCredentialPathLength(rs
							.getInt(DELEGATION_PATH_LENGTH));
					ProxyLifetime lifetime = new ProxyLifetime();
					lifetime.setHours(rs.getInt(PROXY_LIFETIME_HOURS));
					lifetime.setMinutes(rs.getInt(PROXY_LIFETIME_MINUTES));
					lifetime.setSeconds(rs.getInt(PROXY_LIFETIME_SECONDS));
					r.setIssuedCredentialLifetime(lifetime);
				}
				rs.close();
				s.close();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw Errors.getDatabaseFault(e);
			} finally {
				this.db.releaseConnection(c);
			}

			try {
				X509Certificate[] certs = this.keyManager
						.getCertificates(String.valueOf(id.getDelegationId()));
				r.setCertificateChain(org.cagrid.gaards.cds.common.Utils
						.toCertificateChain(certs));
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw Errors.getInternalFault(
						Errors.UNEXPECTED_ERROR_LOADING_CERTIFICATE_CHAIN, e);
			}
			try {
				PolicyHandler handler = this.findHandler(getPolicyType(id
						.getDelegationId()));
				r.setDelegationPolicy(handler.getPolicy(id));
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw Errors.getInternalFault(
						Errors.UNEXPECTED_ERROR_LOADING_DELEGATION_POLICY, e);
			}

			return r;
		} else {
			throw Errors
					.getDelegationFault(Errors.DELEGATION_RECORD_DOES_NOT_EXIST);
		}
	}

	public synchronized DelegationIdentifier approveDelegation(
			String callerGridIdentity, DelegationSigningResponse res)
			throws CDSInternalFault, DelegationFault, PermissionDeniedFault {
		DelegationIdentifier id = res.getDelegationIdentifier();
		if (this.delegationExists(id)) {
			DelegationRecord r = getDelegationRecord(id);

			if (!r.getDelegationStatus().equals(DelegationStatus.Pending)) {
				throw Errors
						.getDelegationFault(Errors.CANNOT_APPROVE_INVALID_STATUS);
			}

			Calendar c = new GregorianCalendar();
			c.setTimeInMillis(r.getDateInitiated());
			c.add(Calendar.SECOND, DELEGATION_BUFFER_SECONDS);
			Date d = new Date();
			if (d.after(c.getTime())) {
				throw Errors
						.getDelegationFault(Errors.DELEGATION_APPROVAL_BUFFER_EXPIRED);
			}

			// Check to make sure that the entity that initiated the delegation
			// is the same entity that is approving it.
			if (!r.getGridIdentity().equals(callerGridIdentity)) {
				throw Errors
						.getDelegationFault(Errors.INITIATOR_DOES_NOT_MATCH_APPROVER);
			}
			CertificateChain chain = res.getCertificateChain();
			if (chain == null) {
				throw Errors
						.getDelegationFault(Errors.CERTIFICATE_CHAIN_NOT_SPECIFIED);
			}
			X509Certificate[] certs = null;
			try {
				certs = Utils.toCertificateArray(chain);

			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw Errors.getInternalFault(
						Errors.UNEXPECTED_ERROR_LOADING_CERTIFICATE_CHAIN, e);
			}

			if (certs.length < 2) {
				throw Errors
						.getDelegationFault(Errors.INSUFFICIENT_CERTIFICATE_CHAIN_SPECIFIED);
			}

			// Check that the public keys match.
			java.security.PublicKey publicKey = this.keyManager
					.getPublicKey(String.valueOf(id.getDelegationId()));
			if (!certs[0].getPublicKey().equals(publicKey)) {
				throw Errors
						.getDelegationFault(Errors.PUBLIC_KEY_DOES_NOT_MATCH);
			}

			// Check delegation path length
			try {
				if (CertUtil.isProxy(BouncyCastleUtil
						.getCertificateType(certs[0]))) {
					int currLength = r.getIssuedCredentialPathLength();
					for (int i = 0; i < certs.length; i++) {
						if (CertUtil.isProxy(BouncyCastleUtil
								.getCertificateType(certs[i]))) {
							int delegationPathLength = CertificateExtensionsUtil
									.getDelegationPathLength(certs[i]);
							int maxLength = delegationPathLength - 1;
							if (maxLength < currLength) {
								throw Errors
										.getDelegationFault(Errors.INSUFFICIENT_DELEGATION_PATH_LENGTH);
							}
							currLength = delegationPathLength;
						}
					}
				} else {
					throw Errors
							.getDelegationFault(Errors.CERTIFICATE_CHAIN_DOES_NOT_CONTAIN_PROXY);
				}

				try {
					ProxyPathValidator validator = new ProxyPathValidator();
					validator.validate(certs, TrustedCertificates
							.getDefaultTrustedCertificates().getCertificates(),
							CertificateRevocationLists
									.getDefaultCertificateRevocationLists());

				} catch (Exception e) {
					throw Errors.getDelegationFault(
							Errors.INVALID_CERTIFICATE_CHAIN, e);
				}

				// Check to make sure the Identity of the proxy cert matches the
				// Identity of the initiator.

				try {
					if (!BouncyCastleUtil.getIdentity(certs).equals(
							r.getGridIdentity())) {
						throw Errors
								.getDelegationFault(Errors.IDENTITY_DOES_NOT_MATCH_INITIATOR);
					}
				} catch (CertificateException e) {
					log.error(e.getMessage(), e);
					throw Errors
							.getInternalFault(
									Errors.UNEXPECTED_ERROR_EXTRACTING_IDENTITY_FROM_CERTIFICATE_CHAIN,
									e);
				}

			} catch (CDSInternalFault e) {
				throw e;
			} catch (DelegationFault e) {
				throw e;
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw Errors
						.getInternalFault(
								Errors.UNEXPECTED_ERROR_DETERMINING_DELEGATION_PATH_LENGTH,
								e);
			}

			this.keyManager.storeCertificates(String.valueOf(id
					.getDelegationId()), certs);
			Connection conn = null;
			try {
				Date now = new Date();
				conn = this.db.getConnection();
				PreparedStatement s = conn.prepareStatement("update " + TABLE
						+ " SET " + STATUS + "=?," + EXPIRATION + "=?,"
						+ DATE_APPROVED + "=?" + " WHERE " + DELEGATION_ID
						+ "= ?");
				s.setString(1, DelegationStatus.Approved.getValue());
				s.setLong(2, Utils.getEarliestExpiration(certs).getTime());
				s.setLong(3, now.getTime());
				s.setLong(4, id.getDelegationId());
				s.executeUpdate();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw Errors.getDatabaseFault(e);
			} finally {
				this.db.releaseConnection(conn);
			}
			logEvent(id.getDelegationId(), callerGridIdentity,
					DelegatedCredentialEvent.DelegationApproved,
					"The delegated credential for " + callerGridIdentity
							+ " has been approved.");
			return id;
		} else {
			throw Errors
					.getDelegationFault(Errors.DELEGATION_RECORD_DOES_NOT_EXIST);
		}

	}

	public void updateDelegatedCredentialStatus(String callerGridIdentity,
			DelegationIdentifier id, DelegationStatus status)
			throws CDSInternalFault, DelegationFault {
		if (this.delegationExists(id)) {
			if (status.equals(DelegationStatus.Pending)) {
				throw Errors
						.getDelegationFault(Errors.CANNOT_CHANGE_STATUS_TO_PENDING);
			}
			DelegationRecord r = getDelegationRecord(id);
			Connection conn = null;
			try {
				conn = this.db.getConnection();
				PreparedStatement s = conn.prepareStatement("update " + TABLE
						+ " SET " + STATUS + "=?" + " WHERE " + DELEGATION_ID
						+ "= ?");
				s.setString(1, status.getValue());
				s.setLong(2, id.getDelegationId());
				s.executeUpdate();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw Errors.getDatabaseFault(e);
			} finally {
				this.db.releaseConnection(conn);
			}

			logEvent(id.getDelegationId(), callerGridIdentity,
					DelegatedCredentialEvent.DelegationStatusUpdated,
					"Delegation Status changed from "
							+ r.getDelegationStatus().getValue() + " to "
							+ status.getValue());
		} else {
			throw Errors
					.getDelegationFault(Errors.DELEGATION_RECORD_DOES_NOT_EXIST);
		}

	}

	public CertificateChain getDelegatedCredential(String gridIdentity,
			DelegationIdentifier id, PublicKey publicKey)
			throws CDSInternalFault, DelegationFault, PermissionDeniedFault {
		if (delegationExists(id)) {
			DelegationRecord r = this.getDelegationRecord(id);

			if (!r.getDelegationStatus().equals(DelegationStatus.Approved)) {
				logEvent(
						id.getDelegationId(),
						gridIdentity,
						DelegatedCredentialEvent.DelegatedCredentialAccessDenied,
						Errors.CANNOT_GET_INVALID_STATUS);
				throw Errors
						.getDelegationFault(Errors.CANNOT_GET_INVALID_STATUS);
			}

			PolicyHandler handler = null;
			try {
				handler = this.findHandler(r.getDelegationPolicy().getClass()
						.getName());
			} catch (Exception e) {
				logEvent(
						id.getDelegationId(),
						gridIdentity,
						DelegatedCredentialEvent.DelegatedCredentialAccessDenied,
						Errors.POLICY_HANDLER_NOT_FOUND);
				throw Errors.getInternalFault(Errors.POLICY_HANDLER_NOT_FOUND,
						e);
			}
			if (!handler.isAuthorized(id, gridIdentity)) {
				logEvent(
						id.getDelegationId(),
						gridIdentity,
						DelegatedCredentialEvent.DelegatedCredentialAccessDenied,
						Errors.PERMISSION_DENIED_TO_DELEGATED_CREDENTIAL);
				throw Errors
						.getPermissionDeniedFault(Errors.PERMISSION_DENIED_TO_DELEGATED_CREDENTIAL);
			}
			Date now = new Date();
			Date expiration = new Date(r.getExpiration());
			if (now.after(expiration)) {
				logEvent(
						id.getDelegationId(),
						gridIdentity,
						DelegatedCredentialEvent.DelegatedCredentialAccessDenied,
						Errors.SIGNING_CREDENTIAL_EXPIRED);
				throw Errors
						.getDelegationFault(Errors.SIGNING_CREDENTIAL_EXPIRED);
			}

			X509Certificate[] certs = null;
			try {
				certs = Utils.toCertificateArray(r.getCertificateChain());

			} catch (Exception e) {
				log.error(e.getMessage(), e);
				logEvent(
						id.getDelegationId(),
						gridIdentity,
						DelegatedCredentialEvent.DelegatedCredentialAccessDenied,
						Errors.UNEXPECTED_ERROR_LOADING_CERTIFICATE_CHAIN);
				throw Errors.getInternalFault(
						Errors.UNEXPECTED_ERROR_LOADING_CERTIFICATE_CHAIN, e);
			}

			try {

				java.security.PublicKey pkey = KeyUtil.loadPublicKey(publicKey
						.getKeyAsString());
				int length = ((RSAPublicKey) pkey).getModulus().bitLength();
				if (!this.proxyPolicy.isKeySizeSupported(length)) {
					logEvent(
							id.getDelegationId(),
							gridIdentity,
							DelegatedCredentialEvent.DelegatedCredentialAccessDenied,
							Errors.INVALID_KEY_LENGTH_SPECIFIED);
					throw Errors
							.getDelegationFault(Errors.INVALID_KEY_LENGTH_SPECIFIED);
				}

				int hours = 0;
				int minutes = 0;
				int seconds = 0;
				Calendar c = new GregorianCalendar();
				c.add(Calendar.HOUR_OF_DAY, r.getIssuedCredentialLifetime()
						.getHours());
				c.add(Calendar.MINUTE, r.getIssuedCredentialLifetime()
						.getMinutes());
				c.add(Calendar.SECOND, r.getIssuedCredentialLifetime()
						.getSeconds()
						+ PROXY_EXPIRATION_BUFFER_SECONDS);

				if (c.getTime().after(certs[0].getNotAfter())) {
					Calendar expires = new GregorianCalendar();
					expires.setTimeInMillis(certs[0].getNotAfter().getTime());
					long diff = (certs[0].getNotAfter().getTime() - System
							.currentTimeMillis()) / 1000;
					if (diff > PROXY_EXPIRATION_BUFFER_SECONDS) {
						seconds = (int) diff - PROXY_EXPIRATION_BUFFER_SECONDS;
					} else {
						logEvent(
								id.getDelegationId(),
								gridIdentity,
								DelegatedCredentialEvent.DelegatedCredentialAccessDenied,
								Errors.SIGNING_CREDENTIAL_ABOUT_EXPIRE);
						throw Errors
								.getDelegationFault(Errors.SIGNING_CREDENTIAL_ABOUT_EXPIRE);
					}
				} else {
					hours = r.getIssuedCredentialLifetime().getHours();
					minutes = r.getIssuedCredentialLifetime().getMinutes();
					seconds = r.getIssuedCredentialLifetime().getSeconds();
				}

				X509Certificate[] proxy = ProxyCreator
						.createImpersonationProxyCertificate(certs,
								this.keyManager.getPrivateKey(String.valueOf(id
										.getDelegationId())), pkey, hours,
								minutes, seconds, r
										.getIssuedCredentialPathLength());
				logEvent(id.getDelegationId(), gridIdentity,
						DelegatedCredentialEvent.DelegatedCredentialIssued,
						"A credential was issued to " + gridIdentity
								+ ".  The credential will expire on "
								+ proxy[0].getNotAfter().toString() + ".");
				return Utils.toCertificateChain(proxy);
			} catch (DelegationFault f) {
				throw f;
			} catch (Exception e) {
				throw Errors.getInternalFault(
						Errors.UNEXPECTED_ERROR_CREATING_PROXY, e);
			}

		} else {
			throw Errors
					.getDelegationFault(Errors.DELEGATION_RECORD_DOES_NOT_EXIST);
		}
	}

	public DelegationRecord[] findDelegatedCredentials(DelegationRecordFilter f)
			throws CDSInternalFault {
		this.buildDatabase();
		Connection c = null;
		try {
			c = this.db.getConnection();
			List<DelegationRecord> records = new ArrayList<DelegationRecord>();
			boolean filterAdded = false;
			StringBuffer sql = new StringBuffer();
			sql.append("select " + DELEGATION_ID + " from " + TABLE);

			if (f.getDelegationIdentifier() != null) {
				if (!filterAdded) {
					sql.append(" WHERE ");
					filterAdded = true;
				} else {
					sql.append(" AND ");
				}
				sql.append(DELEGATION_ID + " = ?");
			}

			if (f.getGridIdentity() != null) {
				if (!filterAdded) {
					sql.append(" WHERE ");
					filterAdded = true;
				} else {
					sql.append(" AND ");
				}
				sql.append(GRID_IDENTITY + " = ?");
			}

			if (f.getDelegationStatus() != null) {
				if (!filterAdded) {
					sql.append(" WHERE ");
					filterAdded = true;
				} else {
					sql.append(" AND ");
				}
				sql.append(STATUS + " = ?");
			}

			if (f.getExpirationStatus() != null) {
				if (!filterAdded) {
					sql.append(" WHERE ");
					filterAdded = true;
				} else {
					sql.append(" AND ");
				}
				if (f.getExpirationStatus().equals(ExpirationStatus.Valid)) {
					sql.append(EXPIRATION + " > ? AND EXPIRATION>0");
				} else {
					sql.append(EXPIRATION + " < ? AND EXPIRATION>0");
				}
			}

			PreparedStatement s = c.prepareStatement(sql.toString());
			int count = 0;

			if (f.getDelegationIdentifier() != null) {
				count = count + 1;
				s.setLong(count, f.getDelegationIdentifier().getDelegationId());
			}

			if (f.getGridIdentity() != null) {
				count = count + 1;
				s.setString(count, f.getGridIdentity());
			}

			if (f.getDelegationStatus() != null) {
				count = count + 1;
				s.setString(count, f.getDelegationStatus().getValue());
			}

			if (f.getExpirationStatus() != null) {
				count = count + 1;
				Date now = new Date();
				s.setLong(count, now.getTime());
			}

			ResultSet rs = s.executeQuery();
			while (rs.next()) {
				DelegationIdentifier id = new DelegationIdentifier();
				id.setDelegationId(rs.getLong(DELEGATION_ID));
				records.add(getDelegationRecord(id));
			}
			rs.close();
			s.close();
			DelegationRecord[] array = new DelegationRecord[records.size()];
			return records.toArray(array);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw Errors.getDatabaseFault(e);
		} finally {
			this.db.releaseConnection(c);
		}
	}

	private String getPolicyType(long delegationId) throws CDSInternalFault {
		Connection c = null;
		String policyType = null;
		try {
			c = this.db.getConnection();
			PreparedStatement s = c.prepareStatement("select " + POLICY_TYPE
					+ " from " + TABLE + " WHERE " + DELEGATION_ID + "= ?");
			s.setLong(1, delegationId);
			ResultSet rs = s.executeQuery();
			if (rs.next()) {
				policyType = rs.getString(POLICY_TYPE);
			}
			rs.close();
			s.close();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw Errors.getDatabaseFault(e);
		} finally {
			this.db.releaseConnection(c);
		}
		return policyType;
	}

	public synchronized void delete(DelegationIdentifier id)
			throws CDSInternalFault {
		this.delete(id.getDelegationId());
	}

	public synchronized void delete(long delegationId) throws CDSInternalFault {
		buildDatabase();
		Connection c = null;
		try {
			String policyType = getPolicyType(delegationId);
			PolicyHandler handler = findHandler(policyType);
			c = db.getConnection();
			PreparedStatement s = c.prepareStatement("DELETE FROM " + TABLE
					+ "  WHERE " + DELEGATION_ID + "= ?");
			s.setLong(1, delegationId);
			s.execute();
			s.close();
			this.keyManager.delete(String.valueOf(delegationId));
			DelegationIdentifier id = new DelegationIdentifier();
			id.setDelegationId(delegationId);
			handler.removePolicy(id);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw Errors.getDatabaseFault(e);
		} finally {
			db.releaseConnection(c);
		}
		try {
			List<Event> list = this.delegationAuditor.findEvents(String
					.valueOf(delegationId), null, null, null, null, null);
			for (int i = 0; i < list.size(); i++) {
				this.delegationAuditor.deleteEvent(list.get(i).getEventId());
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw Errors.getInternalFault(
					"An inexpected error occurred in deleting the audit log.",
					e);
		}
	}

	public void clearDatabase() throws CDSInternalFault {
		buildDatabase();
		try {
			for (int i = 0; i < handlers.size(); i++) {
				this.handlers.get(i).removeAllStoredPolicies();
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		try {
			this.keyManager.deleteAll();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		try {
			events.clearHandlers();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		try {
			db.update("DELETE FROM " + TABLE);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw Errors.getDatabaseFault(e);
		}
		dbBuilt = false;

	}

	private void buildDatabase() throws CDSInternalFault {
		if (!dbBuilt) {
			try {
				if (!this.db.tableExists(TABLE)) {
					String trust = "CREATE TABLE " + TABLE + " ("
							+ DELEGATION_ID
							+ " BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,"
							+ GRID_IDENTITY + " VARCHAR(255) NOT NULL,"
							+ POLICY_TYPE + " VARCHAR(255) NOT NULL," + STATUS
							+ " VARCHAR(50) NOT NULL," + DATE_INITIATED
							+ " BIGINT," + DATE_APPROVED + " BIGINT,"
							+ DELEGATION_PATH_LENGTH + " INT,"
							+ PROXY_LIFETIME_HOURS + " INT,"
							+ PROXY_LIFETIME_MINUTES + " INT,"
							+ PROXY_LIFETIME_SECONDS + " INT," + EXPIRATION
							+ " BIGINT, INDEX document_index (" + DELEGATION_ID
							+ "));";
					db.update(trust);
				}

				dbBuilt = true;
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw Errors.getDatabaseFault(e);
			}
		}
	}

}
