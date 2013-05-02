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
package gov.nih.nci.cagrid.sdkquery4.style.beanmap;

/**
 * BeanTypeDiscoveryEvent 
 * Container for bean type discovery events
 * 
 * @author David Ervin
 * 
 * @created Jan 15, 2008 12:50:42 PM
 * @version $Id: BeanTypeDiscoveryEvent.java,v 1.1 2008-01-16 17:05:31 dervin Exp $
 */
public class BeanTypeDiscoveryEvent {

    private int totalBeans;
    private int currentBean;
    private String beanClassname;


    public BeanTypeDiscoveryEvent(int total, int current, String classname) {
        this.totalBeans = total;
        this.currentBean = current;
        this.beanClassname = classname;
    }


    public String getBeanClassname() {
        return beanClassname;
    }


    public int getCurrentBean() {
        return currentBean;
    }


    public int getTotalBeans() {
        return totalBeans;
    }

}
