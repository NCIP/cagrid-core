package gov.nih.nci.cagrid.graph.uml.layout;

import java.awt.Dimension;


/**
 * This interface is for container in a layouted diagram. They are
 * intended to hold other object like nodes or even other containers.
 * An example are nested packages in classdiagrams.
 */
public interface LayoutedContainer {

    /**
     * Add an object to this container.
     *
     * @param obj represents the object to add to this container.
     */
    void add(LayoutedObject obj);

    /**
     * Remove an object from this container.
     *
     * @param obj represents the object to be removed.
     */
    void remove(LayoutedObject obj);

    /**
     * Operation getContent returns all the objects from
     * this container.
     *
     * @return All the objects from this container.
     */
    LayoutedObject [] getContent();

    /**
     * Resize this container, so it fits the layouted objects within itself.
     *
     * @param newSize represents The new size of this container.
     */
    void resize(Dimension newSize);
}
