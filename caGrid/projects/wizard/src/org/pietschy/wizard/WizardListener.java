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
 * $Id: WizardListener.java,v 1.1 2007-05-17 13:58:50 joshua Exp $
 */

package org.pietschy.wizard;

import java.util.EventListener;

/**
 * This interface allows other classes to be notified when this wizard is cancelled or closed.
 * @see WizardAdapter
 */
public interface
WizardListener
extends EventListener
{
   /**
    * Called when the user closes the wizard.
    * @param e the wizard event.
    */
   public void wizardClosed(WizardEvent e);

   /**
    * Called when the user cancels the wizard.
    * @param e the wizard event.
    */
   public void wizardCancelled(WizardEvent e);
}
