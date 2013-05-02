/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
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
