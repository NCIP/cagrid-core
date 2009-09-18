package org.cagrid.grape;

import javax.swing.ImageIcon;

import org.cagrid.grape.model.ConfigurationGroup;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class ConfigurationGroupTreeNode extends ConfigurationBaseTreeNode {

	private ConfigurationGroup group;


	public ConfigurationGroupTreeNode(ConfigurationWindow window, ConfigurationTree tree, ConfigurationGroup group)
		throws Exception {
		super(window, tree);
		this.group = group;
		this.processConfigurationDescriptors(group.getConfigurationDescriptors());
		this.setDisplayPanel(new ConfigurationDisplayPanel(group.getName()));
	}


	public ImageIcon getIcon() {
		return LookAndFeel.getConfigurationGroupIcon();
	}


	public String toString() {
		return group.getName();
	}
}
