package org.cagrid.index.scalability;

import gov.nih.nci.cagrid.discovery.client.DiscoveryClient;
import gov.nih.nci.cagrid.metadata.common.PointOfContact;
import gov.nih.nci.cagrid.metadata.common.UMLClass;

import org.apache.axis.message.addressing.EndpointReferenceType;


public class DiscoveryExamples {

    @SuppressWarnings("null")
    public static void main(String[] args) {
        DiscoveryClient client = null;
        try {
            if (args.length == 1) {
                client = new DiscoveryClient(args[0]);
            } else {
                client = new DiscoveryClient();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        String searchString = "Production";
        String center = "Ohio State University";
        PointOfContact poc = new PointOfContact();
        poc.setFirstName("Scott");
        poc.setLastName("Oster");
        String servName = "CaTissueCore";
        String operName = "getServiceSecurityMetadata";
        UMLClass umlClass = new UMLClass();
        umlClass.setClassName("Project");

        try {
            EndpointReferenceType[] services = null;
            long start;

            printHeader("All Registered Services");
            start = System.currentTimeMillis();
            services = client.getAllServices(false);
            printResults(services, System.currentTimeMillis() - start);

            printHeader("All Compliant Services");
            start = System.currentTimeMillis();
            services = client.getAllServices(true);
            printResults(services, System.currentTimeMillis() - start);

            printHeader("Search String [" + searchString + "]");
            start = System.currentTimeMillis();
            services = client.discoverServicesBySearchString(searchString);
            printResults(services, System.currentTimeMillis() - start);

            printHeader("Research Center Name [" + center + "]");
            start = System.currentTimeMillis();
            services = client.discoverServicesByResearchCenter(center);
            printResults(services, System.currentTimeMillis() - start);

            printHeader("POC [" + poc + "]");
            start = System.currentTimeMillis();
            services = client.discoverServicesByPointOfContact(poc);
            printResults(services, System.currentTimeMillis() - start);

            printHeader("Service name [" + servName + "]");
            start = System.currentTimeMillis();
            services = client.discoverServicesByName(servName);
            printResults(services, System.currentTimeMillis() - start);

            printHeader("Operation name [" + operName + "]");
            start = System.currentTimeMillis();
            services = client.discoverServicesByOperationName(operName);
            printResults(services, System.currentTimeMillis() - start);

            printHeader("Operation input [" + umlClass + "]");
            start = System.currentTimeMillis();
            services = client.discoverServicesByOperationInput(umlClass);
            printResults(services, System.currentTimeMillis() - start);

            printHeader("Operation output [" + umlClass + "]");
            start = System.currentTimeMillis();
            services = client.discoverServicesByOperationOutput(umlClass);
            printResults(services, System.currentTimeMillis() - start);

            printHeader("Operation class [" + umlClass + "]");
            start = System.currentTimeMillis();
            services = client.discoverServicesByOperationClass(umlClass);
            printResults(services, System.currentTimeMillis() - start);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void printHeader(String title) {
        System.out.println("==================================================");
        System.out.println("Querying by " + title);
        System.out.println("==================================================");
    }


    private static void printResults(EndpointReferenceType[] types, long time) {
        if (types != null) {
            for (int i = 0; i < types.length; i++) {
                System.out.println("\t" + i + ")  " + types[i].toString().trim());
            }
        } else {
            System.out.println("no results.");
        }
        System.out.println("Elapsed Seconds: " + time / 1000.0);
        System.out.println("--------------------------------------------------\n\n");
    }
}
