package org.cagrid.installer.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;


public class DownloadPropertiesThread extends Thread {

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
        try {
            File to = new File(this.toFile);
            URL from = new URL(this.fromUrl);

            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(to));
            InputStream in = from.openStream();
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            out.flush();
            out.close();
            in.close();

            this.finished = true;
        } catch (Exception ex) {
            this.ex = ex;
        }
    }


    public boolean isFinished() {
        return this.finished;
    }

}