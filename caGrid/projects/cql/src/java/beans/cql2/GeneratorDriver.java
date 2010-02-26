import org.exolab.castor.builder.SourceGenerator;


public class GeneratorDriver {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            SourceGenerator gen = new SourceGenerator();
            gen.setGenerateImportedSchemas(true);
            gen.setCreateMarshalMethods(false);
            gen.setDescriptorCreation(false);
            gen.setEqualsMethod(true);
            gen.setGenerateMappingFile(true);
            gen.setDestDir("./temp/castor/testing");
            gen.setNamespacePackageMapping("http://CQL.caBIG/2/org.cagrid.cql2", "org.cagrid.cql2");
            gen.setVerbose(true);
            gen.setUseEnumeratedTypeInterface(true);
            System.out.println("Running");
            gen.generateSource("./schema/cql2.0/CQLQueryComponents.xsd", "org.cagrid.cql2");
            System.out.println("Done");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
