package gov.nih.nci.cagrid.introduce.portal.updater.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.beans.software.ExtensionType;
import gov.nih.nci.cagrid.introduce.beans.software.IntroduceType;
import gov.nih.nci.cagrid.introduce.beans.software.SoftwareType;
import gov.nih.nci.cagrid.introduce.codegen.SyncTools;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.pietschy.wizard.InvalidStateException;
import org.pietschy.wizard.PanelWizardStep;


public class DownloadsUpdatesStep extends PanelWizardStep {
    
    private static final Logger logger = Logger.getLogger(DownloadsUpdatesStep.class);

    private SoftwareType softwareUpdates;

    private JPanel descriptionPanel = null;

    private JPanel busyPanel = null;

    private JProgressBar busyProgressBar = null;

    private JPanel updatesPanel = null;

    private JTextArea statusTextArea = null;

    private CheckForUpdatesStep updatesStep = null;


    /**
     * This method initializes
     */
    public DownloadsUpdatesStep(CheckForUpdatesStep updatesStep) {
        super("Downloading Updates", "Retrieving update files from remote site.");
        initialize();
        this.updatesStep = updatesStep;
    }


    public void applyState() throws InvalidStateException {
    }


    public void downloadUpdates() {
        Thread th = new Thread(new Runnable() {

            public void run() {
                // TODO Auto-generated method stub
                getBusyProgressBar().setIndeterminate(true);
                // get the selected set of software chosen in the previous step
                softwareUpdates = updatesStep.getRequestedDownloads();
                File updatesDir = new File("." + File.separator + "updates");
                updatesDir.mkdirs();

                IntroduceType[] introduceTypes = softwareUpdates.getIntroduce();
                if (introduceTypes != null) {
                    for (int i = 0; i < introduceTypes.length; i++) {
                        if (!introduceTypes[i].getIsInstalled().booleanValue()) {
                            URL url = null;
                            try {
                                url = new URL(introduceTypes[i].getZipFileURL().toString());
                            } catch (MalformedURLException e) {
                                logger.error(e);
                                break;
                            }
                            URLConnection connection = null;
                            try {
                                connection = url.openConnection();
                                addStatusLine("Downloading Introduce " + introduceTypes[i].getVersion() + " ("
                                    + connection.getContentLength() / 1024 / 1024 + " MB)");
                                getBusyProgressBar().setMinimum(0);
                                getBusyProgressBar().setMaximum(connection.getContentLength());
                                getBusyProgressBar().setValue(0);
                            } catch (IOException e) {
                                e.printStackTrace();
                                break;
                            }
                            try {
                                InputStream stream = connection.getInputStream();
                                FileOutputStream fos = new FileOutputStream(new File(updatesDir.getAbsolutePath()
                                    + File.separator + "introduce" + introduceTypes[i].getVersion() + ".zip"));
                                getBusyProgressBar().setIndeterminate(false);
                                byte[] bytes = new byte[1024];
                                int read = stream.read(bytes);
                                final int length = read;
                                while (read > 0) {
                                    SwingUtilities.invokeLater(new Runnable() {
                                        public void run() {
                                            getBusyProgressBar().setValue(getBusyProgressBar().getValue() + length);
                                        }
                                    });
                                    fos.write(bytes, 0, read);
                                    read = stream.read(bytes);
                                }
                                fos.close();
                                stream.close();

                            } catch (IOException e) {
                                e.printStackTrace();
                                break;
                            }
                        }

                        if (introduceTypes[i].getIntroduceRev() != null && introduceTypes[i].getIntroduceRev(0) != null) {
                            // need to get the patch
                            URL url = null;
                            try {
                                url = new URL(introduceTypes[i].getIntroduceRev(0).getZipFileURL().toString());
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                                break;
                            }
                            URLConnection connection = null;
                            try {
                                connection = url.openConnection();
                                addStatusLine("Downloading Introduce Patch "
                                    + introduceTypes[i].getIntroduceRev(0).getPatchVersion() + " ("
                                    + connection.getContentLength() / 1024 / 1024 + " MB)");
                                getBusyProgressBar().setMinimum(0);
                                getBusyProgressBar().setMaximum(connection.getContentLength());
                                getBusyProgressBar().setValue(0);
                            } catch (IOException e) {
                                e.printStackTrace();
                                break;
                            }
                            try {
                                InputStream stream = connection.getInputStream();
                                FileOutputStream fos = new FileOutputStream(new File(updatesDir.getAbsolutePath()
                                    + File.separator + "introduce" + introduceTypes[i].getVersion() + "Patch"
                                    + introduceTypes[i].getIntroduceRev(0).getPatchVersion() + ".zip"));
                                getBusyProgressBar().setIndeterminate(false);
                                byte[] bytes = new byte[1024];
                                int read = stream.read(bytes);
                                final int length = read;
                                while (read > 0) {
                                    SwingUtilities.invokeLater(new Runnable() {
                                        public void run() {
                                            getBusyProgressBar().setValue(getBusyProgressBar().getValue() + length);
                                        }
                                    });
                                    fos.write(bytes, 0, read);
                                    read = stream.read(bytes);
                                }
                                fos.close();
                                stream.close();

                            } catch (IOException e) {
                                e.printStackTrace();
                                break;
                            }
                        }
                    }
                }

                ExtensionType[] extensionTypes = softwareUpdates.getExtension();
                if (extensionTypes != null) {
                    for (int i = 0; i < extensionTypes.length; i++) {
                        URL url = null;
                        try {
                            url = new URL(extensionTypes[i].getZipFileURL().toString());
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                            break;
                        }
                        URLConnection connection = null;
                        try {
                            connection = url.openConnection();
                            if (extensionTypes[i].getVersion() != null) {
                                addStatusLine("Downloading " + extensionTypes[i].getDisplayName() + " version "
                                    + extensionTypes[i].getVersion() + " (" + connection.getContentLength() / 1024
                                    / 1024 + " MB)");
                            } else {
                                addStatusLine("Downloading " + extensionTypes[i].getDisplayName() + " version "
                                    + "initial version" + " (" + connection.getContentLength() / 1024 / 1024 + " MB)");
                            }
                            getBusyProgressBar().setMinimum(0);
                            getBusyProgressBar().setMaximum(connection.getContentLength());
                            getBusyProgressBar().setValue(0);
                        } catch (IOException e) {
                            e.printStackTrace();
                            break;
                        }
                        try {
                            InputStream stream = connection.getInputStream();
                            FileOutputStream fos = new FileOutputStream(new File(updatesDir.getAbsolutePath()
                                + File.separator + extensionTypes[i].getName() + extensionTypes[i].getVersion()
                                + ".zip"));
                            getBusyProgressBar().setIndeterminate(false);
                            byte[] bytes = new byte[1024];
                            int read = stream.read(bytes);
                            final int length = read;
                            while (read > 0) {
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        getBusyProgressBar().setValue(getBusyProgressBar().getValue() + length);
                                    }
                                });
                                fos.write(bytes, 0, read);
                                read = stream.read(bytes);
                            }
                            fos.close();
                            stream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                }

                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        getBusyProgressBar().setIndeterminate(true);
                    }
                });

