package org.cagrid.grape.utils;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;


public abstract class BusyDialogRunnable implements Runnable {
    final private BusyDialog dialog;
    private String errorMessage = "";
    private boolean valid = true;


    public BusyDialogRunnable(JFrame owner, String title) {
        this(new BusyDialog(owner, "Progress (" + title + ")"));
    }


    public BusyDialogRunnable(BusyDialog dialog) {
        this.dialog = dialog;
        this.dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }


    public void setProgressText(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                BusyDialogRunnable.this.dialog.setProgressText(text);
                
            }
        });
    }


    public void run() {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                BusyDialogRunnable.this.dialog.setVisible(true);
            }
        });
        thread.start();
        try {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    BusyDialogRunnable.this.dialog.getProgress().setIndeterminate(true);
                }
            });
            process();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    BusyDialogRunnable.this.dialog.getProgress().setIndeterminate(false);
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
            this.valid = false;
            this.errorMessage = e.getMessage();
        }

        if (!this.valid) {
            JOptionPane.showMessageDialog(this.dialog.getOwner(), this.errorMessage);
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                BusyDialogRunnable.this.dialog.dispose();
            }
        });

    }


    public abstract void process();


    public String getErrorMessage() {
        return this.errorMessage;
    }


    public void setErrorMessage(String message) {
        this.valid = false;
        this.errorMessage = message;
    }

}
