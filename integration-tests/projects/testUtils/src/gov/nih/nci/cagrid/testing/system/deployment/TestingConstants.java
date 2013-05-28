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
package gov.nih.nci.cagrid.testing.system.deployment;

/** 
 *  TestingConstants
 *  Constants used in testing
 * 
 * @author David Ervin
 * 
 * @created Oct 31, 2007 10:40:12 AM
 * @version $Id: TestingConstants.java,v 1.1 2008-05-14 17:17:42 hastings Exp $ 
 */
public class TestingConstants {

    // the default min and max testing ports
    public static final Integer TEST_PORT_LOWER_BOUND = Integer.valueOf(44444);
    public static final Integer TEST_PORT_UPPER_BOUND = Integer.valueOf(TEST_PORT_LOWER_BOUND.intValue() + 1000);

    public static final String TEST_TEMP_DIR = "tmp";
    
    private TestingConstants() {
        // prevents instantiation
    }
}
