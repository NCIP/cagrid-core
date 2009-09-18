package org.cagrid.tools.events;

import gov.nih.nci.cagrid.common.FaultUtil;

import java.util.Set;

import junit.framework.TestCase;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A href="mailto:ervin@bmi.osu.edu">David Ervin</A>
 */
public class TestEventManager extends TestCase{

	public void testEventManager() {
		try {
			EventManager em = new EventManager();
			String e1 = "Event 1";
			String e2 = "Event 2";
			String e3 = "Event 3";
			String e4 = "Event 4";
			String h1Name = "Handler 1";
			SimpleEventHandler h1 = new SimpleEventHandler(h1Name);
			String h2Name = "Handler 2";
			SimpleEventHandler h2 = new SimpleEventHandler(h2Name);
			em.registerHandler(h1);
			em.registerHandler(h2);
			em.registerEventWithHandler(new EventToHandlerMapping(e1, h1.getName()));
			em.registerEventWithHandler(new EventToHandlerMapping(e3, h1.getName()));
			em.registerEventWithHandler(new EventToHandlerMapping(e2, h2.getName()));
			em.registerEventWithHandler(new EventToHandlerMapping(e3, h2.getName()));

			Set s1 = em.getHandlers(e1);
			assertEquals(1, s1.size());
			assertTrue(s1.contains(h1));

			Set s2 = em.getHandlers(e2);
			assertEquals(1, s2.size());
			assertTrue(s2.contains(h2));

			Set s3 = em.getHandlers(e3);
			assertEquals(2, s3.size());
			assertTrue(s3.contains(h1));
			assertTrue(s3.contains(h2));

			em.logEvent("", "", e1, "");
			Set e1h1 = h1.getEventsRecord();
			Set e1h2 = h2.getEventsRecord();
			assertEquals(1, e1h1.size());
			assertTrue(e1h1.contains(e1));
			assertEquals(0, e1h2.size());
			h1.reset();
			h2.reset();

			em.logEvent("", "", e2, "");
			Set e2h1 = h1.getEventsRecord();
			Set e2h2 = h2.getEventsRecord();
			assertEquals(0, e2h1.size());
			assertEquals(1, e2h2.size());
			assertTrue(e2h2.contains(e2));
			h1.reset();
			h2.reset();

			em.logEvent("", "", e3, "");
			Set e3h1 = h1.getEventsRecord();
			Set e3h2 = h2.getEventsRecord();
			assertEquals(1, e3h1.size());
			assertTrue(e3h1.contains(e3));
			assertEquals(1, e3h2.size());
			assertTrue(e3h2.contains(e3));
			h1.reset();
			h2.reset();

			em.logEvent("", "", e4, "");
			Set e4h1 = h1.getEventsRecord();
			Set e4h2 = h2.getEventsRecord();
			assertEquals(0, e4h1.size());
			assertEquals(0, e4h2.size());

		} catch (Exception e) {
			FaultUtil.printFault(e);
			assertTrue(false);
		} finally {

		}
	}
	
	public void testEventManagerFromConfiguration() {
		try {
			EventManager em = Utils.getEventManager();
			String e1 = "Event 1";
			String e2 = "Event 2";
			String e3 = "Event 3";
			String e4 = "Event 4";
			String h1Name = "Handler 1";
		
			String h2Name = "Handler 2";
	
			SimpleEventHandler h1 = (SimpleEventHandler)em.getHandler(h1Name);
			SimpleEventHandler h2 = (SimpleEventHandler)em.getHandler(h2Name);
			
			Set s1 = em.getHandlers(e1);
			assertEquals(1, s1.size());
			assertTrue(s1.contains(h1));

			Set s2 = em.getHandlers(e2);
			assertEquals(1, s2.size());
			assertTrue(s2.contains(h2));

			Set s3 = em.getHandlers(e3);
			assertEquals(2, s3.size());
			assertTrue(s3.contains(h1));
			assertTrue(s3.contains(h2));

			em.logEvent("", "", e1, "");
			Set e1h1 = h1.getEventsRecord();
			Set e1h2 = h2.getEventsRecord();
			assertEquals(1, e1h1.size());
			assertTrue(e1h1.contains(e1));
			assertEquals(0, e1h2.size());
			h1.reset();
			h2.reset();

			em.logEvent("", "", e2, "");
			Set e2h1 = h1.getEventsRecord();
			Set e2h2 = h2.getEventsRecord();
			assertEquals(0, e2h1.size());
			assertEquals(1, e2h2.size());
			assertTrue(e2h2.contains(e2));
			h1.reset();
			h2.reset();

			em.logEvent("", "", e3, "");
			Set e3h1 = h1.getEventsRecord();
			Set e3h2 = h2.getEventsRecord();
			assertEquals(1, e3h1.size());
			assertTrue(e3h1.contains(e3));
			assertEquals(1, e3h2.size());
			assertTrue(e3h2.contains(e3));
			h1.reset();
			h2.reset();

			em.logEvent("", "", e4, "");
			Set e4h1 = h1.getEventsRecord();
			Set e4h2 = h2.getEventsRecord();
			assertEquals(0, e4h1.size());
			assertEquals(0, e4h2.size());

		} catch (Exception e) {
			FaultUtil.printFault(e);
			assertTrue(false);
		} finally {

		}
	}


	protected void setUp() throws Exception {
		super.setUp();

	}


	protected void tearDown() throws Exception {
		super.setUp();

	}


}
