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
 * $Id: AbstractPath.java,v 1.1 2007-05-17 13:58:49 joshua Exp $
 */

package org.pietschy.wizard.models;

import org.pietschy.wizard.WizardStep;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Paths represent a sequence of {@link WizardStep}s.
 */
public abstract class
AbstractPath implements Path
{
   private ArrayList steps = new ArrayList();

   protected
   AbstractPath()
   {
   }

   /**
    * Gets the path that will follow this one.
    * @return the next path.
    */
   protected abstract Path
   getNextPath(MultiPathModel model);

   /* (non-Javadoc)
 * @see org.pietschy.wizard.models.Path#addStep(org.pietschy.wizard.WizardStep)
 */
   public void
   addStep(WizardStep step)
   {
      steps.add(step);
   }

   /* (non-Javadoc)
 * @see org.pietschy.wizard.models.Path#firstStep()
 */
public WizardStep
   firstStep()
   {
      return (WizardStep) steps.get(0);
   }

   /* (non-Javadoc)
 * @see org.pietschy.wizard.models.Path#nextStep(org.pietschy.wizard.WizardStep)
 */
public WizardStep
   nextStep(WizardStep currentStep)
   {
      int index = steps.indexOf(currentStep);
      return (WizardStep) steps.get(index+1);
   }

   /* (non-Javadoc)
 * @see org.pietschy.wizard.models.Path#previousStep(org.pietschy.wizard.WizardStep)
 */
public WizardStep
   previousStep(WizardStep currentStep)
   {
      int index = steps.indexOf(currentStep);
      return (WizardStep) steps.get(index-1);
   }

   /* (non-Javadoc)
 * @see org.pietschy.wizard.models.Path#lastStep()
 */
public WizardStep
   lastStep()
   {
      return (WizardStep) steps.get(steps.size() - 1);
   }

   /* (non-Javadoc)
 * @see org.pietschy.wizard.models.Path#isFirstStep(org.pietschy.wizard.WizardStep)
 */
   public boolean
   isFirstStep(WizardStep step)
   {
      return steps.indexOf(step) == 0;
   }

   /* (non-Javadoc)
 * @see org.pietschy.wizard.models.Path#isLastStep(org.pietschy.wizard.WizardStep)
 */
   public boolean
   isLastStep(WizardStep step)
   {
      boolean lastStep = steps.lastIndexOf(step) == steps.size() - 1;
      return lastStep;
   }

   /* (non-Javadoc)
 * @see org.pietschy.wizard.models.Path#getSteps()
 */
public ArrayList
   getSteps()
   {
      return steps;
   }

   /* (non-Javadoc)
 * @see org.pietschy.wizard.models.Path#contains(org.pietschy.wizard.WizardStep)
 */
public boolean
   contains(WizardStep step)
   {
      return steps.contains(step);
   }

   /* (non-Javadoc)
 * @see org.pietschy.wizard.models.Path#acceptVisitor(org.pietschy.wizard.models.PathVisitor)
 */
public abstract void
   acceptVisitor(PathVisitor visitor);
}
