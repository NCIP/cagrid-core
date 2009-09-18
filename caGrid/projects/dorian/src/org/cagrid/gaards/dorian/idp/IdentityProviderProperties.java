package org.cagrid.gaards.dorian.idp;

import gov.nih.nci.cagrid.common.Utils;

import org.cagrid.gaards.dorian.stubs.types.DorianInternalFault;


public class IdentityProviderProperties {
    private static final String DEFAULT_NAME = "Dorian";
    private static final int MIN_UID_LENGTH = 4;
    private static final int MAX_UID_LENGTH = 255;
    private static final int MAX_NAME_LENGTH = 255;

    private String name;
    private boolean autoRenewAssertingCredentials = false;
    private String assertingCredentialsEncryptionPassword;
    private int minUserIdLength = 4;
    private int maxUserIdLength = 255;
    private IdPRegistrationPolicy registrationPolicy;
    private PasswordSecurityPolicy passwordSecurityPolicy;


    public String getName() {
        if (name == null) {
            name = DEFAULT_NAME;
        }
        return name;
    }


    public void setName(String name) throws DorianInternalFault {
        if (name.length() > MAX_NAME_LENGTH) {
            DorianInternalFault f = new DorianInternalFault();
            f.setFaultString("The name of the Dorian IdP cannot exceed " + MAX_NAME_LENGTH + " characters.");
            throw f;
        }
        this.name = name;
    }


    public boolean autoRenewAssertingCredentials() {
        return autoRenewAssertingCredentials;
    }


    public void setAutoRenewAssertingCredentials(boolean autoRenewAssertingCredentials) {
        this.autoRenewAssertingCredentials = autoRenewAssertingCredentials;
    }


    public String getAssertingCredentialsEncryptionPassword() {
        return assertingCredentialsEncryptionPassword;
    }


    public void setAssertingCredentialsEncryptionPassword(String assertingCredentialsEncryptionPassword)
        throws DorianInternalFault {
        if (Utils.clean(assertingCredentialsEncryptionPassword) == null) {
            DorianInternalFault f = new DorianInternalFault();
            f.setFaultString("Invalid asserting credentials password specified.");
            throw f;
        }
        this.assertingCredentialsEncryptionPassword = assertingCredentialsEncryptionPassword;
    }


    public int getMinUserIdLength() {
        return minUserIdLength;
    }


    public void setMinUserIdLength(int minUserIdLength) throws DorianInternalFault {
        if (minUserIdLength < MIN_UID_LENGTH) {
            DorianInternalFault f = new DorianInternalFault();
            f.setFaultString("The minimum user id must be at least " + MIN_UID_LENGTH + " characters.");
            throw f;
        }
        this.minUserIdLength = minUserIdLength;
    }


    public int getMaxUserIdLength() {
        return maxUserIdLength;
    }


    public void setMaxUserIdLength(int maxUserIdLength) throws DorianInternalFault {
        if (maxUserIdLength > MAX_UID_LENGTH) {
            DorianInternalFault f = new DorianInternalFault();
            f.setFaultString("The maximum user id must be no more than " + MAX_UID_LENGTH + " characters.");
            throw f;
        }
        this.maxUserIdLength = maxUserIdLength;
    }


    public IdPRegistrationPolicy getRegistrationPolicy() {
        return registrationPolicy;
    }


    public PasswordSecurityPolicy getPasswordSecurityPolicy() {
        return passwordSecurityPolicy;
    }


    public void setRegistrationPolicy(IdPRegistrationPolicy registrationPolicy) {
        this.registrationPolicy = registrationPolicy;
    }


    public void setPasswordSecurityPolicy(PasswordSecurityPolicy passwordSecurityPolicy) {
        this.passwordSecurityPolicy = passwordSecurityPolicy;
    }

}
