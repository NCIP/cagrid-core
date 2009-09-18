package gov.nih.nci.cagrid.data.style.cacore31.wizard;

import gov.nih.nci.cagrid.common.JarUtilities;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.portal.DocumentChangeAdapter;
import gov.nih.nci.cagrid.common.portal.validation.IconFeedbackPanel;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.ExtensionDataUtils;
import gov.nih.nci.cagrid.data.common.CastorMappingUtil;
import gov.nih.nci.cagrid.data.extension.AdditionalLibraries;
import gov.nih.nci.cagrid.data.extension.Data;
import gov.nih.nci.cagrid.data.ui.wizard.AbstractWizardPanel;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.FileFilters;
import gov.nih.nci.cagrid.introduce.common.ResourceManager;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;

import org.cagrid.grape.GridApplication;
import org.cagrid.grape.utils.CompositeErrorDialog;

import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.util.ValidationUtils;
import com.jgoodies.validation.view.ValidationComponentUtils;

/** 
 *  SDKClientSelectionPanel
 *  Panel to select the SDK system's client libraries 
 *  and mode to use local or remote API
 * 
 * @author David Ervin
 * 
 * @created Jun 4, 2007 1:45:08 PM
 * @version $Id: SDKClientSelectionPanel.java,v 1.2 2009-01-29 20:14:17 dervin Exp $ 
 */
public class SDKClientSelectionPanel extends AbstractWizardPanel {
    // keys for validation components
    public static final String KEY_CLIENT_LIB_DIR = "Client library directory";
    
    public static final String[] LOCAL_CLIENT_REQUIRED_FILES = new String[] {
        "DAOConfig.xml", "roleLookup.properties", "SDKSpringBeanConfig.xml"
    };
    
    private JLabel qpJarLabel = null;
    private JLabel clientLibDirLabel = null;
    private JTextField qpJarTextField = null;
    private JTextField clientLibDirTextField = null;
    private JLabel dependsLabel = null;
    private JList dependsList = null;
    private JScrollPane dependsScrollPane = null;
    private JButton clientLibBrowseButton = null;
    private JButton addDependButton = null;
    private JButton removeDependButton = null;
    private IconFeedbackPanel validationPanel = null;
    
    private ValidationResultModel validationModel;
    private DocumentChangeAdapter documentChangeListener;
    
    public SDKClientSelectionPanel(
        ServiceExtensionDescriptionType extensionDescription, ServiceInformation info) {
        super(extensionDescription, info);
        this.validationModel = new DefaultValidationResultModel();
        this.documentChangeListener = new DocumentChangeAdapter() {
            public void documentEdited(DocumentEvent e) {
                validateInput();
            }
        };
        initialize();
    }
    
    
    private void initialize() {
        this.setLayout(new GridLayout());
        this.add(getValidationPanel());
        // set up for validation
        configureValidation();
    }
    
    
    private IconFeedbackPanel getValidationPanel() {
        if (validationPanel == null) {
            JPanel holder = new JPanel();
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.gridx = 2;
            gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints7.anchor = GridBagConstraints.NORTH;
            gridBagConstraints7.gridy = 4;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.gridx = 2;
            gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints6.gridy = 3;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.fill = GridBagConstraints.BOTH;
            gridBagConstraints5.gridy = 3;
            gridBagConstraints5.weightx = 1.0;
            gridBagConstraints5.weighty = 1.0;
            gridBagConstraints5.gridheight = 2;
            gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints5.gridx = 1;
            GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
            gridBagConstraints31.gridx = 2;
            gridBagConstraints31.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints31.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints31.gridy = 1;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.gridy = 1;
            gridBagConstraints4.weightx = 1.0;
            gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints4.gridx = 1;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridy = 0;
            gridBagConstraints3.weightx = 1.0;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.gridx = 1;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.gridheight = 2;
            gridBagConstraints2.anchor = GridBagConstraints.NORTH;
            gridBagConstraints2.gridy = 3;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.gridy = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.gridy = 0;
            holder.setLayout(new GridBagLayout());
            holder.add(getQpJarLabel(), gridBagConstraints);
            holder.add(getClientLibDirLabel(), gridBagConstraints1);
            holder.add(getDependsLabel(), gridBagConstraints2);
            holder.add(getQpJarTextField(), gridBagConstraints3);
            holder.add(getClientLibDirTextField(), gridBagConstraints4);
            holder.add(getClientLibBrowseButton(), gridBagConstraints31);
            holder.add(getDependsScrollPane(), gridBagConstraints5);
            holder.add(getAddDependButton(), gridBagConstraints6);
            holder.add(getRemoveDependButton(), gridBagConstraints7);
            validationPanel = new IconFeedbackPanel(validationModel, holder);
        }
        return validationPanel;
    }


