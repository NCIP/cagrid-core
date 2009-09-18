package gov.nih.nci.cagrid.introduce.portal.modification.types;

import gov.nih.nci.cagrid.introduce.common.CommonTools;

import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.jgoodies.validation.util.ValidationUtils;


public class SchemaElementTypeValidator {

    private static final String CLASSNAME = "Classname"; // @jve:decl-index=0:

    private static final String SERIALIZER = "Serializer"; // @jve:decl-index=0:

    private static final String DESERIALIZER = "Deserializer"; //@jve:decl-index
                                                               // =0:


    public static ValidationResult validateSchemaElementType(String classname, String serializer, String deserializer) {

        ValidationResult result = new ValidationResult();

        if (ValidationUtils.isNotBlank(classname)) {
            if (ValidationUtils.isBlank(serializer)) {
                result.add(new SimpleValidationMessage(SERIALIZER + " must not be blank.", Severity.ERROR, SERIALIZER));
            }
            if (ValidationUtils.isBlank(deserializer)) {
                result.add(new SimpleValidationMessage(DESERIALIZER + " must not be blank.", Severity.ERROR,
                    DESERIALIZER));
            }
            if (!CommonTools.isValidClassName(classname)) {
                result.add(new SimpleValidationMessage(CLASSNAME + " might not be a valid class name.",
                    Severity.WARNING, CLASSNAME));
            }
        }

        if (ValidationUtils.isNotBlank(serializer)) {
            if (ValidationUtils.isBlank(classname)) {
                result.add(new SimpleValidationMessage(CLASSNAME + " must not be blank.", Severity.ERROR, CLASSNAME));
            }
            if (ValidationUtils.isBlank(deserializer)) {
                result.add(new SimpleValidationMessage(DESERIALIZER + " must not be blank.", Severity.ERROR,
                    DESERIALIZER));
            }
            if (!CommonTools.isValidPackageAndClassName(serializer)) {
                result.add(new SimpleValidationMessage(
                    SERIALIZER + " might not be a valid fully qualified class name.", Severity.WARNING, SERIALIZER));
            }
        }

        if (ValidationUtils.isNotBlank(deserializer)) {
            if (ValidationUtils.isBlank(serializer)) {
                result.add(new SimpleValidationMessage(SERIALIZER + " must not be blank.", Severity.ERROR, SERIALIZER));
            }
            if (ValidationUtils.isBlank(classname)) {
                result.add(new SimpleValidationMessage(CLASSNAME + " must not be blank.", Severity.ERROR, CLASSNAME));
            }
            if (!CommonTools.isValidPackageAndClassName(deserializer)) {
                result.add(new SimpleValidationMessage(DESERIALIZER
                    + " might not be a valid fully qualified class name.", Severity.WARNING, DESERIALIZER));
            }
        }

        return result;
    }

}
