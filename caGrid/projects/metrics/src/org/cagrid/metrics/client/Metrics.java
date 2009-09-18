package org.cagrid.metrics.client;

import gov.nih.nci.cagrid.common.IOUtils;
import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.Utils;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.metrics.common.Community;
import org.cagrid.metrics.common.Component;
import org.cagrid.metrics.common.Event;
import org.cagrid.metrics.common.EventDescription;
import org.cagrid.metrics.common.EventSubmission;
import org.cagrid.metrics.common.UsageEvent;

public class Metrics {

	public static String METRICS_CLIENT_PROPERTY_FILE = "metrics-client.properties";
	private static final String REPORTING_ENABLED_PROPERTY = "org.cagrid.metrics.reporting.enabled";
	private static final String REPORTING_ENABLED_PROPERTY_DEFAULT_VALUE = "true";
	private static final String LOGGING_ENABLED_PROPERTY = "org.cagrid.metrics.logging.enabled";
	private static final String LOGGING_ENABLED_PROPERTY_DEFAULT_VALUE = "true";
	private static final String METRICS_URL_PROPERTY = "org.cagrid.metrics.service.url";
	private static final String COMMUNITY_PROPERTY = "org.cagrid.metrics.community";
	private static final String COMMUNITY_PROPERTY_DEFAULT_VALUE = "caGrid";
	private static final String COMMUNITY_INSTANCE_PROPERTY = "org.cagrid.metrics.community.instance";
	private static final String COMMUNITY_INSTANCE_PROPERTY_DEFAULT_VALUE = "Training";
	private static final String QUEUE_SIZE_FLUSH_PROPERTY = "org.cagrid.metrics.queue.flush.size";
	private static final String QUEUE_SIZE_FLUSH_PROPERTY_DEFAULT_VALUE = "5";
	private static final String QUEUE_TIME_FLUSH_PROPERTY = "org.cagrid.metrics.queue.flush.seconds";
	private static final String QUEUE_TIME_FLUSH_PROPERTY_DEFAULT_VALUE = "60";
	private static final String THREAD_SLEEP_TIME_PROPERTY = "org.cagrid.metrics.thread.sleep.time";
	private static final String THREAD_SLEEP_TIME_PROPERTY_DEFAULT_VALUE = "10";
	private static Metrics instance;
	private boolean loggingEnabled;
	private boolean reportingEnabled;
	private String metricsURL;
	private String community;
	private String communityInstance;
	private Object queueMutex;
	private Object propertiesMutex;
	private int flushQueueSeconds;
	private int flushQueueSize;
	private int threadSleepTime;
	private Log log;
	private List<Event> queue;
	Date nextFlush;

	private Metrics() {
		this.log = LogFactory.getLog(this.getClass().getName());
		this.queue = new ArrayList<Event>();
		queueMutex = new Object();
		propertiesMutex = new Object();
		this.flushQueueSeconds = Integer.MIN_VALUE;
		loadProperties();
		Thread t = new Thread(getRunner());
		t.setDaemon(true);
		t.start();
	}

	private void loadProperties() {
		synchronized (propertiesMutex) {
			try {
				Properties properties = new Properties();
				properties.load(new FileInputStream(
						METRICS_CLIENT_PROPERTY_FILE));
				this.reportingEnabled = Boolean.valueOf(
						properties.getProperty(REPORTING_ENABLED_PROPERTY,
								REPORTING_ENABLED_PROPERTY_DEFAULT_VALUE))
						.booleanValue();
				this.loggingEnabled = Boolean.valueOf(
						properties.getProperty(LOGGING_ENABLED_PROPERTY,
								LOGGING_ENABLED_PROPERTY_DEFAULT_VALUE))
						.booleanValue();
				this.metricsURL = Utils.clean(properties
						.getProperty(METRICS_URL_PROPERTY));
				if (this.metricsURL == null) {
					throw new Exception(
							"The URL of the Metrics service was not specified, it must be specified as the property "
									+ METRICS_URL_PROPERTY + ".");
				}
				this.community = Utils.clean(properties.getProperty(
						COMMUNITY_PROPERTY, COMMUNITY_PROPERTY_DEFAULT_VALUE));
				this.communityInstance = Utils.clean(properties.getProperty(
						COMMUNITY_INSTANCE_PROPERTY,
						COMMUNITY_INSTANCE_PROPERTY_DEFAULT_VALUE));
				this.flushQueueSize = Integer.valueOf(
						properties.getProperty(QUEUE_SIZE_FLUSH_PROPERTY,
								QUEUE_SIZE_FLUSH_PROPERTY_DEFAULT_VALUE))
						.intValue();
				int oldFlushTime = this.flushQueueSeconds;
				this.flushQueueSeconds = Integer.valueOf(
						properties.getProperty(QUEUE_TIME_FLUSH_PROPERTY,
								QUEUE_TIME_FLUSH_PROPERTY_DEFAULT_VALUE))
						.intValue();
				this.threadSleepTime = Integer.valueOf(
						properties.getProperty(THREAD_SLEEP_TIME_PROPERTY,
								THREAD_SLEEP_TIME_PROPERTY_DEFAULT_VALUE))
						.intValue();
				if (oldFlushTime != this.flushQueueSeconds) {
					resetFlushTime();
				}
			} catch (Exception e) {
				loggingEnabled = true;
				logError("Error loading properties file "
						+ METRICS_CLIENT_PROPERTY_FILE + ".", e);
			}
		}
	}

