package gov.nih.nci.cagrid.common;

public class MemoryByteQueueTestCase extends BaseByteQueueTest {

    protected ByteBuffer getByteBuffer() {
        return new MemoryByteBuffer();
    }
}
