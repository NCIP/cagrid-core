package gov.nih.nci.cagrid.data.ui;

import gov.nih.nci.cagrid.data.common.ExtensionDataManager;
import gov.nih.nci.cagrid.data.style.CreationWizardPanel;
import gov.nih.nci.cagrid.data.style.DataServiceStyleCreationWizardPanels;
import gov.nih.nci.cagrid.data.style.ServiceStyleContainer;
import gov.nih.nci.cagrid.data.ui.wizard.AbstractWizardPanel;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/** 
 *  StyleUiLoader
 *  Utility to load UI components of a style
 * 
 * @author David Ervin
 * 
 * @created Jul 12, 2007 10:49:42 AM
 * @version $Id: StyleUiLoader.java,v 1.2 2007-12-18 19:11:40 dervin Exp $ 
 */
public class StyleUiLoader {

    /**
     * Loads the creation wizard panels for the style
     * 
     * @param extensionDescription
     * @param serviceInfo
     * 
     * @return
     *      The creation wizard panels for this style
     * @throws Exception
     */
    public static List<AbstractWizardPanel> loadWizardPanels(ServiceStyleContainer container,
        ServiceExtensionDescriptionType extensionDescription, ServiceInformation serviceInfo) 
        throws Exception {
        List<AbstractWizardPanel> wizardPanels = new ArrayList<AbstractWizardPanel>();
        DataServiceStyleCreationWizardPanels panelDescriptions = 
            container.getServiceStyle().getCreationWizardPanels();
        if (panelDescriptions != null) {
            ClassLoader classLoader = container.createClassLoader();
            for (CreationWizardPanel panelDescription : panelDescriptions.getCreationWizardPanel()) {
                String panelClassname = panelDescription.getClassname();
                Class panelClass = classLoader.loadClass(panelClassname);
                Constructor panelConstructor = panelClass.getConstructor(
                    new Class[] {ServiceExtensionDescriptionType.class, ServiceInformation.class});
                AbstractWizardPanel panel = (AbstractWizardPanel) panelConstructor.newInstance(
                    new Object[] {extensionDescription, serviceInfo});
                wizardPanels.add(panel);
            }
        }
        return wizardPanels;
    }
    
    
    /**
     * Loads the service modification UI panel supplied by this service style
     * 
     * @param serviceInfo
     * @param dataManager
     * 
     * @return
     *      The modification UI panel, or <code>null</code> if none was supplied
     * @throws Exception
     */
    public static DataServiceModificationSubPanel loadModificationUiPanel(ServiceStyleContainer container,
        ServiceInformation serviceInfo, ExtensionDataManager dataManager) throws Exception {
        if (container.getServiceStyle().getModificationUiPanel() != null) {
            ClassLoader loader = container.createClassLoader();
            Class panelClass = loader.loadClass(
                container.getServiceStyle().getModificationUiPanel().getClassname());
            Constructor panelConstructor = panelClass.getConstructor(
                new Class[] {ServiceInformation.class, ExtensionDataManager.class});
            DataServiceModificationSubPanel panel = (DataServiceModificationSubPanel)
                panelConstructor.newInstance(new Object[] {serviceInfo, dataManager});
            return panel;
        }
        return null;
    }
}
