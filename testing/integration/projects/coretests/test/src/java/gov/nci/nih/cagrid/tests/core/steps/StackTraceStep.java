/*
 * Created on Sep 26, 2006
 */
package gov.nci.nih.cagrid.tests.core.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;

public class StackTraceStep
	extends Step
{
	private long delay;
	private long frequency;
	private File logFile;
	
	public StackTraceStep()
	{
		this(1000 * 60 * 10, 1000 * 5, null);
	}
	
	public StackTraceStep(File file)
	{
		this(1000 * 60 * 10, 1000 * 5, file);
	}
	
	public StackTraceStep(long delay, long frequency, File logFile)
	{
		super();
		
		this.delay = delay;
		this.frequency = frequency;
		this.logFile = logFile;
	}
	
	public void runStep() 
		throws Throwable
	{
		new StackTraceThread().start();
	}
	
	private class StackTraceThread
		extends Thread
	{
		private HashSet<String> ignoreThreads = new HashSet<String>();
		
		public StackTraceThread()
		{
			super("StackTraceThread");
			
			super.setDaemon(true);
			
			ignoreThreads.add("Signal Dispatcher");
			ignoreThreads.add("Reference Handler");
			ignoreThreads.add("Finalizer");
			ignoreThreads.add("StackTraceThread");
			ignoreThreads.add("DestroyJavaVM");
		}
		
		public void run()
		{
			try { sleep(delay); }
			catch (InterruptedException e) { return; }
			
			PrintStream out = System.out;
			if (logFile != null) {
				try {
					out = new PrintStream(new BufferedOutputStream(new FileOutputStream(logFile, true)));
					out.println("-------------------------------------------------------------");
					out.println(SimpleDateFormat.getDateTimeInstance().format(new Date()));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			
			while (true) {
				out.println("-------------------------------------------------------------");
				Map<Thread, StackTraceElement[]> threadTable = Thread.getAllStackTraces();
				for (Thread t : threadTable.keySet()) {
					if (ignoreThreads.contains(t.getName())) continue;
					out.println(t.getName());
					for (StackTraceElement ste : threadTable.get(t)) {
						out.println("  " + ste.getFileName() + " (" + ste.getLineNumber() + ")");
					}
					out.flush();
				}
				try { sleep(frequency); }
				catch (InterruptedException e) { break; }
			}
		}
	}
	
	public static void main(String[] args) 
		throws Throwable
	{
		new StackTraceStep(5000, 1000, new File("c:\\test.txt")).runStep();
		Object sleep = new Object();
		synchronized (sleep) {
			sleep.wait(10000);
		}
	}
}
