package gov.nih.nci.cagrid.common;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;

public class MemoryByteBuffer implements ByteBuffer {
    
    private LinkedList<byte[]> byteChunks = null;
    
    public MemoryByteBuffer() {
        this.byteChunks = new LinkedList<byte[]>();
    }
    

    public void appendData(byte[] data, int length) {
        // ignorant implementation, just stuff the bytes into the queue
        byte[] storeme = new byte[length];
        System.arraycopy(data, 0, storeme, 0, length);
        byteChunks.add(storeme);
    }


    public byte[] extractData(int length) {
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        while (bytesOut.size() < length && byteChunks.size() != 0) {
            // get the first chunk of data out
            byte[] chunk = byteChunks.removeFirst();
            // figure out how many bytes to write out
            int maxBytesToWrite = length - bytesOut.size();
            int numBytes = Math.min(chunk.length, maxBytesToWrite);
            // write them into the output
            bytesOut.write(chunk, 0, numBytes);
            // if the chunk was bigger than we could fit, save the rest of the bytes for later
            int extraBytes = chunk.length - numBytes;
            if (extraBytes > 0) {
                byte[] extra = new byte[extraBytes];
                System.arraycopy(chunk, numBytes, extra, 0, extraBytes);
                byteChunks.addFirst(extra);
            }
        }
        // return the bytes
        return bytesOut.toByteArray();
    }
    
    
    public void cleanUp() {
        byteChunks = null;
    }
}
