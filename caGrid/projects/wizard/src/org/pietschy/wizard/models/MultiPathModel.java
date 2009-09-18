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
 * $Id: MultiPathModel.java,v 1.1 2007-05-17 13:58:49 joshua Exp $
 */

package org.pietschy.wizard.models;

import org.pietschy.wizard.AbstractWizardModel;
import org.pietschy.wizard.WizardStep;

import java.util.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * MultiPathModels are built from a joined set of {@link AbstractPath Paths} that each contain one or more
 * {@link WizardStep WizardSteps}.  Two types of {@link AbstractPath} are available, {@link SimplePath} and
 * {@link BranchingPath}.  The paths must be fully constructed before the model is instantiated.
 * <pre>
 * // Construct each of the paths involved in the wizard.
 *BranchingPath firstPath = new BranchingPath();
 *SimplePath optionalPath = new SimplePath();
 *SimplePath lastPath = new SimplePath();
 *
 *firstPath.addStep(stepOne);
 *firstPath.addStep(stepTwo);
 *
 *optionalPath.addStep(optionalStepOne);
 *optionalPath.addStep(optionalStepTwo);
 *optionalPath.addStep(optionalStepThree);
 *
 *lastPath.addStep(lastStep);
 *
 * // Now bind all the paths together, first the branching path then the optional path.
 *
 * // add the optional path and the condition that determines when it should be followed
 *firstPath.addBranch(optionalPath, new Condition() {
 *   public boolean evaluate(WizardModel model) {
 *      return ((MyModel)model).includeOptional();
 *    }
 *});
 *
 * // add the end path and the condition that determines when it should be followed
 *firstPath.addBranch(lastPath, new Condition() {
 *   public boolean evaluate(WizardModel model) {
 *      return !((MyModel)model).includeOptional();
 *    }
 *});
 *
 * // the optional path proceeds directly to the lastPath
 *optionalPath.setNextPath(lastPath);
 *
 * // Now create the model and wizard.
 *MultiPathModel model = new MultiPathModel(firstPath);
 *Wizard wizard = new Wizard(model);
 * </pre>
 *
 * During the initialization the wizard will scan all the paths to determine the ending path.  The
 * end path is an instance of {@link SimplePath} that is reachable from the
 * {@link #getFirstPath firstPath} and for whom {@link SimplePath#getNextPath} returns null.  If no
 * matching path is found or more than one is found the model will throw an exception.
 */
public class
MultiPathModel
extends AbstractWizardModel
{
   private Path firstPath;
   private Path lastPath;
   private Map pathMapping;

   private Stack history = new Stack();



   /**
    * Creates a new MultiPathModel. The paths must be full constructed and
    * linked before the this constructor is called.<p>
    *  During the initialization the wizard will scan all the paths to determine the ending path.  The
    * end path is an instance of {@link SimplePath} that is reachable from the
    * {@link #getFirstPath firstPath} and for whom {@link SimplePath#getNextPath} returns null.  If no
    * matching path is found or more than one is found the model will throw an exception.
    * @param firstPath the starting path of the model.  The paths must be populated with their
    * {@link WizardStep steps} and be linked before the this constructor is called.
    */
   public
   MultiPathModel(Path firstPath)
   {
      this.firstPath = firstPath;

      PathMapVisitor visitor = new PathMapVisitor();
      firstPath.acceptVisitor(visitor);
      pathMapping = visitor.getMap();

      LastPathVisitor v = new LastPathVisitor();
      firstPath.acceptVisitor(v);
      lastPath = v.getPath();

      if (lastPath == null)
         throw new IllegalStateException("Unable to locate last path");

      for (Iterator iter = pathMapping.keySet().iterator(); iter.hasNext();)
      {
         addCompleteListener((WizardStep) iter.next());
      }
   }

   public Path
   getFirstPath()
   {
      return firstPath;
   }

   public Path
   getLastPath()
   {
      return lastPath;
   }

   public void
   nextStep()
   {
      WizardStep currentStep = getActiveStep();
      AbstractPath currentPath = getPathForStep(currentStep);

      if (currentPath.isLastStep(currentStep))
      {
         Path nextPath = currentPath.getNextPath(this);
         setActiveStep(nextPath.firstStep());
      }
      else
      {
         setActiveStep(currentPath.nextStep(currentStep));
      }

      history.push(currentStep);
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
      history.push(getActiveStep());
      WizardStep lastStep = getLastPath().lastStep();
      setActiveStep(lastStep);
   }

   public void
   reset()
   {
      history.clear();
      WizardStep firstStep = firstPath.firstStep();
      setActiveStep(firstStep);
      history.push(firstStep);
   }

   public boolean
   isLastStep(WizardStep step)
   {
      Path path = getPathForStep(step);
      return path.equals(getLastPath()) && path.isLastStep(step);
   }

   public void
   refreshModelState()
   {
      WizardStep activeStep = getActiveStep();
      Path activePath = getPathForStep(activeStep);

      setNextAvailable(activeStep.isComplete() && !isLastStep(activeStep));
      setPreviousAvailable(!(activePath.equals(firstPath) && activePath.isFirstStep(activeStep)));
      setLastAvailable(allStepsComplete() && !isLastStep(activeStep));
      setCancelAvailable(true);
   }

   /**
    * Returns true if all the steps in the wizard return <tt>true</tt> from
    * {@link WizardStep#isComplete}.  This is primarily used to determine if the last button
    * can be enabled.
    * @return <tt>true</tt> if all the steps in the wizard are complete, <tt>false</tt> otherwise.
    */
   public boolean
   allStepsComplete()
   {
      for (Iterator iterator = stepIterator(); iterator.hasNext();)
      {
         if (!((WizardStep) iterator.next()).isComplete())
            return false;
      }

      return true;
   }


   public Iterator
   stepIterator()
   {
      return pathMapping.keySet().iterator();
   }

   protected AbstractPath
   getPathForStep(WizardStep step)
   {
      return (AbstractPath) pathMapping.get(step);
   }


   private class LastPathVisitor
   extends AbstractPathVisitor
   {
      private Path last;

      public void visitPath(SimplePath p)
      {
         if (enter(p))
         {
            if (p.getNextPath() == null)
            {
               if (this.last != null)
                  throw new IllegalStateException("Two paths have empty values for nextPath");

               this.last = p;
            }
            else
            {
               p.visitNextPath(this);
            }
         }
      }

      public void visitPath(BranchingPath path)
      {
         if (enter(path))
            path.visitBranches(this);
      }

      public Path
      getPath()
      {
         return last;
      }
   }

   private class
   PathMapVisitor
   extends AbstractPathVisitor
   {
      private HashMap map = new HashMap();

      public PathMapVisitor()
      {
      }

      public void
      visitPath(SimplePath path)
      {
         if (enter(path))
         {
            populateMap(path);
            path.visitNextPath(this);
         }
      }

      public void
      visitPath(BranchingPath path)
      {
         if (enter(path))
         {
            populateMap(path);
            path.visitBranches(this);
         }
      }

      private void
      populateMap(Path path)
      {
         for (Iterator iter = path.getSteps().iterator(); iter.hasNext();)
         {
            WizardStep step = (WizardStep) iter.next();
            map.put(step, path);
         }
      }


      public Map
      getMap()
      {
         return map;
      }
   }

}
