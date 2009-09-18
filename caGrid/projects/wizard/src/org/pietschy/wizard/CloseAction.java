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
 * $Id: CloseAction.java,v 1.1 2007-05-17 13:58:50 joshua Exp $
 */

package org.pietschy.wizard;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: andrewp
 * Date: 10/06/2004
 * Time: 19:55:40
 * To change this template use Options | File Templates.
 */
class CloseAction
extends AbstractAction
{
   private Wizard wizard;

   protected CloseAction(Wizard wizard)
   {
      super(I18n.getString("close.text"));
      this.wizard = wizard;
      putValue(Action.MNEMONIC_KEY, new Integer(I18n.getMnemonic("close.mnemonic")));
   }

   public void
   actionPerformed(ActionEvent e)
   {
      wizard.close();
   }
}
