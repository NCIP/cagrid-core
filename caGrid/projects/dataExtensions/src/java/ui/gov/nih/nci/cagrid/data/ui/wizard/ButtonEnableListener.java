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
package gov.nih.nci.cagrid.data.ui.wizard;

/** 
 *  ButtonEnableListener
 *  Listener to enable / disable buttons
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Sep 29, 2006 
 * @version $Id: ButtonEnableListener.java,v 1.1 2007-07-12 17:20:52 dervin Exp $ 
 */
public interface ButtonEnableListener {

	public void setNextEnabled(boolean enable);
	
	
	public void setPrevEnabled(boolean enable);
	
	
	public void setWizardDone(boolean done);
}
