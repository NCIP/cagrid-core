package gov.nih.nci.cagrid.testing.system.haste;

/*
 * HASTE - High-level Automated System Test Environment
 * Copyright (C) 2002  Atomic Object, LLC.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * Contact Atomic Object:
 * 
 * Atomic Object, LLC.
 * East Building Suite 190
 * 419 Norwood Ave SE
 * Grand Rapids, MI 49506
 * USA
 *
 * info@atomicobject.com
 */

/**
 * A Step is the basic unit of a Story -- a Story
 * executes a series of Steps that perform actions and
 * make assertions.  Step is the system-test-level
 * analogue of test methods within a JUnit TestCase.
 * @version $Revision: 1.1 $
 */
public abstract class Step extends junit.framework.Assert {
	
	/**
	 * The method that gets executed by Story's <code>runTest()</code>
	 * loop.
	 * @throws Throwable Assertions, failures and other Throwables need
	 * to be able to throw into 
	 */
	public abstract void runStep() throws Throwable;

	/**
	 * Define the name of this step.  Currently uses name of class, but
	 * user can override to redefine.
	 */
	public String getName() {
		String className = getClass().getName();
		// removed '.' 's
		if (className != null && (className.indexOf(".") != -1)) {
			try {
				String name = className.substring(className.lastIndexOf(".") + 1);
				className = name;
			} catch (IndexOutOfBoundsException ex) {
				// keep old className
			}
		}
		// remove $'s
		if (className != null && (className.indexOf("$") != -1)) {
			try {
				String name = className.substring(className.lastIndexOf("$") + 1);
				className = name;
			} catch (IndexOutOfBoundsException ex) {
				// keep old className
			}
		}
		return className;
	}
	
	
}
