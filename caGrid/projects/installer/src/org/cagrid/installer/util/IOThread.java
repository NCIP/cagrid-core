/*
 * Created on Jun 10, 2006
 */
package org.cagrid.installer.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;


public class IOThread extends Thread {
    private InputStream is;
    private PrintStream out;
    private StringBuffer sb;
    private Exception exception;


    public IOThread(InputStream is, PrintStream out, StringBuffer sb) {
        super();

        this.is = is;
        this.out = out;
        this.sb = sb;
        this.setDaemon(true);
    }


    public void run() {
        this.exception = null;
        try {
            InputStreamReader in = new InputStreamReader(is);

            int len = -1;
            char[] buf = new char[1024];
            while ((len = in.read(buf)) != -1) {
                if (out != null)
                    out.print(new String(buf, 0, len));
                if (sb != null)
                    sb.append(buf, 0, len);
            }

            out.flush();
            in.close();
            is.close();
        } catch (IOException e) {
            this.exception = e;
            e.printStackTrace();
        }
    }


    public Exception getException() {
        return exception;
    }
}
