package org.cagrid.iso21090.extensions;

import java.io.File;

import org.cagrid.iso21090.portal.discovery.FixISO21090IntroduceStubs;

public class FixISO21090IntroduceStubsTest {

	/**
	 * @param args the Introduce service directory is a required argument 
	 * @throws Exception if there are no args or the directory doesn't exist
	 */
	public static void main(String[] args) throws Exception {
		//required arg is the service directory
		if (args.length == 0) {
			throw new Exception("Pass in the service directory as an argument");
		}
		
		File serviceDir = new File(args[0]);
		
		if (!serviceDir.exists()) {
			throw new Exception("Directory " + serviceDir.getAbsolutePath() + " does not exist");
		}
		
		FixISO21090IntroduceStubs fix = new FixISO21090IntroduceStubs();
		fix.modifyDevBuildFileWithOperationStubsFixes(serviceDir);
	}
}
