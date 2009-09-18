package gov.nih.nci.cagrid.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;


public class FixedPortionFileInputStream extends InputStream {
    File file;
    long offset;
    long length;
    RandomAccessFile randFile;
    long amountRead = 0;


    public FixedPortionFileInputStream(File file, long offset, long length) throws Exception {
        this.file = file;
        this.offset = offset;
        this.length = length;
        randFile = new RandomAccessFile(this.file, "r");
        randFile.seek(offset);
    }


    public long getAmountRead() {
        return this.amountRead;
    }


    public long getOffset() {
        return this.getOffset();
    }


    @Override
    public void close() throws IOException {
        super.close();
        randFile.close();
    }


    public int read() throws IOException {
        if (amountRead < length) {
            int b = randFile.read();
            if (b != -1) {
                amountRead++;
            }
            return b;
        } else {
            return -1;
        }
    }
}