/**
 * 
 */
package gov.nih.nci.cagrid.common.security;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.map.MultiValueMap;

/**
 * This class wraps a String that is an RFC 2253 compliant DN and allows it to
 * be compared with other RFC 2253 compliant DNs without regard to the order in
 * which the RDNs appear within the DN.
 * 
 * @author Mark Grand
 */
public class OrderInsensitiveDN {
    private static final SetFactory mySetFactory = new SetFactory();

    private String distinguishedName;
    private SetMap rdnMap;

    /**
     * Constructor
     * 
     * @param distinguishedName
     *            the DN string that this object will wrap.
     * @throws InvalidNameException
     *             if distinguishedName is not an RFC2253-compliant DN.
     */
    public OrderInsensitiveDN(String distinguishedName)
            throws InvalidNameException {
        this.distinguishedName = distinguishedName;
        List<Rdn> rdnList = (new LdapName(distinguishedName)).getRdns();
        rdnMap = new SetMap(rdnList.size());
        for (Rdn rdn : rdnList) {
            rdnMap.put(rdn.getType(), rdn.getValue());
        }
    }

    /**
     * Return the distinguishedName string that this object wraps.
     */
    public String getDistinguishedName() {
        return distinguishedName;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof OrderInsensitiveDN))
            return false;
        OrderInsensitiveDN other = (OrderInsensitiveDN) obj;
        return distinguishedName.equals(other.distinguishedName);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return distinguishedName.hashCode();
    }

	/**
	 * Return true if the RDNs in the given OrderInsensitiveDN are equal
	 * to the RNDs in this object, ignoring their order.
	 */
	public boolean equalsIgnoringOrder(OrderInsensitiveDN that) {
		return this.rdnMap.equals(that.rdnMap);
	}

    private static class SetFactory implements Factory {
        @Override
        public Object create() {
            return new HashSet<Object>();
        }
    }

    /**
     * A hash map whose keys can have multiple unique values associated with
     * them. Attempts to add a duplicate value to an existing key are ignored.
     * 
     * @author Mark Grand
     */
    private class SetMap extends MultiValueMap {

        /**
         * Constructor
         * 
         * @param capacity
         *            The number of values that will be stored in this map.
         */
        SetMap(int capacity) {
            super(new HashMap<Object, Object>(capacity * 2), mySetFactory);
        }
    }
}
