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

import org.cagrid.gaards.dorian.service.BeanUtils;


public abstract class Upgrade {

    private BeanUtils beanUtils;


    public abstract void upgrade(boolean trialRun) throws Exception;


    public abstract String getStartingVersion();


    public abstract String getUpgradedVersion();


    protected void setBeanUtils(BeanUtils beanUtils) {
        this.beanUtils = beanUtils;
    }


    public BeanUtils getBeanUtils() {
        return beanUtils;
    }
}
