package gov.nih.nci.cagrid.testing.system.deployment;

import java.io.File;

public interface SecureContainer {

    public File getCertificatesDirectory() throws Exception;
}
