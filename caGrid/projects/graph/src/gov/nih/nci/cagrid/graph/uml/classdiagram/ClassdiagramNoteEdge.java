package gov.nih.nci.cagrid.graph.uml.classdiagram;

import java.awt.Point;

import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigEdge;
import org.tigris.gef.presentation.FigPoly;

/**
 * This class represents note edges to enable an appropriate positioning
 * of notes.
 *
 * @author  David Gunkel
 */
public class ClassdiagramNoteEdge
    extends ClassdiagramEdge {
    /**
     * The constructor.
     *
     * @param edge the fig edge
     */
    public ClassdiagramNoteEdge(FigEdge edge) {
        super(edge);
    }

    /**
     * NoteEdges are drawn directly between the linked nodes, using vertically
     * centered points on the right- resp. left-hand side of the nodes.
     * @see org.argouml.uml.diagram.static_structure.layout.ClassdiagramEdge#layout()
     */
    public void layout() {
        // use left and right, up and down
        Fig fs = getSourceFigNode();
        Fig fd = getDestFigNode();
        if (fs.getLocation().x < fd.getLocation().x) {
            addPoints(fs, fd);
        } else {
            addPoints(fd, fs);
        }

        FigPoly fig = getUnderlyingFig();
        fig.setFilled(false);
        getCurrentEdge().setFig(fig);
    }

    /**
     * Add points to the underlying FigPoly.
     *
     * @param fs - source Fig of this edge
     * @param fd - destination Fig of this edge
     */
    private void addPoints(Fig fs, Fig fd) {
        FigPoly fig = getUnderlyingFig();
        Point p = fs.getLocation();
        p.translate(fs.getWidth(), fs.getHeight() / 2);
        fig.addPoint(p);
        p = fd.getLocation();
        p.translate(0, fd.getHeight() / 2);
        fig.addPoint(p);
    }
}

