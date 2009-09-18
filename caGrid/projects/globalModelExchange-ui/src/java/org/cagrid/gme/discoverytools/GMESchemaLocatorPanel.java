package org.cagrid.gme.discoverytools;

import gov.nih.nci.cagrid.introduce.common.ConfigurationUtil;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gme.client.GlobalModelExchangeClient;
import org.cagrid.gme.domain.XMLSchema;
import org.cagrid.gme.domain.XMLSchemaNamespace;
import org.cagrid.grape.utils.CompositeErrorDialog;


public class GMESchemaLocatorPanel extends JPanel {

    private static final Log logger = LogFactory.getLog(GMESchemaLocatorPanel.class);

    public static String GME_URL = "GME_URL";

    private JPanel mainPanel = null;
    private JButton queryButton = null;
    protected File schemaDir;
    private JComboBox schemaComboBox = null;
    private JPanel schemaPanel = null;
    private JLabel namespaceLabel = null;

    private Object modificationMutex = null;

    protected XMLSchema currentSchema;


    /**
     * This method initializes
     */
    public GMESchemaLocatorPanel() {
        super();
        this.modificationMutex = new Object();
        initialize();
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weighty = 1.0D;
        gridBagConstraints.ipadx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0D;
        gridBagConstraints.gridx = 0;
        this.setLayout(new GridBagLayout());
        this.add(getSchemaPanel(), gridBagConstraints);
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    public JPanel getMainPanel() {
        if (this.mainPanel == null) {
            this.mainPanel = new JPanel();
            this.mainPanel.setLayout(new GridBagLayout());
        }
        return this.mainPanel;
    }


    public void discoverFromGME() {
        synchronized (this.modificationMutex) {
            try {
                GlobalModelExchangeClient gme = new GlobalModelExchangeClient(ConfigurationUtil
                    .getGlobalExtensionProperty(GME_URL).getValue());

                XMLSchemaNamespace[] schemaNamespaces = gme.getXMLSchemaNamespaces();
                List<XMLSchemaNamespace> namespacesList;
                if (schemaNamespaces != null) {
                    namespacesList = Arrays.asList(schemaNamespaces);
                } else {
                    namespacesList = new ArrayList<XMLSchemaNamespace>();
                }

                Collections.sort(namespacesList, new Comparator() {
                    public int compare(Object o1, Object o2) {
                        String s1 = o1.toString();
                        String s2 = o2.toString();
                        return s1.toLowerCase().compareTo(s2.toLowerCase());
                    }
                });

                makeCombosEnabled(false);

                getSchemaComboBox().removeAllItems();
                for (int i = 0; i < namespacesList.size(); i++) {
                    getSchemaComboBox().addItem(namespacesList.get(i));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                CompositeErrorDialog.showErrorDialog("Error contacting the GME", ex.getMessage(), ex);
            }
            makeCombosEnabled(true);
        };
    }


    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    public JButton getQueryButton() {
        if (this.queryButton == null) {
            this.queryButton = new JButton("Refresh");
            this.queryButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    discoverFromGME();
                }
            });
        }
        return this.queryButton;
    }


    /**
     * This method initializes jComboBox
     * 
     * @return javax.swing.JComboBox
     */
    public JComboBox getSchemaComboBox() {
        if (this.schemaComboBox == null) {
            this.schemaComboBox = new JComboBox();
            this.schemaComboBox.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        synchronized (GMESchemaLocatorPanel.this.modificationMutex) {

                            if (GMESchemaLocatorPanel.this.schemaComboBox.getSelectedItem() != null) {
                                try {
                                    GlobalModelExchangeClient gme = new GlobalModelExchangeClient(ConfigurationUtil
                                        .getGlobalExtensionProperty(GME_URL).getValue());
                                    XMLSchemaNamespace namespace = ((XMLSchemaNamespace) GMESchemaLocatorPanel.this.schemaComboBox
                                        .getSelectedItem());
                                    GMESchemaLocatorPanel.this.currentSchema = gme.getXMLSchema(namespace);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    CompositeErrorDialog.showErrorDialog("Error contacting GME", ex.getMessage(), ex);
                                }
                            }

                        }
                    }
                }
            });
        }
        return this.schemaComboBox;
    }


    /**
     * This method initializes schemaPanel
     * 
     * @return javax.swing.JPanel
     */
    public JPanel getSchemaPanel() {
        if (this.schemaPanel == null) {
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.anchor = GridBagConstraints.CENTER;
            gridBagConstraints2.gridwidth = 2;
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.gridy = 2;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            this.namespaceLabel = new JLabel();
            this.namespaceLabel.setText("Target Namespace:");
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints10.gridy = 1;
            gridBagConstraints10.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints10.gridx = 0;
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints8.gridy = 1;
            gridBagConstraints8.weightx = 1.0;
            gridBagConstraints8.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints8.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints8.weighty = 1.0D;
            gridBagConstraints8.gridx = 1;

            this.schemaPanel = new JPanel();
            this.schemaPanel.setLayout(new GridBagLayout());
            this.schemaPanel.add(getSchemaComboBox(), gridBagConstraints8);
            this.schemaPanel.add(this.namespaceLabel, gridBagConstraints10);
            this.schemaPanel.add(getQueryButton(), gridBagConstraints2);
        }
        return this.schemaPanel;
    }


    private synchronized void makeCombosEnabled(boolean enabled) {
        getSchemaComboBox().setEnabled(enabled);
    }


    public XMLSchemaNamespace getSelectedSchemaNamespace() {
        synchronized (this.modificationMutex) {
            return (XMLSchemaNamespace) getSchemaComboBox().getSelectedItem();
        }
    }

}
