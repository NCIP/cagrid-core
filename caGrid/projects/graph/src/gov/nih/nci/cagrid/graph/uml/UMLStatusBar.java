package gov.nih.nci.cagrid.graph.uml;

import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

public class UMLStatusBar extends JComponent
{
     public JTextField coord;
     public JTextField msg;

     public UMLStatusBar()
     {
          coord = new JTextField();
          msg = new JTextField();
          msg.setEditable(false);
          coord.setEditable(false);
          msg.setFont(new Font("verdana", Font.BOLD, 10));
          coord.setFont(new Font("verdana", Font.PLAIN, 10));

          this.add(coord);
          this.add(msg);

          this.addComponentListener(new UMLStatusBarComponentListener());

          this.msg.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
          this.coord.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

          this.setDefaultMsg();

     }

     public void setMsg(String msg)
     {
          this.msg.setText(" " + msg);
     }

     public void setDefaultMsg()
     {
          this.setMsg("XML Data Model Viewer 1.0");
     }


     public void setCoords(int x, int y)
     {
          this.coord.setText("  " + x + ", " + y);
     }

     public void setCoordsMsg(String msg)
     {
          this.coord.setText(" " + msg);
     }

}


class UMLStatusBarComponentListener extends ComponentAdapter
{
     public void componentResized(ComponentEvent e)
     {
          UMLStatusBar s = (UMLStatusBar) e.getSource();

          s.msg.setBounds(0, 0, s.getWidth()-101, s.getHeight());
          s.coord.setBounds(s.getWidth()-100, 0, 100, s.getHeight());
     }
}
