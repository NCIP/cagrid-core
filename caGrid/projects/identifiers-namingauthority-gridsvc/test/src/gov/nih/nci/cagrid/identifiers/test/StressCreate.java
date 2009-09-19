package gov.nih.nci.cagrid.identifiers.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Arrays;

import gov.nih.nci.cagrid.identifiers.TypeValues;
import gov.nih.nci.cagrid.identifiers.TypeValuesMap;
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
		
		TypeValues[] typeValues = new TypeValues[2];
		typeValues[0] = new TypeValues();
		typeValues[0].setType("EPR");
		Values values = new Values();
		values.setValue(new String[] { eprStr });
		typeValues[0].setValues(values);

		typeValues[1] = new TypeValues();
		typeValues[1].setType("CQL");
		values = new Values();
		values.setValue(new String[] { cqlStr });
		typeValues[1].setValues(values);
		
		TypeValuesMap typeValuesMap = new TypeValuesMap();
		typeValuesMap.setTypeValues(typeValues);
		
		IdentifiersNAServiceClient client = new IdentifiersNAServiceClient( gridSvcUrl );
	
		for(int i=0; i < iterations; i++) {
			String identifier = client.createIdentifier(typeValuesMap);
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

