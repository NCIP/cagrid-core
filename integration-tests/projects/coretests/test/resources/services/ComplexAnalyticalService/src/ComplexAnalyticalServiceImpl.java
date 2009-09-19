package gov.nih.nci.cagrid.tests.service;

import java.rmi.RemoteException;

/** 
 *  gov.nih.nci.cagrid.testsI
 *  TODO:DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.0
 * 
 */
public class ComplexAnalyticalServiceImpl extends ComplexAnalyticalServiceImplBase {

	public ComplexAnalyticalServiceImpl() throws RemoteException {
		
	}
	
	public gov.nih.nci.cagrid.tests.gene.Gene reverseTranslate(gov.nih.nci.cagrid.tests.gene.Gene gene) throws RemoteException {
 		// reverse
 		StringBuffer dnaBuf = new StringBuffer(gene.getDna());
 		dnaBuf.reverse();
 		char[] bases = dnaBuf.toString().toCharArray();
 		
 		// translate
 		for (int i = 0; i < bases.length; i++) {
 			char base = bases[i]; 			
 			switch (base) {
 				case 'a': base = 't'; break;
 				case 'A': base = 'T'; break;
 				case 't': base = 'a'; break;
 				case 'T': base = 'A'; break;
 				case 'c': base = 'g'; break;
 				case 'C': base = 'G'; break;
 				case 'g': base = 'c'; break;
 				case 'G': base = 'C'; break;
 			}
 			bases[i] = base;
 		}
 		
 		gene.setDna(new String(bases));
 		return gene;
	}

}

