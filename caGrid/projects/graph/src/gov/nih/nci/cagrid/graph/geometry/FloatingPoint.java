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
package gov.nih.nci.cagrid.graph.geometry;

import java.awt.Point;

public class FloatingPoint
{
     public float x = 0;
     public float y = 0;

     public FloatingPoint(float x, float y)
     {
          this.x = x;
          this.y = y;
     }

     public Point getPoint()
     {
          return new Point( (int) x, (int) y);
     }

     public String toString()
     {
          return "FloatingPoint[ " + this.x + ", " + this.y + "]";
     }
}
