package org.cagrid.common.wizard;

import javax.swing.JComponent;

import org.pietschy.wizard.OverviewProvider;
import org.pietschy.wizard.models.MultiPathModel;
import org.pietschy.wizard.models.Path;

public class CaGridWizardModel extends MultiPathModel implements OverviewProvider {
    JComponent overviewComponent = null;
	public CaGridWizardModel(Path arg0, JComponent overviewComponent) {
		super(arg0);
		this.overviewComponent = overviewComponent;
	}

	public JComponent getOverviewComponent() {
		return overviewComponent;
	}
	

}
