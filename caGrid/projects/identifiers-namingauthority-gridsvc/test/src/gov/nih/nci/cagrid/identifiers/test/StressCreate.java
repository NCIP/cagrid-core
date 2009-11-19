package gov.nih.nci.cagrid.identifiers.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Arrays;

import gov.nih.nci.cagrid.identifiers.KeyValues;
import gov.nih.nci.cagrid.identifiers.KeyValuesMap;
import gov.nih.nci.cagrid.identifiers.Values;
import gov.nih.nci.cagrid.identifiers.client.IdentifiersNAServiceClient;

import org.apache.axis.client.Stub;
import org.apache.axis.types.URI.MalformedURIException;


public class StressCreate  {

	public static String readFromFile( String fileName ) throws IOException {
		FileReader fileReader = new FileReader( fileName );
		BufferedReader bfReader = new BufferedReader(fileReader);
		String idStr = "";
		String str = "";
		while ((str = bfReader.readLine()) != null)
		{
			idStr += str;
		}
		bfReader.close();
		return idStr;
	}

	private static void run(String gridSvcUrl, int iterations, String eprStr, String cqlStr) 
		throws MalformedURIException, RemoteException {
		
		KeyValues[] keyValues = new KeyValues[2];
		keyValues[0] = new KeyValues();
		keyValues[0].setKey("EPR");
		Values values = new Values();
		values.setValue(new String[] { eprStr });
		keyValues[0].setValues(values);

		keyValues[1] = new KeyValues();
		keyValues[1].setKey("CQL");
		values = new Values();
		values.setValue(new String[] { cqlStr });
		keyValues[1].setValues(values);
		
		KeyValuesMap keyValuesMap = new KeyValuesMap();
		keyValuesMap.setKeyValues(keyValues);
		
		IdentifiersNAServiceClient client = new IdentifiersNAServiceClient( gridSvcUrl );
	
		for(int i=0; i < iterations; i++) {
			org.apache.axis.types.URI identifier = client.createIdentifier(keyValuesMap);
			System.out.println("["+i+"] " + identifier);
		}
	}
	
	public static void main(String[] args) {
		
		String eprStr, cqlStr = "";
		try {
			eprStr = readFromFile( System.getProperty( "epr.file" ) );
			cqlStr = readFromFile( System.getProperty( "cql.file" ) );
			
			run( System.getProperty("service.url"), 
					Integer.parseInt(System.getProperty("iterations")),
					eprStr, cqlStr );
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

