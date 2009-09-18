package gov.nih.nci.cagrid.testing.system.haste.io;

/*
 * HASTE - High-level Automated System Test Environment
 * Copyright (C) 2002  Atomic Object, LLC.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * Contact Atomic Object:
 * 
 * Atomic Object, LLC.
 * East Building Suite 190
 * 419 Norwood Ave SE
 * Grand Rapids, MI 49506
 * USA
 *
 * info@atomicobject.com
 */

import java.io.Writer;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.IOException;

/**
 * This writer prepends every line with the <code>REPORTLINE_PREFIX</code>
 * for later filtering.
 * $Revision: 1.1 $
 */
public class ReportWriter extends Writer {

	/** Text to use for all report-oriented output */
	public static final String REPORTLINE_PREFIX = "[REPORT]:";

	/** Line Break String */
	static String lineSep = System.getProperty("line.separator");

	/** OuputStream to write to */
	OutputStream os;

	/** Flag for first write */
	boolean firstTime;

	/** Buffer for writes with possible line separators at end */
	StringBuffer buffer;

	/** 
	 * Create a new ReportWriter to write to an OutputStream.
	 * @param stream the OutputStream to write to.
	 * @throws IllegalArgumentException if stream is null.
	 */
	public ReportWriter(OutputStream stream) {
		if (stream == null) {
			throw new IllegalArgumentException("stream was null");
		}
		os = stream;
		firstTime = true;
		buffer = new StringBuffer();
	}

	/**
	 * Write a portion of an array of characters.  <code>REPORTLINE_PREFIX</code> 
	 * will be inserted at beginning of stream and after every <code>BR</code>.
	 * @param cbuf Array of characters.
	 * @param off Offset at which to start writing characters.
	 * @param len Number of characters to write.
	 * @throws IOException if an I/O error occurs.
	 * @throws IllegalArgumentException if cbuf is null.
	 * @throws IndexOutOfBoundsException if the off and len arguments index 
	 *         characters outside the bounds of the cbuf array.
	 */
	public void write(char[] cbuf, int off, int len) 
		throws IOException, IllegalArgumentException, IndexOutOfBoundsException 
	{
		String str = new String(cbuf, off, len);
		write(str);
	}

	/**
	 * Write a String.  <code>REPORTLINE_PREFIX</code> will be inserted at 
	 * beginning of stream and after every <code>BR</code>.
	 * @param str String to write.
	 * @throws IOException if an I/O error occurs.
	 * @throws IllegalArgumentException if str is null.
	 */
	public void write(String str) throws IOException {
		if (str == null) {
			throw new IllegalArgumentException("str was null");
		}

		buffer.append(str);

		for (int i=1; i < getLineSep().length(); i++) {
			if ( str.endsWith(getLineSep().substring(0, i)) ) {
				// we don't know for sure if the stream will
				// contain a line break string yet
				return;
			}
		}

		StringBuffer tempBuf = prependLines(buffer.toString());
		buffer = new StringBuffer();

		os.write(tempBuf.toString().getBytes());
	}

	/**
	 * Flushes the underlying output stream.
	 * @throws IOException if an I/O error occurs or called when 
	 * buffer is not empty due to undeterminable line break sequence.
	 */
	public void flush() throws IOException {
		if (buffer.length() > 0) {
			throw new IOException("buffer contains undetermined linebreak sequence");
		}
		os.flush();
	}
	
	/**
	 * Closes the underlying output stream.
	 * @throws IOException if an I/O error occurs.
	 */
	public void close() throws IOException {
		os.close();
	}

	public static String getLineSep() {
		return lineSep;
	}

	/**
	 * Inserts <code>REPORTLINE_PREFIX</code> before all occurences 
	 * of <code>BR</code>, and prepends entire string if firstTime
	 * is true.
	 * @param str String to operate on.
	 * @return StringBuffer after search / replace.
	 */
	StringBuffer prependLines(String str) {
		if (firstTime) {
			firstTime = false;
			str = REPORTLINE_PREFIX + str;
		}

		StringBuffer buf = new StringBuffer(str);

		// get first occurence of line break
		int index = buf.toString().indexOf(getLineSep());
		while (index != -1) {
			buf.insert(index + getLineSep().length(), REPORTLINE_PREFIX);
			index = buf.toString().indexOf(getLineSep(), index + 1 + REPORTLINE_PREFIX.length());
		}

		return buf;
	}

}

