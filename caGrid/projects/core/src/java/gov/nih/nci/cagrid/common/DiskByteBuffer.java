package gov.nih.nci.cagrid.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.UUID;

public class DiskByteBuffer implements ByteBuffer {
    
    public static final long DEFAULT_BYTES_PER_FILE = Integer.MAX_VALUE;
    public static final String DEFAULT_BUFFER_DIR_NAME = "DiskByteBuffer";
    
    public static final File DEFAULT_BUFFER_DIR = new File(Utils.getCaGridUserHome(), DEFAULT_BUFFER_DIR_NAME);
    
    private long maxBytesPerFile;
    private File bufferDir;
    private long uniqueFileIndex;
    
    private LinkedList<File> bufferFiles = null;
    private long currentReadIndex;
    private long currentWriteIndex;
    private FileOutputStream bufferOutput = null;
    private FileInputStream bufferInput = null;
    
    public DiskByteBuffer() {
        this(DEFAULT_BUFFER_DIR, DEFAULT_BYTES_PER_FILE);
    }
    
    
    public DiskByteBuffer(File bufferStorageDir, long maxBytesPerFile) {
        String uniqueFilePart = UUID.randomUUID().toString();
        this.bufferDir = new File(bufferStorageDir, uniqueFilePart);
        if (!bufferDir.exists()) {
            this.bufferDir.mkdirs();
        }
        this.maxBytesPerFile = maxBytesPerFile;
        this.bufferFiles = new LinkedList<File>();
        this.currentReadIndex = maxBytesPerFile;
        this.uniqueFileIndex = 0;
    }
    

    public void appendData(byte[] data, int length) throws IOException {
        int totalWritten = 0;
        while (totalWritten < length) {
            if (currentWriteIndex == maxBytesPerFile || bufferOutput == null) {
                initNextBuffer();
            }
            long canWrite = maxBytesPerFile - currentWriteIndex;
            long tryToWrite = Math.min(length, canWrite);
            bufferOutput.write(data, totalWritten, (int) tryToWrite);
            totalWritten += tryToWrite;
            currentWriteIndex += tryToWrite;
        }
    }


    public synchronized void cleanUp() {
        if (bufferInput != null) {
            try {
                bufferInput.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        if (bufferOutput != null) {
            try {
                bufferOutput.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        Utils.deleteDir(bufferDir);
    }


    public byte[] extractData(int length) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while (out.size() < length && bufferFiles.size() != 0) {
            if (currentReadIndex == maxBytesPerFile) {
                advanceBufferReader();
            }
            if (bufferInput == null) {
                // no more buffer files to read
                break;
            }
            // a temporary place to stuff bytes into from the file system
            byte[] temp = new byte[2048];
            int bytesRead = bufferInput.read(temp);
            if (bytesRead != -1) {
                currentReadIndex += bytesRead;
                out.write(temp, 0, bytesRead);
            } else {
                break;
            }
        }
        return out.toByteArray();
    }
    
    
    private void initNextBuffer() throws IOException {
        synchronized (bufferFiles) {
            System.out.flush();
            // close out the previous writer, if any
            if (bufferOutput != null) {
                bufferOutput.flush();
                bufferOutput.close();
            }
            // create the next buffer file
            File bufferFile = new File(bufferDir, String.valueOf(uniqueFileIndex));
            uniqueFileIndex++;
            // reset the write index
            currentWriteIndex = 0;
            // start up the next buffer writer
            bufferOutput = new FileOutputStream(bufferFile);
            // keep a reference to the buffer
            bufferFiles.addLast(bufferFile);
        }
    }
    
    
    private void advanceBufferReader() throws IOException {
        synchronized (bufferFiles) {
            System.out.flush();
            // close the current reader, if any
            if (bufferInput != null) {
                bufferInput.close();
                // if we were reading, we were reading from the head file 
                // in the bufferFiles list... that can be deleted
                File removeme = bufferFiles.removeFirst();
                System.out.flush();
                removeme.delete();
            }
            currentReadIndex = 0;
            if (bufferFiles.size() != 0) {
                bufferInput = new FileInputStream(bufferFiles.getFirst());
            } else {
                bufferInput = null;
            }
        }
    }
}
