package gov.nih.nci.cagrid.common;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * VersionNumber
 * 
 * Gives information about a String representation of a project version number.
 * 
 * This utility parses version numbers of the format
 * major.minor.revision.maintenance-suffix
 * 
 * For example, 1.4.7.0-rc1
 * 
 * @author ervin
 */
public class VersionNumber {
    
    public static final String DEFAULT_SEPARATOR = ".";
    public static final String DEFAULT_SUFFIX_SEPARATOR = "-";
    
    private String versionString = null;
    private ArrayList<String> versionParts = null;
    private String suffix = null;
    
    public VersionNumber(String versionString) {
        this(versionString, DEFAULT_SEPARATOR, DEFAULT_SUFFIX_SEPARATOR);
    }
    
    
    public VersionNumber(String versionString, String separator, String suffixSeparator) {
        this.versionString = versionString;
        this.versionParts = new ArrayList<String>();
        // split the version down to its parts
        StringTokenizer tok = new StringTokenizer(versionString, separator);
        while (tok.hasMoreTokens()) {
            versionParts.add(tok.nextToken());
        }
        // check the last token for a suffix
        String last = versionParts.get(versionParts.size() - 1);
        int index = last.indexOf(suffixSeparator);
        if (index != -1) {
            versionParts.set(versionParts.size() - 1, last.substring(0, index));
            suffix = last.substring(index + 1);
        }
    }
    
    
    public static VersionNumber getCaGridVersion() {
        String version = VersionNumber.class.getPackage().getImplementationVersion();
        return new VersionNumber(version, DEFAULT_SEPARATOR, DEFAULT_SUFFIX_SEPARATOR);
    }
    
    
    public String getOriginalVersionString() {
        return versionString;
    }
    
    
    public String getMajorVersion() {
        return getVersionAtLevel(0);
    }
    
    
    public String getMinorVersion() {
        return getVersionAtLevel(1);
    }
    
    
    public String getRevision() {
        return getVersionAtLevel(2);
    }
    
    
    public String getMaintenanceVersion() {
        return getVersionAtLevel(3);
    }
    
    
    public String getSuffix() {
        return this.suffix;
    }
    
    
    private String getVersionAtLevel(int level) {
        return level >= versionParts.size() ? null : versionParts.get(level);
    }
    
    
    public boolean equals(Object o) {
        boolean eq = false;
        if (o instanceof VersionNumber) {
            VersionNumber other = (VersionNumber) o;
            return getOriginalVersionString().equals(other.getOriginalVersionString());
        }
        return eq;
    }
    
    
    public int hashCode() {
        return getOriginalVersionString().hashCode();
    }
}
