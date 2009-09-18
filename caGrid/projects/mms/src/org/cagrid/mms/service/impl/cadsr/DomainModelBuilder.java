package org.cagrid.mms.service.impl.cadsr;

import gov.nih.nci.cadsr.umlproject.domain.Project;
import gov.nih.nci.cadsr.umlproject.domain.UMLAssociationMetadata;
import gov.nih.nci.cadsr.umlproject.domain.UMLClassMetadata;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModelExposedUMLAssociationCollection;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModelExposedUMLClassCollection;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModelUmlGeneralizationCollection;
import gov.nih.nci.cagrid.metadata.dataservice.UMLAssociation;
import gov.nih.nci.cagrid.metadata.dataservice.UMLAssociationEdge;
import gov.nih.nci.cagrid.metadata.dataservice.UMLAssociationSourceUMLAssociationEdge;
import gov.nih.nci.cagrid.metadata.dataservice.UMLAssociationTargetUMLAssociationEdge;
import gov.nih.nci.cagrid.metadata.dataservice.UMLClass;
import gov.nih.nci.cagrid.metadata.dataservice.UMLClassReference;
import gov.nih.nci.cagrid.metadata.dataservice.UMLGeneralization;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.mms.domain.UMLAssociationExclude;
import org.globus.wsrf.impl.work.WorkManagerImpl;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;

import commonj.work.Work;
import commonj.work.WorkManager;


/**
 * DomainModelBuilder Builds a DomainModel using a thread pool for concurrent
 * requests to the remote ApplicationService. TODO: attempt to handle remote
 * connection refusals by sleeping for a few seconds and retrying?
 * 
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @created Jun 1, 2006
 * @version $Id: DomainModelBuilder.java,v 1.1 2008-11-26 21:04:20 oster Exp $
 */
public class DomainModelBuilder {
    private static final String EXCLUDE_WILDCARD = "*";

    private static final int DEFAULT_POOL_SIZE = 5;

    protected static Log LOG = LogFactory.getLog(DomainModelBuilder.class.getName());

    private ApplicationService cadsr = null;
    private WorkManager workManager = null;


    public DomainModelBuilder(ApplicationService cadsr) {
        this.cadsr = cadsr;
    }


    /**
     * Gets a DomainModel that represents the entire project
     * 
     * @param project
     *            The project for which a domain model will be created
     * @return The domain model
     */
    public DomainModel createDomainModel(Project project) throws DomainModelGenerationException {
        Project proj;
        try {
            proj = CaDSRUtils.findCompleteProject(this.cadsr, project);
        } catch (CaDSRGeneralException e) {
            throw new DomainModelGenerationException("Problem with specified project:" + e.getMessage(), e);
        }
        // get all classes in project, preloading attributes, semantic
        // metadata (current hibernate we are using doesn't process more
        // than 1 preload, but this should work when we move forward)

        HQLCriteria criteria = new HQLCriteria("SELECT DISTINCT c FROM UMLClassMetadata c "
            + "LEFT JOIN c.UMLAttributeMetadataCollection as atts " + "LEFT JOIN atts.semanticMetadataCollection "
            + "LEFT JOIN c.semanticMetadataCollection " + "WHERE c.project.id='" + proj.getId() + "'");

        UMLClassMetadata classArr[];
        try {
            classArr = getProjectClasses(proj, criteria);
        } catch (ApplicationException e) {
            throw new DomainModelGenerationException("Problem getting project's classes.", e);
        }

        UMLAssociationMetadata[] assocArr;
        try {
            assocArr = getProjectAssociationClosure(proj, classArr, null);
        } catch (ApplicationException e) {
            throw new DomainModelGenerationException("Problem getting project's associations.", e);
        }

        return buildDomainModel(proj, classArr, assocArr);

    }


