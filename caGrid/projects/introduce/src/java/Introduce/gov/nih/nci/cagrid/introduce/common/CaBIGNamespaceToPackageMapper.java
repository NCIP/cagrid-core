package gov.nih.nci.cagrid.introduce.common;

import org.apache.axis.wsdl.toJava.Utils;


public class CaBIGNamespaceToPackageMapper implements NamespaceToPackageMapper {

    private static final String DOT_SEPARATED_WORDS_REGEX = "([\\w-])+(\\.([\\w-])+)*";


    public String getPackageName(String namespace) throws UnsupportedNamespaceFormatException {
        int i = namespace.lastIndexOf("/");
        if (i > 0 && i < namespace.length() - 1) {
            String pack = namespace.substring(i + 1);
            if (pack.matches(DOT_SEPARATED_WORDS_REGEX)) {
                return pack.toLowerCase();
            }
        }

        // if we get here, this didn't work
        String axisPackage = Utils.makePackageName(namespace);
        if (axisPackage == null) {
            return "";
        }

        return axisPackage.toLowerCase();

    }


    public static void main(String[] args) {
        CaBIGNamespaceToPackageMapper mapper = new CaBIGNamespaceToPackageMapper();
        try {
            System.out.println(mapper.getPackageName("gme://caCORE.cabig/3.0/gov.nih.nci.cadsr.domain"));
            System.out.println(mapper.getPackageName("urn:hl7-org:v3"));
        } catch (UnsupportedNamespaceFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
