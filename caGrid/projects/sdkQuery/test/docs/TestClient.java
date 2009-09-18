

import gov.nih.nci.system.applicationservice.*;
import gov.nih.nci.cabio.domain.*;
import org.hibernate.criterion.*;
import java.io.FileReader;
import org.globus.wsrf.encoding.ObjectDeserializer;
import org.xml.sax.InputSource;
import gov.nih.nci.cagrid.cqlquery.Group;
import gov.nih.nci.cagrid.cqlquery.LogicalOperator;
import java.util.Iterator;
import java.util.List;
import gov.nih.nci.cagrid.data.cql.cacore.CQL2DetachedCriteria;


public class TestClient {
	
	public ApplicationService caBioService= null;
	
	
	public TestClient(){
		try{
			caBioService = ApplicationServiceProvider.getApplicationService();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	/**
	 * This test case is based on XML schema instance CQL_Query1.xml
	 *
	 */
	
	public void cql_Grid_Test_one(){
		try{
			
			DetachedCriteria dtc = CQL2DetachedCriteria.createQueryCriteria(this.getCqlObject("CQL_Query1.xml"));
			List resultList = caBioService.query(dtc, Gene.class.getName());
			System.out.println("\n Total # of  records = "
					+ resultList.size());

			for (Iterator resultsIterator = resultList.iterator(); resultsIterator
					.hasNext();) {
				Gene returnedGene = (Gene) resultsIterator
						.next();
				
				System.out.println(returnedGene.getSymbol());
			}
		}catch(Exception ex){
			ex.printStackTrace();
			
		}
	}
	/**
	 * This test case is based on XML schema instance CQL_Query1.xml
	 *
	 */
	public void cql_Grid_Test_Two(){
		try{
			
			DetachedCriteria dtc = ProcessorHelper.createQueryCriteria(this.getCqlObject("CQL_Query2.xml"));
			List resultList = caBioService.query(dtc, Taxon.class.getName());
			System.out.println("\n Total # of  records = "
					+ resultList.size());

			for (Iterator resultsIterator = resultList.iterator(); resultsIterator
					.hasNext();) {
				Taxon returnedTaxon = (Taxon)resultsIterator
						.next();
				
				System.out.println(returnedTaxon.getId());
			}
		}catch(Exception ex){
			ex.printStackTrace();
			
		}
	}
	
	public void cql_Grid_Test_ObjectWithAssociation(){
		try{
			
			DetachedCriteria dtc = ProcessorHelper.createQueryCriteria(this.getCqlObject("objectWithAssociation.xml"));
			List resultList = caBioService.query(dtc, Gene.class.getName());
			System.out.println("\n Total # of  records = "
					+ resultList.size());

			for (Iterator resultsIterator = resultList.iterator(); resultsIterator
					.hasNext();) {
				Gene returnedGene = (Gene)resultsIterator
						.next();
				
				System.out.println(returnedGene.getId());
			}
		}catch(Exception ex){
			ex.printStackTrace();
			
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		TestClient testClient = new TestClient();
		testClient.cql_Grid_Test_one();
		testClient.cql_Grid_Test_Two();
		testClient.cql_Grid_Test_ObjectWithAssociation();

	}
	
	/**
	 * This method takes XML schema instance and returns a CQL object
	 * which can be used for building DeatachedCriteria.
	 * @param fileName
	 * @return gov.nih.nci.cagrid.cqlquery.Object
	 */
	private gov.nih.nci.cagrid.cqlquery.Object getCqlObject(String fileName){
		gov.nih.nci.cagrid.cqlquery.Object cql = null;
		try{
			/**
		cql = (gov.nih.nci.cagrid.cqlquery.Object)Unmarshaller.unmarshal(gov.nih.nci.cagrid.cqlquery.Object.class,new FileReader("CQL_Query1.xml"));
		System.out.println(cql.getName());
		Group grp = cql.getGroup();
		
		LogicalOperator lg = grp.getLogicRelation();
	
		System.out.println(lg.getValue());
		
		*/
			 InputSource is = new InputSource(new FileReader(fileName));
			 
			 
			 cql = (gov.nih.nci.cagrid.cqlquery.Object)ObjectDeserializer.deserialize(is,gov.nih.nci.cagrid.cqlquery.Object.class);
			 
			 System.out.println(cql.getName());
			 
			 
	    
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return cql;
	}


}
