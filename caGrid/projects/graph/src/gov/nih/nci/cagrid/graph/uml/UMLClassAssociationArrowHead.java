package gov.nih.nci.cagrid.graph.uml;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import org.tigris.gef.presentation.ArrowHead;
import org.tigris.gef.presentation.ArrowHeadGreater;
import org.tigris.gef.presentation.ArrowHeadTriangle;
import org.tigris.gef.presentation.FigNode;

public class UMLClassAssociationArrowHead extends FigNode
{
     protected ArrowHead arr;

     protected ArrowHeadTriangle highlight = new ArrowHeadTriangle();
     protected ArrowHeadGreater  normal = new ArrowHeadGreater();

     protected Point p1 = new Point();
     protected Point p2 = new Point();

     public UMLClassAssociationArrowHead()
     {
          super();
          arr = normal;
     }

     public void highlight()
     {
          arr = highlight;
          arr.setFillColor(Color.red);
          arr.setLineColor(Color.black);
     }

     public void setNormal()
     {
          arr = normal;
          arr.setLineColor(Color.black);
     }

     public void fade()
     {
          arr = normal;
          arr.setLineColor(Color.lightGray);
     }

     public void setDirection(Point p1, Point p2)
     {
          this.p1 = p1;
          this.p2 = p2;

     }

     public void paint(Graphics g)
     {
          arr.paint(g, p1, p2);
     }
}
