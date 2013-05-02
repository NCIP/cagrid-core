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
package org.cagrid.gaards.dorian.service.upgrader;

import java.util.ArrayList;

import org.cagrid.gaards.dorian.stubs.types.DorianInternalFault;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.AbstractResource;


public class UpgradeBeanUtils {

    public final static String UPGRADE_LIST_BEAN = "UpgradeList";

    private XmlBeanFactory factory;


    public UpgradeBeanUtils(AbstractResource upgraderConf) throws DorianInternalFault {
        this.factory = new XmlBeanFactory(upgraderConf);
    }


    public ArrayList<Upgrade> getUpgradeList() throws Exception {
        return (ArrayList<Upgrade>) factory.getBean(UPGRADE_LIST_BEAN);
    }

}
