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
package gov.nih.nci.cagrid.graph.uml.layout;

/**
 * This is a layouted edge in a diagram.
 */
public interface LayoutedEdge extends LayoutedObject {

    /**
     * Abstract method to layout the edge.
     */
    public void layout();
}
