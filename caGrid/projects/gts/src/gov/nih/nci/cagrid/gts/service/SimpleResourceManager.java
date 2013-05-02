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
package gov.nih.nci.cagrid.gts.service;

import java.io.InputStream;

import org.projectmobius.common.MobiusConfigurator;
import org.projectmobius.common.MobiusResourceManager;

public class SimpleResourceManager extends MobiusResourceManager{
    public SimpleResourceManager(String file) throws Exception{
            MobiusConfigurator.parseMobiusConfiguration(file,this);
    }
    
    public SimpleResourceManager(InputStream in) throws Exception{
            MobiusConfigurator.parseMobiusConfiguration(in,this);
    }

}
