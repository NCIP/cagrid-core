/*
 * Created on Jun 5, 2006
 */
package gov.nci.nih.cagrid.tests.core.compare;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import junit.framework.Assert;


public class BeanComparator {
    public static final boolean DEBUG = false;

    private Assert test;
    private List<String> ignoreMethods = null;


    public BeanComparator(Assert test) {
        this(test, new ArrayList<String>());
    }


    public BeanComparator(Assert test, List<String> ignoreMethods) {
        super();
        this.test = test;
        this.ignoreMethods = ignoreMethods;
    }


    public void assertEquals(Object o1, Object o2) throws IllegalArgumentException, IllegalAccessException,
        InvocationTargetException {
        // null
        if (o1 == null && o2 == null)
            return;
        test.assertNotNull(o1);
        test.assertNotNull(o2);

        // axis stuff
        if (o1.getClass().getName().startsWith("org.apache.axis"))
            return;

        // classes equal
        test.assertTrue(o1.getClass().equals(o2.getClass()));

        // array
        if (o1.getClass().isArray()) {
            int len1 = Array.getLength(o1);
            int len2 = Array.getLength(o2);
            test.assertEquals(len1, len2);

            for (int i = 0; i < len1; i++) {
                assertEquals(Array.get(o1, i), Array.get(o2, i));
            }
            return;
        }
        // list
        else if (o1 instanceof List) {
            int len1 = ((List) o1).size();
            int len2 = ((List) o2).size();
            test.assertEquals(len1, len2);

            for (int i = 0; i < len1; i++) {
                assertEquals(((List) o1).get(i), ((List) o2).get(i));
            }
            return;
        }

        // primitives equal
        if (o1 instanceof Short) {
            test.assertEquals(((Short) o1).shortValue(), ((Short) o2).shortValue());
            return;
        } else if (o1 instanceof Integer) {
            test.assertEquals(((Integer) o1).intValue(), ((Integer) o2).intValue());
            return;
        } else if (o1 instanceof Long) {
            test.assertEquals(((Long) o1).longValue(), ((Long) o2).longValue());
            return;
        } else if (o1 instanceof Float) {
            test.assertEquals(((Float) o1).floatValue(), ((Float) o2).floatValue());
            return;
        } else if (o1 instanceof Double) {
            test.assertEquals(((Double) o1).doubleValue(), ((Double) o2).doubleValue());
            return;
        } else if (o1 instanceof Boolean) {
            test.assertEquals(((Boolean) o1).booleanValue(), ((Boolean) o2).booleanValue());
            return;
        } else if (o1 instanceof Character) {
            test.assertEquals(((Character) o1).charValue(), ((Character) o2).charValue());
            return;
        } else if (o1 instanceof Byte) {
            test.assertEquals(((Byte) o1).byteValue(), ((Byte) o2).byteValue());
            return;
        } else if (o1 instanceof String) {
            test.assertEquals((String) o1, (String) o2);
            return;
        } else if (o1 instanceof QName) {
            test.assertEquals(((QName) o1).getPrefix(), ((QName) o2).getPrefix());
            test.assertEquals(((QName) o1).getLocalPart(), ((QName) o2).getLocalPart());
            return;
        }

        // methods same length
        Method[] m1 = o1.getClass().getMethods();
        Method[] m2 = o2.getClass().getMethods();
        test.assertEquals(m1.length, m2.length);

        for (int i = 0; i < m1.length; i++) {
            // getter
            String name = m1[i].getName();
            if (!name.startsWith("get") || name.equals("getClass") || this.ignoreMethods.contains(name)) {
                continue;
            }

            // same method name
            test.assertEquals(m1[i].getName(), m2[i].getName());

            // accessible
            m1[i].setAccessible(true);
            m2[i].setAccessible(true);

            // no parameters for getter
            if (m1[i].getParameterTypes().length != 0 || m1[2].getParameterTypes().length != 0) {
                continue;
            }
            test.assertEquals(0, m1[i].getParameterTypes().length);
            test.assertEquals(0, m2[i].getParameterTypes().length);

            // assert gotten values equal
            Object result1 = m1[i].invoke(o1, new Object[0]);
            Object result2 = m2[i].invoke(o2, new Object[0]);
            System.out.println("BeanComparator comparing " + m1[i].getName() + " and " + m2[i].getName());
            assertEquals(result1, result2);
        }
    }
}
