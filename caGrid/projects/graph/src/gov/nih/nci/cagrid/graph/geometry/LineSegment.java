package gov.nih.nci.cagrid.graph.geometry;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;

public class LineSegment
{
     public Point p1;
     public Point p2;

     public static final int LEFT = 0;
     public static final int RIGHT = 1;
     public static final int UP = 2;
     public static final int DOWN = 3;
     public static final int EVEN = 4;
     public static final int TOP = 5;
     public static final int BOTTOM = 6;

     public int getSway(LineSegment ls, int orientation)
     {
          return EVEN;
     }

     public LineSegment(int x1, int y1, int x2, int y2)
     {
          this.p1 = new Point(x1, y1);
          this.p2 = new Point(x2, y2);
     }

     public FloatingPoint getIntersection(LineSegment ls)
     {
    	 /*ToDo:  Two linesegments that are the same...what to do?
    	  *  	  Two linesegments that are coincident, but are not the same line segment...what to do? */
          FloatingPoint rval = new FloatingPoint(0,0);

          Line2D line1 = new Line2D.Float(this.p1.x, this.p1.y, this.p2.x, this.p2.y);
		  Line2D line2 = new Line2D.Float(ls.p1.x, ls.p1.y, ls.p2.x, ls.p2.y);


		  if((this.p1.x == this.p2.x && this.p1.y==this.p2.y) || (ls.p1.x==ls.p2.x && ls.p1.y==ls.p2.y) )
		  {
			  return null;
		  }

		  if(line1.intersectsLine(line2))
		  {
			  if (this.isParallel(ls))
				  return null;
			  float numerator = (float)( (ls.p2.x-ls.p1.x)*(this.p1.y-ls.p1.y) - (ls.p2.y-ls.p1.y)*(this.p1.x-ls.p1.x) );
			  float denominator = (float)( (ls.p2.y-ls.p1.y)*(this.p2.x - this.p1.x)- (ls.p2.x-ls.p1.x)*(this.p2.y-this.p1.y) );
			  float ua = numerator/denominator;
			  rval.x = this.p1.x + ua*(this.p2.x-this.p1.x);
			  rval.y = this.p1.y + ua*(this.p2.y-this.p1.y);
			  return rval;
		  }

          return null;
     }

     public boolean isVertical()
     {
          return (this.p1.x == this.p2.x);
     }

     public boolean isHorizontal()
     {
          return (this.p1.y == this.p2.y);
     }

     public boolean isParallel(LineSegment ls)
     {
    	 if(this.isVertical() && ls.isVertical())
    		 return true;
    	 int rise1 = this.p2.y - this.p1.y;
		 int run1 = this.p2.x - this.p1.x;
		 float slope1 = (float)rise1/(float)run1;
		 int rise2 = ls.p2.y - ls.p1.y;
		 int run2 = ls.p2.x - ls.p1.x;
		 float slope2 = (float)rise2/(float)run2;
		 if(slope1 == slope2)
			 return true;
		 else
			 return false;
     }

     public String toString()
     {
          return "LineSegment[" + this.p1.x + ", " + this.p1.y + ", " + this.p2.x + ", " + this.p2.y + "]";
     }


     public float cutsRectangle(Rectangle r)
      {

           float rval = 0;

           float area = r.width * r.height;

           if(r.intersectsLine(this.p1.x, this.p1.y, this.p2.x, this.p2.y))
           {
                LineSegment top = new LineSegment(r.x, r.y, r.x + r.width, r.y);
                LineSegment bot = new LineSegment(r.x, r.y + r.height, r.x + r.width, r.y + r.height);
                LineSegment left = new LineSegment(r.x, r.y, r.x, r.y + r.height);
                LineSegment rite = new LineSegment(r.x + r.width, r.y, r.x + r.width, r.y + r.height);

                FloatingPoint top_int = this.getIntersection(top);
                FloatingPoint bot_int = this.getIntersection(bot);
                FloatingPoint left_int = this.getIntersection(left);
                FloatingPoint rite_int = this.getIntersection(rite);

                if(top_int != null && bot_int == null && left_int == null && rite_int == null)
                {

                }
                else if(top_int == null && bot_int != null && left_int == null && rite_int == null)
                {

                }
                else if(top_int == null && bot_int == null && left_int != null && rite_int == null)
                {

                }
                else if(top_int == null && bot_int == null && left_int == null && rite_int != null)
                {

                }
                else if(top_int == null && bot_int == null && left_int == null && rite_int == null)
                {

                }
                else if(top_int != null && bot_int != null && left_int != null && rite_int != null)
                {
                     return 1;
                }




                // top + others
                else if(top_int != null && bot_int == null && left_int != null && rite_int == null)
                {
                     float triangle_area = ((float)(top_int.x - r.x) * (left_int.y - r.y))/((float)2);
                     float area_diff = area - triangle_area;
                     return (float)triangle_area/(float)area_diff;
                }
                else if(top_int != null && bot_int != null && left_int == null && rite_int == null)
                {
                     return 1;
                }
                else if(top_int != null && bot_int == null && left_int == null && rite_int != null)
                {
                     float triangle_area = ((float)(r.x + r.width - top_int.x) * (rite_int.y - r.y))/((float)2);
                     float area_diff = area - triangle_area;
                     return (float)triangle_area/(float)area_diff;

                }

                // bottom + others

                else if(top_int == null && bot_int != null && left_int != null && rite_int == null)
                {
                     float triangle_area = ((float)(bot_int.x - r.x) * (r.y + r.height - left_int.y))/((float)2);
                     float area_diff = area - triangle_area;
                     return (float)triangle_area/(float)area_diff;
                }
                else if(top_int == null && bot_int != null && left_int == null && rite_int != null)
                {
                     float triangle_area = ((float)(r.x + r.width - bot_int.x) * (r.y + r.height - rite_int.y))/((float)2);
                     float area_diff = area - triangle_area;
                     return  (float)triangle_area/(float)area_diff;
                }

                // left + right

                else if(top_int == null && bot_int == null && left_int != null && rite_int != null)
                {
                     return 1;
                }

                // 4 other diagonal special cases

                // topleft + others
                else if(top_int != null && left_int != null && bot_int != null && rite_int == null)
                {
                     float triangle_area = ((float)(bot_int.x - r.x) * (r.y + r.height - left_int.y))/((float)2);
                     float area_diff = area - triangle_area;
                     return (float)triangle_area/(float)area_diff;

                }
                else if(top_int != null && left_int != null && bot_int == null && rite_int == null)
                {

                }

                // toprite + others

                else if(top_int != null && left_int == null && bot_int != null && rite_int != null)
                {

                }
                else if(top_int != null && left_int != null && bot_int != null && rite_int == null)
                {

                }

                // botleft + others

                else if(top_int == null && left_int != null && bot_int != null && rite_int != null)
                {

                }




                // botrite + others



                // else

                else
                {

                     System.out.println("Shouldn't be here");
                }





           }

           return rval;
      }

}
