package gov.nih.nci.cagrid.graph.uml;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;

import org.tigris.gef.base.Layer;
import org.tigris.gef.presentation.FigNode;
import org.tigris.gef.presentation.FigText;


public class UMLClass extends FigNode 
{
	protected java.lang.String name = "";
	protected java.util.Vector attributes = new java.util.Vector();
	protected java.util.Vector methods = new java.util.Vector();

	protected static final int PROTECTED = 0;
	protected static final int PRIVATE = 1;
	protected static final int PUBLIC = 2;

	protected FigText header;
	protected FigText attribs;
	protected FigText opers;

	public static int NORMAL = 4;
	public static int FADED = 5;
	public int defaultRendering = NORMAL;

	public UMLClass(java.lang.String name) {
		// start out with a default size
		this.setSize(300, 500);
		this.name = name;
		
		header = new FigText(0, 0, 0, 0);
		attribs = new FigText(0, 0, 0, 0);
		opers = new FigText(0, 0, 0, 0);
		
		header.setFillColor(new Color(252, 242, 227));
		attribs.setFillColor(new Color(252, 242, 227));
		attribs.setTextColor(new Color(139, 0, 0));
		opers.setFillColor(new Color(252, 242, 227));

	}

	public void setDefaultRendering(int rendering)
	{
		if(rendering == NORMAL || rendering == FADED)
		{
			this.defaultRendering = rendering;
		}
	}

	public void highlight(Layer layer) {
		layer.bringToFront(this);
		
		header.setFillColor(new Color(252, 242, 227));
		attribs.setFillColor(new Color(252, 242, 227));
		attribs.setTextColor(new Color(139, 0, 0));
		opers.setFillColor(new Color(252, 242, 227));

		this.header.setLineColor(Color.black);
		this.attribs.setLineColor(Color.black);
		this.opers.setLineColor(Color.black);

		this.header.setTextColor(Color.black);
		//this.attribs.setTextColor(Color.black);
		this.opers.setTextColor(Color.black);

		this.header.setLineWidth(2);
		this.attribs.setLineWidth(2);
		this.opers.setLineWidth(2);

	}


	public void setNormal() {

		header.setFillColor(new Color(252, 242, 227));
		attribs.setFillColor(new Color(252, 242, 227));
		attribs.setTextColor(new Color(139, 0, 0));
		opers.setFillColor(new Color(252, 242, 227));
		
		
		this.header.setLineColor(Color.black);
		this.attribs.setLineColor(Color.black);
		this.opers.setLineColor(Color.black);

		this.header.setTextColor(Color.black);
		this.opers.setTextColor(Color.black);

		this.header.setLineWidth(1);
		this.attribs.setLineWidth(1);
		this.opers.setLineWidth(1);
	}


	public void fade() 
	{
		this.header.setLineColor(Color.lightGray);
		this.attribs.setLineColor(Color.lightGray);
		this.opers.setLineColor(Color.lightGray);

		this.header.setTextColor(Color.lightGray);
		this.attribs.setTextColor(Color.lightGray);
		this.opers.setTextColor(Color.lightGray);

		this.header.setLineWidth(1);
		this.attribs.setLineWidth(1);
		this.opers.setLineWidth(1);
		
		header.setFillColor(Color.white);
		attribs.setFillColor(Color.white);
		opers.setFillColor(Color.white);

	}


	public void setName(java.lang.String name) {
		this.name = name;
	}
	
	public void setToolTip(String text)
	{
		
	}


	public void addAttribute(java.lang.String type, java.lang.String name) {
		this.attributes.addElement(new Attribute(0, type, name));

	}


	public void addAttribute(Attribute att) {
		this.attributes.addElement(att);
	}


	public void addMethod(Method meth) {
		this.methods.addElement(meth);
	}


