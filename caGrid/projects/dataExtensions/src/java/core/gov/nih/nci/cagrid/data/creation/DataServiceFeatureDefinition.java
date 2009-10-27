package gov.nih.nci.cagrid.data.creation;

import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.util.ArrayList;
import java.util.List;

/** 
 *  DataServiceFeatureDefinition
 *  Defines data service features; Names, extension(s) to run, 
 *  feature implementation classes
 * 
 * @author David Ervin
 * 
 * @created May 6, 2008 10:37:03 AM
 * @version $Id: DataServiceFeatureDefinition.java,v 1.2 2009-04-10 14:51:31 dervin Exp $ 
 */
public enum DataServiceFeatureDefinition {

    ENUMERATION_FEATURE, TRANSFER_FEATURE;
    
    public String getName() {
        String name = null;
        switch (this) {
            case ENUMERATION_FEATURE:
                name = "WS-Enumeration";
                break;
            case TRANSFER_FEATURE:
                name = "caGrid Transfer";
                break;
            default:
                handleUnknown();
        }
        return name;
    }
    
    
    public String getDescription() {
        String description = null;
        switch (this) {
            case ENUMERATION_FEATURE:
                description = "Adds caGrid WS-Enumeration support to the data service";
                break;
            case TRANSFER_FEATURE:
                description = "Adds caGrid Transfer support to the data service";
                break;
            default:
                handleUnknown();
        }
        return description;
    }
    
    
    public List<String> getExtensionNames() {
        List<String> extensions = new ArrayList<String>();
        switch (this) {
            case ENUMERATION_FEATURE:
                extensions.add("cagrid_wsEnum");
                break;
            case TRANSFER_FEATURE:
                extensions.add("caGrid_Transfer");
                break;
            default:
                handleUnknown();
        }
        return extensions;
    }
    
    
    public FeatureCreator getFeatureCreator(ServiceInformation info, ServiceType mainService) {
        FeatureCreator creator = null;
        switch (this) {
            case ENUMERATION_FEATURE:
                creator = new WsEnumerationFeatureCreator(info, mainService);
                break;
            case TRANSFER_FEATURE:
                creator = new TransferFeatureCreator(info, mainService);
                break;
            default:
                handleUnknown();
        }
        return creator;
    }
    
    
    private void handleUnknown() throws IllegalArgumentException {
        throw new IllegalArgumentException("Unknown feature definition: " + this);
    }
}
