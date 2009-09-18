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
 * $Id: WizardStep.java,v 1.1 2007-05-17 13:58:50 joshua Exp $
 */
package org.pietschy.wizard;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;

/**
 * All changes to properties must fire property change events.
 *
 * @author andrewp
 * @version $Revision: 1.1 $
 */
public interface
WizardStep
{
   // Constants and variables
   // -------------------------------------------------------------------------
   static final String _ID_ = "$Id: WizardStep.java,v 1.1 2007-05-17 13:58:50 joshua Exp $";

   /**
    * Gets the name of this step. This will be displayed in the title of the wizard while this
    * step is active.
    *
    * @return the name of this step.
    */
   public String
   getName();

   /**
    * Gets the summary of this step. This will be displayed in the title of the wizard while this
    * step is active.  The summary is typically an overview of the step or some usage guidelines
    * for the user.
    *
    * @return the summary of this step.
    */
   public String
   getSummary();

   /**
    * Gets the {@link javax.swing.Icon} that represents this step.
    *
    * @return the {@link javax.swing.Icon} that represents this step, or <tt>null</tt> if the step
    *         doesn't have an icon.
    */
   public Icon
   getIcon();

   /**
    * Returns the current view this step is displaying.  This component will be displayed in the main
    * section of the wizard with this step is active.  This may changed at any time by as long as
    * an appropriate property change event is fired.
    *
    * @return the current view of the step.
    */
   public Component
   getView();

   /**
    * Checks if this step is compete.  This method should return true if the wizard can proceed
    * to the next step.
    *
    * @return <tt>true</tt> if the wizard can proceed from this step, <tt>false</tt> otherwise.
    */
   boolean
   isComplete();

   /**
    * Checks if the current task is busy.  This usually indicates that the step is performing
    * a time consuming task on a background thread.
    *
    * @return <tt>true</tt> if step is busy performing a background operation, <tt>false</tt>
    *         otherwise.
    * @see #abortBusy()
    */
   boolean
   isBusy();

   /**
    * Called to initialize the step.  This method will be called when the wizard is
    * first initialising.
    *
    * @param model the model to which the step belongs.
    */
   void
   init(WizardModel model);

   /**
    * Called to prepare this step to display.  Subclasses should query the model and configure
    * their view appropriately.
    * <p>
    * This method will be called whenever the step is to be displayed, regardless of whether the
    * user pressed next or previous.
    */
   void
   prepare();

   /**
    * This method is called whenever the user presses next while this step is active.
    * <p>
    * If this method will take a long time to complete, subclasses should consider executing the
    * work and a separate thread and displaying some kind of progress indicator.
    * <p>
    * This method will only be called if {@link WizardModel#isNextAvailable} and {@link #isComplete}
    * return true.
    *
    * @throws InvalidStateException if an error occurs and the wizard can't progress to the next
    *                               step.  By default the message of this exception will be displayed to the user.  If you wish to
    *                               prevent this behaviour please ensure {@link InvalidStateException#setShowUser} is called with
    *                               a value of <tt>false</tt>.
    */
   void
   applyState()
   throws InvalidStateException;

   /**
    * Called by the wizard if the user presses cancel while the step is in a {@link #isBusy busy}
    * state.  This method will be called after the user has confirmed the abort process and as
    * such may be invoked after the step is no longer busy.
    */
   void
   abortBusy();

   /**
    * This method must return the maximum preferred size of this wizard step.  This method will
    * be called during wizard initialization to determine the correct size of the wizard.
    * This method will be called after {@link #init}.
    *
    * @return the preferred size of this step.
    */
   Dimension
   getPreferredSize();


   void
   addPropertyChangeListener(PropertyChangeListener listener);

   void
   removePropertyChangeListener(PropertyChangeListener listener);

   void
   addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

   void
   removePropertyChangeListener(String propertyName, PropertyChangeListener listener);

}
