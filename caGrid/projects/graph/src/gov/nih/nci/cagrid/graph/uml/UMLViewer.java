// Forhad Ahmed
//
// JGraph hack that enables
// scrolling via a 'pager' control
// in addition to scrollbars =)

package gov.nih.nci.cagrid.graph.uml;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.tigris.gef.base.Globals;
import org.tigris.gef.graph.presentation.JGraph;
import org.tigris.gef.presentation.Fig;


// The UMLDiagram class is the public class that
// exposes clients to a UML diagram component.
//
// The UMLViewer class is the internal (non-public)
// class that implements the core of the UMLDiagram
// functionality

public class UMLViewer extends JGraph {
	protected UMLDiagram diagram;
	protected Pager pager;
	protected PagerCaptionBar pagerCaptionBar;
	protected PagerButton pagerButton;


	public UMLViewer(UMLDiagram d) {
		super(d.diagram);
		this.diagram = d;
		pagerButton = new PagerButton(diagram);
		this.getScrollPane().setCorner(JScrollPane.LOWER_RIGHT_CORNER, pagerButton);

		// this will make sure that the pager scrolls with the scrollbars
		this.getScrollPane().getViewport().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				pager.updateScroller();
				pager.repaint();
			}
		});

		this.addComponentListener(new UMLViewerComponentListener());
		this.addKeyListener(new UMLViewerKeyListener(this));
		this.addMouseListener(new UMLViewerMouseListener(this));
		this.addMouseMotionListener(new UMLViewerMouseMotionListener(this));

		this.pager = new Pager(this.diagram);
		this.pagerCaptionBar = new PagerCaptionBar(this.pager);

		
		this.setDrawingSize(1500, 1000);
		Globals.setShowFigTips(false);
		
		

	}

	public void unHighlightAll()
	{
		for (int k = 0; k < diagram.classes.size(); k++) {
			UMLClass c = (UMLClass) diagram.classes.get(k);
			diagram.diagram.getLayer().bringToFront(c);
			c.setNormal();
		}

		for (int m = 0; m < diagram.assocs.size(); m++) {
			UMLClassAssociation edge = (UMLClassAssociation) diagram.assocs.get(m);
			edge.setNormal();
		}		
		
		diagram.viewer.repaint();
		repaint();

	}
	

	public void updateDrawingSizeToIncludeAllFigs() {
		Iterator iter = this.diagram.diagram.getLayer().getContents().iterator();
		if (iter == null) {
			return;
		}
		Dimension drawingSize = new Dimension(10, 10);
		while (iter.hasNext()) {
			Fig fig = (Fig) iter.next();
			Rectangle rect = fig.getBounds();
			Point point = rect.getLocation();
			Dimension dim = rect.getSize();
			if ((point.x + dim.width + 50) > drawingSize.width) {
				drawingSize.setSize(point.x + dim.width + 50, drawingSize.height);
			}
			if ((point.y + dim.height + 50) > drawingSize.height) {
				drawingSize.setSize(drawingSize.width, point.y + dim.height + 50);
			}
		}
		//if (drawingSize.width < drawingSize.height)
		//	drawingSize.width = drawingSize.height;
		//if (drawingSize.height < drawingSize.width)
		//	drawingSize.height = drawingSize.width;
		setDrawingSize(drawingSize.width, drawingSize.height);
	}


	public void setDiagram(UMLDiagram d) {
		super.setDiagram(d.diagram);
		this.diagram = d;
		this.pager.diagram = this.diagram;
	}


	public boolean highlightClass(UMLClass c) {
		if (this.diagram.classes.contains((UMLClass) c)) {
			c.highlight(diagram.diagram.getLayer());
			pager.highlightClass(c);

			for (int j = 0; j < diagram.assocs.size(); j++) {
				UMLClassAssociation temp = (UMLClassAssociation) diagram.assocs.get(j);

				temp.fade();

			}

			for (int k = 0; k < diagram.classes.size(); k++) {
				UMLClass c2 = (UMLClass) diagram.classes.get(k);

				if (c != c2) {
					c2.fade();
				}

			}

			diagram.statusBar.setMsg("Class: [ " + c.name + " ]");
			return true;
		}
		return false;
	}

}

