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
 * $Id: DynamicModel.java,v 1.1 2007-05-17 13:58:49 joshua Exp $
 */

package org.pietschy.wizard.models;

import org.pietschy.wizard.AbstractWizardModel;
import org.pietschy.wizard.WizardStep;
import org.pietschy.wizard.WizardModel;

import java.util.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * The DynamicModel is very similar to the static model, except that steps can be dynamically removed
 * from the wizard flow.
 * <pre>
 * // create a subclass of DynamicModel
 * MyDynamicModel model = new MyDynamicModel();
 *
 * // add the first step..
 * model.add(new MyFirstStep());
 *
 * // add an optional step..
 * model.add(new MyOptionalStep(), new Condition()
 * {
 *    public boolean evaluate(WizardModel model)
 *    {
 *        return ((MyDynamicModel) model).isOptionalRequired();
 *    }
 * });
 *
 * // add the last step.
 * model.add(new MyLastStep());
 *
 * // now create the wizard and use it..
 * Wizard wizard = new Wizard(model);
 * </pre>
 * It is also worth noting that steps that implement {@link Condition} can be added using the basic
 * {@link #add(WizardStep)} and the model will automatically add them as an optional step.
 * <p>
 *
 *
 *
 * @see #add(WizardStep)
 * @see #add(WizardStep, Condition)
 */
public class
DynamicModel
extends AbstractWizardModel
{

   /** An implementation of {@link Condition} that always returns <tt>true</tt>. */
   public static final Condition TRUE_CONDITION = new Condition()
   {
      public boolean evaluate(WizardModel model)
      {
         return true;
      }
   };

   private ArrayList steps = new ArrayList();
   private ArrayList conditions = new ArrayList();

   private Stack history = new Stack();

   /**
    * Creates a new DynamicModel.
    */
   public
   DynamicModel()
   {
   }

   /**
    * Adds the next step to the wizard.  If the {@link WizardStep} implements {@link Condition}, then this method is
    * equivalent to calling {@link #add(WizardStep, Condition) add(step, (Condition)step)}, other wise it is equivalent to
    * calling {@link #add(org.pietschy.wizard.WizardStep, Condition) add(step, TRUE_CONDITION)}.
    * <p>
    * This allows the easy use of {@link WizardStep}s that determine the condition under which they are displayed.
    *
    * @param step the step to added.
    */
   public void
   add(WizardStep step)
   {
      if (step instanceof Condition)
         add(step, (Condition) step);
      else
         add(step, TRUE_CONDITION);
   }

   /**
    * Adds an optional step to the model.  The step will only be displayed if the specified
    * condition is met.
    *
    * @param step      the {@link org.pietschy.wizard.WizardStep} to add.
    * @param condition the {@link Condition} under which it should be included in the wizard.
    */
   public void
   add(WizardStep step, Condition condition)
   {
      addCompleteListener(step);
      steps.add(step);
      conditions.add(condition);
   }

   public void
   nextStep()
   {
      WizardStep currentStep = getActiveStep();
      history.push(currentStep);
      setActiveStep(findNextVisibleStep(currentStep));
   }

   public void
   previousStep()
   {
      WizardStep step = (WizardStep) history.pop();
      setActiveStep(step);
   }

   public void
   lastStep()
   {
      WizardStep activeStep = getActiveStep();
      history.push(activeStep);
      setActiveStep(findLastStep());
   }

   public void
   reset()
   {
      history.clear();
      setActiveStep(findNextVisibleStep(null));
   }

   public boolean
   isLastStep(WizardStep step)
   {
      return findLastStep().equals(step);
   }

   /**
    * Forces the model to re-evaluate it's current state.  This method will re-evalute the
    * conditional steps by calling {@link Condition#evaluate(org.pietschy.wizard.WizardModel)}.
    * <p>
    * Subclasses that override this method must be sure to invoke <tt>super.refreshModelState()</tt>.
    */
   public void
   refreshModelState()
   {
      WizardStep activeStep = getActiveStep();
      setNextAvailable(activeStep != null && activeStep.isComplete() && !isLastStep(activeStep));
      setPreviousAvailable(activeStep != null && !history.isEmpty());
      setLastAvailable(activeStep != null && allStepsComplete() && !isLastStep(activeStep));
      setCancelAvailable(true);
   }

   /**
    * Returns true if all included steps in the wizard return <tt>true</tt> from {@link WizardStep#isComplete}.  This
    * is primarily used to determine if the last button can be enabled.
    * <p>
    * Please note that this method ignores all steps for which their {@link Condition} returns false.
    *
    * @return <tt>true</tt> if all the visible steps in the wizard are complete, <tt>false</tt> otherwise.
    */
   public boolean
   allStepsComplete()
   {
      for (int i = 0; i < steps.size(); i++)
      {
         WizardStep step = (WizardStep) steps.get(i);
         Condition condition = (Condition) conditions.get(i);

         if (condition.evaluate(this))
         {
            if (!step.isComplete())
               return false;
         }

      }

      return true;
   }


   public Iterator
   stepIterator()
   {
      return Collections.unmodifiableList(steps).iterator();
   }


   /**
    * @param currentStep
    * @return
    */
   private WizardStep
   findNextVisibleStep(WizardStep currentStep)
   {
      int startIndex = (currentStep == null) ? 0 : steps.indexOf(currentStep) + 1;

      for (int i = startIndex; i < conditions.size(); i++)
      {
         Condition condition = (Condition) conditions.get(i);
         if (condition.evaluate(this))
         {
            return (WizardStep) steps.get(i);
         }
      }

      throw new IllegalStateException("Wizard contains no more visible steps");
   }

   /**
    * @return
    */
   private WizardStep
   findLastStep()
   {
      for (int i = conditions.size() - 1; i >= 0; i--)
      {
         Condition condition = (Condition) conditions.get(i);
         if (condition.evaluate(this))
         {
            return (WizardStep) steps.get(i);
         }
      }

      throw new IllegalStateException("Wizard contains no visible steps");
   }
}
