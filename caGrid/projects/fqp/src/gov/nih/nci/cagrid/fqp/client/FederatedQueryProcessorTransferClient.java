package gov.nih.nci.cagrid.fqp.client;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.Predicate;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;
import gov.nih.nci.cagrid.data.utilities.ResultsCreationException;
import gov.nih.nci.cagrid.dcql.DCQLQuery;
import gov.nih.nci.cagrid.dcql.Object;
import gov.nih.nci.cagrid.fqp.common.DCQLConstants;
import gov.nih.nci.cagrid.fqp.results.client.FederatedQueryResultsClient;
 
import java.io.FileWriter;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Iterator;

import javax.xml.namespace.QName;
 
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.transfer.context.client.TransferServiceContextClient;
import org.cagrid.transfer.context.client.helper.TransferClientHelper;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.globus.gsi.GlobusCredential;
import org.oasis.wsrf.lifetime.SetTerminationTime;
import org.oasis.wsrf.lifetime.SetTerminationTimeResponse;
 
public class FederatedQueryProcessorTransferClient
{ 
	
    public static void usage() {
        System.out.println(FederatedQueryProcessorTransferClient.class.getName() + " -Durl <service url> -Dcql <DCQL file> -dcredential <caGrid Proxy>");
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
    	
    	System.out.println("Running the Grid Service Client");
        try {
        	if (!(args.length < 4)) {
                if (args[0].equals("-url")) {
                	String url = args[1];
            		
            		if (!args[2].equals("-dcql")) {
                        usage();
                        System.exit(1);
                    }
            		String dcqlFile = args[3]; 
            		
            		FederatedQueryProcessorClient client;
            		GlobusCredential cred = null;
            		if (url.startsWith("https")) {
	            		cred = GlobusCredential.getDefaultCredential();
	            		if (cred != null) {
	            			System.out.println("Identity =" + cred.getIdentity());
	            		}
	            		
	                    client = new FederatedQueryProcessorClient(url, cred);
            		}
            		else {
            			client = new FederatedQueryProcessorClient(url);
            		}
            		
            		DCQLQuery dcql = (DCQLQuery) Utils.deserializeDocument(dcqlFile, DCQLQuery.class);
       
            		System.out.println("Starting query");
		            
		            FederatedQueryResultsClient resultsClient = client.executeAsynchronously(dcql);
		            while (!resultsClient.isProcessingComplete()) {		                
		            	Thread.sleep(1000);
		                System.out.print(".");
		            }
		            
		            System.out.println();
		            System.out.println("Transfering...");
		            TransferServiceContextReference transferRef = resultsClient.transfer();
		            
		            InputStream transferStream = null;
		            if (url.startsWith("https")) {
		            	TransferServiceContextClient transferClient = new TransferServiceContextClient(transferRef.getEndpointReference(), 
		            		cred);
		            	transferStream = TransferClientHelper.getData(transferClient.getDataTransferDescriptor(), cred);
		            }
		            else {
		            	TransferServiceContextClient transferClient = new TransferServiceContextClient(transferRef.getEndpointReference());
			            transferStream = TransferClientHelper.getData(transferClient.getDataTransferDescriptor());
		            }
		            
		            StringBuffer text = Utils.inputStreamToStringBuffer(transferStream);
		            System.out.println("Text from transfer:");
		            System.out.println(text);
		            System.out.println("Done");

                }
	            else {
	            	usage();
                    System.exit(1);
	            }
        	}
            else {
            	usage();
                System.exit(1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