    /**
     * Gets a DomainModel that represents the project and packages
     * 
     * @param project
     *            The project to build a domain model for
     * @param packageNames
     *            The names of packages to include in the domain model
     * @return The domain model
     */
    public DomainModel createDomainModelForPackages(Project project, String[] packageNames)
        throws DomainModelGenerationException {
        Project proj;
        try {
            proj = CaDSRUtils.findCompleteProject(this.cadsr, project);
        } catch (CaDSRGeneralException e) {
            throw new DomainModelGenerationException("Problem with specified project:" + e.getMessage(), e);
        }
        UMLClassMetadata classArr[] = null;
        UMLAssociationMetadata[] assocArr = null;

        if (packageNames != null && packageNames.length > 0) {
            // build up the OR for all the package names
            Set<String> namesSet = new HashSet<String>();
            for (String pack : packageNames) {
                namesSet.add(pack);
            }
            String packageNameFilter = createFilter(namesSet);

            // get all classes in project (where package name IN
            // (packageNames[0], packageNames[1] ...), pre-loading attributes,
            // semantic metadata

            HQLCriteria criteria = new HQLCriteria("SELECT DISTINCT c FROM UMLClassMetadata c "
                + "LEFT JOIN c.UMLAttributeMetadataCollection as atts " + "LEFT JOIN atts.semanticMetadataCollection "
                + "LEFT JOIN c.semanticMetadataCollection " + "WHERE c.project.id='" + proj.getId() + "' "
                + "AND c.UMLPackageMetadata.name " + packageNameFilter);

            try {
                classArr = getProjectClasses(proj, criteria);
            } catch (ApplicationException e) {
                throw new DomainModelGenerationException("Problem getting project's classes.", e);
            }

            try {
                assocArr = getProjectAssociationClosure(proj, classArr, null);
            } catch (ApplicationException e) {
                throw new DomainModelGenerationException("Problem getting project's associations.", e);
            }

        }
        return buildDomainModel(proj, classArr, assocArr);
    }


    /**
     * Gets a DomainModel that represents the project and listed classes;
     * associations will all those between listed classes.
     * 
     * @param project
     *            The project to build a domain model for
     * @param exposedClasses
     *            fully qualified name of classes to include
     * @return The domain model
     * @throws DomainModelGenerationException
     */
    public DomainModel createDomainModelForClasses(Project project, String[] exposedClasses)
        throws DomainModelGenerationException {
        return createDomainModelForClassesWithExcludes(project, exposedClasses, null);
    }


    /**
     * Gets a DomainModel that represents the project and listed classes;
     * associations will all those between listed classes that are not in the
     * excludes list.
     * 
     * @param project
     *            The project to build a domain model for
     * @param fullClassNames
     *            fully qualified name of classes to include
     * @param excludedAssociations
     *            associations to not include
     * @return The domain model
     * @throws DomainModelGenerationException
     */
    public DomainModel createDomainModelForClassesWithExcludes(Project project, String[] fullClassNames,
        UMLAssociationExclude[] excludedAssociations) throws DomainModelGenerationException {
        Project proj;
        try {
            proj = CaDSRUtils.findCompleteProject(this.cadsr, project);
        } catch (CaDSRGeneralException e) {
            throw new DomainModelGenerationException("Problem with specified project:" + e.getMessage(), e);
        }
        UMLClassMetadata classArr[] = null;
        UMLAssociationMetadata[] assocArr = null;

        if (fullClassNames != null && fullClassNames.length > 0) {
            Set<String> namesSet = new HashSet<String>();
            for (String pack : fullClassNames) {
                namesSet.add(pack);
            }
            String fqnFilter = createFilter(namesSet);

            // get all classes in project (wherefullyQualifiedName IN
            // (fullClassNames[0], fullClassNames[1] ...), pre-loading
            // attributes,
            // semantic metadata

            HQLCriteria criteria = new HQLCriteria("SELECT DISTINCT c FROM UMLClassMetadata c "
                + "LEFT JOIN c.UMLAttributeMetadataCollection as atts " + "LEFT JOIN atts.semanticMetadataCollection "
                + "LEFT JOIN c.semanticMetadataCollection " + "WHERE c.project.id='" + proj.getId() + "' "
                + "AND c.fullyQualifiedName " + fqnFilter);

            try {
                classArr = getProjectClasses(proj, criteria);
            } catch (ApplicationException e) {
                throw new DomainModelGenerationException("Problem getting project's classes.", e);
            }

            try {
                assocArr = getProjectAssociationClosure(proj, classArr, excludedAssociations);
            } catch (ApplicationException e) {
                throw new DomainModelGenerationException("Problem getting project's associations.", e);
            }

        }
        return buildDomainModel(proj, classArr, assocArr);
    }


