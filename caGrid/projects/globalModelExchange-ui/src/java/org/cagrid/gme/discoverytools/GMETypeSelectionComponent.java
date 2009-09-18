package org.cagrid.gme.discoverytools;

import gov.nih.nci.cagrid.introduce.beans.extension.DiscoveryExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespacesType;
import gov.nih.nci.cagrid.introduce.common.ConfigurationUtil;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import org.cagrid.gme.domain.XMLSchemaNamespace;


@SuppressWarnings("serial")
public class GMETypeSelectionComponent extends GMETypeSelectionComponentBase {

    private GMESchemaLocatorPanel gmePanel = null;

    public static String GME_URL = "GME_URL";


    public GMETypeSelectionComponent(DiscoveryExtensionDescriptionType descriptor, NamespacesType types) {
        super(descriptor, types);
        initialize();
        this.getGmePanel().discoverFromGME();
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
        gridBagConstraints4.insets = new java.awt.Insets(0, 0, 0, 0);
        gridBagConstraints4.gridy = 0;
        gridBagConstraints4.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints4.gridwidth = 1;
        gridBagConstraints4.weightx = 1.0D;
        gridBagConstraints4.weighty = 1.0D;
        gridBagConstraints4.gridx = 0;
        this.setLayout(new GridBagLayout());
        this.add(getGmePanel(), gridBagConstraints4);
    }


    protected String getGMEURL() throws Exception {
        return ConfigurationUtil.getGlobalExtensionProperty(GMETypeSelectionComponent.GME_URL).getValue();
    }


    /**
     * This method initializes gmePanel
     * 
     * @return javax.swing.JPanel
     */
    GMESchemaLocatorPanel getGmePanel() {
        if (this.gmePanel == null) {
            this.gmePanel = new GMESchemaLocatorPanel();
        }
        return this.gmePanel;
    }


    @Override
    protected XMLSchemaNamespace getCurrentSchemaNamespace() {
        return getGmePanel().getSelectedSchemaNamespace();
    }
}
