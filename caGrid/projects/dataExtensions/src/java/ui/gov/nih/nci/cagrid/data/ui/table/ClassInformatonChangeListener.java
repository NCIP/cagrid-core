/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package gov.nih.nci.cagrid.data.ui.table;

/** 
 *  ClassInformatonChangeListener
 *  Listens for events pertaining to changes of the class information
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Oct 6, 2006 
 * @version $Id: ClassInformatonChangeListener.java,v 1.1 2007-07-12 17:20:52 dervin Exp $ 
 */
public interface ClassInformatonChangeListener {

	public void elementNameChanged(ClassChangeEvent e);
	
	
	public void serializationChanged(ClassChangeEvent e);
	
	
	public void targetabilityChanged(ClassChangeEvent e);
}
