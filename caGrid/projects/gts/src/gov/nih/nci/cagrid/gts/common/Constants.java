package gov.nih.nci.cagrid.gts.common;

import gov.nih.nci.cagrid.common.Utils;

import java.io.File;


public class Constants {

	public final static String ALL_TRUST_AUTHORITIES = "*";


	public static File getGTSUserHome() {
		File f = new File(Utils.getCaGridUserHome().getAbsolutePath() + File.separator + "gts");
		f.mkdirs();
		return f;
	}
}
