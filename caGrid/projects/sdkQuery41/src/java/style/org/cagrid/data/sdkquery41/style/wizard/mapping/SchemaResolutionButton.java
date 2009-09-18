package org.cagrid.data.sdkquery41.style.wizard.mapping;

import gov.nih.nci.cagrid.data.extension.ModelPackage;
import gov.nih.nci.cagrid.data.ui.SchemaResolutionDialog;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import org.cagrid.data.sdkquery41.style.wizard.config.SchemaMappingConfigStep;
import org.cagrid.grape.utils.CompositeErrorDialog;

public class SchemaResolutionButton extends JButton implements ActionListener {
    
    private ServiceInformation serviceInfo = null;
    private ModelPackage modelPack = null;
    private SchemaMappingConfigStep configuration = null;
    private SchemaMappingTable mappingTable = null;
    
    public SchemaResolutionButton(ServiceInformation serviceInfo, ModelPackage modelPack, 
        SchemaMappingConfigStep configuration, SchemaMappingTable mappingTable) {
        super();
        this.serviceInfo = serviceInfo;
        this.modelPack = modelPack;
        this.configuration = configuration;
        this.mappingTable = mappingTable;
        this.setText("Map Schema");
        addActionListener(this);
    }
    
    
    public void actionPerformed(ActionEvent e) {
        // use the schema resolution dialog to load an XSD
        NamespaceType[] namespaces = SchemaResolutionDialog.resolveSchemas(serviceInfo);
        if (namespaces != null && namespaces.length != 0) {
            NamespaceType selected = namespaces[0];
            // add the namespaces to the service
            for (NamespaceType ns : namespaces) {
                CommonTools.addNamespace(serviceInfo.getServiceDescriptor(), ns);
            }
            
            // set the namespace for the package
            try {
                configuration.setPackageNamespace(
                    modelPack.getPackageName(), selected.getNamespace());
            } catch (Exception ex) {
                ex.printStackTrace();
                CompositeErrorDialog.showErrorDialog(
                    "Error storing mapping", ex.getMessage(), ex);
            }
            
            // only the first namespace is the one selected; the rest are imports
            MappingCustomizationDialog.customizeElementMapping(selected, modelPack, configuration);
            
            // update the rendering of this mapping
            try {
                mappingTable.reloadCadsrInformation();
            } catch (Exception ex) {
                ex.printStackTrace();
                CompositeErrorDialog.showErrorDialog(
                    "Error reloading mapping information", ex.getMessage(), ex);
            }
        }
    }
}