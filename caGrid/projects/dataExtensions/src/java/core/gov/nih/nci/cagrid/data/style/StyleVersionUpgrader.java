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
package gov.nih.nci.cagrid.data.style;

import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionTypeExtensionData;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.upgrade.common.ExtensionUpgradeStatus;

public interface StyleVersionUpgrader {

    public void upgradeStyle(ServiceInformation serviceInformation, ExtensionTypeExtensionData extensionData,
            ExtensionUpgradeStatus status, String serviceFromVersion, String serviceToVersion) throws Exception;
}
