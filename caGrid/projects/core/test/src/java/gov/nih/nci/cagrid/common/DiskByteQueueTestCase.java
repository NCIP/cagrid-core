package gov.nih.nci.cagrid.common;

public class DiskByteQueueTestCase extends BaseByteQueueTest {

    protected ByteBuffer getByteBuffer() {
        return new DiskByteBuffer();
    }
}
