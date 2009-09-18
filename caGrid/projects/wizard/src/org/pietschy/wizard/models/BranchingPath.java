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
 * $Id: BranchingPath.java,v 1.1 2007-05-17 13:58:49 joshua Exp $
 */

package org.pietschy.wizard.models;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.pietschy.wizard.WizardStep;

/**
 * BranchingPaths represent a sequence of {@link WizardStep}s that has multiple choices for
 * the next path to traverse.
 * @see #addBranch
 * @see #addStep
 */
public class
BranchingPath
extends AbstractPath
{
   private Map paths = new LinkedHashMap();

   /**
    * Creates a new empth BranchingPath.
    */
   public
   BranchingPath()
   {
   }

   /**
    * Creates a new BranchingPath that is initialized with the specified step.
    * @param step the first step of the path.
    */
   public
   BranchingPath(WizardStep step)
   {
      addStep(step);
   }

   /**
    * Gets the path to traverse after this path has exhausted all its steps.  This method will
    * call iterate over each path selector to determine the path to return.
    *
    * @return the next path in the sequence.
    * @throws IllegalStateException if no matching path is found.
    */
   protected Path
   getNextPath(MultiPathModel model)
   {
      for (Iterator iter = paths.entrySet().iterator(); iter.hasNext();)
      {
         Map.Entry entry = (Map.Entry) iter.next();
         Condition condition = (Condition) entry.getKey();
         if (condition.evaluate(model))
            return (Path) entry.getValue();
      }

      throw new IllegalStateException("No next path selected");
   }

   /**
    * Adds a possible branch from this path.
    *
    * @param path the {@link AbstractPath} to traverse based when the condition returns <tt>true</tt>.
    * @param condition a {@link Condition} that activates this path.
    */
   public void
   addBranch(Path path, Condition condition)
   {
      paths.put(condition, path);
   }

   public void acceptVisitor(PathVisitor visitor)
   {
      visitor.visitPath(this);
   }

   public void visitBranches(PathVisitor visitor)
   {
      for (Iterator iter = paths.values().iterator(); iter.hasNext();)
      {
         Path path = (Path) iter.next();
         path.acceptVisitor(visitor);
      }
   }

}
