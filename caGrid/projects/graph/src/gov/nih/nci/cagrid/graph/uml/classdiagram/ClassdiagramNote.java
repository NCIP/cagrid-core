package gov.nih.nci.cagrid.graph.uml.classdiagram;

import org.tigris.gef.presentation.FigNode;

/**
 * This class overrides some of the aspects of ClassdiagramNodes to simplify
 * the positioning of notes nearby the commented nodes.
 *
 * @author David Gunkel
 */
public class ClassdiagramNote extends ClassdiagramNode {
    /**
     * @param f the fig
     */
    public ClassdiagramNote(FigNode f) {
        super(f);
    }

    /**
     * @see org.argouml.uml.diagram.static_structure.layout.ClassdiagramNode#getTypeOrderNumer()
     */
    public int getTypeOrderNumer() {
        return first() == null
	    ? super.getTypeOrderNumer()
	    : first().getTypeOrderNumer();
    }
    /**
     * @see org.argouml.uml.diagram.static_structure.layout.ClassdiagramNode#calculateWeight()
     */
    public float calculateWeight() {
        setWeight(getWeight());
        return getWeight();
    }

    /**
     * @see org.argouml.uml.diagram.static_structure.layout.ClassdiagramNode#getRank()
     */
    public int getRank() {
        return first() == null ? 0 : first().getRank();
    }

    /**
     * @see org.argouml.uml.diagram.static_structure.layout.ClassdiagramNode#getWeight()
     */
    public float getWeight() {
        return first() == null ? 0 : first().getWeight() * 0.9999999f;
    }

    /**
     * @see org.argouml.uml.diagram.static_structure.layout.ClassdiagramNode#isStandalone()
     */
    public boolean isStandalone() {
        return first() == null ? true : first().isStandalone();
    }

    /**
     * Return the first node to which this note is attached to.
     *
     * @return A ClassdiagramNode.
     */
    private ClassdiagramNode first() {
        return getUplinks().isEmpty() ? null : (ClassdiagramNode) getUplinks()
            .firstElement();
    }
}
