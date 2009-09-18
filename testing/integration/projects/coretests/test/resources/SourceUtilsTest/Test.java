public class Test
	extends Object
{
	public Test()
	{
		super();
	}
	
	public void helloWorld()
		throws Exception
	{
		for (int i = 0; i < 12; i++) {
			System.out.println("hello world");
			System.out.println("hello world again");
		}
	}
	
	public static void main(String[] args)
	{
		new Test().helloWorld();
	}
}