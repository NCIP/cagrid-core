package gov.nih.nci.cagrid.introduce.extensions.metadata.editors.domainmodel;

import gov.nih.nci.cagrid.graph.uml.UMLDiagram;
import gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertyType;
import gov.nih.nci.cagrid.introduce.portal.extension.ResourcePropertyEditorPanel;
import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.common.UMLAttribute;
import gov.nih.nci.cagrid.metadata.common.UMLClass;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.metadata.dataservice.UMLAssociation;
import gov.nih.nci.cagrid.metadata.dataservice.UMLAssociationEdge;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * @author oster
 */
public class DomainModelViewer extends ResourcePropertyEditorPanel {
    private DomainModel domainModel = null;
    private UMLDiagram umlDiagram = null;
    private JPanel graphPanel = null;
    private JPanel infoPanel = null;
    private JLabel domainLabel = null;
    private JLabel projectDescLabel = null;


    public DomainModelViewer(ResourcePropertyType type, String doc, File schemaFile, File schemaDir) {
        super(type, doc, schemaFile, schemaDir);
        if (doc != null) {
            try {
                setDomainModel(MetadataUtils.deserializeDomainModel(new StringReader(doc)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        initialize();
    }


    private void initialize() {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridx = 0;
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.weightx = 1.0D;
        gridBagConstraints1.weighty = 1.0D;
        gridBagConstraints1.gridy = 1;

        this.setLayout(new GridBagLayout());
        this.add(getGraphPanel(), gridBagConstraints1);
        this.add(getInfoPanel(), gridBagConstraints);
    }


    /**
     * This method initializes infoPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getInfoPanel() {
        if (this.infoPanel == null) {
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints3.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints3.gridy = 0;
            gridBagConstraints3.weightx = 1;
            gridBagConstraints3.weighty = 1;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints4.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints4.gridy = 1;
            gridBagConstraints4.weightx = 1;
            gridBagConstraints4.weighty = 1;
            this.infoPanel = new JPanel();
            this.infoPanel.setLayout(new GridBagLayout());
            this.infoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                null, "Data Service Domain Model",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            this.infoPanel.add(getDomainLabel(), gridBagConstraints3);
            this.infoPanel.add(getDomainDescLabel(), gridBagConstraints4);
        }
        return this.infoPanel;
    }


    private JLabel getDomainLabel() {
        if (this.domainLabel == null) {
            this.domainLabel = new JLabel();
            this.domainLabel.setText("Project Name Version: ");
            this.domainLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        }

        return this.domainLabel;
    }


    private JLabel getDomainDescLabel() {
        if (this.projectDescLabel == null) {
            this.projectDescLabel = new JLabel();
            this.projectDescLabel.setText("Project Description");
            this.projectDescLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.ITALIC, 12));
        }

        return this.projectDescLabel;
    }


    /**
     * @return the domainModel
     */
    public DomainModel getDomainModel() {
        return this.domainModel;
    }


    /**
     * @param domainModel
     *            the domainModel to set
     */
    public void setDomainModel(DomainModel domainModel) {
        this.domainModel = domainModel;
        initializeUMLDiagram();
    }


    private void initializeUMLDiagram() {
        getUMLDiagram().clear();
        // add classes
        if (this.domainModel != null) {

            getDomainLabel().setText(
                this.domainModel.getProjectLongName() + "  version: " + this.domainModel.getProjectVersion());
            getDomainDescLabel().setText(this.domainModel.getProjectDescription());

            // class ID->UMLClass
            Map<String, gov.nih.nci.cagrid.graph.uml.UMLClass> classMap = 
                new HashMap<String, gov.nih.nci.cagrid.graph.uml.UMLClass>();
            if (this.domainModel.getExposedUMLClassCollection() != null
                && this.domainModel.getExposedUMLClassCollection().getUMLClass() != null) {
                UMLClass[] classArr = this.domainModel.getExposedUMLClassCollection().getUMLClass();
                for (UMLClass c : classArr) {
                    gov.nih.nci.cagrid.graph.uml.UMLClass diagramClass = 
                        new gov.nih.nci.cagrid.graph.uml.UMLClass(trimClassName(c.getClassName()));
                    if (c.getUmlAttributeCollection() != null) {
                        if (c.getUmlAttributeCollection().getUMLAttribute() != null) {
                            for (int j = 0; j < c.getUmlAttributeCollection().getUMLAttribute().length; j++) {
                                UMLAttribute attribute = c.getUmlAttributeCollection().getUMLAttribute()[j];
                                diagramClass.addAttribute(attribute.getDataTypeName(), attribute.getName());
                            }
                        }
                    }
                    diagramClass.setToolTip(c.getDescription());
                    classMap.put(c.getId(), diagramClass);
                    getUMLDiagram().addClass(diagramClass);
                }

                if (this.domainModel.getExposedUMLAssociationCollection() != null
                    && this.domainModel.getExposedUMLAssociationCollection().getUMLAssociation() != null) {

                    UMLAssociation[] assocArr = this.domainModel.getExposedUMLAssociationCollection()
                        .getUMLAssociation();
                    for (UMLAssociation assoc : assocArr) {
                        UMLAssociationEdge sourceEdge = assoc.getSourceUMLAssociationEdge().getUMLAssociationEdge();
                        UMLAssociationEdge targetEdge = assoc.getTargetUMLAssociationEdge().getUMLAssociationEdge();

                        gov.nih.nci.cagrid.graph.uml.UMLClass source = classMap.get(
                            sourceEdge.getUMLClassReference().getRefid());
                        gov.nih.nci.cagrid.graph.uml.UMLClass target = classMap.get(
                            targetEdge.getUMLClassReference().getRefid());

                        if (source == null || target == null) {
                            System.err.println("ERROR: can't process the association, as it " +
                                    "references an unexposed class... ignoring!");
                            System.err.println("Source ID:" + sourceEdge.getUMLClassReference().getRefid());
                            System.err.println("Target ID:" + targetEdge.getUMLClassReference().getRefid());
                        } else {
                            String sourceMultiplicity = sourceEdge.getMinCardinality() + ".." + 
                                (sourceEdge.getMaxCardinality() == -1 ? "*" : String.valueOf(sourceEdge.getMaxCardinality()));
                            String targetMultiplicity = targetEdge.getMinCardinality() + ".." + 
                                (targetEdge.getMaxCardinality() == -1 ? "*" : String.valueOf(targetEdge.getMaxCardinality()));
                            getUMLDiagram().addAssociation(
                                source, target, sourceEdge.getRoleName(), sourceMultiplicity,
                                targetEdge.getRoleName(), targetMultiplicity, assoc.isBidirectional());
                        }
                    }
                }
            }
        }
        getUMLDiagram().refresh();
    }


    private JPanel getGraphPanel() {
        if (this.graphPanel == null) {
            this.graphPanel = new JPanel();
            this.graphPanel.setLayout(new GridBagLayout());
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints1.weightx = 1.0D;
            gridBagConstraints1.weighty = 1.0D;
            gridBagConstraints1.gridy = 1;
            this.graphPanel.add(getUMLDiagram(), gridBagConstraints1);

        }
        return this.graphPanel;
    }


    private UMLDiagram getUMLDiagram() {
        if (this.umlDiagram == null) {
            this.umlDiagram = new UMLDiagram();

        }
        return this.umlDiagram;
    }


    public static String trimClassName(String name) {
        if (name == null) {
            return null;
        }

        int ind = name.lastIndexOf(".");
        if (ind >= 0 && ind < name.length() - 1) {
            return name.substring(ind + 1);
        } else {
            return name.trim();
        }

    }

    @Override
    public String getResultRPString() {
        return getRPString();
    }
    
    
    public static void main(String[] args) {
        JFrame f = new JFrame();
        DomainModelViewer viewer = new DomainModelViewer(null, null, null, null);

        try {
            JFileChooser fc = new JFileChooser(".");
            fc.showOpenDialog(f);

            DomainModel model = MetadataUtils.deserializeDomainModel(new FileReader(fc.getSelectedFile()));
            viewer.setDomainModel(model);

            f.getContentPane().add(viewer);
            f.pack();
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void validateResourceProperty() throws Exception {
        // TODO Auto-generated method stub
        
    }
}