class UMLViewerKeyListener extends KeyAdapter {
	protected UMLViewer parent;


	public UMLViewerKeyListener(UMLViewer p) {
		this.parent = p;
	}


	public void keyPressed(KeyEvent e) {
		parent.diagram.repositionLabelsAndArrowHeads();
	}
}

class UMLViewerMouseListener extends MouseAdapter {
	protected UMLViewer parent;


	public UMLViewerMouseListener(UMLViewer p) {
		this.parent = p;
	}



	
	public void mousePressed(MouseEvent e) {
		if(!parent.diagram.inactiveState)
		{
			parent.diagram.statusBar.setDefaultMsg();
	
			parent.pager.unhighlightAll();
	
			for (int k = 0; k < parent.diagram.classes.size(); k++) {
				UMLClass c = (UMLClass) parent.diagram.classes.get(k);
				parent.diagram.diagram.getLayer().bringToFront(c);
				if(c.defaultRendering == UMLClass.NORMAL)
				{
					c.setNormal();
				}
				else
				{
					c.fade();
				}
			}
	
			for (int m = 0; m < parent.diagram.assocs.size(); m++) {
				UMLClassAssociation edge = (UMLClassAssociation) parent.diagram.assocs.get(m);
				edge.setNormal();
			}
	
			parent.diagram.viewer.repaint();
	
			if (parent.diagram.viewer.selectedFigs().size() == 1) {
				if (parent.diagram.viewer.selectedFigs().get(0) instanceof UMLClassAssociation) {
	
					UMLClassAssociation edge = (UMLClassAssociation) parent.diagram.viewer.selectedFigs().get(0);
					edge.highlight(parent.diagram.diagram.getLayer());
					parent.pager.highlightEdge(edge);
	
					for (int j = 0; j < parent.diagram.assocs.size(); j++) {
						UMLClassAssociation temp = (UMLClassAssociation) parent.diagram.assocs.get(j);
	
						if (temp != edge) {
							temp.fade();
						}
					}
	
					for (int k = 0; k < parent.diagram.classes.size(); k++) {
						UMLClass c = (UMLClass) parent.diagram.classes.get(k);
	
						if (edge.source != c && edge.destination != c) {
	
							c.fade();
						}
	
					}
	
					UMLClass source = (UMLClass) edge.getSourceFigNode();
					UMLClass dest = (UMLClass) edge.getDestFigNode();
	
					parent.diagram.statusBar.setMsg("Association: [ " + source.name + " , " + dest.name + " ]");
				} else if (parent.diagram.viewer.selectedFigs().get(0) instanceof Text) {
					Text t = (Text) parent.diagram.viewer.selectedFigs().get(0);
	
					t.parent.highlight(parent.diagram.diagram.getLayer());
					parent.pager.highlightEdge(t.parent);
	
					for (int j = 0; j < parent.diagram.assocs.size(); j++) {
						UMLClassAssociation temp = (UMLClassAssociation) parent.diagram.assocs.get(j);
	
						if (temp != t.parent) {
							temp.fade();
						}
	
					}
	
					UMLClassAssociation edge = t.parent;
					for (int k = 0; k < parent.diagram.classes.size(); k++) {
						UMLClass c = (UMLClass) parent.diagram.classes.get(k);
	
						if (edge.source == c || edge.destination == c) {
	
						} else {
							c.fade();
						}
	
					}
	
					UMLClass source = (UMLClass) t.parent.getSourceFigNode();
					UMLClass dest = (UMLClass) t.parent.getDestFigNode();
	
					parent.diagram.statusBar.setMsg("Association: [ " + source.name + " , " + dest.name + " ]");
	
				} else if (parent.diagram.viewer.selectedFigs().get(0) instanceof UMLClass) {
	
					UMLClass c = (UMLClass) parent.diagram.viewer.selectedFigs().get(0);
	
					parent.diagram.viewer.highlightClass(c);
	
				}
			}
	
			parent.repaint();
		}
	}


