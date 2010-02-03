package gov.nih.nci.cagrid.data;

public interface ValidatorConstants {
    
    // validation constants
    public static final String CQL_VALIDATOR_CLASS = ServiceParametersConstants.DATA_SERVICE_PARAMS_PREFIX + "_cqlValidatorClass";
    public static final String DOMAIN_MODEL_VALIDATOR_CLASS = ServiceParametersConstants.DATA_SERVICE_PARAMS_PREFIX + "_domainModelValidatorClass";
    public static final String CQL2_VALIDATOR_CLASSES = ServiceParametersConstants.DATA_SERVICE_PARAMS_PREFIX + "_cql2ValidatorClasses";
    public static final String VALIDATE_CQL_FLAG = ServiceParametersConstants.DATA_SERVICE_PARAMS_PREFIX + "_validateCqlFlag";
    public static final String VALIDATE_DOMAIN_MODEL_FLAG = ServiceParametersConstants.DATA_SERVICE_PARAMS_PREFIX + "_validateDomainModelFlag";
    public static final String DEFAULT_VALIDATE_CQL_FLAG = Boolean.TRUE.toString();
    public static final String DEFAULT_VALIDATE_DOMAIN_MODEL_FLAG = Boolean.TRUE.toString();
}
