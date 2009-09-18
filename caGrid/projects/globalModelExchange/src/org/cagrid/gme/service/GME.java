package org.cagrid.gme.service;

import gov.nih.nci.cagrid.common.Utils;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xs.StringList;
import org.cagrid.gme.domain.XMLSchema;
import org.cagrid.gme.domain.XMLSchemaBundle;
import org.cagrid.gme.domain.XMLSchemaDocument;
import org.cagrid.gme.domain.XMLSchemaImportInformation;
import org.cagrid.gme.domain.XMLSchemaNamespace;
import org.cagrid.gme.persistence.SchemaPersistenceGeneralException;
import org.cagrid.gme.sax.GMEErrorHandler;
import org.cagrid.gme.sax.GMEXMLSchemaLoader;
import org.cagrid.gme.service.dao.XMLSchemaInformationDao;
import org.cagrid.gme.service.domain.XMLSchemaInformation;
import org.cagrid.gme.stubs.types.InvalidSchemaSubmissionFault;
import org.cagrid.gme.stubs.types.NoSuchNamespaceExistsFault;
import org.cagrid.gme.stubs.types.UnableToDeleteSchemaFault;
import org.globus.wsrf.utils.FaultHelper;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public class GME {
    protected static Log LOG = LogFactory.getLog(GME.class.getName());
    protected XMLSchemaInformationDao schemaDao;

    // provides coarse grain persistence layer locking, used to ensure integrity
    // of
    protected ReentrantReadWriteLock lock = new ReentrantReadWriteLock();


    public void setXMLSchemaInformationDao(XMLSchemaInformationDao schemaDao) {
        if (schemaDao == null) {
            throw new IllegalArgumentException("Cannot use a null XMLSchemaInformationDao!");
        }
        this.schemaDao = schemaDao;
    }


    public void deleteSchemas(Collection<URI> schemaNamespaces) throws NoSuchNamespaceExistsFault,
        UnableToDeleteSchemaFault {

        if (schemaNamespaces == null || schemaNamespaces.size() == 0) {
            String description = "null or empty set is not a valid collection of namespaces to delete.";

            NoSuchNamespaceExistsFault fault = new NoSuchNamespaceExistsFault();
            gov.nih.nci.cagrid.common.FaultHelper helper = new gov.nih.nci.cagrid.common.FaultHelper(fault);
            helper.setDescription(description);
            LOG.debug("Refusing to delete schema: " + description);

            throw (NoSuchNamespaceExistsFault) helper.getFault();
        }

        // need to get a "lock" on the database here
        this.lock.writeLock().lock();
        try {

            Map<URI, XMLSchemaInformation> schemaMap = new HashMap<URI, XMLSchemaInformation>();
            for (URI ns : schemaNamespaces) {
                XMLSchemaInformation schema = this.schemaDao.getByTargetNamespace(ns);
                if (schema == null) {
                    String description = "No schema is published with given targetNamespace (" + ns + ")";

                    NoSuchNamespaceExistsFault fault = new NoSuchNamespaceExistsFault();
                    gov.nih.nci.cagrid.common.FaultHelper helper = new gov.nih.nci.cagrid.common.FaultHelper(fault);
                    helper.setDescription(description);
                    LOG.debug("Refusing to delete schema: " + description);

                    throw (NoSuchNamespaceExistsFault) helper.getFault();
                } else {
                    schemaMap.put(ns, schema);
                }
                // TODO: permission check
                // 1. Check permissions on each schema being deleted; fail if
                // don't have permissions

                // 2. Check that all schemas being deleted are in a state where
                // the
                // contents can be deleted ; fail otherwise
            }

            for (URI ns : schemaNamespaces) {
                Collection<XMLSchema> dependingXMLSchemas = this.schemaDao.getDependingXMLSchemas(ns);
                // get the list of depending schemas for each of the namespace

                // for each, make sure the list is 0, or every depending schema
                // will
                // also be deleted (i.e. the namespace is in the provided list
                // to
                // delete)
                for (XMLSchema dependingSchema : dependingXMLSchemas) {
                    URI dependingTargetNamespace = dependingSchema.getTargetNamespace();
                    if (!schemaMap.containsKey(dependingTargetNamespace)) {
                        String description = "Cannot delete XMLSchema (" + ns + ") as it is imported by XMLSchema ("
                            + dependingTargetNamespace + ").  You must also delete the XMLSchema ("
                            + dependingTargetNamespace + ") or udpate it to not depend on XMLSchema (" + ns + ")";
                        // REVIST: should i keep going and build up a list of
                        // these,
                        // or just failfast?

                        UnableToDeleteSchemaFault fault = new UnableToDeleteSchemaFault();
                        gov.nih.nci.cagrid.common.FaultHelper helper = new gov.nih.nci.cagrid.common.FaultHelper(fault);
                        helper.setDescription(description);
                        LOG.debug("Refusing to delete schema: " + description);
                        throw (UnableToDeleteSchemaFault) helper.getFault();
                    } else {
                        LOG.debug("So far ok to delete XMLSchema (" + ns
                            + ") even though it is imported by XMLSchema (" + dependingTargetNamespace
                            + "), as it is being requested to be deleted as well.");
                    }
                }
            }
            // ok to delete all these schemas now
            this.schemaDao.delete(schemaMap.values());

        } finally {
            // release database lock
            this.lock.writeLock().unlock();
        }
    }


    public void publishSchemas(List<XMLSchema> schemas) throws InvalidSchemaSubmissionFault {

        // 0. sanity check submission
        if (schemas == null || schemas.size() == 0) {
            String message = "No schemas were found in the submission.";
            LOG.error(message);

            InvalidSchemaSubmissionFault e = new InvalidSchemaSubmissionFault();
            e.setFaultString(message);

            throw e;
        }

        // need to get a "lock" on the database here
        this.lock.writeLock().lock();
        try {
            // 3. Create a list of "processed schemas"
            Map<URI, SchemaGrammar> processedSchemas = verifySubmissionAndInitializeProcessedSchemasMap(schemas);

            // 4. Create a model with error and entity resolver (on
            // callback
            // to imports/includes/redefines, entity resolver needs to first
            // load schema from
            // submission if present, if not in submission load from DB, if not
            // in
            // DB error out)
            GMEXMLSchemaLoader schemaLoader = new GMEXMLSchemaLoader(schemas, this.schemaDao);

            // 5. Call processSchema() for each schema being uploaded
            for (XMLSchema submittedSchema : schemas) {
                try {
                    processSchema(schemaLoader, processedSchemas, submittedSchema);
                } catch (Exception e) {
                    String message = "Problem processing schema submissions; the schema ["
                        + submittedSchema.getTargetNamespace() + "] was not valid:" + Utils.getExceptionMessage(e);
                    LOG.error(message, e);

                    InvalidSchemaSubmissionFault fault = new InvalidSchemaSubmissionFault();
                    fault.setFaultString(message);
                    FaultHelper helper = new FaultHelper(fault);
                    helper.addFaultCause(e);
                    fault = (InvalidSchemaSubmissionFault) helper.getFault();

                    throw fault;
                }
            }

            // 8. Commit new/modified schemas to database, populating dependency
            // schema information (gathered from imports)

            commitSchemas(schemas, processedSchemas);

        } finally {
            // release database lock
            this.lock.writeLock().unlock();
        }
    }


    protected Map<URI, SchemaGrammar> verifySubmissionAndInitializeProcessedSchemasMap(List<XMLSchema> schemas)
        throws InvalidSchemaSubmissionFault {
        Map<URI, SchemaGrammar> processedSchemas = new HashMap<URI, SchemaGrammar>();
        for (XMLSchema submittedSchema : schemas) {
            // verify the schema's have unique and valid URIs
            URI namespace = submittedSchema.getTargetNamespace();
            if (namespace == null) {
                String message = "The schema submission contained a schema with a null URI.";
                LOG.error(message);

                InvalidSchemaSubmissionFault e = new InvalidSchemaSubmissionFault();
                e.setFaultString(message);

                throw e;

            }
            if (processedSchemas.containsKey(namespace)) {
                String message = "The schema submission contained multiple schemas of the same URI ("
                    + namespace
                    + ").  If you intend to submit schemas with includes, you need to package them as a single Schema with multiple SchemaDocuments.";
                LOG.error(message);

                InvalidSchemaSubmissionFault e = new InvalidSchemaSubmissionFault();
                e.setFaultString(message);

                throw e;
            } else {
                if (submittedSchema.getRootDocument() == null) {
                    String message = "The schema submission contained a schema ["
                        + submittedSchema.getTargetNamespace() + "] without a root schema document.";
                    LOG.error(message);

                    InvalidSchemaSubmissionFault e = new InvalidSchemaSubmissionFault();
                    e.setFaultString(message);

                    throw e;
                }

                // REVISIT: should probably check SchemaDocument rules here:
                // unique systemIDs for all [this is accomplished by using
                // Set now]
                // need to actually check that the schema rules are true
                // (i.e include must have no ns, or same ns), but basically
                // need the parsed model to know this... so i can check at
                // resolution time, but if something is never referenced, it
                // wont be loaded and so may be invalid... probably need to
                // check it after we have the grammar (can we ask for
                // includes and check for correspondence in the submission?)
                // [this is checked by combination of parser (checking rules
                // of what it reads) and matching
                // the loaded doc size matches the submitted doc
                // size (meaning they were all read)]

                // preload the submission schemas with "null" models (which
                // will be replaced by the actual models once they are
                // processed) so they don't get inappropriately processed
                // (as could be the case during the loading of "depending
                // schemas" )
                processedSchemas.put(namespace, null);
            }

            // TODO: permission check
            // 1. Check permissions on each schema being published; fail if
            // don't have permissions

            // 2. Check that all schemas being published are either not yet
            // published, or are in a state where the contents can be
            // updated ;
            // fail otherwise
            XMLSchema storedSchema = this.schemaDao.getXMLSchemaByTargetNamespace(namespace);
            if (storedSchema != null) {
                // TODO: check that it can be modified before doing this
                // (right now it is never allowed)
                // if(storeSchema.getState... != ){
                String message = "The schema [" + namespace + "] already exists and cannot be modified.";
                LOG.error(message);

                InvalidSchemaSubmissionFault e = new InvalidSchemaSubmissionFault();
                e.setFaultString(message);

                throw e;
            }
        }
        return processedSchemas;
    }


    protected void commitSchemas(List<XMLSchema> schemas, Map<URI, SchemaGrammar> processedSchemas)
        throws InvalidSchemaSubmissionFault {

        // if got here with no error, schemas are ok to persist
        // build up DB objects to commit
        Map<XMLSchema, List<URI>> toCommit = new HashMap<XMLSchema, List<URI>>();
        for (XMLSchema submittedSchema : schemas) {
            // extract the schema model
            SchemaGrammar schemaGrammar = processedSchemas.get(submittedSchema.getTargetNamespace());
            assert schemaGrammar != null;
            assert !toCommit.containsKey(submittedSchema);

            // this has all the expanded locations which built up the schema
            // for us (using the namespace as the basesystemID, this should
            // be the ns+systemID)
            // REVISIT: is there a better way to figure out the
            // included/redefined documents?
            StringList documentLocations = schemaGrammar.getDocumentLocations();
            for (XMLSchemaDocument schemaDocument : submittedSchema.getAdditionalSchemaDocuments()) {
                URI expandedURI;
                try {
                    // REVISIT: is this the right way to construct this.
                    // NOTE: this must match the behavior of the
                    // GMEEntityResolver when it create the baseSystemID for
                    // the XMLInputSource it loads
                    expandedURI = new URI(submittedSchema.getTargetNamespace().toString() + "/"
                        + schemaDocument.getSystemID());

                } catch (Exception e) {
                    String message = "Problem processing schema submissions; the schema ["
                        + submittedSchema.getTargetNamespace() + "] included a SchemaDocument ["
                        + schemaDocument.getSystemID() + "] whose expanded URI was not valid:"
                        + Utils.getExceptionMessage(e);
                    LOG.error(message, e);

                    InvalidSchemaSubmissionFault fault = new InvalidSchemaSubmissionFault();
                    fault.setFaultString(message);
                    FaultHelper helper = new FaultHelper(fault);
                    helper.addFaultCause(e);
                    fault = (InvalidSchemaSubmissionFault) helper.getFault();

                    throw fault;
                }
                if (!documentLocations.contains(expandedURI.toString())) {
                    String message = "Problem processing schema submissions; the schema ["
                        + submittedSchema.getTargetNamespace() + "] included a SchemaDocument ["
                        + schemaDocument.getSystemID() + "] which was not used by the parsed grammar";
                    LOG.error(message);

                    InvalidSchemaSubmissionFault fault = new InvalidSchemaSubmissionFault();
                    fault.setFaultString(message);
                    FaultHelper helper = new FaultHelper(fault);
                    fault = (InvalidSchemaSubmissionFault) helper.getFault();

                    throw fault;
                }
            }
            // we must have a document for the root and each additional
            // schema document
            if (documentLocations.getLength() != submittedSchema.getAdditionalSchemaDocuments().size() + 1) {
                String message = "Problem processing schema submissions; the schema ["
                    + submittedSchema.getTargetNamespace() + "] contained ["
                    + submittedSchema.getAdditionalSchemaDocuments().size()
                    + "] SchemaDocuments but the parsed grammar contained [" + documentLocations.getLength()
                    + "].  All SchemaDocuments must be used by the Schema.";
                LOG.error(message);

                InvalidSchemaSubmissionFault fault = new InvalidSchemaSubmissionFault();
                fault.setFaultString(message);
                FaultHelper helper = new FaultHelper(fault);
                fault = (InvalidSchemaSubmissionFault) helper.getFault();

                throw fault;

            }

            // build an import list
            List<URI> importList = new ArrayList<URI>();
            Vector importedGrammars = schemaGrammar.getImportedGrammars();
            if (importedGrammars != null) {
                for (int i = 0; i < importedGrammars.size(); i++) {
                    SchemaGrammar importedSchema = (SchemaGrammar) importedGrammars.get(i);
                    String importedTargetNS = importedSchema.getTargetNamespace();
                    LOG.info("Schema [" + schemaGrammar.getTargetNamespace() + "] imports schema [" + importedTargetNS
                        + "]");
                    try {
                        importList.add(new URI(importedTargetNS));
                    } catch (URISyntaxException e) {
                        String message = "Problem processing schema submissions; the schema ["
                            + submittedSchema.getTargetNamespace() + "] imported a schema [" + importedTargetNS
                            + "] whose URI was not valid:" + Utils.getExceptionMessage(e);
                        LOG.error(message, e);

                        InvalidSchemaSubmissionFault fault = new InvalidSchemaSubmissionFault();
                        fault.setFaultString(message);
                        FaultHelper helper = new FaultHelper(fault);
                        helper.addFaultCause(e);
                        fault = (InvalidSchemaSubmissionFault) helper.getFault();

                        throw fault;
                    }
                }
            }
            // add the schema and its import list to the Map to commit
            toCommit.put(submittedSchema, importList);
        }

        // TODO: replace this call by embedding its logic above
        // commit to database
        this.storeSchemas(toCommit);
    }


    // TODO: rewrite this with the caller above to directly make use of the DAO
    // instead of building up a map and calling this (this is refactor cruft
    // leftover from removing the SchemaPersitence layer)
    // TODO: need to add rollBackFor=... to specify exceptions which should
    // cause rollbacks (does that rollbackfor subclasses of the specified
    // exception?)
    private void storeSchemas(Map<XMLSchema, List<URI>> schemasToStore) {
        // REVISIT: is there a simpler way to do this

        // this is a list of newly persistent XMLSchemaInformation (for those
        // which are being saved), and already persistent XMLSchemaInformation
        // (for those that are being imported and not updated)
        Map<URI, XMLSchemaInformation> persistedInfos = new HashMap<URI, XMLSchemaInformation>();

        // foreach XMLSchema
        for (XMLSchema s : schemasToStore.keySet()) {
            // find PersistableXMLSchema (by URI), create if null, save
            XMLSchemaInformation info = this.schemaDao.getByTargetNamespace(s.getTargetNamespace());
            if (info == null) {
                info = new XMLSchemaInformation();
            }
            // -setSchema XMLSchema on XMLSchemaInformation
            info.setSchema(s);

            this.schemaDao.save(info);

            // -put in hash of URI->XMLSchemaInformation
            persistedInfos.put(s.getTargetNamespace(), info);
        }
        // all new/updated schemas are now in the hash and persistent

        // foreach XMLSchema (make the changes)
        for (XMLSchema s : schemasToStore.keySet()) {
            // -get PersistableXMLSchema from hash
            XMLSchemaInformation info = persistedInfos.get(s.getTargetNamespace());

            Set<XMLSchemaInformation> importSet = new HashSet<XMLSchemaInformation>();
            List<URI> importList = schemasToStore.get(s);
            // -foreach URI in import List
            for (URI importedURI : importList) {
                // --if not in hash
                XMLSchemaInformation importedInfo = persistedInfos.get(importedURI);
                if (importedInfo == null) {
                    // --- getReference to PersistableXMLSchema, put in hash
                    importedInfo = this.schemaDao.getByTargetNamespace(importedURI);
                    // this must either be new and already in the hash (the
                    // containing if), or existing and therefore in the db
                    // already
                    assert importedInfo != null;
                    persistedInfos.put(s.getTargetNamespace(), importedInfo);
                }
                // --add toimportSet
                importSet.add(importedInfo);
            }

            // -set importSet on PersistableXMLSchema
            info.setImports(importSet);

            // TODO: I don't think I should have to do this... shouldn't the
            // DAO-returned objects still be persistent and notice the changes?
            this.schemaDao.save(info);
        }
    }


    protected void processSchema(GMEXMLSchemaLoader schemaLoader, Map<URI, SchemaGrammar> processedSchemas,
        XMLSchema schemaToProcess) throws XNIException, IOException, SchemaPersistenceGeneralException {
        LOG.debug("About to process schema [" + schemaToProcess.getTargetNamespace() + "].");

        // 6.2. Add the schema to the model which will recursively fire
        // callbacks to the entity resolver for all imports (loading all
        // dependency schemas, and their dependency schemas, etc)
        String ns = schemaToProcess.getTargetNamespace().toString();

        XMLSchemaDocument rootSD = schemaToProcess.getRootDocument();

        // REVISIT: what to set for the baseSystemID? it's used to convert
        // includes, etc into full URIs and so is relevant later when examining
        // documentLocations of the SchemaGrammar
        XMLInputSource xis = new XMLInputSource(ns, rootSD.getSystemID(), ns, new StringReader(rootSD.getSchemaText()),
            "UTF-16");
        SchemaGrammar model = (SchemaGrammar) schemaLoader.loadGrammar(xis);
        if (model == null) {
            GMEErrorHandler errorHandler = schemaLoader.getErrorHandler();
            throw errorHandler.createXMLParseException();
        }

        // we should not have processed this schema before, and if the URI
        // is in the list it should have a null model (indicating it was
        // preloaded from the submission schemas set)
        assert !processedSchemas.containsKey(schemaToProcess.getTargetNamespace())
            || processedSchemas.get(schemaToProcess.getTargetNamespace()) == null;

        // store the resultant schema model (this needs to happen before
        // recursion)
        processedSchemas.put(schemaToProcess.getTargetNamespace(), model);

        String targetURI = model.getTargetNamespace();
        if (!ns.equals(targetURI)) {
            String message = "Problem processing schema submissions; the schema["
                + schemaToProcess.getTargetNamespace() + "] was not valid as its acutal targetURI [" + targetURI
                + "] did not match.";
            LOG.error(message);
            InvalidSchemaSubmissionFault fault = new InvalidSchemaSubmissionFault();
            fault.setFaultString(message);
            throw fault;
        }

        // 6.3. Look in the DB for depending schemas (will only be present if
        // schema was already published and is being updated)
        Collection<XMLSchema> dependingSchemas = this.schemaDao.getDependingXMLSchemas(schemaToProcess
            .getTargetNamespace());

        // 6.4. For each depending schema not in the list of "processed schemas"
        // call processSchema()
        for (XMLSchema dependingSchema : dependingSchemas) {
            if (processedSchemas.containsKey(dependingSchema.getTargetNamespace())) {
                LOG.debug("Depending schema [" + dependingSchema.getTargetNamespace()
                    + "] was already processed (or will be processed).");
            } else {
                LOG.debug("Processing depending schema [" + dependingSchema.getTargetNamespace()
                    + "] which is not in the submission package.");
                processSchema(schemaLoader, processedSchemas, dependingSchema);
            }
        }
    }


    /**
     * Returns the targetNamespaces (represented by URIs) of all published
     * XMLSchemas
     * 
     * @return the targetNamespaces (represented by URIs) of all published
     *         XMLSchemas
     */
    @Transactional(readOnly = true)
    public Collection<URI> getNamespaces() {
        this.lock.readLock().lock();
        try {
            return this.schemaDao.getAllNamespaces();
        } finally {
            this.lock.readLock().unlock();
        }
    }


    /**
     * Returns a published XMLSchema with a targetNamespace equal to the given
     * URI
     * 
     * @param uri
     *            the targetNamespace of the desired XMLSchema
     * @return a published XMLSchema with a targetNamespace equal to the given
     *         URI
     * @throws NoSuchNamespaceExistsFault
     *             if there is no published Schema with a targetNamespace equal
     *             to the given URI
     */
    @Transactional(readOnly = true)
    public XMLSchema getSchema(URI uri) throws NoSuchNamespaceExistsFault {
        this.lock.readLock().lock();
        try {
            XMLSchema result = this.schemaDao.getMaterializedXMLSchemaByTargetNamespace(uri);
            if (result == null) {
                String description = "No schema is published with given targetNamespace (" + uri + ")";

                NoSuchNamespaceExistsFault fault = new NoSuchNamespaceExistsFault();
                gov.nih.nci.cagrid.common.FaultHelper helper = new gov.nih.nci.cagrid.common.FaultHelper(fault);
                helper.setDescription(description);
                LOG.debug("Cannot retrieve requested schema: " + description);

                throw (NoSuchNamespaceExistsFault) helper.getFault();
            } else {
                return result;
            }

        } finally {
            this.lock.readLock().unlock();
        }
    }


    /**
     * Return a Collection of URIs representing the targetNamespaces of the
     * XMLSchemas which are imported by the XMLSchema identified by the given
     * targetNamespace
     * 
     * @param targetNamespace
     *            the targetNamespace of the desired XMLSchema
     * @return a Collection of URIs representing the targetNamespaces of the
     *         XMLSchemas which are imported by the XMLSchema identified by the
     *         given targetNamespace
     * @throws NoSuchNamespaceExistsFault
     *             if there is no published Schema with a targetNamespace equal
     *             to the given URI
     */
    @Transactional(readOnly = true)
    public Collection<URI> getImportedNamespaces(URI targetNamespace) throws NoSuchNamespaceExistsFault {
        XMLSchemaInformation info = this.schemaDao.getByTargetNamespace(targetNamespace);
        if (info == null) {
            String description = "No schema is published with given targetNamespace (" + targetNamespace + ")";

            NoSuchNamespaceExistsFault fault = new NoSuchNamespaceExistsFault();
            gov.nih.nci.cagrid.common.FaultHelper helper = new gov.nih.nci.cagrid.common.FaultHelper(fault);
            helper.setDescription(description);
            LOG.debug("Cannot retrieve imported namespaces of schema: " + description);

            throw (NoSuchNamespaceExistsFault) helper.getFault();
        }

        List<URI> result = new ArrayList<URI>();
        Set<XMLSchemaInformation> imports = info.getImports();
        for (XMLSchemaInformation importedInfo : imports) {
            result.add(importedInfo.getSchema().getTargetNamespace());
        }

        return result;

    }


    /**
     * Return a Collection of URIs representing the targetNamespaces of the
     * XMLSchemas which import the XMLSchema identified by the given
     * targetNamespace
     * 
     * @param targetNamespace
     *            the targetNamespace of the desired XMLSchema
     * @return a Collection of URIs representing the targetNamespaces of the
     *         XMLSchemas which import the XMLSchema identified by the given
     *         targetNamespace
     * @throws NoSuchNamespaceExistsFault
     *             if there is no published Schema with a targetNamespace equal
     *             to the given URI
     */
    @Transactional(readOnly = true)
    public Collection<URI> getImportingNamespaces(URI targetNamespace) throws NoSuchNamespaceExistsFault {
        XMLSchema schema = this.schemaDao.getXMLSchemaByTargetNamespace(targetNamespace);
        if (schema == null) {
            String description = "No schema is published with given targetNamespace (" + targetNamespace + ")";

            NoSuchNamespaceExistsFault fault = new NoSuchNamespaceExistsFault();
            gov.nih.nci.cagrid.common.FaultHelper helper = new gov.nih.nci.cagrid.common.FaultHelper(fault);
            helper.setDescription(description);
            LOG.debug("Cannot retrieve importing namespaces of schema: " + description);

            throw (NoSuchNamespaceExistsFault) helper.getFault();
        }

        Collection<XMLSchema> dependingXMLSchemas = this.schemaDao.getDependingXMLSchemas(targetNamespace);

        List<URI> result = new ArrayList<URI>();
        for (XMLSchema importingSchema : dependingXMLSchemas) {
            result.add(importingSchema.getTargetNamespace());
        }

        return result;
    }


    @Transactional(readOnly = true)
    public XMLSchemaBundle getSchemBundle(URI targetNamespace) throws NoSuchNamespaceExistsFault {
        XMLSchemaInformation info = this.schemaDao.getByTargetNamespace(targetNamespace);
        if (info == null) {
            String description = "No schema is published with given targetNamespace (" + targetNamespace + ")";

            NoSuchNamespaceExistsFault fault = new NoSuchNamespaceExistsFault();
            gov.nih.nci.cagrid.common.FaultHelper helper = new gov.nih.nci.cagrid.common.FaultHelper(fault);
            helper.setDescription(description);
            LOG.debug("Cannot retrieve schema and dependencies: " + description);

            throw (NoSuchNamespaceExistsFault) helper.getFault();
        }

        XMLSchemaBundle bundle = new XMLSchemaBundle();
        collectSchemasForBundle(bundle, info);

        return bundle;

    }


    private void collectSchemasForBundle(XMLSchemaBundle bundle, XMLSchemaInformation info) {
        // make sure we haven't already processed these schema, such as from
        // another schemas's import
        Set<XMLSchema> schemas = bundle.getXMLSchemas();
        if (schemas.contains(info.getSchema())) {
            // we've already processed this, so return
            return;
        }

        // add this to the bucket and make sure it is populated
        this.schemaDao.materializeXMLSchemaInformation(info);
        schemas.add(info.getSchema());

        // create importinfo for each imported schema (if any)
        if (info.getImports().size() > 0) {
            Set<XMLSchemaImportInformation> importInfoSet = bundle.getImportInformation();
            // make a new importinfo for this schema
            XMLSchemaImportInformation importInfo = new XMLSchemaImportInformation();
            importInfo.setTargetNamespace(new XMLSchemaNamespace(info.getSchema().getTargetNamespace()));
            assert !importInfoSet.contains(importInfo) : "The bundle should not contain import information about XMLSchema ("
                + info.getSchema().getTargetNamespace() + ") as it did not contain the schema itself.";

            // add the collected import set
            importInfoSet.add(importInfo);

            // recursively process each of the schemas this schema imports
            for (XMLSchemaInformation importedInfo : info.getImports()) {
                importInfo.getImports().add(new XMLSchemaNamespace(importedInfo.getSchema().getTargetNamespace()));
                collectSchemasForBundle(bundle, importedInfo);
            }
        }

    }
}
