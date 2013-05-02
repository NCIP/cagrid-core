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
package gov.nih.nci.cagrid.introduce.common;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public interface FileFilters {

	public static final FileFilter XSD_FILTER = new XSDFileFilter();

	public static final FileFilter XML_FILTER = new XMLFileFilter();
	
	public static final FileFilter XMI_FILTER = new XMIFileFilter();
	
	public static final FileFilter JAR_FILTER = new JarFileFilter();
	
	public static final FileFilter WSDL_FILTER = new WSDLFileFilter();

	public class XSDFileFilter extends javax.swing.filechooser.FileFilter implements java.io.FileFilter {
		public boolean accept(File file) {
			String filename = file.getName();
			return file.isDirectory() || filename.endsWith(".xsd");
		}

		public String getDescription() {
			return "XML Schema Files (*.xsd)";
		}
	}
	
	public class XMIFileFilter extends javax.swing.filechooser.FileFilter implements java.io.FileFilter {
		public boolean accept(File file) {
			String filename = file.getName();
			return file.isDirectory() || filename.endsWith(".xmi");
		}

		public String getDescription() {
			return "XML Metadata Interchange Files (*.xmi)";
		}
	}
	
	public class WSDLFileFilter extends javax.swing.filechooser.FileFilter implements java.io.FileFilter {
		public boolean accept(File file) {
			String filename = file.getName();
			return file.isDirectory() || filename.endsWith(".wsdl");
		}

		public String getDescription() {
			return "WSDL Files (*.wsdl)";
		}
	}

	public class XMLFileFilter extends javax.swing.filechooser.FileFilter implements java.io.FileFilter {
		public boolean accept(File file) {
			String filename = file.getName();
			return file.isDirectory() || filename.endsWith(".xml");
		}

		public String getDescription() {
			return "XML Files (*.xml)";
		}
	}

	public class JarFileFilter extends javax.swing.filechooser.FileFilter implements java.io.FileFilter {

		public boolean accept(File f) {
			return f.isDirectory() || f.getName().endsWith(".jar");
		}

		public String getDescription() {
			return "JAR Files (*.jar)";
		}
	}

}