    private String createClassIDFilter(UMLClassMetadata[] classArr) {
        // create a list of class IDs for building closures
        Set<String> idSet = new HashSet<String>();
        if (classArr != null) {
            for (UMLClassMetadata element : classArr) {
                idSet.add(element.getId());
            }
        }

        return createFilter(idSet);
    }


    private String createFilter(Set<String> items) {
        String filter = "";
        StringBuffer sb = new StringBuffer();
        // now build the criteria from the set
        for (Iterator iter = items.iterator(); iter.hasNext();) {
            String item = (String) iter.next();
            sb.append("'" + item + "'");
            if (iter.hasNext()) {
                sb.append(", ");
            }
        }
        if (items.size() > 0) {
            filter = " IN (" + sb.toString() + ")";
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Filter was:" + filter);
        }

        return filter;
    }


    /**
     * Gets all of the associations of this project that are closed over the
     * classes specified in the classArr, and not present in the excludes list.
     * 
     * @param proj
     * @param classArr
     * @param excludedAssociations
     * @return The UML Association metadata array
     * @throws ApplicationException
     */
    private UMLAssociationMetadata[] getProjectAssociationClosure(Project proj, UMLClassMetadata[] classArr,
        UMLAssociationExclude[] excludedAssociations) throws ApplicationException {
        if (classArr == null || classArr.length <= 0) {
            return null;
        }

        // get all associations between classes we are exposing
        String classIDFilter = createClassIDFilter(classArr);

        String associationAlias = "assoc";
        String excludesFilter = createAssociationExcludeFilter(excludedAssociations, associationAlias);

        // get all associations in project
        HQLCriteria hql = new HQLCriteria(("SELECT " + associationAlias + ", " + associationAlias + ".sourceRoleName, "
            + associationAlias + ".sourceUMLClassMetadata.id, " + associationAlias + ".targetRoleName, "
            + associationAlias + ".targetUMLClassMetadata.id " +

            "FROM UMLAssociationMetadata AS " + associationAlias + " WHERE " + associationAlias + ".project.id='"
            + proj.getId() + "' AND " + associationAlias + ".sourceUMLClassMetadata.id " + classIDFilter + " AND "
            + associationAlias + ".targetUMLClassMetadata.id " + classIDFilter + " " + excludesFilter).trim());

        if (LOG.isDebugEnabled()) {
            LOG.debug("Issuing Association query:" + hql.getHqlString());
        }

        // association, src role, src id, tar role, tar id
        long start = System.currentTimeMillis();
        List rList = this.cadsr.query(hql, UMLAssociationMetadata.class.getName());
        // the query incorrectly returns more than one association for the same
        // thing (the association ids are even different), so
        // I build up a unique set based on source and target ids and role names
        // I shouldn't have to do this (its a database view bug), but it creates
        // a lot of unnecessary processing so I'm cutting it out here
        Iterator iterator = rList.iterator();
        Map<String, UMLAssociationMetadata> uniqMap = new HashMap<String, UMLAssociationMetadata>();
        while (iterator.hasNext()) {
            Object[] res = (Object[]) iterator.next();
            UMLAssociationMetadata assoc = (UMLAssociationMetadata) res[0];
            String srcRole = (String) res[1];
            String srcID = (String) res[2];
            String targetRole = (String) res[3];
            String targetID = (String) res[4];
            String createdKey = srcRole + srcID + targetRole + targetID;
            LOG.debug("Created unique key:" + createdKey);
            uniqMap.put(createdKey, assoc);
        }

        Collection uniqList = uniqMap.values();
        LOG.info("Association filtering eliminated " + (rList.size() - uniqList.size())
            + " associations from returned list of:" + rList.size());
        UMLAssociationMetadata assocArr[] = new UMLAssociationMetadata[uniqList.size()];
        // caCORE's toArray(arr) is broken (cacore bug #1382), so need to do
        // this way
        System.arraycopy(uniqList.toArray(), 0, assocArr, 0, uniqList.size());

        double duration = (System.currentTimeMillis() - start) / 1000.0;
        LOG.info(proj.getShortName() + "'s association fetch took " + duration + " seconds, and found "
            + assocArr.length + " associations.");

        return assocArr;
    }


