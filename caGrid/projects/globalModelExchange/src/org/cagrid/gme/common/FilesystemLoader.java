package org.cagrid.gme.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.cagrid.gme.common.exceptions.SchemaParsingException;
import org.cagrid.gme.domain.XMLSchema;
import org.cagrid.gme.domain.XMLSchemaDocument;
import org.jdom.Document;
import org.jdom.Element;


public class FilesystemLoader extends FilesystemProcessor {

    private final List<File> schemaFiles;


    /**
     * Constructs the loader with a list of files which should indicate all the
     * schema "root documents" which should be used. NOTE: this DOES NOT verify
     * the provided files are legal XML Schemas, or are complete with respect to
     * imports.
     * 
     * @param schemaFiles
     */

    public FilesystemLoader(List<File> schemaFiles) {
        this.schemaFiles = schemaFiles;
    }


    /**
     * Reads all the files and creates XMLSchemas instances for them, loading
     * includes/redefines from the filesystem and adding them as additional
     * documents to the schemas.
     * 
     * @return a List of XMLSchema in the same order as the provided List of
     *         Files
     * @throws IOException
     *             if a schema was not able to be parsed.
     * @throws FileNotFoundException
     *             if an included/redefined schema document didn't have a
     *             schemaLocation which could be interpreted as hint to a file
     *             on the filesystem
     */
    public List<XMLSchema> loadSchemas() throws FileNotFoundException, IOException {
        List<XMLSchema> schemas = new ArrayList<XMLSchema>(this.schemaFiles.size());

        // for each "schema" file provided
        for (File f : this.schemaFiles) {
            // load the contents to jdom and extract the namespace
            Document schemaDoc = this.createDocumentFromFile(f);
            URI targetNamespace;
            try {
                targetNamespace = this.getTargetNamespace(schemaDoc);
            } catch (URISyntaxException e) {
                throw new SchemaParsingException("Problem determining the targetNamespace of the specified document ("
                    + f.getPath() + "); the namespace did not appear valid.");
            }

            // create a list of documents that comprise the namespace (item(0)
            // is the root document (the one provided to us))
            List<File> docs = new ArrayList<File>();
            docs.add(f);

            // this need to recursively process (A includes B includes C)
            processAdditionalDocuments(f.getParentFile(), schemaDoc, docs);

            XMLSchema createdSchema = XSDUtil.createSchema(targetNamespace, docs);
            schemas.add(createdSchema);
        }

        return schemas;

    }


    // recursively look for new files through includes and redefines
    private void processAdditionalDocuments(File directory, Document schemaDoc, List<File> docs)
        throws SchemaParsingException {
        List<Element> includeElements = this.getIncludeElements(schemaDoc);
        for (Element includeElement : includeElements) {
            String loc = includeElement.getAttributeValue(FilesystemProcessor.XSD_SCHEMALOCATION_ATTRIBUTE_NAME);
            File includedFile = new File(loc);
            if (!includedFile.isAbsolute()) {
                includedFile = new File(directory, loc);
            }
            if (!docs.contains(includedFile)) {
                docs.add(includedFile);
                processAdditionalDocuments(includedFile.getParentFile(), this.createDocumentFromFile(includedFile),
                    docs);
            }

        }

        // add any redefined documents
        List<Element> redefineElements = this.getRedefineElements(schemaDoc);
        for (Element redefineElement : redefineElements) {
            String loc = redefineElement.getAttributeValue(FilesystemProcessor.XSD_SCHEMALOCATION_ATTRIBUTE_NAME);
            File redefinedFile = new File(loc);
            if (!redefinedFile.isAbsolute()) {
                redefinedFile = new File(directory, loc);
            }
            if (!docs.contains(redefinedFile)) {
                docs.add(redefinedFile);
                processAdditionalDocuments(redefinedFile.getParentFile(), this.createDocumentFromFile(redefinedFile),
                    docs);
            }

        }
    }


    public static void main(String[] args) throws FileNotFoundException, IOException {
        File dir = new File(args[0]);
        File[] listFiles = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith("Includer_cycle.xsd");
            }
        });
        List<File> files = new ArrayList<File>(listFiles.length);
        files = Arrays.asList(listFiles);
        FilesystemLoader loader = new FilesystemLoader(files);
        List<XMLSchema> loadedSchemas = loader.loadSchemas();
        System.out.println("Found (" + loadedSchemas.size() + ") schemas.");
        int i = 0;
        for (XMLSchema s : loadedSchemas) {
            System.out.println("File (" + files.get(i++) + ") created XMLSchema (" + s.getTargetNamespace() + ").");
            if (s.getAdditionalSchemaDocuments().size() > 0) {
                System.out.println("\tAdditional Documents:");
                for (XMLSchemaDocument doc : s.getAdditionalSchemaDocuments()) {
                    System.out.println("\t\t - " + doc.getSystemID());
                }
            }

        }
    }
}
