package gov.nih.nci.cagrid.common;

import java.util.List;
import java.util.Vector;

/** 
 * 
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella</A> 
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings</A> 
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster</A>      
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin </A> 
 *   
 * @created Dec 18, 2003 
 * @version $Id: RunnerGroup.java,v 1.3 2007-12-18 19:09:46 dervin Exp $ 
 */

public class RunnerGroup {

	public List<Runner> tasks;


	public RunnerGroup() {
		tasks = new Vector<Runner>();
	}


	public void add(Runner runnable) {
		tasks.add(runnable);
	}


	public int size() {
		return tasks.size();
	}


	public Runner get(int i) {
		return tasks.get(i);
	}
}