    /**
     * @param excludedAssociations
     * @return An HQL fragment to exclude the specified associations
     */
    private String createAssociationExcludeFilter(UMLAssociationExclude[] excludedAssociations, String alias) {
        if (excludedAssociations == null || excludedAssociations.length == 0) {
            return "";
        }

        StringBuffer sb = new StringBuffer();
        for (UMLAssociationExclude exclude : excludedAssociations) {
            int filterCount = 0;
            sb.append(" AND NOT(");

            // now only process non wildcards (because this criteria is "not"ed
            // so not filtering on the wildcards makes them be excluded)
            if (exclude.getSourceClassName()!=null && !exclude.getSourceClassName().equals(EXCLUDE_WILDCARD)) {
                if (filterCount++ > 0) {
                    sb.append(" AND ");
                }
                sb.append(alias + ".sourceUMLClassMetadata.fullyQualifiedName='" + exclude.getSourceClassName() + "'");

            }
            if (exclude.getTargetClassName()!=null && !exclude.getTargetClassName().equals(EXCLUDE_WILDCARD)) {
                if (filterCount++ > 0) {
                    sb.append(" AND ");
                }
                sb.append(alias + ".targetUMLClassMetadata.fullyQualifiedName='" + exclude.getTargetClassName() + "'");
            }
            if (exclude.getSourceRoleName()!=null && !exclude.getSourceRoleName().equals(EXCLUDE_WILDCARD)) {
                if (filterCount++ > 0) {
                    sb.append(" AND ");
                }
                sb.append(alias + ".sourceRoleName='" + exclude.getSourceRoleName() + "'");
            }
            if (exclude.getTargetRoleName()!=null && !exclude.getTargetRoleName().equals(EXCLUDE_WILDCARD)) {
                if (filterCount++ > 0) {
                    sb.append(" AND ");
                }
                sb.append(alias + ".targetRoleName='" + exclude.getTargetRoleName() + "'");
            }

            // check for all wildcards
            if (filterCount == 0) {
                // just stop processing and create a predicate thats never true,
                // because excluding
                // everything will obviously yeild no results
                return "AND 1=2";
            }

            sb.append(")");
        }

        return sb.toString();
    }


    /**
     * @return Classes in a project
     * @throws ApplicationException
     */
    private UMLClassMetadata[] getProjectClasses(Project proj, HQLCriteria classCriteria) throws ApplicationException {

        long start = System.currentTimeMillis();
        List rList = this.cadsr.query(classCriteria, UMLClassMetadata.class.getName());
        UMLClassMetadata classArr[] = new UMLClassMetadata[rList.size()];
        // caCORE's toArray(arr) is broken (cacore bug #1382), so need to do
        // this way
        System.arraycopy(rList.toArray(), 0, classArr, 0, rList.size());

        double duration = (System.currentTimeMillis() - start) / 1000.0;
        LOG.info(proj.getShortName() + "'s class fetch took " + duration + " seconds, and found " + classArr.length
            + " classes.");

        return classArr;
    }


