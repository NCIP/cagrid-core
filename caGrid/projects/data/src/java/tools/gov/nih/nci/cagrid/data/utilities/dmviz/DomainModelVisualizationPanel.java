package gov.nih.nci.cagrid.data.utilities.dmviz;

import gov.nih.nci.cagrid.graph.uml.UMLClassAssociation;
import gov.nih.nci.cagrid.graph.uml.UMLDiagram;
import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.common.UMLAttribute;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModelExposedUMLAssociationCollection;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModelExposedUMLClassCollection;
import gov.nih.nci.cagrid.metadata.dataservice.UMLAssociation;
import gov.nih.nci.cagrid.metadata.dataservice.UMLAssociationEdge;
import gov.nih.nci.cagrid.metadata.dataservice.UMLAssociationSourceUMLAssociationEdge;
import gov.nih.nci.cagrid.metadata.dataservice.UMLAssociationTargetUMLAssociationEdge;
import gov.nih.nci.cagrid.metadata.dataservice.UMLClass;
import gov.nih.nci.cagrid.metadata.dataservice.UMLClassReference;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.tigris.gef.event.GraphSelectionEvent;
import org.tigris.gef.event.GraphSelectionListener;

/** 
 *  DomainModelVisualizationPanel
 *  Visualizes a domain model
 * 
 * @author David Ervin
 * 
 * @created Mar 30, 2007 10:23:05 AM
 * @version $Id: DomainModelVisualizationPanel.java,v 1.7 2009-05-29 20:50:22 dervin Exp $ 
 */
public class DomainModelVisualizationPanel extends JPanel {

    private List<ModelSelectionListener> modelListeners;
    private Map<gov.nih.nci.cagrid.graph.uml.UMLClass, UMLClass> graphClassToUML;
    
    private UMLDiagram umlDiagram = null;
    private JLabel findClassLabel = null;
    private JTextField findClassTextField = null;
    private JButton findClassButton = null;
    private JPanel findClassPanel = null;  //  @jve:decl-index=0:visual-constraint="81,314"
    
