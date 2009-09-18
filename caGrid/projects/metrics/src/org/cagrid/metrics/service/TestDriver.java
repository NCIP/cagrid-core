package org.cagrid.metrics.service;

import java.util.GregorianCalendar;

import javax.xml.namespace.QName;

import org.cagrid.metrics.common.Community;
import org.cagrid.metrics.common.Detail;
import org.cagrid.metrics.common.EventDescription;
import org.cagrid.metrics.common.EventRecord;
import org.cagrid.metrics.common.InvocationEvent;
import org.cagrid.metrics.common.ReporterDetails;
import org.cagrid.metrics.common.Service;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class TestDriver {

	public static void main(String[] args) {
		try {
			Configuration cfg = new Configuration();
			SessionFactory factory = cfg.configure("metrics.hibernate.cfg.xml")
					.buildSessionFactory();
			Session s = factory.openSession();
			s.beginTransaction();
			EventRecord e = getEvent();
			s.save(e);
			s.getTransaction().commit();

			EventRecord loadedEvent = (EventRecord) s.load(EventRecord.class, e
					.getId());

			// TODO: Test Delete
			s.beginTransaction();
			s.delete(e);
			s.getTransaction().commit();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static EventRecord getEvent() {
		EventRecord e = new EventRecord();
		e.setStartedAt(new GregorianCalendar());
		e.setEndedAt(new GregorianCalendar());
		e.setReportedAt(new GregorianCalendar());

		Service s = new Service();
		s.setName("Dorian");
		s.setVersion("1.2");
		s.setNamespace("http://cagrid.nci.nih.gov/Dorian");
		s
				.setAddress("https://dorian.cagrid.org:6443/wsrf/services/cagrid/Dorian");
		s.setAdditionalDetails(getDetails(s.getName(), 3));
		;
		e.setAdditionalDetails(getDetails("Event", 3));
		e.setEventSource(s);

		EventDescription des = new EventDescription();
		// des.setCustomEvent("MyEvent");
		// des.setUsageEvent(UsageEvent.LAUNCH);
		// des.setServiceLifeCycleEvent(ServiceLifeCycleEvent.MODIFICATION);
		InvocationEvent ie = new InvocationEvent();
		ie.setOperationName("createProxy");
		QName qname = new QName("http://cagrid.nci.nih.gov/Dorian",
				"createProxy");
		ie.setOperationQName(qname);
		ie
				.setClientIdentity("/O=caBIG/OU=caGrid/OU=Training/OU=Dorian/CN=langella");
		ie.setClientAddress("dwight");
		des.setInvocationEvent(ie);
		e.setEventDescription(des);

		Community c = new Community();
		c.setName("caGrid");
		c.setDeployment("Training");
		c.setAdditionalDetails(getDetails(c.getName(), 3));
		e.setCommunity(c);

		ReporterDetails rp = new ReporterDetails();
		rp.setIdentity("/O=caBIG/OU=caGrid/OU=Training/OU=Dorian/CN=langella");
		rp.setIPAddress("127.0.0.1");
		e.setReporterDetails(rp);
		return e;
	}

	public static Detail[] getDetails(String title, int count) {
		Detail[] d = new Detail[count];
		for (int i = 0; i < count; i++) {
			d[i] = new Detail();
			d[i].setName(title + " Detail " + (i + 1));
			d[i].set_value(title + " Value " + (i + 1));
		}
		return d;
	}
}