	public void mouseReleased(MouseEvent e) {

	}
}

class UMLViewerMouseMotionListener extends MouseMotionAdapter {
	protected UMLViewer parent;


	public UMLViewerMouseMotionListener(UMLViewer p) {
		this.parent = p;
	}


	public void mouseDragged(MouseEvent e) {
		if(!parent.diagram.inactiveState)
		{
			parent.diagram.repositionLabelsAndArrowHeads();
			parent.diagram.statusBar.setCoords(e.getX(), e.getY());
			parent.diagram.viewer.repaint();
			parent.updateDrawingSizeToIncludeAllFigs();
		}
	}


	public void mouseMoved(MouseEvent e) {
		parent.diagram.statusBar.setCoords(e.getX(), e.getY());
	}
}

class UMLViewerComponentListener extends ComponentAdapter {
	public void componentResized(ComponentEvent e) {

		UMLViewer s = (UMLViewer) e.getSource();
		Container parent = s.diagram.getParent().getParent();
		s.pager.updateScroller();
		//s.pager.setBounds(parent.getWidth() - 200 - s.pagerButton.getWidth() - 5, parent.getHeight() - 200
		//	- s.pagerButton.getHeight() - s.diagram.statusBar.getHeight() - 5, 200, 200);
		//s.pagerCaptionBar.setBounds(parent.getWidth() - 200 - s.pagerButton.getWidth() - 5, parent.getHeight() - 200
		//	- s.pagerButton.getHeight() - s.diagram.statusBar.getHeight() - 5 - 17, 200, 17);
	}
}

class PagerButton extends JButton implements MouseListener {
	
	protected UMLDiagram diagram;

	protected boolean pressed = true;


	public PagerButton(UMLDiagram d) {
		this.diagram = d;
		this.addMouseListener(this);
		this.setBackground(Color.white);

	}


	public void paint(Graphics g) {
		super.paint(g);

		// draw the arrow and the light/dark colors

		if (!pressed) {
			g.setColor(Color.gray);
			g.fillRect(1, 1, this.getWidth() - 2, this.getHeight() - 2);

			g.setColor(Color.black);
			g.drawLine(4, 4, 10, 10);
			g.drawLine(10, 10, 10, 4);
			g.drawLine(10, 10, 4, 10);

		} else {
			// g.setColor(Color.black);

			g.drawLine(9, 9, 12, 12);
			g.drawLine(9, 10, 11, 12);
			g.drawLine(10, 9, 12, 11);
			g.drawArc(3, 3, 6, 6, 0, 360);

		}

	}


	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			this.setFocusable(false);

			if (!pressed) {
				pressed = true;
				this.diagram.viewer.pager.updateScroller();
				this.diagram.viewer.pager.setVisible(false);
				this.diagram.viewer.pagerCaptionBar.setVisible(false);
			} else {
				
				JLayeredPane parent = (JLayeredPane) diagram;//.getParent().getParent();

				// this is a bad hack but it works... must find underlying
				// problem later
				this.diagram.viewer.setSize(this.diagram.viewer.getWidth() - 1, this.diagram.viewer.getHeight());
				this.diagram.viewer.setSize(this.diagram.viewer.getWidth() + 1, this.diagram.viewer.getHeight());
				// end of bad hack

				this.diagram.viewer.pager.setBounds(parent.getWidth() - 200 - this.getWidth() - 5, parent.getHeight()
					- 200 - this.getHeight() - this.diagram.statusBar.getHeight() - 5, 200, 200);

				this.diagram.viewer.pagerCaptionBar.setBounds(parent.getWidth() - 200 - this.getWidth() - 5, parent
					.getHeight()
					- 200 - this.getHeight() - 5 - 17 - this.diagram.statusBar.getHeight(), 200, 17);
				
		
				if (parent.getComponentCount() == 3) {
					parent.add(this.diagram.viewer.pager, JLayeredPane.POPUP_LAYER);
					parent.add(this.diagram.viewer.pagerCaptionBar, JLayeredPane.POPUP_LAYER);
				}
				this.diagram.viewer.pager.updateScroller();
				this.diagram.viewer.pager.setVisible(true);
				this.diagram.viewer.pagerCaptionBar.setVisible(true);
				pressed = false;
			}
		}
	}


	public void mouseReleased(MouseEvent e) {

	}


	public void mouseEntered(MouseEvent e) {

	}


	public void mouseExited(MouseEvent e) {

	}


	public void mouseClicked(MouseEvent e) {

	}

}

