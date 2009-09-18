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
 * $Id: NextAction.java,v 1.1 2007-05-17 13:58:49 joshua Exp $
 */

package org.pietschy.wizard;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * Created by IntelliJ IDEA.
 * User: andrewp
 * Date: 7/06/2004
 * Time: 16:06:09
 * To change this template use Options | File Templates.
 */
class
NextAction
extends WizardAction
{
   protected NextAction(Wizard model)
   {
      super("next",
            model,
            new ArrowIcon(SwingConstants.EAST)
      );
   }

   public void
   doAction(ActionEvent e)
   throws InvalidStateException
   {
      getModel().getActiveStep().applyState();
      getModel().nextStep();
   }

   protected void
   updateState()
   {
      WizardStep activeStep = getActiveStep();
      boolean busy = activeStep != null && activeStep.isBusy();
      setEnabled(getModel().isNextAvailable() && !busy);
   }
}
