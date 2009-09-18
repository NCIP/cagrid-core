package gov.nih.nci.cagrid.sdkquery4.style.wizard;

import gov.nih.nci.cagrid.data.style.sdkstyle.wizard.DomainModelPanel;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

/** 
 *  SDK4DomainModelPanel
 *  Dereives from basic domain model panel, but prevents use of 'No Domain Model'
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Sep 25, 2006 
 * @version $Id: SDK4DomainModelPanel.java,v 1.1 2008-09-05 16:06:21 dervin Exp $ 
 */
public class SDK4DomainModelPanel extends DomainModelPanel {
    
    public SDK4DomainModelPanel(ServiceExtensionDescriptionType extensionDescription, ServiceInformation info) {
        super(extensionDescription, info);
        getNoDomainModelRadioButton().setEnabled(false);
        getNoDomainModelRadioButton().setToolTipText(
            "A domain model is required for data services backed by caCORE SDK v4");
    }    
}
