package org.cagrid.gaards.dorian.ca;

/**
 * KeySizeValidator
 * Validates that the specified key size is supported
 * 
 * @author David
 */
public class KeySizeValidator {
    
    public static final int[] VALID_KEY_SIZES = {
        512, 1024, 2048
    };

    public static boolean isKeySizeValid(int keySize) {
        boolean valid = false;
        for (int size : VALID_KEY_SIZES) {
            if (size == keySize) {
                valid = true;
                break;
            }
        }
        return valid;
    }

}
