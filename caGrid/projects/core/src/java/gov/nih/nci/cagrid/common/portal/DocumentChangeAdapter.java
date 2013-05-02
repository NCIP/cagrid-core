/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package gov.nih.nci.cagrid.common.portal;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/** 
 *  DocumentChangeAdapter
 *  Useful document listener for cases where you're only
 *  interested in recording a change to a text field's value
 * 
 * @author David Ervin
 * 
 * @created Mar 23, 2007 3:15:24 PM
 * @version $Id: DocumentChangeAdapter.java,v 1.1 2007-04-16 16:06:31 dervin Exp $ 
 */
public abstract class DocumentChangeAdapter implements DocumentListener {

    public void changedUpdate(DocumentEvent e) {
        documentEdited(e);
    }


    public void insertUpdate(DocumentEvent e) {
        documentEdited(e);
    }


    public void removeUpdate(DocumentEvent e) {
        documentEdited(e);
    }

    
    /**
     * Called when any change is made to the document
     */
    public abstract void documentEdited(DocumentEvent e);
}
