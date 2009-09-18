package gov.nih.nci.cagrid.data.utilities.invoker;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.client.DataServiceClient;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;

import java.util.Iterator;


/** 
 *  DataServiceInvoker
 *  Simple utility to invoke a remote data service with a CQL query
 *  and print results as either XML or attempt to produce some text values.
 * 
 * @author David Ervin
 * 
 * @created Aug 24, 2007 11:46:43 AM
 * @version $Id: DataServiceInvoker.java,v 1.2 2007-08-24 17:02:36 dervin Exp $ 
 */
public class DataServiceInvoker {
    public static void main(String[] args) {
        if (args.length != 2) {
            usage();
        }
        String url = args[0];
        String file = args[1];
        
        System.out.println("Querying service " + url);
        System.out.println("Using query file " + file);
        
        try {
            DataServiceClient client = new DataServiceClient(url);
            
            CQLQuery query = (CQLQuery) Utils.deserializeDocument(file, CQLQuery.class);
            
            CQLQueryResults results = client.query(query);
            Iterator i = new CQLQueryResultsIterator(results, true);
            while (i.hasNext()) {
                System.out.println(i.next());
            }
            System.out.println("Query Complete");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
    
    
    private static void usage() {
        System.out.println("Usage: " + DataServiceInvoker.class.getName() + " <serviceURL> <cqlFile>");
        System.out.println("or");
        System.out.println("ant invokeDataService -Dservice.url=<serviceUrl> -Dservice.cql=<cqlFile>");
        System.exit(1);
    }
}
