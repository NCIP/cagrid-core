package org.cagrid.gaards.cds.delegated.service;

import java.util.Date;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gaards.cds.common.DelegationIdentifier;
import org.cagrid.gaards.cds.common.DelegationRecord;
import org.cagrid.gaards.cds.common.DelegationStatus;
import org.cagrid.gaards.cds.service.DelegatedCredentialManager;
import org.cagrid.gaards.cds.service.DelegationManager;
import org.cagrid.gaards.cds.stubs.types.CDSInternalFault;
import org.cagrid.gaards.cds.stubs.types.DelegationFault;
import org.globus.wsrf.InvalidResourceKeyException;
import org.globus.wsrf.NoSuchResourceException;
import org.globus.wsrf.RemoveNotSupportedException;
import org.globus.wsrf.Resource;
import org.globus.wsrf.ResourceException;
import org.globus.wsrf.ResourceHome;
import org.globus.wsrf.ResourceKey;
import org.globus.wsrf.impl.SimpleResourceKey;

public class DelegatedCredentialResourceHome implements ResourceHome {

	public static final String SERVICE_NS = "http://cds.gaards.cagrid.org/CredentialDelegationService/DelegatedCredential";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS,
			"DelegatedCredentialKey");

	private DelegationManager cds;
	private Log log;

	public DelegatedCredentialResourceHome() {
		this.log = LogFactory.getLog(this.getClass().getName());

	}

	public Resource find(ResourceKey key) throws ResourceException,
			NoSuchResourceException, InvalidResourceKeyException {
		try {
			DelegationIdentifier id = (DelegationIdentifier) key.getValue();
			DelegatedCredentialManager cdm = cds
					.getDelegatedCredentialManager();
			if (cdm.delegationExists(id)) {
				DelegationRecord r = cdm.getDelegationRecord(id);
				if (r.getDelegationStatus().equals(DelegationStatus.Approved)) {
					Date now = new Date();
					Date expires = new Date(r.getExpiration());
					if (now.before(expires)) {
						return new DelegatedCredentialResource(this.cds, id);
					}
				}
			}
			throw new NoSuchResourceException();
		} catch (DelegationFault f) {
			log.error(f.getFaultString(), f);
			throw new ResourceException(f.getFaultString(), f);
		} catch (CDSInternalFault f) {
			log.error(f.getFaultString(), f);
			throw new ResourceException(f.getFaultString(), f);
		}
	}

	public Class getKeyTypeClass() {
		return DelegationIdentifier.class;
	}

	public QName getKeyTypeName() {
		return RESOURCE_KEY;
	}

	public void remove(ResourceKey arg0) throws ResourceException,
			NoSuchResourceException, InvalidResourceKeyException,
			RemoveNotSupportedException {
		DelegatedCredentialResource r = (DelegatedCredentialResource) find(arg0);
		r.remove();
	}

	public ResourceKey getResourceKey(DelegationIdentifier id) throws Exception {
		ResourceKey key = new SimpleResourceKey(getKeyTypeName(), id);
		return key;
	}

	public DelegationManager getCDS() {
		return cds;
	}

	public void setCDS(DelegationManager cds) {
		this.cds = cds;
	}

}
