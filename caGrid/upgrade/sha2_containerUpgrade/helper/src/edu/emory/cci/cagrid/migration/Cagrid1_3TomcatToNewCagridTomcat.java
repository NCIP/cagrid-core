package edu.emory.cci.cagrid.migration;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

/**
 * Copy jars and the service directory for a deployed CaGrid service from the
 * old tomcat container to a new tomcat container, substituting newer caGrid
 * jars as seems appropriate. Also update the service's introduceDeployment.xml
 * file to reflect changes in file names.
 * 
 * @author Mark Grand
 */
public class Cagrid1_3TomcatToNewCagridTomcat {
	private File newServiceDir;
	private File newLibDir;

	/**
	 * Constructor
	 * 
	 * @param oldServiceDir
	 * @param newTomcatDir
	 */
	public Cagrid1_3TomcatToNewCagridTomcat(File oldServiceDir,
			File newTomcatDir) {
		super();
		String serviceDirName = oldServiceDir.getName();
		File webappsDir = new File(newTomcatDir, "webapps");
		File wsrfDir = new File(webappsDir, "wsrf");
		File webinfDir = new File(wsrfDir, "WEB-INF");
		newLibDir = new File(webinfDir, "lib");
		File etcDir = new File(wsrfDir, "etc");
		newServiceDir = new File(etcDir, serviceDirName);
	}

	/**
	 * @param args
	 *            A two element array containing the path of the service
	 *            directory in the old tomcat container and the path of the new
	 *            tomcat container.
	 */
	public static void main(String[] args) {
		try {
			if (args.length != 2) {
				String msg = "The program requires two command-line arguments:"
						+ " the path of the old tomcat's service directory and the path of the new tomcat container";
				System.err.println(msg);
				System.exit(1);
			}
			File oldServiceDir = new File(args[0]);
			ensureIsDirectory(oldServiceDir);
			File newTomcatDir = new File(args[1]);
			ensureIsDirectory(newTomcatDir);
			Cagrid1_3TomcatToNewCagridTomcat instance = new Cagrid1_3TomcatToNewCagridTomcat(
					oldServiceDir, newTomcatDir);
			instance.copyIntroduceJars();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(99);
		}
	}

	/**
	 * Make a modified copy of introduceDeployment.xml. For each .jar file
	 * listed, a determination is made if it identified a .jar file that has
	 * been superseded in
	 * 
	 * @throws Exception
	 * 
	 */
	private void copyIntroduceJars() throws Exception {
		File introduceDeployment = new File(newServiceDir,
				"introduceDeployment.xml");
		if (!introduceDeployment.isFile()) {
			System.err.println(introduceDeployment.getAbsolutePath()
					+ " does not exist or is not a regular file.");
			System.exit(3);
		}
		makeBackupCopy(introduceDeployment);
	}

	/**
	 * Make a backup copy of the given file. The name of the backup copy will be
	 * the name of the original followed by ".backup".
	 * 
	 * @param f
	 *            The file to be copied
	 * @return A file object identifying the backup copy
	 * @throws IOException
	 *             if there is a problem.
	 */
	private File makeBackupCopy(File f) throws IOException {
		File backup = new File(f.getParent(), f.getName() + ".backup");
		FileUtils.copyFileToDirectory(f, backup, true);
		return backup;
	}

	private static void ensureIsDirectory(File dir) {
		if (!dir.isDirectory()) {
			String msg = dir.getAbsolutePath()
					+ " does not exist or is not a directory";
			System.err.println(msg);
			System.exit(2);
		}
	}

}