    public String getPanelShortName() {
        return "Client Selection";
    }


    public String getPanelTitle() {
        return "Client jar and dependency selection";
    }
    
    
    private File getSdk32QPLib() {
        File libDir = new File(SDK31InitializationPanel.SDK_31_LIB_DIR);
        File[] jars = libDir.listFiles(new FileFilters.JarFileFilter());
        if (jars.length != 1) {
            StringBuffer detail = new StringBuffer();
            detail.append("Expected to find a single jar file in the directory\n");
            detail.append(libDir.getAbsolutePath()).append("\n");
            detail.append("Found the following libs instead:\n");
            for (File f : jars) {
                detail.append("\t").append(f.getName()).append("\n");
            }
            CompositeErrorDialog.showErrorDialog("Error locating SDK Query 3.1 library", detail.toString());
        }
        return jars[0];
    }


    public void update() {
        // -- configure the UI -- //
        // verify the sdk query library has been copied into the service
        String sdkQueryLibName = getSdk32QPLib().getName();
        File sdkQueryLib = new File(getServiceInformation().getBaseDirectory().getAbsolutePath() 
            + File.separator + "lib" + File.separator + sdkQueryLibName);
        
        if (sdkQueryLib.exists()) {
            getQpJarTextField().setText(sdkQueryLib.getName());
        } else {
            getQpJarTextField().setText("ERROR: LIBRARY NOT FOUND");
        }

        // any 'additional libs' added previously should show up
        try {
            Vector<String> dependJars = getDependJars();
            getDependsList().setListData(dependJars);
        } catch (Exception ex) {
            ex.printStackTrace();
            CompositeErrorDialog.showErrorDialog("Error updating UI", ex.getMessage(), ex);
        }
        
        // update the availability of the next button
        updateNextEnabledState();
    }


    /**
     * This method initializes qpJarLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getQpJarLabel() {
        if (qpJarLabel == null) {
            qpJarLabel = new JLabel();
            qpJarLabel.setText("Query Processor Jar:");
        }
        return qpJarLabel;
    }


    /**
     * This method initializes clientLibDirLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getClientLibDirLabel() {
        if (clientLibDirLabel == null) {
            clientLibDirLabel = new JLabel();
            clientLibDirLabel.setText("Client Lib Directory:");
        }
        return clientLibDirLabel;
    }


    /**
     * This method initializes qpJarTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getQpJarTextField() {
        if (qpJarTextField == null) {
            qpJarTextField = new JTextField();
            qpJarTextField.setToolTipText("The JAR file containing the query processor");
            qpJarTextField.setEditable(false);
        }
        return qpJarTextField;
    }


    /**
     * This method initializes clientLibDirTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getClientLibDirTextField() {
        if (clientLibDirTextField == null) {
            clientLibDirTextField = new JTextField();
            clientLibDirTextField.setToolTipText(
                "The directory containing the client libraries for a caCORE SDK data source");
            clientLibDirTextField.setEditable(false);
            clientLibDirTextField.getDocument().addDocumentListener(documentChangeListener);
        }
        return clientLibDirTextField;
    }


    /**
     * This method initializes dependsLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getDependsLabel() {
        if (dependsLabel == null) {
            dependsLabel = new JLabel();
            dependsLabel.setText("Dependencies:");
        }
        return dependsLabel;
    }


    /**
     * This method initializes dependsList	
     * 	
     * @return javax.swing.JList	
     */
    private JList getDependsList() {
        if (dependsList == null) {
            dependsList = new JList();
        }
        return dependsList;
    }


