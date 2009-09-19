package gov.nih.nci.cagrid.introduce.extensions.metadata.upgrade.system.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.util.Iterator;

import org.apache.commons.jxpath.JXPathContext;


/**
 * XPathValidatingStep TODO:DOCUMENT ME
 * 
 * @author oster
 * @created Apr 11, 2007 8:07:04 PM
 * @version $Id: multiscaleEclipseCodeTemplates.xml,v 1.1 2007/03/02 14:35:01
 *          dervin Exp $
 */
public abstract class XPathValidatingStep extends Step {

    protected Iterator createIterator(Object object, String xpath) {
        JXPathContext duceContext = JXPathContext.newContext(object);
        Iterator duceOperIter = duceContext.iterate(xpath);
        return duceOperIter;
    }


    protected void assertStringIteratorsEqual(Iterator iter1, Iterator iter2) {

        assertTrue("Nothing found in first iterator!", iter1.hasNext());

        // iterate orig and fail if new doesn't have the same entry
        while (iter1.hasNext()) {
            String val1 = Utils.clean((String) iter1.next());
            if (!iter2.hasNext()) {
                fail("The first iterator has more values than the second.");
            }
            String val2 = Utils.clean((String) iter2.next());

            // System.out.println("Comparing [" + val1 + "] to [" + val2 +
            // "].");
            // make sure the values are the same
            assertEquals(val1, val2);

        }
        // both should be done; if not fail
        if (iter2.hasNext()) {
            fail("The second iterator has more values than the first.");
        }
    }
}
