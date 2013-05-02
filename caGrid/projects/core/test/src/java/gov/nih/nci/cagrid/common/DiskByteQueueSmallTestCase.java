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

public class DiskByteQueueSmallTestCase extends BaseByteQueueTest {

    protected ByteBuffer getByteBuffer() {
        return new DiskByteBuffer(DiskByteBuffer.DEFAULT_BUFFER_DIR, 128);
    }
}
