package gov.nih.nci.cagrid.testing.system.haste.io;

import java.io.*;

/**
 * ReportLineCollector acts as a special filter on an InputStream,
 * which removes lines beginning with <code>REPORTLINE_PREFIX</code>
 * from the main stream of data and buffers such lines for later,
 * all-at-once access.
 *
 * @version $Revision: 1.1 $
 */
public class ReportLineCollector extends InputStream implements Runnable {

	/** Incoming data */
	BufferedReader inReader;

	/** Where we write data that hasn't been filtered out */
	PrintWriter outWriter;

	/** Data stream for "user" class that reads from us */
	PipedInputStream inPipe;

	/** Streaming worker thread */
	Thread innerThread;

	/** Exception that may occur in the run() method */
	IOException caught;

	/** Buffer for report lines */
	StringBuffer reportBuffer;

	/** System-dependant line break character */
	String lineBreakChar;

	/** 
	 * Make a new ReportLineCollector that filters (and collects)
	 * lines of text that match a certain special reporting patterns.
	 */
	public ReportLineCollector(InputStream in) throws IOException {
		if (in == null) {
			throw new IllegalArgumentException("InputStream was null");
		}
		lineBreakChar = System.getProperty("line.separator");
		reportBuffer = new StringBuffer();
		inReader = new BufferedReader(new InputStreamReader(in));
		
		inPipe = new PipedInputStream();
		PipedOutputStream outPipe = new PipedOutputStream(inPipe);

		outWriter = new PrintWriter(outPipe,true);

		innerThread = new Thread(this);
		innerThread.start();
	}

	/**
	 * Read lines from the incoming data stream, determine
	 * if they should be filtered and stored or just passed along,
	 * and write the unfiltered lines into the write-end of the pipe.
	 */
	public void run() {
		try {
			String line = inReader.readLine();
			String filtered;
			while (line != null) {
				filtered = filterLine(line);
				if (filtered != null) {
					outWriter.println(line);
				}
				line = inReader.readLine();
			}
			outWriter.flush();
			outWriter.close();
		} catch (IOException ex) {
			caught = ex;
		}
	}

	/**
	 * Filter and collect "report" lines.
	 * @return null if the line was collected and therefore filtered from the output,
	 * or a line of text that should be passed to the output.  (In this case,
	 * it is <code>line</code> unmodified.
	 */
	String filterLine(String inLine) {
		if (inLine != null && inLine.trim().startsWith(ReportWriter.REPORTLINE_PREFIX)) {
			synchronized (reportBuffer) {
				inLine = inLine.substring(ReportWriter.REPORTLINE_PREFIX.length());
				reportBuffer.append(inLine);
				reportBuffer.append(lineBreakChar);
			}
			return null;
		}
		return inLine;
	}	
	
	/**
	 * Return the contents of the report buffer.
	 */
	public String getReportLines() {
		String ret = "";
		synchronized (reportBuffer) {
			ret = reportBuffer.toString();
		}
		if (ret.endsWith(lineBreakChar)) {
			ret = ret.substring(0, ret.length() - lineBreakChar.length());
		}
		return ret;
	}

	/** 
	 * Conform to InputStream interface.
	 * Deliver data that is waiting in the pipe.
	 */
	public int read() throws IOException {
		if (caught != null) {
			throw caught;
		}
		return inPipe.read();
	}

	/**
	 * How many bytes can be read without having to block
	 */
	public int available() throws IOException {
		return inPipe.available();
	}

	// XXX
	//
//	public static void main(String args[]) throws Exception {
//		String src = "Hello.\n[REPORT]: \tThis is report text.\nThis is the end.\n[REPORT]: \tFinal reprot line";
//		ByteArrayInputStream bais = new ByteArrayInputStream(src.getBytes());
//		ReportLineCollector col = new ReportLineCollector(bais);

//		System.out.println("NORMAL OUTPUT:");
//		BufferedReader bread = new BufferedReader(new InputStreamReader(col));
//		String line = bread.readLine();
//		while (line != null) {
//			System.out.println(line);
//			line = bread.readLine();
//		}
//		 
//		System.out.println("Report: ["+col.getReportLines()+"]");
//	}
}