class Pager extends JButton {
	protected UMLDiagram diagram;
	protected PagerScroller scroller;
	
	public Color superLightGray = new Color(235, 235, 235);

	protected UMLClassAssociation highlightEdge = null;
	protected UMLClass highlightClass1 = null;
	protected UMLClass highlightClass2 = null;

	int pagerVirtualWidth;
	int pagerVirtualHeight;

	public Pager(UMLDiagram d) {
		super();
		this.diagram = d;
		this.addMouseListener(new PagerMouseListener());
		this.addMouseMotionListener(new PagerMouseMotionListener());
		this.scroller = new PagerScroller(diagram, this);
		this.setDoubleBuffered(true);
		this.add(scroller);
		this.setLayout(null);

	}


	public void updateScroller() {
		
	
		
		int canvasWidth = diagram.viewer.getScrollPane().getViewport().getView().getSize().width;
		int canvasHeight = diagram.viewer.getScrollPane().getViewport().getView().getSize().height;

		pagerVirtualWidth = 200;
		pagerVirtualHeight = 200;
		
		if(canvasWidth > canvasHeight)
		{
			pagerVirtualWidth = 200;
			pagerVirtualHeight = (int)((float)(canvasHeight/(float)canvasWidth) * (float)200);

						
		

		}
		else if(canvasWidth <= canvasHeight) 
		{
			pagerVirtualHeight = 200;
			pagerVirtualWidth = (int)((float)(canvasWidth/(float)canvasHeight) * (float)200);


		}
	
		
		int pagerX = -diagram.viewer.getScrollPane().getViewport().getView().getBounds().x;
		int pagerY = -diagram.viewer.getScrollPane().getViewport().getView().getBounds().y;
		int pagerW = diagram.viewer.getScrollPane().getViewport().getBounds().width;
		int pagerH = diagram.viewer.getScrollPane().getViewport().getBounds().height;

		float updatedXs = pagerX * (float) ((float) pagerVirtualWidth / (float) canvasWidth);
		float updatedYs = pagerY * (float) ((float) pagerVirtualHeight / (float) canvasHeight);
		float updatedWidths = pagerW * (float) ((float) pagerVirtualWidth / (float) canvasWidth);
		float updatedHeights = pagerH * (float) ((float) pagerVirtualHeight / (float) canvasHeight);

		scroller.setBounds((int) updatedXs, (int) updatedYs, (int) updatedWidths, (int) updatedHeights);

	}


	public void unhighlightAll() {
		this.highlightClass1 = null;
		this.highlightClass2 = null;
		this.highlightEdge = null;
	}


	public void highlightEdge(UMLClassAssociation edge) {
		this.highlightEdge = edge;
		this.highlightClass1 = edge.source;
		this.highlightClass2 = edge.destination;
	}


	public void highlightClass(UMLClass c) {
		this.highlightClass1 = c;
	}


