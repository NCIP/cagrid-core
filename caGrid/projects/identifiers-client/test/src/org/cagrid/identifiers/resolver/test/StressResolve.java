package org.cagrid.identifiers.resolver.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.http.HttpException;
import org.cagrid.identifiers.namingauthority.IdentifierValues;
import org.cagrid.identifiers.resolver.ResolverUtil;

public class StressResolve {
	
	public void httpResolution( String identifier ) throws HttpException, IOException {
		
		IdentifierValues ivs = ResolverUtil.resolveHttp(identifier);
		//System.out.println(ivs.toString());
		//System.out.println("========== httpResolution SUCCESS =============");
	}
	
	public void run(String identifiers) throws Exception {
		FileReader fileReader = new FileReader( identifiers );
		BufferedReader bfReader = new BufferedReader(fileReader);
		try {
			String identifier;
			int i =0;
			while ((identifier = bfReader.readLine()) != null)
			{
				httpResolution(identifier);
				i++;
				System.out.println(i + ":" + identifier);
				//System.exit(0);
			}
			System.out.println("Resolved " + i + " identifiers");
		}
		catch(Exception e) {
			bfReader.close();
			throw e;
		}
	}


	public static void main(String[] args) {
		String identifiers = "";
		String forever = "";
		try {
			identifiers = System.getProperty( "identifiers.file" );
			forever = System.getProperty( "run.forever" );
			
			StressResolve resolver = new StressResolve();
			while(true) {
				resolver.run(identifiers);
				if (!forever.equalsIgnoreCase("true")) {
					break;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

