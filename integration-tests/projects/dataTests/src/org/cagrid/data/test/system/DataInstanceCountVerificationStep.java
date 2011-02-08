package org.cagrid.data.test.system;

import gov.nih.nci.cagrid.data.utilities.DataServiceFeatureDiscoveryUtil;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI;
import org.projectmobius.bookstore.Book;
import org.projectmobius.bookstore.BookStore;

/**
 * DataInstanceCountVerificationStep
 * 
 * Verifies the DataInstanceCount metadata works as expected
 * @author David
 *
 */
public class DataInstanceCountVerificationStep extends Step {
    
    // number of times to retry getting the count
    public static final int COUNT_RETRIES = 5;
    // ms to wait between retries
    public static final long COUNT_RETRY_TIMEOUT = 2000;
    
    private ServiceContainer container;
    private String serviceName;
    
    public DataInstanceCountVerificationStep(ServiceContainer container, String serviceName) {
        this.container = container;
        this.serviceName = serviceName;
    }
    

    public void runStep() throws Throwable {
        String url = getServiceUrl();
        EndpointReferenceType epr = new EndpointReferenceType(new Address(url));
        // get the count of Books that the TestingCQL2QueryProcessor knows about
        // the count may take a moment to get populated, so retry a few times with a timeout
        long bookCount = -1;
        long bookStoreCount = -1;
        int tryNum = 0;
        Exception countRetrievalException = null;
        while (bookCount == -1 && tryNum < COUNT_RETRIES) {
            tryNum++;
            try {
                bookCount = DataServiceFeatureDiscoveryUtil.getDataInstanceCount(epr, Book.class.getName());
                bookStoreCount = DataServiceFeatureDiscoveryUtil.getDataInstanceCount(epr, BookStore.class.getName());
            } catch (Exception ex) {
                countRetrievalException = ex;
            }
            try {
                Thread.sleep(COUNT_RETRY_TIMEOUT);
            } catch (InterruptedException ex) {
                // ?
            }
        }
        if (bookCount == -1 && countRetrievalException != null) {
            countRetrievalException.printStackTrace();
            fail("Error retrieving instance count: " + countRetrievalException.getMessage());
        }
        // compare to the test results generator counts
        assertEquals("Number of book instances in instance count not as expected", TestQueryResultsGenerator.BOOK_COUNT, bookCount);
        assertEquals("Number of book store instances in instance count not as expected", 1, bookStoreCount);
    }
    
    
    private String getServiceUrl() throws Exception {
        URI baseUri = container.getContainerBaseURI();
        String url = baseUri.toString() + "cagrid/" + serviceName;
        return url;
    }
}
