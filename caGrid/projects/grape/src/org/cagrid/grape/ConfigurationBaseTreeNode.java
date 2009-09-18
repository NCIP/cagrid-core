package org.cagrid.grape;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;

import org.cagrid.grape.model.ConfigurationDescriptor;
import org.cagrid.grape.model.ConfigurationDescriptors;
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

abstract class ConfigurationBaseTreeNode extends DefaultMutableTreeNode {

	private ConfigurationTree tree;

	private JPanel displayPanel;

	private ConfigurationWindow configurationWindow;


	public ConfigurationBaseTreeNode(ConfigurationWindow window, ConfigurationTree tree) {
		this.tree = tree;
		this.configurationWindow = window;
	}


	public ConfigurationTree getTree() {
		return tree;
	}


	public ConfigurationWindow getConfigurationWindow() {
		return configurationWindow;
	}


	public JPanel getDisplayPanel() {
		return displayPanel;
	}


	public void setDisplayPanel(JPanel displayPanel) {
		this.displayPanel = displayPanel;
	}


	public void showPanel() {
		if (getDisplayPanel() != null) {
			getConfigurationWindow().showDisplayPanel(getIdentifier());
		}
	}


	public void addToDisplay() {
		if (getDisplayPanel() != null) {
			getConfigurationWindow().addDisplayPanel(getIdentifier(), getDisplayPanel());
			for (int i = 0; i < this.getChildCount(); i++) {
				ConfigurationBaseTreeNode node = (ConfigurationBaseTreeNode) this.getChildAt(i);
				node.addToDisplay();
			}

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


	protected void processConfigurationDescriptors(ConfigurationDescriptors list) throws Exception {

		if (list != null) {
			ConfigurationDescriptor[] des = list.getConfigurationDescriptor();
			if (des != null) {
				for (int i = 0; i < des.length; i++) {
					this.processConfigurationDescriptor(des[i]);
				}

			}
		}
	}


	protected void processConfigurationGroup(ConfigurationGroup des) throws Exception {
		if (des != null) {
			ConfigurationGroupTreeNode node = new ConfigurationGroupTreeNode(getConfigurationWindow(), getTree(), des);
			this.add(node);
		}
	}


	protected void processConfigurationDescriptor(ConfigurationDescriptor des) throws Exception {
		if (des != null) {
			ConfigurationDescriptorTreeNode node = new ConfigurationDescriptorTreeNode(getConfigurationWindow(),
				getTree(), des);
			this.add(node);
		}
	}


	public ConfigurationManager getConfigurationManager() {
		return GridApplication.getContext().getConfigurationManager();
	}


	public abstract ImageIcon getIcon();


	public abstract String toString();


	public String getIdentifier() {
		ConfigurationBaseTreeNode node = (ConfigurationBaseTreeNode) this.getParent();
		if (node == null) {
			return "Preferences";
		} else {
			return node.getIdentifier() + ":" + toString();
		}
	}

}
