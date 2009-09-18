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
 * $Id: InvalidStateException.java,v 1.1 2007-05-17 13:58:50 joshua Exp $
 */

package org.pietschy.wizard;

/**
 * This exception is thrown by {@link WizardStep} instances if the call to
 * {@link WizardStep#applyState} can't be fullfilled.  By default this exception's message will
 * be displayed to the user.  To disable this feature, please ensure you call {@link #setShowUser}
 * with the value of <tt>false</tt>.
 */
public class
InvalidStateException
extends Exception
{
   private boolean showUser = true;

   public InvalidStateException()
   {
      this.showUser = false;
   }

   public InvalidStateException(String message)
   {
      this(message, true);
   }

   public InvalidStateException(String message, Throwable cause)
   {
      this(message, cause, true);
   }


   public InvalidStateException(String message, boolean showUser)
   {
      super(message);
      this.showUser = showUser;
   }

   public InvalidStateException(String message, Throwable cause, boolean showUser)
   {
      super(message, cause);
      this.showUser = showUser;
   }


   /**
    * Checks if this exception should be presented to the user.
    * @return <tt>true</tt> to present the exception to the user, <tt>false</tt> otherwise.
    */
   public boolean
   isShowUser()
   {
      return showUser;
   }

   /**
    * Configures if this exception should be presented to the user.
    * @param showUser <tt>true</tt> to present the exception to the user, <tt>false</tt> otherwise.
    */
   public void
   setShowUser(boolean showUser)
   {
      this.showUser = showUser;
   }
}
