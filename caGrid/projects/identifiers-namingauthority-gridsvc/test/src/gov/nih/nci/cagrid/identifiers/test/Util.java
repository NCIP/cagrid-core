package gov.nih.nci.cagrid.identifiers.test;

import java.util.Arrays;
import junit.framework.Assert;
import namingauthority.IdentifierValues;
import namingauthority.KeyValues;

public class Util {
	public static void assertEquals( IdentifierValues values1, IdentifierValues values2 ) {
		Util.assertEquals( values1.getKeyValues(), values2.getKeyValues() );
    }
	
	public static void assertEquals(KeyValues[] tvs1, KeyValues[] tvs2) {
		
		// Make sure the Keys match
		if (!Arrays.equals(getSortedKeys(tvs1), getSortedKeys(tvs2))) {
			Assert.fail("keys are not the same");
		}

		for( KeyValues tv : tvs1 ) {
			Util.assertEquals(tvs2, tv.getKey(), tv.getValue());
		}
	}
    
    private static String[] getSortedKeys(KeyValues[] tvs) {
		String[] Keys = new String[ tvs.length ];
		for(int i=0; i < tvs.length; i++) {
			Keys[i] = tvs[i].getKey();
		}

		Arrays.sort(Keys);
		return Keys;
	}
    
    private static void assertEquals(KeyValues[] tvs, String Key, String[] values) {
		for( KeyValues tv : tvs ) {
			if (tv.getKey().equals(Key)) {
				String[] myValues = tv.getValue();
				Arrays.sort(values);
				Arrays.sort(myValues);
				Assert.assertEquals("values are not the same", true, Arrays.equals(values, myValues));
			}
		}
	}


}
