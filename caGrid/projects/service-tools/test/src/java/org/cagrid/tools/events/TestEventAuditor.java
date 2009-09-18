package org.cagrid.tools.events;

import gov.nih.nci.cagrid.common.FaultUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

public class TestEventAuditor extends TestCase {
	private final static String TABLE = "EVENT_AUDITOR";

	public void testEventAuditor() {
		EventAuditor auditor = null;
		try {
			auditor = new EventAuditor("Auditor", org.cagrid.tools.Utils
					.getDB(), TABLE);
			validateEventAuditor(auditor);
		} catch (Exception e) {
			FaultUtil.printFault(e);
			assertTrue(false);
		} finally {
			try {
				auditor.clearDatabase();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void testEventAuditorFromConfig() {
		EventAuditor auditor = null;
		try {
			auditor = org.cagrid.tools.events.Utils.getEventAuditor();
			validateEventAuditor(auditor);
		} catch (Exception e) {
			FaultUtil.printFault(e);
			assertTrue(false);
		} finally {
			try {
				auditor.clearDatabase();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void validateEventAuditor(EventAuditor auditor) throws Exception {
		int events = 5;
		String target = "Some User";
		String reportingParty = "Some Admin";
		String eventType = "Error";

		GregorianCalendar c = new GregorianCalendar();
		Date start = c.getTime();
		c.add(Calendar.HOUR, 1);
		Date end = c.getTime();
		Thread.currentThread().sleep(100);
		for (int i = 0; i < events; i++) {
			Event e = new Event();
			String targetId = target + i;
			e.setTargetId(targetId);
			e.setReportingPartyId(reportingParty);
			e.setEventType(eventType);
			e.setOccurredAt(new Date().getTime());
			String message = "Testing for " + e.getTargetId();
			e.setMessage(message);
			auditor.handleEvent(e);
			long expectedId = i + 1;
			assertEquals(expectedId, e.getEventId());
			assertTrue(auditor.eventExists(e.getEventId()));
			Event e2 = auditor.getEvent(e.getEventId());
			assertEquals(e, e2);
			assertEquals(1, auditor
					.findEvents(targetId, null, null, null, null,null).size());
			assertEquals(1, auditor
                .findEvents(null, null, null, null, null,message).size());
			assertEquals(1, auditor
                .findEvents(null, null, null, null, null,targetId).size());
			assertEquals((i + 1), auditor.findEvents(null, reportingParty,
					null, null, null,null).size());
			assertEquals((i + 1), auditor.findEvents(null, null, eventType,
					null, null,null).size());
			assertEquals((i + 1), auditor.findEvents(null, null, null, start,
					null,null).size());
			assertEquals((i + 1), auditor.findEvents(null, null, null, null,
					end,null).size());
			assertEquals((i + 1), auditor.findEvents(null, null, null, start,
					end,null).size());
			assertEquals(0, auditor.findEvents(null, null, null, null, start,null).size());
			assertEquals(0, auditor.findEvents(null, null, null, end, null,null)
					.size());

			assertEquals(1, auditor.findEvents(targetId, reportingParty,
					eventType, start, end,message).size());
		}

		for (int i = 0; i < events; i++) {
			long eventId = i + 1;
			assertTrue(auditor.eventExists(eventId));
			auditor.deleteEvent(eventId);
			assertFalse(auditor.eventExists(eventId));
		}
	}
}
