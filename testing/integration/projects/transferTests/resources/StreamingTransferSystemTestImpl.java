package org.cagrid.transfer.system.test.service;

import java.rmi.RemoteException;

import org.cagrid.transfer.context.service.helper.TransferServiceHelper;


/**
 * TODO:I am the service side implementation class. IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.2
 */
public class TransferSystemTestImpl extends TransferSystemTestImplBase {

    public TransferSystemTestImpl() throws RemoteException {
        super();
    }


    public org.cagrid.transfer.context.stubs.types.TransferServiceContextReference createStreamingTransferMethodStep()
        throws RemoteException {
        java.io.InputStream is = new java.io.InputStream() {
            int bytesRead = 0;
            java.util.Random rand = new java.util.Random(System.currentTimeMillis());
            int mbWrote = 0;
            boolean streamOpen = true;


            @Override
            public int read() throws java.io.IOException {
                // TODO Auto-generated method stub
                if (streamOpen) {
                    int next = rand.nextInt();
                    bytesRead++;
                    if (bytesRead % 1024 == 0) {
                        mbWrote++;
                        System.out.print(".");
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println(mbWrote);
                        if (mbWrote == 50) {
                            streamOpen = false;
                            return -1;
                        }
                    }
                    return next;

                } else {
                    return -1;

                }
            }

        };

        return org.cagrid.transfer.context.service.helper.TransferServiceHelper.createTransferContext(is, null);
    }
}
