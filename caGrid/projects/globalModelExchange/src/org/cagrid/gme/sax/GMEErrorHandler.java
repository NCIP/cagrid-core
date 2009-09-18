package org.cagrid.gme.sax;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.xni.parser.XMLParseException;


public class GMEErrorHandler implements XMLErrorHandler {
    protected static Log LOG = LogFactory.getLog(GMEErrorHandler.class.getName());
    private XMLParseException exception;
    private final List<String> warnings = new ArrayList<String>();


    public GMEErrorHandler() {
    }


    public XMLParseException getLastError() {
        return this.exception;
    }


    public List<String> getWarnings() {
        return this.warnings;
    }


    public void warning(String domain, String key, XMLParseException ex) throws XNIException {
        this.warnings.add(formatMessage(ex));
        LOG.warn(formatMessage(ex), ex);
    }


    public void error(String domain, String key, XMLParseException ex) throws XNIException {
        LOG.error(formatMessage(ex), ex);
        this.exception = ex;
        throw createXMLParseException();
    }


    public void fatalError(String domain, String key, XMLParseException ex) throws XNIException {
        LOG.error(formatMessage(ex), ex);
        this.exception = ex;
        throw createXMLParseException();
    }


    public XMLParseException createXMLParseException() {
        XMLParseException toThrow = getLastError();
        StringBuilder builder = new StringBuilder();
        if (getWarnings().size() > 0) {
            builder.append("\nWarnings:\n==========\n");
            for (int i = 0; i < getWarnings().size(); i++) {
                builder.append("\t" + (i + 1) + ") " + getWarnings().get(i) + "\n");
            }
            builder.append("==========");
        }
        if (toThrow == null) {
            toThrow = new XMLParseException(null, "Unknown error processing schema(s)." + builder.toString());
        } else {
            toThrow = new FormattedXMLParseException(new ExceptionDelegatingXMLLocator(toThrow), toThrow.getMessage()
                + builder.toString(), toThrow);
        }
        return toThrow;
    }


    private static String formatMessage(XMLParseException ex) {
        return "Problem processing schema [" + ex.getLiteralSystemId() + "], on line (" + ex.getLineNumber()
            + "), column (" + ex.getColumnNumber() + ")> " + ex.getMessage();
    }


    @SuppressWarnings("serial")
    class FormattedXMLParseException extends XMLParseException {
        public FormattedXMLParseException(XMLLocator locator, String message) {
            super(locator, message);
        }


        public FormattedXMLParseException(XMLLocator locator, String message, Exception exception) {
            super(locator, message, exception);
        }


        @Override
        public String toString() {
            return formatMessage(this);
        }

    }


    class ExceptionDelegatingXMLLocator implements XMLLocator {
        XMLParseException e = null;


        public ExceptionDelegatingXMLLocator(XMLParseException e) {
            this.e = e;
        }


        public String getPublicId() {
            return this.e.getPublicId();
        }


        public String getLiteralSystemId() {
            return this.e.getLiteralSystemId();
        }


        public String getBaseSystemId() {
            return this.e.getBaseSystemId();
        }


        public String getExpandedSystemId() {
            return this.e.getExpandedSystemId();
        }


        public int getLineNumber() {
            return this.e.getLineNumber();
        }


        public int getColumnNumber() {
            return this.e.getColumnNumber();
        }


        public String getEncoding() {
            return "";
        }

    }
}
