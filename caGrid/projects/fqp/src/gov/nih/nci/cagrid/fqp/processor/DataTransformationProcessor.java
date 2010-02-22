package gov.nih.nci.cagrid.fqp.processor;

import java.math.BigDecimal;

import org.cagrid.data.dcql.DataTransformation;
import org.cagrid.data.dcql.TransformationOperator;

public class DataTransformationProcessor {

    private DataTransformation transformation = null;
    
    public DataTransformationProcessor(DataTransformation transformation) {
        this.transformation = transformation;
    }
    
    
    public String apply(String value) throws TransformationHandlingException {
        String transformed = null;
        if (transformation != null) {
            if (TransformationOperator.ABS.equals(getOperator())) {
                transformed = handleAbs(value);
            } else if (TransformationOperator.ADD.equals(getOperator())) {
                transformed = handleAdd(value);
            } else if (TransformationOperator.APPEND.equals(getOperator())) {
                transformed = handleAppend(value);
            } else if (TransformationOperator.DIVIDE.equals(getOperator())) {
                transformed = handleDivide(value);
            } else if (TransformationOperator.LOWER.equals(getOperator())) {
                transformed = value != null ? value.toLowerCase() : null;
            } else if (TransformationOperator.MULTIPLY.equals(getOperator())) {
                transformed = handleMultiply(value);
            } else if (TransformationOperator.PREPEND.equals(getOperator())) {
                transformed = handlePrepend(value);
            } else if (TransformationOperator.STRLENGTH.equals(getOperator())) {
                transformed = String.valueOf(value != null ? value.length() : 0);
            } else if (TransformationOperator.SUBTRACT.equals(getOperator())) {
                transformed = handleSubtract(value);
            } else if (TransformationOperator.UPPER.equals(getOperator())) {
                transformed = value != null ? value.toUpperCase() : null;
            } else {
                throw new TransformationHandlingException("Unknown transformation operator " + getOperator().getValue());
            }
        } else {
            transformed = value;
        }
        return transformed;
    }
    
    
    private TransformationOperator getOperator() {
        return transformation.getOperation().getOperator();
    }
    
    
    private String getDelta() {
        return transformation.getOperation().getValue();
    }
    
    
    // -------------------------
    // operation implementations
    // -------------------------
    
    
    private String handleAbs(String val) {
        if (val != null && isNumeric(val) && val.startsWith("-") && val.length() > 1) {
            return val.substring(1);
        }
        return val;
    }
    
    
    private String handleAdd(String val) throws TransformationHandlingException { 
        if (!isNumeric(val)) {
            throw new TransformationHandlingException(
                "Cannot perform operation " + TransformationOperator.ADD.getValue() + 
                ": " + val + " is not numeric");
        }
        if (getDelta() == null || getDelta().length() == 0 || !isNumeric(getDelta())) {
            throw new TransformationHandlingException(
                "Cannot perform operation " + TransformationOperator.ADD.getValue() +
                ": Transformation vector " + getDelta() + " is not numeric");
        }
        BigDecimal decVal = new BigDecimal(val);
        BigDecimal delta = new BigDecimal(getDelta());
        BigDecimal result = decVal.add(delta);
        return result.toString();
    }
    
    
    private String handleAppend(String val) {
        if (getDelta() != null) {
            return (val != null ? val : "") + getDelta();
        }
        return val;
    }
    
    
    private String handleDivide(String val) throws TransformationHandlingException {
        if (!isNumeric(val)) {
            throw new TransformationHandlingException("Cannot perform operation " + TransformationOperator.DIVIDE.getValue() + 
                ": " + val + " is not numeric");
        }
        if (getDelta() == null || getDelta().length() == 0 || !isNumeric(getDelta())) {
            throw new TransformationHandlingException("Cannot perform operation " + TransformationOperator.DIVIDE.getValue() + 
                ": " + getDelta() + " is not numeric");
        }
        BigDecimal decVal = new BigDecimal(val);
        BigDecimal delta = new BigDecimal(getDelta());
        BigDecimal result = decVal.divide(delta);
        return result.toString();
    }
    
    
    private String handleMultiply(String val) throws TransformationHandlingException {
        if (!isNumeric(val)) {
            throw new TransformationHandlingException("Cannot perform operation " + TransformationOperator.MULTIPLY.getValue() + 
                ": " + val + " is not numeric");
        }
        if (getDelta() == null || getDelta().length() == 0 || !isNumeric(getDelta())) {
            throw new TransformationHandlingException("Cannot perform operation " + TransformationOperator.MULTIPLY.getValue() + 
                ": " + getDelta() + " is not numeric");
        }
        BigDecimal decVal = new BigDecimal(val);
        BigDecimal delta = new BigDecimal(getDelta());
        BigDecimal result = decVal.multiply(delta);
        return result.toString();
    }
    
    
    private String handlePrepend(String val) {
        if (getDelta() != null) {
            return getDelta() + (val != null ? val : "");
        }
        return val;
    }
    
    
    private String handleSubtract(String val) throws TransformationHandlingException { 
        if (!isNumeric(val)) {
            throw new TransformationHandlingException(
                "Cannot perform operation " + TransformationOperator.SUBTRACT.getValue() + 
                ": " + val + " is not numeric");
        }
        if (getDelta() == null || getDelta().length() == 0 || !isNumeric(getDelta())) {
            throw new TransformationHandlingException(
                "Cannot perform operation " + TransformationOperator.SUBTRACT.getValue() +
                ": Transformation vector " + getDelta() + " is not numeric");
        }
        BigDecimal decVal = new BigDecimal(val);
        BigDecimal delta = new BigDecimal(getDelta());
        BigDecimal result = decVal.subtract(delta);
        return result.toString();
    }
    
    
    // -------
    // helpers
    // -------
    
    
    private boolean isNumeric(String val) {
        boolean is = false;
        if (!(val == null || val.length() == 0)) {
            try {
                new BigDecimal(val);
                is = true;
            } catch (NumberFormatException ex) {
                // expected for non-numerics
            }
        }
        return is;
    }
}
