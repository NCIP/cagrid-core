package gov.nih.nci.cagrid.graph.uml;

import gov.nih.nci.cagrid.graph.uml.classdiagram.ClassdiagramAssociationEdge;
import gov.nih.nci.cagrid.graph.uml.classdiagram.ClassdiagramLayouter;
import gov.nih.nci.cagrid.graph.uml.classdiagram.ClassdiagramNode;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;

import org.tigris.gef.base.Diagram;
import org.tigris.gef.presentation.Fig;


public class UMLDiagram extends JLayeredPane {
    protected Diagram diagram;
    public UMLViewer viewer;
    protected UMLStatusBar statusBar;

    ClassdiagramLayouter layouter = new ClassdiagramLayouter();

    protected Vector classes = new Vector();
    protected Vector assocs = new Vector();

    public boolean inactiveState = true;


    public UMLViewer getViewer() {
        return this.viewer;
    }


    public UMLDiagram() {
        super();

        diagram = new Diagram();
        viewer = new UMLViewer(this);

        viewer.setBorder(BorderFactory.createLineBorder(Color.lightGray, 1));
        viewer.setBorder(BorderFactory.createEmptyBorder());

        this.statusBar = new UMLStatusBar();

        this.add(this.viewer);
        this.add(statusBar);

        this.addComponentListener(new UMLDiagramComponentListener());

        this.setPreferredSize(new Dimension(500, 500));

    }


    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        UMLDiagram diagram = new UMLDiagram();
        UMLClass foo = new UMLClass("Foo");
        UMLClass bar = new UMLClass("Bar");
        diagram.addClass(foo);
        diagram.addClass(bar);
        diagram.addAssociation(foo, bar, "src", "0..1", "target", "1..5", true);

        diagram.performLayout();
        diagram.refresh();

        f.getContentPane().add(diagram);
        f.pack();
        f.setVisible(true);
    }


    public void setStatusMessage(String msg) {
        this.statusBar.setMsg(msg);
    }


    public boolean addClass(UMLClass gc) {

        if (classes.contains(gc))
            return false;

        gc.refresh();

        gc.setVisible(false);
        this.classes.addElement(gc);
        this.diagram.add(gc);
        this.layouter.add(new ClassdiagramNode(gc));

        return true;

    }


    public boolean highlightClass(UMLClass c) {

        return this.viewer.highlightClass(c);

    }


    public void unHighlightAll() {
        this.viewer.unHighlightAll();
    }


    public boolean addAssociation(UMLClass gc1, UMLClass gc2, String sourceRoleName, String sourceMultiplicity,
        String targetRoleName, String targetMultiplicity, boolean bidirectional) {
        UMLClassAssociation edge = new UMLClassAssociation(sourceRoleName, sourceMultiplicity, targetRoleName,
            targetMultiplicity, bidirectional);

        edge.setSourceFigNode(gc1);
        edge.setSourcePortFig(gc1);

        edge.setDestFigNode(gc2);
        edge.setDestPortFig(gc2);

        edge.setVisible(false);

        this.diagram.add(edge);
        this.diagram.add(edge.sourceLabel);
        this.diagram.add(edge.destinationLabel);
        this.diagram.add(edge.sourceMultiplicity);
        this.diagram.add(edge.destinationMultiplicity);
        this.diagram.add(edge.sourceArrow);
        this.diagram.add(edge.destinationArrow);

        this.assocs.addElement(edge);

        this.layouter.add(new ClassdiagramAssociationEdge(edge));

        return true;
    }


    public void scrollToShowClass(String name) {
        for (int k = 0; k < this.classes.size(); k++) {
            UMLClass c = (UMLClass) classes.get(k);

            if (c.name.equals(name)) {
                this.viewer.getScrollPane().getViewport().setViewPosition(c.getLocation());
                this.highlightClass(c);
                return;
            }
        }

    }


    public void addFig(Fig f) {
        this.diagram.add(f);

    }


    public void classDoubleClicked(UMLClass c) {

    }


    public void zoom(int percent) {

    }


    public void refresh() {
        this.viewer.setDiagram(this.diagram);

        for (int k = 0; k < this.classes.size(); k++) {
            UMLClass gc = (UMLClass) this.classes.get(k);
            gc.setVisible(true);
            viewer.diagram.diagram.getLayer().bringToFront(gc);
        }

        performLayout();
        repositionLabelsAndArrowHeads();

        this.viewer.updateDrawingSizeToIncludeAllFigs();

        this.inactiveState = false;

    }


    public void clear() {
        this.inactiveState = true;
        this.layouter = new ClassdiagramLayouter();

        for (int k = 0; k < this.classes.size(); k++) {
            UMLClass gc = (UMLClass) this.classes.get(k);
            this.diagram.remove(gc);
        }

        for (int k = 0; k < this.assocs.size(); k++) {
            UMLClassAssociation edge = (UMLClassAssociation) this.assocs.get(k);
            this.diagram.remove(edge);
            this.diagram.remove(edge.sourceArrow);
            this.diagram.remove(edge.destinationArrow);
            this.diagram.remove(edge.sourceLabel);
            this.diagram.remove(edge.destinationLabel);
            this.diagram.remove(edge.sourceMultiplicity);
            this.diagram.remove(edge.destinationMultiplicity);
        }

        this.classes = new Vector();
        this.assocs = new Vector();
    }


    public void performLayout() {

        layouter.layout();

    }


    protected void repositionLabelsAndArrowHeads() {
        UMLClassAssociation edge = null;

        for (int c = 0; c < this.assocs.size(); c++) {
            edge = (UMLClassAssociation) this.assocs.elementAt(c);
            edge.repositionLabelsAndArrowHeads();
            this.repaint();
        }
    }

}

class UMLDiagramComponentListener extends ComponentAdapter {
    public void componentResized(ComponentEvent e) {
        UMLDiagram s = (UMLDiagram) e.getSource();

        // s.menubar.setBounds(0, 0, s.getWidth(), 25);
        // s.viewer.setBounds(0, 26, s.getWidth(), s.getHeight()-26-22);
        // s.statusBar.setBounds(0, s.getHeight()- 20, s.getWidth(), 20);
        // s.validate();

        s.viewer.setBounds(0, 0, s.getWidth(), s.getHeight());
        s.validate();
    }
}
