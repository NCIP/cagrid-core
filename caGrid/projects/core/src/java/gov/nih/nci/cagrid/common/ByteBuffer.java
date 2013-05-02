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
package gov.nih.nci.cagrid.common;

import java.io.IOException;

/**
 * Buffers data for a byte queue
 * 
 * @author David
 *
 */
public interface ByteBuffer {

    /**
     * Appends data to the end of the byte buffer.
     * 
     * @param data
     *      The data array
     * @param length
     *      The length of the data
     */
    public void appendData(byte[] data, int length) throws IOException;
    
    
    /**
     * Request data be extracted from the buffer.  The data returned may
     * be of length equal to or less than that requested.  CHECK IT!
     * 
     * @param length
     *      The number of bytes requested to extract
     * @return
     */
    public byte[] extractData(int length) throws IOException;
    
    
    /**
     * Tear down this byte buffer.  Dispose of any resources 
     * created and release memory
     */
    public void cleanUp();
}
