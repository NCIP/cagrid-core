package gov.nih.nci.cagrid.introduce.portal.common;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;


public class LogPanel extends JPanel {

    private static final Logger logger = Logger.getLogger(LogPanel.class); //@jve
    // :
    // decl
    // -
    // index
    // =
    // 0:

    private JTextArea logTextArea = null;

    private String fileName = null;

    private JScrollPane textScrollPane = null;

    private Thread th = null;

    private boolean cancel = false;


    /**
     * This method initializes
     */
    public LogPanel(String fileName) {
        super();
        this.fileName = fileName;
        initialize();
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.setLayout(new GridBagLayout());
        this.add(getTextScrollPane(), gridBagConstraints);

    }


    public void cancel() {
        this.cancel = true;
    }


    /**
     * This method initializes logTextArea
     * 
     * @return javax.swing.JTextArea
     */
    private synchronized JTextArea getLogTextArea() {
        if (this.logTextArea == null) {
            this.logTextArea = new JTextArea();
            this.logTextArea.setEditable(false);
            this.logTextArea.setFont(new Font("Lucida Console", Font.PLAIN, 10));
            try {

                final BufferedReader in = new BufferedReader(new FileReader(LogPanel.this.fileName));
                StringBuffer sb = new StringBuffer();
                for (String s = null; (s = in.readLine()) != null;) {
                    sb.append(s + "\n");
                }
                final String contents = sb.toString();

                this.logTextArea.insert(contents, 0);
                this.logTextArea.setCaretPosition(this.logTextArea.getText().length());

                Runnable reader = new Runnable() {

                    public void run() {
                        try {
                            String line;
                            while (true) {
                                line = in.readLine();
                                if (line != null) {
                                    final String finalLine = line;
                                    final int oldLength = LogPanel.this.logTextArea.getText().length();

                                    LogPanel.this.logTextArea.insert(finalLine + "\n", oldLength + 1);
                                    LogPanel.this.logTextArea.setCaretPosition(LogPanel.this.logTextArea.getText().length());
                                } else {
                                    try {
                                        Thread.sleep(500);
                                    } catch (Throwable t) {
                                        t.printStackTrace();
                                    }
                                }

                                if (LogPanel.this.cancel) {
                                    in.close();
                                    return;
                                }
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                };
                this.th = new Thread(reader);
                this.th.start();
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        }
        return this.logTextArea;
    }


    /**
     * This method initializes textScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getTextScrollPane() {
        if (this.textScrollPane == null) {
            this.textScrollPane = new JScrollPane();
            this.textScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            this.textScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            this.textScrollPane.setViewportView(getLogTextArea());
        }
        return this.textScrollPane;
    }

}
