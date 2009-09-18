package gov.nih.nci.cagrid.syncgts.tools;

import gov.nih.nci.cagrid.common.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class DeployGTSTrustRoots {

	public static void main(String[] args) {

		if (args.length != 1) {
			usage();
			System.exit(-1);
		} else {
			try {
				String srcDir = args[0];
				File dir = new File(srcDir);
				if (dir.exists()) {
					File[] list = dir.listFiles();
					if (list != null) {
						for (int i = 0; i < list.length; i++) {
							if (list[i].isFile()) {
								File dest = new File(Utils.getTrustedCerificatesDirectory() + File.separator
									+ list[i].getName());
								copy(list[i], dest);
							}
						}
					}
				} else {
					System.out.println("The source directory, " + dir.getAbsolutePath() + " does not exist.");
					System.exit(-1);
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}


	public static void copy(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}


	public static void usage() {
		System.err.println("java " + DeployGTSTrustRoots.class.getName() + " SOURCE_DIR");
	}
}
