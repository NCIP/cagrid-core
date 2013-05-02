/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package gov.nih.nci.cagrid.data.ui;

import gov.nih.nci.cagrid.data.common.ExtensionDataManager;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import javax.swing.JPanel;

/** 
 *  DataServiceModificationSubPanel
 *  A sub panel to be displayed as a tab of the data service modification GUI
 * 
 * @author David Ervin
 * 
 * @created Jul 9, 2007 1:42:17 PM
 * @version $Id: DataServiceModificationSubPanel.java,v 1.1 2007-07-12 17:20:52 dervin Exp $ 
 */
public abstract class DataServiceModificationSubPanel extends JPanel implements UpdatablePanel {
    private ServiceInformation serviceInfo;
    private ExtensionDataManager extensionDataManager;

    public DataServiceModificationSubPanel(ServiceInformation serviceInfo, ExtensionDataManager extensionDataManager) {
        this.serviceInfo = serviceInfo;
        this.extensionDataManager = extensionDataManager;
    }
    
    
    protected ServiceInformation getServiceInfo() {
        return this.serviceInfo;
    }
    
    
    protected ExtensionDataManager getExtensionDataManager() {
        return extensionDataManager;
    }
}
