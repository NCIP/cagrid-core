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
                return "SDK 4.0 XMI from EA";
            case SDK_40_ARGO:
                return "SDK 4.0 XMI from ArgoUML";
        }
        throw new IllegalArgumentException("Unknown type: " + this.name());
    }
}
