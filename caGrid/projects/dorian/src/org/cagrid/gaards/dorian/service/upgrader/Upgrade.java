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
