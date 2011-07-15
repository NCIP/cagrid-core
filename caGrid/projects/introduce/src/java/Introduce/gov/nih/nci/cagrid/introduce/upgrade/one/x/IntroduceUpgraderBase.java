package gov.nih.nci.cagrid.introduce.upgrade.one.x;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.upgrade.common.IntroduceUpgradeStatus;
import gov.nih.nci.cagrid.introduce.upgrade.common.IntroduceUpgraderI;
import gov.nih.nci.cagrid.introduce.upgrade.common.StatusBase;

import org.apache.log4j.Logger;

public abstract class IntroduceUpgraderBase implements IntroduceUpgraderI {

	private static final Logger logger = Logger.getLogger(IntroduceUpgraderBase.class);

	ServiceInformation serviceInformation;
	IntroduceUpgradeStatus status;
	String fromVersion;
	String toVersion;
	String servicePath;

	protected static final FileFilter JAR_FILTER = new FileFilter() {
		public boolean accept(File name) {
			String filename = name.getName();
			return filename.endsWith(".jar");
		}
	};

	protected static final File SKELETON_DIR = new File("skeleton");

	public IntroduceUpgraderBase(IntroduceUpgradeStatus status, ServiceInformation serviceInformation, String servicePath, String fromVersion, String toVersion)
			throws Exception {
		this.status = status;
		this.serviceInformation = serviceInformation;
		this.fromVersion = fromVersion;
		this.toVersion = toVersion;
		this.servicePath = servicePath;
		status.setFromVersion(fromVersion);
		status.setToVersion(toVersion);
		status.setType(StatusBase.UPGRADE_TYPE_INTRODUCE);
		status.setName("IntroduceUpgrader " + fromVersion + " - " + toVersion);

	}

	public void execute() throws Exception {
		logger.info("Upgrading Introduce Service From Version " + this.getFromVersion() + " to Version " + this.getToVersion());
		upgrade();
		getServiceInformation().getServiceDescriptor().setIntroduceVersion(getToVersion());
	}

	public String getFromVersion() {
		return fromVersion;
	}

	public void setFromVersion(String fromVersion) {
		this.fromVersion = fromVersion;
	}

	public String getToVersion() {
		return toVersion;
	}

	public void setToVersion(String toVersion) {
		this.toVersion = toVersion;
	}

	protected abstract void upgrade() throws Exception;

	public ServiceInformation getServiceInformation() {
		return serviceInformation;
	}

	public void setServiceInformation(ServiceInformation serviceInformation) {
		this.serviceInformation = serviceInformation;
	}

	public String getServicePath() {
		return servicePath;
	}

	public void setServicePath(String servicePath) {
		this.servicePath = servicePath;
	}

	public IntroduceUpgradeStatus getStatus() {
		return status;
	}

	public void setStatus(IntroduceUpgradeStatus status) {
		this.status = status;
	}

	/**
	 * Replace old build files with new ones
	 * 
	 * @throws IOException
	 *             if there is a problem.
	 */
	protected void replaceOldBuildFilesWithNew() throws IOException {
		Utils.copyFile(new File(getServicePath(), "build.xml"), new File(getServicePath(), "build.xml.OLD"));
		Utils.copyFile(new File(getServicePath(), "build-deploy.xml"), new File(getServicePath(), "build-deploy.xml.OLD"));
		Utils.copyFile(new File(getServicePath(), "run-tools.xml"), new File(getServicePath(), "run-tools.xml.OLD"));
		Utils.copyFile(new File(SKELETON_DIR, "build.xml"), new File(getServicePath(), "build.xml"));
		Utils.copyFile(new File(SKELETON_DIR, "build-deploy.xml"), new File(getServicePath(), "build-deploy.xml"));
	}

	/**
	 * @return
	 */
	protected File getServiceLibDir() {
		File serviceLibDir = new File(getServicePath(), "lib");
		return serviceLibDir;
	}

	/**
	 * 
	 */
	protected void removeOldCagridGlobusAndMobiusJars() throws Exception {
		OldCagridGlobusAndMobiusJarsFilter oldDskeletonLibFilter = new OldCagridGlobusAndMobiusJarsFilter();

		File[] serviceLibs = getServiceLibDir().listFiles(oldDskeletonLibFilter);
		if (serviceLibs == null || serviceLibs.length == 0) {
			String msg = getServiceLibDir().getAbsolutePath() + " does not exist or is empty.\n"
					+ "This condition can be caused when the project's externally created .jar files\n"
					+ "are managed by a dependency management tool such as Ivy or maven ant and the\n" + "has just been used to clean the project.\n"
					+ "The upgrader cannot function properly unless all of the .jar files that are\n" + "used to build the project with caGrid "
					+ getFromVersion() + " are present in its lib directory.";
			getStatus().addDescriptionLine(msg);
			throw new Exception(msg);
		}

		// delete the old libraries
		for (int i = 0; i < serviceLibs.length; i++) {
			boolean deleted = serviceLibs[i].delete();
			if (deleted) {
				getStatus().addDescriptionLine(serviceLibs[i].getName() + " removed");
			} else {
				getStatus().addDescriptionLine(serviceLibs[i].getName() + " could not be removed");
			}
		}
	}

	/**
	 * @throws Exception
	 */
	protected void copySkeletonJarsToLib() throws Exception {
		File skeletonLibDir = new File(SKELETON_DIR, "lib");

		// copy new libraries in (every thing in skeleton/lib)
		File[] skeletonLibs = skeletonLibDir.listFiles(JAR_FILTER);
		for (int i = 0; i < skeletonLibs.length; i++) {
			File out = new File(getServiceLibDir().getAbsolutePath() + File.separator + skeletonLibs[i].getName());
			try {
				Utils.copyFile(skeletonLibs[i], out);
				getStatus().addDescriptionLine(skeletonLibs[i].getName() + " added");
			} catch (IOException ex) {
				throw new Exception("Error copying library (" + skeletonLibs[i] + ") to service: " + ex.getMessage(), ex);
			}
		}
	}

	protected final class OldCagridGlobusAndMobiusJarsFilter implements FileFilter {
		public OldCagridGlobusAndMobiusJarsFilter() {
		}

		public boolean accept(File name) {
			String filename = name.getName();
			if (!filename.endsWith(".jar")) {
				return false;
			}
			// Can't ignore caGrid-dorian-stubs-1.2.jar and caGrid-BulkDataHandler*
			return (filename.startsWith("caGrid-") && !filename.equals("caGrid-dorian-stubs-1.2.jar") && !filename.startsWith("caGrid-BulkDataHandler")) //
					|| filename.startsWith("globus_wsrf_mds") //
					|| filename.startsWith("globus_wsrf_servicegroup") //
					|| filename.startsWith("mobius") //
					// Added for 1.5
					;
		}

	}
}
