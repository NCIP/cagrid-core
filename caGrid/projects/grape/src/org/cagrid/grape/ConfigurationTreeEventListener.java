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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class ConfigurationTreeEventListener extends MouseAdapter {

	private ConfigurationTree tree;


	public ConfigurationTreeEventListener(ConfigurationTree owningTree) {
		this.tree = owningTree;
	}


	public void mouseEntered(MouseEvent e) {
		process(e);
	}


	public void mouseReleased(MouseEvent e) {
		process(e);
	}


	private void process(MouseEvent e) {

		if ((e.isPopupTrigger()) || (SwingUtilities.isLeftMouseButton(e))) {
			ConfigurationBaseTreeNode node = this.tree.getCurrentNode();
			if (node != null) {
				node.showPanel();
			}
		}
	}
}
