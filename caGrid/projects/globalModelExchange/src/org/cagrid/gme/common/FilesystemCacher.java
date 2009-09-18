package org.cagrid.gme.common;

import gov.nih.nci.cagrid.common.Utils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cagrid.gme.common.exceptions.SchemaParsingException;
import org.cagrid.gme.domain.XMLSchema;
import org.cagrid.gme.domain.XMLSchemaBundle;
import org.cagrid.gme.domain.XMLSchemaDocument;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;


public class FilesystemCacher extends FilesystemProcessor {

    private final XMLSchemaBundle bundle;
    private final File directory;
    private final Map<XSDDocument, File> fNameMap = new HashMap<XSDDocument, File>();
    private final Map<URI, File> uriMap = new HashMap<URI, File>();


    /**
     * Construct the cacher; you likely want to call cacheSchemas() next.
     * 
     * @param bundle
     * @param directory
     * @throws IllegalArgumentException
     *             if null is passed in
     */
    public FilesystemCacher(XMLSchemaBundle bundle, File directory) throws IllegalArgumentException {
        if (bundle == null || directory == null) {
            throw new IllegalArgumentException("Non-null arguments must be provided");
        }

        this.bundle = bundle;
        this.directory = directory;
    }


    public XMLSchemaBundle getBundle() {
        return this.bundle;
    }


    public File getDirectory() {
        return this.directory;
    }


    /**
     * Writes the documents from the provided bundle to the file system, in the
     * provided directory. NOTE: repeated calls to this method will write new
     * copies of the files
     * 
     * @return a Map of targetNamespace to File that was written
     * @throws IOException
     */
    public Map<URI, File> cacheSchemas() throws IOException {
        return this.cacheSchemas(new HashMap<URI, File>());
    }


    /**
     * Writes the documents from the provided bundle to the file system, in the
     * provided directory. NOTE: repeated calls to this method will write new
     * copies of the files, unless the previously produced Map is passed as
     * input to the subsequent call, in which case no files will be copied. If
     * you pass a subset of the Map, only the missing files will be copied.
     * 
     * @param existingSchemas
     *            a Map of exising schemas which should be used instead of the
     *            those in the bundle
     * @return a Map of targetNamespace to File that was written (or passed in)
     * @throws IOException
     */

    public Map<URI, File> cacheSchemas(Map<URI, File> existingSchemas) throws IOException {
        this.directory.mkdirs();

        // build the map of file names
        buildFileNameMaps(existingSchemas);

        // walk the schemas, write out the documents, fixing
        // imports/includes/redefines schemaLocation to the right location
        fixAndWriteDocuments(existingSchemas);

        return this.uriMap;

    }


    // we need to do this first, so all the imports can be rewritten (need to
    // know all filenames before we can start to write to disk)
    protected void buildFileNameMaps(Map<URI, File> existingSchemas) {

        // for each schema in the bundle
        for (XMLSchema s : this.bundle.getXMLSchemas()) {
            XSDDocument doc = new XSDDocument();
            doc.namespace = s.getTargetNamespace();
            doc.systemID = s.getRootDocument().getSystemID();

            // if we are to ignore this NS, just use the existing file and
            // put it in the maps; skip the additional documents
            // create a unique filename for the schema's root document
            boolean useExisting = false;
            if (existingSchemas.containsKey(s.getTargetNamespace())) {
                useExisting = true;
            }

            File file = null;
            if (useExisting) {
                file = existingSchemas.get(s.getTargetNamespace());
            } else {
                file = createUniqueFileName(doc);
            }

            // save it
            this.fNameMap.put(doc, file);

            // save the root document file as the location to use for imports of
            // that namespace
            this.uriMap.put(s.getTargetNamespace(), file);

            // we only need to process the other files if we are processing the
            // file itself (as it is the only file that can reference these
            // documents)
            if (!useExisting) {
                // for each additional document in the schema's namespace
                for (XMLSchemaDocument d : s.getAdditionalSchemaDocuments()) {
                    doc = new XSDDocument();
                    doc.namespace = s.getTargetNamespace();
                    doc.systemID = d.getSystemID();

                    // create a unique filename
                    file = createUniqueFileName(doc);

                    // save it
                    this.fNameMap.put(doc, file);
                }
            }

        }

    }


