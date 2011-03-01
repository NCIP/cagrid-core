package gov.nih.nci.cagrid.data.service;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.MetadataConstants;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.cql2.CQL2QueryProcessor;
import gov.nih.nci.cagrid.data.utilities.DomainModelUtils;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.metadata.dataservice.UMLClass;

import java.io.StringWriter;
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
    
    // TODO: Evaluate if this is even needed
    public static final long SLEEP_BETWEEN_QUERIES = 1000; // milliseconds.  The amount of time to pause between count queries.
        
    
    /**
     * Starts up the instance count update task
     * 
     * @param model
     *      The domain model to look for classes that should be counted in
     * @param processor
     *      The CQL 2 query processor which will handle the counting
     * @param baseResource
     *      The WSRF resource which must be updated
     * @param setterMethod
     *      The setter method to the resource which takes the instance count
     * @param updateFrequency
     *      How often, in seconds, to update the instance count
     */
    public static void startCountUpdateTask(final DomainModel model, final CQL2QueryProcessor processor, 
        final Resource baseResource, final Method setterMethod, int updateFrequency) {
        boolean shouldStart = false;
        // verify we have everything we need before setting up the task
        if (model == null) {
            LOG.warn("Refusing to start up instance count task with no domain model.");
            shouldStart = false;
        }
        if (processor == null) {
            LOG.warn("Refusing to start up instance count task with no query processor.");
            shouldStart = false;
        }
        if (baseResource == null) {
            LOG.warn("Refusing to start up instance count task with no base resource.");
            shouldStart = false;
        }
        if (setterMethod == null) {
            LOG.warn("Refusing to start up instance count task with no resource property setter method.");
            shouldStart = false;
        }
        if (shouldStart) {
            TimerTask task = new TimerTask() {
                public void run() {
                    LOG.debug("Starting instance count update");
                    DataServiceInstanceCounts counts = new DataServiceInstanceCounts();
                    if (model != null) {
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
                    } else {
                        LOG.error("No domain model found; can't update instance counts!");
                    }
                    counts.setLastUpdated(Calendar.getInstance());
                    if (LOG.isDebugEnabled()) {
                        try {
                            StringWriter writer = new StringWriter();
                            Utils.serializeObject(counts, MetadataConstants.DATA_INSTANCE_QNAME, writer);
                            LOG.debug("Here's the instance count document:\n" + writer.getBuffer().toString());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
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
            t.schedule(task, 0, updateFrequency * 1000);
        } else {
            LOG.info("Instance count task will not be started");
        }
    }
}