	public void paint(Graphics g) {
		super.paint(g);

		g.setColor(Color.white);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		

		int canvasWidth = diagram.viewer.getScrollPane().getViewport().getView().getSize().width;
		int canvasHeight = diagram.viewer.getScrollPane().getViewport().getView().getSize().height;

		
		if(canvasWidth > canvasHeight)
		{
			pagerVirtualWidth = 200;
			pagerVirtualHeight = (int)((float)(canvasHeight/(float)canvasWidth) * (float)200);

						
			g.setColor(superLightGray);
			g.fillRect(0, pagerVirtualHeight, 200, 200);

		}
		else if(canvasWidth <= canvasHeight) 
		{
			pagerVirtualHeight = 200;
			pagerVirtualWidth = (int)((float)(canvasWidth/(float)canvasHeight) * (float)200);

			
			g.setColor(superLightGray);
			g.fillRect(pagerVirtualWidth , 0, 200, 200 );
		}
		

		if(!this.diagram.inactiveState)
		{
			g.setColor(Color.lightGray);
	
			for (int i = 0; i < diagram.assocs.size(); i++) {
				UMLClassAssociation edge = (UMLClassAssociation) diagram.assocs.elementAt(i);
	
				if (edge.getPoints().length > 1) {
					for (int k = 0; k < edge.getPoints().length - 1; k++) {
						int originalX1 = edge.getPoints()[k].x;
						int originalY1 = edge.getPoints()[k].y;
	
						int originalX2 = edge.getPoints()[k + 1].x;
						int originalY2 = edge.getPoints()[k + 1].y;
	
						float updatedX1 = originalX1 * (float) ((float) pagerVirtualWidth / (float) canvasWidth);
						float updatedY1 = originalY1 * (float) ((float) pagerVirtualHeight / (float) canvasHeight);
	
						float updatedX2 = originalX2 * (float) ((float) pagerVirtualWidth/ (float) canvasWidth);
						float updatedY2 = originalY2 * (float) ((float)pagerVirtualHeight / (float) canvasHeight);
	
						g.drawLine((int) updatedX1, (int) updatedY1, (int) updatedX2, (int) updatedY2);
	
					}
				}
			}
	
			for (int c = 0; c < diagram.classes.size(); c++) {
				UMLClass f = (UMLClass) diagram.classes.elementAt(c);
	
				int originalX = f.getLocation().x;
				int originalY = f.getLocation().y;
				int originalWidth = f.getSize().width;
				int originalHeight = f.getSize().height;
	
				float updatedX = originalX * (float) ((float) pagerVirtualWidth / (float) canvasWidth);
				float updatedY = originalY * (float) ((float) pagerVirtualHeight / (float) canvasHeight);
				float updatedWidth = originalWidth * (float) ((float) pagerVirtualWidth/ (float) canvasWidth);
				float updatedHeight = originalHeight * (float) ((float) pagerVirtualHeight / (float) canvasHeight);
	
				g.setColor(Color.white);
				g.fillRect((int) updatedX, (int) updatedY, (int) updatedWidth, (int) updatedHeight);
	
				g.setColor(Color.lightGray);
				g.drawRect((int) updatedX, (int) updatedY, (int) updatedWidth, (int) updatedHeight);
	
			}
	
			UMLClassAssociation edge = this.highlightEdge;
	
			if (edge != null) {
	
				g.setColor(Color.red);
	
				if (edge.getPoints().length > 1) {
					for (int k = 0; k < edge.getPoints().length - 1; k++) {
						int originalX1 = edge.getPoints()[k].x;
						int originalY1 = edge.getPoints()[k].y;
	
						int originalX2 = edge.getPoints()[k + 1].x;
						int originalY2 = edge.getPoints()[k + 1].y;
	
						float updatedX1 = originalX1 * (float) ((float) pagerVirtualWidth / (float) canvasWidth);
						float updatedY1 = originalY1 * (float) ((float) pagerVirtualHeight/ (float) canvasHeight);
	
						float updatedX2 = originalX2 * (float) ((float) pagerVirtualWidth / (float) canvasWidth);
						float updatedY2 = originalY2 * (float) ((float) pagerVirtualHeight / (float) canvasHeight);
	
						g.drawLine((int) updatedX1, (int) updatedY1, (int) updatedX2, (int) updatedY2);
	
					}
				}
			}
	
			UMLClass f = this.highlightClass1;
	
			if (f != null) {
				int originalX = f.getLocation().x;
				int originalY = f.getLocation().y;
				int originalWidth = f.getSize().width;
				int originalHeight = f.getSize().height;
	
				float updatedX = originalX * (float) ((float) pagerVirtualWidth / (float) canvasWidth);
				float updatedY = originalY * (float) ((float) pagerVirtualHeight / (float) canvasHeight);
				float updatedWidth = originalWidth * (float) ((float) pagerVirtualWidth / (float) canvasWidth);
				float updatedHeight = originalHeight * (float) ((float) pagerVirtualHeight / (float) canvasHeight);
	
				g.setColor(Color.white);
				g.fillRect((int) updatedX, (int) updatedY, (int) updatedWidth, (int) updatedHeight);
	
				g.setColor(Color.black);
				g.drawRect((int) updatedX, (int) updatedY, (int) updatedWidth, (int) updatedHeight);
			}
	
			f = this.highlightClass2;
	
			if (f != null) {
				int originalX = f.getLocation().x;
				int originalY = f.getLocation().y;
				int originalWidth = f.getSize().width;
				int originalHeight = f.getSize().height;
	
				float updatedX = originalX * (float) ((float) pagerVirtualWidth / (float) canvasWidth);
				float updatedY = originalY * (float) ((float) pagerVirtualHeight / (float) canvasHeight);
				float updatedWidth = originalWidth * (float) ((float) pagerVirtualWidth / (float) canvasWidth);
				float updatedHeight = originalHeight * (float) ((float) pagerVirtualHeight / (float) canvasHeight);
	
				g.setColor(Color.white);
				g.fillRect((int) updatedX, (int) updatedY, (int) updatedWidth, (int) updatedHeight);
	
				g.setColor(Color.black);
				g.drawRect((int) updatedX, (int) updatedY, (int) updatedWidth, (int) updatedHeight);
			}
	
		}
		
		g.setColor(Color.black);
		g.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);

