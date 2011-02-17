package gov.nih.nci.cagrid.data;

public interface InstanceCountConstants {

    // constants related to the instance count updater
    public static final String COUNT_UPDATE_FREQUENCY = ServiceParametersConstants.DATA_SERVICE_PARAMS_PREFIX + "_countUpdateFrequency";
    public static final String COUNT_UPDATE_FREQUENCY_DEFAULT = "600"; // 600 seconds = 10 minutes
}
