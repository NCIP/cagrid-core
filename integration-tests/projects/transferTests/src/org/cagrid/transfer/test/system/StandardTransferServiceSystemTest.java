package org.cagrid.transfer.test.system;


public class StandardTransferServiceSystemTest extends TransferServiceTest {
    
    @Override
    protected boolean streamingTest() {
        return false;
    }


    @Override
    protected boolean secureTest() {
        return false;
    }
}
