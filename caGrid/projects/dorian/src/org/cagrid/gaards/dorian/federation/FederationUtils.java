package org.cagrid.gaards.dorian.federation;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class FederationUtils {
    public static Date getProxyValid(CertificateLifetime valid) {
        Calendar c = new GregorianCalendar();
        c.add(Calendar.HOUR_OF_DAY, valid.getHours());
        c.add(Calendar.MINUTE, valid.getMinutes());
        c.add(Calendar.SECOND, valid.getSeconds());
        return c.getTime();
    }


    public static Date getMaxProxyLifetime(IdentityFederationProperties conf) {
        Calendar c = new GregorianCalendar();
        c.add(Calendar.HOUR_OF_DAY, conf.getUserCertificateLifetime().getHours());
        c.add(Calendar.MINUTE, conf.getUserCertificateLifetime().getMinutes());
        c.add(Calendar.SECOND, conf.getUserCertificateLifetime().getSeconds());
        return c.getTime();
    }


    public static long getTimeInSeconds(CertificateLifetime lifetime) {
        long seconds = lifetime.getSeconds();
        seconds = seconds + (lifetime.getMinutes() * 60);
        seconds = seconds + (lifetime.getHours() * 60 * 60);
        return seconds;
    }
}
