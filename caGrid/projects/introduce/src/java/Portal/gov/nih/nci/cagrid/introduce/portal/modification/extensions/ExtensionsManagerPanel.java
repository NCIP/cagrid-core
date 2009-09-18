package gov.nih.nci.cagrid.introduce.portal.modification.extensions;

import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;
import gov.nih.nci.cagrid.introduce.portal.common.IntroduceLookAndFeel;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.cagrid.grape.GridApplication;


public class ExtensionsManagerPanel extends JPanel {

    private JPanel extSelectionPanel = null;

    private JComboBox serviceStyleSeletor = null;

    private JButton removeExtensionButton = null;

    private JButton addExtensionButton = null;

    private ExtensionsTable extensionsTable = null;

    private JPanel extensionsTablePanel = null;

    private JScrollPane extensionsScrollPane = null;

    private JLabel upExtensionLabel = null;

    private JLabel downExtensionLabel = null;

    private ServiceInformation info;


    /**
     * This method initializes
     */
    public ExtensionsManagerPanel(ServiceInformation info) {
        super();
        this.info = info;
        initialize();
    }


    public void reInitialize(ServiceInformation reloadInfo) {
        this.info = reloadInfo;
        for (int i = getExtensionsTable().getRowCount(); i > 0; i--) {
            getExtensionsTable().removeRow(i - 1);
        }
        if (reloadInfo.getExtensions() != null && reloadInfo.getExtensions().getExtension() != null) {
            for (int i = reloadInfo.getExtensions().getExtension().length - 1; i >= 0; i--) {
                ExtensionType ext = reloadInfo.getExtensions().getExtension(i);
                ServiceExtensionDescriptionType desc = ExtensionsLoader.getInstance()
                    .getServiceExtension(ext.getName());
                if (desc != null) {
                    getExtensionsTable().addRow(desc.getDisplayName());
                }
            }
        }
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.fill = GridBagConstraints.BOTH;
        gridBagConstraints3.weightx = 1.0D;
        gridBagConstraints3.weighty = 0.0D;
        gridBagConstraints3.gridx = 0;
        gridBagConstraints3.gridy = 0;
        gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.fill = GridBagConstraints.BOTH;
        gridBagConstraints2.weightx = 1.0D;
        gridBagConstraints2.weighty = 1.0D;
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 1;
        gridBagConstraints2.gridheight = 2;
        gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
        this.setLayout(new GridBagLayout());
        this.setSize(new Dimension(631, 289));
        this.add(getExtSelectionPanel(), gridBagConstraints3);
        this.add(getExtensionsTablePanel(), gridBagConstraints2);

    }


