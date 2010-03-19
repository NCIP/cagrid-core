package org.cagrid.installer.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


public class DownloadPropertiesThread extends Thread {

    private static final int BUFFER_SIZE = 1024;

    private static final int LOGAFTER_SIZE = BUFFER_SIZE * 1000;
    
    private Exception ex;

    private String fromUrl;

    private String toFile;

    private boolean finished;


    DownloadPropertiesThread(String fromUrl, String toFile) {
        this.fromUrl = fromUrl;
        this.toFile = toFile;
        this.setDaemon(true);
    }


    Exception getException() {
        return this.ex;
    }


    public void run() {
         this.finished = false;
        
        try {
            File to = new File(this.toFile);
            URL from = new URL(this.fromUrl);
            
            URLConnection conn = from.openConnection();
            conn.connect();
            long totalBytes = conn.getContentLength();
            InputStream in = conn.getInputStream();

            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(to));
            byte[] buffer = new byte[BUFFER_SIZE];
            int len = -1;
            int bytesRead = 0;
            int nextLog = -1;
            String lastMsg = null;
            
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
                bytesRead += len;
                
                if (bytesRead > nextLog) {
                    nextLog += LOGAFTER_SIZE;
                    double percent = bytesRead / (double) totalBytes;
                    String currMsg = "";
                    if (percent > 0) {
                        currMsg = Math.round(percent * 100) + " % complete";
                    } else {
                        currMsg = "Read " + bytesRead + " bytes of unknown total size.";
                    }

                    if (!currMsg.equals(lastMsg)) {
                        System.out.println(currMsg);
                    }
                    lastMsg = currMsg;
                }
                else if (bytesRead == totalBytes) {
                    this.finished = true;
                }
            } 
            
            in.close();
            out.flush();
            out.close();

        } catch (Exception ex) {
            this.ex = ex;
        }
    }


    public boolean isFinished() {
        return this.finished;
    }

}