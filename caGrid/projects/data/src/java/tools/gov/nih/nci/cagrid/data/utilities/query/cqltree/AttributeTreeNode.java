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
package gov.nih.nci.cagrid.data.utilities.query.cqltree;

import gov.nih.nci.cagrid.cqlquery.Attribute;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/** 
 *  AttributeTreeNode
 *  Tree node to represent an attribute in a CQL Query
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Sep 11, 2006 
 * @version $Id$ 
 */
public class AttributeTreeNode extends IconTreeNode {
	private static Icon icon = null;

	private Attribute attribute;
	
	public AttributeTreeNode(Attribute attrib) {
		this.attribute = attrib;
		setAllowsChildren(false);
		setText();
	}
	
	
	public Attribute getAttribute() {
		return this.attribute;
	}
	
	
	public Icon getIcon() {
		if (icon == null) {
			icon = new ImageIcon(getClass().getResource("QueryAttribute.png"));
		}
		return null;
	}
	
	
	public void rebuild() throws IllegalStateException {
		setText();
	}
	
	
	private void setText() {
		setUserObject("Attribute: " + attribute.getName() + " :: " 
			+ attribute.getPredicate().getValue() + " :: " + attribute.getValue());
	}
}
