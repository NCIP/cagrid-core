package gov.nih.nci.cagrid.data.service;

import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.cql2.CQL2QueryProcessor;
import gov.nih.nci.cagrid.data.utilities.DomainModelUtils;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.metadata.dataservice.UMLClass;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.dataservice.metadata.instancecount.DataServiceInstanceCounts;
import org.cagrid.dataservice.metadata.instancecount.InstanceCount;
import org.globus.wsrf.Resource;

/**
 * InstanceCountUpdater
 * 
 * Utility that keeps the instance count up to date
 * 
 * @author David
 *
 */
public class InstanceCountUpdater {
    
    private static Log LOG = LogFactory.getLog(InstanceCountUpdater.class);
    
    public static final long UPDATE_FREQUENCY = 600; // seconds. 600 = 10 minutes
    public static final long SLEEP_BETWEEN_QUERIES = 1000; // milliseconds.  The amount of time to pause between count queries.
        
    
    public static void startCountUpdateTask(final DomainModel model, final CQL2QueryProcessor processor, 
        final Resource baseResource, final Method setterMethod) {
        TimerTask task = new TimerTask() {
            public void run() {
                LOG.debug("Starting instance count update");
                DataServiceInstanceCounts counts = new DataServiceInstanceCounts();
                UMLClass[] modelClasses = model.getExposedUMLClassCollection().getUMLClass();
                if (modelClasses != null) {
                    InstanceCount[] instanceCounts = new InstanceCount[modelClasses.length];
                    LOG.debug("Found " + instanceCounts.length + " classes to count");
                    for (int i = 0; i < modelClasses.length; i++) {
                        String className = DomainModelUtils.getQualifiedClassname(modelClasses[i]);
                        LOG.debug("Counting instances of " + className);
                        long count = -1;
                        try {
                            count = processor.getInstanceCount(className);
                            LOG.debug("Found " + count + " instances");
                        } catch (QueryProcessingException ex) {
                            LOG.warn("Error obtaining a count of " + className + ": " + ex.getMessage(), ex);
                        }
                        instanceCounts[i] = new InstanceCount(className, count);
                        try {
                            Thread.sleep(SLEEP_BETWEEN_QUERIES);
                        } catch (InterruptedException ex) {
                            // whatever
                        }
                    }
                    counts.setInstanceCount(instanceCounts);                    
                } else {
                    LOG.debug("No classes in the model; no counts to update");
                }
                counts.setLastUpdated(Calendar.getInstance());
                LOG.debug("Setting the count on the resource property");
                try {
                    setterMethod.invoke(baseResource, counts);
                } catch (Exception ex) {
                    LOG.error("Error invoking setter for the instance count resource property: " + ex.getMessage(), ex);
                }
            }
        };
        
        // TODO: may have to make the timer use a daemon thread
        Timer t = new Timer(InstanceCountUpdater.class.getName() + " task");
        t.schedule(task, 0, UPDATE_FREQUENCY);
    }
}
