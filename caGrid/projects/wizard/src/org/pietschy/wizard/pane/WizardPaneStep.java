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
 * $Id: WizardPaneStep.java,v 1.1 2007-05-17 13:58:50 joshua Exp $
 */
package org.pietschy.wizard.pane;

import org.pietschy.wizard.AbstractWizardStep;
import org.pietschy.wizard.InvalidStateException;
import org.pietschy.wizard.WizardModel;

import javax.swing.*;
import java.awt.*;

/**
 * A simple step that can be used to decouple the step from its user interface
 * so that the user interface can be created in a standalone class implementing
 * {@link WizardPane}.<br/>
 * Particularly useful if the user interface is built using a GUI tool.
 *
 * For example:
 *
 * <pre>
 *    StaticModel model = new StaticModel();
 *    model.add(new (new WizardPaneStep(new FirstPane(), "Start", "Description"));
 *    model.add(...);
 *    ...
 *
 *    Wizard wizard = new Wizard(model);
 *    wizard.showInFrame("My Wizard");
 * </pre>
 *
 * @author Andrea Aime
 * @deprecated since 0.1.10.  WizardStep is now an interface allowing implementation by any swing
 * componenent.  See {@link org.pietschy.wizard.PanelWizardStep}.
 */
public class
WizardPaneStep
extends AbstractWizardStep
{
   WizardPane pane;

   /**
    * Creates a new WizardPaneStep
    *
    * @param pane    -
    *                the graphical component. Should be a subclass of Component
    * @param name
    * @param summary
    */
   public WizardPaneStep(WizardPane pane, String name, String summary)
   {
      super(name, summary);

      if (!(pane instanceof Component))
         throw new IllegalArgumentException("The pane should also be a Component subclass");
      this.pane = pane;
   }

   /**
    * Creates a new WizardPaneStep
    *
    * @param pane    -
    *                the graphical component. Should be a subclass of Component
    * @param name
    * @param summary
    * @param icon
    */
   public WizardPaneStep(WizardPane pane, String name, String summary, Icon icon)
   {
      super(name, summary, icon);
      this.pane = pane;
   }

   /**
    * @see org.pietschy.wizard.WizardStep#init(org.pietschy.wizard.WizardModel)
    */
   public void
   init(WizardModel model)
   {
      pane.init(this, model);
   }

   /**
    * @see org.pietschy.wizard.WizardStep#prepare()
    */
   public void
   prepare()
   {
      setView((Component) pane);
      pane.prepare();
   }

   /**
    * @see org.pietschy.wizard.WizardStep#applyState()
    */
   public void
   applyState() throws InvalidStateException
   {
      pane.applyState();
   }

   /**
    * @see org.pietschy.wizard.WizardStep#getPreferredSize()
    */
   public Dimension
   getPreferredSize()
   {
      return ((Component) pane).getPreferredSize();
   }
}