		g.translate(scroller.getX(), scroller.getY());
		// draw the pager now
		this.scroller.paint(g);
	}
}

class PagerMouseMotionListener extends MouseMotionAdapter {
	public void mouseDragged(MouseEvent e) {

	}

}

class PagerMouseListener extends MouseAdapter {
	public void mouseClicked(MouseEvent e) {

	}
}

class PagerScroller extends JPanel {
	protected UMLDiagram diagram;
	protected Pager pager;

	protected Point lastClicked;


	public PagerScroller(UMLDiagram d, Pager p) {
		super();
		this.diagram = d;
		this.pager = p;

		this.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));

		this.addMouseMotionListener(new PagerScrollerMouseMotionListener());
		this.addMouseListener(new PagerScrollerMouseListener());
	}


	public void paint(Graphics g) {
		g.setColor(Color.black);
		g.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
	}

}

class PagerScrollerMouseMotionListener extends MouseMotionAdapter {
	public void mouseDragged(MouseEvent e) {
		PagerScroller s = (PagerScroller) e.getSource();

		// this makes the pager 'drag' with the mouse
		s.setLocation(s.getX() + e.getX() - s.lastClicked.x, s.getY() + e.getY() - s.lastClicked.y);

		// make sure the pager stays within bounds!!

		if (s.getX() < 0 || s.getY() < 0 || s.getX() + s.getWidth() > 200 || s.getY() + s.getHeight() > 200) {
			if (s.getX() < 0)
				s.setLocation(0, s.getY());
			if (s.getY() < 0)
				s.setLocation(s.getX(), 0);
			if (s.getX() + s.getWidth() > s.pager.pagerVirtualWidth)
				s.setLocation(s.pager.pagerVirtualWidth - s.getWidth(), s.getY());
			if (s.getY() + s.getHeight() > s.pager.pagerVirtualHeight)
				s.setLocation(s.getX(), s.pager.pagerVirtualHeight - s.getHeight());
		}

		int canvasWidth = s.diagram.viewer.getScrollPane().getViewport().getView().getSize().width;
		int canvasHeight = s.diagram.viewer.getScrollPane().getViewport().getView().getSize().height;

		float scaleFactorX = ((float) canvasWidth / (float) s.pager.pagerVirtualWidth);
		float scaleFactorY = ((float) canvasHeight / (float) s.pager.pagerVirtualHeight);

		// this makes the viewport scroll with the pager
		s.pager.diagram.viewer.getScrollPane().getViewport().setViewPosition(
			new Point((int) ((float) s.getX() * scaleFactorX), (int) ((float) s.getY() * scaleFactorY)));
		s.pager.diagram.viewer.validate();
		s.pager.repaint();

	}
}

