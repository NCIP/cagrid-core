package gov.nih.nci.cagrid.graph.uml.layout;

/**
 * This is a layouted edge in a diagram.
 */
public interface LayoutedEdge extends LayoutedObject {

    /**
     * Abstract method to layout the edge.
     */
    public void layout();
}
