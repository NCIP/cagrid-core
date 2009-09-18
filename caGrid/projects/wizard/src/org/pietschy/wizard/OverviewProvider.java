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
 * $Id: OverviewProvider.java,v 1.1 2007-05-17 13:58:50 joshua Exp $
 */

package org.pietschy.wizard;

import javax.swing.*;

/**
 * An interface that marks a model as providing an overview component for the wizard.
 * <p>
 * If the models implements this interface, the overview will be displayed in the left pane of
 * the wizard.  The overview can be hidden by calling {@link Wizard#setOverviewVisible} with a
 * value of <tt>false</tt>.
 */
public interface
OverviewProvider
{
   /**
    * Called to get a component that provides an overview of the wizard.  This component will be
    * shown in the wizard on the side corresponding the {@link java.awt.BorderLayout#LINE_START}.
    * @return a componet that provides an overview of the wizard progress.
    */
   public JComponent
   getOverviewComponent();
}
