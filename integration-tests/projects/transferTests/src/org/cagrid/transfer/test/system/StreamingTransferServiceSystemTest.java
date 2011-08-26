package org.cagrid.transfer.test.system;


public class StreamingTransferServiceSystemTest extends TransferServiceTest {
    
    @Override
    protected boolean streamingTest() {
        return true;
    }


    @Override
    protected boolean secureTest() {
        return true;
    }
}
