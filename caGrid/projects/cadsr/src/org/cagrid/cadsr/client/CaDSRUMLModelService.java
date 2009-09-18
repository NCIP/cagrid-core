package org.cagrid.cadsr.client;

import gov.nih.nci.cadsr.umlproject.domain.Project;
import gov.nih.nci.cadsr.umlproject.domain.UMLClassMetadata;
import gov.nih.nci.cadsr.umlproject.domain.UMLPackageMetadata;
import gov.nih.nci.cagrid.cqlquery.Association;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Group;
import gov.nih.nci.cagrid.cqlquery.LogicalOperator;
import gov.nih.nci.cagrid.cqlquery.Object;
import gov.nih.nci.cagrid.cqlquery.Predicate;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.client.DataServiceClient;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.cadsr.UMLModelService;


public class CaDSRUMLModelService implements UMLModelService {

    private DataServiceClient client;


    public CaDSRUMLModelService(String address) throws MalformedURIException, RemoteException {
        this(new EndpointReferenceType(new URI(address)));
    }


    public CaDSRUMLModelService(EndpointReferenceType epr) throws MalformedURIException, RemoteException {
        client = new DataServiceClient(epr);
    }


    public Project[] findAllProjects() throws RemoteException {
        CQLQuery query = new CQLQuery();
        Object target = new Object();
        query.setTarget(target);
        target.setName(Project.class.getName());

        CQLQueryResults results = this.client.query(query);

        Iterator iter = new CQLQueryResultsIterator(results, this.getClass().getResourceAsStream("client-config.wsdd"));
        int count = 0;

        List<Project> projects = new ArrayList<Project>();
        // iterate and print XML
        while (iter.hasNext()) {
            Project prj = (Project) iter.next();
            projects.add(prj);
            count++;
        }

        Project[] projectsArray = new Project[count];
        return projects.toArray(projectsArray);

    }


    public UMLClassMetadata[] findClassesInPackage(Project project, String packageName) throws RemoteException {
        CQLQuery query = new CQLQuery();

        // looking for UMLClasses
        Object target = new Object();
        query.setTarget(target);
        target.setName(UMLClassMetadata.class.getName());

        // in the specified package
        Association packageAssociation = new Association();
        packageAssociation.setName(UMLPackageMetadata.class.getName());
        Attribute pkgNameAttribute = new Attribute();
        packageAssociation.setAttribute(pkgNameAttribute);
        pkgNameAttribute.setName("name");
        pkgNameAttribute.setPredicate(Predicate.EQUAL_TO);
        pkgNameAttribute.setValue(packageName);

        // with associations to the specified project
        Association projAssociation = new Association();
        projAssociation.setName(Project.class.getName());
        Attribute projIDAttribute = new Attribute();
        projAssociation.setAttribute(projIDAttribute);
        projIDAttribute.setName("id");
        projIDAttribute.setPredicate(Predicate.EQUAL_TO);
        projIDAttribute.setValue(project.getId());

        // our target has both associations
        Group group = new Group();
        target.setGroup(group);
        group.setLogicRelation(LogicalOperator.AND);
        group.setAssociation(new Association[]{projAssociation, packageAssociation});

        CQLQueryResults results = this.client.query(query);

        Iterator iter = new CQLQueryResultsIterator(results, this.getClass().getResourceAsStream("client-config.wsdd"));
        int count = 0;

        List<UMLClassMetadata> classes = new ArrayList<UMLClassMetadata>();
        // iterate and print XML
        while (iter.hasNext()) {
            UMLClassMetadata prj = (UMLClassMetadata) iter.next();
            classes.add(prj);
            count++;
        }

        UMLClassMetadata[] classArray = new UMLClassMetadata[count];
        return classes.toArray(classArray);
    }


    public UMLPackageMetadata[] findPackagesInProject(Project project) throws RemoteException {
        CQLQuery query = new CQLQuery();

        // looking for UMLPackages
        Object target = new Object();
        query.setTarget(target);
        target.setName(UMLPackageMetadata.class.getName());

        // with associations to the specified project
        Association projAssociation = new Association();
        projAssociation.setName(Project.class.getName());
        Attribute projIDAttribute = new Attribute();
        projAssociation.setAttribute(projIDAttribute);
        projIDAttribute.setName("id");
        projIDAttribute.setPredicate(Predicate.EQUAL_TO);
        projIDAttribute.setValue(project.getId());
        target.setAssociation(projAssociation);

        CQLQueryResults results = this.client.query(query);

        Iterator iter = new CQLQueryResultsIterator(results, this.getClass().getResourceAsStream("client-config.wsdd"));
        int count = 0;

        List<UMLPackageMetadata> packages = new ArrayList<UMLPackageMetadata>();
        // iterate and print XML
        while (iter.hasNext()) {
            UMLPackageMetadata prj = (UMLPackageMetadata) iter.next();
            packages.add(prj);
            count++;
        }

        UMLPackageMetadata[] packageArray = new UMLPackageMetadata[count];
        return packages.toArray(packageArray);
    }


    public static void main(String[] args) throws MalformedURIException, RemoteException {
        CaDSRUMLModelService umlService = new CaDSRUMLModelService(
            "http://cadsr-dataservice.nci.nih.gov:80/wsrf/services/cagrid/CaDSRDataService");
        Project[] projects = umlService.findAllProjects();
        for (Project p : projects) {
            System.out.println(p.getLongName());
            UMLPackageMetadata[] packages = umlService.findPackagesInProject(p);
            for (UMLPackageMetadata pack : packages) {
                System.out.println("\t-" + pack.getName());
                UMLClassMetadata[] classes = umlService.findClassesInPackage(p, pack.getName());
                for (UMLClassMetadata clazz : classes) {
                    System.out.println("\t\t-" + clazz.getName());
                }
            }

        }
    }
}
