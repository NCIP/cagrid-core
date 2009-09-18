package org.cagrid.gme.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.cagrid.gme.domain.XMLSchema;
import org.cagrid.gme.domain.XMLSchemaDocument;


public class XSDUtil {

    private XSDUtil() {
    }


    /**
     * Convenience method when only a single file is needed
     * 
     * @param namespace
     *            the namespace to assign to the schema
     * @param schemaFiles
     *            the files to create the schema with
     * @return the constructed Schema
     * @throws FileNotFoundException
     *             if a file is not valid
     * @throws IOException
     *             if a file is not valid
     */
    public static XMLSchema createSchema(URI namespace, File schemaFile) throws FileNotFoundException, IOException {
        List<File> list = new ArrayList<File>();
        list.add(schemaFile);
        return createSchema(namespace, list);
    }


    /**
     * Constructs a Schema using the given namespace and populates it with
     * SchemaDocuments with the given files' contents. Note: this does not check
     * that the files actually represent valid XML Schemas, nor does it check
     * that the are all of the same specified namespace.
     * 
     * @param namespace
     *            the namespace to assign to the schema
     * @param schemaFiles
     *            the files to create the schema with
     * @return the constructed Schema
     * @throws FileNotFoundException
     *             if a file is not valid
     * @throws IOException
     *             if a file is not valid
     */
    public static XMLSchema createSchema(URI namespace, List<File> schemaFiles) throws FileNotFoundException,
        IOException {
        if (schemaFiles == null || schemaFiles.size() == 0) {
            throw new IllegalArgumentException("schemaFiles must be a valid array of files.");
        }

        XMLSchemaDocument root = createSchemaDocument(schemaFiles.get(0));

        Set<XMLSchemaDocument> docs = new HashSet<XMLSchemaDocument>(schemaFiles.size() - 1);
        for (int i = 1; i < schemaFiles.size(); i++) {
            docs.add(createSchemaDocument(schemaFiles.get(i)));
        }

        XMLSchema schema = new XMLSchema();
        schema.setTargetNamespace(namespace);
        schema.setRootDocument(root);
        schema.setAdditionalSchemaDocuments(docs);

        return schema;
    }


    /**
     * Constructs a SchemaDocument with the given file's contents, and uses the
     * filename (not the full path), as the systemID. Note: this does not check
     * that the file actually represents a valid XML Schema.
     * 
     * @param schemaFile
     *            the file to convert to a schemadocument
     * @return the constructed SchemaDocument
     * @throws FileNotFoundException
     *             if the file is not valid
     * @throws IOException
     *             if the file is not valid
     */
    public static XMLSchemaDocument createSchemaDocument(File schemaFile) throws FileNotFoundException, IOException {
        if (schemaFile == null || !schemaFile.canRead()) {
            throw new IllegalArgumentException("schemaFile [" + schemaFile + "] must be a valid, readable file.");
        }

        FileInputStream fileInputStream = new FileInputStream(schemaFile);
        String fileContents = IOUtils.toString(fileInputStream);
        fileInputStream.close();
        String systemID = schemaFile.getName();

        return new XMLSchemaDocument(fileContents, systemID);
    }


    /**
     * Loops through the SchemaDocuments in the Schema and returns null or the
     * first which has a matching systemID to the given systemID;
     * 
     * @throws IllegalArgumentException
     *             if Schema is null or contains no SchemaDocuments
     * @param schema
     *            the Schema to search
     * @param systemId
     *            the systemID to look for
     * @return the matching SchemaDocuemnt or null
     */
    public static XMLSchemaDocument getSchemaDocumentFromSchema(XMLSchema schema, String systemId) {
        if (schema == null) {
            throw new IllegalArgumentException("Schema must be non null.");
        }
        if (schema.getRootDocument().getSystemID().equals(systemId)) {
            return schema.getRootDocument();
        } else {
            for (XMLSchemaDocument sd : schema.getAdditionalSchemaDocuments()) {
                if (sd.getSystemID().equals(systemId)) {
                    return sd;
                }
            }
        }

        return null;
    }
}
