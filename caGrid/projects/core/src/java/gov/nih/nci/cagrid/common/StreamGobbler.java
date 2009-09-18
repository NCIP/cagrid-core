package gov.nih.nci.cagrid.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;

import org.apache.commons.logging.Log;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;


/**
 * StreamGobbler 
 * Reads input from a stream as long as more data is available
 * 
 * @author David Ervin
 * @author Shannon Hastings
 * @created Jun 21, 2007 11:03:06 AM
 * @version $Id: StreamGobbler.java,v 1.5 2008-04-17 14:55:06 dervin Exp $
 */
public class StreamGobbler extends Thread {

    public static final String TYPE_OUT = "OUT";
    public static final String TYPE_ERR = "ERR";

    private InputStream gobble;
    private String type;
    private PrintStream redirect;
    private Log log;
    private LogPriority priority;
    
    /**
     * Creates a stream gobbler which will just read the input stream until it's gone
     * 
     * @param is
     * @param type
     */
    public StreamGobbler(InputStream is, String type) {
        this(is, type, null);
    }
    

    /**
     * Creates a stream gobbler to consume an input stream and redirect its 
     * contents to an output stream
     * 
     * @param is
     * @param type
     * @param redrirect
     */
    public StreamGobbler(InputStream is, String type, OutputStream redrirect) {
        this.gobble = is;
        this.type = type;
        if (redrirect != null) {
            this.redirect = new PrintStream(redrirect);
        }
    }
    
    
    /**
     * @deprecated use a commons logger, StreamGobbler(InputStream is, String type, Log log, LogPriority priority)
     * 
     * @param is
     * @param type
     * @param logger
     * @param priority
     */
    public StreamGobbler(InputStream is, String type, Logger logger, Priority priority) {
        this(is, type, wrapLog4J(logger), convertLog4JPriority(priority));
    }
    
    
    /**
     * Creates a stream gobbler to consume an input stream and redirect
     * its contents to a Log with the specified priority level
     * 
     * @param is
     * @param type
     * @param log
     * @param priority
     */
    public StreamGobbler(InputStream is, String type, Log log, LogPriority priority) {
        this.gobble = is;
        this.type = type;
        this.log = log;
        this.priority = priority;
    }


    /**
     * creates readers to handle the text created by the external program
     */
    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(gobble);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                if (redirect != null || (log != null && priority != null)) {
                    line = type + "> " + line;
                    if (redirect != null) {
                        redirect.println(line);
                    } else {
                        switch (priority) {
                            case INFO:
                                log.info(line);
                                break;
                            case DEBUG:
                                log.debug(line);
                                break;
                            case WARN:
                                log.warn(line);
                                break;
                            case ERROR:
                                log.error(line);
                                break;
                        }
                    }
                }
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    
    public static Log wrapLog4J(final Logger logger) {
        Log log = new Log() {
            public void debug(Object arg0, Throwable arg1) {
                logger.debug(arg0, arg1);
            }


            public void debug(Object arg0) {
                logger.debug(arg0);
            }


            public void error(Object arg0, Throwable arg1) {
                logger.error(arg0, arg1);
            }


            public void error(Object arg0) {
                logger.error(arg0);
            }


            public void fatal(Object arg0, Throwable arg1) {
                logger.fatal(arg0, arg1);
            }


            public void fatal(Object arg0) {
                logger.fatal(arg0);
            }


            public void info(Object arg0, Throwable arg1) {
                logger.info(arg0, arg1);
            }


            public void info(Object arg0) {
                logger.info(arg0);
            }


            public boolean isDebugEnabled() {
                return logger.isDebugEnabled();
            }


            public boolean isErrorEnabled() {
                return logger.isEnabledFor(Priority.ERROR);
            }


            public boolean isFatalEnabled() {
                return logger.isEnabledFor(Priority.FATAL);
            }


            public boolean isInfoEnabled() {
                return logger.isInfoEnabled();
            }


            public boolean isTraceEnabled() {
                // TODO: solve what this should be
                return false;
            }


            public boolean isWarnEnabled() {
                return logger.isEnabledFor(Priority.WARN);
            }


            public void trace(Object arg0, Throwable arg1) {
                // TODO: trace == info?
                logger.info(arg0, arg1);
            }


            public void trace(Object arg0) {
                // TODO: trace == info?
                logger.info(arg0);
            }


            public void warn(Object arg0, Throwable arg1) {
                logger.warn(arg0, arg1);
            }


            public void warn(Object arg0) {
                logger.warn(arg0);
            }
        };
        return log;
    }
    
    
    public static LogPriority convertLog4JPriority(Priority priority) {
        LogPriority pri = LogPriority.INFO; // a reasonable default
        if (priority == Priority.DEBUG) {
            pri = LogPriority.DEBUG;
        } else if (priority == Priority.ERROR) {
            pri = LogPriority.ERROR;
        } else if (priority == Priority.FATAL) {
            pri = LogPriority.FATAL;
        } else if (priority == Priority.INFO) {
            pri = LogPriority.INFO;
        } else if (priority == Priority.WARN) {
            pri = LogPriority.WARN;
        }
        return pri;
    }
    
    
    /**
      *  LogPriority
      *  Priority with which to log output from the stream gobbler
      * 
      * @author David Ervin
      * 
      * @created Apr 17, 2008 9:29:33 AM
      * @version $Id: StreamGobbler.java,v 1.5 2008-04-17 14:55:06 dervin Exp $
     */
    public static enum LogPriority {
        INFO, DEBUG, WARN, ERROR, FATAL
    }
}
