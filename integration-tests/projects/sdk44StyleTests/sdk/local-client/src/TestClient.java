import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import gov.nih.nci.system.client.ApplicationServiceProvider;
import gov.nih.nci.system.applicationservice.ApplicationService;


public class TestClient
{
	public static void main(String args[])
	{
		TestClient client = new TestClient();
		try
		{
			client.testSearch();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		System.exit(0);
	}
	public ApplicationService getApplicationService() throws Exception
	{
		return ApplicationServiceProvider.getApplicationService();
	}
	
	public void testSearch() throws Exception
	{
		ApplicationService appService = getApplicationService();
		
		Collection<Class> classList = getClasses();
		for(Class klass:classList)
		{
			if (!Modifier.isAbstract(klass.getModifiers())){
				Object o = klass.newInstance();
				System.out.println("Searching for "+klass.getName());
				try
				{
					Collection results = appService.search(klass, o);
					for(Object obj : results)
					{
						printObject(obj, klass);
						break;
					}
				}catch(Exception e)
				{
					System.out.println(">>>"+e.getMessage());
				}
			}
		}
	}
	
	public static void printObject(Object obj, Class klass, boolean includeAssociation) throws Exception {
		System.out.println("\nPrinting "+ klass.getName());
		Method[] methods = klass.getMethods();
		for(Method method:methods)
		{
			if(method.getName().startsWith("get") && !method.getName().equals("getClass"))
			{
				System.out.print("\t"+method.getName().substring(3)+":");
				Object val = null;
				try {
				val = method.invoke(obj, (Object[])null);
				} catch(Exception e){
					val = "ERROR - unable to determine value"; 
						
				}
				if (val instanceof java.util.Set) {
					Collection list = (Collection)val;
					for(Object object: list){
						System.out.println(object.getClass().getName()+":");
						if (includeAssociation){
							printObject(object, object.getClass(), false);
						} else {
							System.out.println(" -- association has been excluded");
						}
					}	
					//System.out.println("size="+((Collection)val).size());
				}
				else if(val instanceof ArrayList)
				{
					Collection list = (ArrayList) val;
					System.out.println("\nPrinting Collection.....");
					for(Object object: list){
						System.out.println(object.getClass().getName()+":");
						if (includeAssociation){
							printObject(object, object.getClass(), false);
						} else {
							System.out.println(" -- association has been excluded");
						}
					}
				}
				else if(val != null && val.getClass().getName().startsWith("gov.nih.nci"))
				{
					if (includeAssociation){
						printObject(val, val.getClass(), false);
					} else {
						System.out.println(" -- association has been excluded");
					}
				}
				else
					System.out.println(val);
			}
		}
	}
	
	private void printObject(Object obj, Class klass) throws Exception {
		printObject(obj,klass,false);
	}


	public Collection<Class> getClasses() throws Exception
	{
		Collection<Class> list = new ArrayList<Class>();
		JarFile file = null;
		int count = 0;
		for(File f:new File("lib").listFiles())
		{
			if(f.getName().endsWith("-beans.jar"))
			{
				file = new JarFile(f);
				count++;
			}
		}
		if(file == null) throw new Exception("Could not locate the bean jar");
		if(count>1) throw new Exception("Found more than one bean jar");
		
		Enumeration e = file.entries();
		while(e.hasMoreElements())
		{
			JarEntry o = (JarEntry) e.nextElement();
			if(!o.isDirectory())
			{
				String name = o.getName();
				if(name.endsWith(".class"))
				{
					String klassName = name.replace('/', '.').substring(0, name.lastIndexOf('.'));
					list.add(Class.forName(klassName));
				}
			}
		}
		return list;
	}
}