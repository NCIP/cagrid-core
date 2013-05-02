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
package org.cagrid.grape.utils;

import java.net.URL;

import javax.swing.ImageIcon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class IconUtils {
	protected static Log LOG = LogFactory.getLog(IconUtils.class.getName());

	public static final String MISSING_ICON_LOCATION = "ICON_MISSING.png";
	public static final ImageIcon MISSING_ICON_ICON = new ImageIcon(java.awt.Toolkit.getDefaultToolkit().getImage(
		IconUtils.class.getResource(MISSING_ICON_LOCATION)));


	public static ImageIcon loadIcon(String resource) {
		if (resource == null) {
			LOG.debug("Resource was null, returning default icon.");
			return MISSING_ICON_ICON;
		}

		URL url = IconUtils.class.getResource(resource);
		if (url == null) {
			LOG.debug("Resource was not found (" + resource + "), returning default icon.");
			return MISSING_ICON_ICON;
		}
		try {
			ImageIcon icon = new ImageIcon(java.awt.Toolkit.getDefaultToolkit().getImage(url));
			return icon;
		} catch (Exception e) {
			LOG.debug("Problem loading resource (" + resource + "), returning default icon:" + e.getMessage(), e);
			return MISSING_ICON_ICON;
		}
	}
}
