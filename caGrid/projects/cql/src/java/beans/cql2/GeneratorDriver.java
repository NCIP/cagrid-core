/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
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
