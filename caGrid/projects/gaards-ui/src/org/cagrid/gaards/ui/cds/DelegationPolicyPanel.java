package org.cagrid.gaards.ui.cds;

import javax.swing.JPanel;

import org.cagrid.gaards.cds.common.DelegationPolicy;

public abstract class DelegationPolicyPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private boolean editMode;
	
	public DelegationPolicyPanel(boolean editMode) {
		this.editMode = editMode;
	}
	
	public boolean isEditMode() {
		return editMode;
	}

	public abstract DelegationPolicy getPolicy();
	public abstract void setPolicy(DelegationPolicy policy);
}
