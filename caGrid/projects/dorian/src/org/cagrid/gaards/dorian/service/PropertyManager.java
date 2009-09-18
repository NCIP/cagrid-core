package org.cagrid.gaards.dorian.service;

import org.cagrid.gaards.dorian.Metadata;
import org.cagrid.gaards.dorian.stubs.types.DorianInternalFault;
import org.cagrid.tools.database.Database;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class PropertyManager {

    private static String TABLE = "properties";
    private static String VERSION_PROPERTY = "version";
    private static String CA_TYPE_PROPERTY = "certificate authority";

    public static String DORIAN_VERSION_1_3 = "1.3";
    public static String DORIAN_VERSION_1_2 = "1.2";
    public static String DORIAN_VERSION_1_1 = "1.1";
    public static String DORIAN_VERSION_1_0 = "1.0";
    public static String CURRENT_VERSION = DORIAN_VERSION_1_3;
    private MetadataManager manager;
    private Metadata version;
    private Metadata certificateAuthorityType;


    public PropertyManager(Database db) throws DorianInternalFault {
        this.manager = new MetadataManager(db, TABLE);
        version = manager.get(VERSION_PROPERTY);
        if (version == null) {
            version = new Metadata();
            version.setName(VERSION_PROPERTY);
            version.setDescription("The software version of this Dorian.");
        }
        certificateAuthorityType = manager.get(CA_TYPE_PROPERTY);
    }


    public String getCertificateAuthorityType() {
        if (this.certificateAuthorityType == null) {
            return null;
        } else {
            return certificateAuthorityType.getValue();
        }
    }


    public void setCertificateAuthorityType(String caType) throws DorianInternalFault {
        this.certificateAuthorityType = new Metadata();
        this.certificateAuthorityType.setName(CA_TYPE_PROPERTY);
        this.certificateAuthorityType.setDescription("The certificate authority type used by this Dorian.");
        this.certificateAuthorityType.setValue(caType);
        this.manager.update(this.certificateAuthorityType);
    }


    public void setCurrentVersion() throws DorianInternalFault {
        this.setVersion(CURRENT_VERSION);
    }


    public void setVersion(String version) throws DorianInternalFault {
        this.version.setValue(String.valueOf(version));
        this.manager.update(this.version);
    }


    public String getVersion() {
        String s = this.version.getValue();
        if (s == null) {
            return CURRENT_VERSION;
        } else {
            return s;
        }
    }

}
