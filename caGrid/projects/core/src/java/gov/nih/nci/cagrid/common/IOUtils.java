package gov.nih.nci.cagrid.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class IOUtils {

	public static int readInteger(String prompt) {
		return readInteger(prompt, false);
	}

	public static long readLong(String prompt) {
		return readLong(prompt, false);
	}

	public static int readInteger(String prompt, boolean force) {
		String s = readLine(prompt);
		try {
			return Integer.valueOf(s).intValue();
		} catch (Exception e) {
			System.err
					.println("Please try again, this time enter a Integer!!!");
			return readInteger(prompt, force);
		}

	}

	public static long readLong(String prompt, boolean force) {
		String s = readLine(prompt, force);
		try {
			return Long.valueOf(s).longValue();
		} catch (Exception e) {
			System.err.println("Please try again, this time enter a Long!!!");
			return readInteger(prompt, force);
		}

	}

	public static String readLine(String prompt, boolean force) {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		String s = null;

		try {
			System.out.print(prompt + ":");
			System.out.flush();
			s = br.readLine();
			while (force && ((s == null) || (s.trim().length() == 0))) {
				System.out.print(prompt + ":");
				s = br.readLine();
			}
		} catch (IOException ioe) {
			System.out.println("IO error trying to read your name!");
			System.exit(1);
		}
		return s;
	}

	public static String readLine(String prompt) {
		return readLine(prompt, false);
	}
}
