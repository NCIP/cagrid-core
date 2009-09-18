package gov.nih.nci.cagrid.graph.uml.classdiagram;


import gov.nih.nci.cagrid.graph.uml.layout.LayoutedEdge;

import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigEdge;
import org.tigris.gef.presentation.FigPoly;

/** This class is an abstract implementation of all edges which are
 *  layoutable in the classdiagram.
 *  @author Markus Klink
 *  @since 0.11.1
 */
public abstract class ClassdiagramEdge implements LayoutedEdge {

    /** the layout is oriented on a grid. These are our grid spaces.
     */
    private static int vGap;
    private static int hGap;


    private FigEdge currentEdge = null;
    /** the underlying fig of the edge we want to layout */
    private FigPoly underlyingFig = null;

    /** each fig has a source and a destination port
     */
    private Fig destFigNode;
    private Fig sourceFigNode;


    /** Constructor.
     * @param edge the Edge to layout
     */
    public ClassdiagramEdge(FigEdge edge) {
        currentEdge = edge;
        underlyingFig = new FigPoly();
        underlyingFig.setLineColor(edge.getFig().getLineColor());

        destFigNode = edge.getDestFigNode();
        sourceFigNode = edge.getSourceFigNode();
    }

    /**
     * Abstract method to layout the edge.
     *
     * @see org.argouml.uml.diagram.layout.LayoutedEdge#layout()
     */
    public abstract void layout();

    /**
     * @param h the horizontal gap
     */
    public static void setHGap(int h) { hGap = h; }

    /**
     * @param v the vertical gap
     */
    public static void setVGap(int v) { vGap = v; }

    /**
     * @return the horizontal gap
     */
    public static int getHGap() { return hGap; }

    /**
     * @return the vertical gap
     */
    public static int getVGap() { return vGap; }

    /**
     * @return Returns the destFigNode.
     */
    Fig getDestFigNode() {
        return destFigNode;
    }

    /**
     * @return Returns the sourceFigNode.
     */
    Fig getSourceFigNode() {
        return sourceFigNode;
    }

    /**
     * @return Returns the currentEdge.
     */
    protected FigEdge getCurrentEdge() {
        return currentEdge;
    }

    /**
     * @return Returns the underlyingFig.
     */
    protected FigPoly getUnderlyingFig() {
        return underlyingFig;
    }
}