    /**
     * Generates a DomainModel that represents the project and the given subset
     * of classes and associations
     * 
     * @param proj
     *            The project to build a domain model for
     * @param classes
     *            The classes to include in the domain model
     * @param associations
     *            The asociations to include in the domain model
     * @return The domain model
     * @throws DomainModelGenerationException
     * @throws RemoteException
     */
    protected DomainModel buildDomainModel(final Project proj, UMLClassMetadata[] classes,
        UMLAssociationMetadata[] associations) throws DomainModelGenerationException {

        final DomainModel model = new DomainModel();
        // project
        model.setProjectDescription(proj.getDescription());
        model.setProjectLongName(proj.getLongName());
        model.setProjectShortName(proj.getShortName());
        model.setProjectVersion(proj.getVersion());

        long start = System.currentTimeMillis();
        LOG.info("Beginning processing of classes for project: " + proj.getShortName());
        // classes
        DomainModelExposedUMLClassCollection exposedClasses = new DomainModelExposedUMLClassCollection();
        if (classes != null) {
            List workList = new ArrayList();

            ClassConversionWork[] workers = new ClassConversionWork[classes.length];
            for (int i = 0; i < classes.length; i++) {
                final UMLClassMetadata classMD = classes[i];
                ClassConversionWork work = new ClassConversionWork() {
                    @Override
                    public void run() {
                        try {
                            setUmlClass(CaDSRUtils.convertClassToDataUMLClass(DomainModelBuilder.this.cadsr, proj
                                .getShortName(), proj.getVersion(), classMD));
                        } catch (Exception e) {
                            LOG.error("Error converting class:" + classMD.getFullyQualifiedName(), e);
                        }
                    }
                };
                workers[i] = work;
                try {
                    workList.add(getWorkManager().schedule(work));
                } catch (Exception e) {
                    LOG.error("Error scheduling class conversion work", e);
                    throw new DomainModelGenerationException("Error sheduling class conversion work", e);
                }
            }
            // wait for work item's to complete
            getWorkManager().waitForAll(workList, WorkManager.INDEFINITE);
            // now that they are done, access all the data from the workers
            UMLClass[] umlClasses = new UMLClass[classes.length];
            for (int i = 0; i < workers.length; i++) {
                ClassConversionWork work = workers[i];
                umlClasses[i] = work.getUmlClass();
                if (umlClasses[i] == null) {
                    throw new DomainModelGenerationException("Class converter returned null data!");
                }

            }
            exposedClasses.setUMLClass(umlClasses);
        } else {
            LOG.debug("Class array was null.");
        }
        model.setExposedUMLClassCollection(exposedClasses);
        double duration = (System.currentTimeMillis() - start) / 1000.0;
        LOG.info("Finished class conversion for project: " + proj.getShortName() + " in " + duration + " seconds.");

        start = System.currentTimeMillis();
        LOG.info("Beginning processing of associations for project: " + proj.getShortName());
        // associations
        DomainModelExposedUMLAssociationCollection exposedAssociations = new DomainModelExposedUMLAssociationCollection();
        if (associations != null) {

            List workList = new ArrayList();

            AssociationConversionWork[] workers = new AssociationConversionWork[associations.length];
            for (int i = 0; i < associations.length; i++) {
                final UMLAssociationMetadata assocMD = associations[i];
                AssociationConversionWork work = new AssociationConversionWork() {
                    @Override
                    public void run() {
                        try {
                            setUMLAssociation(convertAssociation(assocMD));
                        } catch (Exception e) {
                            LOG.error("Error converting association:" + associationToString(assocMD), e);
                        }
                    }
                };
                workers[i] = work;
                try {
                    workList.add(getWorkManager().schedule(work));
                } catch (Exception e) {
                    LOG.error("Error scheduling class conversion work", e);
                    throw new DomainModelGenerationException("Error sheduling class conversion work", e);
                }
            }
            // wait for work item's to complete
            getWorkManager().waitForAll(workList, WorkManager.INDEFINITE);
            // now that they are done, access all the data from the workers
            gov.nih.nci.cagrid.metadata.dataservice.UMLAssociation[] umlAssociations = new gov.nih.nci.cagrid.metadata.dataservice.UMLAssociation[associations.length];
            for (int i = 0; i < workers.length; i++) {
                AssociationConversionWork work = workers[i];
                umlAssociations[i] = work.getUMLAssociation();
                if (umlAssociations[i] == null) {
                    throw new DomainModelGenerationException("Association converter returned null data!");
                }
            }
            exposedAssociations.setUMLAssociation(umlAssociations);
        } else {
            LOG.debug("Association array was null.");
        }
        model.setExposedUMLAssociationCollection(exposedAssociations);
        duration = (System.currentTimeMillis() - start) / 1000.0;
        LOG.info("Finished association conversion for project: " + proj.getShortName() + " in " + duration
            + " seconds.");

        LOG.info("Beginning processing of generalizations for project: " + proj.getShortName());
        start = System.currentTimeMillis();
        DomainModelUmlGeneralizationCollection genCollection = new DomainModelUmlGeneralizationCollection();
        if (classes != null && classes.length > 0) {
            // build generalizations
            UMLGeneralization[] genArr = buildGeneralizations(classes);
            LOG.info("Found " + genArr.length + " generalizations for project: " + proj.getShortName());
            genCollection.setUMLGeneralization(genArr);
        }
        model.setUmlGeneralizationCollection(genCollection);
        duration = (System.currentTimeMillis() - start) / 1000.0;
        LOG.info("Finished generalization processing for project: " + proj.getShortName() + " in " + duration
            + " seconds.");

        return model;
    }