	public void report(List<Event> events) {
		if (reportingEnabled) {
			for (int i = 0; i < events.size(); i++) {
				report(events.get(i));
			}
		}
	}

	public void report(Event event) {
		if (reportingEnabled) {
			synchronized (queueMutex) {
				queue.add(event);
			}
		}
	}

	public void reportNow(List<Event> events) {
		if ((reportingEnabled) && (events != null)) {
			try {
				Event[] array = new Event[events.size()];
				for (int i = 0; i < events.size(); i++) {
					array[i] = events.get(i);
				}
				EventSubmission submission = new EventSubmission();
				Community community = new Community();
				community.setName(this.community);
				community.setDeployment(this.communityInstance);
				submission.setCommunity(community);
				submission.setEvent(array);
				MetricsClient client = new MetricsClient(this.metricsURL);
				client.report(submission);
			} catch (Exception e) {
				logError("Error reporting events to the Metrics Service.", e);
			}
		}
	}

	public void reportNow(Event event) {
		if (reportingEnabled) {
			List<Event> list = new ArrayList<Event>();
			list.add(event);
			reportNow(list);
		}
	}

	private void resetFlushTime() {
		GregorianCalendar cal = new GregorianCalendar();
		cal.add(Calendar.SECOND, this.flushQueueSeconds);
		this.nextFlush = cal.getTime();
	}

	public void flushEventQueue() {
		resetFlushTime();
		if (reportingEnabled) {
			Event[] events = null;
			synchronized (queueMutex) {
				events = new Event[queue.size()];
				for (int i = 0; i < queue.size(); i++) {
					events[i] = queue.get(i);
				}
				queue.clear();
			}

			try {
				if ((events != null) && (events.length > 0)) {
					EventSubmission submission = new EventSubmission();
					Community community = new Community();
					community.setName(this.community);
					community.setDeployment(this.communityInstance);
					submission.setCommunity(community);
					submission.setEvent(events);
					MetricsClient client = new MetricsClient(this.metricsURL);
					client.report(submission);
				}
			} catch (Exception e) {
				logError(
						"Error reporting events in the event queue to the Metrics Service.",
						e);
				synchronized (queueMutex) {
					if (events != null) {
						for (int i = 0; i < events.length; i++) {
							queue.add(events[i]);
						}
					}
				}
			}
		}
	}

	private void logError(Throwable t) {
		if (loggingEnabled) {
			log.error(t);
		}
	}

	private void logError(String s, Throwable t) {
		if (loggingEnabled) {
			log.error(s, t);
		}
	}

	public static Metrics getInstance() {
		if (instance == null) {
			instance = new Metrics();
		}
		return instance;
	}

	private Runner getRunner() {
		Runner runner = new Runner() {
			public void execute() {
				while (true) {
					System.out.println("SLEEPING");
					try {
						Thread.currentThread().sleep(threadSleepTime * 1000);
					} catch (Exception e) {
						logError(e);
					}
					
					System.out.println("AWAKE");
					loadProperties();
					Date now = new Date();
					if (queue.size() >= flushQueueSize) {
						System.out.println("Flushing queue " + queue.size()
								+ ">=" + flushQueueSize);
						flushEventQueue();
					} else if (now.after(nextFlush)) {
						System.out.println("Flushing queue "
								+ nextFlush.toString() + " is after "
								+ now.toString());
						flushEventQueue();
					}
				}
			}
		};
		return runner;
	}

	public static void main(String[] args) {
		int count = 1;
		while (true) {
			try {
				IOUtils.readLine("Hit enter to submit event");
				Event e = new Event();
				e.setStartedAt(new GregorianCalendar());
				e.setEndedAt(new GregorianCalendar());
				EventDescription des = new EventDescription();
				des.setUsageEvent(UsageEvent.LAUNCH);
				e.setEventDescription(des);
				Component c = new Component();
				c.setName("Dorian");
				c.setVersion("1.2");
				e.setEventSource(c);
				Metrics.getInstance().report(e);
				count = count + 1;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
