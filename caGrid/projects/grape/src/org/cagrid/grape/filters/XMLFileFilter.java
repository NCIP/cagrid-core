package org.cagrid.grape.filters;

import java.io.File;


/**
 * Filter all but XML files
 * 
 * @author <A href="mailto:ervin@bmi.osu.edu">David W. Ervin</A>
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster</A>
 * @created May 27, 2004
 * @version $Id: XMLFileFilter.java,v 1.4 2007-03-16 14:02:48 dervin Exp $
 */

public class XMLFileFilter extends javax.swing.filechooser.FileFilter implements java.io.FileFilter {
	public static final String XML_EXTENSION = ".xml";


	public XMLFileFilter() {
		super();
	}


	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}
		return f.getName().toLowerCase().endsWith(XML_EXTENSION);
	}


	public String getDescription() {
		return "XML Documents (*.xml)";
	}
}
