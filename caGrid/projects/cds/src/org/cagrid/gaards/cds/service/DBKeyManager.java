package org.cagrid.gaards.cds.service;

import gov.nih.nci.cagrid.common.FaultHelper;

import java.io.ByteArrayInputStream;
import java.security.PrivateKey;

import org.cagrid.gaards.cds.stubs.types.CDSInternalFault;
import org.cagrid.gaards.pki.KeyUtil;
import org.cagrid.tools.database.Database;

public class DBKeyManager extends AbstractDBKeyManager {

	private String keyEncryptionPassword;

	public DBKeyManager(Database db) throws CDSInternalFault {
		super(db);
	}

	public void setKeyEncryptionPassword(String keyEncryptionPassword) {
		this.keyEncryptionPassword = keyEncryptionPassword;
	}

	public PrivateKey unwrapPrivateKey(WrappedKey wrappedKey)
			throws CDSInternalFault {
		try {
			return KeyUtil.loadPrivateKey(new ByteArrayInputStream(wrappedKey
					.getWrappedKeyData()), keyEncryptionPassword);
		} catch (Exception e) {
			getLog().error(e.getMessage(), e);
			CDSInternalFault f = new CDSInternalFault();
			f.setFaultString("Unexpected error unwrapping key.");
			FaultHelper helper = new FaultHelper(f);
			helper.addFaultCause(e);
			f = (CDSInternalFault) helper.getFault();
			throw f;
		}
	}

	public WrappedKey wrapPrivateKey(PrivateKey key) throws CDSInternalFault {
		try {
			WrappedKey wk = new WrappedKey(KeyUtil.writePrivateKey(key,
					keyEncryptionPassword).getBytes(), null);
			return wk;
		} catch (Exception e) {
			getLog().error(e.getMessage(), e);
			CDSInternalFault f = new CDSInternalFault();
			f.setFaultString("Unexpected error wrapping key.");
			FaultHelper helper = new FaultHelper(f);
			helper.addFaultCause(e);
			f = (CDSInternalFault) helper.getFault();
			throw f;
		}
	}

}
