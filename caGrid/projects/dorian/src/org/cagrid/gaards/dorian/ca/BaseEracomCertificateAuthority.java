package org.cagrid.gaards.dorian.ca;

import gov.nih.nci.cagrid.common.FaultHelper;

import java.io.ByteArrayInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;

import org.cagrid.gaards.pki.CertUtil;
import org.cagrid.gaards.pki.KeyUtil;


public abstract class BaseEracomCertificateAuthority extends CertificateAuthority {

    public static final String WRAPPER_KEY_ALIAS = "dorian-wrapper-key";
    public static final String SIGNATURE_ALGORITHM = "SHA1WithRSA";
    public static final String SLOT_PROPERTY = "slot";
    private Provider provider;
    private KeyStore keyStore;
    private Key wrapper;
    private boolean isInit = false;


    public BaseEracomCertificateAuthority(EracomCertificateAuthorityProperties properties)
        throws CertificateAuthorityFault {
        super(properties);
        try {
            isInit = false;
            int slot = properties.getSlot();
            provider = (Provider) Class.forName("au.com.eracom.crypto.provider.slot" + slot + ".ERACOMProvider")
                .newInstance();
            Security.addProvider(provider);
            keyStore = KeyStore.getInstance("CRYPTOKI", provider.getName());
            keyStore.load(null, properties.getCertificateAuthorityPassword().toCharArray());
        } catch (Exception e) {
            logError(e.getMessage(), e);
            CertificateAuthorityFault fault = new CertificateAuthorityFault();
            fault.setFaultString("Error initializing the Dorian Certificate Authority.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CertificateAuthorityFault) helper.getFault();
            throw fault;
        }

    }


    public String getSignatureAlgorithm() {
        return SIGNATURE_ALGORITHM;
    }


    public String getCACredentialsProvider() {
        return getProvider().getName();
    }


    protected Provider getProvider() {
        return provider;
    }


    protected KeyStore getKeyStore() {
        return keyStore;
    }


    protected Key getWrapperKey() {
        return wrapper;
    }


    protected void setInitialized(boolean val) {
        this.isInit = val;
    }


    protected void init() throws CertificateAuthorityFault {
        try {
            if (!isInit) {
                if (keyStore.containsAlias(WRAPPER_KEY_ALIAS)) {
                    wrapper = keyStore.getKey(WRAPPER_KEY_ALIAS, null);
                } else {
                    KeyGenerator generator1 = KeyGenerator.getInstance("AES", provider);
                    generator1.init(256, new SecureRandom());
                    keyStore.setKeyEntry(WRAPPER_KEY_ALIAS, generator1.generateKey(), null, null);
                    wrapper = keyStore.getKey(WRAPPER_KEY_ALIAS, null);
                }
                isInit = true;
            }
        } catch (Exception e) {
            logError(e.getMessage(), e);
            CertificateAuthorityFault fault = new CertificateAuthorityFault();
            fault.setFaultString("An unexpected error occurred, could not add certificate.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CertificateAuthorityFault) helper.getFault();
            throw fault;
        }
    }


    public PrivateKey unwrap(WrappedKey key) throws CertificateAuthorityFault {
        try {
            init();
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", provider);
            IvParameterSpec dps = new IvParameterSpec(key.getIV());
            cipher.init(Cipher.DECRYPT_MODE, wrapper, dps);
            byte[] output = cipher.doFinal(key.getWrappedKeyData());
            return KeyUtil.loadPrivateKey(new ByteArrayInputStream(output), null);
        } catch (Exception e) {
            logError(e.getMessage(), e);
            CertificateAuthorityFault fault = new CertificateAuthorityFault();
            fault.setFaultString("An unexpected error occurred unwrapping a key.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CertificateAuthorityFault) helper.getFault();
            throw fault;
        }
    }


    public WrappedKey wrap(PrivateKey key) throws CertificateAuthorityFault {
        try {
            init();
            byte[] input = KeyUtil.writePrivateKey(key, (String) null).getBytes();
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", provider);
            cipher.init(Cipher.ENCRYPT_MODE, wrapper);
            byte[] wrappedKey = cipher.doFinal(input);
            byte[] iv = cipher.getIV();
            return new WrappedKey(wrappedKey, iv);
        } catch (Exception e) {
            logError(e.getMessage(), e);
            CertificateAuthorityFault fault = new CertificateAuthorityFault();
            fault.setFaultString("An unexpected error occurred wrapping a  key.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CertificateAuthorityFault) helper.getFault();
            throw fault;
        }
    }


    protected X509Certificate convert(X509Certificate cert) throws Exception {
        String str = CertUtil.writeCertificate(cert);
        return CertUtil.loadCertificate(str);
    }

}
