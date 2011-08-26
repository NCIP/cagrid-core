package org.cagrid.transfer.test.system;


public class SecureStandardTransferServiceSystemTest extends TransferServiceTest {
    
    @Override
    protected boolean streamingTest() {
        return false;
    }


    @Override
    protected boolean secureTest() {
        return true;
    }
}
