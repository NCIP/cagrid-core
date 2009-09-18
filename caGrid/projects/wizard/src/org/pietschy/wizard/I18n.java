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
 * $Id: I18n.java,v 1.1 2007-05-17 13:58:50 joshua Exp $
 */
package org.pietschy.wizard;

import javax.swing.*;
import java.util.ResourceBundle;
import java.util.MissingResourceException;
import java.util.Locale;


/**
 * Internationalization Helper.  By default this class attempts to load the bundle called 'org-pietshcy-wizard'
 * from the classpath but you can specify you own bundle by calling the static {@link #setBundle} method.
 *
 * @author andrewp
 * @version $Revision: 1.1 $
 */
public class
I18n
{
   private static final String _ID_ = "$Id: I18n.java,v 1.1 2007-05-17 13:58:50 joshua Exp $";

   private static ResourceBundle bundle = null;

   private static ResourceBundle
   getBundle()
   {
      if (bundle == null)
         bundle = ResourceBundle.getBundle("org-pietschy-wizard");

      return bundle;
   }

   public static void
   setBundle(ResourceBundle bundle)
   {
      I18n.bundle = bundle;
   }

   public static String
   getString(String key)
   {
      return getBundle().getString(key);
   }

   public static Object
   getObject(String key)
   {
      return getBundle().getObject(key);
   }

   public static String[]
   getStringArray(String key)
   {
      return getBundle().getStringArray(key);
   }

   public static int
   getMnemonic(String key)
   {
      String mnemonicString = getBundle().getString(key);

      if (mnemonicString == null)
         throw new MissingResourceException("Missing resource: " + key, I18n.class.getName(), key);

      if (mnemonicString.length() != 1)
         throw new IllegalStateException("mnemonic string invalid: " + mnemonicString);

      KeyStroke ks = KeyStroke.getKeyStroke(mnemonicString.toUpperCase());

      if (ks == null)
         throw new IllegalStateException("mnemonic string invalid: " + mnemonicString);

      return ks.getKeyCode();
   }
}
