package org.cagrid.gaards.csm.client;

import java.util.Calendar;


public class Application {

    private CSM csm;
    private long id;
    String name;
    String description;
    Calendar lastUpdate;


    public Application() {

    }


    public Application(CSM csm, org.cagrid.gaards.csm.bean.Application bean) {
        setCSM(csm);
        if (bean.getId() != null) {
            setId(bean.getId().longValue());
        }
        setName(bean.getName());
        setDescription(bean.getDescription());
        setLastUpdate(bean.getLastUpdated());
    }


    /**
     * Returns the CSM Web Service managing the application.
     * 
     * @return The CSM managing the application.
     */
    
    public CSM getCSM() {
        return csm;
    }


    private void setCSM(CSM csm) {
        this.csm = csm;
    }


    /**
     * Returns the CSM assigned Id of the application.
     * @return The CSM assigned Id of the application.
     */
    
    public long getId() {
        return id;
    }


    private void setId(long id) {
        this.id = id;
    }

    /**
     * Returns the name of the application.
     * @return The name of the application.
     */

    public String getName() {
        return name;
    }


    private void setName(String name) {
        this.name = name;
    }

    /**
     * Returns a description of the application.
     * @return A description of the application.
     */
    public String getDescription() {
        return description;
    }


    private void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the date the application was last updated.
     * @return The date the application was last updated.
     */

    public Calendar getLastUpdate() {
        return lastUpdate;
    }


    private void setLastUpdate(Calendar lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

}
