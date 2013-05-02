/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package gov.nih.nci.cagrid.graph.uml;

import java.awt.Font;

import org.tigris.gef.presentation.FigText;


public class Text extends FigText
{
     public UMLClassAssociation parent;

     public Text(int x, int y, String text, UMLClassAssociation parent)
     {
          super(0,0,0,0);
          super.setText(text);
          super.setFont(new Font("verdana", Font.PLAIN, 11));
          super.setFilled(false);
          super.setLineWidth(0);
          super.setLocation(x, y);
          //super.setResizable(false);
          //super.setMovable(false);
          super.setEditable(false);

          this.parent = parent;
     }
}
