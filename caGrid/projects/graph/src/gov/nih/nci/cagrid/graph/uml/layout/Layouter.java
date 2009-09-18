package gov.nih.nci.cagrid.graph.uml.layout;

import java.awt.Dimension;


/**
 * Any layouter for any diagram type should implement this
 * interface.
 */
public interface Layouter {

    /**
     * Add another object to the diagram.
     *
     * @param obj represents the object to be part of the diagram.
     */
    void add(LayoutedObject obj);

    /**
     * Remove a object from the diagram.
     *
     * @param obj represents the object to be removed.
     */
    void remove(LayoutedObject obj);

    /**
     * Operation getObjects returns all the layouted objects
     * from this diagram.
     *
     * @return An array with the layouted objects of this diagram.
     */
    LayoutedObject [] getObjects();

    /**
     * Operation getObject returns one object from the diagram.
     *
     * @param index represents the index of this object.
     * @return the object
     */
    LayoutedObject getObject(int index);

    /**
     * This operation starts the actual layout process.
     */
    void layout();

    /**
     * Operation getMinimumDiagramSize returns the minimum
     * diagram size after the layout, so the diagram could
     * be resized to this size.
     *
     * @return the size/dimension
     */
    Dimension getMinimumDiagramSize();
}