    protected File createUniqueFileName(XSDDocument doc) {
        File docFile = new File(this.directory, doc.systemID);

        int i = 0;
        // while the file exists on the file system, or we already plan to use
        // it, come up with a new name and check again
        while (docFile.exists() || this.fNameMap.containsValue(docFile)) {
            docFile = new File(this.directory, i++ + "_" + doc.systemID);
        }

        return docFile;
    }


    protected void fixAndWriteDocuments(Map<URI, File> existingSchemas) throws IOException {
        for (XMLSchema s : this.bundle.getXMLSchemas()) {
            // if we are to ignore this NS: continue because we don't need to
            // fix it or write it
            if (existingSchemas.containsKey(s.getTargetNamespace())) {
                continue;
            }

            XSDDocument doc = new XSDDocument();
            doc.namespace = s.getTargetNamespace();
            doc.systemID = s.getRootDocument().getSystemID();

            fixAndWriteDocument(doc.namespace, s.getRootDocument().getSchemaText(), this.fNameMap.get(doc));

            // for each additional document in the schema's namespace
            for (XMLSchemaDocument d : s.getAdditionalSchemaDocuments()) {
                doc = new XSDDocument();
                doc.namespace = s.getTargetNamespace();
                doc.systemID = d.getSystemID();

                fixAndWriteDocument(doc.namespace, d.getSchemaText(), this.fNameMap.get(doc));
            }

        }

    }


    protected void fixAndWriteDocument(URI namespace, String text, File file) throws IOException {

        Document schemaDoc = createDocumentFromText(text);

        // fix the import elements
        List<Element> imports = getImportElements(schemaDoc);

        for (Element elm : imports) {
            Attribute namespaceAtt = elm.getAttribute(FilesystemProcessor.XSD_NAMESPACE_ATTRIBUTE_NAME);
            URI ns = null;
            try {
                ns = new URI(namespaceAtt.getValue());
            } catch (URISyntaxException e) {
                e.printStackTrace();
                throw new SchemaParsingException(e.getMessage());
            }

            File f = this.uriMap.get(ns);
            assert (f != null);
            String relativePath = Utils.getRelativePath(this.directory, f);

            // this attribute may not exist, so let's just always set a new one
            // (replacing any existing)
            elm.setAttribute(FilesystemProcessor.XSD_SCHEMALOCATION_ATTRIBUTE_NAME, relativePath);
        }

        // fix the includes
        List<Element> includes = getIncludeElements(schemaDoc);
        for (Element elm : includes) {
            Attribute locationAtt = elm.getAttribute(FilesystemProcessor.XSD_SCHEMALOCATION_ATTRIBUTE_NAME);
            String currLocation = locationAtt.getValue();

            XSDDocument doc = new XSDDocument();
            doc.namespace = namespace;
            doc.systemID = currLocation;
            File f = this.fNameMap.get(doc);

            String relativePath = Utils.getRelativePath(this.directory, f);

            // replace the schemaLocation with the new file
            locationAtt.setValue(relativePath);
        }

        // fix the redefines

        List<Element> redefines = getRedefineElements(schemaDoc);
        for (Element elm : redefines) {
            Attribute locationAtt = elm.getAttribute(FilesystemProcessor.XSD_SCHEMALOCATION_ATTRIBUTE_NAME);
            String currLocation = locationAtt.getValue();

            XSDDocument doc = new XSDDocument();
            doc.namespace = namespace;
            doc.systemID = currLocation;

            File f = this.fNameMap.get(doc);
            String relativePath = Utils.getRelativePath(this.directory, f);

            // replace the schemaLocation with the new file
            locationAtt.setValue(relativePath);

        }

        writeDocumentToFile(file, schemaDoc);

    }

}
