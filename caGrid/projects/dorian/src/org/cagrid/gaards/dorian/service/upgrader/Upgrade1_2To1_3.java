package org.cagrid.gaards.dorian.service.upgrader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.cagrid.gaards.dorian.ca.CredentialsManager;
import org.cagrid.gaards.dorian.ca.DBCertificateAuthority;
import org.cagrid.gaards.dorian.ca.EracomCertificateAuthority;
import org.cagrid.gaards.dorian.federation.AutoApprovalPolicy;
import org.cagrid.gaards.dorian.federation.CertificateBlacklistManager;
import org.cagrid.gaards.dorian.federation.HostCertificateManager;
import org.cagrid.gaards.dorian.federation.ManualApprovalPolicy;
import org.cagrid.gaards.dorian.federation.TrustedIdP;
import org.cagrid.gaards.dorian.federation.TrustedIdPManager;
import org.cagrid.gaards.dorian.federation.UserManager;
import org.cagrid.gaards.dorian.idp.PasswordSecurityManager;
import org.cagrid.gaards.dorian.service.PropertyManager;
import org.cagrid.tools.database.Database;


public class Upgrade1_2To1_3 extends Upgrade {

    public String getStartingVersion() {
        return PropertyManager.DORIAN_VERSION_1_2;
    }


    public String getUpgradedVersion() {
        return PropertyManager.DORIAN_VERSION_1_3;
    }


    private void upgradeBlacklistManager(boolean trialRun) throws Exception {
        Database db = getBeanUtils().getDatabase();
        CertificateBlacklistManager bm = new CertificateBlacklistManager(db);
        if (!trialRun) {
            bm.buildDatabase();
        }
        Connection c = null;
        try {
            if (!trialRun) {
                System.out.print("Updating the Certificate Blacklist field " + CertificateBlacklistManager.SUBJECT
                    + " from VARCHAR to TEXT....");
                c = db.getConnection();
                PreparedStatement s = c.prepareStatement("ALTER TABLE " + CertificateBlacklistManager.TABLE
                    + " MODIFY " + CertificateBlacklistManager.SUBJECT + " TEXT NOT NULL");
                s.execute();
                s.close();
                System.out.println(" COMPLETED.");
            } else {
                System.out.print("The Certificate Blacklist field " + CertificateBlacklistManager.SUBJECT
                    + " needs to be updated from from VARCHAR to TEXT.");
            }
        } catch (Exception e) {
            throw e;
        } finally {
            db.releaseConnection(c);
        }
    }


    private void upgradeGridUserManager(boolean trialRun) throws Exception {
        Database db = getBeanUtils().getDatabase();
        if (db.tableExists(UserManager.USERS_TABLE)) {

            Connection c = null;
            try {
                if (!trialRun) {
                    System.out.print("Updating the grid user field " + UserManager.GID_FIELD
                        + " from VARCHAR to TEXT....");
                    c = db.getConnection();
                    PreparedStatement s = c.prepareStatement("ALTER TABLE " + UserManager.USERS_TABLE + " MODIFY "
                        + UserManager.GID_FIELD + " TEXT NOT NULL");
                    s.execute();
                    s.close();
                    System.out.println(" COMPLETED.");
                } else {
                    System.out.print("The grid user field " + UserManager.GID_FIELD
                        + " needs to be updated from from VARCHAR to TEXT.");
                }

            } catch (Exception e) {
                throw e;
            } finally {
                db.releaseConnection(c);
            }
        }
    }


    private void upgradeHostCertificateManager(boolean trialRun) throws Exception {
        Database db = getBeanUtils().getDatabase();
        if (db.tableExists(HostCertificateManager.TABLE)) {

            Connection c = null;
            try {
                if (!trialRun) {
                    System.out.print("Updating the host certificate field " + HostCertificateManager.SUBJECT
                        + " from VARCHAR to TEXT....");
                    c = db.getConnection();
                    PreparedStatement s = c.prepareStatement("ALTER TABLE " + HostCertificateManager.TABLE + " MODIFY "
                        + HostCertificateManager.SUBJECT + " TEXT NOT NULL");
                    s.execute();
                    s.close();
                    System.out.println(" COMPLETED.");
                } else {
                    System.out.print("The host certificate field " + HostCertificateManager.SUBJECT
                        + " needs to be updated from from VARCHAR to TEXT.");
                }

                if (!trialRun) {
                    System.out.print("Updating the host certificate field " + HostCertificateManager.OWNER
                        + " from VARCHAR to TEXT....");
                    PreparedStatement s = c.prepareStatement("ALTER TABLE " + HostCertificateManager.TABLE + " MODIFY "
                        + HostCertificateManager.OWNER + " TEXT NOT NULL");
                    s.execute();
                    s.close();
                    System.out.println(" COMPLETED.");
                } else {
                    System.out.print("The host certificate field " + HostCertificateManager.OWNER
                        + " needs to be updated from from VARCHAR to TEXT.");
                }
            } catch (Exception e) {
                throw e;
            } finally {
                db.releaseConnection(c);
            }
        }
    }


