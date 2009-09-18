/*
 * Created on Aug 1, 2006
 */
package gov.nci.nih.cagrid.tests.cqlprocessors;

import gov.nci.nih.cagrid.tests.cqlprocessors.util.RandomObject;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.cql.CQLQueryProcessor;
import gov.nih.nci.cagrid.data.mapping.ClassToQname;
import gov.nih.nci.cagrid.data.mapping.Mappings;
import gov.nih.nci.cagrid.data.utilities.CQLResultsCreationUtil;

import java.util.ArrayList;

import javax.xml.namespace.QName;


public class SimpleCQLQueryProcessor extends CQLQueryProcessor {
    
    public SimpleCQLQueryProcessor() {
        super();
    }
    

	public CQLQueryResults processQuery(CQLQuery cqlQuery) 
        throws MalformedQueryException, QueryProcessingException {
		try {
			ArrayList<Object> resultList = new ArrayList<Object>();
			for (int i = 10; i >= 0; i--) {
				Object obj = new RandomObject().next(
					Class.forName(cqlQuery.getTarget().getName()), 3);
				resultList.add(obj);
			}
			
            // the results creation util needs a class to QName mapping
            Mappings mapping = new Mappings();
            ClassToQname stringMap = new ClassToQname();
            stringMap.setClassName(String.class.getName());
            stringMap.setQname(new QName("http://www.w3.org/2001/XMLSchema", "string").toString());
            mapping.setMapping(new ClassToQname[] {stringMap});
            
			return CQLResultsCreationUtil.createObjectResults(resultList, cqlQuery.getTarget().getName(),  mapping);
		} catch (Exception t) {
			System.out.println(t.getMessage());
			t.printStackTrace();
			throw new QueryProcessingException(t.getMessage(), t);
		}
	}
}
