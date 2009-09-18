package org.cagrid.gme.test;

public interface SpringTestApplicationContextConstants {

    public static final String SPRING_CLASSPATH_PREFIX = "classpath*:";
    public static final String SPRING_FILE_PREFIX = "file:";

    public static final String GME_BASE_LOCATION = SPRING_FILE_PREFIX + "etc/applicationContext-gme.xml";
    public static final String TEST_BASE_LOCATION = SPRING_CLASSPATH_PREFIX + "spring/test-applicationContext-gme.xml";
    public static final String CYCLES_LOCATION = SPRING_CLASSPATH_PREFIX + "spring/test-applicationContext-cycles.xml";
    public static final String ERRORS_LOCATION = SPRING_CLASSPATH_PREFIX + "spring/test-applicationContext-errors.xml";
    public static final String INCLUDES_LOCATION = SPRING_CLASSPATH_PREFIX
        + "spring/test-applicationContext-includes.xml";
    public static final String REDEFINES_LOCATION = SPRING_CLASSPATH_PREFIX
        + "spring/test-applicationContext-redefines.xml";
    public static final String SIMPLE_LOCATION = SPRING_CLASSPATH_PREFIX + "spring/test-applicationContext-simple.xml";
}
