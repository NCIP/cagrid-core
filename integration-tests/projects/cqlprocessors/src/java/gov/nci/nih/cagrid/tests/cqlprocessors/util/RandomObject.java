/*
 * Created on Aug 1, 2006
 */
package gov.nci.nih.cagrid.tests.cqlprocessors.util;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Random;

public class RandomObject
{
	private Random rand;
	private boolean ignoreAxisTypes = true;
	
	public RandomObject()
	{
		super();
		
		this.rand = new Random();
	}
	
	public Object next(Class cl) 
		throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return next(cl, 0);
	}
	
	public Object next(Class cl, int depth) 
		throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		if (isPrimitive(cl)) return nextPrimitive(cl);
		
		Object obj = null;
		Constructor cstor = null;
		try {
			cstor = cl.getConstructor(new Class[0]);
		} catch (NoSuchMethodException e) {
			Constructor[] cstors = cl.getConstructors(); 
			if (cstors.length > 0) cstor = cstors[0];
		}
		if (cstor == null) {
			for (Field field : cl.getFields()) {
				int mods = field.getModifiers();
				if (Modifier.isPublic(mods) && Modifier.isStatic(mods) && field.getType().equals(cl)) {
					field.setAccessible(true);
					obj = field.get(null);
					break;
				}
			}
			if (obj == null) {
				throw new InstantiationException("unable to instantiate " + cl.getName() + " because no constructor or public static field");
			}
		} else {
			Class[] cstorTypes = cstor.getParameterTypes();
			Object[] cstorObjs = new Object[cstorTypes.length];
			for (int i = 0; i < cstorTypes.length; i++) cstorObjs[i] = next(cstorTypes[i], depth);
			obj = cstor.newInstance(cstorObjs);
		}
		
		for (Method m : cl.getMethods()) {
			String name = m.getName();
			if (! name.startsWith("set")) continue;
			
			Class[] paramTypes = m.getParameterTypes();
			if (paramTypes.length != 1) continue;
			Class paramType = paramTypes[0];
			
			if (ignoreAxisTypes && paramType.getName().startsWith("org.apache.axis")) continue;
			
			Object paramVal = null;
			if (paramType.isArray()) {
				Class componentType = getComponentType(paramType);
				if (! isPrimitive(componentType) && depth == 0) continue;
				
				int[] dims = new int[getDimensionality(paramType)];
				for (int i = 0; i < dims.length; i++) {
					dims[i] = rand.nextInt(2) + 1;
				}
				paramVal = Array.newInstance(componentType, dims);
				fillArray(paramVal, depth);
			} else if (Collection.class.isAssignableFrom(paramType)) {
				continue;
			} else {
				if (! isPrimitive(paramType) && depth == 0) continue;
				paramVal = next(paramType, depth-1);
			}
			
			m.setAccessible(true);
			m.invoke(obj, new Object[] { paramVal });
		}
		
		return obj;
	}

	private Object nextPrimitive(Class type)
	{
		if (type.equals(short.class) || type.equals(Short.class)) {
			return (short) rand.nextInt(Short.MAX_VALUE);
		} else if (type.equals(int.class) || type.equals(Integer.class)) {
			return rand.nextInt();
		} else if (type.equals(long.class) || type.equals(Long.class)) {
			return rand.nextLong();
		} else if (type.equals(float.class) || type.equals(Float.class)) {
			return rand.nextFloat();
		} else if (type.equals(double.class) || type.equals(Double.class)) {
			return rand.nextDouble();
		} else if (type.equals(byte.class) || type.equals(Byte.class)) {
			byte[] bytes = new byte[1];
			rand.nextBytes(bytes);
			return bytes[0];
		} else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
			return rand.nextBoolean();
		} else if (type.equals(char.class) || type.equals(Character.class)) {
			char c = 0;
			while (! Character.isLetterOrDigit(c)) {
				c = (char) (rand.nextInt('z' - 'A') + 'A');
			}
			return c;
		} else if (type.equals(String.class)) {
			StringBuffer sb = new StringBuffer();
			for (int i = rand.nextInt(10) + 10; i > 0; i--) {
				sb.append(nextPrimitive(char.class));
			}
			return sb.toString();
		}
		
		return null;
	}
	
	private boolean isPrimitive(Class cl)
	{
		return cl.isPrimitive() || 
			cl.equals(Short.class) ||
			cl.equals(Integer.class) ||
			cl.equals(Long.class) ||
			cl.equals(Float.class) ||
			cl.equals(Double.class) ||
			cl.equals(Byte.class) ||
			cl.equals(Boolean.class) ||
			cl.equals(Character.class) ||
			cl.equals(String.class);
	}
	
	private Class getComponentType(Class cl)
	{
		while (cl.isArray()) cl = cl.getComponentType();
		return cl;
	}
	
	private int getDimensionality(Class cl)
	{
		int dim = 0;
		while (cl.isArray()) {
			dim++;
			cl = cl.getComponentType();
		}
		return dim;
	}
	
	private void fillArray(Object obj, int depth) 
		throws ArrayIndexOutOfBoundsException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException
	{
		Class type = obj.getClass();
		Class componentType = type.getComponentType();
		int len = Array.getLength(obj);

		if (componentType.isArray()) {
			for (int i = 0; i < len; i++) {
				fillArray(Array.get(obj, i), depth);
			}
		} else {
			for (int i = 0; i < len; i++) {
				Array.set(obj, i, next(componentType, depth-1));
			}			
		}
	}
}
