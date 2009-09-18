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
 * $Id: WizardAdapter.java,v 1.1 2007-05-17 13:58:50 joshua Exp $
 */

package org.pietschy.wizard;

/**
 * This class provides an empty implementation of {@link WizardListener}.
 */
public abstract class
WizardAdapter
implements WizardListener
{
   /**
    * Empty implementation of {@link WizardListener#wizardClosed}
    * @param e the wizard event
    */
   public void wizardClosed(WizardEvent e)
   {
   }

   /**
    * Empty implementation of {@link WizardListener#wizardCancelled}
    * @param e the wizard event
    */
   public void wizardCancelled(WizardEvent e)
   {
   }
}
