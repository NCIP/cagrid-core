package gov.nih.nci.cagrid.common;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.logging.Log;
import org.oasis.wsrf.faults.BaseFaultType;

/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 */
public class FaultUtil {
	public static void printFault(Throwable e) {
		if (e instanceof BaseFaultType) {
			BaseFaultType fault = (BaseFaultType) e;
			System.err.println(fault.getFaultString());
			FaultHelper helper = new FaultHelper(fault);
			helper.printStackTrace();
		} else {
			e.printStackTrace();
		}
	}

	public static String printFaultToString(Throwable e) {
		StringWriter writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		if (e instanceof BaseFaultType) {
			BaseFaultType fault = (BaseFaultType) e;
			printWriter.println(Utils.getExceptionMessage(fault));
			FaultHelper helper = new FaultHelper(fault);
			helper.printStackTrace(printWriter);
		} else {
			e.printStackTrace(printWriter);
		}
		String str = writer.getBuffer().toString();
		printWriter.close();
		try {
			writer.close();
		} catch (Exception ex) {
		}
		return str;
	}
	
	public static void logFault(Log log, Throwable e) {
		if (e instanceof BaseFaultType) {
			BaseFaultType fault = (BaseFaultType) e;
			log.error(fault.getFaultString());
			FaultHelper helper = new FaultHelper(fault);
			log.error(helper.getStackTrace());
		} else {
			log.error(e, e);
		}
	}

}
