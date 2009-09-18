/**
 * Wizard Framework
 * Copyright 2004 - 2005 Andrew Pietsch
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * $Id: Condition.java,v 1.1 2007-05-17 13:58:49 joshua Exp $
 */
package org.pietschy.wizard.models;

import org.pietschy.wizard.WizardModel;

/**
 * An interface that is used by {@link DynamicModel} and {@link MultiPathModel} to determine
 * if a wizard should display a particular step or group of steps.
 * @version $Revision: 1.1 $
 * @author andrewp
 * @see DynamicModel#add(org.pietschy.wizard.WizardStep, Condition)
 * @see BranchingPath#addBranch(Path, Condition)
 *
 */
public interface
Condition
{
   /**
    * Called to determine when the path being added using {@link BranchingPath#addBranch} should
    * be traversed.  The selector is passed the current {@link WizardModel} to assist with the
    * decision.
    * @param model the {@link WizardModel} the condition should use.
    * @return true if the .
    */
   public boolean evaluate(WizardModel model);
}
