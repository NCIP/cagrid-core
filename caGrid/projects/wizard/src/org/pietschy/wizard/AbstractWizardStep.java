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
 * $Id: AbstractWizardStep.java,v 1.1 2007-05-17 13:58:50 joshua Exp $
 */

package org.pietschy.wizard;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * This is the base class for all non panel related wizard steps.  Subclasses must implement the
 * abstract methods {@link #init}, {@link #prepare}, {@link #applyState} and
 * {@link #getPreferredSize}.  In addition an appropriate UI must be installed by
 * calling {@link #setView}.
 * <p>
 * The {@link Wizard} listens to property change events from the step and will update
 * accordingly when ever {@link #setView}, {@link #setComplete} or {@link #setBusy} is called.
 * <p>
 * An example is shown below.
 * <pre>
 *    public class MyWizardStep
 *    extends WizardStep
 *    {
 *       private MyModel model;
 *       private JPanel mainView;
 *       private JCheckBox agreeCheckbox;
 *       private JTextArea license;
 *
 *       public MyWizardStep()
 *       {
 *          super("My First Step", "A summary of the first step");
 *
 *          // build and layout the components..
 *          mainView = new JPanel();
 *          agreeCheckbox = new JCheckBox("Agree");
 *          license = new JTextArea();
 *          mainView.setLayout(...);
 *          mainView.add(agreeCheckbox);
 *          ...
 *
 *          // listen to changes in the state..
 *          agreeCheckbox.addItemListener(new ItemListener()
 *          {
 *             public void itemSelected(ItemEvent e)
 *             {
 *                // only continue if they agree
 *                MyWizardStep.this.<b>setComplete(agreeCheckbox.isSelected());</b>
 *             }
 *          });
 *       }
 *
 *       public void init(WizardModel model)
 *       {
 *          this.model = (MyModel) model;
 *       }
 *
 *       public void prepare()
 *       {
 *          // load our view...
 *          <b>setView(mainView);</b>
 *       }
 *
 *       public void applyState()
 *       throws InvalidStateException
 *       {
 *          // display a progress bar of some kind..
 *          <b>setView(myProgressView);</b>
 *
 *          <b>setBusy(true);</b>
 *          try
 *          {
 *             // do some work on another thread.. see <a href="http://foxtrot.sourceforge.net/">Foxtrot</a>
 *             ...
 *          }
 *          finally
 *          {
 *             <b>setBusy(false);</b>
 *          }
 *
 *          // if error then throw an exception
 *          if (!ok)
 *          {
 *             // restore our original view..
 *             <b>setView(mainView)</b>
 *             throw new InvalidStateException("That didn't work!");
 *          }
 *
 *          // this isn't really meaningful as we refuse to continue
 *          // while the checkbox is un-checked.
 *          model.setAcceptsLicense(agreeCheckbox.isSelected());
 *       }
 *
 *       public void getPreferredSize()
 *       {
 *          // use the size of our main view...
 *          return mainView.getPreferredSize();
 *       }
 *    }
 * </pre>
 */
public abstract class
AbstractWizardStep
implements WizardStep
{
   private PropertyChangeSupport pcs;

   /**
    * The name of this step.
    */
   private String name;

   /**
    * A summary of this step, or some usage advice.
    */
   private String summary;

   /**
    * An Icon that represents this step.
    */
   private Icon icon;

   /**
    * The current view of the step.  This will be displayed in the main area of the wizard.  This
    * property is bound.
    */
   private Component view;

   /**
    * Marks this step as being fully configured.  Only when this is <tt>true</tt> can the wizard
    * progress.  This is a bound property.
    */
   private boolean complete;

   /**
    * Marks the task as being busy.  While in this state the wizard will prevent cancel opertations.
    */
   private boolean busy = false;

   /**
    * Creates a new step with the specified name and summary.  The name and summary are displayed in
    * the wizard title block while this step is active.
    *
    * @param name    the name of this step.
    * @param summary a brief summary of this step or some usage guidelines.
    */
   public AbstractWizardStep(String name, String summary)
   {
      this(name, summary, null);
   }

   /**
    * Creates a new step with the specified name and summary.  The name and summary are displayed in
    * the wizard title block while this step is active.
    *
    * @param name    the name of this step.
    * @param summary a brief summary of this step or some usage guidelines.
    */
   public AbstractWizardStep(String name, String summary, Icon icon)
   {
      this.pcs = new PropertyChangeSupport(this);
      this.name = name;
      this.summary = summary;
      this.icon = icon;
   }

   /**
    * Gets the name of this step. This will be displayed in the title of the wizard while this
    * step is active.
    *
    * @return the name of this step.
    */
   public String
   getName()
   {
      return name;
   }

   /**
    * Sets the name of this step. This will be displayed in the title of the wizard while this
    * step is active.
    *
    * @param name the name of this step.
    */
   public void
   setName(String name)
   {
      if ((this.name != null && !this.name.equals(name)) || this.name == null && name != null)
      {
         String old = this.name;
         this.name = name;
         pcs.firePropertyChange("name", old, name);
      }
   }

   /**
    * Gets the summary of this step. This will be displayed in the title of the wizard while this
    * step is active.  The summary is typically an overview of the step or some usage guidelines
    * for the user.
    *
    * @return the summary of this step.
    */
   public String
   getSummary()
   {
      return summary;
   }

   /**
    * Sets the summary of this step. This will be displayed in the title of the wizard while this
    * step is active.  The summary is typically an overview of the step or some usage guidelines
    * for the user.
    *
    * @param summary the summary of this step.
    */
   public void
   setSummary(String summary)
   {
      if ((this.summary != null && !this.summary.equals(summary)) ||
      this.summary == null && summary != null)
      {
         String old = this.summary;
         this.summary = summary;
         pcs.firePropertyChange("summary", old, summary);
      }
   }

   /**
    * Gets the {@link javax.swing.Icon} that represents this step.
    *
    * @return the {@link javax.swing.Icon} that represents this step, or <tt>null</tt> if the step
    *         doesn't have an icon.
    */
   public Icon
   getIcon()
   {
      return icon;
   }

   /**
    * Sets the {@link javax.swing.Icon} that represents this step.
    *
    * @param icon the {@link javax.swing.Icon} that represents this step, or <tt>null</tt> if the step
    *             doesn't have an icon.
    */
   public void
   setIcon(Icon icon)
   {
      if ((this.icon != null && !this.icon.equals(icon)) || this.icon == null && icon != null)
      {
         Icon old = this.icon;
         this.icon = icon;
         pcs.firePropertyChange("icon", old, icon);
      }
   }

   /**
    * Returns the current view this step is displaying.  This component will be displayed in the main
    * section of the wizard with this step is active.  This may changed at any time by calling
    * {@link #setView} and the wizard will update accordingly.
    *
    * @return the current view of the step.
    * @see #setView
    */
   public Component
   getView()
   {
      return view;
   }

   /**
    * Sets the current view this step is displaying.  This component will be displayed in the main
    * section of the wizard with this step is active.  This method may changed at any time and the
    * wizard will update accordingly.
    *
    * @param component the current view of the step.
    */
   protected void
   setView(Component component)
   {
      if (!component.equals(view))
      {
         Component old = view;
         view = component;
         pcs.firePropertyChange("view", old, view);
      }
   }

   /**
    * Checks if this step is compete.  This method should return true if the wizard can proceed
    * to the next step.  This property is bound and changes can be made at anytime by calling
    * {@link #setComplete(boolean)} .
    *
    * @return <tt>true</tt> if the wizard can proceed from this step, <tt>false</tt> otherwise.
    * @see #setComplete
    */
   public boolean
   isComplete()
   {
      return complete;
   }

   /**
    * Marks this step as compete.  The wizard will not be able to proceed from this step until
    * this property is configured to <tt>true</tt>.
    *
    * @param complete <tt>true</tt> to allow the wizard to proceed, <tt>false</tt> otherwise.
    * @see #isComplete
    */
   public void
   setComplete(boolean complete)
   {
      if (this.complete != complete)
      {
         this.complete = complete;
         pcs.firePropertyChange("complete", !complete, complete);
      }
   }


   /**
    * Checks if the current task is busy.  This usually indicates that the step is performing
    * a time consuming task on a background thread.
    *
    * @return <tt>true</tt> if step is busy performing a background operation, <tt>false</tt>
    *         otherwise.
    */
   public boolean
   isBusy()
   {
      return busy;
   }

   /**
    * Sets the busy state of this wizard step.  This should usually be set when a time consuming
    * task is being performed on a background thread.  The Wizard responds by disabling the various
    * buttons appropriately.<p>
    * Wizard steps that go into a busy state must also implement {@link #abortBusy} to cancel any
    * inprogress operation.
    *
    * @param busy <tt>true</tt> to mark the step as busy and disable further user action, <tt>false</tt>
    *             to return the wizard to its normal state.
    */
   public void
   setBusy(boolean busy)
   {
      if (this.busy != busy)
      {
         boolean old = this.busy;
         this.busy = busy;
         pcs.firePropertyChange("busy", old, busy);
      }
   }

   /////////////////////////////////////////////////////////////////////
   // Abstract Methods
   //

   /**
    * Called to initialize the step.  This method will be called when the wizard is
    * first initialising.
    *
    * @param model the model to which the step belongs.
    */
   public abstract void
   init(WizardModel model);


   /**
    * Called by the wizard if the user presses cancel while the step is in a {@link #isBusy busy}
    * state.  Steps that are never busy need not override this method.
    */
   public void
   abortBusy()
   {
   }


   /////////////////////////////////////////////////////////////////////
   // Property change support
   //
   public void
   addPropertyChangeListener(PropertyChangeListener listener)
   {
      pcs.addPropertyChangeListener(listener);
   }

   public void
   removePropertyChangeListener(PropertyChangeListener listener)
   {
      pcs.removePropertyChangeListener(listener);
   }

   public void
   addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
   {
      pcs.addPropertyChangeListener(propertyName, listener);
   }

   public void
   removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
   {
      pcs.removePropertyChangeListener(propertyName, listener);
   }
}
