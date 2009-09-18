package gov.nih.nci.cagrid.data.ui.browser;

import java.util.HashSet;
import java.util.Set;

/** 
 *  SetUtil
 *  Utilities for set manipulation
 * 
 * @author David Ervin
 * 
 * @created Jul 16, 2007 11:02:33 AM
 * @version $Id: SetUtil.java,v 1.2 2007-12-18 19:12:03 dervin Exp $ 
 */
public class SetUtil {

    public static Set difference(Set<?> a, Set<?> b) {
        Set<Object> diff = new HashSet<Object>();
        for (Object o : a) {
            if (!b.contains(o)) {
                diff.add(o);
            }
        }
        for (Object o : b) {
            if (!a.contains(o)) {
                diff.add(o);
            }
        }
        return diff;
    }
    
    public static Set intersect(Set<?> a, Set<?> b) {
        Set<Object> intersect = new HashSet<Object>();
        Set<Object> union = new HashSet<Object>();
        union.addAll(a);
        union.addAll(b);
        for (Object o : union) {
            if (a.contains(o) && b.contains(o)) {
                intersect.add(o);
            }
        }
        return intersect;
    }
}
