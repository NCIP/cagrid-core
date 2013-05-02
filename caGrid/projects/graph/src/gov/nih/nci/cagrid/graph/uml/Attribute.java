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
package gov.nih.nci.cagrid.graph.uml;

public class Attribute {
	public static final int PROTECTED = 0;
	public static final int PUBLIC = 1;
	public static final int PRIVATE = 2;

	protected int access;
	protected String type;
	protected String name;


	public Attribute(int access, String type, String name) {
		this.access = access;
		this.type = type;
		this.name = name;
	}


	public String toString() {
		return " + " + name + ": " + type;
	}
}
