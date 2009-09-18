package gov.nih.nci.cagrid.introduce.portal.updater.steps.updatetree;

import gov.nih.nci.cagrid.introduce.beans.software.IntroduceRevType;
import gov.nih.nci.cagrid.introduce.beans.software.SoftwareType;

import javax.swing.JCheckBox;
import javax.swing.tree.DefaultTreeModel;

public class IntroduceRevUpdateTreeNode extends UpdateTypeTreeNode {

	private SoftwareType software;

	private IntroduceRevType introduce;

	private JCheckBox checkBox;

	private boolean installed = false;

	public IntroduceRevUpdateTreeNode(String displayName, DefaultTreeModel model,
			IntroduceRevType introduce, SoftwareType software) {
		super(displayName, model);
		this.software = software;
		this.introduce = introduce;
		checkBox = new JCheckBox(displayName);
		if (model != null) {
			initialize();
		}
	}

	public boolean isSelected() {
		return getCheckBox().isSelected();
	}

	public JCheckBox getCheckBox() {
		return checkBox;
	}

	public void setInstalled(boolean installed) {
		this.installed = installed;
	}

	public boolean isInstalled() {
		return installed;
	}

	public Object getUserObject() {
		return checkBox;
	}

	public void initialize() {
		
	}

	public IntroduceRevType getIntroduceRev() {
		return introduce;
	}

	public void setIntroduce(IntroduceRevType introduce) {
		this.introduce = introduce;
	}
}
