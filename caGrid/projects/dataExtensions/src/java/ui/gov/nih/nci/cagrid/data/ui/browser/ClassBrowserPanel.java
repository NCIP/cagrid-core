package gov.nih.nci.cagrid.data.ui.browser;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.ExtensionDataUtils;
import gov.nih.nci.cagrid.data.common.ExtensionDataManager;
import gov.nih.nci.cagrid.data.cql.CQLQueryProcessor;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.FileFilters;
import gov.nih.nci.cagrid.introduce.common.ResourceManager;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.cagrid.grape.utils.CompositeErrorDialog;


/**
 * ClassBrowserPanel 
 * Panel to enable browsing for a class and building up a list
 * of JARs to look in
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @created May 11, 2006
 * @version $Id: ClassBrowserPanel.java,v 1.7 2007-12-18 19:12:03 dervin Exp $
 */
public class ClassBrowserPanel extends JPanel {

	private JList additionalJarsList = null;
	private JScrollPane additionalJarsScrollPane = null;
	private JButton addJarButton = null;
	private JButton removeJarsButton = null;
	private JPanel jarButtonsPanel = null;
	private JPanel jarsPanel = null;
	private JComboBox classSelectionComboBox = null;
	private JPanel classSelectionPanel = null;
	private JLabel classSelectionLabel = null;
	
	private transient List<ClassSelectionListener> classSelectionListeners = null;
	private transient List<AdditionalJarsChangeListener> additionalJarsListeners = null;
	
	private transient ExtensionDataManager extensionDataManager = null;
	private transient ServiceInformation serviceInfo = null;

	public ClassBrowserPanel(ExtensionDataManager extensionDataManager, ServiceInformation serviceInfo) {
		this.extensionDataManager = extensionDataManager;
		this.serviceInfo = serviceInfo;
		classSelectionListeners = new LinkedList<ClassSelectionListener>();
		additionalJarsListeners = new LinkedList<AdditionalJarsChangeListener>();
        initFirstTime();
        initialize();
	}
    
    
    private void initFirstTime() {
        // get the additional jars
        String[] jarNames = null; 
        try {
            jarNames = extensionDataManager.getAdditionalJarNames();
        } catch (Exception ex) {
            CompositeErrorDialog.showErrorDialog("Error loading list of additional jars", ex);
        }
        if (jarNames != null) {
            addJars(jarNames);
        }
        
        // populate available classes from the jars
        populateClassDropdown();
        // set the selected query processor class
        if (CommonTools.servicePropertyExists(
            serviceInfo.getServiceDescriptor(), DataServiceConstants.QUERY_PROCESSOR_CLASS_PROPERTY)) {
            try {
                String qpClassname = CommonTools.getServicePropertyValue(
                    serviceInfo.getServiceDescriptor(), DataServiceConstants.QUERY_PROCESSOR_CLASS_PROPERTY);
                getClassSelectionComboBox().setSelectedItem(qpClassname);
            } catch (Exception ex) {
                ex.printStackTrace();
                CompositeErrorDialog.showErrorDialog("Error getting query processor class name from properties",
                    ex.getMessage(), ex);
            }
        }
    }
	
	
	public void populateFields() {
		// check that the service property for the query processor class
        // hasn't changed in value
	    try {
	        if (servicePropertyClassDifferentFromDisplayed()) {
	            String qpClassname = CommonTools.getServicePropertyValue(
	                serviceInfo.getServiceDescriptor(), DataServiceConstants.QUERY_PROCESSOR_CLASS_PROPERTY);
                getClassSelectionComboBox().setSelectedItem(qpClassname);
            }
	    } catch (Exception ex) {
	        ex.printStackTrace();
	        CompositeErrorDialog.showErrorDialog("Error getting query processor classname from properties", 
	            ex.getMessage(), ex);
	    }    
	}


