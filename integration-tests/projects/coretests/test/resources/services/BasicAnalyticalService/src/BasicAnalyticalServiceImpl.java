package gov.nih.nci.cagrid.tests.service;

import java.rmi.RemoteException;

import javax.naming.InitialContext;

import org.apache.axis.MessageContext;
import org.globus.wsrf.Constants;

/** 
 *  gov.nih.nci.cagrid.testsI
 *  TODO:DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.0
 * 
 */
public class BasicAnalyticalServiceImpl {

	public BasicAnalyticalServiceImpl() throws RemoteException {
		
	}

	     public java.lang.String reverseTranslate(java.lang.String dna) throws RemoteException {
	 		// reverse
	 		StringBuffer dnaBuf = new StringBuffer(dna);
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
	 		
	 		return new String(bases);
	}

}

