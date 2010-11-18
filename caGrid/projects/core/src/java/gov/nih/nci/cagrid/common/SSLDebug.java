package gov.nih.nci.cagrid.common;


public class SSLDebug {
    
    public static String debugSSL(String message, byte[] cert) {
        StringBuffer hex = new StringBuffer();
        hex.append(message).append("\n");
        String rawHex = Utils.bytesToHex(cert);
        for (int i = 0; i < rawHex.length(); i += 2) {
            if (i != 0 && (i % 12) == 0) {
                hex.append("\n");
            }
            hex.append(rawHex.charAt(i)).append(rawHex.charAt(i + 1)).append(" ");
        }
        return hex.toString();
    }
    
    
    public static void main(String[] args) {
        byte[] wut = new byte[] {1, 2, 3, 4, 5, 6, 7, 8, 9};
        System.out.println(debugSSL("Hi!", wut));
    }
}