                addStatusLine("");
                addStatusLine("Writting out software updates description...");

                try {
                    Utils.serializeObject(softwareUpdates, new QName("gme://gov.nih.nci.cagrid.introduce/1/Software",
                        "Software"), new FileWriter(new File("." + File.separator + "updates" + File.separator
                        + "software.xml")));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                addStatusLine("");
                addStatusLine("Finished.");

                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        getBusyProgressBar().setIndeterminate(false);
                    }
                });

                setComplete(true);
            }

        });
        th.start();
    }


    public void prepare() {
        getStatusTextArea().setText("");
        addStatusLine("Preparing to download updates...\n");
        downloadUpdates();
    }


    private void addStatusLine(String statusLine) {
        getStatusTextArea().setText(getStatusTextArea().getText() + statusLine + "\n");
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.weightx = 1.0D;
        gridBagConstraints1.weighty = 1.0D;
        gridBagConstraints1.fill = GridBagConstraints.BOTH;
        gridBagConstraints1.gridheight = 2;
        gridBagConstraints1.gridy = 0;
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.weightx = 1.0D;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.0D;
        gridBagConstraints.gridy = 1;
        this.setLayout(new GridBagLayout());
        this.setSize(new Dimension(342, 270));
        this.add(getDescriptionPanel(), gridBagConstraints1);
    }


    /**
     * This method initializes descriptionPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getDescriptionPanel() {
        if (descriptionPanel == null) {
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.fill = GridBagConstraints.BOTH;
            gridBagConstraints2.gridy = 1;
            gridBagConstraints2.weightx = 1.0D;
            gridBagConstraints2.weighty = 0.2D;
            gridBagConstraints2.gridwidth = 1;
            gridBagConstraints2.gridx = 0;
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.gridx = 0;
            gridBagConstraints8.fill = GridBagConstraints.BOTH;
            gridBagConstraints8.weightx = 1.0D;
            gridBagConstraints8.weighty = 1.0D;
            gridBagConstraints8.gridwidth = 1;
            gridBagConstraints8.gridheight = 1;
            gridBagConstraints8.gridy = 0;
            descriptionPanel = new JPanel();
            descriptionPanel.setLayout(new GridBagLayout());
            descriptionPanel.setFont(new Font("Dialog", Font.PLAIN, 12));
            descriptionPanel.add(getUpdatesPanel(), gridBagConstraints8);
            descriptionPanel.add(getBusyPanel(), gridBagConstraints2);
        }
        return descriptionPanel;
    }


    /**
     * This method initializes busyPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getBusyPanel() {
        if (busyPanel == null) {
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.weightx = 1.0D;
            gridBagConstraints4.gridy = 0;
            busyPanel = new JPanel();
            busyPanel.setLayout(new GridBagLayout());
            busyPanel.add(getBusyProgressBar(), gridBagConstraints4);
        }
        return busyPanel;
    }


    /**
     * This method initializes busyProgressBar
     * 
     * @return javax.swing.JProgressBar
     */
    private JProgressBar getBusyProgressBar() {
        if (busyProgressBar == null) {
            busyProgressBar = new JProgressBar();
            busyProgressBar.setPreferredSize(new Dimension(148, 16));
        }
        return busyProgressBar;
    }


    /**
     * This method initializes updatesPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getUpdatesPanel() {
        if (updatesPanel == null) {
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = GridBagConstraints.BOTH;
            gridBagConstraints3.gridy = 0;
            gridBagConstraints3.weightx = 1.0;
            gridBagConstraints3.weighty = 1.0;
            gridBagConstraints3.gridx = 0;
            updatesPanel = new JPanel();
            updatesPanel.setLayout(new GridBagLayout());
            updatesPanel.add(getStatusTextArea(), gridBagConstraints3);
        }
        return updatesPanel;
    }


    /**
     * This method initializes statusTextArea
     * 
     * @return javax.swing.JTextArea
     */
    private JTextArea getStatusTextArea() {
        if (statusTextArea == null) {
            statusTextArea = new JTextArea();
            statusTextArea.setEditable(false);
            statusTextArea.setFont(new Font("Sanserif", Font.PLAIN, 10));
        }
        return statusTextArea;
    }
} // @jve:decl-index=0:visual-constraint="10,10"