	public void refresh() {

		this.removeAll();

		
		header.setJustification(FigText.JUSTIFY_CENTER);
		header.setLeftMargin(10);
		header.setRightMargin(10);
		header.setTextColor(Color.darkGray);

		header.setFont(new Font("verdana", Font.BOLD, 11));
		header.setText(this.name);

		if (header.getWidth() > 100) {
			header.setSize(header.getWidth(), 25);
		} else {
			header.setSize(100, 25);
		}

		header.setEditable(false);
		this.addFig(header);

		
		attribs.setFont(new Font("verdana", Font.PLAIN, 11));
		java.lang.String content = "";
		for (int k = 0; k < this.attributes.size(); k++) {
			content += this.attributes.elementAt(k).toString() + "\n";
		}

		if (this.attributes.size() == 0) {
			// attribs.setTextColor(Color.lightGray);
			attribs.setJustification(FigText.JUSTIFY_CENTER);
			content += "(no attributes)";
		}

		attribs.setMultiLine(true);
		attribs.setJustification(FigText.JUSTIFY_LEFT);
		attribs.setLeftMargin(10);
		attribs.setRightMargin(10);
		attribs.setTopMargin(10);
		attribs.setBotMargin(10);
		attribs.setText(content);
		attribs.setLocation(0, 24);

		if (attribs.getWidth() <= header.getWidth()) {
			attribs.setSize(header.getWidth(), attribs.getHeight());
		} else if (attribs.getWidth() > header.getWidth()) {
			header.setSize(attribs.getWidth(), 25);
		}
		this.addFig(attribs);

		
		opers.setFont(new Font("verdana", Font.PLAIN, 11));
		java.lang.String content2 = "";
		for (int k = 0; k < this.methods.size(); k++) {
			content2 += this.methods.elementAt(k).toString() + "\n";
		}

		if (this.methods.size() == 0) {
			opers.setTextColor(Color.lightGray);
			opers.setJustification(FigText.JUSTIFY_CENTER);
			content2 += "(no methods)";
			return;
		}

		opers.setMultiLine(true);
		opers.setJustification(FigText.JUSTIFY_LEFT);
		opers.setLeftMargin(10);
		opers.setRightMargin(10);
		opers.setTopMargin(10);
		opers.setBotMargin(10);
		opers.setText(content2);
		opers.setLocation(0, 18 + attribs.getHeight() - 1);
		if (opers.getWidth() <= header.getWidth()) {
			opers.setSize(header.getWidth(), opers.getHeight());
		} else if (opers.getWidth() > header.getWidth()) {
			header.setSize(opers.getWidth(), 25);
		}
		this.addFig(opers);

	}


	public int getLabelOverlap(Rectangle r) {
		int overlap = 0;

		UMLClassAssociation edge = null;

		for (int k = 0; k < this.getFigEdges().size(); k++) {
			edge = (UMLClassAssociation) this.getFigEdges().get(k);
			overlap += edge.sourceLabel.getBounds().intersection(r).width
				* edge.sourceLabel.getBounds().intersection(r).height;
			overlap += edge.destinationLabel.getBounds().intersection(r).width
				* edge.destinationLabel.getBounds().intersection(r).height;
			overlap += edge.sourceMultiplicity.getBounds().intersection(r).width
				* edge.sourceMultiplicity.getBounds().intersection(r).height;
			overlap += edge.destinationMultiplicity.getBounds().intersection(r).width
				* edge.destinationMultiplicity.getBounds().intersection(r).height;
		}

		return overlap;
	}


	public int getLabelOverlap(int x, int y, int width, int height) {
		int overlap = 0;

		Rectangle r = new Rectangle(x, y, width, height);

		int area = r.width * r.height;

		overlap -= area;

		UMLClassAssociation edge = null;

		for (int k = 0; k < this.getFigEdges().size(); k++) {
			edge = (UMLClassAssociation) this.getFigEdges().get(k);

			// overlap += edge.sourceLabel.getBounds().intersection(r).width *
			// edge.sourceLabel.getBounds().intersection(r).height;

			// overlap +=
			// edge.destinationLabel.getBounds().intersection(r).width *
			// edge.destinationLabel.getBounds().intersection(r).height;

			overlap += edge.sourceMultiplicity.getBounds().intersection(r).width
				* edge.sourceMultiplicity.getBounds().intersection(r).height;

			overlap += edge.destinationMultiplicity.getBounds().intersection(r).width
				* edge.destinationMultiplicity.getBounds().intersection(r).height;

		}

		return overlap;
	}


	public float associationsIntersectRectangle(int x, int y, int width, int height) {
		float intersections = 0;

		Rectangle r = new Rectangle(x, y, width, height);

		UMLClassAssociation edge = null;

		for (int k = 0; k < this.getFigEdges().size(); k++) {
			edge = (UMLClassAssociation) this.getFigEdges().get(k);

			intersections += edge.intersectsRectangle(r);
		}

		return intersections;
	}

}
