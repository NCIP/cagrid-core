package org.cagrid.installer.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.installer.steps.Constants;

public class DownloadPropertiesUtils {
    
    private static final Log logger = LogFactory.getLog(DownloadPropertiesUtils.class);

    public static Properties getDownloadedProps() {

        Properties defaultProps = null;

        InputStream propsIn = null;
        String downloadPropsFileName = System.getProperty("download.properties");
        if (downloadPropsFileName != null) {
            try {
                propsIn = new FileInputStream(downloadPropsFileName);
            } catch (Exception ex) {
                InstallerUtils.handleException("Couldn't load download.properties file '" + downloadPropsFileName + "': "
                    + ex.getMessage(), ex);
            }
        } else {
            try {
                propsIn = Thread.currentThread().getContextClassLoader().getResourceAsStream("download.properties");
            } catch (Exception ex) {
                InstallerUtils.handleException("Error loading default download.properties resource: " + ex.getMessage(), ex);
            }
            if (propsIn == null) {
                InstallerUtils.handleException("Couldn't find download.properties resource.", null);
            }
        }
        String downloadUrl = null;

        Properties downloadProps = new Properties();
        try {
            downloadProps.load(propsIn);
            downloadUrl = downloadProps.getProperty(Constants.DOWNLOAD_URL);
        } catch (Exception ex) {
            InstallerUtils.handleException("Error loading download.properties", ex);
        }
        logger.info("Downloading default properties from: " + downloadUrl);

        File toFile = null;
        try {
            String tempDir = new File(System.getProperty("java.io.tmpdir")).getAbsolutePath();
            toFile = new File(tempDir + "/download.properties");
            if (toFile.exists()) {
                toFile.delete();
            }
        } catch (Exception ex) {
            InstallerUtils.handleException("Getting path for download.properties", ex);
        }
        DownloadPropertiesThread dpt = new DownloadPropertiesThread(downloadUrl, toFile.getAbsolutePath());
        dpt.start();
        try {
            dpt.join(Constants.CONNECT_TIMEOUT);
        } catch (InterruptedException ex) {
            InstallerUtils.handleException("Download thread interrupted", ex);
        }

        if (dpt.getException() != null) {
            Exception ex = dpt.getException();
            String msg = "Error loading default properties: " + ex.getMessage();
            InstallerUtils.handleException( msg, ex);
        }

        if (!dpt.isFinished()) {
            String msg = "Download of default properties timed out.";
            InstallerUtils.handleException(msg, new Exception(msg));
        }
        try {
            defaultProps = new Properties();
            defaultProps.load(new FileInputStream(toFile));
        } catch (Exception ex) {
            InstallerUtils.handleException("Error loading default properties: " + ex.getMessage(), ex);
        }

        return defaultProps;
    }

}