    public boolean servicePropertyClassDifferentFromDisplayed() throws Exception {
        String propertyValue = null;
        if (CommonTools.servicePropertyExists(
            serviceInfo.getServiceDescriptor(), DataServiceConstants.QUERY_PROCESSOR_CLASS_PROPERTY)) {
            propertyValue = CommonTools.getServicePropertyValue(
                serviceInfo.getServiceDescriptor(), DataServiceConstants.QUERY_PROCESSOR_CLASS_PROPERTY);
        }
        String displayedValue = getSelectedClassName();
        return !Utils.equals(propertyValue, displayedValue);
    }
	
	
	private void addJars(String[] jarFiles) {
		// only bother adding the jar file to the list if it's not in there yet
        Set<String> uniqueJars = new HashSet<String>();
        Collections.addAll(uniqueJars, getAdditionalJars());
        for (String jarFile : jarFiles) {
            String shortJarName = (new File(jarFile)).getName();
            if (!uniqueJars.contains(shortJarName)) {
                copyJarToService(jarFile);
                uniqueJars.add(shortJarName);
            }
        }
        String[] additionalJars = new String[uniqueJars.size()];
        uniqueJars.toArray(additionalJars);
        getAdditionalJarsList().setListData(additionalJars);
	}


	/**
	 * This method initializes this
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
		gridBagConstraints7.gridx = 0;
		gridBagConstraints7.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints7.weightx = 1.0D;
		gridBagConstraints7.weighty = 1.0D;
		gridBagConstraints7.gridy = 1;
		GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
		gridBagConstraints6.gridx = 0;
		gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints6.weightx = 1.0D;
		gridBagConstraints6.gridy = 0;
		this.setLayout(new GridBagLayout());
		this.add(getClassSelectionPanel(), gridBagConstraints6);
		this.add(getJarsPanel(), gridBagConstraints7);
	}


	public String getSelectedClassName() {
		Object selected = getClassSelectionComboBox().getSelectedItem();
		if (selected != null && selected.toString().length() != 0) {
			return selected.toString();
		}
		return null;
	}
	

	public String[] getAdditionalJars() {
		String[] jars = new String[getAdditionalJarsList().getModel().getSize()];
		for (int i = 0; i < getAdditionalJarsList().getModel().getSize(); i++) {
			jars[i] = (String) getAdditionalJarsList().getModel().getElementAt(i);
		}
		return jars;
	}


	/**
	 * This method initializes jList
	 * 
	 * @return javax.swing.JList
	 */
	private JList getAdditionalJarsList() {
		if (additionalJarsList == null) {
			additionalJarsList = new JList();
			// load any previous additional jars information
            String[] jarNames = null;
            try {
                jarNames = extensionDataManager.getAdditionalJarNames();
            } catch (Exception ex) {
                ex.printStackTrace();
                CompositeErrorDialog.showErrorDialog("Error loading list of additional jars", ex);
            }
			if (jarNames != null) {
                additionalJarsList.setListData(jarNames);
			}
		}
		return additionalJarsList;
	}


	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getAdditionalJarsScrollPane() {
		if (additionalJarsScrollPane == null) {
			additionalJarsScrollPane = new JScrollPane();
			additionalJarsScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(
				null, "Additional Jars", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION, null, PortalLookAndFeel.getPanelLabelColor()));
			additionalJarsScrollPane.setViewportView(getAdditionalJarsList());
		}
		return additionalJarsScrollPane;
	}


	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getAddJarButton() {
		if (addJarButton == null) {
			addJarButton = new JButton();
			addJarButton.setText("Add Jar");
			addJarButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					browseForJars();
					fireAdditionalJarsChanged();
					populateClassDropdown();
				}
			});
		}
		return addJarButton;
	}


	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getRemoveJarsButton() {
		if (removeJarsButton == null) {
			removeJarsButton = new JButton();
			removeJarsButton.setText("Remove Jars");
			removeJarsButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// identify selected / kept jars
					Set<Object> selected = new HashSet<Object>();
					Collections.addAll(selected, getAdditionalJarsList().getSelectedValues());
					Vector<String> keptJars = new Vector<String>();
					for (int i = 0; i < getAdditionalJarsList().getModel().getSize(); i++) {
						String jarName = (String) getAdditionalJarsList().getModel().getElementAt(i);
						if (!selected.contains(jarName)) {
							keptJars.add(jarName);
						}
					}
					// change the list contents
					getAdditionalJarsList().setListData(keptJars);
					// delete the selected jars
					Iterator deleteJarsIter = selected.iterator();
					while (deleteJarsIter.hasNext()) {
						deleteAdditionalJar((String) deleteJarsIter.next());
					}
					
					// update the class selection dropdown
					populateClassDropdown();
					
					// notify listeners
					fireAdditionalJarsChanged();
				}
			});
		}
		return removeJarsButton;
	}


	private void deleteAdditionalJar(String shortJarName) {
		String libDir = serviceInfo.getBaseDirectory().getAbsolutePath()
            + File.separator + "lib";
		File jarFile = new File(libDir + File.separator + shortJarName);
		jarFile.delete();
	}


	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJarButtonsPanel() {
		if (jarButtonsPanel == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints1.gridy = 0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints.gridy = 0;
			jarButtonsPanel = new JPanel();
			jarButtonsPanel.setLayout(new GridBagLayout());
			jarButtonsPanel.add(getAddJarButton(), gridBagConstraints);
			jarButtonsPanel.add(getRemoveJarsButton(), gridBagConstraints1);
		}
		return jarButtonsPanel;
	}


	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJarsPanel() {
		if (jarsPanel == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.gridy = 1;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints2.gridy = 0;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.weighty = 1.0D;
			gridBagConstraints2.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints2.gridx = 0;
			jarsPanel = new JPanel();
			jarsPanel.setLayout(new GridBagLayout());
			jarsPanel.add(getAdditionalJarsScrollPane(), gridBagConstraints2);
			jarsPanel.add(getJarButtonsPanel(), gridBagConstraints3);
		}
		return jarsPanel;
	}


	/**
	 * This method initializes jComboBox
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getClassSelectionComboBox() {
		if (classSelectionComboBox == null) {
			classSelectionComboBox = new JComboBox();
			classSelectionComboBox.setEditable(false);
			classSelectionComboBox.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					fireClassSelectionChanged();
				}
			});
		}
		return classSelectionComboBox;
	}


	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getClassSelectionPanel() {
		if (classSelectionPanel == null) {
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.gridy = 0;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints5.gridx = 1;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints4.gridy = 0;
			classSelectionPanel = new JPanel();
			classSelectionPanel.setLayout(new GridBagLayout());
			classSelectionPanel.add(getClassSelectionLabel(), gridBagConstraints4);
			classSelectionPanel.add(getClassSelectionComboBox(), gridBagConstraints5);
		}
		return classSelectionPanel;
	}


	/**
	 * This method initializes jLabel
	 * 
	 * @return javax.swing.JLabel
	 */
	private JLabel getClassSelectionLabel() {
		if (classSelectionLabel == null) {
			classSelectionLabel = new JLabel();
			classSelectionLabel.setText("Selected Class:");
		}
		return classSelectionLabel;
	}


	private void browseForJars() {
		String[] jarFiles = null;
		try {
			jarFiles = ResourceManager.promptMultiFiles(this, null, new FileFilters.JarFileFilter());
		} catch (Exception ex) {
			ex.printStackTrace();
			CompositeErrorDialog.showErrorDialog("Error selecting files: " + ex.getMessage(), ex);
		}
		if (jarFiles != null) {
			addJars(jarFiles);
		}
	}
	

	private synchronized void copyJarToService(final String jarFile) {
		String libDir = serviceInfo.getBaseDirectory().getAbsolutePath()
            + File.separator + "lib";
		try {
			File inJarFile = new File(jarFile);
			File outJarFile = new File(libDir + File.separator + inJarFile.getName());
			Utils.copyFile(inJarFile, outJarFile);
		} catch (Exception ex) {
			ex.printStackTrace();
			CompositeErrorDialog.showErrorDialog("Error copying the jar " + jarFile, ex);
		}
	}


	private void populateClassDropdown() {
        SortedSet<String> foundClassNames = new TreeSet<String>();
		String libDir = serviceInfo.getBaseDirectory().getAbsolutePath() + File.separator + "lib";
		String[] additionalJarNames = getAdditionalJars();
        try {
            // get URLs for all jar files in the service's lib dir
            File[] libFiles = (new File(libDir)).listFiles(new FileFilters.JarFileFilter());
            URL[] urls = new URL[libFiles.length];
            for (int i = 0; i < libFiles.length; i++) {
                urls[i] = libFiles[i].toURL();
            }
			Class<?> queryProcessorClass = CQLQueryProcessor.class;
            // search for query processor classes from additional jars list
            for (String jarName : additionalJarNames) {
                // creates a new loader each time to avoid having every class in the service
                // loaded at once, clogging up the loader
                ClassLoader loader = new URLClassLoader(urls);
                JarFile jarFile = new JarFile(libDir + File.separator + jarName);
                Enumeration jarEntries = jarFile.entries();
                while (jarEntries.hasMoreElements()) {
                    JarEntry entry = (JarEntry) jarEntries.nextElement();
                    String name = entry.getName();
                    if (name.endsWith(".class")) {
                        name = name.replace('/', '.');
                        name = name.substring(0, name.length() - 6);
                        Class loadedClass = null;
                        try {
                            loadedClass = loader.loadClass(name);
                        } catch (Throwable e) {
                            // theres a lot of these...
                        }
                        if (loadedClass != null) {
                            if (queryProcessorClass.isAssignableFrom(loadedClass)) {
                                foundClassNames.add(name);
                            }
                        }
                    }                    
                }
                // allow the created class loader to be reclaimed by GC
                loader = null;
                jarFile.close();
            }
            // potentially populate the class drop down
            DefaultComboBoxModel model = (DefaultComboBoxModel) getClassSelectionComboBox().getModel();
            SortedSet<String> currentClassNames = new TreeSet<String>();
            for (int i = 0; i < model.getSize(); i++) {
                currentClassNames.add(model.getElementAt(i).toString());
            }
            String qpStubName = ExtensionDataUtils.getQueryProcessorStubClassName(serviceInfo);
            Set diff = SetUtil.difference(currentClassNames, foundClassNames);
            // the query processor stub will frequently be a difference between found and current...
            diff.remove(qpStubName);
            if (diff.size() != 0) {
                // just replace the current with the found jars
                model.removeAllElements();
                for (String className : foundClassNames) {
                    model.addElement(className);
                }
            }
            // ensure the query processor stub is always available
            if (model.getIndexOf(qpStubName) == -1) {
                model.addElement(qpStubName);
            }
            // set the selected query processor class
            if (CommonTools.servicePropertyExists(
                serviceInfo.getServiceDescriptor(), DataServiceConstants.QUERY_PROCESSOR_CLASS_PROPERTY)) {
                try {
                    String qpClassname = CommonTools.getServicePropertyValue(
                        serviceInfo.getServiceDescriptor(), 
                        DataServiceConstants.QUERY_PROCESSOR_CLASS_PROPERTY);
                    if (model.getIndexOf(qpClassname) != -1) {
                        getClassSelectionComboBox().setSelectedItem(qpClassname);
                     }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    CompositeErrorDialog.showErrorDialog("Error getting query processor class name from properties",
                        ex.getMessage(), ex);
                }
            }
		} catch (Exception ex) {
			ex.printStackTrace();
			CompositeErrorDialog.showErrorDialog("Error populating class names", ex.getMessage(), ex);
		}
	}


	public void addClassSelectionListener(ClassSelectionListener listener) {
		this.classSelectionListeners.add(listener);
	}
	
	
	public ClassSelectionListener[] getClassSelectionListeners() {
		ClassSelectionListener[] listeners = 
			new ClassSelectionListener[this.classSelectionListeners.size()];
		this.classSelectionListeners.toArray(listeners);
		return listeners;
	}


	public boolean removeClassSelectionListener(ClassSelectionListener listener) {
		return this.classSelectionListeners.remove(listener);
	}


	public void addAdditionalJarsChangeListener(AdditionalJarsChangeListener listener) {
		this.additionalJarsListeners.add(listener);
	}
	
	
	public AdditionalJarsChangeListener[] getAdditionalJarsChangeListeners() {
		AdditionalJarsChangeListener[] listeners = 
			new AdditionalJarsChangeListener[this.additionalJarsListeners.size()];
		this.additionalJarsListeners.toArray(listeners);
		return listeners;
	}


	public boolean removeAdditionalJarsChangeListener(AdditionalJarsChangeListener listener) {
		return this.additionalJarsListeners.remove(listener);
	}


	protected synchronized void fireClassSelectionChanged() {
		ClassSelectionEvent event = null;
        for (ClassSelectionListener listener : classSelectionListeners) {
            if (event == null) {
                event = new ClassSelectionEvent(this);
            }
            listener.classSelectionChanged(event);
        }
	}


	protected synchronized void fireAdditionalJarsChanged() {
		AdditionalJarsChangedEvent event = null;
        for (AdditionalJarsChangeListener listener : additionalJarsListeners) {
            if (event == null) {
                event = new AdditionalJarsChangedEvent(this);
            }
            listener.additionalJarsChanged(event);
        }
	}
}
