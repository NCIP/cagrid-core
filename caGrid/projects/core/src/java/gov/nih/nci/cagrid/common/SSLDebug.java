package gov.nih.nci.cagrid.common;


public class SSLDebug {
    
    public static String debugSSL(String message, byte[] cert) {
        StringBuffer hex = new StringBuffer();
        hex.append(message).append("\n");
        String rawHex = Utils.bytesToHex(cert);
        for (int i = 0; i < rawHex.length(); i++) {
            if (i != 0 && (i % 12) == 0) {
                hex.append("\n");
            }
            hex.append(rawHex.charAt(i));
            if ((i % 2) == 0) {
                hex.append(" ");
            }
        }
        return hex.toString();
    }
}
