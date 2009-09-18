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
 * $Id: HTMLPane.java,v 1.1 2007-05-17 13:58:50 joshua Exp $
 */


package org.pietschy.wizard;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.EditorKit;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.IOException;
import java.net.URL;

/**
 * This class displays HTML text using an instance of {@link JEditorPane} but allows
 * the font, foreground and background colors to be easily changed.  This is accomplished by
 * updating the documents style sheet rules when ever the font and color attributes are changed.
 *
 * @see #setForeground
 */
public class
HTMLPane
extends JEditorPane
{
   private HTMLEditorKit kit;
   private boolean antiAlias = false;
   private boolean forceReload = false;

   /**
    * Creates a new <code>JEditorPane</code>.
    * The document model is set to <code>null</code>.
    */
   public HTMLPane()
   {
      this(true);
   }

   /**
    * Creates a new {@link HTMLPane}.
    */
   public HTMLPane(boolean opaque)
   {
      kit = new HTMLEditorKit();
      setEditorKit(kit);
      setFont(UIManager.getFont("Label.font"));
      updateEditorColor(getForeground());
      setEditable(false);
      setOpaque(opaque);


   }

   public URL
   getPage()
   {
      if (forceReload)
      {
         forceReload = false;
         return null;
      }

      return super.getPage();
   }

   public void
   setPage(URL page) throws IOException
   {
      forceReload = true;
      super.setPage(page);
   }

   public void
   setFont(Font font)
   {
      super.setFont(font);
      if (kit != null)
         updateEditorFont(font);
   }

   public void
   setForeground(Color fg)
   {
      super.setForeground(fg);
      if (kit != null)
         updateEditorColor(fg);
   }

   public void
   setEditorKit(EditorKit kit)
   {
      super.setEditorKit(kit);
      updateEditorColor(getForeground());
      updateEditorFont(getFont());
   }

   private void
   updateEditorFont(Font font)
   {
      StringBuffer rule = new StringBuffer("body { ");
      rule.append("font-family: ").append(font.getFamily()).append(";");
      rule.append(" font-size: ").append(font.getSize()).append("pt;");
      if (font.isBold())
      {
         rule.append("font-weight: 700;");
      }
      if (font.isItalic())
      {
         rule.append("font-style: italic;");
      }

      rule.append("}");

      kit.getStyleSheet().addRule(rule.toString());
   }

   private void
   updateEditorColor(Color fg)
   {
      StringBuffer rule = new StringBuffer("body { color: #");

      if (fg.getRed() < 16)
      {
         rule.append('0');
      }
      rule.append(Integer.toHexString(fg.getRed()));

      if (fg.getGreen() < 16)
      {
         rule.append('0');
      }
      rule.append(Integer.toHexString(fg.getGreen()));

      if (fg.getBlue() < 16)
      {
         rule.append('0');
      }
      rule.append(Integer.toHexString(fg.getBlue()));

      rule.append(";}");

      kit.getStyleSheet().addRule(rule.toString());
   }


   public boolean
   isAntiAlias()
   {
      return antiAlias;
   }

   public void
   paint(Graphics g)
   {
      if (antiAlias)
      {
         Graphics2D g2 = (Graphics2D) g;
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         super.paint(g2);
      }
      else
      {
         super.paint(g);
      }
   }

   public void
   setAntiAlias(boolean antiAlias)
   {
      this.antiAlias = antiAlias;
      repaint();
   }


}
