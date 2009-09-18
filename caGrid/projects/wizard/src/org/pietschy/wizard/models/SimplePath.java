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
 * $Id: SimplePath.java,v 1.1 2007-05-17 13:58:49 joshua Exp $
 */

package org.pietschy.wizard.models;

import org.pietschy.wizard.WizardStep;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A SimplePath represents a sequence of {@link WizardStep wizard steps} whose next path is determined
 * at compile time.  That is, SimplePaths can't branch.
 * @see #setNextPath
 * @see #addStep
 */
public class
SimplePath
extends AbstractPath
{
   private Path nextPath;

   public
   SimplePath()
   {
   }

   /**
    * Creates a new SimplePath that is initialized with the specified step.
    * @param step the first step of the path.
    */
   public
   SimplePath(WizardStep step)
   {
      addStep(step);
   }

   protected Path
   getNextPath(MultiPathModel model)
   {
      return nextPath;
   }

   public Path
   getNextPath()
   {
      return nextPath;
   }

   public void
   setNextPath(Path nextPath)
   {
      this.nextPath = nextPath;
   }

   public void
   acceptVisitor(PathVisitor visitor)
   {
      visitor.visitPath(this);
   }

   public void
   visitNextPath(PathVisitor visitor)
   {
      if (nextPath != null)
         nextPath.acceptVisitor(visitor);
   }


}
