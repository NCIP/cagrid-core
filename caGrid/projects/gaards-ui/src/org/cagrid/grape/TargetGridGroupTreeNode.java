package org.cagrid.grape;

import javax.swing.ImageIcon;

import org.cagrid.grape.configuration.Grid;
import org.cagrid.grape.model.ConfigurationGroup;

public class TargetGridGroupTreeNode extends TargetGridBaseTreeNode {

	private ConfigurationGroup group;

	public TargetGridGroupTreeNode(ConfigurationWindow window,
			ConfigurationTree tree, ConfigurationGroup group, Grid grid) throws Exception {
		super(window, tree, grid);
		this.group = group;
		this.processConfigurationDescriptors(group
				.getConfigurationDescriptors());
		this.setDisplayPanel(new ConfigurationDisplayPanel(group.getName()));
	}

	public ImageIcon getIcon() {
		return LookAndFeel.getConfigurationGroupIcon();
	}


	public String toString() {
		return group.getName();
	}
}