    /**
     * This method initializes dependsScrollPane	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getDependsScrollPane() {
        if (dependsScrollPane == null) {
            dependsScrollPane = new JScrollPane();
            dependsScrollPane.setViewportView(getDependsList());
            dependsScrollPane.setVerticalScrollBarPolicy(
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        }
        return dependsScrollPane;
    }


    /**
     * This method initializes clientLibBrowseButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getClientLibBrowseButton() {
        if (clientLibBrowseButton == null) {
            clientLibBrowseButton = new JButton();
            clientLibBrowseButton.setText("Browse");
            clientLibBrowseButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    selectClientLibDir();
                    updateNextEnabledState();
                    validateInput();
                }
            });
        }
        return clientLibBrowseButton;
    }


    /**
     * This method initializes addDependButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getAddDependButton() {
        if (addDependButton == null) {
            addDependButton = new JButton();
            addDependButton.setText("Add");
            addDependButton.setToolTipText(
                "Add additional libraries which the data service will require");
            addDependButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    addDependJars();
                }
            });
        }
        return addDependButton;
    }


    /**
     * This method initializes removeDependButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getRemoveDependButton() {
        if (removeDependButton == null) {
            removeDependButton = new JButton();
            removeDependButton.setText("Remove");
            removeDependButton.setToolTipText("Remove the selected libraries");
            removeDependButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    removeDependJars();
                }
            });
        }
        return removeDependButton;
    }
    
    
    // -------
    // helpers
    // -------
    
    
    private void updateNextEnabledState() {
        setNextEnabled(false);
        // verify the client jar exists and is valid
        File clientJarFile = locateClientJarInDir(getClientLibDirTextField().getText());
        try {
            if (clientJarFile != null && clientJarFile.exists() 
                && isValidClientJar(clientJarFile)) {
                setNextEnabled(true);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    
    private void selectClientLibDir() {
        try {
            String clientLibDir = ResourceManager.promptDir(null);
            if (clientLibDir != null) {
                // locate the client jar file
                final File clientJarFile = locateClientJarInDir(clientLibDir);
                if (clientJarFile != null && isValidClientJar(clientJarFile)) {
                    if (getClientLibDirTextField().getText().length() != 0) {
                        // delete the old client jar
                        File oldClientJarFile = new File(getServiceInformation().getBaseDirectory().getAbsolutePath() 
                            + File.separator + "lib" + File.separator + getClientLibDirTextField().getText());
                        oldClientJarFile.delete();                                
                    }
                    // copy in the client jar
                    File copyOfClient = new File(getServiceInformation().getBaseDirectory().getAbsolutePath() 
                        + File.separator + "lib" + File.separator + clientJarFile.getName());
                    Utils.copyFile(clientJarFile, copyOfClient);

                    // copy the castor mapping file
                    JarFile jar = new JarFile(copyOfClient);
                    StringBuffer mappingFile = JarUtilities.getFileContents(
                        jar, DataServiceConstants.CACORE_CASTOR_MAPPING_FILE);
                    jar.close();
                    // copy the mapping file to the service's source dir + base package name
                    String mappingOut = CastorMappingUtil.getCustomCastorMappingFileName(getServiceInformation());
                    Utils.stringBufferToFile(mappingFile, mappingOut);

                    // get a list of the rest of the jars in the client lib dir
                    // and set them as jars the client depends on
                    File[] extraLibraries = new File(clientLibDir).listFiles(new FileFilter() {
                        public boolean accept(File pathname) {
                            String name = pathname.getName();
                            return name.toLowerCase().endsWith(".jar") 
                            && !pathname.getName().equals(clientJarFile.getName());
                        }
                    });

                    // copy them into the service's lib dir
                    for (File lib : extraLibraries) {
                        File libOut = new File(getServiceInformation().getBaseDirectory().getAbsolutePath() 
                            + File.separator + "lib" + File.separator + lib.getName());
                        Utils.copyFile(lib, libOut);
                    }
                    
                    // set the directory name in the UI
                    getClientLibDirTextField().setText(clientLibDir);
                    
                    // store the library information
                    // storeLibrariesInExtensionData();
                } else {
                    String[] message = {
                        "The selected directory does not appear to contain",
                        "a valid client jar file for the currently",
                        "selected version of the caCORE API.",
                        "Please choose a valid library directory file."
                    };
                    GridApplication.getContext().showMessage(message);
                    ValidationResult result = new ValidationResult();
                    String packedMessage = "";
                    for (String m : message) {
                        packedMessage += m + "\n";
                    }
                    result.add(new SimpleValidationMessage(packedMessage, Severity.ERROR, KEY_CLIENT_LIB_DIR));
                    validationModel.setResult(result);
                    updateComponentTreeSeverity();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            CompositeErrorDialog.showErrorDialog("Error in selection of client lib dir", 
                ex.getMessage(), ex);
        }
    }
    
    
    private File locateClientJarInDir(String dirName) {
        File libDir = new File(dirName);
        File[] jars = libDir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                String name = pathname.getName().toLowerCase();
                if (name.endsWith(".jar")) {
                    return (name.startsWith("client") || name.endsWith("-client.jar") 
                        || name.endsWith("-thickclient.jar")) && !name.contains("jboss");
                }
                return false;
            }
        });
        if (jars != null && jars.length == 1) {
            return jars[0];
        }
        return null;
    }
    
    
    private boolean isValidClientJar(File jarFile) throws IOException {
        JarFile jar = new JarFile(jarFile);
        // verify the file is a client.jar with a castor mapping file
        JarEntry mappingEntry = jar.getJarEntry(DataServiceConstants.CACORE_CASTOR_MAPPING_FILE);
        // all versions, local and remote client jars need this xml mapping file
        if (mappingEntry == null) {
            jar.close();
            return false;
        }
        jar.close();
        return true;
    }
    
    
    private void storeLibrariesInExtensionData() {
        try {
            Data data = ExtensionDataUtils.getExtensionData(getExtensionData());
            AdditionalLibraries libs = data.getAdditionalLibraries();
            if (libs == null) {
                libs = new AdditionalLibraries();
                data.setAdditionalLibraries(libs);
            }
            List<String> jarNames = new ArrayList();
            for (int i = 0; i < getDependsList().getModel().getSize(); i++) {
                jarNames.add((String) getDependsList().getModel().getElementAt(i));
            }
            // add the query processor jar
            jarNames.add(getQpJarTextField().getText());
            if (getClientLibDirTextField().getText().length() != 0) {
                jarNames.add(locateClientJarInDir(
                    getClientLibDirTextField().getText()).getName());
            }
            String[] jarNameArray = new String[jarNames.size()];
            jarNames.toArray(jarNameArray);
            libs.setJarName(jarNameArray);
            ExtensionDataUtils.storeExtensionData(getExtensionData(), data);
        } catch (Exception ex) {
            ex.printStackTrace();
            CompositeErrorDialog.showErrorDialog("Error storing jar information", ex);
        }
    }
    
    
    private Vector<String> getDependJars() throws Exception {
        // any 'additional libs' added previously should show up
        Data data = ExtensionDataUtils.getExtensionData(getExtensionData());
        AdditionalLibraries additionalLibs = data.getAdditionalLibraries();
        Vector<String> dependJars = new Vector();
        File clientJar = locateClientJarInDir(getClientLibDirTextField().getText());
        if (additionalLibs != null && additionalLibs.getJarName() != null) {
            String[] jarNames = additionalLibs.getJarName();
            for (int i = 0; i < jarNames.length; i++) {
                if (!jarNames[i].equals(getQpJarTextField().getText())
                    && !jarNames[i].equals(clientJar != null ? clientJar.getName() : null)) {
                    dependJars.add(jarNames[i]);
                }
            }
            Collections.sort(dependJars);
        }
        return dependJars;
    }
    
    
    private void addDependJars() {
        // get the jar selection
        String[] selection = null;
        try {
            selection = ResourceManager.promptMultiFiles(null, FileFilters.JAR_FILTER);
        } catch (IOException ex) {
            ex.printStackTrace();
            CompositeErrorDialog.showErrorDialog("Error selecting files", ex);
        }
        
        if (selection != null) {
            Vector allLibs = new Vector();
            for (int i = 0; i < getDependsList().getModel().getSize(); i++) {
                allLibs.add(getDependsList().getModel().getElementAt(i));
            }
            for (int i = 0; i < selection.length; i++) {
                File inFile = new File(selection[i]);
                // ignore anything that is a repeat jar
                if (shouldAddJar(inFile.getName())) {
                    File outFile = new File(getServiceInformation().getBaseDirectory().getAbsolutePath() 
                        + File.separator + "lib" + File.separator + inFile.getName());
                    try {
                        Utils.copyFile(inFile, outFile);
                        allLibs.add(outFile.getName());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        CompositeErrorDialog.showErrorDialog("Error copying library " + inFile.getAbsolutePath(), ex);
                    }                   
                }
            }
            Collections.sort(allLibs);
            getDependsList().setListData(allLibs);
            storeLibrariesInExtensionData();
        }
    }
    
    
    private boolean shouldAddJar(String jarName) {
        Set usedNames = new HashSet();
        usedNames.add(getQpJarTextField().getText());
        usedNames.add(getClientLibDirTextField().getText());
        for (int i = 0; i < getDependsList().getModel().getSize(); i++) {
            usedNames.add(getDependsList().getModel().getElementAt(i));
        }
        return !usedNames.contains(jarName);
    }
    
    
    private void removeDependJars() {
        // seperate wheat from the chaff
        Set deleteItems = new HashSet();
        Collections.addAll(deleteItems, getDependsList().getSelectedValues());
        Vector remainingItems = new Vector(getDependsList().getModel().getSize());
        for (int i = 0; i < getDependsList().getModel().getSize(); i++) {
            String libName = (String) getDependsList().getModel().getElementAt(i);
            if (!deleteItems.contains(libName)) {
                remainingItems.add(libName);
            }
        }
        // gather the wheat into the barn
        Collections.sort(remainingItems);
        getDependsList().setListData(remainingItems);
        // burn the chaff in the firey furnace
        Iterator deleteItemIter = deleteItems.iterator();
        while (deleteItemIter.hasNext()) {
            File libFile = new File(getServiceInformation().getBaseDirectory().getAbsolutePath() 
                + File.separator + "lib" + File.separator + (String) deleteItemIter.next());
            libFile.delete();
        }
        storeLibrariesInExtensionData();
    }
    
    
    // ----------
    // validation
    // ----------
    
    
    private void configureValidation() {
        ValidationComponentUtils.setMessageKey(getClientLibDirTextField(), KEY_CLIENT_LIB_DIR);
        
        validateInput();
        updateComponentTreeSeverity();
    }
    
    
    private void validateInput() {
        ValidationResult result = new ValidationResult();
        
        if (ValidationUtils.isBlank(getClientLibDirTextField().getText())) {
            result.add(new SimpleValidationMessage(
                KEY_CLIENT_LIB_DIR + " cannot be blank", Severity.ERROR, KEY_CLIENT_LIB_DIR));
        }
        
        validationModel.setResult(result);
        
        updateComponentTreeSeverity();
        // update next button enabled
        setNextEnabled(!validationModel.hasErrors());
    }
    
    
    private void updateComponentTreeSeverity() {
        ValidationComponentUtils.updateComponentTreeMandatoryAndBlankBackground(this);
        ValidationComponentUtils.updateComponentTreeSeverityBackground(this, validationModel.getResult());
    }
}
