package gov.nih.nci.cagrid.introduce.portal.modification.discovery.globus;

import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.common.CommonTools;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.io.File;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.cagrid.grape.utils.ErrorDialog;


public class GlobusConfigurationPanel extends JPanel {

    public String currentNamespace = null;

    public File currentSchemaFile = null;

    protected File schemaDir;

    private JComboBox namespaceComboBox = null;

    private JLabel namespaceLabel = null;

    public String filterType = null;


    /**
     * This method initializes
     */
    public GlobusConfigurationPanel() {
        super();
        initialize();
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
        gridBagConstraints9.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints9.gridy = 0;
        gridBagConstraints9.insets = new java.awt.Insets(2, 2, 2, 2);
        gridBagConstraints9.gridx = 0;
        this.namespaceLabel = new JLabel();
        this.namespaceLabel.setText("Schema");
        GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
        gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints7.gridy = 0;
        gridBagConstraints7.weightx = 1.0;
        gridBagConstraints7.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints7.insets = new java.awt.Insets(2, 2, 2, 2);
        gridBagConstraints7.weighty = 1.0D;
        gridBagConstraints7.gridx = 1;
        this.setLayout(new GridBagLayout());
        this.add(getNamespaceComboBox(), gridBagConstraints7);
        this.add(this.namespaceLabel, gridBagConstraints9);
    }


    public void discoverFromGlobus() {
        List namespaces = IntroduceConstants.GLOBUS_NAMESPACES;

        getNamespaceComboBox().removeAllItems();
        for (int i = 0; i < namespaces.size(); i++) {
            getNamespaceComboBox().addItem(namespaces.get(i));
        }
    }


    /**
     * This method initializes jComboBox
     * 
     * @return javax.swing.JComboBox
     */
    public JComboBox getNamespaceComboBox() {
        if (this.namespaceComboBox == null) {
            this.namespaceComboBox = new JComboBox();
            this.namespaceComboBox.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        String schemaNamespace = (String) GlobusConfigurationPanel.this.namespaceComboBox
                            .getSelectedItem();
                        GlobusConfigurationPanel.this.currentNamespace = schemaNamespace;
                        try {
                            if (new File(CommonTools.getGlobusLocation()).exists()) {

                                File schemasDir = new File(CommonTools.getGlobusLocation() + File.separator + "share"
                                    + File.separator + "schema");
                                try {
                                    GlobusConfigurationPanel.this.currentSchemaFile = CommonTools.findSchema(
                                        schemaNamespace, schemasDir);

                                } catch (Exception ex) {
                                    ErrorDialog
                                        .showError("Globus Location seems to be wrong or corrupted:  Please check setting in the Preferences Menu!");
                                }

                            } else {
                                ErrorDialog
                                    .showError("Globus Location cannot be found:  Please check setting in the Preferences Menu!");
                            }
                        } catch (Exception ex) {
                            ErrorDialog
                                .showError("Globus Location cannot be found:  Please check setting in the Preferences Menu!");
                        }
                    }
                }
            });
        }
        return this.namespaceComboBox;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
