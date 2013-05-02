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

package org.cagrid.gaards.ui.gridgrouper.expressioneditor;

import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */

public abstract class BaseTreeNode extends DefaultMutableTreeNode {
	
	private static final long serialVersionUID = 1L;

	private GridGrouperExpressionEditor editor;


	public BaseTreeNode(GridGrouperExpressionEditor editor) {
		this.editor = editor;
	}


	public GridGrouperExpressionEditor getEditor() {
		return editor;
	}


	public abstract ImageIcon getIcon();


	public abstract String toString();


	public abstract void refresh();

}