    public DomainModelVisualizationPanel() {
        modelListeners = new LinkedList<ModelSelectionListener>();
        graphClassToUML = new HashMap<gov.nih.nci.cagrid.graph.uml.UMLClass, UMLClass>();
        initialize();
    }
    
    
    private void initialize() {
        setLayout(new GridBagLayout());
        this.setSize(new Dimension(500, 207));
        GridBagConstraints cons1 = new GridBagConstraints();
        cons1.gridx = 0;
        cons1.gridy = 0;
        cons1.fill = GridBagConstraints.BOTH;
        cons1.weightx = 1.0D;
        cons1.weighty = 1.0D;
        GridBagConstraints cons2 = new GridBagConstraints();
        cons2.gridx = 0;
        cons2.gridy = 1;
        cons2.fill = GridBagConstraints.HORIZONTAL;
        cons2.weightx = 1.0D;
        add(getUmlDiagram(), cons1);
        add(getFindClassPanel(), cons2);
    }
    
    
    public void addModelSelectionListener(ModelSelectionListener listener) {
        modelListeners.add(listener);
    }
    
    
    public boolean removeModelSelectionListener(ModelSelectionListener listener) {
        return modelListeners.remove(listener);
    }
    
    
    public void selectClass(String name) {
        Map<UMLClass, gov.nih.nci.cagrid.graph.uml.UMLClass> reverse = 
            new HashMap<UMLClass, gov.nih.nci.cagrid.graph.uml.UMLClass>();
        for (gov.nih.nci.cagrid.graph.uml.UMLClass gClass : graphClassToUML.keySet()) {
            reverse.put(graphClassToUML.get(gClass), gClass);
        }
        gov.nih.nci.cagrid.graph.uml.UMLClass graphClass = null;
        for (UMLClass clazz : reverse.keySet()) {
            if (clazz.getClassName().equals(name)) {
                graphClass = reverse.get(clazz);
            }
        }
        if (graphClass != null) {
            // find the center of the viewport
            Dimension viewSize = getUmlDiagram().getViewer().getSize();
            Dimension classSize = graphClass.getSize();
            Point classLocation = graphClass.getLocation();
            Point scrollLocation = new Point();
            scrollLocation.x = Math.max(0, 
                classLocation.x - (viewSize.width / 2) + (classSize.width / 2));
            scrollLocation.y = Math.max(0, 
                classLocation.y - (viewSize.height / 2) + (classSize.height / 2));
            // scroll to center the graph class
            getUmlDiagram().getViewer().setViewPosition(scrollLocation);
            getUmlDiagram().getViewer().select(graphClass);
            getUmlDiagram().getViewer().highlightClass(graphClass);
        } else {
            System.out.println("Class " + name + " not found");
        }
    }
    
    
    protected void fireClassSelected(UMLClass selected) {
        for (ModelSelectionListener listener : modelListeners) {
            listener.classSelected(selected);
        }
    }
    
    
    protected void fireAssociationSelected(UMLAssociation selected) {
        for (ModelSelectionListener listener : modelListeners) {
            listener.associationSelected(selected);
        }
    }
    
    
    public void setDomainModel(DomainModel model) {
        // this keeps UML class ids mapped to the classes in the graph 
        Map<String, gov.nih.nci.cagrid.graph.uml.UMLClass> idsToGraphClasses = 
            new HashMap<String, gov.nih.nci.cagrid.graph.uml.UMLClass>();
        
        graphClassToUML.clear();
        getUmlDiagram().clear();
        
        if (model == null) {
            return;
        }
        
        // verify the model contains classes
        DomainModelExposedUMLClassCollection exposedClassCollection = 
            model.getExposedUMLClassCollection();
        if (exposedClassCollection == null || exposedClassCollection.getUMLClass() == null
            || exposedClassCollection.getUMLClass().length == 0) {
            // no classes == nothing to do!
            return;
        }
        
        // put the classes into the graph
        UMLClass[] modelClasses = exposedClassCollection.getUMLClass();
        for (UMLClass currentModelClass : modelClasses) {
            String fqClassName = currentModelClass.getClassName();
            if (currentModelClass.getPackageName() != null && currentModelClass.getPackageName().length() != 0) {
                fqClassName = currentModelClass.getPackageName() + "." + fqClassName;
            }
            gov.nih.nci.cagrid.graph.uml.UMLClass graphClass = 
                new gov.nih.nci.cagrid.graph.uml.UMLClass(fqClassName);
            
            // add attributes to the graph class
            UMLAttribute[] attribs = currentModelClass.getUmlAttributeCollection().getUMLAttribute();
            if (attribs != null) {
                for (UMLAttribute currentAttrib : attribs) {
                    graphClass.addAttribute(currentAttrib.getDataTypeName(), currentAttrib.getName());
                }
            }
            
            // add the class to the model
            getUmlDiagram().addClass(graphClass);
            
            // keep a handle to the graph class by way of the model class' ID
            idsToGraphClasses.put(currentModelClass.getId(), graphClass);
            graphClassToUML.put(graphClass, currentModelClass);
        }
        
        // associations
        DomainModelExposedUMLAssociationCollection exposedAssociationCollection = 
            model.getExposedUMLAssociationCollection();
        if (exposedAssociationCollection != null 
            && exposedAssociationCollection.getUMLAssociation() != null) {
           UMLAssociation[] associations = exposedAssociationCollection.getUMLAssociation();
           for (UMLAssociation currentAssociation : associations) {
               String sourceId = null;
               String sourceRoleName = null;
               int sourceMinCardinality = -1;
               int sourceMaxCardinality = -1;
               if (currentAssociation.getSourceUMLAssociationEdge() != null 
                   && currentAssociation.getSourceUMLAssociationEdge().getUMLAssociationEdge() != null) {
                   UMLAssociationEdge sourceEdge = currentAssociation.getSourceUMLAssociationEdge().getUMLAssociationEdge();
                   sourceId = sourceEdge.getUMLClassReference().getRefid();
                   sourceRoleName = sourceEdge.getRoleName();
                   sourceMinCardinality = sourceEdge.getMinCardinality();
                   sourceMaxCardinality = sourceEdge.getMaxCardinality();
               }
               String targetId = null;
               String targetRoleName = null;
               int targetMinCardinality = -1;
               int targetMaxCardinality = -1;
               if (currentAssociation.getTargetUMLAssociationEdge() != null
                   && currentAssociation.getTargetUMLAssociationEdge().getUMLAssociationEdge() != null) {
                   UMLAssociationEdge targetEdge = currentAssociation.getTargetUMLAssociationEdge().getUMLAssociationEdge();
                   targetId = targetEdge.getUMLClassReference().getRefid();
                   targetRoleName = targetEdge.getRoleName();
                   targetMinCardinality = targetEdge.getMinCardinality();
                   targetMaxCardinality = targetEdge.getMaxCardinality();
               }
               gov.nih.nci.cagrid.graph.uml.UMLClass sourceClass 
                   = idsToGraphClasses.get(sourceId);
               gov.nih.nci.cagrid.graph.uml.UMLClass targetClass 
                   = idsToGraphClasses.get(targetId);
               String sourceCardinality = sourceMinCardinality + ".." + 
                   (sourceMaxCardinality == -1 ? "*" : String.valueOf(sourceMaxCardinality));
               String targetCardinality = targetMinCardinality + ".." +
                   (targetMaxCardinality == -1 ? "*" : String.valueOf(targetMaxCardinality));
               
               getUmlDiagram().addAssociation(sourceClass, targetClass,
                   sourceRoleName, sourceCardinality, targetRoleName, targetCardinality,
                   currentAssociation.isBidirectional());
           }
        }
        getUmlDiagram().refresh();
    }
    
    
    private UMLDiagram getUmlDiagram() {
        if (umlDiagram == null) {
            umlDiagram = new UMLDiagram();
            umlDiagram.getViewer().addGraphSelectionListener(new GraphSelectionListener() {
                public void selectionChanged(GraphSelectionEvent e) {
                    if (e.getSelections() != null && e.getSelections().size() != 0) {
                        Object selection = e.getSelections().get(0);
                        if (selection instanceof gov.nih.nci.cagrid.graph.uml.UMLClass) {
                            UMLClass selectedUmlClass = graphClassToUML.get(selection);
                            fireClassSelected(selectedUmlClass);
                        } else if (selection instanceof UMLClassAssociation) {
                            // get the endpoints of the graphical association
                            UMLClassAssociation graphAssociation = (UMLClassAssociation) selection;
                            gov.nih.nci.cagrid.graph.uml.UMLClass sourceGraphClass = 
                                (gov.nih.nci.cagrid.graph.uml.UMLClass) graphAssociation.getSourcePortFig();
                            gov.nih.nci.cagrid.graph.uml.UMLClass targetGraphClass = 
                                (gov.nih.nci.cagrid.graph.uml.UMLClass) graphAssociation.getDestPortFig();
                            
                            // get the UML classes
                            UMLClass sourceClass = graphClassToUML.get(sourceGraphClass);
                            String sourceRoleName = graphAssociation.sourceLabel.getText();
                            int[] sourceCardinality = getCardinalities(graphAssociation.sourceMultiplicity.getText());
                            
                            UMLClass targetClass = graphClassToUML.get(targetGraphClass);
                            String targetRoleName = graphAssociation.destinationLabel.getText();
                            int[] targetCardinality = getCardinalities(graphAssociation.destinationMultiplicity.getText());
                            
                            // create the source and target edges
                            UMLAssociationEdge sourceEdge = new UMLAssociationEdge();
                            sourceEdge.setUMLClassReference(new UMLClassReference(sourceClass.getId()));
                            sourceEdge.setRoleName(sourceRoleName);
                            sourceEdge.setMinCardinality(sourceCardinality[0]);
                            sourceEdge.setMaxCardinality(sourceCardinality[1]);
                            
                            UMLAssociationEdge targetEdge = new UMLAssociationEdge();
                            targetEdge.setUMLClassReference(new UMLClassReference(targetClass.getId()));
                            targetEdge.setRoleName(targetRoleName);
                            targetEdge.setMinCardinality(targetCardinality[0]);
                            targetEdge.setMaxCardinality(targetCardinality[1]);
                            
                            // create the association instance
                            UMLAssociation association = new UMLAssociation();
                            association.setSourceUMLAssociationEdge(new UMLAssociationSourceUMLAssociationEdge(sourceEdge));
                            association.setTargetUMLAssociationEdge(new UMLAssociationTargetUMLAssociationEdge(targetEdge));
                            association.setBidirectional(graphAssociation.bidirectional);
                            
                            // fire the event
                            fireAssociationSelected(association);
                        }
                    }
                }
            });
        }
        return umlDiagram;
    }
    
    
    private int[] getCardinalities(String cards) {
        int dotIndex = cards.indexOf("..");
        int min = Integer.valueOf(cards.substring(0, dotIndex)).intValue();
        String maxVal = cards.substring(dotIndex + "..".length());
        if (maxVal.equals("*")) {
            return new int[] {min, -1};
        }
        return new int[] {min, Integer.valueOf(maxVal).intValue()};
    }
    
    
    /**
     * This method initializes findClassLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getFindClassLabel() {
        if (findClassLabel == null) {
            findClassLabel = new JLabel();
            findClassLabel.setText("Find Class:");
        }
        return findClassLabel;
    }


    /**
     * This method initializes findClassTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getFindClassTextField() {
        if (findClassTextField == null) {
            findClassTextField = new JTextField();
        }
        return findClassTextField;
    }


    /**
     * This method initializes findClassButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getFindClassButton() {
        if (findClassButton == null) {
            findClassButton = new JButton();
            findClassButton.setText("Find");
            findClassButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    String className = getFindClassTextField().getText();
                    selectClass(className);
                }
            });
        }
        return findClassButton;
    }


    /**
     * This method initializes findClassPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getFindClassPanel() {
        if (findClassPanel == null) {
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 2;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.gridy = 0;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.weightx = 1.0;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.gridx = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.gridy = 0;
            findClassPanel = new JPanel();
            findClassPanel.setLayout(new GridBagLayout());
            findClassPanel.setSize(new Dimension(321, 46));
            findClassPanel.add(getFindClassLabel(), gridBagConstraints);
            findClassPanel.add(getFindClassTextField(), gridBagConstraints1);
            findClassPanel.add(getFindClassButton(), gridBagConstraints2);
        }
        return findClassPanel;
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setSize(500,500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        DomainModelVisualizationPanel panel = new DomainModelVisualizationPanel();
        frame.setContentPane(panel);
        // JFileChooser chooser = new JFileChooser();
        // chooser.showOpenDialog(frame);
        // File choice = chooser.getSelectedFile();
        File choice = new File("../data/caBIO-domainModel.xml");
        try {
            DomainModel model = MetadataUtils.deserializeDomainModel(new FileReader(choice));
            panel.setDomainModel(model);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        frame.setVisible(true);
    }
}  //  @jve:decl-index=0:visual-constraint="10,10"
