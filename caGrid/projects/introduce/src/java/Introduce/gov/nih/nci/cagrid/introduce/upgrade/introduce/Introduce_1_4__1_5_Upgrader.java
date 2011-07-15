/**
 * 
 */
package gov.nih.nci.cagrid.introduce.upgrade.introduce;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.upgrade.common.IntroduceUpgradeStatus;
import gov.nih.nci.cagrid.introduce.upgrade.common.StatusBase;
import gov.nih.nci.cagrid.introduce.upgrade.one.x.IntroduceUpgraderBase;

import java.io.File;
import java.io.IOException;

/**
 * Upgrade a service created by introduce 1.4 to introduce 1.5 standards.
 * 
 * @author Mark Grand
 */
public class Introduce_1_4__1_5_Upgrader extends IntroduceUpgraderBase {

    /**
     * Constructor
     * 
     * @param status
     * @param serviceInformation
     * @param servicePath
     * @throws Exception
     */
    public Introduce_1_4__1_5_Upgrader(IntroduceUpgradeStatus status, ServiceInformation serviceInformation, String servicePath) throws Exception {
        super(status, serviceInformation, servicePath, "1.4", "1.5");
    }

    /* (non-Javadoc)
     * @see gov.nih.nci.cagrid.introduce.upgrade.one.x.IntroduceUpgraderBase#upgrade()
     */
    @Override
    protected void upgrade() throws Exception {
		replaceOldBuildFilesWithNew();
		

		upgradeJars();
		fixDevBuildDeploy();
		fixSource();
		fixWSDD();
		fixSecurityOnMetadataAccessProviders();

		OrganizeImports oi = new OrganizeImports(new File(getServicePath()));
		oi.runStep();

		getStatus().setStatus(StatusBase.UPGRADE_OK);
    }

	protected void fixDevBuildDeploy() throws Exception {

	}

	protected void fixSecurityOnMetadataAccessProviders() {

	}

	protected void fixSource() throws Exception {

	}

	protected void fixWSDD() throws Exception {

	}

	private void upgradeJars() throws Exception {
		removeOldCagridGlobusAndMobiusJars();
		copySkeletonJarsToLib();

		// remove the old introduce tools jar
		File serviceToolsLibDir = new File(new File(getServicePath(), "tools"), "lib");
		File skeletonToolsLibDir = new File(new File(SKELETON_DIR, "tools"), "lib");
		File serviceTasksJar = new File(serviceToolsLibDir, "caGrid-Introduce-buildTools-1.3.jar");
		serviceTasksJar.delete();

		// copy new libraries into tools (every thing in skeleton/tool/lib)
		File[] skeletonToolsLibs = skeletonToolsLibDir.listFiles(JAR_FILTER);
		for (int i = 0; i < skeletonToolsLibs.length; i++) {
			File out = new File(serviceToolsLibDir.getAbsolutePath() + File.separator + skeletonToolsLibs[i].getName());
			try {
				Utils.copyFile(skeletonToolsLibs[i], out);
				getStatus().addDescriptionLine(skeletonToolsLibs[i].getName() + " added");
			} catch (IOException ex) {
				throw new Exception("Error copying library (" + skeletonToolsLibs[i] + ") to service: " + ex.getMessage(), ex);
			}
		}
	}
}
