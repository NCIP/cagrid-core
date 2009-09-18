package gov.nih.nci.cagrid.data.cql.validation;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.data.MalformedQueryException;

/** 
 *  CqlStructureValidator
 *  Validation for CQL syntax
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Sep 1, 2006 
 * @version $Id$ 
 */
public interface CqlStructureValidator {

	public void validateCqlStructure(CQLQuery query) throws MalformedQueryException;
}
