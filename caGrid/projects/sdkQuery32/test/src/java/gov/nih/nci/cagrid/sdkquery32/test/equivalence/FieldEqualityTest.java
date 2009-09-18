package gov.nih.nci.cagrid.sdkquery32.test.equivalence;

import gov.nih.nci.cagrid.common.Utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/** 
 *  FieldEqualityTest
 * 
 * @author David Ervin
 * 
 * @created Jun 12, 2007 3:28:16 PM
 * @version $Id: FieldEqualityTest.java,v 1.2 2007-07-06 18:43:53 dervin Exp $ 
 */
public class FieldEqualityTest {
    
    private static Map<Class, Field[]> classFields = new HashMap();
    
    public static boolean fieldsEqual(Object o1, Object o2) throws Exception {
        if (o1 == null && o2 == null) {
            return true;
        } else if (o1 == null && o2 != null) {
            return false;
        } else if (o1 != null && o2 == null) {
            return false;
        }
        if (!o1.getClass().equals(o2.getClass())) {
            throw new IllegalArgumentException("o1 and o2 must be of the same type!");
        }
        Class c = o1.getClass();
        Field[] fields = classFields.get(c);
        if (fields == null) {
            fields = c.getDeclaredFields();
            classFields.put(c, fields);
        }
        for (Field f : fields) {
            int fieldMods = f.getModifiers();
            if (Modifier.isStatic(fieldMods) || Modifier.isFinal(fieldMods) || Modifier.isPrivate(fieldMods)) {
                continue;
            }
            Class type = f.getType();
            if (!type.isArray() && !Collection.class.isAssignableFrom(type)) {
                // handle any non-array / collection values
                Object v1 = f.get(o1);
                Object v2 = f.get(o2);
                if (!Utils.equals(v1, v2)) {
                    return false;
                }
            } else if (type.isPrimitive()) {
                // handle primitives
                if (type.equals(Integer.TYPE)) {
                    int i1 = f.getInt(o1);
                    int i2 = f.getInt(o2);
                    if (i1 != i2) {
                        return false;
                    }
                } else if (type.equals(Boolean.TYPE)) {
                    boolean b1 = f.getBoolean(o1);
                    boolean b2 = f.getBoolean(o2);
                    if (b1 != b2) {
                        return false;
                    }
                } else if (type.equals(Byte.TYPE)) {
                    byte b1 = f.getByte(o1);
                    byte b2 = f.getByte(o2);
                    if (b1 != b2) {
                        return false;
                    }
                } else if (type.equals(Character.TYPE)) {
                    char c1 = f.getChar(o1);
                    char c2 = f.getChar(o2);
                    if (c1 != c2) {
                        return false;
                    }
                } else if (type.equals(Short.TYPE)) {
                    short s1 = f.getShort(o1);
                    short s2 = f.getShort(o2);
                    if (s1 != s2) {
                        return false;
                    }
                } else if (type.equals(Long.TYPE)) {
                    long l1 = f.getLong(o1);
                    long l2 = f.getLong(o2);
                    if (l1 != l2) {
                        return false;
                    }
                } else if (type.equals(Float.TYPE)) {
                    float f1 = f.getLong(o1);
                    float f2 = f.getLong(o2);
                    if (f1 != f2) {
                        return false;
                    }
                } else if (type.equals(Double.TYPE)) {
                    double d1 = f.getDouble(o1);
                    double d2 = f.getDouble(o2);
                    if (d1 != d2) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    /*
     * 
            } else if (type.equals(Integer.class)) {
                Integer i1 = (Integer) f.get(o1);
                Integer i2 = (Integer) f.get(o2);
                if (!Utils.equals(i1, i2)) {
                    return false;
                }
            } else if (type.equals(Long.class)) {
                Long l1 = (Long) f.get(o1);
                Long l2 = (Long) f.get(o2);
                if (!Utils.equals(l1, l2)) {
                    return false;
                }
            } else if (type.equals(Boolean.class)) {
                Boolean b1 = (Boolean) f.get(o1);
                Boolean b2 = (Boolean) f.get(o2);
                if (!Utils.equals(b1, b2)) {
                    return false;
                }
            } else if (type.equals(Byte.class)) {
                Byte b1 = (Byte) f.get(o1);
                Byte b2 = (Byte) f.get(o2);
                if (!Utils.equals(b1, b2)) {
                    return false;
                }
            } else if (type.equals(Character.class)) {
                Character c1 = (Character) f.get(o1);
                Character c2 = (Character) f.get(o2);
                if (!Utils.equals(c1, c2)) {
                    return false;
                }
            } else if (type.equals(Short.class)) {
                Short s1 = (Short) f.get(o1);
                Short s2 = (Short) f.get(o2);
                if (!Utils.equals(s1, s2)) {
                    return false;
                }
            } else if (type.equals(Float.class)) {
                Float f1 = (Float) f.get(o1);
                Float f2 = (Float) f.get(o2);
                if (!Utils.equals(f1, f2)) {
                    return false;
                }
            } else if (type.equals(Double.class)) {
                 
     */
}
