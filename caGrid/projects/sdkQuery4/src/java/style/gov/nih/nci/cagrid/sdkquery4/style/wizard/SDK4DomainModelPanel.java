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
