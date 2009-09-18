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