    /**
     * This method initializes extSelectionPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getExtSelectionPanel() {
        if (extSelectionPanel == null) {
            GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
            gridBagConstraints22.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints22.gridy = 0;
            gridBagConstraints22.gridx = 1;
            GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
            gridBagConstraints16.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints16.gridy = 0;
            gridBagConstraints16.gridx = 2;
            GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
            gridBagConstraints15.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints15.gridx = 0;
            gridBagConstraints15.gridy = 0;
            gridBagConstraints15.weightx = 1.0;
            gridBagConstraints15.insets = new Insets(2, 2, 2, 2);
            extSelectionPanel = new JPanel();
            extSelectionPanel.setLayout(new GridBagLayout());
            extSelectionPanel.add(getServiceStyleSeletor(), gridBagConstraints15);
            extSelectionPanel.add(getRemoveExtensionButton(), gridBagConstraints16);
            extSelectionPanel.add(getAddExtensionButton(), gridBagConstraints22);
        }
        return extSelectionPanel;
    }


    /**
     * This method initializes serviceStyleSeletor
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getServiceStyleSeletor() {
        if (serviceStyleSeletor == null) {
            serviceStyleSeletor = new JComboBox();
            serviceStyleSeletor.addItem("NONE");

            List extensionDescriptors = ExtensionsLoader.getInstance().getServiceExtensions();
            for (int i = 0; i < extensionDescriptors.size(); i++) {
                ServiceExtensionDescriptionType ex = (ServiceExtensionDescriptionType) extensionDescriptors.get(i);
                serviceStyleSeletor.addItem(ex.getDisplayName());
            }
        }
        return serviceStyleSeletor;
    }


    /**
     * This method initializes removeExtensionButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getRemoveExtensionButton() {
        if (removeExtensionButton == null) {
            removeExtensionButton = new JButton();
            removeExtensionButton.setEnabled(false);
            removeExtensionButton.setIcon(IntroduceLookAndFeel.getSubtractIcon());
            removeExtensionButton.setText("Remove");
            removeExtensionButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        getExtensionsTable().removeSelectedRow();
                    } catch (Exception e1) {
                    }
                    List<String> extensions = getExtensionsTable().getExtensionNamesAsList();
                    String extS = "";
                    for (int i = 0; i < extensions.size(); i++) {
                        extS += extensions.get(i);
                        if (i < extensions.size() - 1) {
                            extS += ",";
                        }
                    }
                    info.getIntroduceServiceProperties().setProperty(IntroduceConstants.INTRODUCE_SKELETON_EXTENSIONS,
                        extS);
                    JOptionPane.showMessageDialog(GridApplication.getContext().getApplication(), "A save should be performed after removing an extension in order to deactivate the extension");
                }
            });
        }
        return removeExtensionButton;
    }


    /**
     * This method initializes addExtensionButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddExtensionButton() {
        if (addExtensionButton == null) {
            addExtensionButton = new JButton();
            addExtensionButton.setIcon(PortalLookAndFeel.getAddIcon());
            addExtensionButton.setText("Add");
            addExtensionButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (!((String) getServiceStyleSeletor().getSelectedItem()).equals("NONE")) {
                        getExtensionsTable().addRow((String) getServiceStyleSeletor().getSelectedItem());
                        List<String> extensions = getExtensionsTable().getExtensionNamesAsList();
                        String extS = "";
                        for (int i = 0; i < extensions.size(); i++) {
                            extS += extensions.get(i);
                            if (i < extensions.size() - 1) {
                                extS += ",";
                            }
                        }
                        info.getIntroduceServiceProperties().setProperty(
                            IntroduceConstants.INTRODUCE_SKELETON_EXTENSIONS, extS);
                        JOptionPane.showMessageDialog(GridApplication.getContext().getApplication(), "After adding a new extension a save should be perfomred in order for the extension to be active.");
                    }
                }
            });
        }
        return addExtensionButton;
    }


    /**
     * This method initializes extensionsTable
     * 
     * @return gov.nih.nci.cagrid.introduce.portal.creation.ExtensionsTable
     */
    private ExtensionsTable getExtensionsTable() {
        if (extensionsTable == null) {
            extensionsTable = new ExtensionsTable();

            if (info.getExtensions() != null && info.getExtensions().getExtension() != null) {
                for (int i = info.getExtensions().getExtension().length - 1; i >= 0; i--) {
                    ExtensionType ext = info.getExtensions().getExtension(i);
                    ServiceExtensionDescriptionType desc = ExtensionsLoader.getInstance().getServiceExtension(
                        ext.getName());
                    if (desc != null) {
                        extensionsTable.addRow(desc.getDisplayName());
                    }
                }
            }

            extensionsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    int row = extensionsTable.getSelectedRow();
                    if (row < 0 || row >= extensionsTable.getRowCount()) {
                        getRemoveExtensionButton().setEnabled(false);
                    } else {
                        try {
                            ServiceExtensionDescriptionType etype = ExtensionsLoader.getInstance()
                                .getServiceExtensionByDisplayName(extensionsTable.getSelectedRowData());
                            if (etype.getServiceExtensionRemover() != null) {
                                getRemoveExtensionButton().setEnabled(true);
                            } else {
                                getRemoveExtensionButton().setEnabled(false);
                            }
                        } catch (Exception e1) {
                            getRemoveExtensionButton().setEnabled(false);
                        }
                    }
                }

            });
        }
        return extensionsTable;
    }


    /**
     * This method initializes extensionsTablePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getExtensionsTablePanel() {
        if (extensionsTablePanel == null) {
            GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
            gridBagConstraints14.anchor = GridBagConstraints.NORTHWEST;
            gridBagConstraints14.gridy = 1;
            gridBagConstraints14.gridx = 1;
            downExtensionLabel = new JLabel();
            downExtensionLabel.setToolTipText("moves the selected extension down "
                + "in the list so that it will be executed after the preceding extensions");
            downExtensionLabel.setIcon(IntroduceLookAndFeel.getDownIcon());
            downExtensionLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    try {
                        getExtensionsTable().moveSelectedRowDown();
                    } catch (Exception e1) {

                    }
                    List<String> extensions = getExtensionsTable().getExtensionNamesAsList();
                    String extS = "";
                    for (int i = 0; i < extensions.size(); i++) {
                        extS += extensions.get(i);
                        if (i < extensions.size() - 1) {
                            extS += ",";
                        }
                    }
                    info.getIntroduceServiceProperties().setProperty(IntroduceConstants.INTRODUCE_SKELETON_EXTENSIONS,
                        extS);
                }
            });
            GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
            gridBagConstraints21.anchor = GridBagConstraints.SOUTHWEST;
            gridBagConstraints21.gridx = 1;
            gridBagConstraints21.gridy = 0;
            gridBagConstraints21.fill = GridBagConstraints.NONE;
            upExtensionLabel = new JLabel();
            upExtensionLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    try {
                        getExtensionsTable().moveSelectedRowUp();
                    } catch (Exception e1) {

                    }
                    List<String> extensions = getExtensionsTable().getExtensionNamesAsList();
                    String extS = "";
                    for (int i = 0; i < extensions.size(); i++) {
                        extS += extensions.get(i);
                        if (i < extensions.size() - 1) {
                            extS += ",";
                        }
                    }
                    info.getIntroduceServiceProperties().setProperty(IntroduceConstants.INTRODUCE_SKELETON_EXTENSIONS,
                        extS);
                }
            });
            upExtensionLabel.setToolTipText("moves the selected extension "
                + "higher in the list so that it will be executed before the following extensions");
            upExtensionLabel.setIcon(IntroduceLookAndFeel.getUpIcon());
            GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
            gridBagConstraints18.fill = GridBagConstraints.BOTH;
            gridBagConstraints18.gridx = 0;
            gridBagConstraints18.gridy = 0;
            gridBagConstraints18.weightx = 1.0;
            gridBagConstraints18.gridheight = 2;
            gridBagConstraints18.weighty = 1.0;
            extensionsTablePanel = new JPanel();
            extensionsTablePanel.setLayout(new GridBagLayout());
            extensionsTablePanel.add(getExtensionsScrollPane(), gridBagConstraints18);
            extensionsTablePanel.add(upExtensionLabel, gridBagConstraints21);
            extensionsTablePanel.add(downExtensionLabel, gridBagConstraints14);
        }
        return extensionsTablePanel;
    }


    /**
     * This method initializes extensionsScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getExtensionsScrollPane() {
        if (extensionsScrollPane == null) {
            extensionsScrollPane = new JScrollPane();
            extensionsScrollPane.setViewportView(getExtensionsTable());
        }
        return extensionsScrollPane;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
