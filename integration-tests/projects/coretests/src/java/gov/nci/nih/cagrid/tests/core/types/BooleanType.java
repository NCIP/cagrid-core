package gov.nci.nih.cagrid.tests.core.types;

/**
 * Type for xs:boolean that allows true, false, 1, and 0
 * 
 * @author MCCON012
 */
public class BooleanType {
    /**
     * Actual boolean value
     */
    private boolean value;


    /**
     * Construct a boolean from the string
     * 
     * @param str
     *            true, false, 1, or 0
     */
    public BooleanType(String str) {
        super();

        this.value = str.equals("true") || str.equals("1");
    }


    /**
     * Test the equality of two BooleanType boolean values
     */
    @Override
    public boolean equals(Object obj) {
        return this.value == ((BooleanType) obj).value;
    }
}
