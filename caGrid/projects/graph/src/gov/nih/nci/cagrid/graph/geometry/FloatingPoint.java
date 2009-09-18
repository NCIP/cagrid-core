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
