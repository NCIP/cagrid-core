package org.cagrid.index.scalability;

import gov.nih.nci.cagrid.advertisement.AdvertisementClient;
import gov.nih.nci.cagrid.advertisement.exceptions.InvalidConfigurationException;
import gov.nih.nci.cagrid.discovery.client.DiscoveryClient;
import gov.nih.nci.cagrid.metadata.exceptions.QueryInvalidException;
import gov.nih.nci.cagrid.metadata.exceptions.RemoteResourcePropertyRetrievalException;
import gov.nih.nci.cagrid.metadata.exceptions.ResourcePropertyRetrievalException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.globus.mds.servicegroup.client.ServiceGroupRegistrationParameters;
import org.globus.wsrf.impl.servicegroup.client.ServiceGroupRegistrationClient;


public class MassRegistration {

    public static final int MULTIPLIER = 1;


    public static void main(String[] args) throws RemoteResourcePropertyRetrievalException, QueryInvalidException,
        ResourcePropertyRetrievalException, InvalidConfigurationException, InterruptedException, IOException {

        for (int i = 0; i < args.length; i++) {
            String sourceURL = args[i];

            DiscoveryClient sourceClient = new DiscoveryClient(sourceURL);
            EndpointReferenceType[] allServices = sourceClient.getAllServices(true);
            System.out.println("Found " + allServices.length + " services in Index Service:" + sourceURL);

            for (EndpointReferenceType epr : allServices) {
                System.out.println("Processing service: " + epr.getAddress());

                try {
                    File registrationFile = new File("resources/registrationTemplate.xml");
                    if (registrationFile.exists() && registrationFile.canRead()) {

                        ServiceGroupRegistrationParameters params = ServiceGroupRegistrationClient
                            .readParams(registrationFile.getAbsolutePath());

                        params.setRegistrantEPR(epr);

                        for (int j = 0; j < MULTIPLIER; j++) {
                            // System.out.println("Registering for the " + (j+1)
                            // + " time.");
                            AdvertisementClient registrationClient = new AdvertisementClient(params);
                            registrationClient.register();
                        }

                        Thread.sleep((long) (1000 + (1000 * (10 * Math.random()))));

                    } else {
                        System.out.println("Unable to read registration file:" + registrationFile);
                    }
                } catch (Exception e) {
                    System.err.println("Exception when trying to register service (" + epr + "): " + e.getMessage());
                }

            }
        }

        System.out.println("Type 'exit' to quit, and unregister services:");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = br.readLine();
        while (line != null) {
            if (line.equals("exit")) {
                System.out.println("Exiting.");
                System.exit(0);
            }
            line = br.readLine();
        }
    }
}
