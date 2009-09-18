package gov.nih.nci.cagrid.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Provides a central control point for writing data into a queue and 
 * reading it back out asynchronously.
 * 
 * @author David
 *
 */
public class ByteQueue {

    private ByteBuffer byteBuffer = null;
    private OutputStream output = null;
    private InputStream input = null;
    private boolean streamOpen;
    private Object writeLock = null;
    
    public ByteQueue(ByteBuffer buffer) {
        this.byteBuffer = buffer;
        this.output = new ByteQueueOutputStream(byteBuffer);
        this.input = new ByteQueueInputStream(byteBuffer);
        this.streamOpen = true;
        this.writeLock = new Object();
    }
    
    
    public OutputStream getByteOutputStream() {
        return this.output;
    }
    
    
    public InputStream getByteInputStream() {
        return this.input;
    }
    
    
    public void cleanUp() {
        byteBuffer.cleanUp();
    }
    
    
    public void finalize() {
        cleanUp();
    }
    
    
    private class ByteQueueOutputStream extends OutputStream {
        
        private ByteBuffer buffer = null;
        private byte[] temp = null;
        private int bytesWritten;
        
        public ByteQueueOutputStream(ByteBuffer buffer) {
            this.buffer = buffer;
            this.temp = new byte[4096];
            this.bytesWritten = 0;
        }
        
        
        public void flush() throws IOException {
            synchronized (writeLock) {
                buffer.appendData(temp, bytesWritten);
                bytesWritten = 0;
                writeLock.notifyAll();
            }
        }
        
        
        public void close() throws IOException {
            synchronized (writeLock) {
                streamOpen = false;
                flush();
            }
        }
        
        
        public void write(int b) throws IOException {
            temp[bytesWritten++] = (byte) b;
            if (bytesWritten == temp.length) {
                flush();
            }
        }
    }
    
    
    private class ByteQueueInputStream extends InputStream {
        
        private static final int DEFAULT_BUFFER_LENGTH = 4096;
        
        private ByteBuffer buffer = null;
        private byte[] temp = null;
        private int tempIndex = 0;
        
        public ByteQueueInputStream(ByteBuffer buffer) {
            this.buffer = buffer;
        }

        
        public int read() throws IOException {
            if (temp == null || tempIndex == temp.length) {
                temp = buffer.extractData(DEFAULT_BUFFER_LENGTH);
                while (temp.length == 0 && streamOpen) {
                    // no data to read, but more should be coming in!
                    // wait for the writer side of things to notify us
                    synchronized (writeLock) {
                        try {
                            writeLock.wait();
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                            throw new IOException("Error waiting for data: " + ex.getMessage());
                        }
                    }
                    temp = buffer.extractData(DEFAULT_BUFFER_LENGTH);
                }
                tempIndex = 0;
            }
            int i = -1;
            if (temp.length != 0) {
                i = temp[tempIndex];
                tempIndex++;
            }
            return i;
        }
    }
}
