package org.cagrid.gme.test.system;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.cagrid.gme.common.XSDUtil;
import org.cagrid.gme.domain.XMLSchema;
import org.cagrid.gme.domain.XMLSchemaImportInformation;
import org.cagrid.gme.domain.XMLSchemaNamespace;
import org.cagrid.gme.serialization.SerializationUtils;


public class TestCaseInfo {

    protected File schemaDirectory;
    protected String description;

    protected Map<String, XMLSchema> schemaFilenameToSchemaMap;
    protected Map<String, XMLSchemaImportInformation> schemaFilenameToSchemaImportInformationMap;


    public TestCaseInfo(File directory) throws Exception {
        this(directory, directory.getName());
    }


    public TestCaseInfo(File directory, String description) throws Exception {
        if (!directory.canRead() || !directory.isDirectory()) {
            throw new IllegalArgumentException("The specified File must be a readable directory");
        }
        this.schemaDirectory = directory;
        this.description = description;

        initialize();

    }


    public Collection<XMLSchema> getSchemas() throws IOException {
        return this.schemaFilenameToSchemaMap.values();
    }


    public Collection<XMLSchemaNamespace> getNamespaces() throws IOException {
        Collection<XMLSchemaNamespace> result = new ArrayList<XMLSchemaNamespace>();
        Collection<XMLSchema> schemas = getSchemas();
        for (XMLSchema s : schemas) {
            result.add(new XMLSchemaNamespace(s.getTargetNamespace()));
        }
        return result;
    }


    public Collection<XMLSchemaImportInformation> getImportInformation() throws IOException {
        return this.schemaFilenameToSchemaImportInformationMap.values();
    }


    public XMLSchema getXMLSchemaForFilename(String filename) {
        return this.schemaFilenameToSchemaMap.get(filename);
    }


    public XMLSchemaImportInformation getXMLSchemaImportInformationForFilename(String filename) {
        return this.schemaFilenameToSchemaImportInformationMap.get(filename);

    }


    private void initialize() throws Exception {
        File[] schemaFiles = this.schemaDirectory.listFiles(new FilenameFilter() {

            public boolean accept(File dir, String name) {
                return name.endsWith(".xsd");
            }
        });

        File iiDirectory = new File(this.schemaDirectory, "ImportInformation");
        File[] iiFiles = iiDirectory.listFiles(new FilenameFilter() {

            public boolean accept(File dir, String name) {
                return name.endsWith(".xml");
            }
        });

        if (iiFiles.length != schemaFiles.length) {
            throw new RuntimeException(
                "Error processing schemas!  The number of import information files does not match the number of schemas files.");
        }

        this.schemaFilenameToSchemaMap = new HashMap<String, XMLSchema>();
        this.schemaFilenameToSchemaImportInformationMap = new HashMap<String, XMLSchemaImportInformation>();

        Map<String, File> iiFileMap = new HashMap<String, File>(iiFiles.length);
        for (File iiFile : iiFiles) {
            iiFileMap.put(iiFile.getName(), iiFile);
        }

        // for each schema file, read the import information, and build a schema
        for (File file : schemaFiles) {
            String fname = file.getName();
            fname = fname.replace(".xsd", ".xml");
            File iiFile = iiFileMap.get(fname);
            if (iiFile == null) {
                throw new RuntimeException("Error processing schemas!  Could not find import information for file ("
                    + file.getCanonicalPath() + ").  Looked for " + fname);
            }
            XMLSchemaImportInformation ii = SerializationUtils.deserializeXMLSchemaImportInformation(new FileReader(
                iiFile));
            this.schemaFilenameToSchemaImportInformationMap.put(file.getName(), ii);

            XMLSchemaNamespace targetNamespace = ii.getTargetNamespace();
            XMLSchema createdSchema = XSDUtil.createSchema(targetNamespace.getURI(), file);
            this.schemaFilenameToSchemaMap.put(file.getName(), createdSchema);
        }
    }


    public File getSchemaDirectory() {
        return this.schemaDirectory;
    }


    public String getDescription() {
        return this.description;
    }

}