    private UMLGeneralization[] buildGeneralizations(UMLClassMetadata[] classes) throws DomainModelGenerationException {
        // get all generalizations between classes we are exposing
        String classIDFilter = createClassIDFilter(classes);

        HQLCriteria hql = new HQLCriteria(
            "SELECT c.id, c.UMLGeneralizationMetadata.superUMLClassMetadata.id FROM UMLClassMetadata AS c WHERE c.id "
                + classIDFilter);
        LOG.debug("Issuing generialization query with HQL:" + hql.getHqlString());

        try {
            List rList = this.cadsr.query(hql, "UMLClassMetadata");
            UMLGeneralization genArr[] = new UMLGeneralization[rList.size()];
            int ind = 0;
            for (Iterator resultsIterator = rList.iterator(); resultsIterator.hasNext();) {
                Object[] result = (Object[]) resultsIterator.next();
                String subID = (String) result[0];
                String superID = (String) result[1];
                UMLGeneralization gen = new UMLGeneralization(new UMLClassReference(subID), new UMLClassReference(
                    superID));
                genArr[ind++] = gen;
            }

            return genArr;
        } catch (Exception e) {
            LOG.error("Error creating Generalizations.", e);
            throw new DomainModelGenerationException("Error creating Generalizations.", e);
        }
    }


