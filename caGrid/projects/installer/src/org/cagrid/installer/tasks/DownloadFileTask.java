/**
 * 
 */
package org.cagrid.installer.tasks;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.installer.model.CaGridInstallerModel;
import org.cagrid.installer.steps.Constants;
import org.cagrid.installer.util.InstallerUtils;
import org.cagrid.installer.util.MD5Checksum;
import org.pietschy.wizard.InvalidStateException;


/**
 * @author <a href="mailto:joshua.phillips@semanticbits.com">Joshua Phillips</a>
 */
public class DownloadFileTask extends BasicTask {

    private static final int BUFFER_SIZE = 1024;

    private static final int LOGAFTER_SIZE = BUFFER_SIZE * 1000;

    private static final int NUM_ATTEMPTS = 2;

    private static final Log logger = LogFactory.getLog(DownloadFileTask.class);

    private String fromUrlProp;

    private String toFileProp;

    private String checksumProp;

    // Max time to wait for connection
    private long connectTimeout;


    /**
     * @param name
     * @param description
     */
    public DownloadFileTask(String name, String description, String fromUrlProp, String toFileProp,
        String checksumProp, long timeout) {
        super(name, description);
        this.fromUrlProp = fromUrlProp;
        this.toFileProp = toFileProp;
        this.checksumProp = checksumProp;
        this.connectTimeout = timeout;
    }


    @Override
    protected Object internalExecute(CaGridInstallerModel model) throws Exception {

        String fromUrl = model.getProperty(this.fromUrlProp);

        URL url = null;
        try {
            url = new URL(fromUrl);
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Bad URL: '" + fromUrl + "'", ex);
        }

        String toFile = model.getProperty(Constants.TEMP_DIR_PATH) + "/" + model.getProperty(this.toFileProp);

        boolean enforceChecksum = true;
        String expectedChecksum = model.getProperty(this.checksumProp);
        if (InstallerUtils.isEmpty(expectedChecksum)) {
            enforceChecksum = false;
            logger.warn("No checksum specified for '" + fromUrl + "'");
        }

        File toFileFile = new File(toFile);

        String checksum = null;
        boolean alreadyDownloaded = false;
        if (toFileFile.exists()) {
            if (enforceChecksum) {
                checksum = MD5Checksum.getChecksum(toFile);
                if (enforceChecksum && !expectedChecksum.equals(checksum)) {
                    logger.warn("Cached file does not match checksum, will attept to download");
                } else {
                    alreadyDownloaded = true;
                }
            }
        } 

        if(!alreadyDownloaded){

            int i = 0;
            for (i = 0; i < NUM_ATTEMPTS; i++) {
                try {
                    download(url, new File(toFile), this.connectTimeout);
                    if (!enforceChecksum) {
                        break;
                    } else {
                        checksum = MD5Checksum.getChecksum(toFile);
                        if (expectedChecksum.equals(checksum)) {
                            break;
                        } else {
                            logger.warn("Downloaded file '" + toFile + "' is corrupted.  Expected checksum ("
                                + expectedChecksum + ") but recieved (" + checksum + ").");
                        }
                    }
                } catch (Exception ex) {
                    logger.warn("Failed attempt (" + (i + 1) + ") to download '" + fromUrl + "': " + ex.getMessage(),
                        ex);
                }
            }

            if (i == NUM_ATTEMPTS) {
                throw new InvalidStateException("Could not download '" + fromUrl + "'. See logs for details.");
            }

            if (enforceChecksum && !expectedChecksum.equals(checksum)) {
                throw new InvalidStateException("Could not download '" + fromUrl + "'. See logs for details.");
            }
        }

        return null;
    }


    private static void download(URL fromUrl, File toFile, long connectTimeout) throws Exception {

        ConnectThread t = new ConnectThread(fromUrl);
        t.start();
        try {
            t.join(connectTimeout);
        } catch (InterruptedException ex) {
            throw new RuntimeException("Thread interrupted", ex);
        }

        if (t.getEx() != null) {
            throw new RuntimeException("Error connecting to " + fromUrl + ": " + t.getEx().getMessage(), t.getEx());
        }
        if (!t.isFinished()) {
            throw new RuntimeException("Connection to " + fromUrl + " timed out.");
        }
        InputStream inputStream = t.getIn();

        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(toFile));
        byte[] buffer = new byte[BUFFER_SIZE];
        int len = -1;
        int bytesRead = 0;
        int nextLog = -1;
        String lastMsg = null;
        while ((len = inputStream.read(buffer)) != -1) {
            out.write(buffer, 0, len);
            bytesRead += len;

            if (bytesRead > nextLog) {
                nextLog += LOGAFTER_SIZE;
                double percent = bytesRead / (double) t.getTotalBytes();
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

        }
        out.flush();
        out.close();
        inputStream.close();
    }


    private static class ConnectThread extends Thread {
        private InputStream in;

        private Exception ex;

        private boolean finished;

        private URL url;

        int totalBytes;


        ConnectThread(URL url) {
            this.url = url;
            this.setDaemon(true);
        }


        @Override
        public void run() {
            try {
                URLConnection conn = this.url.openConnection();
                conn.connect();
                this.totalBytes = conn.getContentLength();
                this.in = conn.getInputStream();
                this.finished = true;
            } catch (Exception e) {
                this.ex = e;
            }
        }


        int getTotalBytes() {
            return this.totalBytes;
        }


        Exception getEx() {
            return this.ex;
        }


        boolean isFinished() {
            return this.finished;
        }


        InputStream getIn() {
            return this.in;
        }
    }
}
