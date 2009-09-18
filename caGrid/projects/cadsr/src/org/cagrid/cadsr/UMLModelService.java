package org.cagrid.cadsr;

import java.rmi.RemoteException;


public interface UMLModelService {

    /**
     * Returns all Projects registered in caDSR.
     * 
     * @return e.g caCore
     */
    public gov.nih.nci.cadsr.umlproject.domain.Project[] findAllProjects() throws RemoteException;


    /**
     * Returns all packages in the given Project.
     * 
     * @param project
     *            e.g caCore
     * @return CSI type = UMLPACKAGE
     * @throws InvalidProjectException
     *             Thrown if the given Project is null, not valid, or ambiquous.
     */
    public gov.nih.nci.cadsr.umlproject.domain.UMLPackageMetadata[] findPackagesInProject(
        gov.nih.nci.cadsr.umlproject.domain.Project project) throws RemoteException;




    /**
     * Returns the Classes in the given Package.
     * 
     * @param project
     *            e.g caCore
     * @param packageName
     * @return caDSR properties of the UML Class in a domain model.
     * @throws InvalidProjectException
     *             Thrown if the given Project is null, not valid, or ambiquous.
     */
    public gov.nih.nci.cadsr.umlproject.domain.UMLClassMetadata[] findClassesInPackage(
        gov.nih.nci.cadsr.umlproject.domain.Project project, java.lang.String packageName) throws RemoteException;


}
