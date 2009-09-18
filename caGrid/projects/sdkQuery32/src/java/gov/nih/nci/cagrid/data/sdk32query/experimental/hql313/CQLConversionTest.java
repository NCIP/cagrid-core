package gov.nih.nci.cagrid.data.sdk32query.experimental.hql313;

import gov.nih.nci.cabio.domain.Gene;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Object;
import gov.nih.nci.cagrid.cqlquery.Predicate;
import gov.nih.nci.cagrid.cqlquery.QueryModifier;
import gov.nih.nci.common.util.HQLCriteria;
import gov.nih.nci.system.applicationservice.ApplicationService;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.List;

/** 
 *  CQLConversionTest
 *  TODO:DOCUMENT ME
 * 
 * @author David Ervin
 * 
 * @created Mar 2, 2007 10:57:03 AM
 * @version $Id: CQLConversionTest.java,v 1.1 2007-03-08 20:21:41 dervin Exp $ 
 */
public class CQLConversionTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ApplicationService service = ApplicationService.getRemoteInstance("http://cabio.nci.nih.gov/cacore32/http/remoteService");
		
		CQLQuery query = new CQLQuery();
		
		/*
		 * Query exercises attribute functionality
		 *
		 *
		Object target = new Object();
		target.setName(Gene.class.getName());
		Attribute attrib = new Attribute("symbol", Predicate.LIKE, "BRCA%");
		target.setAttribute(attrib);
		query.setTarget(target);
		*/
		
		/*
		 * Query exercises associations and attributes
		 *
		Object target = new Object();
		target.setName(Gene.class.getName());
		Association assoc = new Association();
		assoc.setName(Chromosome.class.getName());
		assoc.setRoleName("chromosome");
		Attribute attrib = new Attribute("number", Predicate.EQUAL_TO, "X");
		assoc.setAttribute(attrib);
		target.setAssociation(assoc);
		query.setTarget(target);
		*/
		
		/*
		 * Query exercises nested association with an attribute
		 *
		Object target = new Object();
		target.setName(Gene.class.getName());
		Association chrAssoc = new Association();
		chrAssoc.setName(Chromosome.class.getName());
		Association taxAssoc = new Association();
		taxAssoc.setName(Taxon.class.getName());
		Attribute taxIdAttrib = new Attribute("id", Predicate.LESS_THAN, "10");
		taxAssoc.setAttribute(taxIdAttrib);
		chrAssoc.setAssociation(taxAssoc);
		target.setAssociation(chrAssoc);
		query.setTarget(target);
		*/
		
		/*
		 * Query tests groups, associations, and attributes
		 *
		Object target = new Object();
		target.setName(Gene.class.getName());
		Group group = new Group();
		group.setLogicRelation(LogicalOperator.AND);
		Association chrAssoc = new Association();
		chrAssoc.setName(Chromosome.class.getName());
		Attribute chrNumAttrib = new Attribute("number", Predicate.EQUAL_TO, "X");
		chrAssoc.setAttribute(chrNumAttrib);
		Association taxAssoc = new Association();
		taxAssoc.setName(Taxon.class.getName());
		Attribute taxIdAttrib = new Attribute("id", Predicate.LESS_THAN, "20");
		taxAssoc.setAttribute(taxIdAttrib);
		group.setAssociation(new Association[] {chrAssoc, taxAssoc});
		target.setGroup(group);
		query.setTarget(target);
		*/
		
		/*
		 * Query exercises nearly everything
		 *
		Object target = new Object();
		target.setName(Gene.class.getName());
		Group group = new Group();
		group.setLogicRelation(LogicalOperator.OR);
		Attribute symAttrib1 = new Attribute("symbol", Predicate.LIKE, "B%");
		Attribute symAttrib2 = new Attribute("symbol", Predicate.LIKE, "A%");
		group.setAttribute(new Attribute[] {symAttrib1, symAttrib2});
		Group nested = new Group();
		nested.setLogicRelation(LogicalOperator.AND);
		Association chrAssoc = new Association();
		chrAssoc.setName(Chromosome.class.getName());
		Attribute chrNumAttrib = new Attribute("number", Predicate.EQUAL_TO, "X");
		chrAssoc.setAttribute(chrNumAttrib);
		Association taxAssoc = new Association();
		taxAssoc.setName(Taxon.class.getName());
		Attribute taxIdAttrib = new Attribute("id", Predicate.LESS_THAN, "20");
		taxAssoc.setAttribute(taxIdAttrib);
		nested.setAssociation(new Association[] {chrAssoc, taxAssoc});
		group.setGroup(new Group[] {nested});
		target.setGroup(group);
		query.setTarget(target);
		*/
		
		/*
		 * Distinct attribute counting
		 *
		Object target = new Object();
		target.setName(Gene.class.getName());
		Attribute attrib = new Attribute("symbol", Predicate.LIKE, "B%");
		target.setAttribute(attrib);
		QueryModifier mods = new QueryModifier();
		mods.setCountOnly(true);
		mods.setDistinctAttribute("symbol");
		query.setTarget(target);
		query.setQueryModifier(mods);
		*/
		
		Object target = new Object();
		target.setName(Gene.class.getName());
		Attribute attrib = new Attribute("symbol", Predicate.LIKE, "B%");
		target.setAttribute(attrib);
		QueryModifier mods = new QueryModifier();
		mods.setAttributeNames(new String[] {"symbol", "fullName"});
		query.setTarget(target);
		query.setQueryModifier(mods);
		
		try {
			String hql = CQL2HQL.convertToHql(query, false, false);
			// String hql = "From " + Gene.class.getName() + " where chromosome.number = 'X' and chromosome.taxon.id < '60' and symbol like 'B%'";
			System.out.println("Converted CQL to HQL");
			System.out.println(hql);
			
			List results = service.query(new HQLCriteria(hql), query.getTarget().getName());
			Iterator resIter = results.iterator();
			/*
			 * For object results
			 *
			while (resIter.hasNext()) {
				Gene g = (Gene) resIter.next();
				System.out.println("Gene: ");
				System.out.println("\t" + g.getSymbol());
				System.out.println("\t" + g.getChromosome().getNumber());
				System.out.println("\t" + g.getChromosome().getTaxon().getId());
			}
			*/
			
			/*
			 * Count results
			 *
			while (resIter.hasNext()) {
				Integer count = (Integer) resIter.next();
				System.out.println(count);
			}
			*/
			
			/*
			 * Multi attrib results
			 */
			while (resIter.hasNext()) {
				java.lang.Object array = resIter.next();
				int length = Array.getLength(array);
				for (int i = 0; i < length; i++) {
					System.out.print(Array.get(array, i));
					if (i + 1 < length) {
						System.out.print(", ");
					}
				}
				System.out.println();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
