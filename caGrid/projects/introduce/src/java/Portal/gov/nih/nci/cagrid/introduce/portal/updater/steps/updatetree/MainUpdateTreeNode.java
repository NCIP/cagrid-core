package gov.nih.nci.cagrid.introduce.portal.updater.steps.updatetree;

import gov.nih.nci.cagrid.introduce.beans.software.IntroduceType;
import gov.nih.nci.cagrid.introduce.beans.software.SoftwareType;
import gov.nih.nci.cagrid.introduce.common.IntroducePropertiesManager;
import gov.nih.nci.cagrid.introduce.portal.updater.common.SoftwareUpdateTools;

import java.awt.Font;

import javax.swing.tree.DefaultTreeModel;

public class MainUpdateTreeNode extends UpdateTypeTreeNode {

	private SoftwareType software = null;

	public MainUpdateTreeNode(String displayName, DefaultTreeModel model,
			SoftwareType software) {
		super(displayName, model);
		this.software = software;
		if (model != null) {
			initialize();
		}
	}

	public void initialize() {
		if (software != null) {
			IntroduceType[] introduceVersions = software.getIntroduce();
			if (introduceVersions != null) {
				for (int i = 0; i < introduceVersions.length; i++) {
					IntroduceType introduce = introduceVersions[i];
					if (!SoftwareUpdateTools.isOlderVersion(IntroducePropertiesManager.getIntroduceVersion(),
							introduce.getVersion())
							&& !IntroducePropertiesManager.getIntroduceVersion().equals(
									introduce.getVersion())) {
						// need to add this introduce version
						IntroduceUpdateTreeNode introduceNode = new IntroduceUpdateTreeNode(
								"Introduce (" + introduce.getVersion() + ")",
								this.getModel(), introduce, software);
						introduceNode.setInstalled(false);
						getModel().insertNodeInto(introduceNode, this,
								this.getChildCount());

					} else if (IntroducePropertiesManager.getIntroduceVersion().equals(
							introduce.getVersion())) {
						// need to add this introduce version
						IntroduceUpdateTreeNode introduceNode = new IntroduceUpdateTreeNode(
								"Introduce (" + introduce.getVersion() + ")",
								this.getModel(), introduce, software);
						introduceNode.getCheckBox().setEnabled(false);
						introduceNode.getCheckBox().setSelected(false);
						introduceNode.setInstalled(true);
						introduceNode.getCheckBox().setText(
								introduceNode.getCheckBox().getText()
										+ " installed");
						introduceNode.getCheckBox().setFont(
								introduceNode.getCheckBox().getFont()
										.deriveFont(Font.ITALIC));
						getModel().insertNodeInto(introduceNode, this,
								this.getChildCount());

					}
				}
			}
		}
	}

}
