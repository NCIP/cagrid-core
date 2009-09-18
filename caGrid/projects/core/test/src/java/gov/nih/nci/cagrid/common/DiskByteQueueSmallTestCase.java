package gov.nih.nci.cagrid.common;

public class DiskByteQueueSmallTestCase extends BaseByteQueueTest {

    protected ByteBuffer getByteBuffer() {
        return new DiskByteBuffer(DiskByteBuffer.DEFAULT_BUFFER_DIR, 128);
    }
}
