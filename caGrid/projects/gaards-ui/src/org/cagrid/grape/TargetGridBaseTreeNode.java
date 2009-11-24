package org.cagrid.grape;

import javax.swing.ImageIcon;

import org.cagrid.grape.configuration.Grid;
import org.cagrid.grape.model.ConfigurationDescriptor;
import org.cagrid.grape.model.ConfigurationDescriptors;
import org.cagrid.grape.model.ConfigurationGroup;

class TargetGridBaseTreeNode extends ConfigurationBaseTreeNode {

	private Grid grid = null;

	public TargetGridBaseTreeNode(ConfigurationWindow window,
			ConfigurationTree tree, Grid grid) {
		super(window, tree);
		this.grid = grid;
		this.setDisplayPanel(new TargetGridDisplayPanel(window, grid));
	}

	public void showPanel() {
		if (getDisplayPanel() != null) {
			getConfigurationWindow().showDisplayPanel(getIdentifier());
		}
	}

	public void addToDisplay() {
		if (getDisplayPanel() != null) {
			getConfigurationWindow().addDisplayPanel(getIdentifier(),
					getDisplayPanel());
			for (int i = 0; i < this.getChildCount(); i++) {
				ConfigurationBaseTreeNode node = (ConfigurationBaseTreeNode) this
						.getChildAt(i);
				node.addToDisplay();
			}
		}
	}

	protected void processConfigurationGroups() throws Exception {
		ConfigurationGroup[] group = GAARDSApplication.getContext()
				.getConfigurationManager().getConfiguration(grid.getSystemName())
				.getConfigurationGroups().getConfigurationGroup();
		if (group != null) {
			for (int i = 0; i < group.length; i++) {
				this.processConfigurationGroup(group[i]);
			}
		}
	}

	protected void processConfigurationDescriptors(ConfigurationDescriptors list)
			throws Exception {

		if (list != null) {
			ConfigurationDescriptor[] des = list.getConfigurationDescriptor();
			if (des != null) {
				for (int i = 0; i < des.length; i++) {
					this.processConfigurationDescriptor(des[i]);
				}
			}
		}
	}

	protected void processConfigurationGroup(ConfigurationGroup des)
			throws Exception {
		if (des != null) {
			TargetGridGroupTreeNode node = new TargetGridGroupTreeNode(
					getConfigurationWindow(), getTree(), des, grid);
			this.add(node);
		}
	}

	protected void processConfigurationDescriptor(ConfigurationDescriptor des)
			throws Exception {
		if (des != null) {
			TargetGridDescriptorTreeNode node = new TargetGridDescriptorTreeNode(
					getConfigurationWindow(), getTree(), des, grid.getSystemName());
			this.add(node);
		}
	}

	public String toString() {
		if (GAARDSApplication.getTargetGrid().equals(grid.getSystemName())) {
			return grid.getDisplayName() + " (Active)";
		} else {
			return grid.getDisplayName();
		}
	}

	public ImageIcon getIcon() {
		if (GAARDSApplication.getTargetGrid().equals(grid.getSystemName())) {
			return LookAndFeel.getActiveGridIcon();
		} else {
			return LookAndFeel.getConfigurationGroupIcon();
		}
	}

	public String getIdentifier() {
		ConfigurationBaseTreeNode node = (ConfigurationBaseTreeNode) this
				.getParent();
		if (node == null) {
			return "Base";
		} else {
			return node.getIdentifier() + ":" + toString();
		}
	}

}