class PagerScrollerMouseListener extends MouseAdapter {
	public void mousePressed(MouseEvent e) {
		PagerScroller s = (PagerScroller) e.getSource();

		s.lastClicked = e.getPoint();
	}
}

class PagerCaptionBar extends JComponent {
	public static Color lightlightgray = new Color(245, 245, 245);
	public static Font font = new Font("verdana", Font.PLAIN, 10);

	public Pager pager;
	
	public Point lastClicked;

	public PagerCaptionBarCloseButton button;


	public PagerCaptionBar(Pager p) {
		super();
		this.pager = p;
		this.button = new PagerCaptionBarCloseButton(this);
		this.addComponentListener(new PagerCaptionBarComponentListener());
		this.addMouseListener(new PagerCaptionBarMouseListener());
		this.addMouseMotionListener(new PagerCaptionBarMouseMotionListener());
		this.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
	}


	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(lightlightgray);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		g.setColor(Color.gray);
		g.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
		g.drawLine(this.getWidth() - 2, 2, this.getWidth() - 2, this.getHeight() - 1);
		g.setFont(font);
		g.drawString("Overview", 4, 12);

	}
}

class PagerCaptionBarCloseButton extends JComponent {
	public PagerCaptionBar parentBar;


	public PagerCaptionBarCloseButton(PagerCaptionBar p) {
		
		this.parentBar = p;
		

	}


	public void paint(Graphics g) {
		super.paint(g);

		g.setColor(Color.black);

		g.drawLine(2, 2, this.getWidth() - 2 - 1, this.getHeight() - 2 - 1);
		g.drawLine(2, this.getHeight() - 2 - 1, this.getWidth() - 2 - 1, 2);

	}

}

class PagerCaptionBarComponentListener extends ComponentAdapter {
	public void componentResized(ComponentEvent e) {
		PagerCaptionBar s = (PagerCaptionBar) e.getSource();

		s.button.setBounds(2, 2, s.getHeight() - 2, s.getHeight() - 2);

	}
}

class PagerCaptionBarMouseListener extends MouseAdapter
{
	public void mousePressed(MouseEvent e)
	{
		PagerCaptionBar s = (PagerCaptionBar) e.getSource();

		s.lastClicked = e.getPoint();		
	}
}

class PagerCaptionBarMouseMotionListener extends MouseMotionAdapter
{
	public void mouseDragged(MouseEvent e)
	{
		PagerCaptionBar s = (PagerCaptionBar) e.getSource();
		s.setLocation(s.getX() + e.getX() - s.lastClicked.x, s.getY() + e.getY() - s.lastClicked.y);
		s.pager.setLocation(s.pager.getX() + e.getX() - s.lastClicked.x, s.pager.getY() + e.getY() - s.lastClicked.y);
	}
}