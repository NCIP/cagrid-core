package org.cagrid.gaards.dorian.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public abstract class LoggingObject {

    protected Log log;
    protected boolean loggerLookup = false;


    private void initLogger() {
        if (!loggerLookup) {
            log = LogFactory.getLog(this.getClass().getName());
            loggerLookup = true;
        }
    }


    public void debug(String s) {
        initLogger();
        if (log != null) {
            log.debug(s);
        }
    }


    public void info(String s) {
        initLogger();
        if (log != null) {
            log.info(s);
        }
    }


    public void logWarning(String s) {
        initLogger();
        if (log != null) {
            log.warn(s);
        }
    }


    public void logWarning(String s, Throwable thrown) {
        initLogger();
        if (log != null) {
            log.warn(s, thrown);
        }
    }


    public void logError(String s) {
        initLogger();
        if (log != null) {
            log.error(s);
        }
    }


    public void logError(String s, Throwable thrown) {
        initLogger();
        if (log != null) {
            log.error(s, thrown);
        }
    }


    public void logFatalError(String s) {
        initLogger();
        if (log != null) {
            log.fatal(s);
        }
    }


    public void logFatalError(String s, Throwable thrown) {
        initLogger();
        if (log != null) {
            log.fatal(s, thrown);
        }
    }


    public Log getLog() {
        initLogger();
        return log;
    }

}