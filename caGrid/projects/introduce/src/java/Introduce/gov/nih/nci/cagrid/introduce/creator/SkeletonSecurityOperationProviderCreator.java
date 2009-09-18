package gov.nih.nci.cagrid.introduce.creator;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeImportInformation;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeOutput;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeProviderInformation;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.SpecificServiceInformation;
import gov.nih.nci.cagrid.introduce.beans.security.MethodAuthorization;
import gov.nih.nci.cagrid.introduce.beans.security.MethodSecurity;
import gov.nih.nci.cagrid.introduce.beans.security.NoAuthorization;
import gov.nih.nci.cagrid.introduce.beans.security.SecuritySetting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import javax.xml.namespace.QName;


/**
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 */
public class SkeletonSecurityOperationProviderCreator {

    private static final String SERVICE_SECURITY_WSDL = "ServiceSecurity.wsdl";
    private static final String SERVICE_NS_EXCLUDE = "-x http\\://security.introduce.cagrid.nci.nih.gov/ServiceSecurity";
    private static final String SERVICE_SECURITY_XSD = "security.xsd";
    private static final String SECURITY_SERVICE_NS = "http://security.introduce.cagrid.nci.nih.gov/ServiceSecurity";
    private static final String SECURITY_SERVICE_PACKAGE = "gov.nih.nci.cagrid.introduce.security.stubs";
    private static final String SECURITY_NS = "gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata.security";
    private static final String PATH_TO_WSDL = "ext" + File.separator + "dependencies" + File.separator + "wsdl";
    private static final String RELATIVE_PATH_TO_SCHEMA = "xsd" + File.separator + "cagrid" + File.separator + "types"
        + File.separator + "security";
    private static final String PATH_TO_SCHEMA = "ext" + File.separator + "dependencies" + File.separator
        + RELATIVE_PATH_TO_SCHEMA;


    public SkeletonSecurityOperationProviderCreator() {
    }


    public void createSkeleton(SpecificServiceInformation info) throws Exception {
        boolean needToAdd = true;
        if (info.getService().getMethods() != null && info.getService().getMethods().getMethod() != null) {
            MethodType[] methods = info.getService().getMethods().getMethod();
            for (int i = 0; i < methods.length; i++) {
                MethodType method = methods[i];
                if (method.getName().equals("getServiceSecurityMetadata")) {
                    needToAdd = false;
                }
            }
        }

        if (needToAdd) {
            // add in the method
            MethodType method = new MethodType();
            method.setName("getServiceSecurityMetadata");

            MethodTypeOutput output = new MethodTypeOutput();
            output.setQName(new QName(SECURITY_NS, "ServiceSecurityMetadata"));
            output.setIsArray(false);
            output.setIsClientHandle(new Boolean(false));
            method.setOutput(output);

            MethodTypeImportInformation ii = new MethodTypeImportInformation();
            ii.setNamespace(SECURITY_SERVICE_NS);
            ii.setPackageName(SECURITY_SERVICE_PACKAGE);
            ii.setPortTypeName("ServiceSecurityPortType");
            ii.setWsdlFile(SERVICE_SECURITY_WSDL);
            // ii.setInputMessage(new QName(SECURITY_SERVICE_NS,
            // "GetServiceSecurityMetadataRequest"));
            // ii.setOutputMessage(new QName(SECURITY_SERVICE_NS,
            // "GetServiceSecurityMetadataResponse"));
            method.setIsImported(true);
            method.setImportInformation(ii);

            MethodTypeProviderInformation pi = new MethodTypeProviderInformation();
            pi.setProviderClass("gov.nih.nci.cagrid.introduce.security.service.globus.ServiceSecurityProviderImpl");
            method.setIsProvided(true);
            method.setProviderInformation(pi);
            MethodSecurity ms = new MethodSecurity();
            ms.setSecuritySetting(SecuritySetting.None);
            MethodAuthorization ma = new MethodAuthorization();
            ms.setMethodAuthorization(ma);
            ma.setNoAuthorization(new NoAuthorization());
            method.setMethodSecurity(ms);

            CommonTools.addMethod(info.getService(), method);

            if (CommonTools.getNamespaceType(info.getNamespaces(), SECURITY_NS) == null) {

                String pathToServSchema = info.getBaseDirectory().getAbsolutePath()
                    + File.separator
                    + "schema"
                    + File.separator
                    + info.getIntroduceServiceProperties().getProperty(
                        IntroduceConstants.INTRODUCE_SKELETON_SERVICE_NAME) + File.separator;

                // copy over the wsdl file and the required schema
                Utils.copyFile(new File(PATH_TO_WSDL + File.separator + SERVICE_SECURITY_WSDL), new File(
                    pathToServSchema + SERVICE_SECURITY_WSDL));
                File servSecurityXSDDest = new File(pathToServSchema + RELATIVE_PATH_TO_SCHEMA + File.separator + SERVICE_SECURITY_XSD);
                Utils.copyFile(new File(PATH_TO_SCHEMA + File.separator + SERVICE_SECURITY_XSD), servSecurityXSDDest);

                // add in the namespace type
                NamespaceType nsType = CommonTools.createNamespaceType(servSecurityXSDDest.getAbsolutePath(), new File(
                    pathToServSchema));
                nsType.setGenerateStubs(new Boolean(false));
                nsType.setPackageName("gov.nih.nci.cagrid.metadata.security");
                CommonTools.addNamespace(info.getServiceDescriptor(), nsType);

                // set the namespace of hte sercure service to be in the ns
                // excludes do that
                // the message beans are not generated again
                Properties props = new Properties();
                props.load(new FileInputStream(new File(info.getBaseDirectory().getAbsolutePath() + File.separator
                    + IntroduceConstants.INTRODUCE_PROPERTIES_FILE)));
                if (props.getProperty(IntroduceConstants.INTRODUCE_NS_EXCLUDES) != null) {
                    props.setProperty(IntroduceConstants.INTRODUCE_NS_EXCLUDES, props
                        .getProperty(IntroduceConstants.INTRODUCE_NS_EXCLUDES)
                        + " " + SERVICE_NS_EXCLUDE);
                } else {
                    props.setProperty(IntroduceConstants.INTRODUCE_NS_EXCLUDES, SERVICE_NS_EXCLUDE);
                }
                props.store(new FileOutputStream(new File(info.getBaseDirectory().getAbsolutePath() + File.separator
                    + IntroduceConstants.INTRODUCE_PROPERTIES_FILE)), "Introduce service properties");
            }
        }
    }

}