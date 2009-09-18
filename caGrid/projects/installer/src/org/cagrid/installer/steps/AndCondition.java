/**
 * 
 */
package org.cagrid.installer.steps;

import org.pietschy.wizard.WizardModel;
import org.pietschy.wizard.models.Condition;

/**
 * @author <a href="mailto:joshua.phillips@semanticbits.com">Joshua Phillips</a>
 *
 */
public class AndCondition implements Condition {
	

	private Condition[] conditions;
	
	public AndCondition(Condition ... conditions){
		this.conditions = conditions;
	}
	
	/* (non-Javadoc)
	 * @see org.pietschy.wizard.models.Condition#evaluate(org.pietschy.wizard.WizardModel)
	 */
	public boolean evaluate(WizardModel model) {
		boolean b = true;
		for(Condition c : this.conditions){
			if(!c.evaluate(model)){
				b = false;
				break;
			}
		}
		return b;
	}

}
