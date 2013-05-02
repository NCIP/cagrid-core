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
package org.cagrid.data.sdkquery44.translator;

import gov.nih.nci.iso21090.hibernate.node.ComplexNode;
import gov.nih.nci.iso21090.hibernate.node.ConstantNode;
import gov.nih.nci.iso21090.hibernate.node.Node;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class IsoDatatypesConstantValueResolver implements ConstantValueResolver {
    
    public static final String DEFAULT_ISO_CONSTANTS_FILENAME = "IsoConstants.xml";
    
    private static Log LOG = LogFactory.getLog(IsoDatatypesConstantValueResolver.class);
    
    private ApplicationContext constantsContext = null;
    
    public IsoDatatypesConstantValueResolver() {
        LOG.debug("Loading Iso constants from classpath resource " + DEFAULT_ISO_CONSTANTS_FILENAME);
        constantsContext = new ClassPathXmlApplicationContext(DEFAULT_ISO_CONSTANTS_FILENAME);
    }
    
    
    public IsoDatatypesConstantValueResolver(ApplicationContext context) {
        this.constantsContext = context;
    }
    
    
    public Object getConstantValue(String rootLevelClassName, List<String> propertyPath) {
        Object value = null;
        String beanName = rootLevelClassName + "." + propertyPath.get(0);
        LOG.debug("Searching context for bean " + beanName);
        if (constantsContext.containsBean(beanName)) {
            LOG.debug("Found bean, traversing nodes for constant...");
            ComplexNode topNode = (ComplexNode) constantsContext.getBean(beanName);
            ConstantNode constant = traverseNodes(topNode, propertyPath, 1);
            if (constant != null) {
                LOG.debug("Constant found: " + constant.getConstantValue());
                value = constant.getInstance();
            }
        }
        if (value == null) {
            LOG.debug("No constant found");
        }
        return value;
    }
    
    
    private ConstantNode traverseNodes(ComplexNode parentNode, List<String> propertyPath, int currentPathIndex) {
        LOG.debug("Traversing node " + parentNode.getName() + ", looking for inner node " + propertyPath.get(currentPathIndex));
        List<Node> nodes = parentNode.getInnerNodes();
        for (Node n : nodes) {
            if (n.getName().equals(propertyPath.get(currentPathIndex))) {
                LOG.debug("Found inner node");
                // found a node of the name we want.  Do we need to dig deeper, or can we return it?
                if (propertyPath.size() == currentPathIndex + 1) {
                    // that's the end
                    LOG.debug("Assuming constant node at the end of the path");
                    return (ConstantNode) n;
                } else {
                    // try one level deeper
                    LOG.debug("Drilling down another level");
                    return traverseNodes((ComplexNode) n, propertyPath, currentPathIndex + 1);
                }
            }
        }
        return null;
    }
}
