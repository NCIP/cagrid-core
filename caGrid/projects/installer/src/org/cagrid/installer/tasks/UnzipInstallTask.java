/**
 * 
 */
package org.cagrid.installer.tasks;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.installer.model.CaGridInstallerModel;
import org.cagrid.installer.steps.Constants;

/**
 * @author <a href="mailto:joshua.phillips@semanticbits.com">Joshua Phillips</a>
 * 
 */
public class UnzipInstallTask extends BasicTask {

	private static final int BUFFER_SIZE = 1024;

	private String tempFileNameProp;

	private String installDirPathProp;

	private String dirNameProp;

	private String homeProp;

	private static final Log logger = LogFactory.getLog(UnzipInstallTask.class);

	public UnzipInstallTask(String name, String description,
			String tempFileNameProp, String installDirPathProp,
			String dirNameProp, String homeProp) {
		super(name, description);
		this.tempFileNameProp = tempFileNameProp;
		this.installDirPathProp = installDirPathProp;
		this.dirNameProp = dirNameProp;
		this.homeProp = homeProp;
	}

	protected Object internalExecute(CaGridInstallerModel model) throws Exception {

		String path = model.getProperty(Constants.TEMP_DIR_PATH) + "/"
		+ model.getProperty(this.tempFileNameProp);
		ZipFile zipFile = null;
		try {
			logger.info("Trying to open ZipFile for " + path);
			zipFile = new ZipFile(new File(path));
		} catch (Exception ex) {
			
			logger.warn("Failed first attempt to open zip file. Trying again.");
			try{
				//Wait 3 seconds
				Thread.sleep(3000);
			}catch(InterruptedException ex2){
				logger.warn("Sleep interrupted");
			}
			try{
				zipFile = new ZipFile(new File(path));				
			}catch(Exception ex2){
				logger.error("Failed second attempt to open zip file. Aborting.", ex2);
				throw new RuntimeException("Error instantiating zip file: "
						+ ex.getMessage(), ex);
			}
		}

		File installDir = new File(model.getProperty(this.installDirPathProp));
		File home = new File(installDir.getAbsolutePath() + "/"
				+ model.getProperty(this.dirNameProp));


		model.setProperty(this.homeProp, home.getAbsolutePath());

		String baseOut = installDir.getAbsolutePath() + "/";
		Enumeration entries = zipFile.entries();
		int logAfterSize = 100;
		int nextLog = -1;
		int numFiles = 0;
		while (entries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) entries.nextElement();
			String fileName = baseOut + entry.getName();
			File file = new File(fileName);

			if (entry.isDirectory()) {
				file.mkdirs();
			} else {
				numFiles++;
				if (numFiles > nextLog) {
					nextLog += logAfterSize;
					System.out.println("Extracting: " + fileName);
				}
				BufferedOutputStream out = null;
				InputStream in = zipFile.getInputStream(entry);
				try {

					if (!file.getParentFile().exists()) {
						file.getParentFile().mkdirs();
					}
					file.createNewFile();
					out = new BufferedOutputStream(new FileOutputStream(file));

				} catch (Exception ex) {
					String msg = "Error creating output stream for '"
							+ file.getAbsolutePath() + "': " + ex.getMessage();
					logger.error(msg, ex);
					throw new RuntimeException(msg, ex);
				}
				byte[] buffer = new byte[BUFFER_SIZE];
				int len = -1;
				while ((len = in.read(buffer)) > 0) {
					out.write(buffer, 0, len);
					buffer = new byte[BUFFER_SIZE];
				}
				out.flush();
				out.close();
				in.close();
			}

		}
		zipFile.close();

		return null;
	}
}