    private void upgradeTrustedIdentityProviders(boolean trialRun) throws Exception {
        Database db = getBeanUtils().getDatabase();
        TrustedIdPManager idp = new TrustedIdPManager(getBeanUtils().getIdentityFederationProperties(), db);
        if (!trialRun) {
            idp.buildDatabase();
        }

        boolean hasDisplayName = false;
        boolean hasAuthenticationServiceURL = false;
        boolean hasAuthenticationServiceIdentity = false;

        Connection c = null;
        try {
            c = db.getConnection();
            PreparedStatement s = c.prepareStatement("show COLUMNS FROM " + TrustedIdPManager.TRUST_MANAGER_TABLE);
            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                if (rs.getString(1).equals(TrustedIdPManager.DISPLAY_NAME_FIELD)) {
                    hasDisplayName = true;
                }
                if (rs.getString(1).equals(TrustedIdPManager.AUTHENTICATION_SERVICE_URL_FIELD)) {
                    hasAuthenticationServiceURL = true;
                }
                if (rs.getString(1).equals(TrustedIdPManager.AUTHENTICATION_SERVICE_IDENTITY_FIELD)) {
                    hasAuthenticationServiceIdentity = true;
                }
            }
            rs.close();
            s.close();

            if (!hasDisplayName) {
                if (!trialRun) {
                    System.out.print("Adding " + TrustedIdPManager.DISPLAY_NAME_FIELD
                        + " field to the TrustedIdP database....");
                    s = c.prepareStatement("ALTER TABLE " + TrustedIdPManager.TRUST_MANAGER_TABLE + " ADD "
                        + TrustedIdPManager.DISPLAY_NAME_FIELD + " TEXT NOT NULL");
                    s.execute();
                    s.close();
                    System.out.println(" COMPLETED.");
                } else {
                    System.out.print("The field " + TrustedIdPManager.DISPLAY_NAME_FIELD
                        + " needs to be added to the TrustedIdP database.");
                }
            }

            if (!hasAuthenticationServiceURL) {
                if (!trialRun) {
                    System.out.print("Adding " + TrustedIdPManager.AUTHENTICATION_SERVICE_URL_FIELD
                        + " field to the TrustedIdP database....");
                    s = c.prepareStatement("ALTER TABLE " + TrustedIdPManager.TRUST_MANAGER_TABLE + " ADD "
                        + TrustedIdPManager.AUTHENTICATION_SERVICE_URL_FIELD + " TEXT NOT NULL");
                    s.execute();
                    s.close();
                    System.out.println(" COMPLETED.");
                } else {
                    System.out.print("The field " + TrustedIdPManager.AUTHENTICATION_SERVICE_URL_FIELD
                        + " needs to be added to the TrustedIdP database.");
                }
            }

            if (!hasAuthenticationServiceIdentity) {
                if (!trialRun) {
                    System.out.print("Adding " + TrustedIdPManager.AUTHENTICATION_SERVICE_IDENTITY_FIELD
                        + " field to the TrustedIdP database....");
                    s = c.prepareStatement("ALTER TABLE " + TrustedIdPManager.TRUST_MANAGER_TABLE + " ADD "
                        + TrustedIdPManager.AUTHENTICATION_SERVICE_IDENTITY_FIELD + " TEXT NOT NULL");
                    s.execute();
                    s.close();
                    System.out.println(" COMPLETED.");
                } else {
                    System.out.print("The field " + TrustedIdPManager.AUTHENTICATION_SERVICE_IDENTITY_FIELD
                        + " needs to be added to the TrustedIdP database.");
                }
            }

            if (!trialRun) {
                System.out.print("Updating the TrustedIdP field " + TrustedIdPManager.IDP_SUBJECT_FIELD
                    + " from VARCHAR to TEXT....");
                s = c.prepareStatement("ALTER TABLE " + TrustedIdPManager.TRUST_MANAGER_TABLE + " MODIFY "
                    + TrustedIdPManager.IDP_SUBJECT_FIELD + " TEXT NOT NULL");
                s.execute();
                s.close();
                System.out.println(" COMPLETED.");
            } else {
                System.out.print("The TrustedIdP field " + TrustedIdPManager.POLICY_CLASS_FIELD
                    + " needs to be updated from from VARCHAR to TEXT.");
            }

            if (!trialRun) {
                System.out.print("Updating the TrustedIdP field " + TrustedIdPManager.IDP_SUBJECT_FIELD
                    + " from VARCHAR to TEXT....");

                s = c.prepareStatement("ALTER TABLE " + TrustedIdPManager.TRUST_MANAGER_TABLE + " MODIFY "
                    + TrustedIdPManager.POLICY_CLASS_FIELD + " TEXT NOT NULL");
                s.execute();
                s.close();
                System.out.println(" COMPLETED.");
            } else {
                System.out.print("The TrustedIdP field " + TrustedIdPManager.POLICY_CLASS_FIELD
                    + " needs to be updated from from VARCHAR to TEXT.");
            }

            if (!trialRun) {
                System.out.print("Updating the trusted identity provider policies....");
                s = c.prepareStatement("update " + TrustedIdPManager.TRUST_MANAGER_TABLE + " SET "
                    + TrustedIdPManager.POLICY_CLASS_FIELD + "='" + AutoApprovalPolicy.class.getName() + "' WHERE "
                    + TrustedIdPManager.POLICY_CLASS_FIELD
                    + "='gov.nih.nci.cagrid.dorian.service.ifs.AutoApprovalAutoRenewalPolicy' OR "
                    + TrustedIdPManager.POLICY_CLASS_FIELD
                    + "='gov.nih.nci.cagrid.dorian.service.ifs.AutoApprovalPolicy'");
                s.execute();
                s.close();
                s = c.prepareStatement("update " + TrustedIdPManager.TRUST_MANAGER_TABLE + " SET "
                    + TrustedIdPManager.POLICY_CLASS_FIELD + "='" + ManualApprovalPolicy.class.getName() + "' WHERE "
                    + TrustedIdPManager.POLICY_CLASS_FIELD
                    + "='gov.nih.nci.cagrid.dorian.service.ifs.ManualApprovalAutoRenewalPolicy' OR "
                    + TrustedIdPManager.POLICY_CLASS_FIELD
                    + "='gov.nih.nci.cagrid.dorian.service.ifs.ManualApprovalPolicy'");
                s.execute();
                s.close();
                System.out.println(" COMPLETED.");
            } else {
                System.out.println("The trusted identity provider policies need to be updated.");
            }
            if (!trialRun) {
                TrustedIdP[] list = idp.getTrustedIdPs();
                if (list != null) {
                    for (int i = 0; i < list.length; i++) {
                        list[i].setDisplayName(list[i].getName());
                        idp.updateIdP(list[i]);
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            db.releaseConnection(c);
        }
    }


    private void upgradePasswordSecurity(boolean trialRun) throws Exception {
        Database db = getBeanUtils().getDatabase();
        PasswordSecurityManager psm = new PasswordSecurityManager(db, getBeanUtils().getIdentityProviderProperties()
            .getPasswordSecurityPolicy());
        if (!trialRun) {
            psm.buildDatabase();
        }

        boolean hasPasswordSalt = false;
        boolean hasEncryptionAlgorithm = false;

        Connection c = null;
        try {
            c = db.getConnection();
            PreparedStatement s = c.prepareStatement("show COLUMNS FROM " + PasswordSecurityManager.TABLE);
            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                if (rs.getString(1).equals(PasswordSecurityManager.DIGEST_SALT)) {
                    hasPasswordSalt = true;
                }
                if (rs.getString(1).equals(PasswordSecurityManager.DIGEST_ALGORITHM)) {
                    hasEncryptionAlgorithm = true;
                }

            }
            rs.close();
            s.close();

            if (!hasPasswordSalt) {
                if (!trialRun) {
                    System.out.print("Adding " + PasswordSecurityManager.DIGEST_SALT
                        + " field to the password security database....");
                    s = c.prepareStatement("ALTER TABLE " + PasswordSecurityManager.TABLE + " ADD "
                        + PasswordSecurityManager.DIGEST_SALT + " VARCHAR(255)");
                    s.execute();
                    s.close();
                    System.out.println(" COMPLETED.");
                } else {
                    System.out.println("The " + PasswordSecurityManager.DIGEST_SALT
                        + " needs to be added to the password security database.");
                }
            }

            if (!hasEncryptionAlgorithm) {
                if (!trialRun) {
                    System.out.print("Adding " + PasswordSecurityManager.DIGEST_ALGORITHM
                        + " field to the password security database....");
                    s = c.prepareStatement("ALTER TABLE " + PasswordSecurityManager.TABLE + " ADD "
                        + PasswordSecurityManager.DIGEST_ALGORITHM + " VARCHAR(25)");
                    s.execute();
                    s.close();
                    System.out.println(" COMPLETED.");
                } else {
                    System.out.println("The " + PasswordSecurityManager.DIGEST_ALGORITHM
                        + " needs to be added to the password security database.");
                }
            }

        } catch (Exception e) {
            throw e;
        } finally {
            db.releaseConnection(c);
        }
    }


    private void clearDBCertificateAuthority() throws Exception {
        Database db = getBeanUtils().getDatabase();
        Connection c = null;
        try {
            c = db.getConnection();
            PreparedStatement s = c.prepareStatement("delete from " + CredentialsManager.CREDENTIALS_TABLE
                + " where ALIAS <> 'dorianca'");
            s.execute();
            s.close();
        } catch (Exception e) {
            throw e;
        } finally {
            db.releaseConnection(c);
        }
    }


    private void clearEracomHybridCertificateAuthority() throws Exception {
        Database db = getBeanUtils().getDatabase();
        Connection c = null;
        try {
            c = db.getConnection();
            PreparedStatement s = c.prepareStatement("DROP TABLE IF EXISTS eracom_wrapped_ca");
            s.execute();
            s.close();
        } catch (Exception e) {
            throw e;
        } finally {
            db.releaseConnection(c);
        }
    }


    private void upgradeCertificateAuthority(PropertyManager pm, boolean trialRun) throws Exception {
        String caType = pm.getCertificateAuthorityType();
        String newCAType = null;
        boolean clearDBCA = false;
        boolean clearEracomHybridCA = false;
        if (caType.equals("DBCA")) {
            newCAType = DBCertificateAuthority.class.getName();
            clearDBCA = true;
        } else if (caType.equals("Eracom")) {
            newCAType = EracomCertificateAuthority.class.getName();
        } else if (caType.equals("EracomHybrid")) {
            newCAType = EracomCertificateAuthority.class.getName();
            clearEracomHybridCA = true;
        }
        if (newCAType != null) {
            if (!trialRun) {
                System.out.print("Updating the CA type from " + caType + " to " + newCAType + "....");
                pm.setCertificateAuthorityType(newCAType);
                System.out.println(" COMPLETED.");
            } else {
                System.out.println("The CA type needs to be updated from " + caType + " to " + newCAType + ".");
            }
        } else {
            throw new Exception("Could not determine how to upgrade the Certificate Authority Type " + caType + ".");
        }
        if (clearDBCA) {
            if (!trialRun) {
                System.out.print("Deleting long term user credentials from the DB CA....");
                clearDBCertificateAuthority();
                System.out.println(" COMPLETED.");
            } else {
                System.out.println("Long term user credentials need to be deleted from the DB CA.");
            }
        }

        if (clearEracomHybridCA) {
            if (!trialRun) {
                System.out.print("Deleting long term user credentials from the Eracom Hybrid CA....");
                clearEracomHybridCertificateAuthority();
                System.out.println(" COMPLETED.");
            } else {
                System.out.println("Long term user credentials need to be deleted from the Eracom Hybird CA.");
            }
        }
    }


    public void upgrade(boolean trialRun) throws Exception {
        Database db = getBeanUtils().getDatabase();
        db.createDatabaseIfNeeded();
        PropertyManager pm = new PropertyManager(db);
        if (pm.getVersion().equals(PropertyManager.DORIAN_VERSION_1_2)) {
            if (!trialRun) {
                System.out.print("Upgrading version number from " + pm.getVersion() + " to "
                    + PropertyManager.DORIAN_VERSION_1_3 + "....");
                pm.setVersion(PropertyManager.DORIAN_VERSION_1_3);
                System.out.println(" COMPLETED.");
            } else {
                System.out.println("The version needs to be upgraded from " + pm.getVersion() + " to "
                    + PropertyManager.DORIAN_VERSION_1_3 + ".");
            }
            upgradeCertificateAuthority(pm, trialRun);
            upgradePasswordSecurity(trialRun);
            upgradeTrustedIdentityProviders(trialRun);
            upgradeBlacklistManager(trialRun);
            upgradeHostCertificateManager(trialRun);
            upgradeGridUserManager(trialRun);
        } else {
            if (!trialRun) {
                throw new Exception("Failed to run upgrader " + getClass().getName()
                    + " the version of Dorian you are running is not " + PropertyManager.DORIAN_VERSION_1_2 + ".");
            }
        }

    }
}
