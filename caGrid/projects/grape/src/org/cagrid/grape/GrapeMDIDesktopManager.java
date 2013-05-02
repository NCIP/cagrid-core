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
package org.cagrid.grape;

import javax.swing.JInternalFrame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GrapeMDIDesktopManager extends MDIDesktopManager {
	private static Log log = LogFactory.getLog(GrapeMDIDesktopManager.class);

	public GrapeMDIDesktopManager(MDIDesktopPane desktop) {
		super(desktop);
	}

	public void activateFrame(JInternalFrame f) {
		try {
			super.activateFrame(f);
		} catch (NullPointerException npe) {
			log.info("Caught an NPE that only appears to occur on OS X.  Doesn't cause any problems so ignoring.");
		}
	}

	private static final long serialVersionUID = 1L;

}
