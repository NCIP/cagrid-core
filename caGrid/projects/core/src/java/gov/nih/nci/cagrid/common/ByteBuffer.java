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
