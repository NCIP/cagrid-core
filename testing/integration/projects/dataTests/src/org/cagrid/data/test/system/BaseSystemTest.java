package org.cagrid.data.test.system;

import gov.nih.nci.cagrid.testing.system.haste.Story;

/** 
 *  BaseSystemTest
 *  The base system test
 * 
 * @author David Ervin
 * 
 * @created Mar 14, 2007 2:21:41 PM
 * @version $Id: BaseSystemTest.java,v 1.2 2008-06-02 20:34:18 dervin Exp $ 
 */
public abstract class BaseSystemTest extends Story {
	public static final String INTRODUCE_DIR_PROPERTY = "introduce.base.dir";
    
    public BaseSystemTest() {
        super();
    }
	

	protected String getIntroduceBaseDir() {
		String dir = System.getProperty(INTRODUCE_DIR_PROPERTY);
		if (dir == null) {
			fail("Introduce base dir environment variable "
					+ INTRODUCE_DIR_PROPERTY + " is required");
		}
		return dir;
	}
}
