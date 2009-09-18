package gov.nih.nci.cagrid.graph.uml.classdiagram;

import java.awt.Point;

import org.tigris.gef.presentation.FigEdge;
import org.tigris.gef.presentation.FigNode;

/**
 *
 * @author  mkl
 */
public class ClassdiagramAssociationEdge extends ClassdiagramEdge {

    /**
     * The constructor.
     *
     * @param edge the fig of the edge
     */
    public ClassdiagramAssociationEdge(FigEdge edge) {
        super(edge);
    }

    /**
     * @see org.argouml.uml.diagram.layout.LayoutedEdge#layout()
     */
    public void layout() {
        // TODO: Multiple associations between the same pair of elements
        // need to be special cased so that they don't overlap - tfm - 20060228

        // self associations are special cases. No need to let the maze
        // runner find the way.
        if (getDestFigNode() == getSourceFigNode()) {
            Point centerRight = getCenterRight((FigNode) getSourceFigNode());
            int yoffset = getSourceFigNode().getHeight() / 2;
            yoffset = java.lang.Math.min(30, yoffset);
            getUnderlyingFig().addPoint(centerRight);
            // move more right
            getUnderlyingFig().addPoint(centerRight.x + 30, centerRight.y);
            // move down
            getUnderlyingFig().addPoint(centerRight.x + 30,
                                        centerRight.y + yoffset);
            // move left
            getUnderlyingFig().addPoint(centerRight.x, centerRight.y + yoffset);

            getUnderlyingFig().setFilled(false);
            getUnderlyingFig().setSelfLoop(true);
            getCurrentEdge().setFig(getUnderlyingFig());
        }
        /*else {
            // brute force rectangular layout
            Point centerSource = getSourceFigNode().getCenter();
            Point centerDest   = getDestFigNode().getCenter();

            getUnderlyingFig().addPoint(centerSource.x, centerSource.y);
            getUnderlyingFig().addPoint(centerSource.x +
                                   (centerDest.x-centerSource.x)/2,
                                   centerSource.y);
            getUnderlyingFig().addPoint(centerSource.x +
                                   (centerDest.x-centerSource.x)/2,
                                   centerDest.y);
            getUnderlyingFig().addPoint(centerDest.x, centerDest.y);
            getUnderlyingFig().setFilled(false);
            getUnderlyingFig().setSelfLoop(false);
            getCurrentEdge().setFig(getUnderlyingFig());
        }*/
    }

    /**
     * Return a point which is just right of the center.
     *
     * @param fig The fig.
     * @return A Point.
     */
    private Point getCenterRight(FigNode fig) {
        Point center = fig.getCenter();
        return new Point(center.x + fig.getWidth() / 2, center.y);
    }
}







