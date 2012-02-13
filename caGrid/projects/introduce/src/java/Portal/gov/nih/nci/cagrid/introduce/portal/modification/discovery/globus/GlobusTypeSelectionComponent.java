package gov.nih.nci.cagrid.introduce.portal.modification.discovery.globus;

import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.cagrid.common.portal.MultiEventProgressBar;
import gov.nih.nci.cagrid.introduce.beans.configuration.NamespaceReplacementPolicy;
import gov.nih.nci.cagrid.introduce.beans.extension.DiscoveryExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespacesType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.portal.modification.discovery.NamespaceTypeDiscoveryComponent;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;


/**
 * GMETypeExtractionPanel
 * 
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @created Jul 7, 2005
 * @version $Id: mobiusEclipseCodeTemplates.xml,v 1.2 2005/04/19 14:58:02 oster
 *          Exp $
 */
public class GlobusTypeSelectionComponent extends NamespaceTypeDiscoveryComponent {
    public static String TYPE = "GLOBUS";

    private GlobusConfigurationPanel globusPanel = null;


    public GlobusTypeSelectionComponent(DiscoveryExtensionDescriptionType descriptor, NamespacesType types) {
        super(descriptor, types);
        initialize();
        this.getGlobusPanel().discoverFromGlobus();
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
        gridBagConstraints4.insets = new java.awt.Insets(0, 0, 0, 0);
        gridBagConstraints4.gridy = 0;
        gridBagConstraints4.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints4.gridwidth = 1;
        gridBagConstraints4.weightx = 1.0D;
        gridBagConstraints4.weighty = 1.0D;
        gridBagConstraints4.gridx = 0;
        this.setLayout(new GridBagLayout());
        this.add(getGlobusPanel(), gridBagConstraints4);
    }


    /**
     * This method initializes gmePanel
     * 
     * @return javax.swing.JPanel
     */
    private GlobusConfigurationPanel getGlobusPanel() {
        if (this.globusPanel == null) {
            this.globusPanel = new GlobusConfigurationPanel();
        }
        return this.globusPanel;
    }


    @Override
    public NamespaceType[] createNamespaceType(File schemaDestinationDir, NamespaceReplacementPolicy replacementPolicy,
        MultiEventProgressBar progress) {
        NamespaceType input = new NamespaceType();
        try {
            String currentNamespace = getGlobusPanel().currentNamespace;
            File currentSchemaFile = getGlobusPanel().currentSchemaFile;

            // set the namespace
            if (currentNamespace != null) {
                input.setNamespace(currentNamespace);
            } else {
                return null;
            }

            if (currentSchemaFile != null) {

                int index = currentSchemaFile.getAbsolutePath().indexOf(
                    CommonTools.getGlobusLocation() + File.separator + "share" + File.separator + "schema"
                        + File.separator)
                    + new String(CommonTools.getGlobusLocation() + "share" + File.separator + "schema" + File.separator)
                        .length();
                String location = ".."
                    + File.separator
                    + currentSchemaFile.getAbsolutePath().substring(index + 1,
                        currentSchemaFile.getAbsolutePath().length());
                location = location.replace('\\', '/');
                input.setLocation(location);
                gov.nih.nci.cagrid.introduce.portal.extension.tools.ExtensionTools.setSchemaElements(input, XMLUtilities
                    .fileNameToDocument(currentSchemaFile.getAbsolutePath()));
                return new NamespaceType[]{input};
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static void main(String[] args) {
    }
}
