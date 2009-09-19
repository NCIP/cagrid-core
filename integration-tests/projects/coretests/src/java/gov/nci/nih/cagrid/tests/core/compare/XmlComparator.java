/**
 * Created on Dec 14, 2004 by MCCON012
 */
package gov.nci.nih.cagrid.tests.core.compare;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Utility class for comparing multiple XML documents.  This comparison tests
 * element names/namespaces, attributes, and the text between start and end 
 * elements.  The implementation uses a streaming approach, so documents
 * of arbitrary size can be used.
 * 
 * @author MCCON012
 */
public class XmlComparator 
	extends AbstractComparator
{
	/**
	 * The count of XML events parsed thus far
	 */
	private int currentCount = 0;
	/**
	 * The number of threads (XML documents)
	 */
	private int threadCount = 0;
	/**
	 * List of XML events parsed
	 */
	private ArrayList parsedItems = new ArrayList();
	/**
	 * Whether a difference was found
	 */
	private boolean foundDifference = false;
	/**
	 * The type mapping, if it exists
	 */
	private XmlSchemaTypes types = new XmlSchemaTypes();
	
	/**
	 * Construct an XmlComparator
	 */
	public XmlComparator()
	{
		super();
	}
	
	/**
	 * Construct an XmlComparator that will use schema types for comparison
	 * 
	 * @param xmlSchema
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public XmlComparator(File xmlSchema) 
		throws ParserConfigurationException, SAXException, IOException
	{
		super();
		
		if (xmlSchema != null) {
			types.parseTypes(xmlSchema);
		}
	}

	/**
	 * Compare content containing XML.
	 */
	public boolean isEqual(InputStream[] is)
		throws Exception
	{
		return xmlEqual(is);
	}
	
	/**
	 * Compare strings containing XML - provided for backwards compatibility
	 * @deprecated Use isEqual 
	 */
	public boolean xmlEqual(String[] xml)
		throws Exception
	{
		return isEqual(xml);
	}
	
	/**
	 * Compare files containing XML - provided for backwards compatibility
	 * @deprecated Use isEqual 
	 */
	public boolean xmlEqual(File[] files)
		throws Exception
	{
		// open input streams
		InputStream[] is = new InputStream[files.length];
		for (int i = 0; i < is.length; i++) {
			is[i] = new BufferedInputStream(new FileInputStream(files[i]));
		}

		try {
			return xmlEqual(is);
		} catch (Exception e) {
			throw e;
		} finally {
			// close input streams
			for (int i = 0; i < is.length; i++) {
				is[i].close();
			}
		}
	}
	
	/**
	 * Compare input streams containing XML - provided for backwards 
	 * compatibility
	 * @deprecated Use isEqual 
	 */
	public boolean xmlEqual(InputStream[] is)
		throws Exception
	{
		// no difference by default
		foundDifference = false;
		
		// initialize counters
		currentCount = 0;
		threadCount = is.length;
		
		// start threads
		CompareThread[] threads = new CompareThread[is.length];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new CompareThread(is[i]);
			threads[i].start();
		}

		// wait until all threads are done
		int aliveCount = threads.length;
		Object sleep = new Object();
		while (aliveCount > 0) {
			synchronized (sleep) {
				try { sleep.wait(500); }
				catch (InterruptedException e) { }
			}
			
			aliveCount = 0;
			for (int i = 0; i < threads.length; i++) {
				if (threads[i].isAlive()) aliveCount++; 
			}
		}
		
		// check for exception
		for (int i = 0; i < threads.length; i++) {
			if (threads[i].hasError()) throw threads[i].getError();
		}
		
		// return comparison results
		return ! foundDifference;
	}
	
	/**
	 * Perform a comparison of a parsed object to all other parsed objects. This
	 * method blocks until it has been call threadCount times, at which point 
	 * a comparison is made and the foundDifference flag is set.  If there has 
	 * been a previous difference detected, this method returns immediately. 
	 */
	protected synchronized void performComparison(Object o)
	{
		if (foundDifference) return;	
		
		parsedItems.add(o);
		if (parsedItems.size() < threadCount) {
			try { wait(); } catch (InterruptedException e) { }
			return;
		}
		
		// check values
		Object[] objs = parsedItems.toArray();
		for (int i = 0; i < objs.length; i++) {
			for (int j = i+1; j < objs.length; j++) {
				if (! objs[i].equals(objs[j])) {
					foundDifference = true;
					System.out.println("Found difference:");
					System.out.println("  " + objs[i]);
					System.out.println("  " + objs[j]);
					break;
				}
			}
			if (foundDifference) break;
		}
		
		// clear parsed items and release blocked threads
		parsedItems.clear();
		notifyAll();
	}
	
	/**
	 * Start element parsed item
	 * 
	 * @author MCCON012
	 */
	private class StartElement
	{
		private String namespaceURI;
		private String localName;
		private String qName;
		private Attributes atts;
		
		public StartElement(
			String namespaceURI, String localName, String qName, Attributes atts
		) {
			super();
			
			this.namespaceURI = namespaceURI;
			this.localName = localName;
			this.qName = qName;
			this.atts = new AttributesImpl(atts);
		}

		public boolean equals(Object o) 
		{
			if (o == null) return false;
			else if (o.getClass() != this.getClass()) return false;
			
			StartElement se = (StartElement) o;
			
			if (! namespaceURI.equals(se.namespaceURI)) return false;
			if (! localName.equals(se.localName)) return false;
			if (! qName.equals(se.qName)) return false;
			
			if (! atttributesEqual(atts, se.atts)) return false;
			if (! atttributesEqual(se.atts, atts)) return false;
			
			return true;
		}
		
		public boolean atttributesEqual(Attributes att1, Attributes att2)
		{
			int count = att1.getLength();
			for (int i = 0; i < count; i++) {
				String qName = att1.getQName(i);
				
				String value1 = att1.getValue(qName);
				String value2 = att2.getValue(qName);
				
				try {
					if (! types.isEqual(qName, new String[] { value1, value2 })) return false;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
			
			return true;			
		}
		
		public String toString()
		{
			String ret = "<" + qName;
			int count = atts.getLength();
			for (int i = 0; i < count; i++) {
				ret += " " + atts.getQName(i) + "=\"" + atts.getValue(i) + "\"";
			}
			ret += ">";
			return ret;
		}
	}
	
	/**
	 * End element parsed item
	 * @author MCCON012
	 */
	private class EndElement
	{
		private String namespaceURI;
		private String localName;
		private String qName;
		private String chars;
		
		public EndElement(
			String namespaceURI, String localName, String qName, String chars
		) {
			super();
			
			this.namespaceURI = namespaceURI;
			this.localName = localName;
			this.qName = qName;
			this.chars = chars;
		}

		public boolean equals(Object o) 
		{
			if (o == null) return false;
			else if (o.getClass() != this.getClass()) return false;

			EndElement ee = (EndElement) o;
			
			if (! namespaceURI.equals(ee.namespaceURI)) return false;
			if (! localName.equals(ee.localName)) return false;
			if (! qName.equals(ee.qName)) return false;

			try {
				//if (! chars.equals(ee.chars)) return false;
				if (! types.isEqual(qName, new String[] { chars, ee.chars })) return false;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			
			return true;
		}
		
		public String toString()
		{
			return chars + "</" + qName + ">";
		}
	}
	
	/**
	 * Emit this even when a thread is done parsing.  This guarantees all
	 * threads will complete
	 * 
	 * @author MCCON012
	 */
	private class EndParsing
	{
		public boolean equals(Object o) 
		{
			if (o == null) return false;
			else if (o.getClass() != this.getClass()) return false;
			return true;
		}
		
		public String toString()
		{
			return "<!-- EndParsing -->";
		}	
	}

	/**
	 * SAX handler that emits parsed items to the performComparison method
	 * @author MCCON012
	 */
	private class CompareHandler
		extends DefaultHandler
	{
		private StringBuffer buf = new StringBuffer();
		
		Object lastItem = null;
		
		/**
		 * Append characters to the buffer
		 */
		public void characters(char[] ch, int start, int length) 
		{
			buf.append(ch, start, length);
		}
		
		/**
		 * Call performComparison
		 */
		public void startElement(
			String namespaceURI, String localName, String qName, Attributes atts
		) {
			buf.delete(0, buf.length());
			
			lastItem = new StartElement(
				namespaceURI, localName, qName, atts
			);
			performComparison(lastItem);
		}
		
		/**
		 * Call performComparison
		 */
		public void endElement(
			String namespaceURI, String localName, String qName
		) {
			if (lastItem instanceof EndElement) buf.delete(0, buf.length());
			
			lastItem = new EndElement(
				namespaceURI, localName, qName, buf.toString()
			);
			performComparison(lastItem);
			
			buf.delete(0, buf.length());
		}		
	}
	
	/**
	 * Thread for parsing XML with a CompareHandler
	 * 
	 * @author MCCON012
	 */
	private class CompareThread
		extends Thread
	{
		/**
		 * Contains XML to be parsed
		 */
		private InputStream is;
		/**
		 * An exception that occurred during parsing
		 */
		private Exception error;
		
		/**
		 * Construct a new thread for comparing XML
		 */
		public CompareThread(InputStream is)
		{
			super();
			
			this.is = is;
		}
		
		/**
		 * Perform the parsing
		 */
		public void run()
		{
			try {
				SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
				CompareHandler handler = new CompareHandler();
				parser.parse(is, handler);			
			} catch (Exception e) {
				this.error = e;
			} finally {
				performComparison(new EndParsing());
			}
		}
		
		/**
		 * Whether an exception occurred during parsing
		 */
		public boolean hasError()
		{
			return getError() != null;
		}
		
		/**
		 * An exception that occurred during parsing
		 */
		public Exception getError()
		{
			return error;
		}
	}
	
	
}