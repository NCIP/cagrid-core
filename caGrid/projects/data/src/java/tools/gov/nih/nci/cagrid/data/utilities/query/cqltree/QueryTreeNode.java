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

import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;

import javax.swing.Icon;

/** 
 *  QueryTreeNode
 *  CQL Query Node
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Sep 11, 2006 
 * @version $Id$ 
 */
public class QueryTreeNode extends IconTreeNode {
	private static Icon icon = null;

	private CQLQuery query;
	
	public QueryTreeNode(CQLQuery query) {
		this.query = query;
		setUserObject("CQL Query");
		rebuild();
	}
	
	
	public CQLQuery getQuery() {
		return this.query;
	}
	
	
	public Icon getIcon() {
		if (icon == null) {
			icon = PortalLookAndFeel.getQueryIcon();
		}
		return icon;
	}
	
	
	public void rebuild() throws IllegalStateException {
		removeAllChildren();
		if (query.getTarget() != null) {
			TargetTreeNode targetNode = new TargetTreeNode(query.getTarget());
			add(targetNode);
		}
		if (query.getQueryModifier() != null) {
			// TODO: mark the query modifier somehow
		}
	}
}
