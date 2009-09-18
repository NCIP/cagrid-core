package gov.nih.nci.cagrid.sdkinstall;

import gov.nih.nci.cagrid.common.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/** 
 *  PropertiesPreservingComments
 *  Properties which maintains comment lines when stored to disk
 * 
 * @author David Ervin
 * 
 * @created Jun 15, 2007 11:28:40 AM
 * @version $Id: PropertiesPreservingComments.java,v 1.2 2007-06-18 14:20:46 dervin Exp $ 
 */
public class PropertiesPreservingComments {
    
    private List<String> lines;
    
    public PropertiesPreservingComments() {
        lines = new LinkedList();
    }
    
    
    public void load(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        load(fis);
        fis.close();
    }
    
    
    public void load(InputStream stream) throws IOException {
        StringBuffer rawProps = Utils.inputStreamToStringBuffer(stream);
        Iterator lineIter = new LineIterator(rawProps);
        while (lineIter.hasNext()) {
            lines.add((String) lineIter.next());
        }
    }
    
    
    public void write(OutputStream stream) {
        PrintWriter writer = new PrintWriter(stream);
        for (String line : lines) {
            writer.println(line);
        }
        writer.flush();
    }
    
    
    public String getProperty(String key) {
        for (String line : lines) {
            if (line.startsWith(key) && line.indexOf('=') != -1) {
                String[] pieces = line.split("=");
                if (pieces.length == 2) {
                    return pieces[1];
                }
            }
        }
        return null;
    }
    
    
    public void setProperty(String key, String value) {
        String setLine = key + "=" + value;
        boolean set = false;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.startsWith(key) && line.indexOf('=') != -1) {
                lines.set(i, setLine);
                set = true;
                break;
            }
        }
        if (!set) {
            lines.add(setLine);
        }
    }
    
    
    private static class LineIterator implements Iterator {
        private StringBuffer buff;
        private int currentIndex;
        
        public LineIterator(StringBuffer buff) {
            this.buff = buff;
            this.currentIndex = 0;
        }
        
        public boolean hasNext() {
            if (currentIndex == -1) {
                return false;
            }
            int nextIndex = buff.indexOf("\n", currentIndex);
            return nextIndex != -1;
        }
        
        
        public Object next() {
            int endIndex = buff.indexOf("\n", currentIndex);
            String next = buff.substring(currentIndex, endIndex - 1);
            currentIndex = endIndex + 1;
            return next;
        }
        
        
        public void remove() {
            throw new UnsupportedOperationException("Operation remove() is not supported");
        }
    }
}