    private gov.nih.nci.cagrid.metadata.dataservice.UMLAssociation convertAssociation(
        UMLAssociationMetadata coreAssociation) throws ApplicationException, DomainModelGenerationException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Converting association:" + associationToString(coreAssociation));
        }

        gov.nih.nci.cagrid.metadata.dataservice.UMLAssociation converted = new gov.nih.nci.cagrid.metadata.dataservice.UMLAssociation();
        converted.setBidirectional(coreAssociation.getIsBidirectional().booleanValue());

        // process the source
        UMLAssociationSourceUMLAssociationEdge convertedSourceEdge = new UMLAssociationSourceUMLAssociationEdge();
        UMLAssociationEdge sourceEdge = new UMLAssociationEdge();
        sourceEdge.setMaxCardinality(coreAssociation.getSourceHighCardinality().intValue());
        sourceEdge.setMinCardinality(coreAssociation.getSourceLowCardinality().intValue());
        sourceEdge.setRoleName(coreAssociation.getSourceRoleName());
        if (sourceEdge.getRoleName() == null) {
            sourceEdge.setRoleName("");
        }
        convertedSourceEdge.setUMLAssociationEdge(sourceEdge);
        converted.setSourceUMLAssociationEdge(convertedSourceEdge);

        // process the target
        UMLAssociationTargetUMLAssociationEdge convertedTargetEdge = new UMLAssociationTargetUMLAssociationEdge();
        UMLAssociationEdge targetEdge = new UMLAssociationEdge();
        targetEdge.setMaxCardinality(coreAssociation.getTargetHighCardinality().intValue());
        targetEdge.setMinCardinality(coreAssociation.getTargetLowCardinality().intValue());
        targetEdge.setRoleName(coreAssociation.getTargetRoleName());
        if (targetEdge.getRoleName() == null) {
            targetEdge.setRoleName("");
        }
        convertedTargetEdge.setUMLAssociationEdge(targetEdge);
        converted.setTargetUMLAssociationEdge(convertedTargetEdge);

        // umlproject.UMLAssociationMetadata is broken so need to issue
        // my own hibernate query to get associated UMLClasses (could have just
        // called getters otherwise)
        setUMLClassReferences(coreAssociation, converted);

        return converted;
    }


    private void setUMLClassReferences(UMLAssociationMetadata coreAssociation, UMLAssociation domainAssociation)
        throws ApplicationException, DomainModelGenerationException {

        SimpleExpression idRes = Restrictions.eq("id", coreAssociation.getId());
        ProjectionList projection = Projections.projectionList().add(Projections.property("sourceUMLClassMetadata.id"))
            .add(Projections.property("targetUMLClassMetadata.id"));
        DetachedCriteria criteria = DetachedCriteria.forClass(UMLAssociationMetadata.class);
        criteria.add(idRes);
        criteria.setProjection(projection);

        long start = System.currentTimeMillis();
        List rList = this.cadsr.query(criteria, UMLAssociationMetadata.class.getName());
        Iterator iterator = rList.iterator();
        if (iterator == null || !iterator.hasNext()) {
            throw new DomainModelGenerationException("Unable to located source and target ids for association!");
        }
        // should have length 2, with src, target
        Object[] ids = (Object[]) iterator.next();
        if (ids == null || ids.length != 2) {
            throw new DomainModelGenerationException("Unexpected result during query for association ids!");
        }

        domainAssociation.getSourceUMLAssociationEdge().getUMLAssociationEdge().setUMLClassReference(
            new UMLClassReference((String) ids[0]));

        domainAssociation.getTargetUMLAssociationEdge().getUMLAssociationEdge().setUMLClassReference(
            new UMLClassReference((String) ids[1]));

        double duration = (System.currentTimeMillis() - start) / 1000.0;
        LOG.info("Association id fetch took " + duration + " seconds.");

    }


    private static String associationToString(UMLAssociationMetadata assoc) {
        return assoc.getSourceRoleName() + "(" + assoc.getSourceLowCardinality() + "..."
            + assoc.getSourceHighCardinality() + ")"
            + ((assoc.getIsBidirectional() != null && assoc.getIsBidirectional().booleanValue()) ? "<" : "") + " -->"
            + assoc.getTargetRoleName() + "(" + assoc.getTargetLowCardinality() + "..."
            + assoc.getTargetHighCardinality() + ")";

    }


    public synchronized WorkManager getWorkManager() {
        if (this.workManager == null) {
            this.workManager = new WorkManagerImpl(DEFAULT_POOL_SIZE);
        }

        return this.workManager;
    }


    public synchronized void setWorkManager(WorkManager workManager) {
        this.workManager = workManager;
    }

}

abstract class ClassConversionWork implements Work {
    private UMLClass umlClass;


    public abstract void run();


    public UMLClass getUmlClass() {
        return this.umlClass;
    }


    public void setUmlClass(UMLClass umlClass) {
        this.umlClass = umlClass;
    }


    public boolean isDaemon() {
        return false;
    }


    public void release() {
        // Do nothing
    }
}

abstract class AssociationConversionWork implements Work {
    private UMLAssociation umlAssociation;


    public abstract void run();


    public UMLAssociation getUMLAssociation() {
        return this.umlAssociation;
    }


    public void setUMLAssociation(UMLAssociation umlAssociation) {
        this.umlAssociation = umlAssociation;
    }


    public boolean isDaemon() {
        return false;
    }


    public void release() {
        // Do nothing
    }
}
