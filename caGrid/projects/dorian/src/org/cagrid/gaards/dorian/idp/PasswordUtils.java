package org.cagrid.gaards.dorian.idp;

import gov.nih.nci.cagrid.common.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PasswordUtils {

    public static final String LOWER_CASE_LETTER_REGEX = "[a-z]+";
    public static final String CAPITAL_LETTER_REGEX = "[A-Z]+";
    public static final String NUMBER_REGEX = "[0-9]+";
    public static final String[] SYMBOLS = new String[]{"~", "!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "_",
            "-", "+", "=", "{", "}", "[", "]", "|", ":", ";", "<", ">", ",", ".", "?"};


    public static boolean hasLowerCaseLetter(String password) {
        return matches(LOWER_CASE_LETTER_REGEX, password);
    }


    public static boolean hasSymbol(String password) {
        for (int i = 0; i < SYMBOLS.length; i++) {
            if (password.indexOf(SYMBOLS[i]) != -1) {
                return true;
            }
        }

        return false;
    }


    public static boolean hasCapitalLetter(String password) {
        return matches(CAPITAL_LETTER_REGEX, password);
    }


    public static boolean hasNumber(String password) {
        return matches(NUMBER_REGEX, password);
    }


    public static boolean matches(String regex, String password) {
        if (Utils.clean(password) != null) {
            Pattern pattern = Pattern.compile(regex);
            Matcher m = pattern.matcher(password);
            if (m.find()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

}
