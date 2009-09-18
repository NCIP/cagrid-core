package org.cagrid.gme.discoverytools;

import gov.nih.nci.cagrid.common.portal.MultiEventProgressBar;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.configuration.NamespaceReplacementPolicy;
import gov.nih.nci.cagrid.introduce.beans.extension.DiscoveryExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespacesType;
import gov.nih.nci.cagrid.introduce.portal.modification.discovery.NamespaceTypeDiscoveryComponent;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gme.client.GlobalModelExchangeClient;
import org.cagrid.gme.common.FilesystemCacher;
import org.cagrid.gme.domain.XMLSchemaBundle;
import org.cagrid.gme.domain.XMLSchemaNamespace;
import org.cagrid.gme.stubs.types.NoSuchNamespaceExistsFault;


public abstract class GMETypeSelectionComponentBase extends NamespaceTypeDiscoveryComponent {

    private static final Log logger = LogFactory.getLog(GMETypeSelectionComponentBase.class);


    /**
     * Uses the SchemaCacher but only returns the "new" schemas
     * 
     * @param dir
     * @param namespace
     * @param exisingSchemasToIgnore
     * @return
     * @throws NoSuchNamespaceExistsFault
     * @throws RemoteException
     * @throws IOException
     * @throws Exception
     */
    private Map<XMLSchemaNamespace, File> cacheSchemas(File dir, XMLSchemaNamespace namespace,
        Map<URI, File> exisingSchemasToIgnore) throws NoSuchNamespaceExistsFault, RemoteException, IOException,
        Exception {
        GlobalModelExchangeClient client = new GlobalModelExchangeClient(getGMEURL());

        XMLSchemaBundle bundle = client.getXMLSchemaAndDependencies(namespace);
        FilesystemCacher cacher = new FilesystemCacher(bundle, dir);
        Map<URI, File> cachedSchemas = cacher.cacheSchemas(exisingSchemasToIgnore);

        Map<XMLSchemaNamespace, File> result = new HashMap<XMLSchemaNamespace, File>();
        for (URI uri : cachedSchemas.keySet()) {
            if (!exisingSchemasToIgnore.containsKey(uri)) {
                result.put(new XMLSchemaNamespace(uri), cachedSchemas.get(uri));
            } else {
                logger.info("Skipping creation of pre-existing schema:" + uri);
            }
        }

        return result;
    }


    protected abstract String getGMEURL() throws Exception;


    protected abstract XMLSchemaNamespace getCurrentSchemaNamespace();


    @Override
    public NamespaceType[] createNamespaceType(File schemaDestinationDir, NamespaceReplacementPolicy replacementPolicy,
        MultiEventProgressBar progress) {
        XMLSchemaNamespace selectedNS = getCurrentSchemaNamespace();
        if (selectedNS == null) {
            String error = "No valid namespace was selected.";
            logger.error(error);
            addError(error);

            return null;
        }

        if (!selectedNS.toString().equals(IntroduceConstants.W3CNAMESPACE)) {
            try {

                int startEventID = progress.startEvent("Contacting GME for schemas...");

                Map<URI, File> existingSchemas = new HashMap<URI, File>();

                if (replacementPolicy.equals(NamespaceReplacementPolicy.IGNORE)) {
                    // pass a Map<URI, File> of the existing
                    // namespaces if the replacement policy is IGNORE (such that
                    // those schemas can be reused)
                    if (getCurrentNamespaces() != null && getCurrentNamespaces().getNamespace() != null) {
                        for (NamespaceType ns : getCurrentNamespaces().getNamespace()) {
                            String namespace = ns.getNamespace();
                            if (!namespace.equals(IntroduceConstants.W3CNAMESPACE)) {
                                String fileLocation = ns.getLocation();
                                if (fileLocation != null) {
                                    existingSchemas.put(new URI(namespace),
                                        new File(schemaDestinationDir, fileLocation));
                                } else {
                                    logger.error("Found an existing namespace (" + namespace
                                        + ") that didn't have a file location; can't ignore it.");
                                }
                            }
                        }
                    }
                }

                Map<XMLSchemaNamespace, File> cachedSchemas = null;

                try {
                    cachedSchemas = cacheSchemas(schemaDestinationDir, selectedNS, existingSchemas);
                } catch (NoSuchNamespaceExistsFault e) {
                    String error = "Namespace (" + selectedNS + ") does not exist in the GME.";
                    logger.error(error, e);
                    addError(error);
                    return null;
                }

                progress.stopEvent(startEventID, "Successfully retrieved " + cachedSchemas.size() + " new schemas.");

                NamespaceType[] types = null;

                // check that it is ok to apply the changes
                boolean shouldFail = false;
                for (XMLSchemaNamespace ns : cachedSchemas.keySet()) {
                    if (namespaceAlreadyExists(ns.toString())) {
                        if (replacementPolicy.equals(NamespaceReplacementPolicy.ERROR)) {
                            shouldFail = true;

                            String error = "Namespace ("
                                + ns
                                + ") already exists, and policy was to error. Change the setting in the Preferences to REPLACE or IGNORE to avoid this error.";
                            logger.error(error);
                            addError(error);
                        }
                    }
                }

                // if we copied in schemas we aren't going to use, clean them up
                // and return
                if (shouldFail) {
                    for (XMLSchemaNamespace ns : cachedSchemas.keySet()) {
                        logger.debug("Removing schema (" + cachedSchemas.get(ns)
                            + ") we aren't keeping because policy was to ERROR.");
                        cachedSchemas.get(ns).delete();
                    }
                    return null;
                }

                types = new NamespaceType[cachedSchemas.size()];
                int typesIndex = 0;
                // now walk again and actually create the types
                for (XMLSchemaNamespace ns : cachedSchemas.keySet()) {
                    File schemaFile = cachedSchemas.get(ns);
                    types[typesIndex++] = NamespaceTools.createNamespaceTypeForFile(schemaFile.getCanonicalPath(),
                        schemaDestinationDir);
                }

                return types;
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e);
                addError(e.getMessage());
                return null;
            } finally {
                progress.stopAll("");
            }
        } else {
            return new NamespaceType[0];
        }
    }


    public GMETypeSelectionComponentBase(DiscoveryExtensionDescriptionType descriptor, NamespacesType currentNamespaces) {
        super(descriptor, currentNamespaces);
    }

}