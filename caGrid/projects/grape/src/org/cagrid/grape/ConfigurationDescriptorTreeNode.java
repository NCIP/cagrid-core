package org.cagrid.grape;

import java.lang.reflect.Constructor;

import javax.swing.ImageIcon;

import org.apache.log4j.Logger;
import org.cagrid.grape.model.ConfigurationDescriptor;
import org.cagrid.grape.model.ConfigurationEditor;
import org.cagrid.grape.model.ConfigurationEditors;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class ConfigurationDescriptorTreeNode extends ConfigurationBaseTreeNode {

	// private ConfigurationDescriptor des;

	private Logger log;

	private String displayName;
	private String systemName;


	public ConfigurationDescriptorTreeNode(ConfigurationWindow window, ConfigurationTree tree,
		ConfigurationDescriptor des) throws Exception {
		super(window, tree);
		this.displayName = des.getDisplayName();
		this.systemName = des.getSystemName();
		log = Logger.getLogger(this.getClass().getName());
		if (des.getConfigurationPanel() == null) {
			this.setDisplayPanel(new ConfigurationDisplayPanel(des.getDisplayName()));
		} else {
			try {
				Class[] types = new Class[2];
				types[0] = ConfigurationDescriptorTreeNode.class;
				types[1] = Object.class;
				Constructor c = Class.forName(des.getConfigurationPanel()).getConstructor(types);
				Object[] args = new Object[2];
				args[0] = this;
				args[1] = this.getConfigurationManager().getConfigurationObject(des.getSystemName());
				this.setDisplayPanel((ConfigurationBasePanel) c.newInstance(args));
			} catch (Exception e) {
				this.setDisplayPanel(new ConfigurationDisplayPanel(des.getDisplayName()));
				log.error("An error occurred using the panel " + des.getConfigurationPanel()
					+ " for editing the preference " + des.getDisplayName());
				log.error(e.getMessage(), e);
			}
		}
		this.processConfigurationEditors(des.getConfigurationEditors());
		// this.processConfigurationDescriptors(des.getConfigurationDescriptors());
	}


	protected void processConfigurationEditors(ConfigurationEditors list) throws Exception {

		if (list != null) {
			ConfigurationEditor[] des = list.getConfigurationEditor();
			if (des != null) {
				for (int i = 0; i < des.length; i++) {
					this.processConfigurationEditor(des[i]);
				}

			}
		}
	}


	protected void processConfigurationEditor(ConfigurationEditor editor) throws Exception {
		if (editor != null) {
			ConfigurationEditorTreeNode node = new ConfigurationEditorTreeNode(getConfigurationWindow(), getTree(),
				editor, GridApplication.getContext().getConfigurationManager().getConfigurationObject(this.systemName));
			this.add(node);
		}
	}


	public void addConfigurationEditor(ConfigurationEditor editor, Object object) throws Exception {
		ConfigurationEditorTreeNode node = new ConfigurationEditorTreeNode(getConfigurationWindow(), getTree(), editor,
			object);
		this.add(node);
		getTree().reload();
	}


	public ImageIcon getIcon() {
		return LookAndFeel.getConfigurationPropertyIcon();
	}


	public String toString() {
		return this.displayName;
	}

}
