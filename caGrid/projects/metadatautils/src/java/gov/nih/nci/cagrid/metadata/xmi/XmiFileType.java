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
package gov.nih.nci.cagrid.metadata.xmi;

/** 
 *  XmiFileTypes
 *  Types of XMI files the parser can handle
 * 
 * @author David Ervin
 * 
 * @created Mar 19, 2008 11:40:36 AM
 * @version $Id: XmiFileType.java,v 1.1 2008-04-04 15:57:41 dervin Exp $ 
 */
public enum XmiFileType {

    SDK_32_EA, SDK_40_EA, SDK_40_ARGO;
    
    public String toString() {
        switch (this) {
            case SDK_32_EA:
                return "SDK 3.2 / 3.2.1 XMI from EA";
            case SDK_40_EA:
                return "SDK 4.x XMI from EA";
            case SDK_40_ARGO:
                return "SDK 4.x XMI from ArgoUML";
        }
        throw new IllegalArgumentException("Unknown type: " + this.name());
    }
}
