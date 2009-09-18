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
 * $Id: HelpBroker.java,v 1.1 2007-05-17 13:58:50 joshua Exp $
 */
package org.pietschy.wizard;

import javax.swing.*;

/**
 * An interface for handling wizard help requests.  If this interface is implemented by a {@link WizardModel}, the
 * help button will be automatically displayed and help request fowwareded the model.  Otherwise, you'll need to
 * call {@link Wizard#setHelpBroker(HelpBroker)} explicitly with an instance of this interface.
 * @author andrewp
 * @version $Revision: 1.1 $
 */
public interface
HelpBroker
{
   /**
    * Activated when the user presses help.  Implementors will typically query the model to determine the current
    * step and display the appropriate information.
    * @param parent the component that is requesting the help.  This is typically the {@link Wizard} itself.
    * @param model the wizard model.
    */
   public void activateHelp(JComponent parent, WizardModel model);
}
