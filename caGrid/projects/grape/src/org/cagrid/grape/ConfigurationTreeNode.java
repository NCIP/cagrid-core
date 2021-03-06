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

import javax.swing.ImageIcon;

import org.cagrid.grape.model.Configuration;
import org.cagrid.grape.model.ConfigurationGroup;
import org.cagrid.grape.model.ConfigurationGroups;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class ConfigurationTreeNode extends ConfigurationBaseTreeNode {

	public ConfigurationTreeNode(ConfigurationWindow window, ConfigurationTree tree) throws Exception {
		super(window, tree);
		processConfiguration(GridApplication.getContext().getConfigurationManager().getConfiguration("default"));
		this.setDisplayPanel(new ConfigurationDisplayPanel("Preferences"));
	}


	private void processConfiguration(Configuration c) throws Exception {
		if (c != null) {
			this.processConfigurationGroups(c.getConfigurationGroups());
			this.processConfigurationDescriptors(c.getConfigurationDescriptors());
		}

	}


	protected void processConfigurationGroups(ConfigurationGroups list) throws Exception {
		if (list != null) {
			ConfigurationGroup[] group = list.getConfigurationGroup();
			if (group != null) {
				for (int i = 0; i < group.length; i++) {
					this.processConfigurationGroup(group[i]);
				}
			}
		}
	}


	public ImageIcon getIcon() {
		return LookAndFeel.getPreferencesTreeIcon();
	}


	public String toString() {
		return "Preferences";
	}
}
